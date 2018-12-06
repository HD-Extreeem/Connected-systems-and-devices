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
 * SupplierWindow
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2005/04/11 12:17:28 $
 *           $Revision: 1.13 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import se.sics.tasim.visualizer.gui.DayChanger;
import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;
import se.sics.tasim.visualizer.gui.PositiveRangeDiagram;
import se.sics.tasim.visualizer.info.Component;
import se.sics.tasim.visualizer.info.ComponentNegotiation;
import se.sics.tasim.visualizer.info.Factory;
import se.sics.tasim.visualizer.info.Manufacturer;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.info.Supplier;

/**
 */
public class SupplierWindow extends JFrame {
    public final static int diagramWidth = 300;
    public final static int diagramHeight = 150;
    SimulationInfo simInfo;
    DayChanger dayChanger;
    Supplier supplier;
    int supplierIndex;
    PositiveBoundedRangeModel dayModel;

  public SupplierWindow(SimulationInfo simInfo, Supplier supplier,
			 PositiveBoundedRangeModel dayModel) {
      super(supplier.getName());
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      this.simInfo = simInfo;
      this.supplier = supplier;
      supplierIndex = simInfo.getSupplierIndex(supplier);

      this.dayModel = dayModel;
      dayChanger = new DayChanger(dayModel);

      JTabbedPane tabPane = new JTabbedPane();
      tabPane.addTab("Production - " + supplier.getComponent(0).getName(),
		     null,
		     createProductionLinePane(0, supplier.getComponent(0)),
		     "Information about suppliers first production line");
      tabPane.addTab("Orders - " + supplier.getComponent(0).getName(),
		     null,
		     createOrderLinePane(0, supplier.getComponent(0)),
		     "Orders from suppliers first production line");
      tabPane.addTab("Production - " + supplier.getComponent(1).getName(),
		     null,
		     createProductionLinePane(1, supplier.getComponent(1)),
		     "Information about suppliers second production line");
      tabPane.addTab("Orders - " + supplier.getComponent(1).getName(),
		     null,
		     createOrderLinePane(1, supplier.getComponent(1)),
		     "Orders from suppliers second production line");

      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(tabPane, BorderLayout.CENTER);
      getContentPane().add(dayChanger.getMainPane(), BorderLayout.SOUTH);

      pack();
  }

    protected JPanel createProductionLinePane(int line, Component component) {
	if(line < 0 || line > 1)
	    return null;

	PositiveRangeDiagram capDiagram, prodDiagram, comboDiagram,
	    inventoryDiagram;

	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gblConstraints = new GridBagConstraints();
	gblConstraints.fill = GridBagConstraints.BOTH;

	JPanel pane = new JPanel();
	pane.setLayout(gbl);



	capDiagram = new PositiveRangeDiagram(1, dayModel);
	capDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	capDiagram.setData(0, supplier.getCapacity(line), 1);
	capDiagram.setDotColor(0, Color.blue);
	capDiagram.addConstant(Color.black, 0);
	capDiagram.addConstant(GUITheme.NOMINAL_CAPACITY_COLOR,
			       Supplier.getNominalCapacity());
	capDiagram.setBorder(BorderFactory.createTitledBorder(""));
	capDiagram.setTitle(0, "Capacity: ", "");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 0;
	gblConstraints.gridy = 0;
	gbl.setConstraints(capDiagram, gblConstraints);
	pane.add(capDiagram);



	prodDiagram = new PositiveRangeDiagram(1, dayModel);
	prodDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	prodDiagram.setData(0, supplier.getProduction(line), 1);
	prodDiagram.setDotColor(0, Color.red);
	prodDiagram.addConstant(Color.black, 0);
	prodDiagram.addConstant(GUITheme.NOMINAL_CAPACITY_COLOR,
				Supplier.getNominalCapacity());
	prodDiagram.setBorder(BorderFactory.createTitledBorder(""));
	prodDiagram.setTitle(0, "Production: ", "");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 1;
	gblConstraints.gridy = 0;
	gbl.setConstraints(prodDiagram, gblConstraints);
	pane.add(prodDiagram);

	inventoryDiagram = new PositiveRangeDiagram(1, dayModel);
	inventoryDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	inventoryDiagram.setData(0, supplier.getInventory(line), 1);
	inventoryDiagram.addConstant(Color.black, 0);
	inventoryDiagram.setBorder(BorderFactory.createTitledBorder(""));
	inventoryDiagram.setTitle(0, "Component inventory: ", "");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 0;
	gblConstraints.gridy = 1;
	gbl.setConstraints(inventoryDiagram, gblConstraints);
	pane.add(inventoryDiagram);


	comboDiagram = new PositiveRangeDiagram(2, dayModel);
	comboDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	comboDiagram.setData(0, supplier.getCapacity(line), 1);
	comboDiagram.setData(1, supplier.getProduction(line), 1);
	comboDiagram.setDotColor(0, Color.blue);
	comboDiagram.setDotColor(1, Color.red);
	comboDiagram.addConstant(GUITheme.NOMINAL_CAPACITY_COLOR,
				 Supplier.getNominalCapacity());
	comboDiagram.addConstant(Color.black, 0);
	comboDiagram.setBorder(BorderFactory.createTitledBorder
			       ("Nominal capacity / Capacity / Production"));
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 1;
	gblConstraints.gridy = 1;
	gbl.setConstraints(comboDiagram, gblConstraints);
	pane.add(comboDiagram);

      return pane;
    }

