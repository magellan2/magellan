// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.resource;

import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;

import java.util.Properties;

/**
 *
 * @author  SirBacon
 */
public class ResourceSettingsFactory implements PreferencesFactory {
	
	Properties settings;
	
	/** Creates a new instance of EresseaClass */
	public ResourceSettingsFactory(Properties settings) {
		this.settings = settings;
	}
	
	public PreferencesAdapter createPreferencesAdapter() {
		return new ResourceSettings(settings);
	}
	
}
