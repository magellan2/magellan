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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import java.util.Map;
import java.util.Properties;

import com.eressea.GameData;

import com.eressea.swing.preferences.PreferencesAdapter;

import com.eressea.util.CollectionFactory;

/**
 * A class providing implementations of several methods required by a renderer
 * that renders hexagonal cells.
 */
public abstract class HexCellRenderer implements MapCellRenderer {
	/** The cell geometry used for rendering. */
	protected CellGeometry cellGeo = null;

	/**
	 * A Properties object used to retrieve and store preferences of this
	 * renderer.
	 */
	protected Properties settings = null;

	/**
	 * The game data this renderer may use for additional information on what
	 * and how to render.
	 */
	protected GameData data = null;

	/** The graphics object set by init() to draw on in the render() method. */
	protected Graphics graphics = null;

	/**
	 * The pixel offset used to compensate the difference of axis origins
	 * between the graphics object and the map.
	 */
	protected Point offset = null;

	/**
	 * A constructor assigning a cell geometry and settings.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public HexCellRenderer(CellGeometry geo, Properties settings) {
		cellGeo		  = geo;
		this.settings = settings;
	}

	/**
	 * Initializes a rendering pass with the specified graphics object g used
	 * to paint on in subsequent calls to render() and the offset where to
	 * draw region 0, 0 on the graphics object.
	 *
	 * @param data TODO: DOCUMENT ME!
	 * @param g TODO: DOCUMENT ME!
	 * @param offset TODO: DOCUMENT ME!
	 */
	public void init(GameData data, Graphics g, Point offset) {
		this.data   = data;
		graphics    = g;
		this.offset = offset;
	}

	/**
	 * Renders the supplied object.
	 */
	public abstract void render(Object obj, boolean active, boolean selected);

	/**
	 * Returns a name for this renderer. By default, the key "name" is looked
	 * up in the component dictionary and returned.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 * Returns the default rendering plane of the renderer. See the constants
	 * specified in  com.eressea.swing.map.Mapper for possible values.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract int getPlaneIndex();

	/**
	 * Returns a default preferences adapter telling the user that this
	 * renderer does not have modifiable preferences.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter getPreferencesAdapter() {
		return new DefaultRendererPreferencesAdapter(this);
	}

	/**
	 * Scales the used cell geometry object.
	 *
	 * @param scaleFactor TODO: DOCUMENT ME!
	 */
	public void scale(float scaleFactor) {
		cellGeo.setScaleFactor(scaleFactor);
	}

	/**
	 * Returns the used cell geometry object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CellGeometry getCellGeometry() {
		return cellGeo;
	}

	/**
	 * Sets the used cell geometry object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 */
	public void setCellGeometry(CellGeometry geo) {
		this.cellGeo = geo;
	}

	/**
	 * Returns a String representation of the renderer.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Returns a translation from the translation table for the specified key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this, key);
	}

	/**
	 * The default preferences adapter telling the user that there are no
	 * modifiable preferences.
	 */
	protected class DefaultRendererPreferencesAdapter
		implements PreferencesAdapter
	{
		protected MapCellRenderer source = null;

		/**
		 * Creates a new DefaultRendererPreferencesAdapter object.
		 *
		 * @param source TODO: DOCUMENT ME!
		 */
		public DefaultRendererPreferencesAdapter(MapCellRenderer source) {
			this.source = source;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Component getComponent() {
			return new javax.swing.JLabel(com.eressea.util.Translations.getTranslation(HexCellRenderer.class,
																					   "lbl.nooptions.caption"));
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getTitle() {
			return getName();
		}
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("lbl.nooptions.caption",
									"There are no options available for this renderer.");
		}

		return defaultTranslations;
	}
}
