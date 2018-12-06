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
 * Factory
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Thu Jun  5 14:25:50 2003
 * Updated : $Date: 2003/07/15 13:55:59 $
 *           $Revision: 1.5 $
 */
package se.sics.tasim.visualizer.info;

/**
 */
public class Factory extends Actor {

  private float[] utilization;

  private Component[] components;
  private PCType[] pcs;

  // componetnQuantity[c][t] : How many components of type
  // componentCatalog[c] are there in stock at time t
  private int[][] componentQuantity;
  private int[][] pcQuantity;

  public Factory(int simulationIndex, String address, String name,
		 Component[] components, PCType[] pcs,
		 int numberOfDays) {
    super(simulationIndex, address, name);
    this.components = components;
    this.pcs = pcs;
    utilization = new float[numberOfDays];
    componentQuantity = new int[components.length][numberOfDays];
    pcQuantity = new int[pcs.length][numberOfDays];
  }

  public void setComponentHolding(int componentIndex, int day, int quantity) {
    componentQuantity[componentIndex][day] = quantity;
  }

  public void setPCHolding(int pcIndex, int day, int quantity) {
    pcQuantity[pcIndex][day] = quantity;
  }

  public void setUtilization(int day, float utilization) {
    this.utilization[day] = utilization;
  }

    public float getUtilization(int day) {
	return utilization[day];
    }

    public int getNumberOfComponentTypes() {
	return components.length;
    }

    public int getNumberOfPCTypes() {
	return pcs.length;
    }

    public int getComponentHolding(int componentIndex, int day) {
	return componentQuantity[componentIndex][day];
    }

    public int getPCHolding(int pcIndex, int day) {
	return pcQuantity[pcIndex][day];
    }

    public Component getComponentType(int componentIndex) {
	return components[componentIndex];
    }

    public PCType getPCType(int pcIndex) {
	return pcs[pcIndex];
    }
} // Factory
