// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eressea.rules.CastleType;
import com.eressea.rules.ItemType;
import com.eressea.rules.RegionType;
import com.eressea.util.Cache;
import com.eressea.util.CollectionFactory;
import com.eressea.util.ROCollection;
import com.eressea.util.logging.Logger;



public class Region extends UnitContainer {
	private final static Logger log = Logger.getInstance(Region.class);

	public int trees = -1;
	public int oldTrees = -1;
	public int sprouts = -1;
	public int oldSprouts = -1;
	public boolean mallorn = false;
	public int iron = -1;
	public int oldIron = -1;
	public int laen = -1;
	public int oldLaen = -1;
	public int peasants = -1;
	public int oldPeasants = -1;
	public int silver = -1;
	public int oldSilver = -1;
	public int horses = -1;
	public int oldHorses = -1;
	public int stones = -1;
	public int oldStones = -1;
	/**
	 * The wage persons can earn by working in this region.
	 * Unfortunately this is not the wage peasants earn but the wage
	 * a player's persons earn and to make it worse, the eressea
	 * server puts different values into CRs depending of the race
	 * of the 'owner' faction of the report. I.e. an orc faction gets
	 * a different value than factions of other races.
	 * Therefore there is a getPeasantWage() method returning
	 * how much a peasant earns in this region depending on the
	 * biggest castle.
	 */
	public int wage = -1;
	public int oldWage = -1;
	public ItemType herb = null;
	public String herbAmount = null;
	/** Indicates that there are too many orcs in this region. */
	public boolean orcInfested = false;

	// pavkovic 2002.05.13: for eressea CR-Version >= 64 we do interpret the recruits tag
	public int recruits = -1;
	public int oldRecruits = -1;

	/**
	 * Constructs a new Region object uniquely identifiable by the
	 * specified id.
	 */
	public Region(ID id, GameData data) {
		super(id, data);
	}

	// pavkovic 2003.09.10: moved from Cache to this object to remove 
	// Cache objects for empty/ocean regions
	// used in swing.map.RegionImageCellRenderer per region
	private int fogOfWar = -1;
	public synchronized boolean fogOfWar() {
		if (fogOfWar == -1) {
			fogOfWar = 1;
			for (Iterator iter = units().iterator(); iter.hasNext();) {
				Faction f = ((Unit)iter.next()).getFaction();
				if (f.trustLevel >= Faction.TL_PRIVILEGED) {
					fogOfWar = 0;
					break;
				}
			}
		}
		return fogOfWar == 1;
	}
	public synchronized void setFogOfWar(int fog) {
		fogOfWar = fog;
	}

	/** 
	 * this unit indicated the "0" unit!
	 */
	private ZeroUnit cachedZeroUnit;

	public Unit getZeroUnit() {
		// only create if needed
		if(cachedZeroUnit ==null) {
			// if there are no units in this region we assume that this
			// region is less interesting (there will be NO Relation nor
			// massive interactive view of this region.
			// So we create the ZeroUnit on the fly.
			if(units().isEmpty()) {
				return new ZeroUnit(this);
			}
			cachedZeroUnit = new ZeroUnit(this);
		}
		return cachedZeroUnit;
	}

	/**
	 * @return the number of modified persons after "give 0", recruit 
	 */
	public int getModifiedPeasants() {
		ZeroUnit zu = (ZeroUnit) getZeroUnit();
		// peasants == peasants - (maxRecruit() - recruited peasants ) + givenPersons
		return (this.peasants == -1) ? -1 : this.peasants - zu.getPersons() + zu.getModifiedPersons() + zu.getGivenPersons();
	}

	public int modifiedRecruit() {
		return getZeroUnit().getModifiedPersons();
	}

	/**
	 * Indicates that refreshUnitRelations() has already been called
	 * once.
	 */
	private boolean unitRelationsRefreshed = false;
	/**
	 * The island this region belongs to.
	 */
	private Island island = null;
	/**
	 * Sets the island this region belongs to.
	 */
	public void setIsland(Island i) {
		if (this.island != null) {
			this.island.invalidateRegions();
		}
		this.island = i;
		if (this.island != null) {
			this.island.invalidateRegions();
		}
	}

	/**
	 * Returns the island this region belongs to.
	 */
	public Island getIsland() {
		return this.island;
	}



	/**
	 * Informs about the reason why this region is visible.
	 */
	private String visibility = null;
	/**
	 * A string constant indicating why this region is visible.
	 * @return the string object or null, if the visibility is
	 *  unspecified.
	 */
	public String getVisibility() {
		return this.visibility;
	}

	/**
	 * Sets a string constant indicating why this region is visible.
	 * @param vis a String object or null to indicate that the
	 *  visibility cannot be determined.
	 */
	public void setVisibility(String vis) {
		this.visibility = vis;
	}

	/**
	 * The prices for luxury goods in this region. The map contains
	 * the name of the luxury good as instance of class <tt>StringID</tt> as key
	 * and instances of class
	 * <tt>LuxuryPrice</tt> as values.
	 */
	public Map prices = null;

