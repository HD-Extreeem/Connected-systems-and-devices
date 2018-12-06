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
 * RankMonitor
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Fri Jul  4 09:22:27 2003
 * Updated : $Date: 2003/07/15 13:00:55 $
 *           $Revision: 1.3 $
 */
package se.sics.tasim.visualizer.monitor;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import se.sics.isl.util.FormatUtils;
import se.sics.tasim.visualizer.info.Manufacturer;
import se.sics.tasim.visualizer.util.SimulationParser;

/**
 */
public class RankMonitor extends ParserMonitor {
    private String[] names;
    private int[] finalScores;

    public void parseStopped() {
        SimulationParser simParser = getSimulationParser();
	Manufacturer[] man = simParser.getManufacturers();
	if(man == null)
	    return;

	names = new String[man.length];
	finalScores = new int[man.length];
	int lastDay = simParser.getNumberOfDays()-1;
	for (int i = 0, n = man.length; i < n; i++) {
	    names[i] = man[i].getName();
	    finalScores[i] = man[i].getAccountBalance(lastDay);
	}

	String swpName;
	int swpScore;
	for (int i = 0, n = finalScores.length; i < n; i++) {
	    int max = i;

	    for (int j = i+1, m = finalScores.length; j < m; j++) {
		if(finalScores[j] > finalScores[max])
		    max = j;
	    }

	    swpScore = finalScores[i];
	    finalScores[i] = finalScores[max];
	    finalScores[max] = swpScore;

	    swpName = names[i];
	    names[i] = names[max];
	    names[max] = swpName;
	}
    }

    public boolean hasSimulationView() {
	return true;
    }

    public JComponent getSimulationView() {
	JPanel mainPane = new JPanel();
	mainPane.setLayout(new BorderLayout());
	mainPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

	JLabel label = new JLabel("Final score of the agents");
	mainPane.add(label, BorderLayout.NORTH);

	final JList list;
	String[] desc = new String[names.length];
	for (int i = 0, n = names.length; i < n; i++)
	    desc[i] = "" + FormatUtils.formatAmount(finalScores[i]) + "  Agent: " + names[i];
	list = new JList(desc);

 	JScrollPane listScrollPane = new JScrollPane(list);
	mainPane.add(listScrollPane, BorderLayout.CENTER);

	return mainPane;
    }

} // RankMonitor
