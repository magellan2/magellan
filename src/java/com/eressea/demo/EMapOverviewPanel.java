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

package com.eressea.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.eressea.Alliance;
import com.eressea.Building;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Group;
import com.eressea.HasRegion;
import com.eressea.ID;
import com.eressea.Island;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.TempUnit;
import com.eressea.Unit;
import com.eressea.UnitID;
import com.eressea.ZeroUnit;

import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.demo.desktop.ShortcutListener;

import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.OrderConfirmEvent;
import com.eressea.event.OrderConfirmListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.event.TempUnitEvent;
import com.eressea.event.TempUnitListener;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.event.UnitOrdersListener;

import com.eressea.relation.TransferRelation;

import com.eressea.rules.SkillType;

import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.MenuProvider;
import com.eressea.swing.context.UnitContainerContextFactory;
import com.eressea.swing.context.UnitContextFactory;
import com.eressea.swing.preferences.ExtendedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.swing.tree.BorderNodeWrapper;
import com.eressea.swing.tree.CellRenderer;
import com.eressea.swing.tree.ContextManager;
import com.eressea.swing.tree.CopyTree;
import com.eressea.swing.tree.FactionNodeWrapper;
import com.eressea.swing.tree.GroupNodeWrapper;
import com.eressea.swing.tree.IslandNodeWrapper;
import com.eressea.swing.tree.NodeWrapperFactory;
import com.eressea.swing.tree.RegionNodeWrapper;
import com.eressea.swing.tree.SimpleNodeWrapper;
import com.eressea.swing.tree.TreeHelper;
import com.eressea.swing.tree.TreeUpdate;
import com.eressea.swing.tree.UnitContainerNodeWrapper;
import com.eressea.swing.tree.UnitNodeWrapper;

