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

package com.eressea;

import java.util.Iterator;

import com.eressea.rules.ShipType;

import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Ship extends UnitContainer implements HasRegion {
	private static final Logger log = Logger.getInstance(Ship.class);

	/** The shore the ship is lying. */
	public int shoreId = -1; // 0 = northwest, 1 = northeast, etc.
							 // -1 = every direction

	/**
	 * The size of this ship. While the ship is being built, size &lt;=
	 * getType().getMaxSize() is true. After the ship is finished,  size
	 * equals getType().getMaxSize().
	 */
	public int size = -1;

	/**
	 * The ratio to which degree this ship is damaged. Values range from 0 to
	 * 100.
	 */
	public int damageRatio = 0;

	/** The weight of the units and items on this ship in GE. */
	public int load = -1;

	/**
	 * The maximum payload of this ship in GE. 0 &lt;= capacity &lt;=
	 * getType().getCapacity() if the ship is damaged.
	 */
	public int capacity = -1;

	/**
	 * Creates a new Ship object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 */
	public Ship(ID id, GameData data) {
		super(id, data);
	}

	/** The region this ship is in. */
	private Region region = null;

	/**
	 * Sets the region this ship is in.
	 *
	 * @param region TODO: DOCUMENT ME!
	 */
	public void setRegion(Region region) {
		if(this.region != null) {
			this.region.removeShip(this);
		}

		this.region = region;

		if(this.region != null) {
			this.region.addShip(this);
		}
	}

	/**
	 * Returns the region this ship is in.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType() {
		return (ShipType) getType();
	}

	/**
	 * Returns the maximum capacity with respect to  damages of the ship
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMaxCapacity() {
		return (capacity != -1) ? capacity : getShipType().getCapacity();
	}

	/**
	 * Returns the weight of all units of this ship that are not horses or
	 * carts in GE  100 based on the modified units.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getModifiedLoad() {
		int modLoad = 0;

		for(Iterator iter = modifiedUnits().iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			modLoad += u.getModifiedWeight();
		}

		return modLoad;
	}

	// this is a helper function for showing inner object state
	public String toDebugString() {
		return "SHIP[" + "shoreId=" + shoreId + "," + "size=" + size + "," +
			   "damageRation=" + damageRatio + "," + "load=" + load + "," +
			   "capacity=" + capacity + "]";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return toString(true);
	}

	/**
	 * Returns the string representation of this
	 *
	 * @param printExtended TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString(boolean printExtended) {
		StringBuffer sb = new StringBuffer();

		sb.append(getName()).append(" (").append(this.getID().toString())
		  .append(")");

		if(printExtended) {
			sb.append(", ").append(getType());

			int nominalShipSize = getShipType().getMaxSize();

			if(size != nominalShipSize) {
				sb.append(" (").append(size).append("/").append(nominalShipSize)
				  .append(")");
			}

			if(damageRatio != 0) {
				sb.append(", ").append(damageRatio).append("% Beschädigung");
			}
		}

		if(log.isDebugEnabled()) {
			log.debug("Ship.toString: " + sb.toString());
		}

		return sb.toString();
	}

	/**
	 * Indicates whether this Ship object is equal to another object. Returns
	 * true only if o is not null and an instance of class Ship and o's id is
	 * equal to the id of this  Ship object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (this == o) ||
			   (o instanceof Ship && this.getID().equals(((Ship) o).getID()));
	}

	/**
	 * Imposes a natural ordering on Ship objects equivalent to the natural
	 * ordering of their ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Ship) o).getID());
	}

	/**
	 * Merges ships.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curShip TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newShip TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Ship curShip, GameData newGD,
							 Ship newShip) {
		UnitContainer.merge(curGD, curShip, newGD, newShip);

		if(curShip.capacity != -1) {
			newShip.capacity = curShip.capacity;
		}

		if(curShip.damageRatio != -1) {
			newShip.damageRatio = curShip.damageRatio;
		}

		if(curShip.load != -1) {
			newShip.load = curShip.load;
		}

		if(curShip.getRegion() != null) {
			newShip.setRegion(newGD.getRegion(curShip.getRegion().getID()));
		}

		newShip.shoreId = curShip.shoreId;

		if(curShip.size != -1) {
			newShip.size = curShip.size;
		}
	}
}
