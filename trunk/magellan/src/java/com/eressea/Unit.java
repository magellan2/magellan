// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Ulrich K�ster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.eressea.relation.AttackRelation;
import com.eressea.relation.EnterRelation;
import com.eressea.relation.InterUnitRelation;
import com.eressea.relation.ItemTransferRelation;
import com.eressea.relation.LeaveRelation;
import com.eressea.relation.PersonTransferRelation;
import com.eressea.relation.RecruitmentRelation;
import com.eressea.relation.TeachRelation;
import com.eressea.relation.TransferRelation;
import com.eressea.relation.TransportRelation;
import com.eressea.relation.UnitContainerRelation;
import com.eressea.relation.UnitRelation;
import com.eressea.rules.Eressea;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.Race;
import com.eressea.rules.SkillType;
import com.eressea.util.Cache;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Direction;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.EresseaRaceConstants;
import com.eressea.util.comparator.LinearUnitTempUnitComparator;
import com.eressea.util.Locales;
import com.eressea.util.OrderParser;
import com.eressea.util.OrderToken;
import com.eressea.util.OrderTokenizer;
import com.eressea.util.OrderWriter;
import com.eressea.util.ROCollection;
import com.eressea.util.ROIterator;
import com.eressea.util.comparator.SortIndexComparator;
import com.eressea.util.Sorted;
import com.eressea.util.TagMap;
import com.eressea.util.Taggable;
import com.eressea.util.logging.Logger;


public class Unit extends DescribedObject implements HasRegion, EresseaOrderConstants, Sorted, Taggable {
	private final static Logger log = Logger.getInstance(Unit.class);

	private final static String CONFIRMEDTEMPCOMMENT = ";"+OrderWriter.CONFIRMEDTEMP;

	/** The unit does not possess horses */
	public static final int CAP_NO_HORSES = Integer.MIN_VALUE;
	/** The unit is not sufficiently skilled in horse riding */
	public static final int CAP_UNSKILLED = CAP_NO_HORSES + 1;
	public String privDesc = null; // private description
	public Race race = null;
	public Race realRace = null;

	/** an object encapsulation  the orders of this unit as <tt>String</tt> objects */
	protected Orders ordersObject = new Orders();

	//protected List orders = null;

	public boolean ordersAreNull() {
		return ordersObject.ordersAreNull();
	}

	public boolean ordersHaveChanged() {
		return ordersObject.ordersHaveChanged();
	}
	public void setOrdersChanged(boolean changed) {
		ordersObject.setOrdersChanged(changed);
	}

	public void clearOrders() {
		ordersObject.clearOrders();
	}

	public void removeOrderAt(int i) {
		ordersObject.removeOrderAt(i);
	}

	public void addOrderAt(int i,String newOrders) {
		ordersObject.addOrderAt(i, newOrders);
	}

	public void addOrders(String newOrders) {
		ordersObject.addOrders(newOrders);
	}

	public void addOrders(Collection newOrders) {
		ordersObject.addOrders(newOrders);
	}

	public void setOrders(Collection newOrders) {
		ordersObject.setOrders(newOrders);
	}

	public Collection getOrders() {
		return new ROCollection(ordersObject.getOrders());
	}

	public int persons = 1;
	public int guard = 0;
	public static final int GUARDFLAG_WOOD = 4;
	public Building siege = null;		// belagert
	public int stealth = -1;			// getarnt
	public int aura = -1;
	public int auraMax = -1;
	public int combatStatus = -1;		// Kampfstatus
	public boolean unaided = false;		// if attacked, this unit will not be helped by allied units
	public boolean hideFaction = false; // Parteitarnung
	public Unit follows = null;			// folgt-Tag
	public String health = null;
	public boolean isStarving = false;	// hunger-Tag
	/**
	 * The cache object containing cached information that may be not
	 * related enough to be encapsulated as a function and is time
	 * consuming to gather.
	 */
	public com.eressea.util.Cache cache = null;
	/**
	 * Messages directly sent to this unit. The list contains
	 * instances of class <tt>Message</tt> with type -1 and only the
	 * text set.
	 */
	public List unitMessages = null;
	/**
	 * A map for storing unknown tags.
	 */
	private TagMap externalMap = null;
	/**
	 * A list containing <tt>String</tt> objects, specifying
	 * effects on this <tt>Unit</tt> object.
	 */
	public List effects = null;
	/**
	 * true indicates that the unit has orders confirmed by an user.
	 */
	public boolean ordersConfirmed = false;
	public Map skills = null;		// maps SkillType.getID() objects to Skill objects
	protected boolean skillsCopied = false;
	/**
	 * The items carried by this unit. The keys are the IDs of the
	 * item's type, the values are the Item objects themselves.
	 */
	public Map items = null;
	/**
	 * The spells known to this unit. The keys are the IDs of the
	 * spells, the values are the Spell objects themselves.
	 */
	public Map spells = null;
	/**
	 * Contains the spells this unit has set for use in a combat.
	 * This map contains data if a unit has a magic skill and has
	 * actively set combat spells. The values in this map are objects
	 * of type CombatSpell, the keys are their ids.
	 */
	public Map combatSpells = null;
	/**
	 * The group this unit belongs to.
	 */
	private Group group = null;
	/**
	 * Sets the group this unit belongs to.
	 */
	public void setGroup(Group g) {
		if (this.group != null) {
			this.group.removeUnit(this.getID());
		}
		this.group = g;
		if (this.group != null) {
			this.group.addUnit(this);
		}
	}

	/**
	 * Returns the group this unit belongs to.
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * The previous id of this unit.
	 */
	private UnitID alias = null;
	/**
	 * Sets an alias id for this unit.
	 */
	public void setAlias(UnitID id) {
		this.alias = id;
	}

	/**
	 * Returns the alias, i.e. the id of this unit it had in the last
	 * turn (e.g. after a NUMMER order).
	 *
	 * @returns the alias or null, if the id did not change.
	 */
	public UnitID getAlias() {
		return alias;
	}

	/**
	 * Returns the item of the specified type if the unit owns such an
	 * item. If not, null is returned.
	 */
	public Item getItem(ItemType type) {
		Item i = null;
		if (items != null) {
			i = (Item)items.get(type.getID());
		}
		return i;
	}

	/**
	 * Indicates that this unit belongs to a different faction than
	 * it pretends to. A unit cannot
	 * disguise itself as a different faction and at the same time be
	 * a spy of another faction, therefore, setting this attribute to
	 * true results in having the guiseFaction attribute set to null.
	 */
	private boolean isSpy = false;
	/**
	 * Sets whether is unit really belongs to its unit or only
	 * pretends to do so. A
	 */
	public void setSpy(boolean bool) {
		this.isSpy = bool;
		if (this.isSpy == true) {
			this.setGuiseFaction(null);
		}
	}

	/**
	 * Returns whether this unit only pretends to belong to its
	 * faction.
	 */
	public boolean isSpy() {
		return this.isSpy;
	}

	/**
	 * If this unit is disguised and pretends to belong to a different
	 * faction this field holds that faction, else it is null.
	 */
	private Faction guiseFaction = null;
	/**
	 * Sets the faction this unit pretends to belong to. A unit cannot
	 * disguise itself as a different faction and at the same time be
	 * a spy of another faction, therefore, setting a value other than
	 * null results in having the spy attribute set to false.
	 */
	public void setGuiseFaction(Faction f) {
		this.guiseFaction = f;
		if (f != null) {
			this.setSpy(false);
		}
	}

	/**
	 * Returns the faction this unit pretends to belong to. If the
	 * unit is not disguised null is returned.
	 */
	public Faction getGuiseFaction() {
		return this.guiseFaction;
	}

	/**
	 * Adds an item to the unit. If the unit already has an item of
	 * the same type, the item is overwritten with the specified item
	 * object.
	 *
	 * @returns the specified item i.
	 */
	public Item addItem(Item i) {
		if (items == null) {
			items = CollectionFactory.createOrderedHashtable();
		}
		items.put(i.getType().getID(), i);
		invalidateCache();
		return i;
	}

	/**
	 * The temp id this unit had before becoming a real unit.
	 */
	private UnitID tempID = null;
	/**
	 * Sets the temp id this unit had before becoming a real unit.
	 */
	public void setTempID(UnitID id) {
		this.tempID = id;
	}

	/**
	 * Returns the id the unit had when it was still a temp unit. This
	 * id is only set in the turn after the unit turned from a temp
	 * unit into to a real unit.
	 *
	 * @returns the temp id or null, if this unit was no temp unit in
	 * the previous turn.
	 */
	public UnitID getTempID() {
		return this.tempID;
	}

	/**
	 * The region this unit is currently in.
	 */
	protected Region region = null;
	/**
	 * Sets the region this unit is in. If this unit already has a
	 * different region set it removes itself from the collection of
	 * units in that region.
	 */
	public void setRegion(Region r) {
		if (r != getRegion()) {
			if (this.region != null)
				this.region.removeUnit(this.getID());
			if (r != null)
				r.addUnit(this);
			this.region = r;
		}
	}

	/**
	 * Returns the region this unit is staying in.
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * The faction this unit belongs to.
	 */
	private Faction  faction = null;
	/**
	 * Sets the faction for this unit. If this unit already has a
	 * different faction set it removes itself from the collection of
	 * units in that faction.
	 */
	public void setFaction(Faction faction) {
		if (faction != getFaction()) {
			if (this.faction != null)
				this.faction.removeUnit(this.getID());
			if (faction != null)
				faction.addUnit(this);
			this.faction = faction;
		}
	}

	/**
	 * Returns the faction this unit belongs to.
	 */
	public Faction getFaction() {
		return faction;
	}

	/**
	 * The building this unit stays in.
	 */
	private Building building = null;
	/**
	 * Sets the building this unit is staying in. If the unit already
	 * is in another building this method removes it from the unit
	 * collection of that building.
	 */
	public void setBuilding(Building building) {
		if (this.building != null) {
			this.building.removeUnit(this.getID());
		}
		this.building = building;
		if (this.building != null) {
			this.building.addUnit(this);
		}
	}

	/**
	 * Returns the building this unit is staying in.
	 */
	public Building getBuilding() {
		return building;
	}

	/**
	 * The ship this unit is on.
	 */
	private Ship ship = null;
	/**
	 * Sets the ship this unit is on. If the unit already is on
	 * another ship this method removes it from the unit
	 * collection of that ship.
	 */
	public void setShip(Ship ship) {
		if (this.ship != null) {
			this.ship.removeUnit(this.getID());
		}
		this.ship = ship;
		if (this.ship != null) {
			this.ship.addUnit(this);
		}
	}

