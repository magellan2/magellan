// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;

import java.util.Collection;
import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.demo.FindDialog;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public class FindAction extends MenuAction implements SelectionListener, GameDataListener {

	private Client client;
	private Collection selectedRegions = CollectionFactory.createLinkedList();

	public FindAction(Client parent) {
		client = parent;
		client.getDispatcher().addGameDataListener(this);
		client.getDispatcher().addSelectionListener(this);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		FindDialog f = new FindDialog(client, false, client.getDispatcher(), client.getData(), client.getSettings(), selectedRegions);
		f.setVisible(true);
	}

	public void selectionChanged(SelectionEvent s) {
		if (s.getSelectionType() == SelectionEvent.ST_REGIONS) {
			selectedRegions.clear();
			if (s.getSelectedObjects() != null) {
				selectedRegions.addAll(s.getSelectedObjects());
			}
		}
	}

	public void gameDataChanged(GameDataEvent e) {
		selectedRegions.clear();
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
			defaultTranslations.put("name"       , "Find...");
			defaultTranslations.put("mnemonic"   , "f");
			defaultTranslations.put("accelerator", "ctrl F");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
