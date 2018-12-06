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
 * ResultWindow
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Mon Jun 30 14:09:42 2003
 * Updated : $Date: 2003/07/04 14:36:34 $
 *           $Revision: 1.1 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 */
public class ResultWindow extends JFrame {
    public ResultWindow(String title, JComponent content) {
	super(title);
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(content, BorderLayout.CENTER);
	pack();
    }
} // ResultWindow
