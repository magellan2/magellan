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

import java.awt.Image;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Properties;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HighlightImageCellRenderer extends ImageCellRenderer {
	private static final Logger log = Logger.getInstance(HighlightImageCellRenderer.class);

	/**
	 * Creates a new HighlightImageCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public HighlightImageCellRenderer(CellGeometry geo, Properties settings) {
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

			if(selected) {
				renderIt(r, "Selected");
			}

			if(active) {
				renderIt(r, "Active");
			}
		}
	}

	private void renderIt(Region r, String imgName) {
		Coordinate c = r.getCoordinate();

		Rectangle rect = cellGeo.getImageRect(c.x, c.y);
		rect.translate(-offset.x, -offset.y);

		Image img = getImage(imgName);

		if(img != null) {
			graphics.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
		} else {
			log.warn("HighlightImageCellRenderer.render(): image " + imgName + " is null!");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_HIGHLIGHT;
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
			defaultTranslations.put("name", "Marker renderer");
		}

		return defaultTranslations;
	}
}
