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

package com.eressea.swing.map;

import com.eressea.swing.preferences.PreferencesAdapter;

/**
 * The interface required of a class used as a map renderer.
 */
public interface MapCellRenderer {
	/**
	 * Renders the supplied object.
	 */
	public void render(Object obj, boolean active, boolean selected);

	/**
	 * Returns a name for this renderer.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName();

	/**
	 * Returns the index of the default rendering plane of this renderer. See the constants
	 * specified in com.eressea.swing.map.Mapper for possible values.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex();

	/**
	 * Returns the CellGeometry object this object uses for rendering.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CellGeometry getCellGeometry();

	/**
	 * Sets the CellGeometry object this object uses for rendering.
	 */
	public void setCellGeometry(CellGeometry geo);

	/**
	 * Initializes the renderer for one rendering pass. All supplied informations stays constant
	 * during a pass and is therefore not transferred with each render() call.
	 */
	public void init(com.eressea.GameData data, java.awt.Graphics g, java.awt.Point offset);

	/**
	 * Tells the renderer that it should re-adjust the scale factor it uses for rendering.
	 */
	public void scale(float scaleFactor);

	/**
	 * Returns a component that allows to modify the preferences available for this renderer.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter getPreferencesAdapter();
}
