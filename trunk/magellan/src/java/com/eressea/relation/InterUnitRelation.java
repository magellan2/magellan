// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.relation;


import com.eressea.Unit;

/**
 * A (possibly abstract) relation indicating between a source and a
 * target unit.
 */
public class InterUnitRelation extends UnitRelation {
	public Unit target;
	
	public InterUnitRelation(Unit s, Unit t, int line) {
		super(s,line);
		this.target = t;
	}

	public String toString() {
		return super.toString()+"@TARGET="+target;
	}
}
