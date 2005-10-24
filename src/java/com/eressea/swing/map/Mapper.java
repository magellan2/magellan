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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import com.eressea.Building;
import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.Scheme;
import com.eressea.Ship;
import com.eressea.Unit;
import com.eressea.demo.desktop.Initializable;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.event.UnitOrdersListener;
import com.eressea.main.MagellanContext;
import com.eressea.rules.ItemType;
import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.context.MapContextMenu;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;
import com.eressea.util.replacers.ReplacerFactory;
import com.eressea.util.replacers.ReplacerHelp;
import com.eressea.util.replacers.ReplacerSystem;

/**
 * A component displaying a map based on a <tt>GameData</tt> object. The appearance of the map is
 * made configurable by using combinations of classes implementing the <tt>CellRenderers</tt>
 * interface.
 * 
 * <p>
 * <b>Note:</b>
 * </p>
 * 
 * <p>
 * This class avoids Java2D methods so it can be used with JDK version earlier than 1.2.
 * </p>
 */
public class Mapper extends InternationalizedDataPanel implements SelectionListener, Scrollable,
																  UnitOrdersListener,
																  GameDataListener, Initializable
{
	private static final Logger log = Logger.getInstance(Mapper.class);

	/**
	 * a mapping for int positions of planes to logical names. Will be used for
	 * magellan_desktop.ini
	 */
	public static final String PLANE_STRINGS[] = {
													 "REGION", "BORDER", "BUILDING", "SHIP", "TEXT",
													 "PATH", "HIGHLIGHT", "MARKINGS", "SCHEMES"
												 };

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_REGION = 0;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_BORDER = 1;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_BUILDING = 2;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_SHIP = 3;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_TEXT = 4;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_PATH = 5;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_HIGHLIGHT = 6;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_MARKINGS = 7;

	/** TODO: DOCUMENT ME! */
	public static final int PLANE_SCHEMES = 8;
	private static final int PLANES = 9;
	private RenderingPlane planes[] = null;
	private Collection availableRenderers = null;
	private MediaTracker tracker = null;
	private Region prevDragRegion = null;
	private boolean doDraggingSelect = false;
	private Object activeObject = null;
	private Region activeRegion = null;
	private Map selectedRegions = CollectionFactory.createHashtable();
	private List pathRegions = CollectionFactory.createLinkedList();
	private boolean pathPersistence = false;
	private Rectangle mapToScreenBounds = null;
	private int showLevel = 0;
	private float scaleFactor = 1.0f;
	private Rectangle currentBounds = null;
	private Image buffer = null;

	// The cell geometry used by the renderes, see setRenderer()
	private CellGeometry cellGeometry = null;

	/** Holds value of property renderContextChanged. */
	private static boolean renderContextChanged;

	//	protected StringBuffer tooltipBuffer=new StringBuffer();
	protected boolean showTooltip = false;
	protected ItemType silverItemType = null;
	protected ReplacerSystem tooltipDefinition;
	protected String tooltipDefinitionString = null;
	protected static ReplacerFactory tooltipReplacers;

	// region sublist for rendering
	protected List regionList = null;
	protected int lastRegionRenderingType = -1;
	protected int inPaint = 0;

	//context
	protected MapContextMenu conMenu;

    protected MagellanContext context;
	/**
	 * Creates a new Mapper object.
	 *
	 * @param ed TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 * @param customRenderers TODO: DOCUMENT ME!
	 * @param geom TODO: DOCUMENT ME!
	 */
	public Mapper(MagellanContext context, Collection customRenderers, CellGeometry geom) {
		super(context.getEventDispatcher(), context.getProperties());

        this.context = context;
        
		conMenu = new MapContextMenu(context.getEventDispatcher(), context.getProperties());

		setTooltipDefinition(settings.getProperty("Mapper.ToolTip.Definition",
												  "<html><font=-1>§rname§</font></html>"));
		setShowTooltip(settings.getProperty("Mapper.showTooltips", "false").equals("true"));

		setDoubleBuffered(false); // we mainly use our own buffer

		final Mapper mapper = this;

		// set the tracker used to repaint when loading and scaling images takes a while
		// TODO: remove this decision from options. This is a developer decision!!!
		if((Boolean.valueOf(settings.getProperty("Mapper.deferPainting", "true"))).booleanValue()) {
			tracker = new MediaTracker(this);
			ImageCellRenderer.setTracker(tracker);
		}

		// load the cell geometry to be used for painting cells
		cellGeometry = geom;

		// initialize renderers and planes (mark the order!)
		availableRenderers = initAvailableRenderers(cellGeometry, settings, customRenderers);
		planes = initRenderingPlanes();

		// determine the size of the map in component coordinates
		mapToScreenBounds = getMapToScreenBounds();

		if(mapToScreenBounds != null) {
			setPreferredSize(mapToScreenBounds.getSize());
		}

		addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					requestFocus();

					if(!pathPersistence) {
						pathRegions.clear();
					}

					if((cellGeometry == null) || (mapToScreenBounds == null)) {
						return;
					}

					Coordinate c = cellGeometry.getCoordinate(me.getPoint().x +
															  mapToScreenBounds.x,
															  me.getPoint().y +
															  mapToScreenBounds.y, showLevel);
					Region r = data.getRegion(c);

					if(r != null) {
						if((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
							if((me.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
								if(selectedRegions.containsKey(c) == false) {
									doDraggingSelect = true;
									selectedRegions.put(c, r);
								} else {
									doDraggingSelect = false;
									selectedRegions.remove(c);
								}

								data.setSelectedRegionCoordinates(selectedRegions);
								dispatcher.fire(new SelectionEvent(mapper,
																   selectedRegions.values(),
																   null,
																   SelectionEvent.ST_REGIONS));
								repaint();
								prevDragRegion = r;
							} else {
								activeRegion = r;
								activeObject = r;
								dispatcher.fire(new SelectionEvent(mapper, null,
																   activeRegion,
																   SelectionEvent.ST_DEFAULT));
								repaint();
							}
						} else if((me.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
							conMenu.init(r, selectedRegions.values());
							conMenu.show(Mapper.this, me.getX(), me.getY());
						}
					} else if((me.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
						conMenu.clear();
						conMenu.show(Mapper.this, me.getX(), me.getY());
					}
				}

				public void mouseRelease(MouseEvent me) {
					if((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
						prevDragRegion = null;
					}
				}
			});

		addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent me) {
					if(((me.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) &&
						   ((me.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
						if(!pathPersistence) {
							pathRegions.clear();
						}

						if(cellGeometry == null) {
							return;
						}

						Coordinate c = cellGeometry.getCoordinate(me.getPoint().x +
																  mapToScreenBounds.x,
																  me.getPoint().y +
																  mapToScreenBounds.y, showLevel);
						Region r = data.getRegion(c);

						if((r != null) && ((prevDragRegion == null) || !prevDragRegion.equals(r))) {
							boolean regionAlreadySelected = selectedRegions.containsKey(c);
							boolean doFire = false;

							if(!regionAlreadySelected) {
								if(doDraggingSelect) {
									selectedRegions.put(c, r);
									doFire = true;
								}
							} else {
								if(!doDraggingSelect) {
									selectedRegions.remove(c);
									doFire = true;
								}
							}

							if(doFire) {
								data.setSelectedRegionCoordinates(selectedRegions);
								dispatcher.fire(new SelectionEvent(mapper,
																   selectedRegions.values(),
																   null,
																   SelectionEvent.ST_REGIONS));
							}

							repaint();
						}

						prevDragRegion = r;
					}
				}
			});

		addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(activeRegion == null) {
						return;
					}

					Coordinate translationCoord = null;

					switch(e.getKeyCode()) {
					case KeyEvent.VK_UP:
					case KeyEvent.VK_NUMPAD9:
						translationCoord = new Coordinate(0, 1);

						break;

					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_NUMPAD6:
						translationCoord = new Coordinate(1, 0);

						break;

					case KeyEvent.VK_DOWN:
					case KeyEvent.VK_NUMPAD1:
						translationCoord = new Coordinate(0, -1);

						break;

					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_NUMPAD4:
						translationCoord = new Coordinate(-1, 0);

						break;

					case KeyEvent.VK_NUMPAD3:
						translationCoord = new Coordinate(1, -1);

						break;

					case KeyEvent.VK_NUMPAD7:
						translationCoord = new Coordinate(-1, 1);

						break;

					case KeyEvent.VK_NUMPAD2:
						translationCoord = new Coordinate(1, -2);

						break;

					case KeyEvent.VK_NUMPAD8:
						translationCoord = new Coordinate(-1, 2);

						break;

					default:
						break;
					}

					if(translationCoord != null) {
						Coordinate c = new Coordinate(activeRegion.getCoordinate());
						activeRegion = data.getRegion(c.translate(translationCoord));
						data.setSelectedRegionCoordinates(null);
						dispatcher.fire(new SelectionEvent(mapper, null,
														   activeRegion,
														   SelectionEvent.ST_REGIONS));
						repaint();
					}
				}
			});

		this.dispatcher.addSelectionListener(this);
		this.dispatcher.addUnitOrdersListener(this);

		conMenu.updateRenderers(this);
		conMenu.updateTooltips(this);
	}

	protected void reprocessTooltipDefinition() {
		setTooltipDefinition(settings.getProperty("Mapper.ToolTip.Definition",
												  "<html><font=-1>§rname§</font></html>"));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getToolTipText(MouseEvent e) {
		if(tooltipDefinition != null) {
			try {
				Coordinate c = cellGeometry.getCoordinate(e.getPoint().x + mapToScreenBounds.x,
														  e.getPoint().y + mapToScreenBounds.y,
														  showLevel);
				Region r = data.getRegion(c);

				if(r != null) {
					Object ret = tooltipDefinition.getReplacement(r);

					if(ret != null) {
						return ret.toString();
					} else {
						return "-?-";
					}
				}
			} catch(Exception exc) {
			}
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void unitOrdersChanged(UnitOrdersEvent e) {
		// TODO: do we really need to repaint this?
		repaint();
	}

	/**
	 * Add a cell renderer object to the mapper. Each cell renderer has a rendering plane
	 * associated with it, so if there is already a renderer in the rendering plane of the added
	 * renderer the old renderer is removed.
	 *
	 * @param renderer the object responsible for rendering a     graphical representation of
	 * 		  regions.
	 */
	public void setRenderer(MapCellRenderer renderer) {
		if(renderer != null) {
			setRenderer(renderer, renderer.getPlaneIndex());
		} else {
			log.warn("Mapper.setRenderer(): null renderer set has been set for unknown rendering plane!");
		}
	}

	/**
	 * Set a cell renderer object for a certain plane of the map. This function can be used to
	 * override the renderes default rendering plane.
	 *
	 * @param renderer the object responsible for rendering a     graphical representation of
	 * 		  regions.
	 * @param plane the plane the renderer will draw to. Lower     planes are painted over by
	 * 		  higher planes. See the constants in com.eressea.swing.map.Mapper for possible values
	 * 		  or choose a value between 0 and getRenderPlainCount() - 1.
	 */
	public void setRenderer(MapCellRenderer renderer, int plane) {
		if((plane >= 0) && (plane < planes.length)) {
			if(planes[plane] == null) {
				planes[plane] = new RenderingPlane(plane, "Zusatzplane");
			}

			planes[plane].setRenderer(renderer);

			String className = "none";

			if(renderer != null) {
				className = renderer.getClass().getName();
			}

			settings.setProperty("Mapper.Planes." + plane, className);
			conMenu.updateRenderers(this);
		} else {
			log.warn("Mapper.setRenderer(): invalid argument: plane out of bounds");
		}
	}

	/**
	 * Get the cell renderer objects that are available for a certain rendering plane. It is
	 * suggested that these objects are used for calling one of the setRenderer() methods.
	 *
	 * @param plane the plane the renderer will draw to. Lower     planes are painted over by
	 * 		  higher planes. See the constants in com.eressea.swing.map.Mapper for possible
	 * 		  values.
	 *
	 * @return the renderer object associated with the specified rendering plane or null if no such
	 * 		   association exists.
	 */
	public Collection getRenderers(int plane) {
		Collection renderers = null;

		if((plane >= 0) && (plane < planes.length)) {
			renderers = CollectionFactory.createLinkedList();

			for(Iterator iter = availableRenderers.iterator(); iter.hasNext();) {
				MapCellRenderer r = (MapCellRenderer) iter.next();

				if(r.getPlaneIndex() == plane) {
					renderers.add(r);
				}
			}
		} else {
			log.warn("Mapper.getRenderers(): invalid argument: plane out of bounds");
		}

		return renderers;
	}

	/**
	 * Returns a list of object containing the rendering planes existing in this Mapper object. The
	 * planes are sorted with ascending plane indices.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getPlanes() {
		return Arrays.asList(planes);
/*
		List p = CollectionFactory.createLinkedList();

		for(int planeIndex = 0; planeIndex < planes.length; planeIndex++) {
			if(planes[planeIndex] != null) {
				p.add(planes[planeIndex]);
			}
		}

		return p;
		*/
	}

	/**
	 * Set a path - a list of consecutive regions - to be rendered by the renderer registered to
	 * the path rendering plane.
	 *
	 * @param path a list of <tt>Region</tt> objects to be rendered as a path on the map.
	 * @param isPersistent if <tt>true</tt>, always render the path, else render the path only
	 * 		  until a different region is selected.
	 */
	public void setPath(List path, boolean isPersistent) {
		pathRegions.clear();
		pathRegions.addAll(path);
		pathPersistence = isPersistent;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		data = e.getGameData();

		conMenu.setGameData(data);

		mapToScreenBounds = getMapToScreenBounds();

		if(mapToScreenBounds != null) {
			setSize(mapToScreenBounds.getSize());
		}

		setPreferredSize(getSize());
		activeRegion = null;
		selectedRegions.clear();

		pathRegions.clear();

		reprocessTooltipDefinition();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param se TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent se) {
		if(se.getSource() == this) {
			return;
		}

		activeObject = se.getActiveObject();

		if(activeObject != null) {
			Region newRegion = null;

			if(activeObject instanceof Region) {
				newRegion = (Region) activeObject;
			} else if(activeObject instanceof Building) {
				newRegion = ((Building) activeObject).getRegion();
			} else if(activeObject instanceof Ship) {
				newRegion = ((Ship) activeObject).getRegion();
			} else if(activeObject instanceof Unit) {
				newRegion = ((Unit) activeObject).getRegion();
			}

			if(newRegion != null) {
				activeRegion = newRegion;

				Coordinate c = activeRegion.getCoordinate();

				if(c.z != showLevel) {
					setLevel(c.z);
				}
			}
		}

		if(log.isDebugEnabled()) {
			log.debug("Mapper.selectionChanged on region " + activeRegion);
		}

		if((se.getSelectedObjects() != null) &&
			   (se.getSelectionType() == SelectionEvent.ST_REGIONS)) {
			selectedRegions.clear();

			for(Iterator iter = se.getSelectedObjects().iterator(); iter.hasNext();) {
				Object o = iter.next();

				if(o instanceof Region) {
					Region r = (Region) o;
					selectedRegions.put(r.getID(), r);
				}
			}
		}

		if((activeObject != null) ||
			   ((se.getSelectedObjects() != null) &&
			   (se.getSelectionType() == SelectionEvent.ST_REGIONS))) {
			repaint();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isFocusTraversable() {
		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isRequestFocusEnabled() {
		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param visibleRect TODO: DOCUMENT ME!
	 * @param orientation TODO: DOCUMENT ME!
	 * @param direction TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if(orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - cellGeometry.getCellSize().width;
		} else {
			return visibleRect.height - cellGeometry.getCellSize().height;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param visibleRect TODO: DOCUMENT ME!
	 * @param orientation TODO: DOCUMENT ME!
	 * @param direction TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		if(orientation == SwingConstants.HORIZONTAL) {
			return cellGeometry.getCellSize().width;
		} else {
			return cellGeometry.getCellSize().height;
		}
	}

	/**
	 * Creates a sublist of regions to render according to the state of the given int. Values are
	 * interpreted as those of RenderingPlane.
	 *
	 * @param condition TODO: DOCUMENT ME!
	 * @param upperLeft TODO: DOCUMENT ME!
	 * @param lowerRight TODO: DOCUMENT ME!
	 * @param regionList TODO: DOCUMENT ME!
	 * @param duration TODO: DOCUMENT ME!
	 * @param paintNumber TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected List createSubList(int condition, Coordinate upperLeft, Coordinate lowerRight,
								 List regionList, int duration, int paintNumber) {
		List main = null;

		if((inPaint < 2) || (paintNumber == 0) || (duration > 0)) {
			main = regionList;
		}

		if(main == null) {
			main = CollectionFactory.createLinkedList();
		} else {
			main.clear();
		}

		if((condition & RenderingPlane.ACTIVE_OBJECT) != 0) {
			//simply add the first region found
			if(activeObject != null) {
				main.add(activeObject);
			}

			return main;
		}

		// just use visible regions as base
		if((condition & RenderingPlane.VISIBLE_REGIONS) != 0) {
			int xstart = upperLeft.x - 2;
			int xend = lowerRight.x + 1;
			int yCounter = 0;
			Coordinate c = new Coordinate(0, 0, upperLeft.z);

			for(int y = upperLeft.y + 1; y >= (lowerRight.y - 1); y--) {
				if((++yCounter % 2) == 0) {
					xstart += 1;
				}

				for(int x = xstart; x < xend; x++) {
					c.x = x;
					c.y = y;

					Region r = data.getRegion(c);

					if(r != null) {
						main.add(r);
					}
				}
			}
		}
		/* get all regions as base
		 * Note: This may be a little bit to easy since I don't know if
		 * regions are sorted by coordinates in GameData. If not the
		 * painting sequence may be bad...
		 * FIX: Have to look at the level...
		 */
		else {
			Iterator it = data.regions().values().iterator();

			while(it.hasNext()) {
				Region r = (Region) it.next();

				if(r.getCoordinate().z == upperLeft.z) {
					main.add(r);
				}
			}
		}

		// sort out according to other states, use AND
		if((condition &
			   (RenderingPlane.SELECTED_REGIONS | RenderingPlane.ACTIVE_OR_SELECTED |
			   RenderingPlane.ACTIVE_REGION | RenderingPlane.TAGGED_REGIONS)) != 0) {
			Iterator it = main.iterator();

			/* Note: On some computers this occasionally throws Concurrent Mod Exceptions.
			*       In this case stop out-sorting an return.
			*/
			try {
				while(it.hasNext()) {
					Region r = (Region) it.next();

					if(((condition & RenderingPlane.SELECTED_REGIONS) != 0) &&
						   !selectedRegions.containsKey(r.getID())) {
						it.remove();
					} else if(((condition & RenderingPlane.ACTIVE_REGION) != 0) &&
								  !(r.equals(activeRegion))) {
						it.remove();
					} else if(((condition & RenderingPlane.ACTIVE_OR_SELECTED) != 0) &&
								  !(r.equals(activeRegion) ||
								  selectedRegions.containsKey(r.getID()))) {
						it.remove();
					} else if(((condition & RenderingPlane.TAGGED_REGIONS) != 0) && !(r.hasTags())) {
						it.remove();
					}
				}
			} catch(Exception exc) {
			}
		}

		return main;
	}

	protected void setLastRegionRenderingType(int l) {
		lastRegionRenderingType = l;
	}

	protected int getLastRegionRenderingType() {
		return lastRegionRenderingType;
	}

	private static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
	private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param g TODO: DOCUMENT ME!
	 */
	protected void paintComponent(Graphics g) {
		//long start = System.currentTimeMillis();
		if(mapToScreenBounds == null) {
			return;
		}

		if(cellGeometry == null) {
			log.warn("Mapper.paint(): Unable to determine drawing area!");

			return;
		}

		Rectangle clipBounds = g.getClipBounds();

		if((clipBounds.width <= 0) || (clipBounds.height <= 0)) {
			return;
		}

		setCursor(WAIT_CURSOR);

		int paintNumber = inPaint;
		inPaint++;

		int duration = 0;
		List regList = regionList;

		//super.paint(g);
		Point offset = new Point(mapToScreenBounds.x + clipBounds.x,
								 mapToScreenBounds.y + clipBounds.y);
		Coordinate upperLeftCorner = cellGeometry.getCoordinate(offset.x, offset.y, showLevel);
		Coordinate lowerRightCorner = cellGeometry.getCoordinate(offset.x + clipBounds.width,
																 offset.y + clipBounds.height,
																 showLevel);

		// if nothing about the drawing area changed we can simply
		// paint our buffer
		if(!clipBounds.equals(currentBounds) || isRenderContextChanged()) {
			setRenderContextChanged(false);

			if((buffer == null) || !clipBounds.equals(currentBounds)) {
				setLastRegionRenderingType(-1); // full redraw

				if(buffer != null) {
					buffer.flush();
					buffer = null;
				}

				buffer = new BufferedImage(clipBounds.width, clipBounds.height,
										   BufferedImage.TYPE_INT_ARGB);
			}

			Graphics bg = buffer.getGraphics();
			bg.setColor(getBackground());
			bg.fillRect(0, 0, clipBounds.width, clipBounds.height);

			for(int planeIndex = 0; (planeIndex < Mapper.PLANE_PATH) && (planeIndex < planes.length);
					planeIndex++) {
				if(planes[planeIndex] == null) {
					continue;
				}

				MapCellRenderer renderer = planes[planeIndex].getRenderer();

				if(renderer == null) {
					continue;
				}

				// maybe another region set
				if(planes[planeIndex].getRegionTypes() != getLastRegionRenderingType()) {
					setLastRegionRenderingType(planes[planeIndex].getRegionTypes());
					regList = createSubList(getLastRegionRenderingType(), upperLeftCorner,
											lowerRightCorner, regList, duration, paintNumber);
					duration++;
				}

				if((regList == null) || (regList.size() == 0)) {
					continue;
				}

				renderer.init(data, bg, offset);

				for(Iterator iter = regList.iterator(); iter.hasNext();) {
					Object obj = iter.next();
					boolean selected = false;
					boolean active = false;

					if(obj instanceof Region) {
						Region r = (Region) obj;

						selected = selectedRegions.containsKey(r.getID());

						if(activeRegion != null) {
							active = activeRegion.equals(r);
						}
					}

					renderer.render(obj, active, selected);
				}
			}

			bg.dispose();
			bg = null;
		}

		g.drawImage(buffer, clipBounds.x, clipBounds.y, this);

		offset.x = mapToScreenBounds.x;
		offset.y = mapToScreenBounds.y;

		// there are some every time repaint things
		if(planes.length > PLANE_PATH) {
			boolean clipChanged = !clipBounds.equals(currentBounds);

			for(int planeIndex = PLANE_PATH; planeIndex < PLANE_SCHEMES; planeIndex++) {
				if(planes[planeIndex] == null) {
					continue;
				}

				MapCellRenderer renderer = planes[planeIndex].getRenderer();

				if(renderer == null) {
					continue;
				}

				if((planes[planeIndex].getRegionTypes() != getLastRegionRenderingType()) ||
					   clipChanged) {
					setLastRegionRenderingType(planes[planeIndex].getRegionTypes());
					regList = createSubList(lastRegionRenderingType, upperLeftCorner,
											lowerRightCorner, regList, duration, paintNumber);
					duration++;
				}

				if((regList == null) || (regList.size() == 0)) {
					continue;
				}

				renderer.init(data, g, offset);

				for(Iterator iter = regList.iterator(); iter.hasNext();) {
					Object obj = iter.next();
					boolean selected = false;
					boolean active = false;

					if(obj instanceof Region) {
						Region r = (Region) obj;

						selected = selectedRegions.containsKey(r.getID());

						if(activeRegion != null) {
							active = activeRegion.equals(r);
						}
					}

					renderer.render(obj, active, selected);
				}
			}

			/**
			 * Paint the schemes-plane
			 */

			// Is there any need to mark schemes?
			if((activeRegion != null) && (getLevel() == 0)) {
				if(activeRegion.getCoordinate().z == 1) {
					// "Astralraum"-region is active
					// contains all schemes of the active region
					Collection regionSchemeList = CollectionFactory.createLinkedList();

					// collect schemes
					if((activeRegion.schemes() != null) && !activeRegion.schemes().isEmpty()) {
						for(Iterator iter = activeRegion.schemes().iterator(); iter.hasNext();) {
							Scheme scheme = (Scheme) iter.next();
							Region r = data.getRegion(scheme.getID());

							if(r != null) {
								regionSchemeList.add(r);
							}
						}

						// now render the regions with the SchemeCellRenderer
						MapCellRenderer renderer = planes[PLANE_SCHEMES].getRenderer();

						if(renderer != null) {
							renderer.init(data, g, offset);

							for(Iterator iter = regionSchemeList.iterator(); iter.hasNext();) {
								renderer.render(iter.next(), true, true);
							}
						}
					}
				}
			}

			/**
			 * End of paint scheme-markings
			 */
		}

		currentBounds = clipBounds;

		if(isDeferringPainting() && !tracker.checkAll()) {
			(new Thread() {
					public void run() {
						if(!tracker.checkAll()) {
							try {
								tracker.waitForAll();
								Thread.sleep(500);
							} catch(InterruptedException e) {
							}

							currentBounds.x = -1;
							repaint();
						}
					}
				}).start();
		}

		inPaint--;
		regionList = regList;

		setCursor(DEFAULT_CURSOR);

		//log.warn((System.currentTimeMillis() - start) + "");
	}

	/**
	 * Returns the current scale or zoom factor. This value is a real factor, i.e. 1.0 means that
	 * the components are painted according to the values supplied by the underlying CellGeometry
	 * object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public float getScaleFactor() {
		return this.scaleFactor;
	}

	/**
	 * Sets the scale or zoom factor. This value is a real factor, i.e. 1.0 means that the
	 * components are painted according to the values supplied by the underlying CellGeometry
	 * object.
	 *
	 * @param scaleFactor TODO: DOCUMENT ME!
	 */
	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
		cellGeometry.setScaleFactor(scaleFactor);

		mapToScreenBounds = getMapToScreenBounds();

		if(mapToScreenBounds != null) {
			setSize(mapToScreenBounds.getSize());
			setPreferredSize(mapToScreenBounds.getSize());
		}

		if(currentBounds != null) {
			currentBounds.setSize(-1, -1);
		}

		for(int planeIndex = 0; planeIndex < planes.length; planeIndex++) {
			if(planes[planeIndex] == null) {
				continue;
			}

			MapCellRenderer renderer = planes[planeIndex].getRenderer();

			if(renderer != null) {
				renderer.scale(scaleFactor);
			}
		}
	}

	/**
	 * Returns a list containing all the different levels ('Eressea-Ebenen') this Mapper knows of.
	 * The list contains Integer objects stating the level number.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getLevels() {
		List levels = CollectionFactory.createLinkedList();

		if(data != null) {
			Iterator iter = data.regions().values().iterator();

			while(iter.hasNext()) {
				Coordinate c = ((Region) iter.next()).getCoordinate();
				Integer i = new Integer(c.z);

				if(levels.contains(i) == false) {
					levels.add(i);
				}
			}
		}

		return levels;
	}

	/**
	 * Sets the level ('Eressea-Ebene') this Mapper knows of. The list contains Integer objects
	 * stating the level number.
	 *
	 * @param level TODO: DOCUMENT ME!
	 */
	public void setLevel(int level) {
		showLevel = level;
		mapToScreenBounds = getMapToScreenBounds();
		setSize(mapToScreenBounds.getSize());
		setPreferredSize(getSize());

		// activeRegion = null;
		if(currentBounds != null) {
			currentBounds.setSize(-1, -1);
		}

		invalidate();
		repaint();
	}

	/**
	 * Returns the level ('Eressea-Ebene') this Mapper actually displays.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLevel() {
		return showLevel;
	}

	/**
	 * Get the selected Regions. The returned map can be empty but is never null.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getSelectedRegions() {
		return selectedRegions;
	}

	/**
	 * Get the active region.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Region getActiveRegion() {
		return activeRegion;
	}

	/**
	 * Returns the bounds of the specified region on this component.
	 *
	 * @param cell the coordinate of the region to be evaluated.
	 *
	 * @return the bounds (the upper left corner and the size) of the region cell in component
	 * 		   coordinates.
	 */
	public Rectangle getCellRect(Coordinate cell) {
		Rectangle bounds = null;

		if(cellGeometry != null) {
			bounds = cellGeometry.getCellRect(cell.x, cell.y);
			bounds.translate(-mapToScreenBounds.x, -mapToScreenBounds.y);
		} else {
			log.warn("Mapper.getCellRect(): Unable to determine cell bounds!");
		}

		return bounds;
	}

	/**
	 * Returns the coordinate of the region that is at the center of the currently displayed area.
	 *
	 * @param clipBounds the bounds indicating which part of the mappers drawing area is actually
	 * 		  visible.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Coordinate getCenter(Rectangle clipBounds) {
		Coordinate center = null;

		if(mapToScreenBounds != null) {
			Point centerScreen = new Point(mapToScreenBounds.x + clipBounds.x +
										   (clipBounds.width / 2),
										   mapToScreenBounds.y + clipBounds.y +
										   (clipBounds.height / 2));

			if(cellGeometry != null) {
				center = cellGeometry.getCoordinate(centerScreen.x, centerScreen.y, showLevel);
			} else {
				log.warn("Mapper.getCenter(): Unable to determine drawing area!");
			}
		}

		return center;
	}

	/**
	 * Returns the location (upper left corner) of the drawing area so that a certain region is at
	 * the center of the view port.
	 *
	 * @param viewSize the size of the mappers viewport, i.e. the size of the part of the mappers
	 * 		  drawing area that is actually visible.
	 * @param center the coordinate to center on.
	 *
	 * @return a Point with x and y so that a view port of size viewSize is centered over the
	 * 		   specified region center.
	 */
	public Point getCenteredViewPosition(Dimension viewSize, Coordinate center) {
		Point viewPos = null;

		if((cellGeometry != null) && (viewSize != null) && (center != null)) {
			// get the cell position as relative screen coordinates
			Rectangle cellPos = cellGeometry.getCellRect(center.x, center.y);

			// transform cell position into absolute screen coordinates on this component
			cellPos.translate(-mapToScreenBounds.x, -mapToScreenBounds.y);

			// shift the cell position by half a cell size to get to its center
			cellPos.translate(cellGeometry.getCellSize().width / 2,
							  cellGeometry.getCellSize().height / 2);

			// now get the view port
			viewPos = new Point(cellPos.x - (viewSize.width / 2), cellPos.y -
								(viewSize.height / 2));
		} else {
			log.warn("Mapper.getCenteredViewPosition(): Unable to determine drawing area!");
		}

		return viewPos;
	}

	/**
	 * Get the cell geometry from the resources and make all renderers that use images reload the
	 * graphics files.
	 */
	public void reloadGraphicSet() {
		cellGeometry = new CellGeometry("cellgeometry.txt");

		for(int i = 0; i < planes.length; i++) {
			if((planes[i] != null) && (planes[i].getRenderer() != null)) {
				planes[i].getRenderer().setCellGeometry(cellGeometry);
			}
		}

		setScaleFactor(getScaleFactor());
		repaint();
	}

	/**
	 * Creates a preferences panel allowing to configure this component.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter getPreferencesAdapter() {
		return new MapperPreferences(this);
	}

	/**
	 * Returns whether deferred painting after loading and scaling images is used or not. Together
	 * with the ImageCellRenderer class this option tells the mapper/renderer whether to scale
	 * images synchronously and introduce delays on painting or to scale images asynchronously and
	 * trigger a redraw after a short amount of time.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isDeferringPainting() {
		return (tracker != null);
	}

	/**
	 * Activates or de-activates deferred painting after loading and scaling images. Together with
	 * the ImageCellRenderer class this option tells the mapper/renderer whether to scale images
	 * synchronously and introduce delays on painting or to scale images asynchronously and
	 * trigger a redraw after a short amount of time.
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void deferPainting(boolean bool) {
		if(bool != isDeferringPainting()) {
			if(bool) {
				tracker = new MediaTracker(this);
			} else {
				tracker = null;
			}

			ImageCellRenderer.setTracker(tracker);
			settings.setProperty("Mapper.deferPainting", String.valueOf(bool));
		}
	}

	/**
	 * Returns a rectangle that indicates the offset and the size of the whole map that is formed
	 * by data.regions(). The values returned are given in pixels as returned by CellGeometry,
	 * i.e. if there is only region 0, 0 then the returned rectangle would be : x=0, y=0,
	 * width=cellwidth, height=cellheight.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private Rectangle getMapToScreenBounds() {
		if((data == null) || (cellGeometry == null)) {
			return null;
		}

		Point upperLeft = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
		Point lowerRight = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

		Iterator iter = data.regions().values().iterator();

		while(iter.hasNext()) {
			Coordinate c = ((Region) iter.next()).getCoordinate();

			if(c.z == showLevel) {
				int x = cellGeometry.getCellPositionX(c.x, c.y);
				int y = cellGeometry.getCellPositionY(c.x, c.y);
				upperLeft.x = Math.min(x, upperLeft.x);
				upperLeft.y = Math.min(y, upperLeft.y);
				lowerRight.x = Math.max(x, lowerRight.x);
				lowerRight.y = Math.max(y, lowerRight.y);
			}
		}

		lowerRight.x += (cellGeometry.getCellSize().width + 1);
		lowerRight.y += (cellGeometry.getCellSize().height + 1);

		return new Rectangle(upperLeft,
							 new Dimension(lowerRight.x - upperLeft.x, lowerRight.y - upperLeft.y));
	}

	protected RenderingPlane[] initRenderingPlanes() {
		RenderingPlane p[] = new RenderingPlane[PLANES];
		p[PLANE_REGION] = new RenderingPlane(PLANE_REGION, getString("plane.region.name"));
		p[PLANE_REGION].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." +
																	 PLANE_REGION,
																	 "com.eressea.swing.map.RegionImageCellRenderer")));

		p[PLANE_BORDER] = new RenderingPlane(PLANE_BORDER, getString("plane.border.name"));
		p[PLANE_BORDER].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." +
																	 PLANE_BORDER,
																	 "com.eressea.swing.map.BorderCellRenderer")));

		p[PLANE_BUILDING] = new RenderingPlane(PLANE_BUILDING, getString("plane.building.name"));
		p[PLANE_BUILDING].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." +
																	   PLANE_BUILDING,
																	   "com.eressea.swing.map.BuildingCellRenderer")));

		p[PLANE_SHIP] = new RenderingPlane(PLANE_SHIP, getString("plane.ship.name"));
		p[PLANE_SHIP].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." + PLANE_SHIP,
																   "com.eressea.swing.map.ShipCellRenderer")));

		p[PLANE_TEXT] = new RenderingPlane(PLANE_TEXT, getString("plane.text.name"));
		p[PLANE_TEXT].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." + PLANE_TEXT,
																   "com.eressea.swing.map.TextCellRenderer")));

		p[PLANE_PATH] = new RenderingPlane(PLANE_PATH, getString("plane.path.name"),
										   RenderingPlane.ACTIVE_OBJECT);
		p[PLANE_PATH].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." + PLANE_PATH,
																   "com.eressea.swing.map.PathCellRenderer")));

		p[PLANE_HIGHLIGHT] = new RenderingPlane(PLANE_HIGHLIGHT, getString("plane.highlight.name"),
												RenderingPlane.VISIBLE_REGIONS |
												RenderingPlane.ACTIVE_OR_SELECTED);
		p[PLANE_HIGHLIGHT].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." +
																		PLANE_HIGHLIGHT,
																		"com.eressea.swing.map.HighlightImageCellRenderer")));

		p[PLANE_MARKINGS] = new RenderingPlane(PLANE_MARKINGS, getString("plane.markings.name"),
											   RenderingPlane.VISIBLE_REGIONS |
											   RenderingPlane.TAGGED_REGIONS);
		p[PLANE_MARKINGS].setRenderer(getRenderer(settings.getProperty("Mapper.Planes." +
																	   PLANE_MARKINGS,
																	   "com.eressea.swing.map.MarkingsImageCellRenderer")));

		p[PLANE_SCHEMES] = new RenderingPlane(PLANE_SCHEMES, getString("plane.schemes.name"),
											  RenderingPlane.VISIBLE_REGIONS);
		p[PLANE_SCHEMES].setRenderer(getRenderer("com.eressea.swing.map.SchemeCellRenderer"));

		return p;
	}

	private Collection initAvailableRenderers(CellGeometry geo, Properties settings,
											  Collection cRenderers) {
		Collection renderers = CollectionFactory.createLinkedList();
		renderers.add(new RegionImageCellRenderer(geo, context));
		renderers.add(new RegionShapeCellRenderer(geo, context));
		renderers.add(new AdvancedRegionShapeCellRenderer(geo, context));
		renderers.add(new BorderCellRenderer(geo, context));
		renderers.add(new BuildingCellRenderer(geo, context));
		renderers.add(new ShipCellRenderer(geo, context));
		renderers.add(new TextCellRenderer(geo, context));
		renderers.add(new TradeTextCellRenderer(geo, context));
		renderers.add(new AdvancedTextCellRenderer(geo, context));
		renderers.add(new PathCellRenderer(geo, context));
		renderers.add(new HighlightImageCellRenderer(geo, context));
		renderers.add(new HighlightShapeCellRenderer(geo, context));
		renderers.add(new MarkingsImageCellRenderer(geo, context));
		renderers.add(new SchemeCellRenderer(geo, context));

		if(cRenderers != null) {
			for(Iterator iter = cRenderers.iterator(); iter.hasNext();) {
				MapCellRenderer map = (MapCellRenderer) iter.next();

				if(map instanceof HexCellRenderer) {
					((HexCellRenderer) map).settings = settings;
				}

				map.setCellGeometry(geo);
				renderers.add(map);
			}
		}

		// look for Mapper-aware renderers. Add Mapper if Interface MapperAware is implemented
		for(Iterator iter = renderers.iterator(); iter.hasNext();) {
			Object o = iter.next();

			if(o instanceof MapperAware) {
				((MapperAware) o).setMapper(this);
			}
		}

		return renderers;
	}

	private MapCellRenderer getRenderer(String className) {
		MapCellRenderer renderer = null;

		if(!className.equals("none")) {
			for(Iterator iter = availableRenderers.iterator(); iter.hasNext();) {
				MapCellRenderer r = (MapCellRenderer) iter.next();

				if(r.getClass().getName().equals(className)) {
					renderer = r;

					break;
				}
			}
		}

		return renderer;
	}

	/**
	 * Getter for property cellGeometry.
	 *
	 * @return Value of property cellGeometry.
	 */
	public CellGeometry getCellGeometry() {
		return cellGeometry;
	}

	/**
	 * Getter for property renderContextChanged.
	 *
	 * @return Value of property renderContextChanged.
	 */
	public static boolean isRenderContextChanged() {
		return renderContextChanged;
	}

	/**
	 * Setter for property renderContextChanged.
	 *
	 * @param r New value of property renderContextChanged.
	 */
	public static void setRenderContextChanged(boolean r) {
		renderContextChanged = r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	public void setShowTooltip(boolean b) {
		showTooltip = b;

		if(b) {
			ToolTipManager.sharedInstance().registerComponent(this);
			settings.setProperty("Mapper.showTooltips", "true");
		} else {
			ToolTipManager.sharedInstance().unregisterComponent(this);
			settings.setProperty("Mapper.showTooltips", "false");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingTooltip() {
		return showTooltip;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTooltipDefinition() {
		return tooltipDefinitionString;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tdef TODO: DOCUMENT ME!
	 */
	public void setTooltipDefinition(String tdef) {
		tooltipDefinitionString = tdef;
		tooltipDefinition = ReplacerHelp.createReplacer(tdef);
		settings.setProperty("Mapper.ToolTip.Definition", tdef);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getAllTooltipDefinitions() {
		String s = settings.getProperty("Mapper.ToolTip.Definitions",
										"Standard~<html><font=-1>§rname§</font></html>");
		StringTokenizer st = new StringTokenizer(s, "~");
		int j = st.countTokens();

		if((j % 2) == 1) {
			j--;
		}

		List al = CollectionFactory.createArrayList(j);

		if(st.countTokens() > 1) {
			for(int i = 0; i < (j / 2); i++) {
				al.add(st.nextToken());
				al.add(st.nextToken());
			}
		}

		return al;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setAllTooltipDefinitions(List l) {
		StringBuffer buf = new StringBuffer();

		if(l.size() > 1) {
			Iterator it = l.iterator();

			for(int i = 0; i < (l.size() / 2); i++) {
				buf.append(it.next());
				buf.append('~');
				buf.append(it.next());

				if(i < (l.size() - 1)) {
					buf.append('~');
				}
			}
		} else {
			buf.append("Standard~<html><font=-1>§rname§</font></html>");
		}

		settings.setProperty("Mapper.ToolTip.Definitions", buf.toString());

		conMenu.updateTooltips(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param def TODO: DOCUMENT ME!
	 */
	public void addTooltipDefinition(String name, String def) {
		settings.setProperty("Mapper.ToolTip.Definitions",
							 settings.getProperty("Mapper.ToolTip.Definitions",
												  "Standard~<html><font=-1>§rname§</font></html>") +
							 "~" + name + "~" + def);

		conMenu.updateTooltips(this);
	}

	/*
	 * desktop init methods
	 *
	 * NOTE: Since the mapper panel is registered as the MAP component, these
	     * methods are not called by the desktop manager for the normal map. They have
	 * to be delegated to this.
	 *
	 * But the MINIMAP component uses this feature itself since it is kind of a
	 * stand-alone although it is managed by the mapper panel.
	 */

	/**
	 * Returns the current configuration of this mapper panel. The current implementation divides
	 * all the information by "_". First the scale factor is stored, then planes(plane index,
	 * renderer class name, renderer configuration).
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getComponentConfiguration() {
		Iterator it = getPlanes().iterator();
		StringBuffer buf = new StringBuffer();
		buf.append(getScaleFactor());
		buf.append('_');

		while(it.hasNext()) {
			RenderingPlane rp = (RenderingPlane) it.next();

			if((rp == null) || (rp.getRenderer() == null)) {
				continue;
			}

			buf.append(rp.getIndex());
			buf.append('_');
			buf.append(((Object) rp.getRenderer()).getClass().getName());
			buf.append('_');

			if(rp.getRenderer() instanceof Initializable) {
				String config = ((Initializable) rp.getRenderer()).getComponentConfiguration();

				if((config == null) || (config.length() < 1)) {
					buf.append("NI");
				} else {
					buf.append(config);
				}
			} else {
				buf.append("NI");
			}

			if(it.hasNext()) {
				buf.append('_');
			}
		}

		return buf.toString();
	}

	/**
	 * Implemented for interface Initializable to set configuration data to this component.
	 *
	 * @param p1 the configuration string from magellan_desktop.ini
	 */
	public void initComponent(String p1) {
		if((p1 == null) || (p1.length() == 0)) {
			return;
		}

		StringTokenizer st = new StringTokenizer(p1, "_");

		try {
			setScaleFactor(Float.parseFloat(st.nextToken()));
		} catch(Exception exc) {
		}

		while(st.hasMoreTokens()) {
			try {
				String index = st.nextToken();
				int iindex = Integer.parseInt(index);
				String className = st.nextToken();
				String config = st.nextToken();
				Collection col = getRenderers(iindex);

				if(col != null) {
					Iterator it = col.iterator();

					while(it.hasNext()) {
						MapCellRenderer mcp = (MapCellRenderer) it.next();

						if(className.equals(((Object) mcp).getClass().getName())) {
							setRenderer(mcp, iindex);

							if((config != null) && !config.equals("NI") &&
								   (mcp instanceof Initializable)) {
								((Initializable) mcp).initComponent(config);
							}

							break;
						}
					}
				}
			} catch(Exception exc) {
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
			defaultTranslations.put("plane.region.name", "Regions");
			defaultTranslations.put("plane.border.name", "Roads");
			defaultTranslations.put("plane.building.name", "Buildings");
			defaultTranslations.put("plane.ship.name", "Ships");
			defaultTranslations.put("plane.text.name", "Text");
			defaultTranslations.put("plane.path.name", "Paths");
			defaultTranslations.put("plane.highlight.name", "Markers");
			defaultTranslations.put("plane.markings.name", "Addit. Markers");
			defaultTranslations.put("plane.schemes.name", "Schemes");
		}

		return defaultTranslations;
	}
}
