/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 * $Id$
 */

package com.eressea.util.comparator;

import java.util.Comparator;

import com.eressea.Faction;
import com.eressea.Unit;

/**
 * A comparator imposing an ordering on Unit objects by comparing the factions
 * they belong to.
 * 
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 * 
 * <p>
 * In order to overcome the inconsistency with equals this comparator allows
 * the introduction of a sub-comparator which is applied in cases of equality.
 * I.e. if the two compared units belong to the same faction and they would be
 * regarded as equal by this comparator, instead of 0 the result of the
 * sub-comparator's comparison is returned.
 * </p>
 */
public class UnitFactionComparator implements Comparator {
	protected Comparator factionCmp		   = null;
	protected Comparator sameFactionSubCmp = null;

	/**
	 * Creates a new UnitFactionComparator object.
	 *
	 * @param factionComparator the comparator used to compare the units'
	 * 		  factions.
	 * @param sameFactionSubComparator if two units belonging to the same
	 * 		  faction are compared, this sub-comparator is applied if it is
	 * 		  not <tt>null</tt>.
	 */
	public UnitFactionComparator(Comparator factionComparator,
								 Comparator sameFactionSubComparator) {
		factionCmp		  = factionComparator;
		sameFactionSubCmp = sameFactionSubComparator;
	}

	/**
	 * Compares its two arguments for order according to the factions they
	 * belong to.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return the result of the faction comparator's comparison of
	 * 		   <tt>o1</tt>'s and <tt>o2</tt>. If both belong to the same
	 * 		   faction and a sub-comparator was specified, the result that
	 * 		   sub-comparator's comparison is returned.
	 */
	public int compare(Object o1, Object o2) {
		int     retVal = 0;

		Faction f1 = ((Unit) o1).getFaction();
		Faction f2 = ((Unit) o2).getFaction();

		if(f1 == null) {
			if(f2 == null) {
				retVal = 0;
			} else {
				retVal = Integer.MAX_VALUE;
			}
		} else {
			if(f2 == null) {
				retVal = Integer.MIN_VALUE;
			} else {
				retVal = factionCmp.compare(f1, f2);

				if((retVal == 0) && (sameFactionSubCmp != null)) {
					retVal = sameFactionSubCmp.compare(o1, o2);
				}
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
