// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


/**
 * A template class for objects to be uniquely identifiable by other
 * objects.
 */
public abstract class Identifiable extends Object implements Unique, Comparable, Cloneable {
	/**
	 * The object imposing the unique identifiability. 
	 * This is immutable.
	 */
	protected final ID id;
	
	/**
	 * Creates a new identifiable object with the specified id.
	 */
	public Identifiable(ID id) {
		if(id == null) throw new NullPointerException();
		this.id = id;
	}
		
	/**
	 * Returns the id uniquely identifying this object.
	 */
	public ID getID() {
		return this.id;
	}
	
	/**
	 * Returns a copy of this object identified by a copy of the
	 * orignial's id. I.e., the following statement holds true:
	 * this.getID() != this.clone().getID()
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * Indicates that this object is to be regarded as equal to some
	 * other object. Especially with implementing sub classes of
	 * Identifiable, equality will often be established through the
	 * equality of ids.
	 */
	public abstract boolean equals(Object o);
	
	/**
	 * Imposes a natural ordering on Identifiable objects. Especially
	 * with implementing sub classes of Identifiable, such orderings
	 * will often be established by the natural order of ids.
	 */
	public abstract int compareTo(Object o);
}
