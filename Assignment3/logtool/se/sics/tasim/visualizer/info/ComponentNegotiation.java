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
 * ComponentNegotiation
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Mon Jun 23 00:47:56 2003
 * Updated : $Date: 2005/04/15 19:38:35 $
 *           $Revision: 1.9 $
 */
package se.sics.tasim.visualizer.info;

/**
 */
public class ComponentNegotiation {
  public static final byte NO_ORDER = 0;
  public static final byte FULL_ORDER = 1;
  public static final byte PARTIAL_ORDER = 2;
  public static final byte EARLIEST_ORDER = 3;
  private static final byte ORDER_MASK = 3;

//   private static final byte NO_OFFER = 10;
//   private static final byte FULL_OFFER = 11;
//   private static final byte COMBO_OFFER = 12;

  private static final byte PARTIAL_OFFER = 1 << 2;
  private static final byte EARLIEST_OFFER = 1 << 3;
  private static final byte FULL_OFFER = 1 << 4;
  private static final byte OFFER_MASK =
    PARTIAL_OFFER | EARLIEST_OFFER | FULL_OFFER;

  private Component component;
  private Manufacturer buyer;
  private Supplier seller;
  private int rfqDueDate, rfqQuantity;

  private short offerPartialQuantity = -1;
  private int offerPartialPrice = -1;

  private short offerQuantity = -1;
  private int offerPrice = -1;
  private short offerDueDate = -1;

  private byte flags = 0;

  private short deliveryDate = -1;

  public ComponentNegotiation(Component component,
			      Manufacturer buyer, Supplier seller,
			      int rfqDueDate, int rfqQuantity) {
    this.component = component;
    this.buyer = buyer;
    this.seller = seller;
    this.rfqDueDate = rfqDueDate;
    this.rfqQuantity = rfqQuantity;
  }

  public int getOrderPrice() {
    int order = flags & ORDER_MASK;
    if(order == NO_ORDER)
      return 0;
    else if(order == PARTIAL_ORDER)
      return offerPartialPrice;

    // FULL or EARLIEST
    return offerPrice;
  }

  public int getOrderDueDate() {
    int order = flags & ORDER_MASK;
    if(order == NO_ORDER)
      return -1;
    else if(order == EARLIEST_ORDER)
      return offerDueDate;

    return rfqDueDate;
  }

//   public short getOfferType() {
//     if(offerPrice == -1)
//       return NO_OFFER;
//     else if (offerQuantity == -1 && offerDueDate == -1)
//       return FULL_OFFER;
//     else
//       return COMBO_OFFER;
//   }

  public boolean hasOffer() {
    return (flags & OFFER_MASK) != 0;
  }

  public boolean hasPartialOffer() {
    return (flags & PARTIAL_OFFER) != 0;
  }

  public short getPartialOfferQuantity() {
    return offerPartialQuantity;
  }

  public int getPartialOfferPrice() {
    return offerPartialPrice;
  }

  public boolean hasEarliestOffer() {
    return (flags & EARLIEST_OFFER) != 0;
  }

  public boolean hasFullOffer() {
    return (flags & FULL_OFFER) != 0;
  }

  public short getOfferQuantity() {
    return offerQuantity;
  }

  public int getOfferPrice() {
    return offerPrice;
  }

  public int getOfferDueDate() {
    return offerDueDate > 0
      ? offerDueDate
      : rfqDueDate;
  }

  public void addOffer(int price, int dueDate, int quantity, boolean partial) {
    if (partial) {
      offerPartialPrice = price;
      offerPartialQuantity = (short) quantity;
      flags |= PARTIAL_OFFER;
    } else {
      offerPrice = price;
      offerQuantity = (short) quantity;
      offerDueDate = (short) dueDate;
      if (rfqDueDate < dueDate) {
	flags |= EARLIEST_OFFER;
      } else {
	flags |= FULL_OFFER;
      }
    }
  }

  public void addOrder(short orderType) {
    flags |= (((byte) orderType) & ORDER_MASK);
  }

  public int getOrderedQuantity() {
    int order = flags & ORDER_MASK;
    if(order == NO_ORDER)
      return 0;
    else if(order == PARTIAL_ORDER)
      return offerPartialQuantity;
    else
      return offerQuantity;
  }

  public int getRFQDueDate() {
    return rfqDueDate;
  }

  public int getRFQQuantity() {
    return rfqQuantity;
  }

  public Manufacturer getBuyer() {
    return buyer;
  }

  public Supplier getSeller() {
    return seller;
  }

  public Component getComponent() {
    return component;
  }

  public boolean isOrdered() {
    return (flags & ORDER_MASK) != NO_ORDER;
  }

  public short getOrderType() {
    return (short) (flags & ORDER_MASK);
  }

  public boolean isDelivered() {
    return deliveryDate > 0;
  }

  public int getDeliveryDate() {
    return deliveryDate;
  }

  public void setDelivered(int day) {
    deliveryDate = (short) day;
  }

} // ComponentNegotiation
