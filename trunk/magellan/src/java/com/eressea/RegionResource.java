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

import com.eressea.rules.ItemType;

/**
 * A class representing a resource and its attributes in a region. The id of RegionResource objects
 * is numerical and does not change between reports of different turns, it can therefore be used
 * for merging reports. In order to access a resource in a region this id proves unuseful, though.
 * Instead, the id of the resource's type makes more sense as it also satisfies the uniqueness
 * condition within a region.
 */
public class RegionResource implements Unique {
	private ID id = null; // the numerical id of this resource, also the block id in the cr
	private int skillLevel = -1; // the minimum skill level required to access the resource
	private ItemType type = null; // the type of resource
	private int amount = -1; // the amount of the resource available

	/**
	 * Constructs a new region resource with the specified id and type. There is no default
	 * constructor in order to enforce a valid id and type set for every RegionResource object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 */
	public RegionResource(ID id, ItemType type) {
		this.id = id;
		this.type = type;
	}

	/**
	 * This method allows to set the id of this resource even after object creation. It should be
	 * use with care as ids are often used as map keys or similar objects and changing them will
	 * have non-obvious side effects.
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public void setID(ID id) {
		if(id == null) {
			throw new IllegalArgumentException("RegionResource.setID(): specified id is null!");
		}

		this.id = id;
	}

	/**
	 * Returns the id uniquely identifying this resource.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ID getID() {
		return this.id;
	}

	/**
	 * Specifies the type of the resource. Semantically, only a small range of item types are valid
	 * for a resource (iron, trees, etc.) Note that the type may server as a hash object for this
	 * resource and changing it may require re-hashing.
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public void setType(ItemType type) {
		if(type == null) {
			throw new IllegalArgumentException("RegionResource.setType(): specified item type is null!");
		}

		this.type = type;
	}

	/**
	 * Returns the type of this resource.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getType() {
		return type;
	}

	/**
	 * Sets the amount of the resource visible or available.
	 *
	 * @param amount TODO: DOCUMENT ME!
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Returns the amount of the resource visible or available.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the minimum skill level that is required to access this resource.
	 *
	 * @param level TODO: DOCUMENT ME!
	 */
	public void setSkillLevel(int level) {
		this.skillLevel = level;
	}

	/**
	 * Returns the minimum skill level that is required to access this resource.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSkillLevel() {
		return this.skillLevel;
	}

	/**
	 * Returns a string representation of this resource object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return type.toString();
	}

	/**
	 * This method is a shortcut for calling this.getType().getName()
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		return type.getName();
	}

	/**
	 * Indicates whether some other object is "equal to" this one based on the ID of this object
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		if(o instanceof RegionResource) {
			// pavkovic 2003.01.16: even if the id seems to be unique 
			// use the item type for uniqueness
			return this.getType().getID().equals(((RegionResource) o).getType().getID());

			// return this.getID().equals(((RegionResource)o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Returns a hash code value for the object based on the ID of this object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int hashCode() {
		// pavkovic 2003.01.16: even if the id seems to be unique 
		// use the item type for uniqueness
		return this.getType().getID().hashCode();

		//return this.getID().hashCode();
	}

	/**
	 * Merges the information of curRes into newRes.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curRes TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newRes TODO: DOCUMENT ME!
	 * @param sameTurn TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, RegionResource curRes, GameData newGD,
							 RegionResource newRes, boolean sameTurn) {
		/* the constructor enforces a valid id and type, so we do not
		 need to set them here */
		if(sameTurn) {
			if(curRes.getSkillLevel() >= newRes.getSkillLevel()) {
				newRes.setSkillLevel(curRes.getSkillLevel());
				newRes.setAmount(curRes.getAmount());
			}
		} else {
			if(curRes.getAmount() != -1) {
				newRes.setAmount(curRes.getAmount());
			}

			if(curRes.getSkillLevel() != -1) {
				newRes.setSkillLevel(curRes.getSkillLevel());
			}
		}
	}
}
