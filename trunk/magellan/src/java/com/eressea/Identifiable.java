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
	 */
	protected ID id = null;
	
	/**
	 * Creates a new identifiable object with the specified id.
	 */
	public Identifiable(ID id) {
		setID(id);
	}
	
	/**
	 * This method allows to set the id of an uniquely identifiable
	 * object even after object creation. It should be use with care
	 * as ids are often used as map keys or similar objects and
	 * changing them will have non-obvious side effects.
	 */
	public void setID(ID id) {
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
		Identifiable i = (Identifiable)super.clone();
		i.setID((ID)this.getID().clone());
		return i;
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