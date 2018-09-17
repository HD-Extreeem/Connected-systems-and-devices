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
 * Class for sending message
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
	private int imageNumber = 1;
	private ImageInputStream img2;

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
			clientSocket = new Socket(IPadress, TCPport);
			bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			printStream = new PrintStream(clientSocket.getOutputStream());
			img2 = ImageIO.createImageInputStream(clientSocket.getInputStream());
			String msg = bufferedReader.readLine().trim();
			System.out.println(msg);
			controller.connected(msg);

			// bufferedReader.close();
		} catch (Exception e) {
			controller.error("Access refused");
		}

		while (isRunning) {

			if (clientSocket != null && printStream != null && clientSocket.isConnected()) {
				try {
					img = ImageIO.read(img2);
					if (img != null) {
						controller.changeImage(img);
						img2.flush();
						img = null;
						System.out.println("SUCCESS!");
					}
				} catch (IOException e) {
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

	public void close() {
		isRunning = false;
		try {
			clientSocket.close();
			printStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
