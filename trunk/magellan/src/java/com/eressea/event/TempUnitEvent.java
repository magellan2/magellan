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

package com.eressea.event;

/**
 * An event indicating that a temporary unit was created or deleted.
 *
 * @see TempUnitListener
 * @see EventDispatcher
 */
public class TempUnitEvent extends TimeStampedEvent {
	/** An event indicating that a temp unit was created. */
	public static final int CREATED = 1;

	/** An event indicating that a temp unit was deleted. */
	public static final int DELETED = 2;
	private com.eressea.TempUnit tempUnit = null;
	private int eventType = 0;

	/**
	 * Creates an event object.
	 *
	 * @param source the object that originated the event.
	 * @param temp the temporary unit affected by this event.
	 * @param type specifies whether the temp unit was created or deleted.
	 */
	public TempUnitEvent(Object source, com.eressea.TempUnit temp, int type) {
		super(source);
		this.tempUnit = temp;
		this.eventType = type;
	}

	/**
	 * Returns the temporary unit affected.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public com.eressea.TempUnit getTempUnit() {
		return tempUnit;
	}

	/**
	 * Returns whether the temp unit was created or deleted.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getType() {
		return eventType;
	}
}
