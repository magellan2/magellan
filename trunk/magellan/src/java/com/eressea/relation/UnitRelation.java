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
 * A (possibly abstract) relation originating from a source unit.
 */
public class UnitRelation {
	/** the origin of this relation, normally the same as source */
	public Unit origin;

	/** the source of this relation */
	public final Unit source;

	public final int line;
	
	public UnitRelation(Unit s, int line) {
		this.origin = s;
		this.source = s;
		this.line = line;
	}
	
	public String toString() {
		return this.getClass().getName()+"@ORIGIN="+origin+"@SOURCE="+source+"@line="+line;
	}

	public boolean isLongOrder() {
		return this instanceof LongOrderRelation;
	}
}
