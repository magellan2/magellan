/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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
 * A comparator imposing an ordering on Unit objects by comparing their health status
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality.
 * </p>
 *
 * @author Ulrich Küster
 */
public class UnitHealthComparator implements Comparator {
	protected Comparator subCmp = null;

	/**
	 * Creates a new UnitHealthComparator object.
	 *
	 * @param subComparator if two units have the same health-status, this sub-comparator is
	 * 		  applied if it is not <tt>null</tt>.
	 */
	public UnitHealthComparator(Comparator subComparator) {
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
		int retVal = 0;
		Unit u1 = (Unit) o1;
		Unit u2 = (Unit) o2;
		String health1 = u1.health;
		String health2 = u2.health;

		if((health1 == null) && (health2 != null)) {
			retVal = Integer.MIN_VALUE;
		} else if((health2 == null) && (health1 != null)) {
			retVal = Integer.MAX_VALUE;
		} else if((health1 == null) && (health2 == null)) {
			retVal = 0;
		} else {
			// the alphabetical sorting is not very pretty
			// this is a try to create a better order
			// (healthy, tired, wounded, heavily wounded)
			if(health1.equalsIgnoreCase("schwer verwundet")) {
				health1 = "z" + health1;
			}

			if(health2.equalsIgnoreCase("schwer verwundet")) {
				health2 = "z" + health2;
			}

			retVal = health1.compareToIgnoreCase(health2);
		}

		return ((retVal == 0) && (subCmp != null)) ? subCmp.compare(o1, o2) : retVal;
	}

}
