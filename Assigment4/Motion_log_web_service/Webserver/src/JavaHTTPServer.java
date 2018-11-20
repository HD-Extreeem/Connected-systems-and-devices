
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Date;


public class JavaHTTPServer{

    public static void main(String[] args) {
        try {
         //   ServerSocket serverConnect = new ServerSocket(8888);
        	 ServerSocket serverConnect = new ServerSocket(8888, 0, InetAddress.getByName("localhost"));
            System.out.println("Server started, Listening on port nbr : " + 8888);

            
            while (true) {
            	System.out.println("X1");
                WebThread wThread = new WebThread(serverConnect.accept());
                System.out.println("Connecton opened. time/date: " + new Date());
                Thread thread = new Thread(wThread);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Server Connection error : " + e.getMessage());
        }
    }

}