	/**
	 * The prices of luxury goods of the last turn.
	 */
	public Map oldPrices = null;

	/**
	 * The messages for this region. The list consists of objects of
	 * class <tt>Message</tt>.
	 */
	public List messages = null;

	/**
	 * Special messages related to this region. The list contains
	 * instances of class <tt>Message</tt> with type -1 and only the
	 * text set.
	 */
	public List events = null;

	/**
	 * Special messages related to this region. The list contains
	 * instances of class <tt>Message</tt> with type -1 and only the
	 * text set.
	 */
	public List playerMessages = null;

	/**
	 * Special messages related to this region. The list contains
	 * instances of class <tt>Message</tt> with type -1 and only the
	 * text set.
	 */
	public List surroundings = null;

	/**
	 * Special messages related to this region. The list contains
	 * instances of class <tt>Message</tt> with type -1 and only the
	 * text set.
	 */
	public List travelThru = null;

	/**
	 * Special messages related to this region. The list contains
	 * instances of class <tt>Message</tt> with type -1 and only the
	 * text set.
	 */
	public List travelThruShips = null;

	/**
	 * RegionResources in this region. The keys in this map are
	 * instances of class <tt>ID</tt> identifying the item type of the
	 * resource, the values are instances of class
	 * <tt>RegionResource</tt>.
	 */
	private Map resources = null;

	/**
	 * A collection view of the resources.
	 */
	private Collection resourceCollection = null;

	/**
	 * Returns all resources of this region.
	 */
	public Collection resources() {
		if (this.resourceCollection == null) {
			/* since resources appear twice in the map, once with the
			 numerical ID and once with the item type ID, we have to
			 make sure that this collection lists only one of them.
			 Since the hashValue() Method of a RegionResource relates
			 to its numerical ID a HashSet can do the job */
			// 2002.02.18 ip: this.resources can be null
			this.resourceCollection = new ROCollection(CollectionFactory.createHashSet(
						   this.resources == null ? Collections.EMPTY_SET :	this.resources.values()));
		}
		return this.resourceCollection;
	}

	/**
	 * Adds a resource to this region.
	 */
	public RegionResource addResource(RegionResource resource) {
		if (resource == null) {
			throw new NullPointerException();
		}

		if (this.resources == null) {
			this.resources = CollectionFactory.createOrderedHashtable();
			// enforce the creation of a new collection view
			this.resourceCollection = null;
		}

		// pavkovic 2002.05.21: If some resources have an amount zero, we ignore it
		if(resource.getAmount() != 0) {
			this.resources.put(resource.getID(), resource);
			this.resources.put(resource.getType().getID(), resource);
		}
		if(log.isDebugEnabled()) {
			log.debug("Region.addResource:"+this);
			log.debug("Region.addResource:"+resource);
			log.debug("Region.addResource:"+resources);
		}
		return resource;
	}

	/**
	 * Removes the resource with the specified numerical id or the id
	 * of its item type from this region.
	 *
	 * @returns the removed resource or null if no resource with the
	 * specified id exists in this region.
	 */
	public RegionResource removeResource(RegionResource r) {
		return this.removeResource(r.getID());
	}


	public RegionResource removeResource(ID id) {
		if (this.resources == null) {
			return null;
		}
		RegionResource ret = (RegionResource)this.resources.remove(id);
		if(ret != null) {
			this.resources.remove(ret.getID());
			this.resources.remove(ret.getType().getID());
		}
		if (this.resources.isEmpty()) {
			this.resources = null;
			this.resourceCollection = null;
		}
		if(log.isDebugEnabled()) {
			log.debug("Region.removeResource:"+this);
			log.debug("Region.removeResource:"+ret);
			if(ret != null) {
				log.debug("Region.removeResource:"+ret.getID());
				log.debug("Region.removeResource:"+ret.getType().getID());
			}
			log.debug("Region.removeResource:"+resources);
		}
		return ret;
	}

	/**
	 * Removes all resources from this region.
	 */
	public void clearRegionResources() {
		if (this.resources != null) {
			this.resources.clear();
			this.resources = null;
			this.resourceCollection = null;
		}
	}

	/**
	 * Returns the resource with the numerical ID or the ID of its
	 * item type.
	 *
	 * @returns the resource object or null if no resource with the
	 * specified ID exists in this region.
	 */
	public RegionResource getResource(ID id) {
		return this.resources != null ? (RegionResource)this.resources.get(id) : null;
	}

	/**
	 * Schemes in this region. The keys in this map are
	 * instances of class <tt>Coordinate</tt> identifying the
	 * location of the scheme, the values are instances of class
	 * <tt>Scheme</tt>.
	 */
	private Map schemes = null;

	/**
	 * A collection view of the schemes.
	 */
	private Collection schemeCollection = null;

	/**
	 * Returns all schemes of this region.
	 */
	public Collection schemes() {
		if (schemeCollection == null) {
			if (schemes == null) {
				return ROCollection.EMPTY_COLLECTION;
			}
			schemeCollection = new ROCollection(schemes);
		}
		return schemeCollection;
	}

