// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;



import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.eressea.ID;
import com.eressea.util.CollectionFactory;

public class RegionType extends UnitContainerType {
	private int inhabitants = -1;

	public RegionType(ID id) {
		super(id);
	}

	public void setInhabitants(int i) {
		inhabitants = i;
	}

	/** helper method for xml reader */
	public void setInhabitants(String i) {
		setInhabitants(Integer.parseInt(i));
	}


	public int getInhabitants() {
		return inhabitants;
	}

	/** @deprecated */
	public int getRoadStones() {
		for(Iterator iter = resources.iterator(); iter.hasNext(); ) {
			Resource r = (Resource) iter.next();
			if(r.getObjectType() instanceof ItemType) {
				return r.getAmount();
			}
		}
		return -1;
	}
	
	/** @deprecated */
	public BuildingType getRoadSupportBuilding() {
		for(Iterator iter = resources.iterator(); iter.hasNext(); ) {
			Resource r = (Resource) iter.next();
			if(r.getObjectType() instanceof BuildingType) {
				return (BuildingType) r.getObjectType();
			}
		}
		return null;
	}

	private List resources = CollectionFactory.createLinkedList();
	public void addRoadResource(Resource r) {
		resources.add(r);
	}

	/**
	 * Gets a List of needed Resources for road building
	 */
	public List getRoadResources() {
		return Collections.unmodifiableList(resources);
	}
	
	
	private boolean isOcean = false;
	public boolean isOcean() {
		return isOcean;
	}
	
	public void setIsOcean(boolean isOcean) {
		this.isOcean = isOcean;
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
