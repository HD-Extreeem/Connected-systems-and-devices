import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import javax.swing.*;

public class GUI_Main implements ActionListener {
	private Controller controller;
	private JPanel background = new Picture("src/files/background1.jpg");
	private JFrame frame = new JFrame();
	private String[] resolution;
	private JComboBox<String> resolutionsBox;
	private JTabbedPane tabbed = new JTabbedPane();
	private JPanel image = new Picture("src/files/icon.png");
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
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		int centerX = (gd.getDisplayMode().getWidth()) / 2 - 250;
		// tas bor titel bar
		frame.setUndecorated(true);
		frame.setLayout(null);
		resolutionsBox.setBounds(centerX - 50, 690, 100, 35);
		tabbed.setBounds(centerX - 50, 30, 650, 650);
		fpsLabel.setBounds(centerX + 80, 695, 100, 25);
		fpsField.setBounds(centerX + 180, 695, 30, 25);
		updateButton.setBounds(centerX+280, 695, 100, 25);
		updateButton.setOpaque(true);
		tabbed.add(image, 0);
		tabbed.setTitleAt(0, "Camera");
		exitButton.setBackground(new Color(10, 10, 10, 50));
		exitButton.setForeground(Color.RED);
		exitButton.setOpaque(false);
		exitButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		exitButton.setFont(new Font("default", Font.BOLD, 25));
		// om amn trycker på knappen programmet ska stäng ner
		frame.setTitle("Assigment1");
		frame.setVisible(true);
		frame.setResizable(false);
		// fullskärm
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
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.update((String) resolutionsBox.getSelectedItem(), fpsField.getText());
			}
		});

	}

	/**
	 * Metoden tar emot en stäng som representerar sökvägen av en bild. Den gamla
	 * bild tas bort från fönster och den nya bilden visas.
	 * 
	 * @param path
	 */
	public void changePath(String path) {
		image = null;
		image = new Picture(path);
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
		// TODO Auto-generated method stub

	}

}
