// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.swing.context;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.eressea.util.CollectionFactory;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.eressea.Coordinate;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.HotSpot;
import com.eressea.ID;
import com.eressea.IntegerID;
import com.eressea.Region;
import com.eressea.crtonr.RegionWriter;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.swing.map.MapCellRenderer;
import com.eressea.swing.map.Mapper;
import com.eressea.swing.map.RenderingPlane;

/**
 * A context menu for the map.
 * Currently providing copy of the name and the coordinates of the region.
 * @author Ulrich K�ster
 */
public class MapContextMenu extends JPopupMenu implements ContextObserver{
	
	// the region on which the menu is inferred
	private Region region;
	private EventDispatcher dispatcher;
	//	private Client client;
	private GameData data;
	private Properties settings;
	private List selectedRegions = CollectionFactory.createLinkedList();
	private Collection armystatsSel = CollectionFactory.createHashSet();
	
	private final static String RKEY = "MAGELLAN.RENDERER";
	private final static String TKEY = "MAGELLAN.TOOLTIP";
	
	protected JMenuItem name, changeSelState, copyNameID, setOrigin, changeHotSpot, printNR, armystats;
	protected JMenu renderer, tooltips;
	protected ActionListener rListener, tListener;
	
	protected Mapper source;
	
	public MapContextMenu(EventDispatcher dispatcher, Properties settings) {
		this.dispatcher = dispatcher;
		this.settings = settings;
		//		this.client = client;
		
		rListener = new RendererActionListener();
		tListener = new TooltipActionListener();
		
		name = new JMenuItem();
		name.setEnabled(false);
		add(name);
		
		changeSelState = new JMenuItem(getString("menu.changeselectionstate"));
		changeSelState.setEnabled(false);
		changeSelState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeSelectionState();
			}
		});
		add(changeSelState);
		
		copyNameID = new JMenuItem(getString("menu.copyidandname.caption"));
		copyNameID.setEnabled(false);
		copyNameID.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyNameID();
			}
		}
		);
		add(copyNameID);
		
		setOrigin = new JMenuItem(getString("menu.setorigin"));
		setOrigin.setEnabled(false);
		setOrigin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setOrigin();
			}
		});
		add(setOrigin);
		
		changeHotSpot = new JMenuItem(getString("menu.changehotspot"));
		changeHotSpot.setEnabled(false);
		changeHotSpot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeHotSpot();
			}
		});
		add(changeHotSpot);
		
		printNR = new JMenuItem(getString("menu.shownr"));
		printNR.setEnabled(false);
		printNR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printNROfRegion();
			}
		});
		add(printNR);
		
		armystats = new JMenuItem(getString("menu.armystats"));
		armystats.setEnabled(false);
		final EventDispatcher ed = dispatcher;
		final Properties set = settings;
		armystats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					new com.eressea.swing.ArmyStatsDialog(new Frame(), ed, data, set, armystatsSel).show();
				}catch(Exception exc) {}
			}
		});
		add(armystats);
		
		addSeparator();
		
		tooltips = new JMenu(getString("menu.tooltips"));
		tooltips.setEnabled(false);
		add(tooltips);
		
		renderer = new JMenu(getString("menu.renderer"));
		renderer.setEnabled(false);
		add(renderer);
	}
	
	public void init(Region r,Collection selectedRegions) {
		this.selectedRegions.clear();
		this.selectedRegions.addAll(selectedRegions);
		armystatsSel.clear();
		armystatsSel.addAll(selectedRegions);
		armystatsSel.add(r);
		region=r;
		setLabel(r.toString());
		name.setText(r.toString());
		changeSelState.setEnabled(true);
		copyNameID.setEnabled(true);
		setOrigin.setEnabled(true);
		changeHotSpot.setEnabled(true);
		printNR.setEnabled(true);
		armystats.setEnabled(true);
	}
	
	public void clear() {
		String s=getString("menu.noregion");
		setLabel(s);
		name.setText(s);
		changeSelState.setEnabled(false);
		copyNameID.setEnabled(false);
		setOrigin.setEnabled(false);
		changeHotSpot.setEnabled(false);
		printNR.setEnabled(false);
		armystats.setEnabled(false);
	}
	
	public void updateTooltips(Mapper source) {
		this.source = source;
		tooltips.removeAll();
		Iterator it = source.getAllTooltipDefinitions().iterator();
		while(it.hasNext()) {
			String name = (String)it.next();
			String tip = (String)it.next();
			String s = name+": "+tip;
			if (s.length() > 25) {
				s = s.substring(0, 23)+"...";
			}			
			JMenuItem item = new JMenuItem(s);
			item.addActionListener(tListener);
			item.putClientProperty(TKEY, tip);
			tooltips.add(item);
		}
		tooltips.setEnabled(tooltips.getItemCount() > 0);
	}
	
	public void updateRenderers(Mapper source) {
		this.source=source;
		renderer.removeAll();
		
		// add renderers that are context changeable
		Collection pl=source.getPlanes();
		Iterator it=pl.iterator();
		boolean added=false;
		while(it.hasNext()) {
			RenderingPlane rp=(RenderingPlane)it.next();
			MapCellRenderer r=rp.getRenderer();
			if (r != null && (r instanceof ContextChangeable)) {
				JMenuItem mi=((ContextChangeable)r).getContextAdapter();
				if (mi instanceof JMenu) {
					renderer.add(mi);
				} else {
					JMenu help = new JMenu(r.getName());
					help.add(mi);
					renderer.add(help);
				}
				((ContextChangeable)r).setContextObserver(this);
				added=true;
			}
		}
		// add renderer choosers
		if (added) {
			renderer.addSeparator();
		}
		pl=source.getPlanes();
		it=pl.iterator();
		while(it.hasNext()) {
			RenderingPlane rp = (RenderingPlane)it.next();
			JMenu help = new JMenu(rp.getName());
			Collection rs = source.getRenderers(rp.getIndex());
			boolean addedi=false;
			if (rs != null) {
				addedi=true;
				JMenuItem item = new JMenuItem(getString("menu.renderer.none"));
				item.setEnabled(rp.getRenderer()!=null);
				item.putClientProperty(RKEY,new Integer(rp.getIndex()));
				item.addActionListener(rListener);
				help.add(item);
				Iterator it2 = rs.iterator();
				while(it2.hasNext()) {
					MapCellRenderer mcp = (MapCellRenderer)it2.next();
					item = new JMenuItem(mcp.getName());
					item.setEnabled(mcp!=rp.getRenderer());
					item.addActionListener(rListener);
					item.putClientProperty(RKEY,mcp);
					help.add(item);
				}
			}
			help.setEnabled(addedi);
			renderer.add(help);
		}
		
		renderer.setEnabled(true);
	}
	
	public void setGameData(GameData d) {
		data=d;
	}
	
	/**
	 * Changes the selection state of the region
	 */
	private void changeSelectionState() {
		if (selectedRegions.contains(region)) {
			selectedRegions.remove(region);
		} else {
			selectedRegions.add(region);
		}
		dispatcher.fire(new SelectionEvent(this, selectedRegions, null, SelectionEvent.ST_REGIONS));
	}
	
	/**
	 * Copies name and coordinates to the sytem clipboard.
	 */
	private void copyNameID() {
		StringSelection strSel = new StringSelection(region.toString());
		Clipboard cb = getToolkit().getSystemClipboard();
		cb.setContents(strSel, null);
	}
	
	/**
	 * Sets the origin to the region
	 */
	private void setOrigin() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Coordinate c = new Coordinate(region.getCoordinate());
		data.placeOrigin(c);
		dispatcher.fire(new GameDataEvent(this, data));
		setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Sets or delets an hotspot
	 */
	private void changeHotSpot() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Map hotspots = data.hotSpots();
		boolean found = false;
		for (Iterator iter = hotspots.values().iterator(); iter.hasNext() && !found; ) {
			HotSpot h = (HotSpot)iter.next();
			if (h.getCenter().equals(region.getCoordinate())) {
				found = true;
				data.removeHotSpot(h.getID());
			}
		}
		if (!found) {
			ID id;
			while (true) {
				id = IntegerID.create((int)Math.random() * Integer.MAX_VALUE);
				if (data.getHotSpot(id) == null) {
					break;
				}
			}
			HotSpot h = new HotSpot(id);
			h.setCenter(region.getCoordinate());
			h.setName(region.toString());
			data.hotSpots().put(id, h);
		}
		dispatcher.fire(new GameDataEvent(this, data));
		setCursor(Cursor.getDefaultCursor());
	}
	
	/**
	 * Shows an NR-representation of the region
	 */
	private void printNROfRegion() {
		Faction f = null;
		for (Iterator iter = data.factions().values().iterator(); iter.hasNext(); ) {
			Faction faction = (Faction)iter.next();
			if (f == null || f.trustLevel < faction.trustLevel) {
				f = faction;
			}
		}
		java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		java.io.PrintStream p = new java.io.PrintStream(stream);
		new RegionWriter(data, p, region, f);
		new NRDialog("NR f�r \"" + f.getName() + "\" f�r \"" + region.toString() +"\"", stream.toString());
	}
	
	private String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this,key);
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
			defaultTranslations.put("menu.copyidandname.caption" , "Copy name and coordinates");
			defaultTranslations.put("menu.changeselectionstate" , "Change selection state");
			defaultTranslations.put("menu.setorigin" , "Set origin to this region");
			defaultTranslations.put("menu.changehotspot" , "Set or delete hotspot");
			defaultTranslations.put("menu.tooltips" , "Tooltips");
			defaultTranslations.put("menu.shownr" , "Show NR for this region (in German only)");
			defaultTranslations.put("menu.armystats" , "Army statistics...");
			defaultTranslations.put("menu.renderer.none" , "Off");
			defaultTranslations.put("menu.noregion" , "No region");
			defaultTranslations.put("menu.renderer" , "Renderer");
		}
		return defaultTranslations;
	}

	

	public void contextDataChanged() {
		if (source != null) {
			Mapper.setRenderContextChanged(true);
			source.repaint();
		}
	}
	
	protected class TooltipActionListener implements ActionListener {		
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			if (source != null && (actionEvent.getSource() instanceof JMenuItem)) {
				JMenuItem src = (JMenuItem)actionEvent.getSource();
				Object obj = src.getClientProperty(TKEY);
				if (obj == null) {
					return;
				}
				source.setTooltipDefinition(obj.toString());
			}
		}		
	}
	
	protected class RendererActionListener implements ActionListener {		
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			if (source != null && (actionEvent.getSource() instanceof JMenuItem)) {
				JMenuItem src = (JMenuItem)actionEvent.getSource();
				try {
					Object obj = src.getClientProperty(RKEY);
					if (obj == null) {
						return;
					}
					if (obj instanceof MapCellRenderer) {
						MapCellRenderer mcp = (MapCellRenderer)obj;
						source.setRenderer(mcp);
					} else if (obj instanceof Integer) {
						source.setRenderer(null,((Integer)obj).intValue());
					}
					Mapper.setRenderContextChanged(true);
					source.repaint();
				} catch (Exception exc) {}
			}
		}
	}
	
	private class NRDialog extends JDialog {
		private NRDialog(String title, String text) {
			this.setTitle(title);
			this.setModal(true);
			this.setSize(new java.awt.Dimension(600, 600));
			JTextArea textArea = new JTextArea(text);
			textArea.setCaretPosition(0);
			JScrollPane pane = new JScrollPane(textArea);
			this.getContentPane().add(pane);
			this.setLocationRelativeTo(MapContextMenu.this);
			this.show();
		}
	}
	
}
