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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.GrayFilter;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.eressea.Coordinate;
import com.eressea.IntegerID;
import com.eressea.Message;
import com.eressea.Ship;
import com.eressea.Unit;
import com.eressea.UnitID;

import com.eressea.rules.MessageType;

import com.eressea.swing.preferences.PreferencesAdapter;

import com.eressea.util.Cache;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Direction;
import com.eressea.util.Regions;
import com.eressea.util.Umlaut;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PathCellRenderer extends ImageCellRenderer {
	private static final Logger log				  = Logger.getInstance(PathCellRenderer.class);
	private static final int    ACTIVE			  = 0;
	private static final int    PASSIVE			  = 1;
	private static final int    ACTIVEPAST		  = 2;
	private static final int    PASSIVEPAST		  = 3;
	private static final int    ALPHALEVEL		  = 100;
	private boolean			    drawPassivePath   = false;
	private boolean			    drawPastPath	  = false;
	protected Map			    ownImages		  = CollectionFactory.createHashMap();
	RGBImageFilter			    passiveFilter;
	RGBImageFilter			    activePastFilter;
	RGBImageFilter			    passivePastFilter;

	/**
	 * Creates a new PathCellRenderer object.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public PathCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
		drawPassivePath = (new Boolean(settings.getProperty("PathCellRenderer.drawPassivePath",
															"true"))).booleanValue();
		drawPastPath = (new Boolean(settings.getProperty("PathCellRenderer.drawPastPath",
														 "true"))).booleanValue();

		passiveFilter     = new GrayFilter(true, 50);
		activePastFilter  = new AlphaFilter(ALPHALEVEL);
		passivePastFilter = new AlphaFilter(ALPHALEVEL,
											new GrayFilter(false, 50));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 * @param active TODO: DOCUMENT ME!
	 * @param selected TODO: DOCUMENT ME!
	 */
	public void render(Object obj, boolean active, boolean selected) {
		try {
			if(obj instanceof Unit) {
				render((Unit) obj);
			} else if(obj instanceof Ship) {
				render(((Ship) obj).getOwnerUnit());
			}
		} catch(Exception e) {
			// FIXME(pavkovic): rendering may NOT fail! so we need this 
			log.error(e);
		}
	}

	/**
	 * Checks the orders of the specified unit for movement orders and renders
	 * arrows indicating the direction the unit is taking. Note that the
	 * movement orders may not be abbreviated for this to work.
	 *
	 * @param u TODO: DOCUMENT ME!
	 */
	private void render(Unit u) {
		if(u == null) {
			return;
		}

		if(drawPastPath) {
			List pastMovement = getPastMovement(u);

			if(log.isDebugEnabled()) {
				log.debug("render for unit u " + u + " travelled through " +
						  pastMovement);
			}

			renderPath(u, pastMovement,
					   isPastMovementPassive(u) ? PASSIVEPAST : ACTIVEPAST);
		}

		List activeMovement = getModifiedMovement(u);

		if(activeMovement.size() > 0) {
			renderPath(u, activeMovement, ACTIVE);
		} else if(drawPassivePath) {
			// unit does not move itself, check for passive movement
			// Perhaps it is on a ship?
			List passiveMovement = null;

			if(u.getModifiedShip() != null) {
				// we are on a ship. try to render movemement from ship owner
				passiveMovement = getModifiedMovement(u.getModifiedShip()
													   .getOwnerUnit());
			} else {
				// the unit is not on a ship, search for carriers
				Map carriers = u.getCarriers();

				if(log.isDebugEnabled()) {
					log.debug("PathCellRenderer.render: " + u + " has " +
							  carriers.size() + " carriers");
				}

				if(carriers.size() == 1) {
					Unit trans = (Unit) carriers.values().iterator().next();
					passiveMovement = getModifiedMovement(trans);
				}
			}

			renderPath(u, passiveMovement, PASSIVE);
		}
	}

	/**
	 * this function inspects travelthru an travelthruship to find the movement
	 * in the past
	 *
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return List of coordinates from start to end region.
	 */
	private List getPastMovement(Unit u) {
		// FIXME(pavkovic) move this stuff into unit.java!
		if(u.cache == null) {
			u.cache = new Cache();
		}

		if(u.cache.movementPath == null) {
			// the result may be null!
			u.cache.movementPath = Regions.getMovement(data, u);
		}

		if(u.cache.movementPath == null) {
			return Collections.EMPTY_LIST;
		} else {
			return u.cache.movementPath;
		}
	}

	private boolean isPastMovementPassive(Unit u) {
		// FIXME(pavkovic) move this stuff into unit.java!
		if(u.cache == null) {
			u.cache = new Cache();
		}

		if(u.cache.movementPathIsPassive == null) {
			u.cache.movementPathIsPassive = new Boolean(evaluatePastMovementPassive(u));
		}

		return u.cache.movementPathIsPassive.booleanValue();
	}

	private static final MessageType transportMessageType = new MessageType(IntegerID.create(891175669));

	private boolean evaluatePastMovementPassive(Unit u) {
		// FIXME(pavkovic) move this stuff into unit.java!
		if(u.getShip() != null) {
			if(u.equals(u.getShip().getOwnerUnit())) {
				// unit is on ship and the owner
				if(log.isDebugEnabled()) {
					log.debug("PathCellRenderer(" + u + "):false on ship");
				}

				return false;
			}

			// unit is on a ship and not the owner
			if(log.isDebugEnabled()) {
				log.debug("PathCellRenderer(" + u + "):true on ship");
			}

			return true;
		}

		// we assume a transportation to be passive, if
		// there is no message of type 891175669
		if(u.getFaction() == null) {
			if(log.isDebugEnabled()) {
				log.debug("PathCellRenderer(" + u + "):false no faction");
			}

			return false;
		}

		if(u.getFaction().messages == null) {
			// faction has no message at all
			if(log.isDebugEnabled()) {
				log.debug("PathCellRenderer(" + u + "):false no faction");
			}

			return true;
		}

		for(Iterator iter = u.getFaction().messages.iterator(); iter.hasNext();) {
			Message m = (Message) iter.next();

			if(false) {
				if(log.isDebugEnabled()) {
					if(transportMessageType.equals(m.getMessageType())) {
						log.debug("PathCellRenderer(" + u + ") Message " + m);

						if((m.attributes != null) &&
							   (m.attributes.get("unit") != null)) {
							log.debug("PathCellRenderer(" + u + ") Unit   " +
									  m.attributes.get("unit"));
							log.debug("PathCellRenderer(" + u + ") UnitID " +
									  UnitID.createUnitID((String) m.attributes.get("unit"),
														  10));
						}
					}
				}
			}

			if(transportMessageType.equals(m.getMessageType()) &&
				   (m.attributes != null) &&
				   (m.attributes.get("unit") != null) &&
				   u.getID().equals(UnitID.createUnitID((String) m.attributes.get("unit"),
															10))) {
				// found a transport message; this is only valid in 
				// units with active movement
				if(log.isDebugEnabled()) {
					log.debug("PathCellRenderer(" + u +
							  "):false with message " + m);
				}

				return false;
			}
		}

		if(log.isDebugEnabled()) {
			log.debug("PathCellRenderer(" + u + "):true with messages");
		}

		return true;
	}

	private List getModifiedMovement(Unit u) {
		return (u == null) ? Collections.EMPTY_LIST : u.getModifiedMovement();
	}

	private void renderPath(Unit u, List coordinates, int imageType) {
		if((coordinates != null) && (coordinates.size() > 0)) {
			renderPath(u, (Coordinate) coordinates.get(0),
					   Regions.getDirectionObjectsOfCoordinates(coordinates),
					   imageType);
		}
	}

	private void renderPath(Unit u, Coordinate start, List directions,
							int imageType) {
		if(log.isDebugEnabled()) {
			log.debug("renderPath for unit " + u + " from " + start +
					  " with list " + directions + ", imageType " + imageType);
		}

		Coordinate actCoord = new Coordinate(start); //  make Coordinate a copy 

		for(Iterator iter = directions.iterator(); iter.hasNext();) {
			Direction dirObj = (Direction) iter.next();
			int		  dir = dirObj.getDir();

			if(dir != -1) {
				Rectangle rect = cellGeo.getImageRect(actCoord.x, actCoord.y);
				rect.translate(-offset.x, -offset.y);

				Image img = getImage("Pfeil" + dir, imageType);

				if(img != null) {
					graphics.drawImage(img, rect.x, rect.y, rect.width,
									   rect.height, null);
				}

				actCoord.translate(Direction.toCoordinate(dir));
			} else {
				break;
			}
		}
	}

	private Image getImage(String name, int imageType) {
		Image img = null;

		if(name != null) {
			String normName = Umlaut.convertUmlauts(name);

			String storeName = imageType + normName;

			if(ownImages.containsKey(storeName)) {
				ImageContainer c = (ImageContainer) ownImages.get(storeName);

				if(c != null) {
					img = c.scaled;
				}
			} else {
				img = getImage(name);

				switch(imageType) {
				case ACTIVE:
					break;

				case PASSIVE:
					img = createImage(img, passiveFilter);

					break;

				case ACTIVEPAST:
					img = createImage(img, activePastFilter);

					break;

				case PASSIVEPAST:
					img = createImage(img, passivePastFilter);

					break;
				}

				if(img != null) {
					ownImages.put(storeName, new ImageContainer(img, scale(img)));
				} else {
					// add null to the map so we do not attempt to load the file again
					ownImages.put(storeName, null);
				}
			}
		}

		return img;
	}

	// BEGIN Image processing
	private Image createImage(Image img, RGBImageFilter filter) {
		if(img == null) {
			return null;
		}

		ImageProducer prod = new FilteredImageSource(img.getSource(), filter);

		return Toolkit.getDefaultToolkit().createImage(prod);
	}

	private class AlphaFilter extends RGBImageFilter {
		private int			   level;
		private RGBImageFilter parent;

		/**
		 * Creates a new AlphaFilter object.
		 *
		 * @param level TODO: DOCUMENT ME!
		 */
		public AlphaFilter(int level) {
			this(level, null);
		}

		/**
		 * Creates a new AlphaFilter object.
		 *
		 * @param level TODO: DOCUMENT ME!
		 * @param parent TODO: DOCUMENT ME!
		 */
		public AlphaFilter(int level, RGBImageFilter parent) {
			this.level  = level * 0x01000000;
			this.parent = parent;

			// canFilterIndexColorModel indicates whether or not it is acceptable
			// to apply the color filtering of the filterRGB method to the color
			// table entries of an IndexColorModel object in lieu of pixel by pixel
			// filtering.
			canFilterIndexColorModel = true;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param x TODO: DOCUMENT ME!
		 * @param y TODO: DOCUMENT ME!
		 * @param rgb TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int filterRGB(int x, int y, int rgb) {
			if(parent == null) {
				return myFilterRGB(x, y, rgb);
			} else {
				return parent.filterRGB(x, y, myFilterRGB(x, y, rgb));
			}
		}

		private int myFilterRGB(int x, int y, int rgb) {
			// set alpha from opaque to given level
			// if alpha channel IS transparent we dont do anything
			if((rgb & 0xff000000) == 0) {
				return rgb;
			} else {
				// we found a non-transparent pixel, so set given alpha level
				return (rgb & 0x00ffffff) | level;
			}
		}
	}

	// END Image processing

	/**
	 * Scale all images this renderer uses to a certain scale factor.
	 *
	 * @param scaleFactor the factor to scale the images with (a scaleFactor of
	 * 		  1.0 would scale all images to their original size).
	 */
	public void scale(float scaleFactor) {
		super.scale(scaleFactor);

		for(Iterator iter = ownImages.values().iterator(); iter.hasNext();) {
			ImageContainer c = (ImageContainer) iter.next();

			if(c != null) {
				c.scaled = scale(c.unscaled);
			}
		}
	}

	/**
	 * Make the renderer reload all of its cached images.
	 */
	public void reloadImages() {
		super.reloadImages();
		ownImages.clear();
	}

	/**
	 * Set the cell geometry this renderer is based on and make it reload all
	 * of its cached images.
	 *
	 * @param geo TODO: DOCUMENT ME!
	 */
	public void setCellGeometry(CellGeometry geo) {
		super.setCellGeometry(geo);
		reloadImages();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPlaneIndex() {
		return Mapper.PLANE_PATH;
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
			defaultTranslations.put("name", "Path renderer");
			defaultTranslations.put("drawpassivepath",
									"draw path for passive transportation");
			defaultTranslations.put("drawpastpath",
									"draw path for past movement");
		}

		return defaultTranslations;
	}

	// BEGIN preferences stuff
	private boolean getDrawPassivePath() {
		return drawPassivePath;
	}

	private void setDrawPassivePath(boolean bool) {
		drawPassivePath = bool;
		settings.setProperty("PathCellRenderer.drawPassivePath",
							 (new Boolean(bool)).toString());
	}

	private boolean getDrawPastPath() {
		return drawPastPath;
	}

	private void setDrawPastPath(boolean bool) {
		drawPastPath = bool;
		settings.setProperty("PathCellRenderer.drawPastPath",
							 (new Boolean(bool)).toString());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter getPreferencesAdapter() {
		return new Preferences(this);
	}

	protected class Preferences extends JPanel implements PreferencesAdapter {
		// The source component to configure
		protected PathCellRenderer source = null;

		// GUI elements
		private JCheckBox chkDrawPassivePath = null;
		private JCheckBox chkDrawPastPath = null;

		/**
		 * Creates a new Preferences object.
		 *
		 * @param r TODO: DOCUMENT ME!
		 */
		public Preferences(PathCellRenderer r) {
			this.source = r;
			init();
		}

		private void init() {
			chkDrawPassivePath = new JCheckBox(getString("drawpassivepath"),
											   source.getDrawPassivePath());
			chkDrawPastPath = new JCheckBox(getString("drawpastpath"),
											source.getDrawPastPath());

			this.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			c.gridx  = 0;
			c.gridy  = 0;
			this.add(chkDrawPassivePath, c);
			c.gridx = 0;
			c.gridy = 1;
			this.add(chkDrawPastPath, c);
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			source.setDrawPassivePath(chkDrawPassivePath.isSelected());
			source.setDrawPastPath(chkDrawPastPath.isSelected());
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Component getComponent() {
			return this;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getTitle() {
			return source.getName();
		}
	}

	// END   preferences stuff
}
