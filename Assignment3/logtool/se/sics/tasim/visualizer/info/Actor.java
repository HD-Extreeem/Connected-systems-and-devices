/**
 * TAC Supply Chain Management Simulator
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
 * Actor
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Fri Feb 28 13:00:43 2003
 * Updated : $Date: 2003/07/15 13:55:59 $
 *           $Revision: 1.2 $
 */
package se.sics.tasim.visualizer.info;

/*
 *
 */
public class Actor {

  private int simulationIndex;
  private String address;
  private String name;

  public Actor(int simulationIndex, String address, String name) {
    this.simulationIndex = simulationIndex;
    this.address = address;
    this.name = name == null ? address : name;
  }

  public int getSimulationIndex() {
    return simulationIndex;
  }

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

} // Actor
