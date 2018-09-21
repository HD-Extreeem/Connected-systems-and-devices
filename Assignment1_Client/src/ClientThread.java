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
	private InputStream in;
	private BufferedInputStream stream;
	private int length = 40000;
	private byte[] imgBuf = new byte[length];
	private boolean isStrNull = false;
	private String msg;

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
			clientSocket = new Socket(IPadress, TCPport);
			bufferedReader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			printStream = new PrintStream(clientSocket.getOutputStream());
			msg = bufferedReader.readLine().trim();
			in = clientSocket.getInputStream();
			System.out.println(msg);
			controller.connected(msg);
			stream = new BufferedInputStream(in);

			// bufferedReader.close();
		} catch (Exception e) {
			controller.error("Access refused");
		}

		while (isRunning) {

			if (clientSocket != null && clientSocket.isConnected()) {
				try {
					/*
					 * stream = new BufferedInputStream(in); if (!isImgNull) {
					 * String msg2;
					 * 
					 * do {
					 * 
					 * // String msg2; msg2 = bufferedReader.readLine(); //
					 * while ((msg2 = bufferedReader.readLine()) != // null);
					 * System.out.println(msg2); try { length =
					 * Integer.parseInt(msg2); isStrNull = false; } catch
					 * (Exception e) { isStrNull = true;
					 * System.out.println("Error int"); } buf = new
					 * byte[length]; } while (isStrNull);
					 * 
					 * }
					 */

					// System.out.println(clientSocket.isClosed());
					/*
					 * //length = bufferedReader.read(); //
					 * System.out.println(readInt(in));
					 * 
					 * // in = clientSocket.getInputStream(); // byte[] byts =
					 * new byte[msg+1]; // int caount = in.read(byts); //
					 * InputStream inBytes = new ByteArrayInputStream(byts); //
					 * img = ImageIO.read(inBytes); //img = ImageIO.read(in);
					 */
					bufferedReader = new BufferedReader(new InputStreamReader(
							clientSocket.getInputStream()));

					msg = bufferedReader.readLine();
					in = clientSocket.getInputStream();
					stream = new BufferedInputStream(in);
					if (isNumber(msg) & msg.length() > 2) {
						length = Integer.parseInt(msg);
						System.out.println(msg);
						imgBuf = new byte[length];
					}

					for (int read = 0; read < length;) {
						read += stream.read(imgBuf, read, imgBuf.length - read);
						// System.out.println("test");
					}
					img = ImageIO.read(new ByteArrayInputStream(imgBuf));

					if (img != null) {
						controller.changeImage(img);
						img = null;
						System.out.println("SUCCESS!");
						// isImgNull = false;
						// controller.rutin();
						// System.out.println(length);
					} /*
					 * else { System.out.println("image is null"); isImgNull =
					 * true; }
					 */
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

	private boolean isNumber(String msg) {
		for (int i = 0; i < msg.length(); i++) {
			if (!Character.isDigit(msg.charAt(i))) {
				return false;
			}

		}
		return true;
	}
}