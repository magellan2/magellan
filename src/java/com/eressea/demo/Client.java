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

package com.eressea.demo;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.FontUIResource;

import com.eressea.CoordinateID;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.IntegerID;
import com.eressea.Message;
import com.eressea.MissingData;
import com.eressea.Unit;
import com.eressea.demo.actions.AbortAction;
import com.eressea.demo.actions.AddCRAction;
import com.eressea.demo.actions.AddSelectionAction;
import com.eressea.demo.actions.ArmyStatsAction;
import com.eressea.demo.actions.ChangeFactionConfirmationAction;
import com.eressea.demo.actions.ConfirmAction;
import com.eressea.demo.actions.ECheckAction;
import com.eressea.demo.actions.ExpandSelectionAction;
import com.eressea.demo.actions.ExportCRAction;
import com.eressea.demo.actions.ExternalModuleAction;
import com.eressea.demo.actions.FactionStatsAction;
import com.eressea.demo.actions.FileSaveAction;
import com.eressea.demo.actions.FileSaveAsAction;
import com.eressea.demo.actions.FillSelectionAction;
import com.eressea.demo.actions.FindAction;
import com.eressea.demo.actions.FindPreviousUnconfirmedAction;
import com.eressea.demo.actions.HelpAction;
import com.eressea.demo.actions.InfoAction;
import com.eressea.demo.actions.InvertSelectionAction;
import com.eressea.demo.actions.IslandAction;
import com.eressea.demo.actions.MapSaveAction;
import com.eressea.demo.actions.MenuAction;
import com.eressea.demo.actions.OpenCRAction;
import com.eressea.demo.actions.OpenOrdersAction;
import com.eressea.demo.actions.OpenSelectionAction;
import com.eressea.demo.actions.OptionAction;
import com.eressea.demo.actions.QuitAction;
import com.eressea.demo.actions.RedoAction;
import com.eressea.demo.actions.RepaintAction;
import com.eressea.demo.actions.SaveOrdersAction;
import com.eressea.demo.actions.SaveSelectionAction;
import com.eressea.demo.actions.SelectAllAction;
import com.eressea.demo.actions.SelectIslandsAction;
import com.eressea.demo.actions.SelectNothingAction;
import com.eressea.demo.actions.SetOriginAction;
import com.eressea.demo.actions.TaskTableAction;
import com.eressea.demo.actions.TileSetAction;
import com.eressea.demo.actions.TipOfTheDayAction;
import com.eressea.demo.actions.TradeOrganizerAction;
import com.eressea.demo.actions.UnconfirmAction;
import com.eressea.demo.actions.UndoAction;
import com.eressea.demo.actions.VorlageAction;
import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.demo.desktop.MagellanDesktop;
import com.eressea.demo.desktop.ShortcutListener;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.OrderConfirmEvent;
import com.eressea.event.OrderConfirmListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.event.TempUnitEvent;
import com.eressea.event.TempUnitListener;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.event.UnitOrdersListener;
import com.eressea.extern.ExternalModule;
import com.eressea.extern.ExternalModule2;
import com.eressea.extern.ExternalModule3;
import com.eressea.extern.ExternalModuleLoader;
import com.eressea.extern.ExternalModule3Loader;
import com.eressea.io.GameDataReader;
import com.eressea.io.file.FileBackup;
import com.eressea.io.file.FileTypeFactory;
import com.eressea.main.MagellanContext;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.resource.ResourceSettingsFactory;
import com.eressea.rules.EresseaDate;
import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.MagellanLookAndFeel;
import com.eressea.swing.MapperPanel;
import com.eressea.swing.MenuProvider;
import com.eressea.swing.MessagePanel;
import com.eressea.swing.StartWindow;
import com.eressea.swing.TipOfTheDay;
import com.eressea.swing.map.CellGeometry;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.swing.tree.IconAdapterFactory;
import com.eressea.util.BookmarkManager;
import com.eressea.util.CollectionFactory;
import com.eressea.util.FileHistory;
import com.eressea.util.JVMUtilities;
import com.eressea.util.LanguageDialog;
import com.eressea.util.Locales;
import com.eressea.util.Log;
import com.eressea.util.MagellanFinder;
import com.eressea.util.NameGenerator;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.RendererLoader;
import com.eressea.util.SelectionHistory;
import com.eressea.util.SelfCleaningProperties;
import com.eressea.util.Translations;
import com.eressea.util.TrustLevels;
import com.eressea.util.VersionInfo;
import com.eressea.util.logging.Logger;

/**
 * This class is the root of all evil. It represents also the main entry point
 * into the application and also the basic frame the application creates.
 * 
 * @author $author$
 * @version $Revision$
 */
public class Client extends JFrame implements ShortcutListener, PreferencesFactory {
    private static final Logger log = Logger.getInstance(Client.class);

    // private GameData data = null;
    private List panels = null;

    private MapperPanel mapPanel = null;

    private EMapOverviewPanel overviewPanel = null;

    private EMapDetailsPanel detailsPanel = null;

    private MessagePanel messagePanel = null;

    /**
     * DOCUMENT ME!
     * 
     * @deprecated, use info from GameData
     */
    private File dataFile = null;

    protected boolean everLoadedReport = false; // indicates that the user
                                                // loaded a report at least once
                                                // in order to decide about
                                                // showing a save dialog when
                                                // quitting

    private FileHistory fileHistory;

    private JMenu factionOrdersMenu;

    private JMenu factionOrdersMenuNot;

    private JMenu invertAllOrdersConfirmation;

    private List nodeWrapperFactories;

    private List preferencesAdapterList;

    private MenuAction saveAction;

    private OptionAction optionAction;

    private MagellanDesktop desktop;

    private ReportObserver reportState;

    /** Manager for setting and activating bookmarks. */
    private BookmarkManager bookmarkManager;

    /** Central undo manager - specialized to deliver change events */
    private MagellanUndoManager undoMgr = null;

    // Directories
    private static File filesDirectory = null; // Magellan directory

    private static File settingsDirectory = null; // Directory of
                                                    // "magellan.ini"

    // show order status in title
    protected boolean showStatus = false;

    protected JMenuItem progressItem;

    // start window, disposed after first init
    protected static StartWindow startWindow;

    protected static ResourceBundle startBundle;

    /**
     * Creates a new Client object taking its data from <tt>gd</tt>.
     * 
     * <p>
     * Preferences are read from and stored in a file called <tt>client.ini</tt>.
     * This file is usually located in the user's home directed, which is the
     * Windows directory in a Microsoft Windows environment.
     * </p>
     * 
     * @param gd
     *            TODO: DOCUMENT ME!
     * @param fileDir
     *            TODO: DOCUMENT ME!
     * @param settingsDir
     *            TODO: DOCUMENT ME!
     */
    public Client(GameData gd, File fileDir, File settingsDir) {
        filesDirectory = fileDir;
        settingsDirectory = settingsDir;

        // get new dispatcher
        EventDispatcher dispatcher = new EventDispatcher();

        startWindow.progress(1, (startBundle != null) ? startBundle.getString("1")
				: "Loading settings...");
        Properties settings = loadSettings(settingsDirectory, "magellan.ini");
        if (settings == null) {
			log.info("Client.loadSettings: settings file " + "magellan.ini"
					+ " does not exist, using default values.");
			settings = new SelfCleaningProperties();
	        initLocales(settings, true);
		}else{
			initLocales(settings, false);
		}
        
        showStatus = PropertiesHelper.getboolean(settings,"Client.ShowOrderStatus", false);

        Properties completionSettings = loadSettings(settingsDirectory, "magellan_completions.ini");
        if (completionSettings==null)
        	completionSettings=new SelfCleaningProperties();

        // initialize the context, this has to be very early.
        context = new MagellanContext(this);
        context.setEventDispatcher(dispatcher);
        context.setProperties(settings);
        context.setCompletionProperties(completionSettings);
        context.init();

        context.setGameData(gd);
        // init icon, fonts, repaint shortcut, L&F, window things
        initUI();

        // create management and observer objects
        dispatcher.addSelectionListener(SelectionHistory.getEventHook());
        bookmarkManager = new BookmarkManager(dispatcher, settings);
        undoMgr = new MagellanUndoManager();
        reportState = new ReportObserver(dispatcher);

        // init components
        startWindow.progress(2, (startBundle != null) ? startBundle
                .getString("2") : "Initializing components...");
        panels = CollectionFactory.createLinkedList();
        nodeWrapperFactories = CollectionFactory.createLinkedList();

        List topLevelComponents = new LinkedList();
        Map components = initComponents(topLevelComponents);

        // init desktop
        startWindow.progress(3, (startBundle != null) ? startBundle
                .getString("3") : "Creating desktop environment...");
        Rectangle bounds = PropertiesHelper.loadRect(settings, null, "Client");
        if (bounds!=null)
        	setBounds(bounds);

        desktop = new MagellanDesktop(this, context, settings, components,
                settingsDirectory);

        setContentPane(desktop);

        // do it here because we need the desktop menu
        setJMenuBar(createMenuBar(topLevelComponents));

        // enable EventDisplayer
        // new
        // com.eressea.util.logging.EventDisplayDialog(this,false,dispatcher).setVisible(true);
    }

