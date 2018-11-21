import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * @author Yurdaer Dalkic & Hadi Deknache
 * 
 * This class handles the connection with the server, receiving/sending messages
 * and images.
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
		 * When everything is initialized, then we can send message to server Is sent
		 * through socket that was opened!
		 */
		try {
			clientSocket = new Socket(IPadress, TCPport); // Create a socket
			bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			printStream = new PrintStream(clientSocket.getOutputStream());
			msg = bufferedReader.readLine().trim(); // read the message (available resolutions)
			in = clientSocket.getInputStream();
			System.out.println(msg);
			controller.connected(msg); // send receives message to the controller
			stream = new BufferedInputStream(in);
			
		} catch (Exception e) {
			controller.error("Access refused"); // inform the controller about the error
		}

		while (isRunning) {

			if (clientSocket != null && clientSocket.isConnected()) { // check if we are still connected to the server
				try {

					msg = bufferedReader.readLine(); // read the size of next image

					//Checks if the data that is received is the image size or image
					if (isNumber(msg) & msg.length() > 2) { // check the size of image
						length = Integer.parseInt(msg);
						System.out.println(msg);
						imgBuf = new byte[length];
					}
					//Reads the image
					for (int read = 0; read < length;) { // read until the byte array is filled
						read += stream.read(imgBuf, read, imgBuf.length - read);
					}
					//Converts the image to a BufferedImage to show on a JPanel
					img = ImageIO.read(new ByteArrayInputStream(imgBuf));

					//Checks if the image might be null, preventing exception
					if (img != null) {
						controller.changeImage(img);
						img = null;
						System.out.println("SUCCESS!");

					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			} 
			//If client not connected then we close socket and stop
			else {
				close();
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
	public void close() {
		isRunning = false;
		try {
			clientSocket.close();
			printStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Closing! bye :(");

	}

	/**
	 * This method checks if a string can converts to the integer
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