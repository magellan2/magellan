// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


/**
 * A class representing a uniquely identifiable object with a
 * modifiable name.
 */
public abstract class NamedObject extends Identifiable implements Named {
	protected String name = null;
	
	/**
	 * Constructs a new named object that is uniquely identifiable
	 * by the specified id.
	 */
	public NamedObject(ID id) {
		super(id);
	}
	
	/**
	 * Sets the name of this object.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of this object.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns a String representation of this object.
	 */
	public String toString() {
		return this.name;
	}
	
	/**
	 * Returns a copy of this named object.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public abstract boolean equals(Object o);
	
	public abstract int compareTo(Object o);
}