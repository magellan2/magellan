// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;

import com.eressea.rules.ItemCategory;

/**
 * @author Ulrich Küster
 */
 public class ItemCategoryNodeWrapper {
	 private int amount = -1;
	 private ItemCategory cat = null;

	public ItemCategoryNodeWrapper(ItemCategory category, int amount) {
		this.amount = amount;
		this.cat = category;
	}

	public void setAmount(int i) {
		amount = i;
	}

	public ItemCategory getItemCategory() {
		return cat;
	}

	public String toString() {
		if (amount == -1) {
			return cat.toString();
		} else {
			return cat.toString() + ": " + amount;
		}
	}
}