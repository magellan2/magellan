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
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @throws NullPointerException if ID is <code>null</code>
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
		return this.id;
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
	public abstract boolean equals(Object o);

	/**
	 * Imposes a natural ordering on Identifiable objects. Especially with implementing sub classes
	 * of Identifiable, such orderings will often be established by the natural order of ids.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract int compareTo(Object o);
}
