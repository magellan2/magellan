/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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
import java.util.List;
import java.util.Map;

import com.eressea.relation.EnterRelation;
import com.eressea.relation.LeaveRelation;
import com.eressea.relation.UnitContainerRelation;
import com.eressea.relation.UnitRelation;
import com.eressea.rules.CastleType;
import com.eressea.rules.RegionType;
import com.eressea.rules.UnitContainerType;
import com.eressea.util.Cache;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Sorted;
import com.eressea.util.TagMap;
import com.eressea.util.Taggable;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public abstract class UnitContainer extends RelatedObject implements Sorted,
																	   Taggable
{
	private static final Logger log = Logger.getInstance(UnitContainer.class);
	private UnitContainerType type = null;
	private Unit owner = null;

	/**
	 * A list containing <tt>String</tt> objects, specifying  effects on this
	 * <tt>UnitContainer</tt> object.
	 */
	public List effects = null;

	// hm, could be private, too, just to prevent it to be null
	// but that probably consumes a lot of memory

	/** Comments modifiable by the user. The comments are represented as String objects. */
	public List comments = null;

	/** The game data this unit capsule refers to. */
	protected GameData data = null;

	/**
	 * The cache object containing cached information that may be not related enough to be
	 * encapsulated as a function and is time consuming to gather.
	 */
	public Cache cache = null;

	/**
	 * The items carried by this unitcontainer. The keys are the IDs of the item's type, the values are the
	 * Item objects themselves.
	 */
	protected Map items = null;

	/**
	 * A map storing all unknown tags for all UnitContainer objects. Keys are IDs of these objects,
	 * values are Maps(should be TagMaps).
	 */
	private TagMap externalMap = null;

	/**
	 * Creates a new UnitContainer object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 */
	public UnitContainer(ID id, GameData data) {
		super(id);
		this.data = data;
	}


	/**
	 * Adds an item to the unitcontainer. If the unitcontainer already has an item of the same type, the item is
	 * overwritten with the specified item object.
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return the specified item i.
	 */
	public Item addItem(Item i) {
		if(items == null) {
			items = CollectionFactory.createOrderedHashtable();
		}

		items.put(i.getItemType().getID(), i);

		return i;
	}

	/**
	 * Returns all the items this unit possesses.
	 *
	 * @return a collection of Item objects.
	 */
	public Collection getItems() {
		return CollectionFactory.unmodifiableCollection(this.items);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param t TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public void setType(UnitContainerType t) {
		if(t != null) {
			this.type = t;
		} else {
			throw new IllegalArgumentException("UnitContainer.setType(): invalid type specified!");
		}
	}

	/**
	 * Returns the associated GameData
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public GameData getData() {
		return data;
	}

	/**
	 * returns the type of the UnitContainer
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitContainerType getType() {
		return type;
	}

	// units are sorted in unit containers with this index
	private int sortIndex = -1;

	/**
	 * Sets an index indicating how instances of class are sorted in the report.
	 *
	 * @param index TODO: DOCUMENT ME!
	 */
	public void setSortIndex(int index) {
		this.sortIndex = index;
	}

	/**
	 * Returns an index indicating how instances of class are sorted in the report.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSortIndex() {
		return sortIndex;
	}

	/** All units that are in this container. */
	private Map units = null;

	/** Provides a collection view of the unit map. */
	private Collection unitCollection = null;

	/**
	 * Returns an unmodifiable collection of all the units in this container.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection units() {
		// note that there is a consistency problem here. If units is
		// null now we create an empty collection, but if units are
		// added later we have to create a new collection object
		// see addUnit()
		if(units == null) {
			return CollectionFactory.EMPTY_COLLECTION;
		}

		if(unitCollection == null) {
			unitCollection = CollectionFactory.unmodifiableCollection(units);
		}

		return unitCollection;
	}

	/**
	 * Retrieve a unit in this container by id.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getUnit(ID key) {
		if(units != null) {
			return (Unit) units.get(key);
		} else {
			return null;
		}
	}

	/**
	 * Adds a unit to this container. This method should only be invoked by Unit.setXXX() methods.
	 *
	 * @param u TODO: DOCUMENT ME!
	 */
	void addUnit(Unit u) {
		if(units == null) {
			units = CollectionFactory.createOrderedHashtable();

			// enforce the creation of a new collection view:
			unitCollection = null;
		}

		units.put(u.getID(), u);
	}

	/**
	 * Removes a unit from this container. This method should only be invoked by Unit.setXXX()
	 * methods.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	Unit removeUnit(ID key) {
		if(units != null) {
			Unit u = (Unit) units.remove(key);

			if(units.isEmpty()) {
				units = null;
			}

			return u;
		} else {
			return null;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection modifiedUnits() {
		if((cache == null) || (cache.modifiedContainerUnits == null)) {
			refreshModifiedUnits();
		}

		if((cache != null) && (cache.modifiedContainerUnits != null)) {
			return CollectionFactory.unmodifiableCollection(cache.modifiedContainerUnits.values());
		} else {
			return CollectionFactory.EMPTY_COLLECTION;
		}
	}

    protected Collection getRelations() {
        if(cache == null) {
            cache = new Cache();
        }
        if(cache.relations == null) {
            cache.relations = CollectionFactory.createArrayList();
        }
        return cache.relations;
    }
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getModifiedUnit(ID key) {
		if((cache == null) || (cache.modifiedContainerUnits == null)) {
			refreshModifiedUnits();
		}

		if(cache.modifiedContainerUnits == null) {
			return null;
		}

		return (Unit) cache.modifiedContainerUnits.get(key);
	}

	private void refreshModifiedUnits() {
		if(cache == null) {
			cache = new Cache();
		}

		// be careful when clearing modifiedContainerUnits, it could
		// be the normal units
		if(cache.modifiedContainerUnits == units) {
			cache.modifiedContainerUnits = null;
		}

		if(cache.modifiedContainerUnits != null) {
			cache.modifiedContainerUnits.clear();
		}

		// if this unit container does not have relations the
		// modified units equal the normal units
		if(cache.relations == null) {
			if(cache.modifiedContainerUnits != units) {
				if(cache.modifiedContainerUnits != null) {
					cache.modifiedContainerUnits.clear();
				}

				cache.modifiedContainerUnits = units;
			}

			return;
		}

		if(cache.modifiedContainerUnits == null) {
			cache.modifiedContainerUnits = CollectionFactory.createHashtable();
		}

		if(units != null) {
			cache.modifiedContainerUnits.putAll(units);
		}

		for(Iterator iter = cache.relations.iterator(); iter.hasNext();) {
			UnitRelation rel = (UnitRelation) iter.next();

			if(rel instanceof UnitContainerRelation) {
				UnitContainerRelation ucr = (UnitContainerRelation) rel;

				if(this.equals(ucr.target)) {
					if(ucr instanceof EnterRelation) {
						cache.modifiedContainerUnits.put(ucr.source.getID(), ucr.source);
					} else if(ucr instanceof LeaveRelation) {
						cache.modifiedContainerUnits.remove(ucr.source.getID());
					}
				} else {
					log.info("UnitContainer.refreshModifiedUnits(): unit container " + this +
							 " has a relation associated that does not point to it!");
				}
			} else {
				log.info("UnitContainer.refreshModifiedUnits(): unit container " + this +
						 " contains a relation that is not a UnitContainerRelation object!");
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return getName() + " (" + id + "), " + type;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 */
	public void setOwnerUnit(Unit unit) {
		this.owner = unit;
	}

	/**
	 * Returns the unit owning this UnitContainer. If this UnitContainer is an instance of class
	 * Ship or Building the normal owning unit is returned (or null, if there is none). In case of
	 * a Region, the OwnerUnit of the largest castle is returned. In case of a Faction, null is
	 * returned.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getOwnerUnit() {
		if((owner == null) && this instanceof Region) {
			Unit foundOwner = null;
			int bSize = 0;

			for(Iterator iter = ((Region) this).buildings().iterator(); iter.hasNext();) {
				Building b = (Building) iter.next();

				if(b.getType() instanceof CastleType) {
					if(b.getSize() > bSize) {
						bSize = b.getSize();
						foundOwner = b.getOwnerUnit();
					}
				}
			}

			if(foundOwner != null) {
				owner = foundOwner;
			}
		}

		return owner;
	}

	/**
	 * Merges UnitContainers.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curUC TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newUC TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, UnitContainer curUC, GameData newGD,
							 UnitContainer newUC) {
		if(curUC.getName() != null) {
			newUC.setName(curUC.getName());
		}

		if(curUC.getDescription() != null) {
			newUC.setDescription(curUC.getDescription());
		}

		if((curUC.comments != null) && (curUC.comments.size() > 0)) {
			if(newUC.comments == null) {
				newUC.comments = CollectionFactory.createLinkedList();
			} else {
				newUC.comments.clear();
			}

			newUC.comments.addAll(curUC.comments);
		}

		// see Region.merge() for the meaning of the following if
		if(curGD.getDate().equals(newGD.getDate())) {
			if((curUC.effects != null) && (curUC.effects.size() > 0)) {
				if(newUC.effects == null) {
					newUC.effects = CollectionFactory.createLinkedList();
				} else {
					newUC.effects.clear();
				}

				newUC.effects.addAll(curUC.effects);
			}
		}

		if(curUC.owner != null) {
			newUC.owner = newGD.getUnit(curUC.owner.getID());
		} else {
			newUC.owner = null;
		}

		if(curUC.getType() != null) {
			if(curUC instanceof Building) {
				newUC.setType(newGD.rules.getBuildingType(curUC.getType().getID(), true));
			} else if(curUC instanceof Region) {
				// pavkovic 2004.01.03: (bugzilla bug 801): overwrite with curUC.getType if
				// known or newUC.getType is same as "unknown" (this is a miracle to me but
				// Ulrich has more experiences with "Astralraum" :-))
				// if (newUC.getType() == null || newUC.getType().equals(RegionType.unknown)) {
				if ((curUC.getType() != null && !curUC.getType().equals(RegionType.unknown)) ||
					newUC.getType() == null ||
					newUC.getType().equals(RegionType.unknown)) {
					newUC.setType(newGD.rules.getRegionType(curUC.getType().getID(), true));
				}
			} else if(curUC instanceof Ship) {
				newUC.setType(newGD.rules.getShipType(curUC.getType().getID(), true));
			} else if(curUC instanceof Faction) {
				newUC.setType(newGD.rules.getRace(curUC.getType().getID(), true));
			}
		}

		//copy tags
		if(curGD.getDate().equals(newGD.getDate()) && curUC.hasTags()) {
			Iterator it = curUC.getTagMap().keySet().iterator();

			while(it.hasNext()) {
				String str = (String) it.next();

				if(!newUC.containsTag(str)) {
					newUC.putTag(str, curUC.getTag(str));
				}
			}
		}

		newUC.cache = null;

		newUC.sortIndex = Math.max(newUC.sortIndex, curUC.sortIndex);

	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rel TODO: DOCUMENT ME!
	 *
	 */
	public void addRelation(UnitRelation rel) {
		if(cache == null) {
			cache = new Cache();
		}

		if(cache.relations == null) {
			cache.relations = CollectionFactory.createLinkedList();
		}

		cache.relations.add(rel);

        invalidateCache();

	}

    private void invalidateCache() {
        if(cache != null) {
            cache.modifiedName = null;
            cache.modifiedContainerUnits = null;
        }
    }
    
    /**
     * @see com.eressea.Named#getModifiedName()
     */
    public String getModifiedName() {
        if(cache == null) {
            cache = new Cache();
        } 
        if(cache.modifiedName == null) {
            cache.modifiedName = super.getModifiedName();
        }
        return cache.modifiedName != null ? cache.modifiedName : getName(); 
    }

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rel TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitRelation removeRelation(UnitRelation rel) {
		UnitRelation r = null;

		if((cache != null) && (cache.relations != null)) {
			if(cache.relations.remove(rel)) {
				r = rel;
                invalidateCache();
			}
		}

		return r;
	}

	/** EXTERNAL TAG METHODS
	 * 
	 * TODO: DOCUMENT ME!
	 * 
	 * @see com.eressea.util.Taggable#deleteAllTags()
	 */
	public void deleteAllTags() {
		externalMap = null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String putTag(String tag, String value) {
		if(externalMap == null) {
			externalMap = new TagMap();
		}

		return (String) externalMap.put(tag, value);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTag(String tag) {
		if(externalMap == null) {
			return null;
		}

		return (String) externalMap.get(tag);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String removeTag(String tag) {
		if(externalMap == null) {
			return null;
		}

		return (String) externalMap.remove(tag);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean containsTag(String tag) {
		return (externalMap != null) && externalMap.containsKey(tag);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getTagMap() {
		if(externalMap == null) {
			externalMap = new TagMap();
		}

		return externalMap;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean hasTags() {
		return (externalMap != null) && !externalMap.isEmpty();
	}
}
