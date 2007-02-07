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

package com.eressea.tasks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eressea.CoordinateID;
import com.eressea.Region;
import com.eressea.Rules;
import com.eressea.Ship;
import com.eressea.rules.RegionType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Direction;
import com.eressea.util.Regions;
import com.eressea.util.Translations;

/**
 * This class inspects ships and checks for overload, missing crew and bad routes.
 * 
 */
public class ShipInspector extends AbstractInspector implements Inspector {

	/** The singleton instance of the ShipInspector */
	public static final ShipInspector INSPECTOR = new ShipInspector();

	/**
	 * Returns an instance of ShipInspector.
	 * 
	 * @return The singleton instance of ShipInspector
	 */
	public static ShipInspector getInstance() {
		return INSPECTOR;
	}

	protected ShipInspector() {
	}

	/**
	 * Reviews the region for ships with problems. 
	 * 
	 * @see com.eressea.tasks.AbstractInspector#reviewRegion(com.eressea.Region, int)
	 */
	public List reviewRegion(Region r, int type) {
		// we notify errors only
		if (type != Problem.ERROR) {
			return Collections.EMPTY_LIST;
		}

		// fail fast if prerequisites are not fulfilled
		if ((r == null) || (r.units() == null) || r.units().isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		// this inspector is only interested in ships
		if ((r.ships() == null) || r.ships().isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		List problems = reviewShips(r);

		if (problems.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			return problems;
		}
	}

	private List reviewShips(Region r) {
		List problems = CollectionFactory.createArrayList(2);

		for (Iterator iter = r.ships().iterator(); iter.hasNext();) {
			Ship s = (Ship) iter.next();
			problems.addAll(reviewShip(s));
		}

		return problems;
	}

	private List reviewShip(Ship s) {
		List problems = CollectionFactory.createArrayList();
		int nominalShipSize = s.getShipType().getMaxSize();

		if (s.size != nominalShipSize) {
			// ship will be built, so dont review ship
			return Collections.EMPTY_LIST;
		}

		if (s.modifiedUnits().isEmpty()) {
			problems.add(new CriticizedError(s.getRegion(), s, this,
					getString("error.nocrew.description")));
		}

		if (s.getModifiedLoad() > (s.getMaxCapacity())) {
			problems.add(new CriticizedError(s.getRegion(), s, this,
					getString("error.overloaded.description")));
		}

		problems.addAll(reviewMovingShip(s));
		return problems;
	}

	private List reviewMovingShip(Ship s) {
		List problems = CollectionFactory.createArrayList();
		if (s.getOwnerUnit() == null) {
			return problems;
		}

		List modifiedMovement = s.getOwnerUnit().getModifiedMovement();

		if (modifiedMovement.isEmpty()) {
			return problems;
		}

		Iterator movementIterator = modifiedMovement.iterator();
		movementIterator.next();
		CoordinateID nextRegionCoord = (CoordinateID) movementIterator.next();
		Region nextRegion = s.getRegion().getData().getRegion(nextRegionCoord);

		Rules rules = s.getData().rules;
		RegionType ebene = rules.getRegionType("Ebene"), wald = rules.getRegionType("Wald"), ozean = rules
				.getRegionType("Ozean");

		// TODO: We should consider harbours, too. But this is difficult because we don't know if
		// harbour owner is allied with ship owner etc. We better leave it up to the user to decide...
		if (s.shoreId != -1) {
			// If ship is shored, it can only move deviate by one from the shore direction and only
			// move to an ocean region
			Direction d = (Direction) Regions.getDirectionObjectsOfCoordinates(modifiedMovement)
					.get(0);

			if (Math.abs(s.shoreId - d.getDir()) > 1 && Math.abs(s.shoreId - d.getDir()) < 5) {
				problems.add(new CriticizedError(s.getRegion(), s, this,
						getString("error.wrongshore.description")));
				return problems;
			}
			if (!nextRegion.getRegionType().equals(ozean)) {
				problems.add(new CriticizedError(s.getRegion(), s, this,
						getString("error.noocean.description")));
				return problems;
			}
			if (movementIterator.hasNext()) {
				nextRegionCoord = (CoordinateID) movementIterator.next();
				nextRegion = s.getRegion().getData().getRegion(nextRegionCoord);
			} else
				nextRegion = null;
		}

		while (nextRegion != null) {
			// if ship is not a boat and on ocean , it can only move to ocean, plain or forest
			if (!(s.getType().equals(rules.getShipType("Boot")) || (nextRegion.getRegionType()
					.equals(ozean)
					|| nextRegion.getRegionType().equals(wald) || nextRegion.getRegionType()
					.equals(ebene)))) {
				problems.add(new CriticizedError(s.getRegion(), s, this,
						getString("error.shipwreck.description")));
				return problems;
			}
			if (movementIterator.hasNext()) {
				nextRegionCoord = (CoordinateID) movementIterator.next();
				nextRegion = s.getRegion().getData().getRegion(nextRegionCoord);
			} else
				nextRegion = null;
		}

		return problems;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * Returns a translation from the translation table for the specified key.
	 * 
	 * @param key 
	 * 
	 * @return The translation of key, if found, or null
	 */
	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}

	/**
	 * Returns a map of keys to translations.
	 * 
	 * @return A map of keys to translations
	 */
	public static synchronized Map getDefaultTranslations() {
		if (defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();

			defaultTranslations.put("error.wrongshore.description",
					"Ship cannot move to this direction.");
			defaultTranslations
					.put("error.noocean.description", "Ships can only cast of to oceans.");
			defaultTranslations.put("error.shipwreck.description", "Possible shipwreck.");
			defaultTranslations.put("error.nocrew.description", "Ship has no crew.");
			defaultTranslations.put("error.overloaded.description", "Ship is overloaded.");
		}

		return defaultTranslations;
	}

}
