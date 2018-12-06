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
 * GlobalAccountPanel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/06/18 13:06:25 $
 *           $Revision: 1.3 $
 */

package se.sics.tasim.visualizer.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.sics.tasim.visualizer.info.Manufacturer;
import javax.swing.ImageIcon;
import se.sics.tasim.visualizer.info.Supplier;
import javax.swing.Box;
import java.awt.Component;
import se.sics.tasim.visualizer.info.SimulationInfo;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import se.sics.tasim.visualizer.gui.PositiveRangeDiagram;

public class GlobalAccountPanel {
    JPanel mainPane;
    PositiveRangeDiagram accountDiagram;

    public GlobalAccountPanel(SimulationInfo simInfo,
			      PositiveBoundedRangeModel dayModel) {
	mainPane = new JPanel();
	mainPane.setLayout(new BorderLayout());
	mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),
			    " Account Balance "));
	mainPane.setMinimumSize(new Dimension(280,200));
	mainPane.setPreferredSize(new Dimension(280,200));

	accountDiagram = new PositiveRangeDiagram
	    (simInfo.getManufacturerCount(), dayModel);

	accountDiagram.addConstant(Color.black, 0);
	for (int i = 0, n = simInfo.getManufacturerCount(); i < n; i++) {
	    accountDiagram.setData(i, simInfo.getManufacturer(i).
				   getAccountBalance(), 1);
	    accountDiagram.setDotColor(i, Color.blue);
	}

	accountDiagram.setToolTipText("Account balance for all agents");
	mainPane.add(accountDiagram, BorderLayout.CENTER);
    }

    public JPanel getMainPane() {
	return mainPane;
    }
} // GlobalAccountPanel
