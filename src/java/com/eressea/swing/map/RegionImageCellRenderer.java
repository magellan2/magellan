// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.map;


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.eressea.Building;
import com.eressea.Coordinate;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.Skill;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.rules.BuildingType;
import com.eressea.rules.RegionType;
import com.eressea.rules.UnitContainerType;
import com.eressea.swing.context.ContextChangeable;
import com.eressea.swing.context.ContextObserver;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.EresseaSkillConstants;
import com.eressea.util.Regions;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.SortIndexComparator;
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

	public void init(com.eressea.GameData data, Graphics g, Point offset) {
		// initialize fog-of-war cache
		if (fogOfWar) {
			// intialize the fog-of-war cache for all regions that are covered by lighthouses
			if (data.buildings() != null) {
				BuildingType type = data.rules.getBuildingType(StringID.create("Leuchtturm"));
				RegionType oceanType = data.rules.getRegionType(StringID.create("Ozean"));
				Comparator sortIndexComparator = new SortIndexComparator(new IDComparator());
				if (type != null) {
					for (Iterator iter = data.buildings().values().iterator(); iter.hasNext();) {
						Building b = (Building)iter.next();
						if (type.equals(b.getType()) && b.getSize() >= 10) {
							int personCounter = 0;
							int perceptionSkillLevel = 0;
							List sortedInmates = CollectionFactory.createLinkedList(b.units());
							Collections.sort(sortedInmates, sortIndexComparator);
							for (Iterator inmates = sortedInmates.iterator(); inmates.hasNext() && personCounter < 4; personCounter++) {
								Unit inmate = (Unit)inmates.next();
								Skill perceptionSkill = inmate.getSkill(data.rules.getSkillType(EresseaSkillConstants.S_WAHRNEHMUNG, true));
								if (perceptionSkill != null) {
									perceptionSkillLevel = Math.max(perceptionSkill.getLevel(), perceptionSkillLevel);
								}
							}
							int maxRadius = (int)Math.min((Math.log(b.getSize())/Math.log(10)) + 1, perceptionSkillLevel / 3);
							if (maxRadius > 0) {
								Map regions = Regions.getAllNeighbours(data.regions(), b.getRegion().getCoordinate(), maxRadius, null);
								for (Iterator regionIter = regions.values().iterator(); regionIter.hasNext();) {
									Region r = (Region)regionIter.next();
									if (oceanType == null || oceanType.equals(r.getType())) {
										r.setFogOfWar(0);
									}
								}
							}
						}
					}
				}
			}
			// intialize the fog-of-war cache for all regions where units or ships traveled through
			for (Iterator iterator = data.regions().values().iterator(); iterator.hasNext(); ) {
				Region r = (Region)iterator.next();
				if (r.travelThru!=null) {
					initTravelThru(data, r, r.travelThru);
				}
				if (r.travelThruShips!=null) {
					initTravelThru(data, r, r.travelThruShips);
				}
			}
		}
		this.data = data;
		graphics = g;
		this.offset = offset;
	}

	private void initTravelThru(GameData data, Region region, Collection travelThru) {
		for (Iterator iter = travelThru.iterator(); iter.hasNext();) {
			Message mes = (Message) iter.next();
			// fetch ID of Unit or Ship from Message of type "<name> (<id>)"
			String s = mes.getText();
			int startpos = s.lastIndexOf("(") + 1;
			int endpos = s.length() - 1;
			if (startpos>-1 && endpos>startpos) {
				try {
					ID id = EntityID.createEntityID(s.substring(startpos, endpos));
					if (data.getUnit(id)!=null && data.getUnit(id).getFaction().trustLevel>=Faction.TL_PRIVILEGED) {
						// fast return
						region.setFogOfWar(0);
						return;
					} else {
						Ship ship = data.getShip(id);
						if (ship!=null) {
							for (Iterator i = ship.units().iterator(); i.hasNext(); ) {
								if (((Unit)i.next()).getFaction().trustLevel>=Faction.TL_PRIVILEGED) {
									// fast return
									region.setFogOfWar(0);
									return;
								}
							}
						}
					}
				} catch (NumberFormatException e) {
				}
			}
		}
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
