// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

import java.util.Locale;
import java.util.Map;

import com.eressea.util.CollectionFactory;



/**
 * An implementation of the <tt>GameData</tt> supporting all of the
 * attributes defined there. No maps are defined as <tt>null</tt>.
 *
 * @see com.eressea.GameData
 */
public class CompleteData extends GameData {
	protected Map regions      = CollectionFactory.createOrderedHashtable();
	protected Map units        = CollectionFactory.createHashtable();
	protected Map factions     = CollectionFactory.createOrderedHashtable();
	protected Map ships        = CollectionFactory.createOrderedHashtable();
	protected Map buildings    = CollectionFactory.createOrderedHashtable();
	protected Map islands      = CollectionFactory.createOrderedHashtable();
	protected Map msgTypes     = CollectionFactory.createOrderedHashtable();
	protected Map spells       = CollectionFactory.createOrderedHashtable();
	protected Map potions      = CollectionFactory.createOrderedHashtable();
	protected Map hotSpots     = CollectionFactory.createOrderedHashtable();
	protected Map translations = CollectionFactory.createOrderedHashtable();
	protected Locale locale = null;

	public Map islands() {
		return islands;
	}

	public Map regions() {
		return regions;
	}

	public Map units() {
		return units;
	}

	public Map factions() {
		return factions;
	}

	public Map ships() {
		return ships;
	}

	public Map buildings() {
		return buildings;
	}

	public Map msgTypes() {
		return msgTypes;
	}

	public Map spells() {
		return spells;
	}

	public Map potions() {
		return potions;
	}
	
	public Map hotSpots() {
		return hotSpots;
	}

	public Map translations() {
		return translations;
	}
	
	public void setLocale(Locale l) {
		this.locale = l;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public CompleteData(Rules rules) {
		super(rules);
	}
	
	public CompleteData(Rules rules, String name) {
		super(rules, name);
	}
}
