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
import com.eressea.swing.ArmyStatsDialog;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class ArmyStatsAction extends MenuAction implements GameDataListener {

	/**
	 * Creates a new ArmyStatsAction object.
	 *
	 * @param client
	 */
	public ArmyStatsAction(Client client) {
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
		new ArmyStatsDialog(client, client.getDispatcher(), client.getData(), client.getProperties()).setVisible(true);
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
			defaultTranslations.put("name", "Army statistics...");
			defaultTranslations.put("mnemonic", "");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
		}

		return defaultTranslations;
	}
}
