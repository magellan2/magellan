/*
 * AdvancedRegionShapeCellRenderer.java
 *
 * Created on 17. Mai 2002, 12:40
 */

package com.eressea.swing.map;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;

import com.eressea.Region;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.swing.context.ContextChangeable;
import com.eressea.swing.context.ContextObserver;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Colors;
import com.eressea.util.replacers.ReplacerHelp;
import com.eressea.util.replacers.ReplacerSystem;

/**
 *
 * @author  Andreas
 * @version
 */
public class AdvancedRegionShapeCellRenderer extends AbstractRegionShapeCellRenderer implements GameDataListener, ContextChangeable, ActionListener, MapperAware{
	
	protected String currentSet;
	protected ColorTable cTable;
	protected ValueMapping vMapping;
	
	protected String minDef, curDef, maxDef;
	protected ReplacerSystem minList, curList, maxList;
	protected boolean minEvalfed = false, maxEvalfed = false;
	protected float minEvalf = 0f, maxEvalf = 1f;
	protected String unknownString;
	protected String mapperTooltip;
	
	protected Color oceanColor, unknownColor;
	
	protected JMenu context;
	protected ContextObserver obs = null;
	
	protected Mapper mapper = null;
	protected String lastMapperDef = null;
	
	/** Creates new AdvancedRegionShapeCellRenderer */
	public AdvancedRegionShapeCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
		
		cTable = new ColorTable();
		vMapping = new ValueMapping();
		context = new JMenu(getName());
		
		EventDispatcher.getDispatcher().addGameDataListener(this);		
		
