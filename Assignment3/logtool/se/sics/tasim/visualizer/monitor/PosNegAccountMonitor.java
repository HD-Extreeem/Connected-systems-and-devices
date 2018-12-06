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
 * PosNegAccountMonitor
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Fri Jul  4 09:22:27 2003
 * Updated : $Date: 2003/07/15 13:55:59 $
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
import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;
import se.sics.tasim.visualizer.info.Actor;
import se.sics.tasim.visualizer.info.Manufacturer;

/**
 */
public class PosNegAccountMonitor extends ParserMonitor {
  private String agentName;
  private Manufacturer manufacturer;
  private int[] day;

    /**
     * Reads the agent name from the config file property
     * <code>monitor.&lt;MONITOR NAME&gt;.agent</code>.
     *
     */
    public void parseStarted() {
      agentName = getConfig().
	getProperty("monitor." + getName() + ".agent");
    }

    public boolean hasAgentView(Actor actor) {
      return manufacturer == actor && manufacturer != null;
    }

    public JComponent getAgentView(Actor actor) {
        if (manufacturer != actor)
	  return new JPanel();

	JPanel mainPane = new JPanel();
	mainPane.setLayout(new BorderLayout());
	mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	JLabel label = new JLabel("Days when account balance passed 0$");
	mainPane.add(label, BorderLayout.NORTH);

	final JList list;
	if(day != null) {
	    String[] desc = new String[day.length];
	    for (int i = 0, n = day.length; i < n; i++)
		desc[i] = "Day: " + day[i];
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

    public void parseStopped() {
	Manufacturer[] man = getSimulationParser().getManufacturers();
	if(man == null)
	    return;

	int[] account = null;
	for (int i = 0, n = man.length; i < n; i++) {
	    if(man[i].getName().equals(agentName)) {
	      this.manufacturer = man[i];
	      account = man[i].getAccountBalance();
	      break;
	    }
	}

	if(account == null)
	    return;

	for (int i = 0, n = account.length-1; i < n; i++) {
	    if((account[i] <= 0 && account[i+1] > 0) ||
	       (account[i] >= 0 && account[i+1] < 0)) {
		day = ArrayUtils.add(day, i);
	    }
	}
    }
} // PosNegAccountMonitor
