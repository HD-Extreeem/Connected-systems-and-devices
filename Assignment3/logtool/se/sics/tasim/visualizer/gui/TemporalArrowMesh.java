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
 * TemporalArrowMesh
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Wed Jun 11 16:47:26 2003
 * Updated : $Date: 2004/02/19 13:28:20 $
 *           $Revision: 1.8 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 */
public abstract class TemporalArrowMesh extends JComponent {

  protected LinkedList[] arrows;
  //  protected boolean[][] visible;
  private Insets insets;
  private ListIterator li;
  protected PositiveBoundedRangeModel timeModel;
  protected CommunicationModel communicationModel;

  public TemporalArrowMesh(CommunicationModel communicationModel,
			   PositiveBoundedRangeModel dayModel) {
    super();
    timeModel = dayModel;
    this.communicationModel = communicationModel;

    arrows = new LinkedList[dayModel.getLast()+1];

    // Repaint when datamodel changes
    if(timeModel != null) {
      timeModel.addChangeListener(new ChangeListener() {
	  public void stateChanged(ChangeEvent ce) {
	    repaint();
	  }
	});
    }


    // When communication model changes create new arrows
    if(communicationModel != null) {
      communicationModel.addChangeListener(new ChangeListener() {
	  public void stateChanged(ChangeEvent ce) {
	    dataChanged();
	  }
	});
    }

    setOpaque(true);
  }

  protected void dataChanged() {
    clearArrows();
    createArrows();
    repaint();
  }

  abstract protected void createArrows();

  protected void addArrow(Arrow arrow, int time) {
    if(time > arrows.length)
      return;

    if(arrows[time] == null)
      arrows[time] = new LinkedList();
    arrows[time].add(arrow);
  }

  protected void clearArrows() {
    for (int i = 0, n = arrows.length; i < n; i++) {
      if(arrows[i] != null)
	arrows[i].clear();
    }
  }


  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    if(arrows == null || arrows[timeModel.getCurrent()] == null)
      return;

    insets = getInsets(insets);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);

    li = arrows[timeModel.getCurrent()].listIterator(0);
    while(li.hasNext()) {
      ((Arrow) li.next()).paintArrowLine
	(g2, insets.left, insets.top,
	 getWidth() - insets.left - insets.right,
	 getHeight() - insets.top - insets.bottom);
    }
    li = arrows[timeModel.getCurrent()].listIterator(0);
    while(li.hasNext()) {
      ((Arrow) li.next()).paintArrowHead
	(g2, insets.left, insets.top,
	 getWidth() - insets.left - insets.right,
	 getHeight() - insets.top - insets.bottom);
    }
  }

} // TemporalArrowMesh
