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

import java.io.StringReader;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.eressea.gamebinding.MovementEvaluator;

import com.eressea.relation.AttackRelation;
import com.eressea.relation.EnterRelation;
import com.eressea.relation.InterUnitRelation;
import com.eressea.relation.ItemTransferRelation;
import com.eressea.relation.LeaveRelation;
import com.eressea.relation.MovementRelation;
import com.eressea.relation.PersonTransferRelation;
import com.eressea.relation.RecruitmentRelation;
import com.eressea.relation.TransportRelation;
import com.eressea.relation.UnitContainerRelation;
import com.eressea.relation.UnitRelation;

import com.eressea.rules.ItemType;
import com.eressea.rules.Race;
import com.eressea.rules.SkillType;

import com.eressea.util.Cache;
import com.eressea.util.CollectionFactory;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.Locales;
import com.eressea.util.OrderToken;
import com.eressea.util.OrderTokenizer;
import com.eressea.util.OrderWriter;
import com.eressea.util.Sorted;
import com.eressea.util.TagMap;
import com.eressea.util.Taggable;
import com.eressea.util.Translations;
import com.eressea.util.comparator.LinearUnitTempUnitComparator;
import com.eressea.util.comparator.SortIndexComparator;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Unit extends DescribedObject implements HasRegion, Sorted, Taggable {
	private static final Logger log = Logger.getInstance(Unit.class);
	private static final String CONFIRMEDTEMPCOMMENT = ";" + OrderWriter.CONFIRMEDTEMP;

	/** The unit does not possess horses */
	public static final int CAP_NO_HORSES = MovementEvaluator.CAP_NO_HORSES;

	/** The unit is not sufficiently skilled in horse riding */
	public static final int CAP_UNSKILLED = MovementEvaluator.CAP_UNSKILLED;

	/** TODO: DOCUMENT ME! */
	public String privDesc = null; // private description

	/** TODO: DOCUMENT ME! */
	public Race race = null;

	/** TODO: DOCUMENT ME! */
	public Race realRace = null;

	/** an object encapsulation  the orders of this unit as <tt>String</tt> objects */
	protected Orders ordersObject = new Orders();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean ordersAreNull() {
		return ordersObject.ordersAreNull();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean ordersHaveChanged() {
		return ordersObject.ordersHaveChanged();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param changed TODO: DOCUMENT ME!
	 */
	public void setOrdersChanged(boolean changed) {
		ordersObject.setOrdersChanged(changed);
	}

	/**
	 * Clears the orders and refreshes the relations
	 */
	public void clearOrders() {
		clearOrders(true);
	}

	/**
	 * Clears the orders and possibly refreshes the relations
	 *
	 * @param refreshRelations TODO: DOCUMENT ME!
	 */
	public void clearOrders(boolean refreshRelations) {
		ordersObject.clearOrders();

		if(refreshRelations) {
			refreshRelations();
		}
	}

	/**
	 * Removes the order at position <tt>i</tt> and refreshes the relations
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void removeOrderAt(int i) {
		removeOrderAt(i, true);
	}

	/**
	 * Removes the order at position <tt>i</tt> and possibly refreshes the relations
	 *
	 * @param i TODO: DOCUMENT ME!
	 * @param refreshRelations TODO: DOCUMENT ME!
	 */
	public void removeOrderAt(int i, boolean refreshRelations) {
		ordersObject.removeOrderAt(i);

		if(refreshRelations) {
			refreshRelations(i);
		}
	}

	/**
	 * Adds the order at position <tt>i</tt> and refreshes the relations
	 *
	 * @param i TODO: DOCUMENT ME!
	 * @param newOrders TODO: DOCUMENT ME!
	 */
	public void addOrderAt(int i, String newOrders) {
		addOrderAt(i, newOrders, true);
	}

	/**
	 * Adds the order at position <tt>i</tt> and possibly refreshes the relations
	 *
	 * @param i TODO: DOCUMENT ME!
	 * @param newOrders TODO: DOCUMENT ME!
	 * @param refreshRelations TODO: DOCUMENT ME!
	 */
	protected void addOrderAt(int i, String newOrders, boolean refreshRelations) {
		ordersObject.addOrderAt(i, newOrders);

		if(refreshRelations) {
			refreshRelations(i);
		}
	}

	/**
	 * Adds the order and refreshes the relations
	 *
	 * @param newOrders TODO: DOCUMENT ME!
	 */
	public void addOrders(String newOrders) {
		addOrders(newOrders, true);
	}

	/**
	 * Adds the order and possibly refreshes the relations
	 *
	 * @param newOrders TODO: DOCUMENT ME!
	 * @param refreshRelations TODO: DOCUMENT ME!
	 */
	public void addOrders(String newOrders, boolean refreshRelations) {
		addOrders(Collections.singleton(newOrders), refreshRelations);
	}

	/**
	 * Adds the orders and refreshes the relations
	 *
	 * @param newOrders TODO: DOCUMENT ME!
	 */
	public void addOrders(Collection newOrders) {
		addOrders(newOrders, true);
	}

	/**
	 * Adds the orders and possibly refreshes the relations
	 *
	 * @param newOrders TODO: DOCUMENT ME!
	 * @param refreshRelations TODO: DOCUMENT ME!
	 */
	public void addOrders(Collection newOrders, boolean refreshRelations) {
		int newPos = ordersObject.addOrders(newOrders);

		if(refreshRelations) {
			refreshRelations(newPos);
		}
	}

	/**
	 * Sets the orders and refreshes the relations
	 *
	 * @param newOrders TODO: DOCUMENT ME!
	 */
	public void setOrders(Collection newOrders) {
		setOrders(newOrders, true);
	}

	/**
	 * Sets the orders and possibly refreshes the relations
	 *
	 * @param newOrders TODO: DOCUMENT ME!
	 * @param refreshRelations TODO: DOCUMENT ME!
	 */
	public void setOrders(Collection newOrders, boolean refreshRelations) {
		ordersObject.setOrders(newOrders);

		if(refreshRelations) {
			refreshRelations();
		}
	}

	/**
	 * Delivers a readonly collection of alle orders of this unit.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getOrders() {
		return CollectionFactory.unmodifiableCollection(ordersObject.getOrders());
	}

	/** TODO: DOCUMENT ME! */
	public int persons = 1;

	/** TODO: DOCUMENT ME! */
	public int guard = 0;

	/** TODO: DOCUMENT ME! */
	public static final int GUARDFLAG_WOOD = 4;

	/** TODO: DOCUMENT ME! */
	public Building siege = null; // belagert

	/** TODO: DOCUMENT ME! */
	public int stealth = -1; // getarnt

	/** TODO: DOCUMENT ME! */
	public int aura = -1;

	/** TODO: DOCUMENT ME! */
	public int auraMax = -1;

	/** TODO: DOCUMENT ME! */
	public int combatStatus = -1; // Kampfstatus

	/** TODO: DOCUMENT ME! */
	public boolean unaided = false; // if attacked, this unit will not be helped by allied units

	/** TODO: DOCUMENT ME! */
	public boolean hideFaction = false; // Parteitarnung

	/** TODO: DOCUMENT ME! */
	public Unit follows = null; // folgt-Tag

	/** TODO: DOCUMENT ME! */
	public String health = null;

	/** TODO: DOCUMENT ME! */
	public boolean isStarving = false; // hunger-Tag

	/**
	 * The cache object containing cached information that may be not related enough to be
	 * encapsulated as a function and is time consuming to gather.
	 */
	public com.eressea.util.Cache cache = null;

	/**
	 * Messages directly sent to this unit. The list contains instances of class <tt>Message</tt>
	 * with type -1 and only the text set.
	 */
	public List unitMessages = null;

	/** A map for storing unknown tags. */
	private TagMap externalMap = null;

	/**
	 * A list containing <tt>String</tt> objects, specifying effects on this <tt>Unit</tt> object.
	 */
	public List effects = null;

	/** true indicates that the unit has orders confirmed by an user. */
	public boolean ordersConfirmed = false;

	/** TODO: DOCUMENT ME! */
	public Map skills = null; // maps SkillType.getID() objects to Skill objects
	protected boolean skillsCopied = false;

	/**
	 * The items carried by this unit. The keys are the IDs of the item's type, the values are the
	 * Item objects themselves.
	 */
	public Map items = null;

	/**
	 * The spells known to this unit. The keys are the IDs of the spells, the values are the Spell
	 * objects themselves.
	 */
	public Map spells = null;

	/**
	 * Contains the spells this unit has set for use in a combat. This map contains data if a unit
	 * has a magic skill and has actively set combat spells. The values in this map are objects of
	 * type CombatSpell, the keys are their ids.
	 */
	public Map combatSpells = null;

	/** The group this unit belongs to. */
	private Group group = null;

	/**
	 * Sets the group this unit belongs to.
	 *
	 * @param g TODO: DOCUMENT ME!
	 */
	public void setGroup(Group g) {
		if(this.group != null) {
			this.group.removeUnit(this.getID());
		}

		this.group = g;

		if(this.group != null) {
			this.group.addUnit(this);
		}
	}

	/**
	 * Returns the group this unit belongs to.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Group getGroup() {
		return this.group;
	}

	/** The previous id of this unit. */
	private UnitID alias = null;

	/**
	 * Sets an alias id for this unit.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public void setAlias(UnitID id) {
		this.alias = id;
	}

	/**
	 * Returns the alias, i.e. the id of this unit it had in the last turn (e.g. after a NUMMER
	 * order).
	 *
	 * @return the alias or null, if the id did not change.
	 */
	public UnitID getAlias() {
		return alias;
	}

	/**
	 * Returns the item of the specified type if the unit owns such an item. If not, null is
	 * returned.
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item getItem(ItemType type) {
		return (items != null) ? (Item) items.get(type.getID()) : null;
	}

	/**
	 * Indicates that this unit belongs to a different faction than it pretends to. A unit cannot
	 * disguise itself as a different faction and at the same time be a spy of another faction,
	 * therefore, setting this attribute to true results in having the guiseFaction attribute set
	 * to null.
	 */
	private boolean isSpy = false;

	/**
	 * Sets whether is unit really belongs to its unit or only pretends to do so. A
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setSpy(boolean bool) {
		this.isSpy = bool;

		if(this.isSpy) {
			this.setGuiseFaction(null);
		}
	}

	/**
	 * Returns whether this unit only pretends to belong to its faction.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isSpy() {
		return this.isSpy;
	}

	/**
	 * If this unit is disguised and pretends to belong to a different faction this field holds
	 * that faction, else it is null.
	 */
	private Faction guiseFaction = null;

	/**
	 * Sets the faction this unit pretends to belong to. A unit cannot disguise itself as a
	 * different faction and at the same time be a spy of another faction, therefore, setting a
	 * value other than null results in having the spy attribute set to false.
	 *
	 * @param f TODO: DOCUMENT ME!
	 */
	public void setGuiseFaction(Faction f) {
		this.guiseFaction = f;

		if(f != null) {
			this.setSpy(false);
		}
	}

	/**
	 * Returns the faction this unit pretends to belong to. If the unit is not disguised null is
	 * returned.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Faction getGuiseFaction() {
		return this.guiseFaction;
	}

	/**
	 * Adds an item to the unit. If the unit already has an item of the same type, the item is
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
		invalidateCache();

		return i;
	}

	/** The temp id this unit had before becoming a real unit. */
	private UnitID tempID = null;

	/**
	 * Sets the temp id this unit had before becoming a real unit.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public void setTempID(UnitID id) {
		this.tempID = id;
	}

	/**
	 * Returns the id the unit had when it was still a temp unit. This id is only set in the turn
	 * after the unit turned from a temp unit into to a real unit.
	 *
	 * @return the temp id or null, if this unit was no temp unit in the previous turn.
	 */
	public UnitID getTempID() {
		return this.tempID;
	}

	/** The region this unit is currently in. */
	protected Region region = null;

	/**
	 * Sets the region this unit is in. If this unit already has a different region set it removes
	 * itself from the collection of units in that region.
	 *
	 * @param r TODO: DOCUMENT ME!
	 */
	public void setRegion(Region r) {
		if(r != getRegion()) {
			if(this.region != null) {
				this.region.removeUnit(this.getID());
			}

			if(r != null) {
				r.addUnit(this);
			}

			this.region = r;
		}
	}

	/**
	 * Returns the region this unit is staying in.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Region getRegion() {
		return region;
	}

	/** The faction this unit belongs to. */
	private Faction faction = null;

	/**
	 * Sets the faction for this unit. If this unit already has a different faction set it removes
	 * itself from the collection of units in that faction.
	 *
	 * @param faction TODO: DOCUMENT ME!
	 */
	public void setFaction(Faction faction) {
		if(faction != getFaction()) {
			if(this.faction != null) {
				this.faction.removeUnit(this.getID());
			}

			if(faction != null) {
				faction.addUnit(this);
			}

			this.faction = faction;
		}
	}

	/**
	 * Returns the faction this unit belongs to.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Faction getFaction() {
		return faction;
	}

	/** The building this unit stays in. */
	private Building building = null;

	/**
	 * Sets the building this unit is staying in. If the unit already is in another building this
	 * method removes it from the unit collection of that building.
	 *
	 * @param building TODO: DOCUMENT ME!
	 */
	public void setBuilding(Building building) {
		if(this.building != null) {
			this.building.removeUnit(this.getID());
		}

		this.building = building;

		if(this.building != null) {
			this.building.addUnit(this);
		}
	}

	/**
	 * Returns the building this unit is staying in.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Building getBuilding() {
		return building;
	}

	/** The ship this unit is on. */
	private Ship ship = null;

	/**
	 * Sets the ship this unit is on. If the unit already is on another ship this method removes it
	 * from the unit collection of that ship.
	 *
	 * @param ship TODO: DOCUMENT ME!
	 */
	public void setShip(Ship ship) {
		if(this.ship != null) {
			this.ship.removeUnit(this.getID());
		}

		this.ship = ship;

		if(this.ship != null) {
			this.ship.addUnit(this);
		}
	}

	/**
	 * Returns the ship this unit is on.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Ship getShip() {
		return ship;
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

	/** A unit dependent prefix to be prepended to this faction's race name. */
	private String raceNamePrefix = null;

	/**
	 * Sets the unit dependent prefix for the race name.
	 *
	 * @param prefix TODO: DOCUMENT ME!
	 */
	public void setRaceNamePrefix(String prefix) {
		this.raceNamePrefix = prefix;
	}

	/**
	 * Returns the unit dependent prefix for the race name.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getRaceNamePrefix() {
		return this.raceNamePrefix;
	}

	/**
	 * Returns the name of this unit's race including the prefixes of itself, its faction and group
	 * if it has such and those prefixes are set.
	 *
	 * @param data TODO: DOCUMENT ME!
	 *
	 * @return the name or null if this unit's race or its name is not set.
	 */
	public String getRaceName(GameData data) {
		if(this.race != null) {
			if(this.getRaceNamePrefix() != null) {
				return data.getTranslationOrKeyIfNull(this.getRaceNamePrefix()) +
					   this.race.getName().toLowerCase();
			} else {
				if((this.group != null) && (this.group.getRaceNamePrefix() != null)) {
					return data.getTranslationOrKeyIfNull(this.group.getRaceNamePrefix()) +
						   this.race.getName().toLowerCase();
				} else {
					if((this.faction != null) && (this.faction.getRaceNamePrefix() != null)) {
						return data.getTranslationOrKeyIfNull(this.faction.getRaceNamePrefix()) +
							   this.race.getName().toLowerCase();
					} else {
						return this.race.getName();
					}
				}
			}
		}

		return null;
	}

	/** A map containing all temp units created by this unit. */
	private Map tempUnits = null;

	/** A collection view of the temp units. */
	private Collection tempUnitCollection = null;

	/**
	 * Returns the child temp units created by this unit's orders.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection tempUnits() {
		if(tempUnitCollection == null) {
			tempUnitCollection = CollectionFactory.unmodifiableCollection(tempUnits);
		}

		return tempUnitCollection;
	}

	/**
	 * Return the child temp unit with the specified ID.
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public TempUnit getTempUnit(ID id) {
		if(tempUnits != null) {
			return (TempUnit) tempUnits.get(id);
		}

		return null;
	}

	/**
	 * Adds a temp unit to this unit.
	 *
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private TempUnit addTemp(TempUnit u) {
		if(tempUnits == null) {
			tempUnits = CollectionFactory.createHashtable();

			// enforce the creation of a new collection view
			tempUnitCollection = null;
		}

		tempUnits.put(u.getID(), u);

		return u;
	}

	/**
	 * Removes a temp unit from the list of child temp units created by this unit's orders.
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private Unit removeTemp(ID id) {
		Unit ret = null;

		if(tempUnits != null) {
			ret = (Unit) tempUnits.remove(id);

			if(tempUnits.isEmpty()) {
				tempUnits = null;
			}
		}

		return ret;
	}

	/**
	 * Clears the list of temp units created by this unit. Clears only the caching collection, does
	 * not perform clean-up like deleteTemp() does.
	 */
	public void clearTemps() {
		if(tempUnits != null) {
			tempUnits.clear();
			tempUnits = null;
		}
	}

	/**
	 * Returns alle orders including the orders necessary to issue the creation of all the child
	 * temp units of this unit.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getCompleteOrders() {
		List cmds = CollectionFactory.createLinkedList();
		cmds.addAll(ordersObject.getOrders());
		cmds.addAll(getTempOrders());

		return cmds;
	}

	/**
	 * Returns the orders necessary to issue the creation of all the child temp units of this unit.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected List getTempOrders() {
		List cmds = CollectionFactory.createLinkedList();

		for(Iterator iter = tempUnits().iterator(); iter.hasNext();) {
			TempUnit u = (TempUnit) iter.next();
			cmds.add(getOrder(EresseaOrderConstants.O_MAKE) + " " +
					 getOrder(EresseaOrderConstants.O_TEMP) + " " + u.getID().toString());
			cmds.addAll(u.getOrders());

			if(u.ordersConfirmed) {
				cmds.add(CONFIRMEDTEMPCOMMENT);
			}

			cmds.add(getOrder(EresseaOrderConstants.O_END));
		}

		return cmds;
	}

	/**
	 * Creates a new temp unit with this unit as the parent. The temp unit is fully initialised,
	 * i.e. it is added to the region units collection in the specified game data,it inherits the
	 * faction, building or ship, region, faction stealth status, group, race and combat status
	 * settings and adds itself to the corresponding unit collections.
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public TempUnit createTemp(ID id) {
		if(((UnitID) id).intValue() >= 0) {
			throw new IllegalArgumentException("Unit.createTemp(): cannot create temp unit with non-negative ID.");
		}

		TempUnit t = new TempUnit(id, this);
		this.addTemp(t);
		t.persons = 0;
		t.hideFaction = this.hideFaction;
		t.combatStatus = this.combatStatus;
		t.ordersConfirmed = false;

		if(this.race != null) {
			t.race = this.race;
		}

		if(this.realRace != null) {
			t.realRace = this.realRace;
		}

		if(this.getRegion() != null) {
			t.setRegion(this.getRegion());
		}

		if(this.getShip() != null) {
			t.setShip(this.getShip());
		} else if(this.getBuilding() != null) {
			t.setBuilding(this.getBuilding());
		}

		if(this.getFaction() != null) {
			t.setFaction(this.getFaction());
		}

		if(this.group != null) {
			t.setGroup(this.group);
		}

		return t;
	}

	/**
	 * Removes a temp unit with this unit as the parent completely from the game data.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 */
	public void deleteTemp(ID id, GameData data) {
		TempUnit t = (TempUnit) this.removeTemp(id);

		if(t != null) {
			t.persons = 0;
			t.race = null;
			t.realRace = null;
			t.setRegion(null);
			t.setShip(null);
			t.setBuilding(null);
			t.setFaction(null);
			t.setGroup(null);

			if(t.cache != null) {
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
		if(cache != null) {
			cache.modifiedSkills = null;
			cache.modifiedItems = null;
			cache.unitWeight = -1;
			cache.modifiedUnitWeight = -1;
			cache.modifiedPersons = -1;
		}
	}

	/**
	 * Returns a Collection over the relations this unit has to other units. The iterator returns
	 * <tt>UnitRelation</tt> objects. An empty iterator is returned if the relations have not been
	 * set up so far or if there are no relations. To have the relations to other units properly
	 * set up the refreshRelations() method has to be invoked.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getRelations() {
		if((cache != null) && (cache.relations != null)) {
			return CollectionFactory.unmodifiableCollection(cache.relations);
		}

		return CollectionFactory.EMPTY_COLLECTION;
	}

	/**
	 * Returns a Collection over the relations this unit has to other units. The collection consist
	 * of  <tt>UnitRelation</tt> objects.  The UnitRelation objects are filtered by the given
	 * relation class.
	 *
	 * @param relationClass TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getRelations(Class relationClass) {
		Collection ret = CollectionFactory.createLinkedList();

		for(Iterator iter = getRelations().iterator(); iter.hasNext();) {
			Object relation = iter.next();

			if(relationClass.isInstance(relation)) {
				ret.add(relation);
			}
		}

		return ret;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rel TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitRelation addRelation(UnitRelation rel) {
		if(cache == null) {
			cache = new Cache();
		}

		if(cache.relations == null) {
			cache.relations = CollectionFactory.createLinkedList();
		}

		cache.relations.add(rel);
		invalidateCache();

		return rel;
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

	/**
	 * deliver all directly related units
	 *
	 * @param units TODO: DOCUMENT ME!
	 */
	public void getRelatedUnits(Collection units) {
		units.add(this);

		for(Iterator iter = this.getRelations(InterUnitRelation.class).iterator(); iter.hasNext();) {
			InterUnitRelation iur = (InterUnitRelation) iter.next();
			units.add(iur.source);

			if(iur.target != null) {
				units.add(iur.target);
			}
		}
	}

	/**
	 * Recursively retrieves all units that are related to this unit via one of the specified
	 * relations.
	 *
	 * @param units all units gathered so far to prevent loops.
	 * @param relations a set of classes naming the types of relations that are eligible for
	 * 		  regarding a unit as related to some other unit.
	 */
	public void getRelatedUnits(Set units, Set relations) {
		units.add(this);

		for(Iterator iter = this.getRelations().iterator(); iter.hasNext();) {
			UnitRelation rel = (UnitRelation) iter.next();

			if(relations.contains(rel.getClass())) {
				Unit src = rel.source;
				Unit target = null;

				if(rel instanceof InterUnitRelation) {
					target = ((InterUnitRelation) rel).target;
				}

				if(units.add(src)) {
					src.getRelatedUnits(units, relations);
				}

				if(units.add(target)) {
					target.getRelatedUnits(units, relations);
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getModifiedMovement() {
		if(this.ordersAreNull()) {
			return Collections.EMPTY_LIST;
		}

		Collection movementRelations = getRelations(MovementRelation.class);

		if(movementRelations.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return ((MovementRelation) movementRelations.iterator().next()).movement;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Ship getModifiedShip() {
		for(Iterator iter = getRelations(UnitContainerRelation.class).iterator(); iter.hasNext();) {
			UnitContainerRelation ucr = (UnitContainerRelation) iter.next();

			if(ucr instanceof EnterRelation) {
				if(ucr.target instanceof Ship) {
					// make fast return: first Ship-EnterRelation wins
					return (Ship) ucr.target;
				}
			} else if(ucr instanceof LeaveRelation && ucr.target.equals(getShip())) {
				// we only left our ship
				return null;
			}
		}

		// we stayed in our ship
		return getShip();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Skill getModifiedSkill(SkillType type) {
		Skill s = null;

		if((cache == null) || (cache.modifiedSkills == null)) {
			// the cache is invalid, refresh
			refreshModifiedSkills();
		}

		if((cache != null) && (cache.modifiedSkills != null)) {
			s = (Skill) cache.modifiedSkills.get(type.getID());
		}

		return s;
	}

	/**
	 * Returns the skills of this unit as they would appear after the orders for person transfers
	 * are processed.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getModifiedSkills() {
		if((cache == null) || (cache.modifiedSkills == null)) {
			refreshModifiedSkills();
		}

		if((cache != null) && (cache.modifiedSkills != null)) {
			return CollectionFactory.unmodifiableCollection(cache.modifiedSkills);
		}

		return CollectionFactory.EMPTY_COLLECTION;
	}

	/**
	 * Updates the cache with the skills of this unit as they would appear after the orders for
	 * person transfers are processed. If the cache object or the modified skills field is still
	 * null after invoking this function, the skill modifications cannot be determined accurately.
	 */
	private synchronized void refreshModifiedSkills() {
		// create the cache
		if(cache == null) {
			cache = new Cache();
		}

		// clear existing modified skills
		// there is special case: to reduce memory consumption
		// cache.modifiedSkills can point to the real skills and
		// you don't want to clear THAT
		// that also means that this should be the only place where
		// cache.modifiedSkills is modified
		if((cache.modifiedSkills != null) && (cache.modifiedSkills != this.skills)) {
			cache.modifiedSkills.clear();
		}

		// if there are no relations, cache.modfiedSkills can point
		// directly to the skills and we can bail out
		if(getRelations().isEmpty()) {
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
		Collections.sort(sortedUnits,
						 new LinearUnitTempUnitComparator(new SortIndexComparator(null)));

		/* clone units with all aspects relevant for skills */
		Map clones = CollectionFactory.createHashtable();

		for(Iterator iter = relatedUnits.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			Unit clone = null;

			try {
				clone = new Unit((ID) u.getID().clone());
				clone.persons = u.getPersons();
				clone.race = u.race;
				clone.realRace = u.realRace;
				clone.region = u.region;
				clone.isStarving = u.isStarving;

				for(Iterator skillIter = u.getSkills().iterator(); skillIter.hasNext();) {
					Skill s = (Skill) skillIter.next();
					clone.addSkill(new Skill(s.getSkillType(), s.getPoints(), s.getLevel(),
											 clone.persons, s.noSkillPoints()));
				}
			} catch(CloneNotSupportedException e) {
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

		for(Iterator unitIter = sortedUnits.iterator(); unitIter.hasNext();) {
			Unit srcUnit = (Unit) unitIter.next();

			for(Iterator relationIter = srcUnit.getRelations().iterator(); relationIter.hasNext();) {
				UnitRelation unitRel = (UnitRelation) relationIter.next();

				if(!(unitRel.source.equals(srcUnit)) ||
					   !(unitRel instanceof PersonTransferRelation)) {
					continue;
				}

				PersonTransferRelation rel = (PersonTransferRelation) unitRel;
				Unit srcClone = (Unit) clones.get(srcUnit.getID());
				Unit targetUnit = (Unit) rel.target;
				Unit targetClone = (Unit) clones.get(targetUnit.getID());
				int transferredPersons = Math.max(0, Math.min(srcClone.getPersons(), rel.amount));

				if(transferredPersons == 0) {
					continue;
				}

				/* modify the target clone */
				/* first modify all skills that are available in the
				 target clone */
				for(Iterator skills = targetClone.getSkills().iterator(); skills.hasNext();) {
					Skill targetSkill = (Skill) skills.next();
					Skill srcSkill = srcClone.getSkill(targetSkill.getSkillType());
					int skillModifier = targetSkill.getModifier(targetClone);

					if(srcSkill == null) {
						/* skill exists only in the target clone, this
						 is equivalent to a target skill at 0.
						 Level is set to lostSkillLevel to avoid
						 confusion about level modifiers in case of
						 noSkillPoints. If skill points are relevant
						 this value is ignored anyway. */
						srcSkill = new Skill(targetSkill.getSkillType(), 0, lostSkillLevel,
											 srcClone.getPersons(), targetSkill.noSkillPoints());
					}

					if(targetSkill.noSkillPoints()) {
						/* Math.max(0, ...) guarantees that the true
						 skill level cannot drop below 0. This also
						 important to handle the Integer.MIN_VALUE
						 case below */
						int transferredSkillFactor = Math.max(0, srcSkill.getLevel() -
															  skillModifier) * transferredPersons;
						int targetSkillFactor = Math.max(0, targetSkill.getLevel() - skillModifier) * targetClone.getPersons();
						int newSkillLevel = (int) (((float) (transferredSkillFactor +
											targetSkillFactor)) / (float) (transferredPersons +
											targetClone.getPersons()));

						/* newSkillLevel == 0 means that that the skill
						 is lost by this transfer but we may not set
						 the skill level to 0 + skillModifier since
						 this would indicate an existing skill
						 depending on the modifier. Thus
						 lostSkillLevel is used to distinctly
						 mark the staleness of this skill. */
						targetSkill.setLevel((newSkillLevel > 0) ? (newSkillLevel + skillModifier)
																 : lostSkillLevel);
					} else {
						targetSkill.setPoints(targetSkill.getPoints() +
											  (int) (((float) srcSkill.getPoints() * (float) transferredPersons) / (float) srcClone.getPersons()));
					}
				}

				/* now modify the skills that only exist in the source
				 clone */
				for(Iterator skills = srcClone.getSkills().iterator(); skills.hasNext();) {
					Skill srcSkill = (Skill) skills.next();
					Skill targetSkill = (Skill) targetClone.getSkill(srcSkill.getSkillType());

					if(targetSkill == null) {
						/* skill exists only in the source clone, this
						 is equivalent to a source skill at 0.
						 Level is set to lostSkillLevel to avoid
						 confusion about level modifiers in case of
						 noSkillPoints. If skill points are relevant
						 this value is ignored anyway. */
						targetSkill = new Skill(srcSkill.getSkillType(), 0, lostSkillLevel,
												targetClone.getPersons(), srcSkill.noSkillPoints());
						targetClone.addSkill(targetSkill);

						if(srcSkill.noSkillPoints()) {
							/* Math.max(0, ...) guarantees that the true
							 skill level cannot drop below 0. This also
							 important to handle the lostSkillLevel
							 case below */
							int skillModifier = srcSkill.getModifier(srcClone);
							int transferredSkillFactor = Math.max(0,
																  srcSkill.getLevel() -
																  skillModifier) * transferredPersons;
							int newSkillLevel = (int) (((float) transferredSkillFactor) / (float) (transferredPersons +
												targetClone.getPersons()));

							/* newSkillLevel == 0 means that that the skill
							 is lost by this transfer but we may not set
							 the skill level to 0 + skillModifier since
							 this would indicate an existing skill
							 depending on the modifier. Thus
							 lostSkillLevel is used to distinctly
							 mark the staleness of this skill. */
							targetSkill.setLevel((newSkillLevel > 0)
												 ? (newSkillLevel + skillModifier) : lostSkillLevel);
						} else {
							int newSkillPoints = (int) (srcSkill.getPoints() * (float) ((float) transferredPersons / (float) srcClone.getPersons()));
							targetSkill.setPoints(newSkillPoints);
						}
					}

					/* modify the skills in the source clone (no extra
					 loop for this) */
					if(!srcSkill.noSkillPoints()) {
						int transferredSkillPoints = (int) (((float) srcSkill.getPoints() * (float) transferredPersons) / (float) srcClone.getPersons());
						srcSkill.setPoints(srcSkill.getPoints() - transferredSkillPoints);
					}
				}

				srcClone.persons = srcClone.getPersons() - transferredPersons;
				targetClone.persons += transferredPersons;
			}
		}

		/* modify the skills according to recruitment */
		Unit clone = (Unit) clones.get(this.getID());

		/* update the person and level information in all clone skills */
		if(clone.getSkills().size() > 0) {
			this.cache.modifiedSkills = CollectionFactory.createHashtable();

			for(Iterator skills = clone.getSkills().iterator(); skills.hasNext();) {
				Skill skill = (Skill) skills.next();
				skill.setPersons(clone.persons);

				/* When skill points are relevant, all we did up to
				 now, was to keep track of these while the skill
				 level was ignored - update it now */
				if(!skill.noSkillPoints()) {
					skill.setLevel(skill.getLevel(clone, false));
				} else {
					/* If skill points are not relevant we always
					 take skill modifiers into account but we marked
					 'lost' skills by Integer.MIN_VALUE which has to
					 be fixed here */
					if(skill.getLevel() == lostSkillLevel) {
						skill.setLevel(0);
					}
				}

				/* inject clone skills into real unit (no extra loop for
				 this */
				if((skill.getPoints() > 0) || (skill.getLevel() > 0)) {
					this.cache.modifiedSkills.put(skill.getSkillType().getID(), skill);
				}
			}
		}
	}

	/**
	 * Returns the unit container this unit is in. The type of unit container returned (faction,
	 * region, building or ship) is equal to the class of the uc parameter.
	 *
	 * @param uc TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitContainer getUnitContainer(UnitContainer uc) {
		if(uc instanceof Faction) {
			return getFaction();
		} else if(uc instanceof Region) {
			return getRegion();
		} else if(uc instanceof Building) {
			return getBuilding();
		} else if(uc instanceof Ship) {
			return getShip();
		}

		return null;
	}

	/**
	 * Returns the skill of the specified type if the unit has such a skill, else null is returned.
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Skill getSkill(SkillType type) {
		return (skills != null) ? (Skill) skills.get(type.getID()) : null;
	}

	/**
	 * Adds a skill to unit's collection of skills. If the unit already has a skill of the same
	 * type it is overwritten with the the new skill object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 *
	 * @return the specified skill s.
	 */
	public Skill addSkill(Skill s) {
		if(skills == null) {
			skills = CollectionFactory.createOrderedHashtable();
		}

		skills.put(s.getSkillType().getID(), s);

		return s;
	}

	/**
	 * Returns all skills this unit has.
	 *
	 * @return a collection of Skill objects.
	 */
	public Collection getSkills() {
		return CollectionFactory.unmodifiableCollection(this.skills);
	}

	/**
	 * Removes all skills from this unit.
	 */
	public void clearSkills() {
		if(skills != null) {
			skills.clear();
			skills = null;

			if((cache != null) && (cache.modifiedSkills != null)) {
				cache.modifiedSkills.clear();
				cache.modifiedSkills = null;
			}
		}
	}

	/**
	 * Copies the skills of the given unit. Does not empty this unit's skills.
	 *
	 * @param u TODO: DOCUMENT ME!
	 * @param v TODO: DOCUMENT ME!
	 */
	public static void copySkills(Unit u, Unit v) {
		copySkills(u, v, true);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 * @param v TODO: DOCUMENT ME!
	 * @param sortOut TODO: DOCUMENT ME!
	 */
	public static void copySkills(Unit u, Unit v, boolean sortOut) {
		v.skillsCopied = true;

		if(u.skills != null) {
			Iterator it = u.getSkills().iterator();

			while(it.hasNext()) {
				Skill sk = (Skill) it.next();

				// sort out if changed to non-existent
				if(sortOut && sk.isLostSkill()) {
					continue;
				}

				Skill newSkill = new Skill(sk.getSkillType(), sk.getPoints(), sk.getLevel(),
										   v.persons, sk.noSkillPoints());
				v.addSkill(newSkill);
			}
		}
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
	 * Removes all items from this unit.
	 */
	public void clearItems() {
		if(items != null) {
			items.clear();
			items = null;
			invalidateCache();
		}
	}

	/**
	 * Returns the item of the specified type as it would appear after the orders of this unit have
	 * been processed, i.e. the amount of the item might be modified by transfer orders. If the
	 * unit does not have an item of the specified type nor is given one by some other unit, null
	 * is returned.
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item getModifiedItem(ItemType type) {
		Item i = null;

		if((cache == null) || (cache.modifiedItems == null)) {
			refreshModifiedItems();
		}

		if((cache != null) && (cache.modifiedItems != null)) {
			i = (Item) cache.modifiedItems.get(type.getID());
		}

		return i;
	}

	/**
	 * Returns a collection of the itemrelations concerning the given Item.
	 *
	 * @param item TODO: DOCUMENT ME!
	 *
	 * @return a collection of ItemTransferRelation objects.
	 */
	public List getItemTransferRelations(Item item) {
		List ret = CollectionFactory.createArrayList(getRelations().size());

		for(Iterator iter = getRelations(ItemTransferRelation.class).iterator(); iter.hasNext();) {
			ItemTransferRelation rel = (ItemTransferRelation) iter.next();

			if(rel.itemType.equals(item.getItemType())) {
				ret.add(rel);
			}
		}

		return ret;
	}

	/**
	 * Returns a collection of the personrelations associated with this unit
	 *
	 * @return a collection of PersonTransferRelation objects.
	 */
	public List getPersonTransferRelations() {
		List ret = (List) getRelations(PersonTransferRelation.class);

		if(log.isDebugEnabled()) {
			log.debug("Unit.getPersonTransferRelations for " + this);
			log.debug(ret);
		}

		return ret;

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
	 * Returns the items of this unit as they would appear after the orders of this unit have been
	 * processed.
	 *
	 * @return a collection of Item objects.
	 */
	public Collection getModifiedItems() {
		if((cache == null) || (cache.modifiedItems == null)) {
			refreshModifiedItems();
		}

		return CollectionFactory.unmodifiableCollection(cache.modifiedItems);
	}

	/**
	 * Deduces the modified items from the current items and the relations between this and other
	 * units.
	 */
	private synchronized void refreshModifiedItems() {
		// 0. clear existing data structures
		if((cache != null) && (cache.modifiedItems != null)) {
			cache.modifiedItems.clear();
		}

		if(cache == null) {
			cache = new Cache();
		}

		if(cache.modifiedItems == null) {
			cache.modifiedItems = CollectionFactory.createHashtable();
		}

		// 1. check whether there is anything to do at all
		if(((items == null) || (items.size() == 0)) && getRelations().isEmpty()) {
			return;
		}

		// 2. clone items
		for(Iterator iter = getItems().iterator(); iter.hasNext();) {
			Item i = (Item) iter.next();
			cache.modifiedItems.put(i.getItemType().getID(),
									new Item(i.getItemType(), i.getAmount()));
		}

		// 3. now check relations for possible modifications
		for(Iterator iter = getRelations().iterator(); iter.hasNext();) {
			UnitRelation rel = (UnitRelation) iter.next();

			if(rel instanceof ItemTransferRelation) {
				ItemTransferRelation itr = (ItemTransferRelation) rel;
				Item modifiedItem = (Item) cache.modifiedItems.get(itr.itemType.getID());

				if(modifiedItem != null) { // the transferred item can be found among this unit's items

					if(this.equals(itr.source)) {
						modifiedItem.setAmount(modifiedItem.getAmount() - itr.amount);
					} else {
						modifiedItem.setAmount(modifiedItem.getAmount() + itr.amount);
					}
				} else { // the transferred item is not among the items the unit already has

					if(this.equals(itr.source)) {
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
		for(Iterator iter = getRelations().iterator(); iter.hasNext();) {
			UnitRelation rel = (UnitRelation) iter.next();

			if(rel instanceof RecruitmentRelation) {
				RecruitmentRelation rr = (RecruitmentRelation) rel;

				// FIXME(pavkovic): here is a bad binding to "Silber", what to do?
				Item modifiedItem = (Item) cache.modifiedItems.get(StringID.create("Silber"));

				if(modifiedItem != null) {
					Race race = this.realRace;

					if(race == null) {
						race = this.race;
					}

					if((race != null) && (race.getRecruitmentCosts() > 0)) {
						modifiedItem.setAmount(modifiedItem.getAmount() -
											   (rr.amount * race.getRecruitmentCosts()));
					}
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPersons() {
		return persons;
	}

	/**
	 * Returns the number of persons in this unit as it would be after the orders of this and other
	 * units have been processed since it may be modified by transfer orders.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getModifiedPersons() {
		if(cache == null) {
			cache = new Cache();
		}

		if(cache.modifiedPersons == -1) {
			cache.modifiedPersons = this.getPersons();

			for(Iterator iter = getPersonTransferRelations().iterator(); iter.hasNext();) {
				PersonTransferRelation ptr = (PersonTransferRelation) iter.next();

				if(this.equals(ptr.source)) {
					cache.modifiedPersons -= ptr.amount;
				} else {
					cache.modifiedPersons += ptr.amount;
				}
			}
		}

		return cache.modifiedPersons;
	}

	/**
	 * Returns the weight of a unit with the specified number of persons, their weight and the
	 * specified items in GE  100.
	 *
	 * @param persons TODO: DOCUMENT ME!
	 * @param personWeight TODO: DOCUMENT ME!
	 * @param items TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static int getWeight(int persons, float personWeight, Iterator items) {
		int weight = 0;

		while((items != null) && items.hasNext()) {
			Item item = (Item) items.next();

			// pavkovic 2003.09.10: only take care about (possibly) modified items with positive amount
			if(item.getAmount() > 0) {
				weight += (item.getAmount() * (int) (item.getItemType().getWeight() * 100));
			}
		}

		weight += (persons * (int) (personWeight * 100));

		return weight;
	}

	/**
	 * Returns the overall weight of this unit (persons and items) in GE  100.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getWeight() {
		if(cache == null) {
			cache = new Cache();
		}

		if(cache.unitWeight == -1) {
			cache.unitWeight = getWeight(this.getPersons(),
										 (this.realRace != null) ? this.realRace.getWeight()
																 : this.race.getWeight(),
										 this.getItems().iterator());
		}

		return cache.unitWeight;
	}

	/**
	 * Returns the maximum payload in GE  100 of this unit when it travels by horse. Horses, carts
	 * and persons are taken into account for this calculation. If the unit has a sufficient skill
	 * in horse riding but there are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 *
	 * @return the payload in GE  100, CAP_NO_HORSES if the unit does not possess horses or
	 * 		   CAP_UNSKILLED if the unit is not sufficiently skilled in horse riding to travel on
	 * 		   horseback.
	 */
	public int getPayloadOnHorse() {
		return getRegion().getData().getGameSpecificStuff().getMovementEvaluator()
				   .getPayloadOnHorse(this);
	}

	/**
	 * Returns the maximum payload in GE  100 of this unit when it travels on foot. Horses, carts
	 * and persons are taken into account for this calculation. If the unit has a sufficient skill
	 * in horse riding but there are too many carts for the horses, the weight of the additional
	 * carts are also already considered. The calculation also takes into account that trolls can
	 * tow carts.
	 *
	 * @return the payload in GE  100, CAP_UNSKILLED if the unit is not sufficiently skilled in
	 * 		   horse riding to travel on horseback.
	 */
	public int getPayloadOnFoot() {
		return getRegion().getData().getGameSpecificStuff().getMovementEvaluator().getPayloadOnFoot(this);
	}

	/**
	 * Returns the weight of all items of this unit that are not horses or carts in GE  100.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLoad() {
		return getRegion().getData().getGameSpecificStuff().getMovementEvaluator().getLoad(this);
	}

	/**
	 * Returns the weight of all items of this unit that are not horses or carts in GE  100 based
	 * on the modified items.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getModifiedLoad() {
		int load = getRegion().getData().getGameSpecificStuff().getMovementEvaluator()
					   .getModifiedLoad(this);

		// also take care of passengers 
		Map passengers = getPassengers();

		for(Iterator iter = passengers.values().iterator(); iter.hasNext();) {
			Unit passenger = (Unit) iter.next();
			load += passenger.getModifiedWeight();
		}

		return load;
	}

	/**
	 * Returns the number of regions this unit is able to travel within one turn based on the
	 * riding skill, horses, carts and load of this unit.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getRadius() {
		// pavkovic 2003.10.02: use modified load here...int load = getLoad();
		int load = getModifiedLoad();
		int payload = getPayloadOnHorse();

		if((payload >= 0) && ((payload - load) >= 0)) {
			return 2;
		} else {
			payload = getPayloadOnFoot();

			if((payload >= 0) && ((payload - load) >= 0)) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Returns the overall weight (persons, items) of this unit in GE  100 based on the modified
	 * items and persons.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getModifiedWeight() {
		if(cache == null) {
			cache = new Cache();
		}

		if(cache.modifiedUnitWeight == -1) {
			cache.modifiedUnitWeight = getWeight(this.getModifiedPersons(),
												 (this.realRace != null)
												 ? this.realRace.getWeight() : this.race.getWeight(),
												 this.getModifiedItems().iterator());
		}

		return cache.modifiedUnitWeight;
	}

	/**
	 * Returns all units this unit is transporting as passengers.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getPassengers() {
		Map passengers = CollectionFactory.createHashtable();

		for(Iterator iter = getRelations(TransportRelation.class).iterator(); iter.hasNext();) {
			TransportRelation tr = (TransportRelation) iter.next();

			if(this.equals(tr.source)) {
				passengers.put(tr.target.getID(), tr.target);
			}
		}

		return passengers;
	}

	/**
	 * Returns all units indicating by their orders that they would transport this unit as a
	 * passanger (if there is more than one such unit, that is a semantical error of course).
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getCarriers() {
		Map carriers = CollectionFactory.createHashtable();

		for(Iterator iter = getRelations(TransportRelation.class).iterator(); iter.hasNext();) {
			TransportRelation tr = (TransportRelation) iter.next();

			if(this.equals(tr.target)) {
				carriers.put(tr.source.getID(), tr.source);
			}
		}

		return carriers;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getAttackVictims() {
		Collection ret = CollectionFactory.createLinkedList();

		for(Iterator iter = getRelations(AttackRelation.class).iterator(); iter.hasNext();) {
			AttackRelation ar = (AttackRelation) iter.next();

			if(ar.source.equals(this)) {
				ret.add(ar.target);
			}
		}

		return ret;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getAttackAggressors() {
		Collection ret = CollectionFactory.createLinkedList();

		for(Iterator iter = getRelations(AttackRelation.class).iterator(); iter.hasNext();) {
			AttackRelation ar = (AttackRelation) iter.next();

			if(ar.target.equals(this)) {
				ret.add(ar.source);
			}
		}

		return ret;
	}

	/**
	 * remove relations that are originating from us with a line number &gt;= <tt>from</tt>
	 *
	 * @param from TODO: DOCUMENT ME!
	 */
	private void removeRelationsOriginatingFromUs(int from) {
		Collection deathRow = CollectionFactory.createLinkedList();

		for(Iterator iter = getRelations().iterator(); iter.hasNext();) {
			UnitRelation r = (UnitRelation) iter.next();

			if(this.equals(r.origin) && (r.line >= from)) {
				if(r instanceof InterUnitRelation) {
					if(((InterUnitRelation) r).target != null) {
						// remove relations in target units
						if(((InterUnitRelation) r).target.equals(this)) {
							((InterUnitRelation) r).source.removeRelation(r);
						} else {
							((InterUnitRelation) r).target.removeRelation(r);
						}
					}
				} else {
					if(r instanceof UnitContainerRelation) {
						// remove relations in target unit containers
						if(((UnitContainerRelation) r).target != null) {
							((UnitContainerRelation) r).target.removeRelation(r);
						}
					}
				}

				// remove relation afterwards
				deathRow.add(r);
			}
		}

		for(Iterator iter = deathRow.iterator(); iter.hasNext();) {
			this.removeRelation((UnitRelation) iter.next());
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void refreshRelations() {
		refreshRelations(1);
	}

	/**
	 * Parses the orders of this unit and detects relations between units established by those
	 * orders. When does this method have to be called? No relation of a unit can affect an object
	 * outside the region that unit is in. So when all relations regarding a certain unit as
	 * target or source need to be determined, this method has to be called for each unit in the
	 * same region. Since relations are defined by unit orders, modified orders may lead to
	 * different relations. Therefore refreshRelations() has to be invoked on a unit after its
	 * orders were modified.
	 *
	 * @param from TODO: DOCUMENT ME!
	 */
	public synchronized void refreshRelations(int from) {
		if(ordersObject.ordersAreNull() || (getRegion() == null)) {
			return;
		}

		invalidateCache();
		removeRelationsOriginatingFromUs(from);
		addAndSpreadRelations(getRegion().getData().getGameSpecificStuff().getRelationFactory()
								  .createRelations(this, from));
	}

	private void addAndSpreadRelations(Collection newRelations) {
		if(log.isDebugEnabled()) {
			log.debug("Relations for " + this);
			log.debug(newRelations);
		}

		for(Iterator iter = newRelations.iterator(); iter.hasNext();) {
			UnitRelation r = (UnitRelation) iter.next();
			this.addRelation(r);

			if(r.source != this) {
				r.source.addRelation(r);

				continue;
			}

			if(r instanceof InterUnitRelation) {
				InterUnitRelation iur = (InterUnitRelation) r;

				if((iur.target != null) && (iur.target != this)) {
					iur.target.addRelation(r);
				}

				continue;
			}

			if(r instanceof UnitContainerRelation) {
				UnitContainerRelation ucr = (UnitContainerRelation) r;

				if(ucr.target != null) {
					ucr.target.addRelation(r);
				}

				continue;
			}
		}
	}

	/**
	 * Returns a String representation of this unit.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		if(name != null) {
			return name + " (" + this.id.toString() + ")";
		} else {
			return getString("unit") + " " + this.id.toString() + " (" + this.id.toString() + ")";
		}
	}

	/**
	 * Kinda obvious, right?
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public Unit(ID id) {
		super(id);
	}

	/**
	 * see Unit.GUARDFLAG_  Converts guard flags into a readable string.
	 *
	 * @param iFlags TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String guardFlagsToString(int iFlags) {
		String strFlags = "";

		if(iFlags != 0) {
			strFlags += getString("guard.region");
		}

		if((iFlags & GUARDFLAG_WOOD) != 0) {
			strFlags += (", " + getString("guard.wood"));
		}

		return strFlags;
	}

	/**
	 * Returns a locale specific string representation of the specified unit combat status.
	 *
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String combatStatusToString(Unit u) {
		String retVal = combatStatusToString(u.combatStatus);

		if(u.unaided) {
			retVal += (", " + getString("combatstatus.unaided"));
		}

		return retVal;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param combatStatus TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String combatStatusToString(int combatStatus) {
		String retVal = null;

		switch(combatStatus) {
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

			Object msgArgs[] = { new Integer(combatStatus) };
			retVal = (new java.text.MessageFormat(getString("combatstatus.unknown"))).format(msgArgs);
		}

		return retVal;
	}

	/**
	 * Add a order to the unit's orders. This function ensures that TEMP units are not affected by
	 * the operation.
	 *
	 * @param order the order to add.
	 * @param replace if <tt>true</tt>, the order replaces any other of the unit's orders of the
	 * 		  same type. If <tt>false</tt> the order is simply added.
	 * @param length denotes the number of tokens that need to be equal for a replacement. E.g.
	 * 		  specify 2 if order is "BENENNE EINHEIT abc" and all "BENENNE EINHEIT" orders should
	 * 		  be replaced but not all "BENENNE" orders.
	 *
	 * @return <tt>true</tt> if the order was successfully added.
	 */
	public boolean addOrder(String order, boolean replace, int length) {
		if((order == null) || order.trim().equals("") || ordersAreNull() ||
			   (replace && (length < 1))) {
			return false;
		}

		// parse order until there are enough match tokens
		int tokenCounter = 0;
		Collection matchTokens = CollectionFactory.createLinkedList();
		OrderTokenizer ct = new OrderTokenizer(new StringReader(order));
		OrderToken t = ct.getNextToken();

		while((t.ttype != OrderToken.TT_EOC) && (tokenCounter++ < length)) {
			matchTokens.add(t);
			t = ct.getNextToken();
		}

		// order does not contain enough match tokens, abort
		if(matchTokens.size() < length) {
			return false;
		}

		// if replace, delete matching orders first
		if(replace) {
			boolean tempBlock = false;

			// cycle through this unit's orders
			for(ListIterator cmds = ordersObject.getOrders().listIterator(); cmds.hasNext();) {
				String cmd = (String) cmds.next();
				ct = new OrderTokenizer(new StringReader(cmd));
				t = ct.getNextToken();

				// skip empty orders and comments
				if((OrderToken.TT_EOC == t.ttype) || (OrderToken.TT_COMMENT == t.ttype)) {
					continue;
				}

				if(false == tempBlock) {
					if(t.equalsToken(getOrder(EresseaOrderConstants.O_MAKE))) {
						t = ct.getNextToken();

						if(OrderToken.TT_EOC == t.ttype) {
							continue;
						} else if(t.equalsToken(getOrder(EresseaOrderConstants.O_TEMP))) {
							tempBlock = true;

							continue;
						}
					} else {
						// compare the current unit order and tokens of the one to add
						boolean removeOrder = true;

						for(Iterator iter = matchTokens.iterator();
								iter.hasNext() && (t.ttype != OrderToken.TT_EOC);) {
							OrderToken matchToken = (OrderToken) iter.next();

							if(!(t.equalsToken(matchToken.getText()) ||
								   matchToken.equalsToken(t.getText()))) {
								removeOrder = false;

								break;
							}

							t = ct.getNextToken();
						}

						if(removeOrder) {
							cmds.remove();
						}

						continue;
					}
				} else {
					if(t.equalsToken(getOrder(EresseaOrderConstants.O_END))) {
						tempBlock = false;

						continue;
					}
				}
			}
		}

		addOrderAt(0, order);

		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curUnit TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newUnit TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Unit curUnit, GameData newGD, Unit newUnit) {
		/*
		 * True, when curUnit is seen by the faction it belongs to and
		 * is therefore fully specified.
		 */
		boolean curWellKnown = !curUnit.ordersAreNull() || (curUnit.combatStatus != -1);

		/*
		 * True, when newUnit is seen by the faction it belongs to and
		 * is therefore fully specified. This is only meaningful in
		 * the second pass.
		 */
		boolean newWellKnown = !newUnit.ordersAreNull() || (newUnit.combatStatus != -1);

		/*
		 * True, when newUnit is completely uninitialized, i.e. this
		 * invokation of merge is the first one of the two to be
		 * expected.
		 */

		//boolean firstPass = (newUnit.getPersons() == 0);
		boolean firstPass = newUnit.getRegion() == null;

		boolean sameRound = curGD.getDate().equals(newGD.getDate());

		if(curUnit.getName() != null) {
			newUnit.setName(curUnit.getName());
		}

		if(curUnit.getDescription() != null) {
			newUnit.setDescription(curUnit.getDescription());
		}

		if(curUnit.getAlias() != null) {
			try {
				newUnit.setAlias((UnitID) curUnit.getAlias().clone());
			} catch(CloneNotSupportedException e) {
			}
		}

		if(curUnit.aura != -1) {
			newUnit.aura = curUnit.aura;
		}

		if(curUnit.auraMax != -1) {
			newUnit.auraMax = curUnit.auraMax;
		}

		if(curUnit.getBuilding() != null) {
			newUnit.setBuilding(newGD.getBuilding(curUnit.getBuilding().getID()));
		}

		newUnit.cache = null;

		if((curUnit.combatSpells != null) && (curUnit.combatSpells.size() > 0)) {
			if(newUnit.combatSpells == null) {
				newUnit.combatSpells = CollectionFactory.createHashtable();
			} else {
				newUnit.combatSpells.clear();
			}

			for(Iterator iter = curUnit.combatSpells.values().iterator(); iter.hasNext();) {
				CombatSpell curCS = (CombatSpell) iter.next();
				CombatSpell newCS = null;

				try {
					newCS = new CombatSpell((ID) curCS.getID().clone());
				} catch(CloneNotSupportedException e) {
				}

				CombatSpell.merge(curGD, curCS, newGD, newCS);
				newUnit.combatSpells.put(newCS.getID(), newCS);
			}
		}

		if(!curUnit.ordersAreNull() && (curUnit.getOrders().size() > 0)) {
			Collection orders = CollectionFactory.createArrayList();

			/*
			if(!newUnit.ordersAreNull()) {
			    for(Iterator iter = newUnit.getOrders().iterator(); iter.hasNext(); ) {
			        String actOrder = (String) iter.next();
			        orders.add("; "+actOrder);
			    }
			}
			*/
			orders.addAll(curUnit.getOrders());
			newUnit.setOrders(orders, false);
		}

		newUnit.ordersConfirmed |= curUnit.ordersConfirmed;

		if((curUnit.effects != null) && (curUnit.effects.size() > 0)) {
			if(newUnit.effects == null) {
				newUnit.effects = CollectionFactory.createLinkedList();
			} else {
				newUnit.effects.clear();
			}

			newUnit.effects.addAll(curUnit.effects);
		}

		if(curUnit.getFaction() != null) {
			if((newUnit.getFaction() == null) || curWellKnown) {
				newUnit.setFaction(newGD.getFaction(curUnit.getFaction().getID()));
			}
		}

		if(curUnit.follows != null) {
			newUnit.follows = newGD.getUnit(curUnit.follows.getID());
		}

		if((curUnit.getGroup() != null) && (newUnit.getFaction() != null) &&
			   (newUnit.getFaction().groups != null)) {
			newUnit.setGroup((Group) newUnit.getFaction().groups.get(curUnit.getGroup().getID()));
		}

		if(curUnit.guard != -1) {
			newUnit.guard = curUnit.guard;
		}

		/* There is a correlation between guise faction and isSpy.
		 Since the guise faction can only be known by the 'owner
		 faction' it should override the isSpy value */
		if(curUnit.getGuiseFaction() != null) {
			newUnit.setGuiseFaction(newGD.getFaction(curUnit.getGuiseFaction().getID()));
		}

		newUnit.isSpy = (curUnit.isSpy && (newUnit.getGuiseFaction() == null));

		if(curUnit.health != null) {
			newUnit.health = curUnit.health;
		}

		newUnit.hideFaction |= curUnit.hideFaction;
		newUnit.isStarving |= curUnit.isStarving;

		// do not overwrite the items in one special case:
		// if both source units are from the same turn, the first one
		// being well known and the second one not and this is the
		// second pass
		if(firstPass || !newWellKnown || curWellKnown) {
			if((curUnit.items != null) && (curUnit.items.size() > 0)) {
				if(newUnit.items == null) {
					newUnit.items = CollectionFactory.createHashtable();
				} else {
					newUnit.items.clear();
				}

				for(Iterator iter = curUnit.items.values().iterator(); iter.hasNext();) {
					Item curItem = (Item) iter.next();
					Item newItem = new Item(newGD.rules.getItemType(curItem.getItemType().getID(),
																	true), curItem.getAmount());
					newUnit.items.put(newItem.getItemType().getID(), newItem);
				}
			}
		}

		if(curUnit.getPersons() != -1) {
			newUnit.persons = curUnit.getPersons();
		}

		if(curUnit.privDesc != null) {
			newUnit.privDesc = curUnit.privDesc;
		}

		if(curUnit.race != null) {
			newUnit.race = newGD.rules.getRace(curUnit.race.getID(), true);
		}

		if(curUnit.raceNamePrefix != null) {
			newUnit.raceNamePrefix = curUnit.raceNamePrefix;
		}

		if(curUnit.realRace != null) {
			newUnit.realRace = newGD.rules.getRace(curUnit.realRace.getID(), true);
		}

		if(curUnit.getRegion() != null) {
			newUnit.setRegion(newGD.getRegion(curUnit.getRegion().getID()));
		}

		if(curUnit.combatStatus != -1) {
			newUnit.combatStatus = curUnit.combatStatus;
		}

		if(curUnit.getShip() != null) {
			newUnit.setShip(newGD.getShip(curUnit.getShip().getID()));
		}

		if(curUnit.siege != null) {
			newUnit.siege = newGD.getBuilding(curUnit.siege.getID());
		}

		// this block requires newUnit.person to be already set!
		Collection oldSkills = CollectionFactory.createLinkedList();

		if(newUnit.skills == null) {
			newUnit.skills = CollectionFactory.createOrderedHashtable();
		} else {
			oldSkills.addAll(newUnit.skills.values());
		}

		if(log.isDebugEnabled()) {
			log.debug("Unit.merge: curUnit.skills: " + curUnit.skills);
			log.debug("Unit.merge: newUnit.skills: " + newUnit.skills);
		}

		if((curUnit.skills != null) && (curUnit.skills.size() > 0)) {
			for(Iterator iter = curUnit.skills.values().iterator(); iter.hasNext();) {
				Skill curSkill = (Skill) iter.next();
				Skill newSkill = new Skill(newGD.rules.getSkillType(curSkill.getSkillType().getID(),
																	true), curSkill.getPoints(),
										   curSkill.getLevel(), newUnit.getPersons(),
										   curSkill.noSkillPoints());

				if(curSkill.isLevelChanged()) {
					newSkill.setLevelChanged(true);
					newSkill.setChangeLevel(curSkill.getChangeLevel());

					if(curSkill.isLostSkill()) {
						newSkill.setLevel(-1);
					}
				}

				// NOTE: Maybe some decision about change-level computation in reports of
				//       same date here
				Skill oldSkill = (Skill) newUnit.skills.put(newSkill.getSkillType().getID(),
															newSkill);

				if(newUnit.skillsCopied) {
					int dec = 0;

					if(oldSkill != null) {
						dec = oldSkill.getLevel();
					}

					newSkill.setChangeLevel(newSkill.getLevel() - dec);

					if(oldSkill == null) { // since we have a skill now, even if it is level 0
						newSkill.setLevelChanged(true);
					}
				}

				if(oldSkill != null) {
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
			if(oldSkills.size() > 0) {
				for(Iterator iter = oldSkills.iterator(); iter.hasNext();) {
					Skill oldSkill = (Skill) iter.next();

					if(oldSkill.isLostSkill()) { // remove if it was lost
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

		if((curUnit.spells != null) && (curUnit.spells.size() > 0)) {
			if(newUnit.spells == null) {
				newUnit.spells = CollectionFactory.createHashtable();
			} else {
				newUnit.spells.clear();
			}

			for(Iterator iter = curUnit.spells.values().iterator(); iter.hasNext();) {
				Spell curSpell = (Spell) iter.next();
				Spell newSpell = newGD.getSpell(curSpell.getID());
				newUnit.spells.put(newSpell.getID(), newSpell);
			}
		}

		if(curUnit.stealth != -1) {
			newUnit.stealth = curUnit.stealth;
		}

		if(curUnit.getTempID() != null) {
			try {
				newUnit.setTempID((UnitID) curUnit.getTempID().clone());
			} catch(CloneNotSupportedException e) {
				log.error(e);
			}
		}

		// temp units are created and merged in the merge methode of
		// the GameData class
		// new true iff cur true, new false iff cur false and well known
		if(curUnit.unaided) {
			newUnit.unaided = true;
		} else {
			if(curWellKnown) {
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
		if(sameRound) {
			if((curUnit.unitMessages != null) && (curUnit.unitMessages.size() > 0)) {
				if(newUnit.unitMessages == null) {
					newUnit.unitMessages = CollectionFactory.createLinkedList();
				}

				for(Iterator iter = curUnit.unitMessages.iterator(); iter.hasNext();) {
					Message curMsg = (Message) iter.next();
					Message newMsg = null;

					try {
						newMsg = new Message((ID) curMsg.getID().clone());
					} catch(CloneNotSupportedException e) {
					}

					Message.merge(curGD, curMsg, newGD, newMsg);
					newUnit.unitMessages.add(newMsg);
				}
			}
		}

		// merge tags
		if(curUnit.hasTags()) {
			Iterator it = curUnit.getTagMap().keySet().iterator();

			while(it.hasNext()) {
				String s = (String) it.next();
				newUnit.putTag(s, curUnit.getTag(s));
			}
		}
	}

	/**
	 * Indicates whether this Unit object is equal to another object. Returns true only if o is not
	 * null and an instance of class Unit and o's id is equal to the id of this Unit object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		if(o instanceof Unit) {
			return this.getID().equals(((Unit) o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on Unit objects equivalent to the natural ordering of their ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Unit) o).getID());
	}

	/**
	 * Returns a translation for the specified order key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private String getOrder(String key) {
		return Translations.getOrderTranslation(key);
	}

	/**
	 * Returns a translation for the specified order key in the specified locale.
	 *
	 * @param key TODO: DOCUMENT ME!
	 * @param locale TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private String getOrder(String key, Locale locale) {
		return Translations.getOrderTranslation(key, locale);
	}

	/**
	 * Scans this unit's orders for temp units to create. It constructs them as TempUnit objects
	 * and removes the corresponding orders from this unit. Uses the default order locale to parse
	 * the orders.
	 *
	 * @param sortIndex an index for sorting units (required to reconstruct the original order in
	 * 		  the report) which is incremented with each new temp unit.
	 *
	 * @return the new sort index. <tt>return value</tt> - sortIndex is the number of temp units
	 * 		   read from this unit's orders.
	 */
	public int extractTempUnits(int sortIndex) {
		return extractTempUnits(sortIndex, Locales.getOrderLocale());
	}

	/**
	 * Scans this unit's orders for temp units to create. It constructs them as TempUnit objects
	 * and removes the corresponding orders from this unit.
	 *
	 * @param sortIndex an index for sorting units (required to reconstruct the original order in
	 * 		  the report) which is incremented with each new temp unit.
	 * @param locale the locale to parse the orders with.
	 *
	 * @return the new sort index. <tt>return value</tt> - sortIndex is the number of temp units
	 * 		   read from this unit's orders.
	 */
	public int extractTempUnits(int sortIndex, Locale locale) {
		if(!this.ordersAreNull()) {
			TempUnit tempUnit = null;

			for(Iterator cmdIterator = ordersObject.getOrders().iterator(); cmdIterator.hasNext();) {
				String line = (String) cmdIterator.next();
				com.eressea.util.OrderTokenizer ct = new com.eressea.util.OrderTokenizer(new StringReader(line));
				com.eressea.util.OrderToken token = ct.getNextToken();

				if(tempUnit == null) {
					if(token.equalsToken(getOrder(EresseaOrderConstants.O_MAKE, locale))) {
						token = ct.getNextToken();

						if(token.equalsToken(getOrder(EresseaOrderConstants.O_TEMP, locale))) {
							token = ct.getNextToken();

							try {
								UnitID id = new UnitID(com.eressea.util.IDBaseConverter.parse(token.getText()) * -1);

								if(this.getRegion().getUnit(id) == null) {
									tempUnit = this.createTemp(id);
									tempUnit.setSortIndex(++sortIndex);
									cmdIterator.remove();
									token = ct.getNextToken();

									if(token.ttype != com.eressea.util.OrderToken.TT_EOC) {
										tempUnit.addOrders(getOrder(EresseaOrderConstants.O_NAME,
																	locale) + " " +
														   getOrder(EresseaOrderConstants.O_UNIT,
																	locale) + " " +
														   token.getText(), false);
									}
								} else {
									log.warn("Unit.extractTempUnits(): region " + this.getRegion() +
											 " already contains a temp unit with the id " + id +
											 ". This temp unit remains in the orders of its parent " +
											 "unit instead of being created as a unit in its own right.");
								}
							} catch(NumberFormatException e) {
							}
						}
					}
				} else {
					cmdIterator.remove();

					if(token.equalsToken(getOrder(EresseaOrderConstants.O_END, locale))) {
						tempUnit = null;
					} else {
						if(CONFIRMEDTEMPCOMMENT.equals(line.trim())) {
							tempUnit.ordersConfirmed = true;
						} else {
							tempUnit.addOrders(line, false);
						}
					}
				}
			}
		}

		return sortIndex;
	}

	/**
	 * Returns a translation from the translation table for the specified key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected static String getString(String key) {
		return com.eressea.util.Translations.getTranslation(Unit.class, key);
	}

	// EXTERNAL TAG METHODS
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
		if(externalMap == null) {
			return false;
		}

		return externalMap.containsKey(tag);
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

	/**
	 * a (hopefully) small class for handling orders in the Unit object
	 */
	private static class Orders {
		private List orders = null;
		private boolean changed = false;

		/**
		 * Creates a new Orders object.
		 */
		public Orders() {
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int getSize() {
			return (orders == null) ? 0 : orders.size();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param newOrders TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public int addOrders(Collection newOrders) {
			int oldSize = getSize();

			if(newOrders == null) {
				return oldSize;
			}

			if(orders == null) {
				orders = CollectionFactory.createArrayList(newOrders.size());
			}

			orders.addAll(newOrders);
			changed = true;

			return oldSize;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param newOrders TODO: DOCUMENT ME!
		 */
		public void setOrders(Collection newOrders) {
			clearOrders();
			addOrders(newOrders);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public List getOrders() {
			return (orders == null) ? Collections.EMPTY_LIST : orders;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void removeOrders() {
			clearOrders();
			orders = null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void clearOrders() {
			if(orders != null) {
				orders.clear();
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean ordersAreNull() {
			return orders == null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param i TODO: DOCUMENT ME!
		 * @param newOrders TODO: DOCUMENT ME!
		 */
		public void addOrderAt(int i, String newOrders) {
			if(orders == null) {
				orders = CollectionFactory.createArrayList(1);
			}

			orders.add(i, newOrders);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param i TODO: DOCUMENT ME!
		 */
		public void removeOrderAt(int i) {
			if(orders == null) {
				orders = CollectionFactory.createArrayList(1);
			}

			orders.remove(i);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean ordersHaveChanged() {
			return changed;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param changed TODO: DOCUMENT ME!
		 */
		public void setOrdersChanged(boolean changed) {
			this.changed = changed;
		}
	}
}