	/**
	 * Returns the ship this unit is on.
	 */
	public Ship getShip() {
		return ship;
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
	 * A unit dependent prefix to be prepended to this faction's
	 * race name.
	 */
	private String raceNamePrefix = null;
	/**
	 * Sets the unit dependent prefix for the race name.
	 */
	public void setRaceNamePrefix(String prefix) {
		this.raceNamePrefix = prefix;
	}

	/**
	 * Returns the unit dependent prefix for the race name.
	 */
	public String getRaceNamePrefix() {
		return this.raceNamePrefix;
	}

	/**
	 * Returns the name of this unit's race including the prefixes of
	 * itself, its faction and group if it has such and those prefixes
	 * are set.
	 *
	 * @returns the name or null if this unit's race or its name is
	 * not set.
	 */
	public String getRaceName(GameData data) {
		if (this.race != null) {
			if (this.getRaceNamePrefix() != null) {
				return data.getTranslationOrKeyIfNull(this.getRaceNamePrefix()) + this.race.getName().toLowerCase();
			} else {
				if (this.group != null && this.group.getRaceNamePrefix() != null) {
					return data.getTranslationOrKeyIfNull(this.group.getRaceNamePrefix()) + this.race.getName().toLowerCase();
				} else {
					if (this.faction != null && this.faction.getRaceNamePrefix() != null) {
						return data.getTranslationOrKeyIfNull(this.faction.getRaceNamePrefix()) + this.race.getName().toLowerCase();
					} else {
						return this.race.getName();
					}
				}
			}
		}
		return null;
	}

	/**
	 * A map containing all temp units created by this unit.
	 */
	private Map tempUnits = null;
	/**
	 * A collection view of the temp units.
	 */
	private Collection tempUnitCollection = null;
	/**
	 * Returns the child temp units created by this unit's orders.
	 */
	public Collection tempUnits() {
		if (tempUnitCollection == null) {
			tempUnitCollection = new ROCollection(tempUnits);
		}
		return tempUnitCollection;
	}

	/**
	 * Return the child temp unit with the specified ID.
	 */
	public TempUnit getTempUnit(ID id) {
		if (tempUnits != null) {
			return (TempUnit)tempUnits.get(id);
		}
		return null;
	}

	/**
	 * Adds a temp unit to this unit.
	 */
	private TempUnit addTemp(TempUnit u) {
		if (tempUnits == null) {
			tempUnits = CollectionFactory.createHashtable();
			// enforce the creation of a new collection view
			tempUnitCollection = null;
		}
		tempUnits.put(u.getID(), u);
		return u;
	}

	/**
	 * Removes a temp unit from the list of child temp units created
	 * by this unit's orders.
	 */
	private Unit removeTemp(ID id) {
		Unit ret = null;
		if (tempUnits != null) {
			ret = (Unit)tempUnits.remove(id);
			if (tempUnits.isEmpty()) {
				tempUnits = null;
			}
		}
		return ret;
	}

	/**
	 * Clears the list of temp units created by this unit. Clears only
	 * the caching collection, does not perform clean-up like
	 * deleteTemp() does.
	 */
	private void clearTemps() {
		if (tempUnits != null) {
			tempUnits.clear();
			tempUnits = null;
		}
	}


	/**
	 * Returns alle orders including the orders necessary to issue the
	 * creation of all the child temp units of this unit.
	 */
	public List getCompleteOrders() {
		List cmds = CollectionFactory.createLinkedList();
		cmds.addAll(ordersObject.getOrders());
		cmds.addAll(getTempOrders());
		return cmds;
	}

	/**
	 * Returns the orders necessary to issue the creation of all the
	 * child temp units of this unit.
	 */
	public List getTempOrders() {
		List cmds = CollectionFactory.createLinkedList();
		for (Iterator iter = tempUnits().iterator(); iter.hasNext(); ) {
			TempUnit u = (TempUnit)iter.next();
			cmds.add(getOrder(O_MAKE) + " " + getOrder(O_TEMP) + " " + u.getID().toString());
			cmds.addAll(u.getOrders());
			if(u.ordersConfirmed) {
				cmds.add(CONFIRMEDTEMPCOMMENT);
			}
			cmds.add(getOrder(O_END));
		}
		return cmds;
	}

	/**
	 * Creates a new temp unit with this unit as the parent.
	 * The temp unit is fully initialised, i.e. it is added to the
	 * region units collection in the specified game data,it
	 * inherits the faction, building or ship, region, faction stealth
	 * status, group, race and combat status settings and adds itself
	 * to the corresponding unit collections.
	 */
	public TempUnit createTemp(ID id) {
		if (((UnitID)id).intValue() >= 0) {
			throw new IllegalArgumentException("Unit.createTemp(): cannot create temp unit with non-negative ID.");
		}
		TempUnit t = new TempUnit(id, this);
		this.addTemp(t);
		t.persons = 0;
		t.hideFaction = this.hideFaction;
		t.combatStatus = this.combatStatus;
		t.ordersConfirmed = false;
		if (this.race != null) {
			t.race = this.race;
		}
		if (this.realRace != null) {
			t.realRace = this.realRace;
		}
		if (this.getRegion() != null) {
			t.setRegion(this.getRegion());
		}
		if (this.getShip() != null) {
			t.setShip(this.getShip());
		} else
			if (this.getBuilding() != null) {
				t.setBuilding(this.getBuilding());
			}
		if (this.getFaction() != null) {
			t.setFaction(this.getFaction());
		}
		if (this.group != null) {
			t.setGroup(this.group);
		}
		return t;
	}

	/**
	 * Removes a temp unit with this unit as the parent completely
	 * from the game data.
	 */
	public void deleteTemp(ID id, GameData data) {
		TempUnit t = (TempUnit)this.removeTemp(id);
		if (t != null) {
			t.persons = 0;
			t.race = null;
			t.realRace = null;
			t.setRegion(null);
			t.setShip(null);
			t.setBuilding(null);
			t.setFaction(null);
			t.setGroup(null);
			if (t.cache != null) {
				t.cache.clear();
				t.cache = null;
			}
			t.ordersObject.removeOrders();
			t.setParent(null);
		}
	}

	/**
	 * Resets the cache of this unit to its uninitalized state.
	 */
	private void invalidateCache() {
		if (cache != null) {
			cache.modifiedSkills = null;
			cache.modifiedItems = null;
			cache.unitWeight = -1;
			cache.modifiedUnitWeight = -1;
			cache.modifiedPersons = -1;
		}
	}

	/**
	 * Returns a Collection over the relations this unit has to other
	 * units. The iterator returns <tt>UnitRelation</tt> objects. An
	 * empty iterator is returned if the relations have not been set
	 * up so far or if there are no relations.
	 * To have the relations to other units properly set up the
	 * refreshRelations() method has to be invoked.
	 */
	public Collection getRelations() {
		if (cache != null && cache.relations != null) {
			return new ROCollection(cache.relations);
		}
		return new ROCollection();
	}

	/**
	 * Returns a Collection over the relations this unit has to other
	 * units. The collection consist of  <tt>UnitRelation</tt> objects. 
	 * The UnitRelation objects are filtered by the given relation class.
	 */
	public Collection getRelations(Class relationClass) {
		Collection ret = CollectionFactory.createLinkedList();
		for(Iterator iter = getRelations().iterator(); iter.hasNext(); ) {
			Object relation = iter.next();
			if(relationClass.isInstance(relation)) {
				ret.add(relation);
			}
		}
		return ret;
	}
	
	public UnitRelation addRelation(UnitRelation rel) {
		if (cache == null) {
			cache = new Cache();
		}
		if (cache.relations == null) {
			cache.relations = CollectionFactory.createLinkedList();
		}
		cache.relations.add(rel);
		invalidateCache();
		return rel;
	}

	public UnitRelation removeRelation(UnitRelation rel) {
		UnitRelation r = null;
		if (cache != null && cache.relations != null) {
			if (cache.relations.remove(rel)) {
				r = rel;
				invalidateCache();
			}
		}
		return r;
	}

	/**
	 * Recursively retrieves all units that are related to this unit
	 * via one of the specified relations.
	 * @param units all units gathered so far to prevent loops.
	 * @param relations a set of classes naming the types of relations
	 *  that are eligible for regarding a unit as related to some
	 *  other unit.
	 */
	public void getRelatedUnits(Set units, Set relations) {
		units.add(this);
		for (Iterator iter = this.getRelations().iterator(); iter.hasNext(); ) {
			UnitRelation rel = (UnitRelation)iter.next();
			if (relations.contains(rel.getClass())) {
				Unit src = rel.source;
				Unit target = null;
				if (rel instanceof InterUnitRelation) {
					target = ((InterUnitRelation)rel).target;
				}

				if (units.add(src)) {
					src.getRelatedUnits(units, relations);
				}
				if (units.add(target)) {
					target.getRelatedUnits(units, relations);
				}
			}
		}
	}

	public List getModifiedMovement() {
		if(this.ordersAreNull()) {
			return Collections.EMPTY_LIST;
		}
		
		String nachOrder = getOrder(EresseaOrderConstants.O_MOVE);
		String routeOrder = getOrder(EresseaOrderConstants.O_ROUTE);
		int nachLength = nachOrder.length();
		int routeLength = routeOrder.length();
		
		// find move order
		String cmd = null;
		for (Iterator iter = this.getOrders().iterator(); iter.hasNext();) {
			// also remove @ from string to make "@nach so so so" interpretable
			// we dont use OrderTokenizer here because of performance
			String s = ((String)iter.next()).replace('@',' ').trim();
			if (s.regionMatches(true, 0, nachOrder, 0, nachLength) || s.regionMatches(true, 0, routeOrder, 0, routeLength)) {
				cmd = s;
				break;
			}
		}
		if(cmd == null) {
			// we did not find 
			return Collections.EMPTY_LIST;
		}
		
		List modifiedMovement = CollectionFactory.createArrayList(2);
		// dissect the order into pieces to detect which way the unit
		// is taking
		OrderTokenizer ct = new OrderTokenizer(new StringReader(cmd));
		OrderToken token = ct.getNextToken();
		if (token.ttype == OrderToken.TT_UNDEF && (token.equalsToken(nachOrder) || token.equalsToken(routeOrder))) {
			Region r = this.getRegion();
			Coordinate c = new Coordinate((Coordinate)r.getID());
			modifiedMovement.add(c);
			
			token = ct.getNextToken();
			while (token.ttype == OrderToken.TT_UNDEF) {
				int dir = Direction.toInt(token.getText());
				if (dir != -1) {
					c = new Coordinate(c); // make c a new copy
					c.translate(Direction.toCoordinate(dir));
					modifiedMovement.add(c);
				} else {
					break;
				}
				token = ct.getNextToken();
			}
		}
		return modifiedMovement;

	}


	public Ship getModifiedShip() {
		for (Iterator iter = getRelations().iterator(); iter.hasNext(); ) {
			UnitRelation rel = (UnitRelation)iter.next();
			if (rel instanceof UnitContainerRelation) {
				UnitContainerRelation ucr = (UnitContainerRelation)rel;
				if (ucr instanceof EnterRelation) {
					if(ucr.target instanceof Ship) {
						// make fast return: first Ship-EnterRelation wins
						return (Ship) ucr.target;
					}
				} else if (ucr instanceof LeaveRelation &&
						   ucr.target.equals(getShip())) {
					// we only left our ship
					return null;
				}
			}
		}
		// we stayed in our ship
		return getShip();
	}

	public Skill getModifiedSkill(SkillType type) {
		Skill s = null;
		if (cache == null || cache.modifiedSkills == null) {
			// the cache is invalid, refresh
			refreshModifiedSkills();
		}
		if (cache != null && cache.modifiedSkills != null) {
			s = (Skill)cache.modifiedSkills.get(type.getID());
		}
		return s;
	}

	/**
	 * Returns the skills of this unit as they would appear after the
	 * orders for person transfers are processed.
	 *
	 * @param an unmodifiable collection of the skills. null is
	 * returned if the person transfers do not allow an accurate
	 * calculation of the skill points an levels.
	 */
	public Collection getModifiedSkills() {
		if (cache == null || cache.modifiedSkills == null) {
			refreshModifiedSkills();
		}
		if (cache != null) {
			if (cache.modifiedSkills != null) {
				return new ROCollection(cache.modifiedSkills);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Updates the cache with the skills of this unit as they would
	 * appear after the orders for person transfers are processed. If
	 * the cache object or the modified skills field is still null
	 * after invoking this function, the skill modifications cannot be
	 * determined accurately.
	 */
	private synchronized void refreshModifiedSkills() {
		// create the cache
		if (cache == null) {
			cache = new Cache();
		}

		// clear existing modified skills
		// there is special case: to reduce memory consumption
		// cache.modifiedSkills can point to the real skills and
		// you don't want to clear THAT
		// that also means that this should be the only place where
		// cache.modifiedSkills is modified
		if (cache.modifiedSkills != null && cache.modifiedSkills != this.skills) {
			cache.modifiedSkills.clear();
		}

		// if there are no relations, cache.modfiedSkills can point
		// directly to the skills and we can bail out
		if (getRelations().isEmpty()) {
			cache.modifiedSkills = this.skills;
			return;
		}
		
		// get all related units (as set) and sort it in a list afterwards
		Set relatedUnits = CollectionFactory.createHashSet();
		Set relationTypes = CollectionFactory.createHashSet();
		relationTypes.add(PersonTransferRelation.class);
		relationTypes.add(RecruitmentRelation.class);
		this.getRelatedUnits(relatedUnits, relationTypes);

		/* sort related units according to report order */
		List sortedUnits = CollectionFactory.createLinkedList(relatedUnits);
		Collections.sort(sortedUnits, new LinearUnitTempUnitComparator(new SortIndexComparator(null)));

		/* clone units with all aspects relevant for skills */
		Map clones = CollectionFactory.createHashtable();
		for (Iterator iter = relatedUnits.iterator(); iter.hasNext(); ) {
			Unit u = (Unit)iter.next();
			Unit clone = null;
			try {
				clone = new Unit((ID)u.getID().clone());
				clone.persons = u.getPersons();
				clone.race = u.race;
				clone.realRace = u.realRace;
				clone.region = u.region;
				clone.isStarving = u.isStarving;
				for (Iterator skillIter = u.getSkills().iterator(); skillIter.hasNext(); ) {
					Skill s = (Skill)skillIter.next();
					clone.addSkill(new Skill(s.getType(), s.getPoints(), s.getLevel(), clone.persons, s.noSkillPoints()));
				}
			} catch (CloneNotSupportedException e) {
				// won't fail
			}
			clones.put(clone.getID(), clone);
		}

		// now modify the skills according to changes introduced by the relations

		/* indicates that a skill is lost through person transfers or
		 recruiting. May not be Integer.MIN_VALUE to avoid wrap-
		 around effects but should also be fairly negative so no
		 modifier can push it up to positive values.*/
		final int lostSkillLevel = (Integer.MIN_VALUE / 2);
		for (Iterator unitIter = sortedUnits.iterator(); unitIter.hasNext(); ) {
			Unit srcUnit = (Unit)unitIter.next();
			for (Iterator relationIter = srcUnit.getRelations().iterator(); relationIter.hasNext(); ) {
				UnitRelation unitRel = (UnitRelation)relationIter.next();
				if (!(unitRel.source.equals(srcUnit)) || !(unitRel instanceof PersonTransferRelation)) {
					continue;
				}
				PersonTransferRelation rel = (PersonTransferRelation)unitRel;
				Unit srcClone = (Unit)clones.get(srcUnit.getID());
				Unit targetUnit = (Unit)rel.target;
				Unit targetClone = (Unit)clones.get(targetUnit.getID());
				int transferredPersons = Math.max(0, Math.min(srcClone.getPersons(), rel.amount));
				if (transferredPersons == 0) {
					continue;
				}

				/* modify the target clone */
				/* first modify all skills that are available in the
				 target clone */
				for (Iterator skills = targetClone.getSkills().iterator(); skills.hasNext(); ) {
					Skill targetSkill = (Skill)skills.next();
					Skill srcSkill = srcClone.getSkill(targetSkill.getType());
					int skillModifier = targetSkill.getModifier(targetClone);
					if (srcSkill == null) {
						/* skill exists only in the target clone, this
						 is equivalent to a target skill at 0.
						 Level is set to lostSkillLevel to avoid
						 confusion about level modifiers in case of
						 noSkillPoints. If skill points are relevant
						 this value is ignored anyway. */
						srcSkill = new Skill(targetSkill.getType(), 0, lostSkillLevel, srcClone.getPersons(), targetSkill.noSkillPoints());
					}
					if (targetSkill.noSkillPoints()) {
						/* Math.max(0, ...) guarantees that the true
						 skill level cannot drop below 0. This also
						 important to handle the Integer.MIN_VALUE
						 case below */
						int transferredSkillFactor = Math.max(0, srcSkill.getLevel() - skillModifier) * transferredPersons;
						int targetSkillFactor = Math.max(0, targetSkill.getLevel() - skillModifier) * targetClone.getPersons();
						int newSkillLevel = (int)(((float)(transferredSkillFactor + targetSkillFactor)) / (float)(transferredPersons + targetClone.getPersons()));
						/* newSkillLevel == 0 means that that the skill
						 is lost by this transfer but we may not set
						 the skill level to 0 + skillModifier since
						 this would indicate an existing skill
						 depending on the modifier. Thus
						 lostSkillLevel is used to distinctly
						 mark the staleness of this skill. */
						targetSkill.setLevel(newSkillLevel > 0 ? (newSkillLevel + skillModifier) : lostSkillLevel);
					} else {
						targetSkill.setPoints(targetSkill.getPoints() + (int)((float)srcSkill.getPoints() * (float)transferredPersons / (float)srcClone.getPersons()));
					}
				}
				/* now modify the skills that only exist in the source
				 clone */
				for (Iterator skills = srcClone.getSkills().iterator(); skills.hasNext(); ) {
					Skill srcSkill = (Skill)skills.next();
					Skill targetSkill = (Skill)targetClone.getSkill(srcSkill.getType());
					if (targetSkill == null) {
						/* skill exists only in the source clone, this
						 is equivalent to a source skill at 0.
						 Level is set to lostSkillLevel to avoid
						 confusion about level modifiers in case of
						 noSkillPoints. If skill points are relevant
						 this value is ignored anyway. */
						targetSkill = new Skill(srcSkill.getType(), 0, lostSkillLevel, targetClone.getPersons(), srcSkill.noSkillPoints());
						targetClone.addSkill(targetSkill);
						if (srcSkill.noSkillPoints()) {
							/* Math.max(0, ...) guarantees that the true
							 skill level cannot drop below 0. This also
							 important to handle the lostSkillLevel
							 case below */
							int skillModifier = srcSkill.getModifier(srcClone);
							int transferredSkillFactor = Math.max(0, srcSkill.getLevel() - skillModifier) * transferredPersons;
							int newSkillLevel = (int)(((float)transferredSkillFactor) / (float)(transferredPersons + targetClone.getPersons()));
							/* newSkillLevel == 0 means that that the skill
							 is lost by this transfer but we may not set
							 the skill level to 0 + skillModifier since
							 this would indicate an existing skill
							 depending on the modifier. Thus
							 lostSkillLevel is used to distinctly
							 mark the staleness of this skill. */
							targetSkill.setLevel(newSkillLevel > 0 ? (newSkillLevel + skillModifier) : lostSkillLevel);
						} else {
							int newSkillPoints = (int)(srcSkill.getPoints() * (float)((float)transferredPersons / (float)srcClone.getPersons()));
							targetSkill.setPoints(newSkillPoints);
						}
					}
					/* modify the skills in the source clone (no extra
					 loop for this) */
					if (!srcSkill.noSkillPoints()) {
						int transferredSkillPoints = (int)((float)srcSkill.getPoints() * (float)transferredPersons / (float)srcClone.getPersons());
						srcSkill.setPoints(srcSkill.getPoints() - transferredSkillPoints);
					}
				}

				srcClone.persons = srcClone.getPersons() - transferredPersons;
				targetClone.persons += transferredPersons;
			}
		}

		/* modify the skills according to recruitment */
		Unit clone = (Unit)clones.get(this.getID());
		/* update the person and level information in all clone skills */
		if (clone.getSkills().size() > 0) {
			this.cache.modifiedSkills = CollectionFactory.createHashtable();
			for (Iterator skills = clone.getSkills().iterator(); skills.hasNext(); ) {
				Skill skill = (Skill)skills.next();
				skill.setPersons(clone.persons);
				/* When skill points are relevant, all we did up to
				 now, was to keep track of these while the skill
				 level was ignored - update it now */
				if (!skill.noSkillPoints()) {
					skill.setLevel(skill.getLevel(clone, false));
				} else {
					/* If skill points are not relevant we always
					 take skill modifiers into account but we marked
					 'lost' skills by Integer.MIN_VALUE which has to
					 be fixed here */
					if (skill.getLevel() == lostSkillLevel) {
						skill.setLevel(0);
					}
				}
				/* inject clone skills into real unit (no extra loop for
				 this */
				if (skill.getPoints() > 0 || skill.getLevel() > 0) {
					this.cache.modifiedSkills.put(skill.getType().getID(), skill);
				}
			}
		}
	}

	/**
	 * Returns the unit container this unit is in. The type of unit
	 * container returned (faction, region, building or ship) is equal
	 * to the class of the uc parameter.
	 */
	public UnitContainer getUnitContainer(UnitContainer uc) {
		if (uc instanceof Faction) {
			return getFaction();
		} else
			if (uc instanceof Region) {
				return getRegion();
			} else
				if (uc instanceof Building) {
					return getBuilding();
				} else
					if (uc instanceof Ship) {
						return getShip();
					}
		return null;
	}

	/**
	 * Returns the skill of the specified type if the unit has such
	 * a skill, else null is returned.
	 */
	public Skill getSkill(SkillType type) {
		Skill s = null;
		if (skills != null) {
			s = (Skill)skills.get(type.getID());
		}
		return s;
	}

	/**
	 * Adds a skill to unit's collection of skills. If the unit
	 * already has a skill of the same type it is overwritten with the
	 * the new skill object.
	 *
	 * @returns the specified skill s.
	 */
	public Skill addSkill(Skill s) {
		if (skills == null) {
			skills = CollectionFactory.createOrderedHashtable();
		}
		skills.put(s.getType().getID(), s);
		return s;
	}

	/**
	 * Returns all skills this unit has.
	 *
	 * @return a collection of Skill objects.
	 */
	public Collection getSkills() {
		return new ROCollection(this.skills);
	}

	/**
	 * Removes all skills from this unit.
	 */
	public void clearSkills() {
		if (skills != null) {
			skills.clear();
			skills = null;
			if (cache != null && cache.modifiedSkills != null) {
				cache.modifiedSkills.clear();
				cache.modifiedSkills = null;
			}
		}
	}

	/**
	 * Copies the skills of the given unit. Does not empty this unit's skills.
	 */
	public static void copySkills(Unit u, Unit v) {
		copySkills(u, v, true);
	}

	public static void copySkills(Unit u, Unit v, boolean sortOut) {
		v.skillsCopied = true;
		if (u.skills != null) {
			Iterator it = u.getSkills().iterator();
			while (it.hasNext()) {
				Skill sk = (Skill)it.next();
				// sort out if changed to non-existent
				if (sortOut && sk.isLostSkill()) {
					continue;
				}
				Skill newSkill = new Skill(sk.getType(), sk.getPoints(), sk.getLevel(), v.persons, sk.noSkillPoints());
				v.addSkill(newSkill);
			}
		}
	}

	/**
	 * Returns all the items this unit possesses.
	 *
	 * @returns a collection of Item objects.
	 */
	public Collection getItems() {
		return new ROCollection(this.items);
	}

	/**
	 * Removes all items from this unit.
	 */
	public void clearItems() {
		if (items != null) {
			items.clear();
			items = null;
			invalidateCache();
		}
	}

	/**
	 * Returns the item of the specified type as it would appear after
	 * the orders of this unit have been processed, i.e. the amount of
	 * the item might be modified by transfer orders. If the unit does
	 * not have an item of the specified type nor is given one by
	 * some other unit, null is returned.
	 */
	public Item getModifiedItem(ItemType type) {
		Item i = null;
		if (cache == null || cache.modifiedItems == null) {
			refreshModifiedItems();
		}
		if (cache != null && cache.modifiedItems != null) {
			i = (Item)cache.modifiedItems.get(type.getID());
		}
		return i;
	}

	/**
	 * Returns a collection of the itemrelations concerning the given Item.
	 *
	 * @returns a collection of ItemTransferRelation objects.
	 */
	public List getItemTransferRelations(Item item) {
		List ret = CollectionFactory.createArrayList(getRelations().size());

		for(Iterator iter = getRelations(ItemTransferRelation.class).iterator(); iter.hasNext();) {
			ItemTransferRelation rel = (ItemTransferRelation) iter.next();
			if(rel.itemType.equals(item.getType())) {
				ret.add(rel);
			}
		}
		return ret;
	}

	/**
	 * Returns a collection of the personrelations associated with this unit
	 *
	 * @returns a collection of PersonTransferRelation objects.
	 */
	public List getPersonTransferRelations() {
		return (List) getRelations(PersonTransferRelation.class);
		/*
		List ret = CollectionFactory.createArrayList(getRelations().size());
		for(Iterator iter = getRelations().iterator(); iter.hasNext();) {
			UnitRelation rel = (UnitRelation) iter.next();
			if(rel instanceof PersonTransferRelation) {
				ret.add(rel);
			}
		}
		return ret;
		*/
	}

	/**
	 * Returns the items of this unit as they would appear after the
	 * orders of this unit have been processed.
	 *
	 * @return a collection of Item objects.
	 */
	public Collection getModifiedItems() {
		if (cache == null || cache.modifiedItems == null) {
			refreshModifiedItems();
		}
		return new ROCollection(cache.modifiedItems);
	}

	/**
	 * Deduces the modified items from the current items and the
	 * relations between this and other units.
	 */
	private synchronized void refreshModifiedItems() {
		// 0. clear existing data structures
		if (cache != null && cache.modifiedItems != null) {
			cache.modifiedItems.clear();
		}
		if (cache == null) {
			cache = new Cache();
		}
		if (cache.modifiedItems == null) {
			cache.modifiedItems = CollectionFactory.createHashtable();
		}

		// 1. check whether there is anything to do at all
		if ((items == null || items.size() == 0) && getRelations().isEmpty()) {
			return;
		}

		// 2. clone items
		for(Iterator iter = getItems().iterator(); iter.hasNext(); ) {
			Item i = (Item)iter.next();
			cache.modifiedItems.put(i.getType().getID(), new Item(i.getType(), i.getAmount()));
		}

		// 3. now check relations for possible modifications
		for(Iterator iter = getRelations().iterator(); iter.hasNext(); ) {
			UnitRelation rel = (UnitRelation)iter.next();
			if (rel instanceof ItemTransferRelation) {
				ItemTransferRelation itr = (ItemTransferRelation)rel;
				Item modifiedItem = (Item)cache.modifiedItems.get(itr.itemType.getID());
				if (modifiedItem != null) {	// the transferred item can be found among this unit's items
					if (this.equals(itr.source)) {
						modifiedItem.setAmount(modifiedItem.getAmount() - itr.amount);
					} else {
						modifiedItem.setAmount(modifiedItem.getAmount() + itr.amount);
					}
				} else { // the transferred item is not among the items the unit already has
					if (this.equals(itr.source)) {
						modifiedItem = new Item(itr.itemType, -itr.amount);
					} else {
						modifiedItem = new Item(itr.itemType, itr.amount);
					}
					cache.modifiedItems.put(itr.itemType.getID(), modifiedItem);
				}
			}
		}

		/* 4. iterate again to mimick that recruit orders are
		 processed after give orders, not very nice but probably not
		 very expensive */
		for(Iterator iter = getRelations().iterator(); iter.hasNext(); ) {
			UnitRelation rel = (UnitRelation)iter.next();
			if (rel instanceof RecruitmentRelation) {
				RecruitmentRelation rr = (RecruitmentRelation)rel;
				Item modifiedItem = (Item)cache.modifiedItems.get(new StringID("Silber"));
				if (modifiedItem != null) {
					Race race = this.realRace;
					if (race == null) {
						race = this.race;
					}
					if (race != null && race.getRecruitmentCosts() > 0) {
						modifiedItem.setAmount(modifiedItem.getAmount() - rr.amount * race.getRecruitmentCosts());
					}
				}
			}
		}
	}

	public int getPersons() {
		return persons;
	}

	/**
	 * Returns the number of persons in this unit as it would be after
	 * the orders of this and other units have been processed since it
	 * may be modified by transfer orders.
	 */
	public int getModifiedPersons() {
		if (cache == null) {
			cache = new Cache();
		}
		if (cache.modifiedPersons == -1) {
			cache.modifiedPersons = this.getPersons();
			for (Iterator iter = getPersonTransferRelations().iterator(); iter.hasNext(); ) {
				PersonTransferRelation ptr = (PersonTransferRelation)iter.next();
				if (this.equals(ptr.source)) {
					cache.modifiedPersons -= ptr.amount;
				} else {
					cache.modifiedPersons += ptr.amount;
				}
			}
		}
		return cache.modifiedPersons;
	}

	/**
	 * Returns the weight of a unit with the specified number of
	 * persons, their weight and the specified items in GE * 100.
	 */
	public static int getWeight(int persons, float personWeight, Iterator items) {
		int weight = 0;
		while (items != null && items.hasNext()) {
			Item item = (Item)items.next();
			// pavkovic 2003.09.10: only take care about (possibly) modified items with positive amount
			if(item.getAmount() > 0) {
				weight += item.getAmount() * (int)(item.getType().getWeight() * 100);
			}
		}
		weight += (persons * (int)(personWeight * 100));
		return weight;
	}

	/**
	 * Returns the overall weight of this unit (persons and items) in
	 * GE * 100.
	 */
	public int getWeight() {
		if (cache == null) {
			cache = new Cache();
		}
		if (cache.unitWeight == -1) {
			cache.unitWeight = getWeight(this.getPersons(), this.realRace != null ? this.realRace.getWeight() : this.race.getWeight(), this.getItems().iterator());
		}
		return cache.unitWeight;
	}

	/**
	 * Returns the maximum payload in GE * 100 of this unit when it
	 * travels by horse.
	 * Horses, carts and persons are taken into account for this
	 * calculation.
	 * If the unit has a sufficient skill in horse riding but there
	 * are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 *
	 * @returns the payload in GE * 100, CAP_NO_HORSES if the unit
	 * does not possess horses or CAP_UNSKILLED if the unit is not
	 * sufficiently skilled in horse riding to travel on horseback.
	 */
	public int getPayloadOnHorse() {
		int capacity = 0;
		int horses = 0;
		Item i = getModifiedItem(new ItemType(StringID.create("Pferd")));
		if (i != null) {
			horses = i.getAmount();
		}
		if (horses <= 0) {
			return CAP_NO_HORSES;
		}

		int skillLevel = 0;
		Skill s = getModifiedSkill(new SkillType(StringID.create("Reiten")));
		if (s != null) {
			skillLevel = s.getLevel();
		}
		if (horses > skillLevel * getModifiedPersons() * 2) {
			return CAP_UNSKILLED;
		}

		int carts = 0;
		i = getModifiedItem(new ItemType(StringID.create("Wagen")));
		if (i != null) {
			carts = i.getAmount();
		}

		int horsesWithoutCarts = horses - carts * 2;
		if (horsesWithoutCarts >= 0) {
			capacity = (carts * 140 + horsesWithoutCarts * 20) * 100 - ((int)((this.realRace != null ? this.realRace.getWeight() : this.race.getWeight()) * 100)) * getModifiedPersons();
		} else {
			int cartsWithoutHorses = carts - horses / 2;
			horsesWithoutCarts = horses % 2;
			capacity = ((carts - cartsWithoutHorses) * 140 + horsesWithoutCarts * 20 - cartsWithoutHorses * 40) * 100 - ((int)((this.realRace != null ? this.realRace.getWeight() : this.race.getWeight()) * 100)) * getModifiedPersons();
		}

		return capacity;
	}

	/**
	 * Returns the maximum payload in GE * 100 of this unit when it
	 * travels on foot.
	 * Horses, carts and persons are taken into account for this
	 * calculation.
	 * If the unit has a sufficient skill in horse riding but there
	 * are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 * The calculation also takes into account that trolls can tow
	 * carts.
	 *
	 * @returns the payload in GE * 100, CAP_UNSKILLED if the unit is
	 * not sufficiently skilled in horse riding to travel on horseback.
	 */
	public int getPayloadOnFoot() {
		int capacity = 0;
		int horses = 0;
		Item i = getModifiedItem(new ItemType(StringID.create("Pferd")));

		if (i != null) {
			horses = i.getAmount();
		}
		if (horses < 0) {
			horses = 0;
		}

		int skillLevel = 0;
		Skill s = getModifiedSkill(new SkillType(StringID.create("Reiten")));
		if (s != null) {
			skillLevel = s.getLevel();
		}
		if (horses > (skillLevel * getModifiedPersons() * 4) + getModifiedPersons()) {
			// too many horses
			return CAP_UNSKILLED;
		}

		int carts = 0;
		i = getModifiedItem(new ItemType(StringID.create("Wagen")));
		if (i != null) {
			carts = i.getAmount();
		}
		if (carts < 0) {
			carts = 0;
		}

		int horsesWithoutCarts = 0;
		int cartsWithoutHorses = 0;
		if (skillLevel == 0) {
			// can't use carts!!!
			horsesWithoutCarts = horses;
			cartsWithoutHorses = carts;
		} else if (carts > horses / 2) {
			// too many carts
			cartsWithoutHorses = carts - (horses / 2);
		} else {
			// too many horses (or exactly right number)
			horsesWithoutCarts = horses - (carts * 2);
		}
		Race race = this.race;
		if (this.realRace != null) {
			race = this.realRace;
		}
		if (race == null || race.getID().equals(EresseaRaceConstants.R_TROLLE) == false) {
			capacity = ((carts - cartsWithoutHorses) * 140 + horsesWithoutCarts * 20 - cartsWithoutHorses * 40) * 100 + ((int)(race.getCapacity() * 100)) * getModifiedPersons();
		} else {
			int horsesMasteredPerPerson = (skillLevel * 4) + 1;
			int trollsMasteringHorses = horses / horsesMasteredPerPerson;
			if (horses % horsesMasteredPerPerson != 0) {
				trollsMasteringHorses++;
			}
			int cartsTowedByTrolls = Math.min((this.getModifiedPersons() - trollsMasteringHorses) / 4, cartsWithoutHorses);
			int trollsTowingCarts = cartsTowedByTrolls * 4;
			int untowedCarts = cartsWithoutHorses - cartsTowedByTrolls;
			capacity = ((carts - untowedCarts) * 140 + horsesWithoutCarts * 20 - untowedCarts * 40) * 100 + ((int)(race.getCapacity() * 100)) * (getModifiedPersons() - trollsTowingCarts);
		}

		return capacity;
	}

	/**
	 * Returns the weight of all items of this unit that are not
	 * horses or carts in GE * 100.
	 */
	public int getLoad() {
		int load = 0;
		ItemType horse = new ItemType(StringID.create("Pferd"));
		ItemType cart = new ItemType(StringID.create("Wagen"));
		for (Iterator iter = getItems().iterator(); iter.hasNext(); ) {
			Item i = (Item)iter.next();
			if (!i.getType().equals(horse) && !i.getType().equals(cart)) {
				// pavkovic 2003.09.10: only take care about (possibly) modified items with positive amount
				if(i.getAmount() > 0) {
					load += ((int)(i.getType().getWeight() * 100)) * i.getAmount();
				}
			}
		}
		return load;
	}

	/**
	 * Returns the weight of all items of this unit that are not
	 * horses or carts in GE * 100 based on the modified items.
	 */
	public int getModifiedLoad() {
		int load = 0;
		ItemType horse = new ItemType(StringID.create("Pferd"));
		ItemType cart = new ItemType(StringID.create("Wagen"));
		for (Iterator iter = getModifiedItems().iterator(); iter.hasNext(); ) {
			Item i = (Item)iter.next();
			// pavkovic 2003.09.10: only take care about modified items with positive amount
			if(i.getAmount() > 0) {
				if (!i.getType().equals(horse) && !i.getType().equals(cart)) {
					load += ((int)(i.getType().getWeight() * 100)) * i.getAmount();
				}
			}
		}
		// also take care of passengers 
		Map passengers = getPassengers();
		for (Iterator iter = passengers.values().iterator(); iter.hasNext(); ) {
			Unit passenger = (Unit)iter.next();
			//load += passenger.getWeight();
			load += passenger.getModifiedWeight();
		}
		return load;
	}

	/**
	 * Returns the number of regions this unit is able to travel
	 * within one turn based on the riding skill, horses, carts and
	 * load of this unit.
	 */
	public int getRadius() {
		// pavkovic 2003.10.02: use modified load here...int load = getLoad();
		int load = getModifiedLoad();
		int payload = getPayloadOnHorse();
		if (payload >= 0 && payload - load >= 0) {
			return 2;
		} else {
			payload = getPayloadOnFoot();
			if (payload >= 0 && payload - load >= 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Returns the overall weight (persons, items) of this unit in
	 * GE * 100 based on the modified items and persons.
	 */
	public int getModifiedWeight() {
		if (cache == null) {
			cache = new Cache();
		}
		if (cache.modifiedUnitWeight == -1) {
			cache.modifiedUnitWeight = getWeight(this.getModifiedPersons(), this.realRace != null ? this.realRace.getWeight() : this.race.getWeight(), this.getModifiedItems().iterator());
		}
		return cache.modifiedUnitWeight;
	}

	/**
	 * Returns all units this unit is transporting as passengers.
	 */
	public Map getPassengers() {
		Map passengers = CollectionFactory.createHashtable();
		for (Iterator iter = getRelations(TransportRelation.class).iterator(); iter.hasNext(); ) {
			TransportRelation tr = (TransportRelation) iter.next();
			if (this.equals(tr.source)) {
				passengers.put(tr.target.getID(), tr.target);
			}
		}

		return passengers;
	}

	public Collection getAttackVictims() {
		Collection ret = CollectionFactory.createLinkedList();
		for(Iterator iter = getRelations(AttackRelation.class).iterator(); iter.hasNext(); ) {
			AttackRelation ar = (AttackRelation) iter.next();
			if(ar.source.equals(this)) {
				ret.add(ar.target);
			}
		}
		return ret;
	}

	public Collection getAttackAggressors() {
		Collection ret = CollectionFactory.createLinkedList();
		for(Iterator iter = getRelations(AttackRelation.class).iterator(); iter.hasNext(); ) {
			AttackRelation ar = (AttackRelation) iter.next();
			if(ar.target.equals(this)) {
				ret.add(ar.source);
			}
		}
		return ret;
	}

	/**
	 * Returns all units indicating by their orders that they would
	 * transport this unit as a passanger (if there is more than one
	 * such unit, that is a semantical error of course).
	 */
	public Map getCarriers() {
		Map carriers = CollectionFactory.createHashtable();
		for (Iterator iter = getRelations(TransportRelation.class).iterator(); iter.hasNext(); ) {
			TransportRelation tr = (TransportRelation) iter.next();
			if (this.equals(tr.target)) {
				carriers.put(tr.source.getID(), tr.source);
			}
		}
		return carriers;
	}

	private void removeRelationsOriginatingFromUs() {
		Collection deathRow = CollectionFactory.createLinkedList();
		for (Iterator iter = getRelations().iterator(); iter.hasNext(); ) {
			UnitRelation r = (UnitRelation)iter.next();
			if (this.equals(r.origin)) {
				if (r instanceof InterUnitRelation) {
					if (((InterUnitRelation)r).target != null) {
						// remove relations in target units
						if(((InterUnitRelation)r).target.equals(this)) {
							((InterUnitRelation)r).source.removeRelation(r);
						} else {
							((InterUnitRelation)r).target.removeRelation(r);
						}
					}
				} else {
					if (r instanceof UnitContainerRelation) {
						// remove relations in target unit containers
						if (((UnitContainerRelation)r).target != null) {
							((UnitContainerRelation)r).target.removeRelation(r);
						}
					}
				}
				// remove relation afterwards
				deathRow.add(r);
			}
		}
		for (Iterator iter = deathRow.iterator(); iter.hasNext(); ) {
			this.removeRelation((UnitRelation)iter.next());
		}
	}

	/**
	 * Parses the orders of this unit and detects relations between
	 * units established by those orders.
	 *
	 * When does this method have to be called?
	 * No relation of a unit can affect an object outside the region
	 * that unit is in. So when all relations regarding a certain unit
	 * as target or source need to be determined, this method has to
	 * be called for each unit in the same region.
	 * Since relations are defined by unit orders, modified orders may
	 * lead to different relations. Therefore refreshRelations() has
	 * to be invoked on a unit after its orders were modified.
	 */
	private final static int REFRESHRELATIONS_ALL = -2;

	public synchronized void refreshRelations(GameData data) {
		Map modItems = null;	// needed to track changes in the items for GIB orders
		int modPersons = this.getPersons();
		// 1. can this unit have relations to others at all?
		if (ordersObject.ordersAreNull()) {
			return;
		}

		// invalidate the cache of this unit
		invalidateCache();

		// 2. remove all relations originating from us
		removeRelationsOriginatingFromUs();

		// 3. clone this unit's items
		modItems = CollectionFactory.createHashtable();
		if (this.items != null) {
			for (Iterator iter = this.items.values().iterator(); iter.hasNext(); ) {
				Item i = (Item)iter.next();
				modItems.put(i.getType().getID(), new Item(i.getType(), i.getAmount()));
			}
		}
		
		// 4. parse the orders and create new relations
		OrderParser parser = new OrderParser((Eressea)data.rules);
		boolean tempOrders = false;
		int line = 0;
		for (Iterator iter = getOrders().iterator(); iter.hasNext(); ) {
			line++; // keep track of line
			String order = (String)iter.next();
			if (parser.read(new StringReader(order))) {
				List tokens = parser.getTokens();
				if (((OrderToken)tokens.get(0)).ttype == OrderToken.TT_COMMENT) {
					continue;
				}
				if (((OrderToken)tokens.get(0)).ttype == OrderToken.TT_PERSIST) {
					tokens.remove(0);
				}
				if (!tempOrders) {
					if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_MAKE)) &&
					((OrderToken)tokens.get(1)).getText().toUpperCase().startsWith(getOrder(O_TEMP))) {
						tempOrders = true;
						continue;
					}
					// transport relation
					if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_CARRY))) {
						OrderToken t = (OrderToken)tokens.get(1);
						Unit target = getTargetUnit(t, this.getRegion());
						if (target == null || this.equals(target)) {
							continue;
						}
						TransportRelation rel = new TransportRelation(this, target, line);
						addRelation(rel);
						if (target != null) {
							target.addRelation(rel);
						}
					} else
						// transfer relation
						if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_GIVE)) ||
							((OrderToken)tokens.get(0)).equalsToken(getOrder(O_SUPPLY))) {
							boolean parseTarget = false;	// indicates whether to parse the object to be transferred
							OrderToken t = (OrderToken)tokens.get(1);
							Unit target = getTargetUnit(t, this.getRegion());
							if (target != null) {
								if (!target.equals(this)) {
									TransferRelation rel = new TransferRelation(this, target, -1, line);
									// -1 means that the amount could not determined
									t = (OrderToken)tokens.get(2);
									if (t.ttype == OrderToken.TT_NUMBER) {
										// this is a plain number
										rel.amount = Integer.parseInt(t.getText());
										parseTarget = true;
									} else if (t.ttype == OrderToken.TT_KEYWORD && t.equalsToken(getOrder(O_ALL))) {
										// -2 encodes that everything is to be transferred
										rel.amount = REFRESHRELATIONS_ALL;
										parseTarget = true;
									} else if (t.equalsToken(getOrder(O_HERBS))) {
										// if the 'amount' is HERBS then create relations for all herbs the unit carries
										ItemCategory herbCategory = data.rules.getItemCategory(StringID.create(("HERBS")));
										if (herbCategory != null && this.items != null) {
											for (Iterator items = modItems.values().iterator(); items.hasNext(); ) {
												Item i = (Item)items.next();
												if (herbCategory.equals(i.getType().getCategory())) {
													TransferRelation r = new ItemTransferRelation(this, target, i.getAmount(), i.getType(), line);
													i.setAmount(0);
													this.addRelation(r);
													target.addRelation(r);
												}
											}
										}
										parseTarget = false;
									}

									if (parseTarget) {
										if (rel.amount != -1) {	// -1 means that the amount could not determined
											t = (OrderToken)tokens.get(3);
											if (t.ttype != OrderToken.TT_EOC) {
												// now the order must look something like:
												// GIVE <unit id> <amount> <object><EOC>
												String itemName = stripQuotes(t.getText());
												if (t.equalsToken(getOrder(O_MEN))) {
													// if the specified amount was 'all':
													if (rel.amount == REFRESHRELATIONS_ALL) {
														rel.amount = modPersons;
													} else {
														// if not, only transfer the minimum amount the unit has
														rel.amount = Math.min(modPersons, rel.amount);
													}
													rel = new PersonTransferRelation(this, target, rel.amount, this.realRace != null ? this.realRace : this.race, line);
													// update the modified person amount
													modPersons = Math.max(0, modPersons - rel.amount);
												} else if (itemName.length() > 0) {
													ItemType iType = ((Eressea)data.rules).getItemType(itemName);
													if (iType != null) {
														// get the item from the list of modified items
														Item i = (Item)modItems.get(iType.getID());
														if(i==null) {
															// item unknown
															rel.amount = 0;
														} else {
															// if the specified amount is 'all', convert this to a decent number
															if (rel.amount == REFRESHRELATIONS_ALL) {
																rel.amount = i.getAmount();
															} else {
																// if not, only transfer the minimum amount the unit has
																rel.amount = Math.min(i.getAmount(), rel.amount);
															}
														}
														// create the new transfer relation
														rel = new ItemTransferRelation(this, target, rel.amount, iType, line);
														// update the modified item amount
														if (i != null) {
															i.setAmount(Math.max(0,i.getAmount() - rel.amount));
														}
													} else {
														rel = null;
													}
												} else {
													rel = null;
												}

												// let's see whether there is a valid relation to add
												if (rel != null) {
													addRelation(rel);
													if (target != null) {
														target.addRelation(rel);
													}
												}
											} else {
												// in this case the order looks like:
												// GIVE <unit id> <amount><EOC>
												if (rel.amount == REFRESHRELATIONS_ALL) { // -2 is used to encode that the amount was 'ALL'
													for (Iterator items = modItems.values().iterator(); items.hasNext(); ) {
														Item i = (Item)items.next();
														TransferRelation r = new ItemTransferRelation(this, target, i.getAmount(), i.getType(), line);
														i.setAmount(0);
														this.addRelation(r);
														target.addRelation(r);
													}
												}
											}
										} else {
											log.warn("Unit.updateRelations(): cannot parse amount in order " + order);
										}
									}
								} else {
									// relation to myself? you're sick
								}
							}
						} else
							// recruitment relation
							if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_RECRUIT))) {
								OrderToken t = (OrderToken)tokens.get(1);
								if (t.ttype == OrderToken.TT_NUMBER) {
									RecruitmentRelation rel = new RecruitmentRelation(this, Integer.parseInt(t.getText()), line);
									rel.source.addRelation(rel);
									rel.target.addRelation(rel);
								} else {
									log.warn("Unit.updateRelations(): invalid amount in order " + order);
								}
							} else
								// enter relation
								if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_ENTER))) {
									OrderToken t = (OrderToken)tokens.get(1);
									UnitContainer uc = null;
									if (t.equalsToken(getOrder(O_CASTLE))) {
										t = (OrderToken)tokens.get(2);
										uc = this.getRegion().getBuilding(EntityID.createEntityID(t.getText()));
									} else
										if (t.equalsToken(getOrder(O_SHIP))) {
											t = (OrderToken)tokens.get(2);
											uc = this.getRegion().getShip(EntityID.createEntityID(t.getText()));
										}
									if (uc != null) {
										EnterRelation rel = new EnterRelation(this, uc, line);
										addRelation(rel);
										uc.addRelation(rel);
									} else {
										log.warn("Unit.updateRelations(): cannot find target in order " + order);
									}
									// check whether the unit leaves a container
									UnitContainer leftUC = this.getBuilding();
									if (leftUC == null) {
										leftUC = this.getShip();
									}
									if (leftUC != null) {
										LeaveRelation rel = new LeaveRelation(this, leftUC, line);
										addRelation(rel);
										leftUC.addRelation(rel);
									}
								} else
									// leave relation
									if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_LEAVE))) {
										UnitContainer uc = getBuilding();
										if (uc == null) {
											uc = getShip();
										}
										if (uc != null) {
											LeaveRelation rel = new LeaveRelation(this, uc, line);
											addRelation(rel);
											uc.addRelation(rel);
										} else {
											log.warn("Unit.updateRelations(): unit " + this + " cannot leave a ship or a building as indicated by order " + order);
										}
									} else
										// teach relation
										if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_TEACH))) {
											int tokCtr = 1;
											OrderToken token = (OrderToken)tokens.get(tokCtr);
											while (token.ttype != OrderToken.TT_EOC) {
												Unit pupil = getTargetUnit(token, this.getRegion());
												if (pupil != null) {
													if (!this.equals(pupil)) {
														TeachRelation rel = new TeachRelation(this, pupil, line);
														this.addRelation(rel);
														if (pupil != null) {
															pupil.addRelation(rel);
														}
													} // else can't teach myself
												} // else pupil not found
												tokCtr++;
												token = (OrderToken)tokens.get(tokCtr);
											}
										} else {
											// attack relation
											if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_ATTACK))) {
												OrderToken token = (OrderToken)tokens.get(1);
												Unit enemy = getTargetUnit(token, this.getRegion());
												if(enemy != null) {
													AttackRelation rel = new AttackRelation(this, enemy, line);
													addRelation(rel);
													enemy.addRelation(rel);
												}
											}
										}
				} else {
					if (((OrderToken)tokens.get(0)).equalsToken(getOrder(O_END))) {
						tempOrders = false;
						continue;
					}
				}
			}
		}
	}

	private Unit getTargetUnit(OrderToken t, Region r) {
		Unit u = null;
		try {
			UnitID id = UnitID.createUnitID(t.getText());
			u = r.getUnit(id);
		} catch (NumberFormatException e) {
			log.warn("Unit.getTargetUnit(): cannot parse unit id \"" + t.getText() + "\"!");
		}

		return u;
	}

	/**
	 * Returns a String representation of this unit.
	 */
	public String toString() {
		if (name != null)
			return name + " (" + this.id.toString() + ")";
		else
			return getString("unit") + " " + this.id.toString() + " (" + this.id.toString() + ")";
	}

	/**
	 * Kinda obvious, right?
	 */
	public Unit(ID id) {
		super(id);
	}

	/** see Unit.GUARDFLAG_*  Converts guard flags into a readable string.
	 */
	public static String guardFlagsToString(int iFlags) {
		String strFlags = "";
		if (iFlags != 0) {
			strFlags += getString("guard.region");
		}
		if ((iFlags & GUARDFLAG_WOOD) != 0) {
			strFlags += ", " + getString("guard.wood");
		}
		return strFlags;
	}

	/**
	 * Returns a locale specific string representation of the
	 * specified unit combat status.
	 */
	public static String combatStatusToString(Unit u) {
		String retVal = combatStatusToString(u.combatStatus);
		if (u.unaided) {
			retVal += ", " + getString("combatstatus.unaided");
		}
		return retVal;
	}

	public static String combatStatusToString(int combatStatus) {
		String retVal = null;
		switch (combatStatus) {
			case 0:
				retVal = getString("combatstatus.aggressive");
				break;

			case 1:
				retVal = getString("combatstatus.front");
				break;

			case 2:
				retVal = getString("combatstatus.back");
				break;

			case 3:
				retVal = getString("combatstatus.defensive");
				break;

			case 4:
				retVal = getString("combatstatus.passive");
				break;

			case 5:
				retVal = getString("combatstatus.escape");
				break;

			default:
				Object[] msgArgs = {
					new Integer(combatStatus)
				};
				retVal = (new java.text.MessageFormat(getString("combatstatus.unknown"))).format(msgArgs);
		}
		return retVal;
	}

	/**
	 * Add a order to the unit's orders. This function ensures
	 * that TEMP units are not affected by the operation.
	 *
	 * @param order the order to add.
	 * @param replace if <tt>true</tt>, the order replaces any
	 * other of the unit's orders of the same type. If <tt>false</tt>
	 * the order is simply added.
	 * @param length denotes the number of tokens that need to be
	 * equal for a replacement. E.g. specify 2 if order is "BENENNE
	 * EINHEIT abc" and all "BENENNE EINHEIT" orders should be
	 * replaced but not all "BENENNE" orders.
	 * @return <tt>true</tt> if the order was successfully added.
	 */
	public boolean addOrder(String order, boolean replace, int length) {
		if (null == order || order.trim().equals("") || ordersAreNull() || (replace && length < 1)) {
			return false;
		}

		// parse order until there are enough match tokens
		int tokenCounter = 0;
		Collection matchTokens = CollectionFactory.createLinkedList();
		OrderTokenizer ct = new OrderTokenizer(new StringReader(order));
		OrderToken t = ct.getNextToken();
		while (t.ttype != OrderToken.TT_EOC && tokenCounter++ < length) {
			matchTokens.add(t);
			t = ct.getNextToken();
		}
		// order does not contain enough match tokens, abort
		if (matchTokens.size() < length) {
			return false;
		}

		// if replace, delete matching orders first
		if (replace) {
			boolean tempBlock = false;
			// cycle through this unit's orders
			for (ListIterator cmds = ordersObject.getOrders().listIterator(); cmds.hasNext(); ) {
				String cmd = (String)cmds.next();
				ct = new OrderTokenizer(new StringReader(cmd));
				t = ct.getNextToken();
				// skip empty orders and comments
				if (OrderToken.TT_EOC == t.ttype || OrderToken.TT_COMMENT == t.ttype) {
					continue;
				}

				if (false == tempBlock) {
					if (t.equalsToken(getOrder(O_MAKE))) {
						t = ct.getNextToken();
						if (OrderToken.TT_EOC == t.ttype) {
							continue;
						} else if (t.equalsToken(getOrder(O_TEMP))) {
							tempBlock = true;
							continue;
						}
					} else {
						// compare the current unit order and tokens of the one to add
						boolean removeOrder = true;
						for (Iterator iter = matchTokens.iterator(); iter.hasNext() && t.ttype != OrderToken.TT_EOC; ) {
							OrderToken matchToken = (OrderToken)iter.next();
							if (!(t.equalsToken(matchToken.getText()) || matchToken.equalsToken(t.getText()))) {
								removeOrder = false;
								break;
							}
							t = ct.getNextToken();
						}
						if (removeOrder) {
							cmds.remove();
						}
						continue;
					}
				} else {
					if (t.equalsToken(getOrder(O_END))) {
						tempBlock = false;
						continue;
					}
				}
			}
		}
		addOrderAt(0, order);
		return true;
	}

	public static void merge(GameData curGD, Unit curUnit, GameData newGD, Unit newUnit) {
		/*
		 * True, when curUnit is seen by the faction it belongs to and
		 * is therefore fully specified.
		 */
		boolean curWellKnown = !curUnit.ordersAreNull() || curUnit.combatStatus != -1;

		/*
		 * True, when newUnit is seen by the faction it belongs to and
		 * is therefore fully specified. This is only meaningful in
		 * the second pass.
		 */
		boolean newWellKnown = !newUnit.ordersAreNull() || newUnit.combatStatus != -1;

		/*
		 * True, when newUnit is completely uninitialized, i.e. this
		 * invokation of merge is the first one of the two to be
		 * expected.
		 */
		//boolean firstPass = (newUnit.getPersons() == 0);
		boolean firstPass = newUnit.getRegion() == null;

		boolean sameRound = curGD.getDate().equals(newGD.getDate());

		if (curUnit.getName() != null) {
			newUnit.setName(curUnit.getName());
		}
		if (curUnit.getDescription() != null) {
			newUnit.setDescription(curUnit.getDescription());
		}

		if (curUnit.getAlias() != null) {
			try {
				newUnit.setAlias((UnitID)curUnit.getAlias().clone());
			} catch (CloneNotSupportedException e) {
			}
		}
		if (curUnit.aura != -1) {
			newUnit.aura = curUnit.aura;
		}
		if (curUnit.auraMax != -1) {
			newUnit.auraMax = curUnit.auraMax;
		}
		if (curUnit.getBuilding() != null) {
			newUnit.setBuilding(newGD.getBuilding(curUnit.getBuilding().getID()));
		}
		newUnit.cache = null;
		if (curUnit.combatSpells != null && curUnit.combatSpells.size() > 0) {
			if (newUnit.combatSpells == null) {
				newUnit.combatSpells = CollectionFactory.createHashtable();
			} else {
				newUnit.combatSpells.clear();
			}
			for (Iterator iter = curUnit.combatSpells.values().iterator(); iter.hasNext(); ) {
				CombatSpell curCS = (CombatSpell)iter.next();
				CombatSpell newCS = null;
				try {
					newCS = new CombatSpell((ID)curCS.getID().clone());
				} catch (CloneNotSupportedException e) {
				}
				CombatSpell.merge(curGD, curCS, newGD, newCS);
				newUnit.combatSpells.put(newCS.getID(), newCS);
			}
		}
		if (!curUnit.ordersAreNull() && curUnit.getOrders().size() > 0) {
			newUnit.setOrders(curUnit.getOrders());
		}
		newUnit.ordersConfirmed |= curUnit.ordersConfirmed;
		if (curUnit.effects != null && curUnit.effects.size() > 0) {
			if (newUnit.effects == null) {
				newUnit.effects = CollectionFactory.createLinkedList();
			} else {
				newUnit.effects.clear();
			}
			newUnit.effects.addAll(curUnit.effects);
		}
		if (curUnit.getFaction() != null) {
			if (newUnit.getFaction() == null || curWellKnown) {
				newUnit.setFaction(newGD.getFaction(curUnit.getFaction().getID()));
			}
		}
		if (curUnit.follows != null) {
			newUnit.follows = newGD.getUnit(curUnit.follows.getID());
		}
		if (curUnit.getGroup() != null && newUnit.getFaction() != null && newUnit.getFaction().groups != null) {
			newUnit.setGroup((Group)newUnit.getFaction().groups.get(curUnit.getGroup().getID()));
		}
		if (curUnit.guard != -1) {
			newUnit.guard = curUnit.guard;
		}
		/* There is a correlation between guise faction and isSpy.
		 Since the guise faction can only be known by the 'owner
		 faction' it should override the isSpy value */
		if (curUnit.getGuiseFaction() != null) {
			newUnit.setGuiseFaction(newGD.getFaction(curUnit.getGuiseFaction().getID()));
		}
		newUnit.isSpy = (curUnit.isSpy && newUnit.getGuiseFaction() == null);

		if (curUnit.health != null) {
			newUnit.health = curUnit.health;
		}
		newUnit.hideFaction |= curUnit.hideFaction;
		newUnit.isStarving |= curUnit.isStarving;
		// do not overwrite the items in one special case:
		// if both source units are from the same turn, the first one
		// being well known and the second one not and this is the
		// second pass
		if (firstPass || !newWellKnown || curWellKnown) {
			if (curUnit.items != null && curUnit.items.size() > 0) {
				if (newUnit.items == null) {
					newUnit.items = CollectionFactory.createHashtable();
				} else {
					newUnit.items.clear();
				}
				for (Iterator iter = curUnit.items.values().iterator(); iter.hasNext(); ) {
					Item curItem = (Item)iter.next();
					Item newItem = new Item(newGD.rules.getItemType(curItem.getType().getID(), true), curItem.getAmount());
					newUnit.items.put(newItem.getType().getID(), newItem);
				}
			}
		}
		if (curUnit.getPersons() != -1) {
			newUnit.persons = curUnit.getPersons();
		}
		if (curUnit.privDesc != null) {
			newUnit.privDesc = curUnit.privDesc;
		}
		if (curUnit.race != null) {
			newUnit.race = newGD.rules.getRace(curUnit.race.getID(), true);
		}
		if (curUnit.raceNamePrefix != null) {
			newUnit.raceNamePrefix = curUnit.raceNamePrefix;
		}
		if (curUnit.realRace != null) {
			newUnit.realRace = newGD.rules.getRace(curUnit.realRace.getID(), true);
		}
		if (curUnit.getRegion() != null) {
			newUnit.setRegion(newGD.getRegion(curUnit.getRegion().getID()));
		}
		if (curUnit.combatStatus != -1) {
			newUnit.combatStatus = curUnit.combatStatus;
		}
		if (curUnit.getShip() != null) {
			newUnit.setShip(newGD.getShip(curUnit.getShip().getID()));
		}
		if (curUnit.siege != null) {
			newUnit.siege = newGD.getBuilding(curUnit.siege.getID());
		}

		// this block requires newUnit.person to be already set!
		Collection oldSkills = CollectionFactory.createLinkedList();
		if (newUnit.skills == null) {
			newUnit.skills = CollectionFactory.createOrderedHashtable();
		} else {
			oldSkills.addAll(newUnit.skills.values());
		}
		if(log.isDebugEnabled()) {
			log.debug("Unit.merge: curUnit.skills: "+curUnit.skills);
			log.debug("Unit.merge: newUnit.skills: "+newUnit.skills);
		}
		if (curUnit.skills != null && curUnit.skills.size() > 0) {
			for (Iterator iter = curUnit.skills.values().iterator(); iter.hasNext(); ) {
				Skill curSkill = (Skill)iter.next();
				Skill newSkill = new Skill(newGD.rules.getSkillType(curSkill.getType().getID(), true), curSkill.getPoints(), curSkill.getLevel(), newUnit.getPersons(), curSkill.noSkillPoints());
				if (curSkill.isLevelChanged()) {
					newSkill.setLevelChanged(true);
					newSkill.setChangeLevel(curSkill.getChangeLevel());
					if (curSkill.isLostSkill()) {
						newSkill.setLevel(-1);
					}
				}
				// NOTE: Maybe some decision about change-level computation in reports of
				//       same date here
				Skill oldSkill = (Skill)newUnit.skills.put(newSkill.getType().getID(), newSkill);
				if (newUnit.skillsCopied) {
					int dec = 0;
					if (oldSkill != null ) {
						dec = oldSkill.getLevel();
					}
					newSkill.setChangeLevel(newSkill.getLevel() - dec);
					if (oldSkill == null) { // since we have a skill now, even if it is level 0
						newSkill.setLevelChanged(true);
					}
				}
				if (oldSkill != null) {
					oldSkills.remove(oldSkill);
				}
			}
		}

		// pavkovic 2002.12.31: Remove oldSkills if the current unit is well known
		// if not, the old skill values stay where they are
		// pavkovic 2003.05.13: ...but never remove skills from the same round (as before with items)
		// andreasg 2003.10.05: ...but if old skills from earlier date!
		if(curWellKnown && (!sameRound || newUnit.skillsCopied)) {
			// Now remove all skills that are lost
			if (oldSkills.size() > 0) {
				for(Iterator iter = oldSkills.iterator(); iter.hasNext(); ) {
					Skill oldSkill = (Skill) iter.next();
					if (oldSkill.isLostSkill()) { // remove if it was lost
						newUnit.skills.remove(oldSkill);
					} else {
						oldSkill.setChangeLevel(-oldSkill.getLevel());
						oldSkill.setLevel(-1);
					}
				}
			}
			// newUnit.skillsCopied = true;
		}

		newUnit.sortIndex = Math.max(newUnit.sortIndex, curUnit.sortIndex);
		if (curUnit.spells != null && curUnit.spells.size() > 0) {
			if (newUnit.spells == null) {
				newUnit.spells = CollectionFactory.createHashtable();
			} else {
				newUnit.spells.clear();
			}
			for (Iterator iter = curUnit.spells.values().iterator(); iter.hasNext(); ) {
				Spell curSpell = (Spell)iter.next();
				Spell newSpell = newGD.getSpell(curSpell.getID());
				newUnit.spells.put(newSpell.getID(), newSpell);
			}
		}
		if (curUnit.stealth != -1) {
			newUnit.stealth = curUnit.stealth;
		}
		if (curUnit.getTempID() != null) {
			try {
				newUnit.setTempID((UnitID)curUnit.getTempID().clone());
			} catch (CloneNotSupportedException e) {
				log.error(e);
			}
		}
		// temp units are created and merged in the merge methode of
		// the GameData class
		// new true iff cur true, new false iff cur false and well known
		if (curUnit.unaided) {
			newUnit.unaided = true;
		} else {
			if (curWellKnown) {
				newUnit.unaided = false;
			}
		}

		// Messages are special because they can contain different
		// data for different factions in the same turn.
		// Take new messages and stuff only into the new game data
		// if the two source game data objects are not from the
		// same turn and curGD is the newer game data or if both
		// are from the same turn. Both conditions are tested by the
		// following if statement
		if (sameRound) {
			if (curUnit.unitMessages != null && curUnit.unitMessages.size() > 0) {
				if (newUnit.unitMessages == null) {
					newUnit.unitMessages = CollectionFactory.createLinkedList();
				}
				for (Iterator iter = curUnit.unitMessages.iterator(); iter.hasNext(); ) {
					Message curMsg = (Message)iter.next();
					Message newMsg = null;
					try {
						newMsg = new Message((ID)curMsg.getID().clone());
					} catch (CloneNotSupportedException e) {
					}
					Message.merge(curGD, curMsg, newGD, newMsg);
					newUnit.unitMessages.add(newMsg);
				}
			}
		}

		// merge tags
		if (curUnit.hasTags()) {
			Iterator it = curUnit.getTagMap().keySet().iterator();
			while (it.hasNext()) {
				String s = (String)it.next();
				newUnit.putTag(s, curUnit.getTag(s));
			}
		}
	}

	/**
	 * Returns the weight of the specified item type in GE * 100 or 0
	 * if type is null.
	 */
	private int getItemWeight(ItemType type) {
		if (type != null) {
			return (int)(type.getWeight() * 100);
		} else {
			return 0;
		}
	}

	/**
	 * Removes quotes at the beginning and at the end of str or
	 * replaces tilde characters with spaces.
	 */
	private String stripQuotes(String str) {
		if (str == null) {
			return null;
		}

		int strLen = str.length();
		if (strLen >= 2 && str.charAt(0) == '"' && str.charAt(strLen - 1) == '"') {
			return str.substring(1, strLen - 1);
		} else {
			return str.replace('~', ' ');
		}
	}

	/**
	 * Indicates whether this Unit object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class Unit and o's id is equal to the id of this
	 * Unit object.
	 */
	public boolean equals(Object o) {
		if (o instanceof Unit) {
			return this.getID().equals(((Unit)o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on Unit objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Unit)o).getID());
	}

	/**
	 * Returns a translation for the specified order key.
	 */
	private String getOrder(String key) {
		return com.eressea.util.Translations.getOrderTranslation(key);
	}

	/**
	 * Returns a translation for the specified order key in the
	 * specified locale.
	 */
	private String getOrder(String key, Locale locale) {
		return com.eressea.util.Translations.getOrderTranslation(key,locale);
	}

	/**
	 * Scans this unit's orders for temp units to create. It
	 * constructs them as TempUnit objects and removes the
	 * corresponding orders from this unit. Uses the default order
	 * locale to parse the orders.
	 *
	 * @param sortIndex an index for sorting units (required to
	 * reconstruct the original order in the report) which is
	 * incremented with each new temp unit.
	 * @return the new sort index. <tt>return value</tt> - sortIndex is the
	 * number of temp units read from this unit's orders.
	 */
	public int extractTempUnits(int sortIndex) {
		return extractTempUnits(sortIndex, Locales.getOrderLocale());
	}

	/**
	 * Scans this unit's orders for temp units to create. It
	 * constructs them as TempUnit objects and removes the
	 * corresponding orders from this unit.
	 *
	 * @param sortIndex an index for sorting units (required to
	 * reconstruct the original order in the report) which is
	 * incremented with each new temp unit.
	 * @param locale the locale to parse the orders with.
	 * @return the new sort index. <tt>return value</tt> - sortIndex is the
	 * number of temp units read from this unit's orders.
	 */
	public int extractTempUnits(int sortIndex, Locale locale) {
		if (!this.ordersAreNull()) {
			TempUnit tempUnit = null;
			for (Iterator cmdIterator = ordersObject.getOrders().iterator(); cmdIterator.hasNext(); ) {
				String line = (String)cmdIterator.next();
				com.eressea.util.OrderTokenizer ct = new com.eressea.util.OrderTokenizer(new StringReader(line));
				com.eressea.util.OrderToken token = ct.getNextToken();
				if (tempUnit == null) {
					if (token.equalsToken(getOrder(O_MAKE, locale))) {
						token = ct.getNextToken();
						if (token.equalsToken(getOrder(O_TEMP, locale))) {
							token = ct.getNextToken();
							try {
								UnitID id = new UnitID(com.eressea.util.IDBaseConverter.parse(token.getText()) * -1);
								if (this.getRegion().getUnit(id) == null) {
									tempUnit = this.createTemp(id);
									tempUnit.setSortIndex(++sortIndex);
									cmdIterator.remove();
									token = ct.getNextToken();
									if (token.ttype != com.eressea.util.OrderToken.TT_EOC) {
										tempUnit.addOrders(getOrder(O_NAME, locale) + " " + getOrder(O_UNIT, locale) + " " + token.getText());
									}
								} else {
									log.warn("Unit.extractTempUnits(): region " +
											 this.getRegion() +
											 " already contains a temp unit with the id " + id +
											 ". This temp unit remains in the orders of its parent "+
											 "unit instead of being created as a unit in its own right.");
								}
							} catch (NumberFormatException e) {
							}
						}
					}
				} else {
					cmdIterator.remove();
					if (token.equalsToken(getOrder(O_END, locale))) {
						tempUnit = null;
					} else {
						if(CONFIRMEDTEMPCOMMENT.equals(line.trim())) {
							tempUnit.ordersConfirmed = true;
						} else {
							tempUnit.addOrders(line);
						}
					}
				}
			}
		}

		return sortIndex;
	}

	/**
	 * Returns a translation from the translation table for the
	 * specified key.
	 */
	protected static String getString(String key) {
		return com.eressea.util.Translations.getTranslation(Unit.class,key);
	}

	// EXTERNAL TAG METHODS
	public void deleteAllTags() {
		externalMap = null;
	}

	public String putTag(String tag, String value) {
		if (externalMap == null)
			externalMap = new TagMap();
		return (String)externalMap.put(tag, value);
	}

	public String getTag(String tag) {
		if (externalMap == null)
			return null;
		return (String)externalMap.get(tag);
	}

	public String removeTag(String tag) {
		if (externalMap == null)
			return null;
		return (String)externalMap.remove(tag);
	}

	public boolean containsTag(String tag) {
		if (externalMap == null)
			return false;
		return externalMap.containsKey(tag);
	}

	public Map getTagMap() {
		if (externalMap == null)
			externalMap = new TagMap();
		return externalMap;
	}

	public boolean hasTags() {
		return externalMap != null && !externalMap.isEmpty();
	}
	
	/** a (hopefully) small class for handling orders in the Unit object */
	private static class Orders {
		private List orders = null;
		private boolean changed = false;

		public Orders() {
		}

		public void addOrders(String newOrders) {
			if(newOrders != null) {
				addOrders(CollectionFactory.singleton(newOrders));
			}
		}

		public void addOrders(Collection newOrders) {
			if(newOrders == null) {
				return;
			}
			if(orders == null) {
				orders = CollectionFactory.createArrayList(newOrders.size());
			}
			orders.addAll(newOrders);
			changed = true;
		}

		public void setOrders(Collection newOrders) {
			clearOrders();
			addOrders(newOrders);
		}

		public List getOrders() {
			return orders == null ? Collections.EMPTY_LIST : orders;

		}

		public void removeOrders() {
			clearOrders();
			orders = null;
		}

		public void clearOrders() {
			if(orders != null) {
				orders.clear();
			}
		}

		public boolean ordersAreNull() {
			return orders == null;
		}


		public void addOrderAt(int i,String newOrders) {
			if(orders == null) {
				orders = CollectionFactory.createArrayList(1);
			}
			orders.add(i, newOrders);
		}

		public void removeOrderAt(int i) {
			if(orders == null) {
				orders = CollectionFactory.createArrayList(1);
			}
			orders.remove(i);
		}

		public boolean ordersHaveChanged() {
			return changed;
		}
		public void setOrdersChanged(boolean changed) {
			this.changed = changed;
		}
	}
}
