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

import com.eressea.Named;

/**
 * A comparator imposing an ordering on named objects by comparing their names
 * alphabetically.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows
 * the introduction of a sub-comparator which is applied in cases of equality.
 * I.e. if the two compared objects have the same name and they would be
 * regarded as equal by this comparator, instead of 0 the result of the
 * sub-comparator's comparison is returned.
 * </p>
 */
public class NameComparator implements Comparator {
	protected Comparator sameNameSubCmp = null;


	/** The default NameComparator without a subComparator */
	public static final Comparator DEFAULT = new NameComparator(null);

	/**
	 * Creates a new NameComparator object.
	 *
	 * @param sameNameSubComparator if two objects with the same name are
	 * 		  compared, this sub-comparator is applied if it is not
	 * 		  <tt>null</tt>.
	 */
	public NameComparator(Comparator sameNameSubComparator) {
		sameNameSubCmp = sameNameSubComparator;
	}

	/**
	 * Compares its two arguments for order according to their names.
	 *
	 * @param o1 an instance of interface Named.
	 * @param o2 an instance of interface Named.
	 *
	 * @return the lexical difference of <tt>o1</tt>'s and <tt>o2</tt>'s names
	 * 		   as returned by String.compareTo(). If the names are equal and a
	 * 		   sub-comparator was specified, the result of that
	 * 		   sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		int    retVal = 0;
		String n1 = ((Named) o1).getName();
		String n2 = ((Named) o2).getName();

		if((n1 == null) && (n2 == null)) {
			retVal = 0;
		} else if((n1 != null) && (n2 == null)) {
			retVal = -1;
		} else if((n1 == null) && (n2 != null)) {
			retVal = 1;
		} else if((n1 != null) && (n1 != null)) {
			retVal = n1.compareToIgnoreCase(n2);
		}

		if((retVal == 0) && (sameNameSubCmp != null)) {
			retVal = sameNameSubCmp.compare(o1, o2);
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
