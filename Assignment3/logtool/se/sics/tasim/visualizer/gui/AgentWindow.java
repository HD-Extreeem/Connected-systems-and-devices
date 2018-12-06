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
 * AgentWindow
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/07/24 13:33:16 $
 *           $Revision: 1.23 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.sics.tasim.visualizer.gui.DayChanger;
import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;
import se.sics.tasim.visualizer.gui.PositiveRangeDiagram;
import se.sics.tasim.visualizer.info.ComponentNegotiation;
import se.sics.tasim.visualizer.info.Factory;
import se.sics.tasim.visualizer.info.Manufacturer;
import se.sics.tasim.visualizer.info.PCNegotiation;
import se.sics.tasim.visualizer.info.PCType;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.monitor.ParserMonitor;

/**
 */
public class AgentWindow extends JFrame {
  SimulationInfo simInfo;
  DayChanger dayChanger;
  Manufacturer manufacturer;
  Factory factory;
  int manufacturerIndex;
  PositiveBoundedRangeModel dayModel;

  public AgentWindow(SimulationInfo simInfo, Manufacturer manufacturer,
		     PositiveBoundedRangeModel dayModel,
		     ParserMonitor[] monitors) {
      super(manufacturer.getName());
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.simInfo = simInfo;
      this.manufacturer = manufacturer;
      factory = manufacturer.getFactory();
      manufacturerIndex = simInfo.getManufacturerIndex(manufacturer);

      this.dayModel = dayModel;
      dayChanger = new DayChanger(dayModel);

      JTabbedPane tabPane = new JTabbedPane();
      tabPane.addTab("Bank", null, createBankPane(), "Financial information");
      tabPane.addTab("Factory", null, createFactoryPane(), "Information about the factory");
      tabPane.addTab("B2C", null, createCustomerPane(), "Customer communication");
      tabPane.addTab("PC Order Details", null, createPCCommPane(), "Detailed information about customer communication");
      tabPane.addTab("B2B", null, createSupplierPane(), "Supplier communication");

      tabPane.addTab("Component Order Details", null, createComponentCommPane(), "Detailed information about supplier communication");

      if(monitors != null) {
	  for (int i = 0, n = monitors.length; i < n; i++) {
	      if(monitors[i].hasAgentView(manufacturer)) {
		  tabPane.addTab("Monitor " + monitors[i].getName(), null,
				 monitors[i].getAgentView(manufacturer),
				 "Information from the monitor "
				 + monitors[i].getName());
	      }
	  }
      }

      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(tabPane, BorderLayout.CENTER);
      getContentPane().add(dayChanger.getMainPane(), BorderLayout.SOUTH);

      pack();
  }


    protected JPanel createPCCommPane() {
	JPanel pane = new JPanel();
	pane.setLayout(new BorderLayout());

	PCTree pcTree = new PCTree(manufacturer, dayModel, simInfo);

	JScrollPane treeView = new JScrollPane(pcTree);
	pane.add(treeView, BorderLayout.CENTER);

      return pane;
    }

    protected JPanel createComponentCommPane() {
	JPanel pane = new JPanel();
	pane.setLayout(new BorderLayout());

	ComponentTree compTree =
	  new ComponentTree(manufacturer, dayModel, simInfo);

	JScrollPane treeView = new JScrollPane(compTree);
	pane.add(treeView, BorderLayout.CENTER);

      return pane;
    }

