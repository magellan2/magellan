// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;



import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.eressea.Faction;
import com.eressea.Island;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

public class IslandNodeWrapper implements CellObject, SupportsClipboard {
	private Island island = null;

	// a static list (will never change) its value over all instances of IslandNodeWrapper
	private static List iconNames = CollectionFactory.singletonList("insel");

	public IslandNodeWrapper(Island island) {
		this.island = island;
	}

	public Island getIsland() {
		return island;
	}

	public String toString() {
		return island.getName();
	}

	public List getIconNames() {
		return iconNames;
	}

	public boolean emphasized() {
		boolean ret = false;
		for (Iterator regionIter = island.regions().iterator(); regionIter.hasNext();) {
			Iterator it = ((Region)regionIter.next()).units().iterator();
			if (it != null) {
				while (it.hasNext()) {
					Unit u = (Unit)it.next();
					if (u.getFaction().trustLevel >= Faction.TL_PRIVILEGED) {
						if (!u.ordersConfirmed) {
							ret = true;
							break;
						}
					}
				}
			}
		}
		return ret;
	}

	public void propertiesChanged() {
	}

	public String getClipboardValue() {
		if (island != null) {
		    return island.getName();
		} else {
		    return toString();
		}
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, String prefix, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
}
