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
 * $Id$
 */

package com.eressea.swing.preferences;

import java.awt.Component;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface PreferencesAdapter {
	/**
	 * This function is called for applying the preferences.
	 */
	public void applyPreferences();

	/**
	 * This function delivers the gui for the preferences adapter.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getComponent();

	/**
	 * This function delivers the visible name of the preferences adapter.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTitle();
}
