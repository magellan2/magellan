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

package com.eressea.relation;

import com.eressea.Unit;

import com.eressea.rules.Race;

/**
 * A relation indicating that a unit transfers a certain amount of persons to another unit.
 */
public class PersonTransferRelation extends TransferRelation {
	/** TODO: DOCUMENT ME! */
	public final Race race;

	/**
	 * Creates a new PersonTransferRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param t TODO: DOCUMENT ME!
	 * @param a TODO: DOCUMENT ME!
	 * @param r TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public PersonTransferRelation(Unit s, Unit t, int a, Race r, int line) {
		super(s, t, a, line);
		this.race = r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return super.toString() + "@RACE=" + race;
	}
}
