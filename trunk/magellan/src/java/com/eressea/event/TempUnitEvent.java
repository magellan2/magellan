// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.event;


/**
 * An event indicating that a temporary unit was created or deleted.
 *
 * @see TempUnitListener
 * @see EventDispatcher
 */
public class TempUnitEvent extends TimeStampedEvent {
	/** An event indicating that a temp unit was created. */
	public final static int CREATED = 1;
	/** An event indicating that a temp unit was deleted. */
	public final static int DELETED = 2;
	private com.eressea.TempUnit tempUnit = null;
	private int eventType = 0;
	
	/**
	 * Creates an event object.
	 *
	 * @param source the object that originated the event.
	 * @param Unit the temporary unit affected by this event.
	 * @param type specifies whether the temp unit was created or
	 * deleted.
	 */
	public TempUnitEvent(Object source, com.eressea.TempUnit temp, int type) {
		super(source);
		this.tempUnit = temp;
		this.eventType = type;
	}

	/**
	 * Returns the temporary unit affected.
	 */
	public com.eressea.TempUnit getTempUnit() {
		return tempUnit;
	}
	
	/**
	 * Returns whether the temp unit was created or deleted.
	 */
	public int getType() {
		return eventType;
	}
}
