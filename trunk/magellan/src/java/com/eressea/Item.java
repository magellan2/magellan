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

package com.eressea;

import com.eressea.rules.ItemType;

/**
 * A class representing an item in Eressea. Items are qualified by their type
 * and a certain amount. Mark that the item class is quite 'bare', i.e. its
 * name and identifiability are not enforced  by sub-classing the respective
 * interfaces.
 */
public class Item {
	private ItemType type;
	private int		 amount;

	/**
	 * Creates a new item of the specified type and with the specified amount.
	 *
	 * @param type TODO: DOCUMENT ME!
	 * @param amount TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public Item(ItemType type, int amount) {
		if(type != null) {
			this.type   = type;
			this.amount = amount;
		} else {
			throw new IllegalArgumentException("Item.Item(): specified item type is null!");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param amount TODO: DOCUMENT ME!
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return type.toString();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType() {
		return type;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @deprecated
	 */
	public ItemType getType() {
		return getItemType();
	}

	/**
	 * This method is a shortcut for calling this.getType().getName()
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		return type.getName();
	}
}
