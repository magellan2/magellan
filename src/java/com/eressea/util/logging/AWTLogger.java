/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package com.eressea.util.logging;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/*
 * @author Ilja Pavkovic
 */
public class AWTLogger implements AWTEventListener {
	PrintStream out;

	// right now log focus, window and key events
	private final long AWTMASK = AWTEvent.FOCUS_EVENT_MASK | AWTEvent.KEY_EVENT_MASK;

	/*

	    | AWTEvent.WINDOW_EVENT_MASK;
	*/
	public AWTLogger() {
		try {
			out = new PrintStream(new FileOutputStream("awtDebug" +
													   System.getProperty("java.version") + ".txt"));
			Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTMASK);
		} catch(IOException e) {
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void eventDispatched(AWTEvent e) {
		log("AWTLogger.eventDispatched: " + e);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aObj TODO: DOCUMENT ME!
	 */
	public void log(Object aObj) {
		log(aObj, null);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aObj TODO: DOCUMENT ME!
	 * @param aThrowable TODO: DOCUMENT ME!
	 */
	public void log(Object aObj, Throwable aThrowable) {
		if(aObj != null) {
			if(aObj instanceof Throwable) {
				((Throwable) aObj).printStackTrace(out);
			} else {
				out.println(aObj);
			}
		}

		if(aThrowable != null) {
			aThrowable.printStackTrace(out);
		}
	}
}
