// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import com.eressea.ID;

public class ShipType extends UnitContainerType {
	private int maxSize = -1;
	private int buildLevel = -1;
	private int range = -1;
	private int capacity = -1;
	private int captainLevel = -1;
	private int sailorLevel = -1;

	public ShipType(ID id) {
		super(id);
	}

	public void setMaxSize(int s) {
		maxSize = s;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setBuildLevel(int l) {
		buildLevel = l;
	}

	public int getBuildLevel() {
		return buildLevel;
	}

	public void setRange(int r) {
		range = r;
	}

	public int getRange() {
		return range;
	}

	public void setCapacity(int c) {
		capacity = c;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCaptainSkillLevel(int l) {
		captainLevel = l;
	}

	public int getCaptainSkillLevel() {
		return captainLevel;
	}

	public void setSailorSkillLevel(int l) {
		sailorLevel = l;
	}

	public int getSailorSkillLevel() {
		return sailorLevel;
	}
	
	/**
	 * Indicates whether this ShipType object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class ShipType and o's id is equal to the id of this 
	 * ShipType object.
	 */
	public boolean equals(Object o) {
		return this == o || 
			(o instanceof ShipType &&  this.getID().equals(((ShipType)o).getID()));
	}
	
	/**
	 * Imposes a natural ordering on ShipType objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((ShipType)o).getID());
	}
}
