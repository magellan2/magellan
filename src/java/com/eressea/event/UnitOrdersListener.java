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

/**
 * The listener interface for receiving unit orders events. A class interested in unit orders
 * events implements this interface and registers with an instance of the EventDispatcher class to
 * receive such events. Unit orders events are issued when the orders of a unit are modified.
 *
 * @see GameDataEvent
 * @see EventDispatcher
 */
public interface UnitOrdersListener {
	/**
	 * Invoked when the orders of a unit are modified.
	 */
	public void unitOrdersChanged(UnitOrdersEvent e);
}
