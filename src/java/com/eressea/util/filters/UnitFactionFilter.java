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
public class UnitFactionFilter extends UnitFilter {
	protected String  factionS;
	protected Faction faction;

	/**
	 * Creates a new UnitFactionFilter object.
	 *
	 * @param faction TODO: DOCUMENT ME!
	 */
	public UnitFactionFilter(String faction) {
		this.factionS = faction;
	}

	/**
	 * Creates a new UnitFactionFilter object.
	 *
	 * @param faction TODO: DOCUMENT ME!
	 */
	public UnitFactionFilter(Faction faction) {
		this.faction = faction;
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

		return (f != null) &&
			   (((factionS != null) &&
			   (factionS.equals(f.getName()) ||
			   factionS.equals(f.getID().toString()))) ||
			   ((faction != null) && (faction.equals(f))));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param faction TODO: DOCUMENT ME!
	 */
	public void setFaction(String faction) {
		factionS = faction;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param faction TODO: DOCUMENT ME!
	 */
	public void setFaction(Faction faction) {
		this.faction = faction;
	}
}
