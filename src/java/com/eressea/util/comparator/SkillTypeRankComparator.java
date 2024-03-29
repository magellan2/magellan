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
import java.util.Hashtable;
import java.util.Properties;

import com.eressea.Skill;
import com.eressea.rules.SkillType;

/**
 * A comparator imposing an ordering on SkillType or Skill objects by comparing their user
 * modifiable ranking. (In case of skill objects the skill's type is compared).
 * 
 * <p>
 * Note: this comparator can impose orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality. I.e. if the two compared objects have the
 * same rank and they would be regarded as equal by this comparator, instead of 0 the result of
 * the sub-comparator's comparison is returned.
 * </p>
 *
 * @author Ulrich K�ster
 */
public class SkillTypeRankComparator implements Comparator {
	private final Comparator subCmp;
	private final Properties settings;

	// avoid unnecessary object creation
	private SkillType s1;

	// avoid unnecessary object creation
	private SkillType s2;

	/**
	 * To reduces memory consumption the ranks of the various skills are cached. Keys are
	 * SkillTypeIDs, values are Integers.
	 */
	private Hashtable skillRanks = new Hashtable();

	/**
	 * Creates a new SkillTypeRankComparator object.
	 *
	 * @param subComparator TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public SkillTypeRankComparator(Comparator subComparator, Properties settings) {
		this.subCmp = subComparator;

		if(settings == null) {
			this.settings = new Properties();
		} else {
			this.settings = settings;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compare(Object o1, Object o2) {
		if(o1 instanceof Skill) {
			s1 = ((Skill) o1).getSkillType();
		} else {
			s1 = (SkillType) o1;
		}

		if(o2 instanceof Skill) {
			s2 = ((Skill) o2).getSkillType();
		} else {
			s2 = (SkillType) o2;
		}

		int retVal = getValue(s1) - getValue(s2);

		if((retVal == 0) && (subCmp != null)) {
			retVal = subCmp.compare(s1, s2);
		}

		return retVal;
	}

	private int getValue(SkillType s) {
		Integer retVal = (Integer) skillRanks.get(s.getID());

		if(retVal == null) {
			String prop = settings.getProperty("ClientPreferences.compareValue." + s.getID(), "-1");
			retVal = new Integer(Integer.parseInt(prop));
			skillRanks.put(s.getID(), retVal);
		}

		return retVal.intValue();
	}
}
