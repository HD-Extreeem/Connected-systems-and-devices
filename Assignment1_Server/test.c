/*
 * Socket server application used for handling multiple conections on the Axis camera
 * Server send resolutions and images to client and also handles configurations sent from the client
 * Authors: Yurdaer Dalkic, Hadi Deknache
 * Date: 2018-09-21
 */

#include<stdio.h>
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

/*
 * Connection_handler method that handles every independent client
 * Sends the resolutions to client and continiously sends images, after client sends the configurations
 */
void *msg_handler(void *socket_desc)
{
    int socket = *(int*)socket_desc;
    int size;
    char *msg, cli_message[2000];
    msg = capture_get_resolutions_list(0);
    write(socket, msg, strlen(msg));
    write(socket, "\n", 1);
    memset(msg, 0, strlen(msg));
    media_stream *stream;
    
    //Receive a message from the client
    while ((size = recv(socket, cli_message, 2000, 0)) > 0)
    {
        
        //Variables used for handling the img
        media_frame  *frame;
        void     *data;
        size_t   img_size;
        int row = 0;
        
        //Opens a stream to the camera to get the img
        stream = capture_open_stream(IMAGE_JPEG, cli_message);
        
        //Continiously sends the img to client
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
	    //Checking if the write failed
	    //Might then be that the client disconnected
            if (error < 0) {
                syslog(LOG_INFO, "Client is disconnected");
                break;
            }
            
	    //Emptying the variables to be sure nothing is stored 
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
    }
    return 0;
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
