// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;

import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.swing.TradeOrganizer;
import com.eressea.util.CollectionFactory;

/**
 * Just a little class to invoke a trade organizer
 * @author Ulrich Küster
 */
public class TradeOrganizerAction extends MenuAction{

	private Client client;

	public TradeOrganizerAction(Client parent) {
		client = parent;
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		TradeOrganizer to = new TradeOrganizer(client, client.getDispatcher(), client.getData(), client.getSettings(), client.getSelectedRegions().values());
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
			defaultTranslations.put("name"       , "Trade organizer...");
			defaultTranslations.put("mnemonic"   , "");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
