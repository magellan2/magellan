// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;


import java.util.Comparator;

import com.eressea.Group;
import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Unit objects by comparing
 * the groups they belong to.
 * <p>Note: this comparator imposes orderings that are inconsistent with
 * equals.</p>
 * <p>In order to overcome the inconsistency with equals this comparator
 * allows the introduction of a sub-comparator which is applied in cases
 * of equality. I.e. if the two compared units belong to the same group
 * or they have no group set and they would be regarded as equal by
 * this comparator, instead of 0 the result of the sub-comparator's
 * comparison is returned.</p>
 */
public class UnitGroupComparator implements Comparator {
	protected Comparator groupCmp = null;
	protected Comparator sameGroupSubCmp = null;
	protected Comparator noGroupSubCmp = null;

	/**
	 * Creates a new UnitGroupComparator object.
	 * @param groupComparator the comparator used to compare the
	 * units' groups.
	 * @param sameGroupSubComparator if two units belonging to the
	 * same group are compared, this sub-comparator is applied if
	 * it is not <tt>null</tt>.
	 * @param noGroupSubComparator if two units belonging to no group
	 * are compared, this sub-comparator is applied if it is not
	 * <tt>null</tt>.
	 */
	public UnitGroupComparator (Comparator groupComparator, Comparator sameGroupSubComparator, Comparator noGroupSubComparator) {
		groupCmp = groupComparator;
		sameGroupSubCmp = sameGroupSubComparator;
		noGroupSubCmp = noGroupSubComparator;
	}

	/**
	 * Compares its two arguments for order according to the groups
	 * they belong to.
	 * @returns the difference of <tt>o1</tt>'s and <tt>o2</tt>'s
	 *	group ids. If both belong to the same group and a
	 *	sub-comparator was specified, the result that sub-comparator's
	 *	comparison is returned. If both units do not belong to any
	 *  group and a no-group sub-comparator was specified, the result
	 *  of that sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		Group g1 = ((Unit)o1).getGroup();
		Group g2 = ((Unit)o2).getGroup();
		if (g1 == null && g2 == null) {
			if (noGroupSubCmp != null) {
				retVal = noGroupSubCmp.compare(o1, o2);
			} else {
				retVal = 0;
			}
		} else if (g1 != null && g2 == null) {
			retVal = Integer.MIN_VALUE;
		} else if (g1 == null && g2 != null) {
			retVal = Integer.MAX_VALUE;
		} else if (g1 != null && g1 != null) {
			retVal = groupCmp.compare(g1, g2);
			if (retVal == 0 && sameGroupSubCmp != null) {
				retVal = sameGroupSubCmp.compare(o1, o2);
			}
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