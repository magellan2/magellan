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

package com.eressea.swing.tree;

import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster A simple nodewrapper wrapping a list of units allowing
 * 		   acces to them via getUnits().
 */
public class UnitListNodeWrapper implements SupportsClipboard {
	// identifies that this UnitListNodeWrapper contains a list of units that are
	// some other unit's students

	/** TODO: DOCUMENT ME! */
	public static final int STUDENT_LIST   = 1;
	private int			    type		   = 0;
	protected Collection    units		   = null;
	protected String	    text		   = null;
	protected String	    clipboardValue = null;

	/**
	 * Creates new UnitListNodeWrapper
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param clipboardValue TODO: DOCUMENT ME!
	 * @param units TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 */
	public UnitListNodeWrapper(String text, String clipboardValue,
							   Collection units, int type) {
		this(text, clipboardValue, units);
		this.type = type;
	}

	/**
	 * Creates a new UnitListNodeWrapper object.
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param clipboardValue TODO: DOCUMENT ME!
	 * @param units TODO: DOCUMENT ME!
	 */
	public UnitListNodeWrapper(String text, String clipboardValue,
							   Collection units) {
		this.text		    = text;
		this.units		    = units;
		this.clipboardValue = clipboardValue;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getType() {
		return type;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getUnits() {
		return units;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return text;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(clipboardValue == null) {
			return toString();
		} else {
			return clipboardValue;
		}
	}
}
