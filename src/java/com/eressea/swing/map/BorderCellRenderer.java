// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

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

public class BorderCellRenderer extends ImageCellRenderer {
	
	public BorderCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
	}
	
	public void render(Object obj, boolean active, boolean selected) {
		if (obj instanceof Region) {
			Region r = (Region)obj;
			Collection borders = r.borders();

			if (borders.isEmpty() == false) {
			
				// since border objects are rare initialization is
				// done as late as possible
				Coordinate c = null;
				Point pos = null;
				Dimension size = null;
				
				for (Iterator iter = r.borders().iterator(); iter.hasNext();) {
					Border b = (Border)iter.next();
					if (com.eressea.util.Umlaut.normalize(b.type).equals("STRASSE") && b.direction != com.eressea.util.Direction.DIR_INVALID) {
						Image img = (b.buildRatio==100)?getImage("Strasse" + b.direction):getImage("Strasse_incomplete" + b.direction);
						if (img == null) {img = getImage("Strasse" + b.direction);}
						if (img != null) {
							if (c == null) {
								c = r.getCoordinate();
								pos = new Point(cellGeo.getImagePosition(c.x, c.y));
								pos.translate(-offset.x, -offset.y);
								size = cellGeo.getImageSize();
							}
							graphics.drawImage(img, pos.x, pos.y, size.width, size.height, null);
						}
					}
				}
			}
		}
	}
	
	public int getPlaneIndex() {
		return Mapper.PLANE_BORDER;
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
			defaultTranslations.put("name", "Road renderer");
		}
		return defaultTranslations;
	}
	
}
