// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.eressea.Coordinate;
import com.eressea.GameData;
import com.eressea.HasRegion;
import com.eressea.HotSpot;
import com.eressea.ID;
import com.eressea.IntegerID;
import com.eressea.Island;
import com.eressea.Region;
import com.eressea.demo.desktop.ExtendedShortcutListener;
import com.eressea.demo.desktop.Initializable;
import com.eressea.demo.desktop.ShortcutListener;
import com.eressea.event.EventDispatcher;
import com.eressea.event.SelectionListener;
import com.eressea.swing.map.CellGeometry;
import com.eressea.swing.map.HexCellRenderer;
import com.eressea.swing.map.Mapper;
import com.eressea.swing.map.Minimapper;
import com.eressea.swing.map.RegionImageCellRenderer;
import com.eressea.swing.preferences.ExtendedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * A panel holding all UI components related to an Eressea map.
 * The component contains a Mapper object and additional controls. The
 * policy of this class is not to be concerned with map details like
 * coordinates etc. as much as possible and to provide a general and
 * flexible interface to the mapper.
 */
public class MapperPanel extends InternationalizedDataPanel implements ActionListener, SelectionListener, ChangeListener, ExtendedShortcutListener, PreferencesFactory, Initializable {
	private final static Logger log = Logger.getInstance(MapperPanel.class);
	/** The map component in this panel. */
	private Mapper mapper = null;
	private JScrollPane scpMapper = null;
	private JLabel lblLevel = null;
	private JComboBox cmbLevel = null;
	private JSlider sldScaling = null;
	private JComboBox cmbHotSpots = null;
	private Timer timer = null;
	private Point dragStart = null;
	private boolean dragValidated = false;
	
	// minimap components
	protected Minimapper minimap;
	protected JScrollPane minimapPane;
	private CellGeometry minimapGeometry;
	protected boolean resizeMinimap;
	protected MinimapScaler minimapScaler;
	protected float lastScale = 1;
	
	//shortcuts
	private java.util.List shortcuts;
	private TooltipShortcut tooltipShortcut;
	
