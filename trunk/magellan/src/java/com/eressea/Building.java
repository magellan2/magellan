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

import com.eressea.rules.BuildingType;

/**
 * A <code>Building</code> found in Atlantis reports.
 *
 * @author $author$
 * @version $Revision$
 */
public class Building extends UnitContainer implements HasRegion {
	/** Size of the building. */
	private int size = 0;

	/** Costs for the building. Could depend on size, so don't put it into the UnitContType. */
	private int cost = 0;
	
	private String trueBuildingType = null; 

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
	 * Sets the region this building is in. If this building already has a region set, this method
	 * takes care of removing it from that region.
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
	 * Returns the <code>BuildingType</code> of this building.
	 *
	 * @return the <code>BuildingType</code> of this building
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
	 * Sets the trueBuildingType which es not realy a type but
	 * just a String
	 * only occurance now "Traumschlößchen",wahrerTyp
	 * Fiete 20060910
	 * 
	 * @param trueBuildingType  as string
	 */
	
	public void setTrueBuildingType(String trueBuildingType){
		this.trueBuildingType = trueBuildingType;
	}
	/**
	 * Gets the trueBuildingType which es not realy a type but
	 * just a String
	 * only occurance now "Traumschlößchen",wahrerTyp
	 * Fiete 20060910
	 * 
	 * 
	 * @return String = trueBuildingType
	 */
	public String getTrueBuildingType() {
		return this.trueBuildingType;
	}
	

	/**
	 * Merges buildings. The new one get the name, comments etc. from the current one, effects etc.
	 * are added, not written over.
	 *
	 * @param curGD current GameData
	 * @param curBuilding the current Building
	 * @param newGD new GameData
	 * @param newBuilding the new Building
	 *
	 * @see UnitContainer#merge
	 */
	public static void merge(GameData curGD, Building curBuilding, GameData newGD,
							 Building newBuilding) {
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
		
		// Fiete 20060910
		// added support for wahrerTyp
		if (curBuilding.getTrueBuildingType()!=null) {
			newBuilding.setTrueBuildingType(curBuilding.getTrueBuildingType());
		}
		
	}

	/**
	 * Returns a String representation of the Building object.
	 *
	 * @return the Building object as string.
	 */
	public String toString() {
		// Fiete 20060910
		// added support for wahrer Typ
		if (this.trueBuildingType==null){
			return getName() + " (" + id + "), " + this.getType() + " (" + this.getSize() + ")";
		} else {
			return this.trueBuildingType + ": " + getName() + " (" + id + "), " + this.getType() + " (" + this.getSize() + ")";
		}
	}
}