	/**
	 * Adds a scheme to this region.
	 */
	public Scheme addScheme(Scheme scheme) {
		if (scheme == null) {
			throw new NullPointerException();
		}

		if (this.schemes == null) {
			this.schemes = CollectionFactory.createOrderedHashtable();
			// enforce the creation of a new collection view
			// AG: Since we just create if the scheme map is non-null not necessary
			//this.schemeCollection = null;
		}

		this.schemes.put(scheme.getID(), scheme);
		return scheme;
	}

	/**
	 * Removes the scheme with the specified id from this region.
	 *
	 * @returns the removed scheme or null if no scheme with the
	 * specified id exists in this region.
	 */
	public Scheme removeScheme(Scheme s) {
		if (this.schemes == null) {
			return null;
		}
		Scheme ret = (Scheme)this.schemes.remove(id);
		if (this.schemes.isEmpty()) {
			this.schemes = null;
			this.schemeCollection = null;
		}
		return ret;
	}

	/**
	 * Removes all schemes from this region.
	 */
	public void clearSchemes() {
		if (this.schemes != null) {
			this.schemes.clear();
			this.schemes = null;
			this.schemeCollection = null;
		}
	}

	/**
	 * Returns the scheme with the specified corodinate.
	 *
	 * @returns the scheme object or null if no scheme with the
	 * specified ID exists in this region.
	 */
	public Scheme getScheme(ID id) {
		return this.schemes != null ? (Scheme)this.schemes.get(id) : null;
	}
	
	/**
	 * Border elements of this region. The list contains
	 * instances of class <tt>Border</tt>.
	 */
	private Map borders = null;
	
	/**
	 * A collection view of the borders.
	 */
	private Collection borderCollection = null;

	/**
	 * Returns all borders of this region.
	 */
	public Collection borders() {
		if (this.borderCollection == null) {
			if (this.borders == null) {
				return ROCollection.EMPTY_COLLECTION;
			}
			this.borderCollection = new ROCollection(this.borders);
		}
		return this.borderCollection;
	}
	
	/**
	 * Adds a border to this region.
	 */
	public Border addBorder(Border border) {
		if (border == null) {
			throw new NullPointerException();
		}
		
		if (this.borders == null) {
			this.borders = CollectionFactory.createOrderedHashtable();
			// enforce the creation of a new collection view
			// AG: Since we just create if the scheme map is non-null not necessary
			// this.borderCollection = null;
		}
		
		this.borders.put(border.getID(), border);
		return border;
	}
	
	/**
	 * Removes the border with the specified id from this region.
	 *
	 * @returns the removed border or null if no border with the
	 * specified id exists in this region.
	 */
	public Border removeBorder(Border b) {
		if (this.borders == null) {
			return null;
		}
		Border ret = (Border)this.borders.remove(id);
		if (this.borders.isEmpty()) {
			this.borders = null;
			this.borderCollection = null;
		}
		return ret;
	}

	/**
	 * Removes all borders from this region.
	 */
	public void clearBorders() {
		if (this.borders != null) {
			this.borders.clear();
			this.borders = null;
			this.borderCollection = null;
		}
	}

	/**
	 * Returns the border with the specified id.
	 *
	 * @returns the border object or null if no border with the
	 * specified id exists in this region.
	 */
	public Border getBorder(ID id) {
		return this.borders != null ? (Border)this.borders.get(id) : null;
	}

	/**
	 * All ships that are in this container.
	 */
	private Map ships = null;

	/**
	 * Provides a collection view of the ship map.
	 */
	private Collection shipCollection = null;

	/**
	 * Returns an unmodifiable collection of all the ships in this
	 * container.
	 */
	public Collection ships() {
		if (this.shipCollection == null) {
			if (ships == null) {
				return ROCollection.EMPTY_COLLECTION;
			}
			this.shipCollection = new ROCollection(this.ships);
		}
		return this.shipCollection;
	}

	/**
	 * Retrieve a ship in this container by id.
	 */
	public Ship getShip(ID id) {
		return ships != null ? (Ship)this.ships.get(id): null;
	}

	/**
	 * Adds a ship to this container. This method should only be
	 * invoked by Ship.setXXX() methods.
	 */
	public void addShip(Ship s) {
		if (this.ships == null) {
			this.ships = CollectionFactory.createHashtable();
			// enforce the creation of a new collection view
			// AG: Since we just create if the ship map is non-null not necessary
			// this.shipCollection = null;
		}
		this.ships.put(s.getID(), s);
	}

	/**
	 * Removes a ship from this container. This method should only be
	 * invoked by Ship.setXXX() methods.
	 */
	public Ship removeShip(Ship s) {
		if(this.ships == null) {
			return null;
		}
		Ship ret = (Ship)this.ships.remove(s.getID());
		if (this.ships.isEmpty()) {
			this.ships = null;
			this.shipCollection = null;
		}
		return ret;
	}

	/**
	 * All buildings that are in this container.
	 */
	private Map buildings = null;

	/**
	 * Provides a collection view of the building map.
	 */

