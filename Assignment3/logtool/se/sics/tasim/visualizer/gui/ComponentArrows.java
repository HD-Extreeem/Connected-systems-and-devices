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
 * ComponentArrows
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Fri Jun 13 14:26:38 2003
 * Updated : $Date: 2003/06/26 14:14:19 $
 *           $Revision: 1.8 $
 */
package se.sics.tasim.visualizer.gui;
import se.sics.tasim.visualizer.gui.TemporalArrowMesh;
import se.sics.tasim.visualizer.info.SimulationInfo;
import java.awt.Color;
import se.sics.tasim.visualizer.gui.Arrow;
import java.util.LinkedList;

/**
 */
public class ComponentArrows extends TemporalArrowMesh {
  protected SimulationInfo simInfo;
  protected double suppHeight, suppSpc;
  protected double agentHeight, agentSpc;

  public ComponentArrows(CommunicationModel communicationModel,
			 PositiveBoundedRangeModel dayModel,
			 SimulationInfo simInfo,
			 double suppHeight, double suppSpc,
			 double agentHeight, double agentSpc) {
    super(communicationModel, dayModel);
    this.simInfo = simInfo;
    this.suppHeight = suppHeight;
    this.suppSpc = suppSpc;
    this.agentHeight = agentHeight;
    this.agentSpc = agentSpc;
    createArrows();
  }

  protected void createArrows() {
    //    System.err.println("Entered components create arrows");
      double ARROW_SPACING = 0.025;

    // Create visible arrows for each day
    for (int t = 0, l = timeModel.getLast(); t < l; t++) {
      for (int i = 0, n = simInfo.getSupplierCount(); i < n; i++) {
	for (int j = 0, m = simInfo.getManufacturerCount(); j < m; j++) {
	  // If both activity and open chanel then draw arrow
	  if(communicationModel.isActive(j,i,t,
					 CommunicationModel.ACTIVITY_RFQ) &&
	     communicationModel.isOpenChanel(j,i,
					     CommunicationModel.XCOM_RFQ) &&
	     communicationModel.isOpenChanel(j,i,
					     CommunicationModel.YCOM_RFQ)) {
	      addArrow(new Arrow
		  (1, 0.5 + agentHeight*(0.5 - 0.5*m + (m-1-j)) +
		   agentSpc*((m-1-j)-0.5*m) + ARROW_SPACING,
		   0, suppHeight / 2 + (suppHeight + suppSpc)*(n-1-i) + ARROW_SPACING,
		   GUITheme.RFQ_COLOR), t);
	  }

	  if(communicationModel.isActive(j,i,t,
					 CommunicationModel.ACTIVITY_ORDER) &&
	     communicationModel.isOpenChanel(j,i,
					     CommunicationModel.XCOM_ORDER) &&
	     communicationModel.isOpenChanel(j,i,
					     CommunicationModel.YCOM_ORDER)) {
	    addArrow(new Arrow
		     (1, 0.5 + agentHeight*(0.5 - 0.5*m + (m-1-j)) +
		      agentSpc*((m-1-j)-0.5*m) - ARROW_SPACING, 0,
		      suppHeight / 2 + (suppHeight + suppSpc)*(n-1-i) - ARROW_SPACING,
		      GUITheme.ORDER_COLOR), t);
	  }

	}
      }

      for (int i = 0, n = simInfo.getManufacturerCount(); i < n; i++) {
	for (int j = 0, m = simInfo.getSupplierCount(); j < m; j++) {

	  if(communicationModel.isActive(i,j,t,
					 CommunicationModel.ACTIVITY_OFFER) &&
	     communicationModel.isOpenChanel(i,j,
					     CommunicationModel.XCOM_OFFER) &&
	     communicationModel.isOpenChanel(i,j,
					     CommunicationModel.YCOM_OFFER)) {
	    addArrow(new Arrow
		     (0, suppHeight / 2 + (suppHeight + suppSpc)*(m-1-j),
		      1, 0.5 + agentHeight*(0.5-0.5*n+(n-1-i)) +
		      agentSpc*((n-1-i)-0.5*n),
		      GUITheme.OFFER_COLOR), t);
	  }
	}
      }
    }
  }
} // ComponentArrows
