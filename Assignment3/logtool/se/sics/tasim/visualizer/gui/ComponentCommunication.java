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
 * ComponentCommunication
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sun Jun 15 13:55:08 2003
 * Updated : $Date: 2004/07/06 14:43:50 $
 *           $Revision: 1.6 $
 */
package se.sics.tasim.visualizer.gui;

import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.info.ComponentNegotiation;

/**
 */
public class ComponentCommunication extends CommunicationModel {

  public ComponentCommunication(SimulationInfo simInfo) {
    super(simInfo.getManufacturerCount(),
	  simInfo.getSupplierCount(),
	  simInfo.getNumberOfDays());

    for (int t = 0, l = simInfo.getNumberOfDays(); t < l; t++) {

      for (int i = 0, n = simInfo.getSupplierCount(); i < n; i++) {
	  // No offers on first day
	  if(t == 0)
	      continue;

	  ComponentNegotiation[] cn =
	      simInfo.getComponentNegotiation(t-1, simInfo.getSupplier(i));
	  if(cn == null)
	      continue;

	  for (int negIndex = 0, lastNeg = cn.length;
	       negIndex < lastNeg; negIndex++) {

	      // If only RFQ then don't bother
	      if(!cn[negIndex].hasOffer())
		  continue;

	      // Otherwise add an activity
	      addActivityType(simInfo.getManufacturerIndex
			      (cn[negIndex].getBuyer()),i,t,ACTIVITY_OFFER);
	  }
      }



      for (int i = 0, n = simInfo.getManufacturerCount(); i < n; i++) {

	  ComponentNegotiation[] cn =
	      simInfo.getComponentNegotiation(t, simInfo.getManufacturer(i));
	  if(cn == null)
	      continue;

	  for (int negIndex = 0, lastNeg = cn.length;
	       negIndex < lastNeg; negIndex++) {

	      // All negs have an rfq, so add activity
	      addActivityType(i, simInfo.getSupplierIndex
			      (cn[negIndex].getSeller()),t,ACTIVITY_RFQ);
	  }

	  if(t == 0)
	      continue;

	  cn = simInfo.getComponentNegotiation(t-1, simInfo.getManufacturer(i));
	  if(cn == null)
	      continue;

	  for (int negIndex = 0, lastNeg = cn.length;
	       negIndex < lastNeg; negIndex++) {

	      // Only conserned about orders
	      if(cn[negIndex].getOrderType() == ComponentNegotiation.NO_ORDER)
		  continue;

	      addActivityType(i,simInfo.getSupplierIndex
			      (cn[negIndex].getSeller()),t,ACTIVITY_ORDER);
	  }
      }
    }
  }

} // ComponentCommunication
