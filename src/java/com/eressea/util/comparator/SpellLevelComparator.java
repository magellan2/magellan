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

import com.eressea.Spell;

/**
 * A comparator imposing an ordering on Spell objects by comparing their levels.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows the introduction of a
 * sub-comparator which is applied in cases of equality. I.e. if the two compared spells have the
 * same level and they would be regarded as equal by this comparator, instead of 0 the result of
 * the sub-comparator's comparison is returned.
 * </p>
 */
public class SpellLevelComparator implements Comparator {
	private Comparator sameLevelSubCmp = null;

	/**
	 * Creates a new SpellLevelComparator object.
	 *
	 * @param sameLevelSubComparator if two spells with the same level are compared, this
	 * 		  sub-comparator is applied if it is not <tt>null</tt>.
	 */
	public SpellLevelComparator(Comparator sameLevelSubComparator) {
		sameLevelSubCmp = sameLevelSubComparator;
	}

	/**
	 * Compares its two arguments for order according to their levels
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return the difference of <tt>o1</tt>'s and <tt>o2</tt>'s numerical level value. If they are
	 * 		   equal and a sub-comparator was specified, the result that sub-comparator's
	 * 		   comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		int l1 = ((Spell) o1).getLevel();
		int l2 = ((Spell) o2).getLevel();

		if((l1 == l2) && (sameLevelSubCmp != null)) {
			return sameLevelSubCmp.compare(o1, o2);
		}

		return l1 - l2;
	}

}