    protected JPanel createBankPane() {
	PositiveRangeDiagram accountDiagram, diffDiagram,
	    interestDiagram, penaltyDiagram;


	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gblConstraints = new GridBagConstraints();
	gblConstraints.fill = GridBagConstraints.BOTH;

	JPanel pane = new JPanel();
	pane.setLayout(gbl);

      /*
      agentImage = new JLabel(new ImageIcon(agentIconFile));
      agentImage.setBorder(BorderFactory.createTitledBorder
			     (BorderFactory.createEtchedBorder(),
			      " " + manufacturer.getName() + " "));

      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 0;
      gbl.setConstraints(agentImage, gblConstraints);
      pane.add(agentImage);
      */

      accountDiagram = new PositiveRangeDiagram(1, dayModel);
      accountDiagram.setPreferredSize(new Dimension(200,150));
      accountDiagram.setData(0, manufacturer.getAccountBalance(), 1);
      accountDiagram.addConstant(Color.black, 0);
      accountDiagram.setBorder(BorderFactory.createTitledBorder(""));
      accountDiagram.setTitle(0, "Account Balance: $", "");
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 3;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 0;
      gbl.setConstraints(accountDiagram, gblConstraints);
      pane.add(accountDiagram);

      int[] diff = new int[dayModel.getLast()-1];
      diff[0] = 0;
      for (int i = 1, n = dayModel.getLast()-1; i < n; i++) {
	  diff[i] = (manufacturer.getAccountBalance(i+1) -
	      manufacturer.getAccountBalance(i-1)) / 2;
      }

      diffDiagram = new PositiveRangeDiagram(1, dayModel);
      diffDiagram.setPreferredSize(new Dimension(200,150));
      diffDiagram.setData(0, diff, 1);
      diffDiagram.addConstant(Color.black, 0);
      diffDiagram.setBorder(BorderFactory.createTitledBorder(""));
      diffDiagram.setTitle(0, "Account diff: $", "");

      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 1;
      gbl.setConstraints(diffDiagram, gblConstraints);
      pane.add(diffDiagram);

      interestDiagram = new PositiveRangeDiagram(1, dayModel);
      interestDiagram.setPreferredSize(new Dimension(200,150));
      interestDiagram.setData(0, manufacturer.getBankInterest(), 1);
      interestDiagram.addConstant(Color.black, 0);
      interestDiagram.setBorder(BorderFactory.createTitledBorder(""));
      interestDiagram.setTitle(0, "Account interest: $", "");

      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 1;
      gblConstraints.gridy = 1;
      gbl.setConstraints(interestDiagram, gblConstraints);
      pane.add(interestDiagram);


      int[] penaltyAmount = new int[dayModel.getLast()+1];
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  PCNegotiation[] pcNeg = simInfo.getPCNegotiation(i);
	  if(pcNeg == null) {
	      penaltyAmount[i] = 0;
	      continue;
	  }
	  // Sum up penalties for day i
	  for (int j = 0, m = pcNeg.length; j < m; j++) {

	      // Only consider this agents orders with penalties
	      if(pcNeg[j].getOrderWinner() != manufacturer ||
		 pcNeg[j].getPenaltyCount() == 0)
		  continue;

	      /*
	      System.err.println("Agent: " +
				 manufacturer.getName() +
				 " sent: " +
				 i +
				 " dueDate: " +
				 pcNeg[j].getRFQDueDate() +
				 " pen: " +
				 pcNeg[j].getPenaltyCount());
	      */
	      int penalty = pcNeg[j].getRFQPenalty();

	      // Iterate over days on wich penalty is received
	      for (int k = pcNeg[j].getRFQDueDate()+1,
		       o = k+pcNeg[j].getPenaltyCount();
		   k < o && k < penaltyAmount.length; k++) {

		  penaltyAmount[k] += penalty;
	      }
	  }
      }


      penaltyDiagram = new PositiveRangeDiagram(1, dayModel);
      penaltyDiagram.setPreferredSize(new Dimension(200,150));
      penaltyDiagram.setData(0, penaltyAmount, 1);
      penaltyDiagram.addConstant(Color.black, 0);
      penaltyDiagram.setBorder(BorderFactory.createTitledBorder(""));
      penaltyDiagram.setTitle(0, "Penalty amount: $", "");

      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 2;
      gblConstraints.gridy = 1;
      gbl.setConstraints(penaltyDiagram, gblConstraints);
      pane.add(penaltyDiagram);

