// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import java.util.Map;
import com.eressea.util.CollectionFactory;

import com.eressea.util.filters.UnitFactionTLFilter;
import com.eressea.util.logging.Logger;
/**
 *
 * @author  Andreas
 * @version
 */
public class TrustlevelSwitch extends AbstractParameterReplacer implements EnvironmentDependent, SwitchOnly {
	private final static Logger log = Logger.getInstance(TrustlevelSwitch.class);
	
	protected ReplacerEnvironment environment;
	
	/** Creates new FactionSwitch */
	public TrustlevelSwitch(int mode) {
		super(mode == 0?1:2);
	}
	
	public Object getReplacement(Object src) {
		try{
			Object param1 = getParameter(0, src);
			Object param2 = null;
			int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;
			if (getParameterCount() > 1) {
				param2 = getParameter(1, src);
				max = Integer.parseInt(param2.toString());
			}
			try{
				min = Integer.parseInt(param1.toString());
			}catch(NumberFormatException nfe) {
				if (param1.toString().equals(CLEAR)) {
					((UnitSelection)environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).removeFilters(UnitFactionTLFilter.class);
					return BLANK;
				}
			}
			((UnitSelection)environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).addFilter(new UnitFactionTLFilter(min, max));
		}catch(RuntimeException npe) {}
		return BLANK;
	}
	
	public void setEnvironment(ReplacerEnvironment env) {
		environment = env;
	}
	
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description."+(getParameterCount()-1));
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
			defaultTranslations.put("description.0" , "Schr\u00E4nkt Einheitenersetzer auf Einheiten von Parteien ein, die mindestens den angegebenen Vertrauenslevel haben. Es wirkt sich insgesamt der niedrigste Schalter aus. Der Schalter kann durch die Angabe von \"clear\" wieder vollst\u00E4ndig zur\u00FCckgesetzt werden. Dies wirkt sich auch auf die erweiterte Version aus.");
			defaultTranslations.put("description.1" , "Schr\u00E4nkt Einheitenersetzer auf Einheiten von Parteien ein, die mindestens den angegebenen Vertrauenslevel 1 (Argument 1) und h\u00F6chstens den angegebenen Vertrauenslevel 2 (Argument 2) haben. Es wirkt sich insgesamt der Durchschnitt der Schalter aus. Der Schalter kann durch die Angabe von \"clear\" wieder vollst\u00E4ndig zur\u00FCckgesetzt werden. Dies wirkt sich auch auf die einfache Version aus.");
		}
		return defaultTranslations;
	}

}
