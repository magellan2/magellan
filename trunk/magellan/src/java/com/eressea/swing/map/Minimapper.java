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

import java.util.Map;

import javax.swing.ToolTipManager;

import com.eressea.main.MagellanContext;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class Minimapper extends Mapper {
	private RegionShapeCellRenderer myRenderer;
	protected int minimapLastType = -1;

	/**
	 * Creates new Minimapper.
	 *
	 * @param context
	 */
    public Minimapper(MagellanContext context) {
		super(context, null, new CellGeometry("cellgeometry.txt"));

		// if Mapper has registered us, we don't want this
		ToolTipManager.sharedInstance().unregisterComponent(this);
	}

	/**
	 * Never shows tooltips.
	 *
	 * @param b ignored
	 * @see com.eressea.swing.map.Mapper#setShowTooltip(boolean)
	 */
	public void setShowTooltip(boolean b) {
		// never show tooltips
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param renderer TODO: DOCUMENT ME!
	 * @param plane TODO: DOCUMENT ME!
	 */
	public void setRenderer(MapCellRenderer renderer, int plane) {
		String old = settings.getProperty("Mapper.Planes." + plane);
		super.setRenderer(renderer, plane);
		settings.setProperty("Mapper.Planes." + plane, old);
	}

	protected RenderingPlane[] initRenderingPlanes() {
		RenderingPlane p[] = new RenderingPlane[1];
		p[PLANE_REGION] = new RenderingPlane(PLANE_REGION, getString("plane.region.name"), 1);
		p[PLANE_REGION].setRenderer(myRenderer = new RegionShapeCellRenderer(getCellGeometry(),
																			 context,
																			 "Minimap.FactionColors",
																			 "Minimap.RegionColors",
																			 "Minimap.PoliticsMode"));

		return p;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public MapCellRenderer getMinimapRenderer() {
		return myRenderer;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param mode TODO: DOCUMENT ME!
	 */
	public void setPaintMode(int mode) {
		myRenderer.setPaintMode(mode);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPaintMode() {
		return myRenderer.getPaintMode();
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void synchronizeColors() {
		// synchronize factions
		myRenderer.loadFactionColors(RegionShapeCellRenderer.DEFAULT_FACTION_KEY, false);
		myRenderer.saveFactionColors();

		// synchronize regions
		myRenderer.loadRegionColors(RegionShapeCellRenderer.DEFAULT_REGION_KEY, false);
		myRenderer.saveRegionColors();

		// load unknown/ocean
		myRenderer.loadOceanColor();
		myRenderer.loadUnknownColor();

		repaint();
	}

	protected void setLastRegionRenderingType(int l) {
		minimapLastType = l;
	}

	protected int getLastRegionRenderingType() {
		return minimapLastType;
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
			defaultTranslations.put("plane.region.name", "Regions");
		}

		return defaultTranslations;
	}
}