      return pane;
    }

    protected JPanel createFactoryPane() {
	PositiveRangeDiagram utilDiagram, compHoldingDiagram, pcHoldingDiagram;

      GridBagLayout gbl = new GridBagLayout();
      GridBagConstraints gblConstraints = new GridBagConstraints();
      gblConstraints.fill = GridBagConstraints.BOTH;

      JPanel pane = new JPanel();
      pane.setLayout(gbl);

      /*
      factoryImage = new JLabel(new ImageIcon(factoryIconFile));
      factoryImage.setBorder(BorderFactory.createTitledBorder
			     (BorderFactory.createEtchedBorder(),
			      " Factory "));
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 0;
      gbl.setConstraints(factoryImage, gblConstraints);
      pane.add(factoryImage);
      */

      int[] util = new int[dayModel.getLast()+1];
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++)
	  util[i] = (int) (100*manufacturer.getFactory().getUtilization(i));

      utilDiagram = new PositiveRangeDiagram(1, dayModel);
      utilDiagram.setPreferredSize(new Dimension(200,150));
      utilDiagram.setData(0, util, 1);
      utilDiagram.addConstant(Color.black, 0);
      utilDiagram.setBorder(BorderFactory.createTitledBorder(""));
      utilDiagram.setTitle(0, "Factory utilization: ", "%");
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 2;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 0;
      gbl.setConstraints(utilDiagram, gblConstraints);
      pane.add(utilDiagram);


      TemporalBarDiagram componentBars = new TemporalBarDiagram(dayModel);
      int nComp = factory.getNumberOfComponentTypes();
      int[][] comp = new int[dayModel.getLast()+1][nComp];
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  for (int j = 0, m = nComp; j < m; j++) {
	      comp[i][j] = factory.getComponentHolding(j,i);
	  }
      }
      componentBars.setData(comp);
      componentBars.setPreferredSize(new Dimension(100,200));
      componentBars.setBorder(BorderFactory
			      .createTitledBorder(" Component Holding "));

      String[] compNames = new String[nComp];
      for (int j = 0, m = nComp; j < m; j++)
	  compNames[j] = factory.getComponentType(j).getName();
      componentBars.setNames(compNames);
      componentBars.setToolTipVisible(true);

      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 1;
      gbl.setConstraints(componentBars, gblConstraints);
      pane.add(componentBars);

      TemporalBarDiagram pcBars = new TemporalBarDiagram(dayModel);
      int nPCs = factory.getNumberOfPCTypes();
      int[][] pc = new int[dayModel.getLast()+1][nPCs];
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  for (int j = 0, m = nPCs; j < m; j++) {
	      pc[i][j] = factory.getPCHolding(j,i);
	  }
      }
      pcBars.setData(pc);
      pcBars.setPreferredSize(new Dimension(100,200));
      pcBars.setBorder(BorderFactory.createTitledBorder(" PC Holding "));
      String[] pcNames = new String[nPCs];

      for (int j = 0, m = nPCs; j < m; j++)
	  pcNames[j] = factory.getPCType(j).getName();
      pcBars.setNames(pcNames);
      pcBars.setToolTipVisible(true);
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 2;
      gblConstraints.gridx = 1;
      gblConstraints.gridy = 1;
      gbl.setConstraints(pcBars, gblConstraints);
      pane.add(pcBars);

      int[] compHolding = new int[dayModel.getLast()+1];
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  for (int j = 0, m = nComp; j < m; j++) {
	      compHolding[i] += comp[i][j];
	  }
      }
      compHoldingDiagram = new PositiveRangeDiagram(1, dayModel);
      compHoldingDiagram.setPreferredSize(new Dimension(200,150));
      compHoldingDiagram.setData(0, compHolding, 1);
      compHoldingDiagram.addConstant(Color.black, 0);
      compHoldingDiagram.setBorder(BorderFactory.createTitledBorder(""));
      compHoldingDiagram.setTitle(0, "Total component holding: ", "");
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 2;
      gbl.setConstraints(compHoldingDiagram, gblConstraints);
      pane.add(compHoldingDiagram);


      int[] pcHolding = new int[dayModel.getLast()+1];
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  for (int j = 0, m = nPCs; j < m; j++) {
	      pcHolding[i] += pc[i][j];
	  }
      }
      pcHoldingDiagram = new PositiveRangeDiagram(1, dayModel);
      pcHoldingDiagram.setPreferredSize(new Dimension(200,150));
      pcHoldingDiagram.setData(0, pcHolding, 1);
      pcHoldingDiagram.addConstant(Color.black, 0);
      pcHoldingDiagram.setBorder(BorderFactory.createTitledBorder(""));
      pcHoldingDiagram.setTitle(0, "Total PC holding: ", "");
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 1;
      gblConstraints.gridy = 2;
      gbl.setConstraints(pcHoldingDiagram, gblConstraints);
      pane.add(pcHoldingDiagram);


      return pane;
    }



    protected JPanel createCustomerPane() {
	PositiveRangeDiagram receivedPCRFQDiagram,
	    sentPCOfferDiagram,
	    receivedPCOrderDiagram,
	    allComDiagram;

      GridBagLayout gbl = new GridBagLayout();
      GridBagConstraints gblConstraints = new GridBagConstraints();
      gblConstraints.fill = GridBagConstraints.BOTH;

      JPanel pane = new JPanel();
      pane.setLayout(gbl);

      int[] pcRFQsReceived = new int[dayModel.getLast()+1];
      int[] pcOffersSent = new int[dayModel.getLast()+1];
      int[] pcOrdersReceived = new int[dayModel.getLast()+1];

      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  PCNegotiation[] pcNeg = simInfo.getPCNegotiation(i);
	  if(pcNeg == null) {
	      pcRFQsReceived[i] = 0;
	      pcOffersSent[i] = 0;
	  }

	  else {
	      pcRFQsReceived[i] = pcNeg.length;

	      // Count those wich manufacturer is bidding on
	      for (int j = 0, m = pcNeg.length; j < m; j++) {
		  if(pcNeg[j].isBidding(manufacturer))
		      pcOffersSent[i]++;
	      }
	  }


	  // Get negotiations from yesterday
	  if(i == 0)
	      continue;
	  pcNeg = simInfo.getPCNegotiation(i-1);

	  if(pcNeg == null)
	      continue;

	  // Count won orders
	  for (int j = 0, m = pcNeg.length; j < m; j++) {
	      if(pcNeg[j].getOrderWinner() == manufacturer)
		  pcOrdersReceived[i]++;
	  }

	  /*
	  if (simInfo.getReceivedPCRFQs(i, manufacturerIndex) == null)
	      pcRFQsReceived[i] = 0;
	  else
	      pcRFQsReceived[i] =
		  simInfo.getReceivedPCRFQs(i, manufacturerIndex).length;

	  if (simInfo.getSentPCOffers(i, manufacturerIndex) == null)
	      pcOffersSent[i] = 0;
	  else
	      pcOffersSent[i] =
		  simInfo.getSentPCOffers(i, manufacturerIndex).length;

	  if (simInfo.getReceivedPCOrders(i, manufacturerIndex) == null)
	      pcOrdersReceived[i] = 0;
	  else
	      pcOrdersReceived[i] =
		  simInfo.getReceivedPCOrders(i, manufacturerIndex).length;

	  */
      }


      allComDiagram = new PositiveRangeDiagram(3, dayModel);
      allComDiagram.setPreferredSize(new Dimension(200,150));
      allComDiagram.setData(0, pcRFQsReceived, 1);
      allComDiagram.setData(1, pcOffersSent, 1);
      allComDiagram.setData(2, pcOrdersReceived, 1);
      allComDiagram.setDotColor(0, GUITheme.RFQ_COLOR);
      allComDiagram.setDotColor(1, GUITheme.OFFER_COLOR);
      allComDiagram.setDotColor(2, GUITheme.ORDER_COLOR.darker());
      allComDiagram.addConstant(Color.black, 0);
      allComDiagram.setBorder(BorderFactory.createTitledBorder
			      ("All customer communication"));
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 3;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 0;
      gbl.setConstraints(allComDiagram, gblConstraints);
      pane.add(allComDiagram);


      receivedPCRFQDiagram = new PositiveRangeDiagram(1, dayModel);
      receivedPCRFQDiagram.setPreferredSize(new Dimension(200,150));
      receivedPCRFQDiagram.setData(0, pcRFQsReceived, 1);
      receivedPCRFQDiagram.setDotColor(0, GUITheme.RFQ_COLOR);
      receivedPCRFQDiagram.addConstant(Color.black, 0);
      receivedPCRFQDiagram.setBorder(BorderFactory.createTitledBorder(""));
      receivedPCRFQDiagram.setTitle(0, "Received PC RFQs: ", "");
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 0;
      gblConstraints.gridy = 1;
      gbl.setConstraints(receivedPCRFQDiagram, gblConstraints);
      pane.add(receivedPCRFQDiagram);


      sentPCOfferDiagram = new PositiveRangeDiagram(1, dayModel);
      sentPCOfferDiagram.setPreferredSize(new Dimension(200,150));
      sentPCOfferDiagram.setData(0, pcOffersSent, 1);
      sentPCOfferDiagram.setDotColor(0, GUITheme.OFFER_COLOR);
      sentPCOfferDiagram.addConstant(Color.black, 0);
      sentPCOfferDiagram.setBorder(BorderFactory.createTitledBorder(""));
      sentPCOfferDiagram.setTitle(0, "Sent PC Offers: ", "");
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridx = 1;
      gblConstraints.gridy = 1;
      gbl.setConstraints(sentPCOfferDiagram, gblConstraints);
      pane.add(sentPCOfferDiagram);


      receivedPCOrderDiagram = new PositiveRangeDiagram(1, dayModel);
      receivedPCOrderDiagram.setPreferredSize(new Dimension(200,150));
      receivedPCOrderDiagram.setData(0, pcOrdersReceived, 1);
      receivedPCOrderDiagram.setDotColor(0, GUITheme.ORDER_COLOR.darker());
      receivedPCOrderDiagram.addConstant(Color.black, 0);
      receivedPCOrderDiagram.setBorder(BorderFactory.createTitledBorder(""));
      receivedPCOrderDiagram.setTitle(0, "Received PC Orders: ", "");
      gblConstraints.weightx = 1;
      gblConstraints.weighty = 1;
      gblConstraints.gridwidth = 1;
      gblConstraints.gridx = 2;
      gblConstraints.gridy = 1;
      gbl.setConstraints(receivedPCOrderDiagram, gblConstraints);
      pane.add(receivedPCOrderDiagram);

      return pane;
    }

    protected JPanel createSupplierPane() {
	Dimension preferredSize = new Dimension(200,150);
	JPanel pane = new JPanel(new GridLayout(0, 2));

	int size = dayModel.getLast()+1;
	int[] compRFQsSent = new int[size];
	int[] compUnitRFQsSent = new int[size];
	int[] compOrdersSent = new int[size];
	int[] compUnitOrdersSent = new int[size];

	for (int i = 0, n = size - 1; i < n; i++) {
	    ComponentNegotiation[] compNegotiation =
		simInfo.getComponentNegotiation(i, manufacturer);
	    if (compNegotiation != null) {
	      compRFQsSent[i] = compNegotiation.length;

	      int totalQuantityRequested = 0;
	      for (int j = 0, m = compNegotiation.length; j < m; j++) {
		totalQuantityRequested += compNegotiation[j].getRFQQuantity();
		if(compNegotiation[j].getOrderType() !=
		   ComponentNegotiation.NO_ORDER) {
		  compOrdersSent[i+1]++;
		  compUnitOrdersSent[i + 1] +=
		    compNegotiation[j].getOrderedQuantity();
		}
	      }
	      compUnitRFQsSent[i] = totalQuantityRequested;
	    }
	}

	/*
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  if (simInfo.getSentComponentRFQs(i, manufacturerIndex) == null)
	      compRFQsSent[i] = 0;
	  else
	      compRFQsSent[i] =
		  simInfo.getSentComponentRFQs(i, manufacturerIndex).length;
      }
	*/


      /*
      for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
	  if (simInfo.getSentComponentOrders(i, manufacturerIndex) == null)
	      compOrdersSent[i] = 0;
	  else
	      compOrdersSent[i] =
		  simInfo.getSentComponentOrders(i, manufacturerIndex).length;
      }
      */
      pane.add(createDiagram(preferredSize, compRFQsSent,
			     "Sent component RFQs: ", ""));
      pane.add(createDiagram(preferredSize, compOrdersSent,
			     "Sent component orders: ", ""));
      pane.add(createDiagram(preferredSize, compUnitRFQsSent,
			     "Sent RFQs for ", " components"));
      pane.add(createDiagram(preferredSize, compUnitOrdersSent,
			     "Sent orders for ", " components"));

      return pane;
    }

  private PositiveRangeDiagram createDiagram(Dimension preferredSize,
					     int[] data,
					     String titlePrefix,
					     String titlePostfix) {
    PositiveRangeDiagram diagram = new PositiveRangeDiagram(1, dayModel);
    diagram.setPreferredSize(preferredSize);
    diagram.setData(0, data, 1);
    diagram.addConstant(Color.black, 0);
    diagram.setBorder(BorderFactory.createTitledBorder(""));
    diagram.setTitle(0, titlePrefix, titlePostfix);
    return diagram;
  }

} // AgentWindow
