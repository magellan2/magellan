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

package com.eressea;

import com.eressea.rules.ItemType;

/**
 * A class representing the price of a luxury good as they are offered in any region.
 */
public class LuxuryPrice {
	private final int price;
	private ItemType itemType;

	/**
	 * Creates a new LuxuryPrice object with the specified luxury good and price.
	 *
	 * @param itemType TODO: DOCUMENT ME!
	 * @param price TODO: DOCUMENT ME!
	 */
	public LuxuryPrice(ItemType itemType, int price) {
		this.price = price;
		this.itemType = itemType;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType() {
		return itemType;
	}
}
