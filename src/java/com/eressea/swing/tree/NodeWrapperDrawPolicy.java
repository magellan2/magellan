/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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

import com.eressea.swing.preferences.PreferencesFactory;

/**
 * DOCUMENT ME!
 *
 * @author SirBacon
 */
public interface NodeWrapperDrawPolicy extends PreferencesFactory {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param co TODO: DOCUMENT ME!
	 */
	public void addCellObject(CellObject co);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTitle();
}
