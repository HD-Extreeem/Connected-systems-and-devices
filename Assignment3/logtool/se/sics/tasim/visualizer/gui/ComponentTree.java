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
 * ComponentTree
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Tue Jul 15 11:33:42 2003
 * Updated : $Date: 2004/07/06 14:43:50 $
 *           $Revision: 1.4 $
 */
package se.sics.tasim.visualizer.gui;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import se.sics.tasim.visualizer.gui.PCNegotiationNode;
import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;
import se.sics.tasim.visualizer.info.Manufacturer;
import se.sics.tasim.visualizer.info.PCNegotiation;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.info.ComponentNegotiation;
import se.sics.tasim.visualizer.info.Supplier;
import se.sics.tasim.visualizer.gui.ComponentNegotiationNode;

/**
 */
public class ComponentTree extends JTree {

  DefaultMutableTreeNode root;
  DefaultMutableTreeNode[] supNodes;
  Supplier[] sup;
  String[] supStr;

  String fromThis, fromOthers, noOrder, complete, partial, earliest;
  DefaultTreeModel model;
  Manufacturer manufacturer;


  public ComponentTree(final Manufacturer manufacturer,
		       final PositiveBoundedRangeModel dayModel,
		       final SimulationInfo simInfo) {
    this.manufacturer = manufacturer;

    fromThis = "RFQs from " + manufacturer.getName();
    fromOthers = "RFQs from other agents";
    noOrder = "RFQs without orders";
    complete = "RFQs with complete orders";
    partial = "RFQs with partial orders";
    earliest = "RFQs with earliest complete orders";

    int suppliers = simInfo.getSupplierCount();
    supNodes = new DefaultMutableTreeNode[2*suppliers];
    sup = new Supplier[suppliers];
    supStr = new String[2*suppliers];

    // Create the main root nodes
    root = new DefaultMutableTreeNode("root");

    for (int i = 0, n = suppliers; i < n; i++) {
      sup[i] = simInfo.getSupplier(i);

      supStr[2*i] = sup[i].getName() + " - " +
	sup[i].getComponent(0).getName();

      supStr[2*i+1] = sup[i].getName() + " - " +
	sup[i].getComponent(1).getName();

      supNodes[2*i] = new DefaultMutableTreeNode(supStr[2*i]);
      supNodes[2*i+1] = new DefaultMutableTreeNode(supStr[2*i+1]);

      root.add(supNodes[2*i]);
      root.add(supNodes[2*i+1]);
    }


    setRootVisible(false);
    setShowsRootHandles(true);

    // Create the model
    model = new DefaultTreeModel(root);
    if(dayModel != null)
      createNodes(simInfo.getComponentNegotiation(dayModel.getCurrent()));
    setModel(model);

    // Create new negotiation nodes for each day
    if(dayModel != null) {
      dayModel.addChangeListener(new ChangeListener() {
	  public void stateChanged(ChangeEvent ce) {
	    createNodes(simInfo.getComponentNegotiation
			(dayModel.getCurrent()));
	    model.reload();
	  }
	});
    }
  }

  protected void createNodes(ComponentNegotiation[] neg) {

    // Clear list and refill each supplier
    for (int i = 0, n = sup.length; i < n; i++) {
      supNodes[2*i].removeAllChildren();
      supNodes[2*i+1].removeAllChildren();

      createNode(i, 0, neg, supNodes[2*i]);
      createNode(i, 1, neg, supNodes[2*i+1]);
    }
  }


