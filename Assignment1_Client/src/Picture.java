

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Innre klassen som representerar en JPanel och ritar bilden som ska visas
 * panellen.
 * 
 * @author Yurdaer Dalkic
 *
 */
@SuppressWarnings("serial")
public class Picture extends JPanel {
	private BufferedImage image;

	/**
	 * Konstruktor tar emot s�kv�gen av bilden som ska visas i panellen.
	 * 
	 * @param Ipath s�kv�gen av bilden
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