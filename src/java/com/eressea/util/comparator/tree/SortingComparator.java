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

package com.eressea.util.comparator.tree;

import java.util.Comparator;

/**
 * This comparator glues two comparators together.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality. I.e. if the two compared units belong to
 * the same faction and they would be regarded as equal by this comparator, instead of 0 the
 * result of the sub-comparator's comparison is returned.
 * </p>
 */
public class SortingComparator implements Comparator {
	protected Comparator main;
	protected Comparator sub;

	/**
	 * Creates a new <tt>SortingComparator</tt> object.
	 * @param mainComparator the comparator used to compare the given objects
	 * @param subComparator the comparator used to compare the given objects if mainComparator delivers 0.
	 */
	public SortingComparator(Comparator mainComparator, Comparator subComparator) {
		if(main == null) throw new NullPointerException();
		main = mainComparator;
		sub = subComparator;
	}

	/**
	 * Compares its two arguments. 
	 */
	public int compare(Object o1, Object o2) {
		int ret = main.compare(o1,o2);
		return (ret != 0 || sub == null) ? ret : sub.compare(o1,o2);
	}
}
