// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import com.eressea.ID;

/**
 * Represents a herb type.
 */
public class Herb extends ItemType {
	private RegionType regionType = null;

	public Herb(ID id) {
		super(id);
	}

	/**
	 * Sets the kind of region this herb can be found in.
	 */
	public void setRegionType(RegionType r) {
		this.regionType = r;
	}

	/**
	 * Returns the region type this herb can be found in.
	 */
	public RegionType getRegionType() {
		return this.regionType;
	}
}