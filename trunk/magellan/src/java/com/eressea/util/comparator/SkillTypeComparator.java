// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;

import java.util.Comparator;

/**
 * A comparator imposing an ordering on Skill objects by comparing
 * their types.
 * <p>Note: this comparator can impose orderings that are inconsistent with
 * equals.</p>
 * <p>In order to overcome the inconsistency with equals this comparator
 * allows the introduction of a sub-comparator which is applied in cases
 * of equality. I.e. if the two compared objects have the same type
 * and they would be regarded as equal by this comparator, instead of 0
 * the result of the sub-comparator's comparison is returned.</p>
 * @author Ulrich Küster
 */
public class SkillTypeComparator implements Comparator {
	private final Comparator typeCmp;
	private final Comparator subCmp;

	/**
	 * Creates a new SkillTypeComparator object.
	 * @param typeComparator used to compare the types of skills.
	 * @param subComparator if the typeComparator's comparison of the
	 * skill types yields 0, this sub-comparator is applied to the
	 * skill objects if it is not <tt>null</tt>.
	 */
	public SkillTypeComparator(Comparator typeComparator, Comparator subComparator) {
		this.typeCmp = typeComparator;
		this.subCmp = subComparator;
	}

	/**
	 * Compares its two arguments for order according to their types.
	 *
	 * @param o1 an instance of class Skill.
	 * @param o2 an instance of class Skill.
	 * @returns the result of the type comparator's comparison of the
	 * skill object types. If this result is 0 and a subcomparator is
	 * specified that subcomparator is applied on the skill objects.
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;

		com.eressea.rules.SkillType s1 = ((com.eressea.Skill)o1).getType();
		com.eressea.rules.SkillType s2 = ((com.eressea.Skill)o2).getType();

		retVal = typeCmp.compare(s1, s2);
		if (retVal == 0 && subCmp != null) {
			retVal = subCmp.compare(o1, o2);
		}
		return retVal;
	}
}