// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import java.util.Iterator;
import java.util.Map;

import com.eressea.LuxuryPrice;
import com.eressea.Region;
import com.eressea.rules.ItemType;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  unknown
 * @version
 */
public class LuxuryPriceReplacer extends AbstractParameterReplacer {
	
	public LuxuryPriceReplacer() {
		super(1);
	}
	
	public Object getReplacement(Object o) {
		if (o instanceof Region) {
			Region r = (Region)o;
			if (r.prices==null)
				return null;
			String luxury = getParameter(0, o).toString();
			Iterator it=r.prices.values().iterator();
			while(it.hasNext()) {
				LuxuryPrice lp=(LuxuryPrice)it.next();
				ItemType ity=lp.getItemType();
				if (ity.getName().equals(luxury) || ity.getID().toString().equals(luxury))
					return new Integer(lp.getPrice());
			}
		}
		return null;
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
			// FIXME(pavkovic)
			defaultTranslations.put("description","Sucht nach dem Preis der Luxusware, die durch den nachfolgenden Text bestimmt ist. F\u00FCr das in der Region verkaufte Gut wird ein negativer Wert ausgegeben.");
		}
		return defaultTranslations;
	}

}
