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

package com.eressea.util.replacers;

import java.util.Map;

import com.eressea.Region;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author unknown
 * @version
 */
public class MaxTradeReplacer extends AbstractRegionReplacer {
	protected static final Integer ZERO = new Integer(0);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getRegionReplacement(Region r) {
		if(r.maxLuxuries() >= 0) {
			if(r.maxLuxuries() == 0) {
				return ZERO;
			}

			return new Integer(r.maxLuxuries());
		}

		return null;
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
			defaultTranslations.put("description", "Return the maximum trade amount of the region.");
		}

		return defaultTranslations;
	}
}
