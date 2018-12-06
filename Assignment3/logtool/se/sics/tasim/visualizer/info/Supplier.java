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
 * Supplier
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Thu Jun  5 14:20:13 2003
 * Updated : $Date: 2003/07/26 19:56:19 $
 *           $Revision: 1.7 $
 */
package se.sics.tasim.visualizer.info;


/**
 */
public class Supplier extends Actor {

    private static int maxRFQs;
    private static float discountFactor;
    private static int nominalCapacity;
    private int productionLines;

  // capacity[l][t] : capacity of production line l at time t
  private int[][] capacity;
  private int[][] inventory;
  private int[][] production;
  private int[][] price;
    private Component[] comp;

  public Supplier(int simulationIndex, String address, String name,
		  int numberOfDays, int lines) {
    super(simulationIndex, address, name);
    productionLines = lines;
    capacity = new int[lines][numberOfDays];
    inventory = new int[lines][numberOfDays];
    production = new int[lines][numberOfDays];
    price = new int[lines][numberOfDays];
    comp = new Component[lines];
  }

    public void setComponent(int line, Component component) {
	comp[line] = component;
    }

    public Component getComponent(int line) {
	return comp[line];
    }

  public void setCapacity(int line, int day, int capacity) {
    this.capacity[line][day] = capacity;
  }

  public void setInventory(int line, int day, int inventory) {
    this.inventory[line][day] = inventory;
  }
  public void setProduction(int line, int day, int production) {
    this.production[line][day] = production;
  }
  public void setPrice(int line, int day, int price) {
    this.price[line][day] = price;
  }

  public int[] getCapacity(int line) {
    if (line < productionLines)
      return capacity[line];
    else
      return null;
  }

  public int[] getInventory(int line) {
    if (line < productionLines)
      return inventory[line];
    else
      return null;
  }
  public int[] getProduction(int line) {
    if (line < productionLines)
      return production[line];
    else
      return null;
  }
  public int[] getPrice(int line) {
    if (line < productionLines)
      return price[line];
    else
      return null;
  }

  public static void setParams(int nominalCapacity, int maxRFQs,
			       float discountFactor) {
    Supplier.nominalCapacity = nominalCapacity;
    Supplier.maxRFQs = maxRFQs;
    Supplier.discountFactor = discountFactor;
  }

  public static int getNominalCapacity() {
    return Supplier.nominalCapacity;
  }

  public static int getMaxRFQs() {
    return Supplier.maxRFQs;
  }

  public static float getDiscountFactor() {
    return Supplier.discountFactor;
  }
} // Supplier
