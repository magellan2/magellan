// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

/**
 * A class encapsulating a scheme object indicating the position of a
 * region in the 'Astralraum' relative to the standard Eressea map.
 */
public class Scheme extends NamedObject {

	/**
	 * Create a new Scheme object with the specified unique ID.
	 */
	public Scheme(ID id) {
		super(id);
	}

	public String toString() {
		return this.name + " (" + this.id.toString() + ")";
	}

	/**
	 * Indicates whether this Scheme object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class Scheem and o's ID is equal to the ID of this 
	 * Scheme object.
	 */
	public boolean equals(Object o) {
		if (o instanceof Scheme) {
			return this.getID().equals(((Scheme)o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on Scheme objects equivalent to
	 * the natural ordering of their IDs.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Scheme)o).getID());
	}

	/**
	 * Merges two Scheme objects.
	 */
	public static void merge(GameData curGD, Scheme curScheme, GameData newGD, Scheme newScheme) {
		if (curScheme.getName() != null) {
			newScheme.setName(curScheme.getName());
		}
	}
}