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
 */

package com.eressea.util.replacers;

import java.util.Map;

import com.eressea.Region;
import com.eressea.StringID;

import com.eressea.rules.RegionType;

import com.eressea.util.CollectionFactory;

/**
 * A switch that reacts if the given region is of a certain type.
 * 
 * <p>
 * Three possible constructors:
 * 
 * <ul>
 * <li>
 * RegionType object -> direct test
 * </li>
 * <li>
 * StringID -> compares given region's type ID
 * </li>
 * <li>
 * String -> creates a StringID
 * </li>
 * </ul>
 * </p>
 *
 * @author Andreas
 * @version 1.0
 */
public class RegionTypeSwitch extends AbstractRegionSwitch {
	/** For use if constructed with Region Type. */
	protected RegionType type = null;

	/** For use if constructed with String or String ID. */
	protected StringID id = null;

	/**
	 * Creates a RegionType Switch out of a String describing a Region Type.
	 *
	 * @param type TODO: DOCUMENT ME!
	 */
	public RegionTypeSwitch(String type) {
		this(StringID.create(type));
	}

	/**
	 * Creates a RegionType Switch out of a String ID describing a Region Type.
	 *
	 * @param type TODO: DOCUMENT ME!
	 */
	public RegionTypeSwitch(StringID type) {
		id = type;
	}

	/**
	 * Creates a RegionType Switch out of a Region Type.
	 *
	 * @param type TODO: DOCUMENT ME!
	 */
	public RegionTypeSwitch(RegionType type) {
		this.type = type;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType() {
		return type;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public StringID getStringID() {
		return id;
	}

	/**
	 * Compares the region's type with the ID given in the constructor.
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isSwitchingRegion(Region r) {
		boolean res = false;

		if(type != null) {
			res = type.equals(r.getType());
		} else if(id != null) {
			res = id.equals(r.getType().getID());
		}

		return res;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		String s = super.getDescription();

		if(s == null) {
			if(type != null) {
				return type.getName();
			}

			return id.toString();
		} else {
			if(type != null) {
				return s + type.getName();
			}

			return s + id.toString();
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

			// FIXME(pavkovic)
			defaultTranslations.put("description",
									"Gibt zur\u00FCck, ob die aktuelle Region vom folgenden Typ ist: ");
		}

		return defaultTranslations;
	}
}
