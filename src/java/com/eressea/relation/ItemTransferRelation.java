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

import com.eressea.Unit;

import com.eressea.rules.ItemType;

/**
 * A relation indicating that a unit transfers a certain amount of an item to
 * another unit.
 */
public class ItemTransferRelation extends TransferRelation {
	/** TODO: DOCUMENT ME! */
	public ItemType itemType;

	/**
	 * Creates a new ItemTransferRelation object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param t TODO: DOCUMENT ME!
	 * @param a TODO: DOCUMENT ME!
	 * @param i TODO: DOCUMENT ME!
	 * @param line TODO: DOCUMENT ME!
	 */
	public ItemTransferRelation(Unit s, Unit t, int a, ItemType i, int line) {
		super(s, t, a, line);
		this.itemType = i;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return super.toString() + "@ITEMTYPE=" + itemType;
	}
}
