// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;
import java.util.Map;

import com.eressea.util.CollectionFactory;

/**
 * A Switch that compares the following to elements by their String replacement.
 * Possible replacers are evaluated by forwarding the Switch object and iterator.
 * If these two are not evaluatable(list too short) or only on of them is
 * <i>null</i> the Switch stays active. <i>Note that if both are null the
 * switch is inactive!</i>
 *
 * @author  Andreas
 * @version 
 */
public class StringEqualReplacer extends AbstractParameterSwitch{

	/**
	 * If the String comparism should be done with regarding to the case this
	 * property is <i>false</i>, else <i>true</i>.
	 */
	protected boolean ignoreCase=false;
	
	/**
	 * Constructs a default String Compare Switch that is case-sensitive.
	 */
	public StringEqualReplacer() {
		this(false);
	}
	
	/**
	 * Constructs a String Compare Switch with the given sensibility for case.
	 */
	public StringEqualReplacer(boolean iCase) {
		super(2);
		ignoreCase=iCase;
	}
	
	/**
	 * Checks the following two elements and evaluates their replacements. They
	 * are treated as Strings through <i>toString()</i> and compared for equality.
	 */
	public boolean isSwitchingObject(Object o) {
		Object o1 = getParameter(0, o), o2 = getParameter(1, o);
		if (o1 != null && o2 != null) {
			if (ignoreCase) {
				return o1.toString().equalsIgnoreCase(o2.toString());
			}
			return o1.toString().equals(o2.toString());
		}
		return false;
	}
	
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description"+ignoreCase);
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
			defaultTranslations.put("description.2" , "Gibt die ersten zwei Buchstaben des in der Region verkauften Luxusguts zur\u00FCck.");
			defaultTranslations.put("description.3" , "Gibt den Preis des verkauften Luxusguts in der aktuellen Region zur\u00FCck(als positiven Wert).");
			defaultTranslations.put("description.1" , "Gibt den ersten Buchstaben des in der Region verkauften Luxusguts zur\u00FCck.");
			defaultTranslations.put("description.0" , "Gibt den vollst\u00E4ndigen Namen des in der Region verkauften Luxusguts zur\u00FCck.");
		}
		return defaultTranslations;
	}

}
