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

import com.eressea.Region;
import com.eressea.Unit;

/**
 * A relation indicating earning money from a unit container (region) based on
 * WORK, (STEAL) (BUY) TAX, ENTERTAIN (SELL)
 */
public class IncomeRelation extends UnitContainerRelation
	implements LongOrderRelation
{
	/** TODO: DOCUMENT ME! */
	public final int amount;

	/**
	 * Creates a new IncomeRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param r TODO: DOCUMENT ME!
	 * @param amount TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public IncomeRelation(Unit s, Region r, int amount, int line) {
		super(s, r, line);
		this.amount = amount;
	}
}
