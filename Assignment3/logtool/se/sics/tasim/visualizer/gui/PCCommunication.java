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
 * PCCommunication
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sun Jun 15 13:55:08 2003
 * Updated : $Date: 2003/06/23 21:19:08 $
 *           $Revision: 1.4 $
 */
package se.sics.tasim.visualizer.gui;

import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.info.PCNegotiation;
import se.sics.tasim.visualizer.info.Manufacturer;

/**
 */
public class PCCommunication extends CommunicationModel {

  public PCCommunication(SimulationInfo simInfo) {
    super(simInfo.getManufacturerCount(),
	  simInfo.getCustomerCount(),
	  simInfo.getNumberOfDays());

    for (int t = 0, l = simInfo.getNumberOfDays(); t < l; t++) {

      for (int i = 0, n = simInfo.getManufacturerCount(); i < n; i++) {

	  PCNegotiation[] pcNeg = simInfo.getPCNegotiation(t);
	  if(pcNeg == null)
	      continue;

	  // rfqs are sent to all manufacturers
	  addActivityType(i,0,t,ACTIVITY_RFQ);

	  for (int offerIndex = 0, nOffers = pcNeg.length;
	       offerIndex < nOffers; offerIndex++) {

	      if(pcNeg[offerIndex].countBids() == 0 ||
		 !pcNeg[offerIndex].isBidding(simInfo.getManufacturer(i)))
		  continue;

	      // Adding offers from bidding manufacturers
	      addActivityType(i,0,t,ACTIVITY_OFFER);
	  }
      }

      // No orders are sent on the first day
      if(t == 0)
	  continue;

      PCNegotiation[] pcNeg = simInfo.getPCNegotiation(t-1);
      if(pcNeg == null)
	  continue;

      for (int i = 0, n = pcNeg.length; i < n; i++) {
	  Manufacturer m = pcNeg[i].getOrderWinner();
	  if(m == null)
	      continue;
	  addActivityType(simInfo.getManufacturerIndex(m), 0, t,
			  ACTIVITY_ORDER);
      }
    }
  }

} // PCCommunication
