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

package com.eressea.swing.map;

import java.util.ResourceBundle;

/**
 * An interface for plug-in renderers. It allows to connect a properties object directly (not
 * through normal getString()).
 *
 * @author Andreas
 * @version
 */
public interface ExternalMapCellRenderer extends MapCellRenderer {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rb TODO: DOCUMENT ME!
	 */
	public void setResourceBundle(ResourceBundle rb);
}
