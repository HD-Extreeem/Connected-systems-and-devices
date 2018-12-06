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
 * OfferCounter
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Thu Jun 26 16:07:51 2003
 * Updated : $Date: 2003/07/03 13:22:45 $
 *           $Revision: 1.3 $
 */

import java.io.IOException;
import java.text.ParseException;

import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;

/**
 * <code>OfferCounter</code> is a simple example of a log handler that
 * uses a specific parser to extract information from log files.
 */
public class OfferCounter extends LogHandler {

  public OfferCounter() {
  }

  /**
   * Invoked when a new log file should be processed.
   *
   * @param reader the log reader for the log file.
   */
  protected void start(LogReader reader) throws IOException, ParseException {
    OfferCounterParser parser = new OfferCounterParser(reader);
    parser.start();
    parser.stop();
    parser.printOfferCount();
  }

} // OfferCounter
