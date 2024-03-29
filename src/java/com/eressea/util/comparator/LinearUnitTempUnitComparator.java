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

import com.eressea.TempUnit;
import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Unit and TempUnit objects by sorting all temp units behind
 * normal units.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality. I.e. if the two compared units are either
 * both normal units or both temp units, instead of 0 the result of the unit sub-comparator's
 * comparison is returned.
 * </p>
 */
public class LinearUnitTempUnitComparator implements Comparator {
	protected Comparator subCmp = null;

	/**
	 * Creates a new LinearUnitTempUnitComparator object.
	 *
	 * @param unitSubComparator if two units are of the same type (standard or temp unit), this
	 * 		  sub-comparator is applied if it is not <tt>null</tt>.
	 */
	public LinearUnitTempUnitComparator(Comparator unitSubComparator) {
		this.subCmp = unitSubComparator;
	}

	/**
	 * Compares its two arguments for order according to their types.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return a value less than zero if o1 is.an instance of class <tt>Unit</tt> and o2 an
	 * 		   instance of class <tt>TempUnit</tt>. Conversely, if o1 is an instance of class
	 * 		   <tt>TempUnit</tt> and o2 an instance of class <tt>Unit</tt>, a value greater than
	 * 		   zero is returned. If o1 and o2 are instances of the same class, the comparison
	 * 		   either returns zero or, when unitSubComparator is not null, that comparator's
	 * 		   result.
	 */
	public int compare(Object o1, Object o2) {
		Unit u1 = (Unit) o1;
		Unit u2 = (Unit) o2;

		if(u1 instanceof TempUnit && u2 instanceof TempUnit) {
			return (this.subCmp != null) ? this.subCmp.compare(o1, o2) : 0;
		} else {
			if(u1 instanceof TempUnit) {
				return 1;
			} else if(u2 instanceof TempUnit) {
				return -1;
			} else {
				return (this.subCmp != null) ? this.subCmp.compare(o1, o2) : 0;
			}
		}
	}
}
