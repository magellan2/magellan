// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;

import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.swing.ArmyStatsDialog;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public class ArmyStatsAction extends MenuAction{
	
	private Client client;
	
	public ArmyStatsAction(Client parent) {
		client = parent;
	}
	
	public void actionPerformed(java.awt.event.ActionEvent e) {
		new ArmyStatsDialog(client, client.getDispatcher(), 
							client.getData(), 
							client.getSettings())
			.setVisible(true);
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
			defaultTranslations.put("name"       , "Army statistics...");
			defaultTranslations.put("mnemonic"   , "");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
