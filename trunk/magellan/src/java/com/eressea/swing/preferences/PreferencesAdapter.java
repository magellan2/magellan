// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.preferences;


import java.awt.Component;

public interface PreferencesAdapter {

	/** 
	 * This function is called for applying the preferences.
	 */
	public void      applyPreferences();

	/** 
	 * This function delivers the gui for the preferences adapter.
	 */
	public Component getComponent();

	/**
	 * This function delivers the visible name of the preferences adapter.
	 */
	public String    getTitle();

}
