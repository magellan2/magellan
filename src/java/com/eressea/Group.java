// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.util.CollectionFactory;
import com.eressea.util.ExternalTagMap;
import com.eressea.util.ROCollection;

/**
 * A class representing a group of units within a faction.
 */
public class Group extends NamedObject {
	private Faction faction = null;
	private Map allies      = CollectionFactory.createOrderedHashtable();
	private GameData data   = null;

	private static ExternalTagMap externalMap = null; // Map for external tags

	/**
	 * Create a new <tt>Group</tt> object.
	 *
	 * @param id the id of this group.
	 * @param data the game data this group belongs to.
	 */
	public Group(ID id, GameData data) {
		this(id, data, null, null);
	}

	/**
	 * Create a new <tt>Group</tt> object.
	 *
	 * @param id the id of this group.
	 * @param data the game data this group belongs to.
	 * @param name the name of this group.
	 */
	public Group(ID id, GameData data, String name) {
		this(id, data, name, null);
	}

	/**
	 * Create a new <tt>Group</tt> object.
	 *
	 * @param id the id of this group.
	 * @param data the game data this group belongs to.
	 * @param name the name of this group.
	 * @param faction the faction this group belongs to.
	 */
	public Group(ID id, GameData data, String name, Faction faction) {
		super(id);
		this.data = data;
		this.setName(name);
		this.faction = faction;
	}

	/**
	 * Set the faction this group belongs to.
	 */
	public void setFaction(Faction f) {
		this.faction = f;
	}

	/**
	 * Get the faction this group belongs to.
	 */
	public Faction getFaction() {
		return faction;
	}

	/**
	 * The alliances specific to this group. The map returned by this
	 * function contains <tt>ID</tt> objects as keys with the id
	 * of the faction that alliance references. The values are
	 * instances of class <tt>Alliance</tt>. The return value is
	 * never null.
	 */
	public Map allies() {
		return allies;
	}

	/**
	 * A group dependent prefix to be prepended to this faction's
	 * race name.
	 */
	private String raceNamePrefix = null;

	/**
	 * Sets the group dependent prefix for the race name.
	 */
	public void setRaceNamePrefix(String prefix) {
		this.raceNamePrefix = prefix;
	}

	/**
	 * Returns the group dependent prefix for the race name.
	 */
	public String getRaceNamePrefix() {
		return this.raceNamePrefix;
	}

	// units are sorted in unit containers with this index
	private int sortIndex = -1;

	/**
	 * Sets an index indicating how instances of class are sorted in
	 * the report.
	 */
	public void setSortIndex(int index) {
		this.sortIndex = index;
	}

	/**
	 * Returns an index indicating how instances of class are sorted in
	 * the report.
	 */
	public int getSortIndex() {
		return sortIndex;
	}



	/**
	 * All units that are in this container.
	 */
	private Map units = null;

	/**
	 * Provides a collection view of the unit map.
	 */
	private Collection unitCollection = null;

	/**
	 * Returns an unmodifiable collection of all the units in this
	 * container.
	 */
	public Collection units() {
		if (unitCollection == null) {
			unitCollection = new ROCollection(units);
		}
		return unitCollection;
	}

	/**
	 * Retrieve a unit in this container by id.
	 */
	public Unit getUnit(ID id) {
		if (units != null) {
			return (Unit)units.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Adds a unit to this container. This method should only be
	 * invoked by Unit.setXXX() methods.
	 */
	void addUnit(Unit u) {
		if (units == null) {
			units = CollectionFactory.createHashtable();
			/* enforce the creation of a new collection view */
			unitCollection = null;
		}
		units.put(u.getID(), u);
	}

	/**
	 * Removes a unit from this container. This method should only be
	 * invoked by Unit.setXXX() methods.
	 */
	Unit removeUnit(ID id) {
		if (units != null) {
			Unit u = (Unit)units.remove(id);
			if (units.isEmpty()) {
				units = null;
			}
			return u;
		} else {
			return null;
		}
	}



	/**
	 * Returns a String representation of this group object.
	 */
	public String toString() {
		return name + " (" + id + ")";
	}

	/**
	 * Indicates whether this Group object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class Group and o's id is equal to the id of this
	 * Group object.
	 */
	public boolean equals(Object o) {
		if (o instanceof Group) {
			return this.getID().equals(((Group)o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on Group objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Group)o).getID());
	}

	/**
	 * Transfers all available information from the current group
	 * to the new one.
	 *
	 * @param curGD fully loaded game data
	 * @param curGroup a fully initialized and valid group
	 * @param newGD the game data to be updated
	 * @param newGroup a group to be updated with the data from
	 * curGroup
	 */
	public static void merge(GameData curGD, Group curGroup, GameData newGD, Group newGroup) {
		if (curGroup.getName() != null) {
			newGroup.setName(curGroup.getName());
		}
		if (curGroup.allies != null && curGroup.allies.size() > 0) {
			if (newGroup.allies == null) {
				newGroup.allies = CollectionFactory.createHashtable();
			} else {
				newGroup.allies.clear();
			}
			for (Iterator iter = curGroup.allies.values().iterator(); iter.hasNext();) {
				Alliance alliance = (Alliance)iter.next();
				Faction ally = newGD.getFaction(alliance.getFaction().getID());
				newGroup.allies.put(ally.getID(), new Alliance(ally, newGD.rules, alliance.getState()));
			}
		}
		if (curGroup.getFaction() != null) {
			newGroup.setFaction(newGD.getFaction(curGroup.getFaction().getID()));
		}
		newGroup.sortIndex = Math.max(newGroup.sortIndex, curGroup.sortIndex);
		if (curGroup.raceNamePrefix != null) {
			newGroup.raceNamePrefix = curGroup.raceNamePrefix;
		}
	}

	// EXTERNAL TAG METHODS
	public String putTag(String tag,String value) {
		if (externalMap==null)
			externalMap=new ExternalTagMap();
		return externalMap.putTag(this.getID(),tag,value);
	}

	public String getTag(String tag) {
		if (externalMap==null)
			return null;
		return externalMap.getTag(this.getID(),tag);
	}

	public String removeTag(String tag) {
		if (externalMap==null)
			return null;
		return externalMap.removeTag(this.getID(),tag);
	}

	public boolean containsTag(String tag) {
		if (externalMap==null)
			return false;
		return externalMap.containsTag(this.getID(),tag);
	}

	public Map getTagMap() {
		if (externalMap==null)
			externalMap=new ExternalTagMap();
		return externalMap.getTagMap(this.getID(),true);
	}

	public boolean hasTags() {
		if (externalMap==null)
			return false;
		return externalMap.getTagMap(this.getID(),false)!=null;
	}
}
