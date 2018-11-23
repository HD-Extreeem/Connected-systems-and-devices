
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Date;

/*
 * This class is the main entry point for the application which starts and accepts the clients
 * That want to post data (Acap application) or get the between two intervals javaScript client
 */
public class JavaHTTPServer{

    public static void main(String[] args) {
        try {
            //ServerSocket serverConnect = new ServerSocket(8888);
        	ServerSocket serverConnect = new ServerSocket(8888);
            System.out.println("Server started, Listening on port nbr : " + 8888);

            //Loops forever waiting for clients to connect (Javascript client or Acap application)
            while (true) {
                WebThread wThread = new WebThread(serverConnect.accept());          //Create a Class instance for each connected client
                System.out.println("Connecton opened. time/date: " + new Date());   //Used for debugging
                Thread thread = new Thread(wThread);                                //Create a thread for each client
                thread.start();                                                     //Start the thread
            }

        } catch (IOException e) {
            System.out.println("Server Connection error : " + e.getMessage());      //If there were some problems, print the problems that occured
        }
    }

}