	private Collection buildingCollection = null;

	/**
	 * Returns an unmodifiable collection of all the buildings in this
	 * container.
	 */
	public Collection buildings() {
		if (this.buildingCollection == null) {
			if (buildings == null) {
				return ROCollection.EMPTY_COLLECTION;
			}
			this.buildingCollection = new ROCollection(this.buildings);
		}
		return this.buildingCollection;
	}

	/**
	 * Retrieve a building in this container by id.
	 */
	public Building getBuilding(ID id) {
		return buildings != null ? (Building)this.buildings.get(id) : null;
	}

	/**
	 * Adds a building to this container. This method should only be
	 * invoked by Building.setXXX() methods.
	 */
	void addBuilding(Building u) {
		if (this.buildings == null) {
			this.buildings = CollectionFactory.createHashtable();
			// enforce the creation of a new collection view
			// AG: Since we just create if the ship map is non-null not necessary
			// this.buildingCollection = null;
		}
		this.buildings.put(u.getID(), u);
	}

	/**
	 * Removes a building from this container. This method should only be
	 * invoked by Building.setXXX() methods.
	 */
	Building removeBuilding(Building b) {
		if(this.buildings == null) {
			return null;
		}
		Building ret = (Building)this.buildings.remove(b.getID());
		if (this.buildings.isEmpty()) {
			this.buildings = null;
			this.buildingCollection = null;
		}
		return ret;
	}

	/**
	 * Returns the items of all units that are stationed in this
	 * region and belonging to a faction that has at least a
	 * privileged trust level. The amount of the items of a particular
	 * item type are added up, so two units with 5 pieces of silver
	 * yield one silver item of amount 10 here.
	 */
	public Collection items() {
		if (cache == null || cache.regionItems == null) {
			refreshItems();
		}
		return new ROCollection(cache.regionItems.values());
	}

	/**
	 * Returns a specific item from the items() collection identified
	 * by the item type.
	 */
	public Item getItem(ItemType type) {
		if (cache == null || cache.regionItems == null) {
			refreshItems();
		}
		return (cache != null && cache.regionItems != null) ? 
			(Item)cache.regionItems.get(type.getID()) : 
			null;
	}

	/**
	 * Updates the cache of items owned by privileged factions in this
	 * region.
	 */
	private void refreshItems() {
		if (cache != null) {
			if (cache.regionItems != null) {
				cache.regionItems.clear();
			} else {
				cache.regionItems = CollectionFactory.createHashtable();
			}
		} else {
			cache = new Cache();
			cache.regionItems = CollectionFactory.createHashtable();
		}

		for (Iterator iter = units().iterator(); iter.hasNext(); ) {
			Unit u = (Unit)iter.next();
			if (u.getFaction().trustLevel >= Faction.TL_PRIVILEGED) {
				for (Iterator items = u.getItems().iterator(); items.hasNext(); ) {
					Item item = (Item)items.next();
					Item i = (Item)cache.regionItems.get(item.getItemType().getID());
					if (i == null) {
						i = new Item(item.getItemType(), 0);
						cache.regionItems.put(item.getItemType().getID(), i);
					}
					i.setAmount(i.getAmount() + item.getAmount());
				}
			}
		}
	}

	/**
	 * Returns the maximum number of persons that can be recruited in
	 * this region.
	 */
	public int maxRecruit() {
		// pavkovic 2002.05.10: in case we dont have a recruit max set we evaluate it
		return recruits == -1 ? maxRecruit(this.peasants) : recruits;
	}

	/**
	 * Returns the maximum number of persons that can be recruited in
	 * this region.
	 */
	public int maxOldRecruit() {
		// pavkovic 2002.05.10: in case we dont have a recruit max set we evaluate it
		return oldRecruits == -1 ? maxRecruit(this.oldPeasants) : oldRecruits;
	}

	/**
	 * Returns the maximum number of persons available for recruitment
	 * in a region with the specified number of peasants.
	 */
	private int maxRecruit(int peasants) {
		if (peasants >= 0) {
			return peasants / 40; // 2.5 %
		}
		return -1;
	}

	/**
	 * Returns the silver that can be earned through entertainment in
	 * this region.
	 */
	public int maxEntertain() {
		return maxEntertain(this.silver);
	}

	/**
	 * Returns the silver that could be earned through entertainment in
	 * this region.
	 */
	public int maxOldEntertain() {
		return maxEntertain(this.oldSilver);
	}

	/**
	 * Return the silver that can be earned through entertainment in
	 * a region with the given amount of silver.
	 */
	private int maxEntertain(int silver) {
		if (silver >= 0) {
			return silver / 20;
		}
		return -1;
	}

	/**
	 * Returns the maximum number of luxury items that can be bought
	 * in this region without a price penalty.
	 */
	public int maxLuxuries() {
		return maxLuxuries(this.peasants);
	}

	/**
	 * Returns the maximum number of luxury items that could be bought
	 * in this region without a price penalty.
	 */
	public int maxOldLuxuries() {
		return maxLuxuries(this.oldPeasants);
	}

