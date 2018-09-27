
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * The inner class representing a JPanel and draws the image to display the
 * panel.
 * 
 * 
 * @author Yurdaer Dalkic & Hadi Deknache
 *
 */
@SuppressWarnings("serial")
public class Picture2 extends JPanel {
	private String Path;

	/**
	 * Constructor receives the path of the image that will displays in the panel.
	 * 
	 * @param Path of the image
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