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
 * DayChanger
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson, Anders Sundman
 * Created : Sat Jun  7 18:52:36 2003
 * Updated : $Date: 2003/06/18 15:25:59 $
 *           $Revision: 1.4 $
 */

package se.sics.tasim.visualizer.gui;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.Box;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.Timer;

public class DayChanger {
    protected final int SLIDER_DELAY = 150;

    JPanel mainPane, buttonPane;
    JSlider daySlider;
    JButton nextDayButton, prevDayButton, lastDayButton, firstDayButton;
    JLabel dayLabel;

    ActionListener actionListeners = null;

    PositiveBoundedRangeModel dayModel;

    public DayChanger(PositiveBoundedRangeModel dm) {
	dayModel = dm;

	mainPane = new JPanel();
	mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
	mainPane.setBorder(BorderFactory.createTitledBorder
			   (BorderFactory.createEtchedBorder(),
			    " Day Changer "));

	buttonPane = new JPanel();
	buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

	daySlider = new JSlider();
	daySlider.setMinimum(0);
	daySlider.setMaximum(dayModel.getLast());
	daySlider.setValue(dayModel.getCurrent());
	daySlider.addChangeListener(new ChangeListener() {
		int value = -1;

		Timer timer = new Timer(SLIDER_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			    dayModel.setCurrent(value);

			    // Stop timer if slider was released
			    if (!daySlider.getValueIsAdjusting())
				timer.stop();
			}
		    });

		public void stateChanged(ChangeEvent ce) {
		    value = daySlider.getValue();

		    // Start timer for updating if it isn't running
		    if(!timer.isRunning())
			timer.start();
		}
	    });

	dayLabel = new JLabel(dayModel.getCurrent() + " / " +
			      dayModel.getLast());

	dayModel.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent ce) {
		    daySlider.setMaximum(dayModel.getLast());
		    daySlider.setValue(dayModel.getCurrent());
		    dayLabel.setText(dayModel.getCurrent() +
				     " / " +
				     dayModel.getLast());
		}
	    });


	nextDayButton = new JButton(">");
	prevDayButton = new JButton("<");
	lastDayButton = new JButton(">|");
	firstDayButton = new JButton("|<");

	nextDayButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	prevDayButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	lastDayButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
	firstDayButton.setAlignmentX(Component.RIGHT_ALIGNMENT);

	nextDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.changeCurrent(1);
		}
	    });

	prevDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.changeCurrent(-1);
		}
	    });

	firstDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.setCurrent(0);
		}
	    });

	lastDayButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    dayModel.setCurrent(dayModel.getLast());
		}
	    });

	buttonPane.add(firstDayButton);
	buttonPane.add(prevDayButton);
	buttonPane.add(Box.createHorizontalGlue());
	buttonPane.add(dayLabel);
	buttonPane.add(Box.createHorizontalGlue());
	buttonPane.add(nextDayButton);
	buttonPane.add(lastDayButton);

	mainPane.add(buttonPane);
	mainPane.add(daySlider);
    }

    public JPanel getMainPane() {
	return mainPane;
    }
} // DayChanger
