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
 * $Id$
 */

package com.eressea.relation;

import java.util.List;

import com.eressea.Unit;

/**
 * A relation indicating a movement of a unit.
 */
public class MovementRelation extends UnitRelation implements LongOrderRelation {
	/**
	 * This list consists of the reached coordinates (starting with the current
	 * region)
	 */
	public final List movement;

	/**
	 * Creates a new MovementRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param m TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public MovementRelation(Unit s, List m, int line) {
		super(s, line);
		this.movement = m;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return super.toString() + "@MOVEMENT=" + movement;
	}
}
