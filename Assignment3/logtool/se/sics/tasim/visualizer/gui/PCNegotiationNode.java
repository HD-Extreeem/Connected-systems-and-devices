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
 * PCNegotiationNode
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Tue Jul 15 13:02:12 2003
 * Updated : $Date: 2003/08/01 12:35:20 $
 *           $Revision: 1.8 $
 */
package se.sics.tasim.visualizer.gui;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import se.sics.tasim.visualizer.info.Manufacturer;
import se.sics.tasim.visualizer.info.PCNegotiation;

/**
 */
public class PCNegotiationNode extends DefaultMutableTreeNode {

  Manufacturer manufacturer;
  PCNegotiation neg;
  TreeNode parent;

  public PCNegotiationNode(TreeNode parent, Manufacturer manufacturer,
			   PCNegotiation neg) {
    this.neg = neg;
    this.parent = parent;
    this.manufacturer = manufacturer;

    // Create node
    createSubTree();
  }

  public void createSubTree() {

    int pCount = neg.getPenaltyCount();

    // Create top info
    StringBuffer topTitle = new StringBuffer();
    topTitle.append(neg.getRFQQuantity());
    topTitle.append(' ');
    topTitle.append(neg.getPCType().getName());

    if (neg.countBids() == 0) {
      topTitle.append(" for $");
      topTitle.append(neg.getRFQPrice());
    }

    // Create subtitle info
    StringBuffer subTitle = new StringBuffer();
    subTitle.append("RFQ due on day ");
    subTitle.append(neg.getRFQDueDate());
    subTitle.append(" with penalty $");
    subTitle.append(neg.getRFQPenalty());
    subTitle.append(" and reserve price $");
    subTitle.append(neg.getRFQPrice());
    add(new DefaultMutableTreeNode(subTitle.toString()));

    // Create info for the offers
    StringBuffer offerTitle;
    for (int i = 0, n = neg.countBids(); i < n; i++) {
      String offerPriceStr =
	Integer.toString(neg.getOfferPrice(i)* neg.getRFQQuantity());
      offerTitle = new StringBuffer();
      offerTitle.append(neg.getBidder(i).getName());
      offerTitle.append(" offered $");
      offerTitle.append(offerPriceStr);
      offerTitle.append(" ($");
      offerTitle.append(neg.getOfferPrice(i));
      offerTitle.append("/unit)");
      DefaultMutableTreeNode offerNode =
	new DefaultMutableTreeNode();

      // If this agent won the RFQ, special treatment
      if(neg.getBidder(i) == neg.getOrderWinner()) {
	offerTitle.append(" and won the order");

	topTitle.append(" for $");
	topTitle.append(offerPriceStr);

	// Delivered (not canceled)
	if(neg.isDelivered()) {
	  topTitle.append(" delivered on day ");
	  topTitle.append(neg.getDeliveryDate());

	  // Delivered with penalties
	  if(pCount != 0) {
	    String penaltyStr = Integer.toString(pCount * neg.getRFQPenalty());

	    topTitle.append(" (penalty $");
	    topTitle.append(penaltyStr);
	    topTitle.append(')');

	    if(pCount == 1)
	      offerNode.add
		(new DefaultMutableTreeNode("1 penalty ($" +
					    penaltyStr +
					    ')'));
	    else
	      offerNode.add
		(new DefaultMutableTreeNode(pCount +
					    " penalties ($" + penaltyStr +
					    ')'));
	  }
	  else {
	    offerNode.add(new
			  DefaultMutableTreeNode("No penalties"));
	  }
	  offerNode.add(new DefaultMutableTreeNode("Delivered on day " +
						   neg.getDeliveryDate()));
	}

	// Canceled
	else {
	  String penaltyStr = Integer.toString(pCount * neg.getRFQPenalty());

	  topTitle.append(" never delivered (penalty $");
	  topTitle.append(penaltyStr);
	  topTitle.append(')');

	  if(pCount == 1)
	    offerNode.add
	      (new DefaultMutableTreeNode("1 penalty ($" + penaltyStr + ')'));
	  else
	    offerNode.add
	      (new DefaultMutableTreeNode(pCount +
					  " penalties ($" +
					  penaltyStr + ')'));
	  offerNode.add(new DefaultMutableTreeNode("Never delivered"));
	}
      }

      offerNode.setUserObject(offerTitle.toString());
      add(offerNode);
    }

    // Set the top node caption for this negotiation
    setUserObject(topTitle.toString());
  }

} // PCNegotiationNode
