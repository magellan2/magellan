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

import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Unit objects by comparing whether they
 * are faction disguised or not.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows
 * the introduction of a sub-comparator which is applied in cases of equality.
 * </p>
 *
 * @author Ulrich Küster
 */
public class UnitFactionDisguisedComparator implements Comparator {
	protected Comparator subCmp = null;

	/**
	 * Creates a new UnitFactionDisguisedComparator object.
	 *
	 * @param subComparator if two units have the same
	 * 		  faction-disguised-status, this sub-comparator is applied if it
	 * 		  is not <tt>null</tt>.
	 */
	public UnitFactionDisguisedComparator(Comparator subComparator) {
		subCmp = subComparator;
	}

	/**
	 * Compares its two arguments for order according to the health-status
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compare(Object o1, Object o2) {
		int  retVal = 0;
		Unit u1 = (Unit) o1;
		Unit u2 = (Unit) o2;

		if(u1.hideFaction == u2.hideFaction) {
			retVal = 0;
		} else if(u1.hideFaction) {
			retVal = 1;
		} else {
			retVal = -1;
		}

		if((retVal == 0) && (subCmp != null)) {
			retVal = subCmp.compare(o1, o2);
		}

		return retVal;
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
