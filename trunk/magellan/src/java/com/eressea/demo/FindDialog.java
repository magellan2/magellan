// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.eressea.Building;
import com.eressea.Described;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.Item;
import com.eressea.Message;
import com.eressea.Named;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.Unique;
import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.swing.InternationalizedDataDialog;
import com.eressea.util.CollectionFactory;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.NameComparator;
import com.eressea.util.logging.Logger;

public class FindDialog extends InternationalizedDataDialog implements javax.swing.event.ListSelectionListener, SelectionListener {
	private final static Logger log = Logger.getInstance(FindDialog.class);
	private JComboBox txtPattern = null;
	private JCheckBox chkIDs = null;
	private JCheckBox chkNames = null;
	private JCheckBox chkDescs = null;
	private JCheckBox chkCmds = null;
	private JCheckBox chkMessages = null;
	private JCheckBox chkItems = null;
	private JComboBox factionCombo = null;
	private JCheckBox addUnits = null;
	private JCheckBox addRegions = null;
	private JCheckBox addBuildings = null;
	private JCheckBox addShips = null;
	private JCheckBox addTraitors = null;
	private JCheckBox addOnlyUnconfirmedUnits = null;
	private JCheckBox addFactions = null;
	private JList resultList = null;
	private JPanel pnlResults = null;

	private List history;

	// selected regions that are stored as region-objects!
	private List selectedRegions = CollectionFactory.createLinkedList();

	/**
	 * @param regions A collection of region objects that were selected on the map.
	 *                Up to the next SelectionEvent with type ST_REGIONS any search
	 *                will be limited to these regions.
	 */
	public FindDialog(Frame owner, boolean modal, EventDispatcher dispatcher, GameData d, Properties p, Collection regions) {
		super(owner, modal, dispatcher, d, p);
		dispatcher.addSelectionListener(this);
		dispatcher.addGameDataListener(this);
		selectedRegions.addAll(regions);
		data = d;
		settings = p;
		this.dispatcher = dispatcher;

		setTitle(getString("window.title"));
		setContentPane(getMainPane());
		setSize(420, 500);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = Integer.parseInt(settings.getProperty("FindDialog.x", ((screen.width - getWidth()) / 2) + ""));
		int y = Integer.parseInt(settings.getProperty("FindDialog.y", ((screen.height - getHeight()) / 2) + ""));
		setLocation(x, y);
	}

	public void selectionChanged(SelectionEvent s) {
		if (s == null || s.getSelectedObjects() == null) {
			return;
		}
		if (s.getSelectionType() == SelectionEvent.ST_REGIONS) {
			// some regions on the map were selected or deselected
			// it is assumed that selections of this type contain only regions
			selectedRegions.clear();
			for (Iterator iter = s.getSelectedObjects().iterator(); iter.hasNext(); ) {
				Region r = (Region)iter.next();
				selectedRegions.add(r);
			}
		}
	}

	public void gameDataChanged(GameDataEvent e) {
		this.data = e.getGameData();
		this.selectedRegions.clear();
	}

	public void valueChanged(javax.swing.event.ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}

		Object o = resultList.getSelectedValue();
		Object fireObject = null;

		if (o != null) {
			if (o instanceof RegionWrapper) {
				fireObject = ((RegionWrapper)o).getRegion();
			} else if (o instanceof UnitWrapper) {
				fireObject = ((UnitWrapper)o).getUnit();
			} else {
				fireObject = o;
			}
		}

