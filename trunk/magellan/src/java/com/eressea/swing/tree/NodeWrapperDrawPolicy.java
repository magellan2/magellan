// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.swing.tree;

import com.eressea.swing.preferences.PreferencesFactory;

/**
 *
 * @author  SirBacon
 */
public interface NodeWrapperDrawPolicy extends PreferencesFactory {
	
	public void addCellObject(CellObject co);

	public String getTitle();
}
