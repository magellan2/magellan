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
import java.util.List;
import java.util.Map;


/**
 * A class for caching data that is time consuming to compute or
 * wasteful to allocate but frequently needed. Objects of this
 * type are available in units and in all UnitContainer subclasses.
 *
 * If fields are added, please comment on where the field is used and
 * with wich scope!
 */
public class Cache {
	private Collection handlers = null;
	
	// used in swing.completion.* classes per unit
	public com.eressea.swing.completion.OrderEditor orderEditor = null;
	// used in swing.map.RegionImageCellRenderer per region
	// public int fogOfWar = -1;

	// used in Unit and UnitContainer for relations between or to units
	public Collection relations = null;
	
	// used in Unit for skills after person transfers and recruiting
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// IMPORTANT: do not modify this thing (except for assignments)
	// since it may point to the Unit.skills map!!
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public Map modifiedSkills = null;
	
	public Map modifiedItems = null;
	public int unitWeight = -1;
	public int modifiedUnitWeight = -1;
	public int modifiedPersons = -1;
	
	// used in UnitContainer
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// IMPORTANT: do not modify this thing (except for assignments)
	// since it may point to the UnitContainer.units map!!
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public Map modifiedContainerUnits = null;
	public Map regionItems = null;

	// Used in Unit (FIXME(pavkovic): right now used in PathCellRenderer) to store
	// movement information extracted from travelThru (-Ship) and faction messages
	public List movementPath   = null;
	public Boolean movementPathIsPassive = null;

	
	public void addHandler(CacheHandler h) {
		if (handlers == null) {
			handlers = CollectionFactory.createLinkedList();
		}
		handlers.add(h);
	}
	
	public void removeHandler(CacheHandler h) {
		if (handlers != null) {
			handlers.remove(h);
		}
	}
	
	public void clear() {
		if (handlers != null) {
			for (Iterator iter = handlers.iterator(); iter.hasNext();) {
				CacheHandler h = (CacheHandler)iter.next();
				h.clearCache(this);
			}
		}
		orderEditor = null;
		// fogOfWar = -1;
		if (relations != null) {
			relations.clear();
			relations = null;
		}
		modifiedSkills = null;
		if (modifiedItems != null) {
			modifiedItems.clear();
			modifiedItems = null;
		}
		unitWeight = -1;
		modifiedUnitWeight = -1;
		modifiedPersons = -1;
		modifiedContainerUnits = null;
		if (regionItems != null) {
			regionItems.clear();
			regionItems = null;
		}

		movementPath = null;
		movementPathIsPassive = null;
	}
}
