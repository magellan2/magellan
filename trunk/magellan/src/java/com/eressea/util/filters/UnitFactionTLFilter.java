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
public class UnitFactionTLFilter extends UnitFilter {
	
	protected int minTL, maxTL;
	
	public UnitFactionTLFilter(int minTL, int maxTL) {
		this.minTL = minTL;
		this.maxTL = maxTL;
	}
	
	public boolean acceptUnit(Unit u) {
		Faction f = u.getFaction();
		return (f != null) && (minTL <= f.trustLevel) && (f.trustLevel <= maxTL);
	}
	
	public int getMinTL() {
		return minTL;
	}
	public int getMaxTL() {
		return maxTL;
	}
	public void setMinTL(int minTL) {
		this.minTL = minTL;
	}
	public void setMaxTL(int maxTL) {
		this.maxTL = maxTL;
	}
}
