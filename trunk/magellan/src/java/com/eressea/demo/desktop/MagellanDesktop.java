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

package com.eressea.demo.desktop;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.eressea.swing.preferences.ExtendedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class MagellanDesktop extends JPanel implements WindowListener, ActionListener,
													   PreferencesFactory
{
	private static final Logger log = Logger.getInstance(MagellanDesktop.class);

	// mode possibilities

	/** The Magellan desktop will be shown with split panes. */
	public static final int MODE_SPLIT = 0;

	/** The Magellan desktop will consist of frames. */
	public static final int MODE_FRAME = 1;

	/** The Magellan desktop will have a configurable layout. */
	public static final int MODE_LAYOUT = 2;

	// activation mode possibilites

	/** All windows will stay were they are when a frame is activated. */
	public static final int ACTIVATION_MODE_NONE = 0;

	/** All frames are moved to front if the client window is activated. */
	public static final int ACTIVATION_MODE_CLIENT_ONLY = 1;

	/** All frames are move to front if any frame is activated. */
	public static final int ACTIVATION_MODE_ANY = 2;

	/**
	 * <p>
	 * The current desktop mode.
	 * </p>
	 * Possible values are MODE_SPLIT amd MODE_FRAME.
	 */
	private int mode = MODE_SPLIT;

	/**
	 * Holds all the components. The key is the global id like NAME or OVERVIEW, the value is the
	 * component.
	 */
	private Map components;
	private Map componentsReversed;

	/** Some shortcut things */
	private Map shortCutListeners = CollectionFactory.createHashMap();
	private Map shortCutTranslations = CollectionFactory.createHashMap();
	private KeyHandler keyHandler;

	/**
	 * In case of MODE_FRAME this HashMap holds all the frames. The key is the ID of the covered
	 * component.
	 */
	private Map frames;

	/**
	 * Holds the activation mode. This value is used in Framed Mode when a frame is activated. It
	 * decides wether the other frames should be activated, too.
	 */
	private int activationMode;

	/** Decides if all frames should be (de)iconified if the client frame is (de)iconified. */
	private boolean iconify;

	/** Holds the settings in case a mode-change occurs. */
	private Properties settings;

	/**
	 * Stores the root component of the splitted desktop. So the desktop can be saved even if a
	 * mode-changed occured.
	 */
	private JComponent splitRoot;

	/** Holds the client frame. This is for (de)iconification compares. */
	private Frame client;

	/** Holds the frame rect for Magellan in Split-Mode. */
	private Rectangle splitRect;

	/** Holds the frame rect for the main Magellan window in Frame-Mode. */
	private Rectangle frameRect;

	/**
	 * Just a variable to suppress double activation events. If anyone can do it better, please DO
	 * IT.
	 */
	private boolean inFront = false;
	private Timer timer;

	// Split mode objects
	private SplitBuilder splitBuilder;
	private Map splitSets;
	private String splitName;

	// Desktop menu
	private JMenu desktopMenu;

	// Desktop menu
	private JMenu setMenu;

	// Desktop menu
	private JMenu framesMenu;

	// Desktop menu
	private JMenu layoutMenu;
	private ButtonGroup setMenuGroup;

	//Objects for mode "Layout"
	private Map layoutComponents;
	private DesktopLayoutManager lManager;
	private String layoutName;
	private File magellanDir = null;
	private boolean modeInitialized[];
	private int bgMode = -1;
	private Image bgImage = null;
	private Color bgColor = Color.red;

	/**
	 * Creates new MagellanDesktop
	 *
	 * @param client TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 * @param components TODO: DOCUMENT ME!
	 * @param dir TODO: DOCUMENT ME!
	 */
	public MagellanDesktop(Frame client, Properties settings, Map components, File dir) {
		this.client = client;

		magellanDir = dir;
		timer = new Timer(1000, this);
		timer.start();
		timer.stop();
		keyHandler = new KeyHandler();
		client.addWindowListener(this);
		this.settings = settings;
		componentsReversed = CollectionFactory.createHashMap();
		setManagedComponents(components);
		setLayout(new BorderLayout());

		try {
			File file = getDesktopFile(false);

			if(file != null) {
				log.info("Using Desktopfile: " + file);
			}
		} catch(Exception exc) {
		}

		modeInitialized = new boolean[3];

		for(int i = 0; i < 3; i++) {
			modeInitialized[i] = false;
		}

		initSplitSets();
		initFrameRectangles(); // to find supported frames for menu

		// init desktop menu
		initDesktopMenu();

		// find the desktop mode, default is split
		String dmode = settings.getProperty("Desktop.Type", "SPLIT");

		if(dmode.equals("SPLIT")) {
			mode = MODE_SPLIT;
		} else if(dmode.equals("FRAMES")) {
			mode = MODE_FRAME;
		} else if(dmode.equals("LAYOUT")) {
			mode = MODE_LAYOUT;
		} else {
			mode = MODE_SPLIT;
		}

		// init the desktop
		if(mode == MODE_SPLIT) { // try to load a split set

			if(!initSplitSet(settings.getProperty("Desktop.SplitSet", "Standard"))) {
				//try to load default
				if(!initSplitSet("Standard")) {
					Iterator it = splitSets.keySet().iterator();
					boolean loaded = false;

					while(!loaded && it.hasNext()) {
						loaded = initSplitSet((String) it.next());
					}

					if(!loaded) { //Sorry, cannot load -> build new default set
						JOptionPane.showMessageDialog(client, getString("msg.corruptsettings.text"));
						System.exit(1);
					}
				}
			}
		}

		if(mode == MODE_FRAME) {
			initFrames();
		}

		if(mode == MODE_LAYOUT) {
			initLayout(settings.getProperty("Desktop.Layout", "Standard"));
		}

		loadTranslations();

		// register keystrokes
		registerKeyStrokes();

		//for adapter
		loadFrameModeSettings();
	}

	/**
	 * Returns the Magellan Desktop Configuration file. If save is false, the method searches in
	 * various places for compatibility with older version. The priorities are:
	 * 
	 * <ol>
	 * <li>
	 * Magellan directory
	 * </li>
	 * <li>
	 * HOME Directory
	 * </li>
	 * <li>
	 * Local directory
	 * </li>
	 * </ol>
	 * 
	 * The file to save to is always in the magellan directory.
	 *
	 * @param save TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public File getDesktopFile(boolean save) {
		if(save) { // always save to magellan directory

			return new File(magellanDir, "magellan_desktop.ini");
		}

		File file = null;

		// LOAD
		try {
			// first look in Class-Directory
			file = new File(magellanDir, "magellan_desktop.ini");

			if(file.exists()) {
				return file;
			}
		} catch(Exception exc2) {
		}

		try {
			// look for global ini
			file = new File(System.getProperty("user.home"), "magellan_desktop.ini");

			if(file.exists()) {
				return file;
			}
		} catch(Exception exc2) {
		}

		try {
			// look for local ini
			file = new File(".", "magellan_desktop.ini");

			if(file.exists()) {
				return file;
			}
		} catch(Exception exc3) {
		}

		// return the default
		return new File(magellanDir, "magellan_desktop.ini");
	}

	/*
	 ########################
	 # Property access code #
	 ########################
	 */
	public JMenu getDesktopMenu() {
		return desktopMenu;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Set getAvailableSplitSets() {
		return splitSets.keySet();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param set TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean addSplitSet(String name, List set) {
		FrameTreeBuilder ftb = new FrameTreeBuilder();

		try {
			ftb.buildTree(set.iterator());
		} catch(Exception exc) {
			return false;
		}

		Object o = splitSets.put(name, ftb.getRoot());

		if(o == null) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(name, false);
			item.addActionListener(this);
			setMenuGroup.add(item);
			setMenu.add(item);
		}

		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Set getAvailableLayouts() {
		return layoutComponents.keySet();
	}

	/**
	 * Returns all the components available to this desktop. Keys are IDs, Values are components.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getManagedComponents() {
		return components;
	}

	/**
	 * Sets the components available for this desktop. This is only useful if called before a
	 * mode-change.
	 *
	 * @param components TODO: DOCUMENT ME!
	 */
	public void setManagedComponents(Map components) {
		this.components = components;

		Iterator it = components.keySet().iterator();
		componentsReversed.clear();

		while(it.hasNext()) {
			Object o = it.next();
			componentsReversed.put(components.get(o), o);
		}
	}

	/**
	 * Returns the activation mode.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getActivationMode() {
		return activationMode;
	}

	/**
	 * Sets the activation mode. Possible values are:
	 * 
	 * <ul>
	 * <li>
	 * ACTIVATION_MODE_NONE: Never activate other windows
	 * </li>
	 * <li>
	 * ACTIVATION_MODE_CLIENT_ONLY: Activate other windows if the client frame is activated
	 * </li>
	 * <li>
	 * ACTIVATION_MODE_ANY: Activate all frames if any of them is activated.
	 * </li>
	 * </ul>
	 * 
	 *
	 * @param activationMode TODO: DOCUMENT ME!
	 */
	public void setActivationMode(int activationMode) {
		this.activationMode = activationMode;
	}

	/**
	 * Returns the iconification mode. TRUE means, that all windows are (de)iconified if the main
	 * window is (de)iconified.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isIconify() {
		return iconify;
	}

	/**
	 * Sets the iconification mode. TRUE means, that all windows are (de)iconified if the main
	 * window is (de)iconified.
	 *
	 * @param iconify TODO: DOCUMENT ME!
	 */
	public void setIconify(boolean iconify) {
		this.iconify = iconify;
	}

	/**
	 * this function creates a FrameRectangle with given id. The name is evaluated (and translated)
	 * out of the given id.
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private FrameRectangle createFrameRectangle(String id) {
		String translationkey = id.toLowerCase();

		if(id.equals("NAME&DESCRIPTION")) {
			translationkey = "name";
		}

		if(id.equals("OVERVIEW&HISTORY")) {
			translationkey = "overviewandhistory";
		}

		// FrameRectangle frame=new FrameRectangle(name,id);
		// new FrameRectangle(getString("frame.orders.title"),"ORDERS");
		// new FrameRectangle(getString("frame.details.title"),"DETAILS");
		// new FrameRectangle(getString("frame.name.title"),"NAME&DESCRIPTION");
		// new FrameRectangle(getString("frame.messages.title"),"MESSAGES");
		// new FrameRectangle(getString("frame.map.title"),"MAP");
		// new FrameRectangle(getString("frame.minimap.title"),"MINIMAP");
		// new FrameRectangle(getString("frame.overviewandhistory.title"),"OVERVIEW&HISTORY");
		// new FrameRectangle(getString("frame.overview.title"),"OVERVIEW");
		// new FrameRectangle(getString("frame.history.title"),"HISTORY");
		return new FrameRectangle(getString("frame." + translationkey + ".title"), id);
	}

	/**
	 * Creates the menu "Desktop" for Magellan. At first creates a sub-menu with all frames, then a
	 * sub-menu for all available split sets and at last a sub-menu with all layouts.
	 */
	protected void initDesktopMenu() {
		desktopMenu = new JMenu(getString("menu.desktop.caption"));
		desktopMenu.setMnemonic(getString("menu.desktop.mnemonic").charAt(0));

		framesMenu = new JMenu(getString("menu.frames.caption"));
		framesMenu.setMnemonic(getString("menu.frames.menmonic").charAt(0));

		if(frames.size() > 0) {
			JMenuItem mitem = new JMenuItem(getString("menu.frames.activate"));
			mitem.addActionListener(this);
			framesMenu.add(mitem);
			framesMenu.addSeparator();

			Iterator it = frames.values().iterator();

			while(it.hasNext()) {
				FrameRectangle f = (FrameRectangle) it.next();

				if(f == null) {
					continue;
				}

				JCheckBoxMenuItem mi = new JCheckBoxMenuItem(f.getFrameTitle(), false);
				framesMenu.add(mi);
				mi.addActionListener(this);
			}
		}

		framesMenu.setEnabled(frames.size() > 0);
		desktopMenu.add(framesMenu);
		setMenu = new JMenu(getString("menu.set.caption"));
		setMenu.setMnemonic(getString("menu.set.mnemonic").charAt(0));
		setMenuGroup = new ButtonGroup();

		int index = 0;

		if((splitSets != null) && (splitSets.size() > 0)) {
			Iterator it = splitSets.keySet().iterator();
			index = 1;

			while(it.hasNext()) {
				JMenuItem mi = null;
				String text = (String) it.next();

				if(index < 10) {
					mi = new JCheckBoxMenuItem(String.valueOf(index) + ": " + text, false);
					mi.setMnemonic(Character.forDigit(index, 10));
				} else if(index == 10) {
					mi = new JCheckBoxMenuItem("0: " + text, false);
					mi.setMnemonic('0');
				} else {
					mi = new JCheckBoxMenuItem(text, false);
				}

				mi.setActionCommand(text);
				setMenu.add(mi);
				mi.addActionListener(this);
				setMenuGroup.add(mi);
				index++;
			}
		} else {
			setMenu.setEnabled(false);
		}

		desktopMenu.add(setMenu);
		layoutMenu = new JMenu(getString("menu.layout.caption"));
		layoutMenu.setMnemonic(getString("menu.layout.mnemonic").charAt(0));

		if((layoutComponents != null) && (layoutComponents.size() > 0)) {
			Iterator it = layoutComponents.keySet().iterator();
			index = 1;

			while(it.hasNext()) {
				JMenuItem mi = null;
				String text = (String) it.next();

				if(index < 10) {
					mi = new JCheckBoxMenuItem(String.valueOf(index) + ": " + text, false);
					mi.setMnemonic(Character.forDigit(index, 10));
				} else if(index == 10) {
					mi = new JCheckBoxMenuItem("0: " + text, false);
					mi.setMnemonic('0');
				} else {
					mi = new JCheckBoxMenuItem(text, false);
				}

				mi.setActionCommand(text);
				layoutMenu.add(mi);
				mi.addActionListener(this);
				setMenuGroup.add(mi);
				index++;
			}
		} else {
			layoutMenu.setEnabled(false);
		}

		desktopMenu.add(layoutMenu);
	}

	/**
	 * Replaces the first substring toRep in def by repBy.
	 *
	 * @param def TODO: DOCUMENT ME!
	 * @param toRep TODO: DOCUMENT ME!
	 * @param repBy TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected String replace(String def, String toRep, String repBy) {
		if(def.indexOf(toRep) > -1) {
			return def.substring(0, def.indexOf(toRep)) + repBy +
				   def.substring(def.indexOf(toRep) + toRep.length());
		}

		return def;
	}

	/**
	 * Parses the Magellan Desktop Configuration file for Split sets and custom layouts.
	 */
	protected void initSplitSets() {
		Map loaded = CollectionFactory.createHashMap();
		boolean read = false;
		List block = CollectionFactory.createLinkedList();

		// load from magellan_desktop.ini
		try {
			File mdfile = getDesktopFile(false);
			BufferedReader r = new BufferedReader(new FileReader(mdfile));
			String s = null;
			String blockName = null;
			boolean inBlock = false;

			do {
				s = r.readLine();

				if((s != null) && !s.equals("")) {
					read = true;

					if(s.startsWith("[")) {
						if(inBlock) {
							loaded.put(blockName, block);
						}

						block = CollectionFactory.createArrayList();
						blockName = s.substring(1, s.length() - 1);
						inBlock = true;
					} else {
						s = replace(s, "COMMANDS", "ORDERS");
						block.add(s.trim());
					}
				}
			} while(s != null);

			if(inBlock && (block.size() > 0)) { // put the last block
				loaded.put(blockName, block);
			}

			r.close();
		} catch(Exception exc) {
			log.error("Error reading desktop file " + exc.toString(), exc);
		}

		//maybe old-style file
		if(read && (loaded.size() == 0)) {
			loaded.put("Standard", block);
		}

		// make sure there's a default set or even any set
		if(!loaded.containsKey("Standard")) {
			loaded.put("Standard", createStandardSplitSet());
			log.info("Creating \"Standard\" Split-Set.");
		}

		// Parse the loaded definitions
		FrameTreeBuilder builder = new FrameTreeBuilder();
		Iterator it = loaded.keySet().iterator();

		while(it.hasNext()) {
			String name = (String) it.next();
			List def = (List) loaded.get(name);

			// that's a layout
			if(name.startsWith("Layout_")) {
				loadLayout(name.substring(7), def);
			}
			// that's a split set
			else {
				log.info("Parsing split-set definition for \"" + name + "\"...");

				Object node = null;

				try {
					builder.buildTree(def.iterator());
					node = builder.getRoot();
				} catch(Exception exc) {
					node = null;
				}

				if(node != null) {
					if(splitSets == null) {
						splitSets = CollectionFactory.createHashMap();
					}

					splitSets.put(name, node);
					log.info("Successful!");
				} else {
					log.info("Unable to parse! Please rework/remove this split-set.");
				}
			}
		}

		// create default values if nothing was successfully parsed
		if(splitSets == null) {
			splitSets = CollectionFactory.createHashMap();

			try {
				builder.buildTree(createStandardSplitSet().iterator());
				splitSets.put("Standard", builder.getRoot());
			} catch(Exception exc) {
			}

			log.info("Creating default split set.");
		}

		if(layoutComponents == null) {
			buildDefaultLayoutComponents();
		}
	}

	/**
	 * Creates the default Split set. Current implementation emulates old-style Magellan.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected List createStandardSplitSet() {
		List st = CollectionFactory.createArrayList(22);
		st.add("SPLIT 400 H");
		st.add("SPLIT 200 H");
		st.add("SPLIT 400 V");
		st.add("SPLIT 300 V");
		st.add("COMPONENT OVERVIEW");
		st.add("COMPONENT HISTORY");
		st.add("/SPLIT");
		st.add("COMPONENT MINIMAP");
		st.add("/SPLIT");
		st.add("SPLIT 400 V");
		st.add("COMPONENT MAP");
		st.add("COMPONENT MESSAGES");
		st.add("/SPLIT");
		st.add("/SPLIT");
		st.add("SPLIT 400 V");
		st.add("SPLIT 200 V");
		st.add("COMPONENT NAME&DESCRIPTION");
		st.add("COMPONENT DETAILS");
		st.add("/SPLIT");
		st.add("COMPONENT ORDERS");
		st.add("/SPLIT");
		st.add("/SPLIT");

		return st;
	}

	/**
	 * Creates a default layout. Current implementation emulates old-style Magellan.
	 */
	protected void buildDefaultLayoutComponents() {
		lManager = new DesktopLayoutManager();

		Map lMap = CollectionFactory.createHashMap();
		lMap.put("OVERVIEW", lManager.createCPref(0, 0, 0.25, 0.5));
		lMap.put("HISTORY", lManager.createCPref(0, 0.5, 0.25, 0.25));
		lMap.put("MINIMAP", lManager.createCPref(0, 0.75, 0.25, 0.25));
		lMap.put("MAP", lManager.createCPref(0.25, 0, 0.5, 0.75));
		lMap.put("MESSAGES", lManager.createCPref(0.25, 0.75, 0.5, 0.25));
		lMap.put("NAME&DESCRIPTION", lManager.createCPref(0.75, 0, 0.25, 0.25));
		lMap.put("DETAILS", lManager.createCPref(0.75, 0.25, 0.25, 0.5));
		lMap.put("ORDERS", lManager.createCPref(0.75, 0.75, 0.25, 0.25));
		layoutComponents = CollectionFactory.createHashMap();
		layoutComponents.put("Standard", lMap);
		log.info("Building \"Standard\" Layout");
	}

	/**
	 * Loads a layout with name lName out of the given definition.
	 *
	 * @param lName TODO: DOCUMENT ME!
	 * @param def TODO: DOCUMENT ME!
	 */
	protected void loadLayout(String lName, List def) {
		String msg = "Loading layout \"" + lName + "\"...";

		if(lManager == null) {
			lManager = new DesktopLayoutManager();
		}

		Map lMap = CollectionFactory.createHashMap();
		Iterator it = def.iterator();

		while(it.hasNext()) {
			String sdef = (String) it.next();

			if(sdef.indexOf('=') < 1) { //syntax is COMPONENT=x;y;w;h[;configuration]

				continue;
			}

			String cName = sdef.substring(0, sdef.indexOf('='));
			String definition = sdef.substring(sdef.indexOf('=') + 1);

			DesktopLayoutManager.CPref cPref = lManager.parseCPref(definition);

			if(cPref != null) {
				lMap.put(cName, cPref);
			}
		}

		if(lMap.size() > 0) {
			if(layoutComponents == null) {
				layoutComponents = CollectionFactory.createHashMap();
			}

			layoutComponents.put(lName, lMap);
			log.info(msg + "Successful!");
		} else {
			log.info(msg + "Unable to resolve! Please rework/remove this layout.");
		}
	}

	/**
	 * Loads the frame definitions out of the settings. If they don't exist, a default set is
	 * created using initFrameDefault().
	 */
	protected void initFrameRectangles() {
		frames = CollectionFactory.createHashMap();

		// seems to be a valid properties object
		if(settings.containsKey("Desktop.Frame0")) {
			int count = 0;
			String indexString = null;

			do {
				indexString = "Desktop.Frame" + String.valueOf(count);

				// good, a key found
				if(settings.containsKey(indexString)) {
					String str = settings.getProperty(indexString);
					str = replace(str, "COMMANDS", "ORDERS");

					StringTokenizer st = new StringTokenizer(str, ",");

					try {
						String xS = st.nextToken();
						String yS = st.nextToken();
						String wS = st.nextToken();
						String hS = st.nextToken();
						String id = st.nextToken();
						/* String name = */ st.nextToken();
						String status = st.nextToken();
						int x = 0;
						int y = 0;
						int w = 0;
						int h = 0;
						x = Integer.parseInt(xS);
						y = Integer.parseInt(yS);
						w = Integer.parseInt(wS);
						h = Integer.parseInt(hS);

						// juch-hu, a valid frame found
						if(components.containsKey(id)) {
							// FrameRectangle frame=new FrameRectangle(name,id);
							FrameRectangle frame = createFrameRectangle(id);
							frame.setBounds(x, y, w, h);
							frames.put(id, frame);

							if(status.equals("ICON")) {
								frame.setState(Frame.ICONIFIED);
							} else {
								frame.setState(Frame.NORMAL);
							}

							// new token for visibility?
							if(st.hasMoreTokens()) {
								String visi = st.nextToken();

								if(visi.equalsIgnoreCase("INVISIBLE")) {
									frame.setVisible(false);
								} else {
									frame.setVisible(true);
								}
							}

							if(st.hasMoreTokens()) {
								frame.setConfiguration(st.nextToken());
							}
						}
					} catch(Exception exc) {
					}
				}

				count++;
			} while(settings.containsKey(indexString));

			if(frames.size() > 0) {
				return;
			}

			// hu - no frames parsed, use default
		}

		// use a default screen
		log.info("Using default frames.");
		initFrameDefault();
	}

	/**
	 * Initializes a default frame set. This implementation emulates the default splitted screen:
	 * 
	 * <p>
	 * |---1/3---|----1/3---|-1/3------   | 1/3     |          |Name&    1/3 | Overview  | Map
	 * |Descript.    |         |----------|          |----------| 1/3 History   | |Details  1/3
	 * |---------|----------|----------| 1/3 Minimap   | Messages |Commands 1/3
	 * |---------|----------|----------|
	 * </p>
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected List initFrameDefault() {
		List compsUsed = CollectionFactory.createLinkedList();
		Toolkit t = client.getToolkit();
		Rectangle frameBounds = computeRectangle(t.getScreenSize());

		// the state the windows are created with
		int frameState = Frame.NORMAL;
		Dimension screenSize = t.getScreenSize();

		// use screenSize/100 as minimum for frameset, iconify if below
		if((frameBounds.width * frameBounds.height) < ((screenSize.width * screenSize.height) / 100)) {
			frameState = Frame.ICONIFIED;
		}

		FrameRectangle frame = null;

		// check if tree and history are separated
		if(components.containsKey("OVERVIEW") && components.containsKey("HISTORY")) {
			// create tree frame
			frame = createFrameRectangle("OVERVIEW");
			compsUsed.add(components.get("OVERVIEW"));
			frame.setBounds(frameBounds.x, frameBounds.y, frameBounds.width / 3,
							frameBounds.height / 3);
			frame.setState(frameState);
			frames.put("OVERVIEW", frame);
			checkFrameMenuItem(frame);

			// create history frame
			frame = createFrameRectangle("HISTORY");
			compsUsed.add(components.get("HISTORY"));
			frame.setBounds(frameBounds.x, frameBounds.y + (frameBounds.height / 3),
							frameBounds.width / 3, frameBounds.height / 3);
			frame.setState(frameState);
			frames.put("HISTORY", frame);
			checkFrameMenuItem(frame);
		}
		// check if connected
		else if(components.containsKey("OVERVIEW&HISTORY")) {
			// create frame for overview and history
			frame = createFrameRectangle("OVERVIEW&HISTORY");
			compsUsed.add(components.get("OVERVIEW&HISTORY"));
			frame.setBounds(frameBounds.x, frameBounds.y, frameBounds.width / 3,
							(2 * frameBounds.height) / 3);
			frame.setState(frameState);
			frames.put("OVERVIEW&HISTORY", frame);
			checkFrameMenuItem(frame);
		}

		// check for minimap
		if(components.containsKey("MINIMAP")) {
			// create frame for map
			frame = createFrameRectangle("MINIMAP");
			compsUsed.add(components.get("MINIMAP"));
			frame.setBounds(frameBounds.x, frameBounds.y + ((2 * frameBounds.height) / 3),
							frameBounds.width / 3, frameBounds.height / 3);
			frame.setState(frameState);
			frames.put("MINIMAP", frame);
			checkFrameMenuItem(frame);
		}

		// check for map
		if(components.containsKey("MAP")) {
			// create frame for map
			frame = createFrameRectangle("MAP");
			compsUsed.add(components.get("MAP"));
			frame.setBounds(frameBounds.x + (frameBounds.width / 3), frameBounds.y,
							frameBounds.width / 3, (2 * frameBounds.height) / 3);
			frame.setState(frameState);
			frames.put("MAP", frame);
			checkFrameMenuItem(frame);
		}

		// check for message panel
		if(components.containsKey("MESSAGES")) {
			// create frame for map
			frame = createFrameRectangle("MESSAGES");
			compsUsed.add(components.get("MESSAGES"));
			frame.setBounds(frameBounds.x + (frameBounds.width / 3),
							frameBounds.y + ((2 * frameBounds.height) / 3), frameBounds.width / 3,
							frameBounds.height / 3);
			frame.setState(frameState);
			frames.put("MESSAGES", frame);
			checkFrameMenuItem(frame);
		}

		// check for name&description
		if(components.containsKey("NAME&DESCRIPTION")) {
			// create frame for name
			frame = createFrameRectangle("NAME&DESCRIPTION");
			compsUsed.add(components.get("NAME&DESCRIPTION"));
			frame.setBounds(frameBounds.x + ((2 * frameBounds.width) / 3), frameBounds.y,
							frameBounds.width / 3, frameBounds.height / 3);
			frame.setState(frameState);
			frames.put("NAME&DESCRIPTION", frame);
			checkFrameMenuItem(frame);
		}

		// check for details pane
		if(components.containsKey("DETAILS")) {
			// create frame for details panel
			frame = createFrameRectangle("DETAILS");
			compsUsed.add(components.get("DETAILS"));
			frame.setBounds(frameBounds.x + ((2 * frameBounds.width) / 3),
							frameBounds.y + (frameBounds.height / 3), frameBounds.width / 3,
							frameBounds.height / 3);
			frame.setState(frameState);
			frames.put("DETAILS", frame);
			checkFrameMenuItem(frame);
		}

		// check for orders
		if(components.containsKey("ORDERS") || components.containsKey("COMMANDS")) {
			// create frame for details panel
			frame = createFrameRectangle("ORDERS");

			if(components.containsKey("ORDERS")) {
				compsUsed.add(components.get("ORDERS"));
			} else {
				compsUsed.add(components.get("COMMANDS"));
			}

			frame.setBounds(frameBounds.x + ((2 * frameBounds.width) / 3),
							frameBounds.y + ((2 * frameBounds.height) / 3), frameBounds.width / 3,
							frameBounds.height / 3);
			frame.setState(frameState);
			frames.put("ORDERS", frame);
			checkFrameMenuItem(frame);
		}

		return compsUsed;
	}

	/**
	 * Computes the largest free rectangle on the screen: the biggest free place without the client
	 * frame.
	 * 
	 * <p>
	 * SOMEBODY SHOULD ADD CODE TO SUPPRESS TASKBAR OVERLAY!!!
	 * </p>
	 *
	 * @param screenSize TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected Rectangle computeRectangle(Dimension screenSize) {
		// create the screen rectangle
		Rectangle screen = new Rectangle(0, 0, screenSize.width, screenSize.height);

		//create the client rectangle
		Rectangle clientR = client.getBounds();

		// client is not on screen!
		if(!screen.intersects(clientR)) {
			return screen;
		}

		clientR = screen.intersection(clientR);

		Rectangle horizontal = null;

		// the horizontally better one(prefer below client)
		if(clientR.y > (screen.height - (clientR.y + clientR.height))) {
			horizontal = new Rectangle(screen.width, clientR.y);
		} else {
			horizontal = new Rectangle(0, clientR.y + clientR.height, screen.width,
									   screen.height - (clientR.y + clientR.height));
		}

		Rectangle vertical = null;

		// the vertically better one(prefer right)
		if(clientR.x > (screen.width - (clientR.x + clientR.width))) {
			vertical = new Rectangle(clientR.x, screen.height);
		} else {
			vertical = new Rectangle(clientR.x + clientR.width, 0,
									 screen.width - (clientR.x + clientR.width), screen.height);
		}

		// check the bigger one, prefer the horizontal one
		if((vertical.width * vertical.height) > (horizontal.width * horizontal.height)) {
			return vertical;
		}

		return horizontal;
	}

	/*
	 ########################
	 # Desktop init methods #
	 ########################
	 */

	/**
	 * Sets the Magellan window bounds according to current mode.
	 */
	protected void setClientBounds() {
		if((mode == MODE_SPLIT) || (mode == MODE_LAYOUT)) {
			if(splitRect == null) { // not initialized before, load it
				splitRect = loadRect(splitRect, "Client");
			}

			if(splitRect == null) { // still null - use full screen

				Dimension d = getToolkit().getScreenSize();
				splitRect = new Rectangle(0, 0, d.width, d.height);
			}

			client.setBounds(splitRect);
		}

		if(mode == MODE_FRAME) {
			// find client frame bounds
			if(frameRect == null) {
				frameRect = loadRect(frameRect, "Client.Frame");
			}

			if(frameRect == null) { // still null -> optimize
				client.pack();
				frameRect = client.getBounds();

				if(frameRect.x > 0) {
					frameRect.x = 0;
				}

				if(frameRect.y > 0) {
					frameRect.y = 0;
				}
			}

			client.setBounds(frameRect);
		}
	}

	protected void unconnectFrames() {
		if(frames != null) {
			Iterator it = frames.values().iterator();
			JPanel dummy = new JPanel();

			while(it.hasNext()) {
				FrameRectangle fr = (FrameRectangle) it.next();
				JFrame f = (JFrame) fr.getConnectedFrame();

				if(f != null) {
					f.setContentPane(dummy);
					fr.connectToFrame(null);
				}
			}
		}
	}

	protected void disableFramesMenu() {
		if((framesMenu != null) && (framesMenu.getItemCount() > 1)) {
			for(int i = 1; i < framesMenu.getItemCount(); i++) {
				try {
					framesMenu.getItem(i).setEnabled(false);
				} catch(Exception exc) {
				}
			}
		}
	}

	protected void enableFramesMenu() {
		if((framesMenu != null) && (framesMenu.getItemCount() > 1)) {
			for(int i = 1; i < framesMenu.getItemCount(); i++) {
				try {
					framesMenu.getItem(i).setEnabled(true);
				} catch(Exception exc) {
				}
			}
		}
	}

	/**
	 * Initializes the desktop using SplitPanes.
	 *
	 * @param setName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected boolean initSplitSet(String setName) {
		// can't find split-set
		if(!splitSets.containsKey(setName)) {
			return false;
		}

		// disable frames menu
		if(framesMenu != null) {
			disableFramesMenu();
		}

		mode = MODE_SPLIT;
		setClientBounds();

		// clear the frames
		unconnectFrames();

		//get out area, (approximatly)
		Rectangle r = client.getBounds();
		r.x += 3;
		r.y += 20;
		r.width -= 6;
		r.height -= 23;

		if(splitBuilder == null) {
			splitBuilder = new SplitBuilder(r);
		}

		splitBuilder.setScreen(r);

		try {
			splitRoot = splitBuilder.buildDesktop((FrameTreeNode) splitSets.get(setName), components);
		} catch(Exception exc) {
			return false;
		}

		if(splitRoot == null) {
			return false;
		}

		splitName = setName;
		add(splitRoot, BorderLayout.CENTER);
		buildShortCutTable(splitBuilder.getComponentsUsed());

		// activate the corresponding menu item
		for(int i = 0; i < setMenu.getItemCount(); i++) {
			if(setMenu.getItem(i).getText().equals(setName)) {
				((JCheckBoxMenuItem) setMenu.getItem(i)).setSelected(true);
			}
		}

		modeInitialized[MODE_SPLIT] = true;

		return true;
	}

	/**
	 * Initializes the desktop using frames. The settings are parsed for properties of the
	 * following syntax:
	 * 
	 * <p>
	 * Desktop.FrameInt=Int,Int,Int,Int,ID,Title,State
	 * </p>
	 * Where ID is the name of the component, Title the frame title and State the state of the
	 * frame: ICON means ICONIFIED. The parser starts with Frame0 and end when the next property
	 * can't be found. If no such property is found a default frame set will be used emulating the
	 * default desktop.
	 */
	protected void initFrames() {
		if(frames == null) {
			return;
		}

		enableFramesMenu();
		mode = MODE_FRAME;
		setClientBounds();
		loadFrameModeSettings();

		List compsUsed = CollectionFactory.createLinkedList();
		Iterator it = frames.keySet().iterator();

		while(it.hasNext()) {
			Object key = it.next();
			FrameRectangle fr = (FrameRectangle) frames.get(key);
			JFrame frame = new JFrame(fr.getFrameTitle());
			Image appIcon = com.eressea.demo.Client.getApplicationIcon();

			if(appIcon != null) {
				frame.setIconImage(appIcon);
			}

			frame.setState(fr.getState());
			frame.setBounds(fr);
			fr.connectToFrame(frame);

			Component c = (Component) components.get(key);

			if((c instanceof Initializable) && (fr.getConfiguration() != null)) {
				((Initializable) c).initComponent(fr.getConfiguration());
			}

			BackgroundPanel bp = new BackgroundPanel();
			bp.setComponent(c);
			frame.setContentPane(bp);

			/*JPanel help = new JPanel(new BorderLayout());
			help.add(c, BorderLayout.CENTER);
			frame.setContentPane(help);*/
			frame.addWindowListener(this);
			compsUsed.add(components.get(key));
		}

		buildShortCutTable(compsUsed);
		modeInitialized[MODE_FRAME] = true;
	}

	private void checkFrameMenuItem(FrameRectangle f) {
		checkFrameMenuItem(f.getFrameTitle(), f.isVisible());
	}

	private void checkFrameMenuItem(String name, boolean state) {
		if(framesMenu != null) {
			for(int i = 0; i < framesMenu.getItemCount(); i++) {
				try {
					if(framesMenu.getItem(i).getText().equals(name)) {
						((JCheckBoxMenuItem) framesMenu.getItem(i)).setState(state);
					}
				} catch(Exception exc) {
				}
			}
		}
	}

	/**
	 * Load the frame mode settings.
	 */
	protected void loadFrameModeSettings() {
		// load activation and iconification settings
		try {
			activationMode = Integer.parseInt(settings.getProperty("Desktop.ActivationMode"));
		} catch(Exception exc) {
			activationMode = 0;
		}

		try {
			iconify = settings.getProperty("Desktop.IconificationMode").equals("true");
		} catch(Exception exc) {
			iconify = false;
		}
	}

	/**
	 * Activates the layout with the given name.
	 *
	 * @param lName TODO: DOCUMENT ME!
	 */
	protected void initLayout(String lName) {
		if((layoutComponents == null) || !layoutComponents.containsKey(lName)) {
			return;
		}

		List scomps = CollectionFactory.createLinkedList();

		// disable frames menu
		if(framesMenu != null) {
			disableFramesMenu();
		}

		// clear the frames
		unconnectFrames();

		JPanel panel = new JPanel();
		panel.setLayout(lManager);

		Map lMap = (Map) layoutComponents.get(lName);
		Iterator it = lMap.keySet().iterator();

		while(it.hasNext()) {
			String c = (String) it.next();
			DesktopLayoutManager.CPref pref = (DesktopLayoutManager.CPref) lMap.get(c);

			if(components.containsKey(c)) {
				Object o = (Component) components.get(c);
				panel.add((Component) o, pref);
				scomps.add(o);

				if((o instanceof Initializable) && (pref.getConfiguration() != null)) {
					((Initializable) o).initComponent(pref.getConfiguration());
				}
			}
		}

		removeAll();
		add(panel, BorderLayout.CENTER);
		setClientBounds();
		layoutName = lName;

		for(int i = 0; i < layoutMenu.getItemCount(); i++) {
			if(layoutMenu.getItem(i).getText().equals(lName)) {
				((JCheckBoxMenuItem) layoutMenu.getItem(i)).setState(true);
			}
		}

		buildShortCutTable(scomps);
		modeInitialized[MODE_LAYOUT] = true;
	}

	/*
	 ######################
	 # Shortcut - Methods #
	 ######################
	 */

	/**
	 * Based on the components used by the desktop this method builds the KeyStroke-HashMap. The
	 * key is the KeyStroke-Object returned by ShortcutListener.getShortCuts(), the value is the
	 * listener object.
	 *
	 * @param scomps TODO: DOCUMENT ME!
	 */
	protected void buildShortCutTable(Collection scomps) {
		//shortCutComponents=CollectionFactory.createHashMap();
		Iterator it = scomps.iterator();

		while(it.hasNext()) {
			Object o = it.next();

			if(o instanceof ShortcutListener) {
				ShortcutListener sl = (ShortcutListener) o;
				Iterator it2 = sl.getShortCuts();

				while(it2.hasNext()) {
					Object stroke = it2.next();
					shortCutListeners.put(stroke, o);
				}
			}
		}
	}

	/**
	 * This method register all KeyStrokes in the KeyStroke-HashMap using registerListener().
	 */
	protected void registerKeyStrokes() {
		registerListener();
	}

	/**
	 * This registers the key handler at all known containers.
	 */
	protected void registerListener() {
		// register at all frames
		keyHandler.disconnect();

		Collection desk = CollectionFactory.createLinkedList();
		desk.add(client);

		if(mode == MODE_FRAME) {
			Iterator it2 = frames.values().iterator();

			while(it2.hasNext()) {
				Component o = ((FrameRectangle) it2.next()).getConnectedFrame();
				desk.add(o);
			}
		}

		keyHandler.connect(desk);
	}

	protected void loadTranslations() {
		String s = settings.getProperty("Desktop.KeyTranslations");

		if(s != null) {
			StringTokenizer st = new StringTokenizer(s, ",");

			while(st.hasMoreTokens()) {
				try {
					KeyStroke newStroke = KeyStroke.getKeyStroke(Integer.parseInt(st.nextToken()),
																 Integer.parseInt(st.nextToken()));
					KeyStroke oldStroke = KeyStroke.getKeyStroke(Integer.parseInt(st.nextToken()),
																 Integer.parseInt(st.nextToken()));
					registerTranslation(newStroke, oldStroke);
				} catch(RuntimeException exc) {
				}
			}
		}
	}

	protected void saveTranslations() {
		if(shortCutTranslations.size() > 0) {
			StringBuffer buf = new StringBuffer();
			Iterator it = shortCutTranslations.entrySet().iterator();

			while(it.hasNext()) {
				Map.Entry e = (Map.Entry) it.next();
				KeyStroke stroke = (KeyStroke) e.getKey();
				buf.append(stroke.getKeyCode());
				buf.append(',');
				buf.append(stroke.getModifiers());
				buf.append(',');
				stroke = (KeyStroke) e.getValue();
				buf.append(stroke.getKeyCode());
				buf.append(',');
				buf.append(stroke.getModifiers());

				if(it.hasNext()) {
					buf.append(',');
				}
			}

			settings.setProperty("Desktop.KeyTranslations", buf.toString());
		} else {
			settings.remove("Desktop.KeyTranslations");
		}
	}

	/**
	 * Registers a shortcut translation.
	 *
	 * @param newStroke TODO: DOCUMENT ME!
	 * @param oldStroke TODO: DOCUMENT ME!
	 */
	public void registerTranslation(KeyStroke newStroke, KeyStroke oldStroke) {
		if(newStroke.equals(oldStroke)) { // senseless

			return;
		}

		if(!shortCutTranslations.containsKey(oldStroke)) {
			keyHandler.removeStroke(oldStroke);
		}

		keyHandler.addTranslationStroke(newStroke);
		shortCutTranslations.put(newStroke, oldStroke);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param stroke TODO: DOCUMENT ME!
	 */
	public void removeTranslation(KeyStroke stroke) {
		if(shortCutTranslations.containsKey(stroke)) {
			KeyStroke old = (KeyStroke) shortCutTranslations.get(stroke);
			keyHandler.removeStroke(stroke);
			keyHandler.install(old);
			shortCutTranslations.remove(stroke);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param newStroke TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public KeyStroke getTranslation(KeyStroke newStroke) {
		return (KeyStroke) shortCutTranslations.get(newStroke);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param oldStroke TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public KeyStroke findTranslation(KeyStroke oldStroke) {
		Iterator it = shortCutTranslations.keySet().iterator();

		while(it.hasNext()) {
			Object obj = it.next();

			if(shortCutTranslations.get(obj).equals(oldStroke)) {
				return (KeyStroke) obj;
			}
		}

		return null;
	}

	/**
	 * Registers a new KeyStroke/ShortcutListener pair at the desktop.
	 *
	 * @param stroke TODO: DOCUMENT ME!
	 * @param sl TODO: DOCUMENT ME!
	 */
	public void registerShortcut(KeyStroke stroke, ShortcutListener sl) {
		keyHandler.addStroke(stroke, sl);
	}

	/**
	 * Register a ShortcutListener with all its shortcuts at the desktop.
	 *
	 * @param sl TODO: DOCUMENT ME!
	 */
	public void registerShortcut(ShortcutListener sl) {
		keyHandler.addListener(sl);
	}

	/**
	 * Registers a new KeyStroke/ActionListener pair at the desktop. If the given boolean is FALSE,
	 * the listener will not be registered at the Client frame. This feature is for menu items.
	 *
	 * @param stroke TODO: DOCUMENT ME!
	 * @param al TODO: DOCUMENT ME!
	 */
	public void registerShortcut(KeyStroke stroke, ActionListener al) {
		keyHandler.addStroke(stroke, al, true);
	}

	/*
	 ################################
	 # Frame management methods     #
	 # ONLY USED WHEN IN FRAME MODE #
	 ################################
	 */

	/**
	 * Just empty - do nothing if a frame is inactivated.
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void windowDeactivated(java.awt.event.WindowEvent p1) {
		// clear "input buffer"
		keyHandler.resetExtendedShortcutListener();
	}

	/**
	 * Just empty - do nothing if a frame is closed.
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void windowClosed(java.awt.event.WindowEvent p1) {
	}

	/**
	 * Check if this event comes from the client window and if all windows should be deiconified -
	 * if so, deiconify all and put them to front.
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void windowDeiconified(java.awt.event.WindowEvent p1) {
		// clear "input buffer"
		keyHandler.resetExtendedShortcutListener();

		if((mode == MODE_FRAME) && (p1.getSource() == client) && iconify) {
			inFront = true;
			timer.start();

			Iterator it = frames.values().iterator();

			while(it.hasNext()) {
				FrameRectangle fr = (FrameRectangle) it.next();
				Frame o = fr.getConnectedFrame();

				if(o != p1.getSource()) {
					o.setVisible(true);

					if(o.getState() == Frame.ICONIFIED) {
						o.setState(Frame.NORMAL);
					}
				}
			}

			client.toFront();
		}
	}

	/**
	 * Just empty - do nothing if a frame is opened.
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void windowOpened(java.awt.event.WindowEvent p1) {
		// clear "input buffer"
		keyHandler.resetExtendedShortcutListener();
	}

	/**
	 * Check if this  event comes from the client window and if all windows should be iconified -
	 * if so, iconify them.
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void windowIconified(java.awt.event.WindowEvent p1) {
		// clear "input buffer"
		keyHandler.resetExtendedShortcutListener();

		if((mode == MODE_FRAME) && (p1.getSource() == client) && iconify) {
			Iterator it = frames.values().iterator();
			inFront = true;
			timer.start();

			while(it.hasNext()) {
				Frame o = (Frame) (((FrameRectangle) it.next()).getConnectedFrame());

				if(o != p1.getSource()) {
					o.setVisible(false);
				}
			}
		}
	}

	/**
	 * Update the frames menu.
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void windowClosing(java.awt.event.WindowEvent p1) {
		// clear "input buffer"
		keyHandler.resetExtendedShortcutListener();

		if(p1.getSource() != client) {
			checkFrameMenuItem(((Frame) p1.getSource()).getTitle(), false);
		}
	}

	/**
	 * Check the activation modus:
	 * 
	 * <ul>
	 * <li>
	 * DO NOTHING ON ACTIVATION
	 * </li>
	 * <li>
	 * ACTIVATE ALL WHEN CLIENT IS ACTIVATED
	 * </li>
	 * <li>
	 * ACTIVATE ALL IF ANY WINDOW IS ACTIVATED
	 * </li>
	 * </ul>
	 * 
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void windowActivated(java.awt.event.WindowEvent p1) {
		// clear "input buffer"
		keyHandler.resetExtendedShortcutListener();

		if(inFront) {
			timer.restart();

			return;
		}

		if(mode == MODE_FRAME) {
			switch(activationMode) {
			case ACTIVATION_MODE_NONE:
				return;

			case ACTIVATION_MODE_CLIENT_ONLY:

				if(p1.getSource() == client) {
					SwingUtilities.invokeLater(new WindowActivator(client));
				}

				break;

			case ACTIVATION_MODE_ANY:
				SwingUtilities.invokeLater(new WindowActivator((Window) p1.getSource()));

				break;

			default:
				break;
			}
		}
	}

	/**
	 * Activates all frames if in frame mode.
	 *
	 * @param visible TODO: DOCUMENT ME!
	 */
	public void setAllVisible(boolean visible) {
		if(mode == MODE_FRAME) {
			inFront = true;
			timer.start();

			Iterator it = frames.values().iterator();

			while(it.hasNext()) {
				FrameRectangle fr = (FrameRectangle) it.next();
				Frame f = fr.getConnectedFrame();
				f.setVisible(fr.isVisible() && visible);
				checkFrameMenuItem(fr);
			}
		}

		client.setVisible(visible);
	}

	/*
	 #########################
	 # Cross-Mode Management #
	 # Focus and Repaint     #
	 #########################
	 */

	/**
	 * The component with the ID id will gain the focus. If Frame Mode is enabled, the parent frame
	 * will be activated.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public void componentRequestFocus(String id) {
		if(!components.containsKey(id)) {
			return;
		}

		if(mode == MODE_FRAME) {
			if(!frames.containsKey(id)) {
				return;
			}

			inFront = true;

			Frame f = (Frame) ((FrameRectangle) frames.get(id)).getConnectedFrame();

			if(f.getState() == Frame.ICONIFIED) {
				f.setState(Frame.NORMAL);
			}

			inFront = true; // maybe cleared by event
			timer.start();
			f.toFront();
		}

		// search in component table to activate directly
		((Component) components.get(id)).requestFocus();
	}

	/**
	 * Repaints the component with ID id.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public void repaint(String id) {
		if(!components.containsKey(id)) {
			return;
		}

		if(mode == MODE_FRAME) {
			((FrameRectangle) frames.get(id)).getConnectedFrame().repaint();
		} else {
			((Component) components.get(id)).repaint();
		}
	}

	/**
	 * Repaints all components.
	 */
	public void repaintAllComponents() {
		if(mode == MODE_FRAME) {
			inFront = true;
			timer.start();

			Iterator it = frames.values().iterator();

			while(it.hasNext()) {
				Frame f = (((FrameRectangle) it.next()).getConnectedFrame());

				if(!f.isVisible()) {
					continue;
				}

				f.repaint(500);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void updateLaF() {
		SwingUtilities.updateComponentTreeUI(client);

		if(mode == MODE_FRAME) {
			Iterator it = frames.values().iterator();

			while(it.hasNext()) {
				SwingUtilities.updateComponentTreeUI((((FrameRectangle) it.next()).getConnectedFrame()));
			}
		}

		// to avoid start bug
		if(desktopMenu != null) {
			SwingUtilities.updateComponentTreeUI(desktopMenu);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent p1) {
		if(p1.getSource() == timer) {
			inFront = false;
			timer.stop();
		} else if(p1.getSource() instanceof JCheckBoxMenuItem) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) p1.getSource();
			JPopupMenu menu = (JPopupMenu) item.getParent();

			if(menu.getInvoker() == setMenu) { //init the split set
				setMode(MODE_SPLIT, p1.getActionCommand());
			} else if(menu.getInvoker() == layoutMenu) {
				setMode(MODE_LAYOUT, p1.getActionCommand());
			} else if(menu.getInvoker() == framesMenu) {
				Iterator it = frames.values().iterator();
				Frame f = null;

				while((f == null) && it.hasNext()) {
					Frame f2 = ((FrameRectangle) it.next()).getConnectedFrame();

					if(f2.getTitle().equals(item.getText())) {
						f = f2;
					}
				}

				if(f != null) {
					f.setVisible(item.isSelected());
				}
			}
		} else if(p1.getSource() instanceof JMenuItem) { // activate frame mode

			if(getMode() != MODE_FRAME) {
				setMode(MODE_FRAME);
			}
		}
	}

	///////////////////////////////////////
	//                                   //
	//  S H O R T C U T - H A N D L E R  //
	//                                   //
	///////////////////////////////////////

	/**
	 * A handler class for key events. It checks if the given combination is stored and calls the
	 * shortcutlistener. This class is also responsible to handle extended shortcut listeners This
	 * implementation only looks for pressed keys because of some mysterious behaviour on CTRL+Key
	 * events.
	 */
	protected class KeyHandler {
		protected class KeyboardActionListener implements ActionListener {
			protected KeyStroke stroke;

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param actionEvent TODO: DOCUMENT ME!
			 */
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				keyPressed(stroke, actionEvent.getSource());
			}
		}

		protected ShortcutListener lastListener = null;
		protected GameEventListener helpListener;
		protected Collection extendedListeners;
		protected Collection lastComponents;

		/**
		 * Creates a new KeyHandler object.
		 */
		public KeyHandler() {
			helpListener = new GameEventListener(this);
			extendedListeners = CollectionFactory.createLinkedList();
			lastComponents = CollectionFactory.createLinkedList();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param deskElements TODO: DOCUMENT ME!
		 */
		public void connect(Collection deskElements) {
			if(lastComponents.size() > 0) {
				disconnect();
			}

			lastComponents.addAll(deskElements);

			Set set = CollectionFactory.createHashSet(shortCutTranslations.keySet());
			Set set2 = CollectionFactory.createHashSet(shortCutListeners.keySet());
			Iterator it = set.iterator();

			while(it.hasNext()) {
				set2.remove(shortCutTranslations.get(it.next()));
			}

			set.addAll(set2);

			it = set.iterator();

			while(it.hasNext()) {
				KeyStroke stroke = (KeyStroke) it.next();
				install(stroke);
			}

			extendedListeners.clear();
			lastListener = null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void disconnect() {
			if(lastComponents.size() > 0) {
				Iterator it = shortCutListeners.keySet().iterator();

				while(it.hasNext()) {
					KeyStroke str = (KeyStroke) it.next();
					remove(str);
				}

				it = extendedListeners.iterator();

				while(it.hasNext()) {
					KeyStroke str = (KeyStroke) it.next();
					remove(str);
				}

				it = shortCutTranslations.keySet().iterator();

				while(it.hasNext()) {
					KeyStroke str = (KeyStroke) it.next();
					remove(str);
				}
			}

			extendedListeners.clear();
			lastListener = null;
			lastComponents.clear();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param stroke TODO: DOCUMENT ME!
		 */
		public void removeStroke(KeyStroke stroke) {
			remove(stroke);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param str TODO: DOCUMENT ME!
		 */
		public void addTranslationStroke(KeyStroke str) {
			install(str);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param str TODO: DOCUMENT ME!
		 * @param dest TODO: DOCUMENT ME!
		 */
		public void addStroke(KeyStroke str, Object dest) {
			addStroke(str, dest, false);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param str TODO: DOCUMENT ME!
		 * @param dest TODO: DOCUMENT ME!
		 * @param actionAware TODO: DOCUMENT ME!
		 */
		public void addStroke(KeyStroke str, Object dest, boolean actionAware) {
			if((findTranslation(str) == null) && !shortCutListeners.containsKey(str)) {
				install(str);
			} else if(actionAware) {
				KeyStroke newStroke = findTranslation(str);

				if((newStroke != null) && (dest instanceof Action)) {
					((Action) dest).putValue("accelerator", newStroke);
				}
			}

			shortCutListeners.put(str, dest);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param sl TODO: DOCUMENT ME!
		 */
		public void addListener(ShortcutListener sl) {
			Iterator it = sl.getShortCuts();

			while(it.hasNext()) {
				addStroke((KeyStroke) it.next(), sl, false);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 * @param src TODO: DOCUMENT ME!
		 */
		public void keyPressed(KeyStroke e, Object src) {
			// "translate" the key
			if(shortCutTranslations.containsKey(e)) {
				e = (KeyStroke) shortCutTranslations.get(e);
			}

			// redirect only for sub-listener
			if(lastListener != null) {
				remove(extendedListeners, false);

				if(extendedListeners.contains(e)) {
					reactOnStroke(e, lastListener);

					return;
				}

				lastListener = null;
			}

			if(shortCutListeners.containsKey(e)) {
				Object o = shortCutListeners.get(e);

				if(o instanceof ShortcutListener) {
					reactOnStroke(e, (ShortcutListener) o);
				} else if(o instanceof ActionListener) {
					/*if (src instanceof JComponent)
					    if (((JComponent)src).getTopLevelAncestor()==client)
					        return;*/
					((ActionListener) o).actionPerformed(null);
				}

				return;
			}

			log.info("Error: Unrecognized key stroke " + e);
		}

		protected void install(ShortcutListener sl, boolean flag) {
			Iterator it = sl.getShortCuts();

			while(it.hasNext()) {
				KeyStroke stroke = (KeyStroke) it.next();

				if(flag) {
					extendedListeners.add(stroke);
				}

				if(!shortCutListeners.containsKey(stroke)) {
					install(stroke);
				}
			}
		}

		protected void install(KeyStroke stroke) {
			Iterator it = lastComponents.iterator();
			KeyboardActionListener kal = new KeyboardActionListener();
			kal.stroke = stroke;

			while(it.hasNext()) {
				Component c = (Component) it.next();

				if(c instanceof JComponent) {
					((JComponent) c).registerKeyboardAction(kal, stroke,
															JComponent.WHEN_IN_FOCUSED_WINDOW);
				} else if(c instanceof Component) {
					addToContainer((Component) c, stroke, kal);
				}
			}
		}

		/**
		 * Removes the given key stroke from the container searching an instance of JComponent.
		 *
		 * @param c TODO: DOCUMENT ME!
		 * @param s TODO: DOCUMENT ME!
		 * @param al TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		protected boolean addToContainer(Component c, KeyStroke s, ActionListener al) {
			if(c instanceof JComponent) {
				((JComponent) c).registerKeyboardAction(al, s, JComponent.WHEN_IN_FOCUSED_WINDOW);

				return true;
			}

			if(c instanceof Container) {
				Container con = (Container) c;

				if(con.getComponentCount() > 0) {
					Component comp[] = con.getComponents();

					for(int i = 0; i < comp.length; i++) {
						if(addToContainer(comp[i], s, al)) {
							return true;
						}
					}
				}
			}

			return false;
		}

		/**
		 * Removes the KeyStrokes in the collection from all desktop components. If the given flag
		 * is true, all key-strokes in the collection are removed, else only non-base-level
		 * key-strokes.
		 *
		 * @param col TODO: DOCUMENT ME!
		 * @param flag TODO: DOCUMENT ME!
		 */
		protected void remove(Collection col, boolean flag) {
			Iterator it = col.iterator();

			while(it.hasNext()) {
				KeyStroke stroke = (KeyStroke) it.next();

				if(flag ||
					   (!shortCutTranslations.containsKey(stroke) &&
					   !shortCutListeners.containsKey(stroke))) {
					remove(stroke);
				}
			}
		}

		protected void remove(KeyStroke stroke) {
			Iterator it2 = lastComponents.iterator();

			while(it2.hasNext()) {
				Component c = (Component) it2.next();

				if(c instanceof JComponent) {
					((JComponent) c).unregisterKeyboardAction(stroke);
				} else if(c instanceof Component) {
					removeFromContainer((Component) c, stroke);
				}
			}
		}

		/**
		 * Removes the given key stroke from the container searching an instance of JComponent.
		 *
		 * @param c TODO: DOCUMENT ME!
		 * @param s TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		protected boolean removeFromContainer(Component c, KeyStroke s) {
			if(c instanceof JComponent) {
				((JComponent) c).unregisterKeyboardAction(s);

				return true;
			}

			if(c instanceof Container) {
				Container con = (Container) c;

				if(con.getComponentCount() > 0) {
					Component comp[] = con.getComponents();

					for(int i = 0; i < comp.length; i++) {
						if(removeFromContainer(comp[i], s)) {
							return true;
						}
					}
				}
			}

			return false;
		}

		/**
		 * Resets the sub-listener.
		 */
		public void resetExtendedShortcutListener() {
			remove(extendedListeners, false);
			lastListener = null;
		}

		/**
		 * Performs the action for the given shortcut listener.
		 *
		 * @param stroke TODO: DOCUMENT ME!
		 * @param sl TODO: DOCUMENT ME!
		 */
		protected void reactOnStroke(KeyStroke stroke, ShortcutListener sl) {
			if(sl instanceof ExtendedShortcutListener) {
				ExtendedShortcutListener esl = (ExtendedShortcutListener) sl;

				if(esl.isExtendedShortcut(stroke)) {
					lastListener = esl.getExtendedShortcutListener(stroke);
					install(lastListener, true);

					return;
				}
			}

			lastListener = null;
			sl.shortCut(stroke);
		}

		/**
		 * The sub-shortcutlistener must be cleared when any game event appears
		 */
		protected class GameEventListener implements com.eressea.event.GameDataListener,
													 com.eressea.event.SelectionListener
		{
			KeyHandler parent;

			/**
			 * Creates a new GameEventListener object.
			 *
			 * @param parent TODO: DOCUMENT ME!
			 */
			public GameEventListener(KeyHandler parent) {
				this.parent = parent;

				com.eressea.event.EventDispatcher e = com.eressea.event.EventDispatcher.getDispatcher();
				e.addGameDataListener(this);
				e.addSelectionListener(this);
			}

			/**
			 * Invoked when different objects are activated or selected.
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void selectionChanged(com.eressea.event.SelectionEvent e) {
				parent.resetExtendedShortcutListener();
			}

			/**
			 * Invoked when the current game data object becomes invalid.
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void gameDataChanged(com.eressea.event.GameDataEvent e) {
				parent.resetExtendedShortcutListener();
			}
		}
	}

	///////////////////////////////////
	// CONFIGURATION CODE - INTERNAL //
	///////////////////////////////////

	/**
	 * Returns the current desktop mode.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Sets the desktop mode. If the mode changes, the desktop is newly initialized. Possible
	 * values are:
	 * 
	 * <ul>
	 * <li>
	 * MODE_SPLIT: Build the desktop with SplitPanes.
	 * </li>
	 * <li>
	 * MODE_FRAME: Build the desktop with Frames.
	 * </li>
	 * <li>
	 * MODE_LAYOUT: Build the desktop with the special Layout.
	 * </li>
	 * </ul>
	 * 
	 *
	 * @param mode TODO: DOCUMENT ME!
	 */
	public void setMode(int mode) {
		switch(mode) {
		case MODE_SPLIT:

			if(splitName != null) {
				setMode(MODE_SPLIT, splitName);
			} else {
				setMode(MODE_SPLIT, settings.getProperty("Desktop.SplitSet", "Standard"));
			}

			break;

		case MODE_FRAME:
			setMode(MODE_FRAME, null);

			break;

		case MODE_LAYOUT:

			if(layoutName != null) {
				setMode(MODE_LAYOUT, layoutName);
			} else {
				setMode(MODE_LAYOUT, settings.getProperty("Desktop.Layout", "Standard"));
			}

			break;
		}
	}

	/**
	 * Sets the desktop mode. If the mode changes, the desktop is newly initialized. Possible
	 * values are:
	 * 
	 * <ul>
	 * <li>
	 * MODE_SPLIT: Build the desktop with SplitPanes.
	 * </li>
	 * <li>
	 * MODE_FRAME: Build the desktop with Frames.
	 * </li>
	 * <li>
	 * MODE_LAYOUT: Build the desktop with the special Layout.
	 * </li>
	 * </ul>
	 * 
	 * The given parameter describes the mode.
	 *
	 * @param mode TODO: DOCUMENT ME!
	 * @param param TODO: DOCUMENT ME!
	 */
	public void setMode(int mode, Object param) {
		if((mode != this.mode) || ((mode == MODE_SPLIT) && !param.equals(splitName)) ||
			   ((mode == MODE_LAYOUT) && !param.equals(layoutName))) {
			switch(this.mode) {
			case MODE_SPLIT:
			case MODE_LAYOUT:
				splitRect = client.getBounds(splitRect);

				break;

			case MODE_FRAME:
				frameRect = client.getBounds(frameRect);

				break;
			}

			setAllVisible(false);
			inFront = false;
			retrieveConfiguration();
			this.mode = mode;
			removeAll();

			if(mode == MODE_SPLIT) {
				if((param != null) || !(param instanceof String)) {
					if(!initSplitSet((String) param)) {
						initSplitSet("Standard");
					}
				} else if(!initSplitSet(settings.getProperty("Desktop.SplitSet", "Standard"))) {
					initSplitSet("Standard");
				}
			}

			if(mode == MODE_LAYOUT) {
				if((param == null) || !(param instanceof String)) {
					initLayout(settings.getProperty("Desktop.Layout", "Standard"));
				} else {
					initLayout((String) param);
				}
			}

			if(mode == MODE_FRAME) {
				initFrames();
			}

			setAllVisible(true);
			registerKeyStrokes(); // register listeners to the new frames
		}

		if(mode == MODE_SPLIT) {
			splitRoot.repaint();
		}

		client.repaint(500);
		repaintAllComponents();
	}

	protected void setSplitSet(String name) {
		if(!splitSets.containsKey(name)) {
			return;
		}

		setAllVisible(false);
		inFront = false;
		retrieveConfiguration();
		mode = MODE_SPLIT;
		removeAll();
		initSplitSet(name);
		setAllVisible(true);
		registerKeyStrokes(); // register listeners to the new frames
		client.repaint(500);
		splitRoot.repaint();
	}

	protected void setActiveLayout(String name) {
		if(!layoutComponents.containsKey(name)) {
			return;
		}

		setAllVisible(false);
		inFront = false;
		retrieveConfiguration();
		mode = MODE_LAYOUT;
		initLayout(name);
		setAllVisible(true);
		registerKeyStrokes(); // register listeners to the new frames
		client.repaint(500);
	}

	/**
	 * Retrieves possible init configurations out of the current desktop.
	 */
	protected void retrieveConfiguration() {
		switch(mode) {
		case MODE_FRAME:
			retrieveFromFrame();

			break;

		case MODE_LAYOUT:
			retrieveFromLayout();

			break;

		case MODE_SPLIT:
			retrieveFromSplit();

			break;
		}
	}

	private void retrieveFromFrame() {
		if(frames != null) {
			Iterator it = frames.values().iterator();

			while(it.hasNext()) {
				FrameRectangle fr = (FrameRectangle) it.next();

				if((components.get(fr.getFrameComponent()) != null) &&
					   (components.get(fr.getFrameComponent()) instanceof Initializable)) {
					fr.setConfiguration(((Initializable) components.get(fr.getFrameComponent())).getComponentConfiguration());
				}
			}
		}
	}

	private void retrieveFromSplit() {
		if((splitSets != null) && splitSets.containsKey(splitName)) {
			FrameTreeNode ftr = (FrameTreeNode) splitSets.get(splitName);
			retrieveFromSplitImpl(ftr);
		}
	}

	private void retrieveFromSplitImpl(FrameTreeNode ftr) {
		if(ftr == null) {
			return;
		}

		if(ftr.isLeaf()) {
			if(components.containsKey(ftr.getName()) &&
				   (components.get(ftr.getName()) instanceof Initializable)) {
				ftr.setConfiguration(((Initializable) components.get(ftr.getName())).getComponentConfiguration());
			}
		} else {
			retrieveFromSplitImpl(ftr.getChild(0));
			retrieveFromSplitImpl(ftr.getChild(1));
		}
	}

	private void retrieveFromLayout() {
		if((layoutComponents != null) && layoutComponents.containsKey(layoutName)) {
			Map map = (Map) layoutComponents.get(layoutName);
			Iterator it = map.keySet().iterator();

			while(it.hasNext()) {
				String id = (String) it.next();

				if(components.get(id) instanceof Initializable) {
					((DesktopLayoutManager.CPref) map.get(id)).setConfiguration(((Initializable) components.get(id)).getComponentConfiguration());
				}
			}
		}
	}

	///////////////////////////////////
	// CONFIGURATION CODE - EXTERNAL //
	///////////////////////////////////

	/**
	 * Writes the configuration of this desktop.
	 */
	public void save() {
		retrieveConfiguration();

		if(modeInitialized[MODE_SPLIT]) {
			saveSplitModeProperties();
		}

		if(modeInitialized[MODE_FRAME]) {
			saveFrameModeProperties();
		}

		if(modeInitialized[MODE_LAYOUT]) {
			saveLayoutModeProperties();
		}

		if(modeInitialized[MODE_SPLIT] || modeInitialized[MODE_LAYOUT]) {
			try {
				saveDesktopFile();
			} catch(Exception exc) {
			}
		}

		saveTranslations();
	}

	/**
	 * Saves properties of Split Mode. Following properties may be set:
	 * 
	 * <ul>
	 * <li>
	 * Client.x,Client.y,Client.width,Client.height
	 * </li>
	 * <li>
	 * Desktop.Type
	 * </li>
	 * <li>
	 * Desktop.SplitSet
	 * </li>
	 * </ul>
	 */
	protected void saveSplitModeProperties() {
		if(getMode() == MODE_SPLIT) {
			settings.setProperty("Desktop.Type", "SPLIT");
			splitRect = client.getBounds(splitRect);
		}

		saveRectangle(splitRect, "Client");
		settings.setProperty("Desktop.SplitSet", splitName);
	}

	/**
	 * Saves properties of Frame Mode. Following properties may be set:
	 * 
	 * <ul>
	 * <li>
	 * Client.Frame.x,Client.Frame.y,Client.Frame.width,Client.Frame.height
	 * </li>
	 * <li>
	 * Desktop.Type
	 * </li>
	 * <li>
	 * Desktop.FrameX
	 * </li>
	 * </ul>
	 */
	protected void saveFrameModeProperties() {
		if(getMode() == MODE_FRAME) {
			settings.setProperty("Desktop.Type", "FRAMES");
			frameRect = client.getBounds(frameRect);
		}

		saveRectangle(frameRect, "Client.Frame");
		saveFrames(frames, settings);
	}

	/**
	 * Saves properties of Split Mode. Following properties may be set:
	 * 
	 * <ul>
	 * <li>
	 * Client.x,Client.y,Client.width,Client.height
	 * </li>
	 * <li>
	 * Desktop.Type
	 * </li>
	 * <li>
	 * Desktop.Layout
	 * </li>
	 * </ul>
	 */
	protected void saveLayoutModeProperties() {
		if(getMode() == MODE_LAYOUT) {
			settings.setProperty("Desktop.Type", "LAYOUT");
			splitRect = client.getBounds(splitRect);
		}

		settings.setProperty("Desktop.Layout", layoutName);
		saveRectangle(splitRect, "Client");
	}

	/**
	 * Saves the content of splitSets and layoutComponents to the desktop file.
	 *
	 * @throws Exception TODO: DOCUMENT ME!
	 */
	protected void saveDesktopFile() throws Exception {
		File magFile = getDesktopFile(true);

		if(magFile.exists()) {
			magFile.delete();
		}

		PrintWriter out = new PrintWriter(new FileWriter(magFile));
		Iterator it = splitSets.keySet().iterator();

		while(it.hasNext()) {
			try {
				String name = (String) it.next();
				saveList(name, (FrameTreeNode) splitSets.get(name), out);
			} catch(Exception exc) {
			}
		}

		it = layoutComponents.keySet().iterator();

		while(it.hasNext()) {
			try {
				String name = (String) it.next();
				saveList("Layout_" + name, (Map) layoutComponents.get(name), out);
			} catch(Exception exc) {
			}
		}

		out.close();
	}

	/**
	 * Writes the given map with the given block name to out.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param map TODO: DOCUMENT ME!
	 * @param out TODO: DOCUMENT ME!
	 */
	private void saveList(String name, Map map, PrintWriter out) {
		out.println('[' + name + ']');

		Iterator it = map.keySet().iterator();

		while(it.hasNext()) {
			Object o = it.next();
			out.println(o.toString() + '=' + map.get(o).toString());
		}
	}

	/**
	 * Writes the given FrameTreeNode structure with block name to out.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param root TODO: DOCUMENT ME!
	 * @param out TODO: DOCUMENT ME!
	 */
	private void saveList(String name, FrameTreeNode root, PrintWriter out) {
		out.println('[' + name + ']');
		root.write(out);
	}

	/**
	 * Loads a rectangle from the settings using the given key.
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private Rectangle loadRect(Rectangle r, String key) {
		if(r == null) {
			r = new Rectangle();
		}

		try {
			r.x = Integer.parseInt(settings.getProperty(key + ".x"));
			r.y = Integer.parseInt(settings.getProperty(key + ".y"));
			r.width = Integer.parseInt(settings.getProperty(key + ".width"));
			r.height = Integer.parseInt(settings.getProperty(key + ".height"));
		} catch(Exception exc) {
			return null;
		}

		return r;
	}

	/**
	 * Saves the rectangle r with property-key key to the settings.
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param key TODO: DOCUMENT ME!
	 */
	private void saveRectangle(Rectangle r, String key) {
		settings.setProperty(key + ".x", String.valueOf(r.x));
		settings.setProperty(key + ".y", String.valueOf(r.y));
		settings.setProperty(key + ".width", String.valueOf(r.width));
		settings.setProperty(key + ".height", String.valueOf(r.height));
	}

	/**
	 * Saves the FrameRectangle structure to the given properties object.
	 *
	 * @param fr TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	protected void saveFrames(Map fr, Properties p) {
		// save activation and iconification setting
		p.setProperty("Desktop.ActivationMode", String.valueOf(activationMode));
		p.setProperty("Desktop.IconificationMode", String.valueOf(iconify));

		Iterator it = fr.keySet().iterator();
		int i = 0;

		while(it.hasNext()) {
			String id = (String) it.next();
			FrameRectangle f = (FrameRectangle) fr.get(id);
			String icon = null;
			String configuration = f.getConfiguration();

			if(f.getState() == Frame.ICONIFIED) {
				icon = "ICON";
			} else {
				icon = "NORMAL";
			}

			String x = String.valueOf((int) f.getX());
			String y = String.valueOf((int) f.getY());
			String w = String.valueOf((int) f.getWidth());
			String h = String.valueOf((int) f.getHeight());
			String v = null;

			if(f.isVisible()) {
				v = "VISIBLE";
			} else {
				v = "INVISIBLE";
			}

			String property = x + ',' + y + ',' + w + ',' + h + ',' + id + ',' + f.getFrameTitle() +
							  ',' + icon + ',' + v;

			if(configuration != null) {
				property += (',' + configuration);
			}

			p.setProperty("Desktop.Frame" + String.valueOf(i), property);
			i++;
		}
	}

	////////////////////////////////
	// LAYOUT-MODE Layout Manager //
	////////////////////////////////

	/**
	 * Simple layout manager for layout mode. It uses the inner class CPref to manage the
	 * components.
	 */
	protected class DesktopLayoutManager implements LayoutManager2 {
		private Map componentPrefs;
		private Dimension minDim;
		private Dimension prefDim;
		private Dimension cSize;

		/**
		 * Creates new DesktopLayoutManager
		 */
		public DesktopLayoutManager() {
			componentPrefs = CollectionFactory.createHashMap();
			minDim = new Dimension(100, 100);

			Toolkit t = Toolkit.getDefaultToolkit();
			prefDim = t.getScreenSize();

			//for frame
			prefDim.width -= 10;
			prefDim.height -= 30;
			cSize = new Dimension();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param str TODO: DOCUMENT ME!
		 * @param component TODO: DOCUMENT ME!
		 */
		public void addLayoutComponent(java.lang.String str, java.awt.Component component) {
			CPref pref = parseCPref(str);

			if(pref != null) {
				addLayoutComponent(component, pref);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 */
		public void layoutContainer(java.awt.Container container) {
			Iterator it = componentPrefs.keySet().iterator();
			cSize = container.getSize(cSize);

			while(it.hasNext()) {
				Component c = (Component) it.next();
				CPref cP = (CPref) componentPrefs.get(c);
				c.setBounds((int) (cSize.width * cP.x), (int) (cSize.height * cP.y),
							(int) (cSize.width * cP.w), (int) (cSize.height * cP.h));
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public java.awt.Dimension minimumLayoutSize(java.awt.Container container) {
			return minDim;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public java.awt.Dimension preferredLayoutSize(java.awt.Container container) {
			return prefDim;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param component TODO: DOCUMENT ME!
		 */
		public void removeLayoutComponent(java.awt.Component component) {
			componentPrefs.remove(component);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param component TODO: DOCUMENT ME!
		 * @param obj TODO: DOCUMENT ME!
		 */
		public void addLayoutComponent(java.awt.Component component, java.lang.Object obj) {
			if(obj instanceof String) {
				addLayoutComponent((String) obj, component);
			}

			if(obj instanceof CPref) {
				componentPrefs.put(component, obj);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public float getLayoutAlignmentX(java.awt.Container container) {
			return 0;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public float getLayoutAlignmentY(java.awt.Container container) {
			return 0;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 */
		public void invalidateLayout(java.awt.Container container) {
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param container TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public java.awt.Dimension maximumLayoutSize(java.awt.Container container) {
			return prefDim;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param s TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public CPref parseCPref(String s) {
			try {
				StringTokenizer st = new StringTokenizer(s, ";");
				String sx = st.nextToken();
				String sy = st.nextToken();
				String sw = st.nextToken();
				String sh = st.nextToken();
				double x = Double.parseDouble(sx);
				double y = Double.parseDouble(sy);
				double w = Double.parseDouble(sw);
				double h = Double.parseDouble(sh);
				CPref pref = new CPref(x, y, w, h);

				if(st.hasMoreTokens()) {
					pref.setConfiguration(st.nextToken());
				}

				return pref;
			} catch(Exception exc) {
			}

			return null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param x TODO: DOCUMENT ME!
		 * @param y TODO: DOCUMENT ME!
		 * @param w TODO: DOCUMENT ME!
		 * @param h TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public CPref createCPref(double x, double y, double w, double h) {
			return new CPref(x, y, w, h);
		}

		/**
		 * Class for storing wished component bounds. Values are between 0 and 1 and are treated as
		 * percent numbers.
		 */
		public class CPref {
			protected double x;
			protected double y;
			protected double w;
			protected double h;
			protected String configuration;

			/**
			 * Creates a new CPref object.
			 */
			public CPref() {
				x = y = w = h = 0;
			}

			/**
			 * Creates a new CPref object.
			 *
			 * @param d1 TODO: DOCUMENT ME!
			 * @param d2 TODO: DOCUMENT ME!
			 * @param d3 TODO: DOCUMENT ME!
			 * @param d4 TODO: DOCUMENT ME!
			 */
			public CPref(double d1, double d2, double d3, double d4) {
				x = d1;
				y = d2;
				w = d3;
				h = d4;
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public String toString() {
				if(configuration == null) {
					return String.valueOf(x) + ';' + String.valueOf(y) + ';' + String.valueOf(w) +
						   ';' + String.valueOf(h);
				}

				return String.valueOf(x) + ';' + String.valueOf(y) + ';' + String.valueOf(w) + ';' +
					   String.valueOf(h) + ';' + configuration;
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @return TODO: DOCUMENT ME!
			 */
			public String getConfiguration() {
				return configuration;
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param config TODO: DOCUMENT ME!
			 */
			public void setConfiguration(String config) {
				configuration = config;
			}
		}
	}

	///////////////////////
	// WINDOW ACTIVATION //
	///////////////////////

	/**
	 * Runnable used to activate all displayed windows. This will move them to the front of the
	 * desktop.
	 */
	private class WindowActivator implements Runnable {
		protected Window source;

		/**
		 * Creates a new WindowActivator object.
		 *
		 * @param s TODO: DOCUMENT ME!
		 */
		public WindowActivator(Window s) {
			source = s;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void run() {
			if((mode != MODE_FRAME) || (frames == null)) {
				return;
			}

			inFront = true;
			timer.start();

			Iterator it = frames.values().iterator();

			while(it.hasNext()) {
				FrameRectangle fr = (FrameRectangle) it.next();
				Frame o = fr.getConnectedFrame();

				// don't activate iconified frames
				if((o.getState() == Frame.ICONIFIED) || !o.isVisible()) {
					continue;
				}

				if(o != source) { // don't move source to front - this would collapse the multiple-event-protection system
					o.toFront();
				}
			}

			if(source != client) {
				client.toFront();
			}

			source.toFront();
		}
	}

	/////////////////////////
	// PREFERENCES ADAPTER //
	/////////////////////////
	public PreferencesAdapter createPreferencesAdapter() {
		return new DesktopPreferences();
	}

	/**
	 * Encapsulates the preferences tab for the desktop.
	 */
	private class DesktopPreferences extends JPanel implements ActionListener,
															   ExtendedPreferencesAdapter
	{
		JComboBox modeBox;
		JTextArea actLabel;
		JTextArea icoLabel;
		JComboBox icoBox;
		JComboBox actMode;
		CardLayout card;
		JPanel center;
		List scList;
		private final String act[] = {
										 getString("prefs.activationdescription.single"),
										 getString("prefs.activationdescription.main"),
										 getString("prefs.activationdescription.all")
									 };
		private final String ico[] = {
										 getString("prefs.iconifydescription.single"),
										 getString("prefs.iconifydescription.main")
									 };

		/**
		 * Creates a new DesktopPreferences object.
		 */
		public DesktopPreferences() {
			this.setLayout(new BorderLayout());

			JPanel up = new JPanel(new FlowLayout(FlowLayout.LEADING));
			up.add(new JLabel(getString("prefs.lbl.mode.caption")));

			String modeItems[] = new String[3];
			modeItems[0] = getString("prefs.modeitem.split");
			modeItems[1] = getString("prefs.modeitem.frames");
			modeItems[2] = getString("prefs.modeitem.layout");
			modeBox = new JComboBox(modeItems);
			modeBox.setSelectedIndex(getMode());
			modeBox.addActionListener(this);
			up.add(modeBox);
			this.add(up, BorderLayout.NORTH);

			center = new JPanel();
			center.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											  getString("prefs.border.options")));
			center.setLayout(card = new CardLayout());

			JTextArea splitText = new JTextArea(getString("prefs.txt.split.text"));
			splitText.setEditable(false);
			splitText.setLineWrap(true);
			splitText.setWrapStyleWord(true);
			splitText.setBackground(center.getBackground());

			JLabel cDummy = new JLabel();
			splitText.setFont(cDummy.getFont());
			splitText.setForeground(cDummy.getForeground());
			center.add(splitText, "0");

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			GridBagConstraints con = new GridBagConstraints();

			con.gridx = 0;
			con.gridwidth = 1;
			con.gridy = 0;
			con.gridheight = 1;
			con.fill = GridBagConstraints.HORIZONTAL;
			con.anchor = GridBagConstraints.NORTHWEST;
			con.weightx = 0.25;

			panel.add(new JLabel(getString("prefs.lbl.activationmode.caption")), con);

			con.gridx = 1;
			con.gridwidth = 3;
			con.weightx = 0.75;

			String actItems[] = new String[3];
			actItems[0] = getString("prefs.activationmode.single");
			actItems[1] = getString("prefs.activationmode.main");
			actItems[2] = getString("prefs.activationmode.all");
			actMode = new JComboBox(actItems);
			actMode.addActionListener(this);
			actLabel = new JTextArea(act[getActivationMode()]);
			actLabel.setEditable(false);
			actLabel.setBackground(this.getBackground());
			actLabel.setLineWrap(true);
			actLabel.setWrapStyleWord(true);
			actMode.setSelectedIndex(getActivationMode());
			panel.add(actMode, con);
			con.gridy = 1;
			panel.add(actLabel, con);

			con.gridx = 0;
			con.gridy = 2;
			con.gridwidth = 1;
			con.weightx = 0.25;
			panel.add(new JLabel(getString("prefs.lbl.iconify.caption")), con);
			con.gridx = 1;
			con.gridwidth = 3;
			con.weightx = 0.75;

			String icoItems[] = new String[2];
			icoItems[0] = getString("prefs.iconify.single");
			icoItems[1] = getString("prefs.iconify.main");
			icoBox = new JComboBox(icoItems);
			icoBox.setSelectedIndex(isIconify() ? 1 : 0);
			icoBox.addActionListener(this);
			icoLabel = new JTextArea(isIconify() ? ico[1] : ico[0]);
			icoLabel.setEditable(false);
			icoLabel.setBackground(this.getBackground());
			icoLabel.setLineWrap(true);
			icoLabel.setWrapStyleWord(true);

			panel.add(icoBox, con);
			con.gridy = 3;
			panel.add(icoLabel, con);

			center.add(panel, "1");

			splitText = new JTextArea(getString("prefs.txt.layout.text"));
			splitText.setEditable(false);
			splitText.setLineWrap(true);
			splitText.setWrapStyleWord(true);
			splitText.setBackground(center.getBackground());
			splitText.setFont(cDummy.getFont());
			splitText.setForeground(cDummy.getForeground());
			center.add(splitText, "2");

			this.add(center, BorderLayout.CENTER);

			setDeskMode(getMode());

			scList = CollectionFactory.createArrayList(1);
			scList.add(new ShortcutList());
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param p1 TODO: DOCUMENT ME!
		 */
		public void actionPerformed(java.awt.event.ActionEvent p1) {
			if(p1.getSource() == modeBox) { // change card

				switch(modeBox.getSelectedIndex()) {
				case 0:
					card.first(center);

					break;

				case 1:
					card.show(center, "1");

					break;

				case 2:
					card.last(center);

					break;
				}
			}

			if(p1.getSource() == actMode) { // change label
				actLabel.setText(act[actMode.getSelectedIndex()]);
			}

			if(p1.getSource() == icoBox) { // change label
				icoLabel.setText(ico[icoBox.getSelectedIndex()]);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getActMode() {
			return actMode.getSelectedIndex();
		}

		/**
		 * Sets the displayed tab.
		 *
		 * @param n TODO: DOCUMENT ME!
		 */
		public void setDeskMode(int n) {
			modeBox.setSelectedIndex(n);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getDeskMode() {
			return modeBox.getSelectedIndex();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean isModeIconify() {
			return icoBox.getSelectedIndex() == 1;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public java.awt.Component getComponent() {
			return this;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public java.lang.String getTitle() {
			return getString("prefs.title");
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			setMode(getDeskMode());
			setActivationMode(getActMode());
			setIconify(isModeIconify());
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getChildren() {
			return scList;
		}

		protected class ShortcutList extends JPanel implements PreferencesAdapter, ActionListener {
			protected JTable table;
			protected DefaultTableModel model;
			protected Collator collator;
			protected Set ownShortcuts;
			protected Set otherShortcuts;

			/**
			 * Creates a new ShortcutList object.
			 */
			public ShortcutList() {
				try {
					collator = Collator.getInstance(com.eressea.util.Locales.getGUILocale());
				} catch(IllegalStateException exc) {
					collator = Collator.getInstance();
				}

				if(shortCutListeners != null) {
					Object columns[] = {
										   getString("prefs.shortcuts.header1"),
										   getString("prefs.shortcuts.header2")
									   };

					Map listeners = CollectionFactory.createHashMap();
					Iterator it = shortCutListeners.entrySet().iterator();

					while(it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						Object value = entry.getValue();

						if(!listeners.containsKey(value)) {
							listeners.put(value, CollectionFactory.createLinkedList());
						}

						// try to find a translation
						KeyStroke oldStroke = (KeyStroke) entry.getKey();
						Object newStroke = findTranslation(oldStroke);

						if(newStroke != null) {
							((Collection) listeners.get(value)).add(newStroke);
						} else {
							((Collection) listeners.get(value)).add(oldStroke);
						}
					}

					Object data[][] = new Object[shortCutListeners.size() + listeners.size()][2];

					List list2 = CollectionFactory.createLinkedList(listeners.keySet());

					Collections.sort(list2, new ListenerComparator());

					it = list2.iterator();

					int i = 0;

					while(it.hasNext()) {
						Object key = it.next();
						ShortcutListener sl = null;

						if(key instanceof ShortcutListener) {
							sl = (ShortcutListener) key;
							data[i][0] = sl.getListenerDescription();

							if(data[i][0] == null) {
								data[i][0] = sl;
							}
						} else {
							data[i][0] = key;
						}

						data[i][1] = null;

						i++;

						List list = (List) listeners.get(key);

						Collections.sort(list, new KeyStrokeComparator());

						Iterator it2 = list.iterator();

						while(it2.hasNext()) {
							Object obj = it2.next();
							data[i][0] = obj;

							if(sl != null) {
								if(shortCutTranslations.containsKey(obj)) {
									obj = getTranslation((KeyStroke) obj);
								}

								try {
									data[i][1] = sl.getShortcutDescription(obj);

									if(data[i][1] == null) {
										data[i][1] = getString("prefs.shortcuts.unknown");
									}
								} catch(RuntimeException re) {
									data[i][1] = getString("prefs.shortcuts.unknown");
								}
							} else {
								data[i][1] = key;
							}

							i++;
						}
					}

					model = new DefaultTableModel(data, columns);

					StrokeRenderer sr = new StrokeRenderer();
					DefaultTableColumnModel tcm = new DefaultTableColumnModel();
					TableColumn column = new TableColumn();
					column.setHeaderValue(columns[0]);
					column.setCellRenderer(sr);
					column.setCellEditor(null);
					tcm.addColumn(column);
					column = new TableColumn(1);
					column.setHeaderValue(columns[1]);
					column.setCellRenderer(sr);
					column.setCellEditor(null);
					tcm.addColumn(column);

					table = new JTable(model, tcm);
					this.setLayout(new BorderLayout());
					this.add(new JScrollPane(table), BorderLayout.CENTER);
					table.addMouseListener(getMousePressedMouseListener());
				}

				// find all java keystrokes
				Set set = CollectionFactory.createHashSet();
				Collection desk = CollectionFactory.createLinkedList();
				desk.add(client);

				if(mode == MODE_FRAME) {
					Iterator it2 = frames.values().iterator();

					while(it2.hasNext()) {
						Component o = ((FrameRectangle) it2.next()).getConnectedFrame();
						desk.add(o);
					}
				}

				Iterator it = desk.iterator();

				while(it.hasNext()) {
					addKeyStrokes((Component) it.next(), set);
				}

				Set set2 = CollectionFactory.createHashSet(shortCutListeners.keySet());
				it = shortCutTranslations.keySet().iterator();

				while(it.hasNext()) {
					set2.remove(shortCutTranslations.get(it.next()));
				}

				ownShortcuts = set2;
				ownShortcuts.addAll(shortCutTranslations.keySet());
				set.removeAll(set2);
				set.removeAll(shortCutTranslations.keySet());
				otherShortcuts = set;

				JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
				JButton help = new JButton(getString("prefs.shortcuts.help"));
				help.addActionListener(this);
				south.add(help);
				this.add(south, BorderLayout.SOUTH);
			}

			protected void addKeyStrokes(Component c, Set set) {
				if(c instanceof JComponent) {
					KeyStroke str[] = ((JComponent) c).getRegisteredKeyStrokes();

					if((str != null) && (str.length > 0)) {
						for(int i = 0; i < str.length; i++) {
							set.add(str[i]);
						}
					}
				}

				if(c instanceof Container) {
					Container con = (Container) c;

					if(con.getComponentCount() > 0) {
						for(int i = 0; i < con.getComponentCount(); i++) {
							addKeyStrokes(con.getComponent(i), set);
						}
					}
				}
			}

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void applyPreferences() {
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
				return getString("prefs.shortcuts.title");
			}

			protected class ListenerComparator implements Comparator {
				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param o1 TODO: DOCUMENT ME!
				 * @param o2 TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public int compare(Object o1, Object o2) {
					String s1 = null;
					String s2 = null;

					if(o1 instanceof ShortcutListener) {
						s1 = ((ShortcutListener) o1).getListenerDescription();
					}

					if(s1 == null) {
						s1 = o1.toString();
					}

					if(o2 instanceof ShortcutListener) {
						s2 = ((ShortcutListener) o2).getListenerDescription();
					}

					if(s2 == null) {
						s2 = o2.toString();
					}

					if((s1 != null) && (s2 != null)) {
						return collator.compare(s1, s2);
					} else if((s1 == null) && (s2 != null)) {
						return -1;
					} else if((s1 != null) && (s2 == null)) {
						return 1;
					}

					return 0;
				}
			}

			protected class KeyStrokeComparator implements Comparator {
				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param o1 TODO: DOCUMENT ME!
				 * @param o2 TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public int compare(Object o1, Object o2) {
					if((o1 instanceof KeyStroke) && (o2 instanceof KeyStroke)) {
						KeyStroke k1 = (KeyStroke) o1;
						KeyStroke k2 = (KeyStroke) o2;

						if(k1.getModifiers() != k2.getModifiers()) {
							int i1 = k1.getModifiers();
							int i2 = 0;
							int j1 = k2.getModifiers();
							int j2 = 0;

							while(i1 != 0) {
								if((i1 % 2) == 1) {
									i2++;
								}

								i1 /= 2;
							}

							while(j1 != 0) {
								if((j1 % 2) == 1) {
									j2++;
								}

								j1 /= 2;
							}

							if(i2 != j2) {
								return i2 - j2;
							}

							return k1.getModifiers() - k2.getModifiers();
						}

						return k1.getKeyCode() - k2.getKeyCode();
					}

					return 0;
				}
			}

			protected class ListTableModel extends DefaultTableModel {
				/**
				 * Creates a new ListTableModel object.
				 *
				 * @param data TODO: DOCUMENT ME!
				 * @param columns TODO: DOCUMENT ME!
				 */
				public ListTableModel(Object data[][], Object columns[]) {
					super(data, columns);
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param r TODO: DOCUMENT ME!
				 * @param c TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public boolean isCellEditable(int r, int c) {
					return false;
				}
			}

			protected String getKeyStroke(KeyStroke stroke) {
				String s = null;

				if(stroke.getModifiers() != 0) {
					s = KeyEvent.getKeyModifiersText(stroke.getModifiers()) + " + " +
						KeyEvent.getKeyText(stroke.getKeyCode());
				} else {
					s = KeyEvent.getKeyText(stroke.getKeyCode());
				}

				return s;
			}

			protected MouseListener getMousePressedMouseListener() {
				return new MouseAdapter() {
						public void mousePressed(MouseEvent mouseEvent) {
							if(mouseEvent.getClickCount() == 2) {
								Point p = mouseEvent.getPoint();

								if((table.columnAtPoint(p) == 0) && (table.rowAtPoint(p) >= 0)) {
									int row = table.rowAtPoint(p);
									Object value = table.getValueAt(row, 0);

									if(value instanceof KeyStroke) {
										editStroke((KeyStroke) value, row);
									}
								}
							}
						}
					};
			}

			protected void editStroke(KeyStroke stroke, int row) {
				Component top = this.getTopLevelAncestor();
				TranslateStroke td = null;

				if(top instanceof Frame) {
					td = new TranslateStroke((Frame) top);
				} else if(top instanceof Dialog) {
					td = new TranslateStroke((Dialog) top);
				}

				td.show();

				KeyStroke newStroke = td.getStroke();

				if((newStroke != null) && !newStroke.equals(stroke)) {
					if(ownShortcuts.contains(newStroke)) {
						JOptionPane.showMessageDialog(this, getString("prefs.shortcuts.error"));
					} else {
						boolean doIt = true;

						if(otherShortcuts.contains(newStroke)) {
							int res = JOptionPane.showConfirmDialog(this,
																	getString("prefs.shortcuts.warning"),
																	getString("prefs.shortcuts.warningtitle"),
																	JOptionPane.YES_NO_OPTION);
							doIt = (res == JOptionPane.YES_OPTION);
						}

						if(doIt) {
							if(shortCutTranslations.containsKey(stroke)) {
								KeyStroke oldStroke = (KeyStroke) shortCutTranslations.get(stroke);
								removeTranslation(stroke);
								stroke = oldStroke;
							}

							if(shortCutListeners.containsKey(stroke) &&
								   (shortCutListeners.get(stroke) instanceof Action)) {
								((Action) shortCutListeners.get(stroke)).putValue("accelerator",
																				  newStroke);
							}

							if(!newStroke.equals(stroke)) {
								registerTranslation(newStroke, stroke);
							}

							model.setValueAt(newStroke, row, 0);
						}
					}
				}
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param actionEvent TODO: DOCUMENT ME!
			 */
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			}

			protected class InformDialog extends JDialog implements ActionListener {
				/**
				 * Creates a new InformDialog object.
				 *
				 * @param parent TODO: DOCUMENT ME!
				 */
				public InformDialog(Frame parent) {
					super(parent, true);
					init();
				}

				/**
				 * Creates a new InformDialog object.
				 *
				 * @param parent TODO: DOCUMENT ME!
				 */
				public InformDialog(Dialog parent) {
					super(parent, true);
					init();
				}

				protected void init() {
					JPanel con = new JPanel(new BorderLayout());

					StringBuffer buf = new StringBuffer();

					Object args[] = { new Integer(otherShortcuts.size()) };
					buf.append(MessageFormat.format(getString("prefs.shortcuts.others"), args));
					buf.append('\n');
					buf.append('\n');

					Iterator it = otherShortcuts.iterator();

					while(it.hasNext()) {
						buf.append(getKeyStroke((KeyStroke) it.next()));

						if(it.hasNext()) {
							buf.append(", ");
						}
					}

					JTextArea java = new JTextArea(buf.toString());
					java.setEditable(false);
					java.setLineWrap(true);
					java.setWrapStyleWord(true);
					con.add(new JScrollPane(java), BorderLayout.SOUTH);

					JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
					JButton ok = new JButton("prefs.shortcuts.dialog.ok");
					ok.addActionListener(this);
					button.add(ok);
					con.add(button, BorderLayout.SOUTH);
					this.setContentPane(con);

					this.pack();
					this.setLocationRelativeTo(this.getParent());
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param actionEvent TODO: DOCUMENT ME!
				 */
				public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
					this.setVisible(false);
				}
			}

			protected class TranslateStroke extends JDialog implements ActionListener {
				protected KeyTextField text;
				protected JButton cancel;
				protected KeyStroke stroke = null;

				/**
				 * Creates a new TranslateStroke object.
				 *
				 * @param parent TODO: DOCUMENT ME!
				 */
				public TranslateStroke(Frame parent) {
					super(parent, true);
					init();
				}

				/**
				 * Creates a new TranslateStroke object.
				 *
				 * @param parent TODO: DOCUMENT ME!
				 */
				public TranslateStroke(Dialog parent) {
					super(parent, true);
					init();
				}

				protected void init() {
					JPanel con = new JPanel(new BorderLayout());
					con.add(new JLabel(getString("prefs.shortcuts.dialog.label")),
							BorderLayout.NORTH);
					text = new KeyTextField();
					con.add(text, BorderLayout.CENTER);

					JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
					JButton ok = new JButton(getString("prefs.shortcuts.dialog.ok"));
					buttons.add(ok);
					ok.addActionListener(this);
					cancel = new JButton(getString("prefs.shortcuts.dialog.cancel"));
					buttons.add(cancel);
					cancel.addActionListener(this);
					con.add(buttons, BorderLayout.SOUTH);
					this.setContentPane(con);
					this.pack();
					this.setSize(this.getWidth() + 5, this.getHeight() + 5);
					this.setLocationRelativeTo(this.getParent());
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param actionEvent TODO: DOCUMENT ME!
				 */
				public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
					if(actionEvent.getSource() != cancel) {
						if(text.getKeyCode() != 0) {
							stroke = KeyStroke.getKeyStroke(text.getKeyCode(), text.getModifiers());
						}
					}

					this.setVisible(false);
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public KeyStroke getStroke() {
					return stroke;
				}

				private class KeyTextField extends JTextField implements KeyListener {
					protected int modifiers = 0;
					protected int key = 0;

					/**
					 * Creates a new KeyTextField object.
					 */
					public KeyTextField() {
						super(20);
						this.addKeyListener(this);
					}

					/**
					 * TODO: DOCUMENT ME!
					 *
					 * @param modifiers TODO: DOCUMENT ME!
					 * @param key TODO: DOCUMENT ME!
					 */
					public void init(int modifiers, int key) {
						this.key = key;
						this.modifiers = modifiers;

						String s = KeyEvent.getKeyModifiersText(modifiers);

						if((s != null) && (s.length() > 0)) {
							s += ('+' + KeyEvent.getKeyText(key));
						} else {
							s = KeyEvent.getKeyText(key);
						}

						setText(s);
					}

					/**
					 * TODO: DOCUMENT ME!
					 *
					 * @param p1 TODO: DOCUMENT ME!
					 */
					public void keyReleased(KeyEvent p1) {
						// maybe should delete any input if there's no "stable"(non-modifying) key
					}

					/**
					 * TODO: DOCUMENT ME!
					 *
					 * @param p1 TODO: DOCUMENT ME!
					 */
					public void keyPressed(KeyEvent p1) {
						modifiers = p1.getModifiers();
						key = p1.getKeyCode();

						// avoid double string
						if((key == KeyEvent.VK_SHIFT) || (key == KeyEvent.VK_CONTROL) ||
							   (key == KeyEvent.VK_ALT) || (key == KeyEvent.VK_ALT_GRAPH)) {
							int xored = 0;

							switch(key) {
							case KeyEvent.VK_SHIFT:
								xored = KeyEvent.SHIFT_MASK;

								break;

							case KeyEvent.VK_CONTROL:
								xored = KeyEvent.CTRL_MASK;

								break;

							case KeyEvent.VK_ALT:
								xored = KeyEvent.ALT_MASK;

								break;

							case KeyEvent.VK_ALT_GRAPH:
								xored = KeyEvent.ALT_GRAPH_MASK;

								break;
							}

							modifiers ^= xored;
						}

						String s = KeyEvent.getKeyModifiersText(modifiers);

						if((s != null) && (s.length() > 0)) {
							s += ('+' + KeyEvent.getKeyText(key));
						} else {
							s = KeyEvent.getKeyText(key);
						}

						setText(s);
						p1.consume();
					}

					/**
					 * TODO: DOCUMENT ME!
					 *
					 * @param p1 TODO: DOCUMENT ME!
					 */
					public void keyTyped(KeyEvent p1) {
					}

					// to allow "tab" as a key
					public boolean isManagingFocus() {
						return true;
					}

					/**
					 * TODO: DOCUMENT ME!
					 *
					 * @return TODO: DOCUMENT ME!
					 */
					public int getKeyCode() {
						return key;
					}

					/**
					 * TODO: DOCUMENT ME!
					 *
					 * @return TODO: DOCUMENT ME!
					 */
					public int getModifiers() {
						return modifiers;
					}
				}
			}

			protected class StrokeRenderer extends DefaultTableCellRenderer {
				protected Font bold;
				protected Font norm;

				/**
				 * Creates a new StrokeRenderer object.
				 */
				public StrokeRenderer() {
					norm = this.getFont();
					bold = this.getFont().deriveFont(Font.BOLD);
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param table TODO: DOCUMENT ME!
				 * @param value TODO: DOCUMENT ME!
				 * @param isSelected TODO: DOCUMENT ME!
				 * @param hasFocus TODO: DOCUMENT ME!
				 * @param row TODO: DOCUMENT ME!
				 * @param column TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public Component getTableCellRendererComponent(JTable table, Object value,
															   boolean isSelected,
															   boolean hasFocus, int row, int column) {
					this.setFont(norm);
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
														column);

					if(value instanceof KeyStroke) {
						this.setText(getKeyStroke((KeyStroke) value));
					} else if(column == 0) {
						this.setFont(bold);
					}

					return this;
				}
			}
		}
	}

	/**
	 * Returns a translation for the specified key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this.getClass(), key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static final Map defaultTranslations = CollectionFactory.createHashtable();

	static {
		defaultTranslations.put("msg.corruptsettings.text",
								"The file magellan_desktop.ini where the desktop layout information is stored seems to be corrupted.\n" +
								"Instead, a standard configuration is used.");
		defaultTranslations.put("menu.desktop.caption", "Desktop");
		defaultTranslations.put("menu.desktop.mnemonic", "d");
		defaultTranslations.put("prefs.shortcuts.others",
								"Found {0, number} other, non-Magellan, non-changeable shortcuts. These belong to the system and should not be used. There may also be some other key combinations in use that are not registered, so choose your shortcuts with care.");
		defaultTranslations.put("prefs.shortcuts.dialog.cancel", "Cancel");
		defaultTranslations.put("prefs.shortcuts.dialog.ok", "OK");
		defaultTranslations.put("prefs.shortcuts.dialog.label", "Enter your desired shortcut:");
		defaultTranslations.put("prefs.shortcuts.warningtitle", "Warning...");
		defaultTranslations.put("prefs.shortcuts.warning",
								"This shortcut is used by the system. Do you really want to use it?");
		defaultTranslations.put("prefs.shortcuts.error", "This shortcut is already in use!");
		defaultTranslations.put("prefs.shortcuts.help", "Help");
		defaultTranslations.put("prefs.shortcuts.title", "Shortcuts");
		defaultTranslations.put("prefs.shortcuts.unknown", "?");
		defaultTranslations.put("prefs.shortcuts.header2", "Description");
		defaultTranslations.put("prefs.shortcuts.header1", "Shortcuts");
		defaultTranslations.put("menu.frames.caption", "Frames");
		defaultTranslations.put("menu.frames.menmonic", "f");
		defaultTranslations.put("menu.set.caption", "Split sets");
		defaultTranslations.put("menu.set.mnemonic", "s");
		defaultTranslations.put("menu.layout.caption", "Layouts");
		defaultTranslations.put("menu.layout.mnemonic", "l");
		defaultTranslations.put("frame.overview.title", "Overview");
		defaultTranslations.put("frame.history.title", "History");
		defaultTranslations.put("frame.overviewandhistory.title", "Overview");
		defaultTranslations.put("frame.minimap.title", "Overview map");
		defaultTranslations.put("frame.map.title", "Map");
		defaultTranslations.put("frame.messages.title", "Messages");
		defaultTranslations.put("frame.name.title", "Name");
		defaultTranslations.put("frame.details.title", "Details");
		defaultTranslations.put("frame.orders.title", "Orders");
		defaultTranslations.put("prefs.title", "Desktop");
		defaultTranslations.put("prefs.activationdescription.single",
								"Activate each frame by clicking on it.");
		defaultTranslations.put("prefs.activationdescription.main",
								"Bring all frames to the front by activating the main window.");
		defaultTranslations.put("prefs.activationdescription.all",
								"Bring all frames to the front by activating any one of them.");
		defaultTranslations.put("prefs.iconifydescription.single",
								"When minimizing or restoring the main window all other frames keep their state.");
		defaultTranslations.put("prefs.iconifydescription.main",
								"When minimizing or restoring the main window the states of all other frames are changed accordingly.");

		defaultTranslations.put("prefs.lbl.mode.caption", "Desktop display mode: ");
		defaultTranslations.put("prefs.modeitem.split", "Split panes");
		defaultTranslations.put("prefs.modeitem.frames", "Frames");
		defaultTranslations.put("menu.frames.activate", "Activate mode");
		defaultTranslations.put("prefs.modeitem.layout", "Fixed layout");
		defaultTranslations.put("prefs.border.options", "Mode options");

		defaultTranslations.put("prefs.txt.split.text",
								"\nNo options available for this mode.\n\nPlease select a split pane set in the Desktop menu.");
		defaultTranslations.put("prefs.txt.layout.text",
								"\nNo options available for this mode.\n\nPlease select a layout set in the Desktop menu.");

		defaultTranslations.put("prefs.lbl.activationmode.caption", "Frame activation: ");

		defaultTranslations.put("prefs.activationmode.single", "Independently");
		defaultTranslations.put("prefs.activationmode.main", "With main frame");
		defaultTranslations.put("prefs.activationmode.all", "By any frame");

		defaultTranslations.put("prefs.lbl.iconify.caption", "Frame iconification: ");
		defaultTranslations.put("prefs.iconify.single", "Independently");
		defaultTranslations.put("prefs.iconify.main", "With main frame");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}

	protected class BackgroundPanel extends JPanel {
		/**
		 * Creates a new BackgroundPanel object.
		 */
		public BackgroundPanel() {
			super(new BorderLayout());
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param c TODO: DOCUMENT ME!
		 */
		public void setComponent(Component c) {
			clearOpaque(c);
			this.add(c, BorderLayout.CENTER);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param g TODO: DOCUMENT ME!
		 */
		public void paintComponent(Graphics g) {
			if(bgMode == -1) {
				super.paintComponent(g);

				return;
			}

			if(bgMode == 0) {
				if(bgColor != null) {
					g.setColor(bgColor);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
				}
			} else {
				if(bgImage != null) {
					if(bgMode == 1) { // resize
						g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), this);
					} else { // repeat

						int w = bgImage.getWidth(this);
						int h = bgImage.getHeight(this);
						int wi = this.getWidth() / w;

						if((this.getWidth() % w) != 0) {
							wi++;
						}

						int hi = this.getHeight() / h;

						if((this.getHeight() % h) != 0) {
							hi++;
						}

						for(int i = 0; i < hi; i++) {
							for(int j = 0; j < wi; j++) {
								g.drawImage(bgImage, j * w, i * h, this);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component add(Component c) {
		clearOpaque(c);

		return super.add(c);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 * @param con TODO: DOCUMENT ME!
	 */
	public void add(Component c, Object con) {
		clearOpaque(c);
		super.add(c, con);
	}

	protected void clearOpaque(Component c) {
		if(bgMode == -1) {
			return;
		}

		if(c instanceof JComponent) {
			((JComponent) c).setOpaque(false);
		}

		if(c instanceof Container) {
			Component children[] = ((Container) c).getComponents();

			if((children != null) && (children.length > 0)) {
				for(int i = 0; i < children.length; i++) {
					clearOpaque(children[i]);
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param g TODO: DOCUMENT ME!
	 */
	public void paintComponent(Graphics g) {
		if(bgMode == -1) {
			super.paintComponent(g);

			return;
		}

		if(bgMode == 0) {
			if(bgColor != null) {
				g.setColor(bgColor);
				g.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
		} else {
			if(bgImage != null) {
				if(bgMode == 1) { // resize
					g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), this);
				} else { // repeat

					int w = bgImage.getWidth(this);
					int h = bgImage.getHeight(this);
					int wi = this.getWidth() / w;

					if((this.getWidth() % w) != 0) {
						wi++;
					}

					int hi = this.getHeight() / h;

					if((this.getHeight() % h) != 0) {
						hi++;
					}

					for(int i = 0; i < hi; i++) {
						for(int j = 0; j < wi; j++) {
							g.drawImage(bgImage, j * w, i * h, this);
						}
					}
				}
			}
		}
	}
}
