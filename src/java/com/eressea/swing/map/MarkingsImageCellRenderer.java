// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.map;


import java.awt.Image;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

public class MarkingsImageCellRenderer extends ImageCellRenderer {
	private final static Logger log = Logger.getInstance(MarkingsImageCellRenderer.class);

	public final static String ICON_TAG="regionicon";
	
	private StringBuffer buf;
	
	public MarkingsImageCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
		buf = new StringBuffer();
	}
	
	public void render(Object obj, boolean active, boolean selected) {
		if (obj instanceof Region && ((Region)obj).hasTags()) {
			
			Region r = (Region)obj;
			Coordinate c = r.getCoordinate();
				
			Rectangle rect = cellGeo.getImageRect(c.x, c.y);
			rect.translate(-offset.x, -offset.y);
			
			int i = 1;
			
			buf.setLength(0);
			buf.append(ICON_TAG);
			
			String key = null;
			
			do{
				key = buf.toString();
				if (r.containsTag(key)) {
					StringTokenizer st=new StringTokenizer(r.getTag(key)," ");
			
					while(st.hasMoreTokens()) {
						Image img=getImage(st.nextToken());
				
						if (img!=null)
							graphics.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
						else
							log.warn("MarkingsImageCellRenderer.render(): marking image is null!");
					}
				}
				if (i>1) {
					buf.setLength(buf.length() - 1);
				}
				buf.append(i);
				i++;
			}while(i<=10);						
		}
	}
	
	public int getPlaneIndex() {
		return Mapper.PLANE_MARKINGS;
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
			defaultTranslations.put("name","Additional icons");
		}
		return defaultTranslations;
	}

}
