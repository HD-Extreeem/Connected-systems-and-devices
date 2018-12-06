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
 * GUITheme
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Wed Jun 18 12:45:31 2003
 * Updated : $Date: 2003/07/26 16:19:40 $
 *           $Revision: 1.3 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.Color;
import java.util.Hashtable;
import javax.swing.ImageIcon;

/**
 */
public class GUITheme {
    public static final Color RFQ_COLOR = Color.blue;
    public static final Color OFFER_COLOR = Color.green.darker();
    //    public static final Color ORDER_COLOR = Color.orange.darker().darker();
    public static final Color ORDER_COLOR = Color.yellow;

  public static final Color NOMINAL_CAPACITY_COLOR = Color.lightGray;

    private static Hashtable iconTable = new Hashtable();

    public static ImageIcon getIcon(String name) {
      ImageIcon icon = (ImageIcon) iconTable.get(name);
      if (icon != null) {
	return icon;
      }

      java.net.URL url = AgentWindow.class.getResource("images/" + name);
      if (url != null) {
	icon = new ImageIcon(url);
	iconTable.put(name, icon);
      }
      return icon;
    }


} // GUITheme