	/**
	 * Return the maximum number of luxury items that can be bought
	 * without a price increase in a region with the specified number
	 * of peasants.
	 */
	private int maxLuxuries(int peasants) {
		return peasants >= 0 ? peasants / 100 : -1;
	}

	/**
	 * Calculates the wage a peasant earns according
	 * to the biggest castle in this region. While the value of the
	 * wage field is directly taken from the report and may be biased
	 * by the race of the owner faction of that report, this function
	 * tries to determine the real wage a peasaent can earn in this
	 * region. Wage for player persons can be derived from that value
	 */
	public int getPeasantWage() {
		int wage = 11;
		if (buildings != null) {
			for (Iterator iter = buildings().iterator(); iter.hasNext(); ) {
				Building b = (Building)iter.next();
				if (b.getType() instanceof CastleType) {
					CastleType ct = (CastleType)b.getType();
					wage = Math.max(ct.getPeasantWage(), wage);
				}
			}
		}
		return wage;
	}

	/**
	 * Indicates whether this Region object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class Region and o's id is equal to the id of this
	 * Region object.
	 */
	public boolean equals(Object o) {
		return this == o || (o instanceof Region && this.getID().equals(((Region)o).getID()));
	}

	/**
	 * Imposes a natural ordering on Region objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Region)o).getID());
	}

	/**
	 * Returns a String representation of this Region object.
	 * If region has no name the string representation of the 
	 * region type is used.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getName() == null) {
			if(getType()!=null) {
				sb.append(getType().toString());
			}
		} else {
			sb.append(getName());
		}
		sb.append(" (").append(((Coordinate)this.getID()).toString(", ")).append(")");
		return sb.toString();
	}

	/**
	 * Returns the coordinate of this region. This method is only a
	 * type-safe short cut for retrieving and converting the ID object
	 * of this region.
	 */
	public Coordinate getCoordinate() {
		return (Coordinate)this.getID();
	}

	/**
	 * Returns the RegionType of this region. This method is only a
	 * type-safe short cut for retrieving and converting the RegionType
	 * of this region.
	 */
	public RegionType getRegionType() {
		return (RegionType) this.getType();
	}

	/**
	 * Refreshes all the relations of all units in this region. It is
	 * preferrable to call this method instead of refreshing the unit
	 * relations 'manually'.
	 */
	public synchronized void refreshUnitRelations() {
		if (unitRelationsRefreshed == false) {
			unitRelationsRefreshed = true;
			for (Iterator iter = this.units().iterator(); iter.hasNext(); ) {
				Unit u = (Unit)iter.next();
				u.refreshRelations();
			}
			getZeroUnit().refreshRelations();
		}
	}

	/**
	 * Guarding units of this region. The list contains
	 * instances of class <tt>Unit</tt>.
	 */
	private List guards;

	/**
	 * add guarding Unit to region
	 */

	public void addGuard(Unit u) {
		if (guards == null)
			guards = CollectionFactory.createArrayList();
		if (!guards.contains(u)) {
			guards.add(u);
		}
	}

	/**
	 * get The List of guarding Units
	 */

	public List getGuards()
	{
		return guards;
	}

