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
 * DayLengthMonitor
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Fri Jul  4 09:22:27 2003
 * Updated : $Date: 2004/03/29 11:49:07 $
 *           $Revision: 1.4 $
 */
package se.sics.tasim.visualizer.monitor;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;

/**
 */
public class DayLengthMonitor extends ParserMonitor {
  boolean verbose;
    int level;
    int[] length;
    int[] day;

    public void parseStarted() {
	level = getConfig().
	    getPropertyAsInt("monitor." + getName() + ".level", 0);
	verbose = getConfig().
	  getPropertyAsBoolean("verbose", false);
    }

    public boolean hasSimulationView() {
	return true;
    }

    public JComponent getSimulationView() {
	JPanel mainPane = new JPanel();
	mainPane.setLayout(new BorderLayout());
	mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	JLabel label = new JLabel("Days longer than " + level +
				  " milliseconds");
	mainPane.add(label, BorderLayout.NORTH);

	final JList list;
	if(day != null) {
	    String[] desc = new String[day.length];
	    for (int i = 0, n = day.length; i < n; i++)
		desc[i] = "Day: " + day[i] + "\t Length:" + length[i];
	    list = new JList(desc);
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		        PositiveBoundedRangeModel dayModel = getDayModel();
			if(dayModel != null && day != null)
			    dayModel.
				setCurrent(day[list.getMinSelectionIndex()]);
		    }
		});
	}
	else {
	    list = new JList();
	}

 	JScrollPane listScrollPane = new JScrollPane(list);
	mainPane.add(listScrollPane, BorderLayout.CENTER);

	return mainPane;
    }


    public void messageToRole(int sender, int role,
			      Transportable content) {
	if(!(content instanceof SimulationStatus))
	    return;

	SimulationStatus simStat = (SimulationStatus) content;
	int millis = simStat.getConsumedMillis();

	if(millis < level)
	    return;
	if (verbose) {
	  System.out.println(getSimulationParser().getSimulationID()
			     + ": day " + getSimulationParser().getCurrentDay()
			     + " started after " + millis + " ms");
	}
	length = ArrayUtils.add(length, millis);
	day = ArrayUtils.add(day, getSimulationParser().getCurrentDay());
    }



} // DayLengthMonitor
