import java.awt.event.ActionEvent;

/*
 *  This class represents a GUI which allows the user to specify the IP address and port number of the server.
 *  
 */

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
	private Picture2 picture;
	private JButton connectButton = new JButton();
	private Controller controller;

	/**
	 * 
	 * Construktor that creates en JFrame and places all components into it.
	 * Construktor calls with en referens in the Controller class .
	 * 
	 * @param controller
	 */
	public GUI_Log(Controller controller) {
		this.picture = new Picture2("src/files/background2.jpg");
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
		frame.setVisible(true);
	}

	/**
	 * This method changes the title of JFrame which contains all components.
	 * 
	 * @param String that repleces.
	 */
	public void setText(String res) {
		frame.setTitle(res);
	}

	/**
	 * Returns a String that represents the IP number of the server
	 * 
	 * @return IP adress.
	 */
	public String getIP() {
		return ipField.getText();
	}

	/**
	 * Returns a String that represents the Port number of the server.
	 * 
	 * @return Port number
	 */
	public String getPort() {
		return TCPportField.getText();
	}

	/**
	 * This method clears and shots down the JFrame (GUI).
	 */
	public void dispose() {
		frame.dispose();
	}

	/**
	 * Displays an error message that informs the user about the connection to the server is
	 * failed.
	 */
	public void error() {
		JOptionPane.showMessageDialog(null, "Can not connect to server");
	}

	/**
	 * This method disables/enables the connection butten
	 * @param isEnable
	 */
	public void disableButton(boolean isEnable) {
		connectButton.setEnabled(isEnable);
	}

	/**
	 * Action listener that decide which method will be called when user click the connect button.
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
