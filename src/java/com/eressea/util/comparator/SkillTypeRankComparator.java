// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;

import java.util.Comparator;
import java.util.Properties;

import com.eressea.Skill;
import com.eressea.rules.SkillType;

/**
 * A comparator imposing an ordering on SkillType objects by comparing
 * their user modifiable ranking.
 * <p>Note: this comparator can impose orderings that are inconsistent with
 * equals.</p>
 * <p>In order to overcome the inconsistency with equals this comparator
 * allows the introduction of a sub-comparator which is applied in cases
 * of equality. I.e. if the two compared objects have the same rank
 * and they would be regarded as equal by this comparator, instead of 0
 * the result of the sub-comparator's comparison is returned.</p>
 * @author Ulrich Küster
 */
public class SkillTypeRankComparator implements Comparator {
	private final Comparator subCmp;
	private final Properties settings;

	public SkillTypeRankComparator(Comparator subComparator, Properties settings) {
		this.subCmp = subComparator;
		if (settings == null) {
			this.settings = new Properties();
		} else {
			this.settings = settings;
		}
	}

	public int compare(Object o1, Object o2) {
		if (o1 instanceof Skill) {
			o1 = ((Skill)o1).getSkillType();
		}
		if (o2 instanceof Skill) {
			o2 = ((Skill)o2).getSkillType();
		}
		SkillType s1 = (SkillType)o1;
		SkillType s2 = (SkillType)o2;

		int retVal = getProperty(s1) - getProperty(s2);
		if (retVal == 0 && subCmp != null) {
			retVal = subCmp.compare(s1, s2);
		}
		return retVal;
	}

	private int getProperty(SkillType s) {
		// FIXME(pavkovic): fallback should only be -1 !
		String fallback = settings.getProperty(s.getID()+ ".compareValue", "-1");
		String prop = settings.getProperty("ClientPreferences.compareValue."+s.getID(),fallback);
		return Integer.parseInt(prop);
	}
}
