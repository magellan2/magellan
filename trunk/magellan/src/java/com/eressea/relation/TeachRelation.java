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
 * A relation indicating that the source unit has a LEHREN order for
 * the target unit.
 */
public class TeachRelation extends InterUnitRelation {
	public TeachRelation(Unit s, Unit t, int line) {
		super(s, t, line);
	}
}
