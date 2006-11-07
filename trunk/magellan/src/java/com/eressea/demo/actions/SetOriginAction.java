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
import com.eressea.demo.SetOriginDialog;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class SetOriginAction extends MenuAction {

	/**
	 * Creates a new SetOriginAction object.
	 *
	 * @param client
	 */
	public SetOriginAction(Client client) {
        super(client);
	}

	/**
	 * Opens the SetOriginDialog, waits for user input
	 * if approved, then setOrigin of class Client is called
	 * sets the new Origin
	 *
	 * @param e ActionEvent
	 */
	public void actionPerformed(ActionEvent e) {
		SetOriginDialog dialog = new SetOriginDialog(client, client.getDispatcher(), client.getData());
		dialog.show();
		if (dialog.approved()){
			client.setOrigin(dialog.getNewOrigin());
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
			defaultTranslations.put("name", "Set origin...");
			defaultTranslations.put("mnemonic", "o");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
		}

		return defaultTranslations;
	}
}
