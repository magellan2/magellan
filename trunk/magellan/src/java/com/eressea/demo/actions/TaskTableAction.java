// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.awt.event.ActionEvent;
import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.tasks.swing.TaskTableDialog;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public class TaskTableAction extends MenuAction{
	
	private Client client;
	
	public TaskTableAction(Client parent) {
		client=parent;
	}
	
	public void actionPerformed(ActionEvent e) {
		TaskTableDialog d = new TaskTableDialog(client, false, client.getDispatcher(), client.getData(), client.getSettings());
		d.setVisible(true);
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
			defaultTranslations.put("name"       , "Review...");
			defaultTranslations.put("mnemonic"   , "r");
			defaultTranslations.put("accelerator", "ctrl shift R");
			defaultTranslations.put("tooltip"    , "tooltip for TaskTable");
		}
		return defaultTranslations;
	}
}
