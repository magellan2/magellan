// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

import java.util.Iterator;

import com.eressea.relation.PersonTransferRelation;
import com.eressea.relation.RecruitmentRelation;
import com.eressea.util.Cache;

public class ZeroUnit extends Unit {
	/**
	 * This is the unit associated with the id 0.
	 * Used for UnitRelations for commands like "give 0..."
	 */
	public final static ID ZERO_ID=UnitID.create(0);
	public ZeroUnit(Region r) {
		// pavkovic 2003.09.09: reduce amount of UnitID(0) from n regions to 1
		super(ZERO_ID);
		setRegion(r);		
	}
	
	public void setRegion(Region r) {
		if (r != getRegion()) {
			if (this.region != null) {
				this.region.removeUnit(this.getID());
			}
			// pavkovic 2002.09.30: dont add to region
			// this unit shall not exist in Region.units()
			// if (r != null)
			//	r.addUnit(this);
			this.region = r;
		}
	}

	public int getPersons() {
		// delivers the amount of recruitable persons
		return getRegion().maxRecruit();
	}

	public int getModifiedPersons() {
		// delivers the amount of recruitable persons - recruited persons
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
		for(Iterator iter = getPersonTransferRelations().iterator(); iter.hasNext(); ) {
			PersonTransferRelation ptr = (PersonTransferRelation) iter.next();
			if(! (ptr instanceof RecruitmentRelation)) {
				result += ptr.amount;
			}
		}
		return result;
	}

	/**
	 * Returns a string representation of this temporary unit.
	 */
	public String toString() {
		return getRegion().toString();
	}

}