		loadSettings();
	}
	
	protected void loadSettings() {
		loadSettings(settings.getProperty("AdvancedShapeRenderer.CurrentSet", "Standard"));
		if (!settings.containsKey("AdvancedShapeRenderer."+currentSet+".Colors")) {
			saveSettings();
		}
	}
	
	protected void loadSettings(String set) {
		loadSettings(set, settings);
	}
		
	protected void loadSettings(String set, Properties settings) {
		
		if (lastMapperDef != null) {
			setMapperTooltip(lastMapperDef);
		}
		
		currentSet = set;
		reprocessMenu();
		StringTokenizer st = new StringTokenizer(settings.getProperty("AdvancedShapeRenderer."+set+".Colors", "0.0;0,0,0;1.0;255,255,255"), ";");
		cTable.removeAll();
		while(st.hasMoreTokens()) try{
			String key = st.nextToken();
			String value = st.nextToken();
			float fl = Float.parseFloat(key);
			cTable.addEntry(fl, Colors.decode(value));
		}catch(RuntimeException exc) {}
		st = new StringTokenizer(settings.getProperty("AdvancedShapeRenderer."+set+".Values", "0.0;0.0;1.0;1.0"), ";");
		vMapping.removeAll();
		while(st.hasMoreTokens()) try{
			String key = st.nextToken();
			String value = st.nextToken();
			float f1 = Float.parseFloat(key);
			float f2 = Float.parseFloat(value);
			vMapping.addEntry(f1, f2);
		}catch(RuntimeException exc) {}
		
		minDef = settings.getProperty("AdvancedShapeRenderer."+set+".Min", "0");
		curDef = settings.getProperty("AdvancedShapeRenderer."+set+".Cur", "0.5");
		maxDef = settings.getProperty("AdvancedShapeRenderer."+set+".Max", "1");
		this.gameDataChanged(null);
		
		unknownString = settings.getProperty("AdvancedShapeRenderer."+set+".Unknown", "?");
		
		mapperTooltip = settings.getProperty("AdvancedShapeRenderer."+set+".Tooltip");
		
		if (mapperTooltip != null) {
			lastMapperDef = getMapperTooltip();
			setMapperTooltip(mapperTooltip);
		}
		
		// load ocean and unknown color from geom. renderer
		loadGeomColors();
				
		settings.setProperty("AdvancedShapeRenderer.CurrentSet", currentSet);
	}
	
	protected void saveSettings() {
		saveSettings(settings);
	}
	
	protected void saveSettings(Properties settings) {
		reprocessMenu();
		
		settings.setProperty("AdvancedShapeRenderer.CurrentSet", currentSet);
		
		Collection entries = cTable.getEntries();
		StringBuffer buf = new StringBuffer();
		if (entries.size() > 0) {
			Iterator it = entries.iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				buf.append(((Float)entry.getKey()).floatValue());
				buf.append(';');
				buf.append(Colors.encode((Color)entry.getValue()));
				if (it.hasNext()) {
					buf.append(';');
				}
			}
			settings.setProperty("AdvancedShapeRenderer."+currentSet+".Colors", buf.toString());
		} else {
			settings.remove("AdvancedShapeRenderer."+currentSet+".Colors");
		}
		
		entries = vMapping.getEntries();
		if (entries.size() > 0) {
			Iterator it = entries.iterator();
			buf.setLength(0);
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				buf.append(((Float)entry.getKey()).floatValue());
				buf.append(';');
				buf.append(((Float)entry.getValue()).floatValue());
				if (it.hasNext()) {
					buf.append(';');
				}
			}
			settings.setProperty("AdvancedShapeRenderer."+currentSet+".Values", buf.toString());
		} else {
			settings.remove("AdvancedShapeRenderer."+currentSet+".Values");
		}
		
		settings.setProperty("AdvancedShapeRenderer."+currentSet+".Min", minDef);
		settings.setProperty("AdvancedShapeRenderer."+currentSet+".Max", maxDef);
		settings.setProperty("AdvancedShapeRenderer."+currentSet+".Cur", curDef);
		
		if (mapperTooltip == null) {
			settings.remove("AdvancedShapeRenderer."+currentSet+".Tooltip");
		} else {
			settings.setProperty("AdvancedShapeRenderer."+currentSet+".Tooltip", mapperTooltip);
		}
		
		// look if this set is registered
		StringTokenizer tokens = new StringTokenizer(settings.getProperty("AdvancedShapeRenderer.Sets", ""), ",");
		boolean found = false;
		while(!found && tokens.hasMoreTokens()) {
			found = (currentSet.equals(tokens.nextToken()));
		}
		if (!found) {
			settings.setProperty("AdvancedShapeRenderer.Sets", settings.getProperty("AdvancedShapeRenderer.Sets", "")+","+currentSet);
		}
		
		// this is a good point to synchronize colors
		loadGeomColors();
	}
	
	protected void loadGeomColors() {
		oceanColor = Colors.decode(settings.getProperty("GeomRenderer.OceanColor","128,128,255").replace(';',','));
		unknownColor = Colors.decode(settings.getProperty("GeomRenderer.UnknownColor","128,128,128").replace(';',','));
	}
	
	protected Color getSingleColor(Region r) {
		boolean nonFound = false;
		float minF = 0f;
		if (minEvalfed) {
			minF = minEvalf;
		} else if (minList != null) {
			Object obj = minList.getReplacement(r);
			if (obj == null) {
				nonFound = true;
			} else {
				String ret = obj.toString();
				try{
					minF = Float.parseFloat(ret);
				}catch(Exception exc) {}
			}
		}
		float maxF = 1f;
		if (!nonFound) {
			if (maxEvalfed) {
				maxF = maxEvalf;
			} else if (maxList != null) {
				Object obj = maxList.getReplacement(r);
				if (obj == null) {
					nonFound = true;
				} else {
					String ret = obj.toString();
					try{
						maxF = Float.parseFloat(ret);
					}catch(Exception exc) {}
				}
			}
		}
		float curF = 0.5f;
		if (!nonFound) {
			if (curList != null) {
				Object obj = curList.getReplacement(r);
				if (obj == null) {					
					nonFound = true;
				} else {
					String ret = obj.toString();
					try{
						curF = Float.parseFloat(ret);
					}catch(Exception exc) {nonFound = true;}
				}
			}
		}
		
		if (nonFound) {
			if (r.getType().getID().toString().equals("Ozean")) {
				return oceanColor;
			}
			return unknownColor;
		}
		
		float percent = (curF-minF)/(maxF-minF);
		if (percent < 0f) {
			percent = 0f;
		} else if (percent > 1f) {
			percent = 1f;
		}
		
		return cTable.interpolate(vMapping.interpolate(percent));
	}
	
	// we only have single colors
	protected Color[] getColor(Region r) {
		return null;
	}
	
	/**
	 * Invoked when the current game data object becomes invalid.
	 */
	public void gameDataChanged(GameDataEvent e) {
		// reload replacers from Mapper
		minList = ReplacerHelp.createReplacer(minDef, unknownString);
		curList = ReplacerHelp.createReplacer(curDef, unknownString);
		maxList = ReplacerHelp.createReplacer(maxDef, unknownString);
		
		minEvalf = 0f;
		minEvalfed = true;
		if (minList != null) {
			try{
				String ret = minList.getReplacement(null).toString();
				try{
					minEvalf = Float.parseFloat(ret);
				}catch(NumberFormatException exc2) {}
				minEvalfed = true;
			}catch(Exception exc) {minEvalfed = false;}
		}
		
		maxEvalf = 1f;
		maxEvalfed = true;
		if (maxList != null) {
			try{
				String ret = maxList.getReplacement(null).toString();
				try{
					maxEvalf = Float.parseFloat(ret);
				}catch(NumberFormatException exc2) {}
				maxEvalfed = true;
			}catch(Exception exc) {maxEvalfed = false;}
		}
	}
	
	public void setMinDef(String s) {
		minDef = s;
		//settings.setProperty("AdvancedShapeRenderer.Min", s);
		this.gameDataChanged(null);
	}
	
	public void setMaxDef(String s) {
		maxDef = s;
		//settings.setProperty("AdvancedShapeRenderer.Max", s);
		this.gameDataChanged(null);
	}
	
	public void setCurDef(String s) {
		curDef = s;
		//settings.setProperty("AdvancedShapeRenderer.Cur", s);
		this.gameDataChanged(null);
	}
	
	public void setUnknown(String s) {
		unknownString = s;
		//settings.setProperty("AdvancedShapeRenderer.Unknown", s);
		this.gameDataChanged(null);
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
			defaultTranslations.put("name" , "ARR");
			defaultTranslations.put("prefs.title" , "ARR");
			defaultTranslations.put("prefs.menu.export" , "Export...");
			defaultTranslations.put("prefs.popup2.remove" , "Remove");
			defaultTranslations.put("prefs.popup2.add" , "Add");
			defaultTranslations.put("prefs.popup.modify" , "Change color...");
			defaultTranslations.put("prefs.popup.remove" , "Remove");
			defaultTranslations.put("prefs.popup.add" , "Add color...");
			defaultTranslations.put("prefs.newcolor" , "New color");
			defaultTranslations.put("prefs.newset" , "Name of new set:");
			defaultTranslations.put("prefs.menu.tooltip.input" , "Please enter your desired tooltip.");
			defaultTranslations.put("prefs.menu.tooltip.title" , "Choose tooltip");
			defaultTranslations.put("prefs.menu.tooltip.choose" , "Choose the (kind of) tooltip you wish.");
			defaultTranslations.put("prefs.menu.tooltip.manual" , "Direct input");
			defaultTranslations.put("prefs.menu.tooltip.current" , "Value of current field");
			defaultTranslations.put("prefs.menu.tooltip.none" , "None");
			defaultTranslations.put("prefs.menu.tlabel.none" , "Not set");
			defaultTranslations.put("prefs.menu.tooltip" , "Tooltip");
			defaultTranslations.put("prefs.changecolor" , "New color");
			defaultTranslations.put("prefs.renameset" , "New name of this set:");
			defaultTranslations.put("prefs.error.already" , "A set with this name already exists!");
			defaultTranslations.put("prefs.chooseset" , "Choose set");
			defaultTranslations.put("prefs.menu.import" , "Import...");
			defaultTranslations.put("prefs.menu.choose" , "Choose...");
			defaultTranslations.put("prefs.label.text" , "Name: ");
			defaultTranslations.put("prefs.menu.title" , "Set...");
			defaultTranslations.put("prefs.menu.rename" , "Rename...");
			defaultTranslations.put("prefs.menu.remove" , "Remove");
			defaultTranslations.put("prefs.menu.add" , "Add...");
			defaultTranslations.put("prefs.help" , "Use the context menu (right click) to add, remove or modify the preferences");
			defaultTranslations.put("prefs.result" , "Result:");
			defaultTranslations.put("prefs.texts.3" , "Unknown");
			defaultTranslations.put("prefs.texts.2" , "Maximum");
			defaultTranslations.put("prefs.texts.1" , "Value");
			defaultTranslations.put("prefs.texts.0" , "Minimum");
		}
		return defaultTranslations;
	}

	
	/**
	 * Implementation via SortedMap, maybe inefficient for small number of entries
	 */
	protected class ValueMapping {
		
		protected SortedMap values;
		
		public ValueMapping() {
			values = new TreeMap();
		}
		
		public float addEntry(float val, float mval) {
			Float fval = new Float(val);
			Float old = (Float)values.put(fval, new Float(mval));
			if (old != null) { // clear interpolation buffer
				return old.floatValue();
			}
			return -1f;
		}
		public void removeEntry(float val) {
			Object old = values.remove(new Float(val));
		}
		public void removeAll() {
			values.clear();
		}
		public void set(ValueMapping valueMapping) {
			removeAll();
			Iterator it = valueMapping.getEntries().iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				values.put(entry.getKey(), entry.getValue());
			}
		}
		
		public Collection getValues() {
			return values.values();
		}
		public Collection getMappedValues() {
			return values.keySet();
		}
		public Collection getEntries() {
			return values.entrySet();
		}
		public boolean hasValue(float f) {
			return values.containsKey(new Float(f));
		}
		public float getValueAt(float f) {
			return ((Float)values.get(new Float(f))).floatValue();
		}
		public float interpolate(float f) {
			Float fl = new Float(f);
			SortedMap m1 = values.headMap(fl);
			if (m1 == null || m1.size() == 0) {
				return ((Float)values.get(values.firstKey())).floatValue();
			}
			SortedMap m2 = values.tailMap(fl);
			if (m2 == null || m2.size() == 0) {
				
				float last = ((Float)values.get(values.lastKey())).floatValue();
				return last;
			}
			if (m2.firstKey().equals(fl)) {
				return ((Float)m2.get(fl)).floatValue();
			}
			Float left = (Float)m1.lastKey(), right = (Float)m2.firstKey();
			Float leftMapping = (Float)m1.get(left), rightMapping = (Float)m2.get(right);
			
			float percent = (f-left.floatValue())/(right.floatValue()-left.floatValue());
			float ret = (leftMapping.floatValue()*(1-percent)+rightMapping.floatValue()*percent);
			
			return ret;
		}
	}
	
	/**
	 * Implementation via SortedMap, maybe inefficient for small number of entries
	 */
	protected class ColorTable {
		
		protected SortedMap colors;
		protected Map interpolBuf;
		protected List interpolLookup;
		
		private int MAX_BUF = 256;
		
		public ColorTable() {
			colors = new TreeMap();
			interpolBuf = CollectionFactory.createHashMap();
			interpolLookup = CollectionFactory.createLinkedList();
		}
		
		public Color addEntry(float val, Color col) {
			Float fval = new Float(val);
			Color old = (Color)colors.put(fval, col);
			if (old != null) { // clear interpolation buffer
				clearBuffer();
			}
			return old;
		}
		public void removeEntry(float val) {
			Object old = colors.remove(new Float(val));
			if (old != null) {
				clearBuffer();
			}
		}
		public void removeAll() {
			colors.clear();
			clearBuffer();
		}
		public void set(ColorTable colorTable) {
			removeAll();
			Iterator it = colorTable.getEntries().iterator();
			while(it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				colors.put(entry.getKey(), entry.getValue());
			}
		}
		
		protected void clearBuffer() {
			interpolBuf.clear();
			interpolLookup.clear();
		}
		
		public Collection getColors() {
			return colors.values();
		}
		public Collection getValues() {
			return colors.keySet();
		}
		public Collection getEntries() {
			return colors.entrySet();
		}
		public boolean hasValue(float f) {
			return colors.containsKey(new Float(f));
		}
		public Color getColorAt(float f) {
			return (Color)colors.get(new Float(f));
		}
		public Color interpolate(float f) {
			Float fl = new Float(f);
			SortedMap m1 = colors.headMap(fl);
			if (m1 == null || m1.size() == 0) {
				return (Color)colors.get(colors.firstKey());
			}
			SortedMap m2 = colors.tailMap(fl);
			if (m2 == null || m2.size() == 0) {
				return (Color)colors.get(colors.lastKey());
			}
			if (m2.firstKey().equals(fl)) {
				return (Color)m2.get(fl);
			}
			Float left = (Float)m1.lastKey(), right = (Float)m2.firstKey();
			Color leftColor = (Color)m1.get(left), rightColor = (Color)m2.get(right);
			float percent = (f-left.floatValue())/(right.floatValue()-left.floatValue());
			int red = (int)(((float)leftColor.getRed())*(1-percent)+((float)rightColor.getRed())*(percent));
			int green = (int)(((float)leftColor.getGreen())*(1-percent)+((float)rightColor.getGreen())*(percent));
			int blue = (int)(((float)leftColor.getBlue())*(1-percent)+((float)rightColor.getBlue())*(percent));
			Integer integer =  new Integer(((red & 255) << 16) | ((green & 255) << 8) | (blue & 255));
			if (interpolBuf.containsKey(integer)) {
				interpolLookup.remove(integer);
				interpolLookup.add(0, integer);
				return (Color)interpolBuf.get(integer);
			}
			Color newColor = new Color(red, green, blue);
			if (interpolLookup.size() == MAX_BUF) {
				Object last = interpolLookup.remove(MAX_BUF-1);
				interpolBuf.remove(last);
			}
			interpolBuf.put(integer, newColor);
			interpolLookup.add(0, integer);
			return newColor;
		}
	}
	
	public PreferencesAdapter getPreferencesAdapter() {
		return new Preferences();
	}
	
	protected void reprocessMenu() {
		context.removeAll();
		StringTokenizer s = new StringTokenizer(settings.getProperty("AdvancedShapeRenderer.Sets", ""), ",");
		while(s.hasMoreTokens()) {
			String str = s.nextToken();
			JMenuItem item = new JMenuItem(str);
			item.addActionListener(this);
			if (str.equals(currentSet)) {
				item.setEnabled(false);
			}
			context.add(item);
		}
	}
	
	public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		loadSettings(actionEvent.getActionCommand());
		if (obs != null) {
			obs.contextDataChanged();
		}
	}
	
	public JMenuItem getContextAdapter() {
		return context;
	}
	
	public void setContextObserver(ContextObserver co) {
		obs = co;
	}
	
	// MAPPER things
	
	public String getMapperTooltip() {
		if (mapper != null) {
			return mapper.getTooltipDefinition();
		}
		return null;
	}
	
	public boolean setMapperTooltip(String tooltip) {
		if (mapper != null) {
			mapper.setTooltipDefinition(tooltip);
			return true;
		}
		return false;
	}
	
	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}
	
	protected class Preferences extends JPanel implements PreferencesAdapter, ActionListener, ListSelectionListener {
		
		protected ValueMapping myMapping;
		protected ColorTable myTable;
		
		protected MappingPanel mPanel;
		protected ColorShowPanel cShowPanel;
		protected ColorPanel cPanel;
		
		protected JTextField minText, maxText, curText, uText;		
		
		protected javax.swing.border.Border selBorder, nonSelBorder;
		protected SelListener selListener;
		
		protected JList list;
		protected DefaultListModel model;
		
		protected AbstractButton addSet, removeSet, renameSet, importSet, exportSet, setTooltip;
		
		protected JLabel tooltipLabel;
		
		public Preferences() {
			
			JPanel help = new JPanel(new GridBagLayout());
			
			GridBagConstraints con = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(2,0,2,0), 0, 0);
			
			selBorder = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED), BorderFactory.createEmptyBorder(2, 2, 2, 2));
			nonSelBorder = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), BorderFactory.createEmptyBorder(2, 2, 2, 2));
			selListener = new SelListener();
			
			// copy mappings
			myMapping = new ValueMapping();
			myMapping.set(vMapping);
			myTable = new ColorTable();
			myTable.set(cTable);
			
			boolean infoExists = false;
			try{
				JTextArea info = new JTextArea(getString("prefs.help"), 10, 5);
				info.setLineWrap(true);
				info.setWrapStyleWord(true);
				info.setEditable(false);
				info.setBackground(this.getBackground());
				info.setBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
				Insets oldInsets = con.insets;
				if (oldInsets != null) {
					con.insets = new Insets(oldInsets.top, Math.max(oldInsets.left, 5), oldInsets.bottom, oldInsets.right);
				} else {
					con.insets = new Insets(1, 5, 1, 1);
				}
				con.gridx = 2;
				con.gridy = 2;
				con.fill = GridBagConstraints.VERTICAL;
				con.gridheight = 3;				
				help.add(info, con);
				con.insets = oldInsets;
				infoExists = true;
			}catch(Exception exc) {}
			
			con.gridx = 1;
			con.gridy = 0;
			con.gridheight = 1;
			if (infoExists) {
				con.gridwidth = 2;
			}
			help.add(createTexts(), con);
			
			con.gridy++;
			con.fill = GridBagConstraints.HORIZONTAL;
			help.add(new JSeparator(JSeparator.HORIZONTAL), con);
			
			cPanel = new ColorPanel();
			cPanel.load();
			
			con.fill = GridBagConstraints.NONE;
			con.gridwidth = 1;
			con.gridy++;
			help.add(cPanel, con);
			
			con.gridy++;
			con.fill = GridBagConstraints.HORIZONTAL;
			help.add(new JSeparator(JSeparator.HORIZONTAL), con);
			
			mPanel = new MappingPanel();
			mPanel.load();
			
			con.fill = GridBagConstraints.NONE;			
			con.gridy++;
			help.add(mPanel, con);
			
			if (infoExists) {
				con.gridwidth = 2;
			}
					
			con.gridy++;
			con.fill = GridBagConstraints.HORIZONTAL;
			help.add(new JSeparator(JSeparator.HORIZONTAL), con);
			con.fill = GridBagConstraints.NONE;
			
			con.gridwidth = 1;
			
			con.gridy++;
			help.add(new JLabel(getString("prefs.result")), con);
			
			cShowPanel = new ColorShowPanel();
			Dimension dim = new Dimension(mPanel.getPreferredSize());
			dim.height = 30;
			cShowPanel.setPreferredSize(dim);
			cShowPanel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
			con.gridy++;
			help.add(cShowPanel, con);
			
			
			con.fill = GridBagConstraints.NONE;
			con.anchor = GridBagConstraints.CENTER;
			con.gridx = 0;
			con.gridy = 0;
			con.gridheight = 3;
			con.insets = new Insets(1, 1, 1, 10);
			help.add(createMenu(), con);
			
			con.gridy = 3;			
			con.gridheight = 5;
			con.fill = GridBagConstraints.BOTH;			
			help.add(createList(), con);
						
			this.setLayout(new BorderLayout());
			this.add(help, BorderLayout.CENTER);
			//this.add(left, BorderLayout.WEST);
		}
		
		protected Component createList() {
			model = new DefaultListModel();
			reprocessSets();
			list = new JList(model);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setSelectedValue(currentSet, true);
			list.addListSelectionListener(this);
			return new JScrollPane(list);
		}
		
		protected void reprocessSets() {
			Object old = null;
			if (list != null) {
				old = list.getSelectedValue();
			}
			reprocessSets(old);
		}
		
		protected void reprocessSets(Object oldSet) {
			List sets = CollectionFactory.createLinkedList();
			StringTokenizer s = new StringTokenizer(settings.getProperty("AdvancedShapeRenderer.Sets", ""), ",");
			while(s.hasMoreTokens()) {
				sets.add(s.nextToken());
			}
			model.clear();
			Iterator it = sets.iterator();
			while(it.hasNext()) {
				model.addElement(it.next());
			}
			if (oldSet != null && model.contains(oldSet)) {
				list.setSelectedValue(oldSet, true);
			} else {
				if (list != null) {
					list.setSelectedValue(currentSet, true);
				}
			}
		}
		
		protected Component createMenu() {
			addSet = new JButton(getString("prefs.menu.add"));
			addSet.addActionListener(this);
			removeSet = new JButton(getString("prefs.menu.remove"));
			removeSet.addActionListener(this);
			renameSet = new JButton(getString("prefs.menu.rename"));
			renameSet.addActionListener(this);
			importSet = new JButton(getString("prefs.menu.import"));
			importSet.addActionListener(this);
			exportSet = new JButton(getString("prefs.menu.export"));
			exportSet.addActionListener(this);
			setTooltip = new JButton(getString("prefs.menu.tooltip"));
			setTooltip.addActionListener(this);
			tooltipLabel = new JLabel();
			tooltipLabel.setHorizontalAlignment(SwingConstants.CENTER);
			updateTooltipLabel();
			
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0);
			
			panel.add(addSet, con);
			con.gridy++;
			panel.add(renameSet, con);
			con.gridy++;
			panel.add(new JSeparator(JSeparator.HORIZONTAL), con);
			con.gridy++;
			panel.add(removeSet, con);
			con.gridy++;
			panel.add(new JSeparator(JSeparator.HORIZONTAL), con);
			con.gridy++;
			panel.add(importSet, con);
			con.gridy++;
			panel.add(exportSet, con);
			con.gridy++;
			panel.add(new JSeparator(JSeparator.HORIZONTAL), con);
			con.gridy++;
			panel.add(setTooltip, con);
			con.gridy++;
			panel.add(tooltipLabel, con);
			
			return panel;			
		}
		
		protected Container createTexts() {
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1, 1, 0, 0), 0, 0);
			
			for(int i=0; i<4; i++) {
				con.gridx = (i%2)*2;
				con.gridy = (i/2);
				panel.add(new JLabel(getString("prefs.texts."+String.valueOf(i))), con);				
			}
			
			con.gridx = 1;
			con.gridy = 0;
			minText = new JTextField(minDef, 10);
			panel.add(minText, con);
			
			curText = new JTextField(curDef, 10);		
			con.gridx = 3;
			panel.add(curText, con);
			maxText = new JTextField(maxDef, 10);
			con.gridx = 1;
			con.gridy = 1;
			panel.add(maxText, con);
			uText = new JTextField(unknownString, 10);
			con.gridx = 3;
			panel.add(uText, con);
			
			return panel;
		}
		
		public void applyPreferences() {
			setMinDef(minText.getText());
			setMaxDef(maxText.getText());
			setCurDef(curText.getText());
			setUnknown(uText.getText());
			
			vMapping.removeAll();
			vMapping.set(myMapping);
			cTable.removeAll();
			cTable.set(myTable);
			saveSettings();
		}
		
		public Component getComponent() {
			return this;
		}
		
		public String getTitle() {
			return getString("prefs.title");
		}
		
		public void dataChanged() {
			cShowPanel.repaint();
			mPanel.repaint();
			cPanel.repaint();
		}
		
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			if (actionEvent.getSource() == addSet) {
				addSet();
			} else if (actionEvent.getSource() == removeSet) {
				removeSet();
			} else if (actionEvent.getSource() == renameSet) {
				renameSet();
			} else if (actionEvent.getSource() == importSet) {
				importSet();
			} else if (actionEvent.getSource() == exportSet) {
				exportSet();
			} else if (actionEvent.getSource() == setTooltip) {
				setTooltip();
			}
		}
		
		protected void addSet() {
			String name = JOptionPane.showInputDialog(this, getString("prefs.newset"));
			if (name != null && !name.trim().equals("")) {
				if (!checkName(name)) {
					JOptionPane.showMessageDialog(this, getString("prefs.error.already"));
					return;
				}
				addSet(name);
				reprocessSets(name);
			}
		}
		
		protected boolean checkName(String name) {
			StringTokenizer s = new StringTokenizer(settings.getProperty("AdvancedShapeRenderer.Sets", ""), ",");
			while(s.hasMoreTokens()) {
				if (name.equals(s.nextToken())) {
					return false;
				}
			}
			return true;
		}
		
		protected void addSet(String name) {			
			applyPreferences(); // save old set
			currentSet = name;
			applyPreferences(); // save new set
		}
		
		protected void removeSet() {
			removeSet(true);
		}
		
		protected void removeSet(boolean loadNew) {
			settings.remove("AdvancedShapeRenderer."+currentSet+".Colors");
			settings.remove("AdvancedShapeRenderer."+currentSet+".Values");
			settings.remove("AdvancedShapeRenderer."+currentSet+".Min");
			settings.remove("AdvancedShapeRenderer."+currentSet+".Max");
			settings.remove("AdvancedShapeRenderer."+currentSet+".Cur");
			settings.remove("AdvancedShapeRenderer."+currentSet+".Unknown");
			String set = settings.getProperty("AdvancedShapeRenderer.Sets");
			if (set != null) {
				StringBuffer buf = new StringBuffer();
				StringTokenizer s = new StringTokenizer(set, ",");
				while(s.hasMoreTokens()) {
					String token = s.nextToken();
					if (!currentSet.equals(token)) {
						if (buf.length() > 0) {
							buf.append(',');
						}
						buf.append(token);
					}
				}
				if (buf.length() > 0) {
					settings.setProperty("AdvancedShapeRenderer.Sets", buf.toString());
				} else {
					settings.remove("AdvancedShapeRenderer.Sets");
				}
			}
			if (loadNew) {
				StringTokenizer s = new StringTokenizer(settings.getProperty("AdvancedShapeRenderer.Sets", ""), ",");
				if (s.hasMoreTokens()) {
					loadSettings(s.nextToken());
				} else {
					settings.remove("AdvancedShapeRenderer.CurrentSet");
					loadSettings();
				}
				updateTables();
				list.setSelectedIndex(-1);
				reprocessSets();				
			}
		}
		
		protected void renameSet() {
			String newName = JOptionPane.showInputDialog(this, getString("prefs.renameset"));
			if (newName != null && !newName.trim().equals("")) {
				if (!checkName(newName)) {
					JOptionPane.showMessageDialog(this, getString("prefs.error.already"));
					return;
				}
				if (!currentSet.equals(newName)) {
					removeSet(false);
					currentSet = newName;
					saveSettings();
					list.setSelectedValue(null, false);
					reprocessSets();
				}
			}
		}
		
		protected void importSet() {
			JFileChooser jfc = new JFileChooser();
			int ret = jfc.showOpenDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				File files[] = jfc.getSelectedFiles();
				if (files == null || files.length == 0) {						
					importSet(jfc.getSelectedFile());
				} else {
					for(int i=0; i<files.length; i++) {
						importSet(files[i]);
					}
				}
				reprocessSets(currentSet);
			}			
		}
		
		protected void importSet(File file) {
			try{
				Properties prop = new Properties();
				FileInputStream in = new FileInputStream(file);
				prop.load(in);
				in.close();
				loadSettings(prop.getProperty("Set.Name"), prop);
				saveSettings();
				updateTables();
			}catch(IOException ioe) {
				showIOError(ioe);
			}
		}
		
		protected void exportSet() {
			JFileChooser jfc = new JFileChooser();
			int ret = jfc.showSaveDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				try{
					File file = jfc.getSelectedFile();
					if (file.exists()) {
						file.delete();
					}
					Properties prop = new Properties();
					saveSettings(prop);
					prop.setProperty("Set.Name", currentSet);
					FileOutputStream out = new FileOutputStream(file);
					prop.store(out, null);
					out.close();
				}catch(IOException ioe) {
					showIOError(ioe);
				}
			}
		}
		
		protected void setTooltip() {
			// make a list of all available tooltips plus "none" and "current"
			List list = CollectionFactory.createLinkedList();
			int selIndex = 2;
			list.add(getString("prefs.menu.tooltip.none"));
			if (mapperTooltip == null) {
				selIndex = 0;
			}
			list.add(getString("prefs.menu.tooltip.current"));
			if (mapperTooltip != null && mapperTooltip.equals(curText.getText())) {
				selIndex = 1;
			}
			list.add(getString("prefs.menu.tooltip.manual"));			
			if (mapper != null) {
				java.util.List l = mapper.getAllTooltipDefinitions();
				Iterator it = l.iterator();
				int i = 3;
				while(it.hasNext()) {
					String name = (String)it.next();
					String tip = (String)it.next();
					if (mapperTooltip != null && tip.equals(mapperTooltip)) {
						selIndex = i;
					}
					list.add(new StringPair(name, tip));
					i++;
				}
			}
			
			Object o[] = list.toArray();
			Object ret = JOptionPane.showInputDialog(this, getString("prefs.menu.tooltip.choose"), getString("prefs.menu.tooltip.title"), JOptionPane.QUESTION_MESSAGE, null, o, o[selIndex]);
			if (ret != null) {
				if (ret == o[0]) {
					mapperTooltip = null;
					saveSettings();
					loadSettings(); // to reset tooltip
					updateTooltipLabel();
				} else if (ret == o[1]) {
					mapperTooltip = curText.getText();
					saveSettings();
					loadSettings(); // to set tooltip
					updateTooltipLabel();
				} else if (ret == o[2]) {
					String tip = JOptionPane.showInputDialog(this, getString("prefs.menu.tooltip.input"));
					if (tip != null) {
						mapperTooltip = tip;
						saveSettings();
						loadSettings(); // to set tooltip
						updateTooltipLabel();
					}
				} else {
					StringPair pair = (StringPair)ret;
					mapperTooltip = pair.s2;
					saveSettings();
					loadSettings(); // to set tooltip
					updateTooltipLabel();
				}
			}
		}
		
		protected void showIOError(IOException ioe) {
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(this, ioe);
		}
		
		protected void updateTooltipLabel() {
			if (mapperTooltip == null) {
				tooltipLabel.setText(getString("prefs.menu.tlabel.none"));
			} else {
				String s = mapperTooltip;
				if (s.length() > 20) {
					s = s.substring(0,18)+"...";
				}
				tooltipLabel.setText(s);
			}
		}
		
		protected void updateTables() {						
			myTable.removeAll();
			myTable.set(cTable);
			myMapping.removeAll();
			myMapping.set(vMapping);
			
			cPanel.clear();
			cPanel.load();
			mPanel.clear();
			mPanel.load();
			
			minText.setText(minDef);
			curText.setText(curDef);
			maxText.setText(maxDef);
			uText.setText(unknownString);			
			dataChanged();
		}
		
		public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
			if (list.getSelectedValue() != null) {
				if (list.getSelectedValue() != currentSet) {
					applyPreferences();					
					loadSettings((String)list.getSelectedValue());					
					updateTables();
					updateTooltipLabel();
				}
			}
		}
		
		protected class StringPair {
			String s1, s2, s3;
			public StringPair(String s1, String s2) {
				this.s1 = s1;
				this.s2 = s2;
				s3=s1+": "+s2;
				if (s3.length() > 50) {
					s3 = s3.substring(0, 28)+"...";
				}
			}
			public String toString() {
				return s3;
			}
		}
		
		protected class ColorShowPanel extends JPanel {
			protected boolean showMapping = true;
			public ColorShowPanel() {
			}
			public ColorShowPanel(boolean show) {
				showMapping = show;
			}
			public void paintComponent(Graphics g) {
				Dimension size = this.getSize();
				float percent = 0;
				
				for(int i=0; i<size.width; i++) {
					percent = ((float)i)/((float)size.width);
					if (showMapping) {
						percent = myMapping.interpolate(percent);
					}
					g.setColor(myTable.interpolate(percent));
					g.drawLine(i,0,i,size.height);
				}
			}
		}
		
		protected class SelListener implements ItemListener {
			
			public void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
				JComponent button = (JComponent)itemEvent.getSource();
				if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
					button.setBorder(selBorder);
				} else if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
					button.setBorder(nonSelBorder);
				}
			}
		}
		
		protected class ColorPanel extends JPanel  implements MouseListener, MouseMotionListener, ActionListener {
			protected int moveIndex = -1, lastMoveIndex = -1;
			
			protected List value, mapping;
			
			protected Cursor normCursor, gripCursor, moveCursor, handCursor;
			
			protected ColorShowPanel myShow;
			protected ColorPainter mapPainter;
			
			protected JPopupMenu popup;
			protected JMenuItem addItem;
			protected JMenuItem removeItem;
			protected JMenuItem modifyItem;
			protected int addX = -1;
			
			protected JTextField textValue;
			protected java.text.NumberFormat number;
			
			public ColorPanel() {
				try{
					number = java.text.NumberFormat.getInstance(com.eressea.util.Locales.getGUILocale());
				}catch(IllegalStateException ise) {
					number = java.text.NumberFormat.getInstance();
				}
				number.setMaximumFractionDigits(3);
				number.setMinimumFractionDigits(0);
				
				this.setLayout(new BorderLayout());			
				
				myShow = new ColorShowPanel(false);
				myShow.setPreferredSize(new Dimension(200, 20));
				mapPainter = new ColorPainter();
				mapPainter.setPreferredSize(new Dimension(200, 30));
				mapPainter.addMouseListener(this);
				mapPainter.addMouseMotionListener(this);
				
				JPanel help = new JPanel(new BorderLayout());
				help.add(myShow, BorderLayout.CENTER);
				help.add(mapPainter, BorderLayout.SOUTH);
				help.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
				help.setMinimumSize(new Dimension(300, 50));
				help.setPreferredSize(new Dimension(300, 50));
				this.add(help, BorderLayout.CENTER);
				
				textValue = new JTextField(5);
				textValue.addActionListener(this);
				help = new JPanel(new FlowLayout(FlowLayout.CENTER));
				help.add(textValue);
				this.add(help, BorderLayout.SOUTH);
				
				normCursor = this.getCursor();
				gripCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
				moveCursor = new Cursor(Cursor.MOVE_CURSOR);
				handCursor = new Cursor(Cursor.HAND_CURSOR);
				
				value = CollectionFactory.createLinkedList();
				mapping = CollectionFactory.createLinkedList();
				
				createPopup();
			}
			
			protected void createPopup() {
				popup = new JPopupMenu();
				
				addItem = new JMenuItem(getString("prefs.popup.add"));
				addItem.addActionListener(this);
				removeItem = new JMenuItem(getString("prefs.popup.remove"));
				removeItem.addActionListener(this);
				modifyItem = new JMenuItem(getString("prefs.popup.modify"));
				modifyItem.addActionListener(this);
			}
			
			protected void fillPopup(int type) {
				popup.removeAll();
				
				if (type == -1) {
					popup.add(addItem);
				} else {
					popup.add(modifyItem);
					if (type != 0 && type != value.size() -1) {
						popup.add(removeItem);
					}
				}
			}
			
			public void clear() {
				value.clear();
				mapping.clear();
			}
			
			protected void load() {
				Collection col = myTable.getEntries();
				Iterator it = col.iterator();
				while(it.hasNext()) {
					Map.Entry entry = (Map.Entry)it.next();
					value.add(entry.getKey());
					mapping.add(entry.getValue());
				}
			}
			
			protected void save() {
				myTable.removeAll();
				if (value.size() > 0) {
					for(int i=0; i<value.size(); i++) {
						myTable.addEntry(((Float)value.get(i)).floatValue(), (Color)mapping.get(i));
					}
				}
			}
			
			protected int find(int x, int y, int maxDiff) {
				if (value.size() > 0) {
					int width = mapPainter.getWidth();
					int height = mapPainter.getHeight();
					for(int i=0;i<value.size();i++) {
						Float next = (Float)value.get(i);
						int j = (int)(next.floatValue()*width);
						if (Math.abs(x-j)<= maxDiff) {
							return i;
						}
					}
				}
				return -1;
			}
			
			protected void setText(int index) {
				lastMoveIndex = index;
				if (index == -1) {
					textValue.setText(null);
				} else {
					textValue.setText(number.format(value.get(index)));
				}
			}
			
			protected void searchMoving(MouseEvent e) {
				moveIndex = find(e.getX(), e.getY(), 5);
				// never move endings
				if (moveIndex == 0 || moveIndex == value.size()-1) {
					moveIndex = -1;
				}
				if (moveIndex >= 0) {
					mapPainter.setCursor(moveCursor);
					setText(moveIndex);
				}
			}
			
			protected boolean moveTo(int index, float newValue) {
				if (newValue == 1f || newValue == 0f) {
					return false;
				}
				if (value.size() > 0) {
					for(int i=0;i<value.size();i++) {
						if (newValue == ((Float)value.get(i)).floatValue()) {
							return false;
						}
					}
				}
				value.set(index, new Float(newValue));
				setText(index);
				save();
				dataChanged();
				return true;
			}
			
			protected void moved(MouseEvent e) {
				if (moveIndex >= 0) {
					float width = mapPainter.getWidth();
					float oldX = ((Float)value.get(moveIndex)).floatValue();
					
					float newHoriz = ((float)e.getX())/width;
					if (newHoriz < 0) {
						newHoriz = 0f;
					}
					if (newHoriz > 1) {
						newHoriz = 1f;
					}
					if (newHoriz != oldX) {
						// don't move endings horizontally
						// check sides
						float leftX = ((Float)value.get(moveIndex -1)).floatValue();
						if (newHoriz <= leftX) {
							int lx = (int)(leftX*width);
							newHoriz = ((float)(lx+1))/width;
						}
						float rightX = ((Float)value.get(moveIndex +1)).floatValue();
						if (rightX <= newHoriz ) {
							int rx = (int)(rightX*width);
							newHoriz = ((float)(rx-1))/width;
						}
						value.set(moveIndex, new Float(newHoriz));
						setText(moveIndex);
						save();
						dataChanged();
					}
				}
			}
			
			public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
				if (SwingUtilities.isRightMouseButton(mouseEvent) && moveIndex == -1) {
					addX = find(mouseEvent.getX(), mouseEvent.getY(), 5);
					fillPopup(addX);
					if (addX == -1) {
						addX = mouseEvent.getX();
					}
					popup.show(mapPainter, mouseEvent.getX(), mouseEvent.getY());
					return;
				}
				searchMoving(mouseEvent);
			}
			
			public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
			}
			
			public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
				if (moveIndex != -1) {
					moved(mouseEvent);
					moveIndex = -1;
					mapPainter.setCursor(gripCursor);
				}
			}
			
			public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
			}
			
			public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
			}
			
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				if (actionEvent.getSource() == addItem) {
					addColor();
					return;
				} else if (actionEvent.getSource() == removeItem) {
					removeColor();
					return;
				} else if (actionEvent.getSource() == modifyItem) {
					changeColor();
					return;
				} else if (actionEvent.getSource() == textValue) {
					textChange();
					return;
				}
			}
			
			protected void textChange() {
				if (lastMoveIndex != -1) {
					try{
						Number nf = number.parse(textValue.getText());
						float fl = nf.floatValue();
						if (fl <= 0) {
							fl = 0.001f;
						} else if (fl >= 1) {
							fl = 0.999f;
						}
						textValue.setText(number.format(fl));
						value.set(lastMoveIndex, new Float(fl));
						save();
						dataChanged();
					}catch(java.text.ParseException exc) {
						setText(lastMoveIndex);
					}
				}
			}
			
			protected void addColor() {
				int x = addX;
				int width = mapPainter.getWidth();
				int index = -1;
				int ox = -1;
				if (value.size() > 0) {
					Iterator it = value.iterator();
					while(ox < x && it.hasNext()) {
						float next = ((Float)it.next()).floatValue();
						ox = (int)(next*width);
						if (ox == x) { // don't allow equal x position
							return;
						}
						index++;
					}
				}
				if (ox < x) {
					index++;
				}
				Color col = JColorChooser.showDialog(this, getString("prefs.newcolor"), Color.white);
				if (col != null) {
					value.add(index, new Float(((float)x)/((float)width)));
					mapping.add(index, col);
					setText(index);
					save();
					dataChanged();
				}
			}
			
			protected void removeColor() {
				setText(-1);
				value.remove(addX);
				mapping.remove(addX);
				save();
				dataChanged();
			}
			
			protected void changeColor() {
				Color col = JColorChooser.showDialog(this, getString("prefs.changecolor"), (Color)mapping.get(addX));
				if (col != null) {
					mapping.set(addX, col);
					setText(addX);
					save();
					dataChanged();
				}
			}
			
			public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {
				int index = find(mouseEvent.getX(), mouseEvent.getY(), 5);
				if (index > 0) {
					if (index > 0 && index < value.size()-1) {
						mapPainter.setCursor(gripCursor);
					} else {
						mapPainter.setCursor(normCursor);
					}
				} else {
					mapPainter.setCursor(handCursor);
				}
			}
			
			public void mouseDragged(java.awt.event.MouseEvent mouseEvent) {
				if (moveIndex != -1) {
					moved(mouseEvent);
				}
			}
			protected class ColorPainter extends JPanel {
				protected Polygon poly;
				protected String numbers[];
				public ColorPainter() {
					java.text.NumberFormat number = null;
					try{
						number = java.text.NumberFormat.getInstance(com.eressea.util.Locales.getGUILocale());
					}catch(IllegalStateException ise) {
						number = java.text.NumberFormat.getInstance();
					}
					number.setMaximumFractionDigits(1);
					number.setMinimumFractionDigits(0);
					int x[] = new int[5];
					int y[] = new int[5];
					poly = new Polygon(x, y, 5);
					numbers = new String[11];
					for(int i=0;i<numbers.length;i++) {
						numbers[i] = number.format(((double)i)/((double)10));
					}
				}
				public void paintComponent(Graphics g) {
					Dimension size = this.getSize();
					g.setColor(this.getBackground());
					g.fillRect(0, 0, size.width, size.height);
					g.setColor(Color.black);
					FontMetrics fm = g.getFontMetrics();
					int y = size.height-1;
					int height = y-fm.getAscent()-1;
					for(int i=0;i<=10;i++) {
						int x = (i*size.width/10);
						if (x == size.width) {
							x--;
						}
						g.drawLine(x, 0, x, height-1);
						if (i == 0) {
							g.drawString(numbers[0], x, y);
						} else if (i == 10) {
							g.drawString(numbers[10], x-fm.stringWidth(numbers[10]), y);
						} else {
							g.drawString(numbers[i], x-(fm.stringWidth(numbers[i])/2), y);
						}
					}
					if (value.size() > 0) {
						y = Math.max(0, size.height-fm.getAscent()-1);
						for(int i=0; i<value.size(); i++) {
							int x = (int)(size.width*((Float)value.get(i)).floatValue());
							paintIcon(g, x, y, (Color)mapping.get(i));
						}
					}
				}
				protected void paintIcon(Graphics g, int x, int h, Color col) {
					g.setColor(col);
					poly.xpoints[0] = x;
					poly.ypoints[0] = 2;
					
					poly.xpoints[4] = x+4;
					poly.ypoints[4] = h/3;
					
					poly.xpoints[3] = x+4;
					poly.ypoints[3] = h-1;
					
					poly.xpoints[2] = x-4;
					poly.ypoints[2] = h-1;
					
					poly.xpoints[1] = x-4;
					poly.ypoints[1] = h/3;
					g.fillPolygon(poly);
					g.setColor(Color.black);
					g.drawPolygon(poly);
				}
			}
		}
		
		protected class MappingPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
			protected int moveIndex = -1;
			
			protected List value, mapping;
			
			protected PaintPanel mapPainter;
			
			protected Cursor normCursor, gripCursor, moveCursor;

			protected JPopupMenu popup;
			protected JMenuItem addItem;
			protected JMenuItem removeItem;
			protected int addX = -1, addY = -1;
			
			public MappingPanel() {
				this.setPreferredSize(new Dimension(300, 150));
				this.setLayout(new BorderLayout());
				
				normCursor = this.getCursor();
				gripCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
				moveCursor = new Cursor(Cursor.MOVE_CURSOR);
				
				mapPainter = new PaintPanel();
				mapPainter.addMouseListener(this);
				mapPainter.addMouseMotionListener(this);
				JPanel help = new JPanel();
				help.setLayout(new BorderLayout());
				help.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
				help.add(mapPainter);
				this.add(help, BorderLayout.CENTER);
				
				value = CollectionFactory.createLinkedList();
				mapping = CollectionFactory.createLinkedList();
				
				createPopup();
			}
			
			protected void createPopup() {
				popup = new JPopupMenu();
				
				addItem = new JMenuItem(getString("prefs.popup2.add"));
				addItem.addActionListener(this);
				removeItem = new JMenuItem(getString("prefs.popup2.remove"));
				removeItem.addActionListener(this);
			}

			protected void fillPopup(int type) {
				popup.removeAll();
				
				if (type == -1) {
					popup.add(addItem);
				} else {
					popup.add(removeItem);
				}
			}
			
			public void clear() {
				value.clear();
				mapping.clear();
			}
			
			protected void load() {
				Collection col = myMapping.getEntries();
				Iterator it = col.iterator();
				while(it.hasNext()) {
					Map.Entry entry = (Map.Entry)it.next();
					value.add(entry.getKey());
					mapping.add(entry.getValue());
				}
			}
			
			protected void save() {
				myMapping.removeAll();
				if (value.size() > 0) {
					for(int i=0; i<value.size(); i++) {
						myMapping.addEntry(((Float)value.get(i)).floatValue(), ((Float)mapping.get(i)).floatValue());
					}
				}
			}
			
			protected int find(int x, int y, int maxDiff) {
				if (value.size() > 0) {
					int width = mapPainter.getWidth();
					int height = mapPainter.getHeight();
					for(int i=0;i<value.size();i++) {
						Float next = (Float)value.get(i);
						int j = (int)(next.floatValue()*width);
						if (Math.abs(x-j)<= maxDiff) {
							Float oheight = (Float)mapping.get(i);
							int k = (int)((1f - oheight.floatValue())*height);
							if (Math.abs(y-k)<= maxDiff) {
								return i;
							}
						}
					}
				}
				return -1;
			}
			
			protected void searchMoving(MouseEvent e) {
				moveIndex = find(e.getX(), e.getY(), 5);
				if (moveIndex >= 0) {
					mapPainter.setCursor(moveCursor);
				}
			}
			
			protected void moved(MouseEvent e) {
				if (moveIndex >= 0) {
					float width = mapPainter.getWidth();
					float height = mapPainter.getHeight();
					float oldX = ((Float)value.get(moveIndex)).floatValue();
					float oldY = ((Float)mapping.get(moveIndex)).floatValue();
					float newVert = ((float)(height-e.getY()))/height;
					if (newVert < 0) {
						newVert = 0f;
					}
					if (newVert > 1) {
						newVert = 1f;
					}
					float newHoriz = ((float)e.getX())/width;
					if (newHoriz < 0) {
						newHoriz = 0f;
					}
					if (newHoriz > 1) {
						newHoriz = 1f;
					}
					if (newVert != oldY || newHoriz != oldX) {
						// don't move endings horizontally
						if (moveIndex == 0 || moveIndex == value.size() -1 ) {
							if (newVert != oldY) {
								mapping.set(moveIndex, new Float(newVert));
							} else {
								return;
							}
						} else {
							// check sides
							if (newHoriz != oldX) {
								float leftX = ((Float)value.get(moveIndex -1)).floatValue();
								if (newHoriz <= leftX) {
									int lx = (int)(leftX*width);
									newHoriz = ((float)(lx+1))/width;
								}
								float rightX = ((Float)value.get(moveIndex +1)).floatValue();
								if (rightX <= newHoriz ) {
									int rx = (int)(rightX*width);
									newHoriz = ((float)(rx-1))/width;
								}
								value.set(moveIndex, new Float(newHoriz));
							}
							if (newVert != oldY) {
								mapping.set(moveIndex, new Float(newVert));
							}
						}
						save();
						dataChanged();
					}
				}
			}
			
			protected void addMapping(int x, int y) {
				int width = mapPainter.getWidth();
				int index = -1;
				int ox = -1;
				if (value.size() > 0) {
					Iterator it = value.iterator();
					while(ox < x && it.hasNext()) {
						float next = ((Float)it.next()).floatValue();
						ox = (int)(next*width);
						if (ox == x) { // don't allow equal x position
							return;
						}
						index++;
					}
				}
				if (ox < x) {
					index++;
				}
				value.add(index, new Float(((float)x)/((float)width)));
				mapping.add(index, new Float(((float)(mapPainter.getHeight()-y))/((float)mapPainter.getHeight())));
				save();
				dataChanged();
			}
			
			protected void removeMapping(int index) {
				if (index > 0 && index < value.size()-1) {
					value.remove(index);
					mapping.remove(index);
					save();
					dataChanged();
				}
			}
			
			public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
				if (SwingUtilities.isRightMouseButton(mouseEvent) && moveIndex == -1) {
					addX = find(mouseEvent.getX(), mouseEvent.getY(), 5);					
					fillPopup(addX);
					if (addX == -1) {
						addX = mouseEvent.getX();
						addY = mouseEvent.getY();
					}
					popup.show(mapPainter, mouseEvent.getX(), mouseEvent.getY());
					return;
				}

				searchMoving(mouseEvent);
			}
			
			public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
			}
			
			public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
				if (moveIndex != -1) {
					moved(mouseEvent);
					moveIndex = -1;
					mapPainter.setCursor(gripCursor);
				}
			}
			
			public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
			}
			
			public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
			}
			
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				if (actionEvent.getSource() == addItem) {
					addMapping(addX, addY);
					return;
				} else if (actionEvent.getSource() == removeItem) {
					removeMapping(addX);
					return;
				}
			}
			
			public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {
				int index = find(mouseEvent.getX(), mouseEvent.getY(), 5);
				if (index >= 0) {
					mapPainter.setCursor(gripCursor);
				} else {
					mapPainter.setCursor(normCursor);
				}
			}
			
			public void mouseDragged(java.awt.event.MouseEvent mouseEvent) {
				if (moveIndex != -1) {
					moved(mouseEvent);
				}
			}
			
			protected class PaintPanel extends JPanel {
				protected Stroke stroke;
				public PaintPanel() {
					float dashs[] = {1f, 4f};
					stroke = new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.5f, dashs, 0f);
				}
				public void paintComponent(Graphics g) {
					Dimension size = this.getSize();
					g.setColor(this.getBackground());
					g.fillRect(0,0, size.width, size.height);
					float fWidth = size.width, fHeight = size.height;
					Stroke oldStroke = null;
					if (g instanceof Graphics2D) {
						Graphics2D g2 = (Graphics2D)g;
						oldStroke = g2.getStroke();
						g2.setStroke(stroke);
					}
					g.setColor(Color.darkGray);
					for(int i=1;i<10;i++) {
						int x = i*size.width/10;
						g.drawLine(x, 0, x, size.height);
						int y = i*size.height/10;
						g.drawLine(0, y, size.width, y);
					}
					g.setColor(Color.black);
					if (oldStroke != null) {
						((Graphics2D)g).setStroke(oldStroke);
					}
					if (value.size() > 0) {
						int lastX = -1, lastY = -1;
						for(int i = 0; i<value.size(); i++) {
							int x = (int)(((Float)value.get(i)).floatValue()*fWidth);
							int y = (int)((1-((Float)mapping.get(i)).floatValue())*fHeight);
							g.fillRect(x-2, y-2, 5, 5);
							if (lastX >= 0) {
								g.drawLine(lastX, lastY, x, y);
							}
							lastX = x;
							lastY = y;
						}
					}
				}
			}
		}
	}
}
