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
 * $Id$
 */

package com.eressea.event;

import java.util.Collection;

import com.eressea.Unit;

import com.eressea.util.CollectionFactory;

/**
 * An event indicating that the orders of a certain unit were modified.
 *
 * @see UnitOrdersListener
 * @see EventDispatcher
 */
public class UnitOrdersEvent extends TimeStampedEvent {
	private Unit unit;

	/** A collection of related units before the unit orders was changed */
	private Collection relatedUnits;

	/**
	 * Creates an event object.
	 *
	 * @param source the object that originated the event.
	 * @param unit the unit which orders changed.
	 */
	public UnitOrdersEvent(Object source, Unit unit) {
		super(source);
		this.unit		  = unit;
		this.relatedUnits = CollectionFactory.createHashSet();
		unit.getRelatedUnits(relatedUnits);
	}

	/**
	 * Returns the unit which orders changed.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * Returns the relates units
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getRelatedUnits() {
		return relatedUnits;
	}
}
