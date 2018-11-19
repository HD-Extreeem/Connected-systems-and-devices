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
char *encrypt_char(char *message, char* key);

/*
 * Connection_handler method that handles every independent client
 * Sends the resolutions to client and continiously sends images, after client sends the configurations
 */
void *msg_handler(void *socket_desc)
{
    int socket = *(int*)socket_desc;
    int size;
    char *msg, cli_message[2000],xor[5] ;
    media_stream *stream;
    int XoR_Key = gen_key();

    recv(socket, cli_message, 2000, 0);
    syslog(LOG_INFO, "recieved msg public key (n and e) %s\n",cli_message);
    int e = atoi(strtok(cli_message,","));
    long long int n = atoi(strtok(NULL,","));
    memset(cli_message,0,sizeof(cli_message));

    //These steps encrypts the xor key and puts them in the array to send to the client
    syslog(LOG_INFO,"xor key generated %d\n",XoR_Key);

    //---------Debug stuff----------
    //long double encrypted = (pow(XoR_Key,e));
    //syslog(LOG_INFO,"POWER & MOD CALL %Le\n",encrypted);
    //syslog(LOG_INFO," MOD CALL %lf\n",fmod((pow(XoR_Key,e)),n));
    //encrypted = fmod(encrypted,n);
    //syslog(LOG_INFO,"MOD %Le\n",encrypted);

    //Fmod not accurate gives wrong values rarely because of calculations
    //Encrypt and write the encrypted value to cli_message(string) and send to the client
    snprintf(cli_message,sizeof(fmod((pow(XoR_Key,e)),n)),"%d",(int) fmod((pow(XoR_Key,e)),n));	
    syslog(LOG_INFO,"Encrypted xor in string %s\n",cli_message);	
    write(socket,cli_message ,strlen(cli_message));
    write(socket, "\n", sizeof("\n"));
	syslog(LOG_INFO, "sent encrypted msg xor");
	memset(cli_message,0,sizeof(cli_message));

	//Put xor key in an array to use later for encrypting images and resolutions list
    sprintf(xor,"%d",XoR_Key);

    //Get the resolutions available on the camera and encrypt it, to send to the client application
    msg = capture_get_resolutions_list(0);
    char *encrypted_res_list = encrypt_char(msg,xor);
    write(socket, encrypted_res_list, strlen(encrypted_res_list));
    write(socket, "\n", sizeof("\n"));
    memset(msg, 0, strlen(msg));
    free(encrypted_res_list);

    //Receive the message from the client (fps&resoution)
    size = recv(socket, cli_message, sizeof(cli_message), 0);
    size = recv(socket, cli_message, sizeof(cli_message), 0);
    syslog(LOG_INFO,"RECIEVED FPS AND RES %s",cli_message);

    media_frame  *frame;
    void     	 *data;
    size_t   	 img_size;
    int 		 row = 0;
    char 		 *encrypted_img;
    //Decrypts the message recieved by the client (Resolution and fps), for opening the stream
    char *res_fps = encrypt_char((char *)cli_message,xor);
    strcpy(cli_message,res_fps);

    stream = capture_open_stream(IMAGE_JPEG, cli_message);
    free(res_fps);

    while (1) {
        frame = capture_get_frame(stream); 			// Get the frame
        data = capture_frame_data(frame); 			// Read data of image
        img_size  = capture_frame_size(frame); 		// Read size of the image
        sprintf(msg,"%zu",img_size);				// Convert the img_size to a char* to be able to send to the client

        encrypted_img = encrypt_char(msg,xor); 					// Encrypt size of the image
        write(socket, encrypted_img, strlen(encrypted_img)); 	// Send key to the client
        write(socket, "\n", sizeof("\n"));
            
        //free(encrypted_img);	//Free the resources used for encrypted_img
        
        char row_data[img_size];
	    int key_length = strlen(xor);
        for(row = 0; row<img_size;row++){
            row_data[row] = (((char*)data)[row] ^ (xor[row%key_length]-48)); // Encrypt data of the image (-48 for ascii conversion)
        }
            
	 
        int error = write(socket, row_data, sizeof(row_data)); // Send encrypted data to the client

        // There seem to be problems to send, leaving the while loop and 
        if (error < 0) {
            syslog(LOG_INFO, "Client is disconnected");
            break;
        }
            
        //memset(data, 0, sizeof(data));			// Empty img_data
        //memset(row_data, 0, sizeof(row_data));	// Empty row_data char[]
        //memset(msg,0,strlen(msg));				// Empty msg char*
        capture_frame_free(frame);					// Free the frame
        
    }
    if (size == 0) fflush(stdout);
    
    // The client seem to have left --> closing the connection and thread
    else if (size == -1){
        syslog(LOG_INFO, "Failed to send, ERROR, CLOSING DOWN!");
        capture_close_stream(stream);
        close(socket);
        free(socket_desc);
        
    }
    return 0;
}

/*Method used for generating random xor key*/
int gen_key(void){
    int nbr = (rand()%401)+100;
    return nbr;
}


int main(void)
{
    int socket_desc;
    int client_socket;
    int conn;
    int *new_socket;
    struct sockaddr_in server, client;
    srand(time(NULL));
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
    //int id;
    //Accept client connections
    conn = sizeof(struct sockaddr_in);
    while ((client_socket = accept(socket_desc, (struct sockaddr *)&client, (socklen_t*)&conn)))
    {
        //id = fork();
        //if(id==0){
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
            //pthread_join(client_thread, NULL);
        //}
    }
    
    if (client_socket < 0)
    {
        syslog(LOG_INFO, "Failed to accept client socket!!!");
        free(new_socket);
        return 1;
    }
    
    return 0;
}

// Message encrypting with XoR key
char *encrypt_char(char *message, char* key){
   int message_length = strlen(message);
   int key_length = strlen(key);
   char* encrypt_msg = malloc(message_length+1);
   int i;
   for ( i = 0; i< message_length; i++){
       encrypt_msg[i] = message[i] ^( key[i%key_length]-48); //Encrypts message to another array with the xor key generated (-48 for int to ascii conversion)
   }
   encrypt_msg[message_length]='\0';
   return encrypt_msg;
}




