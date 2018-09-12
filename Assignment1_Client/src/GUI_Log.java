import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
public class GUI_Log implements ActionListener {



	private JFrame frame = new JFrame("Assigment1");
	private JPanel main = new JPanel();
	private JLabel ipLabel = new JLabel();
	private JLabel TCPportLabel = new JLabel();
	private JTextField ipField = new JTextField();
	private JTextField TCPportField = new JTextField();
	private Picture picture;
	private JButton connectButton = new JButton();
	private Controller controller;

	/**
		 * Konstruktor som skapar en Jframe och s�tter alla komponenter in i den.
		 * Konstruktor anropas med en referens till klassen Controller.
		 * 
		 * @param controller
		 */
		public GUI_Log(Controller controller) {
		 this.picture = new Picture("src/files/background2.jpg");
			this.controller = controller;
			main.setLayout(null);
			ipLabel.setForeground(Color.white);
			ipLabel.setText("IP ADRESS");
			TCPportLabel.setText("TCP PORT");
			TCPportLabel.setForeground(Color.white);
		
			ipField.setText("192.168.20.247");
			TCPportField.setText("8080");
		
			connectButton.setText("Connect");
			ipField.setBounds(280, 50, 100, 25);
			ipLabel.setBounds(170, 50, 100, 25);
			TCPportField.setBounds(280, 90, 100, 25);
			TCPportLabel.setBounds(170, 90, 100, 25);
			connectButton.setBounds(200, 180, 100, 25);
			picture.add(ipField);
			picture.add(ipLabel);
			picture.add(TCPportField);
			picture.add(TCPportLabel);
			picture.add(connectButton);
			frame.setSize(800, 500);
			frame.setLocation(350, 180);
			frame.setResizable(false);
			frame.setVisible(true);
			frame.add(picture);
			main.setOpaque(false);
			connectButton.addActionListener(this);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

	/**
		 * Metoden �ndrar tiltle i JFrame som inneh�ller komponenter.
		 * 
		 * @param str�ngen
		 *            som ska vara title.
		 */
		public void setText(String res) {
			frame.setTitle(res);
		}

	/**
		 * Returnerar en str�ng som representerar IP nummer som finns i ipField.
		 * 
		 * @return en str�ng.
		 */
		public String getIP() {
			return ipField.getText();
		}

	/**
	 * Returnerar en str�ng som representerar Port nummer som finns i ipField.
	 * 
	 * @return en str�ng.
	 */
	public String getPort() {
		return TCPportField.getText();
	}


	/**
	 * Metoden rensar och st�nger f�nstret (GUI).
	 */
	public void dispose() {
		frame.dispose();
	}

	/**
	 * Visar ett fellmeddalende som informerar anv�ndaren om anslutning till server
	 * misslyckades.
	 */
	public void error() {
		JOptionPane.showMessageDialog(null, "Can not connect to server");
	}
	
	public void disableButton(boolean isEnable) {
			connectButton.setEnabled(isEnable);
	}

	/**
	 * Action Listener som s�ger vad som ska h�nda n�r man trycker p� knappen.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connectButton) {
			try {
				try {
					controller.connect();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
