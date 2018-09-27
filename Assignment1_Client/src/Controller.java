import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.CharBuffer;

import javax.swing.JOptionPane;

/**
 * 
 * @author Yurdaer Dalkic & Hadi Deknache
 * 
 *         This class handles all logical operations etc. connection with
 *         server, closing GUI_Log and opening GUI_Main, changing received
 *         images on the display, closing the connection with the server...
 *
 */
public class Controller {

	private GUI_Log gui_log;
	private GUI_Main gui_main;
	private String IPadress;
	private String TCPport;
	private ClientThread clientThread;
	private String res;
	private String[] resolutions = new String[] { "360x360", "560x560", "480x480", "280x280", "2800x2800" };

	/**
	 * Constructor which starts the GUI_Log in order to allow user type in IP
	 * address and Port number of the server.
	 */
	public Controller() {
		gui_log = new GUI_Log(this);
	}

	/**
	 * This method calls when user click the button on GUI_Log. This method checks
	 * the IP address and port number and starts a new thread which handles the
	 * communication with the server (ClientThread).
	 * 
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public void connect() throws NumberFormatException, IOException {
		this.IPadress = gui_log.getIP();
		this.TCPport = gui_log.getPort();

		// Check the IP address and port number
		if (IPadress.length() == 0 || TCPport.length() == 0) {
			fail();
		}
		//
		else {
			// Start a new threat which will handles the communication with the server
			clientThread = new ClientThread(this, IPadress, TCPport);
			clientThread.setIsRunning(true);
			Thread cliThread = new Thread(clientThread);
			cliThread.start();
			gui_log.disableButton(false);
		}
	}

	/**
	 * This method displays a message dialog that inform the user about there is
	 * something wrong with IP address or port number
	 */
	public void fail() {
		JOptionPane.showMessageDialog(null, "Enter IP adress and Port number");
	}

	/**
	 * This method displays a message dialog with received string.
	 * 
	 * @param message
	 */
	public void error(String message) {
		JOptionPane.showMessageDialog(null, message);
		gui_log.disableButton(true);
	}

	/**
	 * This method displays the received image in the GUI_Main.
	 * 
	 * @param image
	 */
	public void changeImage(BufferedImage image) {
		gui_main.changePath(image);
	}

	/**
	 * This method is responsible for closing the GUI_Main and opening the GUI_Log.
	 */
	public void close() {
		System.out.println("closing");
		gui_main.dispose();
		gui_main = null;
		gui_log = new GUI_Log(this);

	}

	/**
	 * This method closes the connection with the server.
	 */
	public void closeConection() {
		System.out.println("closing conenction");
		clientThread.close();

	}

	/**
	 * 
	 * @param msg
	 */
	public void connected(String msg) {
		gui_log.dispose();
		System.out.println("controller gui");
		String[] items = msg.split(",");
		gui_main = new GUI_Main(this, items);

	}

	/**
	 * This method is not currently used.
	 */
	public void rutin() {
		clientThread.send(res);
	}

	/**
	 * This method sends the chosen resolution and frame rate to the server.
	 * 
	 * @param selectedItem
	 * @param frameRate
	 */
	public void update(String selectedItem, String frameRate) {
		String message = "resolution=" + selectedItem + "&fps=" + frameRate;
		res = message;
		clientThread.send(message);
	}

}
