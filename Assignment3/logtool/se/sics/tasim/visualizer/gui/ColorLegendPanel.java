/**
 * TAC Supply Chain Management Log Tools
 * http://www.sics.se/tac/    tac-dev@sics.se
 *
 * Copyright (c) 2001-2003 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * ColorLegendPanel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Wed Jun 18 13:08:03 2003
 * Updated : $Date: 2003/07/15 12:04:09 $
 *           $Revision: 1.5 $
 */
package se.sics.tasim.visualizer.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.Box;
import java.awt.Insets;
import java.awt.geom.Line2D;



/**
 */
public class ColorLegendPanel {
    private static int SIZE = 20;

    JPanel mainPane;
    JLabel rfqLabel;

    public ColorLegendPanel() {
	mainPane = new JPanel();
	mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
// 	mainPane.setBorder(BorderFactory.createTitledBorder
// 			   (BorderFactory.createEtchedBorder(),
// 			    " Color Legend "));

	BufferedImage square =
	    new BufferedImage(SIZE,SIZE, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2 = square.createGraphics();
	try {
	  g2.setPaint(GUITheme.RFQ_COLOR);
	  g2.fill(new Rectangle(0,0,SIZE,SIZE));
	  g2.setPaint(Color.black);
	  g2.draw(new Rectangle(0,0,SIZE-1,SIZE-1));
	} finally {
	  g2.dispose();
	}
	JLabel rfqLabel = new JLabel("RFQ color",
				     new ImageIcon(square),
				     JLabel.LEFT);
	rfqLabel.setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
	mainPane.add(rfqLabel);

	square = new BufferedImage(SIZE,SIZE, BufferedImage.TYPE_INT_RGB);
	g2 = square.createGraphics();
	try {
	  g2.setPaint(GUITheme.OFFER_COLOR);
	  g2.fill(new Rectangle(0,0,SIZE,SIZE));
	  g2.setPaint(Color.black);
	  g2.draw(new Rectangle(0,0,SIZE-1,SIZE-1));
	} finally {
	  g2.dispose();
	}
	JLabel offerLabel = new JLabel("Offer color",
				       new ImageIcon(square),
				       JLabel.LEFT);
	offerLabel.setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
	mainPane.add(offerLabel);

	square = new BufferedImage(SIZE,SIZE, BufferedImage.TYPE_INT_RGB);
	g2 = square.createGraphics();
	try {
	  g2.setPaint(GUITheme.ORDER_COLOR);
	  g2.fill(new Rectangle(0,0,SIZE,SIZE));
	  g2.setPaint(Color.black);
	  g2.draw(new Rectangle(0,0,SIZE-1,SIZE-1));
	} finally {
	  g2.dispose();
	}
	JLabel orderLabel = new JLabel("Order color",
				       new ImageIcon(square),
				       JLabel.LEFT);
	orderLabel.setBorder(BorderFactory.createEmptyBorder(2,6,6,6));
	mainPane.add(orderLabel);
    }

    public JPanel getMainPane() {
	return mainPane;
    }


} // ColorLegendPanel
