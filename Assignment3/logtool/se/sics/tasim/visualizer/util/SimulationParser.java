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
 * SimulationParser
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Thu Jun  5 16:53:26 2003
 * Updated : $Date: 2005/04/15 19:53:40 $
 *           $Revision: 1.23 $
 */

package se.sics.tasim.visualizer.util;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.botbox.util.ArrayUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.*;
import se.sics.tasim.tac03.Parser;
import se.sics.tasim.visualizer.info.*;
import se.sics.tasim.visualizer.monitor.ParserMonitor;

public class SimulationParser extends Parser {

  private static final int SUPPLIER_PRODUCTIONLINES = 2;
  private static final int INITIAL_SIZE_CUSTOMER_RFQS = 200;

  private LogHandler logHandler;

  private ParserMonitor[] monitors;

  private int numberOfDays;
  private int simulationID;
  private String simulationType;
  private long startTime;
  private int secondsPerDay;
  private int bankInterest;
  private int bankInterestMax;
  private int bankInterestMin;
  private int storageCost;
  private String serverName;
  private String serverVersion;

  private ParticipantInfo[] participants;
  private int[] newParticipantIndex;

  private int manufacturerCount;
  private int factoryCount;
  private int supplierCount;
  private int customerCount;

  private Manufacturer[] manufacturers;
  private Factory[] factories;
  private Supplier[] suppliers;
  private Customer[] customers;

  private PCType[] pcTypes;
  private Component[] components;

  private MarketDevelopment marketDevelopment;
  private int marketReportCount;

  private ArrayList[] compNegotiation;
  private TreeMap compRFQs;
  private TreeMap compOffersFull;
  private TreeMap compOffersPartial;
  private TreeMap compOffersEarliest;
  private TreeMap compOrders;

  private PCNegotiation[][] pcNegotiation;
  private TreeMap pcRFQs;
  private TreeMap pcOffers;
  private TreeMap pcOrders;

  private int currentDay;

  private BOMBundle bomBundle;
  private ComponentCatalog componentCatalog;
  private ServerConfig serverConfig;

  private boolean[] gotLastDayAccount;
  private boolean[] gotLastDayInterest;

  private boolean errorParsing = false;
  private boolean isParserWarningsEnabled = true;

  public SimulationParser(LogHandler logHandler, LogReader lr) {
    super(lr);
    this.logHandler = logHandler;
    simulationID = lr.getSimulationID();
    simulationType = lr.getSimulationType();
    startTime = lr.getStartTime();
    serverName = lr.getServerName();
    serverVersion = lr.getServerVersion();
    isParserWarningsEnabled =
      logHandler.getConfig().getPropertyAsBoolean("visualizer.parserWarnings",
						  true);

    // Set up new indexing for participants [0..] for each type
    participants = lr.getParticipants();
    newParticipantIndex = new int[participants.length];

    for (int i = 0, n = participants.length; i < n; i++) {
      switch(participants[i].getRole()) {
      case MANUFACTURER :
	newParticipantIndex[i] = manufacturerCount++; break;
      case SUPPLIER :
	newParticipantIndex[i] = supplierCount++; break;
      case FACTORY :
	newParticipantIndex[i] = factoryCount++; break;
      case CUSTOMER :
	  newParticipantIndex[i] = customerCount++; break;
      }
    }
  }

    protected void parseStarted() {
	if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		monitors[i].parseStarted();
    }


    protected void parseStopped() {
      System.err.println();

	if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		monitors[i].parseStopped();
    }


  /**********************************************************************
   * Message handling dispatching
   *  - a number of methods that gets a chuck, enterperates it and
   *    dispatches it for further processing.
   **********************************************************************/

