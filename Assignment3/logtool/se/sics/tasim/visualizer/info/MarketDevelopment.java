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
 * MarketDevelopment
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sun Jun  8 13:13:56 2003
 * Updated : $Date: 2003/06/10 15:28:18 $
 *           $Revision: 1.1 $
 */
package se.sics.tasim.visualizer.info;

/**
 */
public class MarketDevelopment {

  public static final int MARKET_REPORT_INTERVAL = 20;

  private Component[] components;
  private PCType[] pcTypes;
  private Supplier[] suppliers;

  // highetsPCPrice[pc][t] : What was the highest price
  // of PC bom[pc] at time t (daily information)
  private int[][] highestPCPrice;
  private int[][] lowestPCPrice;

  // averagePCPrice[pc][i] : Average price reported to the
  // manufacturers of PC bom[pc] for the interval i (non daily information)
  private int[][] averagePCPrice;

  // noPCOrdered[pc][i] : How many PCs of type bom[pc] were ordered
  // by customerd during interval i (non daily information)
  private int[][] noPCOrdered;

  // How many components were produced / delivered during an interval
  // (non daily information)
  private int[][] componentsProduced;
  private int[][] componentsDelivered;

  public MarketDevelopment(Supplier[] suppliers, Component[] components,
			   PCType[] pcTypes, int numberOfDays) {
    int numberOfMarketReports = numberOfDays / MARKET_REPORT_INTERVAL;
    this.suppliers = suppliers;
    this.components = components;
    this.pcTypes = pcTypes;

    highestPCPrice = new int[pcTypes.length][numberOfDays];
    lowestPCPrice = new int[pcTypes.length][numberOfDays];

    averagePCPrice = new int[pcTypes.length][numberOfMarketReports];
    noPCOrdered = new int[pcTypes.length][numberOfMarketReports];
    componentsProduced = new int[components.length][numberOfMarketReports];
    componentsDelivered = new int[components.length][numberOfMarketReports];
  }

  public void setHighestPCPrice(int day, int pcIndex, int price) {
    highestPCPrice[pcIndex][day] = price;
  }
  public void setLowestPCPrice(int day, int pcIndex, int price) {
    lowestPCPrice[pcIndex][day] = price;
  }

  public void setAveragePCPrice(int interval, int pcIndex, int price) {
    averagePCPrice[pcIndex][interval] = price;
  }

  public void setOrderedPCCount(int interval, int pcIndex, int count) {
    noPCOrdered[pcIndex][interval] = count;
  }

  public void setComponentsProduced(int interval, int componentIndex,
				    int count) {
    componentsProduced[componentIndex][interval] = count;
  }

  public void setComponentsDelivered(int interval, int componentIndex,
				     int count) {
    componentsDelivered[componentIndex][interval] = count;
  }
} // MarketDevelopment
