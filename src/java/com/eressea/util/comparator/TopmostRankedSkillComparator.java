// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util.comparator;

import java.util.*;

import com.eressea.Skill;
import com.eressea.util.CollectionFactory;

/**
 * A comparator imposing an ordering on collections of Skill objects
 * by comparing the highest ranked skill available in each set with
 * a SkillComparator.
 * In case of equality the second highest ranked skills are compared and
 * so on and so on. In case of total equality (e.g. if there is only
 * one skill oject in both maps and the skilltype and value is the same)
 * the sub-comparator is used for comparison.
 * Note: Skilltype rankings can be defined in the preferences and are
 * available through SkillTypeRankComparator.
 * <p>Note: this comparator imposes orderings that are inconsistent with
 * equals.</p>
 * <p>In order to overcome the inconsistency with equals this comparator
 * allows the introduction of a sub-comparator which is applied in cases
 * of equality.</p>
 */
public class TopmostRankedSkillComparator implements Comparator {
	private Comparator rankCmp;
	private Comparator subCmp;

	public TopmostRankedSkillComparator (Comparator subComparator, Properties settings) {
		rankCmp = new SkillTypeRankComparator(null, settings);
		this.subCmp = subComparator;
	}

	/**
	 * Compares its two arguments for order according to their skills.
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		int rank = 0;
		Map map1 = (Map)o1;
		Map map2 = (Map)o2;
		while (true) {
			Skill s1 = getRankedSkill(map1, rank);
			Skill s2 = getRankedSkill(map2, rank);
			if (s1 == null && s2 != null) {
				return Integer.MAX_VALUE;
			}
			if (s1 != null && s2 == null) {
				return Integer.MIN_VALUE;
			}
			if (s1 == null && s2 == null) {
				if (subCmp != null) {
					return subCmp.compare(o1, o2);
				} else {
					return 0;
				}
			}
			retVal = rankCmp.compare(s1.getType(), s2.getType());
			if (retVal != 0) {
				return retVal;
			} else {
				retVal = SkillComparator.skillCmp.compare(s1, s2);
				if (retVal != 0) {
					return retVal;
				} else {
					// test if there are more skills available in both sets
					if (map1.size() > (rank + 1) || map2.size() > (rank + 1)) {
						rank++;
					} else {
						if (subCmp != null) {
							return subCmp.compare(s1, s2);
						} else {
							return 0;
						}
					}
				}
			}
		}
	}

	/**
	 * To retrieve the skill with the specified rank in the map.
	 * For example getRankedSkill(skills, 0) returns the topmost
	 * ranked skill in the map. If there are less skills than rank
	 * in the map, null is returned.
	 */
	private Skill getRankedSkill(Map skills, int rank) {
		if (skills == null || skills.size() <= rank) {
			return null;
		}
		List v = CollectionFactory.createLinkedList(skills.values());
		Collections.sort(v, rankCmp);
		return (Skill)v.get(rank);
	}

	/**
	 * Checks the Object <tt>o</tt> for equality.
	 * @returns <tt>false</tt>
	 */
	public boolean equals(Object o1) {
		return false;
	}
}
