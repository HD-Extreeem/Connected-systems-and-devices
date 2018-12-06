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
 * PCNegotiation
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Mon Jun 23 16:05:57 2003
 * Updated : $Date: 2003/08/01 12:35:20 $
 *           $Revision: 1.8 $
 */
package se.sics.tasim.visualizer.info;

/**
 */
public class PCNegotiation {
    PCType pc;
    Manufacturer[] bidders;
    Customer buyer;

    short rfqQuantity;
    int rfqPrice;
    short rfqDueDate;
    int rfqPenalty;

    int[] offerPrice;

    short order = -1;
    short noPenalties;
    short deliveryDate = -1;

    public PCNegotiation(Customer buyer, PCType pc,
			 int quantity, int dueDate,
			 int penalty, int price) {
	this.buyer = buyer;
	this.pc = pc;
	this.rfqQuantity = (short) quantity;
	this.rfqDueDate = (short) dueDate;
	this.rfqPenalty = penalty;
	this.rfqPrice = price;
    }

    public void addPenalty() {
	noPenalties++;
    }

    public int getPenaltyCount() {
	return noPenalties;
    }

//     public boolean isCancelled() {
// 	return noPenalties >= Customer.getDaysBeforeVoid();
//     }

    // Add a manufacturer to the bidder array and it's price
    // to the offerPrice array. Grows the arrays.
    public void addBid(Manufacturer manufacturer, int price) {
	if(bidders == null) {
	    bidders = new Manufacturer[1];
	    offerPrice = new int[1];
	}
	else {
	    Manufacturer[] newManuf = new Manufacturer[bidders.length+1];
	    int[] newOfferPrice = new int[bidders.length+1];
	    for (int i = 0, n = bidders.length; i < n; i++) {
		newManuf[i] = bidders[i];
		newOfferPrice[i] = offerPrice[i];
	    }
	    bidders = newManuf;
	    offerPrice = newOfferPrice;
	}

	bidders[bidders.length-1] = manufacturer;
	offerPrice[bidders.length-1] = price;
    }

    public int countBids() {
	if(bidders == null)
	    return 0;
	return bidders.length;
    }

    public void addOrder(Manufacturer manufacturer) {
	for (int i = 0, n = bidders.length; i < n; i++) {
	    if(manufacturer == bidders[i]) {
		order = (short) i;
		break;
	    }
	}
    }


    // returns true if manufacturer is bidding on rfq
    public boolean isBidding(Manufacturer manufacturer) {
	if(bidders == null)
	    return false;

	for (int i = 0, n = bidders.length; i < n; i++)
	    if(manufacturer == bidders[i])
		return true;

	return false;
    }

  public Manufacturer getBidder(int index) {
    if(bidders == null || index >= bidders.length)
      return null;

    return bidders[index];
  }

  public int getOfferPrice(int index) {
    if(bidders == null || index >= offerPrice.length)
      return -1;

    return offerPrice[index];
  }

    public Manufacturer getOrderWinner() {
	if(order == -1)
	    return null;
	return bidders[order];
    }

  public PCType getPCType() {
    return pc;
  }

  public int getRFQQuantity() {
    return rfqQuantity;
  }

    public int getRFQPenalty() {
	return rfqPenalty;
    }

    public int getRFQDueDate() {
	return rfqDueDate;
    }

    public int getRFQPrice() {
	return rfqPrice;
    }

    public boolean isOrdered() {
	return order != -1;
    }

  public int getOrderPrice() {
    if (order == -1) {
      return 0;
    }
    return offerPrice[order];
  }

  public void setCancelled() {
    if (this.deliveryDate > 0) {
      System.err.println("warning, cancelled already delivered order");
    }
    this.deliveryDate = (short) -2;
  }

//   public boolean isCancelled() {
//     return deliveryDate < 0;
//   }

  public boolean isDelivered() {
    return deliveryDate >= 0;
  }

  public void setDeliveryDate(int date) {
    if (this.deliveryDate < -1) {
      System.err.println("warning, accepted already cancelled order");
    }

    this.deliveryDate = (short) date;
  }

  public int getDeliveryDate() {
    return deliveryDate;
  }

} // PCNegotiation
