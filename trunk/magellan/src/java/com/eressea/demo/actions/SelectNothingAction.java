// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.util.Iterator;
import java.util.Map;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.demo.Client;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.util.CollectionFactory;

/**
 * @author  Ulrich Küster
 */
public class SelectNothingAction extends MenuAction implements SelectionListener, GameDataListener {
	private Client client;
	private Map selectedRegions = CollectionFactory.createHashtable();

	public SelectNothingAction(Client parent) {
		client = parent;
		client.getDispatcher().addSelectionListener(this);
		client.getDispatcher().addGameDataListener(this);
	}

	public void selectionChanged(SelectionEvent e) {
		if (e.getSource() == this) {
			return;
		}
		if (e.getSelectedObjects() != null && e.getSelectionType() == SelectionEvent.ST_REGIONS) {
			selectedRegions.clear();
			for (Iterator iter = e.getSelectedObjects().iterator(); iter.hasNext(); ) {
				Object o = iter.next();
				if (o instanceof Region) {
					Region r = (Region) o;
					selectedRegions.put(r.getID(), r);
				}
			}
		}
	}

	public void gameDataChanged(GameDataEvent e) {
		selectedRegions.clear();
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		for (Iterator iter = selectedRegions.keySet().iterator(); iter.hasNext(); ) {
			Coordinate c = (Coordinate) iter.next();
			if (c.z == client.getLevel()) {
				iter.remove();
			}
		}
		client.getDispatcher().fire(new SelectionEvent(this, selectedRegions.values(), 
													 null, SelectionEvent.ST_REGIONS));
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
			defaultTranslations.put("name"       , "Select none");
			defaultTranslations.put("mnemonic"   , "n");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