    // ////////////////////////
    // BASIC initialization //
    // ////////////////////////

    private MagellanContext context;

    /**
     * Load the file fileName in the given directory into the settings object.
     * 
     * @param directory
     *            TODO: DOCUMENT ME!
     * @param fileName
     *            TODO: DOCUMENT ME!
     */
    protected Properties loadSettings(File directory, String fileName) {
        Properties settings =  new SelfCleaningProperties();
        // settings = new OrderedOutputProperties();
        // settings = new AgingProperties();

        settings.clear();

        File settingsFile = new File(directory, fileName);

        // load settings from file
        if (settingsFile.exists()) {
            try {
                settings.load(new BufferedInputStream(new FileInputStream(
                        settingsFile)));
                log.info("Client.loadSettings: successfully loaded "+settingsFile);
            } catch (IOException e) {
                log.error("Client.loadSettings: Error while loading "
                        + settingsFile, e);
                return null;
            }
        } else {
        	return null;
        }
        return settings;
    }
    
    protected void initLocales(Properties settings, boolean ask) {
    	if (ask){
    		LanguageDialog ld = new LanguageDialog(settings, filesDirectory);

    		if (ld.languagesFound()) {
    			startWindow.toBack();
    			Locale locale = ld.showDialog(startWindow);
    			startWindow.toFront();

    			if ((locale != null) && !locale.equals(Locale.getDefault())) {
    				settings.setProperty("locales.gui", locale.getLanguage());
    				settings.setProperty("locales.orders", locale.getLanguage());
    			}
    		}
    	}
    	
        if (settings.getProperty("locales.gui") != null)
        	Locales.setGUILocale(new Locale(settings.getProperty("locales.gui")));
        else
        	Locales.setGUILocale(Locale.getDefault());
        if (settings.getProperty("locales.orders") != null)
        	Locales.setOrderLocale(new Locale(settings.getProperty("locales.orders")));
        else
        	Locales.setOrderLocale(Locale.GERMAN);
        log.info("GUI locale: "+Locales.getGUILocale()+settings.getProperty("locales.gui")+", orders locale: "+Locales.getOrderLocale()+settings.getProperty("locales.orders"));
    }

    // TODO (stm) this is used by exactly once in the whole project. Why do we
    // need context anyway?
    /**
     * Returns the MagellanContext
     */
    public MagellanContext getMagellanContext() {
        return context;
    }

    /**
     * Returns the application icon
     * 
     * @return the application icon
     */
    public static Image getApplicationIcon() {
        // set the application icon
        URL iconURL = ResourcePathClassLoader
                .getResourceStatically("images/about/appicon.gif");

        if (iconURL == null) {
            iconURL = ResourcePathClassLoader
                    .getResourceStatically("res/images/about/appicon.gif");
        }

        if (log.isDebugEnabled()) {
            log.debug("Client.getApplicationIcon() iconURL: " + iconURL);
        }

        return (iconURL == null) ? null : Toolkit.getDefaultToolkit()
                .createImage(iconURL);
    }

