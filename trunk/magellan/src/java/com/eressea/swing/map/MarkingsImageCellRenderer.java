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

import java.awt.Image;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.main.MagellanContext;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class MarkingsImageCellRenderer extends ImageCellRenderer {
	private static final Logger log = Logger.getInstance(MarkingsImageCellRenderer.class);

	/** TODO: DOCUMENT ME! */
	public static final String ICON_TAG = "regionicon";
	private StringBuffer buf;

	/**
	 * Creates a new MarkingsImageCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public MarkingsImageCellRenderer(CellGeometry geo, MagellanContext context) {
		super(geo, context);
		buf = new StringBuffer();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 * @param active TODO: DOCUMENT ME!
	 * @param selected TODO: DOCUMENT ME!
	 */
	public void render(Object obj, boolean active, boolean selected) {
		if(obj instanceof Region && ((Region) obj).hasTags()) {
			Region r = (Region) obj;
			Coordinate c = r.getCoordinate();

			Rectangle rect = cellGeo.getImageRect(c.x, c.y);
			rect.translate(-offset.x, -offset.y);

			int i = 1;

			buf.setLength(0);
			buf.append(ICON_TAG);

			String key = null;

			do {
				key = buf.toString();

				if(r.containsTag(key)) {
					StringTokenizer st = new StringTokenizer(r.getTag(key), " ");

					while(st.hasMoreTokens()) {
						Image img = getImage(st.nextToken());

						if(img != null) {
							graphics.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
						} else {
							log.warn("MarkingsImageCellRenderer.render(): marking image is null!");
						}
					}
				}

				if(i > 1) {
					buf.setLength(buf.length() - 1);
				}

				buf.append(i);
				i++;
			} while(i <= 10);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_MARKINGS;
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
			defaultTranslations.put("name", "Additional icons");
		}

		return defaultTranslations;
	}
}
