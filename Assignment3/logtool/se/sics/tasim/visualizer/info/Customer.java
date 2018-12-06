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
 * Customer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Thu Jun  5 14:01:43 2003
 * Updated : $Date: 2003/07/15 13:55:59 $
 *           $Revision: 1.4 $
 */
package se.sics.tasim.visualizer.info;

/**
 */
public class Customer extends Actor {
    private static int quantityMin;
    private static int quantityMax;
    private static int dueDateMin;
    private static int dueDateMax;
    private static int rfqAvgMin;
    private static int rfqAvgMax;
    private static int daysBeforeVoid;
    private static float trendMin;

  public Customer(int simulationIndex, String address, String name) {
      super(simulationIndex, address, name);
    }

  public static void setParams(int quantityMin, int quantityMax,
			       int dueDateMin, int dueDateMax,
			       int rfqAvgMin, int rfqAvgMax,
			       int daysBeforeVoid, float trendMin) {
    Customer.quantityMin = quantityMin;
    Customer.quantityMax = quantityMax;
    Customer.dueDateMin = dueDateMin;
    Customer.dueDateMax = dueDateMax;
    Customer.rfqAvgMin = rfqAvgMin;
    Customer.rfqAvgMax = rfqAvgMax;
    Customer.daysBeforeVoid = daysBeforeVoid;
    Customer.trendMin = trendMin;
  }

    public static int getDaysBeforeVoid() {
	return daysBeforeVoid;
    }
} // Customer