    protected JPanel createOrderLinePane(int line, Component component) {
	if(line < 0 || line > 1)
	    return null;

	PositiveRangeDiagram priceTodayDiagram, priceDueDateDiagram,
	    ordersTodayDiagram, ordersDueDateDiagram,
	    noCompTodayDiagram, noCompDueDateDiagram;

	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gblConstraints = new GridBagConstraints();
	gblConstraints.fill = GridBagConstraints.BOTH;

	JPanel pane = new JPanel();
	pane.setLayout(gbl);


	int[] priceToday = new int[dayModel.getLast()+1];
	int[] ordersToday = new int[dayModel.getLast()+1];
	int[] noCompToday = new int[dayModel.getLast()+1];
	int[] priceDueDate = new int[dayModel.getLast()+1];
	int[] ordersDueDate = new int[dayModel.getLast()+1];
	int[] noCompDueDate = new int[dayModel.getLast()+1];
	for (int i = 1, n = dayModel.getLast()+1; i < n; i++) {
	    ComponentNegotiation[] compNeg =
		simInfo.getComponentNegotiation(i-1, supplier);

	    if(compNeg == null || compNeg.length == 0)
		continue;

	    for (int j = 0, m = compNeg.length; j < m; j++) {
		if(compNeg[j].getComponent() != component ||
		   compNeg[j].getOrderType() == ComponentNegotiation.NO_ORDER)
		    continue;

		int quantity = compNeg[j].getOrderedQuantity();
		int fullPrice = compNeg[j].getOrderPrice() * quantity;
		priceToday[i] += fullPrice;
		noCompToday[i] += quantity;
		ordersToday[i]++;

		if(compNeg[j].getOrderDueDate() >= priceDueDate.length)
		    continue;

		priceDueDate[compNeg[j].getOrderDueDate()] += fullPrice;
		noCompDueDate[compNeg[j].getOrderDueDate()] += quantity;
		ordersDueDate[compNeg[j].getOrderDueDate()]++;
	    }

	    if(ordersToday[i] == 0)
		continue;

	    priceToday[i] = noCompToday[i] == 0
	      ? 0
	      : (priceToday[i] / noCompToday[i]);
	}

	// compute average for due date price diagram
	for (int i = 0, n = priceDueDate.length; i < n; i++) {
	    if(ordersDueDate[i] == 0)
		continue;

	    priceDueDate[i] = noCompDueDate[i] == 0
	      ? 0
	      : (priceDueDate[i] / noCompDueDate[i]);
	}

	priceTodayDiagram = new PositiveRangeDiagram(1, dayModel);
	priceTodayDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	priceTodayDiagram.setData(0, priceToday, 1);
	priceTodayDiagram.addConstant(Color.black, 0);
	priceTodayDiagram.addConstant
	    (Color.green.darker(),
	     supplier.getComponent(line).getBasePrice());
	priceTodayDiagram.setBorder(BorderFactory.createTitledBorder(""));
	priceTodayDiagram.setTitle
	    (0, "Avg price (ordered today): ", " $/unit");
	priceTodayDiagram.setToolTipText
	    ("Average price for the components ordered on the current day");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 0;
	gblConstraints.gridy = 0;
	gbl.setConstraints(priceTodayDiagram, gblConstraints);
	pane.add(priceTodayDiagram);


	ordersTodayDiagram = new PositiveRangeDiagram(1, dayModel);
	ordersTodayDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	ordersTodayDiagram.setData(0, ordersToday, 1);
	ordersTodayDiagram.addConstant(Color.black, 0);
	ordersTodayDiagram.setBorder(BorderFactory.createTitledBorder(""));
	ordersTodayDiagram.setTitle
	    (0, "Number of orders (placed today): ", "");
	ordersTodayDiagram.setToolTipText
	    ("Number of component orders placed on the current day");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 0;
	gblConstraints.gridy = 1;
	gbl.setConstraints(ordersTodayDiagram, gblConstraints);
	pane.add(ordersTodayDiagram);


	noCompTodayDiagram = new PositiveRangeDiagram(1, dayModel);
	noCompTodayDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	noCompTodayDiagram.setData(0, noCompToday, 1);
	noCompTodayDiagram.addConstant(Color.black, 0);
	noCompTodayDiagram.setBorder(BorderFactory.createTitledBorder(""));
	noCompTodayDiagram.setTitle
	    (0, "Ordered components (ordered today): ", "");
	noCompTodayDiagram.setToolTipText
	    ("Total number of components ordered on the current day");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 0;
	gblConstraints.gridy = 2;
	gbl.setConstraints(noCompTodayDiagram, gblConstraints);
	pane.add(noCompTodayDiagram);


	priceDueDateDiagram = new PositiveRangeDiagram(1, dayModel);
	priceDueDateDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	priceDueDateDiagram.setData(0, priceDueDate, 1);
	priceDueDateDiagram.addConstant(Color.black, 0);
	priceDueDateDiagram.addConstant
	    (Color.green.darker(), supplier.getComponent(line).getBasePrice());
	priceDueDateDiagram.setBorder(BorderFactory.createTitledBorder(""));
	priceDueDateDiagram.setTitle
	    (0, "Avg price (due today): ", " $/unit");
	priceDueDateDiagram.setToolTipText
	    ("Average price for the components ordered due for delivery on the current day");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 1;
	gblConstraints.gridy = 0;
	gbl.setConstraints(priceDueDateDiagram, gblConstraints);
	pane.add(priceDueDateDiagram);


	ordersDueDateDiagram = new PositiveRangeDiagram(1, dayModel);
	ordersDueDateDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	ordersDueDateDiagram.setData(0, ordersDueDate, 1);
	ordersDueDateDiagram.addConstant(Color.black, 0);
	ordersDueDateDiagram.setBorder(BorderFactory.createTitledBorder(""));
	ordersDueDateDiagram.setTitle
	    (0, "Number of orders (due today): ", "");
	ordersDueDateDiagram.setToolTipText
	    ("Number of component orders due for delivery on the current day");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 1;
	gblConstraints.gridy = 1;
	gbl.setConstraints(ordersDueDateDiagram, gblConstraints);
	pane.add(ordersDueDateDiagram);



	noCompDueDateDiagram = new PositiveRangeDiagram(1, dayModel);
	noCompDueDateDiagram.setPreferredSize(new Dimension(diagramWidth,150));
	noCompDueDateDiagram.setData(0, noCompDueDate, 1);
	noCompDueDateDiagram.addConstant(Color.black, 0);
	noCompDueDateDiagram.setBorder(BorderFactory.createTitledBorder(""));
	noCompDueDateDiagram.setTitle
	    (0, "Ordered components (due today): ", "");
	noCompDueDateDiagram.setToolTipText
	    ("Total number of components ordered due for delivery on the current day");
	gblConstraints.weightx = 1;
	gblConstraints.weighty = 1;
	gblConstraints.gridwidth = 1;
	gblConstraints.gridx = 1;
	gblConstraints.gridy = 2;
	gbl.setConstraints(noCompDueDateDiagram, gblConstraints);
	pane.add(noCompDueDateDiagram);


	return pane;
    }
} // SupplierWindow
