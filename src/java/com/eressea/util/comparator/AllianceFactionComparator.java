// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Ulrich K�ster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;


import java.util.Comparator;

import com.eressea.Alliance;

/**
 * A comparator imposing an ordering on <tt>Alliance</tt> objects by
 * comparing the factions they contain.
 */
public class AllianceFactionComparator implements Comparator {
	protected Comparator factionSubCmp = null;

	/**
	 * Creates a new <tt>AllianceFactionComparator</tt> object.
	 * @param factionSubComparator is used to compare the factions
	 * of two alliance objects.
	 */
	public AllianceFactionComparator (Comparator factionSubComparator) {
		factionSubCmp = factionSubComparator;
	}

	/**
	 * Compares its two arguments for order with regard to their
	 * trust levels.
	 * @returns the result of the faction comparator applied to the
	 * factions of the alliances o1 and o2.
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		Alliance a1 = (Alliance)o1;
		Alliance a2 = (Alliance)o2;
		if (a1 != null && a2 != null) {
			retVal = factionSubCmp.compare(a1.getFaction(), a2.getFaction());
		} else if (a1 == null && a2 != null) {
			retVal = Integer.MAX_VALUE;
		} else if (a1 != null && a2 == null) {
			retVal = Integer.MIN_VALUE;
		}
		return retVal;
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 * @returns <tt>false</tt>
	 */
	public boolean equals(Object o1) {
		return false;
	}
}