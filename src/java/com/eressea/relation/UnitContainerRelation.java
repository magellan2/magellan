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
import com.eressea.UnitContainer;

/**
 * A (possibly abstract) relation indicating that the source unit interacts
 * with the target unit container.
 */
public class UnitContainerRelation extends UnitRelation {
	/** TODO: DOCUMENT ME! */
	public final UnitContainer target;

	/**
	 * Creates a new UnitContainerRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param t TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public UnitContainerRelation(Unit s, UnitContainer t, int line) {
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
