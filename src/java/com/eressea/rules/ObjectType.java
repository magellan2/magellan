// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import com.eressea.ID;
import com.eressea.NamedObject;

public abstract class ObjectType extends NamedObject {

	public ObjectType(ID id) {
		super(id);
	}
	
	/**
	 * Indicates whether this ObjectType object is equal to another
	 * object type object.
	 */
	public abstract boolean equals(Object o);
	
	/**
	 * Imposes a natural ordering on ObjectType objects.
	 */
	public abstract int compareTo(Object o);
}