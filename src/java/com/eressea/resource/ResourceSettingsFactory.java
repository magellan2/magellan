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

package com.eressea.resource;

import java.util.Properties;

import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;

/**
 * DOCUMENT ME!
 *
 * @author SirBacon
 */
public class ResourceSettingsFactory implements PreferencesFactory {
	Properties settings;

	/**
	 * Creates a new instance of EresseaClass
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public ResourceSettingsFactory(Properties settings) {
		this.settings = settings;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter createPreferencesAdapter() {
		return new ResourceSettings(settings);
	}
}
