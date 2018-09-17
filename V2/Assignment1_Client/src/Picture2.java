

import java.awt.Graphics;
import java.awt.Image;
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
public class Picture2 extends JPanel {
	private String Path;

	/**
	 * Konstruktor tar emot sökvägen av bilden som ska visas i panellen.
	 * 
	 * @param Ipath sökvägen av bilden
	 */
	public Picture2(String Ipath) {
		this.Path = Ipath;
		setLayout(null);
	}

	protected void paintComponent(Graphics e) {
		super.paintComponent(e);
		Image image = new ImageIcon(Path).getImage();
		int startx = 0;
		int starty = 0;
		int finishx = getSize().width;
		int finishy = getSize().height;
		e.drawImage(image, startx, starty, finishx, finishy, null);
	}

}