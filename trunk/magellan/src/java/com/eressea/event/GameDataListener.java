// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.event;


/**
 * The listener interface for receiving game data events. A class
 * interested in game data events implements this interface and
 * registers with an instance of the EventDispatcher class to
 * receive game data events. Game data events are issued when the
 * current game data object becomes invalid e.g. after the user
 * loads a report.
 *
 * @see GameDataEvent
 * @see EventDispatcher
 */
public interface GameDataListener {
	/**
	 * Invoked when the current game data object becomes invalid.
	 */
	public void gameDataChanged(GameDataEvent e);
}