  protected void message(int sender, int receiver,
			 Transportable content) {

    if (content instanceof DeliveryNotice)
      handleMessage(sender, receiver, (DeliveryNotice) content);
    else if (content instanceof DeliverySchedule)
      handleMessage(sender, receiver, (DeliverySchedule) content);
    else if (content instanceof FactoryStatus)
      handleMessage(sender, receiver, (FactoryStatus) content);
    else if (content instanceof OfferBundle)
      handleMessage(sender, receiver, (OfferBundle) content);
    else if (content instanceof OrderBundle)
      handleMessage(sender, receiver, (OrderBundle) content);
    else if (content instanceof ProductionSchedule)
      handleMessage(sender, receiver, (ProductionSchedule) content);
    else if (content instanceof RFQBundle)
      handleMessage(sender, receiver, (RFQBundle) content);
    else if (content instanceof StartInfo)
      handleMessage(receiver, (StartInfo) content);
    else if (content instanceof BankStatus)
      handleMessage(sender, receiver, (BankStatus) content);

    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].message(sender, receiver, content);
  }

  protected void dataUpdated(int type, Transportable content) {

    if (content instanceof BOMBundle)
      handleData((BOMBundle) content);
    else if (content instanceof ComponentCatalog)
      handleData((ComponentCatalog) content);
    else if (content instanceof StartInfo)
      handleData((StartInfo) content);

	if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		monitors[i].dataUpdated(type, content);
  }

  protected void data(Transportable object) {

    if (object instanceof ServerConfig)
      handleData((ServerConfig) object);

	if(monitors != null)
	    for (int i = 0, n = monitors.length; i < n; i++)
		monitors[i].data(object);
  }


  protected void messageToRole(int sender, int role,
			       Transportable content) {

    if (content instanceof MarketReport) {
      handleData((MarketReport) content);
    }

    else if (content instanceof PriceReport) {
      handleData((PriceReport) content);
    }

    else if (content instanceof RFQBundle && role == MANUFACTURER) {
      handlePCRFQ((RFQBundle) content, newParticipantIndex[sender]);
    }

    //else if (content instanceof SimulationStatus)

    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].messageToRole(sender, role, content);
  }



  /**********************************************************************
   *
   * Message handling routines
   *  - a number of methods called to handle particular types of messages
   *
   **********************************************************************/

    protected void penalty(int supplier, int customer,
			   int orderID, int amount,
			   boolean orderCancelled) {
	PCNegotiation pcNeg = (PCNegotiation) pcOrders.get
	    (new CommMessageKey(orderID, newParticipantIndex[supplier], 0));
	if (pcNeg == null) {
	  warn("no PC order " + orderID + " to "
	       + participants[supplier].getName()
	       + " found for penalty");
	} else {
	  pcNeg.addPenalty();
	  if (orderCancelled) {
	    pcNeg.setCancelled();
	  }
	}

    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].penalty(supplier, customer, orderID, amount,
				orderCancelled);
    }

  protected void dataUpdated(int agent, int type, long value) {
    if (type == DU_BANK_ACCOUNT) {

	int intValue;
	if(value > Integer.MAX_VALUE) {
	    intValue = Integer.MAX_VALUE;
	    warn("suspiciously large account balance " + value
		 + " for " + participants[agent].getName());
	}
	else if(value < Integer.MIN_VALUE) {
	    intValue = Integer.MIN_VALUE;
	    warn("suspiciously small account balance " + value
		 + " for " + participants[agent].getName());
	}
	else
	    intValue = (int) value;

	if(gotLastDayAccount[newParticipantIndex[agent]]) {
	    manufacturers[newParticipantIndex[agent]].
		setAccountBalance(currentDay, intValue);
	}
	else if(currentDay != 0) {
	    manufacturers[newParticipantIndex[agent]].
		setAccountBalance(currentDay-1, intValue);
	    if(currentDay == numberOfDays-1)
		gotLastDayAccount[newParticipantIndex[agent]] = true;
	}
    }

    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].dataUpdated(agent, type, value);
  }

  protected void dataUpdated(int agent, int type, int value) {
      if (type == DU_BANK_ACCOUNT) {

	  if(gotLastDayAccount[newParticipantIndex[agent]]) {
	      manufacturers[newParticipantIndex[agent]].
		  setAccountBalance(currentDay, value);
	  }
	  else if(currentDay != 0) {
	      manufacturers[newParticipantIndex[agent]].
		  setAccountBalance(currentDay-1, value);

	      if(currentDay == numberOfDays-1)
		  gotLastDayAccount[newParticipantIndex[agent]] = true;
	  }
      }

    else if(type >= DU_CAPACITY_FLAG &&
	    type < DU_CAPACITY_FLAG + SUPPLIER_PRODUCTIONLINES) {
      suppliers[newParticipantIndex[agent]].
	setCapacity(type - DU_CAPACITY_FLAG, currentDay, value);
    }
    else if(type >= DU_INVENTORY_FLAG &&
	    type < DU_INVENTORY_FLAG + SUPPLIER_PRODUCTIONLINES) {
      suppliers[newParticipantIndex[agent]].
	setInventory(type - DU_INVENTORY_FLAG, currentDay, value);
    }
    else if(type >= DU_PRODUCTION_FLAG &&
	    type < DU_PRODUCTION_FLAG + SUPPLIER_PRODUCTIONLINES) {
      suppliers[newParticipantIndex[agent]].
	setProduction(type - DU_PRODUCTION_FLAG, currentDay, value);
    }
    else if(type >= DU_COMPONENT_PRICE_FLAG &&
	    type < DU_COMPONENT_PRICE_FLAG + SUPPLIER_PRODUCTIONLINES) {
      suppliers[newParticipantIndex[agent]].
	setPrice(type - DU_COMPONENT_PRICE_FLAG, currentDay, value);
    }

    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].dataUpdated(agent, type, value);
  }

    protected void dataUpdated(int agent, int type, Transportable content) {
    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].dataUpdated(agent, type, content);
    }

  protected void interest(int agent, long amount) {

      int intValue;
      if(amount > Integer.MAX_VALUE) {
	  intValue = Integer.MAX_VALUE;
	  warn("suspiciously large bank interest $" + amount
	       + " for " + participants[agent].getName());
      }
      else if(amount < Integer.MIN_VALUE) {
	  intValue = Integer.MIN_VALUE;
	  warn("suspiciously small bank interest $" + amount
	       + " for " + participants[agent].getName());
      }
      else
	  intValue = (int) amount;

      if(gotLastDayInterest[newParticipantIndex[agent]]) {
	  manufacturers[newParticipantIndex[agent]].
	      setBankInterest(currentDay, intValue);
// 	  System.err.println("Last on last");
      }
      else if(currentDay != 0) {
	  manufacturers[newParticipantIndex[agent]].
	      setBankInterest(currentDay-1, intValue);
	  if(currentDay == numberOfDays-1) {
	      gotLastDayInterest[newParticipantIndex[agent]] = true;
	  }
      }

    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].interest(agent, amount);
  }

  protected void nextDay(int date, long serverTime) {
      currentDay = date;

      int done = (int) (10 * (double) (currentDay+1) / numberOfDays);
      int notDone = 10 - done;

    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].nextDay(date, serverTime);

    System.err.print("Parsing game " + simulationID + ": [");
    for (int i = 0, n = done; i < n; i++)
	System.err.print("*");
    for (int i = 0, n = notDone; i < n; i++)
	System.err.print("-");
    System.err.print("]");
    System.err.print((char)13);
  }

  // Message to manufacturers
  protected void handleMessage(int receiver,
			       StartInfo content) {
    Factory f = null;

    if (bankInterest <= 0) {
      bankInterest = content.getAttributeAsInt("bank.interestRate", -1);
    }
    if (storageCost <= 0) {
      storageCost = content.getAttributeAsInt("factory.storageCost", -1);
    }
    // Find factory
    String factoryAddress = content.getAttribute("factory.address");
    if (factoryAddress != null) {
      for (int i = 0, n = factoryCount; i < n; i++) {
	if(factories[i].getAddress().equals(factoryAddress)) {
	  f = factories[i];
	  break;
	}
      }
    }
    manufacturers[newParticipantIndex[receiver]].setFactory(f);
  }


  protected void handleMessage(int sender, int receiver,
			       BankStatus content) {
  }


  private void handleMessage(int sender, int receiver,
			     FactoryStatus content) {
    factories[newParticipantIndex[sender]].
      setUtilization(currentDay, content.getUtilization());
    for (int i = 0, n = content.getProductCount(); i < n; i++) {
      int productID = content.getProductID(i);
      int productIndex = componentCatalog.getIndexFor(productID);

      // If stock item was not a component it's a PC
      if (productIndex == -1) {
	productIndex = bomBundle.getIndexFor(productID);

	factories[newParticipantIndex[sender]].
	  setPCHolding(productIndex, currentDay, content.getQuantity(i));
      }

      // The stock item was a component
      else {
	factories[newParticipantIndex[sender]].
	  setComponentHolding(productIndex, currentDay,
			      content.getQuantity(i));
      }
    }
  }


  private void handleData(MarketReport content) {
    for (int i = 0, n = content.getSupplyCount(); i < n; i++) {
      int componentIndex = componentCatalog.
	getIndexFor(content.getSupplyProductID(i));

      marketDevelopment.setComponentsProduced(marketReportCount,
					      componentIndex,
					      content.getSupplyProduced(i));
      marketDevelopment.setComponentsDelivered(marketReportCount,
					       componentIndex,
					       content.getSupplyDelivered(i));
    }

    for (int i = 0, n = content.getDemandCount(); i < n; i++) {
      int pcIndex = bomBundle.getIndexFor(content.getDemandProductID(i));

      marketDevelopment.setAveragePCPrice(marketReportCount,
					  pcIndex,
					  content.getAverageProductPrice(i));
      marketDevelopment.setOrderedPCCount(marketReportCount,
					  pcIndex,
					  content.getProductsOrdered(i));
    }

    marketReportCount++;
  }

  // Highest and lowest order price for PC's
  private void handleData(PriceReport content) {
    for (int i = 0, n = content.size(); i < n; i++) {
      int index = bomBundle.getIndexFor(content.getProductID(i));
      marketDevelopment.setHighestPCPrice(currentDay, index,
					  content.getHighestPrice(i));
      marketDevelopment.setLowestPCPrice(currentDay, index,
					 content.getLowestPrice(i));
    }
  }


  private void handleData(ServerConfig content) {
      serverConfig = content;

    secondsPerDay = content.getAttributeAsInt("game.secondsPerDay", -1);
    bankInterestMax = content.getAttributeAsInt("bank.interestMax", -1);
    bankInterestMin = content.getAttributeAsInt("bank.interestMin", -1);

    Customer.setParams
      (content.getAttributeAsInt("customer.quantityMin", -1),
       content.getAttributeAsInt("customer.quantityMax", -1),
       content.getAttributeAsInt("customer.dueDateMin", -1),
       content.getAttributeAsInt("customer.dueDateMax", -1),
       content.getAttributeAsInt("customer.rfqAvgMin", -1),
       content.getAttributeAsInt("customer.rfqAvgMax", -1),
       content.getAttributeAsInt("customer.daysBeforeVoid", -1),
       Float.parseFloat(content.getAttribute("customer.trendMin", "0")));
    Supplier.setParams
      (content.getAttributeAsInt("supplier.nominalCapacity", -1),
       content.getAttributeAsInt("supplier.maxRFQs", -1),
       content.getAttributeAsFloat("supplier.discountFactor", -1f));

    connectSuppliersWithComponents();
  }


  private void handleData(StartInfo startInfo) {
    numberOfDays = startInfo.getNumberOfDays();
    initActors();
    initCommunication();
    marketDevelopment = new MarketDevelopment(suppliers, components,
					      pcTypes, numberOfDays);
  }

  private void handleData(BOMBundle bomBundle) {
    this.bomBundle = bomBundle;
    initMerchandise();
  }

  private void handleData(ComponentCatalog catalog) {
    this.componentCatalog = catalog;
    initMerchandise();
  }

  public void unhandledNode(String nodeName) {
    if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	    monitors[i].unhandledNode(nodeName);
  }

  /**********************************************************************
   *
   * Message handling routines - Communication
   *  - a number of methods called to handle transaction messages
   *
   **********************************************************************/

    protected void transaction(int supplier, int customer,
			       int orderID, long amount) {
      if (participants[customer].getRole() == CUSTOMER) {
	PCNegotiation pcNeg = (PCNegotiation) pcOrders.get
	  (new CommMessageKey(orderID, newParticipantIndex[supplier], 0));
	if (pcNeg != null) {
	  if (pcNeg.isDelivered()) {
	    warn("PC order " + orderID + " to "
		 + participants[supplier].getName() + " delivered again "
		 + " (first delivered " + pcNeg.getDeliveryDate()
		 + ')');
	  } else {
	    pcNeg.setDeliveryDate(currentDay);
	  }
	} else {
	  warn("no PC order " + orderID + " to "
	       + participants[supplier].getName()
	       + " found for delivery");
	}
      }

      if(monitors != null)
	for (int i = 0, n = monitors.length; i < n; i++)
	  monitors[i].transaction(supplier, customer, orderID, amount);
    }

  private void handleMessage(int sender, int receiver,
			     DeliveryNotice content) {
    int senderIndex = newParticipantIndex[sender];
    int receiverIndex = newParticipantIndex[receiver];

    for (int i = 0, n = content.size(); i < n; i++) {
      // Hack: using sender index as day in key! FIX THIS!!!
      Object compNeg = compOrders.get
	(new CommMessageKey(content.getOrderID(i), receiverIndex,
			    senderIndex));
      if (compNeg instanceof ComponentNegotiation) {
	ComponentNegotiation cn = (ComponentNegotiation) compNeg;
	if (cn.getDeliveryDate() > 0) {
	  warn("order " + content.getOrderID(i)
	       + " from " + participants[sender].getName()
	       + " to " + participants[receiver].getName()
	       + " already delivered");
	} else {
	  cn.setDelivered(currentDay);
	}

      } else if (compNeg instanceof ArrayList) {
	int productID = content.getProductID(i);
	int quantity = content.getQuantity(i);
	ArrayList list = (ArrayList) compNeg;
	boolean orderFound = false;
	for (int j = 0, m = list.size(); j < m; j++) {
	  ComponentNegotiation cn = (ComponentNegotiation) list.get(j);
	  if (cn.isDelivered()
	      || cn.getOrderedQuantity() != quantity
	      || cn.getComponent().getProductID() != productID) {
	    continue;
	  }

	  cn.setDelivered(currentDay);
	  orderFound = true;
	  break;
	}
	if (!orderFound) {
	  warn("could not find order " + content.getOrderID(i)
	       + " for delivery from "
	       + participants[sender].getName()
	       + " to " + participants[receiver].getName());
	}

      } else if (compNeg != null) {
	warn("unknown component negotiation data: " + compNeg);

      } else {
	warn("could not find order " + content.getOrderID(i)
	     + " for delivery from "
	     + participants[sender].getName()
	     + " to " + participants[receiver].getName());
      }


//       ComponentNegotiation cn = (ComponentNegotiation) compOrders.get
// 	(new CommMessageKey(content.getOrderID(i), receiverIndex,
// 			    senderIndex));

//       /*
//       if(currentDay == 5)
// 	System.err.println(manufacturers[receiverIndex].getName() +
// 			   " RFQ day: " + cn.getRFQDueDate() +
// 			   " component: " + cn.getComponent().getName());
//       */

//       if(cn != null) {
// 	if (cn.getDeliveryDate() > 0) {
// // 	  warn("order " + content.getOrderID(i)
// // 	       + " from " + participants[sender].getName()
// // 	       + " to " + participants[receiver].getName()
// // 	       + " already delivered");
// 	} else {
// 	  cn.setDelivered(currentDay);
// 	}
//       } else
// 	warn("could not find order " + content.getOrderID(i)
// 	     + " for delivery from "
// 	     + participants[sender].getName()
// 	     + " to " + participants[receiver].getName());
    }
  }

  private void handleMessage(int sender, int receiver,
			     DeliverySchedule content) {
  }
  private void handleMessage(int sender, int receiver,
			     ProductionSchedule content) {
  }


  private void handleMessage(int sender, int receiver,
			     RFQBundle content) {

    int senderIndex = newParticipantIndex[sender];
    int receiverIndex = newParticipantIndex[receiver];

    // RFQs to suppliers for components
    if(participants[sender].getRole() == MANUFACTURER) {

      // Create new negotiation obj
      if(compNegotiation[currentDay] == null)
	compNegotiation[currentDay] = new ArrayList();

      for (int i = 0, n = content.size(); i < n; i++) {
	int componentIndex =
	  componentCatalog.getIndexFor(content.getProductID(i));
	if (componentIndex < 0) {
	  warn("unknown RFQ product "
	       + content.getProductID(i)
	       + " from "
	       + participants[sender].getName()
	       + " to " + participants[receiver].getName());
	} else {
	  ComponentNegotiation cn = new ComponentNegotiation
	    (components[componentIndex],
	     manufacturers[senderIndex], suppliers[receiverIndex],
	     content.getDueDate(i),content.getQuantity(i));
	  compNegotiation[currentDay].add(cn);
	  compRFQs.put(new CommMessageKey(content.getRFQID(i),
					  senderIndex, currentDay), cn);
	}
      }
    }
  }

  private void handlePCRFQ(RFQBundle bundle, int customer) {
    int size = bundle.size();
    if (size == 0) {
      return;
    }

    //RFQs from customer for PC's
    int index = 0;
    PCNegotiation[] pcList;
    if(pcNegotiation[currentDay] == null) {
      pcList = pcNegotiation[currentDay] = new PCNegotiation[size];
    } else {
      index = pcNegotiation[currentDay].length;
      pcList = pcNegotiation[currentDay] = (PCNegotiation[])
	ArrayUtils.setSize(pcNegotiation[currentDay], index + size);
    }

    for (int i = 0; i < size; i++) {
      PCNegotiation pcNeg = new PCNegotiation
	(customers[customer],
	 pcTypes[bomBundle.getIndexFor(bundle.getProductID(i))],
	 bundle.getQuantity(i), bundle.getDueDate(i),
	 bundle.getPenalty(i), bundle.getReservePricePerUnit(i));

      pcList[index++] = pcNeg;

      pcRFQs.put(new CommMessageKey(bundle.getRFQID(i),
				    0, currentDay), pcNeg);
    }
  }



  private void handleMessage(int sender, int receiver,
			     OfferBundle content) {

    int senderIndex = newParticipantIndex[sender];
    int receiverIndex = newParticipantIndex[receiver];

    // Offers from Manufacturers to customers
    if(participants[sender].getRole() == MANUFACTURER) {
      for (int i = 0, n = content.size(); i < n; i++) {

	// Get corresponding RFQ (in compNegotiation) from same day
	PCNegotiation pcNeg = (PCNegotiation)
	  pcRFQs.get(new CommMessageKey(content.getRFQID(i), 0,
					currentDay));
	// No rfq? error!
	if(pcNeg == null) {
	  warn("no RFQ found for PC offer " + content.getOfferID(i)
	       + " from " + participants[sender].getName());
	  return;
	}

	int offeredUnitPrice = content.getUnitPrice(i);
	if (0 > offeredUnitPrice) {
	  warn("negative PC offer price " + offeredUnitPrice
	       + " from " + participants[sender].getName()
	       + " to " + participants[receiver].getName());
	}
	if (content.getDueDate(i) != pcNeg.getRFQDueDate()) {
	  warn("wrong PC offer dueDate " + content.getDueDate(i)
	       + " <=> " + pcNeg.getRFQDueDate()
	       + " from " + participants[sender].getName()
	       + " to " + participants[receiver].getName());
	}
	if (content.getQuantity(i) != pcNeg.getRFQQuantity()) {
	  warn("wrong PC offer quantity "
	       + content.getQuantity(i)
	       + " <=> " + pcNeg.getRFQQuantity()
	       + " from " + participants[sender].getName()
	       + " to " + participants[receiver].getName());
	}
	pcNeg.addBid(manufacturers[senderIndex], content.getUnitPrice(i));

	// Add store offer in tree
	pcOffers.put(new CommMessageKey(content.getOfferID(i),
					senderIndex, currentDay), pcNeg);

      }
    }

    // Offers from Suppliers to manufacturers
    else if(participants[sender].getRole() == SUPPLIER) {
      for (int i = 0, n = content.size(); i < n; i++) {

	// Get corresponding RFQ (in compNegotiation) from the day before
	ComponentNegotiation cn = (ComponentNegotiation)
	  compRFQs.get(new CommMessageKey(content.getRFQID(i),
					  receiverIndex, currentDay-1));

	// No rfq? error!
	if(cn == null) {
	  warn("no RFQ found for component offer " + content.getOfferID(i)
	       + " from " + participants[sender].getName());
	  return;
	}

	boolean isPartial = false;
	if (content.getQuantity(i) < cn.getRFQQuantity()
	    && content.getDueDate(i) == cn.getRFQDueDate()) {
	  // Full or partial offer
	  for (int j = i + 1; j < n; j++) {
	    if (content.getRFQID(j) == content.getRFQID(i)) {
	      // Offer to same RFQ
	      if ((content.getDueDate(j) > cn.getRFQDueDate())
		  || (content.getQuantity(i) < content.getQuantity(j))) {
		// Offer j is full or earliest offer, offer i must be partial
		isPartial = true;
	      }
	      break;
	    }
	  }
	}

	cn.addOffer(content.getUnitPrice(i),
		    content.getDueDate(i), content.getQuantity(i),
		    isPartial);

	// Add offer to correct 'offer type' container
	if(isPartial)
	  compOffersPartial.put(new CommMessageKey(content.getOfferID(i),
						   senderIndex,
						   currentDay), cn);
	else if(content.getDueDate(i) > cn.getRFQDueDate())
	  compOffersEarliest.put(new CommMessageKey(content.getOfferID(i),
						    senderIndex,
						    currentDay), cn);
	else
	  compOffersFull.put(new CommMessageKey(content.getOfferID(i),
						senderIndex,
						currentDay), cn);
      }
    }
  }


  private void handleMessage(int sender, int receiver,
			     OrderBundle content) {
    int senderIndex = newParticipantIndex[sender];
    int receiverIndex = newParticipantIndex[receiver];

    // Orders from Manufacturers to suppliers
    if(participants[sender].getRole() == MANUFACTURER &&
       participants[receiver].getRole() == SUPPLIER) {

      for (int i = 0, n = content.size(); i < n; i++) {
	CommMessageKey key = new CommMessageKey(content.getOfferID(i),
						receiverIndex, currentDay);
	ComponentNegotiation cn;
	short orderType;

	if(compOffersFull.containsKey(key)) {
	  cn = (ComponentNegotiation) compOffersFull.get(key);
	  orderType = ComponentNegotiation.FULL_ORDER;
	}
	else if(compOffersPartial.containsKey(key)) {
	  cn = (ComponentNegotiation) compOffersPartial.get(key);
	  orderType = ComponentNegotiation.PARTIAL_ORDER;
	}
	else if(compOffersEarliest.containsKey(key)) {
	  cn = (ComponentNegotiation) compOffersEarliest.get(key);
	  orderType = ComponentNegotiation.EARLIEST_ORDER;
	}
	else {// No offer? error!
	  warn("no offer " + content.getOfferID(i)
	       + " for order " + content.getOrderID(i)
	       + " from " + participants[sender].getName()
	       + " to " + participants[receiver].getName());
	  continue;
	}

	if (cn.isOrdered()) {
// 	  warn("other offer for order " + content.getOrderID(i)
// 	       + " from " + participants[sender].getName()
// 	       + " to " + participants[receiver].getName()
// 	       + " already accepted");
	  continue;
	}
	cn.addOrder(orderType);

	// Hack: using receiver index as day in key! FIX THIS!!!
	key = new CommMessageKey(content.getOrderID(i), senderIndex,
				 receiverIndex);
	Object compNeg = compOrders.get(key);
	if (compNeg == null) {
	  compOrders.put(key, cn);
	} else if (compNeg instanceof ComponentNegotiation) {
	  ArrayList list = new ArrayList();
	  ComponentNegotiation oldCn = (ComponentNegotiation) compNeg;
	  addOrder(list, oldCn);
	  addOrder(list, cn);
	  compOrders.put(key, list);

	} else if (compNeg instanceof ArrayList) {
	  addOrder((ArrayList) compNeg, cn);
	} else {
	  warn("unknown component negotiation data: " + compNeg);
	}
      }
    }

    // Orders from Customers to manufactureres
    else if(participants[sender].getRole() == CUSTOMER &&
	    participants[receiver].getRole() == MANUFACTURER) {
      for (int i = 0, n = content.size(); i < n; i++) {

	PCNegotiation pcNeg = (PCNegotiation) pcOffers.get
	  (new CommMessageKey(content.getOfferID(i),
			      receiverIndex, currentDay-1));

	if(pcNeg == null)
	  return;

	pcNeg.addOrder(manufacturers[receiverIndex]);

	// store order in tree
	pcOrders.put(new CommMessageKey(content.getOrderID(i),
					receiverIndex, 0), pcNeg);

      }
    }

    else {
      warn("strange offers from " + participants[sender].getName()
	   + " to " + participants[receiver].getName());
    }
  }

  private void addOrder(ArrayList list, ComponentNegotiation cn) {
    int dueDate = cn.getOrderDueDate();
    // Insert the new order to have the orders sorted in due date order
    ComponentNegotiation previousOrder;
    int index = list.size();
    list.add(null);
    while ((index > 0) &&
	   ((previousOrder = (ComponentNegotiation) list.get(index - 1))
	    .getOrderDueDate() > dueDate)) {
      list.set(index, previousOrder);
      index--;
    }
    list.set(index, cn);
  }


  /**********************************************************************
   *
   * Access methods - to get data from the parser
   *
   **********************************************************************/

    public void addMonitor(ParserMonitor monitor) {
	monitors = (ParserMonitor[]) ArrayUtils.add(ParserMonitor.class,
						    monitors, monitor);
    }

    public void removeMonitor(ParserMonitor monitor) {
	monitors = (ParserMonitor[]) ArrayUtils.remove(monitors, monitor);
    }

    public ParserMonitor[] getMonitors() {
	return monitors;
    }

    public int getCurrentDay() {
	return currentDay;
    }

  public boolean errorParsing() {
    return errorParsing;
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

  public int getSimulationID() {
    return simulationID;
  }

  public String getSimulationType() {
    return simulationType;
  }

  public long getStartTime() {
    return startTime;
  }

  public String getServer() {
    return serverName + " (version " + serverVersion + ')';
  }

  public BOMBundle getBOMBundle() {
    return bomBundle;
  }

  public ComponentCatalog getComponentCatalog() {
    return componentCatalog;
  }

  public Manufacturer[] getManufacturers() {
    return manufacturers;
  }

  public Factory[] getFactories() {
    return factories;
  }

  public Supplier[] getSuppliers() {
    return suppliers;
  }

  public Customer[] getCustomers() {
    return customers;
  }

  public PCType[] getPCTypes() {
    return pcTypes;
  }

  public Component[] getComponents() {
    return components;
  }


  public MarketDevelopment getMarketDevelopment() {
    return marketDevelopment;
  }

  public ComponentNegotiation[][] getComponentNegotiation() {
    ComponentNegotiation[][] compNeg =
      new ComponentNegotiation[numberOfDays][];
    for (int i = 0, n = numberOfDays; i < n; i++) {
      if(compNegotiation[i] == null)
	continue;

      int noNegs = compNegotiation[i].size();
      if (noNegs == 0)
	continue;

      compNeg[i] = (ComponentNegotiation[])
	compNegotiation[i].toArray(new ComponentNegotiation[noNegs]);
    }
    return compNeg;
  }

  public PCNegotiation[][] getPCNegotiation() {
    return pcNegotiation;
  }




  /**********************************************************************
   *
   * Inititalization methods for internal data structures
   *
   **********************************************************************/




  private void initMerchandise() {
    if(componentCatalog == null || bomBundle == null)
      return;

    int componentCount = componentCatalog.size();
    // Creating a list of components
    components = new Component[componentCount];

    for (int i = 0, n = componentCount; i < n; i++) {
      components[i] = new Component(componentCatalog.getProductID(i),
				    componentCatalog.getProductName(i),
				    componentCatalog.getProductBasePrice(i));
    }

    int pcTypeCount = bomBundle.size();
    // Creating a list of PC types (BOMs)
    pcTypes = new PCType[pcTypeCount];
    for (int i = 0, n = pcTypeCount; i < n; i++) {
      //componentIDs required for PC i
      int[] partsID = bomBundle.getComponents(i);
      Component[] parts = new Component[partsID.length];
      int basePrice = 0;

      // Look up parts in the component catalog and add the component instance
      for (int j = 0, m = partsID.length; j < m; j++) {
	parts[j] = components[componentCatalog.getIndexFor(partsID[j])];
	basePrice += parts[j].getBasePrice();
      }

      // Verification of the BOM bundle base price
      int productBasePrice = bomBundle.getProductBasePrice(i);
      pcTypes[i] = new PCType(bomBundle.getProductID(i),
			      bomBundle.getProductName(i),
			      productBasePrice > 0
			      ? productBasePrice
			      : basePrice,
			      parts, bomBundle.getAssemblyCyclesRequired(i));
    }

    connectSuppliersWithComponents();
  }



  private void initActors() {
      manufacturers = new Manufacturer[manufacturerCount];
      factories = new Factory[factoryCount];
      suppliers = new Supplier[supplierCount];
      customers = new Customer[customerCount];

    gotLastDayAccount = new boolean[manufacturerCount];
    gotLastDayInterest = new boolean[manufacturerCount];

    for (int i = 0, n = participants.length; i < n; i++) {
      switch(participants[i].getRole()) {
      case MANUFACTURER :
	manufacturers[newParticipantIndex[i]] =
	  new Manufacturer(participants[i].getIndex(),
			   participants[i].getAddress(),
			   participants[i].getName(), numberOfDays);

	break;
      case FACTORY :
	factories[newParticipantIndex[i]] =
	  new Factory(participants[i].getIndex(),
		      participants[i].getAddress(),
		      participants[i].getName(), components, pcTypes,
		      numberOfDays);

	break;
      case SUPPLIER :
	suppliers[newParticipantIndex[i]] =
	  new Supplier(participants[i].getIndex(),
		       participants[i].getAddress(),
		       participants[i].getName(), numberOfDays,
		       SUPPLIER_PRODUCTIONLINES);
	break;
      case CUSTOMER :
	customers[newParticipantIndex[i]] =
	  new Customer(participants[i].getIndex(),
		       participants[i].getAddress(),
		       participants[i].getName());
	break;
      }
    }

    connectSuppliersWithComponents();
  }

    private void connectSuppliersWithComponents() {

	if(suppliers == null ||
	   componentCatalog == null ||
	   serverConfig == null)
	    return;

	for (int i = 0, n = supplierCount; i < n; i++) {
	    StringTokenizer stok = new StringTokenizer
		(serverConfig.getAttribute("supplier." +
					   suppliers[i].getName() +
					   ".products"), ",");
	    int line = 0;
	    while (stok.hasMoreTokens()) {
		String compName = componentCatalog.getProductName
		    (componentCatalog.getIndexFor
		     (Integer.parseInt(stok.nextToken())));

		for (int j = 0, m = components.length; j < m; j++) {
		    if(components[j].getName().equals(compName)) {
			suppliers[i].setComponent(line, components[j]);
			break;
		    }
		}

		line++;
	    }
	}
    }

  private void initCommunication() {
    compNegotiation = new ArrayList[numberOfDays];
    compRFQs = new TreeMap();
    compOffersFull = new TreeMap();
    compOffersPartial = new TreeMap();
    compOffersEarliest = new TreeMap();
    compOrders = new TreeMap();

    pcNegotiation = new PCNegotiation[numberOfDays][];
    pcRFQs = new TreeMap();
    pcOffers = new TreeMap();
    pcOrders = new TreeMap();
  }


  /**********************************************************************
   *
   * Parser log handling (perhaps should be direct calls to LogHandler?)
   *
   **********************************************************************/

  private void warn(String message) {
    if (isParserWarningsEnabled) {
      int timeunit = currentDay;
      logHandler.warn("Parse: [Day " + (timeunit < 10 ? " " : "")
		      + timeunit + "] " + message);
    }
  }


  /**********************************************************************
   *
   *
   *
   **********************************************************************/

  // Keys to hash comMessages with
  protected static class CommMessageKey implements Comparable {
    int id;
    int sender;
    int day;

    CommMessageKey(int id, int sender, int day) {
      this.id = id;
      this.sender = sender;
      this.day = day;
    }

    public int compareTo(Object o) {
      CommMessageKey cm = (CommMessageKey) o;
      if(cm.day < day)
	return -1;
      else if(cm.day > day)
	return 1;

      if(cm.sender < sender)
	return -1;
      else if(cm.sender > sender)
	return 1;

      if(cm.id < id)
	return -1;
      else if(cm.id > id)
	return 1;

      return 0;
    }
  }

} // SimulationParser
