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
 * A class representing a uniquely identifiable object with a modifiable name.
 */
public abstract class NamedObject extends Identifiable implements Named {
	private String name = null;

	/**
	 * Constructs a new named object that is uniquely identifiable by the specified id.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public NamedObject(ID id) {
		super(id);
	}

	/**
	 * Sets the name of this object.
	 *
	 * @param name TODO: DOCUMENT ME!
	 */
	public void setName(String name) {
		this.name = name;
	}

    public String getName() {
        return name;
    }
	/**
	 * Returns the (possibly) modified name of this object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getModifiedName() {
		return this.name;
	}

	/**
	 * Returns a String representation of this object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return this.name;
	}

	/**
	 * Returns a copy of this named object.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws CloneNotSupportedException TODO: DOCUMENT ME!
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
