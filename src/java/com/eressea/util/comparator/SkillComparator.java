// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Ulrich K�ster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;


import java.util.Comparator;

import com.eressea.Skill;


/**
 * A comparator imposing an ordering on Skill objects by comparing
 * the skill level and days.
 * <p>Note: this comparator imposes orderings that are inconsistent with
 * equals.</p>
 */
public class SkillComparator implements Comparator {

	/**
	 * A default instance
	 */
	public final static SkillComparator skillCmp = new SkillComparator();

	/**
	 * Creates a new SkillComparator object.
	 */
	public SkillComparator() {
	}

	/**
	 * Compares its two arguments for order according to the skill
	 * levels and days.
	 * @returns a number &lt; 0 if o1's level is larger than the one of
	 * o2 or if both levels are equal and o1's days is larger than
	 * o2's.
	 * A number &gt; 0 is returned if o2's level is larger than o1's or
	 * if both levels are equal and o2's days is larger than o1's.
	 * 0 is returned if both values are equal.
	 */
	public int compare(Object o1, Object o2) {
		Skill s1 = (Skill)o1;
		Skill s2 = (Skill)o2;
		if (s1.getLevel() != s2.getLevel() || (s1.noSkillPoints() && s2.noSkillPoints())) {
			return s2.getLevel() - s1.getLevel();
		} else {
			return s2.getPointsPerPerson() - s1.getPointsPerPerson();
		}
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 * @returns <tt>false</tt>
	 */
	public boolean equals(Object o1) {
		return false;
	}
}
