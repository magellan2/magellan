// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.relation;

import java.util.List;

import com.eressea.Unit;

/**
 * A relation indicating a movement of a unit.
 */
public class MovementRelation extends UnitRelation implements LongOrderRelation {
	/** 
	 * This list consists of the reached coordinates (starting with the current region)
	 */
	public final List movement;
	
	public MovementRelation(Unit s, List m, int line) {
		super(s,line);
		this.movement = m;
	}

	public String toString() {
		return super.toString()+"@MOVEMENT="+movement;
	}
}
