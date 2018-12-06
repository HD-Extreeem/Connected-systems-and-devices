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
 * Manufacturer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Thu Jun  5 14:23:24 2003
 * Updated : $Date: 2005/04/15 12:46:11 $
 *           $Revision: 1.8 $
 */
package se.sics.tasim.visualizer.info;

/**
 */
public class Manufacturer extends Actor {
  private Factory myFactory;

  private int[] account;
  private int[] interest;

  public Manufacturer(int simulationIndex, String address, String name,
		      int numberOfDays) {
    super(simulationIndex, address, name);
    account = new int[numberOfDays];
    interest = new int[numberOfDays];
  }

  public void setAccountBalance(int day, int balance) {
    account[day < account.length ? day : (account.length - 1)] = balance;
  }

  public int getAccountBalance(int day) {
    return account[day];
  }

  public int[] getAccountBalance() {
    return account;
  }

  public void setBankInterest(int day, int interest) {
    this.interest[day] = interest;
  }

  public int getBankInterest(int day) {
    return interest[day];
  }

    public int[] getBankInterest() {
	return interest;
    }

    public Factory getFactory() {
	return myFactory;
    }
    public void setFactory(Factory f) {
	myFactory = f;
    }

} // Manufacturer
