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

package com.eressea.demo.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.swing.TradeOrganizer;
import com.eressea.util.CollectionFactory;

/**
 * Just a little class to invoke a trade organizer
 *
 * @author Ulrich Küster
 */
public class TradeOrganizerAction extends MenuAction implements GameDataListener {

	/**
	 * Creates a new TradeOrganizerAction object.
	 *
	 * @param client
	 */
	public TradeOrganizerAction(Client client) {
        super(client);
        setEnabled(false);
        client.getDispatcher().addGameDataListener(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		new TradeOrganizer(client, client.getDispatcher(), client.getData(), client.getProperties(),
						   client.getSelectedRegions().values());
	}

	public void gameDataChanged(GameDataEvent e) {
		// TODO Auto-generated method stub
		int i = super.client.getData().regions().size();
		if (i>0) {
			setEnabled(true);
		} else {
			setEnabled(false);
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
			defaultTranslations.put("name", "Trade organizer...");
			defaultTranslations.put("mnemonic", "");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
		}

		return defaultTranslations;
	}
}
