// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import com.eressea.ID;
import com.eressea.NamedObject;

/** 
 * A Resource reflects a needed resource for 
 * e.g. building roads (stones, building)
 * e.g. a building (stones, wood, iron, silver
 * e.g. recruiting (silver, potion?)
 */
public class Resource {
	private int amount = 1;
	private ObjectType type = null;

	public Resource() {
	}

	public Resource(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}

	public ObjectType getObjectType() {
		return type;
	}
	public void setObjectType(ObjectType type) {
		this.type = type;
	}

	public String toString() {
		return "Resource: "+amount+" "+type;
	}
}
