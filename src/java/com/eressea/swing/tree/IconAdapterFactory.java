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

package com.eressea.swing.tree;

import java.util.List;

import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;

/**
 * DOCUMENT ME!
 *
 * @author SirBacon
 */
public class IconAdapterFactory implements PreferencesFactory {
	List nodeWrapperFactories;

	/**
	 * Creates a new instance of EresseaClass
	 *
	 * @param nw TODO: DOCUMENT ME!
	 */
	public IconAdapterFactory(List nw) {
		nodeWrapperFactories = nw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter createPreferencesAdapter() {
		return new IconAdapter(nodeWrapperFactories);
	}
}
