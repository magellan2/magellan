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
 * This class maps game names to <code>GameSpecicifStuff</code> objects
 *
 * @author $author$
 * @version $Revision$
 */
public class GameSpecificStuffProvider {
	private static final Logger log = Logger.getInstance(GameSpecificStuffProvider.class);

	/**
	 * Returns the GameSpecificStuff object for the given game name
	 *
	 * @param aName the name of the game to load
	 *
	 * @return a GameSpecificStuff object based on the given game name
	 */
	public GameSpecificStuff getGameSpecificStuff(String aName) {
		if("eressea".equalsIgnoreCase(aName)) {
			return new EresseaSpecificStuff();
		}

		log.warn("Unable to determine GameSpecificStuff for name '" + aName +
				 "'. Falling back to eressea");

		return new EresseaSpecificStuff();
	}
}
