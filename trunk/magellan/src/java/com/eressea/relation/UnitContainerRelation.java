// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.relation;


import com.eressea.Unit;
import com.eressea.UnitContainer;

/**
 * A (possibly abstract) relation indicating that the source unit
 * interacts with the target unit container.
 */
public class UnitContainerRelation extends UnitRelation {
	public final UnitContainer target;
	
	public UnitContainerRelation(Unit s, UnitContainer t, int line) {
		super(s, line);
		this.target = t;
	}

	public String toString() {
		return super.toString()+"@TARGET="+target;
	}

}