	/**
	 * GameData event handler.
	 */
	public void gameDataChanged(com.eressea.event.GameDataEvent e) {
		data = e.getGameData();
		mapper.gameDataChanged(e);
		minimap.gameDataChanged(e);
		List levels = mapper.getLevels();
		lblLevel.setVisible(levels.size() > 1);
		cmbLevel.setVisible(levels.size() > 1);
		cmbLevel.removeAllItems();
		for (int i = 0; i < levels.size(); i++) {
			cmbLevel.addItem(levels.get(i));
		}
		if (cmbLevel.getItemCount() > 0) {
			cmbLevel.setSelectedIndex(0);
		}
		
		// fill hot spot combo
		cmbHotSpots.removeAllItems();
		if (data != null && data.hotSpots() != null) {
			List hotSpots = CollectionFactory.createLinkedList(data.hotSpots().values());
			Collections.sort(hotSpots, new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((HotSpot)o1).getName().compareTo(((HotSpot)o2).getName());
				}
			});
			cmbHotSpots.setModel(new DefaultComboBoxModel(hotSpots.toArray()));
		}
		cmbHotSpots.setVisible(cmbHotSpots.getItemCount() > 0);
		
		rescale();
		minimapPane.doLayout();
		minimapPane.repaint();
	}
	
	/**
	 * Selection event handler, updating the map if a new region
	 * is selected.
	 */
	public void selectionChanged(com.eressea.event.SelectionEvent se) {
		if(log.isDebugEnabled()) {
			log.debug("MapperPanel.selectionChanged called with "+se.getActiveObject());
		}
		
		// update the currently selected item in the level combo box
		Object o = se.getActiveObject();
		Coordinate newCenter = null;
		if (o != null) {
			Region newCenterRegion = null;
			if (o instanceof Region) {
				newCenterRegion = (Region)o;
			} else
				if (o instanceof HasRegion) {
					newCenterRegion = ((HasRegion)o).getRegion();
				}
			if (newCenterRegion != null) {
				newCenter = (Coordinate)newCenterRegion.getID();
			}
		}
		
		if (newCenter != null) {
			if (cmbLevel.isVisible()) {
				Integer level = new Integer(newCenter.z);
				if (level.intValue()!=mapper.getLevel()) {
					cmbLevel.setSelectedItem(level);
				}
			}
			// re-center the map if necessary
			// do this later, mapper probably does not
			// yet know the right active region
			class CenterRunner implements Runnable {
				public Coordinate center = null;
				public CenterRunner(Coordinate c) {
					center = c;
				}
				public void run() {
					if(log.isDebugEnabled()) {
						log.debug("MapperPanel.selectionChanged: Running CenterRunner on "+center);
					}
					Rectangle cellRect = mapper.getCellRect(center);
					if (cellRect != null) {
						if (!scpMapper.getViewport().getViewRect().contains(cellRect)) {
							setCenter(center);
						}
					}
					cellRect = minimap.getCellRect(center);
					if (cellRect != null) {
						if (!minimapPane.getViewport().getViewRect().contains(cellRect)) {
							setMinimapCenter(center);
						}
					}
				}
			}
			
			SwingUtilities.invokeLater(new CenterRunner(newCenter));
		} else
			if (o != null && o instanceof Island) {
				// center to island
				Island island = (Island) o;
				if(!island.regions().isEmpty()) {
					// first set right level
					Region r = (Region) island.regions().iterator().next();
					Coordinate coord = (Coordinate)r.getID();
					if (cmbLevel.isVisible()) {
						Integer level = new Integer(coord.z);
						if (level.intValue() != mapper.getLevel()) {
							cmbLevel.setSelectedItem(level);
						}
					}
					
					// then set center rectangle on right pane
					class ParamRunnable implements Runnable {
						Island island;
						Rectangle centerRect;
						ParamRunnable(Island i) {
							island = i;
						}
						public void run() {
							Rectangle islandBounds = null;
							for (Iterator iter = island.regions().iterator(); iter.hasNext();) {
								Region r = (Region)iter.next();
								Coordinate coord = r.getCoordinate();
								if (islandBounds == null) {
									islandBounds = mapper.getCellRect(coord);
								}else{
									islandBounds.add( mapper.getCellRect(coord) );
								}
							}
							Rectangle centerRect = islandBounds;
							if(log.isDebugEnabled()) {
								log.debug("MapperPanel.selectionChanged: Running ParamRunnable with "+centerRect);
							}
							if (!scpMapper.getViewport().getViewRect().contains(centerRect)) {
								/* FIX these numbers should get some bounding */
								centerRect.x -= (scpMapper.getViewport().getViewRect().getWidth() -
								centerRect.getWidth())/2;
								centerRect.y -= (scpMapper.getViewport().getViewRect().getHeight() -
								centerRect.getHeight())/2;
								scpMapper.getViewport().setViewPosition(centerRect.getLocation());
							}
						}
					}
					
					SwingUtilities.invokeLater( new ParamRunnable(island) );
				}
			}
	}
	
	/**
	 * Action event handler for timer events related to the scaling
	 * slider.
	 */
	Cursor waitCursor    = new Cursor(Cursor.WAIT_CURSOR);
	Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	public void actionPerformed(ActionEvent ae) {
		setCursor(waitCursor);
		mapper.setCursor(waitCursor);
		Coordinate center = mapper.getCenter(scpMapper.getViewport().getViewRect());
		mapper.setScaleFactor((float)(sldScaling.getValue() / 50.0 + 0.3));
		setCenter(center);
		this.repaint();
		setCursor(defaultCursor);
		mapper.setCursor(defaultCursor);
	}
	
	/**
	 * Creates a new <tt>MapperPanel</tt> object.
	 */
	public MapperPanel(EventDispatcher ed, Properties p,Collection customRenderers,CellGeometry geo) {
		super(ed, p);
		final MapperPanel thisMapperPanel = this;
		
		initMinimap();
		
		
		ed.addSelectionListener(this);
		
		setLayout(new BorderLayout());
		add(getMainPane(customRenderers,geo), BorderLayout.CENTER);
		
		// register own mouse listener for letting the user drag the
		// map
		mapper.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				dragStart = e.getPoint();
				dragValidated = false;
			}
			
			public void mouseReleased(MouseEvent e) {
				if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0 ||
				!dragValidated ||
				(e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					return;
				}
				
				Rectangle bounds = new Rectangle(dragStart.x - 2, dragStart.y - 2, 4, 4);
				if (bounds.contains(e.getPoint())) {
					return;
				}
				
				JViewport viewport = scpMapper.getViewport();
				Point viewPos = viewport.getViewPosition();
				dragStart.translate(-e.getPoint().x, -e.getPoint().y);
				dragStart.translate(viewPos.x, viewPos.y);
				if (dragStart.x < 0) {
					dragStart.x = 0;
				} else {
					int maxX = mapper.getWidth() - viewport.getWidth();
					if (dragStart.x > maxX) {
						dragStart.x = maxX;
					}
				}
				if (dragStart.y < 0) {
					dragStart.y = 0;
				} else {
					int maxY = mapper.getHeight() - viewport.getHeight();
					if (dragStart.y > maxY) {
						dragStart.y = maxY;
					}
				}
				viewport.setViewPosition(dragStart);
			}
		});
		
		// use a mouse motion listener to confirm that the user actually moved the mouse while dragging
		// to avoid unintended drag effects
		mapper.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (!dragValidated) {
					Rectangle bounds = new Rectangle(dragStart.x - 2, dragStart.y - 2, 4, 4);
					if (!bounds.contains(e.getPoint())) {
						dragValidated = true;
					}
				}
			}
		});
		
		
		// initialize Shortcuts
		tooltipShortcut = new TooltipShortcut();
		
		shortcuts=CollectionFactory.createArrayList(6);
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_2,KeyEvent.CTRL_MASK));
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_2,KeyEvent.ALT_MASK));
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_H,KeyEvent.CTRL_MASK));
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_H,KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		// fog of war
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
		// tooltips
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
	}
	
	protected void initMinimap() {
		minimap=new Minimapper(dispatcher,settings);
		minimapGeometry=minimap.getCellGeometry();
		Dimension d=minimapGeometry.getCellSize();
		int size=10;
		try{size=Integer.parseInt(settings.getProperty("Minimap.Scale"));}
		catch(Exception exc) {}
		lastScale = (float)(size/(float)d.width);
		minimap.setScaleFactor(lastScale);
		minimapPane=new JScrollPane(minimap);
		resizeMinimap = settings.getProperty("Minimap.AutoScale", "false").equals("true");
		minimapScaler = new MinimapScaler();
		minimapPane.addComponentListener(minimapScaler);
	}
	
	public void setMinimapScale(int scale) {
		Dimension size = minimapGeometry.getCellSize();
		minimap.setScaleFactor((float)scale*minimapGeometry.getScaleFactor()/(float)size.width);
		minimapPane.doLayout();
		minimapPane.repaint();
	}
	
	public int getMinimapScale() {
		return minimapGeometry.getCellSize().width;
	}
	
	public void setAutoScaling(boolean bool) {
		resizeMinimap = bool;
		settings.setProperty("Minimap.AutoScale", bool?"true":"false");
		if (bool) {
			rescale();
		}
	}
	
	public boolean isAutoScaling() {
		return resizeMinimap;
	}
	
	protected void rescale() {
		minimapScaler.componentResized(new ComponentEvent(minimapPane, ComponentEvent.COMPONENT_RESIZED));
	}
	
	public void synchronizeMinimap() {
		minimap.synchronizeColors();
	}
	
	public void setMinimapMode(int mode) {
		minimap.setPaintMode(mode);
	}
	
	public int getMinimapMode() {
		return minimap.getPaintMode();
	}
	
	/**
	 * Sets a new scaling factor for the map.
	 *
	 * @param fScale the new scaling factor, values may range
	 * 	from 0.3 to 2.3.
	 */
	public void setScaleFactor(float fScale) {
		fScale = Math.max(0.3f, fScale);
		fScale = Math.min(fScale, 100.0f / 50.0f + 0.3f);
		sldScaling.setValue((int)((fScale - 0.3) * 50.0));
		mapper.setScaleFactor(fScale);
	}
	
	/**
	 * Returns the current scaling factor applied to the map.
	 */
	public float getScaleFactor() {
		return mapper.getScaleFactor();
	}
	
	/**
	 * Set a cell renderer object for its default rendering plane.
	 * See com.eressea.swing.map.Mapper for further reference.
	 *
	 * @param renderer the object responsible for rendering a
	 *	graphical representation of regions.
	 * @param plane the plane the renderer will draw to. Lower
	 *	planes are painted over by higher planes.
	 */
	public void setRenderer(HexCellRenderer renderer) {
		mapper.setRenderer(renderer);
	}
	
	/**
	 * Set a cell renderer object for a certain plane of the map.
	 * See com.eressea.swing.map.Mapper for further reference.
	 *
	 * @param renderer the object responsible for rendering a
	 *	graphical representation of regions.
	 * @param plane the plane the renderer will draw to. Lower
	 *	planes are painted over by higher planes.
	 */
	public void setRenderer(HexCellRenderer renderer, int plane) {
		mapper.setRenderer(renderer, plane);
	}
	
	/**
	 * Get the selected Regions. The returned map can be empty but is
	 * never null.
	 */
	public Map getSelectedRegions() {
		return mapper.getSelectedRegions();
	}
	
	/**
	 * Get the active region.
	 */
	public Region getActiveRegion() {
		return mapper.getActiveRegion();
	}
	
	public int getLevel() {
		return mapper.getLevel();
	}
	
	public void setLevel(int level) {
		mapper.setLevel(level);
	}
	
	/**
	 * Centers the map on a certain region.
	 *
	 * @param center the coordinate of the region to center the map
	 * on.
	 */
	public void setCenter(Coordinate center) {
		
		Point newViewPosition = mapper.getCenteredViewPosition(scpMapper.getSize(), center);
		if (newViewPosition != null) {
			Dimension size = scpMapper.getViewport().getSize();
			newViewPosition.x = Math.max(0, newViewPosition.x);
			newViewPosition.x = Math.min(Math.max(0, mapper.getWidth() - size.width), newViewPosition.x);
			newViewPosition.y = Math.max(0, newViewPosition.y);
			newViewPosition.y = Math.min(Math.max(0, mapper.getHeight() - size.height), newViewPosition.y);
			scpMapper.getViewport().setViewPosition(newViewPosition);
		}
	}
	
	/**
	 * Centers the minimap on a certain region.
	 *
	 * @param center the coordinate of the region to center the map
	 * on.
	 */
	public void setMinimapCenter(Coordinate center) {
		
		Point newViewPosition = minimap.getCenteredViewPosition(minimapPane.getSize(), center);
		if (newViewPosition != null) {
			Dimension size = minimapPane.getViewport().getSize();
			newViewPosition.x = Math.max(0, newViewPosition.x);
			newViewPosition.x = Math.min(Math.max(0, minimap.getWidth() - size.width), newViewPosition.x);
			newViewPosition.y = Math.max(0, newViewPosition.y);
			newViewPosition.y = Math.min(Math.max(0, minimap.getHeight() - size.height), newViewPosition.y);
			minimapPane.getViewport().setViewPosition(newViewPosition);
		}
	}
	
	/**
	 * Assign the currently visible part of the map (the region at
	 * the center), a hot spot, an id and add it to the list of
	 * hot spots.
	 *
	 * @param id the id to assign to the hot spot.
	 */
	public void assignHotSpot(String name) {
		Coordinate center = mapper.getCenter(scpMapper.getViewport().getViewRect());
		if (center != null) {
			ID id = getNewHotSpotID();
			if (id == null) {
				log.warn("MapperPanel.assignHotSpot(): unable to determine free id for new hot spot!");
				return;
			}
			HotSpot h = new HotSpot(id);
			h.setName(name);
			h.setCenter(center);
			data.setHotSpot(h);
			
			List hotSpots = CollectionFactory.createLinkedList(data.hotSpots().values());
			Collections.sort(hotSpots, new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((HotSpot)o1).getName().compareTo(((HotSpot)o2).getName());
				}
			});
			cmbHotSpots.setModel(new DefaultComboBoxModel(hotSpots.toArray()));
			
			if (cmbHotSpots.getItemCount() != 0) {
				cmbHotSpots.setVisible(true);
			}
			if (cmbHotSpots.getFontMetrics(cmbHotSpots.getFont()).stringWidth(name) > cmbHotSpots.getMinimumSize().width) {
				cmbHotSpots.setMinimumSize(new Dimension(cmbHotSpots.getFontMetrics(cmbHotSpots.getFont()).stringWidth(name), cmbHotSpots.getMinimumSize().height));
			}
		}
	}
	
	/**
	 * Center the map on the specified hot spot.
	 *
	 * @param h the hot spot to move the map to.
	 */
	public void showHotSpot(HotSpot h) {
		
		// switch planes
		if (mapper.getActiveRegion() == null || ((Coordinate)mapper.getActiveRegion().getID()).z != h.getCenter().z) {
			if (cmbLevel.isVisible()) {
				cmbLevel.setSelectedItem(new Integer(h.getCenter().z));
			}
		}
		
		// re-center mapper
		Point viewPos = mapper.getCenteredViewPosition(scpMapper.getSize(), h.getCenter());
		if (viewPos != null) {
			scpMapper.getViewport().setViewPosition(viewPos);
			mapper.requestFocus();
		}
	}
	
	/**
	 * Remove the specified hot spot.
	 *
	 * @param h the hot spot to remove.
	 */
	public void removeHotSpot(HotSpot h) {
		data.removeHotSpot(h.getID());
		cmbHotSpots.removeItem(h);
		if (cmbHotSpots.getItemCount() == 0) {
			cmbHotSpots.setVisible(false);
		}
	}
	
	/**
	 * Get the cell geometry from the resources and make
	 * all renderers that use images reload the graphics files.
	 */
	public void reloadGraphicSet() {
		mapper.reloadGraphicSet();
	}
	
	/**
	 * Stores the region that is at the center of the currently
	 * visible area.
	 */
	public void quit() {
		settings.setProperty("Map.scaleFactor", Float.toString(getScaleFactor()));
		Coordinate center = mapper.getCenter(scpMapper.getViewport().getViewRect());
		if (center != null) {
			settings.setProperty("Map.lastCenterRegion", center.toString());
		}
		settings.setProperty("Minimap.Scale",String.valueOf(getMinimapScale()));
	}
	
	/**
	 * Returns the component that draws the map.
	 */
	public Component getView() {
		return mapper;
	}
	
	/**
	 * Creates random integer values until one is not already used
	 * as a key in the game data's hot spot map.
	 *
	 * @return an integer the Integer representation of which is not
	 * already used as a key in the current game data's hot spot map.
	 */
	private ID getNewHotSpotID() {
		ID i = null;
		do {
			i = IntegerID.create((int)(Math.random() * Integer.MAX_VALUE));
		} while (data.getHotSpot(i) != null);
		return i;
	}
	
	private Container getMainPane(Collection renderers,CellGeometry geo) {
		
		mapper = new Mapper(dispatcher, settings, renderers,geo);
		scpMapper = new JScrollPane(mapper, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JLabel lblScaling = new JLabel(getString("lbl.zoom.caption"));
		sldScaling = new JSlider(SwingConstants.HORIZONTAL);
		sldScaling.setMajorTickSpacing(5);
		sldScaling.setPaintTicks(true);
		sldScaling.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent ce) {
					/*
					 * Timer support routine for events related to the scaling
					 * slider.
					 */
					if(timer == null) {
						timer = new Timer(200,MapperPanel.this);
						timer.setRepeats(false);
					}
					// always restart to prevent refreshing while moving around
					timer.restart();
				}
			});
		
		sldScaling.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
						sldScaling.setSnapToTicks(true);
					}
				}
				public void keyReleased(KeyEvent e) {
					if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
						sldScaling.setSnapToTicks(false);
					}
				}
			});
		lblScaling.setLabelFor(sldScaling);
		
		lblLevel = new JLabel(getString("lbl.level.caption"));
		cmbLevel = new JComboBox(mapper.getLevels().toArray());
		if (cmbLevel.getItemCount() > 0) {
			cmbLevel.setSelectedIndex(0);
		}
		cmbLevel.setMinimumSize(new Dimension(50, 25));
		cmbLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Integer level = (Integer)((JComboBox)ae.getSource()).getSelectedItem();
				if (level != null) {
					mapper.setLevel(level.intValue());
					minimap.setLevel(level.intValue());
				}
			}
		});
		lblLevel.setLabelFor(cmbLevel);
		lblLevel.setVisible(cmbLevel.getItemCount() > 1);
		cmbLevel.setVisible(cmbLevel.getItemCount() > 1);
		
		
		cmbHotSpots = new JComboBox();
		if (data != null && data.hotSpots() != null) {
			for (Iterator iter = data.hotSpots().values().iterator(); iter.hasNext();) {
				HotSpot h = (HotSpot)iter.next();
				cmbHotSpots.addItem(h);
			}
		}
		cmbHotSpots.setMinimumSize(new Dimension(50, 25));
		cmbHotSpots.setVisible(cmbHotSpots.getItemCount() != 0);
		cmbHotSpots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				HotSpot h = (HotSpot)((JComboBox)ae.getSource()).getSelectedItem();
				if (h != null) {
					showHotSpot(h);
				}
			}
		});
		
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		mainPanel.setBorder(new javax.swing.border.EmptyBorder(2, 2, 2, 2));
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		mainPanel.add(lblScaling, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 0, 5, 0);
		c.weightx = 0.1;
		c.weighty = 0.0;
		mainPanel.add(sldScaling, c);
		
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 3, 0, 0);
		c.weightx = 0.0;
		c.weighty = 0.0;
		mainPanel.add(lblLevel, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 3, 0);
		c.weightx = 0.0;
		c.weighty = 0.0;
		mainPanel.add(cmbLevel, c);
		
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 5, 3, 0);
		c.weightx = 0.0;
		c.weighty = 0.0;
		mainPanel.add(cmbHotSpots, c);
		
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 5;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0.2;
		c.weighty = 0.2;
		mainPanel.add(scpMapper, c);
		
		
		return mainPanel;
	}
	
	/**
	 * Called when the viewed rect of the main mapper changes.
	 * In further implementations a rect of the visible bounds
	 * should be displayed in the minimap.
	 */
	public void stateChanged(javax.swing.event.ChangeEvent p1) {
	}
	
	public Component getMinimap() {
		return minimapPane;
	}
	
	/**
	 * Should return all short cuts this class want to be informed.
	 * The elements should be of type javax.swing.KeyStroke
	 */
	public java.util.Iterator getShortCuts() {
		return shortcuts.iterator();
	}
	
	/**
	 * This method is called when a shortcut from getShortCuts() is
	 * recognized.
	 */
	public void shortCut(javax.swing.KeyStroke shortcut) {
		int index=shortcuts.indexOf(shortcut);
		switch(index) {
			case -1: break; //unknown shortcut
			case 0:
			case 1: com.eressea.demo.desktop.DesktopEnvironment.requestFocus("MAP");
			mapper.requestFocus(); //activate the mapper, not the scrollpane
			break;
			case 2:	String input = JOptionPane.showInputDialog(getString("msg.enterhotspotname.text"));
			if (input != null && !input.equals(""))
				assignHotSpot(input); // just CTRL
			break;
			case 3: HotSpot h = (HotSpot)cmbHotSpots.getSelectedItem();
			if (h!=null)
				removeHotSpot(h); // SHIFT + CTRL
			break;
			case 4: Collection renderers = mapper.getRenderers(Mapper.PLANE_REGION);
			if (renderers != null && renderers.size() > 0) {
				Object o = renderers.iterator().next();
				if (o instanceof RegionImageCellRenderer) {
					RegionImageCellRenderer r = (RegionImageCellRenderer)o;
					r.setFogOfWar(!r.getFogOfWar());
					Mapper.setRenderContextChanged(true);
					mapper.repaint();
				}
			} break;
			case 5: break;
		}
	}
	
	public PreferencesAdapter createPreferencesAdapter() {
		return new MapperPanelPreferences(this);
	}
	
	/**
	 * Returns the current configuration of this mapper panel.
	 *
	 * The current implementation divides all the information by "_". First the
	 * scale factor is stored, then planes(plane index, renderer class name,
	 * renderer configuration).
	 */
	public java.lang.String getComponentConfiguration() {
		return mapper.getComponentConfiguration()+":"+minimap.getComponentConfiguration();
	}
	
	public void initComponent(java.lang.String p1) {
		if (p1.indexOf(':')>=0) {
			mapper.initComponent(p1.substring(0,p1.indexOf(':')));
			minimap.initComponent(p1.substring(p1.indexOf(':')+1));
		} else {
			mapper.initComponent(p1);
		}
	}
	
	/**
	 * Returns the listener responsible for the sub-short-cuts
	 */
	public ShortcutListener getExtendedShortcutListener(KeyStroke stroke) {
		return tooltipShortcut;
	}
	
	/**
	 * Returns wether the given stroke is for an extended short-cut.
	 */
	public boolean isExtendedShortcut(KeyStroke stroke) {
		int index = shortcuts.indexOf(stroke);
		return (index == 5);
	}
	
	public String getShortcutDescription(Object stroke) {
		int index = shortcuts.indexOf(stroke);		
		return getString("shortcuts.description."+String.valueOf(index));
	}
	
	public String getListenerDescription() {
		return getString("shortcuts.description");
	}
	
	private class MapperPanelPreferences extends JPanel implements ExtendedPreferencesAdapter{
		
		protected class MinimapPreferences extends JPanel implements PreferencesAdapter {
			private JSlider sldZoom;
			private JComboBox cmbDisplayMode;
			private JCheckBox autoScale;
			private PreferencesAdapter renderers;
			
			public MinimapPreferences() {
				renderers = minimap.getPreferencesAdapter();
				// display mode combo box
				String items[]=new String[5];
				items[0]=getString("prefs.minimapitems.terrain");
				items[1]=getString("prefs.minimapitems.politics");
				items[2]=getString("prefs.minimapitems.allfactions");
				items[3]=getString("prefs.minimapitems.trustlevel");
				items[4]=getString("prefs.minimapitems.trustlevelguard");
				cmbDisplayMode = new JComboBox(items);
				cmbDisplayMode.setSelectedIndex(source.getMinimapMode());
				JLabel lblDisplayMode = new JLabel(getString("prefs.lbl.minimapoptions"));
				lblDisplayMode.setLabelFor(cmbDisplayMode);
				lblDisplayMode.setHorizontalTextPosition(JLabel.CENTER);
				
				// color synching button
				JButton btnSyncColors = new JButton(getString("prefs.lbl.synccolors.caption"));
				btnSyncColors.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MapperPanel.this.synchronizeMinimap();
					}
				});
				
				// zoom slider
				sldZoom=new JSlider(1,25,10);
				sldZoom.setLabelTable(sldZoom.createStandardLabels(5));
				sldZoom.setMajorTickSpacing(10);
				sldZoom.setMinorTickSpacing(5);
				sldZoom.setPaintLabels(true);
				sldZoom.setPaintTicks(true);
				sldZoom.setValue(getMinimapScale());
				
				JLabel lblZoom = new JLabel(getString("prefs.lbl.zoom"));
				lblZoom.setLabelFor(sldZoom);
				lblZoom.setHorizontalTextPosition(JLabel.CENTER);
				
				// auto scale checkbox
				autoScale = new JCheckBox(getString("prefs.lbl.minimapautoscale"), source.isAutoScaling());
				
				// panel grouping minimap stuff
				this.setBorder(new javax.swing.border.TitledBorder(BorderFactory.createEtchedBorder(),getString("prefs.border.minimap")));
				
				this.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = 4;
				c.gridheight = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.1;
				c.weighty = 1;
				this.add(renderers.getComponent(),c);
				/*this.add(lblDisplayMode, c);
				
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 1;
				c.gridy = 0;
				c.gridwidth = 2;
				c.gridheight = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 0;
				this.add(cmbDisplayMode, c);
				
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 3;
				c.gridy = 0;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.fill = GridBagConstraints.NONE;
				c.weightx = 0;
				c.weighty = 0;
				this.add(btnSyncColors, c);*/
				
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 0;
				c.gridy = 1;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 0.1;
				c.weighty = 0;
				this.add(lblZoom, c);
				
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 1;
				c.gridy = 1;
				c.gridwidth = 2;
				c.gridheight = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1;
				c.weighty = 0;
				this.add(sldZoom, c);
				
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 3;
				c.gridy = 1;
				c.gridwidth = 1;
				c.gridheight = 1;
				c.fill = GridBagConstraints.NONE;
				c.weightx = 1;
				c.weighty = 0;
				this.add(autoScale, c);
				
			}
			
			public Component getComponent() {
				return this;
			}
			
			public String getTitle() {
				return getString("prefs.border.minimap");
			}
			
			public void applyPreferences() {
				renderers.applyPreferences();
				//setMinimapMode(cmbDisplayMode.getSelectedIndex());
				if (autoScale.isSelected()) {
					setAutoScaling(true);
					rescale();
				} else {
					setAutoScaling(false);
					setMinimapScale(sldZoom.getValue());
				}
				minimapPane.doLayout();
				minimapPane.repaint(100);				
			}
		}
		
		// The source component to configure
		private MapperPanel source = null;
		
		// GUI elements
		private PreferencesAdapter prefMapper = null;
		
		private List subAdapter;
		
		public MapperPanelPreferences(MapperPanel m) {
			this.source = m;
			prefMapper = mapper.getPreferencesAdapter();
			
			subAdapter = CollectionFactory.createArrayList(1);
			subAdapter.add(new MinimapPreferences());
		}
			
		public java.util.List getChildren() {
			return subAdapter;
		}
				
		public Component getComponent() {
			return prefMapper.getComponent();
		}
		
		public void applyPreferences() {
			prefMapper.applyPreferences();
			
			mapper.repaint(100);
		}
		
		public String getTitle() {
			return getString("prefs.title");
		}
	}
	
	protected class MinimapScaler extends ComponentAdapter {
		
		public void componentResized(ComponentEvent e) {
			if (resizeMinimap && e.getSource()==minimapPane) {
				resizeMinimap = false;
				Dimension newSize = minimapPane.getSize();
				if ((newSize.width <= 0) || (newSize.height <= 0)) {
					resizeMinimap = true;
					return;
				}
				Dimension prefSize;
				int loops = 0;
				do {
					loops++;
					// make it a little bit smaller
					newSize.width -= 2;
					newSize.height -= 2;
					prefSize = minimap.getPreferredSize();
					float sc = 1.0f;
					try{
						if (newSize.width != prefSize.width) {
							sc = ((float)newSize.width)/((float)prefSize.width);
						}
					}catch(Exception exc) {}
					try{
						if (newSize.height != prefSize.height) {
							float sc2 = ((float)newSize.height)/((float)prefSize.height);
							if (sc2 < sc) {
								sc = sc2;
							}
						}
					} catch(Exception exc) {}
					try{
						if (sc != 1.0f) {
							if (sc < 0) {
								sc = 0;
							}
							minimap.setScaleFactor(minimap.getScaleFactor()*sc);
						}
					} catch(Exception exc) {}
					prefSize = minimap.getPreferredSize();
				} while (loops<3 && (prefSize.width > newSize.width || prefSize.height > newSize.height));
				minimapPane.doLayout();
				minimapPane.repaint();
				resizeMinimap = true;
			}
		}
	}
	
	protected class TooltipShortcut implements ShortcutListener {
		protected java.util.List shortcuts;
		public TooltipShortcut() {
			shortcuts = CollectionFactory.createArrayList(10);
			for(int i=1;i<10;i++) {
				shortcuts.add(KeyStroke.getKeyStroke(Character.forDigit(i, 10)));
			}
			shortcuts.add(KeyStroke.getKeyStroke(Character.forDigit(0, 10)));
		}
		
		protected void setTooltip(int index) {
			java.util.List list = mapper.getAllTooltipDefinitions();
			if (list != null && list.size()>2*index) {
				mapper.setTooltipDefinition((String)list.get(2*index+1));
			}
		}
		
		/**
		 * This method is called when a shortcut from getShortCuts() is
		 * recognized.
		 */
		public void shortCut(javax.swing.KeyStroke shortcut) {
			int index = shortcuts.indexOf(shortcut);
			if (index >= 0 && index <10) {
				setTooltip(index);
			}
		}
		
		/**
		 * Should return all short cuts this class want to be informed.
		 * The elements should be of type javax.swing.KeyStroke
		 */
		public java.util.Iterator getShortCuts() {
			return shortcuts.iterator();
		}
		
		public String getShortcutDescription(Object stroke) {
			int index = shortcuts.indexOf(stroke);
			return getString("shortcuts.tooltips."+String.valueOf(index));
		}
		
		public String getListenerDescription() {
			return getString("shortcuts.tooltips");
		}
		
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
			defaultTranslations.put("msg.enterhotspotname.text" , "Please enter a name for the hot spot:");
			defaultTranslations.put("prefs.lbl.minimapautoscale" , "automatic");
			defaultTranslations.put("lbl.zoom.caption" , "Zoom: ");
			defaultTranslations.put("lbl.level.caption" , "Level: ");
			defaultTranslations.put("prefs.title" , "Map");
			defaultTranslations.put("prefs.border.minimap" , "Minimap");
			defaultTranslations.put("prefs.minimapitems.terrain" , "Terrain");
			defaultTranslations.put("shortcuts.description.3" , "Remove Hotspot");
			defaultTranslations.put("shortcuts.description.5" , "Tooltip selection (0-9)");
			defaultTranslations.put("shortcuts.description.4" , "Change Fog of War");
			defaultTranslations.put("shortcuts.description.2" , "Assign Hotspot");
			defaultTranslations.put("shortcuts.description.1" , "Request Focus");
			defaultTranslations.put("shortcuts.description.0" , "Request Focus");
			defaultTranslations.put("shortcuts.description" , "Map");
			defaultTranslations.put("shortcuts.tooltips" , "Tooltips");
			defaultTranslations.put("prefs.minimapitems.politics" , "By faction");
			defaultTranslations.put("prefs.minimapitems.allfactions" , "All factions");
			defaultTranslations.put("prefs.minimapitems.trustlevel" , "Trust level");
			defaultTranslations.put("prefs.minimapitems.trustlevelguard" , "Trust level (guard)");
			defaultTranslations.put("prefs.lbl.minimapoptions" , "Options: ");
			defaultTranslations.put("prefs.lbl.zoom" , "Zoom: ");
			defaultTranslations.put("prefs.lbl.synccolors.caption" , "Sync colors");
		}
		return defaultTranslations;
	}
}
