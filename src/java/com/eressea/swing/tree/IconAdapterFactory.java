// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.swing.tree;

import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;

import java.util.List;

/**
 *
 * @author  SirBacon
 */
public class IconAdapterFactory implements PreferencesFactory{
	
	List nodeWrapperFactories;
	
	/** Creates a new instance of EresseaClass */
	public IconAdapterFactory(List nw) {
		nodeWrapperFactories = nw;
	}
	
	public PreferencesAdapter createPreferencesAdapter() {
		return new IconAdapter(nodeWrapperFactories);
	}		
	
}
