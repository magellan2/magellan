package com.eressea.gamebinding;

import com.eressea.gamebinding.eressea.EresseaSpecificStuff;
import com.eressea.util.logging.Logger;

public class GameSpecificStuffProvider {
	private final static Logger log = Logger.getInstance(GameSpecificStuffProvider.class);

	public GameSpecificStuffProvider() {
	}

	/**
	 * Returns the GameSpecificStuff object for the given game name
	 */ 
	public GameSpecificStuff getGameSpecificStuff(String aName) {

		log.warn("Unable to determine GameSpecificStuff for name '"+aName+"'. Falling back to eressea");
		return new EresseaSpecificStuff();
	}

}
