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

package com.eressea.util.comparator;

import java.util.Comparator;

import com.eressea.Building;
import com.eressea.rules.BuildingType;

/**
 * A comparator imposing an ordering on Building objects by comparing their types.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality. I.e. if the two compared buildings have
 * the same type and they would be regarded as equal by this comparator, instead of 0 the result
 * of the sub-comparator's comparison is returned.
 * </p>
 */
public class BuildingTypeComparator implements Comparator {
	protected Comparator subCmp = null;

	/**
	 * Creates a new BuildingTypeComparator object.
	 *
	 * @param aSubCmp if two buildings having the same type are compared, this sub-comparator is
	 * 		  applied if it is not<tt>null</tt>.
	 */
	public BuildingTypeComparator(Comparator aSubCmp) {
		subCmp = aSubCmp;
	}

	/**
	 * Compares its two arguments for order according to their types.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return the natural ordering of <tt>o1</tt>'s and <tt>o2</tt>'s types as returned by
	 * 		   BuildingType.compareTo(). If the types are equal and a sub-comparator was
	 * 		   specified, the result of that sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		BuildingType t1 = (BuildingType) ((Building) o1).getType();
		BuildingType t2 = (BuildingType) ((Building) o2).getType();

		int retVal = t1.compareTo(t2);

		return ((retVal == 0) && (subCmp != null)) ? subCmp.compare(o1, o2) : retVal;
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 *
	 * @return <tt>false</tt>
	 */
	public boolean equals(Object o1) {
		return false;
	}
}
