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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ShipCellRenderer extends ImageCellRenderer {
	/**
	 * Creates a new ShipCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public ShipCellRenderer(CellGeometry geo, Properties settings) {
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

			Iterator iter = r.ships().iterator();

			if(iter.hasNext()) {
				Coordinate c = r.getCoordinate();
				Point pos = new Point(cellGeo.getImagePosition(c.x, c.y));
				pos.translate(-offset.x, -offset.y);

				Dimension size = cellGeo.getImageSize();

				// grep ships in region
				// The ship with the maximum capacity will be drawn only
				// Directions 0-6
				ShipInformation shipInformations[] = new ShipInformation[7];

				// find ships with max capacity
				while(iter.hasNext()) {
					Ship s = (Ship) iter.next();

					if(shipInformations[s.shoreId + 1] == null) {
						shipInformations[s.shoreId + 1] = new ShipInformation(s.capacity,
																			  s.getType().getName());
					}

					ShipInformation actShip = shipInformations[s.shoreId + 1];

					if(actShip.capacity < s.capacity) {
						actShip.capacity = s.capacity;
						actShip.typeName = s.getType().getName();
					}
				}

				// 2. draw ships in region
				for(int i = 0; i < shipInformations.length; i++) {
					ShipInformation actShip = shipInformations[i];

					if(actShip == null) {
						continue;
					}

					Image img = getImage(actShip.typeName + i);

					if(img == null) {
						// special image not found, use generic image
						img = getImage("Schiff" + i);
					}

					if(img != null) {
						graphics.drawImage(img, pos.x, pos.y, size.width, size.height, null);
					}
				}
			}
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
			defaultTranslations.put("name", "Ship renderer");
		}

		return defaultTranslations;
	}

	private static class ShipInformation {
		/** TODO: DOCUMENT ME! */
		public int capacity = -1;

		/** TODO: DOCUMENT ME! */
		public String typeName = null;

		ShipInformation(int aCap, String aName) {
			capacity = aCap;
			typeName = aName;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_SHIP;
	}
}
