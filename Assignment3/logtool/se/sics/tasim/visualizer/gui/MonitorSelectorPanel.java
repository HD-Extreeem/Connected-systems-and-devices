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
 * MonitorSelectorPanel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Wed Jun 18 13:08:03 2003
 * Updated : $Date: 2003/07/15 08:48:25 $
 *           $Revision: 1.4 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.botbox.util.ArrayUtils;
import se.sics.tasim.visualizer.monitor.ParserMonitor;

/**
 */
public class MonitorSelectorPanel {
    JPanel mainPane;
    JLabel label;
    JList list;
    ResultWindow[] windows;
    ParserMonitor[] mon;

    public MonitorSelectorPanel(ParserMonitor[] parserMon) {
	mainPane = new JPanel();
	mainPane.setLayout(new BorderLayout());
	mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),
			    " Monitor list "));

	JLabel label = new JLabel("FUBAR");

	if(parserMon == null) {
	    list = new JList();
	}
	else {

	    // Pick out monitors that can be displayed
	    for (int i = 0, n = parserMon.length; i < n; i++) {
		if(parserMon[i].hasSimulationView())
		    mon = (ParserMonitor[]) ArrayUtils.add(ParserMonitor.class,
							   mon, parserMon[i]);
	    }

	    list = new JList(mon);
	    windows = new ResultWindow[mon.length];
	}

	list.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    if (e.getClickCount() == 2) {
			int index = list.locationToIndex(e.getPoint());
			if(windows == null || index >= windows.length)
			    return;

			if(windows[index] == null) {
			    windows[index] =
				new ResultWindow(mon[index].getName(),
						 mon[index].getSimulationView());

			    windows[index].setLocationRelativeTo(mainPane);
			    windows[index].setVisible(true);
			}
			else if (windows[index].isVisible())
			    windows[index].toFront();
			else
			    windows[index].setVisible(true);

			int state = windows[index].getExtendedState();
			if ((state & SupplierWindow.ICONIFIED) != 0) {
			    windows[index].setExtendedState
				(state & ~SupplierWindow.ICONIFIED);
			}
		    }
		}
	    });

 	JScrollPane listScrollPane = new JScrollPane(list);
	mainPane.add(listScrollPane, BorderLayout.CENTER);
    }

    public JPanel getMainPane() {
	return mainPane;
    }
} // MonitorSelectorPanel
