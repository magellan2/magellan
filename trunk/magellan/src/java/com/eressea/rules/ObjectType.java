/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 * $Id$
 */

package com.eressea.rules;

import com.eressea.ID;
import com.eressea.NamedObject;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public abstract class ObjectType extends NamedObject {
	/**
	 * Creates a new ObjectType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public ObjectType(ID id) {
		super(id);
	}

	/**
	 * Indicates whether this ObjectType object is equal to another object type
	 * object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract boolean equals(Object o);

	/**
	 * Imposes a natural ordering on ObjectType objects.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract int compareTo(Object o);
}
