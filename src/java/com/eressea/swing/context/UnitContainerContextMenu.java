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

package com.eressea.swing.context;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;
import java.util.Properties;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Ship;
import com.eressea.Unit;
import com.eressea.UnitContainer;

import com.eressea.event.EventDispatcher;
import com.eressea.event.UnitOrdersEvent;

import com.eressea.swing.FactionStatsDialog;

import com.eressea.util.CollectionFactory;
import com.eressea.util.ShipRoutePlanner;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster A context menu for UnitContainers like ships or buildings. Providing copy
 * 		   ID and copy ID+name.
 */
public class UnitContainerContextMenu extends JPopupMenu {
	private UnitContainer uc;
	private EventDispatcher dispatcher;
	private GameData data;
	private Properties settings;

	/**
	 * Creates a new UnitContainerContextMenu object.
	 *
	 * @param uc TODO: DOCUMENT ME!
	 * @param dispatcher TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param settings TODO: DOCUMENT ME!
	 */
	public UnitContainerContextMenu(UnitContainer uc, EventDispatcher dispatcher, GameData data,
									Properties settings) {
		super(uc.toString());
		this.uc = uc;
		this.dispatcher = dispatcher;
		this.data = data;
		this.settings = settings;

		initMenu();
	}

	private void initMenu() {
		JMenuItem name = new JMenuItem(uc.toString());
		name.setEnabled(false);
		add(name);

		JMenuItem copyID = new JMenuItem(getString("menu.copyid.caption"));
		copyID.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					copyID();
				}
			});
		add(copyID);

		JMenuItem copyNameID = new JMenuItem(getString("menu.copyidandname.caption"));
		copyNameID.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					copyNameID();
				}
			});
		add(copyNameID);

		if(uc instanceof Ship) {
			JMenuItem planShipRoute = new JMenuItem(getString("menu.planshiproute.caption"));
			planShipRoute.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						planShipRoute();
					}
				});
			planShipRoute.setEnabled(ShipRoutePlanner.canPlan((Ship) uc));
			add(planShipRoute);
		} else if(uc instanceof Faction) {
			JMenuItem copyMail = new JMenuItem(getString("menu.copymail.caption"));
			copyMail.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						copyMail();
					}
				});

			JMenuItem factionStats = new JMenuItem(getString("menu.factionstats.caption"));
			factionStats.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						factionStats();
					}
				});
			add(copyMail);
			add(factionStats);
		}
	}

	/**
	 * Copies the ID of the UnitContainer to the clipboard.
	 */
	private void copyID() {
		StringSelection strSel = new StringSelection(uc.getID().toString());
		Clipboard cb = getToolkit().getSystemClipboard();
		cb.setContents(strSel, null);
	}

	/**
	 * Copies name and id to the sytem clipboard.
	 */
	private void copyNameID() {
		StringSelection strSel = new StringSelection(uc.getName() + " (" + uc.getID().toString() +
													 ")");
		Clipboard cb = getToolkit().getSystemClipboard();
		cb.setContents(strSel, null);
	}

	/**
	 * Copies the mailadress of a faction to the clipboard.
	 */
	private void copyMail() {
		Faction f = (Faction) uc;

		// pavkovic 2002.11.12: creating mail addresses in a form like: Noeskadu <noeskadu@gmx.de>
		StringSelection strSel = new StringSelection(f.getName() + " <" + f.email + ">");
		Clipboard cb = getToolkit().getSystemClipboard();
		cb.setContents(strSel, null);
	}

	/**
	 * Calls the factionstats
	 */
	private void factionStats() {
		FactionStatsDialog d = new FactionStatsDialog(JOptionPane.getFrameForComponent(this),
													  false, dispatcher, data, settings,
													  (Faction) uc);
		d.setVisible(true);
	}

	/**
	 * Plans a route for a ship (typically over several weeks)
	 *
	 * @see ShipRoutingDialog
	 */
	private void planShipRoute() {
		Unit unit = ShipRoutePlanner.planShipRoute((Ship) uc, data, this);

		if(unit != null) {
			dispatcher.fire(new UnitOrdersEvent(this, unit));
		}
	}

	private String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this, key);
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
			defaultTranslations.put("menu.copyid.caption", "Copy ID");
			defaultTranslations.put("menu.copyidandname.caption", "Copy ID and name");
			defaultTranslations.put("menu.planshiproute.caption", "Ship route scheduler");
			defaultTranslations.put("menu.copymail.caption", "Copy email address");
			defaultTranslations.put("menu.factionstats.caption", "Factionstats");
		}

		return defaultTranslations;
	}
}
