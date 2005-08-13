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

package com.eressea.swing;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.eressea.Battle;
import com.eressea.Building;
import com.eressea.Coordinate;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.IntegerID;
import com.eressea.Island;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.UnitID;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.swing.tree.CellRenderer;
import com.eressea.swing.tree.CopyTree;
import com.eressea.swing.tree.LineWrapCellRenderer;
import com.eressea.swing.tree.MixedTreeCellRenderer;
import com.eressea.swing.tree.NodeWrapperFactory;
import com.eressea.swing.tree.RegionNodeWrapper;
import com.eressea.swing.tree.UnitNodeWrapper;
import com.eressea.util.CollectionFactory;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.MessageTypeComparator;

/**
 * A class for displaying Eressea messages for regions or factions.
 */
public class MessagePanel extends InternationalizedDataPanel implements SelectionListener,
																		PreferencesFactory,
																		MenuProvider
{
	// tree elements
	private CopyTree tree = null;
	private DefaultTreeModel treeModel = null;
	private DefaultMutableTreeNode rootNode = null;
	protected NodeWrapperFactory nodeFactory;
	protected LineWrapCellRenderer lineRenderer;

	/**
	 * Creates a new <tt>MessagePanel</tt> object.
	 *
	 * @param d the central event dispatcher.
	 * @param gd the game data this message panel initiates with.
	 * @param p settings
	 */
	public MessagePanel(EventDispatcher d, GameData gd, Properties p) {
		super(d, gd, p);

		nodeFactory = new NodeWrapperFactory(p, "MessagePanel.Nodes", getString("nodeFactory.title"));

		// create dummies to have a valid pref adapter
		nodeFactory.createUnitNodeWrapper(null, null);
		nodeFactory.createRegionNodeWrapper(null);

		d.addSelectionListener(this);

		initTree();
	}

	/**
	 * Handles changes on game data.
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		data = e.getGameData();
		rootNode.removeAllChildren();
		treeModel.reload();
	}

	/**
	 * Handles the selection of a new object to display messages for.
	 *
	 * @param se TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent se) {
		Object activeObject = se.getActiveObject();

		if(se.getSource() == this) {
			return;
		}

		// clear tree nodes
		rootNode.removeAllChildren();

		if(activeObject instanceof Island) {
			show((Island) activeObject, rootNode);
		} else if(activeObject instanceof Region) {
			show((Region) activeObject, rootNode);
		} else if(activeObject instanceof Faction) {
			show((Faction) activeObject, rootNode);
		} else if(activeObject instanceof Building || activeObject instanceof Ship) {
			show((UnitContainer) activeObject, rootNode);
		} else if(activeObject instanceof Unit) {
			show((Unit) activeObject, rootNode);
		}

		treeModel.reload();

		Enumeration enum = rootNode.children();

		while(enum.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) enum.nextElement();

			if(node.getChildCount() > 0) {
				DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getFirstChild();
				tree.makeVisible(new TreePath(firstChild.getPath()));
			}
		}
	}

	/**
	 * Shows the messages for the specified island.
	 *
	 * @param i TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	public void show(Island i, DefaultMutableTreeNode parent) {
		if(data.factions() == null) {
			return;
		}

		// collect messages
		List sortedMessages = CollectionFactory.createLinkedList();

		for(Iterator fac = data.factions().values().iterator(); fac.hasNext();) {
			Faction f = (Faction) fac.next();

			if(f.messages != null) {
				Iterator msgs = f.messages.iterator();

				while(msgs.hasNext()) {
					Message msg = (Message) msgs.next();

					if(msg.attributes != null) {
						Iterator iter = msg.attributes.values().iterator();

						while(iter.hasNext()) {
							String attribute = (String) iter.next();
							Coordinate c = Coordinate.parse(attribute, ",");

							if(c == null) {
								c = Coordinate.parse(attribute, " ");
							}

							if((c != null) && (i.getRegion(c) != null)) {
								sortedMessages.add(msg);
							}
						}
					}
				}
			}
		}

		if(!sortedMessages.isEmpty()) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(getString("node.factionmessages"));
			parent.add(node);
			addCategorizedMessages(sortedMessages, node);
		}
	}

	/**
	 * Shows the messages for the specified region.
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void show(Region r, DefaultMutableTreeNode parent) {
		if(r == null) {
			return;
		}

		DefaultMutableTreeNode node = null;

		// for all categories of messages for a region, create
		// a node for the category and add the messages as sub-
		// nodes if the category is not empty
		if((r.messages != null) && (r.messages.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.messages"));
			parent.add(node);

			Iterator iter = r.messages.iterator();

			while(iter.hasNext() == true) {
				show((Message) iter.next(), node);
			}
		}

		if((r.effects != null) && (r.effects.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.effects"));
			parent.add(node);

			Iterator iter = r.effects.iterator();

			while(iter.hasNext() == true) {
				show((String) iter.next(), node);
			}
		}

		if((r.playerMessages != null) && (r.playerMessages.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.dispatches"));
			parent.add(node);

			Iterator iter = r.playerMessages.iterator();

			while(iter.hasNext() == true) {
				node.add(new DefaultMutableTreeNode(((Message) iter.next()).getText()));
			}
		}

		if((r.events != null) && (r.events.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.events"));
			parent.add(node);

			Iterator iter = r.events.iterator();

			while(iter.hasNext() == true) {
				node.add(new DefaultMutableTreeNode(((Message) iter.next()).getText()));
			}
		}

		// removed because seemingly useless and overriden by UnitContainer.comments

		/*if (r.comments != null && r.comments.size() > 0) {
		    node = new DefaultMutableTreeNode("Kommentare");
		    parent.add(node);
		    Iterator iter = r.comments.iterator();
		    while (iter.hasNext() == true) {
		        node.add(new DefaultMutableTreeNode(((Message)iter.next()).getText()));
		    }
		}*/
		if((r.surroundings != null) && (r.surroundings.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.surroundings"));
			parent.add(node);

			Iterator iter = r.surroundings.iterator();

			while(iter.hasNext() == true) {
				node.add(new DefaultMutableTreeNode(((Message) iter.next()).getText()));
			}
		}

		if(((r.travelThru != null) && (r.travelThru.size() > 0)) ||
			   ((r.travelThruShips != null) && (r.travelThruShips.size() > 0))) {
			node = new DefaultMutableTreeNode(getString("node.travelthru"));
			parent.add(node);

			if(r.travelThru != null) {
				Iterator iter = r.travelThru.iterator();

				while(iter.hasNext() == true) {
					node.add(new DefaultMutableTreeNode(((Message) iter.next()).getText()));
				}
			}

			if(r.travelThruShips != null) {
				Iterator iter = r.travelThruShips.iterator();

				while(iter.hasNext() == true) {
					node.add(new DefaultMutableTreeNode(((Message) iter.next()).getText()));
				}
			}
		}

		if(data.factions() != null) {
			List sortedMessages = CollectionFactory.createLinkedList();

			for(Iterator fac = data.factions().values().iterator(); fac.hasNext();) {
				Faction f = (Faction) fac.next();

				if(f.messages != null) {
					Iterator msgs = f.messages.iterator();

					while(msgs.hasNext()) {
						Message msg = (Message) msgs.next();

						if(msg.attributes != null) {
							Iterator iter = msg.attributes.values().iterator();

							while(iter.hasNext()) {
								String attribute = (String) iter.next();
								Coordinate c = Coordinate.parse(attribute, ",");

								if(c == null) {
									c = Coordinate.parse(attribute, " ");
								}

								if((c != null) && r.getID().equals(c)) {
									sortedMessages.add(msg);
								}
							}
						}
					}
				}
			}

			if(!sortedMessages.isEmpty()) {
				node = new DefaultMutableTreeNode(getString("node.factionmessages"));
				parent.add(node);
				addCategorizedMessages(sortedMessages, node);
			}
		}
	}

	/**
	 * Shows the messages for the specified Faction.
	 *
	 * @param f TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void show(Faction f, DefaultMutableTreeNode parent) {
		if(f == null) {
			return;
		}

		DefaultMutableTreeNode node = null;

		// for all categories of messages for a faction, create
		// a node for the category and add the messages as sub-
		// nodes if the category is not empty
		if((f.errors != null) && (f.errors.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.errors"));
			parent.add(node);

			Iterator msgs = f.errors.iterator();

			while(msgs.hasNext() == true) {
				show((String) msgs.next(), node);
			}
		}

		if((f.battles != null) && (f.battles.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.battles"));
			parent.add(node);

			Iterator battles = f.battles.iterator();

			while(battles.hasNext() == true) {
				show((Battle) battles.next(), node);
			}
		}

		if((f.messages != null) && (f.messages.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.messages"));
			parent.add(node);
			addCategorizedMessages(f.messages, node);
		}
	}

	/**
	 * Show a unit's messages.
	 *
	 * @param u TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void show(Unit u, DefaultMutableTreeNode parent) {
		if(u == null) {
			return;
		}

		DefaultMutableTreeNode node = null;

		if((u.effects != null) && (u.effects.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.effects"));
			parent.add(node);

			Iterator iter = u.effects.iterator();

			while(iter.hasNext() == true) {
				show((String) iter.next(), node);
			}
		}

		if((u.unitMessages != null) && (u.unitMessages.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.unitdispatches"));
			parent.add(node);

			Iterator iter = u.unitMessages.iterator();

			while(iter.hasNext() == true) {
				node.add(new DefaultMutableTreeNode(((Message) iter.next()).getText()));
			}
		}

		Faction f = u.getFaction();

		// FIXME ip 21.01.2002: sometimes it occurs that f is null. I don't know why
		if((f != null) && (f.messages != null)) {
			Iterator msgs = f.messages.iterator();

			while(msgs.hasNext()) {
				Message msg = (Message) msgs.next();

				if(msg.attributes != null) {
					Iterator iter = msg.attributes.values().iterator();

					while(iter.hasNext()) {
						try {
							int i = Integer.parseInt((String) iter.next());

							/* it would be cleaner to compare UnitID
							   objects here but that's too expensive */
							if(((UnitID) u.getID()).intValue() == i) {
								node = new DefaultMutableTreeNode(msg.getText());
								parent.add(node);
							}
						} catch(NumberFormatException e) {
						}
					}
				}
			}
		}
	}

	/**
	 * Show the messages of a battle.
	 *
	 * @param b TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void show(Battle b, DefaultMutableTreeNode parent) {
		if(b == null) {
			return;
		}

		DefaultMutableTreeNode node = null;
		DefaultMutableTreeNode subNode = null;

		Coordinate c = (Coordinate) b.getID();
		Region r = data.getRegion(c);

		if(r != null) {
			node = new DefaultMutableTreeNode(nodeFactory.createRegionNodeWrapper(r));
		} else {
			node = new DefaultMutableTreeNode(c.toString());
		}

		parent.add(node);

		Iterator msgs = b.messages().iterator();

		while(msgs.hasNext()) {
			Message msg = (Message) msgs.next();
			subNode = new DefaultMutableTreeNode(msg.getText());
			node.add(subNode);
		}
	}

	/**
	 * Show the messages related to the specified Building or Ship.
	 *
	 * @param uc TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void show(UnitContainer uc, DefaultMutableTreeNode parent) {
		if(uc == null) {
			return;
		}

		DefaultMutableTreeNode node = null;
		List sortedMessages = CollectionFactory.createLinkedList();

		if((uc.effects != null) && (uc.effects.size() > 0)) {
			node = new DefaultMutableTreeNode(getString("node.effects"));
			parent.add(node);

			Iterator iter = uc.effects.iterator();

			while(iter.hasNext() == true) {
				show((String) iter.next(), node);
			}
		}

		if(data.factions() != null) {
			for(Iterator factions = data.factions().values().iterator(); factions.hasNext();) {
				Faction f = (Faction) factions.next();

				if(f.messages != null) {
					Iterator msgs = f.messages.iterator();

					while(msgs.hasNext()) {
						Message msg = (Message) msgs.next();

						if(msg.attributes != null) {
							Iterator iter = msg.attributes.values().iterator();

							while(iter.hasNext()) {
								try {
									IntegerID id = IntegerID.create((String) iter.next());

									if(id.equals(uc.getID())) {
										sortedMessages.add(msg);
									}
								} catch(NumberFormatException e) {
								}
							}
						}
					}
				}
			}

			if(!sortedMessages.isEmpty()) {
				addCategorizedMessages(sortedMessages, parent);
			}
		}
	}

	/**
	 * Display a <tt>Message</tt> object under a parent node.
	 *
	 * @param m TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void show(Message m, DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode node = null;
		DefaultMutableTreeNode subNode = null;

		node = new DefaultMutableTreeNode(m.getText());
		parent.add(node);

		if(m.attributes != null) {
			String value = (String) m.attributes.get("unit");

			if(value != null) {
				try {
					int i = Integer.parseInt(value);
					Unit u = data.getUnit(UnitID.createUnitID(i,data.base));

					if(u != null) {
						subNode = new DefaultMutableTreeNode(nodeFactory.createUnitNodeWrapper(u));
						node.add(subNode);
					}
				} catch(NumberFormatException e) {
				}
			}

			for(Iterator attributes = m.attributes.values().iterator(); attributes.hasNext();) {
				String val = (String) attributes.next();
				Coordinate c = Coordinate.parse(val, ",");

				if(c == null) {
					c = Coordinate.parse(val, " ");
				}

				if(c != null) {
					Region r = data.getRegion(c);

					if(r != null) {
						subNode = new DefaultMutableTreeNode(nodeFactory.createRegionNodeWrapper(r));
						node.add(subNode);
					}
				}
			}
		}
	}

	/**
	 * Display a <tt>String</tt> object under a parent node.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void show(String s, DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode node = null;

		node = new DefaultMutableTreeNode(s);
		parent.add(node);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	private void handleValueChange() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if(node == null) {
			return;
		}

		Object o = node.getUserObject();

		if(o instanceof UnitNodeWrapper) {
			o = ((UnitNodeWrapper) o).getUnit();
		} else if(o instanceof RegionNodeWrapper) {
			o = ((RegionNodeWrapper) o).getRegion();
		}

		if(o instanceof Unit || o instanceof Region) {
			dispatcher.fire(new SelectionEvent(this, null, o));
		}
	}

	private void initTree() {
		rootNode = new DefaultMutableTreeNode("Rootnode");
		treeModel = new DefaultTreeModel(rootNode);
		tree = new CopyTree(treeModel);
		tree.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount() == 2) {
						handleValueChange();
					}
				}
			});

		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		JScrollPane treeScrollPane = new JScrollPane(tree);

		// ClearLook suggests to remove this border
		treeScrollPane.setBorder(null);

		setLayout(new GridLayout(1, 0));
		add(treeScrollPane);

		URL imgURL = ResourcePathClassLoader.getResourceStatically("images/bullets/leaf.gif");

		if(imgURL != null) {
			((DefaultTreeCellRenderer) tree.getCellRenderer()).setLeafIcon(new ImageIcon(imgURL));
		}

		imgURL = ResourcePathClassLoader.getResourceStatically("images/bullets/open.gif");

		if(imgURL != null) {
			((DefaultTreeCellRenderer) tree.getCellRenderer()).setOpenIcon(new ImageIcon(imgURL));
		}

		imgURL = ResourcePathClassLoader.getResourceStatically("images/bullets/closed.gif");

		if(imgURL != null) {
			((DefaultTreeCellRenderer) tree.getCellRenderer()).setClosedIcon(new ImageIcon(imgURL));
		}

		lineRenderer = new LineWrapCellRenderer((DefaultTreeCellRenderer) tree.getCellRenderer());
		lineRenderer.setLineWrap(settings.getProperty("MessagePanel.LineWrap", "true").equals("true"));
		lineRenderer.registerTree(tree);

		CellRenderer cr = new CellRenderer(getMagellanContext());

		MixedTreeCellRenderer mixed = new MixedTreeCellRenderer(lineRenderer);
		mixed.putRenderer(UnitNodeWrapper.class, cr);
		mixed.putRenderer(RegionNodeWrapper.class, cr);
		tree.setCellRenderer(mixed);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isLineWrap() {
		return lineRenderer.isLineWrap();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setLineWrap(boolean bool) {
		lineRenderer.setLineWrap(bool);
		settings.setProperty("MessagePanel.LineWrap", (bool ? "true" : "false"));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperFactory getNodeWrapperFactory() {
		return nodeFactory;
	}

	/**
	 * Adds the given messages to the parent node, categorized by the section of the messagetypes
	 * and then sorted by messagetypes.
	 *
	 * @param messages TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	private void addCategorizedMessages(Collection messages, DefaultMutableTreeNode parent) {
		Map categories = CollectionFactory.createHashtable();

		// categorize messages
		for(Iterator iter = messages.iterator(); iter.hasNext();) {
			Message message = (Message) iter.next();
			String section = null;

			if(message.getMessageType() != null) {
				section = message.getMessageType().getSection();
			}

			if(section == null) {
				section = getString("node.others");
			}

			List l = (List) categories.get(section);

			if(l == null) {
				l = CollectionFactory.createLinkedList();
				categories.put(section, l);
			}

			l.add(message);
		}

		// sort messages in the single categories
		// and add them as nodes
		Comparator comp = new MessageTypeComparator(IDComparator.DEFAULT);

		for(Iterator iter = categories.keySet().iterator(); iter.hasNext();) {
			Object category = iter.next();
			String s = null;

			try {
				s = getString("section." + category);
			} catch(MissingResourceException e) {
				s = category.toString();
			}

			DefaultMutableTreeNode n = new DefaultMutableTreeNode(s);
			parent.add(n);

			List l = (List) categories.get(category);
			Collections.sort(l, comp);

			for(Iterator iterator = l.iterator(); iterator.hasNext();) {
				show((Message) iterator.next(), n);
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
			defaultTranslations.put("node.factionmessages", "Faction messages");
			defaultTranslations.put("node.messages", "Messages");
			defaultTranslations.put("node.effects", "Effects");
			defaultTranslations.put("node.dispatches", "Dispatches");
			defaultTranslations.put("node.unitdispatches", "Unit dispatches");
			defaultTranslations.put("node.events", "Events");
			defaultTranslations.put("node.surroundings", "Surroundings");
			defaultTranslations.put("node.travelthru", "Travel through");
			defaultTranslations.put("node.battles", "Battles");
			defaultTranslations.put("node.errors", "Errors");
			defaultTranslations.put("node.others", "Others");
			defaultTranslations.put("section.economy", "Economy and Trade");
			defaultTranslations.put("section.movement", "Movement and Travel");
			defaultTranslations.put("section.events", "Events");
			defaultTranslations.put("section.magic", "Magic and Artefacts");
			defaultTranslations.put("section.study", "Learning and Teaching");
			defaultTranslations.put("prefs.linewrap", "Activate line wrapping");
			defaultTranslations.put("prefs.border.title", "Line Wrapping");
			defaultTranslations.put("prefs.title", "Messages");
			defaultTranslations.put("nodeFactory.title", "Message Entries");
			defaultTranslations.put("section.production", "Resources and Production");
			defaultTranslations.put("section.errors", "Warnings and Errors");

			defaultTranslations.put("menu.caption", "Messages");
			defaultTranslations.put("menu.mnemonic", "M");
			defaultTranslations.put("menu.supertitle", "Tree");
		}

		return defaultTranslations;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter createPreferencesAdapter() {
		return new Pref(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JMenu getMenu() {
		JMenu tree = new JMenu(getString("menu.caption"));
		tree.setMnemonic(getString("menu.mnemonic").charAt(0));
		tree.add(nodeFactory.getContextMenu());

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

	protected class Pref extends JPanel implements PreferencesAdapter {
		protected MessagePanel src;
		protected JCheckBox lineWrap;

		/**
		 * Creates a new Pref object.
		 *
		 * @param src TODO: DOCUMENT ME!
		 */
		public Pref(MessagePanel src) {
			super(new GridBagLayout());
			this.src = src;

			JPanel help = new JPanel(new FlowLayout(FlowLayout.LEADING));
			help.setBorder(BorderFactory.createTitledBorder(getString("prefs.border.title")));

			lineWrap = new JCheckBox(getString("prefs.linewrap"), src.isLineWrap());
			help.add(lineWrap);

			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1,
														  GridBagConstraints.WEST,
														  GridBagConstraints.BOTH,
														  new Insets(1, 1, 1, 1), 0, 0);
			this.add(new JPanel(), c);
			c.gridy = 2;
			this.add(new JPanel(), c);

			c.gridy = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			this.add(help, c);
		}
        
        public void initPreferences() {
            // TODO: implement it
        }

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void applyPreferences() {
			src.setLineWrap(lineWrap.isSelected());
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
}
