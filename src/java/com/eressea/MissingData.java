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

import java.util.Locale;
import java.util.Map;

import com.eressea.rules.GenericRules;

import com.eressea.util.CollectionFactory;

/**
 * A Zero Pattern implementation of the <tt>GameData</tt> supporting all of the
 * attributes defined there. No maps are defined as <tt>null</tt>.
 *
 * @see com.eressea.GameData
 */
public class MissingData extends GameData {
	protected Map    regions	  = CollectionFactory.createOrderedHashtable();
	protected Map    units		  = CollectionFactory.createHashtable();
	protected Map    factions     = CollectionFactory.createOrderedHashtable();
	protected Map    ships		  = CollectionFactory.createOrderedHashtable();
	protected Map    buildings    = CollectionFactory.createOrderedHashtable();
	protected Map    islands	  = CollectionFactory.createOrderedHashtable();
	protected Map    msgTypes     = CollectionFactory.createOrderedHashtable();
	protected Map    spells		  = CollectionFactory.createOrderedHashtable();
	protected Map    potions	  = CollectionFactory.createOrderedHashtable();
	protected Map    hotSpots     = CollectionFactory.createOrderedHashtable();
	protected Map    translations = CollectionFactory.createOrderedHashtable();
	protected Locale locale		  = Locale.getDefault();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map islands() {
		return islands;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map regions() {
		return regions;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map units() {
		return units;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map factions() {
		return factions;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map ships() {
		return ships;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map buildings() {
		return buildings;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map msgTypes() {
		return msgTypes;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map spells() {
		return spells;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map potions() {
		return potions;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map hotSpots() {
		return hotSpots;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map translations() {
		return translations;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setLocale(Locale l) {
		this.locale = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Creates a new MissingData object.
	 */
	public MissingData() {
		super(new GenericRules(), "void");
	}
}
