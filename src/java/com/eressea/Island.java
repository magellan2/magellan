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

package com.eressea;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.util.CollectionFactory;
import com.eressea.util.ROCollection;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Island extends DescribedObject {
	/**
	 * The game data required to construct the collection of regions belonging
	 * to this island.
	 */
	private GameData data = null;

	/**
	 * Constructs a new Island object uniquely identifiable by the specified
	 * id.
	 *
	 * @param id allows this island to return all region objects that belong to
	 * 		  it via the<tt>regions()</tt> and <tt>getRegion()</tt> methods.
	 * @param data TODO: DOCUMENT ME!
	 */
	public Island(ID id, GameData data) {
		super(id);
		this.data = data;
	}

	/**
	 * Indicates whether this Island object is equal to another object. Returns
	 * true only if o is not null and an instance of class Island and o's id
	 * is equal to the id of this  Island object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		if(o instanceof Island) {
			return this.getID().equals(((Island) o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on Island objects equivalent to the natural
	 * ordering of their ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Island) o).getID());
	}

	/**
	 * Returns a String representation of this Island object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return name + " (ID: " + id + ")";
	}

	/**
	 * Contains all regions that are in this region. This map is updated
	 * lazily, i.e. this container is notified every time an object is added
	 * or removed and it refreshes that map only when it is accessed from
	 * outside. This strategy has two major advantages over one drawback. On
	 * the one hand, it reduces memory consumption, as the map is only
	 * allocated with data when acually needed. Since the map is used like a
	 * cache, the computational overhead is acceptable. Furthermore, the
	 * complexity of merging to game data objects is significantly reduced
	 * because there are no more consistency  issues between such maps and the
	 * contained objects referring to this container. On the other hand, there
	 * is basically no code re-use, which could be realized through
	 * inheritance (not applicable here) or by using a generic data structure,
	 * which itself has again the disadvantage of lacking type safety. Also,
	 * without wrapping it there is again the problem of either having to deal
	 * with null values outside this class or increased memory consumption by
	 * always allocating this data structure.
	 */
	private Map regions = null;

	/** Indicates that the current container map has to be refreshed. */
	private boolean regionsInvalidated = true;

	/**
	 * Returns an unmodifiable collection of all the regions in this container.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection regions() {
		if(regionsInvalidated) {
			refreshRegions();
		}

		if(regions != null) {
			return new ROCollection(regions.values());
		} else {
			return ROCollection.EMPTY_COLLECTION;
		}
	}

	/**
	 * Retrieve a region in this container by id.
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Region getRegion(ID id) {
		if(regionsInvalidated) {
			refreshRegions();
		}

		if(regions != null) {
			return (Region) regions.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Informs this container that a region was added or removed. It is
	 * mandatory that this function is called every time a region is added or
	 * removed from this container for keeping the objects returned by the
	 * getRegion() and regions() methods consistent.
	 */
	public void invalidateRegions() {
		regionsInvalidated = true;
	}

	/**
	 * Recreates the map of regions in this container. This function is called
	 * every time the collection of regions is accessed and the
	 * regionsInvalidated variable is true.
	 */
	private void refreshRegions() {
		if(regions != null) {
			regions.clear();
		}

		if(data.regions() != null) {
			for(Iterator iter = data.regions().values().iterator();
					iter.hasNext();) {
				Region r = (Region) iter.next();

				if(this.equals(r.getIsland())) {
					if(regions == null) {
						regions = CollectionFactory.createHashtable();
					}

					regions.put(r.getID(), r);
				}
			}
		}

		regionsInvalidated = false;
	}

	/**
	 * Merges island.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curIsland TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newIsland TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Island curIsland, GameData newGD,
							 Island newIsland) {
		if(curIsland.getName() != null) {
			newIsland.setName(curIsland.getName());
		}

		if(curIsland.getDescription() != null) {
			newIsland.setDescription(curIsland.getDescription());
		}

		newIsland.invalidateRegions();
	}
}
