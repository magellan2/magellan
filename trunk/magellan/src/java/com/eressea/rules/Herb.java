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
 */

package com.eressea.rules;

import com.eressea.ID;

/**
 * Represents a herb type.
 */
public class Herb extends ItemType {
	private RegionType regionType = null;

	/**
	 * Creates a new Herb object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public Herb(ID id) {
		super(id);
	}

	/**
	 * Sets the kind of region this herb can be found in.
	 *
	 * @param r TODO: DOCUMENT ME!
	 */
	public void setRegionType(RegionType r) {
		this.regionType = r;
	}

	/**
	 * Returns the region type this herb can be found in.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType() {
		return this.regionType;
	}
}