  protected void createNode(int supplier, int line,
			    ComponentNegotiation[] neg,
			    DefaultMutableTreeNode parent) {

    if(neg == null || parent == null) {
      parent.setUserObject(supStr[2*supplier + line] + " (0/0)");
      return;
    }

    int thisCount = 0, otherCount = 0;
    DefaultMutableTreeNode fromThis, fromOthers, noOrder, noOrderThis,
      complete, completeThis, partial, partialThis, earliest, earliestThis;

    fromThis = new DefaultMutableTreeNode();
    fromOthers = new DefaultMutableTreeNode();

    noOrderThis = new DefaultMutableTreeNode();
    completeThis = new DefaultMutableTreeNode();
    partialThis = new DefaultMutableTreeNode();
    earliestThis = new DefaultMutableTreeNode();
    noOrder = new DefaultMutableTreeNode();
    complete = new DefaultMutableTreeNode();
    partial = new DefaultMutableTreeNode();
    earliest = new DefaultMutableTreeNode();

    // Add each negotiation to the right category
    for (int i = 0, n = neg.length; i < n; i++) {
      if(neg[i].getSeller() != sup[supplier] ||
	 neg[i].getComponent() != sup[supplier].getComponent(line))
	continue;

      int orderType = neg[i].getOrderType();

      // This agent
      if(neg[i].getBuyer() == manufacturer) {
	thisCount++;

	if(orderType == ComponentNegotiation.NO_ORDER)
	  noOrderThis.add(new ComponentNegotiationNode(noOrderThis,
						       manufacturer, neg[i],
						       false));
	else if(orderType == ComponentNegotiation.FULL_ORDER)
	  completeThis.add(new ComponentNegotiationNode(completeThis,
							manufacturer, neg[i],
							false));
	else if(orderType == ComponentNegotiation.PARTIAL_ORDER)
	  partialThis.add(new ComponentNegotiationNode(partialThis,
						       manufacturer, neg[i],
						       false));
	else if(orderType == ComponentNegotiation.EARLIEST_ORDER)
	  earliestThis.add(new ComponentNegotiationNode(earliestThis,
							manufacturer, neg[i],
							false));
      }

      // Other agents
      else {
	otherCount++;

	if(orderType == ComponentNegotiation.NO_ORDER)
	  noOrder.add(new ComponentNegotiationNode(noOrder,
						   manufacturer, neg[i],
						   true));
	else if(orderType == ComponentNegotiation.FULL_ORDER)
	  complete.add(new ComponentNegotiationNode(complete,
						    manufacturer, neg[i],
						    true));
	else if(orderType == ComponentNegotiation.PARTIAL_ORDER)
	  partial.add(new ComponentNegotiationNode(partial,
						   manufacturer, neg[i],
						   true));
	else if(orderType == ComponentNegotiation.EARLIEST_ORDER)
	  earliest.add(new ComponentNegotiationNode(earliest,
						    manufacturer, neg[i],
						    true));
      }
    }

    fromThis.setUserObject(this.fromThis + " (" + thisCount + ")");
    fromOthers.setUserObject(this.fromOthers + " (" + otherCount + ")");

    if(noOrderThis.getChildCount() != 0) {
      noOrderThis.setUserObject(this.noOrder +" (" +
				noOrderThis.getChildCount() + ")");
      fromThis.add(noOrderThis);
    }

    if(completeThis.getChildCount() != 0) {
      completeThis.setUserObject(this.complete +" (" +
				 completeThis.getChildCount() + ")");
      fromThis.add(completeThis);
    }

    if(partialThis.getChildCount() != 0) {
      fromThis.add(partialThis);
      partialThis.setUserObject(this.partial +" (" +
				partialThis.getChildCount() + ")");
    }

    if(earliestThis.getChildCount() != 0) {
      fromThis.add(earliestThis);
      earliestThis.setUserObject(this.earliest +" (" +
				 earliestThis.getChildCount() + ")");
    }

    if(noOrder.getChildCount() != 0) {
      fromOthers.add(noOrder);
      noOrder.setUserObject(this.noOrder +" (" +
			    noOrder.getChildCount() + ")");
    }

    if(complete.getChildCount() != 0) {
      fromOthers.add(complete);
      complete.setUserObject(this.complete +" (" +
			     complete.getChildCount() + ")");
    }

    if(partial.getChildCount() != 0) {
      fromOthers.add(partial);
      partial.setUserObject(this.partial +" (" +
			    partial.getChildCount() + ")");
    }

    if(earliest.getChildCount() != 0) {
      fromOthers.add(earliest);
      earliest.setUserObject(this.earliest +" (" +
			     earliest.getChildCount() + ")");
    }

    parent.setUserObject(supStr[2*supplier + line] + " (" +
			 thisCount + "/" + otherCount + ")");
    parent.add(fromThis);
    parent.add(fromOthers);
  }

} // ComponentTree
