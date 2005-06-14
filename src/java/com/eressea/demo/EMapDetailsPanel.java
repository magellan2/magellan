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
import com.eressea.Named;
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
import com.eressea.relation.PersonTransferRelation;
import com.eressea.relation.TeachRelation;
import com.eressea.relation.UnitRelation;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.SkillCategory;
import com.eressea.rules.SkillType;
import com.eressea.rules.UnitContainerType;
import com.eressea.swing.BasicRegionPanel;
import com.eressea.swing.InternationalizedDataPanel;
import com.eressea.swing.MenuProvider;
import com.eressea.swing.completion.MultiEditorOrderEditorList;
import com.eressea.swing.context.ContextFactory;
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
import com.eressea.swing.tree.UnitContainerNodeWrapper;
import com.eressea.swing.tree.UnitListNodeWrapper;
import com.eressea.swing.tree.UnitNodeWrapper;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Direction;
import com.eressea.util.EresseaRaceConstants;
import com.eressea.util.EresseaSkillConstants;
import com.eressea.util.ShipRoutePlanner;
import com.eressea.util.Taggable;
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
	protected ContextManager contextManager;
	protected StealthContextFactory stealthContext;
	protected CombatStateContextFactory combatContext;
	protected CommentContextFactory commentContext;
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
							JOptionPane.showMessageDialog((Client) ((JComponent) e.getSource()).getTopLevelAncestor(),
														  getString("msg.cannotrename.text"),
														  getString("error"),
														  javax.swing.JOptionPane.WARNING_MESSAGE);
							tree.grabFocus();
						}
					} else if(displayedObject instanceof Island) {
						((Named) displayedObject).setName(name.getText());
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
							JOptionPane.showMessageDialog((Client) ((JComponent) e.getSource()).getTopLevelAncestor(),
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

					if((selectedNode != null) && selectedNode instanceof CommentNode) {
						CommentNode cn = (CommentNode) selectedNode;
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) cn.getParent();
						UnitContainer uc = cn.getUnitContainer();

						if((uc != null) && (uc.comments != null)) {
							uc.comments.set(parent.getIndex(cn),
											tree.getCellEditor().getCellEditorValue());
						}
					}
				}
			});

		contextManager = new ContextManager(tree,dispatcher);
		stealthContext = new StealthContextFactory();
		combatContext = new CombatStateContextFactory();
		commentContext = new CommentContextFactory();
		unitContext = new DetailsUnitContextFactory();
		contextManager.putSimpleObject(CommentListNode.class, commentContext);
		contextManager.putSimpleObject(CommentNode.class, commentContext);
		contextManager.putSimpleObject(UnitNodeWrapper.class, unitContext);
		contextManager.putSimpleObject(UnitListNodeWrapper.class, unitContext);

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

		// split pane combining name, desc & tree
		topSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, nameDescPanel, pnlRegionInfoTree);
		topSplitPane.setOneTouchExpandable(true);
		editor = new MultiEditorOrderEditorList(dispatcher, data, settings, _undoMgr);

		// build auto completion structure
		orders = new AutoCompletion(settings, dispatcher);
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
	 * shows a tree: Terrain  : region.type Coordinates  : region.coordinates guarding units (Orc
	 * Infestination) (resources) (peasants) (luxuries) (schemes) (comments) (tags)
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 * @param expandableNodes TODO: DOCUMENT ME!
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
	 * this function adds a node with subnodes to given parent - Luxuries: amount luxury item 1:
	 * price 1 ... luxury item n: price n
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 * @param expandableNodes TODO: DOCUMENT ME!
	 */
	private void appendRegionLuxuriesInfo(Region r, DefaultMutableTreeNode parent,
										  Collection expandableNodes) {
		if((r == null) || (r.prices == null) || r.prices.values().isEmpty()) {
			return;
		}

		DefaultMutableTreeNode luxuriesNode = new DefaultMutableTreeNode(getString("node.trade") +
																		 ": " +
																		 getDiffString(r.maxLuxuries(),
																					   r.maxOldLuxuries()));
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
	 * this function adds a node with subnodes to given parent - Peasants: amount / max amount
	 * recruit: recruit amount of recruit amount silver: silver surplus: surplus wage: entertain:
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 * @param expandableNodes TODO: DOCUMENT ME!
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

		if(data.rules.getRace(EresseaRaceConstants.R_ORKS) != null) {
			nodeText.append(", ").append(data.rules.getRace(EresseaRaceConstants.R_ORKS).getName())
					.append(": ").append(getDiffString(getWage(wage, true), getWage(r.oldWage, true)));
		}

		peasantsNode.add(createSimpleNode(nodeText.toString(), "lohn"));

		// entertain
		peasantsNode.add(createSimpleNode(getString("node.enterainment") + ": " +
										  getDiffString(r.maxEntertain(), r.maxOldEntertain()),
										  "items/silber"));
	}

	private void appendRegionResourceInfo(Region r, DefaultMutableTreeNode parent,
										  Collection expandableNodes) {
		DefaultMutableTreeNode resourceNode = new DefaultMutableTreeNode(getString("node.resources"));

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
					treeNode = createSimpleNode(getString("node.mallorntrees") + ": " +
												getDiffString(r.trees, r.oldTrees),
												"items/" + mallornID);
				} else {
					treeNode = createSimpleNode(getString("node.trees") + ": " +
												getDiffString(r.trees, r.oldTrees),
												"items/" + treesID);
				}

				resourceNode.add(treeNode);
			}

			if((r.iron > 0) || (r.oldIron > 0)) {
				resourceNode.add(createSimpleNode(data.rules.getItemType(StringID.create("Eisen"))
															.getName() + ": " +
												  getDiffString(r.iron, r.oldIron), "items/Eisen"));
			}

			if((r.laen > 0) || (r.oldLaen > 0)) {
				resourceNode.add(createSimpleNode(getString("node.laen") + ": " +
												  getDiffString(r.laen, r.oldLaen),
												  "items/" + laenID));
			}
		}

		// horse
		if((r.horses > 0) || (r.oldHorses > 0)) {
			resourceNode.add(createSimpleNode(getString("node.horses") + ": " +
											  getDiffString(r.horses, r.oldHorses), "items/pferd"));
		}

		// herb
		if(r.herb != null) {
			StringBuffer sb = new StringBuffer(getString("node.herbs")).append(": ").append(r.herb.getName());

			if(r.herbAmount != null) {
				sb.append(" (").append(r.herbAmount).append(")");
			}

			String icon = null;

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

		if(res.getType().getID().equals(sproutsID)) {
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

	// region guards
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
			DefaultMutableTreeNode guardRoot = new DefaultMutableTreeNode(getString("node.guarded") +
																		  ": ");
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

		parent.add(createSimpleNode(sb.toString(), "items/" + icon));
	}

	/**
	 * Appends a folder with the schemes in the specified region to the specified parent node.
	 *
	 * @param region TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 * @param expandableNodes TODO: DOCUMENT ME!
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
	 * Appends a folder with the external tags to the given node.
	 *
	 * @param uc TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 * @param expandableNodes TODO: DOCUMENT ME!
	 */
	private void appendTags(UnitContainer uc, DefaultMutableTreeNode parent,
							Collection expandableNodes) {
		appendTags(uc, parent, expandableNodes, uc.getType().getID().toString() + "TagsExpanded");
	}

	/**
	 * Appends a folder with the external tags to the given node.
	 *
	 * @param uc TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 * @param expandableNodes TODO: DOCUMENT ME!
	 */
	private void appendTags(Unit uc, DefaultMutableTreeNode parent, Collection expandableNodes) {
		appendTags(uc, parent, expandableNodes, "UnitTagsExpanded");
	}

	private void appendTags(Taggable uc, DefaultMutableTreeNode parent, Collection expandableNodes,
							String nodeInfo) {
		if(!uc.hasTags()) {
			return;
		}

		DefaultMutableTreeNode tags = new DefaultMutableTreeNode(getString("node.tags"));

		for(Iterator iter = uc.getTagMap().keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = uc.getTag(name);
			tags.add(new DefaultMutableTreeNode(name + ": " + value));
		}

		parent.add(tags);
		expandableNodes.add(new NodeWrapper(tags, "EMapDetailsPanel." + nodeInfo));
	}

	private void showFaction(Faction f) {
		setNameAndDescription(f, isPrivileged(f));
		appendFactionsInfo(Collections.singleton(f), rootNode, myExpandableNodes);
	}

	private void appendFactionsInfo(Collection factions, DefaultMutableTreeNode parent,
									Collection expandableNodes) {
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

	private void appendUnitsInfo(Collection units, DefaultMutableTreeNode parent,
								 Collection expandableNodes) {
		Map skills = CollectionFactory.createHashtable();

		// key: racename (string), Value: Integer-Object containing number of persons of that race
		Map races = CollectionFactory.createHashtable();

		for(Iterator iter = units.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			Integer number = (Integer) races.get(u.getRaceName(data));
			int personCount = u.getPersons();

			if(number != null) {
				personCount += number.intValue();
			}

			races.put(u.getRaceName(data), new Integer(personCount));

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
			int i = ((Integer) races.get(race)).intValue();
			parent.add(createSimpleNode(i + " " + race, "person"));
		}

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
			DefaultMutableTreeNode skillsNode = new DefaultMutableTreeNode(getString("node.skills"));
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
				n = new DefaultMutableTreeNode(getString("node.skills"));
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
		DefaultMutableTreeNode fNode;

		if(u.getFaction() == null) {
			fNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.faction") +
																						  ": " +
																						  getString("node.unknownfaction"),
																						  "faction"));
		} else {
			fNode = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.faction") +
																						  ": " +
																						  u.getFaction()
																						   .toString(),
																						  "faction"));
		}

		parent.add(fNode);

		// Race
		if(u.race != null) {
			StringBuffer nodeText = new StringBuffer(getString("node.race")).append(": ").append(u.getRaceName(this.data));

			if(u.realRace != null) {
				nodeText.append(" (").append(u.realRace.getName()).append(")");
			}

			parent.add(createSimpleNode(nodeText.toString(), "rasse"));
		}

		// Group
		if(u.getGroup() != null) {
			Group group = u.getGroup();
			DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(getString("node.group") +
																		  ": \"" + group.getName() +
																		  "\"");
			parent.add(groupNode);
			expandableNodes.add(new NodeWrapper(groupNode, "EMapDetailsPanel.UnitGroupExpanded"));
			appendAlliances(group.allies(), groupNode);
		}

		// Kampfreihenanzeige
		SimpleNodeWrapper cWrapper = nodeWrapperFactory.createSimpleNodeWrapper(getString("node.combatstatus") +
																				": " +
																				Unit.combatStatusToString(u),
																				"kampfstatus");

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
			String verw = data.getTranslation("verwundet");
			String sverw = data.getTranslation("schwer verwundet");
			String ersch = data.getTranslation("erschöpft");
			String hicon = "gesundheit";

			if(u.health.equals(verw)) {
				hicon = "verwundet";
			} else if(u.health.equals(sverw)) {
				hicon = "schwerverwundet";
			} else if(u.health.equals(ersch)) {
				hicon = "erschoepft";
			}

			parent.add(createSimpleNode(getString("node.health") + ": " + u.health, hicon));
		}

		// guard state
		if(u.guard != 0) {
			parent.add(createSimpleNode(getString("node.guards") + ": " +
										Unit.guardFlagsToString(u.guard), "bewacht"));
		}

		// faction hidden, stealth level
		Skill stealth = u.getSkill(data.rules.getSkillType(EresseaSkillConstants.S_TARNUNG, true));
		int stealthLevel = 0;

		if(stealth != null) {
			stealthLevel = stealth.getLevel();
		}

		if(u.hideFaction || (stealthLevel > 0)) {
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

		// spy
		if(u.isSpy()) {
			parent.add(createSimpleNode(getString("node.spy"), "spion"));
		}

		// Gebaeude-/Schiffsanzeige
		if(u.getUnitContainer() != null) {
			parent.add(new DefaultMutableTreeNode(nodeWrapperFactory.createUnitContainerNodeWrapper(u.getUnitContainer())));
		}
		if(u.getModifiedUnitContainer() != null && !u.getModifiedUnitContainer().equals(u.getUnitContainer())) {
			parent.add(new DefaultMutableTreeNode(nodeWrapperFactory.createUnitContainerNodeWrapper(u.getModifiedUnitContainer())));
		}

		// magic aura
		if(u.aura != -1) {
			parent.add(createSimpleNode(getString("node.aura") + ": " + u.aura + " / " + u.auraMax,
										"aura"));
		}

		// weight
		Float uWeight = new Float(u.getWeight() / 100.0F);
		Float modUWeight = new Float(u.getModifiedWeight() / 100.0F);
		String text = getString("node.totalweight") + ": " + weightNumberFormat.format(uWeight);

		if(!uWeight.equals(modUWeight)) {
			text += (" (" + weightNumberFormat.format(modUWeight) + ")");
		}

		text += (" " + getString("node.weightunits"));
		parent.add(createSimpleNode(text, "gewicht"));

		// load
		int load = u.getLoad();
		int modLoad = u.getModifiedLoad();

		if((load != 0) || (modLoad != 0)) {
			text = getString("node.load") + ": " +
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
																									 "fuss"));
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
																									 "reiten"));
				appendUnitCapacityByItems(capacityNode, u, maxOnHorse - modLoad);

				if(capacityNode.getChildCount() > 0) {
					expandableNodes.add(new NodeWrapper(capacityNode,
														"EMapDetailsPanel.UnitCapacityOnHorseExpanded"));
				}
			}

			parent.add(capacityNode);
		}

		// items
		if(u.getModifiedItems().size() > 0) {
			DefaultMutableTreeNode itemsNode = new DefaultMutableTreeNode(getString("node.items"));
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
			DefaultMutableTreeNode skillsNode = new DefaultMutableTreeNode(getString("node.skills"));
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

		// teacher pupil relations
		Collection pupils = CollectionFactory.createLinkedList();
		Collection teachers = CollectionFactory.createLinkedList();

		for(Iterator iter = u.getRelations().iterator(); iter.hasNext();) {
			UnitRelation rel = (UnitRelation) iter.next();

			if(rel instanceof TeachRelation) {
				TeachRelation tr = (TeachRelation) rel;

				if(u.equals(tr.source)) {
					if(tr.target != null) {
						pupils.add(tr.target);
					}
				} else {
					teachers.add(tr.source);
				}
			}
		}

		if(teachers.size() > 0) {
			DefaultMutableTreeNode teachersNode = new DefaultMutableTreeNode("");
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
																1)), null, teachers));
		}

		if(pupils.size() > 0) {
			DefaultMutableTreeNode pupilsNode = new DefaultMutableTreeNode("");
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
			}

			pupilsNode.setUserObject(new UnitListNodeWrapper(getString("node.pupils") + ": " +
															 pupilCounter + " / " +
															 (u.getModifiedPersons() * 10), null,
															 pupils));
		}

		// transportation relations
		Map carriers = u.getCarriers();

		if(carriers.size() > 0) {
			DefaultMutableTreeNode carriersNode = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.carriers"),
																									 null,
																									 carriers.values()));
			parent.add(carriersNode);
			expandableNodes.add(new NodeWrapper(carriersNode,
												"EMapDetailsPanel.UnitCarriersExpanded"));

			for(Iterator iter = carriers.values().iterator(); iter.hasNext();) {
				Unit carrier = (Unit) iter.next();
				UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(carrier,
																			 carrier.getPersons(),
																			 carrier.getModifiedPersons());
				carriersNode.add(new DefaultMutableTreeNode(w));
			}
		}

		Map passengers = u.getPassengers();

		if(passengers.size() > 0) {
			DefaultMutableTreeNode passengersNode = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.passengers"),
																									   null,
																									   passengers.values()));
			parent.add(passengersNode);
			expandableNodes.add(new NodeWrapper(passengersNode,
												"EMapDetailsPanel.UnitPassengersExpanded"));

			for(Iterator iter = passengers.values().iterator(); iter.hasNext();) {
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

		// attack relations
		{
			Collection attacks = u.getAttackVictims();

			if(attacks.size() > 0) {
				DefaultMutableTreeNode attacksNode = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.attacks"),
																										null,
																										carriers.values()));
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
																										   carriers.values()));
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

		// magic spells
		if((u.spells != null) && (u.spells.size() > 0)) {
			DefaultMutableTreeNode spellsNode = new DefaultMutableTreeNode(getString("node.spells"));
			parent.add(spellsNode);
			expandableNodes.add(new NodeWrapper(spellsNode, "EMapDetailsPanel.UnitSpellsExpanded"));

			List sortedSpells = CollectionFactory.createLinkedList(u.spells.values());
			Collections.sort(sortedSpells, new SpellLevelComparator(new NameComparator(null)));

			for(Iterator iter = sortedSpells.iterator(); iter.hasNext();) {
				Spell spell = (Spell) iter.next();
				spellsNode.add(createSimpleNode(spell, "spell"));
			}
		}

		// Kampfzauber-Anzeige
		appendUnitCombatSpells(u.combatSpells, parent, expandableNodes);

		// Trank Anzeige
		if((data.potions() != null) && (u.skills != null) &&
			   u.skills.containsKey(EresseaSkillConstants.S_ALCHEMIE)) {
			DefaultMutableTreeNode potionsNode = new DefaultMutableTreeNode(getString("node.potions"));
			Skill alchSkill = (Skill) u.skills.get(EresseaSkillConstants.S_ALCHEMIE);
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

		if(isTrader) {
			appendRegionLuxuriesInfo(u.getRegion(), parent, expandableNodes);
		}

		appendTags(u, parent, expandableNodes);
	}

	private void appendUnitPersonInfo(Unit u, DefaultMutableTreeNode parent,
									  Collection expandableNodes) {
		String strPersons = getString("node.persons") + ": " + u.getPersons();

		if(u.getPersons() != u.getModifiedPersons()) {
			strPersons += (" (" + u.getModifiedPersons() + ")");
		}

		DefaultMutableTreeNode personNode = createSimpleNode(strPersons, "person");
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

	private void appendUnitCapacityByItems(DefaultMutableTreeNode parent, Unit u, int freeCapacity) {
		ItemType horses = data.rules.getItemType(StringID.create("Pferd"));
		ItemType carts = data.rules.getItemType(StringID.create("Wagen"));
		ItemType silver = data.rules.getItemType(StringID.create("Silber"));

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

	private void appendBuildingInfo(Building b, DefaultMutableTreeNode parent,
									Collection expandableNodes) {
		int inmates = 0; // the number of persons currently on the ship
		int modInmates = 0; // the number of persons on the ship in the next turn
		List units = null; // the list of units currently on the ship
		Collection modUnits = null; // the collection of units on the ship in the next turn

		/* make sure that all unit relations have been established
		 (necessary for enter-/leave relations) */
		if(b.getRegion() != null) {
			b.getRegion().refreshUnitRelations();
		}

		DefaultMutableTreeNode n = null;
		DefaultMutableTreeNode m = null;

		// Typ
		n = createSimpleNode(getString("node.type") + ": " + b.getType().getName(),
							 b.getType().getID().toString());
		parent.add(n);

		// Besitzer
		if(b.getOwnerUnit() != null) {
			UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(b.getOwnerUnit(),
																		 getString("node.owner") +
																		 ": ",
																		 b.getOwnerUnit()
																		  .getPersons(),
																		 b.getOwnerUnit()
																		  .getModifiedPersons());
			n = new DefaultMutableTreeNode(w);
			parent.add(n);

			if(b.getOwnerUnit().getFaction() == null) {
				n = createSimpleNode(getString("node.faction") + ": " +
									 getString("node.unknownfaction"), "faction");
			} else {
				n = createSimpleNode(getString("node.faction") + ": " +
									 b.getOwnerUnit().getFaction().toString(), "faction");
			}
		}

		parent.add(n);

		// Insassen
		units = CollectionFactory.createLinkedList(b.units()); // the collection of units currently in the building
		Collections.sort(units, sortIndexComparator);
		modUnits = b.modifiedUnits(); // the collection of units in the building in the next turn

		Collection allInmates = CollectionFactory.createLinkedList();
		allInmates.addAll(units);
		allInmates.addAll(modUnits);

		if((units.size() > 0) || (modUnits.size() > 0)) {
			n = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.inmates"), null,
																   allInmates));
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.BuildingInmatesExpanded"));

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
				n.setUserObject(getString("node.inmates") + ": " + inmates);
			} else {
				n.setUserObject(getString("node.inmates") + ": " + inmates + " (" + modInmates +
								")");
			}
		}

		n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(((inmates == modInmates)
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

		// Gebaeudeunterhalt
		Iterator iter = b.getBuildingType().getMaintenanceItems();

		if(iter.hasNext()) {
			n = new DefaultMutableTreeNode(getString("node.upkeep"));
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.BuildingMaintenanceExpanded"));
		}

		while(iter.hasNext()) {
			Item i = (Item) iter.next();
			String text = i.getName();

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

			n.add(m);
		}

		// Baukosten
		iter = b.getBuildingType().getRawMaterials();

		if(iter.hasNext()) {
			n = new DefaultMutableTreeNode(getString("node.buildingcost"));
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.BuildingCostExpanded"));
		}

		while(iter.hasNext()) {
			Item i = (Item) iter.next();
			String text = i.getName();
			m = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(i.getAmount() +
																					  " " + text,
																					  "items/" +
																					  i.getItemType()
																					   .getID()
																					   .toString()));
			n.add(m);
		}

		// Kommentare
		appendComments(b, parent, expandableNodes);
		appendTags(b, parent, expandableNodes);
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
		int load = 0;

		//int modLoad = 0;
		DefaultMutableTreeNode loadNode = null;
		DefaultMutableTreeNode n = null;
		DefaultMutableTreeNode m = null;
		int sailingSkillAmount = 0;
		List units = null; // the list of units currently on the ship
		Collection modUnits = null; // the collection of units on the ship in the next turn
		int inmates = 0; // the number of persons currently on the ship
		int modInmates = 0; // the number of persons on the ship in the next turn

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
			int ratio = 0;

			if(nominalShipSize != 0) {
				ratio = (s.size * 100) / nominalShipSize;
			}

			parent.add(new DefaultMutableTreeNode(getString("node.completion") + ": " + ratio +
												  "% (" + s.size + "/" + nominalShipSize + ")"));
		} else {
			// Kueste
			if(s.shoreId > -1) {
				parent.add(createSimpleNode(getString("node.shore") + ": " +
											Direction.toString(s.shoreId),
											"shore_" + String.valueOf(s.shoreId)));
			}

			// Reichweite (bei Schaden aufrunden)
			int rad = s.getShipType().getRange();

			// rad = rad*(100.0-damageRatio)/100.0
			rad = new BigDecimal(rad).multiply(new BigDecimal(100 - s.damageRatio))
									 .divide(new BigDecimal(100), BigDecimal.ROUND_UP).intValue();

			String rangeString = getString("node.range") + ": " + rad;

			if((s.getOwnerUnit() != null) && (s.getOwnerUnit().race != null) &&
				   s.getOwnerUnit().race.getID().equals(EresseaRaceConstants.R_MEERMENSCHEN)) {
				rangeString += (" + 1 (" + s.getOwnerUnit().race.getName() + ")");
			}

			parent.add(createSimpleNode(rangeString, "radius"));
			loadNode = new DefaultMutableTreeNode();
			parent.add(loadNode);

			if(s.damageRatio > 0) {
				int absolute = new BigDecimal(s.damageRatio * s.size).divide(new BigDecimal(100),
																			 BigDecimal.ROUND_UP)
																	 .intValue();
				parent.add(createSimpleNode(getString("node.damage") + ": " + s.damageRatio +
											"% / " + absolute, "damage"));
			}
		}

		// Besitzer
		SkillType sailingSkillType = data.rules.getSkillType(StringID.create("Segeln"), true);

		if(s.getOwnerUnit() != null) {
			UnitNodeWrapper w = nodeWrapperFactory.createUnitNodeWrapper(s.getOwnerUnit(),
																		 getString("node.captain") +
																		 ": ",
																		 s.getOwnerUnit()
																		  .getPersons(),
																		 s.getOwnerUnit()
																		  .getModifiedPersons());
			n = new DefaultMutableTreeNode(w);
			parent.add(n);

			Faction fac = s.getOwnerUnit().getFaction();

			if(fac == null) {
				n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.faction") +
																						  ": " +
																						  getString("node.unknownfaction"),
																						  "faction"));
			} else {
				n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.faction") +
																						  ": " +
																						  fac.toString(),
																						  "faction"));
			}

			parent.add(n);

			Skill sailingSkill = s.getOwnerUnit().getModifiedSkill(sailingSkillType);
			sailingSkillAmount = (sailingSkill == null) ? 0 : sailingSkill.getLevel();
			n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.captainskill") +
																					  ": " +
																					  sailingSkillAmount +
																					  " / " +
																					  s.getShipType()
																					   .getCaptainSkillLevel(),
																					  "captain"));
			parent.add(n);
		}

		// Matrosen
		sailingSkillAmount = 0;

		// pavkovic 2003.10.03: use modifiedUnits to reflect FUTURE value?
		modUnits = s.modifiedUnits(); // the collection of units on the ship in the next turn

		for(Iterator sailors = modUnits.iterator(); sailors.hasNext();) {
			Unit u = (Unit) sailors.next();
			Skill sailingSkill = u.getModifiedSkill(sailingSkillType);

			if(sailingSkill != null) {
				sailingSkillAmount += (sailingSkill.getLevel() * u.getModifiedPersons());
			}
		}

		n = new DefaultMutableTreeNode(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.crewskill") +
																				  ": " +
																				  sailingSkillAmount +
																				  " / " +
																				  s.getShipType()
																				   .getSailorSkillLevel(),
																				  "crew"));
		parent.add(n);

		// Insassen
		units = CollectionFactory.createLinkedList(s.units()); // the collection of units currently on the ship
		Collections.sort(units, sortIndexComparator);

		Collection allInmates = CollectionFactory.createLinkedList();
		allInmates.addAll(units);
		allInmates.addAll(modUnits);

		if((units.size() > 0) || (modUnits.size() > 0)) {
			n = new DefaultMutableTreeNode(new UnitListNodeWrapper(getString("node.inmates"), null,
																   allInmates));
			parent.add(n);
			expandableNodes.add(new NodeWrapper(n, "EMapDetailsPanel.ShipInmatesExpanded"));

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
				n.setUserObject(getString("node.inmates") + ": " + inmates);
			} else {
				n.setUserObject(getString("node.inmates") + ": " + inmates + " (" + modInmates +
								")");
			}

			if(ShipRoutePlanner.canPlan(s)) { // add a link to the ship route planer
				parent.add(new DefaultMutableTreeNode(new ShipRoutingPlanerButton(s)));
			}
		}

		// Ladung
		if(loadNode != null) {
			String strLoad = (s.load != -1) ? Integer.toString(s.load)
											: weightNumberFormat.format(new Float(load / 100.0F));

			String strModLoad = weightNumberFormat.format(new Float(s.getModifiedLoad() / 100.0F));

			String strCap = Integer.toString(s.getMaxCapacity());

			if(log.isDebugEnabled()) {
				log.debug("outer ship state: " + s.getShipType().getCapacity() + "(strCap " +
						  strCap + ")");
			}

			loadNode.setUserObject(nodeWrapperFactory.createSimpleNodeWrapper(getString("node.load") +
																			  ": " + strLoad +
																			  ((!strModLoad.equals(strLoad))
																			   ? (" (" +
																			   strModLoad + ") ")
																			   : " ") + "/ " +
																			  strCap + " " +
																			  getString("node.weightunits"),
																			  "beladung"));
		}

		// Kommentare
		appendComments(s, parent, expandableNodes);
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
		parent.add(new DefaultMutableTreeNode(getString("node.type") + ": " + s.getTypeName()));
		parent.add(new DefaultMutableTreeNode(getString("node.level") + ": " + s.getLevel()));
		parent.add(new DefaultMutableTreeNode(getString("node.rank") + ": " + s.getRank()));

		if(s.getOnShip()) {
			parent.add(new DefaultMutableTreeNode(getString("node.spell.ship")));
		}

		if(s.getIsFar()) {
			parent.add(new DefaultMutableTreeNode(getString("node.spell.far")));
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
				} else {
					compNode = createSimpleNode(key + ": " + val, "items/" + key);
				}

				componentsNode.add(compNode);
			}
		}

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
		if((uc.comments != null) && (uc.comments.size() > 0)) {
			CommentListNode commentNode = new CommentListNode(uc, getString("node.comments"));
			parent.add(commentNode);
			expandableNodes.add(new NodeWrapper(commentNode, "EMapDetailsPanel.CommentsExpanded"));

			int i = 0;

			for(Iterator iter = uc.comments.iterator(); iter.hasNext() && (i < uc.comments.size());
					i++) {
				commentNode.add(new CommentNode(uc, (String) iter.next()));
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
						String name = ((String) o2).substring(0, index);
						log.info("Removing " + name);

						if(displayedObject instanceof UnitContainer) {
							((UnitContainer) displayedObject).removeTag(name);
							show(displayedObject, false);
						} else if(displayedObject instanceof Unit) {
							Unit u = (Unit) displayedObject;
							u.removeTag(name);
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
		javax.swing.plaf.TreeUI ui = tree.getUI();

		if(ui instanceof javax.swing.plaf.basic.BasicTreeUI) {
			javax.swing.plaf.basic.BasicTreeUI ui2 = (javax.swing.plaf.basic.BasicTreeUI) ui;
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

			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
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

		// preferences adapter code:
		public void applyPreferences() {
			source.setShowTagButtons(chkShowTagButtons.isSelected());
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

				Skill stealth = unit.getSkill(data.rules.getSkillType(EresseaSkillConstants.S_TARNUNG,
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
			}

			return null;
		}

		private class CommentContextMenu extends JPopupMenu implements ActionListener {
			private UnitContainer uc = null;
			private CommentNode node = null;
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

				if(node instanceof CommentNode) {
					this.node = (CommentNode) node;
				}

				addComment = new JMenuItem(getString("menu.createcomment"));
				addComment.addActionListener(this);
				this.add(addComment);

				if(this.node != null) {
					if(!(node instanceof CommentListNode)) {
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

				DefaultMutableTreeNode newNode = new CommentNode(uc, newComment);
				parent.add(newNode);
				treeModel.reload();
				restoreExpansionState();
				tree.startEditingAtPath(new TreePath(newNode.getPath()));
				storeExpansionState();
			}

			private void modifyComment() {
				if((node.getUnitContainer() != null) && (node.getUnitContainer().comments != null)) {
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
			}

			return null;
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
			defaultTranslations.put("node.Hero", "Hero");
			defaultTranslations.put("node.starved", "Unit is starving");
			defaultTranslations.put("node.guards", "Guards");
			defaultTranslations.put("node.stealth", "Stealth");
			defaultTranslations.put("node.disguised", "Disguised");
			defaultTranslations.put("node.disguisedas", "Disguised as faction ");
			defaultTranslations.put("node.spy", "This is another faction's spy!");
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

			defaultTranslations.put("menu.caption", "Details");
			defaultTranslations.put("menu.mnemonic", "D");
			defaultTranslations.put("menu.supertitle", "Tree");
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
}
