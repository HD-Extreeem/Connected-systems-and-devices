

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * The inner class representing a JPanel and draws the image to display the panel.
 * 
 * @author Yurdaer Dalkic & Hadi Deknache
 *
 */
@SuppressWarnings("serial")
public class Picture extends JPanel {
	private BufferedImage image;

	/**
	 * Constructor receives the image that will displays in the panel.
	 * 
	 * @param image
	 */
	public Picture(BufferedImage image) {
		this.image = image;
		setLayout(null);
	}

	protected void paintComponent(Graphics e) {
		super.paintComponent(e);
		int startx = 0;
		int starty = 0;
		int finishx = getSize().width;
		int finishy = getSize().height;
		e.drawImage(image, startx, starty, finishx, finishy, null);
	}

}