/*
 * Socket server application used for handling multiple conections on the Axis camera
 * Server send resolutions and images to client and also handles configurations sent from the client
 * Authors: Yurdaer Dalkic, Hadi Deknache
 * Date: 2018-09-21
 */

#include<stdio.h>
#include<math.h>
#include<string.h>    
#include<stdlib.h>    
#include<sys/socket.h>
#include <sys/types.h>
#include<arpa/inet.h> 
#include<unistd.h>    
#include<pthread.h> 
#include<capture.h>
#include<syslog.h>


//Connection handler assigned for each client
void *msg_handler(void *);
int gen_key(void);
char* encrypt_char(char* message, char* key);

/*
 * Connection_handler method that handles every independent client
 * Sends the resolutions to client and continiously sends images, after client sends the configurations
 */
void *msg_handler(void *socket_desc)
{
    int socket = *(int*)socket_desc;
    int size;
    char *msg, cli_message[2000],*xor ;
    media_stream *stream;
    int XoR_Key = gen_key();

    recv(socket, cli_message, 2000, 0);
    syslog(LOG_INFO, "recieved msg");
    int e =atoi(strtok(cli_message,","));
    uint32_t n = atoi(strtok(NULL,","));
    
double encrypted = ((pow(XoR_Key,e))%n);
    //sprintf(msg,"%zu\n",encrypted);		
send(socket,(char *)&encrypted ,sizeof(double),NULL);

 //   write(socket, msg, strlen(msg));
    syslog(LOG_INFO, "sent msg xor");
    sprintf(xor,"%d",XoR_Key);
    msg = capture_get_resolutions_list(0);
    msg= encrypt_char(msg,xor);
    write(socket, msg, strlen(msg));
    write(socket, "\n", 1);
    memset(msg, 0, strlen(msg));

    
    //Receive a message from the client
    while ((size = recv(socket, cli_message, 2000, 0)) > 0)
    {
        
        //Sends the message back to the client
        media_frame  *frame;
        void     *data;
        size_t   img_size;
        int row = 0;
        
        stream = capture_open_stream(IMAGE_JPEG, cli_message);
        while (1) {
            frame = capture_get_frame(stream);
            
            data = capture_frame_data(frame);
            img_size  = capture_frame_size(frame);
            
            sprintf(msg,"%zu\n",img_size);
            write(socket, msg, strlen(msg));
            
            unsigned char row_data[img_size];
            for(row = 0; row<img_size;row++){
                row_data[row] = ((unsigned char*)data)[row];
            }
            
            int error = write(socket, row_data, sizeof(row_data));
            if (error < 0) {
                syslog(LOG_INFO, "Client is disconnected");
                break;
            }
            
            memset(data, 0, sizeof(data));
            memset(row_data, 0, sizeof(row_data));
            capture_frame_free(frame);
        }
    }
    if (size == 0) fflush(stdout);
    
    else if (size == -1){
        syslog(LOG_INFO, "Failed to send, ERROR, CLOSING DOWN!");
        capture_close_stream(stream);
        close(socket);
        free(socket_desc);
        
    }
    return 0;
}

int gen_key(void){
    int nbr = (rand()%4001)+1000;
    return nbr;
}


int main(void)
{
    int socket_desc;
    int client_socket;
    int conn;
    int *new_socket;
    struct sockaddr_in server, client;
    
    //Create socket
    socket_desc = socket(AF_INET, SOCK_STREAM, 0);
    
    if (socket_desc == -1) syslog(LOG_INFO, "FAILED TO CREATE SOCKET!");
    
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons(8080);
    
    //Binds the connection to port, so the client can connet to it through sockets
    if (bind(socket_desc, (struct sockaddr *)&server, sizeof(server)) < 0)
    {
        //Log error!
        syslog(LOG_INFO, "Bind ERROR");
        return 1;
    }
    
    listen(socket_desc, 4);
    
    conn = sizeof(struct sockaddr_in);
    int id;
    //Accept client connections
    conn = sizeof(struct sockaddr_in);
    while ((client_socket = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&conn)))
    {
        id = fork();
        if(id==0){
            pthread_t client_thread;
            new_socket = malloc(sizeof *new_socket);
            *new_socket = client_socket;
            
            //MSG THREAD
            if (pthread_create(&client_thread, NULL, msg_handler, (void*)new_socket) < 0)
            {
                syslog(LOG_INFO, "Failed to create msg thread!!!");
                return 1;
            }
            
            //Join thread
            pthread_join(client_thread, NULL);
        }
    }
    
    if (client_socket < 0)
    {
        syslog(LOG_INFO, "Failed to accept client socket!!!");
        free(new_socket);
        return 1;
    }
    
    return 0;
}

char* encrypt_char(char* message, char* key){
   int message_length = strlen(message);
   int key_length = strlen(key);
   char* encrypt_msg = malloc(message_length);
   int i;
   for ( i = 0; i< message_length; i++){
       encrypt_msg[i] = message[i] ^ key[i%key_length];
   }
   return encrypt_msg;
}


