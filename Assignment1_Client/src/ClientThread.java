import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class for sending message
 */
public class ClientThread implements Runnable {
    private Socket clientSocket = null;
    private static PrintStream printStream = null;

    private  BufferedReader bufferedReader = null;
    private  boolean isRunning = false;
    private JLabel status;
    private JButton btn;

    public ClientThread(JButton btnSend, JLabel status){
        this.btn = btnSend;
        this.status = status;
    }

    @Override
    public void run() {
        /*
         * When everything is initialized, then we can send message to server
         * Is sent through socket that was opened!
         */
        byte [] img = new byte[0];
        try {
            clientSocket = new Socket("192.168.20.247",80);
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            printStream = new PrintStream(clientSocket.getOutputStream());

            //btn.setEnabled(true);
            status.setText(" Status: Connected! ");
            status.setBackground(Color.GREEN);
            status.setBounds(160, 5, 130, 20);
            

        } catch (UnknownHostException e) {
            System.err.println("No such host " + 24);
            btn.setEnabled(false);
            status.setText("Status: No such host!");
        } catch (IOException ignored) {
        
        }
        send("Hej");
        if (clientSocket != null && printStream != null) {
            try {
            	String msg = bufferedReader.readLine().trim();
            	System.out.println(msg);
            	printStream.println(msg);
            	
                //ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                //ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                //output.flush();
                //output.writeUTF(imageId);
                //output.flush();
                //img = (byte[]) input.readObject();
                //System.out.println(img.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the running to on/off
     * @param isRunning
     */
    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;

    }

    /**
     * Method returns socket to the server
     * @return socket to server
     */
    public Socket getSocket() {
        return clientSocket;
    }

    /**
     * Sends message to server
     * @param msg
     */
    public static void send(String msg){
        printStream.println(msg);
    }

    public void stop(){
        if (!clientSocket.isConnected()){
            printStream.close();
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Closing! bye :(");
        }

    }
}
