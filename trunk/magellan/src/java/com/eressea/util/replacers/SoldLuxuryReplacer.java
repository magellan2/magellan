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

/*
 * SoldLuxuryReplacer.java
 *
 * Created on 29. Dezember 2001, 16:12
 */
package com.eressea.util.replacers;

import java.util.Iterator;
import java.util.Map;

import com.eressea.LuxuryPrice;
import com.eressea.Region;
import com.eressea.StringID;

import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class SoldLuxuryReplacer extends AbstractRegionReplacer {
	protected int mode;

	/**
	 * Creates new SoldLuxuryReplacer
	 *
	 * @param mode TODO: DOCUMENT ME!
	 */
	public SoldLuxuryReplacer(int mode) {
		this.mode = mode;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getRegionReplacement(Region r) {
		if(r.prices != null) {
			Iterator it = r.prices.keySet().iterator();

			while(it.hasNext()) {
				StringID    id = (StringID) it.next();
				LuxuryPrice lp = (LuxuryPrice) r.prices.get(id);

				if(lp.getPrice() < 0) {
					switch(mode) {
					case 0:
						return id.toString();

					case 1:
						return new String(lp.getItemType().getName()
											.toCharArray(), 0, 1);

					case 2:
						return new String(lp.getItemType().getName()
											.toCharArray(), 0, 2);

					case 3:
						return new Integer(-lp.getPrice());
					}
				}
			}
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,
															"description." +
															mode);
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
			defaultTranslations.put("description.2",
									"Returns the first two letters of the luxury item which may be sold in the region.");
			defaultTranslations.put("description.3",
									"Returns the price of the luxury item which may be sold in the region (as positive value)");
			defaultTranslations.put("description.1",
									"Returns the first letter of the luxury item which may be sold in the region.");
			defaultTranslations.put("description.0",
									"Returns the full name of the luxury item which may be sold in the region.");
		}

		return defaultTranslations;
	}
}
