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
 * A (possibly abstract) relation originating from a source unit.
 */
public abstract class UnitRelation {
	/** the origin of this relation, normally the same as source */
	public Unit origin;

	/** the source of this relation */
	public final Unit source;

	/** TODO: DOCUMENT ME! */
	public final int line;

	/**
	 * Creates a new UnitRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public UnitRelation(Unit s, int line) {
		this.origin = s;
		this.source = s;
		this.line = line;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return this.getClass().getName() + "@ORIGIN=" + origin + "@SOURCE=" + source + "@line=" +
			line;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isLongOrder() {
		return this instanceof LongOrderRelation;
	}
}
