// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.util;

import java.util.Iterator;
import java.util.Map;

import com.eressea.Alliance;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;

/**
 * @author Ulrich Küster
 * A class providing useful methods on handling factions' trustlevels
 */
public class TrustLevels {

	/**
	 * recalculates the default-trustlevel based on the alliances of all
	 * privileged factions in the given GameData-Object.
	 */
	public static void recalculateTrustLevels(GameData data) {
		if (data.factions() != null) {
			// first reset all trustlevel, that were not set by the user
			// but by Magellan itself to TL_DEFAULT
			for (Iterator iter = data.factions().values().iterator(); iter.hasNext(); ) {
			    Faction f = (Faction)iter.next();
				if (!f.trustLevelSetByUser) {
				    f.trustLevel = Faction.TL_DEFAULT;
				}
			}
			for (Iterator factions = data.factions().values().iterator(); factions.hasNext(); ) {
				Faction f = (Faction)factions.next();
				if (f.password != null && !f.trustLevelSetByUser) {	// password set
				    f.trustLevel = Faction.TL_PRIVILEGED;
				}
				if (f.getID().equals(EntityID.createEntityID(-1))) { // monster
				    if (!f.trustLevelSetByUser) {
						f.trustLevel = -20;
				    }
				} else if (f.getID().equals(EntityID.createEntityID(0))) {  // faction disguised
				    if (!f.trustLevelSetByUser) {
					    f.trustLevel = -100;
				    }

				} else if (f.trustLevel >= Faction.TL_PRIVILEGED && f.allies != null) { // privileged
					Iterator iter = f.allies.entrySet().iterator();
					while(iter.hasNext()) {
						Alliance alliance = (Alliance)((Map.Entry)iter.next()).getValue();
						// update the trustlevel of the allied factions if their
						// trustlevels were not set by the user
						Faction ally = alliance.getFaction();
						if (!ally.trustLevelSetByUser) {
						    ally.trustLevel = Math.max(ally.trustLevel, getTrustLevel(alliance));
						}
					}
				}
			}
		}
	}

	/**
	 * A method to convert an alliance into a trustlevel.
	 * This method should be uses when Magellan calculates trust levels on its own.
	 */
	public static int getTrustLevel(Alliance alliance) {
		int retVal = 0;
		if (alliance.getState(Alliance.ALL)) {
			retVal = 60;
		} else if (alliance.getState(Alliance.GUISE)) {
			retVal = 50;
		} else if (alliance.getState(Alliance.COMBAT)) {
			retVal = 40;
		} else if (alliance.getState(Alliance.GUARD)) {
			retVal = 30;
		} else if (alliance.getState(Alliance.GIVE)) {
			retVal = 20;
		} else if (alliance.getState(Alliance.SILVER)) {
			retVal = 10;
		}
		return retVal;
	}

	/**
	 * determines if the specified gamedata contains trust levels, that were
	 * set by the user explicitly or read from CR (which means the same)
	 */
	public static boolean containsTrustLevelsSetByUser(GameData data) {
	    boolean retVal = false;
		for (Iterator iter = data.factions().values().iterator(); iter.hasNext() && !retVal; ) {
		    if (((Faction)iter.next()).trustLevelSetByUser) {
			    retVal = true;
		    }
		}
		return retVal;
	}
}
