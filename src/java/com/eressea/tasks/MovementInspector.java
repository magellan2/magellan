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
import java.util.List;
import java.util.Map;

import com.eressea.Unit;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;

/**
 * Checks land movement for overload or too many horses.
 * 
 */
public class MovementInspector extends AbstractInspector implements Inspector {
	/** The singelton instance of this Inspector */
	public static final MovementInspector INSPECTOR = new MovementInspector();

	/**
	 * Returns an instance this inspector.
	 * 
	 * @return The singleton instance of MovementInspector
	 */
	public static MovementInspector getInstance() {
		return INSPECTOR;
	}

	protected MovementInspector() {
	}

	/**
	 * Checks the specified movement for overload and too many horses.
	 * 
	 * @see com.eressea.tasks.AbstractInspector#reviewUnit(com.eressea.Unit, int)
	 */
	public List reviewUnit(Unit u, int type) {
		if ((u == null) || u.ordersAreNull()) {
			return Collections.EMPTY_LIST;
		}

		// we only warn
		if (type != Problem.WARNING) {
			return Collections.EMPTY_LIST;
		}

		List problems = CollectionFactory.createArrayList();

		if (!u.getModifiedMovement().isEmpty()) {
			// only test for foot/horse movement if unit is not owner of a modified ship
			if ((u.getModifiedShip() == null) || !u.equals(u.getModifiedShip().getOwnerUnit())) {
				problems.addAll(reviewUnitOnFoot(u));
				if (u.getModifiedMovement().size() > 2)
					problems.addAll(reviewUnitOnHorse(u));
			}
		}

		// TODO: check for movement length
		// TODO: check for roads

		/*
		switch(u.getRadius()) {
		case 0:
		    problems.add(new CriticizedWarning(u, this, "Cannot move, radius is on "+u.getRadius()+"!"));
		case 1:
		    problems.add(new CriticizedWarning(u, this, "Cannot ride, radius is on "+u.getRadius()+"!"));
		default:
		    ;
		}
		*/
		if(problems.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			return problems;
		}
	}

	private List reviewUnitOnFoot(Unit u) {
		int maxOnFoot = u.getPayloadOnFoot();

		if (maxOnFoot == Unit.CAP_UNSKILLED) {
			return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
					getString("error.toomanyhorsesfoot.description")));
		}

		int modLoad = u.getModifiedLoad();

		if ((maxOnFoot - modLoad) < 0) {
			return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
					getString("error.footoverloaded.description")));
		}

		return Collections.EMPTY_LIST;
	}

	private List reviewUnitOnHorse(Unit u) {
		int maxOnHorse = u.getPayloadOnHorse();

		if (maxOnHorse == Unit.CAP_UNSKILLED) {
			return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
					getString("error.toomanyhorsesride.description")));
		}

		if (maxOnHorse != Unit.CAP_NO_HORSES) {
			int modLoad = u.getModifiedLoad();

			if ((maxOnHorse - modLoad) < 0) {
				return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
						getString("error.horseoverloaded.description")));
			}
		}

		return Collections.EMPTY_LIST;
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

			defaultTranslations.put("error.toomanyhorsesfoot.description",
					"Foot movement: too many horses!");
			defaultTranslations.put("error.footoverloaded.description", "Foot movement: overloaded!");
			defaultTranslations.put("error.toomanyhorsesride.description",
					"Horse movement: too many horses!");
			defaultTranslations
					.put("error.horseoverloaded.description", "Horse movement: overloaded!");
		}

		return defaultTranslations;
	}

}
