// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.relation;


import com.eressea.Unit;
import com.eressea.rules.ItemType;

/**
 * A relation indicating that a unit transfers a certain amount of an
 * item to another unit.
 */
public class ItemTransferRelation extends TransferRelation {
	public ItemType itemType;

	public ItemTransferRelation(Unit s, Unit t, int a, ItemType i, int line) {
		super(s, t, a, line);
		this.itemType = i;
	}

	public String toString() {
		return super.toString()+"@ITEMTYPE="+itemType;
	}
}
