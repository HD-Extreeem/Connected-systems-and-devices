import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;

public class GUI_Main implements ActionListener {
	private Controller controller;
	private JPanel background = new Picture2("src/files/background1.jpg");
	private JFrame frame = new JFrame();
	private String[] resolution;
	private JComboBox<String> resolutionsBox;
	private JTabbedPane tabbed = new JTabbedPane();
	private JPanel image = new Picture2("src/files/icon.png");
	private JButton exitButton = new JButton("X");
	private JButton updateButton = new JButton("Update");
	private JLabel fpsLabel = new JLabel();
	private JTextField fpsField = new JTextField();

	public GUI_Main(Controller controller, String[] resolution) {
		this.controller = controller;
		this.resolution = resolution;
		fpsLabel.setForeground(Color.WHITE);
		fpsLabel.setFont(new Font("Serif", Font.BOLD, 17));
		fpsLabel.setText("Frame Rate");
		fpsField.setBackground(Color.WHITE);
		fpsField.setText("1");
		fpsField.setFont(new Font("Serif", Font.BOLD, 16));
		updateButton.setText("Update");
		updateButton.setFont(new Font("Serif", Font.BOLD, 16));
		resolutionsBox = new JComboBox<>(resolution);
		//GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		//int centerX = (gd.getDisplayMode().getWidth()) / 2 - 250;
		int centerX = 200;
		// tas bor titel bar
		frame.setUndecorated(true);
		frame.setLayout(null);
		frame.setSize(1000, 1000);
		resolutionsBox.setBounds(centerX - 50, 690, 100, 35);
		tabbed.setBounds(centerX - 50, 30, 650, 650);
		fpsLabel.setBounds(centerX + 80, 695, 125, 25);
		fpsField.setBounds(centerX + 200, 695, 30, 25);
		updateButton.setBounds(centerX + 280, 695, 125, 25);
		updateButton.setOpaque(true);
		tabbed.add(image, 0);
		tabbed.setTitleAt(0, "Camera");
		exitButton.setBackground(new Color(10, 10, 10, 50));
		exitButton.setForeground(Color.RED);
		exitButton.setOpaque(false);
		exitButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		exitButton.setFont(new Font("default", Font.BOLD, 25));
		// om man trycker p� knappen programmet ska st�ng ner
		frame.setTitle("Assignment1");
		frame.setVisible(true);
		frame.setResizable(false);
		// fullsk�rm
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		background.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		background.setOpaque(false);
		exitButton.setBounds(frame.getWidth() - 60, 10, 40, 40);
		frame.add(exitButton);
		frame.add(tabbed);
		frame.add(fpsLabel);
		frame.add(fpsField);
		frame.add(resolutionsBox);
		frame.add(updateButton);
		frame.add(background);
		frame.setFocusable(true);
		frame.requestFocusInWindow();
		exitButton.addActionListener(this);
		updateButton.addActionListener(this);
	}

	/**
	 * Metoden tar emot en st�ng som representerar s�kv�gen av en bild. Den
	 * gamla bild tas bort fr�n f�nster och den nya bilden visas.
	 * 
	 * @param path
	 */
	public void changePath(BufferedImage image2) {

		image= null;
		image = new Picture(image2);
		tabbed.remove(0);
		tabbed.add(image, 0);
		tabbed.setTitleAt(0, "Camera");
		tabbed.setSelectedIndex(0);

	}



	public void dispose() {
		frame.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateButton) {
			controller.update((String)resolutionsBox.getSelectedItem(), fpsField.getText());
		
		} else if (e.getSource() == exitButton) {
			controller.closeConection();
			System.exit(0);
		}

	}

	public void isActive(boolean b) {
		updateButton.setEnabled(b);	
	}

	
	
	

}
