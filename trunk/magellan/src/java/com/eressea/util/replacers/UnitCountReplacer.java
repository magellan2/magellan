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
 * UnitCountReplacer.java
 *
 * Created on 29. Dezember 2001, 15:47
 */
package com.eressea.util.replacers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class UnitCountReplacer extends AbstractRegionReplacer implements EnvironmentDependent {
	private static final Integer ZERO = new Integer(0);
	protected ReplacerEnvironment environment;
	protected boolean countPersons;

	/**
	 * Creates a new UnitCountReplacer object.
	 */
	public UnitCountReplacer() {
		this(true);
	}

	/**
	 * Creates a new UnitCountReplacer object.
	 *
	 * @param countPersons TODO: DOCUMENT ME!
	 */
	public UnitCountReplacer(boolean countPersons) {
		this.countPersons = countPersons;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getRegionReplacement(Region r) {
		Collection c = ((UnitSelection) environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).getUnits(r);

		if(c != null) {
			int count = 0;

			if(countPersons) {
				Iterator it = c.iterator();

				while(it.hasNext()) {
					count += ((Unit) it.next()).persons;
				}
			} else {
				count = c.size();
			}

			if(count > 0) {
				return new Integer(count);
			}

			return ZERO;
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param env TODO: DOCUMENT ME!
	 */
	public void setEnvironment(ReplacerEnvironment env) {
		environment = env;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this, "description." + countPersons);
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
			defaultTranslations.put("description.true",
									"Z\u00E4hlt alle Personen in der Region. Die Einheiten k\u00F6nnen \u00FCber Einheitenfilter eingeschr\u00E4nkt werden.");
			defaultTranslations.put("description.false",
									"Z\u00E4hlt alle Einheiten in der Region. Die Einheiten k\u00F6nnen \u00FCber Einheitenfilter eingeschr\u00E4nkt werden.");
		}

		return defaultTranslations;
	}
}
