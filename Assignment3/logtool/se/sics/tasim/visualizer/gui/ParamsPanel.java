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
 * ParamsPanel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2005/03/17 09:51:33 $
 *           $Revision: 1.7 $
 */

package se.sics.tasim.visualizer.gui;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.info.Supplier;

public class ParamsPanel {
    JPanel mainPane;
    JLabel simulationID, secondsPerDay, bankInterest, server;
  JLabel storageCostLabel;
    JLabel suppNomCap, suppMaxRFQs, suppDiscountFactor;

    public ParamsPanel(SimulationInfo simInfo) {
	mainPane = new JPanel();
	mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
	mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),
			    " Simulation Parameters "));

	simulationID = new JLabel("Simulation: " + simInfo.getSimulationID()
				  + " (" + simInfo.getSimulationType() + ')');
	server = new JLabel("Server: " + simInfo.getServer());
	secondsPerDay = new JLabel("Seconds per day: " +
				   simInfo.getSecondsPerDay());
	bankInterest = new JLabel("Bank interest: " +
				  simInfo.getBankInterest() + '%');
// 	bankInterest = new JLabel("Bank interest: [" +
// 				  simInfo.getBankInterestMin() + "," +
// 				  simInfo.getBankInterestMax() + "]");

	suppNomCap = new JLabel("Suppliers INCy: " +
				       Supplier.getNominalCapacity());
	suppMaxRFQs = new JLabel("Supplier max RFQs: " +
				 Supplier.getMaxRFQs());
	suppDiscountFactor = new JLabel("Supplier discount factor: " +
					Supplier.getDiscountFactor());
	mainPane.add(server);
	mainPane.add(simulationID);
	mainPane.add(secondsPerDay);
	mainPane.add(bankInterest);
	int storageCost = simInfo.getStorageCost();
	if (storageCost > 0) {
	  storageCostLabel = new JLabel("Storage Cost: " + storageCost
					+ '%');
	  mainPane.add(storageCostLabel);
	}
	mainPane.add(suppNomCap);
	mainPane.add(suppMaxRFQs);
	mainPane.add(suppDiscountFactor);
    }

    public JPanel getMainPane() {
	return mainPane;
    }
} // ParamsPanel
