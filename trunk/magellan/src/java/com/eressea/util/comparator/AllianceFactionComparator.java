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

import com.eressea.Alliance;

/**
 * A comparator imposing an ordering on <tt>Alliance</tt> objects by comparing the factions they
 * contain.
 */
public class AllianceFactionComparator implements Comparator {
	protected Comparator factionSubCmp = null;

	/**
	 * Creates a new <tt>AllianceFactionComparator</tt> object.
	 *
	 * @param factionSubComparator is used to compare the factions of two alliance objects.
	 */
	public AllianceFactionComparator(Comparator factionSubComparator) {
		factionSubCmp = factionSubComparator;
	}

	/**
	 * Compares its two arguments for order with regard to their trust levels.
	 *
	 * @param o1 TODO: DOCUMENT ME!
	 * @param o2 TODO: DOCUMENT ME!
	 *
	 * @return the result of the faction comparator applied to the factions of the alliances o1 and
	 * 		   o2.
	 */
	public int compare(Object o1, Object o2) {
		Alliance a1 = (Alliance) o1;
		Alliance a2 = (Alliance) o2;

		if(a1 == null) {
			return (a2 == null) ? 0 : 1;
		} else {
			return (a2 == null) ? (-1) : factionSubCmp.compare(a1.getFaction(), a2.getFaction());
		}
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
