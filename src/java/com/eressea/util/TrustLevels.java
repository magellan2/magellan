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

package com.eressea.util;

import java.util.Iterator;
import java.util.Map;

import com.eressea.Alliance;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster A class providing useful methods on handling factions'
 * 		   trustlevels
 */
public class TrustLevels {
	/**
	 * recalculates the default-trustlevel based on the alliances of all
	 * privileged factions in the given GameData-Object.
	 *
	 * @param data TODO: DOCUMENT ME!
	 */
	public static void recalculateTrustLevels(GameData data) {
		if(data.factions() != null) {
			// first reset all trustlevel, that were not set by the user
			// but by Magellan itself to TL_DEFAULT
			for(Iterator iter = data.factions().values().iterator();
					iter.hasNext();) {
				Faction f = (Faction) iter.next();

				if(!f.trustLevelSetByUser) {
					f.trustLevel = Faction.TL_DEFAULT;
				}
			}

			for(Iterator factions = data.factions().values().iterator();
					factions.hasNext();) {
				Faction f = (Faction) factions.next();

				if((f.password != null) && !f.trustLevelSetByUser) { // password set
					f.trustLevel = Faction.TL_PRIVILEGED;
				}

				if(f.getID().equals(EntityID.createEntityID(-1))) { // monster

					if(!f.trustLevelSetByUser) {
						f.trustLevel = -20;
					}
				} else if(f.getID().equals(EntityID.createEntityID(0))) { // faction disguised

					if(!f.trustLevelSetByUser) {
						f.trustLevel = -100;
					}
				} else if((f.trustLevel >= Faction.TL_PRIVILEGED) &&
							  (f.allies != null)) { // privileged

					Iterator iter = f.allies.entrySet().iterator();

					while(iter.hasNext()) {
						Alliance alliance = (Alliance) ((Map.Entry) iter.next()).getValue();

						// update the trustlevel of the allied factions if their
						// trustlevels were not set by the user
						Faction ally = alliance.getFaction();

						if(!ally.trustLevelSetByUser) {
							ally.trustLevel = Math.max(ally.trustLevel,
													   alliance.getTrustLevel());
						}
					}
				}
			}
		}

		data.postProcessAfterTrustlevelChange();
	}

	/**
	 * determines if the specified gamedata contains trust levels, that were
	 * set by the user explicitly or read from CR (which means the same)
	 *
	 * @param data TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean containsTrustLevelsSetByUser(GameData data) {
		for(Iterator iter = data.factions().values().iterator();
				iter.hasNext();) {
			if(((Faction) iter.next()).trustLevelSetByUser) {
				return true;
			}
		}

		return false;
	}
}
