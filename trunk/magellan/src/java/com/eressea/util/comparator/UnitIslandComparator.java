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

import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Island objects
 */
public class UnitIslandComparator implements Comparator {
	protected Comparator subCmp = null;

	/**
	 * Creates a new UnitIslandComparator object.
	 *
	 * @param subComparator if two units belonging to the same faction are
	 * 		  compared, this sub-comparator is applied if it is
	 * 		  not<tt>null</tt>.
	 */
	public UnitIslandComparator(Comparator subComparator) {
		subCmp = subComparator;
	}

	/**
	 * Compares its two arguments for order according to the factions they
	 * belong to.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return the result of the faction comparator's comparison of
	 * 		   <tt>o1</tt>'s and <tt>o2</tt>. If both belong to the same
	 * 		   faction and a sub-comparator was specified, the result that
	 * 		   sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		int ret = ((Unit) o1).compareTo(o2);

		// if equality found, ask sub comparator
		return ((ret == 0) && (subCmp != null)) ? subCmp.compare(o1, o2) : ret;
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
