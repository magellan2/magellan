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

import com.eressea.util.CollectionFactory;

/**
 * A Switch that compares the following to elements by their String
 * replacement. Possible replacers are evaluated by forwarding the Switch
 * object and iterator. If these two are not evaluatable(list too short) or
 * only on of them is <i>null</i> the Switch stays active. <i>Note that if
 * both are null the switch is inactive!</i>
 *
 * @author Andreas
 * @version
 */
public class StringIndexReplacer extends AbstractParameterSwitch {
	/**
	 * If the String comparism should be done with regarding to the case this
	 * property is <i>false</i>, else <i>true</i>.
	 */
	protected boolean ignoreCase = false;

	/**
	 * Constructs a default String Compare Switch that is case-sensitive.
	 */
	public StringIndexReplacer() {
		this(false);
	}

	/**
	 * Constructs a String Compare Switch with the given sensibility for case.
	 *
	 * @param iCase TODO: DOCUMENT ME!
	 */
	public StringIndexReplacer(boolean iCase) {
		super(2);
		ignoreCase = iCase;
	}

	/**
	 * Checks the following two elements and evaluates their replacements. They
	 * are treated as Strings through <i>toString()</i> and compared for
	 * equality.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isSwitchingObject(Object o) {
		Object o1 = getParameter(0, o);
		Object o2 = getParameter(1, o);

		if((o1 != null) && (o2 != null)) {
			int i = -1;

			if(ignoreCase) {
				i = o1.toString().toUpperCase().indexOf(o2.toString()
														  .toUpperCase());
			} else {
				i = o1.toString().indexOf(o2.toString());
			}

			return i >= 0;
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,
															"description" +
															ignoreCase);
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
			defaultTranslations.put("description.false",
									"Pr\u00FCft, ob das zweite Argument(als String) im ersten(als String) vorkommt. Es wird Gro\u00DF-/Kleinschreibung beachtet.");
			defaultTranslations.put("description.true",
									"Pr\u00FCft, ob das zweite Argument(als String) im ersten(als String) vorkommt. Gro\u00DF-/Kleinschreibung wird ignoriert.");
		}

		return defaultTranslations;
	}
}
