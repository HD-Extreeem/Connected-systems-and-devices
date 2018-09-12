/*
    Socket server application used for handling multiple conections on the Axis camera
*/
 
#include<stdio.h>
#include<string.h>    
#include<stdlib.h>    
#include<sys/socket.h>
#include<arpa/inet.h> 
#include<unistd.h>    
#include<pthread.h> 
#include<capture.h>
#include<syslog.h>
 
//Connection handler
void *conn_handler(void *);
 
/*
 * Connection_handler method that handles every independent client
 * 
 */
void *conn_handler(void *socket_desc)
{
    //Get the socket descriptor
    int socket = *(int*)socket_desc;
    int read_size;
    char *msg , cli_message[1500];

    msg = capture_get_resolutions_list(0);
    write(socket , msg , strlen(msg));
    write(socket, "\n",1);

    //Receive a message from client
    while( (read_size = recv(socket , cli_message , 3000 , 0)) > 0 )
    {
        //Send the message back to client
        write(socket , cli_message , strlen(cli_message));
        memset(cli_message,0,strlen(cli_message));
    }
     
    if(read_size == 0)
    {
        fflush(stdout);
    }
    else if(read_size == -1)
    {
        perror("recv failed");
    }
         
    //Free the socket pointer
    free(socket_desc);
     
    return 0;
}


int main(void)
{
    int socket_desc , client_socket , conn , *new_socket;
    struct sockaddr_in server , client;
   
    //Create socket
    socket_desc = socket(AF_INET , SOCK_STREAM , 0);
    if (socket_desc == -1)
    {
        syslog(LOG_INFO,"FAILED TO CREATE SOCKET!");
    }
     
    
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons( 8080 );
     
    //Bind
    if( bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
    {
        //print the error message
        syslog(LOG_INFO,"bind error");
        return 1;
    }
     
    listen(socket_desc , 3);
     
    conn = sizeof(struct sockaddr_in);
     
    //Accept connections
    conn = sizeof(struct sockaddr_in);
    while( (client_socket = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&conn)) )
    {
         
        pthread_t client_thread;
        new_socket = malloc(1);
        *new_socket = client_socket;
         
        if( pthread_create( &client_thread , NULL ,  conn_handler , (void*) new_socket) < 0)
        {
            syslog("Failed to create thread!!!");
            return 1;
        }
         
        //Now join the thread , so that we dont terminate before the thread
        pthread_join(client_thread , NULL);
    }
     
    if (client_socket < 0)
    {
        syslog(LOG_INFO,"Failed to accept client socket!!!");
        return 1;
    }
     
    return 0;
}
 