    /**
     * Inits base UI things: # frame icon # window event things # fonts #
     * repaint shortcut # L&F
     */
    protected void initUI() {
        Image iconImage = getApplicationIcon();

        // set the application icon
        if (iconImage != null) {
            setIconImage(iconImage);
        }

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                quit(true);
            }
        });

        /* setup font size */
        try {
            float fScale = PropertiesHelper.getfloat(getProperties(),
                    "Client.FontScale", 1.0f);

            if (fScale != 1.0f) {
                // TODO(pavkovic): the following code bloats the fonts in an
                // undesired way, perhaps
                // we remove this configuration option?
                UIDefaults table = UIManager.getDefaults();
                Enumeration eKeys = table.keys();

                while (eKeys.hasMoreElements()) {
                    Object obj = eKeys.nextElement();
                    Font font = UIManager.getFont(obj);

                    if (font != null) {
                        font = new FontUIResource(font.deriveFont(font
                                .getSize2D()
                                * fScale));
                        UIManager.put(obj, font);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }

        // initialize client shortcut - F5 to repaint
        DesktopEnvironment.registerShortcutListener(KeyStroke.getKeyStroke(
                KeyEvent.VK_F5, 0), this);

        // init L&F
        initLookAndFeels();
    }

    // ////////////////////////////
    // COMPONENT initialization //
    // ////////////////////////////

    /**
     * Initializes the Magellan components. The returned hashtable holds all
     * components with well-known desktop keywords.
     * 
     * @param topLevel
     *            TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    protected Map initComponents(List topLevel) {
        Map components = CollectionFactory.createHashtable();

        // configure and add map panel
        // get cell geometry
        CellGeometry geo = new CellGeometry("cellgeometry.txt");

        // load custom renderers
        // ForcedFileClassLoader.directory = filesDirectory;
        RendererLoader rl = new RendererLoader(filesDirectory, ".", geo,
                getProperties());
        Collection cR = rl.loadRenderers();

        // init mapper
        mapPanel = new MapperPanel(getMagellanContext(), cR, geo);
        mapPanel.setMinimumSize(new Dimension(100, 10));
        mapPanel.setScaleFactor(PropertiesHelper.getfloat(getProperties(),
                "Map.scaleFactor", 1.0f));
        panels.add(mapPanel);
        components.put("MAP", mapPanel);
        components.put("MINIMAP", mapPanel.getMinimap());
        topLevel.add(mapPanel);

        // configure and add message panel
        messagePanel = new MessagePanel(getDispatcher(), getData(),
                getProperties());
        messagePanel.setMinimumSize(new Dimension(100, 10));
        panels.add(messagePanel);
        nodeWrapperFactories.add(messagePanel.getNodeWrapperFactory());
        components.put("MESSAGES", messagePanel);
        topLevel.add(messagePanel);

        // configure and add details panel
        detailsPanel = new EMapDetailsPanel(getDispatcher(), getData(),
                getProperties(), undoMgr);
        detailsPanel.setMinimumSize(new Dimension(100, 10));
        panels.add(detailsPanel);
        nodeWrapperFactories.add(detailsPanel.getNodeWrapperFactory());

        Container c = (Container) detailsPanel.topSplitPane.getTopComponent();
        components.put("NAME&DESCRIPTION", c);
        components.put("NAME", c.getComponent(0));
        components.put("DESCRIPTION", c.getComponent(1));
        components.put("DETAILS", detailsPanel.topSplitPane
                .getBottomComponent());
        components.put("ORDERS", detailsPanel.bottomSplitPane
                .getBottomComponent());

        // this keyword is deprecated
        components.put("COMMANDS", detailsPanel.bottomSplitPane
                .getBottomComponent());
        topLevel.add(detailsPanel);

        // configure and add overview panel
        overviewPanel = new EMapOverviewPanel(getDispatcher(), getProperties());
        overviewPanel.setMinimumSize(new Dimension(100, 10));
        panels.add(overviewPanel);
        components.put("OVERVIEW", overviewPanel.getOverviewComponent());
        components.put("HISTORY", overviewPanel.getHistoryComponent());
        components.put("OVERVIEW&HISTORY", overviewPanel);
        nodeWrapperFactories.add(overviewPanel.getNodeWrapperFactory());
        topLevel.add(overviewPanel);

        return components;
    }

    // //////////////////////////
    // MENUBAR initialization //
    // //////////////////////////

    /**
     * Creates a menu bar to be added to this frame.
     * 
     * @param components
     *            TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    private JMenuBar createMenuBar(Collection components) {
        JMenuBar menuBar = new JMenuBar();

        // create static menus
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createOrdersMenu());
        menuBar.add(createBookmarkMenu());
        menuBar.add(createMapMenu());

        // create dynamix menus
        Map topLevel = new HashMap();
        List direction = new LinkedList();
        Iterator it = components.iterator();
        log.info("Checking for menu-providers...");

        while (it.hasNext()) {
            Object o = it.next();

            if (o instanceof MenuProvider) {
                MenuProvider mp = (MenuProvider) o;

                if (mp.getSuperMenu() != null) {
                    if (!topLevel.containsKey(mp.getSuperMenu())) {
                        topLevel.put(mp.getSuperMenu(), new JMenu(mp
                                .getSuperMenuTitle()));
                        direction.add(topLevel.get(mp.getSuperMenu()));
                    }

                    JMenu top = (JMenu) topLevel.get(mp.getSuperMenu());
                    top.add(mp.getMenu());
                } else {
                    direction.add(mp.getMenu());
                }
            }
        }
        
        log.info("Checking for menu-providers...(Module3");
        // add external modules if some can be found
        Collection menuS = getExternalModules3();
        if (menuS!=null && menuS.size()>0) {
        	for (Iterator i3 = menuS.iterator();i3.hasNext();){
        		ExternalModule3 externalModule3 = (ExternalModule3)i3.next();
        		JMenu externalJMenu3 = externalModule3.getJMenu(this);
        		if (externalJMenu3!=null) {
        			menuBar.add(externalJMenu3);
        		}
        	}
        }
        

        // menuBar.add(createTreeMenu());
        // desktop and extras last
        menuBar.add(desktop.getDesktopMenu());
        menuBar.add(createExtrasMenu());

        return menuBar;
    }

    protected JMenu createFileMenu() {
        JMenu file = new JMenu(getString("menu.file.caption"));
        file.setMnemonic(getString("menu.file.mnemonic").charAt(0));
        addMenuItem(file, new OpenCRAction(this));
        addMenuItem(file, new AddCRAction(this));
        addMenuItem(file, new OpenOrdersAction(this));
        file.addSeparator();
        saveAction = new FileSaveAction(this);
        addMenuItem(file, saveAction);
        addMenuItem(file, new FileSaveAsAction(this));
        addMenuItem(file, new SaveOrdersAction(this));
        file.addSeparator();
        addMenuItem(file, new ExportCRAction(this));
        file.addSeparator();

        // now create the file history since we have all data
        fileHistory = new FileHistory(this, getProperties(), file, file
                .getItemCount());
        fileHistory.buildFileHistoryMenu();
        file.addSeparator();
        addMenuItem(file, new AbortAction(this));
        addMenuItem(file, new QuitAction(this));

        return file;
    }

    protected JMenu createEditMenu() {
        JMenu edit = new JMenu(getString("menu.edit.caption"));
        edit.setMnemonic(getString("menu.edit.mnemonic").charAt(0));
        addMenuItem(edit, new UndoAction(this, undoMgr));
        addMenuItem(edit, new RedoAction(this, undoMgr));
        edit.addSeparator();
        addMenuItem(edit, new FindAction(this));

        return edit;
    }

    protected JMenu createOrdersMenu() {
        JMenu ordersMenu = new JMenu(getString("menu.orders.caption"));
        ordersMenu.setMnemonic(getString("menu.orders.mnemonic").charAt(0));
        addMenuItem(ordersMenu, new UnconfirmAction(this, overviewPanel));
        addMenuItem(ordersMenu, new FindPreviousUnconfirmedAction(this,
                overviewPanel));

        addMenuItem(ordersMenu, new ConfirmAction(this, overviewPanel));

        // add factionordersmenu to ordersmenu
        factionOrdersMenu = new JMenu(getString("menu.orders.all.caption"));
        factionOrdersMenu.setMnemonic(getString("menu.orders.all.mnemonic")
                .charAt(0));
        ordersMenu.add(factionOrdersMenu);

        // add factionordersmenunot to ordersmenu
        factionOrdersMenuNot = new JMenu(
                getString("menu.orders.allnot.caption"));
        factionOrdersMenuNot.setMnemonic(getString(
                "menu.orders.allnot.mnemonic").charAt(0));
        ordersMenu.add(factionOrdersMenuNot);

        // add factionordersmenu to ordersmenu
        invertAllOrdersConfirmation = new JMenu(
                getString("menu.orders.invert.caption"));
        invertAllOrdersConfirmation.setMnemonic(getString(
                "menu.orders.invert.mnemonic").charAt(0));
        ordersMenu.add(invertAllOrdersConfirmation);

        updateConfirmMenu();

        return ordersMenu;
    }

    private void refillChangeFactionConfirmation(JMenu aMenu,
            int aConfirmationType) {
        if (aMenu.getItemCount() == 0) {
            // fill basic faction "all units"
            addMenuItem(aMenu, new ChangeFactionConfirmationAction(this, null,
                    aConfirmationType, false));
            addMenuItem(aMenu, new ChangeFactionConfirmationAction(this, null,
                    aConfirmationType, true));
        } else {
            JMenuItem one = aMenu.getItem(0);
            JMenuItem two = aMenu.getItem(1);
            aMenu.removeAll();
            aMenu.add(one);
            aMenu.add(two);
        }

        if (getData() != null) {
            // add all privileged factions
            for (Iterator iter = getData().factions().values().iterator(); iter
                    .hasNext();) {
                Faction f = (Faction) iter.next();

                if ((f.isPrivileged()) && !f.units().isEmpty()) {
                    aMenu.add(new ChangeFactionConfirmationAction(this, f,
                            aConfirmationType, false));
                    aMenu.add(new ChangeFactionConfirmationAction(this, f,
                            aConfirmationType, true));
                }
            }
        }
    }

    protected JMenu createMapMenu() {
        JMenu map = new JMenu(getString("menu.map.caption"));
        map.setMnemonic(getString("menu.map.mnemonic").charAt(0));
        addMenuItem(map, new SetOriginAction(this));
        addMenuItem(map, new IslandAction(this));
        addMenuItem(map, new MapSaveAction(this, mapPanel));
        map.addSeparator();
        addMenuItem(map, new SelectAllAction(this));
        addMenuItem(map, new SelectNothingAction(this));
        addMenuItem(map, new InvertSelectionAction(this));
        addMenuItem(map, new SelectIslandsAction(this));
        addMenuItem(map, new FillSelectionAction(this));
        addMenuItem(map, new ExpandSelectionAction(this));
        map.addSeparator();
        addMenuItem(map, new OpenSelectionAction(this));
        addMenuItem(map, new AddSelectionAction(this));
        addMenuItem(map, new SaveSelectionAction(this));

        return map;
    }

    protected JMenu createBookmarkMenu() {
        JMenu bookmarks = new JMenu(getString("menu.bookmarks.caption"));
        bookmarks.setMnemonic(getString("menu.bookmarks.mnemonic").charAt(0));

        JMenuItem toggle = new JMenuItem(
                getString("menu.bookmarks.toggle.caption"));
        toggle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,
                KeyEvent.CTRL_MASK));
        toggle.addActionListener(new ToggleBookmarkAction());
        bookmarks.add(toggle);

        JMenuItem forward = new JMenuItem(
                getString("menu.bookmarks.forward.caption"));
        forward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        forward.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookmarkManager.jumpForward();
            }
        });
        bookmarks.add(forward);

        JMenuItem backward = new JMenuItem(
                getString("menu.bookmarks.backward.caption"));
        backward.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,
                KeyEvent.SHIFT_MASK));
        backward.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookmarkManager.jumpBackward();
            }
        });
        bookmarks.add(backward);

        JMenuItem show = new JMenuItem(getString("menu.bookmarks.show.caption"));
        show.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,
                KeyEvent.ALT_MASK));
        show.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookmarkManager.showDialog(Client.this);
            }
        });
        bookmarks.add(show);

        JMenuItem clear = new JMenuItem(
                getString("menu.bookmarks.clear.caption"));
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookmarkManager.clearBookmarks();
            }
        });
        bookmarks.add(clear);

        return bookmarks;
    }

    protected JMenu createExtrasMenu() {
        JMenu extras = new JMenu(getString("menu.extras.caption"));
        extras.setMnemonic(getString("menu.extras.mnemonic").charAt(0));
        addMenuItem(extras, new FactionStatsAction(this));
        addMenuItem(extras, new ArmyStatsAction(this));
        addMenuItem(extras, new TradeOrganizerAction(this));

        // add external modules if some can be found
        JMenuItem menuItems[] = getExternalModuleItems();

        if (menuItems.length > 0) {
            extras.addSeparator();
        }

        for (int i = 0; i < menuItems.length; i++) {
            if (menuItems[i] != null) {
                extras.add(menuItems[i]);
            }
        }

        if (menuItems.length > 0) {
            extras.addSeparator();
        }

        addMenuItem(extras, new TaskTableAction(this));
        addMenuItem(extras, new ECheckAction(this));
        addMenuItem(extras, new VorlageAction(this));
        extras.addSeparator();
        addMenuItem(extras, new RepaintAction(this));
        addMenuItem(extras, new TileSetAction(this, mapPanel));
        extras.addSeparator();
        preferencesAdapterList = CollectionFactory.createArrayList(8);
        preferencesAdapterList.add(this);
        preferencesAdapterList.add(desktop);
        preferencesAdapterList.add(overviewPanel);
        preferencesAdapterList.add(detailsPanel);
        preferencesAdapterList.add(mapPanel);
        preferencesAdapterList.add(messagePanel);
        preferencesAdapterList
                .add(new IconAdapterFactory(nodeWrapperFactories));
        preferencesAdapterList
                .add(new ResourceSettingsFactory(getProperties()));
        optionAction = new OptionAction(this, preferencesAdapterList);
        addMenuItem(extras, optionAction);

        // TODO(pavkovic): currently EresseaOptionPanel is broken, I deactivated
        // it.
        extras.addSeparator();
        addMenuItem(extras, new HelpAction(this));
        addMenuItem(extras, new TipOfTheDayAction(this));
        extras.addSeparator();
        addMenuItem(extras, new InfoAction(this));

        return extras;
    }

    private Collection externalModules = null;

    /**
     * delivers all externalModules
     */
    public synchronized Collection getExternalModules() {
        if (externalModules == null) {
            externalModules = new ArrayList();

            Collection c = ExternalModuleLoader
                    .getExternalModuleClasses(getProperties());
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                Class foundClass = (Class) iter.next();
                try {

                    // get it's constructor
                    Object externalModule = foundClass.getConstructor(
                            new Class[] {}).newInstance(new Object[] {});

                    if (externalModule instanceof ExternalModule
                            || externalModule instanceof ExternalModule2) {
                        // register as SelectionListener if applicable
                        if (externalModule instanceof SelectionListener) {
                            getDispatcher().addSelectionListener(
                                    (SelectionListener) externalModule);
                        }

                        externalModules.add(externalModule);
                    }
                } catch (IllegalAccessException e) {
                    log.error(e);
                } catch (NoSuchMethodException e) {
                    log.error(e);
                } catch (InstantiationException e) {
                    log.error(e);
                } catch (InvocationTargetException e) {
                    log.error(e);
                }
            }
        }
        return externalModules;

    }

    private Collection externalModules3 = null;
    
    /**
     * delivers all externalModules3
     */
    public synchronized Collection getExternalModules3() {
        if (externalModules3 == null) {
            externalModules3 = new ArrayList();

            Collection c = ExternalModule3Loader
                    .getExternalModuleClasses(getProperties());
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                Class foundClass = (Class) iter.next();
                try {

                    // get it's constructor
                    Object externalModule3 = foundClass.getConstructor(
                            new Class[] {}).newInstance(new Object[] {});

                    if (externalModule3 instanceof ExternalModule3) {
                        // do not register as SelectionListener if applicable
                        externalModules3.add(externalModule3);
                    }
                } catch (IllegalAccessException e) {
                    log.error(e);
                } catch (NoSuchMethodException e) {
                    log.error(e);
                } catch (InstantiationException e) {
                    log.error(e);
                } catch (InvocationTargetException e) {
                    log.error(e);
                }
            }
        }
        return externalModules3;
    }
    
    /**
     * Retrieves the menu items for the external modules.
     * 
     * @see com.eressea.extern.ExternalModule2 and
     * @see com.eressea.extern.ExternalModuleLoader for documentation.
     * 
     * @return TODO: DOCUMENT ME!
     */
    private JMenuItem[] getExternalModuleItems() {
        List menuItems = CollectionFactory.createArrayList();

        for (Iterator iter = getExternalModules().iterator(); iter.hasNext();) {
            Object externalModule = iter.next();

            // get menuString
            String menuString = null;

            if (externalModule instanceof ExternalModule) {
                ExternalModule module = (ExternalModule) externalModule;
                menuString = module.getMenuItemName();
            }

            if (externalModule instanceof ExternalModule2) {
                ExternalModule2 module = (ExternalModule2) externalModule;
                menuString = module.getMenuItemName();
            }

            if (menuString != null) {
                // create menu item
                JMenuItem item = new JMenuItem(new ExternalModuleAction(this,
                        menuString, externalModule));
                menuItems.add(item);
            }
        }

        return (JMenuItem[]) menuItems.toArray(new JMenuItem[] {});
    }

    private class ToggleBookmarkAction implements ActionListener,
            SelectionListener {
        private Object activeObject;

        /**
         * Creates a new ToggleBookmarkAction object.
         */
        public ToggleBookmarkAction() {
            getDispatcher().addSelectionListener(this);
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param se
         *            TODO: DOCUMENT ME!
         */
        public void selectionChanged(SelectionEvent se) {
            activeObject = se.getActiveObject();
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param e
         *            TODO: DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent e) {
            bookmarkManager.toggleBookmark(activeObject);
        }
    }

    /**
     * Adds a new menu item to the specifie menu associating it with the
     * specified action, setting its mnemonic and registers its accelerator if
     * it has one.
     * 
     * @param parentMenu
     *            TODO: DOCUMENT ME!
     * @param action
     *            TODO: DOCUMENT ME!
     * 
     * @return the menu item created.
     */
    private JMenuItem addMenuItem(JMenu parentMenu, MenuAction action) {
        JMenuItem item = parentMenu.add(action);
        item.setMnemonic(action.getMnemonic());

        if (action.getAccelerator() != null) {
            DesktopEnvironment.registerActionListener(action.getAccelerator(),
                    action);
            item.setAccelerator(action.getAccelerator());
        }

        new MenuActionObserver(item, action);

        return item;
    }

    // ////////////////////
    // START & END Code //
    // ////////////////////
    /**
     * START & END Code
     * 
     * @param args TODO: DOCUMENT ME!
     */
    public static void main(String args[]) {
        try {
            String report = null; // the report to be loaded on startup
            File fileDir = null; // the directory to store ini files and
                                    // stuff in

            /* set the stderr to stdout while there is no log attached */
            System.setErr(System.out);

            // Fiete 20061208
            // set finalizer prio to max
            com.eressea.util.MemoryManagment.setFinalizerPriority(Thread.MAX_PRIORITY);
            
            // initialize start window
            Icon startIcon = null;

            try {
                URL url = ClassLoader.getSystemClassLoader().getResource(
                        "res/images/about/magellan.gif");

                if (url != null) {
                    startIcon = new ImageIcon(url);
                }
            } catch (Exception exc) {
                log.info("Could not find start image.");
            }

            try {
                startBundle = ResourceBundle
                        .getBundle("res/lang/com-eressea-demo-clientstart");
            } catch (RuntimeException exc) {
                log.info(startBundle);
            }

            startWindow = new StartWindow(startIcon, 5);

            startWindow.setVisible(true);

            startWindow.progress(0, (startBundle != null) ? startBundle
                    .getString("0") : "Initializing Magellan...");

            /* determine default value for files directory */
            fileDir = MagellanFinder.findMagellanDirectory();

            File settFileDir = null;

            /* process command line parameters */
            int i = 0;

            while (i < args.length) {
                if (args[i].toLowerCase().startsWith("-log")) {
                    String level = null;

                    if (args[i].toLowerCase().startsWith("-log=")
                            && (args[i].length() > 5)) {
                        level = args[i].charAt(5) + "";
                    } else if (args[i].equals("-log")
                            && (args.length > (i + 1))) {
                        i++;
                        level = args[i];
                    }

                    if (level != null) {
                        level = level.toUpperCase();
                        log.info("Client.main: Set logging to " + level);
                        Logger.setLevel(level);

                        if ("A".equals(level)) {
                            log
                                    .awt("Start logging of awt events to awtdebug.txt.");
                        }
                    }
                } else if (args[i].equals("-d") && (args.length > (i + 1))) {
                    i++;

                    try {
                        File tmpFile = new File(args[i]).getCanonicalFile();

                        if (tmpFile.exists() && tmpFile.isDirectory()
                                && tmpFile.canWrite()) {
                            fileDir = tmpFile;
                        } else {
                            log
                                    .info("Client.main(): the specified files directory does not "
                                            + "exist, is not a directory or is not writeable.");
                        }
                    } catch (Exception e) {
                        log
                                .error(
                                        "Client.main(): the specified files directory is invalid.",
                                        e);
                    }
                } else {
                    if (args[i].toLowerCase().endsWith(".cr")
                            || args[i].toLowerCase().endsWith(".bz2")
                            || args[i].toLowerCase().endsWith(".zip")) {
                        report = args[i];
                    }
                }

                i++;
            }

            settFileDir = MagellanFinder.findSettingsDirectory(fileDir,
                    settFileDir);

            // tell the user where we expect ini files and errors.txt
            log.info("Client.main(): directory used for ini files: "
                    + settFileDir.toString());

            // now redirect stderr through our log
           
            Log LOG = new Log(fileDir);
            System.setErr(LOG.getPrintStream());

            log.warn("Start writing error file with encoding " + LOG.encoding
					+ ", log level " + Logger.getLevel(Logger.getLevel()));
            
            
            String version = VersionInfo.getVersion();
            if (version==null){
            	log.warn("no magellan version available");
            } else {
            	log.warn("This is Magellan Version " + version);
            }
            
            try {
            	log.warn("OS: "+System.getProperty("os.name")+" "+System.getProperty("os.arch")+" "+System.getProperty("os.version"));
            	log.warn("Java Version: "+System.getProperty("java.version")+" "+System.getProperty("java.vendor"));
            	log.warn("Java Spec: "+System.getProperty("java.specification.version")+" "+System.getProperty("java.specification.vendor")+" "+System.getProperty("java.specification.name"));
            	log.warn("VM Version: "+System.getProperty("java.vm.version")+" "+System.getProperty("java.vm.vendor")+" "+System.getProperty("java.vm.name"));
            	log.warn("VM Specification: "+System.getProperty("java.vm.specification.version")+" "+System.getProperty("java.vm.specification.vendor")+" "+System.getProperty("java.vm.specification.name"));
            	log.warn("Java Class Version: "+System.getProperty("java.class.version"));
            } catch (SecurityException e) {
                log.warn("Unable to retrieve system properties: "+e);
           }
            
            // can't call loadRules from here, so we initially work with an
            // empty ruleset.
            // This is not very nice, though...
            GameData data = new MissingData();

            // new CompleteData(new com.eressea.rules.Eressea(), "void");
            Client c = new Client(data, fileDir, settFileDir);

            if (report != null) {
                startWindow.progress(4, (startBundle != null) ? startBundle
                        .getString("4") : "Loading game data...");

                File crFile = new File(report);

                c.dataFile = crFile;

                // load new data
                c.setData(c.loadCR(crFile));
            }

            c.setReportChanged(false);

            startWindow.progress(5, (startBundle != null) ? startBundle
                    .getString("5") : "Opening work-space...");
            c.setAllVisible(true);
            startWindow.setVisible(false);
            startWindow.dispose();
            startWindow = null;

            // show tip of the day window
            if (c.getProperties().getProperty("TipOfTheDay.showTips", "true")
                    .equals("true")
                    || c.getProperties().getProperty("TipOfTheDay.firstTime",
                            "true").equals("true")) {
                TipOfTheDay totd = new TipOfTheDay(c, c.getProperties());

                if (totd.doShow()) {
                    totd.setVisible(true);
                    totd.showNextTip();
                }
            }
        } catch (Throwable exc) { // any fatal error
            log.error(exc); // print it so it can be written to errors.txt

            // try to create a nice output
            String out = null;

            if (startBundle != null) {
                try {
                    out = startBundle.getString("error") + " "
                            + startBundle.getString(exc.getClass().getName());

                    if (out == null) {
                        out = startBundle.getString("error") + " "
                                + startBundle.getString("error.other");
                    }

                    if (out == null) {
                        out = startBundle.getString("error") + " "
                                + exc.toString();
                    }
                } catch (Exception exc2) {
                }
            }

            if (out == null) {
                out = "A fatal error occured: " + exc.toString();
            }

            log.error(out, exc);
            JOptionPane.showMessageDialog(new JFrame(), out);
            System.exit(1);
        }
    }

    /**
     * Asks the user whether the current report should be saved and does so if
     * necessary. Called before open and close actions.
     * 
     * @return whether the action shall be continued (user did not press cancel
     *         button)
     */
    public boolean askToSave() {
        if (reportState.isStateChanged()) {
            String msg = null;

            if (dataFile != null) {
                Object msgArgs[] = { dataFile.getAbsolutePath() };
                msg = (new MessageFormat(
                        getString("msg.quit.confirmsavefile.text")))
                        .format(msgArgs);
            } else {
                msg = getString("msg.quit.confirmsavenofile.text");
            }

            switch (JOptionPane.showConfirmDialog(this, msg,
                    getString("msg.quit.confirmsave.title"),
                    JOptionPane.YES_NO_CANCEL_OPTION)) {
            case JOptionPane.YES_OPTION:

                try {
                    saveAction.actionPerformed(null);
                } catch (Exception e) {
                    log.error(e);

                    return false;
                }

                break;

            case JOptionPane.CANCEL_OPTION:
                return false;
            }
        }

        return true;
    }

    /**
     * This method should be called before the application is terminated in
     * order to store GUI settings etc.
     * 
     * @param storeSettings
     *            store the settings to magellan.ini if
     *            <code>storeSettings</code> is <code>true</code>.
     */
    public void quit(boolean storeSettings) {
        if (reportState.isStateChanged()) {
            if (!askToSave()) {
                return; // cancel or exception
            }
        }

        saveExtendedState();
        this.setVisible(false);

        for (Iterator iter = panels.iterator(); iter.hasNext();) {
            InternationalizedDataPanel p = (InternationalizedDataPanel) iter
                    .next();
            p.quit();
        }

        NameGenerator.quit();

        fileHistory.storeFileHistory();

        // store settings to file
        if (storeSettings) {
            // save the desktop
            desktop.save();

            try {
                // if necessary, use settings file in local directory
                File settingsFile = new File(settingsDirectory, "magellan.ini");

                if (settingsFile.exists()) {
                    try {
                        File backup = FileBackup.create(settingsFile);
                        log.info("Created backupfile " + backup);
                    } catch (IOException ie) {
                        log.warn("Could not create backupfile for file "
                                + settingsFile);
                    }
                }

                log.info("Storing Magellan configuration to " + settingsFile);

                getProperties().store(new FileOutputStream(settingsFile), "");
            } catch (IOException ioe) {
                log.error(ioe);
            }
        }

        System.exit(0);
    }

    // //////////////////
    // GAME DATA Code //
    // //////////////////

    /**
     * DOCUMENT ME!
     * 
     * @param fileName
     *            The file name to be loaded.
     * 
     * @return a new <tt>GameData</tt> object filled with the data from the
     *         CR.
     */
    public GameData loadCR(File fileName) {
        GameData d = null;

        try {
            d = new GameDataReader().readGameData(FileTypeFactory.singleton()
                    .createFileType(fileName, true,
                            new ClientFileTypeChooser(this)));
            everLoadedReport = true;
        } catch (FileTypeFactory.NoValidEntryException e) {
            JOptionPane.showMessageDialog(this,
                    getString("msg.loadcr.missingcr.text.1") + fileName
                            + getString("msg.loadcr.missingcr.text.2"),
                    getString("msg.loadcr.error.title"),
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception exc) {
            // here we also catch RuntimeExceptions on purpose!
            // } catch (IOException exc) {
            JOptionPane.showMessageDialog(this,
                    getString("msg.loadcr.error.text") + exc.toString(),
                    getString("msg.loadcr.error.title"),
                    JOptionPane.ERROR_MESSAGE);
            log.error(exc);
        }
       
        if (d.outOfMemory){
        	JOptionPane.showMessageDialog(this,
                    getString("msg.outofmemory.text"),
                    getString("msg.outofmemory.title"),
                    JOptionPane.ERROR_MESSAGE);
            log.error(getString("msg.outofmemory.text"));
        }
        return d;
    }

	/**
	 * Sets the origin of this client's data to newOrigin.
	 *
	 * 
	 * @param newOrigin The region in the GameData that is going to be the new origin
	 */
	public void setOrigin(CoordinateID newOrigin) {
		GameData newData=null;
		try {
			newData = (GameData) getData().clone(newOrigin);
		} catch (final CloneNotSupportedException e) {
			e.printStackTrace();
		}

		if(newData != null) {
			setData(newData);
			setReportChanged(false);
		}
	}
	

    /**
     * Callbacks of FileTypeFactory are handled by this object. Right now it
     * returns the first ZipEntry to mimic old cr loading behaviour for zip
     * files.
     */
    private static class ClientFileTypeChooser extends
            FileTypeFactory.FileTypeChooser {
        Client client;

        /**
         * Creates a new ClientFileTypeChooser object.
         * 
         * @param client
         *            the parent Client object
         */
        public ClientFileTypeChooser(Client client) {
            this.client = client;
        }

        /** 
         * open selection window to choose a zipentry
         * 
         * @see com.eressea.io.file.FileTypeFactory.FileTypeChooser#chooseZipEntry(java.util.zip.ZipEntry[])
         */
        public ZipEntry chooseZipEntry(ZipEntry entries[]) {
            String stringEntries[] = new String[entries.length];

            for (int i = 0; i < entries.length; i++) {
                stringEntries[i] = entries[i].toString();
            }

            Object selected = JOptionPane.showInputDialog(client.getRootPane(),
                    client.getString("msg.loadcr.multiplezipentries.text"),
                    client.getString("msg.loadcr.multiplezipentries.title"),
                    JOptionPane.QUESTION_MESSAGE, null, stringEntries,
                    stringEntries[0]);

            if (selected == null) {
                return null;
            }

            for (int i = 0; i < entries.length; i++) {
                if (selected.equals(entries[i].toString())) {
                    return entries[i];
                }
            }

            return null;
        }
    }

    /**
     * Do some additional checks after loading a report.
     * 
     * @param aData
     *            the currently loaded game data
     */
    private void postProcessLoadedCR(GameData aData) {
        // show a warning if no password is set for any of the privileged
        // factions
        boolean privFacsWoPwd = true;

        if ((aData != null) && (aData.factions() != null)) {
            for (Iterator factions = aData.factions().values().iterator(); factions
                    .hasNext();) {
                Faction f = (Faction) factions.next();

                if (f.password == null) {
                    // take password from settings but only if it is not an
                    // empty string
                    String pwd = getProperties().getProperty(
                            "Faction.password."
                                    + ((EntityID) f.getID()).intValue(), null);

                    if ((pwd != null) && !pwd.equals("")) {
                        f.password = pwd;
                    }
                }

                // now check whether this faction has a password and eventually
                // set Trustlevel
                if ((f.password != null) && !f.trustLevelSetByUser) {
                    f.trustLevel = Faction.TL_PRIVILEGED;
                }

                if (f.password != null) {
                    privFacsWoPwd = false;
                }

                // check messages whether the password was changed
                if (f.messages != null) {
                    for (Iterator iter = f.messages.iterator(); iter.hasNext();) {
                        Message m = (Message) iter.next();

                        // check message id (new and old)
                        if ((m.getMessageType() != null)
                                && ((((IntegerID) m.getMessageType().getID())
                                        .intValue() == 1784377885) || (((IntegerID) m
                                        .getMessageType().getID()).intValue() == 19735))) {
                            // this message indicates that the password has been
                            // changed
                            if (m.attributes != null) {
                                String value = (String) m.attributes
                                        .get("value");

                                // if the password in the message is valid and
                                // does not match
                                // the password already set anyway set it for
                                // the faction and in the settings
                                if (value != null) {
                                    String password = value;

                                    if (!password.equals("")
                                            && !password.equals(f.password)) {
                                        // ask user for confirmation to take new
                                        // password from message
                                        Object msgArgs[] = { f.toString() };

                                        if (JOptionPane
                                                .showConfirmDialog(
                                                        getRootPane(),
                                                        (new java.text.MessageFormat(
                                                                getString("msg.postprocessloadedcr.acceptnewpassword.text")))
                                                                .format(msgArgs),
                                                        getString("msg.postprocessloadedcr.acceptnewpassword.title"),
                                                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                            f.password = password;

                                            if (!f.trustLevelSetByUser) { // password
                                                                            // set
                                                f.trustLevel = Faction.TL_PRIVILEGED;
                                            }

                                            privFacsWoPwd = false;

                                            if (getProperties() != null) {
                                                getProperties()
                                                        .setProperty(
                                                                "Faction.password."
                                                                        + ((EntityID) f
                                                                                .getID())
                                                                                .intValue(),
                                                                f.password);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // recalculate default-trustlevels after CR-Load
            TrustLevels.recalculateTrustLevels(aData);

            if (privFacsWoPwd) { // no password set for any faction
                JOptionPane
                        .showMessageDialog(
                                getRootPane(),
                                getString("msg.postprocessloadedcr.missingpassword.text"));
            }
        }
    }

    // ///////////////
    // UPDATE Code //
    // ///////////////
    private void updateTitleCaption() {
        String title = createTitle(getData(), showStatus, false);

        try {
            title = createTitle(getData(), showStatus, true);
        } catch (Exception e) {
            log.error("createTitle failed!", e);
        }

        setTitle(title);
    }

    private String createTitle(GameData data, boolean showStatusOverride,
            boolean longTitle) {
        // set frame title (date)
        String title = "Magellan";

        String version = VersionInfo.getVersion();

        if (version != null) {
            title += (" " + version);
        }

        // pavkovic 2002.05.7: data may be null in this situation
        if (data == null) {
            return title;
        }

        if (data.filetype != null) {
            String file;

            try {
                file = data.filetype.getFile().toString();
            } catch (IOException e) {
                file = data.filetype.toString();
            }

            file = file.substring(file.lastIndexOf(File.separator) + 1);
            title += (" [" + file + "]");
        }

        if (data.getDate() != null) {
            title = title
                    + " - "
                    + data.getDate().toString(
                            showStatusOverride ? EresseaDate.TYPE_SHORT
                                    : EresseaDate.TYPE_PHRASE_AND_SEASON)
                    + " (" + data.getDate().getDate() + ")";
        }

        if (!longTitle) {
            return title;
        }

        if (showStatusOverride) {
            int units = 0;
            int done = 0;

            for (Iterator iter = data.units().values().iterator(); iter
                    .hasNext();) {
                Unit u = (Unit) iter.next();

                if (u.getFaction().isPrivileged()) {
                    units++;

                    if (u.ordersConfirmed) {
                        done++;
                    }
                }

                // also count temp units
                for (Iterator iter2 = u.tempUnits().iterator(); iter2.hasNext();) {
                    Unit u2 = (Unit) iter2.next();

                    if (u2.getFaction().isPrivileged()) {
                        units++;

                        if (u2.ordersConfirmed) {
                            done++;
                        }
                    }
                }
            }

            if (units > 0) {
                BigDecimal percent = (new BigDecimal(( done * 100)
                        / ((float) units))).setScale(2, BigDecimal.ROUND_DOWN);
                title += (" (" + units + " " + getString("title.unit") + ", "
                        + done + " " + getString("title.done") + ", "
                        + getString("title.thatare") + " " + percent + " "
                        + getString("title.percent") + ")");
            }
        }

        return title;
    }

    /**
     * Updates the order confirmation menu after the game data changed.
     */
    private void updateConfirmMenu() {
        refillChangeFactionConfirmation(factionOrdersMenu,
                ChangeFactionConfirmationAction.SETCONFIRMATION);
        refillChangeFactionConfirmation(factionOrdersMenuNot,
                ChangeFactionConfirmationAction.REMOVECONFIRMATION);
        refillChangeFactionConfirmation(invertAllOrdersConfirmation,
                ChangeFactionConfirmationAction.INVERTCONFIRMATION);
    }

    /**
     * Called after GameData changes. Also called via EventDispatcher thread to ensure graphical changes do occur.
     */
    private void updatedGameData() {
        updateTitleCaption();
        updateConfirmMenu();

        if (getData().getCurTempID() == -1) {
            String s = getProperties().getProperty(
                    "ClientPreferences.TempIDsInitialValue", "");

            try {
                getData().setCurTempID(
                        "".equals(s) ? 0 : Integer.parseInt(s, getData().base));
            } catch (java.lang.NumberFormatException nfe) {
            }
        }

        // pavkovic 2004.01.04:
        // this method behaves at if the gamedata has been loaded by this
        // method.
        // this is not true at all but true enough for our needs.
        // dispatcher.fire(new GameDataEvent(this, data));
        getDispatcher().fire(new GameDataEvent(this, getData(), true));
        // also inform system about the new selection found in the GameData
        // object
        getDispatcher().fire(
                new SelectionEvent(this, getData()
                        .getSelectedRegionCoordinates().values(), null,
                        SelectionEvent.ST_REGIONS));

    }

    // ////////////
    // L&F Code //
    // ////////////
    /**
     * @param laf
     */
    public void setLookAndFeel(String laf) {
        boolean lafSet = true;

        if (MagellanLookAndFeel.equals(laf)
                && laf.equals(getProperties().getProperty("Client.lookAndFeel",
                        ""))) {
            lafSet = false;
        } else {
            lafSet = MagellanLookAndFeel.setLookAndFeel(laf);

            if (!lafSet) {
                laf = "Metal";
                lafSet = MagellanLookAndFeel.setLookAndFeel("Metal");
            }
        }

        if (laf.equals("Metal")) {
            MagellanLookAndFeel.loadBackground(getProperties());
        }

        if (!lafSet) {
            return;
        }

        updateLaF();

        getProperties().setProperty("Client.lookAndFeel", laf);
    }

    /**
     * TODO: DOCUMENT ME!
     */
    public void updateLaF() {
        // call updateUI in MagellanDesktop
        if (desktop != null) {
            desktop.updateLaF();
        }

        // call updateUI on preferences
        if (optionAction != null) {
            optionAction.updateLaF();
        }
    }

    private void initLookAndFeels() {
        setLookAndFeel(getProperties().getProperty("Client.lookAndFeel",
                "Metal"));
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public String[] getLookAndFeels() {
        return (String[]) MagellanLookAndFeel.getLookAndFeelNames().toArray(
                new String[] {});
    }

    // ///////////////////
    // HISTORY methods //
    // ///////////////////

    /**
     * Adds a single file to the file history.
     * 
     * @param f
     *            TODO: DOCUMENT ME!
     */
    public void addFileToHistory(File f) {
        fileHistory.addFileToHistory(f);
    }

    /**
     * Returns the maximum number of entries in the history of loaded files.
     * 
     * @return TODO: DOCUMENT ME!
     */
    public int getMaxFileHistorySize() {
        return fileHistory.getMaxFileHistorySize();
    }

    /**
     * Allows to set the maximum number of files appearing in the file history.
     * 
     * @param size
     *            TODO: DOCUMENT ME!
     */
    public void setMaxFileHistorySize(int size) {
        fileHistory.setMaxFileHistorySize(size);
    }

    // ///////////////////
    // PROPERTY Access //
    // ///////////////////
    /**
     * Changes to the report state can be done here. Normally, a change
     * is recognized by the following events.
     *
     * @param changed
     */
    public void setReportChanged(boolean changed) {
        if (changed == false) {
            // for call from FileSaveAsAction
            updateTitleCaption();
        }

        reportState.setStateChanged(changed);
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public boolean isReportChanged() {
        return reportState.isStateChanged();
    }

    /**
     * Get the selected Regions. The returned map can be empty but is never
     * null. This is a wrapper function so we dont need to give away
     * MapperPanel.
     * 
     * @return TODO: DOCUMENT ME!
     */
    public Map getSelectedRegions() {
        return mapPanel.getSelectedRegions();
    }

    /**
     * Get the Level on the mapper Panel. This is a wrapper function so we dont
     * need to give away MapperPanel.
     * 
     * @return TODO: DOCUMENT ME!
     */
    public int getLevel() {
        return mapPanel.getLevel();
    }

    // //////////////////////////
    // GENERAL ACCESS METHODS //
    // //////////////////////////

    /**
     * Returns the global settings used by Magellan.
     * 
     * @return TODO: DOCUMENT ME!
     */
    public Properties getProperties() {
        return context.getProperties();
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @param newData
     *            TODO: DOCUMENT ME!
     */
    public void setData(GameData newData) {
        context.setGameData(newData);
        postProcessLoadedCR(newData);
        
        if (newData!=null && PropertiesHelper.getboolean(getProperties(), "map.creating.void", false)) {
        	newData.postProcessTheVoid();
        }
        
        getDispatcher().fire(new GameDataEvent(this, newData));
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {        
                Client.this.updatedGameData();
            }});
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public GameData getData() {
        return context.getGameData();
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public MagellanDesktop getDesktop() {
        return desktop;
    }

    /**
     * 
     * @return TODO: DOCUMENT ME!
     */
    public EventDispatcher getDispatcher() {
        return context.getEventDispatcher();
    }

    /**
     * Returns the directory the local copy of Magellan is inside.
     * 
     * @return TODO: DOCUMENT ME!
     */
    public static File getMagellanDirectory() {
        return filesDirectory;
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public static File getSettingsDirectory() {
        return settingsDirectory;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return the BookmarkManager associated with this Client-Object
     */
    public BookmarkManager getBookmarkManager() {
        return bookmarkManager;
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public boolean isShowingStatus() {
        return showStatus;
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @param bool
     *            TODO: DOCUMENT ME!
     */
    public void setShowStatus(boolean bool) {
        if (showStatus != bool) {
            showStatus = bool;
            getProperties().setProperty("Client.ShowOrderStatus",
                    showStatus ? "true" : "false");

            if (getData() != null) {
                updateTitleCaption();
            }
        }
    }

    // /////////////////////////////
    // INTERNATIONALIZATION Code //
    // /////////////////////////////

    /**
     * Returns a translation from the translation table for the specified key.
     * 
     * @param key
     *            TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    protected String getString(String key) {
        // this will indirectly evaluate getDefaultTranslation!
        return Translations.getTranslation(Client.class, key);
    }

    // pavkovic 2003.01.28: this is a Map of the default Translations mapped to
    // this class
    // it is called by reflection (we could force the implementation of an
    // interface,
    // this way it is more flexible.)
    // Pls use this mechanism, so the translation files can be created
    // automagically
    // by inspecting all classes.
    private static Map defaultTranslations;

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public static synchronized Map getDefaultTranslations() {
        if (defaultTranslations == null) {
            defaultTranslations = CollectionFactory.createHashtable();
            defaultTranslations.put("menu.file.caption", "File");
            defaultTranslations.put("menu.file.mnemonic", "f");
            defaultTranslations.put("menu.edit.caption", "Edit");
            defaultTranslations.put("menu.edit.mnemonic", "e");
            defaultTranslations.put("menu.orders.caption", "Orders");
            defaultTranslations.put("menu.orders.mnemonic", "r");
            defaultTranslations.put("menu.orders.all.caption", "All");
            defaultTranslations.put("menu.orders.all.mnemonic", "a");
            defaultTranslations.put("menu.orders.allnot.caption", "All not");
            defaultTranslations.put("menu.orders.allnot.mnemonic", "n");
            defaultTranslations.put("menu.orders.invert.caption", "Invert");
            defaultTranslations.put("menu.orders.invert.mnemonic", "i");
            defaultTranslations.put("shortcut.description", "Repaint all");
            defaultTranslations.put("shortcut.title", "Client");
            defaultTranslations.put("title.unit", "Units");
            defaultTranslations.put("menu.tree.caption", "Tree");
            defaultTranslations.put("menu.tree.mnemonic", "t");
            defaultTranslations.put("menu.map.caption", "Map");
            defaultTranslations.put("menu.map.mnemonic", "m");
            defaultTranslations.put("menu.extras.caption", "Extras");
            defaultTranslations.put("menu.extras.mnemonic", "x");
            defaultTranslations.put("msg.loadcr.multiplezipentries.text",
                    "Multiple computer reports have been\n"
                            + "found in the comppressed file.\n"
                            + "Please choose one.");
            defaultTranslations.put("msg.loadcr.multiplezipentries.title",
                    "Choose one");
            defaultTranslations.put("msg.loadcr.missingcr.text.1",
                    "No Computer Report was found in the compressed file \"");
            defaultTranslations.put("msg.loadcr.missingcr.text.2", "\".");
            defaultTranslations.put("msg.loadcr.error.text",
                    "While loading the report the following error occurred:\n");
            defaultTranslations.put("msg.loadcr.error.title", "Error");
            defaultTranslations
                    .put("msg.main.invalidcr.text",
                            "The file \"{0}\" cannot be opened because it does not exist.");
            defaultTranslations.put("msg.quit.confirmsavefile.text",
                    "Do you want to save changes in file \"{0}\"?");
            defaultTranslations.put("msg.quit.confirmsavenofile.text",
                    "Do you want to save changes?");
            defaultTranslations.put("title.percent", "%");
            defaultTranslations.put("title.thatare", "that are");
            defaultTranslations.put("title.done", "confirmed");
            defaultTranslations.put("msg.quit.confirmsave.title", "Save");
            defaultTranslations
                    .put(
                            "msg.postprocessloadedcr.acceptnewpassword.text",
                            "The loaded report contains a message indicating that\n"
                                    + "faction {0} has a new password set.\n"
                                    + "Do you want Magellan to use and save this new password for\n"
                                    + "exporting orders?");
            defaultTranslations.put(
                    "msg.postprocessloadedcr.acceptnewpassword.title",
                    "Accept new passwort?");
            defaultTranslations
                    .put(
                            "msg.postprocessloadedcr.missingpassword.text",
                            "None of the factions in the report has a password set.\n"
                                    + "Therefore, you cannot edit unit orders!\n"
                                    + "To edit them and avoid this message in the future open the\n"
                                    + "faction statistics dialog via the Extras menu and set a\n"
                                    + "password for your faction (The password will be stored on\n"
                                    + "your computer. It will not be included in any exported\n"
                                    + "computer report files).");
            defaultTranslations
                    .put(
                            "msg.startpatcher.text",
                            "A new version of Magellan is available. Do you want to exit\n"
                                    + "Magellan and launch the patcher to upgrade Magellan to that\n"
                                    + "latest version?");
            defaultTranslations
                    .put("msg.startpatcher.title", "Launch patcher?");
            defaultTranslations
                    .put("msg.patchererror.text",
                            "The patcher could not be started due to the following error:\n");
            defaultTranslations.put("msg.patchererror.title", "Error");
            defaultTranslations
                    .put(
                            "msg.newversion.text",
                            "A new version of Magellan is available. If the patcher was\n"
                                    + "installed (http://eressea.upb.de/magellan/download.shtml)\n"
                                    + "you could instantly upgrade Magellan to that latest version.");
            defaultTranslations.put("msg.newversion.title", "New version");
            defaultTranslations
                    .put(
                            "msg.versionunknown.text",
                            "Version information about Magellan could not be retrieved.\n"
                                    + "Make sure that you are online while Magellan tries to\n"
                                    + "determine the version information.");
            defaultTranslations.put("msg.versionunknown.title",
                    "Version unknown");
            defaultTranslations.put("menu.bookmarks.caption", "Bookmarks");
            defaultTranslations.put("menu.bookmarks.mnemonic", "b");
            defaultTranslations.put("menu.bookmarks.toggle.caption",
                    "Toggle bookmark");
            defaultTranslations.put("menu.bookmarks.forward.caption",
                    "Jump forward");
            defaultTranslations.put("menu.bookmarks.backward.caption",
                    "Jump backward");
            defaultTranslations.put("menu.bookmarks.show.caption", "Show list");
            defaultTranslations
                    .put("menu.bookmarks.clear.caption", "Clear all");
            defaultTranslations.put("msg.outofmemory.text","Java VM is running out of memory.");
            defaultTranslations.put("msg.outofmemory.title","Out Of Memory.");
        }

        return defaultTranslations;
    }

    // /////////////////////////////
    // REPAINT & VISIBILITY Code //
    // /////////////////////////////
    /**
     * @param v
     */
    public void setAllVisible(boolean v) {
        desktop.setAllVisible(v);
        resetExtendedState();
    }

    private void saveExtendedState() {
        getProperties().setProperty("Client.extendedState",
                String.valueOf(JVMUtilities.getExtendedState(this)));
    }

    private void resetExtendedState() {
        int state = new Integer(getProperties().getProperty(
                "Client.extendedState", "-1")).intValue();

        if (state != -1) {
            JVMUtilities.setExtendedState(this, state);
        }
    }

    // The repaint functions are overwritten to repaint the whole Magellan
    // Desktop. This is necessary because of the desktop mode FRAME.
    /**
     * @see java.awt.Component#repaint()
     */
    public void repaint() {
        super.repaint();

        if (desktop != null) {
            desktop.repaintAllComponents();
        }
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @param millis
     *            TODO: DOCUMENT ME!
     */
    public void repaint(int millis) {
        super.repaint(millis);

        if (desktop != null) {
            desktop.repaintAllComponents();
        }
    }

    // /////////////////
    // SHORTCUT Code //
    // /////////////////

    /**
     * Empty because registered directly.
     * 
     * @return TODO: DOCUMENT ME!
     */
    public Iterator getShortCuts() {
        return null; // not used - we register directly with a KeyStroke
    }

    /**
     * Repaints the client.
     * 
     * @param shortcut
     *            TODO: DOCUMENT ME!
     */
    public void shortCut(javax.swing.KeyStroke shortcut) {
        desktop.repaintAllComponents();
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @param stroke
     *            TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public String getShortcutDescription(Object stroke) {
        return getString("shortcut.description");
    }

    /**
     * TODO: DOCUMENT ME!
     * 
     * @return TODO: DOCUMENT ME!
     */
    public String getListenerDescription() {
        return getString("shortcut.title");
    }

    /**
     * Returns an adapter for the preferences of this class.
     * 
     * @return TODO: DOCUMENT ME!
     */
    public PreferencesAdapter createPreferencesAdapter() {
        return new ClientPreferences(getProperties(), this);
    }

    // /////////////////
    // INNER Classes //
    // /////////////////
    private class MenuActionObserver implements PropertyChangeListener {
        protected JMenuItem item;

        /**
         * Creates a new MenuActionObserver object.
         * 
         * @param item
         *            TODO: DOCUMENT ME!
         * @param action
         *            TODO: DOCUMENT ME!
         */
        public MenuActionObserver(JMenuItem item, Action action) {
            this.item = item;
            action.addPropertyChangeListener(this);
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param e
         *            TODO: DOCUMENT ME!
         */
        public void propertyChange(PropertyChangeEvent e) {
            if ((e.getPropertyName() != null)
                    && e.getPropertyName().equals("accelerator")) {
                item.setAccelerator((KeyStroke) e.getNewValue());
            }
        }
    }

    /**
     * Simple class to look for events changing the data.
     */
    protected class ReportObserver implements GameDataListener,
            OrderConfirmListener, TempUnitListener, UnitOrdersListener {
        protected boolean stateChanged = false;

        protected long lastClear;

        /**
         * Creates a new ReportObserver object.
         */
        public ReportObserver(EventDispatcher e) {

            e.addGameDataListener(this);
            e.addOrderConfirmListener(this);
            e.addTempUnitListener(this);
            e.addUnitOrdersListener(this);

            lastClear = -1;
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @return TODO: DOCUMENT ME!
         */
        public boolean isStateChanged() {
            return stateChanged;
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param newState
         *            TODO: DOCUMENT ME!
         */
        public void setStateChanged(boolean newState) {
            stateChanged = newState;

            if (!newState) {
                lastClear = System.currentTimeMillis();
            }
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param e
         *            TODO: DOCUMENT ME!
         */
        public void orderConfirmationChanged(OrderConfirmEvent e) {
            if ((getData() != null) && isShowingStatus()) {
                updateTitleCaption();
            }

            if (lastClear < e.getTimestamp()) {
                stateChanged = true;
            }
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param e
         *            TODO: DOCUMENT ME!
         */
        public void tempUnitCreated(TempUnitEvent e) {
            if (lastClear < e.getTimestamp()) {
                stateChanged = true;
            }
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param e
         *            TODO: DOCUMENT ME!
         */
        public void tempUnitDeleted(TempUnitEvent e) {
            if (lastClear < e.getTimestamp()) {
                stateChanged = true;
            }
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param e
         *            TODO: DOCUMENT ME!
         */
        public void gameDataChanged(GameDataEvent e) {
            if ((lastClear < e.getTimestamp()) && (e.getGameData() != null)) {
                stateChanged = true;
            } else {
                stateChanged = false;
            }
        }

        /**
         * TODO: DOCUMENT ME!
         * 
         * @param e
         *            TODO: DOCUMENT ME!
         */
        public void unitOrdersChanged(UnitOrdersEvent e) {
            if (lastClear < e.getTimestamp()) {
                stateChanged = true;
            }
        }
    }
    
    public Collection getSelectedObjects(){
    	return overviewPanel.getSelectedObjects();
    }


}
