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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.util.Map;
import java.util.Properties;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.main.MagellanContext;
import com.eressea.util.CollectionFactory;

/**
 * A renderer for Scheme objects. Schemes are seen from the "Astralraum".
 */
public class SchemeCellRenderer extends ImageCellRenderer {
	/**
	 * Creates a new BuildingCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public SchemeCellRenderer(CellGeometry geo, MagellanContext context) {
		super(geo, context);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj the region to be rendered
	 * @param active no use
	 * @param selected region to be rendered shall be marked as part of the schemes that are active
	 * 		  right now
	 */
	public void render(Object obj, boolean active, boolean selected) {
		if(obj instanceof Region) {
			Region r = (Region) obj;

			Image schemeImage = getImage("schemen");

			Coordinate c = r.getCoordinate();
			Point pos = new Point(cellGeo.getImagePosition(c.x, c.y));
			pos.translate(-offset.x, -offset.y);

			Dimension size = cellGeo.getImageSize();

			graphics.drawImage(schemeImage, pos.x, pos.y, size.width, size.height, null);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_SCHEMES;
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
			defaultTranslations.put("name", "Scheme renderer");
		}

		return defaultTranslations;
	}
}
