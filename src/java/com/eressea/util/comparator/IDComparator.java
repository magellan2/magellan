// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;


import java.util.Comparator;

import com.eressea.Unique;

/**
 * A comparator imposing a total ordering on identifiable objects by
 * comparing their ids.
 */
public class IDComparator implements Comparator {

	/**
	 * Creates a new IDComparator object.
	 */
	public IDComparator () {
	}

	/**
	 * Compares its two arguments for order according to their ids.
	 * @returns the natural ordering of <tt>o1</tt>'s id and
	 * <tt>o2</tt>'s id.
	 */
	public int compare(Object o1, Object o2) {
		return ((Unique)o1).getID().compareTo(((Unique)o2).getID());
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 * @returns <tt>false</tt>
	 */
	public boolean equals(Object o) {
		return false;
	}
}
