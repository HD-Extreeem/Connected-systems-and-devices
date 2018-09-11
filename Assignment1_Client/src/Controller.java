import java.io.IOException;

import javax.swing.JOptionPane;

public class Controller {

	private GUI_Log gui_log;
	private GUI_Main gui_main;
	private String IPadress;
	private String TCPport;
	private ClientThread clientThread;
	private String[] resolutions = new String[] { "360*360", "560x560", "480x480", "280x280", "2800x2800" };

	public Controller() {
		gui_log = new GUI_Log(this);
	}

	/**
	 * Metoden anropas n�r man vill att programmet ansluta till servern. Metoden
	 * kontrollerar IP, TCPport och UDPPort f�lt om dem �r tom f�r�ker ansluta TCP
	 * server och UDP server. Om programmet har anslutning till TCP server st�ngs
	 * ner f�nstret och �ppnas ett nyt f�nster som anv�nderen kan styra bilen.
	 * 
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public void connect() throws NumberFormatException, IOException {
		this.IPadress = gui_log.getIP();
		this.TCPport = gui_log.getPort();

		// Anv�ndaren m�ste mata in IP och Port nummer f�r att kunna ansluta
		// till server
		if (IPadress.length() == 0 || TCPport.length() == 0) {
			fail();
		}
		//
		else {
			// anslutar rill TCP och UDP server
			clientThread = new ClientThread(this,IPadress, TCPport);
			clientThread.setIsRunning(true);
			Thread cliThread = new Thread(clientThread);
			cliThread.start();
			gui_log.disableButton(false);
		}
	}

	/**
	 * Visar ett fellmeddalende som informerar anv�ndraren om Ip nummer eller port
	 * nummer har inte angivits.
	 */
	public void fail() {
		JOptionPane.showMessageDialog(null, "Enter IP adress and Port number");
	}

	public void error(String message) {
		JOptionPane.showMessageDialog(null, message);
		gui_log.disableButton(true);
	}

	/**
	 * Metoden tar emot en str�ng som representerar s�gv�gen av ny bild som ska
	 * visas i f�nstrer och skickar den s�gv�gen till GUI_Main klassen.
	 * 
	 * @param imagePath s�kv�gen av ny bilden.
	 */
	public void changeImage(String imagePath) {
		gui_main.changePath(imagePath);
	}

	/**
	 * Metoden st�nger ner anslutningen till server, st�nger ner f�nster (GUI_Main)
	 * och �ppnar f�rsta f�nster (GUI_Log).
	 */
	public void close() {
		gui_main.dispose();
		gui_main = null;
		gui_log = new GUI_Log(this);

	}
	
	public void connected() {
		gui_log.dispose();
		gui_main = new GUI_Main(this, resolutions);
	}
	

	public void update(String selectedItem, String string) {
		// TODO Auto-generated method stub

	}

}
