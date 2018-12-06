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
 * CustomerWindow
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Wed Jul 16 22:30:42 2003
 * Updated : $Date: 2003/07/16 21:23:06 $
 *           $Revision: 1.1 $
 */
package se.sics.tasim.visualizer.gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import se.sics.tasim.visualizer.gui.DayChanger;
import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;
import se.sics.tasim.visualizer.gui.PositiveRangeDiagram;
import se.sics.tasim.visualizer.info.Customer;
import se.sics.tasim.visualizer.info.PCNegotiation;
import se.sics.tasim.visualizer.info.SimulationInfo;

/**
 */
public class CustomerWindow extends JFrame {

  public final static int diagramWidth = 300;
  public final static int diagramHeight = 150;

  private SimulationInfo simInfo;
  private DayChanger dayChanger;
  private Customer customer;
  private int customerIndex;
  private PositiveBoundedRangeModel dayModel;

  public CustomerWindow(SimulationInfo simInfo, Customer customer,
			PositiveBoundedRangeModel dayModel,
			int[] demandUnitData, int[] orderUnitData) {
    super(customer.getName());
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.simInfo = simInfo;
    this.customer = customer;
    customerIndex = simInfo.getCustomerIndex(customer);

    this.dayModel = dayModel;
    this.dayChanger = new DayChanger(dayModel);

    int size = dayModel.getLast() + 1;
    int[] demandData = new int[size];
    int[] orderData = new int[size];
    for (int i = 0; i < size; i++) {
      PCNegotiation[] pcNeg = simInfo.getPCNegotiation(i);
      if (pcNeg != null) {
	demandData[i] += pcNeg.length;
	for (int j = 0, m = pcNeg.length; j < m; j++) {
	  if (pcNeg[j].isOrdered()) {
	    orderData[i]++;
	  }
	}
      }
    }

    JTabbedPane tabPane = new JTabbedPane();
    tabPane.addTab("Customer Demand",
		   null,
		   createDemandPane(demandData, demandUnitData,
				    orderData, orderUnitData),
		   "Information about customer demand");

    Container contentPane = getContentPane();
    contentPane.add(tabPane, BorderLayout.CENTER);
    contentPane.add(dayChanger.getMainPane(), BorderLayout.SOUTH);

    pack();
  }

  protected JPanel createDemandPane(int[] demandData, int[] demandUnitData,
				    int[] orderData, int[] orderUnitData) {
    JPanel pane = new JPanel(new GridLayout(0, 2));
    Dimension preferredSize = new Dimension(diagramWidth, diagramHeight);
    Color orderColor = GUITheme.ORDER_COLOR.darker();
    pane.add(createDiagram(preferredSize, demandData, GUITheme.RFQ_COLOR,
			   "Sent PC RFQs: ", ""));
    pane.add(createDiagram(preferredSize, demandUnitData, GUITheme.RFQ_COLOR,
			   "Sent RFQs for ", " PCs"));
    pane.add(createDiagram(preferredSize, orderData, orderColor,
			   "Sent PC orders: ", ""));
    pane.add(createDiagram(preferredSize, orderUnitData, orderColor,
			   "Send orders for ", " PCs"));
    return pane;
  }

  private PositiveRangeDiagram createDiagram(Dimension preferredSize,
					     int[] data, Color color,
					     String titlePrefix,
					     String titlePostfix) {
    PositiveRangeDiagram diagram = new PositiveRangeDiagram(1, dayModel);
    diagram.setPreferredSize(preferredSize);
    diagram.setData(0, data, 1);
    diagram.setDotColor(0, color);
    diagram.addConstant(Color.black, 0);
    diagram.setBorder(BorderFactory.createTitledBorder(""));
    diagram.setTitle(0, titlePrefix, titlePostfix);
    return diagram;
  }

} // CustomerWindow
