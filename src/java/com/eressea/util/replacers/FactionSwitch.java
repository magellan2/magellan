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

/*
 * FactionSwitch.java
 *
 * Created on 30. Dezember 2001, 15:55
 */
package com.eressea.util.replacers;

import java.util.Map;
import java.util.StringTokenizer;

import com.eressea.util.CollectionFactory;
import com.eressea.util.filters.UnitFactionFilter;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class FactionSwitch extends AbstractParameterReplacer implements EnvironmentDependent,
																		SwitchOnly
{
	protected ReplacerEnvironment environment;

	/**
	 * Creates new FactionSwitch
	 */
	public FactionSwitch() {
		super(1);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this, "description");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param src TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getReplacement(Object src) {
		try {
			String fName = getParameter(0, src).toString();

			if(!fName.equals(CLEAR)) {
				if(fName.indexOf(',') != -1) {
					StringTokenizer st = new StringTokenizer(fName, ",");

					while(st.hasMoreTokens()) {
						UnitFactionFilter filter = new UnitFactionFilter(st.nextToken());
						((UnitSelection) environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).addFilter(filter);
					}
				} else {
					UnitFactionFilter filter = new UnitFactionFilter(fName);
					((UnitSelection) environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).addFilter(filter);
				}
			} else {
				((UnitSelection) environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).removeFilters(UnitFactionFilter.class);
			}
		} catch(NullPointerException npe) {
		}

		return BLANK;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param env TODO: DOCUMENT ME!
	 */
	public void setEnvironment(ReplacerEnvironment env) {
		environment = env;
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
									"Begrenzt Einheitenersetzer auf die als Argument angegebene(n) Partei(en). Es k\u00F6nnen mehrere Einschr\u00E4nkungen hintereinander ausgef\u00FChrt werden. Die Einschr\u00E4nkungen werden durch Angabe von \"clear\" als Argument vollst\u00E4ndig aufgehoben.");
		}

		return defaultTranslations;
	}
}