	/**
	 * Merges regions.
	 */
	public static void merge(GameData curGD, Region curRegion, GameData newGD, Region newRegion, boolean sameTurn) {
		UnitContainer.merge(curGD, curRegion, newGD, newRegion);
		if (sameTurn) {
			if (curRegion.oldTrees != -1) {
				newRegion.oldTrees = curRegion.oldTrees;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.trees != -1) {
					newRegion.oldTrees = curRegion.trees;
				}
			} else {
				if (curRegion.trees == -1) {
					newRegion.oldTrees = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldSprouts != -1) {
				newRegion.oldSprouts = curRegion.oldSprouts;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.sprouts != -1) {
					newRegion.oldSprouts = curRegion.sprouts;
				}
			} else {
				if (curRegion.sprouts == -1) {
					newRegion.oldSprouts = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldIron != -1) {
				newRegion.oldIron = curRegion.oldIron;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.iron != -1) {
					newRegion.oldIron = curRegion.iron;
				}
			} else {
				if (curRegion.iron == -1) {
					newRegion.oldIron = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldLaen != -1) {
				newRegion.oldLaen = curRegion.oldLaen;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.laen != -1) {
					newRegion.oldLaen = curRegion.laen;
				}
			} else {
				if (curRegion.laen == -1) {
					newRegion.oldLaen = -1;
				}
			}
		}
		if (sameTurn) {
			newRegion.orcInfested |= curRegion.orcInfested;
		} else {
			newRegion.orcInfested = curRegion.orcInfested;
		}
		if (sameTurn) {
			if (curRegion.oldPeasants != -1) {
				newRegion.oldPeasants = curRegion.oldPeasants;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.peasants != -1) {
					newRegion.oldPeasants = curRegion.peasants;
				}
			} else {
				if (curRegion.peasants == -1) {
					newRegion.oldPeasants = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldSilver != -1) {
				newRegion.oldSilver = curRegion.oldSilver;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.silver != -1) {
					newRegion.oldSilver = curRegion.silver;
				}
			} else {
				if (curRegion.silver == -1) {
					newRegion.oldSilver = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldStones != -1) {
				newRegion.oldStones = curRegion.oldStones;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.stones != -1) {
					newRegion.oldStones = curRegion.stones;
				}
			} else {
				if (curRegion.stones == -1) {
					newRegion.oldStones = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldHorses != -1) {
				newRegion.oldHorses = curRegion.oldHorses;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.horses != -1) {
					newRegion.oldHorses = curRegion.horses;
				}
			} else {
				if (curRegion.horses == -1) {
					newRegion.oldHorses = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldWage != -1) {
				newRegion.oldWage = curRegion.oldWage;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.wage != -1) {
					newRegion.oldWage = curRegion.wage;
				}
			} else {
				if (curRegion.wage == -1) {
					newRegion.oldWage = -1;
				}
			}
		}
		if (sameTurn) {
			if (curRegion.oldRecruits != -1) {
				newRegion.oldRecruits = curRegion.oldRecruits;
			}
		} else {
			if (!curGD.getDate().equals(newGD.getDate())) {
				if (curRegion.recruits != -1) {
					newRegion.oldRecruits = curRegion.recruits;
				}
			} else {
				if (curRegion.recruits == -1) {
					newRegion.oldRecruits = -1;
				}
			}
		}

		if (newRegion.prices != null && curRegion.prices != null && !curRegion.prices.equals(newRegion.prices)) {
			newRegion.oldPrices = CollectionFactory.createHashtable();
			for (Iterator iter = newRegion.prices.values().iterator(); iter.hasNext(); ) {
				LuxuryPrice curPrice = (LuxuryPrice)iter.next();
				LuxuryPrice newPrice = new LuxuryPrice(newGD.rules.getItemType(curPrice.getItemType().getID()), curPrice.getPrice());
				newRegion.oldPrices.put(newPrice.getItemType().getID(), newPrice);
			}
		} else if (curRegion.oldPrices != null) {
			newRegion.oldPrices = CollectionFactory.createHashtable();
			for (Iterator iter = curRegion.oldPrices.values().iterator(); iter.hasNext(); ) {
				LuxuryPrice curPrice = (LuxuryPrice)iter.next();
				LuxuryPrice newPrice = new LuxuryPrice(newGD.rules.getItemType(curPrice.getItemType().getID()), curPrice.getPrice());
				if (newPrice.getItemType() == null) {
					// this happens if there does exist an unknown tag in
					// the current block description
					log.warn("WARNING: Invalid tag \"" +
							 curPrice.getItemType() + "\" found in Region " +
							 curRegion + ", ignoring it.");
				} else {
					newRegion.oldPrices.put(newPrice.getItemType().getID(), newPrice);
				}
			}
		}

		// pavkovic 2002.04.12: This logic seems to be more reasonable:
		// prerequisites: there are borders in the current region
		// if there are no borders in the new region
		//   -> the borders of the current region are added to the new region
		// if there are borders in the new region *and* there is at least one
		//    person in the current region
		//   -> the borders of the current region are added to the new region
		//
		if (!curRegion.borders().isEmpty() &&
			(newRegion.borders().isEmpty() || !curRegion.units().isEmpty())) {
			newRegion.clearBorders();
			for (Iterator iter = curRegion.borders().iterator(); iter.hasNext(); ) {
				Border curBorder = (Border)iter.next();
				Border newBorder = null;
				try {
					newBorder = new Border((ID)curBorder.getID().clone(), curBorder.direction, curBorder.type, curBorder.buildRatio);
				} catch (CloneNotSupportedException e) {
				}
				newRegion.addBorder(newBorder);
			}
		}
		if (curRegion.herb != null) {
			newRegion.herb = newGD.rules.getItemType(curRegion.herb.getID(), true);
		}
		if (curRegion.herbAmount != null) {
			/* There was a bug around 2002.02.16 where numbers would be
			 stored in this field - filter them out. This should only
			 be here for one or two weeks. */
			if (curRegion.herbAmount.length() > 2) {
				newRegion.herbAmount = curRegion.herbAmount;
			}
		}
		if (curRegion.horses != -1) {
			newRegion.horses = curRegion.horses;
		}
		if (curRegion.iron != -1) {
			newRegion.iron = curRegion.iron;
		}
		if (curRegion.getIsland() != null) {
			Island newIsland = newGD.getIsland(curRegion.getIsland().getID());
			if (newIsland != null) {
				newRegion.setIsland(newIsland);
			} else {
				log.warn("Region.merge(): island could not be found in the merged data: " +
						 curRegion.getIsland());
			}
		}
		if (curRegion.laen != -1) {
			newRegion.laen = curRegion.laen;
		}
		newRegion.mallorn |= curRegion.mallorn;
		if (curRegion.peasants != -1) {
			newRegion.peasants = curRegion.peasants;
		}
		if (curRegion.prices != null && curRegion.prices.size() > 0) {
			if (newRegion.prices == null) {
				newRegion.prices = CollectionFactory.createOrderedHashtable();
			} else {
				newRegion.prices.clear();
			}
			for (Iterator iter = curRegion.prices.values().iterator(); iter.hasNext(); ) {
				LuxuryPrice curPrice = (LuxuryPrice)iter.next();
				LuxuryPrice newPrice = new LuxuryPrice(newGD.rules.getItemType(curPrice.getItemType().getID()), curPrice.getPrice());
				if (newPrice.getItemType() == null) {
					// this happens if there does exist an unknown tag in
					// the current block description
					log.warn("Invalid tag \"" +	curPrice.getItemType() + "\" found in Region " +
							 curRegion + ", ignoring it.");
				} else {
					newRegion.prices.put(newPrice.getItemType().getID(), newPrice);
				}
			}
		}
		if (!curRegion.resources().isEmpty()) {
			for (Iterator iter = curRegion.resources().iterator(); iter.hasNext(); ) {
				RegionResource curRes = (RegionResource)iter.next();
				RegionResource newRes = newRegion.getResource(curRes.getID());
				try {
					/**
					  Remember: Merging of regions works like follows:
					  A new set of regions is created in the new GameData object.
					  Then first the regions of the older report are merged into that new object.
					  Then the regions of the newer report are merged into that new object.
					  At this time sameTurn is guaranteed to be true!
					  The crucial point is when a resource is suddenly not seen any longer,
					  because its level has increased.
					**/
					if (newRes == null) {
						// add Resource
						newRes = new RegionResource((ID)curRes.getID().clone(), newGD.rules.getItemType(curRes.getType().getID(), true));
						newRegion.addResource(newRes);
					}
					RegionResource.merge(curGD, curRes, newGD, newRes, sameTurn);
				} catch (CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}
		// Now look for those resources, that are in the new created game data,
		// but not in the current one. These are those, that are not seen in the
		// maybe newer report! This maybe because their level has changed.
		if (newRegion.resources != null && !newRegion.resources.isEmpty()) {
			for (Iterator iter = newRegion.resources.values().iterator(); iter.hasNext(); ) {
				RegionResource newRes = (RegionResource)iter.next();
				RegionResource curRes = curRegion.getResource(newRes.getID());
				if (curRes == null) {
					// check wheather talent is good enogh that it should be seen!
					// Keep in mind, that the units are not jet merged (Use those of curRegion)
					boolean found = false;
					for (Iterator i = curRegion.units().iterator(); i.hasNext() && !found; ) {
						Unit unit = (Unit)i.next();
						if (unit.skills != null) {
							for (Iterator skillIterator = unit.skills.values().iterator(); skillIterator.hasNext() && !found; ) {
								Skill skill = (Skill)skillIterator.next();
								Skill makeSkill = newRes.getType().getMakeSkill();
								if (makeSkill != null && skill.getSkillType().equals(makeSkill.getSkillType())) {
									// found a unit with right skill, level high enough?
									if (skill.getLevel() >= newRes.getSkillLevel()) {
										found = true;
									}
								}
							}
						}
					}
					if (found) {
						// enforce this information to be taken!
						newRes.setSkillLevel(newRes.getSkillLevel() + 1);
						newRes.setAmount(-1);
					}
				}
			}
		}


		if (!curRegion.schemes().isEmpty()) {
			for (Iterator iter = curRegion.schemes().iterator(); iter.hasNext(); ) {
				Scheme curScheme = (Scheme)iter.next();
				Scheme newScheme = newRegion.getScheme(curScheme.getID());
				try {
					if (newScheme == null) {
						newScheme = new Scheme((ID)curScheme.getID().clone());
						newRegion.addScheme(newScheme);
					}

					Scheme.merge(curGD, curScheme, newGD, newScheme);
				} catch (CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}
		if (curRegion.silver != -1) {
			newRegion.silver = curRegion.silver;
		}
		if (curRegion.sprouts != -1) {
			newRegion.sprouts = curRegion.sprouts;
		}
		if (curRegion.stones != -1) {
			newRegion.stones = curRegion.stones;
		}
		if (curRegion.trees != -1) {
			newRegion.trees = curRegion.trees;
		}
		if (!sameTurn) {
			/* as long as both reports are from different turns we
			 can just overwrite the visibility status with the newer
			 version */
			newRegion.setVisibility(curRegion.getVisibility());
		} else {
			/* this where trouble begins: reports from the same turn
			 so we basically have 4 visibility status:
			 1 contains units (implicit)
			 2 travel (explicit)
			 3 lighthouse (explicit)
			 4 next to a unit containing region (implicit)
			 now - how do we merge this?
			 for a start, we just make sure that the visibility
			 value is not lost */
			if (curRegion.getVisibility() != null) {
				newRegion.setVisibility(curRegion.getVisibility());
			}
		}
		if (curRegion.wage != -1) {
			newRegion.wage = curRegion.wage;
		}

		// Messages are special because they can contain different
		// data for different factions in the same turn.
		// Take new messages and stuff only into the new game data
		// if the two source game data objects are not from the
		// same turn and curGD is the newer game data or if both
		// are from the same turn. Both conditions are tested by the
		// following if statement
		if (curGD.getDate().equals(newGD.getDate())) {
			if (curRegion.events != null && curRegion.events.size() > 0) {
				if (newRegion.events == null) {
					newRegion.events = CollectionFactory.createLinkedList();
				}
				for (Iterator iter = curRegion.events.iterator(); iter.hasNext(); ) {
					Message curMsg = (Message)iter.next();
					Message newMsg = null;
					try {
						newMsg = new Message((ID)curMsg.getID().clone());
					} catch (CloneNotSupportedException e) {
					}
					Message.merge(curGD, curMsg, newGD, newMsg);
					newRegion.events.add(newMsg);
				}
			}
			if (curRegion.messages != null && curRegion.messages.size() > 0) {
				if (newRegion.messages == null) {
					newRegion.messages = CollectionFactory.createLinkedList();
				}
				for (Iterator iter = curRegion.messages.iterator(); iter.hasNext(); ) {
					Message curMsg = (Message)iter.next();
					Message newMsg = null;
					try {
						newMsg = new Message((ID)curMsg.getID().clone());
					} catch (CloneNotSupportedException e) {
					}
					Message.merge(curGD, curMsg, newGD, newMsg);
					newRegion.messages.add(newMsg);
				}
			}
			if (curRegion.playerMessages != null && curRegion.playerMessages.size() > 0) {
				if (newRegion.playerMessages == null) {
					newRegion.playerMessages = CollectionFactory.createLinkedList();
				}
				for (Iterator iter = curRegion.playerMessages.iterator(); iter.hasNext(); ) {
					Message curMsg = (Message)iter.next();
					Message newMsg = null;
					try {
						newMsg = new Message((ID)curMsg.getID().clone());
					} catch (CloneNotSupportedException e) {
					}
					Message.merge(curGD, curMsg, newGD, newMsg);
					newRegion.playerMessages.add(newMsg);
				}
			}
			if (curRegion.surroundings != null && curRegion.surroundings.size() > 0) {
				if (newRegion.surroundings == null) {
					newRegion.surroundings = CollectionFactory.createLinkedList();
				}
				for (Iterator iter = curRegion.surroundings.iterator(); iter.hasNext(); ) {
					Message curMsg = (Message)iter.next();
					Message newMsg = null;
					try {
						newMsg = new Message((ID)curMsg.getID().clone());
					} catch (CloneNotSupportedException e) {
					}
					Message.merge(curGD, curMsg, newGD, newMsg);
					newRegion.surroundings.add(newMsg);
				}
			}
			if (curRegion.travelThru != null && curRegion.travelThru.size() > 0) {
				if (newRegion.travelThru == null) {
					newRegion.travelThru = CollectionFactory.createLinkedList();
				}
				for (Iterator iter = curRegion.travelThru.iterator(); iter.hasNext(); ) {
					Message curMsg = (Message)iter.next();
					Message newMsg = null;
					try {
						newMsg = new Message((ID)curMsg.getID().clone());
					} catch (CloneNotSupportedException e) {
					}
					Message.merge(curGD, curMsg, newGD, newMsg);
					// 2002.02.21 pavkovic: prevent double entries
					if(!newRegion.travelThru.contains(newMsg)) {
						newRegion.travelThru.add(newMsg);
					} else {
						log.warn("Region.merge(): Duplicate message \""+newMsg.getText()+"\", removing it.");
						/*
						if(log.isDebugEnabled()) {
							log.debug("list: "+newRegion.travelThru);
							log.debug("entry:"+newMsg);
						}
						*/
					}
				}
			}
			if (curRegion.travelThruShips != null && curRegion.travelThruShips.size() > 0) {
				if (newRegion.travelThruShips == null) {
					newRegion.travelThruShips = CollectionFactory.createLinkedList();
				}
				for (Iterator iter = curRegion.travelThruShips.iterator(); iter.hasNext(); ) {
					Message curMsg = (Message)iter.next();
					Message newMsg = null;
					try {
						newMsg = new Message((ID)curMsg.getID().clone());
					} catch (CloneNotSupportedException e) {
					}
					Message.merge(curGD, curMsg, newGD, newMsg);
					// 2002.02.21 pavkovic: prevent duplicate entries
					if(!newRegion.travelThruShips.contains(newMsg)) {
						newRegion.travelThruShips.add(newMsg);
					} else {
						log.warn("Region.merge(): Duplicate message \""+newMsg.getText()+"\", removing it.");
						/*
						if(log.isDebugEnabled()) {
							log.debug("list: "+newRegion.travelThruShips);
							log.debug("entry:"+newMsg);
						}
						*/
					}
				}
			}
		}
	}

	public Unit getUnit(ID id) {
		if(ZeroUnit.ZERO_ID.equals(id)) {
			return getZeroUnit();
		} else {
			return super.getUnit(id);
		}
	}
}
