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
 * $Id$
 */

package com.eressea.io.xml;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This class encapsulates some occuring exceptions as IOException.
 */
public class XMLIOException extends IOException {
	private Exception		    exception;
	private static final String EXCEPTION_SEPARATOR = "______________ORIGINAL EXCEPTION____________";

	/**
	 * Creates a new XMLIOException object.
	 *
	 * @param aMessage TODO: DOCUMENT ME!
	 */
	public XMLIOException(String aMessage) {
		super(aMessage);
	}

	/**
	 * Creates a new XMLIOException object.
	 *
	 * @param exception TODO: DOCUMENT ME!
	 */
	public XMLIOException(Exception exception) {
		super(exception.getMessage());
		this.exception = exception;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aTarget TODO: DOCUMENT ME!
	 */
	public void printStackTrace(PrintStream aTarget) {
		synchronized(aTarget) {
			super.printStackTrace(aTarget);

			if(exception != null) {
				aTarget.println(EXCEPTION_SEPARATOR);
				exception.printStackTrace(aTarget);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aTarget TODO: DOCUMENT ME!
	 */
	public void printStackTrace(PrintWriter aTarget) {
		synchronized(aTarget) {
			super.printStackTrace(aTarget);

			if(exception != null) {
				aTarget.println(EXCEPTION_SEPARATOR);
				exception.printStackTrace(aTarget);
			}
		}
	}
}
