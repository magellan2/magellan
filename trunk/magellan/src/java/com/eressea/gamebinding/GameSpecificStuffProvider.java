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

package com.eressea.gamebinding;

import com.eressea.gamebinding.eressea.EresseaSpecificStuff;

import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GameSpecificStuffProvider {
	private static final Logger log = Logger.getInstance(GameSpecificStuffProvider.class);

	/**
	 * Creates a new GameSpecificStuffProvider object.
	 */
	public GameSpecificStuffProvider() {
	}

	/**
	 * Returns the GameSpecificStuff object for the given game name
	 *
	 * @param aName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public GameSpecificStuff getGameSpecificStuff(String aName) {
		log.warn("Unable to determine GameSpecificStuff for name '" + aName +
				 "'. Falling back to eressea");

		return new EresseaSpecificStuff();
	}
}
