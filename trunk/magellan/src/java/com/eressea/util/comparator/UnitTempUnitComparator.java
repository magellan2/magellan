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
 * $Id$
 */

package com.eressea.util.comparator;

import java.util.Comparator;

import com.eressea.TempUnit;
import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Unit and TempUnit objects by sorting
 * them according to a parent-child relation.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows
 * the introduction of a sub-comparator which is applied in cases of equality.
 * I.e. if the two compared units do not have a parent- child relation,
 * instead of 0 the result of either the unit sub-comparator's or the
 * same-parent sub-comparator's comparison is returned.
 * </p>
 */
public class UnitTempUnitComparator implements Comparator {
	protected Comparator sameParentSubCmp = null;
	protected Comparator unitSubCmp = null;

	/**
	 * Creates a new UnitTempUnitComparator object.
	 *
	 * @param sameParentSubComparator if two units with the same parent unit
	 * 		  are compared, this sub-comparator is applied if it is not
	 * 		  <tt>null</tt>.
	 * @param unitSubComparator if two units do not have the same parent unit
	 * 		  and do not have a parent-child relation, this sub- comparator is
	 * 		  applied if it is not <tt>null</tt>.
	 */
	public UnitTempUnitComparator(Comparator sameParentSubComparator,
								  Comparator unitSubComparator) {
		this.sameParentSubCmp = sameParentSubComparator;
		this.unitSubCmp		  = unitSubComparator;
	}

	/**
	 * Compares its two arguments for order according to their names.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return a value less than zero if o1 is.an instance of class
	 * 		   <tt>Unit</tt> and o2 an instance of class <tt>TempUnit</tt> and
	 * 		   o1 is o2's parent. Conversely, if o1 is an instance of class
	 * 		   <tt>TempUnit</tt> and o2 an instance of class <tt>Unit</tt> and
	 * 		   o2 is the parent of o1, a value greater than zero is returned.
	 * 		   When o1 and o2 have the same parent the result of the
	 * 		   sameparentsubcomparator is applied if it is not null, else 0 is
	 * 		   returned. If not both, o1 and o2, are instances of class
	 * 		   TempUnit or they are but have different parents they or their
	 * 		   parents respectively are compared with the unitsubcomparator if
	 * 		   it is not null, else 0 is returned.
	 */
	public int compare(Object o1, Object o2) {
		Unit u1 = (Unit) o1;
		Unit u2 = (Unit) o2;

		if(u1 instanceof TempUnit && u2 instanceof TempUnit) {
			if(((TempUnit) u1).getParent().equals(((TempUnit) u2).getParent())) {
				if(sameParentSubCmp != null) {
					return sameParentSubCmp.compare(o1, o2);
				} else {
					return 0;
				}
			} else {
				if(unitSubCmp != null) {
					return unitSubCmp.compare(((TempUnit) u1).getParent(),
											  ((TempUnit) u2).getParent());
				} else {
					return 0;
				}
			}
		}

		if(u1 instanceof TempUnit) {
			if(((TempUnit) u1).getParent().equals(u2)) {
				return 1;
			} else {
				if(unitSubCmp != null) {
					return unitSubCmp.compare(((TempUnit) o1).getParent(), o2);
				}
			}
		} else if(u2 instanceof TempUnit) {
			if(((TempUnit) u2).getParent().equals(u1)) {
				return -1;
			} else {
				if(unitSubCmp != null) {
					return unitSubCmp.compare(o1, ((TempUnit) o2).getParent());
				}
			}
		} else {
			if(unitSubCmp != null) {
				return unitSubCmp.compare(o1, o2);
			}
		}

		return 0;
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
