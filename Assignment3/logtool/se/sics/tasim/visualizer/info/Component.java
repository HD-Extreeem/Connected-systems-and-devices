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
 * Component
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sun Jun  8 12:12:30 2003
 * Updated : $Date: 2003/07/15 12:34:12 $
 *           $Revision: 1.2 $
 */
package se.sics.tasim.visualizer.info;

/**
 */
public class Component {

  private int productID;
  private String name;
  private int basePrice;

  public Component(int productID, String name, int basePrice) {
    this.productID = productID;
    this.name = name;
    this.basePrice = basePrice;
  }

  public int getProductID() {
    return productID;
  }

  public String getName() {
    return name;
  }

  public int getBasePrice() {
    return basePrice;
  }

} // Component
