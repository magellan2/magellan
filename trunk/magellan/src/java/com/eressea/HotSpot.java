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
 * A class encapsulating a hot spot, which represents a region of interest on the map.
 */
public class HotSpot extends NamedObject {
	private ID center = null;

	/**
	 * Create a new HotSpot object with the specified unique id.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public HotSpot(ID id) {
		super(id);
	}

	/**
	 * Returns the ID in the center of the region of interest this HotSpot points to.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ID getCenter() {
		return center;
	}

	/**
	 * Set the ID the is at the center of the region of interest this HotSpot object should point
	 * to.
	 *
	 * @param center TODO: DOCUMENT ME!
	 */
	public void setCenter(ID center) {
		this.center = center;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Merges two HotSpot objects.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curHS TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newHS TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, HotSpot curHS, GameData newGD, HotSpot newHS) {
		if(curHS.getName() != null) {
			newHS.setName(curHS.getName());
		}

		if(curHS.getCenter() != null) {
			try {
				newHS.setCenter((ID) curHS.getCenter().clone());
			} catch(CloneNotSupportedException e) {
				// impossible position, should throw a runtime exception here
			}
		}
	}
}
