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
 * @author Ulrich K�ster
 */
public class UnitTrustComparator implements Comparator {
	protected Comparator subCmp = null;

	/**
	 * Creates a new UnitTrustComparator object.
	 *
	 * @param subComparator if two units have the same health-status, this sub-comparator is
	 * 		  applied if it is not <tt>null</tt>.
	 */
	public UnitTrustComparator(Comparator subComparator) {
		subCmp = subComparator;
	}

	/** TODO: DOCUMENT ME! */
	public static final UnitTrustComparator DEFAULT_COMPARATOR = new UnitTrustComparator(null);

	/**
	 * Compares its two arguments for order according to the health-status
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compare(Object o1, Object o2) {
		int ret = FactionTrustComparator.DEFAULT_COMPARATOR.compare(((Unit) o1).getFaction(),
																	((Unit) o2).getFaction());

		// if equality found, ask sub comparator
		return ((ret == 0) && (subCmp != null)) ? subCmp.compare(o1, o2) : ret;
	}
}
