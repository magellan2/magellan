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
 * A relation indicating that the source unit has a ATTACKIEREN order for the target unit.
 */
public class AttackRelation extends InterUnitRelation {
	/**
	 * Creates a new AttackRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param t TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public AttackRelation(Unit s, Unit t, int line) {
		super(s, t, line);
	}
}
