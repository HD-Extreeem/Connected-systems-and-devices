
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class for sending message
 */
public class ClientImage implements Runnable {
	private Controller controller;
	private ServerSocket serverSocket;
	private Socket clientSocket = null;
	private DataInputStream inputStream = null;
	private boolean isRunning = false;
	private BufferedImage img;
	private int imageNumber = 1;
	private int TCPport;

	public ClientImage(Controller controller, String tCPport) {
		this.controller = controller;
		this.TCPport = Integer.valueOf(tCPport);
		try {
			serverSocket = new ServerSocket(TCPport);
			serverSocket.setSoTimeout(180000);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		/*
		 * When everything is initialized, then we can send message to server Is sent
		 * through socket that was opened!
		 */
		try {

			clientSocket = serverSocket.accept();
		} catch (Exception e) {
		}

		while (isRunning) {

			if (clientSocket != null && inputStream != null && clientSocket.isConnected()) {
				try {
					inputStream = new DataInputStream(clientSocket.getInputStream());
					img = ImageIO.read(ImageIO.createImageInputStream(inputStream));
					ImageIO.write(img, "jpg", new File("src/files/" + imageNumber + ".jpg"));
					controller.changeImage("src/files/" + imageNumber + ".jpg");
					imageNumber++;

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
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

	public void stop() {
		if (!clientSocket.isConnected()) {
			try {
				clientSocket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Closing! bye :(");
		}

	}

}
