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

package com.eressea.swing.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import com.eressea.Border;
import com.eressea.Faction;
import com.eressea.Group;
import com.eressea.Island;
import com.eressea.Item;
import com.eressea.Potion;
import com.eressea.Region;
import com.eressea.Skill;
import com.eressea.Unit;
import com.eressea.UnitContainer;

import com.eressea.swing.context.ContextChangeable;
import com.eressea.swing.context.ContextObserver;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;

import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class NodeWrapperFactory extends JTabbedPane implements PreferencesFactory, ContextObserver {
	private static final Logger log = Logger.getInstance(NodeWrapperFactory.class);

	/** TODO: DOCUMENT ME! */
	public static final int BORDER = 0;

	/** TODO: DOCUMENT ME! */
	public static final int FACTION = 1;

	/** TODO: DOCUMENT ME! */
	public static final int ISLAND = 2;

	/** TODO: DOCUMENT ME! */
	public static final int REGION = 3;

	/** TODO: DOCUMENT ME! */
	public static final int UNITCONTAINER = 4;

	/** TODO: DOCUMENT ME! */
	public static final int UNIT = 5;

	/** TODO: DOCUMENT ME! */
	public static final int POTION = 6;

	/** TODO: DOCUMENT ME! */
	public static final int ITEM = 7;

	/** TODO: DOCUMENT ME! */
	public static final int SKILL = 8;

	/** TODO: DOCUMENT ME! */
	public static final int GROUP = 9;

	/** TODO: DOCUMENT ME! */
	public static final int SIMPLE = 10;
	protected Properties settings;
	protected boolean initialized[];
	protected NodeWrapperDrawPolicy adapters[];
	protected JTabbedPane tabs;
	protected String initString = null;
	protected String title = null;
	protected JMenu contextMenu;
	protected TreeUpdate source;

	/**
	 * Creates new NodeWrapperFactory
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public NodeWrapperFactory(Properties settings) {
		this(settings, null, null);
	}

	/**
	 * Creates a new NodeWrapperFactory object.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param initString TODO: DOCUMENT ME!
	 * @param title TODO: DOCUMENT ME!
	 */
	public NodeWrapperFactory(Properties settings, String initString, String title) {
		super(JTabbedPane.LEFT);

		this.initString = initString;

		if(title == null) {
			title = getString("title.unknown");
		}

		this.title = title;
		this.settings = settings;
		initialized = new boolean[11];
		adapters = new NodeWrapperDrawPolicy[11];

		for(int i = 0; i < 11; i++) {
			initialized[i] = false;
			adapters[i] = null;
		}

		// init our Pref-Adapter interface
		initUI();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	public void setSource(TreeUpdate s) {
		source = s;
	}

	// Initializes the PreferencesAdapter GUI
	protected void initUI() {
		// (context) menu
		contextMenu = new JMenu(title);
		contextMenu.setEnabled(false);
	}

	//update the context menu
	protected void updateContextMenu() {
		contextMenu.removeAll();

		for(int i = 0; i < adapters.length; i++) {
			if((adapters[i] != null) && (adapters[i] instanceof ContextChangeable)) {
				ContextChangeable cc = (ContextChangeable) adapters[i];
				JMenuItem item = cc.getContextAdapter();

				if(item instanceof JMenu) {
					contextMenu.add(item);
				} else {
					JMenu help = new JMenu(adapters[i].getTitle());
					help.add(item);
					contextMenu.add(help);
				}

				cc.setContextObserver(this);
			}
		}

		contextMenu.setEnabled(true);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JMenu getContextMenu() {
		return contextMenu;
	}

	// initialize a CellObject by category index
	protected void init(CellObject co, int index) {
		NodeWrapperDrawPolicy o = null;

		if(initString == null) {
			o = co.init(settings, adapters[index]);
		} else {
			o = co.init(settings, initString, adapters[index]);
		}

		if(!initialized[index] && (o != null)) {
			adapters[index] = o;
			initialized[index] = true;

			if(o instanceof ContextChangeable) {
				updateContextMenu();
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param b TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BorderNodeWrapper createBorderNodeWrapper(Border b) {
		BorderNodeWrapper bnw = new BorderNodeWrapper(b);
		init(bnw, BORDER);

		return bnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param f TODO: DOCUMENT ME!
	 * @param r TODO: DOCUMENT ME!
	 * @param alliances TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public FactionNodeWrapper createFactionNodeWrapper(Faction f, Region r, Map alliances) {
		FactionNodeWrapper fnw = new FactionNodeWrapper(f, r, alliances);
		init(fnw, FACTION);

		return fnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public IslandNodeWrapper createIslandNodeWrapper(Island i) {
		IslandNodeWrapper inw = new IslandNodeWrapper(i);
		init(inw, ISLAND);

		return inw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionNodeWrapper createRegionNodeWrapper(Region r) {
		RegionNodeWrapper rnw = new RegionNodeWrapper(r);
		init(rnw, REGION);

		return rnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param amount TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionNodeWrapper createRegionNodeWrapper(Region r, int amount) {
		RegionNodeWrapper rnw = new RegionNodeWrapper(r, amount);
		init(rnw, REGION);

		return rnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param uc TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitContainerNodeWrapper createUnitContainerNodeWrapper(UnitContainer uc) {
		UnitContainerNodeWrapper ucnw = new UnitContainerNodeWrapper(uc);
		init(ucnw, UNITCONTAINER);

		return ucnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitNodeWrapper createUnitNodeWrapper(Unit unit) {
		return createUnitNodeWrapper(unit, null, -1, -1);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param num TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitNodeWrapper createUnitNodeWrapper(Unit unit, int num) {
		return createUnitNodeWrapper(unit, null, num, -1);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param num TODO: DOCUMENT ME!
	 * @param mod TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitNodeWrapper createUnitNodeWrapper(Unit unit, int num, int mod) {
		return createUnitNodeWrapper(unit, null, num, mod);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param prfx TODO: DOCUMENT ME!
	 * @param num TODO: DOCUMENT ME!
	 * @param mod TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitNodeWrapper createUnitNodeWrapper(Unit unit, String prfx, int num, int mod) {
		UnitNodeWrapper unw = new UnitNodeWrapper(unit, prfx, num, mod);
		init(unw, UNIT);

		return unw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param text TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitNodeWrapper createUnitNodeWrapper(Unit unit, String text) {
		UnitNodeWrapper unw = new UnitNodeWrapper(unit, text);
		init(unw, UNIT);

		return unw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param potion TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PotionNodeWrapper createPotionNodeWrapper(Potion potion) {
		PotionNodeWrapper pnw = new PotionNodeWrapper(potion);
		init(pnw, POTION);

		return pnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param potion TODO: DOCUMENT ME!
	 * @param postfix TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PotionNodeWrapper createPotionNodeWrapper(Potion potion, String postfix) {
		PotionNodeWrapper pnw = new PotionNodeWrapper(potion, postfix);
		init(pnw, POTION);

		return pnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param item TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemNodeWrapper createItemNodeWrapper(Item item) {
		return createItemNodeWrapper(null, item);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param item TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemNodeWrapper createItemNodeWrapper(Unit unit, Item item) {
		ItemNodeWrapper inw = new ItemNodeWrapper(unit, item);
		init(inw, ITEM);

		return inw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param skill TODO: DOCUMENT ME!
	 * @param modSkill TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillNodeWrapper createSkillNodeWrapper(Unit unit, Skill skill, Skill modSkill) {
		SkillNodeWrapper snw = new SkillNodeWrapper(unit, skill, modSkill);
		init(snw, SKILL);

		return snw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param group TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public GroupNodeWrapper createGroupNodeWrapper(Group group) {
		GroupNodeWrapper gnw = new GroupNodeWrapper(group);
		init(gnw, GROUP);

		return gnw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param icons TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SimpleNodeWrapper createSimpleNodeWrapper(Object text, Object icons) {
		SimpleNodeWrapper snw = new SimpleNodeWrapper(text, icons);
		init(snw, SIMPLE);

		return snw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param icons TODO: DOCUMENT ME!
	 * @param clipValue TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SimpleNodeWrapper createSimpleNodeWrapper(Object text, Object icons, String clipValue) {
		SimpleNodeWrapper snw = new SimpleNodeWrapper(text, icons, clipValue);
		init(snw, SIMPLE);

		return snw;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void contextDataChanged() {
		log.info("Context data changed. " + source);

		if(source != null) {
			source.updateTree(this);
		}
	}

	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static final Map defaultTranslations = CollectionFactory.createHashtable();

	static {
		defaultTranslations.put("title.unknown", "Some Icons");
		defaultTranslations.put("wrappers.title", "Icon type");
		defaultTranslations.put("wrappers.none", "No selection");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}

	/////////////////////////////
	// PreferencesAdapter code //
	/////////////////////////////
	public PreferencesAdapter createPreferencesAdapter() {
		return new NodeWrapperFactoryPreferences(adapters);
	}

	class NodeWrapperFactoryPreferences extends JTabbedPane implements PreferencesAdapter {
		List myAdapters;

		protected NodeWrapperFactoryPreferences(NodeWrapperDrawPolicy adapters[]) {
			myAdapters = CollectionFactory.createLinkedList();

			for(int i = 0; i < adapters.length; i++) {
				if(adapters[i] != null) {
					PreferencesAdapter pref = adapters[i].createPreferencesAdapter();

					if(pref != null) {
						addTab(pref.getTitle(), pref.getComponent());
						myAdapters.add(pref);
					}
				}
			}

			// try to enforce only one column
			setPreferredSize(null);

			java.awt.Dimension dim = getPreferredSize();
			int tabHeight = getTabCount() * 30; // just approximate since there are no public functions :-(

			if(dim.height < tabHeight) {
				dim.height = tabHeight;
				setPreferredSize(dim);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			Iterator it = myAdapters.iterator();

			while(it.hasNext()) {
				PreferencesAdapter pref = (PreferencesAdapter) it.next();
				pref.applyPreferences();
			}
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
		public String getTitle() {
			return title;
		}
	}
}
