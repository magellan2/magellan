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
public class GroupingComparator implements Comparator {

	protected Comparator main = null;
	protected Comparator sub = null;

	/**
	 * Creates a new <tt>GroupingComparator</tt> object.
	 * @param mainComparator the comparator used to compare the given objects
	 * @param subComparator the comparator used to compare the given objects if mainComparator delivers 0.
	 */
	public GroupingComparator(Comparator mainComparator, Comparator subComparator) {
		if(main == null) throw new NullPointerException();
		main = mainComparator;
		sub  = subComparator;
	}

	/**
	 * Compares its two arguments. Also it returns powers of 2 to return the depth of
	 * the underlying comparators
	 */
	public int compare(Object o1, Object o2) {
		int ret = main.compare(o1,o2);
		return 2* (ret != 0 || sub == null ? ret : sub.compare(o1,o2));
	}

	
	/** 
	 * returns the "depth" of a compare result. 
	 * This is nothing else but the logarithm of compareResult with base 2.
	 * I remember deeply that logb(x) == ln(x)/ln(b)
	 */
	public int depth(int compareResult) {
		return compareResult == 0
			? 0 
			: (int) (Math.log(Math.abs(compareResult))/Math.log(2));
	}

}
