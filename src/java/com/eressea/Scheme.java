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
 * A class encapsulating a scheme object indicating the position of a region in the 'Astralraum'
 * relative to the standard Eressea map.
 */
public class Scheme extends NamedObject {
	/**
	 * Create a new Scheme object with the specified unique ID.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public Scheme(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return this.name + " (" + this.id.toString() + ")";
	}

	/**
	 * Merges two Scheme objects.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curScheme TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newScheme TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Scheme curScheme, GameData newGD, Scheme newScheme) {
		if(curScheme.getName() != null) {
			newScheme.setName(curScheme.getName());
		}
	}
}
