// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.GameData;
import com.eressea.IntegerID;
import com.eressea.Island;
import com.eressea.Region;
import com.eressea.Rules;
import com.eressea.StringID;
import com.eressea.util.logging.Logger;

/**
 * A class offering common operations on islands and regions.
 */
public class Islands {
	private final static Logger log = Logger.getInstance(Islands.class);

	/**
	 * Retrieve all islands formed by a collection of regions.
	 *
	 * @param rules the rules to retrieve the ocean region type from.
	 * @param regions the regions the islands consist of.
	 * @param islands islands contained in this map are regarded as
	 *  already existing. They can be expanded or merged, depending
	 *  on the regions supplied. To indicate that no islands are
	 *  known supply an empty map or null.
	 * @param data the game data as required by the Island constructor.
	 */
	public static Map getIslands(Rules rules, Map regions, Map islands, GameData data) {
		if (regions == null || regions.size() == 0) {
			return CollectionFactory.createHashtable();
		}
		if (islands == null) {
			islands = CollectionFactory.createHashtable();
		}
		Map unassignedPool = CollectionFactory.createHashtable();
		unassignedPool.putAll(regions);
		
		// completely update all known islands
		for (Iterator iter = islands.values().iterator(); iter.hasNext();) {
			Island curIsland = (Island)iter.next();
			Collection oldRegions = curIsland.regions();
			if (oldRegions.size() > 0) {
				Map islandRegions = getIsland(rules, unassignedPool, (Region)oldRegions.iterator().next());
				for (Iterator it = islandRegions.values().iterator(); it.hasNext();) {
					Region curRegion = (Region)it.next();
					curRegion.setIsland(curIsland);
					unassignedPool.remove(curRegion.getID());
				}
			}
		}
		
		// assign new islands to remaining regions
		IntegerID newID = IntegerID.create(0);
		while (unassignedPool.size() > 0) {
			Region curRegion = (Region)unassignedPool.remove(unassignedPool.keySet().iterator().next());
			Map islandRegions = getIsland(rules, unassignedPool, curRegion);
			
			if (islandRegions.size() > 0) {
				while (islands.containsKey(newID)) {
					newID = IntegerID.create(newID.intValue() + 1);
				}
				Island curIsland = new Island(newID, data);
				curIsland.setName(newID.toString());
				islands.put(newID, curIsland);
				
				for (Iterator it = islandRegions.values().iterator(); it.hasNext();) {
					Region ir = (Region)it.next();
					ir.setIsland(curIsland);
					unassignedPool.remove(ir.getID());
				}
			}
		}
		
		return islands;
	}
	
	/**
	 * Get all regions belonging the same island as the region r.
	 *
	 * @param regions all regions that could possibly belong to the
	 *  island. 
	 * @param r a region forming an island with its neighbouring
	 *  regions.
	 * @return a map containing all regions that can be reached from
	 *  region r via any number of regions that are not of type ocean.
	 */
	
	public static Map getIsland(Rules rules, Map regions, Region r) {
		Map checked = CollectionFactory.createHashtable();
		Map unchecked = CollectionFactory.createHashtable();
		com.eressea.rules.RegionType oceanType = rules.getRegionType(StringID.create("Ozean"));
		if (oceanType == null) {
			log.warn("Islands.getIsland(): unable to determine the ocean region type!");
			return null;
		}
		Map excludedRegionTypes = CollectionFactory.createHashtable();
		excludedRegionTypes.put(oceanType.getID(), oceanType);
		
		if (!r.getType().equals(oceanType)) {
			unchecked.put(r.getID(), r);
		}
		
		while (unchecked.size() > 0) {
			Region currentRegion = (Region)unchecked.remove(unchecked.keySet().iterator().next());
			checked.put(currentRegion.getID(), currentRegion);
			
			Map neighbours = Regions.getAllNeighbours(regions, currentRegion.getCoordinate(), 1, excludedRegionTypes);
			for (Iterator iter = neighbours.values().iterator(); iter.hasNext();) {
				Region neighbour = (Region)iter.next();
				if (!checked.containsKey(neighbour.getID())) {
					unchecked.put(neighbour.getID(), neighbour);
				}
			}
		}
		
		return checked;
	}
}
