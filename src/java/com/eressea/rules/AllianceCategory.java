// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import com.eressea.ID;

public class AllianceCategory extends ObjectType {
	private int bitMask = -1;
	
	public AllianceCategory(ID id) {
		super(id);
	}

	/** copy constructor */
	public AllianceCategory(AllianceCategory orig) {
		super(orig.getID());
		bitMask = orig.bitMask;
	}

	public void setBitMask(int mask) {
		this.bitMask = mask;
	}
	
	public int getBitMask() {
		return this.bitMask;
	}
	
	public boolean equals(Object o) {
		return o==this || (o instanceof AllianceCategory) && ((AllianceCategory)o).getID().equals(this.getID());
	}
	
	public int compareTo(Object o) {
		int anotherBitMask = ((AllianceCategory) o).bitMask;
		return bitMask<anotherBitMask ? -1 : (bitMask==anotherBitMask ? 0 : 1);
	}

	public String toString() {
		return "AllianceCategory[name="+name+", bitMask="+bitMask+"]";
	}
	
}
