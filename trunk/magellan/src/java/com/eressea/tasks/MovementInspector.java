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

package com.eressea.tasks;

import java.util.Collections;
import java.util.List;

import com.eressea.Unit;

import com.eressea.util.CollectionFactory;

/**
 */
public class MovementInspector extends AbstractInspector implements Inspector {
	/** TODO: DOCUMENT ME! */
	public static final MovementInspector INSPECTOR = new MovementInspector();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static MovementInspector getInstance() {
		return INSPECTOR;
	}

	protected MovementInspector() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewUnit(Unit u, int type) {
		if((u == null) || u.ordersAreNull()) {
			return Collections.EMPTY_LIST;
		}

		// we only warn
		if(type != Problem.WARNING) {
			return Collections.EMPTY_LIST;
		}

		List problems = CollectionFactory.createArrayList();

		if(!u.getModifiedMovement().isEmpty()) {
			// only test for foot/horse movement if unit is not owner of a modified ship
			if((u.getModifiedShip() == null) || !u.equals(u.getModifiedShip().getOwnerUnit())) {
				problems.addAll(reviewUnitOnFoot(u));
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

		if(maxOnFoot == Unit.CAP_UNSKILLED) {
			return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
																		 "Foot movement too many horses!"));
		}

		int modLoad = u.getModifiedLoad();

		if((maxOnFoot - modLoad) < 0) {
			return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
																		 "Foot movement overloaded!"));
		}

		return Collections.EMPTY_LIST;
	}

	private List reviewUnitOnHorse(Unit u) {
		int maxOnHorse = u.getPayloadOnHorse();

		if(maxOnHorse == Unit.CAP_UNSKILLED) {
			return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
																		 "Horse movement too many horses!"));
		}

		if(maxOnHorse != Unit.CAP_NO_HORSES) {
			int modLoad = u.getModifiedLoad();

			if((maxOnHorse - modLoad) < 0) {
				return CollectionFactory.singletonList(new CriticizedWarning(u, u, this,
																			 "Horse movement overloaded!"));
			}
		}

		return Collections.EMPTY_LIST;
	}
}
