// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


/**
 * A class encapsulating a hot spot, which represents a region of
 * interest on the map.
 */
public class HotSpot extends NamedObject {
	private ID center = null;
	
	/**
	 * Create a new HotSpot object with the specified unique id.
	 */
	public HotSpot(ID id) {
		super(id);
	}
	
	/**
	 * Returns the ID in the center of the region of interest
	 * this HotSpot points to.
	 */
	public ID getCenter() {
		return center;
	}
	
	/**
	 * Set the ID the is at the center of the region of
	 * interest this HotSpot object should point to.
	 */
	public void setCenter(ID center) {
		this.center = center;
	}
	
	public String toString() {
		return this.name;
	}
	
	/**
	 * Indicates whether this HotSpot object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class HotSpot and o's id is equal to the id of this 
	 * HotSpot object.
	 */
	public boolean equals(Object o) {
		if (o instanceof HotSpot) {
			return this.getID().equals(((HotSpot)o).getID());
		} else {
			return false;
		}
	}
	
	/**
	 * Imposes a natural ordering on HotSpot objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((HotSpot)o).getID());
	}
	
	/**
	 * Merges two HotSpot objects.
	 */
	public static void merge(GameData curGD, HotSpot curHS, GameData newGD, HotSpot newHS) {
		if (curHS.getName() != null) {
			newHS.setName(curHS.getName());
		}
		if (curHS.getCenter() != null) {
			try {
				newHS.setCenter((ID) curHS.getCenter().clone());
			} catch(CloneNotSupportedException e) {
				// impossible position, should throw a runtime exception here
			}
		}
	}
}
