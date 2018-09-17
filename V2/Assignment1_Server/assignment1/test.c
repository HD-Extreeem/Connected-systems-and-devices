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
void *msg_handler(void *);
 
/*
 * Connection_handler method that handles every independent client
 * 
 */
void *msg_handler(void *socket_desc)
{
    int socket = *(int*)socket_desc;
    int size;
    char *msg , cli_message[2000];

    msg = capture_get_resolutions_list(0);
    write(socket , msg , strlen(msg));
    write(socket, "\n",1);
    memset(msg,0,strlen(msg));
    syslog(LOG_INFO, "Handler!");
    //Receive a message from client
    while( (size = recv(socket , cli_message , 2000 , 0)) > 0 )
    {
	while(1){
	syslog(LOG_INFO, "SENDING MESSAGE!!");
        //Sends the message back to the client
          //config = cli_message;
	  media_frame  *frame;
          void     *data;
    	  size_t   img_size;
	  int row = 0;
    	  media_stream *stream;

    	  stream    = capture_open_stream(IMAGE_JPEG, cli_message);
    	  frame     = capture_get_frame(stream);
	  data      = capture_frame_data(frame);
          img_size  = capture_frame_size(frame);
	  //char str_size[sizeof(img_size)];
	  //snprintf(str_size,sizeof(str_size),"%zu",img_size); 
	  //syslog(LOG_INFO,str_size);

	  //write(socket , str_size , sizeof(str_size));
	  //write(socket, "\n",1);

	  unsigned char row_data[img_size];  
	  for(row = 0; row<img_size;row++){
	    row_data[row] = ((unsigned char*)data)[row];
	  }

    	  write(socket , row_data , sizeof(row_data));
	  memset(data,0,sizeof(data));
	  memset(row_data,0,sizeof(row_data));
    	  }
	  
   }
    if(size == 0) fflush(stdout);
    
    else if(size == -1) syslog(LOG_INFO, "Failed to send, ERROR!");
    
    free(socket_desc);

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
    socket_desc = socket(AF_INET , SOCK_STREAM , 0);
    
    if (socket_desc == -1) syslog(LOG_INFO,"FAILED TO CREATE SOCKET!");
    
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = INADDR_ANY;
    server.sin_port = htons( 8080 );
     
    //Bind
    if( bind(socket_desc,(struct sockaddr *)&server , sizeof(server)) < 0)
    {
        //Log error!
        syslog(LOG_INFO,"Bind ERROR");
        return 1;
    }
     
    listen(socket_desc , 3);
     
    conn = sizeof(struct sockaddr_in);
     
    //Accept connections
    conn = sizeof(struct sockaddr_in);
    while( (client_socket = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&conn)) )
    {
         
        pthread_t client_thread;
        new_socket = malloc(sizeof *new_socket);
        *new_socket = client_socket;
        
        //MSG THREAD
        if( pthread_create( &client_thread , NULL ,  msg_handler , (void*) new_socket) < 0)
        {
            syslog(LOG_INFO,"Failed to create msg thread!!!");
            return 1;
        }
        
        /*//IMG THREAD
        if( pthread_create( &client_thread , NULL ,  img_handler , (void*) new_socket) < 0)
        {
            syslog("Failed to create img thread!!!");
            return 1;
        }*/
         
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
 
