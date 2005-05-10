/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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

import java.util.Collections;
import java.util.Map;

import com.eressea.util.CollectionFactory;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.Translations;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TempUnit extends Unit {
	/** If this is a temp unit the parent is the unit that created this temp unit. */
	private Unit parent = null;

	/**
	 * Creates a new TempUnit object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	public TempUnit(ID id, Unit parent) {
		super(id);
		this.parent = parent;

		// pavkovic 2003.12.04: TempUnits have empty orders by default
		this.setOrders(Collections.singleton(""), false);
		this.clearOrders();
	}

	/**
	 * Assigns this temp unit a parent unit.
	 *
	 * @param u TODO: DOCUMENT ME!
	 */
	public void setParent(Unit u) {
		this.parent = u;
	}

	/**
	 * Returns the parent of this temp unit. If this is not a temp unit, null is returned.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getParent() {
		return parent;
	}

	/**
	 * Returns a string representation of this temporary unit.
	 *
	 * @return TODO: DOCUMENT ME!
	 */

    public String toString(boolean withName) {
        if(withName) {
            return super.toString(withName);
        } else {
            String temp = Translations.getOrderTranslation(EresseaOrderConstants.O_TEMP);
			return temp+ " "+id.toString();
		}
	}

	/**
	 * Merges two temp units.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curTemp TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newTemp TODO: DOCUMENT ME!
	 * @param sameRound notifies if both game data objects have been from the same round
	 */
	public static void merge(GameData curGD, TempUnit curTemp, GameData newGD, TempUnit newTemp,
							 boolean sameRound) {
		Unit.merge(curGD, curTemp, newGD, newTemp, sameRound);

		if(curTemp.getParent() != null) {
			newTemp.setParent(newGD.getUnit(curTemp.getParent().getID()));
		}
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("nameprefix", "Temp unit");
		}

		return defaultTranslations;
	}
}
