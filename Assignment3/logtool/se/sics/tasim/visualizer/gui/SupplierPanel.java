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
 * SupplierPanel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/07/26 16:19:40 $
 *           $Revision: 1.13 $
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import se.sics.tasim.visualizer.info.Manufacturer;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.info.Supplier;
import javax.swing.JCheckBox;

public class SupplierPanel {
    JPanel mainPane, imageRowPane;
    JLabel image;
    JCheckBox name;
    PositiveRangeDiagram inventoryDiagram;
    Supplier supplier;
    PositiveBoundedRangeModel dayModel;
		SupplierWindow supplierWindow;
    SimulationInfo simInfo;


    public SupplierPanel(final SimulationInfo simInfo,
			 final Supplier supplier,
			 final PositiveBoundedRangeModel dayModel,
			 final ComponentCommunication compComModel) {
	this.dayModel = dayModel;
	this.supplier = supplier;
	this.simInfo = simInfo;

	mainPane = new JPanel();
	mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
	mainPane.setBorder(BorderFactory.
			   createBevelBorder
			   (BevelBorder.RAISED));


	mainPane.addMouseListener(new MouseInputAdapter() {
		public void mouseClicked(MouseEvent me) {
		    openSupplierWindow();
		}
	    });

	imageRowPane = new JPanel();
	imageRowPane.setLayout(new BoxLayout(imageRowPane,
					     BoxLayout.X_AXIS));

	inventoryDiagram = new PositiveRangeDiagram(2, dayModel);
	inventoryDiagram.setPreferredSize(new Dimension(120,50));
	inventoryDiagram.setMaximumSize(new Dimension(120,50));
	inventoryDiagram.setMinimumSize(new Dimension(120,50));
	inventoryDiagram.setData(0, supplier.getCapacity(0), 1);
	inventoryDiagram.setData(1, supplier.getCapacity(1), 1);
	inventoryDiagram.setDotColor(0, Color.blue);
	inventoryDiagram.setDotColor(1, Color.green.darker());
	inventoryDiagram.addConstant(Color.black, 0);
	inventoryDiagram.addConstant(GUITheme.NOMINAL_CAPACITY_COLOR,
				     Supplier.getNominalCapacity());

	inventoryDiagram.setToolTipText("Production capacity");
	inventoryDiagram.addMouseListener(new MouseInputAdapter() {
		public void mouseClicked(MouseEvent me) {
		    openSupplierWindow();
		}
	    });


	image = new JLabel(GUITheme.getIcon("supplier-small.jpg"));
	name = new JCheckBox(supplier.getName(), true);
	name.setAlignmentX(Component.CENTER_ALIGNMENT);
	name.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent ce) {
		int i = simInfo.getSupplierIndex(supplier);
		if(name.isSelected()) {
		    compComModel.openAllYChannels(i, CommunicationModel.YCOM_ALL);
		}

		else {
		    compComModel.closeAllYChannels(i, CommunicationModel.YCOM_ALL);
		}
	    }
	});



	imageRowPane.add(image);
	imageRowPane.add(Box.createRigidArea(new Dimension(5,0)));
	imageRowPane.add(inventoryDiagram);

	mainPane.add(name);
	mainPane.add(imageRowPane);
    }

    protected void openSupplierWindow() {
	if(supplierWindow == null) {
	    supplierWindow = new SupplierWindow(simInfo, supplier, dayModel);
	    supplierWindow.setLocationRelativeTo(mainPane);
	    supplierWindow.setVisible(true);
	}
	else if (supplierWindow.isVisible())
	    supplierWindow.toFront();
	else
	    supplierWindow.setVisible(true);

	int state = supplierWindow.getExtendedState();
	if ((state & SupplierWindow.ICONIFIED) != 0) {
	  supplierWindow.setExtendedState(state & ~SupplierWindow.ICONIFIED);
	}
    }

    public JPanel getMainPane() {
	return mainPane;
    }

} // SupplierPanel
