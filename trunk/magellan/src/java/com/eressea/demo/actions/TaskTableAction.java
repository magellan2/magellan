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
import com.eressea.tasks.swing.TaskTableDialog;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class TaskTableAction extends MenuAction {

	/**
	 * Creates a new TaskTableAction object.
	 *
	 * @param parent TODO: DOCUMENT ME!
	 */
	public TaskTableAction(Client client) {
        super(client);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		TaskTableDialog d = new TaskTableDialog(client, false, client.getDispatcher(),
												client.getData(), client.getProperties());
		d.setVisible(true);
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
			defaultTranslations.put("name", "Review...");
			defaultTranslations.put("mnemonic", "r");
			defaultTranslations.put("accelerator", "ctrl shift R");
			defaultTranslations.put("tooltip", "tooltip for TaskTable");
		}

		return defaultTranslations;
	}
}