import com.eressea.util.CollectionFactory;
import com.eressea.util.SelectionHistory;
import com.eressea.util.comparator.BestSkillComparator;
import com.eressea.util.comparator.FactionTrustComparator;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.NameComparator;
import com.eressea.util.comparator.RegionIslandComparator;
import com.eressea.util.comparator.SkillComparator;
import com.eressea.util.comparator.SkillTypeComparator;
import com.eressea.util.comparator.SkillTypeRankComparator;
import com.eressea.util.comparator.SortIndexComparator;
import com.eressea.util.comparator.TopmostRankedSkillComparator;
import com.eressea.util.comparator.UnitCombatStatusComparator;
import com.eressea.util.comparator.UnitFactionComparator;
import com.eressea.util.comparator.UnitFactionDisguisedComparator;
import com.eressea.util.comparator.UnitGroupComparator;
import com.eressea.util.comparator.UnitHealthComparator;
import com.eressea.util.comparator.UnitSkillComparator;
import com.eressea.util.comparator.UnitTempUnitComparator;
import com.eressea.util.comparator.UnitTrustComparator;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EMapOverviewPanel extends InternationalizedDataPanel
	implements TreeSelectionListener, TreeExpansionListener, SelectionListener,
			   OrderConfirmListener, PreferencesFactory, TempUnitListener,
			   ShortcutListener, ChangeListener, TreeUpdate, MenuProvider
{
	private static final Logger log = Logger.getInstance(EMapOverviewPanel.class);

	// GUI elements
	private JSplitPane			   sppTreeHistory = null;
	private CopyTree			   tree		  = null;
	private DefaultTreeModel	   treeModel  = null;
	private DefaultMutableTreeNode rootNode   = null;
	private JScrollPane			   scpTree    = null;
	private JList				   lstHistory = null;
	private JScrollPane			   scpHistory;

	// node maps, mapping the object ids to their tree nodes
	private Map regionNodes   = CollectionFactory.createHashtable();
	private Map unitNodes     = CollectionFactory.createHashtable();
	private Map buildingNodes = CollectionFactory.createHashtable();
	private Map shipNodes     = CollectionFactory.createHashtable();

	// relation map, mapping unit's id to its relations
	private Map     unitRelations		 = CollectionFactory.createHashMap();
	private boolean ignoreTreeSelections = false;

	// region with previously selected item
	private Region previousRegion  = null;
	private Object activeObject    = null;
	private List   selectedObjects = CollectionFactory.createLinkedList();

	// needed by FactionNodeWrapper to determine the active alliances
	// keys: FactionIDs, values: Alliance-objects
	private Map activeAlliances = CollectionFactory.createHashtable();

	// This flag is added out of performance reasons
	// to avoid unnecessary updates to the activeAlliances map
	private boolean activeAlliancesAreDefault = false;

	// shortcuts
	private List shortcuts;

	// factory for tree NodeWrapper
	private NodeWrapperFactory nodeWrapperFactory;

	// the context menu manager
	private ContextManager contextManager;

	// tree expand/collapse properties

	/** TODO: DOCUMENT ME! */
	public static final int EXPAND_FLAG = 1;

	/** TODO: DOCUMENT ME! */
	public static final int EXPAND_IFINSIDE_FLAG = 2;
	private int			    expandMode		 = EXPAND_FLAG | (3 << 2);
	private int			    expandTrustlevel = Faction.TL_PRIVILEGED;

	/** TODO: DOCUMENT ME! */
	public static final int COLLAPSE_FLAG = 1;

	/** TODO: DOCUMENT ME! */
	public static final int		    COLLAPSE_ONLY_EXPANDED = 2;
	private int					    collapseMode = COLLAPSE_FLAG |
												   COLLAPSE_ONLY_EXPANDED |
												   (3 << 2);
	private List				    lastExpanded	  = CollectionFactory.createLinkedList();
	private Set					    collapsedNodes    = CollectionFactory.createHashSet();
	private Set					    collapseInfo	  = CollectionFactory.createHashSet();
	private Set					    expandInfo		  = CollectionFactory.createHashSet();
	private Set					    selectionTransfer = CollectionFactory.createHashSet();
	private static final Comparator idCmp			  = new IDComparator();
	private static final Comparator nameCmp			  = new NameComparator(idCmp);

	/**
	 * Creates a new EMapOverviewPanel object.
	 *
	 * @param d TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public EMapOverviewPanel(EventDispatcher d, Properties p) {
		super(d, p);

		loadExpandProperties();
		loadCollapseProperty();

		nodeWrapperFactory = new NodeWrapperFactory(settings,
													"EMapOverviewPanel",
													getString("wrapperfactory.title"));
		nodeWrapperFactory.setSource(this);

		// to get the pref-adapter
		nodeWrapperFactory.createUnitNodeWrapper(new Unit(UnitID.createUnitID(0)));

		SelectionHistory.addListener(this);
		d.addSelectionListener(this);
		d.addOrderConfirmListener(this);
		d.addTempUnitListener(this);

		// create tree and add it to this panel within a scroll pane
		rootNode  = new DefaultMutableTreeNode(null);
		treeModel = new DefaultTreeModel(rootNode);
		tree	  = new CopyTree(treeModel, dispatcher);
		tree.setRootVisible(false);

		// pavkovic 2005.05.28: enable handle for root nodes
		tree.setShowsRootHandles(true);
		tree.setScrollsOnExpand(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		// set the tree to have a large model (almost always much data...)
		tree.setLargeModel(true);

		tree.addTreeSelectionListener(this);
		tree.addTreeExpansionListener(this);

		ToolTipManager.sharedInstance().registerComponent(tree);
		scpTree = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
								  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// tree uses different cell renderer
		tree.setCellRenderer(new CellRenderer(settings));

		// init context manager with different node wrappers
		contextManager = new ContextManager(tree);
		contextManager.putSimpleObject(UnitNodeWrapper.class,
									   new UnitContextFactory());

		UnitContainerContextFactory conMenu = new UnitContainerContextFactory(settings);
		contextManager.putSimpleObject(FactionNodeWrapper.class, conMenu);
		contextManager.putSimpleObject(RegionNodeWrapper.class, conMenu);
		contextManager.putSimpleObject(UnitContainerNodeWrapper.class, conMenu);

		// history list
		lstHistory = new JList();
		lstHistory.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if(!e.getValueIsAdjusting()) {
						dispatcher.fire(new SelectionEvent(lstHistory, null,
														   lstHistory.getSelectedValue()));
					}
				}
			});
		SelectionHistory.ignoreSource(lstHistory);
		scpHistory = new JScrollPane(lstHistory,
									 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// split pane
		sppTreeHistory = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scpTree,
										scpHistory);
		sppTreeHistory.setOneTouchExpandable(true);
		sppTreeHistory.setDividerLocation(Integer.parseInt(settings.getProperty("EMapOverviewPanel.treeHistorySplit",
																				"100")));

		// add components to this panel
		this.setLayout(new GridLayout(1, 0));
		this.add(sppTreeHistory);

		// initialize shortcuts
		shortcuts = CollectionFactory.createArrayList(8);

		// 0-1: Focus
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_MASK));
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.ALT_MASK));

		// 2-4: Other CTRL shortcuts
		//shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_MASK));
		//shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_B,KeyEvent.CTRL_MASK));
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,
											 KeyEvent.CTRL_MASK));
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD,
											 KeyEvent.CTRL_MASK));

		// 5-6: Other ALT shortcuts
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
											 KeyEvent.CTRL_MASK));
		shortcuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
											 KeyEvent.CTRL_MASK));

		// register for shortcuts
		DesktopEnvironment.registerShortcutListener(this);

		// register order change listener
		d.addUnitOrdersListener(new UnitWrapperUpdater());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperFactory getNodeWrapperFactory() {
		return nodeWrapperFactory;
	}

	/**
	 * Returns the component displaying the overview tree.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getOverviewComponent() {
		return scpTree;
	}

	/**
	 * Returns the component displaying the history.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getHistoryComponent() {
		return scpHistory;
	}

	/**
	 * GameDataChanged event handler routine updating the tree. Note: It is
	 * significant for the valueChanged() method, how deep the different node
	 * types are nested.
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		this.data = e.getGameData();

		contextManager.setGameData(this.data);

		rebuildTree();

		// initialize activeAlliances-Map
		setDefaultAlliances();
	}

	protected void rebuildTree() {
		// clear the history
		SelectionHistory.clear();
		lstHistory.setListData(SelectionHistory.getHistory().toArray());

		// clear node maps
		regionNodes.clear();
		unitNodes.clear();
		buildingNodes.clear();
		shipNodes.clear();
		rootNode.removeAllChildren();

		// clear relation map
		unitRelations.clear();

		// clear other buffers
		selectedObjects.clear();
		activeObject = null;
		lastExpanded.clear();

		if((data != null) && (data.regions() != null)) {
			// preprocess regions to have relations
			// pavkovic 2003.09.24: TODO: this takes ages
			for(Iterator iter = data.regions().values().iterator();
					iter.hasNext();) {
				((Region) iter.next()).refreshUnitRelations();
			}
		}

		// initialize variables used in while loop
		boolean createIslandNodes = ((new Boolean(settings.getProperty("EMapOverviewPanel.displayIslands",
																	   "true"))).booleanValue() == true) &&
									((new Boolean(settings.getProperty("EMapOverviewPanel.sortRegions",
																	   "true"))).booleanValue() == true) &&
									(settings.getProperty("EMapOverviewPanel.sortRegionsCriteria",
														  "coordinates")
											 .equalsIgnoreCase("islands") == true);

		if(treeBuilder == null) {
			createTreeBuilder();
		}

		treeBuilder.setMode((treeBuilder.getMode() & 16383) |
							(createIslandNodes ? treeBuilder.CREATE_ISLANDS : 0));

		// creation of Comparator outsourced to Comparator getUnitSorting(java.util.Properties)
		treeBuilder.setUnitComparator(getUnitSorting(settings));
		treeBuilder.setTreeStructure(getTreeStructure(settings));

		treeBuilder.buildTree(rootNode, data);

		treeModel.reload();
	}

	/**
	 * Retrieve a Comparator to sort the units according to the settings.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Comparator getUnitSorting(Properties settings) {
		// create Comparator used for unit sorting
		String criteria = settings.getProperty("EMapOverviewPanel.sortUnitsCriteria",
											   "skills");

		// used as the comparator on the lowest structure level
		Comparator cmp = null;

		if(criteria.equals("skills")) {
			if(settings.getProperty("EMapOverviewPanel.useBestSkill", "true")
						   .equalsIgnoreCase("true")) {
				cmp = new UnitSkillComparator(new BestSkillComparator(SkillComparator.skillCmp,
																	  new SkillTypeComparator(new SkillTypeRankComparator(new NameComparator(null),
																														  settings),
																							  SkillComparator.skillCmp),
																	  null),
											  idCmp);
			} else {
				cmp = new UnitSkillComparator(new TopmostRankedSkillComparator(null,
																			   settings),
											  idCmp);
			}
		} else if(criteria.equals("names")) {
			cmp = nameCmp;
		} else {
			cmp = new SortIndexComparator(idCmp);
		}

		// get an array of ints out of the definition string:
		int treeStructure[] = getTreeStructure(settings);

		// now build the Comparator used for unit sorting
		int		   i    = treeStructure.length - 1;
		Comparator help = null;

		while(i >= 0) {
			switch(treeStructure[i]) {
			case TreeHelper.FACTION:
				help = new UnitFactionComparator(new FactionTrustComparator(nameCmp),
												 cmp);

				break;

			case TreeHelper.GROUP:
				help = new UnitGroupComparator(idCmp, cmp, cmp);

				break;

			case TreeHelper.COMBAT_STATUS:
				help = new UnitCombatStatusComparator(cmp);

				break;

			case TreeHelper.HEALTH:
				help = new UnitHealthComparator(cmp);

				break;

			case TreeHelper.FACTION_DISGUISE_STATUS:
				help = new UnitFactionDisguisedComparator(cmp);

				break;

			case TreeHelper.TRUSTLEVEL:
				help = new UnitTrustComparator(cmp);

				break;

			default:
				help = cmp;
			}

			cmp = help;
			i--;
		}

		// care for temp units
		cmp = new UnitTempUnitComparator(idCmp, cmp);

		return cmp;
	}

	/**
	 * Retrieve the structure of the unit tree according to the settings
	 *
	 * @param settings TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static int[] getTreeStructure(Properties settings) {
		String criteria = settings.getProperty("EMapOverviewPanel.treeStructure",
											   " " + TreeHelper.FACTION + " " +
											   TreeHelper.GROUP);
		StringTokenizer tokenizer = new StringTokenizer(criteria);
		int    treeStructure[]    = new int[tokenizer.countTokens()];
		int    i				  = 0;

		while(tokenizer.hasMoreTokens()) {
			try {
				String s = tokenizer.nextToken();
				treeStructure[i] = Integer.parseInt(s);
				i++;
			} catch(NumberFormatException e) {
			}
		}

		//		System.out.print("treeStructure: ");
		//		for (i = 0; i < treeStructure.length; i++) {
		//			System.out.print(treeStructure[i] + " ");
		//		}
		//		System.out.println("");
		return treeStructure;
	}

	/**
	 * TreeSelection event handler, notifies event listeners.
	 *
	 * @param tse TODO: DOCUMENT ME!
	 */
	public void valueChanged(TreeSelectionEvent tse) {
		if(ignoreTreeSelections == true) {
			return;
		}

		DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		TreePath			   paths[] = tse.getPaths();

		// search for nodes which have been selected/deselected because of collapse
		DefaultMutableTreeNode node    = null;
		boolean				   removed = false;

		for(int i = 0; i < paths.length; i++) {
			if(selectionTransfer.contains(paths[i])) {
				removed = true;

				break;
			}
		}

		if(removed) { // some selection transfer detected
			selectionTransfer.clear();

			return;

			/* Note: This is perhaps not the best way because there may be
			 *       other nodes than that from collapse. But "normally"
			 *       this shouldn't happen.
			 */
		}

		/**
		 * DETERMINE ACTIVE OBJECT :
		 */
		node = firstNode;

		if(node == null) {
			return; // there may be an inconsistent state with active object
		} else {
			Object o = node.getUserObject();

			// region node selected?
			if(o instanceof RegionNodeWrapper) {
				activeObject   = ((RegionNodeWrapper) o).getRegion();
				previousRegion = ((RegionNodeWrapper) o).getRegion();

				if(!activeAlliancesAreDefault) {
					setDefaultAlliances();
					tree.repaint();
				}
			} else if(o instanceof FactionNodeWrapper) {
				// faction node selected?
				activeObject = ((FactionNodeWrapper) o).getFaction();

				Region selectedRegion = getSelectedRegion(node);

				if((selectedRegion != null) &&
					   (selectedRegion.equals(previousRegion) == false)) {
					previousRegion = selectedRegion;

					// fire to make clear we have another region
					dispatcher.fire(new SelectionEvent(this, null,
													   selectedRegion));
				}

				Faction f = (Faction) activeObject;

				if((f.allies == null) && (activeAlliances.size() > 0)) {
					activeAlliances.clear();
					activeAlliancesAreDefault = false;
					tree.repaint();
				} else if((f.allies != null) &&
							  !activeAlliances.equals(f.allies)) {
					activeAlliances.clear();
					activeAlliances.putAll(f.allies);
					activeAlliancesAreDefault = false;

					// add the selected faction to be able to show, on whose alliances
					// the current colors of the icons of a faction node depend
					activeAlliances.put(f.getID(),
										new Alliance(f, Integer.MAX_VALUE));
					tree.repaint();
				}
			} else if(o instanceof GroupNodeWrapper) {
				// group node selected?
				Group g = ((GroupNodeWrapper) o).getGroup();
				activeObject = g;

				Region selectedRegion = getSelectedRegion(node);

				if((selectedRegion != null) &&
					   !selectedRegion.equals(previousRegion)) {
					previousRegion = selectedRegion;

					// fire to make clear we have another region
					dispatcher.fire(new SelectionEvent(this, null,
													   selectedRegion));
				}

				if((g.allies() == null) &&
					   (activeAlliances.values().size() > 0)) {
					activeAlliances.clear();
					activeAlliancesAreDefault = false;
					tree.repaint();
				} else if((g.allies() != null) &&
							  !activeAlliances.equals(g.allies())) {
					activeAlliances.clear();
					activeAlliancesAreDefault = false;
					activeAlliances.putAll(g.allies());

					// add the group's faction to be able to show, on whose alliances
					// the current colors of the icons of a faction node depend
					if(g.getFaction() == null) {
						log.warn("Found group without set faction: " +
								 g.getName());
					} else {
						activeAlliances.put(g.getFaction().getID(),
											new Alliance(g.getFaction(),
														 Integer.MAX_VALUE));
					}

					tree.repaint();
				}
			} else if(o instanceof UnitNodeWrapper) {
				// unit node selected?
				activeObject = ((UnitNodeWrapper) o).getUnit();

				Region selectedRegion = getSelectedRegion(node);

				if((selectedRegion != null) &&
					   !selectedRegion.equals(previousRegion)) {
					previousRegion = selectedRegion;

					// fire to make clear we have another region
					dispatcher.fire(new SelectionEvent(this, null,
													   selectedRegion));
				}

				/*
				Region selectedRegion = ((Unit)activeObject).getRegion();
				if (selectedRegion != null && selectedRegion.equals(previousRegion) == false) {
				    previousRegion = selectedRegion;

				    }*/
				Group g = ((Unit) activeObject).getGroup();

				if(g == null) {
					Faction f = ((Unit) activeObject).getFaction();

					if((f.allies == null) &&
						   (activeAlliances.values().size() > 0)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						tree.repaint();
					} else if((f.allies != null) &&
								  !activeAlliances.equals(f.allies)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						activeAlliances.putAll(f.allies);

						// add the selected faction to be able to show, on whose alliances
						// the current colors of the icons of a faction node depend
						activeAlliances.put(f.getID(),
											new Alliance(f, Integer.MAX_VALUE));
						tree.repaint();
					}
				} else {
					if((g.allies() == null) &&
						   (activeAlliances.values().size() > 0)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						tree.repaint();
					} else if((g.allies() != null) &&
								  !g.allies().equals(activeAlliances)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						activeAlliances.putAll(g.allies());

						// add the group's faction to be able to show, on whose alliances
						// the current colors of the icons of a faction node depend
						if(g.getFaction() == null) {
							log.warn("Found group without set faction: " +
									 g.getName());
						} else {
							activeAlliances.put(g.getFaction().getID(),
												new Alliance(g.getFaction(),
															 Integer.MAX_VALUE));
						}

						tree.repaint();
					}
				}
			} else if(o instanceof UnitContainerNodeWrapper) {
				// building node selected?
				if(((UnitContainerNodeWrapper) o).getUnitContainer() instanceof Building ||
					   ((UnitContainerNodeWrapper) o).getUnitContainer() instanceof Ship) {
					activeObject = ((UnitContainerNodeWrapper) o).getUnitContainer();

					Region selectedRegion = ((HasRegion) activeObject).getRegion();

					if((selectedRegion != null) &&
						   (selectedRegion.equals(previousRegion) == false)) {
						previousRegion = selectedRegion;

						// fire to make clear we have another region
						dispatcher.fire(new SelectionEvent(this, null,
														   selectedRegion));
					}
				}

				if(!activeAlliancesAreDefault) {
					setDefaultAlliances();
					tree.repaint();
				}
			} else if(o instanceof BorderNodeWrapper) {
				// border node selected?
				activeObject = ((BorderNodeWrapper) o).getBorder();

				Region selectedRegion = getSelectedRegion(node);

				if((selectedRegion != null) &&
					   (selectedRegion.equals(previousRegion) == false)) {
					previousRegion = selectedRegion;

					// fire to make clear we have another region
					dispatcher.fire(new SelectionEvent(this, null,
													   selectedRegion));
				}

				if(!activeAlliancesAreDefault) {
					setDefaultAlliances();
					tree.repaint();
				}
			} else if(o instanceof IslandNodeWrapper) {
				// island node selected?
				activeObject = ((IslandNodeWrapper) o).getIsland();

				if(!activeAlliancesAreDefault) {
					setDefaultAlliances();
					tree.repaint();
				}
			} else if(o instanceof SimpleNodeWrapper) {
				activeObject = null;

				Region selectedRegion = getSelectedRegion(node);

				if((selectedRegion != null) &&
					   (selectedRegion.equals(previousRegion) == false)) {
					previousRegion = selectedRegion;

					// fire to make clear we have another region
					dispatcher.fire(new SelectionEvent(this, null,
													   selectedRegion));
				}

				// not a very smart implementation
				// this is basically a workaround for those nodes
				// that represent health- or combat-status.
			}
		}

		/**
		 * UPDATE SELECTED OBJECTS :
		 */
		for(int i = 0; i < paths.length; i++) {
			if(paths[i] == null) {
				continue;
			}

			TreePath path = paths[i];
			node = (DefaultMutableTreeNode) path.getLastPathComponent();

			Object o = node.getUserObject();

			if(o instanceof RegionNodeWrapper) {
				o = ((RegionNodeWrapper) o).getRegion();
			} else if(o instanceof FactionNodeWrapper) {
				o = ((FactionNodeWrapper) o).getFaction();
			} else if(o instanceof GroupNodeWrapper) {
				o = ((GroupNodeWrapper) o).getGroup();
			} else if(o instanceof UnitNodeWrapper) {
				o = ((UnitNodeWrapper) o).getUnit();
			} else if(o instanceof UnitContainerNodeWrapper) {
				o = ((UnitContainerNodeWrapper) o).getUnitContainer();
			} else if(o instanceof BorderNodeWrapper) {
				o = ((BorderNodeWrapper) o).getBorder();
			} else if(o instanceof IslandNodeWrapper) {
				o = ((IslandNodeWrapper) o).getIsland();
			} else if(o instanceof SimpleNodeWrapper) {
				o = ((SimpleNodeWrapper) o).getText();
			} else {
				log.warn("EMapOverviewPanel.valueChanged() : Type of the user object of a selected node is unknown. Please report to http://eressea.upb.de/magellan/bugs/ :" +
						 o.toString());
			}

			if(tse.isAddedPath(path)) {
				selectedObjects.add(o);
			} else {
				selectedObjects.remove(o);
			}
		}

		dispatcher.fire(new SelectionEvent(this, selectedObjects, activeObject));
	}

	private Region getSelectedRegion(DefaultMutableTreeNode node) {
		while((node != null) &&
				  !(node.getUserObject() instanceof RegionNodeWrapper)) {
			node = (DefaultMutableTreeNode) node.getParent();
		}

		if((node != null) && node.getUserObject() instanceof RegionNodeWrapper) {
			return ((RegionNodeWrapper) node.getUserObject()).getRegion();
		} else {
			return null;
		}
	}

	/**
	 * Event handler for TreeExpansionEvents (recenters the tree if necessary)
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void treeExpanded(TreeExpansionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath()
																.getLastPathComponent();
		Rectangle			   rec = tree.getPathBounds(new TreePath(node.getPath()));

		if(node.getChildCount() > 0) {
			TreePath path = new TreePath(((DefaultMutableTreeNode) node.getLastChild()).getPath());

			if(path != null) {
				Rectangle newRect = tree.getPathBounds(path);

				if(newRect != null) {
					rec.add(newRect);
				}
			}
		}

		SwingUtilities.invokeLater(new ScrollerRunnable(rec));
	}

	/**
	 * Event handler for TreeCollapsedEvents
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void treeCollapsed(TreeExpansionEvent e) {
	}

	/**
	 * Change event handler, change the display status of the tree if an unit
	 * orderConfimation Status changed
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void orderConfirmationChanged(OrderConfirmEvent e) {
		if((e.getSource() == this) || (e.getUnits() == null) ||
			   (e.getUnits().size() == 0)) {
			return;
		}

		tree.repaint();
	}

	/**
	 * Collections all information about nodes to be collapsed.
	 */
	protected void collectCollapseInfo() {
		// only clear when not expanded-only, here use also old collapse info
		if((collapseMode & COLLAPSE_ONLY_EXPANDED) == 0) {
			collapseInfo.clear();
		}

		if((collapseMode & COLLAPSE_FLAG) != 0) {
			int depth = collapseMode >> 2;

			if((collapseMode & COLLAPSE_ONLY_EXPANDED) != 0) {
				if(lastExpanded.size() > 0) {
					collapseInfo.addAll(lastExpanded);
					lastExpanded.clear();
				}
			} else {
				Enumeration e = rootNode.children();

				while(e.hasMoreElements()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
					Object				   obj = node.getUserObject();

					if(obj instanceof IslandNodeWrapper) {
						collapseImpl(node, depth + 1, true, collapseInfo);
					} else {
						collapseImpl(node, depth, true, collapseInfo);
					}
				}
			}
		}
	}

	private void collapseImpl(DefaultMutableTreeNode node, int depth,
							  boolean add, Set infoSet) {
		if((depth > 0) && (node.getChildCount() > 0)) {
			depth--;

			for(int i = 0; i < node.getChildCount(); i++) {
				collapseImpl((DefaultMutableTreeNode) node.getChildAt(i),
							 depth, true, infoSet);
			}
		}

		// important to do this here because children look on parent's expand state
		TreePath path = new TreePath(node.getPath());

		if(tree.isExpanded(path)) {
			infoSet.add(node);
		}
	}

	protected void resetExpandInfo() {
		expandInfo.clear();
	}

	protected void addExpandInfo(DefaultMutableTreeNode node) {
		expandInfo.add(node);
	}

	protected void collectExpandInfo(Region region,
									 DefaultMutableTreeNode node, TreePath path) {
		if((expandMode & EXPAND_FLAG) != 0) {
			if((expandMode & EXPAND_IFINSIDE_FLAG) != 0) {
				// search for privileged faction
				//    note: Node searching is not nice, but the fastest way
				boolean     found = false;
				Enumeration enum = node.children();

				while(!found && enum.hasMoreElements()) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) enum.nextElement();
					Object				   obj = child.getUserObject();

					if(obj instanceof FactionNodeWrapper) {
						Faction fac = ((FactionNodeWrapper) obj).getFaction();

						if(fac.trustLevel >= expandTrustlevel) {
							found = true;
						}
					}
				}

				if(!found) {
					return;
				}
			}

			expandInfo.add(node);

			int eDepth = expandMode >> 2;

			if(eDepth > 0) {
				eDepth--;

				Enumeration enum = node.children();
				boolean     open;

				while(enum.hasMoreElements()) {
					open = false;

					DefaultMutableTreeNode child = (DefaultMutableTreeNode) enum.nextElement();
					Object				   obj = child.getUserObject();

					if(obj instanceof FactionNodeWrapper) {
						Faction fac = ((FactionNodeWrapper) obj).getFaction();

						if(fac.trustLevel >= expandTrustlevel) {
							open = true;
						}
					}

					if(open) {
						expandImpl(child, eDepth, expandInfo);
					}
				}
			}
		}
	}

	private void expandImpl(DefaultMutableTreeNode node, int depth, Set infoSet) {
		infoSet.add(node);

		if((depth > 0) && (node.getChildCount() > 0)) {
			depth--;

			for(int i = 0; i < node.getChildCount(); i++) {
				expandImpl((DefaultMutableTreeNode) node.getChildAt(i), depth,
						   infoSet);
			}
		}
	}

	protected void doExpandAndCollapse(Collection newSelection) {
		// filter collapse info that would be re-expanded
		Iterator it   = collapseInfo.iterator();
		Map		 buf1 = CollectionFactory.createHashMap();
		Map		 buf2 = CollectionFactory.createHashMap();

		while(it.hasNext()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.next();
			TreePath			   path = new TreePath(node.getPath());
			buf1.put(node, path);

			Iterator it2 = expandInfo.iterator();

			while(it2.hasNext()) {
				DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) it2.next();

				if(!buf2.containsKey(node2)) {
					buf2.put(node2, new TreePath(node2.getPath()));
				}

				TreePath path2 = (TreePath) buf2.get(node2);

				if(path.isDescendant(path2)) {
					it.remove();

					// but save it for last expand on expand-only mode
					if((collapseMode & COLLAPSE_ONLY_EXPANDED) != 0) {
						lastExpanded.add(path.getLastPathComponent());
					}

					break;
				}
			}
		}

		// now expand
		it = expandInfo.iterator();

		while(it.hasNext()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.next();
			TreePath			   path = null;

			if(buf2.containsKey(node)) {
				path = (TreePath) buf2.get(node);
			} else {
				path = new TreePath(node.getPath());
			}

			checkPathToExpand(path);
		}

		// prepare the selection transfer
		selectionTransfer.clear();

		// sort out already selected objects
		it = newSelection.iterator();

		while(it.hasNext()) {
			TreePath path = (TreePath) it.next();

			if(tree.isPathSelected(path)) {
				it.remove();
			}
		}

		selectionTransfer.addAll(newSelection);

		// transfer the selection
		TreePath paths[] = new TreePath[selectionTransfer.size()];
		it = selectionTransfer.iterator();

		int i = 0;

		while(it.hasNext()) {
			paths[i] = (TreePath) it.next();
			i++;
		}

		tree.setSelectionPaths(paths);

		// now collapse
		//   (this should'nt trigger selection events any more)
		it = collapseInfo.iterator();

		while(it.hasNext()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.next();
			TreePath			   path = (TreePath) buf1.get(node);

			if(tree.isExpanded(path)) {
				tree.collapsePath(path);
			}
		}
	}

	/**
	 * Looks a path down from root to the leaf if to expand any segment
	 *
	 * @param path TODO: DOCUMENT ME!
	 */
	protected void checkPathToExpand(TreePath path) {
		if(path.getPathCount() > 1) {
			checkPathToExpand(path.getParentPath());
		}

		if(!tree.isExpanded(path)) {
			lastExpanded.add(path.getLastPathComponent());
			tree.expandPath(path);
		}
	}

	/**
	 * Selection event handler, update the selection status of the tree. First
	 * the active object is considered and selected in the tree if contained.
	 * After that selected objects are considered, but only if != null and
	 * selection type is different to SelectionEvent.ST_REGIONS. In this case
	 * the tree selection is set to the selected objects (as long as they are
	 * contained in the tree anyway). Keep in mind, thtat this will produce
	 * the active object _NOT_ to be selected, if selectedObjects != null &&
	 * !selectedObjects.contains(activeObject) !!!
	 *
	 * @param se TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent se) {
		// update the selection in the context manager
		if(se.getSelectionType() != SelectionEvent.ST_REGIONS) {
			contextManager.setSelection(se.getSelectedObjects());
		}

		// try to prevent notification loops, i.e. that calling this
		// procedure results in a notification of the registered
		// listeners of this object
		if(se.getSource() == this) {
			return;
		}

		// clear current selection to avoid intermediate selections through
		// expand and collapse
		ignoreTreeSelections = true;
		tree.clearSelection();
		ignoreTreeSelections = false;

		collectCollapseInfo();
		resetExpandInfo();

		Collection newSel = CollectionFactory.createLinkedList();

		/** HANDLE activeObject : */
		Object o = se.getActiveObject();

		if(o != null) {
			activeObject = o;

			// The path of the selected object (if contained in the tree)
			TreePath path = null;

			// region selected?
			if(o instanceof ZeroUnit) {
				o = ((ZeroUnit) o).getRegion();
			}

			if(o instanceof Region) {
				if(!activeAlliancesAreDefault) {
					setDefaultAlliances();
					tree.repaint();
				}

				Region				   i    = (Region) o;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) regionNodes.get(i.getID());

				if(node != null) {
					path = new TreePath(node.getPath());
					newSel.add(path);
					collectExpandInfo(i, node, path);
				}
			} else
			// unit selected?
			if(o instanceof Unit) {
				Unit				   i    = (Unit) o;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) unitNodes.get(i.getID());

				if(node != null) {
					path = new TreePath(node.getPath());
					addExpandInfo(node);
				}

				Group g = i.getGroup();

				if(g == null) {
					Faction f = i.getFaction();

					if((f.allies == null) &&
						   (activeAlliances.values().size() > 0)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						tree.repaint();
					} else if((f.allies != null) &&
								  !f.allies.equals(activeAlliances)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						activeAlliances.putAll(f.allies);

						// add the selected faction to be able to show, on whose alliances
						// the current colors of the icons of a faction node depend
						activeAlliances.put(f.getID(),
											new Alliance(f, Integer.MAX_VALUE));
						tree.repaint();
					}
				} else {
					if((g.allies() == null) &&
						   (activeAlliances.values().size() > 0)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						tree.repaint();
					} else if((g.allies() != null) &&
								  !g.allies().equals(activeAlliances)) {
						activeAlliances.clear();
						activeAlliancesAreDefault = false;
						activeAlliances.putAll(g.allies());

						// add the group's faction to be able to show, on whose alliances
						// the current colors of the icons of a faction node depend
						if(g.getFaction() == null) {
							log.warn("Found group without set faction: " +
									 g.getName());
						} else {
							activeAlliances.put(g.getFaction().getID(),
												new Alliance(g.getFaction(),
															 Integer.MAX_VALUE));
						}

						tree.repaint();
					}
				}
			} else
			// ship selected?
			if(o instanceof Ship) {
				Ship				   i    = (Ship) o;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) shipNodes.get(i.getID());

				if(node != null) {
					path = new TreePath(node.getPath());
					addExpandInfo(node);
				}

				if(!activeAlliancesAreDefault) {
					setDefaultAlliances();
					tree.repaint();
				}
			} else
			// building selected?
			if(o instanceof Building) {
				Building			   i    = (Building) o;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) buildingNodes.get(i.getID());

				if(node != null) {
					path = new TreePath(node.getPath());
					addExpandInfo(node);
				}

				if(!activeAlliancesAreDefault) {
					setDefaultAlliances();
					tree.repaint();
				}
			}

			// the active object has to be selected in the tree :
			selectedObjects.clear();
			selectedObjects.add(activeObject);

			if(path != null) {
				newSel.add(path);
			}

			doExpandAndCollapse(newSel);

			if(path != null) {
				// center on the selected item if it is outside the view port
				SwingUtilities.invokeLater(new ScrollerRunnable(tree.getPathBounds(path)));
			}
		}

		/**
		 * HANDLE selectedObjects Don't change anything, if selectedObjects ==
		 * null or selection event type is ST_REGIONS (which means that this
		 * is a selection of regions on the map or of regions to be selected
		 * on the map. Keep in mind that selections on the map don't have
		 * anything to do with selections in the tree).
		 */
		if((se.getSelectedObjects() != null) &&
			   (se.getSelectionType() != SelectionEvent.ST_REGIONS)) {
			selectedObjects.clear();
			selectedObjects.addAll(se.getSelectedObjects());
			ignoreTreeSelections = true;
			tree.clearSelection();

			for(Iterator iter = selectedObjects.iterator(); iter.hasNext();) {
				o = iter.next();

				DefaultMutableTreeNode node = null;

				if(o instanceof Region) {
					node = (DefaultMutableTreeNode) regionNodes.get(((Region) o).getID());
				} else if(o instanceof Ship) {
					node = (DefaultMutableTreeNode) shipNodes.get(((Ship) o).getID());
				} else if(o instanceof Building) {
					node = (DefaultMutableTreeNode) buildingNodes.get(((Building) o).getID());
				} else if(o instanceof Unit) {
					node = (DefaultMutableTreeNode) unitNodes.get(((Unit) o).getID());
				}

				if(node != null) {
					tree.addSelectionPath(new TreePath(node.getPath()));
				}
			}

			ignoreTreeSelections = false;
		}
	}

	protected void expandSelected(Region region, DefaultMutableTreeNode node,
								  TreePath path) {
		if((expandMode & EXPAND_FLAG) != 0) {
			if((expandMode & EXPAND_IFINSIDE_FLAG) != 0) {
				// search for privileged faction
				//    note: Node searching is not nice, but the fastest way
				boolean     found = false;
				Enumeration enum = node.children();

				while(!found && enum.hasMoreElements()) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) enum.nextElement();
					Object				   obj = child.getUserObject();

					if(obj instanceof FactionNodeWrapper) {
						Faction fac = ((FactionNodeWrapper) obj).getFaction();

						if(fac.trustLevel >= expandTrustlevel) {
							found = true;
						}
					}
				}

				if(!found) {
					return;
				}
			}

			collapsedNodes.remove(node);

			if(!tree.isExpanded(path)) {
				lastExpanded.add(node);
				tree.expandPath(path);
			}

			int eDepth = expandMode >> 2;

			if(eDepth > 0) {
				Enumeration enum = node.children();
				boolean     open;

				while(enum.hasMoreElements()) {
					open = false;

					DefaultMutableTreeNode child = (DefaultMutableTreeNode) enum.nextElement();
					Object				   obj = child.getUserObject();

					if(obj instanceof FactionNodeWrapper) {
						Faction fac = ((FactionNodeWrapper) obj).getFaction();

						if(fac.trustLevel >= expandTrustlevel) {
							open = true;
						}
					}

					if(open) {
						expandImpl(child, eDepth - 1);
					}
				}
			}
		}
	}

	private void expandImpl(DefaultMutableTreeNode node, int depth) {
		TreePath path = new TreePath(node.getPath());

		if(!tree.isExpanded(path)) {
			tree.expandPath(path);
			lastExpanded.add(0, node);
		}

		collapsedNodes.remove(node);

		if((depth > 0) && (node.getChildCount() > 0)) {
			depth--;

			for(int i = 0; i < node.getChildCount(); i++) {
				expandImpl((DefaultMutableTreeNode) node.getChildAt(i), depth);
			}
		}
	}

	protected void collapseSelected() {
		if((collapseMode & COLLAPSE_FLAG) != 0) {
			int depth = collapseMode >> 2;

			if((collapseMode & COLLAPSE_ONLY_EXPANDED) != 0) {
				if(lastExpanded.size() > 0) {
					List     copy = CollectionFactory.createLinkedList(lastExpanded);
					Iterator it = copy.iterator();

					while(it.hasNext()) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) it.next();

						Collection			   col = childSelected(node, false);

						if(col != null) {
							collapsedNodes.addAll(col);
						}

						collapsedNodes.add(node);
						collapseImpl(node, 0, false);
						lastExpanded.remove(node);
					}
				}
			} else {
				Enumeration e = rootNode.children();

				while(e.hasMoreElements()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
					Object				   obj = node.getUserObject();

					if(obj instanceof IslandNodeWrapper) {
						collapseImpl(node, depth + 1, true);
					} else {
						collapseImpl(node, depth, true);
					}
				}
			}
		}
	}

	private void collapseImpl(DefaultMutableTreeNode node, int depth,
							  boolean add) {
		if((depth > 0) && (node.getChildCount() > 0)) {
			depth--;

			for(int i = 0; i < node.getChildCount(); i++) {
				collapseImpl((DefaultMutableTreeNode) node.getChildAt(i),
							 depth, true);
			}
		}

		// important to do this here because children look on parent's expand state
		TreePath path = new TreePath(node.getPath());

		if(tree.isExpanded(path)) {
			if(add && tree.isPathSelected(path)) {
				collapsedNodes.add(node);
			}

			tree.collapsePath(path);
		}
	}

	private Collection childSelected(DefaultMutableTreeNode node,
									 boolean include) {
		if(tree.isSelectionEmpty()) {
			return null;
		}

		List list = CollectionFactory.createLinkedList();

		if(include && tree.isPathSelected(new TreePath(node.getPath()))) {
			list.add(node);
		}

		Enumeration e = node.children();

		while(e.hasMoreElements()) {
			Collection col = childSelected((DefaultMutableTreeNode) e.nextElement(),
										   true);

			if(col != null) {
				list.addAll(col);
			}
		}

		if(list.size() == 0) {
			list = null;
		}

		return list;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void tempUnitCreated(TempUnitEvent e) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) unitNodes.get(e.getTempUnit()
																					.getParent()
																					.getID());

		if(parentNode != null) {
			Comparator idComp   = new IDComparator();
			List	   siblings = CollectionFactory.createLinkedList(e.getTempUnit()
																	  .getParent()
																	  .tempUnits());
			Collections.sort(siblings, idComp);

			int index = Collections.binarySearch(siblings, e.getTempUnit(),
												 idComp);

			if(index >= 0) {
				insertUnit(parentNode, e.getTempUnit(), index);
			} else {
				log.info("EMapOverviewPanel.tempUnitCreated(): new temp unit is not a child of its parent!");
			}
		} else {
			log.info("EMapOverviewPanel.tempUnitCreated(): cannot determine parent node of temp unit!");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void tempUnitDeleted(TempUnitEvent e) {
		TempUnit			   t	    = e.getTempUnit();
		DefaultMutableTreeNode unitNode = (DefaultMutableTreeNode) unitNodes.get(t.getID());
		unitNodes.remove(t.getID());

		//DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)unitNode.getParent();
		Object o = tree.getLastSelectedPathComponent();

		if((o != null) && (o == unitNode)) {
			tree.getSelectionModel().removeSelectionPath(new TreePath(treeModel.getPathToRoot(unitNode)));
		}

		treeModel.removeNodeFromParent(unitNode);

		/*        parentNode.remove(unitNode);
		        treeModel.reload(parentNode);*/
	}

	private void insertUnit(DefaultMutableTreeNode parentNode, Unit u, int index) {
		UnitNodeWrapper		   w	    = nodeWrapperFactory.createUnitNodeWrapper(u);
		DefaultMutableTreeNode unitNode = new DefaultMutableTreeNode(w);
		parentNode.insert(unitNode, index);
		unitNodes.put(u.getID(), unitNode);
		treeModel.reload(parentNode);
	}

	/**
	 * Sort a collection of regions in a specific order.
	 *
	 * @param regions TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private Collection sortRegions(Collection regions) {
		if((new Boolean(settings.getProperty("EMapOverviewPanel.sortRegions",
												 "true"))).booleanValue() == true) {
			if(settings.getProperty("EMapOverviewPanel.sortRegionsCriteria",
										"coordinates").equals("coordinates")) {
				List sortedRegions = CollectionFactory.createLinkedList(regions);
				Collections.sort(sortedRegions, new IDComparator());

				return sortedRegions;
			} else if(settings.getProperty("EMapOverviewPanel.sortRegionsCriteria",
											   "coordinates").equals("islands")) {
				List	   sortedRegions = CollectionFactory.createLinkedList(regions);
				Comparator idCmp = new IDComparator();
				Collections.sort(sortedRegions,
								 new RegionIslandComparator(new NameComparator(idCmp),
															idCmp, idCmp));

				return sortedRegions;
			} else {
				return regions;
			}
		} else {
			return regions;
		}
	}

	/////////////////
	// Key handler //
	/////////////////

	/**
	 * Should return all short cuts this class want to be informed. The
	 * elements should be of type javax.swing.KeyStroke
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getShortCuts() {
		return shortcuts.iterator();
	}

	/**
	 * This method is called when a shortcut from getShortCuts() is recognized.
	 *
	 * @param shortcut TODO: DOCUMENT ME!
	 */
	public void shortCut(javax.swing.KeyStroke shortcut) {
		int index = shortcuts.indexOf(shortcut);

		switch(index) {
		case -1:
			break; // unknown shortcut

		case 0:
		case 1:
			DesktopEnvironment.requestFocus("OVERVIEW");
			tree.requestFocus(); //activate the tree, not the scrollpane

			break;

		//case 2: shortCut_N();break;
		//case 2: toggleOrderConfirmation();break;
		case 2:
		case 4:
			jumpInSelectionHistory(1);

			break;

		case 3:
		case 5:
			jumpInSelectionHistory(-1);

			break;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void shortCut_B() {
		toggleOrderConfirmation();
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void shortCut_N() {
		shortCut_N(true);
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void shortCut_Reverse_N() {
		shortCut_N(false);
	}

	protected void shortCut_N(boolean traverseDown) {
		if((tree == null) || (rootNode == null) ||
			   (tree.getSelectionPath() == null)) {
			// fail fast
			return;
		}

		DefaultMutableTreeNode actNode = (DefaultMutableTreeNode) tree.getSelectionPath()
																	  .getLastPathComponent();
		Unit				   u = null;

		if(traverseDown) {
			// iterate over objects after  actNode
			u = getUnitInTree(iterateToNode(rootNode.preorderEnumeration(),
											actNode), actNode, traverseDown);

			if(u == null) {
				// iterate over objects before actNode
				u = getUnitInTree(rootNode.preorderEnumeration(), actNode,
								  traverseDown);
			}
		} else {
			// iterate over objects before actNode
			u = getUnitInTree(rootNode.preorderEnumeration(), actNode,
							  traverseDown);

			if(u == null) {
				// iterate over objects after  actNode
				u = getUnitInTree(iterateToNode(rootNode.preorderEnumeration(),
												actNode), actNode, traverseDown);
			}
		}

		if(u != null) {
			if(log.isDebugEnabled()) {
				log.debug("EMapOverviewPanel.shortCut_N(): firing Selection Event with Unit " +
						  u + " (" + u.ordersConfirmed + ")");
			}

			previousRegion = u.getRegion();

			// event source u? yeah, just provide some
			// stupid value here, null is prohibited, else
			// the selectionChanged() method would reject
			// the event since it originates from this.
			selectionChanged(new SelectionEvent(u, null, u));
			dispatcher.fire(new SelectionEvent(this, null, u));
		}
	}

	private Enumeration iterateToNode(Enumeration nodes, Object actNode) {
		while(nodes.hasMoreElements()) {
			if(nodes.nextElement().equals(actNode)) {
				break;
			}
		}

		return nodes;
	}

	private Unit getUnitInTree(Enumeration nodes, Object actNode, boolean first) {
		Unit ret = null;

		while(nodes.hasMoreElements()) {
			DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) nodes.nextElement();

			if(nextNode.equals(actNode)) {
				// return latest found
				return ret;
			}

			if(nextNode.getUserObject() instanceof UnitNodeWrapper) {
				UnitNodeWrapper uw = (UnitNodeWrapper) nextNode.getUserObject();
				Unit		    u = uw.getUnit();

				// pavkovic 2003.06.17: foreign units have their ordersConfirmed set to false!
				// we use uw.emphasized() to find a *candidate* for selecting. 
				// The candidate needs to have ordersConfirmed set to false.
				if(uw.emphasized() && !u.ordersConfirmed) {
					ret = u;

					// now we found a candidate
					if(first) {
						// ... and we are interested in the *first* hit
						return ret;
					}
				}
			}
		}

		// ... here we are interested in the *last* hit (e.q. to find a predecessor of a unit)
		return ret;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter createPreferencesAdapter() {
		return new EMapOverviewPreferences(settings);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void stateChanged(javax.swing.event.ChangeEvent p1) {
		// update the history list
		lstHistory.setListData(SelectionHistory.getHistory().toArray());
	}

	/**
	 * Repaints this component. This method also explicitly repaints it's
	 * managed components because the may be separated by the Magellan
	 * Desktop.
	 */
	public void repaint() {
		super.repaint();

		if(scpTree != null) {
			scpTree.repaint();
		}

		if(scpHistory != null) {
			scpHistory.repaint();
		}
	}

	private void toggleOrderConfirmation() {
		if(activeObject == null) {
			return;
		}

		if(activeObject instanceof Region) {
			Region  r	    = (Region) activeObject;
			boolean first   = true;
			boolean confirm = false;

			for(Iterator iter = r.units().iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();

				if(EMapDetailsPanel.isPrivilegedAndNoSpy(u)) {
					if(first) {
						confirm = !u.ordersConfirmed;
						first   = false;
					}

					u.ordersConfirmed = confirm;
				}
			}

			dispatcher.fire(new OrderConfirmEvent(this, r.units()));
		} else if(activeObject instanceof Unit) {
			Unit u = (Unit) activeObject;

			if(EMapDetailsPanel.isPrivilegedAndNoSpy(u)) {
				u.ordersConfirmed = !u.ordersConfirmed;

				List units = CollectionFactory.createLinkedList();
				units.add(u);
				dispatcher.fire(new OrderConfirmEvent(this, units));
			}
		}

		tree.invalidate();
		tree.repaint();
	}

	/**
	 * Do clean-up on quitting.
	 */
	public void quit() {
		settings.setProperty("EMapOverviewPanel.treeHistorySplit",
							 Integer.toString(sppTreeHistory.getDividerLocation()));
	}

	/**
	 * Selects and entry in the selection history by a relative offset.
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	private void jumpInSelectionHistory(int i) {
		ListModel model = lstHistory.getModel();

		if(model.getSize() > 0) {
			int selectedIndex = Math.max(lstHistory.getSelectedIndex(), 0);
			int newIndex = Math.min(Math.max(selectedIndex + i, 0),
									model.getSize() - 1);

			if(selectedIndex != newIndex) {
				lstHistory.setSelectedIndex(newIndex);
				dispatcher.fire(new SelectionEvent(lstHistory, null,
												   lstHistory.getModel()
															 .getElementAt(newIndex)));
			}
		}
	}

	/**
	 * This is a helper method to set this.activeAlliances to a usefull value,
	 * if no faction or group is active. The idea is to take all alliances of
	 * all privileged factions and combine their states by & (or in other
	 * words to take the intersection over all alliances of all privileged
	 * factions)
	 */
	private void setDefaultAlliances() {
		activeAlliancesAreDefault = true;

		List privilegedFactions = CollectionFactory.createLinkedList();
		activeAlliances.clear();

		boolean privilegedWithoutAllies = false;

		for(Iterator iter = data.factions().values().iterator();
				iter.hasNext();) {
			Faction f = (Faction) iter.next();

			if(f.isPrivileged()) {
				privilegedFactions.add(f);

				if((f.allies == null) || (f.allies.values().size() <= 0)) {
					// remember that one privileged faction had no allies
					// so it is not necessary to do further calculations
					privilegedWithoutAllies = true;
				}
			}
		}

		if(!privilegedWithoutAllies) {
			// take the alliances of the first found privileged faction as activeAlliances
			if(privilegedFactions.size() > 0) {
				activeAlliances.putAll(((Faction) privilegedFactions.get(0)).allies);
			}

			// now check whether they are contained in the alliances-Maps of the other
			// privileged factions and adjust their states if necessary
			boolean delEntry = false;

			for(Iterator iter = activeAlliances.keySet().iterator();
					iter.hasNext();) {
				ID id = (ID) iter.next();

				for(int factionCount = 1;
						factionCount < privilegedFactions.size();
						factionCount++) {
					Faction f = (Faction) privilegedFactions.get(factionCount);

					if(!f.allies.containsKey(id)) {
						// mark this alliances as to be deleted out of activeAlliances
						delEntry = true;

						break;
					} else {
						Alliance a1 = (Alliance) activeAlliances.get(id);
						Alliance a2 = (Alliance) f.allies.get(id);
						a1.setState(a1.getState() & a2.getState());
					}
				}

				if(delEntry) {
					delEntry = false;
					iter.remove();
				}
			}
		}

		// now add all privileged factions with alliance state Integer.MAX_VALUE
		for(Iterator iter = privilegedFactions.iterator(); iter.hasNext();) {
			Faction f = (Faction) iter.next();
			activeAlliances.put(f.getID(), new Alliance(f, Integer.MAX_VALUE));
		}
	}

	protected void loadExpandProperties() {
		try {
			expandMode = Integer.parseInt(settings.getProperty("EMapOverviewPanel.ExpandMode"));
		} catch(Exception exc) {
			expandMode = EXPAND_FLAG | (3 << 2);
		}

		try {
			expandTrustlevel = Integer.parseInt(settings.getProperty("EMapOverviewPanel.ExpandTrustlevel"));
		} catch(Exception exc) {
			expandTrustlevel = Faction.TL_PRIVILEGED;
		}
	}

	protected void saveExpandProperties() {
		settings.setProperty("EMapOverviewPanel.ExpandMode",
							 String.valueOf(expandMode));
		settings.setProperty("EMapOverviewPanel.ExpandTrustlevel",
							 String.valueOf(expandTrustlevel));
	}

	protected void loadCollapseProperty() {
		try {
			collapseMode = Integer.parseInt(settings.getProperty("EMapOverviewPanel.CollapseMode"));
		} catch(Exception exc) {
			expandMode = 3 << 2;
		}
	}

	protected void saveCollapseProperty() {
		settings.setProperty("EMapOverviewPanel.CollapseMode",
							 String.valueOf(collapseMode));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param src TODO: DOCUMENT ME!
	 */
	public void updateTree(Object src) {
		tree.treeDidChange();

		// Use the UI Fix
		javax.swing.plaf.TreeUI ui = tree.getUI();

		if(ui instanceof javax.swing.plaf.basic.BasicTreeUI) {
			javax.swing.plaf.basic.BasicTreeUI ui2 = (javax.swing.plaf.basic.BasicTreeUI) ui;
			int								   i = ui2.getLeftChildIndent();
			ui2.setLeftChildIndent(100);
			ui2.setLeftChildIndent(i);
		}

		tree.revalidate();
		tree.repaint();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param stroke TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getShortcutDescription(Object stroke) {
		int index = shortcuts.indexOf(stroke);

		return getString("shortcut.description." + String.valueOf(index));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getListenerDescription() {
		return getString("shortcut.title");
	}

	private class EMapOverviewPreferences extends JPanel
		implements ExtendedPreferencesAdapter
	{
		private class SkillPreferences extends JPanel
			implements PreferencesAdapter
		{
			/** TODO: DOCUMENT ME! */
			public JList    skillList		  = null;
			private JButton upButton		  = null;
			private JButton downButton		  = null;
			private JButton refreshListButton = null;

			/**
			 * Creates a new SkillPreferences object.
			 */
			public SkillPreferences() {
				this.setLayout(new BorderLayout());
				this.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
												getString("prefs.skillorder")));

				skillList = new JList();
				skillList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

				if(data != null) {
					List v = CollectionFactory.createLinkedList();

					for(Iterator iter = data.rules.getSkillTypeIterator();
							iter.hasNext();) {
						SkillType type = (SkillType) iter.next();
						v.add(type);
					}

					Collections.sort(v,
									 new SkillTypeRankComparator(new NameComparator(new IDComparator()),
																 EMapOverviewPanel.this.settings));
					skillList.setListData(v.toArray());
				}

				this.add(new JScrollPane(skillList), BorderLayout.CENTER);

				JPanel			   buttons = new JPanel(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0,
															  GridBagConstraints.WEST,
															  GridBagConstraints.HORIZONTAL,
															  new Insets(0, 1,
																		 2, 1),
															  0, 0);

				upButton = new JButton(getString("prefs.upbutton.caption"));
				upButton.setPreferredSize(new Dimension(110, 40));
				upButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if((skillList.getModel() == null) ||
								   (skillList.getModel().getSize() == 0)) {
								return;
							}

							int selIndices[] = skillList.getSelectedIndices();

							if(selIndices.length == 0) {
								return;
							}

							List	  newData			 = CollectionFactory.createLinkedList();
							ListModel oldData			 = skillList.getModel();
							List	  newSelectedIndices = CollectionFactory.createLinkedList();

							for(int i = 0; i < oldData.getSize(); i++) {
								Object o = oldData.getElementAt(i);

								if(skillList.isSelectedIndex(i)) {
									int newPos;

									if((i > 0) &&
										   !newSelectedIndices.contains(new Integer(i -
																						1))) {
										newPos = i - 1;
									} else {
										newPos = i;
									}

									newData.add(newPos, o);
									newSelectedIndices.add(new Integer(newPos));
								} else {
									newData.add(o);
								}
							}

							skillList.setListData(newData.toArray());

							int   selection[] = new int[newSelectedIndices.size()];
							int   i = 0;

							for(Iterator iter = newSelectedIndices.iterator();
									iter.hasNext(); i++) {
								selection[i] = ((Integer) iter.next()).intValue();
							}

							skillList.setSelectedIndices(selection);
							skillList.ensureIndexIsVisible(selection[0]);
						}
					});
				buttons.add(upButton, c);

				c.gridy++;

				downButton = new JButton(getString("prefs.downbutton.caption"));
				downButton.setPreferredSize(new Dimension(110, 40));
				downButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if((skillList.getModel() == null) ||
								   (skillList.getModel().getSize() == 0)) {
								return;
							}

							int selIndices[] = skillList.getSelectedIndices();

							if(selIndices.length == 0) {
								return;
							}

							List	  newData			 = CollectionFactory.createLinkedList();
							ListModel oldData			 = skillList.getModel();
							List	  newSelectedIndices = CollectionFactory.createLinkedList();

							for(int i = oldData.getSize() - 1; i >= 0; i--) {
								Object o = oldData.getElementAt(i);

								if(skillList.isSelectedIndex(i)) {
									int newPos;

									if((i < (oldData.getSize() - 1)) &&
										   !newSelectedIndices.contains(new Integer(i +
																						1))) {
										newPos = i + 1;
										newData.add(1, o);
									} else {
										newPos = i;
										newData.add(0, o);
									}

									newSelectedIndices.add(new Integer(newPos));
								} else {
									newData.add(0, o);
								}
							}

							skillList.setListData(newData.toArray());

							int   selection[] = new int[newSelectedIndices.size()];
							int   i = 0;

							for(Iterator iter = newSelectedIndices.iterator();
									iter.hasNext(); i++) {
								selection[i] = ((Integer) iter.next()).intValue();
							}

							skillList.setSelectedIndices(selection);
							skillList.ensureIndexIsVisible(selection[0]);
						}
					});
				buttons.add(downButton, c);

				// add a filler
				c.gridy++;
				c.fill    = GridBagConstraints.BOTH;
				c.weighty = 1;
				buttons.add(new JPanel(), c);

				c.anchor = GridBagConstraints.SOUTHWEST;
				c.gridy++;
				c.fill		    = GridBagConstraints.HORIZONTAL;
				c.weighty	    = 0;
				c.insets.bottom = 0;

				refreshListButton = new JButton(getString("prefs.refreshlistbutton.caption"));
				refreshListButton.setPreferredSize(new Dimension(110, 40));
				refreshListButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if((skillList.getModel() == null) ||
								   (skillList.getModel().getSize() == 0)) {
								return;
							}

							ListModel listData = skillList.getModel();
							List	  v = CollectionFactory.createLinkedList();

							for(int index = 0; index < listData.getSize();
									index++) {
								v.add(listData.getElementAt(index));
							}

							Collections.sort(v);
							skillList.setListData(v.toArray());
						}
					});
				buttons.add(refreshListButton, c);

				this.add(buttons, BorderLayout.EAST);

				setEnabled(rdbSortUnitsSkills.isSelected());
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param b TODO: DOCUMENT ME!
			 */
			public void setEnabled(boolean b) {
				super.setEnabled(b);
				skillList.setEnabled(b);
				upButton.setEnabled(b);
				downButton.setEnabled(b);
				refreshListButton.setEnabled(b);
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
				return getString("prefs.skillorder");
			}

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void applyPreferences() {
				ListModel listData = skillList.getModel();

				for(int index = 0; index < listData.getSize(); index++) {
					SkillType s = (SkillType) listData.getElementAt(index);
					settings.setProperty("ClientPreferences.compareValue." +
										 s.getID(), String.valueOf(index));

					//pavkovic 2003.06.19: this is historical!settings.setProperty(type.getID() + ".compareValue", String.valueOf(index));
				}
			}
		}

		/** TODO: DOCUMENT ME! */
		public JCheckBox chkSortRegions = null;

		/** TODO: DOCUMENT ME! */
		public JRadioButton rdbSortRegionsCoordinates = null;

		/** TODO: DOCUMENT ME! */
		public JRadioButton rdbSortRegionsIslands = null;

		/** TODO: DOCUMENT ME! */
		public JCheckBox chkDisplayIslands = null;

		/** TODO: DOCUMENT ME! */
		public JRadioButton rdbSortUnitsUnsorted = null;

		/** TODO: DOCUMENT ME! */
		public JRadioButton rdbSortUnitsSkills = null;

		// use the best skill of the unit to sort it

		/** TODO: DOCUMENT ME! */
		public JRadioButton useBestSkill = null;

		// use the topmost skill in (selfdefined) skilltype-list to sort it

		/** TODO: DOCUMENT ME! */
		public JRadioButton useTopmostSkill = null;

		/** TODO: DOCUMENT ME! */
		public JRadioButton		 rdbSortUnitsNames = null;
		protected ExpandPanel    ePanel;
		protected CollapsePanel  cPanel;
		private SkillPreferences skillSort;
		private List			 subAdapter;
		private JList			 useList;
		private JList			 elementsList;

		/**
		 * Creates a new EMapOverviewPreferences object.
		 *
		 * @param settings TODO: DOCUMENT ME!
		 */
		public EMapOverviewPreferences(Properties settings) {
			chkSortRegions = new JCheckBox(getString("prefs.sortregions"),
										   (new Boolean(settings.getProperty("EMapOverviewPanel.sortRegions",
																			 "true"))).booleanValue());

			rdbSortRegionsCoordinates = new JRadioButton(getString("prefs.sortbycoordinates"),
														 settings.getProperty("EMapOverviewPanel.sortRegionsCriteria",
																			  "coordinates")
																 .equals("coordinates"));
			rdbSortRegionsIslands = new JRadioButton(getString("prefs.sortbyislands"),
													 settings.getProperty("EMapOverviewPanel.sortRegionsCriteria",
																		  "coordinates")
															 .equals("islands"));

			ButtonGroup regionSortButtons = new ButtonGroup();
			regionSortButtons.add(rdbSortRegionsCoordinates);
			regionSortButtons.add(rdbSortRegionsIslands);

			JPanel pnlRegionSortButtons = new JPanel();
			pnlRegionSortButtons.setLayout(new BoxLayout(pnlRegionSortButtons,
														 BoxLayout.Y_AXIS));
			pnlRegionSortButtons.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
															getString("prefs.regionsorting")));
			pnlRegionSortButtons.add(chkSortRegions);
			pnlRegionSortButtons.add(rdbSortRegionsCoordinates);
			pnlRegionSortButtons.add(rdbSortRegionsIslands);

			chkDisplayIslands = new JCheckBox(getString("prefs.showislands"),
											  (new Boolean(settings.getProperty("EMapOverviewPanel.displayIslands",
																				"true"))).booleanValue());

			JPanel pnlTreeStructure = new JPanel();
			pnlTreeStructure.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1,
														  GridBagConstraints.CENTER,
														  GridBagConstraints.HORIZONTAL,
														  new Insets(2, 2, 2, 2),
														  0, 0);
			pnlTreeStructure.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
														getString("prefs.treestructure")));

			JPanel elementsPanel = new JPanel();
			elementsPanel.setLayout(new BorderLayout(0, 0));
			elementsPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
													 getString("prefs.treeStructure.available")));

			DefaultListModel model = new DefaultListModel();
			model.add(0, getString("prefs.treeStructure.element.faction"));
			model.add(1, getString("prefs.treeStructure.element.group"));
			model.add(2, getString("prefs.treeStructure.element.combat"));
			model.add(3, getString("prefs.treeStructure.element.health"));
			model.add(4,
					  getString("prefs.treeStructure.element.factiondisguise"));
			model.add(5, getString("prefs.treeStructure.element.trustlevel"));
			elementsList = new JList(model);

			JScrollPane pane = new JScrollPane(elementsList);
			elementsPanel.add(pane, BorderLayout.CENTER);

			JPanel usePanel = new JPanel();
			usePanel.setLayout(new GridBagLayout());
			usePanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
												getString("prefs.treeStructure.use")));

			String criteria = settings.getProperty("EMapOverviewPanel.treeStructure",
												   " " + TreeHelper.FACTION +
												   " " + TreeHelper.GROUP);
			StringTokenizer tokenizer = new StringTokenizer(criteria);
			model = new DefaultListModel();

			while(tokenizer.hasMoreTokens()) {
				try {
					String s = tokenizer.nextToken();
					int    i = Integer.parseInt(s);
					s = "";

					if(i == TreeHelper.FACTION) {
						s = getString("prefs.treeStructure.element.faction");
					} else if(i == TreeHelper.GROUP) {
						s = getString("prefs.treeStructure.element.group");
					} else if(i == TreeHelper.COMBAT_STATUS) {
						s = getString("prefs.treeStructure.element.combat");
					} else if(i == TreeHelper.HEALTH) {
						s = getString("prefs.treeStructure.element.health");
					} else if(i == TreeHelper.FACTION_DISGUISE_STATUS) {
						s = getString("prefs.treeStructure.element.factiondisguise");
					} else if(i == TreeHelper.TRUSTLEVEL) {
						s = getString("prefs.treeStructure.element.trustlevel");
					}

					model.add(model.getSize(), s);
				} catch(NumberFormatException e) {
				}
			}

			useList = new JList(model);
			useList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			pane		 = new JScrollPane(useList);
			c.gridheight = 4;
			usePanel.add(pane, c);

			c.gridheight = 1;
			c.gridx		 = 1;
			c.weightx    = 0;
			usePanel.add(new JPanel(), c);

			c.gridy++;
			c.weighty = 0;

			JButton up = new JButton(getString("prefs.treeStructure.up"));
			up.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int				 pos   = useList.getSelectedIndex();
						DefaultListModel model = (DefaultListModel) useList.getModel();

						if(pos == 0) {
							return;
						}

						Object o = model.elementAt(pos);
						model.remove(pos);
						model.insertElementAt(o, pos - 1);
						useList.setSelectedIndex(pos - 1);
					}
				});
			usePanel.add(up, c);

			c.gridy++;

			JButton down = new JButton(getString("prefs.treeStructure.down"));
			down.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int				 pos   = useList.getSelectedIndex();
						DefaultListModel model = (DefaultListModel) useList.getModel();

						if(pos == (model.getSize() - 1)) {
							return;
						}

						Object o = model.elementAt(pos);
						model.remove(pos);
						model.insertElementAt(o, pos + 1);
						useList.setSelectedIndex(pos + 1);
					}
				});
			usePanel.add(down, c);

			c.gridy++;
			c.weighty = 1.0;
			usePanel.add(new JPanel(), c);

			c.gridx		 = 0;
			c.gridy		 = 0;
			c.gridheight = 4;
			c.weightx    = 0.5;
			c.weighty    = 0.5;
			pnlTreeStructure.add(elementsPanel, c);

			c.gridx = 2;
			pnlTreeStructure.add(usePanel, c);

			c.gridx		 = 1;
			c.gridheight = 1;
			c.weightx    = 0;
			c.weighty    = 1.0;
			pnlTreeStructure.add(new JPanel(), c);

			c.gridy++;
			c.weighty = 0;

			JButton right = new JButton("  -->  ");
			right.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Object			   selection[] = elementsList.getSelectedValues();
						DefaultListModel   model = (DefaultListModel) useList.getModel();

						for(int i = 0; i < selection.length; i++) {
							if(!model.contains(selection[i])) {
								model.add(model.getSize(), selection[i]);
							}
						}
					}
				});
			pnlTreeStructure.add(right, c);

			c.gridy++;

			JButton left = new JButton("  <--  ");
			left.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						DefaultListModel model		 = (DefaultListModel) useList.getModel();
						Object			 selection[] = useList.getSelectedValues();

						for(int i = 0; i < selection.length; i++) {
							model.removeElement(selection[i]);
						}
					}
				});
			pnlTreeStructure.add(left, c);

			c.gridy++;
			c.weighty = 1;
			pnlTreeStructure.add(new JPanel(), c);

			// Unit sorting
			rdbSortUnitsUnsorted = new JRadioButton(getString("prefs.reportorder"),
													settings.getProperty("EMapOverviewPanel.sortUnitsCriteria",
																		 "skills")
															.equals("unsorted"));
			rdbSortUnitsUnsorted.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						skillSort.setEnabled(false);
						useBestSkill.setEnabled(false);
						useTopmostSkill.setEnabled(false);
					}
				});
			rdbSortUnitsSkills = new JRadioButton(getString("prefs.sortbyskills"),
												  settings.getProperty("EMapOverviewPanel.sortUnitsCriteria",
																	   "skills")
														  .equals("skills"));
			rdbSortUnitsSkills.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						skillSort.setEnabled(true);
						useBestSkill.setEnabled(true);
						useTopmostSkill.setEnabled(true);
					}
				});

			boolean temp = settings.getProperty("EMapOverviewPanel.useBestSkill",
												"true").equalsIgnoreCase("true");
			useBestSkill = new JRadioButton(getString("prefs.usebestskill"),
											temp);
			useTopmostSkill = new JRadioButton(getString("prefs.usetopmostskill"),
											   !temp);

			ButtonGroup whichSkillToUse = new ButtonGroup();
			whichSkillToUse.add(useBestSkill);
			whichSkillToUse.add(useTopmostSkill);
			rdbSortUnitsNames = new JRadioButton(getString("prefs.sortbynames"),
												 settings.getProperty("EMapOverviewPanel.sortUnitsCriteria",
																	  "skills")
														 .equals("names"));
			rdbSortUnitsNames.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						skillSort.setEnabled(false);
						useBestSkill.setEnabled(false);
						useTopmostSkill.setEnabled(false);
					}
				});

			ButtonGroup unitsSortButtons = new ButtonGroup();
			unitsSortButtons.add(rdbSortUnitsUnsorted);
			unitsSortButtons.add(rdbSortUnitsSkills);
			unitsSortButtons.add(rdbSortUnitsNames);

			JPanel pnlUnitSort = new JPanel();
			pnlUnitSort.setLayout(new GridBagLayout());
			c = new GridBagConstraints(0, 0, 1, 1, 0.1, 0.1,
									   GridBagConstraints.WEST,
									   GridBagConstraints.NONE,
									   new Insets(0, 0, 0, 0), 0, 0);
			pnlUnitSort.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
												   getString("prefs.unitsorting")));
			pnlUnitSort.add(rdbSortUnitsUnsorted, c);
			c.gridy = 1;
			pnlUnitSort.add(rdbSortUnitsSkills, c);
			c.gridy  = 2;
			c.insets = new Insets(0, 30, 0, 0);
			pnlUnitSort.add(useBestSkill, c);
			c.gridy = 3;
			pnlUnitSort.add(useTopmostSkill, c);
			c.gridy  = 4;
			c.ipadx  = 0;
			c.insets = new Insets(0, 0, 0, 0);
			pnlUnitSort.add(rdbSortUnitsNames, c);

			this.setLayout(new GridBagLayout());
			c.anchor     = GridBagConstraints.CENTER;
			c.gridx		 = 0;
			c.gridy		 = 0;
			c.gridwidth  = 1;
			c.gridheight = 1;
			c.fill		 = GridBagConstraints.HORIZONTAL;
			c.weightx    = 1.0;
			c.weighty    = 0.0;
			this.add(pnlRegionSortButtons, c);

			c.anchor = GridBagConstraints.WEST;
			c.gridy++;
			c.insets.left = 10;
			c.fill		  = GridBagConstraints.NONE;
			c.weightx     = 0.0;
			this.add(chkDisplayIslands, c);

			c.insets.left = 0;
			c.anchor	  = GridBagConstraints.CENTER;
			c.gridy++;
			c.fill    = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			this.add(pnlTreeStructure, c);

			c.gridy++;
			this.add(pnlUnitSort, c);

			JPanel help = new JPanel(new GridLayout(1, 2));
			help.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											getString("prefs.expand.title")));
			help.add(ePanel = new ExpandPanel()); //, BorderLayout.WEST);
			help.add(cPanel = new CollapsePanel()); //, BorderLayout.EAST);
			c.gridy++;
			this.add(help, c);

			subAdapter = CollectionFactory.createArrayList(1);
			subAdapter.add(skillSort = new SkillPreferences());
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			settings.setProperty("EMapOverviewPanel.sortRegions",
								 (new Boolean(chkSortRegions.isSelected())).toString());

			if(rdbSortRegionsCoordinates.isSelected() == true) {
				settings.setProperty("EMapOverviewPanel.sortRegionsCriteria",
									 "coordinates");
			} else if(rdbSortRegionsIslands.isSelected() == true) {
				settings.setProperty("EMapOverviewPanel.sortRegionsCriteria",
									 "islands");
			}

			settings.setProperty("EMapOverviewPanel.displayIslands",
								 (new Boolean(chkDisplayIslands.isSelected())).toString());

			if(rdbSortUnitsUnsorted.isSelected() == true) {
				settings.setProperty("EMapOverviewPanel.sortUnitsCriteria",
									 "unsorted");
			} else if(rdbSortUnitsSkills.isSelected() == true) {
				settings.setProperty("EMapOverviewPanel.sortUnitsCriteria",
									 "skills");
			} else if(rdbSortUnitsNames.isSelected() == true) {
				settings.setProperty("EMapOverviewPanel.sortUnitsCriteria",
									 "names");
			}

			settings.setProperty("EMapOverviewPanel.useBestSkill",
								 String.valueOf(useBestSkill.isSelected()));

			DefaultListModel model	    = (DefaultListModel) useList.getModel();
			String			 definition = "";

			for(int i = 0; i < model.getSize(); i++) {
				String s = (String) model.getElementAt(i);

				if(s.equals(getString("prefs.treeStructure.element.faction"))) {
					definition += (TreeHelper.FACTION + " ");
				} else if(s.equals(getString("prefs.treeStructure.element.group"))) {
					definition += (TreeHelper.GROUP + " ");
				} else if(s.equals(getString("prefs.treeStructure.element.combat"))) {
					definition += (TreeHelper.COMBAT_STATUS + " ");
				} else if(s.equals(getString("prefs.treeStructure.element.health"))) {
					definition += (TreeHelper.HEALTH + " ");
				} else if(s.equals(getString("prefs.treeStructure.element.factiondisguise"))) {
					definition += (TreeHelper.FACTION_DISGUISE_STATUS + " ");
				} else if(s.equals(getString("prefs.treeStructure.element.trustlevel"))) {
					definition += (TreeHelper.TRUSTLEVEL + " ");
				}
			}

			settings.setProperty("EMapOverviewPanel.treeStructure", definition);

			ePanel.apply();
			cPanel.apply();

			// We have to assure, that SkillPreferences.applyPreferences is called
			// before we rebuild the tree, i.e. before we call gameDataChanged().
			skillSort.applyPreferences();

			// TODO: wirklich ein GameDatachange?
			if(data != null) {
				gameDataChanged(new GameDataEvent(EMapOverviewPanel.this, data));
			}
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
			return getString("prefs.title");
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getChildren() {
			return subAdapter;
		}

		protected class ExpandPanel extends JPanel implements ActionListener {
			protected JRadioButton radioButtons[];
			protected JCheckBox    checkBox;
			protected JTextField   trustlevel;

			/**
			 * Creates a new ExpandPanel object.
			 */
			public ExpandPanel() {
				super(new GridBagLayout());

				GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 1,
																0,
																GridBagConstraints.NORTHWEST,
																GridBagConstraints.NONE,
																new Insets(0,
																		   0,
																		   0, 0),
																0, 0);
				radioButtons = new JRadioButton[3];

				ButtonGroup group = new ButtonGroup();

				boolean     expanded = (expandMode & EXPAND_FLAG) != 0;

				radioButtons[0] = new JRadioButton(getString("prefs.expand.none"),
												   !expanded);
				group.add(radioButtons[0]);
				this.add(radioButtons[0], con);

				con.gridy++;
				con.fill = GridBagConstraints.HORIZONTAL;
				this.add(new JSeparator(JSeparator.HORIZONTAL), con);
				con.fill = GridBagConstraints.NONE;

				radioButtons[1] = new JRadioButton(getString("prefs.expand.faction"),
												   (expanded &&
												   ((expandMode >> 2) == 0)));
				group.add(radioButtons[1]);
				con.gridy++;
				this.add(radioButtons[1], con);

				radioButtons[2] = new JRadioButton(getString("prefs.expand.full"),
												   (expanded &&
												   ((expandMode >> 2) == 3)));
				group.add(radioButtons[2]);
				con.gridy++;
				this.add(radioButtons[2], con);

				trustlevel = new JTextField(String.valueOf(expandTrustlevel), 3);
				trustlevel.setEnabled(radioButtons[2].isSelected());

				JPanel help = new JPanel();
				help.add(Box.createRigidArea(new Dimension(20, 5)));

				String s     = getString("prefs.expand.trustlevel");
				int    index = s.indexOf("#T");

				if(index == 0) {
					help.add(trustlevel);
					help.add(new JLabel(s.substring(2)));
				} else if((index == -1) || (index == (s.length() - 2))) {
					if(index != -1) {
						s = s.substring(0, index);
					}

					help.add(new JLabel(s));
					help.add(trustlevel);
				} else {
					help.add(new JLabel(s.substring(0, index)));
					help.add(trustlevel);
					help.add(new JLabel(s.substring(index + 2)));
				}

				con.gridy++;
				this.add(help, con);

				con.gridy++;
				checkBox = new JCheckBox(getString("prefs.expand.ifinside"),
										 (expandMode & EXPAND_IFINSIDE_FLAG) != 0);
				checkBox.setEnabled(expanded);
				this.add(checkBox, con);

				registerListener();
			}

			protected void registerListener() {
				for(int i = 0; i < radioButtons.length; i++) {
					radioButtons[i].addActionListener(this);
				}
			}

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void apply() {
				if(radioButtons[0].isSelected()) {
					expandMode = expandMode & (0xFFFFFFFF ^ EXPAND_FLAG);
				} else {
					expandMode = expandMode | EXPAND_FLAG;
				}

				if(checkBox.isSelected()) {
					expandMode = expandMode | EXPAND_IFINSIDE_FLAG;
				} else {
					expandMode = expandMode &
								 (0xFFFFFFFF ^ EXPAND_IFINSIDE_FLAG);
				}

				int i = expandMode >> 2;

				if(radioButtons[1].isSelected()) {
					i = 0;
				} else if(radioButtons[2].isSelected()) {
					i = 3;
				}

				expandMode = (expandMode &
							 (EXPAND_FLAG | EXPAND_IFINSIDE_FLAG)) | (i << 2);

				try {
					expandTrustlevel = Integer.parseInt(trustlevel.getText());
				} catch(NumberFormatException nfe) {
				}

				saveExpandProperties();
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param actionEvent TODO: DOCUMENT ME!
			 */
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				checkBox.setEnabled(actionEvent.getSource() != radioButtons[0]);
				trustlevel.setEnabled(actionEvent.getSource() == radioButtons[2]);
			}
		}

		protected class CollapsePanel extends JPanel implements ActionListener {
			protected JRadioButton radioButtons[];
			protected JCheckBox    checkBox;

			/**
			 * Creates a new CollapsePanel object.
			 */
			public CollapsePanel() {
				super(new GridBagLayout());

				this.setBorder(new LeftBorder());

				GridBagConstraints con = new GridBagConstraints(0, 0, 1, 1, 1,
																0,
																GridBagConstraints.NORTHWEST,
																GridBagConstraints.NONE,
																new Insets(0,
																		   3,
																		   0, 0),
																0, 0);
				radioButtons = new JRadioButton[3];

				ButtonGroup group = new ButtonGroup();

				boolean     collapse = (collapseMode & COLLAPSE_FLAG) != 0;

				radioButtons[0] = new JRadioButton(getString("prefs.collapse.none"),
												   !collapse);
				group.add(radioButtons[0]);
				this.add(radioButtons[0], con);

				con.gridy++;
				con.fill	    = GridBagConstraints.HORIZONTAL;
				con.insets.left = 0;
				this.add(new JSeparator(JSeparator.HORIZONTAL), con);
				con.insets.left = 3;
				con.fill	    = GridBagConstraints.NONE;

				radioButtons[1] = new JRadioButton(getString("prefs.collapse.faction"),
												   (collapse &&
												   ((collapseMode >> 2) == 0)));
				group.add(radioButtons[1]);
				con.gridy++;
				this.add(radioButtons[1], con);

				radioButtons[2] = new JRadioButton(getString("prefs.collapse.full"),
												   (collapse &&
												   ((collapseMode >> 2) == 3)));
				group.add(radioButtons[2]);
				con.gridy++;
				this.add(radioButtons[2], con);

				con.gridy++;
				checkBox = new JCheckBox(getString("prefs.collapse.onlyautoexpanded"),
										 (collapseMode &
										 COLLAPSE_ONLY_EXPANDED) != 0);
				this.add(checkBox, con);

				// to make it equally high to ePanel
				con.gridy++;
				this.add(Box.createVerticalStrut(checkBox.getPreferredSize().height +
												 5), con);

				/*con.gridx = 0;
				con.gridheight = con.gridy + 1;
				con.gridy = 0;
				con.fill = GridBagConstraints.VERTICAL;
				JComponent c = new JSeparator(JSeparator.VERTICAL);
				c.setMaximumSize(new Dimension(3, 1000));
				this.add(c, con);*/
				registerListener();
			}

			protected void registerListener() {
				for(int i = 0; i < radioButtons.length; i++) {
					radioButtons[i].addActionListener(this);
				}
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param actionEvent TODO: DOCUMENT ME!
			 */
			public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
				checkBox.setEnabled(actionEvent.getSource() != radioButtons[0]);
			}

			/**
			 * TODO: DOCUMENT ME!
			 */
			public void apply() {
				if(radioButtons[0].isSelected()) {
					collapseMode &= (0xFFFFFFFF ^ COLLAPSE_FLAG);
				} else {
					collapseMode |= COLLAPSE_FLAG;
				}

				if(checkBox.isSelected()) {
					collapseMode |= COLLAPSE_ONLY_EXPANDED;
				} else {
					collapseMode &= (0xFFFFFFFF ^ COLLAPSE_ONLY_EXPANDED);
				}

				int i = collapseMode >> 2;

				if(radioButtons[1].isSelected()) {
					i = 0;
				} else if(radioButtons[2].isSelected()) {
					i = 3;
				}

				collapseMode = (collapseMode &
							   (COLLAPSE_FLAG | COLLAPSE_ONLY_EXPANDED)) |
							   (i << 2);

				saveCollapseProperty();
			}

			protected class LeftBorder extends AbstractBorder {
				protected JSeparator sep;

				/**
				 * Creates a new LeftBorder object.
				 */
				public LeftBorder() {
					sep = new JSeparator(JSeparator.VERTICAL);
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param c TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public Insets getBorderInsets(Component c) {
					return getBorderInsets(c, null);
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param c TODO: DOCUMENT ME!
				 * @param in TODO: DOCUMENT ME!
				 *
				 * @return TODO: DOCUMENT ME!
				 */
				public Insets getBorderInsets(Component c, Insets in) {
					if(in == null) {
						in = new Insets(0, 0, 0, 0);
					}

					in.top    = 0;
					in.bottom = 0;
					in.right  = 0;
					in.left   = sep.getPreferredSize().width;

					return in;
				}

				/**
				 * TODO: DOCUMENT ME!
				 *
				 * @param c TODO: DOCUMENT ME!
				 * @param g TODO: DOCUMENT ME!
				 * @param x TODO: DOCUMENT ME!
				 * @param y TODO: DOCUMENT ME!
				 * @param width TODO: DOCUMENT ME!
				 * @param height TODO: DOCUMENT ME!
				 */
				public void paintBorder(Component c, Graphics g, int x, int y,
										int width, int height) {
					sep.setBounds(x, y, width, height);
					SwingUtilities.paintComponent(g, sep, new JPanel(), x, y,
												  width, height);
				}
			}
		}
	}

	private class ScrollerRunnable implements Runnable {
		private Rectangle centerRect;

		/**
		 * Creates a new ScrollerRunnable object.
		 *
		 * @param r TODO: DOCUMENT ME!
		 */
		public ScrollerRunnable(Rectangle r) {
			centerRect = r;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void run() {
			if(centerRect != null) {
				Rectangle viewRect = scpTree.getViewport().getViewRect();
				centerRect.x     = viewRect.x;
				centerRect.width = viewRect.width;

				if(viewRect.contains(centerRect) == false) {
					Point viewPos = new Point(0, centerRect.y);

					if(centerRect.height < viewRect.height) {
						viewPos.y = Math.min(tree.getHeight() -
											 viewRect.height,
											 centerRect.y -
											 ((viewRect.height -
											 centerRect.height) / 2));
					}

					scpTree.getViewport().setViewPosition(viewPos);
				}
			}
		}
	}

	/**
	 * Updates UnitNodeWrappers on changes of orders
	 */
	private class UnitWrapperUpdater implements UnitOrdersListener {
		Collection buf[] = {
							   CollectionFactory.createLinkedList(),
							   CollectionFactory.createLinkedList()
						   };

		/**
		 * Invoked when the orders of a unit are modified.
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void unitOrdersChanged(UnitOrdersEvent e) {
			update(e.getUnit(), true);
		}

		protected synchronized void update(Unit u,
										   boolean updateRelationPartners) {
			if(unitNodes.containsKey(u.getID())) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) unitNodes.get(u.getID());
				UnitNodeWrapper		   unw = (UnitNodeWrapper) node.getUserObject();
				unw.clearBuffer();
				treeModel.nodeChanged(node);

				Collection relations = u.getRelations();

				if(!unitRelations.containsKey(u.getID())) {
					if(relations.size() == 0) {
						return;
					}

					unitRelations.put(u.getID(),
									  CollectionFactory.createLinkedList());
				}

				Collection oldRelations = (Collection) unitRelations.get(u.getID());
				Collection buffer = buf[updateRelationPartners ? 0 : 1];

				if(!relations.equals(oldRelations)) {
					buffer.clear();
					buffer.addAll(oldRelations);

					Iterator it = relations.iterator();

					while(it.hasNext()) {
						Object o = it.next();

						if(oldRelations.contains(o)) {
							buffer.remove(o);
						} else {
							oldRelations.add(o);

							if(updateRelationPartners &&
								   (o instanceof TransferRelation)) {
								update(((TransferRelation) o).target, false);
							}
						}
					}

					if(buffer.size() > 0) {
						it = buffer.iterator();

						while(it.hasNext()) {
							Object o = it.next();
							oldRelations.remove(o);

							if(updateRelationPartners &&
								   (o instanceof TransferRelation)) {
								update(((TransferRelation) o).target, false);
							}
						}
					}
				}
			}
		}
	}

	TreeBuilder treeBuilder;

	class TreeBuilder {
		/** TODO: DOCUMENT ME! */
		public final int UNITS = 1;

		/** TODO: DOCUMENT ME! */
		public final int BUILDINGS = 2;

		/** TODO: DOCUMENT ME! */
		public final int SHIPS = 4;

		/** TODO: DOCUMENT ME! */
		public final int COMMENTS = 8;

		/** TODO: DOCUMENT ME! */
		public final int   CREATE_ISLANDS = 16384;
		private int		   mode = UNITS | BUILDINGS | SHIPS | COMMENTS;
		private Map		   regionNodes;
		private Map		   unitNodes;
		private Map		   buildingNodes;
		private Map		   shipNodes;
		private Map		   activeAlliances;
		private Comparator unitComparator;
		private int		   treeStructure[];

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param mode TODO: DOCUMENT ME!
		 */
		public void setMode(int mode) {
			this.mode = mode;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getMode() {
			return mode;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param regions TODO: DOCUMENT ME!
		 */
		public void setRegionNodes(Map regions) {
			regionNodes = regions;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param units TODO: DOCUMENT ME!
		 */
		public void setUnitNodes(Map units) {
			unitNodes = units;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param buildings TODO: DOCUMENT ME!
		 */
		public void setBuildingNodes(Map buildings) {
			buildingNodes = buildings;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param ships TODO: DOCUMENT ME!
		 */
		public void setShipNodes(Map ships) {
			shipNodes = ships;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param alliances TODO: DOCUMENT ME!
		 */
		public void setActiveAlliances(Map alliances) {
			activeAlliances = alliances;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param compare TODO: DOCUMENT ME!
		 */
		public void setUnitComparator(Comparator compare) {
			unitComparator = compare;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param structure TODO: DOCUMENT ME!
		 */
		public void setTreeStructure(int structure[]) {
			treeStructure = structure;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param rootNode TODO: DOCUMENT ME!
		 * @param data TODO: DOCUMENT ME!
		 */
		public void buildTree(DefaultMutableTreeNode rootNode, GameData data) {
			if(data == null) {
				return;
			}

			buildTree(rootNode, sortRegions(data.regions().values()),
					  data.units().values(), regionNodes, unitNodes,
					  buildingNodes, shipNodes, unitComparator,
					  activeAlliances, treeStructure, data);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param rootNode TODO: DOCUMENT ME!
		 * @param regionCollection TODO: DOCUMENT ME!
		 * @param units TODO: DOCUMENT ME!
		 * @param regionNodes TODO: DOCUMENT ME!
		 * @param unitNodes TODO: DOCUMENT ME!
		 * @param buildingNodes TODO: DOCUMENT ME!
		 * @param shipNodes TODO: DOCUMENT ME!
		 * @param unitSorting TODO: DOCUMENT ME!
		 * @param activeAlliances TODO: DOCUMENT ME!
		 * @param treeStructure TODO: DOCUMENT ME!
		 * @param data TODO: DOCUMENT ME!
		 */
		public void buildTree(DefaultMutableTreeNode rootNode,
							  Collection regionCollection, Collection units,
							  Map regionNodes, Map unitNodes,
							  Map buildingNodes, Map shipNodes,
							  Comparator unitSorting, Map activeAlliances,
							  int treeStructure[], GameData data) {
			Iterator			   regions			   = regionCollection.iterator();
			boolean				   unitInteresting     = (mode & UNITS) != 0;
			boolean				   buildingInteresting = (mode & BUILDINGS) != 0;
			boolean				   shipInteresting     = (mode & SHIPS) != 0;
			boolean				   commentInteresting  = (mode & COMMENTS) != 0;
			boolean				   createIslandNodes   = (mode &
														 CREATE_ISLANDS) != 0;

			DefaultMutableTreeNode islandNode = null;
			DefaultMutableTreeNode regionNode = null;
			Region				   r		  = null;
			Island				   curIsland  = null;

			while(regions.hasNext()) {
				r = (Region) regions.next();

				if(!((unitInteresting && !r.units().isEmpty()) ||
					   (buildingInteresting && !r.buildings().isEmpty()) ||
					   (shipInteresting && !r.ships().isEmpty()) ||
					   (commentInteresting &&
					   !((r.comments == null) || (r.comments.size() == 0))))) {
					continue;
				}

				// add region node to tree an node map
				regionNode = (DefaultMutableTreeNode) TreeHelper.createRegionNode(r,
																				  nodeWrapperFactory,
																				  activeAlliances,
																				  unitNodes,
																				  buildingNodes,
																				  shipNodes,
																				  unitSorting,
																				  treeStructure,
																				  data);

				if(regionNode == null) {
					continue;
				}

				// update island node
				if(createIslandNodes) {
					if(r.getIsland() != null) {
						if(!r.getIsland().equals(curIsland)) {
							curIsland  = r.getIsland();
							islandNode = new DefaultMutableTreeNode(nodeWrapperFactory.createIslandNodeWrapper(curIsland));
							rootNode.add(islandNode);
						}
					} else {
						islandNode = null;
					}
				}

				if(islandNode != null) {
					islandNode.add(regionNode);
				} else {
					rootNode.add(regionNode);
				}

				regionNodes.put(r.getID(), regionNode);
			}

			// add the homeless
			DefaultMutableTreeNode n = new DefaultMutableTreeNode(getString("node.regionlessunits"));

			for(Iterator iter = units.iterator(); iter.hasNext();) {
				Unit un = (Unit) iter.next();

				if(un.getRegion() == null) {
					n.add(new DefaultMutableTreeNode(nodeWrapperFactory.createUnitNodeWrapper(un)));
				}
			}

			if(n.getChildCount() > 0) {
				rootNode.add(n);
			}
		}
	}

	protected TreeBuilder createTreeBuilder() {
		TreeBuilder treeBuilder = new TreeBuilder();
		treeBuilder.setRegionNodes(regionNodes);
		treeBuilder.setUnitNodes(unitNodes);
		treeBuilder.setShipNodes(shipNodes);
		treeBuilder.setBuildingNodes(buildingNodes);
		treeBuilder.setActiveAlliances(activeAlliances);
		treeBuilder.setMode(Integer.parseInt(settings.getProperty("EMapOverviewPanel.filters",
																  "1")));

		return treeBuilder;
	}

	// class encapsulating the whole menu
	class OverviewMenu extends JMenu implements ActionListener {
		JCheckBoxMenuItem items[];

		/**
		 * Creates a new OverviewMenu object.
		 *
		 * @param label TODO: DOCUMENT ME!
		 * @param mnemonic TODO: DOCUMENT ME!
		 */
		public OverviewMenu(String label, char mnemonic) {
			super(label);
			this.setMnemonic(mnemonic);

			this.add(nodeWrapperFactory.getContextMenu());
			addSeparator();

			JMenuItem item = this.add(getString("menu.filter"));
			item.setEnabled(false);

			if(treeBuilder == null) {
				treeBuilder = createTreeBuilder();
			}

			int mode = treeBuilder.getMode();

			items = new JCheckBoxMenuItem[4];

			for(int i = 0; i < 4; i++) {
				items[i] = new JCheckBoxMenuItem(getString("menu.filter." +
														   String.valueOf(i)));
				items[i].setSelected((mode & (1 << i)) != 0);
				items[i].addActionListener(this);
				this.add(items[i]);
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param actionEvent TODO: DOCUMENT ME!
		 */
		public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
			updateState();
		}

		protected void updateState() {
			if(treeBuilder == null) {
				treeBuilder = createTreeBuilder();
			}

			int mode = treeBuilder.getMode() & treeBuilder.CREATE_ISLANDS;

			for(int i = 0; i < items.length; i++) {
				mode |= ((items[i].isSelected() ? 1 : 0) << i);
			}

			if(mode != treeBuilder.getMode()) {
				treeBuilder.setMode(mode);
				rebuildTree();
				settings.setProperty("EMapOverviewPanel.filters",
									 String.valueOf(mode ^
													treeBuilder.CREATE_ISLANDS));
			}
		}
	}

	// returns the menu that is used by the client
	// we will create it every time the method is invoked since that should be
	// only once...
	public JMenu getMenu() {
		return new OverviewMenu(getString("menu.caption"),
								getString("menu.mnemonic").charAt(0));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getSuperMenu() {
		return "tree";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getSuperMenuTitle() {
		return getString("menu.supertitle");
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
			defaultTranslations.put("prefs.title", "Regions");
			defaultTranslations.put("prefs.sortregions", "Sort regions");
			defaultTranslations.put("prefs.sortbycoordinates", "By coordinate");
			defaultTranslations.put("prefs.sortbyislands", "By island");
			defaultTranslations.put("prefs.regionsorting", "Region sorting");
			defaultTranslations.put("prefs.showislands", "Show islands");
			defaultTranslations.put("prefs.reportorder", "Use report order");
			defaultTranslations.put("prefs.sortbyskills", "By skill");
			defaultTranslations.put("prefs.sortbynames", "By name");
			defaultTranslations.put("prefs.unitsorting", "Unit sorting");

			defaultTranslations.put("prefs.treestructure",
									"Hierarchical tree structure");
			defaultTranslations.put("prefs.treestructure.available",
									"Available structure elements");
			defaultTranslations.put("prefs.treestructure.element.faction",
									"Faction");
			defaultTranslations.put("prefs.treestructure.element.group", "Group");
			defaultTranslations.put("prefs.treestructure.element.combat",
									"Combat status");
			defaultTranslations.put("prefs.treestructure.element.health",
									"Health status");
			defaultTranslations.put("prefs.treeStructure.element.factiondisguise",
									"Faction disguised");
			defaultTranslations.put("prefs.treeStructure.element.trustlevel",
									"Trustlevel");
			defaultTranslations.put("prefs.treestructure.use", "Use");
			defaultTranslations.put("prefs.treestructure.up", "Up");
			defaultTranslations.put("prefs.treestructure.down", "Down");

			defaultTranslations.put("prefs.showskillicons", "Show skill icons");
			defaultTranslations.put("prefs.showcontainericons",
									"Show building and ship icons");
			defaultTranslations.put("prefs.uniticons", "Unit icons");
			defaultTranslations.put("prefs.icontextcolor", "Color of icon text");
			defaultTranslations.put("prefs.showskilllevel", "Show skill level");
			defaultTranslations.put("prefs.skillorder", "Order of skills");
			defaultTranslations.put("prefs.upbutton.caption", "Up");
			defaultTranslations.put("prefs.downbutton.caption", "Down");
			defaultTranslations.put("prefs.refreshlistbutton.caption",
									"Alphabetical");
			defaultTranslations.put("prefs.usebestskill",
									"According to best skill");
			defaultTranslations.put("prefs.usetopmostskill",
									"According topmost skill in list");

			defaultTranslations.put("icontextdialog.title", "Tree icons labels");
			defaultTranslations.put("icontextdialog.txt.info.text",
									"On the left, you can select the position of the icon labels (the center box indicates the position of the icon itself).");
			defaultTranslations.put("icontextdialog.btn.ok.caption", "OK");
			defaultTranslations.put("icontextdialog.btn.cancel.caption",
									"Cancel");
			defaultTranslations.put("icontextdialog.lbl.font.caption", "Font");
			defaultTranslations.put("icontextdialog.chk.bold.caption", "Bold");
			defaultTranslations.put("icontextdialog.chk.italic.caption",
									"Italic");
			defaultTranslations.put("icontextdialog.btn.color.caption",
									"Font color");
			defaultTranslations.put("icontextdialog.colorchooser.title",
									"Font color of icon labels");
			defaultTranslations.put("wrapperfactory.title",
									"Region Tree Entries");
			defaultTranslations.put("prefs.collapse.onlyautoexpanded",
									"Collapse only auto-expanded elements");
			defaultTranslations.put("prefs.collapse.full", "Full");
			defaultTranslations.put("prefs.collapse.faction", "First level");
			defaultTranslations.put("prefs.collapse.none", "No auto-collapse");
			defaultTranslations.put("prefs.expand.ifinside",
									"Only if own units in region");
			defaultTranslations.put("prefs.expand.trustlevel",
									"Only with trustlevel #T or higher");
			defaultTranslations.put("prefs.expand.full", "Full");
			defaultTranslations.put("prefs.expand.faction", "First level");
			defaultTranslations.put("prefs.expand.title", "Tree-Expansion");
			defaultTranslations.put("prefs.expand.none", "No expansion");
			defaultTranslations.put("shortcut.description.5",
									"Backward through history");
			defaultTranslations.put("shortcut.description.3",
									"Backward through history");
			defaultTranslations.put("shortcut.description.4",
									"Forward through history");
			defaultTranslations.put("shortcut.description.2",
									"Forward through history");
			defaultTranslations.put("shortcut.description.1", "Request Focus");
			defaultTranslations.put("shortcut.description.0", "Request Focus");
			defaultTranslations.put("shortcut.title", "Overview");

			defaultTranslations.put("menu.caption", "Region Overview");
			defaultTranslations.put("menu.mnemonic", "R");
			defaultTranslations.put("menu.filter", "Filters");
			defaultTranslations.put("menu.filter.0", "Units");
			defaultTranslations.put("menu.filter.1", "Buildings");
			defaultTranslations.put("menu.filter.2", "Ships");
			defaultTranslations.put("menu.filter.3", "Comments");
			defaultTranslations.put("menu.supertitle", "Tree");
		}

		return defaultTranslations;
	}
}
