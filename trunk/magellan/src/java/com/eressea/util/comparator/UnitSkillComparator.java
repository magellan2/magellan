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
import java.util.Map;

import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Unit objects by comparing their skills.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality.
 * </p>
 */
public class UnitSkillComparator implements Comparator {
	private final Comparator skillsCmp;
	private final Comparator subCmp;

	/**
	 * Creates a new UnitSkillComparator object.
	 *
	 * @param skillsComparator used to compare the skills of two units
	 * @param subComparator if two units do not possess skills or if the skills comparator regards
	 * 		  them as equal, this sub-comparator is applied if it is not <tt>null</tt>.
	 */
	public UnitSkillComparator(Comparator skillsComparator, Comparator subComparator) {
		this.skillsCmp = skillsComparator;
		this.subCmp = subComparator;
	}

	/**
	 * Compares its two arguments for order according to their skills. The learning days of the
	 * best skill of unit one is compared to those of the second unit.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return a number &lt; 0 if o1's best skill is alphabetically less than o2's best skill. If
	 * 		   both units have the same best skill these are compared using the standard skill
	 * 		   comparator. If these two values are the same, the subcomparator is used to compare
	 * 		   the two units.
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		Map s1 = ((Unit) o1).skills;
		Map s2 = ((Unit) o2).skills;

		if((s1 == null) && (s2 != null)) {
			retVal = Integer.MAX_VALUE;
		} else if((s1 != null) && (s2 == null)) {
			retVal = Integer.MIN_VALUE;
		} else if((s1 == null) && (s2 == null)) {
			retVal = subCmp.compare(o1, o2);
		} else {
			retVal = skillsCmp.compare(s1, s2);

			if((retVal == 0) && (subCmp != null)) {
				retVal = subCmp.compare(o1, o2);
			}
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
