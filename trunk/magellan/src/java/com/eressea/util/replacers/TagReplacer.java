/*
 * TagReplacer.java
 *
 * Created on 6. Juni 2002, 18:41
 */

package com.eressea.util.replacers;


import java.util.Map;

import com.eressea.util.CollectionFactory;
import com.eressea.util.Taggable;

/**
 *
 * @author  Andreas
 * @version
 */
public class TagReplacer extends AbstractParameterReplacer {
	
	protected boolean mode;
	
	/** Creates new TagReplacer */
	public TagReplacer(boolean mode) {
		super(1);
		this.mode = mode;
	}
	
	public Object getReplacement(Object o) {
		if (o instanceof Taggable) {
			Object obj = getParameter(0, o);
			if (obj != null) {
				Taggable t = (Taggable)o;
				String s = obj.toString();
				if (t.containsTag(s)) {
					return t.getTag(s);
				}
				if (mode) {
					return BLANK;
				}				
			}
		}
		return null; // no parameter or not an object with tags is always error
	}
	
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description."+mode);
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
			defaultTranslations.put("description.false" , "Gibt den als Argument angegebenen externen Tag zur\u00FCck. Liefert den Wert f\u00FCr \"unbekannt\", falls kein solcher Tag existiert.");
			defaultTranslations.put("description.true" , "Gibt den als Argument angegebenen externen Tag zur\u00FCck. Liefert einen leeren String (\"\"), falls der Tag nicht existiert.");
		}
		return defaultTranslations;
	}


}
