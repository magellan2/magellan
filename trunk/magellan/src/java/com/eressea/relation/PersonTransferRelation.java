// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.relation;


import com.eressea.Unit;
import com.eressea.rules.Race;

/**
 * A relation indicating that a unit transfers a certain amount of
 * persons to another unit.
 */
public class PersonTransferRelation extends TransferRelation {
	public final Race race;

	public PersonTransferRelation(Unit s, Unit t, int a, Race r, int line) {
		super(s, t, a, line);
		this.race = r;
	}

	public String toString() {
		return super.toString()+"@RACE="+race;
	}
}
