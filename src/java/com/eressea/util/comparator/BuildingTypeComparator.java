// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;


import java.util.Comparator;

import com.eressea.Building;
import com.eressea.rules.BuildingType;

/**
 * A comparator imposing an ordering on Building objects by comparing
 * their types.
 * <p>Note: this comparator imposes orderings that are inconsistent with
 * equals.</p>
 * <p>In order to overcome the inconsistency with equals this comparator
 * allows the introduction of a sub-comparator which is applied in cases
 * of equality. I.e. if the two compared buildings have the same type
 * and they would be regarded as equal by this comparator, instead of 0
 * the result of the sub-comparator's comparison is returned.</p>
 */
public class BuildingTypeComparator implements Comparator {
	protected Comparator sameTypeSubCmp = null;

	/**
	 * Creates a new BuildingTypeComparator object.
	 * @param sameTypeSubComparator if two buildings having the
	 * same type are compared, this sub-comparator is applied if
	 * it is not <tt>null</tt>.
	 */
	public BuildingTypeComparator (Comparator sameTypeSubComparator) {
		sameTypeSubCmp = sameTypeSubComparator;
	}

	/**
	 * Compares its two arguments for order according to their types.
	 *
	 * @returns the natural ordering of <tt>o1</tt>'s and <tt>o2</tt>'s
	 * types as returned by BuildingType.compareTo(). If the types are
	 * equal and a sub-comparator was specified, the result of that
	 * sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		BuildingType t1 = (BuildingType)((Building)o1).getType();
		BuildingType t2 = (BuildingType)((Building)o2).getType();
		int retVal = t1.compareTo(t2);
		if (retVal == 0 && sameTypeSubCmp != null) {
			retVal = sameTypeSubCmp.compare(o1, o2);
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