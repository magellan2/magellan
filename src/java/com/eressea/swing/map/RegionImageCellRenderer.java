// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

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

public class RegionImageCellRenderer extends ImageCellRenderer implements ContextChangeable{

	public final static String MAP_TAG = "mapicon";

	private final static Logger log = Logger.getInstance(RegionImageCellRenderer.class);
	private boolean fogOfWar = true;

	protected JCheckBoxMenuItem item = null;
	protected ContextObserver obs = null;

	public RegionImageCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
		fogOfWar = (new Boolean(settings.getProperty("RegionImageCellRenderer.fogOfWar", Boolean.TRUE.toString()))).booleanValue();
		item = new JCheckBoxMenuItem(getString("chk.showfow.caption"),fogOfWar);
		item.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				fogOfWar = item.isSelected();
				getSettings().setProperty("RegionImageCellRenderer.fogOfWar", (new Boolean(fogOfWar)).toString());
				if (obs != null)
					obs.contextDataChanged();
			}
		});
	}

	protected Properties getSettings() {
		return settings;
	}

	public void render(Object obj, boolean active, boolean selected) {
		if (obj instanceof Region) {
			Region r = (Region)obj;
			Coordinate c = r.getCoordinate();

			Rectangle rect = cellGeo.getImageRect(c.x, c.y);
			rect.translate(-offset.x, -offset.y);

			if (r.containsTag(MAP_TAG)) {
				Image img = getImage(r.getTag(MAP_TAG));
				if (img != null) {
					drawImage(r, img, rect);
					return;
				}
			}
			UnitContainerType type = r.getType();
			if (type != null) {
				drawImage(r, getImage(type.getID().toString()), rect);
			}
		}
	}

	protected void drawImage(Region r, Image img, Rectangle rect) {
		if (img != null) {
			graphics.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
		} else {
			log.warn("RegionImageCellRenderer.render(): image is null");
		}

		if (fogOfWar) {
			Image fogImg = getImage("Nebel");
			if (fogImg != null && r.fogOfWar()) {
				graphics.drawImage(fogImg, rect.x, rect.y, rect.width, rect.height, null);
			}
		}
	}

	public int getPlaneIndex() {
		return Mapper.PLANE_REGION;
	}

	public boolean getFogOfWar() {
		return fogOfWar;
	}

	public void setFogOfWar(boolean bool) {
		fogOfWar = bool;
		settings.setProperty("RegionImageCellRenderer.fogOfWar", (new Boolean(fogOfWar)).toString());
		item.setSelected(fogOfWar);
	}

	public PreferencesAdapter getPreferencesAdapter() {
		return new Preferences(this);
	}

	public JMenuItem getContextAdapter() {
		return item;
	}

	public void setContextObserver(ContextObserver co) {
		obs = co;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("name" , "Region renderer");
			defaultTranslations.put("chk.showfow.caption" , "Enable fog of war");
		}
		return defaultTranslations;
	}

	private class Preferences extends JPanel implements PreferencesAdapter {
		// The source component to configure
		private RegionImageCellRenderer source = null;

		// GUI elements
		private JCheckBox chkFogOfWar = null;

		public Preferences(RegionImageCellRenderer r) {
			this.source = r;
			init();
		}

		private void init() {
			chkFogOfWar = new JCheckBox(getString("chk.showfow.caption"), source.getFogOfWar());
			this.add(chkFogOfWar);
		}

		public void applyPreferences() {
			source.setFogOfWar(chkFogOfWar.isSelected());
		}

		public Component getComponent() {
			return this;
		}

		public String getTitle() {
			return source.getName();
		}
	}

}
