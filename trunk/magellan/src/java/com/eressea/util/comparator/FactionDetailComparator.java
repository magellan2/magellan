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

import com.eressea.Faction;

/**
 * @author steffen
 *
 * TODO DOCUMENT ME!
 */
public class FactionDetailComparator implements Comparator {
	protected Comparator sameTrustSubCmp = null;

	/**
	 * Creates a new <tt>FactionTrustComparator</tt> object.
	 *
	 * @param sameFactionSubComparator if two factions with the same trust level are compared, this
	 * 		  sub-comparator is applied if it is not <tt>null</tt>.
	 */
	public FactionDetailComparator(Comparator sameFactionSubComparator) {
		sameTrustSubCmp = sameFactionSubComparator;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		Faction f1 = (Faction) o1;
		Faction f2 = (Faction) o2;
		int t1 = f1.trustLevel;
		int t2 = f2.trustLevel;
		return (t2==t1 && sameTrustSubCmp != null) ? sameTrustSubCmp.compare(o1, o2) : (t2-t1);
	}

}