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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.Faction;
import com.eressea.Unit;
import com.eressea.demo.Client;
import com.eressea.event.OrderConfirmEvent;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;

/**
 * DOCUMENT ME!
 *
 * @author Ilja Pavkovic
 */
public class ChangeFactionConfirmationAction extends MenuAction {
	/** TODO: DOCUMENT ME! */
	public static final int SETCONFIRMATION = 0;

	/** TODO: DOCUMENT ME! */
	public static final int REMOVECONFIRMATION = 1;

	/** TODO: DOCUMENT ME! */
	public static final int INVERTCONFIRMATION = 2;
	private Client client;
	private Faction faction;
	private int confirmation; // one of the values above, should be selfexplaining
	private boolean selectedRegionsOnly; // only change confirmation in selected regions

	/**
	 * Creates a new ChangeFactionConfirmationAction object.
	 *
	 * @param c TODO: DOCUMENT ME!
	 * @param f TODO: DOCUMENT ME!
	 * @param conf TODO: DOCUMENT ME!
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public ChangeFactionConfirmationAction(Client c, Faction f, int conf, boolean r) {
		super();
		client = c;

		if(f != null) {
			setName(f.toString());
		}

		faction = f;

		if((conf < 0) || (conf > 2)) {
			throw new IllegalArgumentException();
		}

		confirmation = conf;
		selectedRegionsOnly = r;

		if(selectedRegionsOnly) {
			setName(getName() + " " + Translations.getTranslation(this, "name.postfix.selected"));
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		Collection units = null;

		if(faction == null) {
			if((client.getData() != null) && (client.getData().units() != null)) {
				units = client.getData().units().values();
			}
		} else {
			units = faction.units();
		}

		if(units != null) {
			for(Iterator iter = units.iterator(); iter.hasNext();) {
				Unit unit = (Unit) iter.next();

				if(!unit.isSpy()) {
					// this is slow but ok for this situation (normally one would iterate over the
					// regions and check the containment once per region)
					if(selectedRegionsOnly &&
						   !client.getSelectedRegions().containsKey(unit.getRegion().getID())) {
						continue;
					}

					changeConfirmation(unit);

					// (!) temp units are contained in Faction.units(),
					// but not in GameData.units() (!)
					for(Iterator temps = unit.tempUnits().iterator(); temps.hasNext();) {
						Unit temp = (Unit) temps.next();
						changeConfirmation(temp);
					}
				}
			}

			client.getDispatcher().fire(new OrderConfirmEvent(this,
															  (faction == null)
															  ? client.getData().units().values()
															  : faction.units()));
		}
	}

	private void changeConfirmation(Unit unit) {
		switch(confirmation) {
		case SETCONFIRMATION:
			unit.ordersConfirmed = true;

			break;

		case REMOVECONFIRMATION:
			unit.ordersConfirmed = false;

			break;

		case INVERTCONFIRMATION:
			unit.ordersConfirmed = !unit.ordersConfirmed;

			break;

		default:
			break;
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
			defaultTranslations.put("name", "All units");
			defaultTranslations.put("mnemonic", "a");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
			defaultTranslations.put("name.postfix.selected", "in selected regions only");
		}

		return defaultTranslations;
	}
}
