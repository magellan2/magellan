// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.awt.event.ActionEvent;
import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public class QuitAction extends MenuAction {

	private Client client;

	/** Creates new OpenCRAction */
	public QuitAction(Client parent) {
		client=parent;
	}

	/**
	 * Called when the file->open menu is selected in order to open
	 * a certain cr file. Displays a file chooser and loads the
	 * selected cr file.
	 */
	public void actionPerformed(ActionEvent e) {
		client.quit(true);
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
			defaultTranslations.put("name"       , "Exit");
			defaultTranslations.put("mnemonic"   , "x");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
