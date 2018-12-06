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
 * Visualizer
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Fri Jun  6 17:28:49 2003
 * Updated : $Date: 2003/07/25 09:27:31 $
 *           $Revision: 1.18 $
 */
package se.sics.tasim.visualizer;
import java.io.IOException;
import java.text.ParseException;

import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.visualizer.gui.MainWindow;
import se.sics.tasim.visualizer.gui.PositiveBoundedRangeModel;
import se.sics.tasim.visualizer.info.SimulationInfo;
import se.sics.tasim.visualizer.monitor.ParserMonitor;
import se.sics.tasim.visualizer.util.SimulationParser;

/**
 */
public class Visualizer extends LogHandler {
    MainWindow mainWindow = null;

    protected void start(LogReader reader)
	throws IllegalConfigurationException,
	       IOException,
	       ParseException {

	SimulationParser sp = new SimulationParser(this, reader);
	PositiveBoundedRangeModel dayModel = new PositiveBoundedRangeModel();
	createMonitors(sp, dayModel);

	// Start parsing
	sp.start();
	if(sp.errorParsing()) {
	    System.err.println("Error while parsing file");
	    return;
	}

	// Create simulation info object and let monitors do post processing
	SimulationInfo simInfo = new SimulationInfo(sp);

	// Show the visualizer window
	if(getConfig().getPropertyAsBoolean("showGUI", true) &&
	   simInfo != null) {
	    dayModel.setLast(simInfo.getNumberOfDays()-1);
	    mainWindow = new MainWindow(simInfo, dayModel, sp.getMonitors());
	    mainWindow.setVisible(true);
	}

	sp = null;
    }


    private void createMonitors(SimulationParser sp,
				PositiveBoundedRangeModel dayModel)
	throws IllegalConfigurationException, IOException {
	String[] names =
	    getConfig().getPropertyAsArray("monitor.names");
	ParserMonitor[] monitors = (ParserMonitor[]) getConfig().
	    createInstances("monitor", ParserMonitor.class, names);

	if(monitors == null)
	    return;

	for (int i = 0, n = monitors.length; i < n; i++) {
	    monitors[i].init(names[i], this, sp, dayModel);
	    sp.addMonitor(monitors[i]);
	}

    }
} // Visualizer
