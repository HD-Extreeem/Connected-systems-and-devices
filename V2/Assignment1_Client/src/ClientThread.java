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
		 * When everything is initialized, then we can send message to server Is
		 * sent through socket that was opened!
		 */
		try {
			clientSocket = new Socket(IPadress, TCPport);
			bufferedReader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			printStream = new PrintStream(clientSocket.getOutputStream());
			 img2 =  ImageIO.createImageInputStream(clientSocket.getInputStream());
			String msg = bufferedReader.readLine().trim();
			System.out.println(msg);

			controller.connected(msg);

			// bufferedReader.close();
		} catch (Exception e) {
			controller.error("Access refused");
		}

		while (isRunning) {

			if (clientSocket != null && printStream != null
					&& clientSocket.isConnected()) {
				try {
					// String msg = bufferedReader.readLine().trim();
					// System.out.println(msg);
					// int size = Integer.parseInt(msg);
					// byte[] marr = new byte[size];

					// DataInputStream inputStream = new
					// DataInputStream(clientSocket.getInputStream());
					// InputStream inputStream = clientSocket.getInputStream();

					// byte[] sizeAr = new byte[4];
					// inputStream.read(sizeAr);
					// int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
					// while ((byteRead = inputStream.read(marr)) != -1) ;
					// for (int i = 0; i < size; i++) {
					// marr[i] = inputStream.readByte();
					// }

					// inputStream.read(marr);
					// System.out.println(marr.toString());
					// BufferedImage image = ImageIO.read(new
					// ByteArrayInputStream(marr));
					
				//	BufferedImage image = ImageIO.read(ImageIO
				//			.createImageInputStream(clientSocket
				//					.getInputStream()));
					
				img = ImageIO.read(img2);	
					// System.out.println("Received " + image.getHeight() + "x"
					// + image.getWidth() + ": " + System.currentTimeMillis());
					if (img != null) {
					//	ImageIO.write(image, "jpeg", new File("src/files/"
					//			+ imageNumber + ".jpg"));
					//	controller.changeImage("src/files/" + imageNumber
					//			+ ".jpg");
					//	imageNumber++;
						controller.changeImage(img);
						img2.flush();
						img=null;
					//	controller.rutin();
						System.out.println("SUCCESS!");
					}
					// InputStream is = clientSocket.getInputStream();
					// FileOutputStream fos = new FileOutputStream("src/files/"
					// + imageNumber + ".jpg");
					// BufferedOutputStream bos = new BufferedOutputStream(fos);
					// bos.write(marr);
					// bos.close();
					// fos.close();
					// int bytesRead = is.read(marr,0,marr.length);

					// bos.flush();
					// bos.close();

					/*
					 * ByteArrayOutputStream boas = new
					 * ByteArrayOutputStream(size); int bytesRead = 0; int
					 * bytesIn = 0; try{ while(bytesRead < size){ bytesIn =
					 * is.read(marr); bytesRead += bytesIn;
					 * boas.write(marr,0,bytesIn); } boas.close();
					 * ByteArrayInputStream bais = new
					 * ByteArrayInputStream(boas.toByteArray()); img =
					 * ImageIO.read(bais); System.out.println("Img done!");
					 * bais.close(); } catch (IOException e){
					 * System.out.println(e.getLocalizedMessage()); }
					 */

					// img =
					// ImageIO.read(ImageIO.createImageInputStream(clientSocket.getInputStream()));
					// ImageIO.write(img, "jpg", new File("src/files/" +
					// imageNumber + ".jpg"));

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
