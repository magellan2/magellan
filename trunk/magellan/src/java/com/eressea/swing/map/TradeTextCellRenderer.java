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

package com.eressea.swing.map;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.eressea.ID;
import com.eressea.LuxuryPrice;
import com.eressea.Region;
import com.eressea.StringID;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.swing.context.ContextChangeable;
import com.eressea.swing.context.ContextObserver;
import com.eressea.util.CollectionFactory;

/**
 * Simple extension of the default text cell renderer that has two modes: Sell-Mode: Shows the
 * luxury sold in the region(first char) and the trade value Price-Mode: Shows the price of a
 * luxury.
 *
 * @author Andreas
 * @version 1.0
 */
public class TradeTextCellRenderer extends TextCellRenderer implements GameDataListener,
																	   ActionListener,
																	   ContextChangeable
{
	protected ItemType item;
	protected String itemName;
	protected boolean sellMode = false;
	protected Collection allLuxuries;
	protected JMenu context;
	protected static final String KEY = "LUXURY";
	protected String stringArray[] = new String[2];
	protected ContextObserver obs;

	/**
	 * Creates new TradeTextCellRenderer
	 *
	 * @param geo TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public TradeTextCellRenderer(CellGeometry geo, Properties settings) {
		super(geo, settings);
		EventDispatcher.getDispatcher().addGameDataListener(this);
		itemName = settings.getProperty("TradeTextCellRenderer.Item");
		sellMode = settings.getProperty("TradeTextCellRenderer.SellMode", "false").equals("true");
		findLuxuries();
		createContextMenu();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param rect TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getSingleString(Region r, Rectangle rect) {
		if(sellMode) {
			if(r.prices == null) {
				return null;
			}

			Iterator it = r.prices.keySet().iterator();

			while(it.hasNext()) {
				ID id = (ID) it.next();
				LuxuryPrice lp = (LuxuryPrice) r.prices.get(id);

				if(lp.getPrice() < 0) {
					ItemType type = data.rules.getItemType(id, false);

					if(type != null) {
						return type.getName().substring(0, 1) + r.maxLuxuries();
					}
				}
			}
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param rect TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String[] getText(Region r, Rectangle rect) {
		if(sellMode) {
			return null;
		} else {
			if((item == null) || (r.prices == null)) {
				return null;
			}

			LuxuryPrice lp = (LuxuryPrice) r.prices.get(item.getID());

			if(lp != null) {
				stringArray[0] = item.getName();
				stringArray[1] = String.valueOf(lp.getPrice());

				return stringArray;
			}

			return null;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		this.data = e.getGameData();
		findLuxuries();
		reprocessContextMenu();
	}

	protected void findLuxuries() {
		if(allLuxuries == null) {
			allLuxuries = CollectionFactory.createLinkedList();
		} else {
			allLuxuries.clear();
		}

		item = null;

		if(data == null) {
			return;
		}

		ItemCategory cat = data.rules.getItemCategory(StringID.create("luxuries"), false);

		if(cat == null) {
			return;
		}

		for(Iterator iter = data.rules.getItemTypeIterator(); iter.hasNext();) {
			ItemType type = (ItemType) iter.next();

			if((type.getCategory() != null) && cat.equals(type.getCategory())) {
				allLuxuries.add(type);

				if(type.getID().toString().equals(itemName)) {
					item = type;
				}
			}
		}
	}

	protected void createContextMenu() {
		context = new JMenu(getName());
	}

	protected void reprocessContextMenu() {
		context.removeAll();

		Iterator it = allLuxuries.iterator();
		boolean added = false;

		while(it.hasNext()) {
			ItemType type = (ItemType) it.next();
			JMenuItem i = new JMenuItem(type.getName());
			i.addActionListener(this);
			i.putClientProperty(KEY, type);
			context.add(i);
			added = true;
		}

		if(added) {
			context.addSeparator();
		}

		JMenuItem i = new JMenuItem(getString("sellMode"));
		i.addActionListener(this);
		context.add(i);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param actionEvent TODO: DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
		JMenuItem src = (JMenuItem) actionEvent.getSource();
		item = (ItemType) src.getClientProperty(KEY);

		if(item == null) {
			sellMode = true;
		} else {
			itemName = item.getID().toString();
			settings.setProperty("TradeTextCellRenderer.Item", itemName);
			sellMode = false;
		}

		if(obs != null) {
			obs.contextDataChanged();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JMenuItem getContextAdapter() {
		return context;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param co TODO: DOCUMENT ME!
	 */
	public void setContextObserver(ContextObserver co) {
		obs = co;
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
			defaultTranslations.put("name", "Trade render");
			defaultTranslations.put("sellMode", "Sell mode");
		}

		return defaultTranslations;
	}
}
