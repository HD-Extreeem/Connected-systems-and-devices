import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Yurdaer Dalkic & Hadi Deknache
 * 
 *         This class handles the connection with the server, receiving/sending
 *         messages and images.
 */
public class ClientThread implements Runnable {
	private Controller controller;
	private Socket clientSocket = null;
	private static PrintStream printStream = null;
	private BufferedReader bufferedReader = null;
	private boolean isRunning = false;
	private String IPadress;
	private int TCPport;
	private BufferedImage img;
	private InputStream in;
	private BufferedInputStream stream;
	private int length = 40000;
	private byte[] imgBuf = new byte[length];
	private String msg;

	/**
	 * 
	 * @param controller
	 * @param iPadress
	 * @param tCPport
	 */
	public ClientThread(Controller controller, String iPadress, String tCPport) {
		this.controller = controller;
		this.IPadress = iPadress;
		this.TCPport = Integer.valueOf(tCPport);
	}

	@Override
	public void run() {
		/*
		 * When everything is initialized, then we can send message to server Is
		 * sent through socket that was opened!
		 */
		try {
			clientSocket = new Socket(IPadress, TCPport); // Create a socket
			bufferedReader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			printStream = new PrintStream(clientSocket.getOutputStream());
			controller.sendKey(); // Send RSA public key to the server
			System.out.println("innan");
			msg = bufferedReader.readLine().trim(); //Receive XoR key
			System.out.println("received XoR key encrypted is : " + msg);
			controller.setKey(msg); // Save XoR key
			msg = bufferedReader.readLine().trim(); // read the message
			System.out.println("reading done");
			System.out.println(msg); // (available resolutions)
			in = clientSocket.getInputStream();
			controller.connected(controller.decryptXOR(msg)); // Decrypt
			
			stream = new BufferedInputStream(in);
			
		} catch (Exception e) {
			System.out.println(e.toString());
			controller.error("Access refused"); // inform the controller about
			// the error
		}

		while (isRunning) {

			if (clientSocket != null && clientSocket.isConnected()) { // check
				// if we are still connected to the server
				try {

					msg = controller.decryptXOR(bufferedReader.readLine().trim()); // read
					/*the size of next*/
					System.out.println("Length : " + msg);
					
					if (isNumber(msg) & msg.length() > 2) { // check the size of
						// image
						length = Integer.parseInt(msg);
						imgBuf = new byte[length];
					}

					for (int read = 0; read < length;) { // read until the byte
						// array is filled
						read += stream.read(imgBuf, read, imgBuf.length - read);
					}
					imgBuf = controller.decryptImage(imgBuf); // Decrypt data of received image
					img = ImageIO.read(new ByteArrayInputStream(imgBuf)); // Read byte array and convert it to an image

					if (img != null) {
						controller.changeImage(img); // Display received image
						img = null;
						System.out.println("SUCCESS!");

					} else {
						System.out.println("FAILED");
					}
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
				}

			} else {
				stop();
				controller.close();
				isRunning = false;
			}
		}
	}

	/**
	 * Sets the running to on/off
	 * 
	 * @param isRunning
	 */
	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;

	}

	/**
	 * Method returns socket to the server
	 * 
	 * @return socket to server
	 */
	public Socket getSocket() {
		return clientSocket;
	}

	/**
	 * Sends message to server
	 * 
	 * @param msg
	 */
	public void send(String msg) {
		printStream.println(msg);
	}

	/**
	 * Close the socket and print stream
	 */
	public void stop() {
		if (!clientSocket.isConnected()) {
			printStream.close();
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Closing! bye :(");
		}

	}

	/**
	 * Close the socket
	 */
	public void close() {
		isRunning = false;
		try {
			clientSocket.close();
			printStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method checks if a string can converts to the integer
	 * 
	 * @param msg
	 * @return
	 */
	private boolean isNumber(String msg) {
		for (int i = 0; i < msg.length(); i++) {
			if (!Character.isDigit(msg.charAt(i))) {
				return false;
			}

		}
		return true;
	}
}