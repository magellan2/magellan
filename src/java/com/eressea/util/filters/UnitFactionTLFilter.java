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

package com.eressea.util.filters;

import com.eressea.Faction;
import com.eressea.Unit;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class UnitFactionTLFilter extends UnitFilter {
	protected int minTL;
	protected int maxTL;

	/**
	 * Creates a new UnitFactionTLFilter object.
	 *
	 * @param minTL TODO: DOCUMENT ME!
	 * @param maxTL TODO: DOCUMENT ME!
	 */
	public UnitFactionTLFilter(int minTL, int maxTL) {
		this.minTL = minTL;
		this.maxTL = maxTL;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean acceptUnit(Unit u) {
		Faction f = u.getFaction();

		return (f != null) && (minTL <= f.trustLevel) &&
			   (f.trustLevel <= maxTL);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMinTL() {
		return minTL;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMaxTL() {
		return maxTL;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param minTL TODO: DOCUMENT ME!
	 */
	public void setMinTL(int minTL) {
		this.minTL = minTL;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param maxTL TODO: DOCUMENT ME!
	 */
	public void setMaxTL(int maxTL) {
		this.maxTL = maxTL;
	}
}
