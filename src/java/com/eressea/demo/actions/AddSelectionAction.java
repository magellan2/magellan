/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
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

import java.util.Iterator;
import java.util.Map;

import com.eressea.Region;

import com.eressea.demo.Client;

import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;

import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Ilja Pavkovic
 */
public class AddSelectionAction extends OpenSelectionAction
	implements SelectionListener
{
	/**
	 * Creates a new AddSelectionAction object.
	 *
	 * @param client TODO: DOCUMENT ME!
	 */
	public AddSelectionAction(Client client) {
		super(client);
		client.getDispatcher().addSelectionListener(this);
	}

	protected void preSetCleanSelection() {
		// adding does not clean selectedRegion
		// System.out.println("do not clean selection");
	}

	protected String getPropertyName() {
		return "Client.lastSELAdded";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		if(e.getSource() == this) {
			return;
		}

		if((e.getSelectedObjects() != null) &&
			   (e.getSelectionType() == SelectionEvent.ST_REGIONS)) {
			selectedRegions.clear();

			for(Iterator iter = e.getSelectedObjects().iterator();
					iter.hasNext();) {
				Object o = iter.next();

				if(o instanceof Region) {
					Region r = (Region) o;
					selectedRegions.put(r.getID(), r);
				}
			}
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
			defaultTranslations.put("name", "Add selection...");
			defaultTranslations.put("mnemonic", "d");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
			defaultTranslations.put("title", "add map selection file");
		}

		return defaultTranslations;
	}
}
