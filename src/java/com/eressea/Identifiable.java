/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package com.eressea;

/**
 * A template class for objects to be uniquely identifiable by other objects.
 */
public abstract class Identifiable extends Object implements Unique, Comparable, Cloneable {
	/** The object imposing the unique identifiability.  This is immutable. */
	protected final ID id;

	/**
	 * Creates a new identifiable object with the specified id.
	 *
	 * @param id ID of the Identifiable
	 *
	 * @throws NullPointerException if <tt>ID</tt> is <code>null</code>
	 */
	public Identifiable(ID id) {
		if(id == null) {
			throw new NullPointerException();
		}

		this.id = id;
	}

	/**
	 * Returns the id uniquely identifying this object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ID getID() {
		return id;
	}

	/**
	 * Returns a copy of this object identified by a copy of the orignial's id. I.e., the following
	 * statement holds true: this.getID() != this.clone().getID()
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws CloneNotSupportedException TODO: DOCUMENT ME!
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Indicates that this object is to be regarded as equal to some other object. Especially with
	 * implementing sub classes of Identifiable, equality will often be established through the
	 * equality of ids.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		try {
			return this == o || 
				(o != null && 
				 getID().equals(((Identifiable) o).getID()) && 
				 getClass().isInstance(o));
		} catch(ClassCastException e) {
			return false;
		}
	}

	/**
	 * As we want to use the hashCode/equals contract we need to force the implementation of 
	 * hashCode.
	 *
	 * @return the hashCode of the current object
	 */
	public int hashCode() {
		return getID() == null ? super.hashCode() : getID().hashCode();
	}

	/**
	 * TODO: DOCUMENT ME!
	 * 
	 * @return TODO: DOCUMENT ME!
	 */
	public int superHashCode() {
		return super.hashCode();
	}

	/**
	 * Imposes a natural ordering on Identifiable objects. Especially with implementing sub classes
	 * of Identifiable, such orderings will often be established by the natural order of ids.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return getID().compareTo(((Identifiable) o).getID());
	}
}
