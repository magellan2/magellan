// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;

import java.util.Comparator;

import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Unit objects by comparing
 * their combat status
 * <p>Note: this comparator imposes orderings that are inconsistent with
 * equals.</p>
 * <p>In order to overcome the inconsistency with equals this comparator
 * allows the introduction of a sub-comparator which is applied in cases
 * of equality.</p>
 * @author Ulrich Küster
 */
public class UnitCombatStatusComparator implements Comparator {
	protected Comparator subCmp = null;

	/**
	 * Creates a new UnitCombatStautsComparator object.
	 * @param subComparator if two units have the same
	 * combat-status, this sub-comparator is applied if
	 * it is not <tt>null</tt>.
	 */
	public UnitCombatStatusComparator(Comparator subComparator) {
		subCmp = subComparator;
	}

	/**
	 * Compares its two arguments for order according to the combat-status
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		Unit u1 = (Unit)o1;
		Unit u2 = (Unit)o2;
		retVal = u1.combatStatus - u2.combatStatus;
		if (retVal == 0 && subCmp != null) {
			retVal = subCmp.compare(o1, o2);
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