		if (fireObject != null) {
			dispatcher.fire(new SelectionEvent(this, null, fireObject));
		}
	}

	private JPanel getMainPane() {
		JPanel main = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		main.setLayout(gridbag);
		main.setBorder(new EmptyBorder(4, 4, 4, 4));

		ActionListener findListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findAction();
			}
		};

		history = CollectionFactory.createLinkedList();
		history.add("");
		if (settings.containsKey("FindDialog.History")) {
			StringTokenizer st = new StringTokenizer(settings.getProperty("FindDialog.History"),"~");
			while(st.hasMoreTokens()) {
				history.add(st.nextToken());
			}
		}
		txtPattern = new JComboBox(history.toArray());
		txtPattern.setEditable(true);
		txtPattern.addActionListener(findListener);
		txtPattern.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		txtPattern.setPreferredSize(new Dimension(100, 25));
		JLabel l = new JLabel(getString("lbl.pattern.caption") + ": ");
		l.setDisplayedMnemonic(getString("lbl.pattern.mnemonic").charAt(0));
		l.setLabelFor(txtPattern);

		JPanel pnlPattern = new JPanel(new BorderLayout());
		pnlPattern.setBorder(new EmptyBorder(4, 4, 4, 4));
		pnlPattern.add(l, BorderLayout.WEST);
		pnlPattern.add(txtPattern, BorderLayout.CENTER);


		chkIDs = new JCheckBox(getString("chk.ids.caption"), settings.getProperty("FindDialog.IDs","true").equals("true"));
		chkIDs.setMnemonic(getString("chk.ids.mnemonic").charAt(0));

		chkNames = new JCheckBox(getString("chk.names.caption"), settings.getProperty("FindDialog.Names","true").equals("true"));
		chkNames.setMnemonic(getString("chk.names.mnemonic").charAt(0));

		chkDescs = new JCheckBox(getString("chk.descriptions.caption"), settings.getProperty("FindDialog.Descriptions","true").equals("true"));
		chkDescs.setMnemonic(getString("chk.descriptions.mnemonic").charAt(0));

		chkCmds = new JCheckBox(getString("chk.orders.caption"), settings.getProperty("FindDialog.Orders","true").equals("true"));
		chkCmds.setMnemonic(getString("chk.orders.mnemonic").charAt(0));

		chkMessages = new JCheckBox(getString("chk.msgsandeffects.caption"), settings.getProperty("FindDialog.Msgs","true").equals("true"));
		chkMessages.setMnemonic(getString("chk.msgsandeffects.mnemonic").charAt(0));

		chkItems = new JCheckBox(getString("chk.items.caption"), settings.getProperty("FindDialog.Items","true").equals("true"));
		chkItems.setMnemonic(getString("chk.items.mnemonic").charAt(0));

		JPanel pnlAttributeCheckBoxes = new JPanel(new GridBagLayout());

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlAttributeCheckBoxes.add(chkIDs, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		c.weighty = 0.0;
		pnlAttributeCheckBoxes.add(chkNames, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlAttributeCheckBoxes.add(chkDescs, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlAttributeCheckBoxes.add(chkCmds, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlAttributeCheckBoxes.add(chkMessages, c);

		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.1;
		c.weighty = 0.0;
		pnlAttributeCheckBoxes.add(chkItems, c);

		List factions = CollectionFactory.createLinkedList(data.factions().values());
		Collections.sort(factions, new NameComparator(new IDComparator()));
		factionCombo = new JComboBox(factions.toArray());
		factionCombo.addItem("");
		factionCombo.setSelectedIndex(factionCombo.getItemCount() - 1);
		JLabel factionLabel = new JLabel(getString("lbl.faction.caption") + ": ");
		factionLabel.setDisplayedMnemonic(getString("lbl.faction.mnemonic").charAt(0));
		factionLabel.setLabelFor(factionCombo);

		JPanel pnlFaction = new JPanel(new BorderLayout());
		pnlFaction.add(factionLabel, BorderLayout.WEST);
		pnlFaction.add(factionCombo, BorderLayout.CENTER);

		JPanel pnlAttributes = new JPanel(new BorderLayout());
		pnlAttributes.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString("frm.attributes")));
		pnlAttributes.add(pnlAttributeCheckBoxes, BorderLayout.CENTER);
		pnlAttributes.add(pnlFaction, BorderLayout.SOUTH);


		JButton findButton = new JButton(getString("btn.find"));
		findButton.setDefaultCapable(true);
		findButton.addActionListener(findListener);
		this.getRootPane().setDefaultButton(findButton);

		JButton cancelButton = new JButton(getString("btn.close"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});

		JPanel pnlButtons = new JPanel(new GridLayout(2, 1, 3, 3));
		pnlButtons.add(findButton);
		pnlButtons.add(cancelButton);

		addUnits = new JCheckBox(getString("chk.units.caption"), settings.getProperty("FindDialog.Units","true").equals("true"));
		addUnits.setMnemonic(getString("chk.units.mnemonic").charAt(0));
		addRegions = new JCheckBox(getString("chk.regions.caption"), settings.getProperty("FindDialog.Regions","false").equals("true"));
		addRegions.setMnemonic(getString("chk.regions.mnemonic").charAt(0));
		addBuildings = new JCheckBox(getString("chk.buildings.caption"), settings.getProperty("FindDialog.Buildings","false").equals("true"));
		addBuildings.setMnemonic(getString("chk.buildings.mnemonic").charAt(0));
		addShips = new JCheckBox(getString("chk.ships.caption"), settings.getProperty("FindDialog.Ships","false").equals("true"));
		addShips.setMnemonic(getString("chk.ships.mnemonic").charAt(0));
		addTraitors = new JCheckBox(getString("chk.traitors.caption"), settings.getProperty("FindDialog.Traitors","false").equals("true"));
		addTraitors.setMnemonic(getString("chk.traitors.mnemonic").charAt(0));
		addOnlyUnconfirmedUnits = new JCheckBox(getString("chk.addonlyunconfirmedunits.caption"), settings.getProperty("FindDialog.OnlyUnconfirmedUnits", "false").equals("true"));
		addOnlyUnconfirmedUnits.setMnemonic(getString("chk.addonlyunconfirmedunits.mnemonic").charAt(0));
		addFactions = new JCheckBox(getString("chk.factions.caption"), settings.getProperty("FindDialog.Factions","true").equals("true"));
		addFactions.setMnemonic(getString("chk.factions.mnemonic").charAt(0));

		JPanel pnlItems = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
		pnlItems.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString("frm.objects")));

		pnlItems.add(addRegions, gbc);
		gbc.gridx++;
		pnlItems.add(addFactions, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		pnlItems.add(addBuildings, gbc);
		gbc.gridx++;
		pnlItems.add(addShips, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		pnlItems.add(addTraitors, gbc);
		gbc.gridx++;
		pnlItems.add(addUnits, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		pnlItems.add(addOnlyUnconfirmedUnits, gbc);



		resultList = new JList();
		resultList.addListSelectionListener(this);
		//resultList.setCellRenderer(new IconListCellRenderer());
		JScrollPane scroller = new JScrollPane(resultList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setBorder(new LineBorder(Color.black));
		JButton bookmarkResults = new JButton(getString("btn.bookmark"));
		bookmarkResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < resultList.getModel().getSize(); i++) {
					Object o = resultList.getModel().getElementAt(i);
					if (o instanceof RegionWrapper) {
						o = ((RegionWrapper)o).getRegion();
					} else if (o instanceof UnitWrapper) {
						o = ((UnitWrapper)o).getUnit();
					}
					((Client)FindDialog.this.getOwner()).getBookmarkManager().addBookmark(o);
				}
			}
		});
		pnlResults = new JPanel(new BorderLayout());
		pnlResults.add(scroller, BorderLayout.CENTER);
		pnlResults.add(bookmarkResults, BorderLayout.SOUTH);
		pnlResults.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString("frm.results")));

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 0.0;
		main.add(pnlPattern, c);

		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 2;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(2, 2, 2, 2);
		c.weightx = 0.0;
		c.weighty = 0.0;
		main.add(pnlButtons, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0.0;
		c.weighty = 0.0;
		main.add(pnlAttributes, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0.0;
		c.weighty = 0.0;
		main.add(pnlItems, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		c.weightx = 0.5;
		c.weighty = 0.5;
		main.add(pnlResults, c);

		return main;
	}

	private Collection find() {
		Collection hits = CollectionFactory.createHashSet();
		Collection items = CollectionFactory.createLinkedList();
		String pattern = "";
		if (txtPattern.getSelectedItem() != null) {
			pattern = txtPattern.getSelectedItem().toString();
			if (!history.contains(pattern)) {
				if (history.contains("")) {
					history.remove("");
				}
				if (history.size()==5) {
					history.remove(4);
				}
				history.add(0,pattern);
				List save=CollectionFactory.createLinkedList(history);
				txtPattern.removeAllItems();
				history = save;
				for(Iterator iter =history.iterator(); iter.hasNext(); ) {
					txtPattern.addItem(iter.next());
				}
				txtPattern.setSelectedIndex(0);
				storeHistory();
				txtPattern.repaint();
			}
		}
		Collection patterns = CollectionFactory.createLinkedList();
		StreamTokenizer st = new StreamTokenizer(new StringReader(pattern));
		st.ordinaryChars('0', '9');
		st.ordinaryChar('.');
		st.ordinaryChar('-');
		st.wordChars('0', '9');
		st.wordChars('.', '.');
		st.wordChars('-', '-');
		st.lowerCaseMode(true);
		do {
			try {
				st.nextToken();
			} catch (java.io.IOException e) {
				log.error(e);
			}
			if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '\'' || st.ttype == '"') {
				patterns.add(st.sval);
//				System.out.println(st.sval);
			} else if (st.ttype != StreamTokenizer.TT_EOF) {
				log.warn("Found uncexpected TokenType (" + st.ttype +
						 ") in FindDialog.find() while parsing token: " + st.toString());
			}
		} while (st.ttype != StreamTokenizer.TT_EOF);

		// determine the items to search
		if (addUnits.isSelected() == true) {
			// items.addAll(data.units().values()); TempUnits were forgotten...
			for (Iterator iter = data.units().values().iterator(); iter.hasNext(); ) {
				Unit u = (Unit)iter.next();
				if (selectedRegions == null || selectedRegions.isEmpty() || selectedRegions.contains(u.getRegion())) {
					if (addOnlyUnconfirmedUnits.isSelected()) {
						if (!u.ordersConfirmed) {
							items.add(u);
						}
						for (Iterator iterator = u.tempUnits().iterator(); iterator.hasNext(); ) {
							Unit tu = (Unit)iterator.next();
							if (!tu.ordersConfirmed) {
								items.add(tu);
							}
						}
					} else {
						items.add(u);
						items.addAll(u.tempUnits());
					}
				}
			}
		}
		if (addRegions.isSelected() == true) {
			if (selectedRegions == null || selectedRegions.isEmpty()) {
				items.addAll(data.regions().values());
			} else {
				items.addAll(selectedRegions);
			}
		}
		if (addBuildings.isSelected() == true) {
			if (selectedRegions == null || selectedRegions.isEmpty()) {
				items.addAll(data.buildings().values());
			} else {
				for (Iterator iter = data.buildings().values().iterator(); iter.hasNext(); ) {
					Building b = (Building)iter.next();
					if (selectedRegions.contains(b.getRegion())) {
						items.add(b);
					}
				}
			}
		}
		if (addShips.isSelected() == true) {
			if (selectedRegions == null || selectedRegions.isEmpty()) {
				items.addAll(data.ships().values());
			} else {
				for (Iterator iter = data.ships().values().iterator(); iter.hasNext(); ) {
					Ship s = (Ship)iter.next();
					if (selectedRegions.contains(s.getRegion())) {
						items.add(s);
					}
				}
			}
		}
		if (addTraitors.isSelected() == true) {
			if (!addUnits.isSelected()) {
				Collection traitors = getAllTraitors();
				if (selectedRegions == null || selectedRegions.isEmpty()) {
					items.addAll(traitors);
				} else {
					for (Iterator iter = traitors.iterator(); iter.hasNext(); ) {
						Unit u = (Unit)iter.next();
						if (selectedRegions.contains(u.getRegion())) {
							items.add(u);
						}
					}
				}
			}
		}
		if (addFactions.isSelected()) {
			items.addAll(data.factions().values());
		}

		// determine the faction to limit the search to
		Faction faction = getFactionFromCombo();

		Iterator i = items.iterator();
		while (i.hasNext() == true) {
			Object item = i.next();
			if (chkItems.isSelected() && filterItem(item, patterns) == true) {
				hits.add(item);
			}
			if (chkIDs.isSelected() && filterId(item, patterns) == true) {
				hits.add(item);
			}
			if (chkNames.isSelected() && filterName(item, patterns) == true) {
				hits.add(item);
			}
			if (chkDescs.isSelected() && filterDesc(item, patterns) == true)  {
				hits.add(item);
			}
			if (chkCmds.isSelected() && filterCmd(item, patterns) == true)  {
				hits.add(item);
			}
			if (chkMessages.isSelected() && filterMessage(item, patterns) == true) {
				hits.add(item);
			}
			if (faction != null && filterFaction(item, faction) == false) {
				hits.remove(item);
			}
		}

		return wrap(hits);
	}

	private boolean filterId(Object item, Collection patterns) {
		if (patterns.size() == 0) {
			return true;
		}

		boolean retVal = false;
		String id = getID(item).toLowerCase();
		for (Iterator iter = patterns.iterator(); iter.hasNext();) {
			if (id.equals((String)iter.next())) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}

	private boolean filterItem(Object item, Collection patterns) {
		if (patterns.size() == 0) {
			return true;
		}

		boolean retVal = false;
		if (item instanceof Unit) {
			Unit u = (Unit)item;
			if (u.items != null) {
				for (Iterator iterator = u.items.values().iterator(); iterator.hasNext(); ) {
					String name = getName(((Item)iterator.next()).getItemType());
					if (name != null) {
						name = name.toLowerCase();
						for (Iterator iter = patterns.iterator(); iter.hasNext();) {
							if (name.indexOf((String)iter.next()) > -1) {
								retVal = true;
								break;
							}
						}
					}
				}
			}
		}
		return retVal;
	}


	private String getID(Object item) {
		ID id = ((Unique)item).getID();
		if (id != null) {
			return id.toString();
		} else {
			log.error("Found Unique without id: " + item);
			return "";
		}
	}

	private boolean filterName(Object item, Collection patterns) {
		boolean retVal = false;
		String name = getName(item);
		if (name != null) {
			name = name.toLowerCase();
			for (Iterator iter = patterns.iterator(); iter.hasNext();) {
				if (name.indexOf((String)iter.next()) > -1) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	private String getName(Object item) {
		String name = null;
		if (item instanceof Named) {
			name = ((Named)item).getName();
		}
		return name;
	}

	private boolean filterDesc(Object item, Collection patterns) {
		boolean retVal = false;
		String desc = getDesc(item);
		if (desc != null) {
			desc = desc.toLowerCase();
			for (Iterator iter = patterns.iterator(); iter.hasNext();) {
				if (desc.indexOf((String)iter.next()) > -1) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	private String getDesc(Object item) {
		String desc = null;
		if (item instanceof Described) {
			desc = ((Described)item).getDescription();
		}
		return desc;
	}

	private boolean filterCmd(Object item, Collection patterns) {
		boolean retVal = false;
		Collection cmds = getCmds(item);
		if (cmds != null)  {
			for (Iterator cmdsIter = cmds.iterator(); cmdsIter.hasNext();)  {
				String cmd = ((String)cmdsIter.next()).toLowerCase();
				for (Iterator ptrnIter = patterns.iterator(); ptrnIter.hasNext();) {
					if (cmd.indexOf((String)ptrnIter.next()) > -1)  {
						retVal = true;
						break;
					}
				}
			}
		}
		return retVal;
	}

	private Collection getAllTraitors() {
		Collection retVal = CollectionFactory.createLinkedList();
		for (Iterator iter = data.units().values().iterator(); iter.hasNext(); ) {
			Unit unit = (Unit)iter.next();
			if (unit.isSpy()) {
				retVal.add(unit);
			}
		}
		return retVal;
	}

	private Collection getCmds(Object item) {
		Collection retVal = null;
		if (item instanceof Unit) {
			retVal = ((Unit)item).getOrders();
		}
		return retVal;
	}

	private boolean filterMessage(Object item, Collection patterns) {
		boolean retVal = false;
		Collection msgs = getMessages(item);
		if (msgs != null)  {
			for (Iterator msgsIter = msgs.iterator(); msgsIter.hasNext();)  {
				Object o = msgsIter.next();
				String msg = "";
				if (o instanceof String) {
					msg = (String)o;
				} else if (o instanceof Message) {
					msg = ((Message)o).getText();
				}
				msg = msg.toLowerCase();
				for (Iterator ptrnIter = patterns.iterator(); ptrnIter.hasNext();) {
					if (msg.indexOf((String)ptrnIter.next()) > -1)  {
						retVal = true;
						break;
					}
				}
			}
		}
		return retVal;
	}

	private Collection getMessages(Object item) {
		Collection retVal = CollectionFactory.createLinkedList();
		if (item instanceof Unit) {
			Unit u = (Unit)item;
			if (u.unitMessages != null) {
				retVal.addAll(u.unitMessages);
			}
			if (u.effects != null) {
				retVal.addAll(u.effects);
			}
		} else if (item instanceof UnitContainer) {
			UnitContainer c = (UnitContainer)item;
			if (c.effects != null) {
				retVal.addAll(c.effects);
			}
			if (c instanceof Region) {
				Region r = (Region)c;
				if (r.messages != null) {
					retVal.addAll(r.messages);
				}
				if (r.events != null) {
					retVal.addAll(r.events);
				}
				if (r.playerMessages != null) {
					retVal.addAll(r.playerMessages);
				}
				if (r.travelThru != null) {
					retVal.addAll(r.travelThru);
				}
				if (r.travelThruShips != null) {
					retVal.addAll(r.travelThruShips);
				}
			} else if (c instanceof Faction) {
				Faction f = (Faction)c;
				if (f.messages != null) {
					retVal.addAll(f.messages);
				}
				if (f.errors != null) {
					retVal.addAll(f.errors);
				}
			}
		}
		return retVal;
	}

	private boolean filterFaction(Object item, Faction pattern) {
		boolean retVal = true;
		Faction faction = getFaction(item);
		if (faction == null || faction.equals(pattern) == false) {
			retVal = false;
		}
		return retVal;
	}

	private Faction getFaction(Object item) {
		Faction faction = null;
		if (item instanceof Unit) {
			faction = ((Unit)item).getFaction();
		} else
		/*if (item instanceof Region) {
			name = ((Region)item).getName();
		} else*/
		if (item instanceof Building || item instanceof Ship) {
			Unit owner = ((UnitContainer)item).getOwnerUnit();
			if (owner != null) {
				faction = owner.getFaction();
			}
		}
		return faction;
	}

	private Collection wrap(Collection items) {
		Collection wrappers = CollectionFactory.createLinkedList();
		Iterator i = items.iterator();
		while (i.hasNext() == true) {
			Object item = i.next();
			if (item instanceof Unit) {
				wrappers.add(new UnitWrapper((Unit)item));
			} else
			if (item instanceof Region) {
				wrappers.add(new RegionWrapper((Region)item));
			} else {
				wrappers.add(item);
			}
			/*if (item instanceof Building) {
			}
			if (item instanceof Ship) {
			}*/
		}
		return wrappers;
	}

	private Faction getFactionFromCombo() {
		Faction f = null;
		Object item = factionCombo.getSelectedItem();
		if (item != null && item instanceof Faction) {
			f = (Faction)item;
		}
		return f;
	}

	/**
	 * A class wrapping a Region object, customizing the toString()
	 * needs for the tree.
	 */
	private class RegionWrapper {
		private Region region = null;

		public RegionWrapper(Region r) {
			region = r;
		}

		public Region getRegion() {
			return region;
		}

		public String toString() {
			return region.getName() + " (" + region.getCoordinate().toString(", ") + ")";
		}
	}

	/**
	 * A class wrapping a Unit object, customizing the toString()
	 * needs for the tree.
	 */
	private class UnitWrapper {
		private Unit unit = null;

		public UnitWrapper(Unit u) {
			unit = u;
		}

		public Unit getUnit() {
			return unit;
		}

		public String toString() {
			return unit.toString();
		}
	}

	/**
	 * Closes the dialog and stores settings of the dialog.
	 */
	protected void quit() {
		settings.setProperty("FindDialog.x", getX() + "");
		settings.setProperty("FindDialog.y", getY() + "");
		storeHistory();
		storeSettings();
		super.dispose();
	}

	protected void storeSettings() {
		storeCheckbox(chkIDs, "IDs");
		storeCheckbox(chkNames, "Names");
		storeCheckbox(chkDescs, "Descriptions");
		storeCheckbox(chkCmds, "Orders");
		storeCheckbox(chkMessages, "Messages");
		storeCheckbox(chkItems, "Items");

		storeCheckbox(addUnits, "Units");
		storeCheckbox(addRegions, "Regions");
		storeCheckbox(addBuildings, "Buildings");
		storeCheckbox(addShips, "Ships");
		storeCheckbox(addTraitors, "Traitors");
		storeCheckbox(addFactions, "Factions");
		storeCheckbox(addOnlyUnconfirmedUnits, "OnlyUnconfirmedUnits");
	}

	protected void storeCheckbox(JCheckBox box, String key) {
		settings.setProperty("FindDialog."+key, box.isSelected()?"true":"false");
	}

	protected void storeHistory() {
		if (history != null && history.size()>0) {
			StringBuffer buf=new StringBuffer();
			Iterator it=history.iterator();
			while(it.hasNext()) {
				buf.append(it.next());
				if (it.hasNext()) {
					buf.append('~');
				}
			}
			settings.setProperty("FindDialog.History",buf.toString());
		} else {
			settings.remove("FindDialog.History");
		}
	}

	protected void findAction() {
		Collection results = find();
		resultList.setListData(results.toArray());
		pnlResults.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), getString("frm.results") + " (" + results.size() + ")"));
		if (results.size() > 0) {
			resultList.requestFocus();
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
			defaultTranslations.put("window.title" , "Find");
			
			defaultTranslations.put("lbl.pattern.caption" , "Pattern");
			defaultTranslations.put("lbl.pattern.mnemonic" , "p");
			defaultTranslations.put("lbl.faction.caption" , "Faction");
			defaultTranslations.put("lbl.faction.mnemonic" , "f");
			
			defaultTranslations.put("frm.attributes" , "Attributes");
			defaultTranslations.put("frm.objects" , "Objects");
			defaultTranslations.put("frm.results" , "Results");
			
			defaultTranslations.put("btn.find" , "Find");
			defaultTranslations.put("btn.close" , "Close");
			defaultTranslations.put("btn.bookmark" , "Bookmark results");
			
			defaultTranslations.put("chk.ids.caption" , "Numbers/IDs");
			defaultTranslations.put("chk.ids.mnemonic" , "i");
			defaultTranslations.put("chk.names.caption" , "Names");
			defaultTranslations.put("chk.names.mnemonic" , "n");
			defaultTranslations.put("chk.descriptions.caption" , "Descriptions");
			defaultTranslations.put("chk.descriptions.mnemonic" , "d");
			defaultTranslations.put("chk.orders.caption" , "Orders");
			defaultTranslations.put("chk.orders.mnemonic" , "o");
			defaultTranslations.put("chk.msgsandeffects.caption" , "Messages and effects");
			defaultTranslations.put("chk.msgsandeffects.mnemonic" , "m");
			defaultTranslations.put("chk.units.caption" , "Units");
			defaultTranslations.put("chk.units.mnemonic" , "u");
			defaultTranslations.put("chk.regions.caption" , "Regions");
			defaultTranslations.put("chk.regions.mnemonic" , "r");
			defaultTranslations.put("chk.buildings.caption" , "Buildings");
			defaultTranslations.put("chk.buildings.mnemonic" , "b");
			defaultTranslations.put("chk.ships.caption" , "Ships");
			defaultTranslations.put("chk.ships.mnemonic" , "s");
			defaultTranslations.put("chk.traitors.caption" , "Traitors");
			defaultTranslations.put("chk.traitors.mnemonic" , "t");
			defaultTranslations.put("chk.items.caption" , "Items");
			defaultTranslations.put("chk.items.mnemonic" , "e");
			defaultTranslations.put("chk.factions.caption" , "Factions");
			defaultTranslations.put("chk.factions.mnemonic" , "f");
			defaultTranslations.put("chk.addonlyunconfirmedunits.caption", "Only unconfirmed");
			defaultTranslations.put("chk.addonlyunconfirmedunits.mnemonic", "u");
			
		}
		return defaultTranslations;
	}

}
