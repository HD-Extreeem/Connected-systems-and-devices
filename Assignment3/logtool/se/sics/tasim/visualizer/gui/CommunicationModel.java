/**
 * SICS ISL Java Utilities
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
 * CommunicationModel
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : Sun Feb 02 13:15:02 2003
 * Updated : $Date: 2003/06/17 19:19:29 $
 *           $Revision: 1.3 $
 */

package se.sics.tasim.visualizer.gui;
import javax.swing.*;
import javax.swing.event.*;

/**
*/
public class CommunicationModel {
  public static final int CHANNEL_ERROR = 1<<6;
  public static final int ACTIVITY_RFQ = 1;
  public static final int ACTIVITY_OFFER = 1<<1;
  public static final int ACTIVITY_ORDER = 1<<2;
  public static final int XCOM_RFQ = 1;
  public static final int XCOM_OFFER = 1<<1;
  public static final int XCOM_ORDER = 1<<2;
  public static final int YCOM_RFQ = 1<<3;
  public static final int YCOM_OFFER = 1<<4;
  public static final int YCOM_ORDER = 1<<5;
  public static final int XCOM_ALL =
    XCOM_RFQ | XCOM_OFFER | XCOM_ORDER;
  public static final int YCOM_ALL =
    YCOM_RFQ | YCOM_OFFER | YCOM_ORDER;
  public static final int COM_ALL = XCOM_ALL | YCOM_ALL;




  protected ChangeEvent changeEvent = null;
  protected EventListenerList listenerList = new EventListenerList();

  protected int numberOfXActors;
  protected int numberOfYActors;

  protected int[][] channels;
  protected int[][][] activity;

  public CommunicationModel(int xActors, int yActors, int time) {
    numberOfXActors = xActors;
    numberOfYActors = yActors;
    channels = new int[xActors][yActors];
    activity = new int[time][xActors][yActors];
  }

  public void setChannelType(int xActor, int yActor, int channelType) {
    if(xActor >= numberOfXActors || yActor >= numberOfYActors)
      return;

    channels[xActor][yActor] = channelType;
    fireStateChanged();
  }

  public void openAllChannels(int channelType) {
    for (int i = 0, n = channels.length; i < n; i++)
      for (int j = 0, m = channels[i].length; j < m; j++)
	channels[i][j] = (int) (channels[i][j] | channelType);
    fireStateChanged();
  }

  public void openAllXChannels(int xActor, int channelType) {
    for (int i = 0, n = channels[xActor].length; i < n; i++)
      channels[xActor][i] = (int) (channels[xActor][i] | channelType);
    fireStateChanged();
  }

  public void openAllYChannels(int yActor, int channelType) {
    for (int i = 0, n = channels.length; i < n; i++)
      channels[i][yActor] = (int) (channels[i][yActor] | channelType);
    fireStateChanged();
  }

  public void openChannel(int xActor, int yActor, int channelType) {
    if(xActor >= numberOfXActors || yActor >= numberOfYActors)
      return;

    channels[xActor][yActor] = (int) (channels[xActor][yActor] | channelType);
    fireStateChanged();
  }

  // Can the channel show messages of channelType
  public boolean isOpenChanel(int xActor, int yActor, int channelType) {
    return (channels[xActor][yActor] & channelType) == channelType;
  }

  public void closeChannel(int xActor, int yActor, int channelType) {
    if(xActor >= numberOfXActors || yActor >= numberOfYActors)
      return;

    channels[xActor][yActor] = (int) (channels[xActor][yActor] & ~channelType);
    fireStateChanged();
  }

  public void closeAllXChannels(int xActor, int channelType) {
    for (int i = 0, n = channels[xActor].length; i < n; i++)
      channels[xActor][i] = (int) (channels[xActor][i] & ~channelType);
    fireStateChanged();
  }

  public void closeAllYChannels(int yActor, int channelType) {
    for (int i = 0, n = channels.length; i < n; i++)
      channels[i][yActor] = (int) (channels[i][yActor] & ~channelType);
    fireStateChanged();
  }

  public void closeAllChannels(int channelType) {
    for (int i = 0, n = channels.length; i < n; i++)
      for (int j = 0, m = channels[i].length; j < m; j++)
	channels[i][j] = (int) (channels[i][j] & ~channelType);
    fireStateChanged();
  }

  public int getChannelType(int xActor, int yActor) {
    if(xActor >= numberOfXActors || yActor >= numberOfYActors)
      return CHANNEL_ERROR;
    return channels[xActor][yActor];
  }

  public void setActivityType(int xActor, int yActor, int time,
			      int activityType) {
    if(xActor >= numberOfXActors || yActor >= numberOfYActors)
      return;

    activity[time][xActor][yActor] = activityType;
    fireStateChanged();
  }

  public void addActivityType(int xActor, int yActor, int time,
			      int activityType) {
    if(xActor >= numberOfXActors || yActor >= numberOfYActors)
      return;

    activity[time][xActor][yActor] =
      (int) (activity[time][xActor][yActor] | activityType);
    fireStateChanged();
  }

  // Ret true if at least activity type is active in the chanel
  public boolean isActive(int xActor, int yActor, int time,
			      int activityType) {
    return (activity[time][xActor][yActor] & activityType) == activityType;

  }

  public int getActivityType(int xActor, int yActor, int time) {
    if(xActor >= numberOfXActors || yActor >= numberOfYActors)
      return CHANNEL_ERROR;
    return activity[time][xActor][yActor];
  }

  /**
   * Adds a <code>ChangeListener</code> to the model. The listener is
   * notified each time the model changes.
   *
   * @param l The <code>ChangeListener</code>
   */
  public void addChangeListener(ChangeListener l) {
    listenerList.add(ChangeListener.class, l);
  }

  /**
   * Removes a <code>ChangeListener</code> from the model. The listener
   * will no longer be notified when the model changes.
   *
   * @param l The <code>ChangeListener</code>
   */
  public void removeChangeListener(ChangeListener l) {
    listenerList.remove(ChangeListener.class, l);
  }

  /**
   * Notifies registered <code>ChangeListeners</code>
   *
   */
  protected void fireStateChanged() {
    Object[] listeners = listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -=2 ) {
      if (listeners[i] == ChangeListener.class) {
	if (changeEvent == null) {
	  changeEvent = new ChangeEvent(this);
	}
	((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
      }
    }
  }
} // CommunicationModel
