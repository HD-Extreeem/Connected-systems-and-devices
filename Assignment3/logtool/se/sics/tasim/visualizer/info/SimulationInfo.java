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
 * SimulationInfo
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Fri Jun  6 16:33:06 2003
 * Updated : $Date: 2005/03/17 09:51:33 $
 *           $Revision: 1.16 $
 */
package se.sics.tasim.visualizer.info;
import java.util.ArrayList;

import se.sics.tasim.visualizer.util.SimulationParser;

/**
 */
public class SimulationInfo {

  private String server;
  private int numberOfDays;
  private int simulationID;
  private String simulationType;
  private int secondsPerDay;
  private int bankInterest;
  private int bankInterestMax;
  private int bankInterestMin;
  private int storageCost;

  private PCType[] bom;
  private Component[] componentCatalog;

  private Manufacturer[] manufacturers;
  private Factory[] factories;
  private Supplier[] suppliers;
  private Customer[] customers;

  private ComponentNegotiation[][] compNegotiation;
  private PCNegotiation[][] pcNegotiation;

  private MarketDevelopment marketDevelopment;


  public SimulationInfo(SimulationParser sp) {
    simulationID = sp.getSimulationID();
    simulationType = sp.getSimulationType();
    numberOfDays = sp.getNumberOfDays();
    manufacturers = sp.getManufacturers();
    secondsPerDay = sp.getSecondsPerDay();
    bankInterest = sp.getBankInterest();
    bankInterestMax = sp.getBankInterestMax();
    bankInterestMin = sp.getBankInterestMin();
    storageCost = sp.getStorageCost();
    server = sp.getServer();

    factories = sp.getFactories();
    suppliers = sp.getSuppliers();
    customers = sp.getCustomers();

    marketDevelopment = sp.getMarketDevelopment();
    bom = sp.getPCTypes();
    componentCatalog = sp.getComponents();

    compNegotiation = sp.getComponentNegotiation();
    pcNegotiation = sp.getPCNegotiation();
  }

  public String getServer() {
    return server;
  }

  public int getSimulationID() {
    return simulationID;
  }

  public String getSimulationType() {
    return simulationType;
  }

  public int getSecondsPerDay() {
    return secondsPerDay;
  }

  public int getBankInterest() {
    return bankInterest;
  }

  public int getBankInterestMax() {
    return bankInterestMax;
  }

  public int getBankInterestMin() {
    return bankInterestMin;
  }

  public int getStorageCost() {
    return storageCost;
  }

  public int getNumberOfDays() {
    return numberOfDays;
  }

  public int getManufacturerCount() {
    return manufacturers.length;
  }

  public int getFactoryCount() {
    return factories.length;
  }

  public int getSupplierCount() {
    return suppliers.length;
  }

  public int getCustomerCount() {
    return customers.length;
  }

  public Manufacturer getManufacturer(int index) {
    return manufacturers[index];
  }

  public Factory getFactory(int index) {
    return factories[index];
  }

  public Supplier getSupplier(int index) {
    return suppliers[index];
  }

  public Customer getCustomer(int index) {
    return customers[index];
  }

  public int getManufacturerIndex(Manufacturer m) {
    for (int i = 0, n = manufacturers.length; i < n; i++) {
      if(m == manufacturers[i])
	return i;
    }
    return -1;
  }

  public int getCustomerIndex(Customer c) {
    for (int i = 0, n = customers.length; i < n; i++) {
      if(c == customers[i])
	return i;
    }
    return -1;
  }
  public int getSupplierIndex(Supplier s) {
    for (int i = 0, n = suppliers.length; i < n; i++) {
      if(s == suppliers[i])
	return i;
    }
    return -1;
  }

  private boolean isValidDay(int day) {
    return !(day < 0 || day > numberOfDays);
  }

  private boolean isValidManufacturer(int manufacturer) {
    return manufacturers == null ||
      !(manufacturer < 0 || manufacturer >= manufacturers.length);
  }

  private boolean isValidFactory(int factory) {
    return factories == null ||
      !(factory < 0 || factory >= factories.length);
  }

  private boolean isValidCustomer(int customer) {
    return customers == null ||
      !(customer < 0 || customer >= customers.length);
  }

  private boolean isValidSupplier(int supplier) {
    return suppliers == null ||
      !(supplier < 0 || supplier >= suppliers.length);
  }


  public ComponentNegotiation[] getComponentNegotiation
    (int day, Manufacturer manufacturer) {

    if(!isValidDay(day) ||
       manufacturer == null ||
       compNegotiation[day] == null)
      return null;

    ArrayList ll = new ArrayList();
    for (int i = 0, n = compNegotiation[day].length; i < n; i++) {
      if(compNegotiation[day][i].getBuyer() == manufacturer)
	ll.add(compNegotiation[day][i]);
    }

    return (ComponentNegotiation[])
      ll.toArray(new ComponentNegotiation[ll.size()]);
  }

  public ComponentNegotiation[] getComponentNegotiation
    (int day, Supplier supplier) {

    if(!isValidDay(day) ||
       supplier == null ||
       compNegotiation[day] == null)
      return null;

    ArrayList ll = new ArrayList();
    for (int i = 0, n = compNegotiation[day].length; i < n; i++) {
      if(compNegotiation[day][i].getSeller() == supplier)
	ll.add(compNegotiation[day][i]);
    }

    return (ComponentNegotiation[])
      ll.toArray(new ComponentNegotiation[ll.size()]);
  }

  public ComponentNegotiation[] getComponentNegotiation(int day) {

    if(!isValidDay(day) ||
       compNegotiation[day] == null)
      return null;

    return compNegotiation[day];
  }

  public PCNegotiation[] getPCNegotiation(int day) {

    if(!isValidDay(day)) {
      return null;
    }

    return pcNegotiation[day];
  }
} // SimulationInfo
