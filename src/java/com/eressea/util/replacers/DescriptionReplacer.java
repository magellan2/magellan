/*
 * TagReplacer.java
 *
 * Created on 6. Juni 2002, 18:41
 */

package com.eressea.util.replacers;

import java.util.Map;

import com.eressea.Described;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public class DescriptionReplacer implements Replacer {
	
	protected boolean mode;
	
	public DescriptionReplacer() {
		this(true);
	}
	
	/** Creates new TagReplacer */
	public DescriptionReplacer(boolean mode) {
		this.mode = mode;
	}
	
	public Object getReplacement(Object o) {
		if (o instanceof Described) {
			return ((Described)o).getDescription();
		}
		if (mode) {
			return BLANK;
		}
		return null;
	}
	
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description");
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
			defaultTranslations.put("description","Returns a description of describable objects. At the moment there are  unit, unit container, island, spell and potion. In \"normal\" use this replacer returns the description of the current region. Together with unit filters it returns the description of a unit, which can be used to filter by means of string comparision/content.");
		}
		return defaultTranslations;
	}
	
}
