// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;

 
import com.eressea.ID;

public abstract class UnitContainerType extends ObjectType {

	public UnitContainerType(ID id) {
		super(id);
	}
	
	public abstract boolean equals(Object o);
	
	public abstract int compareTo(Object o);
}