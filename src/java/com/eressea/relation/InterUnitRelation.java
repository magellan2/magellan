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

/**
 * A (possibly abstract) relation indicating between a source and a target unit.
 */
public class InterUnitRelation extends UnitRelation {
	/** TODO: DOCUMENT ME! */
	public Unit target;

	/**
	 * Creates a new InterUnitRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param t TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public InterUnitRelation(Unit s, Unit t, int line) {
		super(s, line);
		this.target = t;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return super.toString() + "@TARGET=" + target;
	}
}
