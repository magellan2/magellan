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

public class OptionCategory extends NamedObject {
	private int bitMask = 0;
	private boolean isActive = false;
	private boolean isOrder = false;
	
	public OptionCategory(ID id) {
		super(id);
	}

	/** copy constructor */
	public OptionCategory(OptionCategory orig) {
		super(orig.getID());
		bitMask = orig.bitMask;
		isActive = orig.isActive;
		isOrder = orig.isOrder;
	}

	public void setBitMask(int mask) {
		this.bitMask = mask;
	}
	
	public int getBitMask() {
		return this.bitMask;
	}
	
	public void setActive(boolean bool) {
		this.isActive = bool;
	}
	
	public boolean isActive() {
		return this.isActive;
	}
	
	public void setOrder(boolean bool) {
		this.isOrder = bool;
	}
	
	public boolean isOrder() {
		return this.isOrder;
	}
	
	public boolean equals(Object o) {
		return (o instanceof OptionCategory) && ((OptionCategory)o).getID().equals(this.getID());
	}
	
	public int compareTo(Object o) {
		return this.getID().compareTo(((OptionCategory)o).getID());
	}
}
