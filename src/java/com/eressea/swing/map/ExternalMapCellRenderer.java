// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.swing.map;

import java.util.ResourceBundle;

/**
 * An interface for plug-in renderers. It allows to connect a properties object
 * directly (not through normal getString()).
 *
 * @author  Andreas
 * @version 
 */
public interface ExternalMapCellRenderer extends MapCellRenderer {

	public void setResourceBundle(ResourceBundle rb);
}

