// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import com.eressea.ID;

public class RegionType extends UnitContainerType {
	private int maxWorkers = -1;
	private int maxTrees = -1;
	private int roadStones = -1;
	private BuildingType roadSupport = null; // Building needed for road.

	public RegionType(ID id) {
		super(id);
	}

	public void setMaxWorkers(int w) {
		maxWorkers = w;
	}

	public int getMaxWorkers() {
		return maxWorkers;
	}

	public void setMaxTrees(int mt) {
		maxTrees = mt;
	}

	public int getMaxTrees() {
		return maxTrees;
	}

	public void setRoadStones(int i) {
		roadStones = i;
	}

	public int getRoadStones() {
		return roadStones;
	}

	public void setRoadSupportBuilding(BuildingType b) {
		roadSupport = b;
	}

	public BuildingType getRoadSupportBuilding() {
		return roadSupport;
	}
	
	/**
	 * Indicates whether this RegionType object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class RegionType and o's id is equal to the id of this 
	 * RegionType object.
	 */
	public boolean equals(Object o) {
		return this == o || 
			(o instanceof RegionType && this.getID().equals(((RegionType)o).getID()));
	}
	
	/**
	 * Imposes a natural ordering on RegionType objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((RegionType)o).getID());
	}
}
