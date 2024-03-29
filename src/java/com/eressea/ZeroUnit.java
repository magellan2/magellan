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

import java.util.Iterator;

import com.eressea.relation.PersonTransferRelation;
import com.eressea.relation.RecruitmentRelation;
import com.eressea.util.Cache;

/**
 * A ZeroUnit mimics behaviour of a unit called "0".
 *
 * @author $author$
 * @version $Revision$
 */
public class ZeroUnit extends Unit {
	/**
	 * This is the unit associated with the id 0. Used for UnitRelations for commands like "give
	 * 0..."
	 */
	public static final ID ZERO_ID = UnitID.create(0);

	/**
	 * Creates a new ZeroUnit object.
	 *
	 * @param r the region this ZeroUnit belongs to.
	 */
	public ZeroUnit(Region r) {
		// pavkovic 2003.09.09: reduce amount of UnitID(0) from n regions to 1
		super(ZERO_ID);
		setRegion(r);
	}

	/**
	 * Sets the region of this unit
	 *
	 * @param r the region of this unit
	 */
	public void setRegion(Region r) {
		if(r != getRegion()) {
			if(this.region != null) {
				this.region.removeUnit(this.getID());
			}

			// pavkovic 2002.09.30: dont add to region
			// this unit shall not exist in Region.units()
			// if (r != null)
			//	r.addUnit(this);
			this.region = r;
		}
	}

	/**
	 * Returns the amount of recruitable persons
	 *
	 * @return amount of recruitable persons
	 */
	public int getPersons() {
		// 
		return getRegion().maxRecruit();
	}

	/**
	 * Returns the amount of recruitable persons - recruited persons
	 *
	 * @return amount of recruitable persons - recruited persons
	 */
	public int getModifiedPersons() {
		if(cache == null) {
			cache = new Cache();
		}

		if(cache.modifiedPersons == -1) {
			cache.modifiedPersons = super.getModifiedPersons() - getGivenPersons();
		}

		return cache.modifiedPersons;
	}

	protected int getGivenPersons() {
		// delivers the number of persons given to region via command "GIVE 0 x PERSONS"
		int result = 0;

		for(Iterator iter = getPersonTransferRelations().iterator(); iter.hasNext();) {
			PersonTransferRelation ptr = (PersonTransferRelation) iter.next();

			if(!(ptr instanceof RecruitmentRelation)) {
				result += ptr.amount;
			}
		}

		return result;
	}

	/**
	 * Returns a string representation of this temporary unit.
	 *
	 * @return a string representation of this temporary unit
	 */
	public String toString() {
		return getRegion().toString();
	}
}
