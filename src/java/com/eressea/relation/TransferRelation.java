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

/**
 * A (possibly abstract) relation indicating that the source unit transfers a certain amount of
 * some objects to the target unit.
 */
public class TransferRelation extends InterUnitRelation {
	/**
	 * The amount to transfer. This has to be a non-negative value based on the rest amount of the
	 * transfered object
	 */
	public int amount;

	/**
	 * Creates a new TransferRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param t TODO: DOCUMENT ME!
	 * @param a TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public TransferRelation(Unit s, Unit t, int a, int line) {
		super(s, t, line);
		this.amount = a;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return super.toString() + "@AMOUNT=" + amount;
	}
}
