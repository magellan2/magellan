// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.event;


/**
 * An event indicating that the orders of a certain unit were
 * modified.
 *
 * @see UnitOrdersListener
 * @see EventDispatcher
 */
public class UnitOrdersEvent extends TimeStampedEvent {
	private com.eressea.Unit unit;

	/**
	 * Creates an event object.
	 *
	 * @param source the object that originated the event.
	 * @param unit the unit which orders changed.
	 */
	public UnitOrdersEvent(Object source, com.eressea.Unit unit) {
		super(source);
		this.unit = unit;
	}

	/**
	 * Returns the unit which orders changed.
	 */
	public com.eressea.Unit getUnit() {
		return unit;
	}
}
