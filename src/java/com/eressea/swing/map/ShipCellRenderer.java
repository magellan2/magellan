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
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Map;

import com.eressea.CoordinateID;
import com.eressea.EntityID;
import com.eressea.ID;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.main.MagellanContext;
import com.eressea.util.CollectionFactory;
import com.eressea.util.comparator.FactionTrustComparator;

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
	public ShipCellRenderer(CellGeometry geo, MagellanContext context) {
		super(geo, context);
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
				CoordinateID c = r.getCoordinate();
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
						shipInformations[s.shoreId + 1] = new ShipInformation(s.deprecatedCapacity,
																			  s.getType().getName());
					}

					ShipInformation actShip = shipInformations[s.shoreId + 1];

					if(actShip.capacity < s.deprecatedCapacity) {
						actShip.capacity = s.deprecatedCapacity;
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
			renderTravelThrough(r);
		}
	}

	/** 
	 * renders shipthrou informations here. May be smarter elsewhere.
	 */
	private void renderTravelThrough(Region region) {
		if(!region.getRegionType().isOcean()) {
			return;
		}
		boolean foundEnemy=false;
		boolean foundAllied=false;
		if(region.travelThruShips != null) {
			for(Iterator iter = region.travelThruShips.iterator(); iter.hasNext(); ) {
				// Messages like "Wogenspalter (64ch)"
				String msg = ((Message) iter.next()).toString();
				int from = msg.lastIndexOf("(");
				int to   = msg.indexOf(")",from);
				if(from>-1 && to <msg.toString().length()) {
					ID sid = EntityID.createEntityID(msg.substring(from+1,to),region.getData().base);
					Ship ship = region.getData().getShip(sid);
					if(ship != null && ship.getOwnerUnit() != null &&
							ship.getOwnerUnit().getFaction() != null && 
							ship.getOwnerUnit().getFaction().trustLevel >=FactionTrustComparator.ALLIED) {
						foundAllied=true;
					} else {
						foundEnemy=true;
					}
				}
			}
		}

		if(foundAllied) {
			Image img = getImage("durchschiffung_alliiert");
			if(img != null) {
				CoordinateID c = region.getCoordinate();
				Rectangle rect = cellGeo.getImageRect(c.x, c.y);		
				rect.translate(-offset.x, -offset.y);
				graphics.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
			}
		}		
		if(foundEnemy) {
			Image img = getImage("durchschiffung_feindlich");
			if(img != null) {
				CoordinateID c = region.getCoordinate();
				Rectangle rect = cellGeo.getImageRect(c.x, c.y);		
				rect.translate(-offset.x, -offset.y);
				graphics.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
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
