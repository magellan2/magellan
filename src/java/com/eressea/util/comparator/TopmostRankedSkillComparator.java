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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.Skill;
import com.eressea.util.CollectionFactory;

/**
 * A comparator imposing an ordering on collections of Skill objects by comparing the highest
 * ranked skill available in each set with a SkillComparator. In case of equality the second
 * highest ranked skills are compared and so on and so on. In case of total equality (e.g. if
 * there is only one skill oject in both maps and the skilltype and value is the same) the
 * sub-comparator is used for comparison. Note: Skilltype rankings can be defined in the
 * preferences and are available through SkillTypeRankComparator.
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
public class TopmostRankedSkillComparator implements Comparator {
	private Comparator rankCmp;
	private Comparator subCmp;

	/**
	 * Creates a new TopmostRankedSkillComparator object.
	 *
	 * @param subComparator TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public TopmostRankedSkillComparator(Comparator subComparator, Properties settings) {
		rankCmp = new SkillTypeRankComparator(null, settings);
		this.subCmp = subComparator;
	}

	/**
	 * Compares its two arguments for order according to their skills.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compare(Object o1, Object o2) {
		int retVal = 0;
		int rank = 0;
		Map map1 = (Map) o1;
		Map map2 = (Map) o2;

		// sort maps according to skill type ranking
		List list1 = CollectionFactory.createLinkedList(map1.values());
		List list2 = CollectionFactory.createLinkedList(map2.values());
		Collections.sort(list1, rankCmp);
		Collections.sort(list2, rankCmp);

		while(true) {
			Skill s1 = null;

			if(list1.size() > rank) {
				s1 = (Skill) list1.get(rank);
			}

			Skill s2 = null;

			if(list2.size() > rank) {
				s2 = (Skill) list2.get(rank);
			}

			if((s1 == null) && (s2 != null)) {
				return Integer.MAX_VALUE;
			}

			if((s1 != null) && (s2 == null)) {
				return Integer.MIN_VALUE;
			}

			if((s1 == null) && (s2 == null)) {
				if(subCmp != null) {
					return subCmp.compare(o1, o2);
				} else {
					return 0;
				}
			}

			retVal = rankCmp.compare(s1.getSkillType(), s2.getSkillType());

			if(retVal != 0) {
				return retVal;
			} else {
				retVal = SkillComparator.skillCmp.compare(s1, s2);

				if(retVal != 0) {
					return retVal;
				} else {
					// test if there are more skills available in both sets
					if((map1.size() > (rank + 1)) || (map2.size() > (rank + 1))) {
						rank++;
					} else {
						if(subCmp != null) {
							return subCmp.compare(s1, s2);
						} else {
							return 0;
						}
					}
				}
			}
		}
	}

}
