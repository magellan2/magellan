// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


import com.eressea.rules.ItemType;

/**
 * A class representing the price of a luxury good as they are offered
 * in any region.
 */
public class LuxuryPrice {
	private final int price;
	private ItemType itemType;

	/**
	 * Creates a new LuxuryPrice object with the specified luxury good
	 * and price.
	 */
	public LuxuryPrice(ItemType itemType, int price) {
		this.price = price;
		this.itemType = itemType;
	}

	public int getPrice() {
		return price;
	}

	public ItemType getItemType() {
		return itemType;
	}
}
