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

import java.math.BigDecimal;
import java.util.Iterator;

import com.eressea.rules.ShipType;
import com.eressea.util.logging.Logger;

/**
 * A class for representing a ship.
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
	 * The size of this ship. While the ship is being built, size &lt;= getType().getMaxSize() is
	 * true. After the ship is finished,  size equals getType().getMaxSize().
	 */
	public int size = -1;

	/** The ratio to which degree this ship is damaged. Values range from 0 to 100. */
	public int damageRatio = 0;

	/** The weight of the units and items on this ship in GE. 
	 * @deprecated replaced by cargo
	 */
	public int deprecatedLoad = -1;

	/**
	 * The maximum payload of this ship in GE. 0 &lt;= capacity &lt;= getType().getCapacity() if
	 * the ship is damaged.
	 * @deprecated replaced by capacity
	 */
	public int deprecatedCapacity = -1;

	/** the weight of the units and items on this ship in silver */
	public int cargo = -1;

	/**
	 * The maximum payload of this ship in silver. 0 &lt;= capacity &lt;= getType().getCapacity() if
	 * the ship is damaged.
	 */
	public int capacity = -1;

	/**
	 * Creates a new Ship object.
	 *
	 * @param id 
	 * @param data 
	 */
	public Ship(ID id, GameData data) {
		super(id, data);
	}

	/** The region this ship is in. */
	private Region region = null;

	/**
	 * Sets the region this ship is in and notifies region about it.
	 *
	 * @param region 
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
	 * @return The region the ship is in, possibly null
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * The type of this ship.
	 *
	 * @return The type of this ship
	 */
	public ShipType getShipType() {
		return (ShipType) getType();
	}

	/**
	 * Returns the maximum capacity with respect to  damages of the ship in silver.
	 *
	 * @return Returns the maximum capacity with respect to  damages of the ship in silver
	 */
	public int getMaxCapacity() {
		if(capacity != -1) {
			return capacity;
		}
		return (deprecatedCapacity != -1) ? deprecatedCapacity*100 : getMaxCapacity(getShipType().getCapacity()*100);
	}

	/**
	 * Returns the maximimum capacity with respect to damages of the ship in GE if the undamaged
	 * capacity was <code>maxCapacity</code>.
	 * 
	 * @param maxCapacity The capacity is calculated relative to this capacity 
	 * 
	 * @return The max damaged capacity
	 */
	private int getMaxCapacity(int maxCapacity) {
		return new BigDecimal(maxCapacity).multiply(new BigDecimal(100 - damageRatio))
										  .divide(new BigDecimal(100), BigDecimal.ROUND_DOWN)
										  .intValue();
	}

	/**
	 * Returns the cargo load of this ship.
	 * 
	 * @return Returns the cargo load of this ship
	 */
	public int getCargo() {
		if(cargo != -1) return cargo;
		return deprecatedLoad*100;
	}
	
	/**
	 * Returns the weight of all units of this ship that are not horses or carts in silver based
	 * on the modified units.
	 *
	 * @return The modified weight of the modified units on the ship
	 */
	public int getModifiedLoad() {
		int modLoad = 0;

		for(Iterator iter = modifiedUnits().iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			modLoad += u.getModifiedWeight();
		}

		return modLoad;
	}

	/**
	 * This is a helper function for showing inner object state.
	 * 
	 * @return A debug message
	 */
	public String toDebugString() {
		return "SHIP[" + "shoreId=" + shoreId + "," + "size=" + size + "," + "damageRation=" +
			   damageRatio + "," + "deprecatedLoad=" + deprecatedLoad + "," + "deprecatedCapacity=" + deprecatedCapacity + "]";
	}

	/**
	 * A string representation of this ship.
	 *
	 * @return A string representation of this ship
	 */
	public String toString() {
		return toString(true);
	}

	/**
	 * Returns the string representation of this ship. If <code>printExtended</code> is true,
	 * type, damage and remaing capacity are shown, too.
	 * 
	 * @param printExtended
	 *            Whether to return a more detailed description
	 * 
	 * @return A strig representation of this ship
	 */
	public String toString(boolean printExtended) {
		StringBuffer sb = new StringBuffer();

		sb.append(getName()).append(" (").append(this.getID().toString()).append(")");

		if(printExtended) {
			sb.append(", ").append(getType());

			int nominalShipSize = getShipType().getMaxSize();

			if(size != nominalShipSize) {
				sb.append(" (").append(size).append("/").append(nominalShipSize).append(")");
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
	 * Merges ships.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curShip TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newShip TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Ship curShip, GameData newGD, Ship newShip) {
		UnitContainer.merge(curGD, curShip, newGD, newShip);

		if(curShip.cargo != -1) {
			newShip.cargo = curShip.cargo;
		}

		if(curShip.capacity != -1) {
			newShip.capacity = curShip.capacity;
		}

		if(curShip.deprecatedCapacity != -1) {
			newShip.deprecatedCapacity = curShip.deprecatedCapacity;
		}

		if(curShip.damageRatio != -1) {
			newShip.damageRatio = curShip.damageRatio;
		}

		if(curShip.deprecatedLoad != -1) {
			newShip.deprecatedLoad = curShip.deprecatedLoad;
		}

		if(curShip.getRegion() != null) {
			newShip.setRegion(newGD.getRegion((CoordinateID) curShip.getRegion().getID()));
		}

		newShip.shoreId = curShip.shoreId;

		if(curShip.size != -1) {
			newShip.size = curShip.size;
		}
	}
}
