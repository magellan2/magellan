// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

import java.util.Map;
import com.eressea.util.CollectionFactory;

public class TempUnit extends Unit {
	/**
	 * If this is a temp unit the parent is the unit that created
	 * this temp unit.
	 */
	private Unit parent = null;
	
	public TempUnit(ID id, Unit parent) {
		super(id);
		this.parent = parent;
	}
	
	/**
	 * Assigns this temp unit a parent unit.
	 */
	public void setParent(Unit u) {
		this.parent = u;
	}
	
	/**
	 * Returns the parent of this temp unit. If this is not a temp
	 * unit, null is returned.
	 */
	public Unit getParent() {
		return parent;
	}
	
	/**
	 * Returns a string representation of this temporary unit.
	 */
	public String toString() {
		if (this.name != null) {
			return this.name + " (TEMP " + this.id.toString() + ")";
		} else {
			return "TEMP " + this.id.toString();
		}
	}
	
	/**
	 * Merges two temp units.
	 */
	public static void merge(GameData curGD, TempUnit curTemp, GameData newGD, TempUnit newTemp) {
		Unit.merge(curGD, curTemp, newGD, newTemp);
		if (curTemp.getParent() != null) {
			newTemp.setParent(newGD.getUnit(curTemp.getParent().getID()));
		}
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("nameprefix","Temp unit");
		}
		return defaultTranslations;
	}


}
