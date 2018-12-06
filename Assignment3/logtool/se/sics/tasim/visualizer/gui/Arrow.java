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
 * Arrow
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Wed Jun 11 16:47:26 2003
 * Updated : $Date: 2003/06/24 16:18:50 $
 *           $Revision: 1.4 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Graphics2D;
import javax.sound.sampled.Line;
import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;

/**
 */
public class Arrow {

    private static final double DEFAULT_ARROW_LENGTH = 0.2;
    private static final double DEFAULT_ARROW_WIDTH = 0.1;
    private double arrowLength, arrowWidth, beta, hyp;

    private double fromX, fromY, toX, toY;
    private Color color;
    GeneralPath arrowHead;
    Line2D.Double line;

    public Arrow(double fromX, double fromY,
		 double toX, double toY, Color color) {
	if(!okRange(fromX) || !okRange(fromY) ||
	   !okRange(toX) || !okRange(toY))
	    return;

	this.fromX = fromX;
	this.fromY = fromY;
	this.toX = toX;
	this.toY = toY;
	this.color = color;

	arrowLength = DEFAULT_ARROW_LENGTH;
	arrowWidth = DEFAULT_ARROW_WIDTH;
	beta = Math.atan(arrowWidth / (2.0* arrowLength));
	hyp = Math.sqrt(arrowWidth*arrowWidth/4 +
			arrowLength*arrowLength);

	line = new Line2D.Double();
    }

    private boolean okRange(double p) {
	return !(p > 1 || p < 0);
    }

    public void paintArrowLine(Graphics2D g, int xOffset, int yOffset,
			       int width, int height) {

	g.setPaint(Color.black);
	g.setPaint(color.darker().darker());

	line.x1 = fromX*width - xOffset;
	line.y1 = height + yOffset - fromY*height;
	line.x2 = toX*width - xOffset;
	line.y2 = height + yOffset - toY*height;

	g.draw(line);
    }

    public void paintArrowHead(Graphics2D g, int xOffset, int yOffset,
			       int width, int height) {
	g.setPaint(color);

	double epsilon = 1E-6;
	double alpha;

	double dx = (toX - fromX) * (double) width;
	double dy = (toY - fromY) * (double) height;


	// Not vetical line
	if(Math.abs(dx) > epsilon)
	    alpha = Math.atan(dy / dx);

	// Vertical line going up
	else if(toY > fromY)
	    alpha = Math.PI / 2;

	// Vertical line going down
	else
	    alpha = 3 * Math.PI / 2;

	// If heading right
	if (toX > fromX)
	    alpha += Math.PI;

	double hw = (double) width / (double) height;
	float[] xPts = new float[]
	  { rescalePointX(toX, width, xOffset),
	    (rescalePointX(toX + hyp*Math.cos(alpha+beta), width, xOffset)),
	    (rescalePointX(toX + hyp*Math.cos(alpha-beta), width, xOffset))};
	float[] yPts = new float[]
	  { rescalePointY(toY, height, yOffset),
	    (rescalePointY(toY + hyp*hw*Math.sin(alpha+beta), height, yOffset)),
	    (rescalePointY(toY + hyp*hw*Math.sin(alpha-beta), height, yOffset))};

	arrowHead = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
	arrowHead.moveTo(xPts[0], yPts[0]);
	arrowHead.lineTo(xPts[1], yPts[1]);
	arrowHead.lineTo(xPts[2], yPts[2]);
	arrowHead.closePath();
	g.fill(arrowHead);
    }

    private float rescalePointX(double x, int width, int xOffset) {
	return (float) (x*width) - xOffset;
    }

    private float rescalePointY(double y, int height, int yOffset) {
	return height+yOffset - (float) (y*height);
    }

} // Arrow
