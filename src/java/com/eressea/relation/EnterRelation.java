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
 * A relation indicating that a unit enters a unit container.
 */
public class EnterRelation extends UnitContainerRelation {
	public EnterRelation(Unit s, UnitContainer t, int line) {
		super(s, t, line);
	}
}
