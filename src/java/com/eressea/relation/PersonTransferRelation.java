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

package com.eressea.relation;

import com.eressea.Unit;
import com.eressea.rules.Race;

/**
 * A relation indicating that a unit transfers a certain amount of persons to another unit.
 */
public class PersonTransferRelation extends TransferRelation {
	/** The source unit's race  */
	public final Race race;

	/**
	 * Creates a new PersonTransferRelation object.
	 *
	 * @param s The source unit
	 * @param t The target unit
	 * @param a The amount to transfer
	 * @param r The race of the source Unit
	 * @param line The line in the source's orders
	 */
	public PersonTransferRelation(Unit s, Unit t, int a, Race r, int line) {
		super(s, t, a, line);
		this.race = r;
	}

	/* (non-Javadoc)
	 * @see com.eressea.relation.TransferRelation#toString()
	 */
	public String toString() {
		return super.toString() + "@RACE=" + race;
	}
}
