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
import java.awt.Image;
import java.awt.Rectangle;

import java.util.Map;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.eressea.Coordinate;
import com.eressea.Region;

import com.eressea.rules.UnitContainerType;

import com.eressea.swing.context.ContextChangeable;
import com.eressea.swing.context.ContextObserver;
import com.eressea.swing.preferences.PreferencesAdapter;

import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class RegionImageCellRenderer extends ImageCellRenderer
	implements ContextChangeable
{
	/** TODO: DOCUMENT ME! */
	public static final String  MAP_TAG  = "mapicon";
	private static final Logger log		 = Logger.getInstance(RegionImageCellRenderer.class);
	private boolean			    fogOfWar = true;
	protected JCheckBoxMenuItem item     = null;
	protected ContextObserver   obs		 = null;

	/**
	 * Creates a new RegionImageCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public RegionImageCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
		fogOfWar = (new Boolean(settings.getProperty("RegionImageCellRenderer.fogOfWar",
													 Boolean.TRUE.toString()))).booleanValue();
		item = new JCheckBoxMenuItem(getString("chk.showfow.caption"), fogOfWar);
		item.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					fogOfWar = item.isSelected();
					getSettings().setProperty("RegionImageCellRenderer.fogOfWar",
											  (new Boolean(fogOfWar)).toString());

					if(obs != null) {
						obs.contextDataChanged();
					}
				}
			});
	}

	protected Properties getSettings() {
		return settings;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 * @param active TODO: DOCUMENT ME!
	 * @param selected TODO: DOCUMENT ME!
	 */
	public void render(Object obj, boolean active, boolean selected) {
		if(obj instanceof Region) {
			Region     r = (Region) obj;
			Coordinate c = r.getCoordinate();

			Rectangle  rect = cellGeo.getImageRect(c.x, c.y);
			rect.translate(-offset.x, -offset.y);

			if(r.containsTag(MAP_TAG)) {
				Image img = getImage(r.getTag(MAP_TAG));

				if(img != null) {
					drawImage(r, img, rect);

					return;
				}
			}

			UnitContainerType type = r.getType();

			if(type != null) {
				drawImage(r, getImage(type.getID().toString()), rect);
			}
		}
	}

	protected void drawImage(Region r, Image img, Rectangle rect) {
		if(img != null) {
			graphics.drawImage(img, rect.x, rect.y, rect.width, rect.height,
							   null);
		} else {
			log.warn("RegionImageCellRenderer.render(): image is null");
		}

		if(fogOfWar) {
			Image fogImg = getImage("Nebel");

			if((fogImg != null) && r.fogOfWar()) {
				graphics.drawImage(fogImg, rect.x, rect.y, rect.width,
								   rect.height, null);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_REGION;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getFogOfWar() {
		return fogOfWar;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setFogOfWar(boolean bool) {
		fogOfWar = bool;
		settings.setProperty("RegionImageCellRenderer.fogOfWar",
							 (new Boolean(fogOfWar)).toString());
		item.setSelected(fogOfWar);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter getPreferencesAdapter() {
		return new Preferences(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JMenuItem getContextAdapter() {
		return item;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param co TODO: DOCUMENT ME!
	 */
	public void setContextObserver(ContextObserver co) {
		obs = co;
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
			defaultTranslations.put("name", "Region renderer");
			defaultTranslations.put("chk.showfow.caption", "Enable fog of war");
		}

		return defaultTranslations;
	}

	private class Preferences extends JPanel implements PreferencesAdapter {
		// The source component to configure
		private RegionImageCellRenderer source = null;

		// GUI elements
		private JCheckBox chkFogOfWar = null;

		/**
		 * Creates a new Preferences object.
		 *
		 * @param r TODO: DOCUMENT ME!
		 */
		public Preferences(RegionImageCellRenderer r) {
			this.source = r;
			init();
		}

		private void init() {
			chkFogOfWar = new JCheckBox(getString("chk.showfow.caption"),
										source.getFogOfWar());
			this.add(chkFogOfWar);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			source.setFogOfWar(chkFogOfWar.isSelected());
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Component getComponent() {
			return this;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getTitle() {
			return source.getName();
		}
	}
}
