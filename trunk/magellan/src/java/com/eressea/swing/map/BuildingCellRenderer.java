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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.eressea.Building;
import com.eressea.Coordinate;
import com.eressea.Region;

import com.eressea.rules.UnitContainerType;

import com.eressea.util.CollectionFactory;

/**
 * A renderer for Building objects.
 */
public class BuildingCellRenderer extends ImageCellRenderer {
	/**
	 * Creates a new BuildingCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public BuildingCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
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
			Region r = (Region) obj;

			Iterator iter = r.buildings().iterator();

			if(iter.hasNext()) {
				Coordinate c = r.getCoordinate();
				Point pos = new Point(cellGeo.getImagePosition(c.x, c.y));
				pos.translate(-offset.x, -offset.y);

				Dimension size = cellGeo.getImageSize();

				while(iter.hasNext()) {
					Building b = (Building) iter.next();
					UnitContainerType type = b.getType();

					if(type != null) {
						Image img = getImage(type.getID().toString());

						if(img != null) {
							graphics.drawImage(img, pos.x, pos.y, size.width, size.height, null);
						}
					}
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_BUILDING;
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
			defaultTranslations.put("name", "Building renderer");
		}

		return defaultTranslations;
	}
}
