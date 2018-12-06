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
 * MainWindow
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/07/15 13:58:38 $
 *           $Revision: 1.15 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;

import se.sics.tasim.visualizer.gui.ColorLegendPanel;
import se.sics.tasim.visualizer.gui.GlobalAccountPanel;
import se.sics.tasim.visualizer.gui.ParamsPanel;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.monitor.ParserMonitor;

/**
 */
public class MainWindow extends JFrame {

    ParamsPanel paramsPane;
    GlobalAccountPanel accountPane;
    DayChanger dayChanger;
    ActorDisplay actorDisplay;

  public MainWindow(final SimulationInfo simInfo,
		    final PositiveBoundedRangeModel dayModel,
		    final ParserMonitor[] monitors) {
    super("Visualizer - main window");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    ComponentCommunication compComModel = new ComponentCommunication(simInfo);
    compComModel.openAllChannels(CommunicationModel.COM_ALL);

    PCCommunication pcComModel = new PCCommunication(simInfo);
    pcComModel.openAllChannels(CommunicationModel.COM_ALL);


    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints gblConstraints = new GridBagConstraints();
    Container pane = getContentPane();
    pane.setLayout(gbl);

    paramsPane = new ParamsPanel(simInfo);
    dayChanger = new DayChanger(dayModel);
    accountPane =
	new GlobalAccountPanel(simInfo, dayModel);
    actorDisplay = new ActorDisplay(simInfo, dayModel,
				    pcComModel, compComModel,
				    monitors);

    ColorLegendPanel colorPane = new ColorLegendPanel();

    MonitorSelectorPanel monitorPane =
	new MonitorSelectorPanel(monitors);

    gblConstraints.fill = GridBagConstraints.HORIZONTAL;
    gblConstraints.anchor = GridBagConstraints.NORTHWEST;
    gblConstraints.weighty = 0;

    gblConstraints.gridx = 0;
    gblConstraints.gridy = 0;
    gbl.setConstraints(paramsPane.getMainPane(), gblConstraints);
    pane.add(paramsPane.getMainPane());

    gblConstraints.gridx = 0;
    gblConstraints.gridy = 1;
    gbl.setConstraints(dayChanger.getMainPane(), gblConstraints);
    pane.add(dayChanger.getMainPane());

    gblConstraints.gridx = 0;
    gblConstraints.gridy = 2;
    gbl.setConstraints(accountPane.getMainPane(), gblConstraints);
    pane.add(accountPane.getMainPane());

//     gblConstraints.gridx = 0;
//     gblConstraints.gridy = 3;
//     gbl.setConstraints(colorPane.getMainPane(), gblConstraints);
//     pane.add(colorPane.getMainPane());

    gblConstraints.fill = GridBagConstraints.BOTH;
    gblConstraints.gridx = 0;
    gblConstraints.gridy = 4;
    gbl.setConstraints(monitorPane.getMainPane(), gblConstraints);
    pane.add(monitorPane.getMainPane());

    gblConstraints.fill = GridBagConstraints.HORIZONTAL;
    gblConstraints.weighty = 1;
    gblConstraints.weightx = 1;
    gblConstraints.gridx = 1;
    gblConstraints.gridy = 0;
    gblConstraints.gridheight = 5;
    gbl.setConstraints(actorDisplay.getMainPane(), gblConstraints);
    pane.add(actorDisplay.getMainPane());

    pack();
    setLocationRelativeTo(null);
  }

} // MainWindow
