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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.eressea.Border;
import com.eressea.Coordinate;
import com.eressea.Region;

import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class BorderCellRenderer extends ImageCellRenderer {
	/**
	 * Creates a new BorderCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public BorderCellRenderer(CellGeometry geo, Properties settings) {
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
			Region     r	   = (Region) obj;
			Collection borders = r.borders();

			if(borders.isEmpty() == false) {
				// since border objects are rare initialization is
				// done as late as possible
				Coordinate c    = null;
				Point	   pos  = null;
				Dimension  size = null;

				for(Iterator iter = r.borders().iterator(); iter.hasNext();) {
					Border b = (Border) iter.next();

					if(com.eressea.util.Umlaut.normalize(b.type).equals("STRASSE") &&
						   (b.direction != com.eressea.util.Direction.DIR_INVALID)) {
						Image img = (b.buildRatio == 100)
									? getImage("Strasse" + b.direction)
									: getImage("Strasse_incomplete" +
											   b.direction);

						if(img == null) {
							img = getImage("Strasse" + b.direction);
						}

						if(img != null) {
							if(c == null) {
								c   = r.getCoordinate();
								pos = new Point(cellGeo.getImagePosition(c.x,
																		 c.y));
								pos.translate(-offset.x, -offset.y);
								size = cellGeo.getImageSize();
							}

							graphics.drawImage(img, pos.x, pos.y, size.width,
											   size.height, null);
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
		return Mapper.PLANE_BORDER;
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
			defaultTranslations.put("name", "Road renderer");
		}

		return defaultTranslations;
	}
}
