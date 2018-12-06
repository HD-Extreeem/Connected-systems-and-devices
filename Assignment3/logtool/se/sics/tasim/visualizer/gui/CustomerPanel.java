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
 * CustomerPanel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/07/16 21:23:06 $
 *           $Revision: 1.8 $
 */

package se.sics.tasim.visualizer.gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import se.sics.tasim.visualizer.info.Customer;
import se.sics.tasim.visualizer.info.PCNegotiation;
import se.sics.tasim.visualizer.info.SimulationInfo;

public class CustomerPanel  {
//     private static final String iconFile =
// 	"se/sics/tasim/visualizer/gui/images/customer.jpg";

  private JPanel mainPane;
    //JLabel image, name;
  private JCheckBox name;
  private Customer customer;
  private boolean selected = true;

  private PositiveBoundedRangeModel dayModel;
  private PositiveRangeDiagram customerDiagram;

  private CustomerWindow customerWindow;

  private SimulationInfo simInfo;
  private int[] demandUnitData;
  private int[] orderUnitData;

  public CustomerPanel(final SimulationInfo simInfo,
		       final Customer customer,
		       final PositiveBoundedRangeModel dayModel,
		       final PCCommunication pcComModel) {
    this.simInfo = simInfo;
    this.customer = customer;
    this.dayModel = dayModel;

    mainPane = new JPanel(null);
    mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
    mainPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    //image = new JLabel(new ImageIcon(iconFile));
    name = new JCheckBox(customer.getName(), true);
    name.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent ce) {
	  int i = simInfo.getCustomerIndex(customer);
	  if (name.isSelected())
	    pcComModel.openAllYChannels(i, CommunicationModel.YCOM_ALL);
	  else
	    pcComModel.closeAllYChannels(i, CommunicationModel.YCOM_ALL);
	}
      });
    name.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPane.add(name);

    demandUnitData = new int[dayModel.getLast()+1];
    orderUnitData = new int[dayModel.getLast()+1];
    for (int i = 0, n = dayModel.getLast()+1; i < n; i++) {
      PCNegotiation[] pcNeg = simInfo.getPCNegotiation(i);
      if (pcNeg == null)
	continue;

      for (int j = 0, m = pcNeg.length; j < m; j++) {
	if (pcNeg[j] == null)
	  continue;

	demandUnitData[i] += pcNeg[j].getRFQQuantity();
	if (pcNeg[j].isOrdered())
	  orderUnitData[i] += pcNeg[j].getRFQQuantity();
      }
    }

    Dimension preferredSize = new Dimension(120, 50);
    customerDiagram = new PositiveRangeDiagram(2, dayModel);
    customerDiagram.setPreferredSize(preferredSize);
    customerDiagram.setMaximumSize(preferredSize);
    customerDiagram.setMinimumSize(preferredSize);
    customerDiagram.setData(0, demandUnitData, 1);
    customerDiagram.setData(1, orderUnitData, 1);
    customerDiagram.setDotColor(0, GUITheme.RFQ_COLOR);
    customerDiagram.setDotColor(1, GUITheme.ORDER_COLOR.darker());
    customerDiagram.addConstant(Color.black, 0);
    customerDiagram.setToolTipText("Customer demand (blue), ordered (yellow)");
    mainPane.add(customerDiagram);
	//	mainPane.add(image);
	//mainPane.add(Box.createVerticalGlue());

    MouseInputAdapter listener = new MouseInputAdapter() {
	public void mouseClicked(MouseEvent me) {
	  openCustomerWindow();
	}
      };
    mainPane.addMouseListener(listener);
    customerDiagram.addMouseListener(listener);
  }

  public JPanel getMainPane() {
    return mainPane;
  }

  protected void openCustomerWindow() {
    if (customerWindow == null) {
      customerWindow = new CustomerWindow(simInfo, customer, dayModel,
					  demandUnitData, orderUnitData);
      customerWindow.setLocationRelativeTo(mainPane);
      customerWindow.setVisible(true);
    }
    else if (customerWindow.isVisible())
      customerWindow.toFront();
    else
      customerWindow.setVisible(true);

    int state = customerWindow.getExtendedState();
    if ((state & CustomerWindow.ICONIFIED) != 0) {
      customerWindow.setExtendedState(state & ~CustomerWindow.ICONIFIED);
    }
  }

} // CustomerPanel
