// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;

import java.util.Collection;

/**
 * @author Ulrich Küster
 * A simple nodewrapper wrapping a list of units allowing
 * acces to them via getUnits().
 */
public class UnitListNodeWrapper implements SupportsClipboard {
	// identifies that this UnitListNodeWrapper contains a list of units that are
	// some other unit's students
	public static final int STUDENT_LIST = 1;
	private int type = 0;
	protected Collection units = null;
	protected String text = null;
	protected String clipboardValue = null;
	/** Creates new UnitListNodeWrapper */
	public UnitListNodeWrapper(String text, String clipboardValue, Collection units, int type) {
		this(text, clipboardValue, units);
		this.type = type;
	}
	public UnitListNodeWrapper(String text, String clipboardValue, Collection units) {
		this.text = text;
		this.units = units;
		this.clipboardValue = clipboardValue;
	}

	public int getType() {
		return type;
	}

	public Collection getUnits() {
		return units;
	}

	public String toString() {
		return text;
	}

	public String getClipboardValue() {
		if (clipboardValue == null) {
			return toString();
		} else {
			return clipboardValue;
		}
	}
}

