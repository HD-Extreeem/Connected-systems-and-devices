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
 * ComponentNegotiationNode
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Tue Jul 15 13:02:12 2003
 * Updated : $Date: 2005/04/15 19:38:35 $
 *           $Revision: 1.6 $
 */
package se.sics.tasim.visualizer.gui;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import se.sics.isl.util.FormatUtils;
import se.sics.tasim.visualizer.info.ComponentNegotiation;
import se.sics.tasim.visualizer.info.Manufacturer;

/**
 */
public class ComponentNegotiationNode extends DefaultMutableTreeNode {

  Manufacturer manufacturer;
  ComponentNegotiation neg;
  TreeNode parent;
  boolean showNames;

  public ComponentNegotiationNode(TreeNode parent, Manufacturer manufacturer,
				  ComponentNegotiation neg,
				  boolean showNames) {
    this.neg = neg;
    this.parent = parent;
    this.manufacturer = manufacturer;
    this.showNames = showNames;

    // Create node
    createSubTree();
  }

  public void createSubTree() {

    // Create top info
    StringBuffer topTitle = new StringBuffer();
    if (showNames) {
      topTitle.append(neg.getBuyer().getName()).append(": ");
    }
    int orderedQuantity = neg.getOrderedQuantity();
    int requestedQuantity = neg.getRFQQuantity();

    if(orderedQuantity < requestedQuantity) {
      topTitle.append(orderedQuantity)
	.append(" of ")
	.append(requestedQuantity);
    }
    else
      topTitle.append(requestedQuantity);

    topTitle.append(' ');
    topTitle.append(neg.getComponent().getName());

    topTitle.append(" requested due ");
    topTitle.append(neg.getRFQDueDate());

    if(neg.getOrderType() == ComponentNegotiation.EARLIEST_ORDER) {
      topTitle.append(" (offered to day ");
      topTitle.append(neg.getOrderDueDate());
      topTitle.append(")");
    }

    if(neg.isOrdered()) {
      int deliveryDate = neg.getDeliveryDate();
      if (deliveryDate > 0) {
	topTitle.append(" delivered on ");
	topTitle.append(deliveryDate);
      } else {
	topTitle.append(" never delivered");
      }

    } else if (neg.hasOffer()) {
      topTitle.append(" (offered ");
      if (neg.hasPartialOffer()) {
	topTitle.append(neg.getPartialOfferQuantity())
	  .append(" units for $").append(neg.getPartialOfferPrice())
	  .append("/unit");
	if (neg.hasEarliestOffer()) {
	  if (neg.getOfferQuantity() < requestedQuantity) {
	    topTitle.append(" and ")
	      .append(neg.getOfferQuantity())
	      .append(" units ");
	  } else {
	    topTitle.append(" and all units ");
	  }
	}
      }
      if (neg.hasEarliestOffer()) {
	topTitle.append(" to day ").append(neg.getOfferDueDate())
	  .append(' ');
      }
      if (neg.hasEarliestOffer() || neg.hasFullOffer()) {
	topTitle.append("for $").append(neg.getOfferPrice()).append("/unit");
      }
      topTitle.append(')');
    } else {
      topTitle.append(" (no offer)");
    }

    if(neg.getOrderedQuantity() != 0) {
      topTitle.append(", price: $");
      topTitle.append(FormatUtils.formatAmount(neg.getOrderPrice()*
					       neg.getOrderedQuantity()));
    }
    if (neg.isOrdered()) {
      topTitle.append(" ($");
      topTitle.append(neg.getOrderPrice());
      topTitle.append("/unit)");
    }

    // Set the top node caption for this negotiation
    setUserObject(topTitle.toString());
  }

} // ComponentNegotiationNode
