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

package com.eressea.swing.tree;

import com.eressea.rules.ItemCategory;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster
 */
public class ItemCategoryNodeWrapper {
	private int			 amount = -1;
	private ItemCategory cat = null;

	/**
	 * Creates a new ItemCategoryNodeWrapper object.
	 *
	 * @param category TODO: DOCUMENT ME!
	 * @param amount TODO: DOCUMENT ME!
	 */
	public ItemCategoryNodeWrapper(ItemCategory category, int amount) {
		this.amount = amount;
		this.cat    = category;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void setAmount(int i) {
		amount = i;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory() {
		return cat;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		if(amount == -1) {
			return cat.toString();
		} else {
			return cat.toString() + ": " + amount;
		}
	}
}
