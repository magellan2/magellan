// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// ===

package com.eressea.util.filters;

import com.eressea.Faction;
import com.eressea.Unit;

/**
 *
 * @author  Andreas
 * @version 
 */
public class UnitFactionFilter extends UnitFilter {
	
	protected String factionS;
	protected Faction faction;
	
	public UnitFactionFilter(String faction) {
		this.factionS = faction;
	}
	
	public UnitFactionFilter(Faction faction) {
		this.faction = faction;
	}
	
	public boolean acceptUnit(Unit u) {
		Faction f = u.getFaction();
		return (f != null) && ((factionS != null && (factionS.equals(f.getName()) || factionS.equals(f.getID().toString()))) || (faction != null && (faction.equals(f))));
	}
	
	public void setFaction(String faction) {
		factionS = faction;
	}
	public void setFaction(Faction faction) {
		this.faction = faction;
	}
}
