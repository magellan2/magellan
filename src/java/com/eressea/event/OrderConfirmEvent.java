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

package com.eressea.event;

import java.util.Collection;

/**
 * An event indicating that the order confirmation status of one or more units has changed.
 *
 * @see OrderConfirmListener
 * @see EventDispatcher
 */
public class OrderConfirmEvent extends TimeStampedEvent {
	private Collection units;

	/**
	 * Constructs a new order confirmation event.
	 *
	 * @param source the object issuing the event.
	 * @param units the units which order confirmation status was modified.
	 */
	public OrderConfirmEvent(Object source, Collection units) {
		super(source);
		this.units = units;
	}

	/**
	 * Returns the units affected by the event.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getUnits() {
		return units;
	}
}
