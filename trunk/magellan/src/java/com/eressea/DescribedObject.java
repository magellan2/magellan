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
 * A class representing a uniquely identifiable object with a modifiable name and description.
 */
public abstract class DescribedObject extends NamedObject implements Described {
	protected String description = null;

	/**
	 * Constructs a new described object that is uniquely identifiable by the specified id.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public DescribedObject(ID id) {
		super(id);
	}

	/**
	 * Sets the description of this object.
	 *
	 * @param description TODO: DOCUMENT ME!
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the description of this object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns a copy of this described object.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws CloneNotSupportedException TODO: DOCUMENT ME!
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
