// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.Item;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.rules.ItemType;
import com.eressea.util.CollectionFactory;

/**
 * Replaces a item name string with the number of that item in the given region.
 *
 * @author  unknown
 * @version
 */
public class ItemTypeReplacer extends AbstractParameterReplacer implements EnvironmentDependent {
	
	private final static Integer ZERO = new Integer(0);
	
	protected ReplacerEnvironment environment;
	
	public ItemTypeReplacer() {
		super(1);
	}
	
	public Object getReplacement(Object o) {
		if (o instanceof Region) {
			String items=getParameter(0, o).toString();
			int count=0;
			Collection c=((UnitSelection)environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).getUnits((Region)o);
			if (c==null)
				return null;
			Iterator it=c.iterator();
			while(it.hasNext()) {
				Unit u=(Unit)it.next();
				Iterator it2=u.getItems().iterator();
				while(it2.hasNext()) {
					Item i=(Item)it2.next();
					ItemType ity=i.getItemType();
					if (ity.getName().equalsIgnoreCase(items) || ity.getID().toString().equalsIgnoreCase(items)) {
						count+=i.getAmount();
						break;
					}
				}
			}
			if (count != 0) {
				return new Integer(count);
			}
			return ZERO;
		}
		return null;
	}
	
	public void setEnvironment(ReplacerEnvironment env) {
		environment = env;
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
			defaultTranslations.put("description","Searches all units in the region for an item given as argument 1 and returns the amount of this item. Restriction of the unit set can be done by means of unit filters..");
		}
		return defaultTranslations;
	}

}
