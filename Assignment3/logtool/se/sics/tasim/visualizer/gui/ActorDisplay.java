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
 * ActorDisplay
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/07/26 16:19:40 $
 *           $Revision: 1.20 $
 */

package se.sics.tasim.visualizer.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import se.sics.tasim.visualizer.gui.TemporalArrowMesh;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.monitor.ParserMonitor;

public class ActorDisplay {
  JPanel mainPane, supplierPane, agentPane, componentPane;
  ManufacturerPanel[] mp;
  SupplierPanel[] sp;
  CustomerPanel cp;
  //ArrowMesh componentComunication;
  //ArrowMesh pcComunication;

  ComponentArrows componentArrows;
  PCArrows pcArrows;
    PCCommunication pcComModel;
    ComponentCommunication componentComModel;

  public ActorDisplay(SimulationInfo simInfo,
		      PositiveBoundedRangeModel dayModel,
		      PCCommunication pcComModel,
		      ComponentCommunication componentComModel,
		      ParserMonitor[] monitors) {

    mainPane = new JPanel();
    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gblConstraints = new GridBagConstraints();
    mainPane.setLayout(gbl);

    mainPane.setBorder(BorderFactory.createTitledBorder
		       (" Game situation "));

    this.componentComModel = componentComModel;
    this.pcComModel = pcComModel;

    sp = new SupplierPanel[simInfo.getSupplierCount()];
    gblConstraints.gridx = 0;
    gblConstraints.weighty = 0;
    gblConstraints.anchor = GridBagConstraints.WEST;
    gblConstraints.insets = new Insets(1,2,1,2);
    for (int i = 0, n = simInfo.getSupplierCount(); i < n; i++) {
      sp[i] = new SupplierPanel(simInfo, simInfo.getSupplier(i),
				dayModel, componentComModel);
      gblConstraints.gridy = i;
      gbl.setConstraints(sp[i].getMainPane(), gblConstraints);
      mainPane.add(sp[i].getMainPane(), gblConstraints);
    }

    mp = new ManufacturerPanel[simInfo.getManufacturerCount()];
    gblConstraints.gridx = 2;
    gblConstraints.weighty = 0;
    gblConstraints.anchor = GridBagConstraints.CENTER;
    for (int i = 0, n = simInfo.getManufacturerCount(); i < n; i++) {
      mp[i] = new ManufacturerPanel(simInfo,
				    simInfo.getManufacturer(i),
				    dayModel,
				    pcComModel,
				    componentComModel,
				    monitors);

      gblConstraints.gridy = i+1;
      gbl.setConstraints(mp[i].getMainPane(), gblConstraints);
      mainPane.add(mp[i].getMainPane(), gblConstraints);
    }


    cp = new CustomerPanel(simInfo, simInfo.getCustomer(0),
			   dayModel, pcComModel);
    gblConstraints.gridx = 4;
    gblConstraints.gridy = 3;
    gblConstraints.gridheight = 2;
    gblConstraints.anchor = GridBagConstraints.EAST;
    gblConstraints.fill = GridBagConstraints.VERTICAL;
    gblConstraints.weighty = 0;

    gbl.setConstraints(cp.getMainPane(), gblConstraints);
    mainPane.add(cp.getMainPane(), gblConstraints);

    gblConstraints.anchor = GridBagConstraints.EAST;
    gblConstraints.gridy = simInfo.getSupplierCount() - 1;
    gblConstraints.fill = GridBagConstraints.NONE;
    mainPane.add(new ColorLegendPanel().getMainPane(), gblConstraints);

    componentArrows = new ComponentArrows(componentComModel, dayModel, simInfo,
					  0.11, 0.018, 0.11, 0.018);
    componentArrows.setPreferredSize(new Dimension(100,100));
    gblConstraints.gridx = 1;
    gblConstraints.gridy = 0;
    gblConstraints.gridheight = 8;
    gblConstraints.fill = GridBagConstraints.BOTH;
    gblConstraints.weighty = 1;
    gblConstraints.weightx = 0.5;
    gbl.setConstraints(componentArrows, gblConstraints);
    mainPane.add(componentArrows);

    pcArrows = new PCArrows(pcComModel, dayModel, simInfo,
			    0.11, 0.018, 0.11, 0.018);
    pcArrows.setPreferredSize(new Dimension(100,100));
    gblConstraints.gridx = 3;
    gblConstraints.gridy = 0;
    gblConstraints.gridheight = 8;
    gblConstraints.fill = GridBagConstraints.BOTH;
    gblConstraints.weighty = 1;
    gblConstraints.weightx = 0.5;
    gbl.setConstraints(pcArrows, gblConstraints);
    mainPane.add(pcArrows);
    //componentComunication.setArrow(1, 1, 0.5, 0, 0.3,
    //		       ArrowMesh.DIRECTION_FORWARD);

  }

  public JPanel getMainPane() {
    return mainPane;
  }
} // ActorDisplay
