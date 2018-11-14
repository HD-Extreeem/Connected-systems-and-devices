#include <glib.h>
#include <axsdk/axevent.h>
#include <syslog.h>
#include <stdio.h>
#include <stdlib.h>
#include <glib/gstdio.h>
#include <pthread.h>
#include <time.h>
#include <unistd.h> /* read, write, close */
#include <string.h> /* memcpy, memset */
#include <sys/socket.h> /* socket, connect */
#include <netinet/in.h> /* struct sockaddr_in, struct sockaddr */
#include <netdb.h> /* struct hostent, gethostbyname */

void sub_callback(guint subs, AXEvent *evt, guint *token);
void *sendReq(char* current_time);
guint set_up_ax_event_subscription(AXEventHandler *evt_handler, guint token);

/* Callback for the motion event triggered each time there 
 * are some changes in the motion state
 */
void sub_callback(guint subs, AXEvent *evt, guint *token){
	const AXEventKeyValueSet *k_v_set;
	gboolean state;
	time_t now;
	(void)subs;

	//Extracting the key value set from event
	k_v_set = ax_event_get_key_value_set(evt);

	//State of the manual trigger port
	ax_event_key_value_set_get_boolean(k_v_set,"state",NULL,&state,NULL);
	char *msg =  state ? "Triggered high" : "Triggered low";
	syslog(LOG_INFO,"Motion: %s \n", msg);

    time(&now);
    char t [30];
    int h,m,s,d,mon,y;
    struct tm *local = localtime(&now);
    h = local->tm_hour;
    m = local->tm_min;
    s = local->tm_sec;
    d = local->tm_mday;
    mon = local->tm_mon+1;
    y = local->tm_year+1900;
    syslog(LOG_INFO,"Hour : %d-%d-%d \n",h,m,s);
    sprintf(t,"%d-%d-%d&%d-%d-%d",y,mon,d,h,m,s);
    syslog(LOG_INFO,"Time is with s: %s \n", t);
    pthread_t client_thread;
    pthread_create(&client_thread,NULL,sendReq,t);
}



/*Method for setting up the Motion detection event and bind to a subscription*/
guint set_up_ax_event_subscription(AXEventHandler *evt_handler, guint token){
	AXEventKeyValueSet *k_v_set;
	guint subs;

	k_v_set = ax_event_key_value_set_new();

	/* Init AXEvent which matches the manual trigger event
	 * 	
	 * tns1 --> topic 0, Rule
	 * tnsaxis --> topic 1, Motion detection
	 * subscribes to all states (active)
	 */
	ax_event_key_value_set_add_key_values(k_v_set, NULL, 
		"topic0", "tns1", "RuleEngine", AX_VALUE_TYPE_STRING, 
		"topic1", "tnsaxis", "VMD3", AX_VALUE_TYPE_STRING, 
		"active", NULL, NULL, AX_VALUE_TYPE_BOOL, NULL);

	//Subscribing to the motion event and assigning the callback to be called
	ax_event_handler_subscribe(evt_handler, k_v_set, &subs, (AXSubscriptionCallback)sub_callback, token, NULL);

	//Free resources for key value set
	ax_event_key_value_set_free(k_v_set);

	return subs;
}



int main(void){
	GMainLoop *m_loop;
  	syslog(LOG_INFO, "Starting motion app");
	AXEventHandler *evt_handler;
	m_loop = g_main_loop_new(NULL, FALSE);
	guint token = 1111;

	evt_handler = ax_event_handler_new();

	/*time(&now);
    char t [40];
    int h,m,s,d,mon,y;
    struct tm *local = localtime(&now);
    h = local->tm_hour;
    m = local->tm_min;
    s = local->tm_sec;
    d = local->tm_mday;
    mon = local->tm_mon+1;
    y = local->tm_year+1900;
    syslog(LOG_INFO,"Hour : %d-%d-%d \n",h,m,s);
    sprintf(t,"%d-%d-%d&%d-%d-%d",y,mon,d,h,m,s);
    //sprintf(t,"%d %d %d",h,m,s);
    //sprintf(time,"%d-%d-%d",h,m,s);
    //time = string_replace(time,' ', '-');
    syslog(LOG_INFO,"Time is with s: %s \n", t);
    pthread_t client_thread;
    pthread_create(&client_thread,NULL,sendReq,t);*/

	set_up_ax_event_subscription(evt_handler, &token);
	g_main_loop_run(m_loop);
	
	ax_event_handler_free(evt_handler);
	

	return 0;
}

void *sendReq(char* current_time)
{
    int portno =        8888;
    char *host =        "192.168.20.231";
    
    struct hostent *server;
    struct sockaddr_in serv_addr;
    int sockfd;
    char message[128],response[2048];
    
    
    /* fill the http params for request */
    sprintf(message,"%s",current_time);
    printf("Request:%s\n",message);
    

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) syslog(LOG_INFO,"Error to init socket!");
    
    /* Check host ip name */
    server = gethostbyname(host);
    if (server == NULL) syslog(LOG_INFO, "NO SUCH HOST!");
    
    /* Prepare struct for network */
    memset(&serv_addr,0,sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(portno);
    memcpy(&serv_addr.sin_addr.s_addr,server->h_addr,server->h_length);
    
    /* Connect to socket */
    if (connect(sockfd,(struct sockaddr *)&serv_addr,sizeof(serv_addr)) < 0)
        syslog(LOG_INFO,"FAILED TO CONNECT!");
    
    char request[200];
    sprintf(request, "POST %s/set_data\r\nHTTP/1.0\r\nHOST:%s\r\n", message,"192.168.20.231");
    send(sockfd,request,sizeof(request),0);
            
    printf("request sent!\n");
    printf("waiting for response...\n");
    recv(sockfd, &response, sizeof(request), 0);
    
    syslog(LOG_INFO,"Received following: %s",response);

    free(current_time);
    close(sockfd);
   
}

