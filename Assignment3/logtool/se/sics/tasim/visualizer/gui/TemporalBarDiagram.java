/**
 * SICS ISL Java Utilities
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
 * TemporalBarDiagram
 * A simple bar diagram
 *
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Sun Feb 02 13:15:02 2003
 * Updated : $Date: 2003/07/15 14:22:13 $
 *           $Revision: 1.4 $
 * Purpose :
 *
 */
package se.sics.tasim.visualizer.gui;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;

public class TemporalBarDiagram extends JComponent {

  private static final double PI2 = 3.141592 / 2d;
  private static final double PADDING = 0.05;

    // [t][v] : t - time (day), v - value
    private int[][] data;

    private Rectangle swapBar, bgRect;
    private GradientPaint gradPaint;

    private int totMax;
    private int totMin;

    private double scaleFactor;

    private boolean rescale = true;
    private int xspace = 5;

    private Color leftColor = Color.cyan;
    private Color rightColor = Color.cyan.darker();
    private String[] names;
    private int barWidth;
    /*
    private int xfill;

    private int sizeX;
    private int sizeY;
    private int lowerY;

    private Color barColor;
    private Color[] allColors;
    */

    /*
    private boolean isShowingValue = false;
  private Color inValueColor = Color.white;
  private Color outValueColor = Color.black;
    */

    private PositiveBoundedRangeModel dayModel;


  // Cache to avoid creating new insets objects for each repaint. Is
  // created when first needed.
  private Insets insets;

    //  private String[] names;

