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

package com.eressea;

import com.eressea.rules.BuildingType;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Building extends UnitContainer implements HasRegion {
	/** Size of the building. */
	private int size = 0;

	/**
	 * Costs for the building. Could depend on size, so don't put it into the
	 * UnitContType.
	 */
	private int cost = 0;

	/**
	 * Creates the Object for a building.
	 *
	 * @see com.eressea.UnitContainer#UnitContainer(ID, GameData)
	 */
	public Building(ID id, GameData data) {
		super(id, data);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the size of the building.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param iSize set the size of the building.
	 */
	public void setSize(int iSize) {
		size = iSize;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return the cost of the building.
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param iCost set the cost for the building.
	 */
	public void setCost(int iCost) {
		cost = iCost;
	}

	/** The region this building is in. */
	private Region region = null;

	/**
	 * Sets the region this building is in. If this building already has a
	 * region set, this method takes care of removing it from that region.
	 *
	 * @param region the region to the the building into.
	 */
	public void setRegion(Region region) {
		// remove the building from a prior location
		if(this.region != null) {
			this.region.removeBuilding(this);
		}

		// set the new region and add the building
		this.region = region;

		if(this.region != null) {
			this.region.addBuilding(this);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType() {
		return (BuildingType) getType();
	}

	/**
	 * Get the region where this building is located.
	 *
	 * @return the region the building is in.
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * Indicates whether this Building object is equal to another object.
	 *
	 * @param o the Building object to compare with.
	 *
	 * @return true only if o is not null and an instance of class Building and
	 * 		   o's id is equal to the id of this  Building object.
	 */
	public boolean equals(Object o) {
		if(o instanceof Building) {
			return this.getID().equals(((Building) o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on Building objects equivalent to the natural
	 * ordering of their ids.
	 *
	 * @param o the Building object to compare with.
	 *
	 * @return zero if equal.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Building) o).getID());
	}

	/**
	 * Merges buildings. The new one get the name, comments etc. from the
	 * current one, effects etc. are added, not written over.
	 *
	 * @param curGD current GameData
	 * @param curBuilding the current Building
	 * @param newGD new GameData
	 * @param newBuilding the new Building
	 *
	 * @see UnitContainer#merge
	 */
	public static void merge(GameData curGD, Building curBuilding,
							 GameData newGD, Building newBuilding) {
		UnitContainer.merge(curGD, curBuilding, newGD, newBuilding);

		if(curBuilding.getCost() != -1) {
			newBuilding.setCost(curBuilding.getCost());
		}

		if(curBuilding.getRegion() != null) {
			newBuilding.setRegion(newGD.getRegion(curBuilding.getRegion().getID()));
		}

		if(curBuilding.getSize() != -1) {
			newBuilding.setSize(curBuilding.getSize());
		}
	}

	/**
	 * Returns a String representation of the Building object.
	 *
	 * @return the Building object as string.
	 */
	public String toString() {
		return name + " (" + id + "), " + this.getType() + " (" +
			   this.getSize() + ")";
	}
}
