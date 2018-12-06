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
 * PCTree
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Tue Jul 15 11:33:42 2003
 * Updated : $Date: 2003/07/21 16:37:28 $
 *           $Revision: 1.5 $
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

/**
 */
public class PCTree extends JTree {

  DefaultMutableTreeNode root, won, notWon, offers, noOffers;
  String wonStr, notWonStr, offersStr, noOffersStr;
  DefaultTreeModel model;
  Manufacturer manufacturer;

  public PCTree(Manufacturer manufacturer,
		final PositiveBoundedRangeModel dayModel,
		final SimulationInfo simInfo) {
    this.manufacturer = manufacturer;


    // Create the main root nodes
    wonStr = "RFQs that " + manufacturer.getName() + " got the order for";
    notWonStr = "RFQs that " + manufacturer.getName() +
      " sent an offer on, but did not get";
    offersStr = "RFQs that received offers, but not from " +
      manufacturer.getName();
    noOffersStr = "RFQs that did not receive any offers at all";

    root = new DefaultMutableTreeNode("root");
    won = new DefaultMutableTreeNode(wonStr);
    notWon = new DefaultMutableTreeNode(notWonStr);
    offers = new DefaultMutableTreeNode(offersStr);
    noOffers = new DefaultMutableTreeNode(noOffersStr);

    root.add(won);
    root.add(notWon);
    root.add(offers);
    root.add(noOffers);
    setRootVisible(false);
    setShowsRootHandles(true);

    // Create the model
    model = new DefaultTreeModel(root);
    if(dayModel != null)
      createNodes(simInfo.getPCNegotiation(dayModel.getCurrent()));
    setModel(model);

    // Create new negotiation nodes for each day
    if(dayModel != null) {
      dayModel.addChangeListener(new ChangeListener() {
	  public void stateChanged(ChangeEvent ce) {
	    createNodes(simInfo.getPCNegotiation
			(dayModel.getCurrent()));
	    model.reload();
	  }
	});
    }
  }

    protected void createNodes(PCNegotiation[] neg) {
	won.removeAllChildren();
	notWon.removeAllChildren();
	offers.removeAllChildren();
	noOffers.removeAllChildren();

	if(neg != null) {
	  // Add each negotiation to the right category
	  for (int i = 0, n = neg.length; i < n; i++) {
	    if(neg[i].countBids() == 0) {
	      noOffers.add(new PCNegotiationNode(noOffers, manufacturer,
						 neg[i]));
	    }
	    else if(!neg[i].isBidding(manufacturer)) {
	      offers.add(new PCNegotiationNode(offers, manufacturer,neg[i]));
	    }
	    else if(neg[i].getOrderWinner() == manufacturer) {
	      won.add(new PCNegotiationNode(won, manufacturer, neg[i]));
	    }
	    else {
	      notWon.add(new PCNegotiationNode(notWon, manufacturer,neg[i]));
	    }
	  }
	}

	noOffers.setUserObject(noOffersStr +
			       " (" + noOffers.getChildCount() + ')');
	offers.setUserObject(offersStr + " (" + offers.getChildCount() + ')');
	won.setUserObject(wonStr + " (" + won.getChildCount() +')' );
	notWon.setUserObject(notWonStr + " (" + notWon.getChildCount() + ')');
    }

} // PCTree