    public TemporalBarDiagram(PositiveBoundedRangeModel dayModel)
    {
	this.dayModel = dayModel;
	setOpaque(true);
	setBackground(Color.white);

	// Repaint when datamodel changes
	if(dayModel != null) {
	    dayModel.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent ce) {
			repaint();
		    }
		});
	}

	addComponentListener(new ComponentAdapter() {
	    public void componentResized(ComponentEvent ce) {
	      rescale = true;
	    }
	  });


    }

  public void setToolTipVisible(boolean showToolTip) {
    setToolTipText(showToolTip ? "" : null);
  }

  /**
   * Sets the names for the bars.  The name and values will be shown
   * as tooltips if tooltips are turned on.
   *
   * @param names the names of the bars or <CODE>null</CODE> to clear the names
   */

  public void setNames(String[] names) {
    this.names = names;
  }


  public void setData(int[][] data) {
      if(data == null || data.length != dayModel.getLast()+1) {
	  totMax = 0;
	  totMin = 0;
	  return;
      }

    totMax = Integer.MIN_VALUE;
    totMin = Integer.MAX_VALUE;

    for (int i = 0, n = data.length; i < n; i++) {
	if(data[i] != null) {
	    for (int j = 0, m = data[i].length; j < m; j++) {
		if (totMax < data[i][j]) totMax = data[i][j];
		if (totMin > data[i][j]) totMin = data[i][j];
	    }
	}
    }

    if (totMin > 0) {
      totMin = 0;
    }
    if (totMax < totMin) {
      totMax = totMin;
    }

    this.data = data;
    rescale = true;

    //setupColors();
    repaint();
  }


    public String getToolTipText(MouseEvent event) {
	// Only show tool tips if some data exists
	if (data == null || data[dayModel.getCurrent()] == null ||
	    names == null) {
	    return null;
	}


	int index = (event.getX() - xspace) / (barWidth + xspace);

	if(index >= names.length)
	    return null;
	else if(index < 0)
	    return null;

	return names[index];
    }

    public void setColors(Color leftColor, Color rightColor) {
	this.leftColor = leftColor;
	this.rightColor = rightColor;
	repaint();
    }

    /*
  public void setBarColor(Color color) {
    barColor = color;
    leftColor = null;
    rightColor = null;
    allColors = null;
  }

  public void setBarColors(Color leftColor, Color rightColor) {
    this.leftColor = leftColor;
    this.rightColor = rightColor;
    allColors = null;
    setupColors();
  }

  private void setupColors() {
    if (leftColor != null && data != null) {
      if (allColors != null && allColors.length == data.length) {
	return;
      }
      float len = data.length;
      float r0 = leftColor.getRed() / len;
      float r1 = rightColor.getRed() / len;
      float g0 = leftColor.getGreen() / len;
      float g1 = rightColor.getGreen() / len;
      float b0 = leftColor.getBlue() / len;
      float b1 = rightColor.getBlue() / len;
      allColors = new Color[data.length];
      for (int i = 0, n = data.length; i < n; i++) {
	allColors[i] = new Color((int) (r0 * (n - i) + r1 * i),
				 (int) (g0 * (n - i) + g1 * i),
				 (int) (b0 * (n - i) + b1 * i));
      }
    }
  }

  public boolean isShowingValue() {
    return isShowingValue;
  }

  public void setShowingValue(boolean isShowingValue) {
    if (this.isShowingValue != isShowingValue) {
      this.isShowingValue = isShowingValue;
      repaint();
    }
  }

  public void setValueColor(Color color) {
    this.outValueColor = this.inValueColor = color;
  }

  public void setValueColors(Color inBarColor, Color outBarColor) {
    this.inValueColor = inBarColor;
    this.outValueColor = outBarColor;
  }
    */

  protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      Color oldColor = g.getColor();



	int totalWidth = getWidth();
	int totalHeight = getHeight();

	insets = getInsets(insets);
	int x = insets.left;
	int y = insets.top;
	int width = totalWidth - insets.left - insets.right;
	int height = totalHeight - insets.top - insets.bottom;

	if(x > 0)
	    g2.clearRect(0, 0, x, totalHeight);
	if(y > 0)
	    g2.clearRect(0, 0, totalWidth, y);
	if(insets.right > 0)
	    g2.clearRect(x+width, 0, totalWidth, totalHeight);
	if(insets.bottom > 0)
	    g2.clearRect(0, y+height, totalWidth, totalHeight);

	g2.setPaint(Color.white);
	if(bgRect == null || rescale)
	    bgRect = new Rectangle(x,y,width,height);

    g2.fill(bgRect);

    if(gradPaint == null || rescale)
    gradPaint = new GradientPaint(0,0,leftColor, x+width,0, rightColor);

    if(rescale) {
	    if (totMax == totMin ) {
		scaleFactor = 1;
	    } else {
		scaleFactor = (double) (height - height*PADDING) /
		    (double) Math.abs(totMax - totMin);
		//System.err.println("height: " + height);
		//System.err.println("totMax: " + totMax + " totMin: " + totMin);
	    }
	    rescale = false;
	}

	g2.setPaint(Color.black);
	//g.drawLine(x, y + lowerY, x + sizeX, y + lowerY);

	if(swapBar == null)
	    swapBar = new Rectangle();

    int curr = dayModel.getCurrent();

    barWidth = (int) ((width - xspace*(data[curr].length+1)) /
		      data[curr].length);
    for (int i = 0, n = data[curr].length; i < n; i++) {
	swapBar.x = x + (i+1)*xspace + i*barWidth;
	swapBar.y = y + height - (int) (scaleFactor * data[curr][i]);
	swapBar.width = barWidth;
	swapBar.height = (int) (scaleFactor * data[curr][i]);
	g2.draw(swapBar);
    }


    g2.setPaint(gradPaint);
    for (int i = 0, n = data[curr].length; i < n; i++) {
	swapBar.x = x + (i+1)*xspace + i*barWidth+1;
	swapBar.y = y + height + 1 - (int) (scaleFactor * data[curr][i]);
	swapBar.width = barWidth-1;
	swapBar.height = (int) (scaleFactor * data[curr][i]) -1;
    g2.fill(swapBar);
    }


    g2.rotate(-PI2);
    for (int i = 0, n = data[curr].length; i < n; i++) {
	if (data[curr][i] > 0) {
	    g.setColor(Color.black);
	    int texty = -y - height + 5;
	    if((int) (scaleFactor * data[curr][i]) + 40 < height)
		texty = -(y + height - (int) (scaleFactor * data[curr][i])) + 5;
	    g2.drawString(Integer.toString(data[curr][i]),
			  texty,
			  x + (i+1)*xspace + i*barWidth+1 + barWidth/2 +4);

	}
      }

    // restore the original non-rotation
    g2.rotate(PI2);
    g.setColor(oldColor);
  }

}
