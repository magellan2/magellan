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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;

import com.eressea.Alliance;
import com.eressea.Border;
import com.eressea.Building;
import com.eressea.CombatSpell;
import com.eressea.Described;
import com.eressea.DescribedObject;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Group;
import com.eressea.ID;
import com.eressea.Island;
import com.eressea.Item;
import com.eressea.LuxuryPrice;
import com.eressea.NamedObject;
import com.eressea.Potion;
import com.eressea.Region;
import com.eressea.RegionResource;
import com.eressea.Rules;
import com.eressea.Scheme;
import com.eressea.Ship;
import com.eressea.Skill;
import com.eressea.Spell;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.UnitID;
import com.eressea.ZeroUnit;
import com.eressea.completion.AutoCompletion;
import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.demo.desktop.ShortcutListener;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.event.UnitOrdersEvent;
import com.eressea.event.UnitOrdersListener;
import com.eressea.gamebinding.eressea.EresseaConstants;
import com.eressea.relation.ControlRelation;
import com.eressea.relation.PersonTransferRelation;
import com.eressea.relation.UnitTransferRelation;
import com.eressea.rules.BuildingType;
import com.eressea.rules.CastleType;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.ShipType;
import com.eressea.rules.SkillCategory;
import com.eressea.rules.SkillType;
import com.eressea.rules.UnitContainerType;
import com.eressea.swing.BasicRegionPanel;
import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.MenuProvider;
import com.eressea.swing.completion.MultiEditorOrderEditorList;
import com.eressea.swing.context.ContextFactory;
import com.eressea.swing.context.UnitCapacityContextMenu;
import com.eressea.swing.context.UnitContextMenu;
import com.eressea.swing.preferences.ExtendedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.swing.tree.CellObject;
import com.eressea.swing.tree.CellRenderer;
import com.eressea.swing.tree.ContextManager;
import com.eressea.swing.tree.CopyTree;
import com.eressea.swing.tree.ItemCategoryNodeWrapper;
import com.eressea.swing.tree.ItemNodeWrapper;
import com.eressea.swing.tree.NodeWrapperDrawPolicy;
import com.eressea.swing.tree.NodeWrapperFactory;
import com.eressea.swing.tree.PotionNodeWrapper;
import com.eressea.swing.tree.RegionNodeWrapper;
import com.eressea.swing.tree.SimpleNodeWrapper;
import com.eressea.swing.tree.TreeUpdate;
import com.eressea.swing.tree.UnitCommentNodeWrapper;
import com.eressea.swing.tree.UnitContainerCommentNodeWrapper;
import com.eressea.swing.tree.UnitContainerNodeWrapper;
import com.eressea.swing.tree.UnitListNodeWrapper;
import com.eressea.swing.tree.UnitNodeWrapper;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Direction;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.ShipRoutePlanner;
import com.eressea.util.Taggable;
import com.eressea.util.Translations;
import com.eressea.util.Umlaut;
import com.eressea.util.Units;
import com.eressea.util.comparator.AllianceFactionComparator;
import com.eressea.util.comparator.BestSkillComparator;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.NameComparator;
import com.eressea.util.comparator.PotionLevelComparator;
import com.eressea.util.comparator.SkillComparator;
import com.eressea.util.comparator.SkillTypeComparator;
import com.eressea.util.comparator.SkillTypeRankComparator;
import com.eressea.util.comparator.SortIndexComparator;
import com.eressea.util.comparator.SpecifiedSkillTypeSkillComparator;
import com.eressea.util.comparator.SpellLevelComparator;
import com.eressea.util.comparator.UnitSkillComparator;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EMapDetailsPanel extends InternationalizedDataPanel implements SelectionListener,
																			ShortcutListener,
																			ActionListener,
																			TreeUpdate,
																			PreferencesFactory,
																			MenuProvider
{
	private static final Logger log = Logger.getInstance(EMapDetailsPanel.class);

	// GUI elements
	private JTextArea name = null;
	private JTextArea description = null;
	private DefaultTreeModel treeModel = null;
	private DefaultMutableTreeNode rootNode = null;
	private CopyTree tree = null;
	private AutoCompletion orders = null;
	private MultiEditorOrderEditorList editor;
	private Region lastRegion = null;
	private Units unitsTools = null;

	/**
	 * A list containing nodewrapper objects, the expansion state of nodes contained in this list
	 * can be automatically stored and restored
	 */
	private Collection myExpandableNodes = CollectionFactory.createLinkedList();

	/** The currently displayed object */
	private Object displayedObject = null;
	
	/**
	 * A list containing selected units
	 */
	private Collection mySelectedUnits = CollectionFactory.createLinkedList();
	
	
	/** split pane */
	JSplitPane topSplitPane;

	/** split pane */
	JSplitPane bottomSplitPane;
	private static final NumberFormat weightNumberFormat = NumberFormat.getNumberInstance();
	protected NodeWrapperFactory nodeWrapperFactory;
	private List shortCuts;
	protected Collection excludeTags = CollectionFactory.createLinkedList();
	protected AbstractButton addTag;
	protected AbstractButton removeTag;
	protected Container tagContainer;
	protected Container treeContainer;
	protected boolean showTagButtons = false;
	protected boolean allowCustomIcons = true;
	protected ContextManager contextManager;
	protected StealthContextFactory stealthContext;
	protected CombatStateContextFactory combatContext;
	protected CommentContextFactory commentContext;
	protected UnitCommentContextFactory unitCommentContext;
	protected DetailsUnitContextFactory unitContext;
	protected UndoManager undoMgr;
	protected BasicRegionPanel regionPanel;

	// pre-initialize this comparator so it is not created over and
	// over again when needed
	private Comparator sortIndexComparator = new SortIndexComparator(IDComparator.DEFAULT);
	private final ID ironID = StringID.create("Eisen");
	private final ID laenID = StringID.create("Laen");
	private final ID treesID = StringID.create("Baeume");
	private final ID mallornID = StringID.create("Mallorn");
	private final ID sproutsID = StringID.create("Schoesslinge");
	private final ID mallornSproutsID = StringID.create("Mallornschösslinge");
	private final ID stonesID = StringID.create("Steine");

	/**
	 * Creates a new EMapDetailsPanel object.
	 *
	 * @param d TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 * @param _undoMgr TODO: DOCUMENT ME!
	 */
	public EMapDetailsPanel(EventDispatcher d, GameData data, Properties p, UndoManager _undoMgr) {
		super(d, data, p);
		initGUI(_undoMgr);
	}

	private void initGUI(UndoManager _undoMgr) {
		// store undomanager
		undoMgr = _undoMgr;

		nodeWrapperFactory = new NodeWrapperFactory(settings, "EMapDetailsPanel",
													getString("wrapperfactory.title"));
		nodeWrapperFactory.setSource(this);

		// to get the pref-adapter
		Unit temp = new Unit(UnitID.createUnitID(0,10));
		nodeWrapperFactory.createUnitNodeWrapper(temp);
		nodeWrapperFactory.createSkillNodeWrapper(temp,
												  new Skill(new SkillType(StringID.create("Test")),
															0, 0, 0, false), null);
		nodeWrapperFactory.createItemNodeWrapper(new Item(new ItemType(StringID.create("Test")), 0));
		nodeWrapperFactory.createSimpleNodeWrapper(null, null);

		weightNumberFormat.setMaximumFractionDigits(2);
		weightNumberFormat.setMinimumFractionDigits(0);
		unitsTools = (data != null) ? new Units(data.rules) : new Units(null);
		dispatcher.addSelectionListener(this);

		// name text area
		name = new JTextArea();
		name.setLineWrap(false);
		//name.setFont(new Font("SansSerif", Font.PLAIN, name.getFont().getSize()));
		name.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		name.setEditable(false);
		name.getDocument().addUndoableEditListener(_undoMgr);
		name.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if((name.isEditable() == false) || (displayedObject == null)) {
						return;
					}

					if(displayedObject instanceof Unit) {
						Unit u = (Unit) displayedObject;

						if((((u.getName() == null) && (name.getText().equals("") == false)) ||
							   ((u.getName() != null) &&
							   (name.getText().equals(u.getName()) == false))) &&
							   isPrivilegedAndNoSpy(u) && !u.ordersAreNull()) {
							// the following code only changes the name
							// right now it is not necessary to refresh the relations; are we sure??
							data.getGameSpecificStuff().getOrderChanger().addNamingOrder(u,
																						 name.getText());
							dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, u));

							//if (u.cache != null && u.cache.orderEditor != null) {
							//	u.cache.orderEditor.reloadOrders();
							//}
						}
					} else if(displayedObject instanceof UnitContainer) {
						UnitContainer uc = (UnitContainer) displayedObject;
						Unit modUnit = null;

						if(uc instanceof Faction) {
							modUnit = (Unit) uc.units().iterator().next();
						} else {
							modUnit = uc.getOwnerUnit();
						}

						if(isPrivilegedAndNoSpy(modUnit) && !modUnit.ordersAreNull()) {
							if(((uc.getName() == null) && (name.getText().equals("") == false)) ||
								   ((uc.getName() != null) &&
								   (name.getText().equals(uc.getName()) == false))) {
								data.getGameSpecificStuff().getOrderChanger().addNamingOrder(modUnit,
																							 uc,
																							 name.getText());
								dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, modUnit));

								//if (modUnit.cache != null && modUnit.cache.orderEditor != null) {
								//	modUnit.cache.orderEditor.reloadOrders();
								//}
							}
						} else {
							JOptionPane.showMessageDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
														  getString("msg.cannotrename.text"),
														  getString("error"),
														  javax.swing.JOptionPane.WARNING_MESSAGE);
							tree.grabFocus();
						}
					} else if(displayedObject instanceof Island) {
						((Island) displayedObject).setName(name.getText());
					}
				}
			});

		// description text area
		description = new JTextArea();
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setFont(new Font("SansSerif", Font.PLAIN, description.getFont().getSize()));
		description.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		description.setEditable(false);
		description.getDocument().addUndoableEditListener(_undoMgr);
		description.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if((description.isEditable() == false) || (displayedObject == null)) {
						return;
					}

					if(displayedObject instanceof Unit) {
						Unit u = (Unit) displayedObject;

						if((((u.getDescription() == null) &&
							   (description.getText().equals("") == false)) ||
							   ((u.getDescription() != null) &&
							   (description.getText().equals(u.getDescription()) == false))) &&
							   isPrivilegedAndNoSpy(u) && !u.ordersAreNull()) {
							String descr = getDescriptionPart(description.getText());
							String privat = getPrivatePart(description.getText());
							data.getGameSpecificStuff().getOrderChanger().addDescribeUnitOrder(u,
																							   descr);

							if(((u.privDesc == null) && (privat.length() > 0)) ||
								   ((u.privDesc != null) && !privat.equals(u.privDesc))) {
								data.getGameSpecificStuff().getOrderChanger()
									.addDescribeUnitPrivateOrder(u, privat);
								u.privDesc = privat;
							}

							dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, u));

							//if (u.cache != null && u.cache.orderEditor != null) {
							//	u.cache.orderEditor.reloadOrders();
							//}
						}
					} else if(displayedObject instanceof UnitContainer) {
						UnitContainer uc = (UnitContainer) displayedObject;
						Unit modUnit = null;

						if(uc instanceof Faction) {
							modUnit = (Unit) uc.units().iterator().next();
						} else {
							modUnit = uc.getOwnerUnit();
						}

						if(isPrivilegedAndNoSpy(modUnit) && !modUnit.ordersAreNull()) {
							if(((uc.getDescription() == null) &&
								   (description.getText().equals("") == false)) ||
								   ((uc.getDescription() != null) &&
								   (description.getText().equals(uc.getDescription()) == false))) {
								data.getGameSpecificStuff().getOrderChanger()
									.addDescribeUnitContainerOrder(modUnit, uc,
																   normalizeDescription(description.getText()));
								dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, modUnit));

								//if (modUnit.cache != null && modUnit.cache.orderEditor != null) {
								//	modUnit.cache.orderEditor.reloadOrders();
								//}
							}
						} else {
							JOptionPane.showMessageDialog(((JComponent) e.getSource()).getTopLevelAncestor(),
														  getString("msg.cannotdescribe.text"),
														  getString("error"),
														  javax.swing.JOptionPane.WARNING_MESSAGE);
							tree.grabFocus();
						}
					} else if(displayedObject instanceof Island) {
						((Described) displayedObject).setDescription(normalizeDescription(description.getText()));
					}
				}
			});

		JScrollPane descScrollPane = new JScrollPane(description,
													 JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
													 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// ClearLook suggests to remove border
		descScrollPane.setBorder(null);

		// panel combining name and description
		JPanel nameDescPanel = new JPanel(new BorderLayout(0, 2));
		nameDescPanel.add(name, BorderLayout.NORTH);
		nameDescPanel.add(descScrollPane, BorderLayout.CENTER);
		nameDescPanel.setPreferredSize(new Dimension(100, 100));

		// tree
		rootNode = new DefaultMutableTreeNode(null);
		treeModel = new DefaultTreeModel(rootNode);
		tree = new CopyTree(treeModel);
		tree.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						handleValueChange();
					}
				}
			});
		
		// keeping track of selection changes for mySelectedUnits (fiete)
		tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent tslE) {
					handleTreeSelectionChangeEvent(tslE);
				}
			});
		
		
		// FIXME: bugzilla bug #823 ?
		tree.putClientProperty("JTree.lineStyle", "Angled");

		tree.setRootVisible(false);

		tree.setEditable(true);
		tree.setCellRenderer(new CellRenderer(getMagellanContext()));
		tree.getCellEditor().addCellEditorListener(new CellEditorListener() {
				public void editingCanceled(ChangeEvent e) {
				}

				public void editingStopped(ChangeEvent e) {
					DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

					if((selectedNode != null) && selectedNode.getUserObject() instanceof UnitContainerCommentNodeWrapper) {
						UnitContainerCommentNodeWrapper cnW = (UnitContainerCommentNodeWrapper) selectedNode.getUserObject();
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
						UnitContainer uc = cnW.getUnitContainer();

						if((uc != null) && (uc.comments != null)) {
							uc.comments.set(parent.getIndex(selectedNode),
											tree.getCellEditor().getCellEditorValue());
						}
						show(displayedObject,false);
					}
				}
			});
		tree.getCellEditor().addCellEditorListener(new CellEditorListener() {
			public void editingCanceled(ChangeEvent e) {
			}

			public void editingStopped(ChangeEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

				if((selectedNode != null) && selectedNode.getUserObject() instanceof UnitCommentNodeWrapper) {
					UnitCommentNodeWrapper cnW = (UnitCommentNodeWrapper) selectedNode.getUserObject();
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
					Unit u = cnW.getUnit();

					if((u != null) && (u.comments != null)) {
						u.comments.set(parent.getIndex(selectedNode),
										tree.getCellEditor().getCellEditorValue());
					}
					show(displayedObject,false);
				}
			}
		});

		contextManager = new ContextManager(tree,dispatcher);
		stealthContext = new StealthContextFactory();
		combatContext = new CombatStateContextFactory();
		commentContext = new CommentContextFactory();
		unitCommentContext = new UnitCommentContextFactory();
		unitContext = new DetailsUnitContextFactory();
		contextManager.putSimpleObject(CommentListNode.class, commentContext);
		contextManager.putSimpleObject(CommentNode.class, commentContext);
		contextManager.putSimpleObject(UnitCommentNode.class, unitCommentContext);
		contextManager.putSimpleObject(UnitCommentListNode.class, unitCommentContext);
		contextManager.putSimpleObject(UnitNodeWrapper.class, unitContext);
		contextManager.putSimpleObject(UnitListNodeWrapper.class, unitContext);
		contextManager.putSimpleObject(DefaultMutableTreeNode.class, unitContext);
		
		
		
		JScrollPane treeScrollPane = new JScrollPane(tree);

		// ClearLook suggests to remove border
		treeScrollPane.setBorder(null);

		// panel combining the region info and the tree
		JPanel pnlRegionInfoTree = new JPanel(new BorderLayout());
		pnlRegionInfoTree.add(regionPanel = new BasicRegionPanel(dispatcher, settings),
							  BorderLayout.NORTH);
		pnlRegionInfoTree.add(treeScrollPane, BorderLayout.CENTER);

		tagContainer = new JPanel();
		tagContainer.setLayout(new GridLayout(1, 2));
		treeContainer = pnlRegionInfoTree;

		addTag = new JButton(getString("addtag.caption"));
		addTag.addActionListener(this);
		addTag.setEnabled(false);
		tagContainer.add(addTag);

		removeTag = new JButton(getString("removetag.caption"));
		removeTag.addActionListener(this);
		removeTag.setEnabled(false);
		tagContainer.add(removeTag);

		showTagButtons = settings.getProperty("EMapDetailsPanel.ShowTagButtons", "false").equals("true");

		if(showTagButtons) {
			pnlRegionInfoTree.add(tagContainer, BorderLayout.SOUTH);
		}
		
		allowCustomIcons = settings.getProperty("EMapDetailsPanel.AllowCustomIcons", "true").equals("true");

		// split pane combining name, desc & tree
		topSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, nameDescPanel, pnlRegionInfoTree);
		topSplitPane.setOneTouchExpandable(true);
		editor = new MultiEditorOrderEditorList(dispatcher, data, settings, _undoMgr);

		// build auto completion structure
		orders = new AutoCompletion(dispatcher.getMagellanContext());
		orders.attachEditorManager(editor);
		shortCuts = CollectionFactory.createArrayList(3);
		shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_MASK));
		shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.ALT_MASK));

		// toggle "limit make completion"
		shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));

		// split pane combining top split pane and orders
		bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, editor);
		bottomSplitPane.setOneTouchExpandable(true);
		setLayout(new GridLayout(1, 0));
		add(bottomSplitPane);

		// adjust split panes
		bottomSplitPane.setDividerLocation(Integer.parseInt(settings.getProperty("EMapDetailsPanel.bottomSplitPane",
																				 "400")));
		topSplitPane.setDividerLocation(Integer.parseInt(settings.getProperty("EMapDetailsPanel.topSplitPane",
																			  "200")));

		// save split pane changes
		ComponentAdapter splitPaneListener = new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				settings.setProperty("EMapDetailsPanel.bottomSplitPane",
									 "" + bottomSplitPane.getDividerLocation());
				settings.setProperty("EMapDetailsPanel.topSplitPane",
									 "" + topSplitPane.getDividerLocation());
			}
		};

		treeScrollPane.addComponentListener(splitPaneListener);
		nameDescPanel.addComponentListener(splitPaneListener);
		editor.addComponentListener(splitPaneListener);

		// register for shortcuts
		DesktopEnvironment.registerShortcutListener(this);

		// update this component if the orders of the currently displayed unit changed
		dispatcher.addUnitOrdersListener(new UnitOrdersListener() {
				public void unitOrdersChanged(UnitOrdersEvent e) {
					if(e.getRelatedUnits().contains(EMapDetailsPanel.this.displayedObject)) {
						EMapDetailsPanel.this.show(e.getUnit(), false);
					}
				}
			});

		excludeTags.add("magStyle");
		excludeTags.add("regionicon");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperFactory getNodeWrapperFactory() {
		return nodeWrapperFactory;
	}

	/*
	 * Function to translate a\nb (view representation to "a\\nb" (String representation)
	 * It also translates \r to \n
	 */
	protected String normalizeDescription(String s) {
		if(s == null) {
			return null;
		}

		StringBuffer ret = new StringBuffer(s.length());

		for(StringTokenizer st = new StringTokenizer(s, "\n\r"); st.hasMoreTokens();) {
			ret.append(st.nextToken());

			if(st.hasMoreTokens()) {
				ret.append("\\n");
			}
		}

		return ret.toString();
	}

	private static final String DESCRIPTION_SEPARATOR = "\n---\n";

	protected String getDescriptionPart(String s) {
		if(s.indexOf(DESCRIPTION_SEPARATOR) == -1) {
			return normalizeDescription(s);
		}

		return normalizeDescription(s.substring(0, s.indexOf(DESCRIPTION_SEPARATOR)));
	}

	protected String getPrivatePart(String s) {
		if(s.indexOf(DESCRIPTION_SEPARATOR) == -1) {
			return "";
		}

		return normalizeDescription(s.substring(s.indexOf(DESCRIPTION_SEPARATOR) +
												DESCRIPTION_SEPARATOR.length()));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean isPrivilegedAndNoSpy(Unit u) {
		return (u != null) && isPrivileged(u.getFaction()) && !u.isSpy();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param f TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean isPrivileged(Faction f) {
		return (f != null) && (f.isPrivileged());
	}

	private void setNameAndDescription(DescribedObject d, boolean isEditable) {
		if(d == null) {
			return;
		}

		setNameAndDescription(this.data.getTranslation(d), d.getDescription(), isEditable);
	}

	private void setNameAndDescription(String n, String desc, boolean isEditable) {
		//pavkovic 2003.10.07: dont notify this as undo action:
		// disable undo
		name.getDocument().removeUndoableEditListener(undoMgr);
		description.getDocument().removeUndoableEditListener(undoMgr);

		// change values
		name.setText((n == null) ? "" : n);
		name.setCaretPosition(0);
		name.setEditable(isEditable);
		description.setText((desc == null) ? "" : desc);
		description.setCaretPosition(0);
		description.setEditable(isEditable);

		// enable undo
		name.getDocument().addUndoableEditListener(undoMgr);
		description.getDocument().addUndoableEditListener(undoMgr);
	}

	private void showRegion(Region r) {
		// make editable for privileged units
		setNameAndDescription(r, (r != null) && isPrivilegedAndNoSpy(r.getOwnerUnit()));

		// build tree
		appendRegionInfo(r, rootNode, myExpandableNodes);

		// enable enable tag button (disable remove tag button?)
		addTag.setEnabled(true);
		removeTag.setEnabled(false);
	}

	/**
	 * Shows a tree: Terrain  : region.type Coordinates  : region.coordinates guarding units (Orc
	 * Infestination) (resources) (peasants) (luxuries) (schemes) (comments) (tags)
	 *
	 * @param r 
	 * @param parent 
	 * @param expandableNodes 
	 */
	private void appendRegionInfo(Region r, DefaultMutableTreeNode parent,
								  Collection expandableNodes) {
		// terrain type
		parent.add(createSimpleNode(getString("node.terrain") + ": " + r.getType().getName(),
									r.getType().getID() + "-detail"));

		// terrain coordinates
		parent.add(createSimpleNode(getString("node.coordinates") + ": " + r.getID(), "koordinaten"));

		// region guards
		appendRegionGuardInfo(r, parent, expandableNodes);

		// orc infestation
		if(r.orcInfested) {
			parent.add(createSimpleNode(getString("node.orcinfestation"), "orkinfest"));
		}

		// resources
		appendRegionResourceInfo(r, parent, expandableNodes);

		// peasants
		appendRegionPeasantInfo(r, parent, expandableNodes);

		// luxuries
		appendRegionLuxuriesInfo(r, parent, expandableNodes);

		// schemes
		appendRegionSchemes(r, parent, expandableNodes);

		// comments
		appendComments(r, parent, expandableNodes);

		// tags
		appendTags(r, parent, expandableNodes);
	}

	/**
	 * This function adds a node with subnodes to given parent - Luxuries: amount luxury item 1:
	 * price 1 ... luxury item n: price n
	 *
	 * @param r 
	 * @param parent 
	 * @param expandableNodes 
	 */
	private void appendRegionLuxuriesInfo(Region r, DefaultMutableTreeNode parent,
										  Collection expandableNodes) {
		if((r == null) || (r.prices == null) || r.prices.values().isEmpty()) {
			return;
		}
		/*
		DefaultMutableTreeNode luxuriesNode = new DefaultMutableTreeNode(getString("node.trade") +
																		 ": " +
																		 getDiffString(r.maxLuxuries(),
																					   r.maxOldLuxuries()));
		*/
		DefaultMutableTreeNode luxuriesNode = createSimpleNode(getString("node.trade") + ": " +
				 getDiffString(r.maxLuxuries(),r.maxOldLuxuries()), "handeln");
		
		parent.add(luxuriesNode);
		expandableNodes.add(new NodeWrapper(luxuriesNode, "EMapDetailsPanel.RegionLuxuriesExpanded"));

		for(Iterator luxuries = r.prices.values().iterator(); luxuries.hasNext();) {
			LuxuryPrice p = (LuxuryPrice) luxuries.next();
			int oldPrice = -1;

			if(r.oldPrices != null) {
				LuxuryPrice old = (LuxuryPrice) r.oldPrices.get(p.getItemType().getID());

				if(old != null) {
					oldPrice = old.getPrice();
				}
			}

			luxuriesNode.add(createSimpleNode(p.getItemType().getName() + ": " +
											  getDiffString(p.getPrice(), oldPrice),
											  "items/" + p.getItemType().getIconName()));
		}
	}

	/**
	 * This function adds a node with subnodes to given parent - Peasants: amount / max amount
	 * recruit: recruit amount of recruit amount silver: silver surplus: surplus wage: entertain:
	 *
	 * @param r 
	 * @param parent 
	 * @param expandableNodes 
	 */
	private void appendRegionPeasantInfo(Region r, DefaultMutableTreeNode parent,
										 Collection expandableNodes) {
		if(r.peasants <= 0) {
			return;
		}

		// peasants
		int maxWorkers = r.getRegionType().getInhabitants() - (Math.max(r.trees, 0) * 8) -
						 (Math.max(r.sprouts, 0) * 4);
		int workers = Math.min(maxWorkers, r.peasants);
		int surplus = (workers * r.getPeasantWage()) - (r.peasants * 10);
		int oldWorkers = Math.min(maxWorkers, r.oldPeasants);
		int oldSurplus = -1;

		if((oldWorkers != -1) && (r.oldWage != -1) && (r.oldPeasants != -1)) {
			oldSurplus = (oldWorkers * (r.oldWage + 1)) - (r.oldPeasants * 10);
		}

		String peasantsInfo = getDiffString(r.peasants, r.oldPeasants, r.getModifiedPeasants())
								  .toString();
		peasantsInfo += (" / " + maxWorkers);

		DefaultMutableTreeNode peasantsNode = createSimpleNode(getString("node.peasants") + ": " +
															   peasantsInfo, "bauern");
		parent.add(peasantsNode);
		expandableNodes.add(new NodeWrapper(peasantsNode, "EMapDetailsPanel.RegionPeasantsExpanded"));

		// recruit
		peasantsNode.add(createSimpleNode(getString("node.recruit") + ": " +
										  getDiffString(r.maxRecruit(), r.maxRecruit(),
														r.modifiedRecruit()) + " " +
										  getString("node.of") + r.maxRecruit(), "rekruten"));

		// silver
		peasantsNode.add(createSimpleNode(getString("node.silver") + ": " +
										  getDiffString(r.silver, r.oldSilver), "items/silber"));

		// surplus
		peasantsNode.add(createSimpleNode(getString("node.surplus") + ": " +
										  getDiffString(surplus, oldSurplus), "items/silber"));

		// wage
		int wage = r.getPeasantWage() - 1;

		if((getWage(wage, false) != r.wage) && (getWage(wage, true) != r.wage)) {
			// well, this can't be standard or orc wage, so let's
			// display this special wage
			wage = r.wage;
		}

		StringBuffer nodeText = new StringBuffer(getString("node.wage")).append(": ").append(getDiffString(getWage(wage,
																												   false),
																										   getWage(r.oldWage,
																												   false)));

		if(data.rules.getRace(EresseaConstants.R_ORKS) != null) {
			nodeText.append(", ").append(data.rules.getRace(EresseaConstants.R_ORKS).getName())
					.append(": ").append(getDiffString(getWage(wage, true), getWage(r.oldWage, true)));
		}

		peasantsNode.add(createSimpleNode(nodeText.toString(), "lohn"));

		// entertain
		peasantsNode.add(createSimpleNode(getString("node.enterainment") + ": " +
										  getDiffString(r.maxEntertain(), r.maxOldEntertain()),
										  "items/silber"));
	}

	 /**
	  * Appends the resources of the region. 
	  *
	  * @param r
	  * @param parent
	  * @param expandableNodes
	  */
	private void appendRegionResourceInfo(Region r, DefaultMutableTreeNode parent,
										  Collection expandableNodes) {
		// DefaultMutableTreeNode resourceNode = new DefaultMutableTreeNode(getString("node.resources"));
		DefaultMutableTreeNode resourceNode = createSimpleNode(getString("node.resources"), "ressourcen");
		String icon = null;
		if(!r.resources().isEmpty()) {
			// resources of region
			for(Iterator iter = r.resources().iterator(); iter.hasNext();) {
				RegionResource res = (RegionResource) iter.next();
				int oldValue = findOldValueByResourceType(r, res);
				appendResource(res, resourceNode, oldValue);
			}
		} else {
			// here we dont have resources, so make the "classic" way to create resource information
			if((r.trees > 0) || (r.oldTrees > 0)) {
				DefaultMutableTreeNode treeNode;

				if(r.mallorn) {
					icon = "items/" + mallornID;
					if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")){
						icon += "_region";
					}
					treeNode = createSimpleNode(getString("node.mallorntrees") + ": " +
												getDiffString(r.trees, r.oldTrees),
												icon);
				} else {
					icon = "items/" + treesID;
					if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")){
						icon += "_region";
					}
					treeNode = createSimpleNode(getString("node.trees") + ": " +
												getDiffString(r.trees, r.oldTrees),
												icon);
				}

				resourceNode.add(treeNode);
			}

			if((r.iron > 0) || (r.oldIron > 0)) {
				icon = "items/eisen";
				if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")){
					icon += "_region";
				}
				resourceNode.add(createSimpleNode(data.rules.getItemType(StringID.create("Eisen"))
															.getName() + ": " +
												  getDiffString(r.iron, r.oldIron), icon));
			}

			if((r.laen > 0) || (r.oldLaen > 0)) {
				icon = "items/" + laenID;
				if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")){
					icon += "_region";
				}
				resourceNode.add(createSimpleNode(getString("node.laen") + ": " +
												  getDiffString(r.laen, r.oldLaen),
												  icon));
			}
		}

		// horse
		if((r.horses > 0) || (r.oldHorses > 0)) {
			icon = "items/pferd";
			if (getMagellanContext().getImageFactory().existImageIcon(icon + "_region")){
				icon += "_region";
			}
			resourceNode.add(createSimpleNode(getString("node.horses") + ": " +
											  getDiffString(r.horses, r.oldHorses), icon));
		}

		// herb
		if(r.herb != null) {
			StringBuffer sb = new StringBuffer(getString("node.herbs")).append(": ").append(r.herb.getName());

			if(r.herbAmount != null) {
				sb.append(" (").append(r.herbAmount).append(")");
			}

			icon = null;

			try {
				icon = r.herb.getMakeSkill().getSkillType().getID().toString();
			} catch(Exception exc) {
				icon = "kraeuterkunde";
			}

			resourceNode.add(createSimpleNode(sb.toString(), icon));
		}

		if(resourceNode.getChildCount() > 0) {
			parent.add(resourceNode);
			expandableNodes.add(new NodeWrapper(resourceNode,
												"EMapDetailsPanel.RegionResourcesExpanded"));
		}
	}

	private int findOldValueByResourceType(Region r, RegionResource res) {
		if((r == null) || (res == null)) {
			return -1;
		}

		if(res.getType().getID().equals(ironID)) {
			return r.oldIron;
		}

		if(res.getType().getID().equals(laenID)) {
			return r.oldLaen;
		}

		if(res.getType().getID().equals(treesID) || res.getType().getID().equals(mallornID)) {
			return r.oldTrees;
		}

		if(res.getType().getID().equals(sproutsID) || res.getType().getID().equals(mallornSproutsID)) {
			return r.oldSprouts;
		}

		if(res.getType().getID().equals(stonesID)) {
			return r.oldStones;
		}

		return -1;
	}

	private void appendMultipleRegionInfo(Collection r, DefaultMutableTreeNode parent,
										  Collection expandableNodes) {
		// collect the data
		// Sort regions for types. Key: RegionType; Value: LinkedList containing the region-objects
		Map regions = CollectionFactory.createHashtable();

		// Collect resources: key: ItemType; value: Integer
		Map resources = CollectionFactory.createHashtable();

		// Collect herbs: key: ItemType; value: LinkedList containing the region-objects
		Map herbs = CollectionFactory.createHashtable();

		// Count peasants, silver
		int peasants = 0;
		int silver = 0;

		for(Iterator iter = r.iterator(); iter.hasNext();) {
			Region region = (Region) iter.next();

			if(region.peasants != -1) {
				peasants += region.peasants;
			}

			if(region.silver != -1) {
				silver += region.silver;
			}

			if(region.herb != null) {
				List regionList = (List) herbs.get(region.herb);

				if(regionList == null) {
					regionList = CollectionFactory.createLinkedList();
					herbs.put(region.herb, regionList);
				}

				regionList.add(region);
			}

			List list = (List) regions.get(region.getType());

			if(list == null) {
				list = CollectionFactory.createLinkedList();
				regions.put(region.getType(), list);
			}

			list.add(region);

			if(!region.resources().isEmpty()) {
				for(Iterator iterator = region.resources().iterator(); iterator.hasNext();) {
					RegionResource res = (RegionResource) iterator.next();
					Integer amount = (Integer) resources.get(res.getType());
					int i = res.getAmount();

					if(amount != null) {
						i += amount.intValue();
					}

					resources.put(res.getType(), new Integer(i));
				}
			}

			if(region.horses > 0) {
				ItemType iType = data.rules.getItemType(StringID.create("Pferd"));
				Integer amount = (Integer) resources.get(iType);
				int i = region.horses;

				if(amount != null) {
					i += amount.intValue();
				}

				resources.put(iType, new Integer(i));
			}
		}

		// Now the data is prepared. Build the tree:
		// peasants
		parent.add(createSimpleNode(getString("node.peasants") + ": " + peasants, "bauern"));

		// silver
		parent.add(createSimpleNode(getString("node.silver") + ": " + silver, "items/silber"));

		// terrains sorted by region type
		DefaultMutableTreeNode terrainsNode = new DefaultMutableTreeNode(getString("node.terrains"));
		parent.add(terrainsNode);
		expandableNodes.add(new NodeWrapper(terrainsNode, "EMapDetailsPanel.RegionTerrainsExpanded"));

		List sortedList = CollectionFactory.createLinkedList(regions.keySet());
		Collections.sort(sortedList, new NameComparator(IDComparator.DEFAULT));

		for(ListIterator iter = sortedList.listIterator(); iter.hasNext();) {
			UnitContainerType rType = (UnitContainerType) iter.next();
			List list = (List) regions.get(rType);
			Collections.sort(list, new NameComparator(IDComparator.DEFAULT));

			DefaultMutableTreeNode regionsNode = createSimpleNode(rType.getName() + ": " +
																  list.size(),
																  rType.getID().toString());
			terrainsNode.add(regionsNode);

			for(Iterator iter2 = list.iterator(); iter2.hasNext();) {
				Region region = (Region) iter2.next();
				int persons = 0;

				for(Iterator iter3 = region.units().iterator(); iter3.hasNext();) {
					persons += ((Unit) iter3.next()).getPersons();
				}

				regionsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createRegionNodeWrapper(region,
																									  persons)));
			}
		}

		// resources of the regions sorted by name,id of resource
		DefaultMutableTreeNode resourcesNode = new DefaultMutableTreeNode(getString("node.resources"));
		sortedList = CollectionFactory.createLinkedList(resources.keySet());
		Collections.sort(sortedList, new NameComparator(IDComparator.DEFAULT));

		for(ListIterator iter = sortedList.listIterator(); iter.hasNext();) {
			ItemType resType = (ItemType) iter.next();
			int amount = ((Integer) resources.get(resType)).intValue();

			if(amount > 0) {
				resourcesNode.add(createSimpleNode(resType.getName() + ": " + amount,
												   "items/" + resType.getIconName()));
			}
		}

		if(resourcesNode.getChildCount() > 0) {
			parent.add(resourcesNode);
			expandableNodes.add(new NodeWrapper(resourcesNode,
												"EMapDetailsPanel.RegionResourcesExpanded"));
		}

		// herbs of the regions sorted by name, id of herb
		DefaultMutableTreeNode herbsNode = new DefaultMutableTreeNode(getString("node.herbs"));
		sortedList = CollectionFactory.createLinkedList(herbs.keySet());
		Collections.sort(sortedList, new NameComparator(IDComparator.DEFAULT));

		for(ListIterator iter = sortedList.listIterator(); iter.hasNext();) {
			ItemType herbType = (ItemType) iter.next();
			List regionList = (List) herbs.get(herbType);
			int i = regionList.size();
			DefaultMutableTreeNode regionsNode = new DefaultMutableTreeNode(herbType.getName() +
																			": " + i, true);

			// m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(herbType.getName() + ": " + i, herbType.getIconName()));
			herbsNode.add(regionsNode);
			Collections.sort(regionList, new NameComparator(IDComparator.DEFAULT));

			for(ListIterator iter2 = regionList.listIterator(); iter2.hasNext();) {
				Region myRegion = (Region) iter2.next();
				regionsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createRegionNodeWrapper(myRegion)));
			}
		}

		if(herbsNode.getChildCount() > 0) {
			parent.add(herbsNode);
			expandableNodes.add(new NodeWrapper(herbsNode, "EMapDetailsPanel.HerbStatisticExpanded"));
		}
	}

	/**
	 * Returns a simple node
	 *
	 * @param obj TODO: DOCUMENT ME!
	 * @param icons TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private DefaultMutableTreeNode createSimpleNode(Object obj, String icons) {
		return new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(obj, icons));
	}

    private DefaultMutableTreeNode createSimpleNode(NamedObject obj, String icons) {
        return new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(obj,this.data.getTranslation(obj),(Object) icons));
    }
	/**
	 * Return a string showing the signed difference of the two int values in the form
	 * "<tt>current</tt> [&lt;sign&gt;&lt;number&gt;]". If the two values are equal or if one of
	 * them is -1, an empty string is returned.
	 *
	 * @param current TODO: DOCUMENT ME!
	 * @param old TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private StringBuffer getDiffString(int current, int old) {
		return getDiffString(current, old, current);
	}

	/**
	 * Return a string showing the signed difference of the three int values in the form
	 * "<tt>current</tt> (future) [&lt;sign&gt;&lt;change&gt;]". e.g. (1,2,1) returns 1 [-1] e.g.
	 * (1,2,2) returns 1 (2) [-1] e.g. (1,1,2) returns 1 (2) e.g. (1,1,1) returns 1 If future ==
	 * -1, then ignore
	 *
	 * @param current TODO: DOCUMENT ME!
	 * @param old TODO: DOCUMENT ME!
	 * @param future TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private StringBuffer getDiffString(int current, int old, int future) {
		StringBuffer ret = new StringBuffer(String.valueOf(current));

		if(current == -1) {
			return ret;
		}

		if((old != -1) && (current != old)) {
			ret.append(" ");
			ret.append(getDiffString(current - old, true));
		}

		// future value is treated differently, also negative numbers may exist
		if(current != future) {
			ret.append(" ");
			ret.append(getDiffString(future, false));
		}

		return ret;
	}

	private StringBuffer getDiffString(int value, boolean printSign) {
		StringBuffer ret = new StringBuffer(4);

		if(printSign) {
			ret.append("[");

			if(value > 0) {
				ret.append("+");
			}

			ret.append(value);
			ret.append("]");
		} else {
			ret.append("(");
			ret.append(value);
			ret.append(")");
		}

		return ret;
	}

	/**
	 * Appends a folder with the (region) resources to the specified parent node.
	 *
	 * @param r
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendRegionGuardInfo(Region r, DefaultMutableTreeNode parent,
									   Collection expandableNodes) {
		Set parties = CollectionFactory.createHashSet();
		Set units = CollectionFactory.createHashSet();

		for(Iterator iter = r.units().iterator(); iter.hasNext();) {
			Unit unit = (Unit) iter.next();

			if(unit.guard != 0) {
				parties.add(unit.getFaction());
				units.add(unit);
			}
		}

		if(parties.size() > 0) {
			// DefaultMutableTreeNode guardRoot = new DefaultMutableTreeNode(getString("node.guarded") + ": ");
			// Fiete 20060911: added support for "bewacht" - icon
			DefaultMutableTreeNode guardRoot = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.guarded") +
					  ": ",
					  "bewacht"));
			
			parent.add(guardRoot);
			expandableNodes.add(new NodeWrapper(guardRoot, "EMapDetailsPanel.RegionGuardExpanded"));

			for(Iterator iter = parties.iterator(); iter.hasNext();) {
				Faction faction = (Faction) iter.next();
				int iGuardFlags = 0;
				StringBuffer strFaction = new StringBuffer();
				DefaultMutableTreeNode guardParty = new DefaultMutableTreeNode(strFaction);
				guardRoot.add(guardParty);

				Iterator iterUnits = units.iterator();

				while(iterUnits.hasNext()) {
					Unit unit = (Unit) iterUnits.next();

					if(unit.getFaction().equals(faction)) {
						DefaultMutableTreeNode guardUnit = new DefaultMutableTreeNode(nodeWrapperFactory.createUnitNodeWrapper(unit));
						guardParty.add(guardUnit);
						iGuardFlags |= unit.guard;
					}
				}

				strFaction.append(faction.toString() + ", ");
				strFaction.append(Unit.guardFlagsToString(iGuardFlags));
			}
		}
	}

	/**
	 * Appends a folder with the (region) resources to the specified parent node.
	 * 
	 * @param res
	 * @param parent
	 * @param oldValue 
	 */
	private void appendResource(RegionResource res, DefaultMutableTreeNode parent, int oldValue) {
		StringBuffer sb = new StringBuffer();
		sb.append(res.getType().getName());

		if((res.getAmount() != -1) || (res.getSkillLevel() != -1)) {
			sb.append(": ");

			if(res.getAmount() != -1) {
				sb.append(getDiffString(res.getAmount(), oldValue));

				if(res.getSkillLevel() != -1) {
					sb.append(" / ");
				}
			}

			if(res.getSkillLevel() != -1) {
				sb.append("T").append(res.getSkillLevel());
			}
		}

		String icon = res.getType().getIconName();

		if(icon.equalsIgnoreCase("Steine")) {
			icon = "stein";
		}
		
		if (getMagellanContext().getImageFactory().existImageIcon("items/" + icon + "_region")){
			icon = icon + "_region";
		}
		
		parent.add(createSimpleNode(sb.toString(), "items/" + icon));
	}

	/**
	 * Appends a folder with the schemes in the specified region to the specified parent node.
	 *
	 * @param region 
	 * @param parent 
	 * @param expandableNodes 
	 */
	private void appendRegionSchemes(Region region, DefaultMutableTreeNode parent,
									 Collection expandableNodes) {
		if(region.schemes().isEmpty()) {
			return;
		}

		DefaultMutableTreeNode schemeNode = new DefaultMutableTreeNode(getString("node.schemes"));
		parent.add(schemeNode);
		expandableNodes.add(new NodeWrapper(schemeNode, "EMapDetailsPanel.RegionSchemesExpanded"));

		for(Iterator iter = region.schemes().iterator(); iter.hasNext();) {
			Scheme s = (Scheme) iter.next();
			schemeNode.add(new DefaultMutableTreeNode(s));
		}
	}

	// NOTE: Don't know if to add the exclusion code for
	//       well-known external tags (magStyle, regionicon)

	/**
	 * Appends a folder with the external tags of a unit container to the given node.
	 *
	 * @param uc 
	 * @param parent 
	 * @param expandableNodes 
	 */
	private void appendTags(UnitContainer uc, DefaultMutableTreeNode parent,
							Collection expandableNodes) {
		appendTags(uc, parent, expandableNodes, uc.getType().getID().toString() + "TagsExpanded");
	}

	/**
	 * Appends a folder with the external tags of a unit to the given node.
	 *
	 * @param u 
	 * @param parent 
	 * @param expandableNodes 
	 */
	private void appendTags(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		appendTags(u, parent, expandableNodes, "UnitTagsExpanded");
	}

	/**
	 * Appends the tags of a taggable.
     *
	 * @param taggable
	 * @param parent
	 * @param expandableNodes
	 * @param nodeInfo An identifier for the expandable node
	 */
	private void appendTags(Taggable taggable, DefaultMutableTreeNode parent, Collection expandableNodes,
							String nodeInfo) {
		if(!taggable.hasTags()) {
			return;
		}

		DefaultMutableTreeNode tags = new DefaultMutableTreeNode(getString("node.tags"));

		for(Iterator iter = taggable.getTagMap().keySet().iterator(); iter.hasNext();) {
			String tempName = (String) iter.next();
			String value = taggable.getTag(tempName);
			tags.add(new DefaultMutableTreeNode(tempName + ": " + value));
		}

		parent.add(tags);
		expandableNodes.add(new NodeWrapper(tags, "EMapDetailsPanel." + nodeInfo));
	}

	private void showFaction(Faction f) {
		setNameAndDescription(f, isPrivileged(f));
		appendFactionsInfo(Collections.singleton(f), rootNode, myExpandableNodes);
	}

	/**
	 * Append information for several selected factions.
	 *
	 * @param factions
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendFactionsInfo(Collection factions, DefaultMutableTreeNode parent,
									Collection expandableNodes) {
		
		// if only one faction selected...show faction info
		if (factions !=null && factions.size()==1){
			Object[] oO = factions.toArray();
			Object o = oO[0];
			if (o instanceof Faction) {
				Faction f = (Faction) o;
				appendFactionInfo(f, parent, expandableNodes);
			}
		}
		
		if(lastRegion != null) {
			Collection units = CollectionFactory.createLinkedList();

			for(Iterator iter = lastRegion.units().iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();

				if((u.getFaction() != null) && factions.contains(u.getFaction())) {
					units.add(u);
				}
			}

			appendUnitsInfo(units, parent, expandableNodes);
		}

		// comments
		for(Iterator iter = factions.iterator(); iter.hasNext();) {
			appendComments((Faction) iter.next(), parent, expandableNodes);
		}
	}

	/** 
	 * Append summary information for several selected units.
	 *
	 * @param units
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendUnitsInfo(Collection units, DefaultMutableTreeNode parent,
								 Collection expandableNodes) {
		Map skills = CollectionFactory.createHashtable();

		// key: racename (string), Value: Integer-Object containing number of persons of that race
		// Fiete: Value: raceInfo with realRace ( nor Prefix, and the amount of Persons
		Map races = CollectionFactory.createHashtable();

		
		// Fiete: calculate weight and modified weight within this loop
		float uWeight = 0;
		float modUWeight = 0;
		
		
		
		for(Iterator iter = units.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			
			// weight (Fiete)
			Float actUWeight = new Float(u.getWeight() / 100.0F);
			uWeight += actUWeight.floatValue();
			
			Float actModUWeight= new Float(u.getModifiedWeight() / 100.0F);
			modUWeight += actModUWeight.floatValue();
			
			RaceInfo rInfo = (RaceInfo) races.get(u.getRaceName(data));
			if (rInfo == null){
				rInfo = new RaceInfo();
				// rInfo.raceNoPrefix = com.eressea.util.Umlaut.convertUmlauts(u.getRealRaceName());
				// umlaut check is done late when loading the image
				rInfo.raceNoPrefix = u.getRealRaceName();
			}
			
			rInfo.amount +=u.getPersons();
			rInfo.amount_modified += u.getModifiedPersons();
			
			races.put(u.getRaceName(data), rInfo);
			

			for(Iterator i = u.getSkills().iterator(); i.hasNext();) {
				Skill skill = (Skill) i.next();
				SkillStatItem stored = (SkillStatItem) skills.get(skill.getName() +
																  skill.getLevel());

				if(stored != null) {
					stored.unitCounter += u.getPersons();
					stored.units.add(u);
				} else {
					stored = new SkillStatItem(skill, u.getPersons());
					stored.units.add(u);
					skills.put(skill.getName() + skill.getLevel(), stored);
				}
			}
		}

		for(Iterator iter = races.keySet().iterator(); iter.hasNext();) {
			String race = (String) iter.next();
			RaceInfo rI = (RaceInfo) races.get(race);
			int i = rI.amount;
			int i_modified = rI.amount_modified;
			String personIconName = "person";
			/**
			String gifNameEN = getString(rI.raceNoPrefix);
			if (!gifNameEN.equalsIgnoreCase(rI.raceNoPrefix)){
				personIconName = gifNameEN;
			}
			*/
			// we check if specific icon for race exists, if so, we use it
			// Fiete 20061218
			if (getMagellanContext().getImageFactory().existImageIcon(rI.raceNoPrefix)){
				personIconName = rI.raceNoPrefix;
			}
			if (i_modified==i){
				parent.add(createSimpleNode(i + " " + race, personIconName));
			} else {
				parent.add(createSimpleNode(i + " (" + i_modified + ") " + race, personIconName));
			}
		}

		// weight (Fiete)
		String text = getString("node.totalweight") + ": " + weightNumberFormat.format(uWeight);

		if(uWeight != modUWeight) {
			text += (" (" + weightNumberFormat.format(modUWeight) + ")");
		}

		text += (" " + getString("node.weightunits"));
		parent.add(createSimpleNode(text, "gewicht"));
		// categorized items
		Collection catNodes = unitsTools.addCategorizedUnitItems(units, parent, null, null,
																 true, nodeWrapperFactory);
		if(catNodes != null) {
			for(Iterator catIter = catNodes.iterator(); catIter.hasNext();) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) catIter.next();
				Object o = node.getUserObject();
				ItemCategory cat = null;
				
				if(o instanceof ItemCategoryNodeWrapper) {
					cat = ((ItemCategoryNodeWrapper) o).getItemCategory();
				} else {
					cat = (ItemCategory) o;
				}
				
				expandableNodes.add(new NodeWrapper(node,
													"EMapDetailsPanel." +
													cat.getID().toString() + "Expanded"));
			}
		}

		if(skills.size() > 0) {
			// DefaultMutableTreeNode skillsNode = new DefaultMutableTreeNode(getString("node.skills"));
			DefaultMutableTreeNode skillsNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.skills"),
				"skills"));
			parent.add(skillsNode);
			expandableNodes.add(new NodeWrapper(skillsNode, "EMapDetailsPanel.FactionSkillsExpanded"));

			List sortedSkills = CollectionFactory.createLinkedList(skills.values());
			Collections.sort(sortedSkills, new SkillStatItemComparator());

			for(Iterator iter = sortedSkills.iterator(); iter.hasNext();) {
				SkillStatItem item = (SkillStatItem) iter.next();
				DefaultMutableTreeNode skillNode = createSimpleNode(item.skill.getName() + " " +
																	item.skill.getLevel() + ": " +
																	item.unitCounter,
																	item.skill.getSkillType().getID()
																			  .toString());
				skillsNode.add(skillNode);

				Comparator idCmp = IDComparator.DEFAULT;
				Comparator unitCmp = new UnitSkillComparator(new SpecifiedSkillTypeSkillComparator(item.skill.getSkillType(),
																								   new SkillComparator(),
																								   null),
															 idCmp);
				Collections.sort(item.units, unitCmp);

				for(Iterator uIter = item.units.iterator(); uIter.hasNext();) {
					Unit u = (Unit) uIter.next();
					StringBuffer sb = new StringBuffer();
					sb.append(u.toString());
					sb.append(": " + u.getPersons());

					if(u.getPersons() != u.getModifiedPersons()) {
						sb.append(" (").append(u.getModifiedPersons()).append(")");
					}

					skillNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createUnitNodeWrapper(u,
																									  sb.toString())));
				}
			}
		}
	}

	private void showGroup(Group g) {
		setNameAndDescription(g.getName(), "", false);
		appendGroupInfo(g, rootNode, myExpandableNodes);
	}

	/**
	 * Append information about the specified group.
	 *
	 * @param g
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendGroupInfo(Group g, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if(lastRegion != null) {
			int personCount = 0;
			Map skills = CollectionFactory.createHashtable();
			Iterator units = lastRegion.units().iterator();
			Collection regionUnits = CollectionFactory.createLinkedList();

			while(units.hasNext() == true) {
				Unit u = (Unit) units.next();

				if((u.getGroup() != null) && u.getGroup().equals(g)) {
					personCount += u.getPersons();
					regionUnits.add(u);

					for(Iterator i = u.getSkills().iterator(); i.hasNext();) {
						Skill skill = (Skill) i.next();
						SkillStatItem stored = (SkillStatItem) skills.get(skill.getName() +
																		  skill.getLevel());

						if(stored != null) {
							stored.unitCounter += u.getPersons();
							stored.units.add(u);
						} else {
							stored = new SkillStatItem(skill, u.getPersons());
							stored.units.add(u);
							skills.put(skill.getName() + skill.getLevel(), stored);
						}
					}
				}
			}

			DefaultMutableTreeNode n = new DefaultMutableTreeNode(personCount + " " +
																  getString("node.persons"));
			parent.add(n);

			if((g.allies() != null) && (g.allies().values().size() > 0)) {
				n = new DefaultMutableTreeNode(getString("node.alliances"));
				parent.add(n);
				expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.AlliancesExpanded"));

				List sortedAllies = CollectionFactory.createLinkedList(g.allies().values());
				Collections.sort(sortedAllies,
								 new AllianceFactionComparator(new NameComparator(IDComparator.DEFAULT)));

				for(Iterator iter = sortedAllies.iterator(); iter.hasNext();) {
					DefaultMutableTreeNode m = new DefaultMutableTreeNode(iter.next());
					n.add(m);
				}
			}

			// categorized items
			Collection catNodes = unitsTools.addCategorizedUnitItems(regionUnits, parent, null,
																	 null, true,
																	 nodeWrapperFactory);
			if(catNodes != null) {
				for(Iterator catIter = catNodes.iterator(); catIter.hasNext();) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) catIter.next();
					
					if(log.isDebugEnabled()) {
						log.debug("EmapDetailPanel.appendGroupInfo: found class " +
								  node.getUserObject().getClass() + " (expected ItemCategory)");
					}
					
					Object o = node.getUserObject();
					ItemCategory cat = null;
					
					if(o instanceof ItemCategoryNodeWrapper) {
						cat = ((ItemCategoryNodeWrapper) o).getItemCategory();
					} else {
						cat = (ItemCategory) o;
					}
					
					expandableNodes.add(new NodeWrapper(node,
														"EMapDetailsPanel." +
														cat.getID().toString() + "Expanded"));
				}
			}

			if(skills.size() > 0) {
				// n = new DefaultMutableTreeNode(getString("node.skills"));
				n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.skills"),
					"skills"));
				parent.add(n);
				expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.RegionSkillsExpanded"));

				List sortedSkills = CollectionFactory.createLinkedList(skills.values());
				Collections.sort(sortedSkills, new SkillStatItemComparator());

				for(Iterator iter = sortedSkills.iterator(); iter.hasNext();) {
					SkillStatItem item = (SkillStatItem) iter.next();
					int bonus = 0;
					Faction f = null;

					if(((f = g.getFaction()) != null) && (f.getType() != null)) {
						bonus = f.getRace().getSkillBonus(item.skill.getSkillType());

						if(lastRegion != null) {
							bonus += f.getRace().getSkillBonus(item.skill.getSkillType(),
															   lastRegion.getRegionType());
						}
					}

					DefaultMutableTreeNode m = new DefaultMutableTreeNode(new SimpleNodeWrapper(item.skill.getName() +
																								" " +
																								item.skill.getLevel() +
																								": " +
																								item.unitCounter,
																								item.skill.getSkillType()
																										  .getID()
																										  .toString()));
					n.add(m);

					Comparator idCmp = IDComparator.DEFAULT;
					Comparator unitCmp = new UnitSkillComparator(new BestSkillComparator(new SkillComparator(),
																						 new SkillTypeComparator(new SkillTypeRankComparator(new NameComparator(idCmp),
																																			 settings),
																												 new SkillComparator()),
																						 null),
																 idCmp);
					Collections.sort(item.units, unitCmp);

					for(Iterator uIter = item.units.iterator(); uIter.hasNext();) {
						Unit u = (Unit) uIter.next();
						StringBuffer sb = new StringBuffer();
						sb.append(u.toString());

						Skill skill = u.getSkill(item.skill.getSkillType());

						if(skill != null) {
							sb.append(": [").append(skill.getPointsPerPerson()).append(" -> ")
							  .append(Skill.getPointsAtLevel((item.skill.getLevel() - bonus) + 1))
							  .append("]");
						}

						sb.append(", ").append(u.getPersons());

						if(u.getPersons() != u.getModifiedPersons()) {
							sb.append(" (").append(u.getModifiedPersons()).append(")");
						}

						UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u,
																					 sb.toString());
						m.add(new DefaultMutableTreeNode(w));
					}
				}
			}
		}
	}

	private void showUnit(Unit u) {
		// update description text area
		String strDesc = "";

		if(u.getDescription() != null) {
			strDesc += u.getDescription();
		}

		if(u.privDesc != null) {
			strDesc += (DESCRIPTION_SEPARATOR + u.privDesc);
		}

		setNameAndDescription(u.getName(), strDesc, isPrivilegedAndNoSpy(u));

		appendUnitInfo(u, rootNode, myExpandableNodes);

		addTag.setEnabled(true);
		removeTag.setEnabled(false);
	}

	/** 
	 * Appends information for the specified unit
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendUnitInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		/* make sure that all unit relations have been established */
		if(u.getRegion() != null) {
			u.getRegion().refreshUnitRelations();
		}

		// Heldenanzeige
		if(u.isHero) {
			parent.add(createSimpleNode(getString("node.hero"), "hero"));
		}

		// Personenanzeige
		appendUnitPersonInfo(u, parent, expandableNodes);

		// Partei
		appendUnitFactionInfo(u, parent, expandableNodes);
		
		// Race
		appendUnitRaceInfo(u, parent, expandableNodes);

		// Group
		appendUnitGroupInfo(u, parent, expandableNodes);

		// Kampfreihenanzeige
		SimpleNodeWrapper cWrapper = nodeWrapperFactory
				.createSimpleNodeWrapper(getString("node.combatstatus") + ": "
						+ Unit.combatStatusToString(u), "kampfstatus");

		if(isPrivileged(u.getFaction())) {
			cWrapper.setContextFactory(combatContext);
			cWrapper.setArgument(u);
		}
		parent.add(new DefaultMutableTreeNode(cWrapper));
		
		// starvation
		if(u.isStarving) {
			parent.add(createSimpleNode(getString("node.starved"), "hunger"));
		}

		// health state
		if(u.health != null) {
			// Fiete 20060910
			// the hp-string is not translated in the cr
			// so u.health = german
			parent.add(createSimpleNode(getString("node.health") + ": " + data.getTranslation(u.health), u.health));
		}

		// guard state
		if(u.guard != 0) {
			parent.add(createSimpleNode(getString("node.guards") + ": " +
										Unit.guardFlagsToString(u.guard), "bewacht"));
		}

		// stealth info
		appendUnitStealthInfo(u, parent, expandableNodes);

		// spy
		if(u.isSpy()) {
			parent.add(createSimpleNode(getString("node.spy"), "spion"));
		}

		// Gebaeude-/Schiffsanzeige
		appendContainerInfo(u, parent, expandableNodes);

		// magic aura
		if(u.aura != -1) {
			parent.add(createSimpleNode(getString("node.aura") + ": " + u.aura + " / " + u.auraMax,
										"aura"));
		}

		// familiar
		appendFamiliarInfo(u, parent, expandableNodes);

		// weight
		appendUnitWeight(u, parent, expandableNodes);
		
		// Fiete 20060915: feature wish..calculate max Horses for walking and riding
		appendUnitHorses(u, parent, expandableNodes);

		// load
		appendUnitLoadInfo(u, parent, expandableNodes);
		
		// items
		appendUnitItemInfo(u, parent, expandableNodes);

		// skills
		boolean isTrader = appendUnitSkillInfo(u, parent, expandableNodes);
		
		// teaching
		appendUnitTeachInfo(u, parent, expandableNodes);

		// transportation
		appendUnitTransportationInfo(u, parent, expandableNodes);

		// attacking
		appendUnitAttackInfo(u, parent, expandableNodes);

		// magic spells 
		appendUnitSpellsInfo(u, parent, expandableNodes);

		// Kampfzauber-Anzeige
		appendUnitCombatSpells(u.combatSpells, parent, expandableNodes);

		// Trank Anzeige
		appendUnitPotionInfo(u, parent, expandableNodes);
		
		// luxuries
		if(isTrader) {
			appendRegionLuxuriesInfo(u.getRegion(), parent, expandableNodes);
		}
		
		// region resources if maker
		appendUnitRegionResource(u, parent, expandableNodes);
		
		// comments
		appendComments(u, parent, expandableNodes);
		
		// tags
		appendTags(u, parent, expandableNodes);
	}

	/** 
	 * Appends information on number of persons (and their race).
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes
	 */ 
	private void appendUnitPersonInfo(Unit u, DefaultMutableTreeNode parent,
									  Collection expandableNodes) {
		
		// display custom Unit Icon ?
		String customUnitIconFileName = "custom/units/" + u.toString(false);
		if (this.isAllowingCustomIcons() &&  getMagellanContext().getImageFactory().existImageIcon(customUnitIconFileName)){
			DefaultMutableTreeNode customUnitIconNode = null;
			if (getMagellanContext().getImageFactory().imageIconSizeCheck(customUnitIconFileName,40,40)){ 
				customUnitIconNode = createSimpleNode(u.getModifiedName(), customUnitIconFileName);
			} else {
				customUnitIconNode = createSimpleNode(u.getModifiedName(), "tobig");
			}
			parent.add(customUnitIconNode);
		}
		
		String strPersons = getString("node.persons") + ": " + u.getPersons();
	
		if(u.getPersons() != u.getModifiedPersons()) {
			strPersons += (" (" + u.getModifiedPersons() + ")");
		}
		String iconPersonName = "person";
		/**
		 * Fiete 20061218...was just a first try....not that good, needs 
		 * allways some support..
		String iconNameEN = getString(com.eressea.util.Umlaut.convertUmlauts(u.getRealRaceName()));
		if (!iconNameEN.equalsIgnoreCase(com.eressea.util.Umlaut.convertUmlauts(u.getRealRaceName()))) {
			iconPersonName = iconNameEN;
		}
		*/
		// now we check if a specific race icon exists, if true, we use it
		if (getMagellanContext().getImageFactory().existImageIcon(u.getRealRaceName())){
			iconPersonName = u.getRealRaceName();
		}
		DefaultMutableTreeNode personNode = createSimpleNode(strPersons, iconPersonName);
		parent.add(personNode);
		expandableNodes.add(new NodeWrapper(personNode, "EMapDetailsPanel.PersonsExpanded"));
	
		for(Iterator iter = u.getPersonTransferRelations().iterator(); iter.hasNext();) {
			PersonTransferRelation itr = (PersonTransferRelation) iter.next();
			String prefix = String.valueOf(itr.amount) + " ";
			String addIcon = null;
			Unit u2 = null;
	
			if(itr.source == u) {
				addIcon = "get";
				u2 = itr.target;
			} else if(itr.target == u) {
				addIcon = "give";
				u2 = itr.source;
			}
	
			UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2, prefix,
																		   u2.getPersons(),
																		   u2.getModifiedPersons());
			unw.setAdditionalIcon(addIcon);
			unw.setReverseOrder(true);
			personNode.add(new DefaultMutableTreeNode(unw));
		}
		
		for (Iterator it = u.getRelations(UnitTransferRelation.class).iterator(); it.hasNext(); ){
			UnitTransferRelation relation = (UnitTransferRelation) it.next();
	
			String addIcon = null;
			Unit u2 = null;
	
			if(relation.source == u) {
				addIcon = "get";
				u2 = relation.target;
			} else if(relation.target == u) {
				addIcon = "give";
				u2 = relation.source;
			}
	
			UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
			unw.setAdditionalIcon(addIcon);
			unw.setReverseOrder(true);
			personNode.add(new DefaultMutableTreeNode(unw));
		}
	}

	/**
	 * Appends information about this unit's faction.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendUnitFactionInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		appendFactionInfo(u.getFaction(), parent, expandableNodes);
	}

	/**
	 * Appends information about this faction.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendFactionInfo(Faction f, DefaultMutableTreeNode parent, Collection expandableNodes) {
		DefaultMutableTreeNode fNode;
		if(f == null) {
			fNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.faction") +
																						  ": " +
																						  getString("node.unknownfaction"),
																						  "faction"));
		} else {
			// custom faction icon ?
			String customFactionIconFileName = "custom/factions/" + f.getID();
			if (this.isAllowingCustomIcons() && getMagellanContext().getImageFactory().existImageIcon(customFactionIconFileName)){
				if (getMagellanContext().getImageFactory().imageIconSizeCheck(customFactionIconFileName,40,40)){ 
					fNode = createSimpleNode(f.toString(), customFactionIconFileName);
				} else {
					fNode = createSimpleNode(f.toString(), "tobig");
				}
				
			} else {
				fNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.faction") +
																						  ": " +
																						  f.toString(),
																						  "faction"));
			}
		}
	
		parent.add(fNode);
	}
	
	
	
	
	/**
	 * Appends information on the unit's race.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendUnitRaceInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if(u.race != null) {
			StringBuffer nodeText = new StringBuffer(getString("node.race")).append(": ").append(u.getRaceName(this.data));
	
			if(u.realRace != null) {
				nodeText.append(" (").append(u.realRace.getName()).append(")");
			}
	
			parent.add(createSimpleNode(nodeText.toString(), "rasse"));
		}
	}

	/**
	 * Appends information on the unit's group.
	 *
	 * @param u 
	 */
	private void appendUnitGroupInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if(u.getGroup() != null) {
			Group group = u.getGroup();
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.group") +
					  ": \"" + group.getName() +
					  "\"","groups"));
			
			parent.add(groupNode);
			expandableNodes.add(new NodeWrapper(groupNode, "EMapDetailsPanel.UnitGroupExpanded"));
			appendAlliances(group.allies(), groupNode);
		}
	
	}

	/**
	 * Append Information on the stealth level etc. for this unit.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendUnitStealthInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// faction hidden, stealth level
		Skill stealth = u.getSkill(data.rules.getSkillType(EresseaConstants.S_TARNUNG, true));
		int stealthLevel = 0;
	
		if(stealth != null) {
			stealthLevel = stealth.getLevel();
		}
	
		if(u.hideFaction || stealthLevel > 0) {
			String strHide = getString("node.stealth") + ":";
	
			if(stealthLevel > 0) {
				if(u.stealth == -1) {
					strHide += (" " + stealthLevel);
				} else {
					strHide += (" " + u.stealth + " / " + stealthLevel);
				}
			}
	
			if(u.hideFaction) {
				if(stealthLevel > 0) {
					strHide += ",";
				}
	
				strHide += (" " + getString("node.disguised"));
			}
	
			String icon = null;
	
			try {
				icon = stealth.getSkillType().getID().toString();
			} catch(Exception exc) {
				icon = "tarnung";
			}
	
			SimpleNodeWrapper wrapper = nodeWrapperFactory.createSimpleNodeWrapper(strHide, icon);
	
			if((stealthLevel > 0) && isPrivileged(u.getFaction())) {
				wrapper.setContextFactory(stealthContext);
				wrapper.setArgument(u);
			}
	
			parent.add(new DefaultMutableTreeNode(wrapper));
		}
		// disguise
		if(u.getGuiseFaction() != null) {
			parent.add(createSimpleNode(getString("node.disguisedas") + u.getGuiseFaction(),
										((stealth != null)
										 ? stealth.getSkillType().getID().toString() : "tarnung")));
		}
	}

	private void appendFamiliarInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// familiar mages (Fiete)
		// we use auraMax to determine a mage...should be OK
		if (u.auraMax != -1) {
			if (u.familiarmageID != null) {
				// ok..this unit is a familiar (Vertrauter)
				// get the parent unit
				
				Unit parentUnit = this.data.getUnit(u.familiarmageID);
				if (parentUnit != null) {
					// DefaultMutableTreeNode parentsNode = new DefaultMutableTreeNode("Parents");
					DefaultMutableTreeNode parentsNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.FamiliarParents"),
						"aura"));
					parent.add(parentsNode);
					expandableNodes.add(new NodeWrapper(parentsNode, "EMapDetailsPanel.FamiliarParentsExpanded"));
					UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(parentUnit);
					DefaultMutableTreeNode parentUnitNode = new DefaultMutableTreeNode(w);
					parentsNode.add(parentUnitNode);
				}
			} else {
				// ok..we have a real mage..may be he has an familiar...
				// for further purpose lets look for all familiars...
				Collection familiars = CollectionFactory.createLinkedList();
				for (Iterator iter = this.data.units().keySet().iterator();iter.hasNext();){
					UnitID uID = (UnitID) iter.next();
					Unit uTest = this.data.getUnit(uID);
					if (uTest.familiarmageID == u.getID()) {
						familiars.add(uTest);
					}
				}
				if (familiars.size()>0){
					DefaultMutableTreeNode childsNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.FamiliarChilds"),
						"aura"));
					parent.add(childsNode);
					expandableNodes.add(new NodeWrapper(childsNode, "EMapDetailsPanel.FamiliarChildsExpanded"));
					for (Iterator iter=familiars.iterator();iter.hasNext();){
						Unit uT = (Unit) iter.next();
						UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(uT);
						DefaultMutableTreeNode parentUnitNode = new DefaultMutableTreeNode(w);
						childsNode.add(parentUnitNode);
					}
				}
			}
		}
	}

	/**
	 * Append information on the unit's weight.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendUnitWeight(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		Float uWeight = new Float(u.getWeight() / 100.0F);
		Float modUWeight = new Float(u.getModifiedWeight() / 100.0F);
		String text = getString("node.totalweight") + ": " + weightNumberFormat.format(uWeight);
	
		if(!uWeight.equals(modUWeight)) {
			text += (" (" + weightNumberFormat.format(modUWeight) + ")");
		}
	
		text += (" " + getString("node.weightunits"));
		parent.add(createSimpleNode(text, "gewicht"));
	}

	/**
	 * Append information on the possible horses.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendUnitHorses(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		int skillLevel = 0;
		Skill s = u.getModifiedSkill(new SkillType(StringID.create("Reiten")));
	
		if(s != null) {
			skillLevel = s.getLevel();
		}
	
		int maxHorsesWalking = ((skillLevel * u.getModifiedPersons() * 4) + u.getModifiedPersons());
		int maxHorsesRiding = (skillLevel * u.getModifiedPersons() * 2);
		
		String text = "Max: " + maxHorsesWalking + " / " + maxHorsesRiding;
		parent.add(createSimpleNode(text, "pferd"));
	}

	/** 
	 * Append information on this unit's payload and capacity.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendUnitLoadInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// load
		int load = u.getLoad();
		int modLoad = u.getModifiedLoad();
	
		if((load != 0) || (modLoad != 0)) {
			String text = getString("node.load") + ": " +
				   weightNumberFormat.format(new Float(load / 100.0F));
	
			if(load != modLoad) {
				text += (" (" + weightNumberFormat.format(new Float(modLoad / 100.0F)) + ")");
			}
	
			text += (" " + getString("node.weightunits"));
			parent.add(createSimpleNode(text, "beladung"));
		}
	
		// payload
		int maxOnFoot = u.getPayloadOnFoot();
	
		if(maxOnFoot == Unit.CAP_UNSKILLED) {
			parent.add(createSimpleNode(getString("node.capacityonfoot") + ": " +
										getString("node.toomanyhorses"), "warnung"));
		} else {
			Float max = new Float(maxOnFoot / 100.0F);
			Float free = new Float(Math.abs(maxOnFoot - modLoad) / 100.0F);
			DefaultMutableTreeNode capacityNode;
	
			if((maxOnFoot - modLoad) < 0) {
				capacityNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.capacityonfoot") +
																									 ": " +
																									 getString("node.overloadedby") +
																									 weightNumberFormat.format(free) +
																									 " " +
																									 getString("node.weightunits"),
																									 "warnung"));
			} else {
				capacityNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.capacityonfoot") +
																									 ": " +
																									 weightNumberFormat.format(free) +
																									 " / " +
																									 weightNumberFormat.format(max) +
																									 " " +
																									 getString("node.weightunits"),
																									 "ladfuss"));
				appendUnitCapacityByItems(capacityNode, u, maxOnFoot - modLoad);
	
				if(capacityNode.getChildCount() > 0) {
					expandableNodes.add(new NodeWrapper(capacityNode,
														"EMapDetailsPanel.UnitCapacityOnFootExpanded"));
				}
			}
	
			parent.add(capacityNode);
		}
	
		int maxOnHorse = u.getPayloadOnHorse();
	
		if(maxOnHorse == Unit.CAP_UNSKILLED) {
			parent.add(createSimpleNode(getString("node.capacityonhorse") + ": " +
										getString("node.toomanyhorses"), "warnung"));
		} else if(maxOnHorse == Unit.CAP_NO_HORSES) {
		} else {
			Float max = new Float(maxOnHorse / 100.0F);
			Float free = new Float(Math.abs(maxOnHorse - modLoad) / 100.0F);
			DefaultMutableTreeNode capacityNode;
	
			if((maxOnHorse - modLoad) < 0) {
				capacityNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.capacityonhorse") +
																									 ": " +
																									 getString("node.overloadedby") +
																									 weightNumberFormat.format(free) +
																									 " " +
																									 getString("node.weightunits"),
																									 "warnung"));
			} else {
				capacityNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.capacityonhorse") +
																									 ": " +
																									 weightNumberFormat.format(free) +
																									 " / " +
																									 weightNumberFormat.format(max) +
																									 " " +
																									 getString("node.weightunits"),
																									 "ladpferd"));
				appendUnitCapacityByItems(capacityNode, u, maxOnHorse - modLoad);
	
				if(capacityNode.getChildCount() > 0) {
					expandableNodes.add(new NodeWrapper(capacityNode,
														"EMapDetailsPanel.UnitCapacityOnHorseExpanded"));
				}
			}
	
			parent.add(capacityNode);
		}
	}

	private void appendUnitItemInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// items
		if(u.getModifiedItems().size() > 0) {
			// DefaultMutableTreeNode itemsNode = new DefaultMutableTreeNode(getString("node.items"));
			DefaultMutableTreeNode itemsNode = createSimpleNode(getString("node.items"), "things");
			parent.add(itemsNode);
			expandableNodes.add(new NodeWrapper(itemsNode, "EMapDetailsPanel.UnitItemsExpanded"));
	
			Collection catNodes = unitsTools.addCategorizedUnitItems(Collections.singleton(u), itemsNode, null, null, false, nodeWrapperFactory);	
			if(catNodes != null) {
				for(Iterator catIter = catNodes.iterator(); catIter.hasNext();) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) catIter.next();
					Object o = node.getUserObject();
					ID id = null;
					if(o instanceof ItemCategoryNodeWrapper) {
						id = (((ItemCategoryNodeWrapper) o).getItemCategory()).getID();
					} else {
						if(o instanceof ItemCategory) {
							id = ((ItemCategory) o).getID();
						} else {
							if(o instanceof ItemNodeWrapper) {
								id = ((ItemNodeWrapper) o).getItem().getItemType().getID();
							}
						}
					}
					
					if(id != null) {
						expandableNodes.add(new NodeWrapper(node,
															"EMapDetailsPanel.UnitItems"+id+"Expanded"));
					}
				}
			}
		}
	}

	/** 
	 * Append information on the skills of this unit.
	 * 
	 * @return <code>true</code> iff <code>u</code> is a trader
	 */
	private boolean appendUnitSkillInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// skills
		boolean isTrader = false;
		SkillCategory tradeCat = data.rules.getSkillCategory(StringID.create("trade"));
		SkillType tradeSkill = null;
	
		if(tradeCat == null) {
			tradeSkill = data.rules.getSkillType(StringID.create("Handeln"));
		}
	
		Collection modSkills = u.getModifiedSkills();
		List sortedSkills = null;
	
		if((modSkills != null) && (modSkills.size() > 0)) {
			sortedSkills = CollectionFactory.createLinkedList(modSkills);
		} else {
			sortedSkills = CollectionFactory.createLinkedList(u.getSkills());
		}
	
		Collections.sort(sortedSkills, new SkillComparator());
	
		if(!sortedSkills.isEmpty()) {
			// DefaultMutableTreeNode skillsNode = new DefaultMutableTreeNode(getString("node.skills"));
			DefaultMutableTreeNode skillsNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.skills"),
				"skills"));
			parent.add(skillsNode);
			expandableNodes.add(new NodeWrapper(skillsNode, "EMapDetailsPanel.UnitSkillsExpanded"));
	
			for(Iterator iter = sortedSkills.iterator(); iter.hasNext();) {
				Skill os = null;
				Skill ms = null;
	
				if(modSkills != null) {
					// assume that we are iterating over the mod skills
					ms = (Skill) iter.next();
					os = u.getSkill(ms.getSkillType());
				} else {
					// assume that we are iterating over the original skills
					os = (Skill) iter.next();
				}
	
				if(!isTrader && (os != null)) { // check for trader
	
					if((tradeCat != null) && tradeCat.isInstance(os.getSkillType())) {
						isTrader = true;
					} else if((tradeSkill != null) && tradeSkill.equals(os.getSkillType())) {
						isTrader = true;
					}
				}
				
				skillsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createSkillNodeWrapper(u,
																									os,
																									ms)));
			}
		}
		return isTrader;
	}

	/**
	 * Append information on this unit's teachers and pupils.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendUnitTeachInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
			// teacher pupil relations
			Collection pupils = CollectionFactory.createLinkedList();
			Collection teachers = CollectionFactory.createLinkedList();
	
			pupils = u.getPupils();
			teachers = u.getTeachers();
	//		for(Iterator iter = u.getRelations(TeachRelation.class).iterator(); iter.hasNext();) {
	//		TeachRelation tr = (TeachRelation) iter.next();
			//
	//		if(u.equals(tr.source)) {
	//		if(tr.target != null) {
	//		pupils.add(tr.target);
	//		}
	//		} else {
	//		teachers.add(tr.source);
	//		}
	//		}
	
			if(teachers.size() > 0) {
				// DefaultMutableTreeNode teachersNode = new DefaultMutableTreeNode("");
				DefaultMutableTreeNode teachersNode = createSimpleNode("", "teacher");
				parent.add(teachersNode);
				expandableNodes.add(new NodeWrapper(teachersNode,
						"EMapDetailsPanel.UnitTeachersExpanded"));
	
				int teacherCounter = 0;
	
				for(Iterator iter = teachers.iterator(); iter.hasNext();) {
					Unit teacher = (Unit) iter.next();
					teacherCounter += teacher.getModifiedPersons();
	
					UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(teacher,
							teacher.getPersons(),
							teacher.getModifiedPersons());
					DefaultMutableTreeNode teacherNode = new DefaultMutableTreeNode(w);
					teachersNode.add(teacherNode);
				}
	
				teachersNode.setUserObject(new UnitListNodeWrapper(getString("node.teacher") + ": " +
						teacherCounter + " / " +
						(((u.getModifiedPersons() % 10) == 0)
								? (u.getModifiedPersons() / 10)
										: ((u.getModifiedPersons() / 10) +
												1)), null, teachers,"teacher"));
			}
			
			
			
			if(pupils.size() > 0) {
				boolean duplicatePupil = false;
				Collection checkPupils = CollectionFactory.createLinkedList();
				// DefaultMutableTreeNode pupilsNode = new DefaultMutableTreeNode("");
				DefaultMutableTreeNode pupilsNode = createSimpleNode("", "pupils");
				parent.add(pupilsNode);
				expandableNodes.add(new NodeWrapper(pupilsNode, "EMapDetailsPanel.UnitPupilsExpanded"));
	
				int pupilCounter = 0;
	
				for(Iterator iter = pupils.iterator(); iter.hasNext();) {
					Unit pupil = (Unit) iter.next();
					pupilCounter += pupil.getModifiedPersons();
	
					UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(pupil,
							pupil.getPersons(),
							pupil.getModifiedPersons());
					DefaultMutableTreeNode pupilNode = new DefaultMutableTreeNode(w);
					pupilsNode.add(pupilNode);
					
					// duplicate check
					if (checkPupils.contains(pupil)){
						duplicatePupil=true;
					} else {
						checkPupils.add(pupil);
					}
					
				}
				String duplicatePupilWarning = "";
				if (duplicatePupil){
					duplicatePupilWarning = " (!!!)";
				}
				pupilsNode.setUserObject(new UnitListNodeWrapper(getString("node.pupils") + ": " +
						pupilCounter + " / " +
						(u.getModifiedPersons() * 10) + duplicatePupilWarning, null,
						pupils,"pupils"));
			}
		}

	private void appendUnitTransportationInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// transportation relations
		Collection carriers = u.getCarriers();
	
		if(carriers.size() > 0) {
			DefaultMutableTreeNode carriersNode = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.carriers"),
					null,
					carriers));
			parent.add(carriersNode);
			expandableNodes.add(new NodeWrapper(carriersNode,
					"EMapDetailsPanel.UnitCarriersExpanded"));
	
			for(Iterator iter = carriers.iterator(); iter.hasNext();) {
				Unit carrier = (Unit) iter.next();
				UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(carrier,
						carrier.getPersons(),
						carrier.getModifiedPersons());
				carriersNode.add(new DefaultMutableTreeNode(w));
			}
		}
		Collection passengers = u.getPassengers();
	
		if(passengers.size() > 0) {
			DefaultMutableTreeNode passengersNode = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.passengers"),
					null,
					passengers));
			parent.add(passengersNode);
			expandableNodes.add(new NodeWrapper(passengersNode,
					"EMapDetailsPanel.UnitPassengersExpanded"));
	
			for(Iterator iter = passengers.iterator(); iter.hasNext();) {
				Unit passenger = (Unit) iter.next();
				String str = passenger.toString() + ": " +
				weightNumberFormat.format(new Float(passenger.getWeight() / 100.0f)) +
				" " + getString("node.weightunits");
	
				if(passenger.getWeight() != passenger.getModifiedWeight()) {
					str += (" (" +
							weightNumberFormat.format(new Float(passenger.getModifiedWeight() / 100.0f)) +
							" " + getString("node.weightunits") + ")");
				}
	
				UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(passenger, str);
				passengersNode.add(new DefaultMutableTreeNode(w));
			}
		}
	}

	private void appendUnitAttackInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// attack relations
	
		Collection attacks = u.getAttackVictims();
	
		if(attacks.size() > 0) {
			DefaultMutableTreeNode attacksNode = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.attacks"),
					null,
					attacks));
			parent.add(attacksNode);
			expandableNodes.add(new NodeWrapper(attacksNode,
					"EMapDetailsPanel.UnitAttacksExpanded"));
	
			for(Iterator iter = attacks.iterator(); iter.hasNext();) {
				Unit victim = (Unit) iter.next();
				UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(victim,
						victim.getPersons(),
						victim.getModifiedPersons());
				attacksNode.add(new DefaultMutableTreeNode(w));
			}
		}
	
		Collection attackedBy = u.getAttackAggressors();
	
		if(attackedBy.size() > 0) {
			DefaultMutableTreeNode attackedByNode = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.attackedBy"),
					null,
					attackedBy));
			parent.add(attackedByNode);
			expandableNodes.add(new NodeWrapper(attackedByNode,
					"EMapDetailsPanel.UnitAttackedByExpanded"));
	
			for(Iterator iter = attackedBy.iterator(); iter.hasNext();) {
				Unit victim = (Unit) iter.next();
				UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(victim,
						victim.getPersons(),
						victim.getModifiedPersons());
				attackedByNode.add(new DefaultMutableTreeNode(w));
			}
		}
	}

	/**
	 * Append information on the unit's spells
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendUnitSpellsInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if((u.spells != null) && (u.spells.size() > 0)) {
			// DefaultMutableTreeNode spellsNode = new DefaultMutableTreeNode(getString("node.spells"));
			DefaultMutableTreeNode spellsNode = createSimpleNode(getString("node.spells"), "magicschool");
			parent.add(spellsNode);
			expandableNodes.add(new NodeWrapper(spellsNode, "EMapDetailsPanel.UnitSpellsExpanded"));
	
			List sortedSpells = CollectionFactory.createLinkedList(u.spells.values());
			Collections.sort(sortedSpells, new SpellLevelComparator(new NameComparator(null)));
	
			for(Iterator iter = sortedSpells.iterator(); iter.hasNext();) {
				Spell spell = (Spell) iter.next();
				spellsNode.add(createSimpleNode(spell, "spell"));
			}
		}
	}

	private void appendUnitCombatSpells(Map spells, DefaultMutableTreeNode parent,
										Collection expandableNodes) {
		if((spells == null) || spells.isEmpty()) {
			return;
		}
	
		DefaultMutableTreeNode combatSpells = new DefaultMutableTreeNode(getString("node.combatspells"));
		parent.add(combatSpells);
		expandableNodes.add(new NodeWrapper(combatSpells,
											"EMapDetailsPanel.UnitCombatSpellsExpanded"));
	
		for(Iterator iter = spells.values().iterator(); iter.hasNext();) {
			CombatSpell spell = (CombatSpell) iter.next();
			combatSpells.add(createSimpleNode(spell, "spell"));
		}
	}

	/**
	 * Appends information on the potions that this unit can make.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendUnitPotionInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if((data.potions() != null) && (u.skills != null) &&
				   u.skills.containsKey(EresseaConstants.S_ALCHEMIE)) {
				// DefaultMutableTreeNode potionsNode = new DefaultMutableTreeNode(getString("node.potions"));
				DefaultMutableTreeNode potionsNode = createSimpleNode(getString("node.potions"), "Alchemie");
				Skill alchSkill = (Skill) u.skills.get(EresseaConstants.S_ALCHEMIE);
				List potions = CollectionFactory.createLinkedList(data.potions().values());
				Collections.sort(potions, new PotionLevelComparator(new NameComparator(null)));

				for(Iterator iter = potions.iterator(); iter.hasNext();) {
					Potion p = (Potion) iter.next();

					if((p.getLevel() * 2) <= alchSkill.getLevel()) {
						int max = getBrewablePotions(data.rules, p, u.getRegion());
						potionsNode.add(new DefaultMutableTreeNode(nodeWrapperFactory.createPotionNodeWrapper(p,
						                                                                data.getTranslation(p),
																											  ": " +
																											  max)));
					}
				}

				if(potionsNode.getChildCount() > 0) {
					parent.add(potionsNode);
					expandableNodes.add(new NodeWrapper(potionsNode,
														"EMapDetailsPanel.UnitPotionsExpanded"));
				}
			}
	}

	/**
	 * Append information on the region resources if this unit is a maker.
	 *
	 * @param u
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendUnitRegionResource(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		DefaultMutableTreeNode resourceNode = null;
		if(!u.getRegion().resources().isEmpty()) {
			for(Iterator iter = u.getRegion().resources().iterator(); iter.hasNext();) {
				RegionResource res = (RegionResource) iter.next();
				ItemType resItemType = res.getType();
				if (resItemType!=null  && resItemType.getMakeSkill()!=null) {
					Skill resMakeSkill = resItemType.getMakeSkill();
					SkillType resMakeSkillType = resMakeSkill.getSkillType();
					if (resMakeSkillType!=null && u.getModifiedSkill(resMakeSkillType)!=null){
						Skill unitSkill = u.getModifiedSkill(resMakeSkillType);
						if (unitSkill.getLevel()>0){
							if (resourceNode==null){
								// resourceNode=new DefaultMutableTreeNode(getString("node.resources_region"));
								resourceNode=createSimpleNode(getString("node.resources_region"), "ressourcen");
								expandableNodes.add(new NodeWrapper(resourceNode, "EMapDetailsPanel.UnitRegionResourceExpanded"));
							}
							int oldValue = findOldValueByResourceType(u.getRegion(), res);
							appendResource(res, resourceNode, oldValue);
						}
					}
				}
			}
		}
		
		if (resourceNode!=null){
			parent.add(resourceNode);
		}
	}

	/**
	 * Append information on the containers this unit is in, if it is commanding any and who it is
	 * passing command. 
	 * 
	 * @param u
	 * @param parent
	 * @param expandableNodes
	 */
	private void appendContainerInfo(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if (u.getUnitContainer() != null) {
			DefaultMutableTreeNode containerNode = null;
			boolean isOwner = u.getUnitContainer().getOwnerUnit() == u;
			UnitContainerNodeWrapper cnw = nodeWrapperFactory.createUnitContainerNodeWrapper(u
					.getUnitContainer(), true, isOwner);
			containerNode = new DefaultMutableTreeNode(cnw);
			parent.add(containerNode);
		}
		if (u.getModifiedUnitContainer() != null
				&& !u.getModifiedUnitContainer().equals(u.getUnitContainer())) {
			DefaultMutableTreeNode containerNode = null;
			boolean isOwner = false; // TODO: can we calculate if the unit is going to be the
										// owner of its new container?
			containerNode = new DefaultMutableTreeNode(nodeWrapperFactory
					.createUnitContainerNodeWrapper(u.getModifiedUnitContainer(), true, isOwner));
			parent.add(containerNode);
		}

		// command relations
		Set getters = null;
		Set givers = null;
		for (Iterator it = u.getRelations(ControlRelation.class).iterator(); it.hasNext();){
			ControlRelation rel = (ControlRelation) it.next();
			if (givers==null) 
				givers = CollectionFactory.createHashSet();
			if (getters==null)
				getters = CollectionFactory.createHashSet();
			if (rel.source==u)
				getters.add(rel.target);
			if (rel.target==u)
				givers.add(rel.source);
		}
		if (givers!=null || getters!=null){
			DefaultMutableTreeNode commandNode = new DefaultMutableTreeNode(getString("node.command"));
			expandableNodes.add(new NodeWrapper(commandNode, "EMapDetailsPanel.PersonsExpanded"));
			if (givers!=null){
				for (Iterator it = givers.iterator(); it.hasNext();){	
					Unit u2 = (Unit) it.next();
					UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
					unw.setAdditionalIcon("give");
					unw.setReverseOrder(true);
					commandNode.add(new DefaultMutableTreeNode(unw));
				}
			}
			if (getters!=null){
				for (Iterator it = getters.iterator(); it.hasNext();){	
					Unit u2 = (Unit) it.next();
					UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
					unw.setAdditionalIcon("get");
					unw.setReverseOrder(true);
					commandNode.add(new DefaultMutableTreeNode(unw));
				}
			}
			parent.add(commandNode);
		}
	}

	private void appendUnitCapacityByItems(DefaultMutableTreeNode parent, Unit u, int freeCapacity) {
		ItemType horses = data.rules.getItemType(StringID.create("Pferd"));
		ItemType carts = data.rules.getItemType(StringID.create("Wagen"));
		ItemType silver = data.rules.getItemType(StringID.create("Silber"));
		// Fiete: feature request...showing not only capacity for "good" items in region...
		if (PropertiesHelper.getboolean(settings, "unitCapacityContextMenuShowFriendly", true)){
			for(Iterator iter = u.getRegion().items().iterator(); iter.hasNext();) {
				Item item = (Item) iter.next();
				ItemType type = item.getItemType();
	
				if((type.getWeight() > 0.0) && !type.equals(horses) && !type.equals(carts) &&
					   !type.equals(silver)) {
					int weight = (int) (type.getWeight() * 100);
					parent.add(createSimpleNode("Max. " + type.getName() + ": " +
												(freeCapacity / weight), "items/" + type.getIconName()));
				}
			}
		} 
		
		if (PropertiesHelper.getboolean(settings, "unitCapacityContextMenuShowSome", false)){
			for(Iterator iter = u.getRegion().allItems().iterator(); iter.hasNext();) {
				Item item = (Item) iter.next();
				ItemType type = item.getItemType();
	
				if((type.getWeight() > 0.0) && !type.equals(horses) && !type.equals(carts) &&
					   !type.equals(silver)) {
					int weight = (int) (type.getWeight() * 100);
					parent.add(createSimpleNode("Max. " + type.getName() + ": " +
												(freeCapacity / weight), "items/" + type.getIconName()));
				}
			}
		} 
		
		if (PropertiesHelper.getboolean(settings, "unitCapacityContextMenuShowAll", false)) {
			// show all itemtypes...need to built and sort a list
			// we take natural order - it works - added Comparable to ItemType (Fiete)
			TreeSet l = new TreeSet();
			// only use the items found in gamedata
			// without fancy merging, for all items should pe Translation present
			for (Iterator iter2 = u.getRegion().getData().regions().values().iterator();iter2.hasNext();){
				Region r  = (Region) iter2.next();
				for (Iterator iteri = r.allItems().iterator();iteri.hasNext();){
					Item item = (Item) iteri.next();
					ItemType type = item.getItemType();
					l.add(type);
				}
			}
			String actLocale = u.getRegion().getData().getLocale().toString();
			if (actLocale.equalsIgnoreCase("de")) {
				// ok...a de GameData...here we use all defined ItemTypes from the rules...too
				// need no present Translation for those...in CR all in german...
				for (Iterator iter2 = u.getRegion().getData().rules.getItemTypeIterator();iter2.hasNext();){
					ItemType type = (ItemType)iter2.next();
					l.add(type);
				}
			} 
			
			for (Iterator iter3 = l.iterator();iter3.hasNext();){
				ItemType type = (ItemType) iter3.next();
				
				if((type.getWeight() > 0.0) && !type.equals(horses) && !type.equals(carts) &&
					   !type.equals(silver)) {
					int weight = (int) (type.getWeight() * 100);
					parent.add(createSimpleNode("Max. " + type.getName() + ": " +
												(freeCapacity / weight), "items/" + type.getIconName()));
				}
			}
		}
		
	}

	/**
	 * Determines how many potions potion can be brewed from the herbs owned by privileged factions
	 * in the specified region.
	 *
	 * @param rules TODO: DOCUMENT ME!
	 * @param potion TODO: DOCUMENT ME!
	 * @param region TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private int getBrewablePotions(Rules rules, Potion potion, Region region) {
		int max = Integer.MAX_VALUE;

		for(Iterator iter = potion.ingredients().iterator(); iter.hasNext();) {
			Item ingredient = (Item) iter.next();

			if(ingredient.getItemType() != null) {
				// units can not own peasants!
				int amount = 0;

				if(ingredient.getItemType().equals(data.rules.getItemType(StringID.create("Bauer")))) {
					amount = region.peasants;
				} else {
					Item item = region.getItem(ingredient.getItemType());

					if(item != null) {
						amount = item.getAmount();
					}
				}

				max = Math.min(amount, max);
			}
		}

		return max;
	}

	private void showBuilding(Building b) {
		setNameAndDescription(b, isPrivilegedAndNoSpy(b.getOwnerUnit()));

		appendBuildingInfo(b, rootNode, myExpandableNodes);
	}

	/**
	 * Appends information on a buildig: Type, owner, inmates, maintenance and costs.
	 *
	 * @param b
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendBuildingInfo(Building b, DefaultMutableTreeNode parent,
									Collection expandableNodes) {
		/* make sure that all unit relations have been established
		 (necessary for enter-/leave relations) */
		if(b.getRegion() != null) {
			b.getRegion().refreshUnitRelations();
		}

		// type and true type
		appendBuildingTypeInfo(b, parent, expandableNodes);

		// Besitzer
		appendBuildingOwnerInfo(b, parent, expandableNodes);

		// GIB KOMMANDO?
		appendContainerCommandInfo(b, parent, expandableNodes);

		// Insassen
		appendBuildingInmatesInfo(b, parent, expandableNodes);

		// Gebaeudeunterhalt
		appendBuildingMaintenance(b, parent, expandableNodes);

		// Baukosten
		appendBuildingCosts(b, parent, expandableNodes);
		
		// Kommentare
		appendComments(b, parent, expandableNodes);
		appendTags(b, parent, expandableNodes);
	}

	/**
	 * 
	 *
	 * @param b
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendBuildingCosts(Building b, DefaultMutableTreeNode parent, Collection expandableNodes) {
		Iterator iter = b.getBuildingType().getRawMaterials();

		DefaultMutableTreeNode n;
		if(iter.hasNext()) {
			// n = new DefaultMutableTreeNode(getString("node.buildingcost"));
			n = createSimpleNode(getString("node.buildingcost"), "buildingcost");
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.BuildingCostExpanded"));
		} else {
			n=null;
		}

		DefaultMutableTreeNode m;
		while(iter.hasNext()) {
			Item i = (Item) iter.next();
			String text = i.getName();
			m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
					i.getAmount() +
					  " " + text,
					  "items/" +
					  i.getItemType()
					   .getID()
					   .toString()));
			n.add(m);
		}
		
		if (n!=null){
			//		 minddestTalent
			BuildingType buildungType = b.getBuildingType();
			if (buildungType!=null && buildungType.getMinSkillLevel()>-1){
				m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
						getString("node.buildingminskilllevel") + ": " + buildungType.getMinSkillLevel(),"skills"));
				n.add(m);
			}
			
			// maxGrösse (bzw bei Burgen Min-Max)
			// Bei Zitadelle: keine max oder min grössenangabe..(kein maxSize verfügbar)
			if (buildungType!=null && buildungType.getMaxSize()>-1){
				if (buildungType instanceof CastleType){
					CastleType castleType = (CastleType) buildungType;
					m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
							getString("node.buildingcastlesizelimits") + ": " +
							      castleType.getMinSize() + " - " + castleType.getMaxSize(),"build_size"));
				} else {
					m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
							"MAX: " + buildungType.getMaxSize(),"build_size"));
				}
				n.add(m);
			}
		}

	}

	/**
	 * 
	 *
	 * @param b
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendBuildingMaintenance(Building b, DefaultMutableTreeNode parent, Collection expandableNodes) {
		Iterator iter = b.getBuildingType().getMaintenanceItems();

		if(iter.hasNext()) {
			// DefaultMutableTreeNode maintNode =  new DefaultMutableTreeNode(getString("node.upkeep"));
			DefaultMutableTreeNode maintNode = createSimpleNode(getString("node.upkeep"), "upkeep");
			parent.add(maintNode);
			expandableNodes.add(new NodeWrapper(maintNode, "EMapDetailsPanel.BuildingMaintenanceExpanded"));

			while(iter.hasNext()) {
				Item i = (Item) iter.next();
				String text = i.getName();

				DefaultMutableTreeNode m;
				if(text.endsWith(" pro Größenpunkt")) {
					int amount = b.getSize() * i.getAmount();
					String newText = text.substring(0, text.indexOf(" pro Größenpunkt"));
					m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(amount +
							" " +
							newText,
							"items/" +
							i.getItemType()
							.getID()
							.toString()));
				} else {
					m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(i.getAmount() +
							" " +
							text,
							"items/" +
							i.getItemType()
							.getID()
							.toString()));
				}

				maintNode.add(m);
			}
		}

	}

	/**
	 * Appends information on the inmates of this building.
	 *
	 * @param b
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendBuildingInmatesInfo(Building b, DefaultMutableTreeNode parent, Collection expandableNodes) {
		List units = CollectionFactory.createLinkedList(b.units()); // the collection of units currently in the building
		Collections.sort(units, sortIndexComparator);
		Collection modUnits = b.modifiedUnits(); // the collection of units in the building in the next turn

		Collection allInmates = CollectionFactory.createLinkedList();
		allInmates.addAll(units);
		allInmates.addAll(modUnits);

		int inmates = 0;
		int modInmates = 0;
		if((units.size() > 0) || (modUnits.size() > 0)) {
			DefaultMutableTreeNode n = new DefaultMutableTreeNode("");
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.BuildingInmatesExpanded"));

			DefaultMutableTreeNode m;
			for(Iterator iter = units.iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();
				StringBuffer text = new StringBuffer();

				if(b.getModifiedUnit(u.getID()) == null) {
					text.append("<- ");
				}

				text.append(u.toString()).append(": ").append(u.getModifiedPersons());

				UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
				m = new DefaultMutableTreeNode(w);
				n.add(m);
				inmates += u.getPersons();
			}

			for(Iterator iter = modUnits.iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();
				modInmates += u.getModifiedPersons();

				if(b.getUnit(u.getID()) == null) {
					StringBuffer text = new StringBuffer();
					text.append("-> ");
					text.append(u.toString()).append(": ").append(u.getModifiedPersons());

					UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
					m = new DefaultMutableTreeNode(w);
					n.add(m);
				}
			}

			if(inmates == modInmates) {
				// n.setUserObject(getString("node.inmates") + ": " + inmates);
				n.setUserObject(new UnitListNodeWrapper(getString("node.inmates") + ": " + inmates, null,
						   allInmates,"occupants"));
			} else {
				// n.setUserObject(getString("node.inmates") + ": " + inmates + " (" + modInmates +	")");
				n.setUserObject(new UnitListNodeWrapper(getString("node.inmates") + ": " + inmates + " (" + modInmates +
						")", null, allInmates,"occupants"));
			}
		}

		DefaultMutableTreeNode n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(((inmates == modInmates)
																				   ? (getString("node.size") +
																				   ": " + inmates)
																				   : (getString("node.size") +
																				   ": " + inmates +
																				   " (" +
																				   modInmates +
																				   ")")) + " / " +
																				  b.getSize(),
																				  "build_size"));
		parent.insert(n, 0);

	}

	/**
	 * Append information on the owner.
	 *
	 * @param b
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendBuildingOwnerInfo(Building b, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if(b.getOwnerUnit() != null) {
			UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(b.getOwnerUnit(),
																		 getString("node.owner") +
																		 ": ",
																		 b.getOwnerUnit()
																		  .getPersons(),
																		 b.getOwnerUnit()
																		  .getModifiedPersons());
			DefaultMutableTreeNode n = new DefaultMutableTreeNode(w);
			parent.add(n);

			if(b.getOwnerUnit().getFaction() == null) {
				n = createSimpleNode(getString("node.faction") + ": " +
									 getString("node.unknownfaction"), "faction");
			} else {
				n = createSimpleNode(getString("node.faction") + ": " +
									 b.getOwnerUnit().getFaction().toString(), "faction");
			}
			parent.add(n);
		}

	}

	/**
	 * Append information on the buildings type and true type.
	 *
	 * @param b
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendBuildingTypeInfo(Building b, DefaultMutableTreeNode parent, Collection expandableNodes) {
		DefaultMutableTreeNode n;
		// Fiete 20060910
		// added support for wahrerTyp
		if (b.getTrueBuildingType()!=null){
			n = createSimpleNode(getString(b.getTrueBuildingType()),
					 "warnung");
			parent.add(n);
		}
		
		
		// Typ
		n = createSimpleNode(getString("node.type") + ": " + b.getType().getName(),
							 b.getType().getID().toString());
		parent.add(n);
	}

	/**
	 * sets the ship name        editable sets the ship description editable shows a tree: (Type  :
	 * ship.type) (Completion : x % (y/z)) (Coast : ship.direction)  (if completed) (Range :
	 * node.range      (- components) Back - ingredients: 1.name ... n.name
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	private void showShip(Ship s) {
		setNameAndDescription(s, isPrivilegedAndNoSpy(s.getOwnerUnit()));

		appendShipInfo(s, rootNode, myExpandableNodes);
	}

	private void appendShipInfo(Ship s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		/* make sure that all unit relations have been established
		 (necessary for enter-/leave relations) */
		if(s.getRegion() != null) {
			s.getRegion().refreshUnitRelations();
		}

		// Schiffstyp
		if((s.getName() != null) && (s.getType().getName() != null)) {
			parent.add(createSimpleNode(getString("node.type") + ": " + s.getType().getName(),
										s.getType().getID().toString()));
		}

		int nominalShipSize = s.getShipType().getMaxSize();

		if(s.size != nominalShipSize) {
			// nominal size and damage
			int ratio = nominalShipSize != 0 ? ratio = (s.size * 100) / nominalShipSize : 0;

			parent.add(createSimpleNode(getString("node.completion") + ": " + ratio +
												  "% (" + s.size + "/" + nominalShipSize + ")","sonstiges"));

			appendShipDamageInfo(s, parent, expandableNodes);
		} else {
			// Kueste
			if (s.shoreId > -1) {
				parent.add(createSimpleNode(getString("node.shore") + ": "
						+ Direction.toString(s.shoreId), "shore_" + String.valueOf(s.shoreId)));
			}

			// Reichweite
			appendShipRangeInfo(s, parent, expandableNodes);
			
			// damage
			appendShipDamageInfo(s, parent, expandableNodes);

			// Fiete rework: Load and Overload
			appendShipLoadInfo(s, parent, expandableNodes);
		}

		
		// Besitzer
		if (s.getOwnerUnit() != null) {
			appendShipOwnerInfo(s, parent, expandableNodes);
		}

		appendContainerCommandInfo(s, parent, expandableNodes);


		// Insassen
		appendShipInmateInfo(s, parent, expandableNodes);
		
		// Baukosten info
		appendShipCosts(s, parent, expandableNodes);
		
		
		// Kommentare
		appendComments(s, parent, expandableNodes);
	}


	/**
	 *
	 * @param s
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendShipCosts(Ship s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		ShipType shipType = s.getShipType();
		if (shipType!=null && shipType.getMaxSize()>-1){
			// DefaultMutableTreeNode n = new DefaultMutableTreeNode(getString("node.buildingcost"));
			DefaultMutableTreeNode n = createSimpleNode(getString("node.buildingcost"), "buildingcost");
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.ShipCostExpanded"));
			DefaultMutableTreeNode m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
					shipType.getMaxSize() + " " + data.getTranslation("Holz"),"items/holz"));
			n.add(m);
			
			m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
					getString("node.buildingminskilllevel") + ": " + shipType.getBuildLevel(),"skills"));
			n.add(m);
		} 
	}

	/**
	 * Appends information about inmates, modified inmates and their respective weights.
	 *
	 * @param s
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendShipInmateInfo(Ship s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		List units = CollectionFactory.createLinkedList(s.units()); // the collection of units currently on the ship
		Collections.sort(units, sortIndexComparator);

		Collection modUnits = s.modifiedUnits(); // the collection of units on the ship in the next turn
		Collection allInmates = CollectionFactory.createLinkedList();
		allInmates.addAll(units);
		allInmates.addAll(modUnits);

		// sum up load, inmates, modified inmates over all units;
		int load=0;
		int inmates=0;
		int modInmates=0;
		if((units.size() > 0) || (modUnits.size() > 0)) {
			DefaultMutableTreeNode n = new DefaultMutableTreeNode("");
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.ShipInmatesExpanded"));

			DefaultMutableTreeNode m;
			for(Iterator iter = units.iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();
				StringBuffer text = new StringBuffer();
				Float weight = new Float(u.getWeight() / 100.0F);
				Float modWeight = new Float(u.getModifiedWeight() / 100.0F);

				if(s.getModifiedUnit(u.getID()) == null) {
					text.append("<- ");
				} else {
					//modLoad += u.getModifiedWeight();
				}

				// FIXME: pavkovic2004.07.01: check this out: load += u.getModifiedWeight();
 				load += u.getWeight();
				text.append(u.toString()).append(": ").append(weightNumberFormat.format(weight));

				if(weight.equals(modWeight) == false) {
					text.append(" (").append(weightNumberFormat.format(modWeight)).append(")");
				}

				text.append(" " + getString("node.weightunits"));

				UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
				m = new DefaultMutableTreeNode(w);
				n.add(m);
				inmates += u.getPersons();
			}

			for(Iterator iter = modUnits.iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();
				modInmates += u.getModifiedPersons();

				if(s.getUnit(u.getID()) == null) {
					StringBuffer text = new StringBuffer();
					Float weight = new Float(u.getWeight() / 100.0F);
					Float modWeight = new Float(u.getModifiedWeight() / 100.0F);

					// modLoad += u.getModifiedWeight();
					text.append("-> ");
					text.append(u.toString()).append(": ").append(weightNumberFormat.format(weight));

					if(weight.equals(modWeight) == false) {
						text.append(" (").append(weightNumberFormat.format(modWeight)).append(")");
					}

					text.append(" " + getString("node.weightunits"));

					UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(u, text.toString());
					m = new DefaultMutableTreeNode(w);
					n.add(m);
				}
			}

			if(inmates == modInmates) {
				// n.setUserObject(getString("node.inmates") + ": " + inmates);
				n.setUserObject(new UnitListNodeWrapper(getString("node.inmates")
						+ ": " + inmates, null, allInmates,"occupants"));
			} else {
				// n.setUserObject(getString("node.inmates") + ": " + inmates + " (" + modInmates + ")");
				n.setUserObject(new UnitListNodeWrapper(getString("node.inmates") + ": " + inmates + " (" + modInmates +
						")", null, allInmates,"occupants"));
			}

			if(ShipRoutePlanner.canPlan(s)) { // add a link to the ship route planer
				parent.add(new DefaultMutableTreeNode(new ShipRoutingPlanerButton(s)));
			}
		}
	}

	/**
	 * TODO DOCUMENT ME! 
	 * One line description.
	 *
	 * Long description.
	 *
	 * @param s
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendShipOwnerInfo(Ship s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		Unit owner = s.getOwnerUnit();
		SkillType sailingSkillType = data.rules.getSkillType(StringID.create("Segeln"), true);
		
		// owner faction
		Faction fac = owner.getFaction();
		DefaultMutableTreeNode n;
		if (fac == null) {
			n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
					getString("node.faction") + ": " + getString("node.unknownfaction"),
					"faction"));
		} else {
			n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
					getString("node.faction") + ": " + fac.toString(), "faction"));
		}
		parent.add(n);

		// captain
		UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(owner,
				getString("node.captain") + ": ", owner.getPersons(), owner
						.getModifiedPersons());
		w.setReverseOrder(true);
		w.setAdditionalIcon("captain");
		DefaultMutableTreeNode ownerNode = new DefaultMutableTreeNode(w);
		parent.add(ownerNode);
		
		// skill
		Skill sailingSkill = s.getOwnerUnit().getModifiedSkill(sailingSkillType);
		int sailingSkillAmount = (sailingSkill == null) ? 0 : sailingSkill.getLevel();
		String text = getString("node.sailingskill") + ": " + getString("node.captain")+" "
				+ sailingSkillAmount + " / " + s.getShipType().getCaptainSkillLevel() + ", ";
//		n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(
//				getString("node.captainskill") + ": " + sailingSkillAmount + " / "
//						+ s.getShipType().getCaptainSkillLevel(), "captain"));
//		parent.add(n);
		
		// Matrosen
		sailingSkillAmount = 0;

		// pavkovic 2003.10.03: use modifiedUnits to reflect FUTURE value?
		Collection modUnits = s.modifiedUnits(); // the collection of units on the ship in the next turn

		for(Iterator sailors = modUnits.iterator(); sailors.hasNext();) {
			Unit u = (Unit) sailors.next();
			sailingSkill = u.getModifiedSkill(sailingSkillType);

			if(sailingSkill != null) {
				sailingSkillAmount += (sailingSkill.getLevel() * u.getModifiedPersons());
			}
		}
		text += getString("node.crew")+" "+sailingSkillAmount + " / "
		+ s.getShipType().getSailorSkillLevel();
		n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(text, "crew"));
		parent.add(n);

	}

	/**
	 * TODO DOCUMENT ME! 
	 * One line description.
	 *
	 * Long description.
	 *
	 * @param s
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendShipLoadInfo(Ship s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		int cargo = s.getCargo();
//		if(cargo == -1) {
//			cargo = load;
//		}
		
		String strLoad = weightNumberFormat.format(new Float(cargo / 100.0F));

		String strModLoad = weightNumberFormat.format(new Float(s.getModifiedLoad() / 100.0F));

		String strCap = weightNumberFormat.format(new Float(s.getMaxCapacity() / 100.0F));

		// mark overloading with (!!!) (Fiete)
		String overLoad = "";
		if (s.getModifiedLoad()>s.getMaxCapacity()){
			overLoad = " (!!!)";
		} 
		
		if(log.isDebugEnabled()) {
			log.debug("outer ship state: " + s.getShipType().getCapacity() + "(strCap " +
					  strCap + ")");
		}

		DefaultMutableTreeNode n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.load") +
																		  ": " + strLoad +
																		  ((!strModLoad.equals(strLoad))
																		   ? (" (" +
																		   strModLoad + ") ")
																		   : " ") + "/ " +
																		  strCap + " " +
																		  getString("node.weightunits") + overLoad,
																		  "beladung"));
		parent.add(n);
		// explizit node for overloading a ship
		if (s.getModifiedLoad()>s.getMaxCapacity()) {
			n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.load") +
					 ": " +
					 getString("node.overloadedby") +
					 weightNumberFormat.format(new Float((s.getModifiedLoad() - s.getMaxCapacity()) / 100.0F)) +
					 " " +
					 getString("node.weightunits"),
					 "warnung"));
			parent.add(n);
		} 

	}

	/**
	 * 
	 *
	 * @param s
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendShipRangeInfo(Ship s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// Reichweite (bei Schaden aufrunden)
		int rad = s.getShipType().getRange();

		if((s.getOwnerUnit() != null) && (s.getOwnerUnit().race != null) &&
				   s.getOwnerUnit().race.getAdditiveShipBonus()!=0) {
				rad+=s.getOwnerUnit().race.getAdditiveShipBonus();
			}

		// rad = rad*(100.0-damageRatio)/100.0
		rad = new BigDecimal(rad).multiply(new BigDecimal(100 - s.damageRatio))
								 .divide(new BigDecimal(100), BigDecimal.ROUND_UP).intValue();

		String rangeString = getString("node.range") + ": " + rad;
		if((s.getOwnerUnit() != null) && (s.getOwnerUnit().race != null) &&
				   s.getOwnerUnit().race.getAdditiveShipBonus()!=0) {
				rangeString += (" (" + s.getOwnerUnit().race.getName() + ")");
			}

		parent.add(createSimpleNode(rangeString, "radius"));

	}

	/**
	 * 
	 *
	 * @param s
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendShipDamageInfo(Ship s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if(s.damageRatio > 0) {
			int absolute = new BigDecimal(s.damageRatio * s.size).divide(new BigDecimal(100),
																		 BigDecimal.ROUND_UP)
																 .intValue();
			parent.add(createSimpleNode(getString("node.damage") + ": " + s.damageRatio +
										"% / " + absolute, "damage"));
		}
	}

	/**
	 * Creates node for GIB KOMMANDO orders for ships and buildings. 
	 *
	 * @param s
	 * @param parent
	 * @param expandableNodes 
	 */
	private void appendContainerCommandInfo(UnitContainer s, DefaultMutableTreeNode parent, Collection expandableNodes) {
		// command relations
		Set givers = null;
		Set getters = null;
		for (Iterator unitIt = s.modifiedUnits().iterator(); unitIt.hasNext();){
			Unit inmate = (Unit) unitIt.next();
			for (Iterator it = inmate.getRelations(ControlRelation.class).iterator(); it.hasNext();){
				ControlRelation rel = (ControlRelation) it.next();
				if (givers==null) 
					givers = CollectionFactory.createHashSet();
				if (getters==null)
					getters = CollectionFactory.createHashSet();
				if (rel.source==inmate)
					givers.add(rel.source);
				if (rel.target==inmate)
					getters.add(rel.target);
			}
		}
		if (givers!=null || getters!=null){
			DefaultMutableTreeNode commandNode = new DefaultMutableTreeNode(getString("node.command"));
			expandableNodes.add(new NodeWrapper(commandNode, "EMapDetailsPanel.PersonsExpanded"));
			if (givers!=null){
				for (Iterator it = givers.iterator(); it.hasNext();){	
					Unit u2 = (Unit) it.next();
					UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
					unw.setAdditionalIcon("give");
					unw.setReverseOrder(true);
					commandNode.add(new DefaultMutableTreeNode(unw));
				}
			}
			if (getters!=null){
				for (Iterator it = getters.iterator(); it.hasNext();){	
					Unit u2 = (Unit) it.next();
					UnitNodeWrapper unw = nodeWrapperFactory.createUnitNodeWrapper(u2);
					unw.setAdditionalIcon("get");
					unw.setReverseOrder(true);
					commandNode.add(new DefaultMutableTreeNode(unw));
				}
			}
			parent.add(commandNode);
		}
	}

	/**
	 * sets the border name        not editable sets the border description not editable shows a
	 * tree: Type  : border.type Direction : border.direction (Completion: border.buildration %
	 * (needed stones of all stones)
	 *
	 * @param b TODO: DOCUMENT ME!
	 */
	private void showBorder(Border b) {
		setNameAndDescription("", "", false);

		appendBorderInfo(b, rootNode, myExpandableNodes);
	}

	private void appendBorderInfo(Border b, DefaultMutableTreeNode parent,
								  Collection expandableNodes) {
		parent.add(createSimpleNode(getString("node.type") + ": " + b.type, b.type));
		parent.add(createSimpleNode(getString("node.direction") + ": " +
									Direction.toString(b.direction),
									"border_" + String.valueOf(b.direction)));

		if((b.buildRatio > -1) && (b.buildRatio != 100)) {
			String str = getString("node.completion") + ": " + b.buildRatio + " %";

			if(Umlaut.normalize(b.type).equals("STRASSE")) {
				int stones = lastRegion.getRegionType().getRoadStones();
				str += (" (" + ((b.buildRatio * stones) / 100) + " / " + stones + " " +
				data.rules.getItemType(StringID.create("Stein")).getName() + ")");
			}

			parent.add(new DefaultMutableTreeNode(str));
		}
	}

	/**
	 * sets the spells name        not editable sets the spells description not editable shows a
	 * tree: Type  : spell.type Level : spell.level Rank  : spell.rank (Ship spells) (Distance
	 * spells) (Distance spells) (- components) Back - ingredients: 1.name ... n.name
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param backTarget TODO: DOCUMENT ME!
	 */
	private void showSpell(Spell s, Object backTarget) {
		setNameAndDescription(s, false);

		appendSpellInfo(s, rootNode, myExpandableNodes, backTarget);
	}

	private void appendSpellInfo(Spell s, DefaultMutableTreeNode parent,
								 Collection expandableNodes, Object backTarget) {
		
		if (s.getLevel()<0 && s.getRank()<0){
			// no information available
			DefaultMutableTreeNode noInfo=createSimpleNode(getString("spell.noinfo"), "spell_noinfo");
			parent.add(noInfo);
		} else {
			// more information to tell about
			parent.add(createSimpleNode(getString("node.type") + ": " + s.getTypeName(),"spell_type"));
			parent.add(createSimpleNode(getString("node.level") + ": " + s.getLevel(),"spell_level"));
			parent.add(createSimpleNode(getString("node.rank") + ": " + s.getRank(),"spell_rank"));
	
			if(s.getOnShip()) {
				// parent.add(new DefaultMutableTreeNode(getString("node.spell.ship")));
				parent.add(createSimpleNode(getString("node.spell.ship"),"spell_ship"));
			}
	
			if(s.getIsFar()) {
				// parent.add(new DefaultMutableTreeNode(getString("node.spell.far")));
				parent.add(createSimpleNode(getString("node.spell.far"),"spell_far"));
			}
	
			if((s.getComponents() != null) && (s.getComponents().size() > 0)) {
				DefaultMutableTreeNode componentsNode = new DefaultMutableTreeNode(getString("node.components"));
				parent.add(componentsNode);
				expandableNodes.add(new NodeWrapper(componentsNode,
													"EMapDetailsPanel.SpellComponentsExpanded"));
	
				for(Iterator iter = s.getComponents().keySet().iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String val = (String) s.getComponents().get(key);
					DefaultMutableTreeNode compNode;
	
					if(key.equalsIgnoreCase("Aura")) {
						int blankPos = val.indexOf(" ");
	
						if((blankPos > 0) && (blankPos < val.length())) {
							String aura = val.substring(0, blankPos);
							String getLevelAtDays = val.substring(blankPos + 1, val.length());
	
							if(getLevelAtDays.equals("0")) {
								compNode = createSimpleNode(aura + " " + getString("node.aura"), "aura");
							} else if(getLevelAtDays.equals("1")) {
								compNode = createSimpleNode(aura + " " + getString("node.aura") +
															" * " + getString("node.level"), "aura");
							} else {
								compNode = createSimpleNode(aura + " " + getString("node.aura") +
															" * " + getLevelAtDays + " * " +
															getString("node.level"), "aura");
							}
	
						} else {
							compNode = createSimpleNode(key + ": " + val, "aura");
						}
					} else if(key.equalsIgnoreCase("permanente Aura")){
						int blankPos = val.indexOf(" ");
	
						if((blankPos > 0) && (blankPos < val.length())) {
							String aura = val.substring(0, blankPos);
							String getLevelAtDays = val.substring(blankPos + 1, val.length());
	
							if(getLevelAtDays.equals("0")) {
								compNode = createSimpleNode(aura + " " + getString("node.permanenteaura"), "permanentaura");
							} else if(getLevelAtDays.equals("1")) {
								compNode = createSimpleNode(aura + " " + getString("node.permanenteaura") +
															" * " + getString("node.level"), "permanentaura");
							} else {
								compNode = createSimpleNode(aura + " " + getString("node.permanenteaura") +
															" * " + getLevelAtDays + " * " +
															getString("node.level"), "permanentaura");
							}
	
						} else {
							compNode = createSimpleNode(key + ": " + val, "permanentaura");
						}
						
					} else {
						int blankPos = val.indexOf(" ");
	
						if((blankPos > 0) && (blankPos < val.length())) {
							String usage = val.substring(0, blankPos);
							String getLevelAtDays = val.substring(blankPos + 1, val.length());
							if(getLevelAtDays.equals("0")) {
								compNode = createSimpleNode(usage + " " + Translations.getOrderTranslation(key), "items/" + key);
							} else if(getLevelAtDays.equals("1")) {
								compNode = createSimpleNode(usage + " " + Translations.getOrderTranslation(key) +
															" * " + getString("node.level"), "items/" + key);
							} else {
								compNode = createSimpleNode(usage + " " + getString("node.permanenteaura") +
															" * " + getLevelAtDays + " * " +
															getString("node.level"), "items/" + key);
							}
						} else {
							compNode = createSimpleNode(key + ": " + val, "items/" + key);
						}
					}
					componentsNode.add(compNode);
				}
			}
	
			// spellsyntax
			if(s.getSyntaxString()!=null) {
				parent.add(createSimpleNode(s.getSyntaxString(),"spell_syntax"));
			}
		
		}
		// Backbutton
		parent.add(new DefaultMutableTreeNode(new BackButton(backTarget)));
	}

	/**
	 * sets the potions name        not editable sets the potions description not editable shows a
	 * tree: Level : potion.level + ingredients (only if |potion.ingredients| &gt; 0) Back -
	 * ingredients: 1.name ... n.name
	 *
	 * @param p TODO: DOCUMENT ME!
	 * @param backTarget TODO: DOCUMENT ME!
	 */
	private void showPotion(Potion p, Object backTarget) {
		setNameAndDescription(p, false);

		appendPotionInfo(p, rootNode, myExpandableNodes, backTarget);
	}

	private void appendPotionInfo(Potion p, DefaultMutableTreeNode parent,
								  Collection expandableNodes, Object backTarget) {
		parent.add(new DefaultMutableTreeNode(getString("node.level") + ": " + p.getLevel()));

		if(p.ingredients().size() > 0) {
			DefaultMutableTreeNode ingredientsNode = new DefaultMutableTreeNode(getString("node.ingredients"));
			parent.add(ingredientsNode);

			for(Iterator iter = p.ingredients().iterator(); iter.hasNext();) {
				Item ingredient = (Item) iter.next();
				ingredientsNode.add(createSimpleNode(ingredient.getItemType(),
													 "items/" +
													 ingredient.getItemType().getIconName()));
			}
		}
		parent.add(new DefaultMutableTreeNode(new BackButton(backTarget)));
	}

	/**
	 * sets the island name        editable sets the island description editable shows an empty
	 * tree
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	private void showIsland(Island i) {
		showNothing();
		setNameAndDescription(i, true);
	}

	/**
	 * sets the name ""            not editable sets the description ""     not editable shows an
	 * empty tree
	 */
	private void showNothing() {
		setNameAndDescription("", "", false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		data = e.getGameData();
		orders.gameDataChanged(e);
		unitsTools.setRules(e.getGameData().rules);
		showNothing();
		lastRegion = null;
		displayedObject = null;
		contextManager.setGameData(data);
	}

	private void storeExpansionState() {
		for(Iterator iter = myExpandableNodes.iterator(); iter.hasNext();) {
			NodeWrapper nw = (NodeWrapper) iter.next();

			if(nw.getNode().getChildCount() > 0) {
				DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) nw.getNode()
																			   .getFirstChild();
				settings.setProperty(nw.getDesc(),
									 tree.isVisible(new TreePath(firstChild.getPath())) ? "1" : "0");
			}
		}
	}

	private void restoreExpansionState() {
		for(Iterator iter = myExpandableNodes.iterator(); iter.hasNext();) {
			NodeWrapper nw = (NodeWrapper) iter.next();

			if(Integer.parseInt(settings.getProperty(nw.getDesc(), "1")) != 0) {
				if(nw.getNode().getChildCount() > 0) {
					DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) nw.getNode()
																				   .getFirstChild();
					tree.makeVisible(new TreePath(firstChild.getPath()));
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void quit() {
		storeExpansionState();
	}

	/**
	 * Selection event handler, update all elements in this panel with the appropriate data.
	 *
	 * @param se TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent se) {
		addTag.setEnabled(false);
		removeTag.setEnabled(false);
		
		/**
		 * @author Fiete
		 * if selection changes, our mySelectedUnits should be cleared
		 */
		this.mySelectedUnits.clear();
		
		
		/**
		 * Decide whether to show a single object (activeObject) or a collection of objects
		 * (selectedObjects). The display for collection is at the moment limited to collections
		 * of regions. Other implementations should be added.
		 */
		Collection c = se.getSelectedObjects();

		if((c != null) && (c.size() > 1)) {
			if(showMultiple(c)) {
				return;
			}
		}

		/**
		 * No collection of objects could be displayd. Show the (single) active object.
		 */
		show(se.getActiveObject());
	}

	protected void show(Object o) {
		show(o, true);
	}

	/**
	 * Tries to show the given collection. If no implementation applies, false is returned,
	 * otherwise true.
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected boolean showMultiple(Collection c) {
		boolean regions = true;
		boolean factions = true;
		boolean units = true;

		for(Iterator iter = c.iterator(); iter.hasNext() && (regions || factions || units);) {
			Object o = iter.next();

			if(regions && !(o instanceof Region)) {
				regions = false;
			}

			if(factions && !(o instanceof Faction)) {
				factions = false;
			}

			if(units && !(o instanceof Unit)) {
				units = false;
			}
		}

		contextManager.setFailFallback(null, null);
		storeExpansionState();
		myExpandableNodes.clear();
		rootNode.removeAllChildren();

		if(regions) {
			setNameAndDescription(c.size() + " Regionen", "", false);
			appendMultipleRegionInfo(c, rootNode, myExpandableNodes);
		} else if(factions) {
			setNameAndDescription("", "", false);
			appendFactionsInfo(c, rootNode, myExpandableNodes);
		} else if(units) {
			setNameAndDescription(c.size() + " Einheiten", "", false);
			appendUnitsInfo(c, rootNode, myExpandableNodes);
		} else {
			return false;
		}

		treeModel.reload();
		restoreExpansionState();

		return true;
	}

	protected void show(Object o, boolean flag) {
		try {
			if((flag && (displayedObject == o))) {
				return;
			}

			addTag.setEnabled(false);
			removeTag.setEnabled(false);

			Object oldDisplayedObject = displayedObject;
			displayedObject = o;

			if((displayedObject != null) && displayedObject instanceof SimpleNodeWrapper) {
				displayedObject = ((SimpleNodeWrapper) displayedObject).getObject();
			}

			contextManager.setFailFallback(null, null);

			// store state, empty tree and expandableNodes
			storeExpansionState();
			myExpandableNodes.clear();
			rootNode.removeAllChildren();

			if(displayedObject == null) {
				showNothing();
			} else if(displayedObject instanceof ZeroUnit) {
				showRegion(((Unit) displayedObject).getRegion());
				lastRegion = ((Unit) displayedObject).getRegion();
				contextManager.setFailFallback(displayedObject, commentContext);
			} else if(displayedObject instanceof Unit) {
				showUnit((Unit) displayedObject);
				lastRegion = ((Unit) displayedObject).getRegion();
				contextManager.setFailFallback(displayedObject, unitCommentContext);
			} else if(displayedObject instanceof Region) {
				showRegion((Region) displayedObject);
				lastRegion = (Region) displayedObject;
				contextManager.setFailFallback(displayedObject, commentContext);
			} else if(displayedObject instanceof Faction) {
				showFaction((Faction) displayedObject);
				contextManager.setFailFallback(displayedObject, commentContext);
			} else if(displayedObject instanceof Group) {
				showGroup((Group) displayedObject);
			} else if(displayedObject instanceof Building) {
				showBuilding((Building) displayedObject);
				lastRegion = ((Building) displayedObject).getRegion();
				contextManager.setFailFallback(displayedObject, commentContext);
			} else if(displayedObject instanceof Ship) {
				showShip((Ship) displayedObject);
				lastRegion = ((Ship) displayedObject).getRegion();
				contextManager.setFailFallback(displayedObject, commentContext);
			} else if(displayedObject instanceof Border) {
				showBorder((Border) displayedObject);
			} else if(displayedObject instanceof Spell) {
				showSpell((Spell) displayedObject, oldDisplayedObject);
			} else if(displayedObject instanceof Potion) {
				showPotion((Potion) displayedObject, oldDisplayedObject);
			} else if(displayedObject instanceof PotionNodeWrapper) {
				showPotion(((PotionNodeWrapper) displayedObject).getPotion(), oldDisplayedObject);
			} else if(displayedObject instanceof Island) {
				showIsland((Island) displayedObject);
			}
		} catch(Exception e) {
			// something very evil happens. Empty tree and expandableNodes
			log.error("EMapDetailsPanel.show hitting on exception", e);
			myExpandableNodes.clear();
			rootNode.removeAllChildren();
		}

		// reload tree, restore state
		treeModel.reload();
		restoreExpansionState();
	}

	/**
	 * Calculates the wage for the units of a certain faction in the specified region.
	 *
	 * @param wage TODO: DOCUMENT ME!
	 * @param isOrkRace TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private int getWage(int wage, boolean isOrkRace) {
		if(isOrkRace) {
			switch(wage) {
			case 12:
				wage = 11;

				break;

			case 13:
				wage = 12;

				break;

			case 14:
				wage = 12;

				break;

			case 15:
				wage = 13;

				break;
			}
		}

		return wage;
	}

	/** 
	 * handles a value change annotated by double click.
	 */
	private void handleValueChange() {
		removeTag.setEnabled(false);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if(node == null) {
			return;
		}

		Object o = node.getUserObject();

		if(o instanceof ActionListener) {
			((ActionListener) o).actionPerformed(null);

			return;
		}

		if(o instanceof SimpleNodeWrapper) {
			o = ((SimpleNodeWrapper) o).getObject();
		}

		Object fireObj = null;

		if(o instanceof UnitNodeWrapper) {
			fireObj = ((UnitNodeWrapper) o).getUnit();
		} else if(o instanceof UnitContainerNodeWrapper) {
			fireObj = ((UnitContainerNodeWrapper) o).getUnitContainer();
		} else if(o instanceof RegionNodeWrapper) {
			fireObj = ((RegionNodeWrapper) o).getRegion();
		} else if(o instanceof Building) {
			fireObj = o;
		} else if(o instanceof Ship) {
			fireObj = o;
		} else if(o instanceof Spell) {
			fireObj = o;
		} else if(o instanceof CombatSpell) {
			fireObj = ((CombatSpell) o).getSpell();
		} else if(o instanceof Potion) {
			fireObj = o;
		} else if(o instanceof PotionNodeWrapper) {
			fireObj = ((PotionNodeWrapper) o).getPotion();
		} else if(o instanceof String) {
			String s = (String) o;

			if(s.indexOf(": ") > 0) {
				TreeNode node2 = node.getParent();

				if((node2 != null) && (node2 instanceof DefaultMutableTreeNode)) {
					Object o2 = ((DefaultMutableTreeNode) node2).getUserObject();

					if((o2 instanceof String) && ((String) o2).equals(getString("node.tags"))) {
						removeTag.setEnabled(true);
					}
				}
			}
		}

		if(fireObj != null) {
			dispatcher.fire(new SelectionEvent(this, null, fireObj));
		}
	}

	private void appendAlliances(Map allies, DefaultMutableTreeNode parent) {
		if((allies == null) || (parent == null)) {
			return;
		}

		for(Iterator iter = allies.values().iterator(); iter.hasNext();) {
			Alliance alliance = (Alliance) iter.next();
			DefaultMutableTreeNode n = new DefaultMutableTreeNode(alliance.getFaction());
			parent.add(n);
			n.add(new DefaultMutableTreeNode(alliance.stateToString()));
		}
	}

	private void appendComments(UnitContainer uc, DefaultMutableTreeNode parent,
			Collection expandableNodes) {
		if ((uc.comments != null) && (uc.comments.size() > 0)) {
			CommentListNode commentNode = new CommentListNode(uc, getString("node.comments"));
			parent.add(commentNode);
			expandableNodes.add(new NodeWrapper(commentNode, "EMapDetailsPanel.CommentsExpanded"));

			int i = 0;

			for (Iterator iter = uc.comments.iterator(); iter.hasNext() && (i < uc.comments.size()); i++) {
				commentNode.add(new DefaultMutableTreeNode(new UnitContainerCommentNodeWrapper(uc,
						(String) iter.next())));
			}
		}
	}

	private void appendComments(Unit u, DefaultMutableTreeNode parent, Collection expandableNodes) {
		if ((u.comments != null) && (u.comments.size() > 0)) {
			UnitCommentListNode unitCommentNode = new UnitCommentListNode(u,
					getString("node.comments"));
			parent.add(unitCommentNode);
			expandableNodes.add(new NodeWrapper(unitCommentNode,
					"EMapDetailsPanel.UnitCommentsExpanded"));

			int i = 0;

			for (Iterator iter = u.comments.iterator(); iter.hasNext() && (i < u.comments.size()); i++) {
				String actComment = (String) iter.next();
				UnitCommentNodeWrapper w = new UnitCommentNodeWrapper(u, actComment);
				unitCommentNode.add(new DefaultMutableTreeNode(w));
			}
		}
	}
	
	
	

	// create a popup menu

	/*private JPopupMenu getDetailsContextMenu(DefaultMutableTreeNode node) {
	    if (displayedObject != null) {
	        return new DetailsContextMenu(displayedObject, node);
	    }
	    return null;
	  }*/

	/**
	 * Should return all short cuts this class want to be informed. The elements should be of type
	 * javax.swing.KeyStroke
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getShortCuts() {
		return shortCuts.iterator();
	}

	/**
	 * This method is called when a shortcut from getShortCuts() is recognized.
	 *
	 * @param shortcut TODO: DOCUMENT ME!
	 */
	public void shortCut(javax.swing.KeyStroke shortcut) {
		int index = shortCuts.indexOf(shortcut);

		switch(index) {
		case -1:
			break; //unknown shortcut

		case 0:
		case 1:
			DesktopEnvironment.requestFocus("COMMANDS");
			DesktopEnvironment.requestFocus("ORDERS");

			break;

		case 2:
			orders.setLimitMakeCompletion(!orders.getLimitMakeCompletion());

			// toggle "limit make completion"
			break;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter createPreferencesAdapter() {
		return new EMapDetailsPreferences(settings, this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param actionEvent TODO: DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		if(actionEvent.getSource() == addTag) {
			String key = JOptionPane.showInputDialog(tree, getString("addtag.tagname.message"));

			if((key != null) && (key.length() > 0)) {
				String value = JOptionPane.showInputDialog(tree,
														   getString("addtag.tagvalue.message"));

				if((value != null) && (value.length() > 0)) {
					if(displayedObject instanceof UnitContainer) {
						((UnitContainer) displayedObject).putTag(key, value);
					} else if(displayedObject instanceof Unit) {
						Unit u = (Unit) displayedObject;
						u.putTag(key, value);
					    // TODO: Coalesce unitordersevent
						dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this,u));
					}

					show(displayedObject, false);

				}
			}
		} else {
			Object o = tree.getLastSelectedPathComponent();

			if(o instanceof DefaultMutableTreeNode) {
				Object o2 = ((DefaultMutableTreeNode) o).getUserObject();

				if(o2 instanceof String) {
					int index = ((String) o2).indexOf(": ");

					if(index > 0) {
						String tagName = ((String) o2).substring(0, index);
						log.info("Removing " + tagName);

						if(displayedObject instanceof UnitContainer) {
							((UnitContainer) displayedObject).removeTag(tagName);
							show(displayedObject, false);
						} else if(displayedObject instanceof Unit) {
							Unit u = (Unit) displayedObject;
							u.removeTag(tagName);
							show(displayedObject, false);

						    // TODO: Coalesce unitordersevent
							dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this,u));
						}
					}
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isShowingTagButtons() {
		return showTagButtons;
	}

	public boolean isAllowingCustomIcons(){
		return allowCustomIcons;
	}
	
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setShowTagButtons(boolean bool) {
		if(bool != isShowingTagButtons()) {
			showTagButtons = bool;
			settings.setProperty("EMapDetailsPanel.ShowTagButtons", bool ? "true" : "false");

			if(bool) {
				treeContainer.add(tagContainer, BorderLayout.SOUTH);
				tagContainer.doLayout();
			} else {
				treeContainer.remove(tagContainer);
			}

			treeContainer.doLayout();
			treeContainer.validate();
			treeContainer.repaint();
		}
	}

	public void setAllowCustomIcons(boolean bool) {
		if(bool != isAllowingCustomIcons()) {
			allowCustomIcons = bool;
			settings.setProperty("EMapDetailsPanel.AllowCustomIcons", bool ? "true" : "false");
			treeContainer.doLayout();
			treeContainer.validate();
			treeContainer.repaint();
		}
	}
	
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param src TODO: DOCUMENT ME!
	 */
	public void updateTree(Object src) {
		tree.treeDidChange();

		/*        if (tree.getModel() instanceof DefaultTreeModel) {
		            ((DefaultTreeModel)tree.getModel()).reload();
		        }*/

		// Use the UI Fix
		javax.swing.plaf.TreeUI treeUI = tree.getUI();

		if(treeUI instanceof javax.swing.plaf.basic.BasicTreeUI) {
			javax.swing.plaf.basic.BasicTreeUI ui2 = (javax.swing.plaf.basic.BasicTreeUI) treeUI;
			int i = ui2.getLeftChildIndent();
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
		int index = shortCuts.indexOf(stroke);

		return getString("shortcuts.description." + String.valueOf(index));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getListenerDescription() {
		return getString("shortcuts.title");
	}

	private class NodeWrapper {
		private DefaultMutableTreeNode node = null;
		private String desc = "";

		/**
		 * Creates a new NodeWrapper object.
		 *
		 * @param n TODO: DOCUMENT ME!
		 * @param d TODO: DOCUMENT ME!
		 */
		public NodeWrapper(DefaultMutableTreeNode n, String d) {
			node = n;
			desc = d;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public DefaultMutableTreeNode getNode() {
			return node;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String getDesc() {
			return desc;
		}
	}

	private class SkillStatItem {
		/** TODO: DOCUMENT ME! */
		public Skill skill = null;

		/** TODO: DOCUMENT ME! */
		public int unitCounter = 0;

		/** TODO: DOCUMENT ME! */
		public List units = CollectionFactory.createLinkedList();

		/**
		 * Creates a new SkillStatItem object.
		 *
		 * @param skill TODO: DOCUMENT ME!
		 * @param unitCounter TODO: DOCUMENT ME!
		 */
		public SkillStatItem(Skill skill, int unitCounter) {
			this.skill = skill;
			this.unitCounter = unitCounter;
		}
	}

	private class SkillStatItemComparator implements Comparator {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 * @param o2 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int compare(Object o1, Object o2) {
			int retVal = ((SkillStatItem) o1).skill.getName().compareTo(((SkillStatItem) o2).skill.getName());

			if(retVal == 0) {
				retVal = ((SkillStatItem) o2).skill.getLevel() -
						 ((SkillStatItem) o1).skill.getLevel();
			}

			return retVal;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param o1 TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean equals(Object o1) {
			return false;
		}
	}

	class EMapDetailsPreferences extends JPanel implements ExtendedPreferencesAdapter {
		private Properties settings = null;
		private EMapDetailsPanel source = null;
		private List subAdapters;
		private PreferencesAdapter regionPref;
		private JCheckBox chkShowTagButtons;
		private JCheckBox chkAllowCustomIcons;

		/**
		 * Creates a new EMapDetailsPreferences object.
		 *
		 * @param settings TODO: DOCUMENT ME!
		 * @param source TODO: DOCUMENT ME!
		 */
		public EMapDetailsPreferences(Properties settings, EMapDetailsPanel source) {
			this.settings = settings;
			this.source = source;

			subAdapters = CollectionFactory.createArrayList(2);
			subAdapters.add(editor.getPreferencesAdapter());
			subAdapters.add(orders.getPreferencesAdapter());

			// layout this container
			/*
			    setLayout(new GridBagLayout());
			    GridBagConstraints c = new GridBagConstraints();
			    GridBagHelper.setConstraints(
			    c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 0.0,
			    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, c.insets, 0, 0);
			    // data view panel
			    JPanel help = getDataViewPanel();
			    this.add(help, c);
			    GridBagHelper.setConstraints(
			     c, 0, 1, GridBagConstraints.REMAINDER, 1, 1.0, 1.0, // different weighty!
			    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, c.insets, 0, 0);
			    // region panel information
			    regionPref = regionPanel.getPreferredAdapter();
			    this.add(regionPref.getComponent(), c);
			 */
			// layout this container
			setLayout(new BorderLayout());

			// data view panel
			JPanel help = getDataViewPanel();
			this.add(help, BorderLayout.NORTH);

			// region panel information
			regionPref = regionPanel.getPreferredAdapter();
			this.add(regionPref.getComponent(), BorderLayout.CENTER);
		}

		private JPanel getDataViewPanel() {
			JPanel help = new JPanel(new GridBagLayout());
			help.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
											getString("prefs.datadisplay")));

			GridBagConstraints c = new GridBagConstraints(0, 0, 2, 1, 1.0, 0,
														  GridBagConstraints.NORTH,
														  GridBagConstraints.HORIZONTAL,
														  new Insets(3, 3, 3, 3), 0, 0);

			c.anchor = GridBagConstraints.WEST;
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.1;
			chkShowTagButtons = new JCheckBox(getString("prefs.showTagButtons"),
											  source.isShowingTagButtons());
			help.add(chkShowTagButtons, c);
			
			c.gridy = 1;
			chkAllowCustomIcons = new JCheckBox(getString("prefs.allowCustomIcons"),
					  source.isAllowingCustomIcons());
			help.add(chkAllowCustomIcons, c);
			
			

			return help;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getChildren() {
			return subAdapters;
		}

        /**
         * @deprecated not implemented?
         * @see com.eressea.swing.preferences.PreferencesAdapter#initPreferences()
         */
        public void initPreferences() {
            regionPref.initPreferences();
            // TODO: implement it
        }

		/**
		 * preferences adapter code:
		 * @see com.eressea.swing.preferences.PreferencesAdapter#applyPreferences()
		 */
		public void applyPreferences() {
			source.setShowTagButtons(chkShowTagButtons.isSelected());
			source.setAllowCustomIcons(chkAllowCustomIcons.isSelected());
			regionPref.applyPreferences();
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
	}

	/**
	 * A class that can be used to interpret a tree entry as a button
	 */
	private abstract class SimpleActionObject implements CellObject, ActionListener {
		protected List icons;
		private final String key;

		SimpleActionObject(String key) {
			this.key = key;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			return getString(key);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean emphasized() {
			return false;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getIconNames() {
			return icons;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void propertiesChanged() {
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param settings TODO: DOCUMENT ME!
		 * @param prefix TODO: DOCUMENT ME!
		 * @param adapter TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public NodeWrapperDrawPolicy init(Properties settings, String prefix,
										  NodeWrapperDrawPolicy adapter) {
			return null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param settings TODO: DOCUMENT ME!
		 * @param adapter TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
			return null;
		}
	}

	/**
	 * A class to recognize a back-button in the tree
	 */
	private class BackButton extends SimpleActionObject {
		private Object target;

		BackButton(Object t) {
			super("backbutton.text");
			icons = CollectionFactory.createArrayList(1);
			icons.add("zurueck");
			target = t;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Object getTarget() {
			return target;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			dispatcher.fire(new SelectionEvent(EMapDetailsPanel.this, null, target));
		}
	}

	/**
	 * Ship routing planer button
	 */
	private class ShipRoutingPlanerButton extends SimpleActionObject {
		private Ship target;

		ShipRoutingPlanerButton(Ship s) {
			super("shipplaner.text");
			target = s;
			icons = CollectionFactory.createArrayList(1);
			icons.add("koordinaten");
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void actionPerformed(ActionEvent e) {
			Unit unit = ShipRoutePlanner.planShipRoute(target, data, EMapDetailsPanel.this);

			if(unit != null) {
				dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, unit));
			}
		}
	}

	private class StealthContextFactory implements ContextFactory {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param data TODO: DOCUMENT ME!
		 * @param argument TODO: DOCUMENT ME!
		 * @param selectedObjects TODO: DOCUMENT ME!
		 * @param node TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public javax.swing.JPopupMenu createContextMenu(EventDispatcher dispatcher, 
                                                        GameData data, Object argument,
														Collection selectedObjects,
														DefaultMutableTreeNode node) {
			if(argument instanceof Unit) {
				try {
					return new StealthContextMenu((Unit) argument);
				} catch(IllegalArgumentException exc) {
				}
			}

			return null;
		}

		private class StealthContextMenu extends JPopupMenu implements ActionListener {
			private Unit unit;

			/**
			 * Creates a new StealthContextMenu object.
			 *
			 * @param u TODO: DOCUMENT ME!
			 *
			 * @throws IllegalArgumentException TODO: DOCUMENT ME!
			 */
			public StealthContextMenu(Unit u) {
				unit = u;

				Skill stealth = unit.getSkill(data.rules.getSkillType(EresseaConstants.S_TARNUNG,
																	  true));

				if(stealth.getLevel() > 0) {
					for(int i = 0; i <= stealth.getLevel(); i++) {
						JMenuItem item = new JMenuItem(String.valueOf(i));
						item.addActionListener(this);
						this.add(item);
					}
				} else {
					throw new IllegalArgumentException("Unit has no stealth capability.");
				}
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void actionPerformed(ActionEvent e) {
				int newStealth = Integer.parseInt(e.getActionCommand());

				if(newStealth != unit.stealth) {
					unit.stealth = newStealth;

					EMapDetailsPanel.this.show(unit, false);
					data.getGameSpecificStuff().getOrderChanger().addHideOrder(unit,
																			   e.getActionCommand()
																				.toString());

					/* Note: Of course it would be better to inform all that a game data object
					 *       has changed but I think it's not necessary and consumes too much time
					 *                  Andreas
					 */
					dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, unit));
				}
			}
		}
	}

	private class CombatStateContextFactory implements ContextFactory {
		private class CombatStateContextMenu extends JPopupMenu implements ActionListener {
			private Unit unit;

			/**
			 * Creates a new CombatStateContextMenu object.
			 *
			 * @param u TODO: DOCUMENT ME!
			 */
			public CombatStateContextMenu(Unit u) {
				this.unit = u;
				init();
			}

			protected void init() {
				for(int i = 0; i < 6; i++) {
					addItem(i);
				}
			}

			protected void addItem(int state) {
				JMenuItem item = new JMenuItem(Unit.combatStatusToString(state));
				item.setActionCommand(String.valueOf(state));
				item.addActionListener(this);
				this.add(item);
			}

			/**
			 * TODO: DOCUMENT ME!
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void actionPerformed(ActionEvent e) {
				int newState = Integer.parseInt(e.getActionCommand());

				if(newState != unit.combatStatus) {
					unit.combatStatus = newState;

					EMapDetailsPanel.this.show(unit, false);
					data.getGameSpecificStuff().getOrderChanger().addCombatOrder(unit, newState);

					// Note: Same as in StealthContextMenu
					//dispatcher.fire(new GameDataEvent(EMapDetailsPanel.this, data));
					dispatcher.fire(new UnitOrdersEvent(EMapDetailsPanel.this, unit));
				}
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param data TODO: DOCUMENT ME!
		 * @param argument TODO: DOCUMENT ME!
		 * @param selectedObjects TODO: DOCUMENT ME!
		 * @param node TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public javax.swing.JPopupMenu createContextMenu(EventDispatcher dispatcher, 
                                                        GameData data, Object argument,
														Collection selectedObjects,
														DefaultMutableTreeNode node) {
			if(argument instanceof Unit) {
				return new CombatStateContextMenu((Unit) argument);
			}

			return null;
		}
	}

	// make comment nodes recognizable through this class
	private class CommentNode extends DefaultMutableTreeNode {
		private UnitContainer uc = null;

		/**
		 * Creates a new CommentNode object.
		 *
		 * @param uc TODO: DOCUMENT ME!
		 * @param comment TODO: DOCUMENT ME!
		 */
		public CommentNode(UnitContainer uc, String comment) {
			super(comment);
			this.uc = uc;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public UnitContainer getUnitContainer() {
			return uc;
		}
	}

	private class CommentListNode extends CommentNode {
		/**
		 * Creates a new CommentListNode object.
		 *
		 * @param uc TODO: DOCUMENT ME!
		 * @param title TODO: DOCUMENT ME!
		 */
		public CommentListNode(UnitContainer uc, String title) {
			super(uc, title);
		}
	}

	
	//	 make unit comment nodes recognizable through this class
	private class UnitCommentNode extends DefaultMutableTreeNode {
		private Unit u = null;

		/**
		 * Creates a new CommentNode object.
		 *
		 * @param u TODO: DOCUMENT ME!
		 * @param comment TODO: DOCUMENT ME!
		 */
		public UnitCommentNode(Unit u, String comment) {
			super(comment);
			this.u = u;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Unit getUnit() {
			return u;
		}
	}
	
	private class UnitCommentListNode extends UnitCommentNode {
		/**
		 * Creates a new CommentListNode object.
		 *
		 * @param u TODO: DOCUMENT ME!
		 * @param title TODO: DOCUMENT ME!
		 */
		public UnitCommentListNode(Unit u, String title) {
			super(u, title);
		}
	}
	
	
	private class CommentContextFactory implements ContextFactory {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param data TODO: DOCUMENT ME!
		 * @param argument TODO: DOCUMENT ME!
		 * @param selectedObjects TODO: DOCUMENT ME!
		 * @param node TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public JPopupMenu createContextMenu(EventDispatcher dispatcher, 
		        GameData data, Object argument,
		        Collection selectedObjects,
		        DefaultMutableTreeNode node) {
			if(argument instanceof UnitContainer) {
				return new CommentContextMenu((UnitContainer) argument, node, false);
			} else if(argument instanceof CommentListNode) {
				return new CommentContextMenu(((CommentListNode) argument).getUnitContainer(),
											  node, true);
			} else if(argument instanceof CommentNode) {
				return new CommentContextMenu(((CommentNode) argument).getUnitContainer(), node,
											  false);
			} else if(argument instanceof UnitContainerCommentNodeWrapper) {
				return new CommentContextMenu(((UnitContainerCommentNodeWrapper) argument).getUnitContainer(), node,
											  false);
			}

			return null;
		}

		private class CommentContextMenu extends JPopupMenu implements ActionListener {
			private UnitContainer uc = null;
			private DefaultMutableTreeNode node = null;
			private UnitContainerCommentNodeWrapper nodeWrapper = null;
			private JMenuItem addComment;
			private JMenuItem removeComment;
			private JMenuItem modifyComment;
			private JMenuItem removeAllComments;

			/**
			 * Creates a new CommentContextMenu object.
			 *
			 * @param container TODO: DOCUMENT ME!
			 * @param node TODO: DOCUMENT ME!
			 * @param removeAll TODO: DOCUMENT ME!
			 */
			public CommentContextMenu(UnitContainer container, DefaultMutableTreeNode node,
									  boolean removeAll) {
				uc = container;

				
				this.node= node;
				Object o = null;
				if (node!=null){
					o = node.getUserObject();
					if (o instanceof UnitContainerCommentNodeWrapper){
						this.nodeWrapper = (UnitContainerCommentNodeWrapper) o;
					}
				}
				
				

				addComment = new JMenuItem(getString("menu.createcomment"));
				addComment.addActionListener(this);
				this.add(addComment);

				if(node != null && o != null) {
					if (o instanceof UnitContainerCommentNodeWrapper){
						modifyComment = new JMenuItem(getString("menu.changecomment"));
						modifyComment.addActionListener(this);
						removeComment = new JMenuItem(getString("menu.removecomment"));
						removeComment.addActionListener(this);

						this.add(modifyComment);
						this.add(removeComment);
					}
				}

				if(removeAll) {
					removeAllComments = new JMenuItem(getString("menu.removecomment.all"));
					removeAllComments.addActionListener(this);
					this.add(removeAllComments);
				}
			}

			private void addComment() {
				DefaultMutableTreeNode parent = null;

				for(Enumeration en = rootNode.children(); en.hasMoreElements();) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) en.nextElement();
					Object obj = n.getUserObject();

					if((obj != null) && obj instanceof String &&
						   obj.equals(getString("node.comments"))) {
						parent = n;

						break;
					}
				}

				if(parent == null) {
					parent = new CommentListNode(uc, getString("node.comments"));
					rootNode.add(parent);
				}

				if(uc.comments == null) {
					uc.comments = CollectionFactory.createLinkedList();
				}

				if(uc.comments.size() != parent.getChildCount()) {
					log.info("EMapDetailsPanel.DetailsContextMenu.getCreateCommentMenuItem(): number of comments and nodes differs!");

					return;
				}

				String newComment = uc.toString();
				uc.comments.add(newComment);

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new UnitContainerCommentNodeWrapper(uc, newComment));
				parent.add(newNode);
				treeModel.reload();
				restoreExpansionState();
				tree.startEditingAtPath(new TreePath(newNode.getPath()));
				storeExpansionState();
			}

			private void modifyComment() {
				if((nodeWrapper.getUnitContainer() != null) && (nodeWrapper.getUnitContainer().comments != null)) {
					tree.startEditingAtPath(new TreePath(node.getPath()));
				}
			}

			private void removeComment() {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

				if(uc.comments != null) {
					if(uc.comments.size() == parent.getChildCount()) {
						uc.comments.remove(parent.getIndex(node));
					} else {
						log.info("EMapDetailsPanel.DetailsContextMenu.getDeleteCommentMenuItem(): number of comments and nodes differs!");

						return;
					}
				}

				treeModel.removeNodeFromParent(node);

				if(parent.getChildCount() == 0) {
					treeModel.removeNodeFromParent(parent);
				}

				treeModel.reload();
				restoreExpansionState();
			}

			private void removeAllComments() {
				uc.comments.clear();
				uc.comments = null;
				EMapDetailsPanel.this.show(uc, false);
			}

			/**
			 * Invoked when an action occurs.
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == addComment) {
					addComment();
				} else if(e.getSource() == removeComment) {
					removeComment();
				} else if(e.getSource() == modifyComment) {
					modifyComment();
				} else if(e.getSource() == removeAllComments) {
					removeAllComments();
				}
			}
		}
	}

	private class UnitCommentContextFactory implements ContextFactory {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param data TODO: DOCUMENT ME!
		 * @param argument TODO: DOCUMENT ME!
		 * @param selectedObjects TODO: DOCUMENT ME!
		 * @param node TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public JPopupMenu createContextMenu(EventDispatcher dispatcher, 
		        GameData data, Object argument,
		        Collection selectedObjects,
		        DefaultMutableTreeNode node) {
			if(argument instanceof Unit) {
				return new UnitCommentContextMenu((Unit) argument, node, false);
			} else if(argument instanceof UnitCommentListNode) {
				return new UnitCommentContextMenu(((UnitCommentListNode) argument).getUnit(),
											  node, true);
			
			} else if(argument instanceof UnitCommentNodeWrapper) {
				return new UnitCommentContextMenu(((UnitCommentNodeWrapper) argument).getUnit(), node,
						  false);
			}
			
			return null;
		}

		private class UnitCommentContextMenu extends JPopupMenu implements ActionListener {
			private Unit u = null;
			private DefaultMutableTreeNode node = null;
			private UnitCommentNodeWrapper nodeWrapper = null;
			private JMenuItem addComment;
			private JMenuItem removeComment;
			private JMenuItem modifyComment;
			private JMenuItem removeAllComments;

			/**
			 * Creates a new CommentContextMenu object.
			 *
			 * @param u TODO: DOCUMENT ME!
			 * @param node TODO: DOCUMENT ME!
			 * @param removeAll TODO: DOCUMENT ME!
			 */
			public UnitCommentContextMenu(Unit u, DefaultMutableTreeNode node,
									  boolean removeAll) {
				
				
				this.u = u;
				this.node= node;
				Object o = null;
				if (node!=null){
					o = node.getUserObject();
					if (o instanceof UnitCommentNodeWrapper){
						this.nodeWrapper = (UnitCommentNodeWrapper) o;
					}
				}
				
				addComment = new JMenuItem(getString("menu.createcomment"));
				addComment.addActionListener(this);
				this.add(addComment);

				if(node != null && o != null) {
					if (o instanceof UnitCommentNodeWrapper){
						modifyComment = new JMenuItem(getString("menu.changecomment"));
						modifyComment.addActionListener(this);
						removeComment = new JMenuItem(getString("menu.removecomment"));
						removeComment.addActionListener(this);

						this.add(modifyComment);
						this.add(removeComment);
					}
				}

				if(removeAll) {
					removeAllComments = new JMenuItem(getString("menu.removecomment.all"));
					removeAllComments.addActionListener(this);
					this.add(removeAllComments);
				}
			}

			private void addComment() {
				DefaultMutableTreeNode parent = null;

				for(Enumeration en = rootNode.children(); en.hasMoreElements();) {
					DefaultMutableTreeNode n = (DefaultMutableTreeNode) en.nextElement();
					Object obj = n.getUserObject();

					if((obj != null) && obj instanceof String &&
						   obj.equals(getString("node.comments"))) {
						parent = n;

						break;
					}
				}

				if(parent == null) {
					parent = new UnitCommentListNode(u, getString("node.comments"));
					rootNode.add(parent);
				}

				if(u.comments == null) {
					u.comments = CollectionFactory.createLinkedList();
				}

				if(u.comments.size() != parent.getChildCount()) {
					log.info("EMapDetailsPanel.DetailsContextMenu.getCreateCommentMenuItem(): number of comments and nodes differs!");

					return;
				}

				String newComment = u.toString();
				u.comments.add(newComment);

				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new UnitCommentNodeWrapper(u, newComment));
				parent.add(newNode);
				treeModel.reload();
				restoreExpansionState();
				tree.startEditingAtPath(new TreePath(newNode.getPath()));
				storeExpansionState();
			}

			private void modifyComment() {
				if((nodeWrapper.getUnit() != null) && (nodeWrapper.getUnit().comments != null)) {
					tree.startEditingAtPath(new TreePath(node.getPath()));
				}
			}

			private void removeComment() {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

				if(u.comments != null) {
					if(u.comments.size() == parent.getChildCount()) {
						u.comments.remove(parent.getIndex(node));
					} else {
						log.info("EMapDetailsPanel.DetailsUnitContextMenu.getDeleteCommentMenuItem(): number of comments and nodes differs!");
						return;
					}
				}

				treeModel.removeNodeFromParent(node);

				if(parent.getChildCount() == 0) {
					treeModel.removeNodeFromParent(parent);
				}

				treeModel.reload();
				restoreExpansionState();
			}

			private void removeAllComments() {
				u.comments.clear();
				u.comments = null;
				EMapDetailsPanel.this.show(u, false);
			}

			/**
			 * Invoked when an action occurs.
			 *
			 * @param e TODO: DOCUMENT ME!
			 */
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == addComment) {
					addComment();
				} else if(e.getSource() == removeComment) {
					removeComment();
				} else if(e.getSource() == modifyComment) {
					modifyComment();
				} else if(e.getSource() == removeAllComments) {
					removeAllComments();
				}
			}
		}
	}
	
	
	
	/**
	 * We need a special factory for the normal UnitContextMenu since we have to take care of unit
	 * lists.
	 */
	private class DetailsUnitContextFactory implements ContextFactory {
		
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param data TODO: DOCUMENT ME!
		 * @param argument TODO: DOCUMENT ME!
		 * @param selectedObjects TODO: DOCUMENT ME!
		 * @param node TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public JPopupMenu createContextMenu(EventDispatcher dispatcher, 
                                GameData data, Object argument,
								Collection selectedObjects,
                                DefaultMutableTreeNode node) {
			
			
			if(argument instanceof Unit) {
				return new UnitContextMenu((Unit) argument, selectedObjects, dispatcher, data);
			} else if(argument instanceof UnitNodeWrapper) {
				return new UnitContextMenu(((UnitNodeWrapper) argument).getUnit(), selectedObjects,
										   dispatcher, data);
			} else if(argument instanceof UnitListNodeWrapper) {
				Collection col = ((UnitListNodeWrapper) argument).getUnits();

				if((col != null) && (col.size() > 0)) {
					return new UnitContextMenu((Unit) col.iterator().next(), col, dispatcher, data);
				}
			} else if(argument instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode actArg = (DefaultMutableTreeNode)argument;
				Object actUserObject = actArg.getUserObject();
				if (actUserObject  instanceof SimpleNodeWrapper) {
					SimpleNodeWrapper actSNW = (SimpleNodeWrapper) actUserObject;
					if (actSNW.toString().toLowerCase().startsWith(getString("node.capacityonfoot").toLowerCase())
							|| actSNW.toString().toLowerCase().startsWith(getString("node.capacityonhorse").toLowerCase()
							)){
						// OK, this is right klick in Capacity...
						
						return new UnitCapacityContextMenu(dispatcher, data, settings);
					}
				}
			} else {
				
			}

			return null;
		}
	}

	private class RaceInfo {
		int amount = 0;
		int amount_modified = 0;
		String raceNoPrefix = null;
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
			defaultTranslations.put("node.herbs", "Herbs");
			defaultTranslations.put("node.peasants", "Peasants");
			defaultTranslations.put("node.recruit", "Recruit");
			defaultTranslations.put("node.silver", "Silver");
			defaultTranslations.put("node.surplus", "Surplus");
			defaultTranslations.put("node.wage", "Wage");
			defaultTranslations.put("node.enterainment", "Entertainment");
			defaultTranslations.put("node.trade", "Trade");
			defaultTranslations.put("node.guarded", "Guarded");
			defaultTranslations.put("node.race", "Race");
			defaultTranslations.put("node.combatstatus", "Combat status");
			defaultTranslations.put("node.health", "Health");
			defaultTranslations.put("node.aura", "Aura");
			defaultTranslations.put("node.permanenteaura", "permanent Aura");
			defaultTranslations.put("node.capacityonfoot", "Capacity on foot");
			defaultTranslations.put("node.capacityonhorse", "Capacity on horse");
			defaultTranslations.put("node.type", "Type");
			defaultTranslations.put("node.faction", "Faction");
			defaultTranslations.put("node.completion", "Completion");
			defaultTranslations.put("node.shore", "Shore");
			defaultTranslations.put("shortcuts.description.2", "Change \"Limit make completion\"");
			defaultTranslations.put("shortcuts.description.1", "Request focus for orders");
			defaultTranslations.put("shortcuts.description.0", "Request focus for orders");
			defaultTranslations.put("shortcuts.title", "Details");
			defaultTranslations.put("menu.removecomment.all", "Remove all comments");
			defaultTranslations.put("node.range", "Range");
			defaultTranslations.put("node.damage", "Damage");
			defaultTranslations.put("node.sailingskill", "Sailing skill");
			defaultTranslations.put("node.crew", "Crew");
			defaultTranslations.put("node.captainskill", "Captain-skill");
			defaultTranslations.put("node.crewskill", "Crew-skill");
			defaultTranslations.put("node.direction", "Direction");
			defaultTranslations.put("node.level", "Level");
			defaultTranslations.put("node.rank", "Rank");
			defaultTranslations.put("node.persons", "Persons");
			defaultTranslations.put("node.items", "Items");
			defaultTranslations.put("node.of", "of ");
			defaultTranslations.put("node.skills", "Skills");
			defaultTranslations.put("node.group", "Group");
			defaultTranslations.put("node.hero", "Hero");
			defaultTranslations.put("node.starved", "Unit is starving");
			defaultTranslations.put("node.guards", "Guards");
			defaultTranslations.put("node.stealth", "Stealth");
			defaultTranslations.put("node.disguised", "Disguised");
			defaultTranslations.put("node.disguisedas", "Disguised as faction");
			defaultTranslations.put("node.spy", "This is another faction's spy!");
			defaultTranslations.put("node.command", "Command");
			defaultTranslations.put("node.totalweight", "Total weight");
			defaultTranslations.put("node.weightunits", "lbs");
			defaultTranslations.put("node.load", "Load");
			defaultTranslations.put("node.toomanyhorses", "too many horses");
			defaultTranslations.put("node.overloadedby", "Overloaded by ");
			defaultTranslations.put("node.teacher", "Teacher");
			defaultTranslations.put("node.pupils", "Pupils");
			defaultTranslations.put("node.attacks", "Victims");
			defaultTranslations.put("node.attackedBy", "Attackers");
			defaultTranslations.put("node.carriers", "Carriers");
			defaultTranslations.put("node.passengers", "Passengers");
			defaultTranslations.put("node.spells", "Spells");
			defaultTranslations.put("node.potions", "Potions");
			defaultTranslations.put("node.combatspells", "Combat spells");
			defaultTranslations.put("node.owner", "Owner");
			defaultTranslations.put("node.unknownfaction", "Unknown faction");
			defaultTranslations.put("node.inmates", "Inmates");
			defaultTranslations.put("node.size", "Size");
			defaultTranslations.put("node.upkeep", "Upkeep");
			defaultTranslations.put("node.buildingcost", "Building costs");
			defaultTranslations.put("node.captain", "Captain");
			defaultTranslations.put("node.spell.ship", "Ship spells");
			defaultTranslations.put("node.spell.far", "Distance spells");
			defaultTranslations.put("node.components", "Components");
			defaultTranslations.put("node.ingredients", "Ingredients");
			defaultTranslations.put("node.comments", "Comments");
			defaultTranslations.put("node.alliances", "Alliances");
			defaultTranslations.put("shipplaner.text", "Ship route scheduler");
			defaultTranslations.put("prefs.showTagButtons", "Show Tag Buttons");
			defaultTranslations.put("prefs.allowCustomIcons", "Allow Custom Icons");
			defaultTranslations.put("addtag.tagvalue.message", "Please enter tag value");
			defaultTranslations.put("addtag.tagname.message", "Please enter tag name");
			defaultTranslations.put("addtag.caption", "Add tag");
			defaultTranslations.put("removetag.caption", "Remove tag");
			defaultTranslations.put("node.tags", "Tags");
			defaultTranslations.put("node.terrain", "Terrain");
			defaultTranslations.put("node.terrains", "Terrains");
			defaultTranslations.put("node.schemes", "Schemes");

			defaultTranslations.put("menu.createcomment", "Create comment");
			defaultTranslations.put("menu.changecomment", "Change comment");
			defaultTranslations.put("menu.removecomment", "Remove comment");
			defaultTranslations.put("menu.copyid.caption", "Copy ID");
			defaultTranslations.put("menu.copyidandname.caption", "Copy ID and name");
			defaultTranslations.put("menu.confirm.caption", "Confirm");
			defaultTranslations.put("menu.addorder.caption", "Add order");
			defaultTranslations.put("menu.units", "Units");

			defaultTranslations.put("prefs.title", "Details");
			defaultTranslations.put("prefs.showNextSkillLevelPoints",
									"Show the number of points required to reach the next skill level with units");
			defaultTranslations.put("prefs.showNextSkillLevelLearnTurns",
									"Show the number of turns to learn in order to reach the next skill level");
			defaultTranslations.put("prefs.showRegionItemAmount", "Show a total with unit items");
			defaultTranslations.put("prefs.datadisplay", "Data display");
			defaultTranslations.put("prefs.showskillicons", "Show skill icons with units");
			defaultTranslations.put("prefs.showcontainericons",
									"Show building and ship icons with units");
			defaultTranslations.put("wrapperfactory.title", "Detail Entries");

			defaultTranslations.put("backbutton.text", "Back");
			defaultTranslations.put("node.coordinates", "Coordinates");
			defaultTranslations.put("node.horses", "Horses");
			defaultTranslations.put("node.resources", "Resources");
			defaultTranslations.put("node.FamiliarParents", "familiar mage:");
			defaultTranslations.put("node.FamiliarChilds", "familiar with:");

			defaultTranslations.put("menu.caption", "Details");
			defaultTranslations.put("menu.mnemonic", "D");
			defaultTranslations.put("menu.supertitle", "Tree");
			
			defaultTranslations.put("spell.noinfo", "no information available (no data in CR)");
			defaultTranslations.put("node.buildingminskilllevel", "needed skill level");
			defaultTranslations.put("node.buildingcastlesizelimits", "size-range of this castletype");
			defaultTranslations.put("node.resources_region", "Resources (Region)");
			
		}

		return defaultTranslations;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JMenu getMenu() {
		JMenu tree = new JMenu(getString("menu.caption"));
		tree.setMnemonic(getString("menu.mnemonic").charAt(0));
		tree.add(nodeWrapperFactory.getContextMenu());

		return tree;
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
	
	/**
	 * outsourced handling of changes in tree-selections
	 * updating mySelectedUnits
	 * @author Fiete
	 * @param tslE the TreeSelectionEvent from The Listener of our Tree
	 */
	private void handleTreeSelectionChangeEvent(TreeSelectionEvent tslE){
		this.mySelectedUnits.clear();
		TreePath paths[] = tree.getSelectionPaths();
		if (paths != null) {
			for(int i = 0; i < paths.length; i++) {
				DefaultMutableTreeNode actNode = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
				Object o = actNode.getUserObject();
				if(o instanceof UnitNodeWrapper) {
					UnitNodeWrapper unitNodeWrapper = (UnitNodeWrapper)o;
					Unit actUnit = unitNodeWrapper.getUnit();
					this.mySelectedUnits.add(actUnit);
				}
			}
		}
		if (this.mySelectedUnits.size()>0) {
			contextManager.setSelection(this.mySelectedUnits);
		} else {
			contextManager.setSelection(null);
		}
	}
	
}
