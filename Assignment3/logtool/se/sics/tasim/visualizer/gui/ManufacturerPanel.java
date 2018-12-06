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
 * ManufacturerPanel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/07/15 07:31:17 $
 *           $Revision: 1.14 $
 */

package se.sics.tasim.visualizer.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import se.sics.tasim.visualizer.info.Manufacturer;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.monitor.ParserMonitor;

public class ManufacturerPanel {

    JPanel mainPane;
    JPanel diagramPane;
    JLabel image;
    PositiveRangeDiagram accountDiagram;
    Manufacturer manufacturer;
    JCheckBox name;

    PositiveBoundedRangeModel dayModel;
    AgentWindow agentWindow;
    SimulationInfo simInfo;
    ParserMonitor[] monitors;

    public ManufacturerPanel(final SimulationInfo simInfo,
			     final Manufacturer manufacturer,
			     final PositiveBoundedRangeModel dayModel,
			     final PCCommunication pcComModel,
			     final ComponentCommunication componentComModel,
			     final ParserMonitor[] monitors) {
    this.dayModel = dayModel;
    this.manufacturer = manufacturer;
    this.simInfo = simInfo;
    this.monitors = monitors;
    mainPane = new JPanel();
    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    mainPane.setBorder(BorderFactory.
		       createBevelBorder
		       (BevelBorder.RAISED));


    mainPane.addMouseListener(new MouseInputAdapter() {
	    public void mouseClicked(MouseEvent me) {
		openAgentWindow();
	    }
	});

    diagramPane = new JPanel();
    diagramPane.setLayout(new BoxLayout(diagramPane, BoxLayout.X_AXIS));

    accountDiagram = new PositiveRangeDiagram(1, dayModel);
    accountDiagram.setPreferredSize(new Dimension(120,50));
    accountDiagram.setMaximumSize(new Dimension(120,50));
    accountDiagram.setMinimumSize(new Dimension(120,50));
    accountDiagram.setData(0, manufacturer.getAccountBalance(), 1);
    accountDiagram.addConstant(Color.black, 0);
    accountDiagram.setToolTipText("Account Balance");
    accountDiagram.addMouseListener(new MouseInputAdapter() {
	    public void mouseClicked(MouseEvent me) {
		openAgentWindow();
	    }
	});

    image = new JLabel(GUITheme.getIcon("manufacturer-small.jpg"));

    name = new JCheckBox(manufacturer.getName(), true);
    name.setAlignmentX(Component.CENTER_ALIGNMENT);
    name.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent ce) {
		int i = simInfo.getManufacturerIndex(manufacturer);
		if(name.isSelected()) {
		    componentComModel.openAllXChannels
			(i, CommunicationModel.XCOM_ALL);
		    pcComModel.openAllXChannels(i,
						CommunicationModel.XCOM_ALL);
		}

		else {
		    componentComModel.closeAllXChannels
			(i, CommunicationModel.XCOM_ALL);
		    pcComModel.closeAllXChannels(i,
						 CommunicationModel.XCOM_ALL);
		}
	    }
	});

    diagramPane.add(image);
    diagramPane.add(Box.createRigidArea(new Dimension(5,0)));
    diagramPane.add(accountDiagram);

    mainPane.add(name);
    mainPane.add(diagramPane);
  }

    protected void openAgentWindow() {
        if(agentWindow == null) {
	    agentWindow = new AgentWindow(simInfo, manufacturer,
					  dayModel, monitors);
	    agentWindow.setLocationRelativeTo(mainPane);
	    agentWindow.setVisible(true);
	} else if (agentWindow.isVisible())
	    agentWindow.toFront();
	else
	  agentWindow.setVisible(true);
	int state = agentWindow.getExtendedState();
	if ((state & AgentWindow.ICONIFIED) != 0) {
	  agentWindow.setExtendedState(state & ~AgentWindow.ICONIFIED);
	}
    }

  public Component getMainPane() {
    return mainPane;
  }

} // ManufacturerPanel
