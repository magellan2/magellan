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
 * modifiable name and description.
 */
public abstract class DescribedObject extends NamedObject implements Described {
	protected String description = null;
	
	/**
	 * Constructs a new described object that is uniquely
	 * identifiable by the specified id.
	 */
	public DescribedObject(ID id) {
		super(id);
	}
	
	/**
	 * Sets the description of this object.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the description of this object.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Indicates whether another described object is "equal to" this
	 * one.
	 */
	public abstract boolean equals(Object o);
	
	/**
	 * Imposes a natural ordering on described objects.
	 */
	public abstract int compareTo(Object o);
	
	/**
	 * Returns a copy of this described object.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}