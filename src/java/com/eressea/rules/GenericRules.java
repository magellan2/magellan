// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;

import java.util.Iterator;
import java.util.Map;

import com.eressea.ID;
import com.eressea.NamedObject;
import com.eressea.Rules;
import com.eressea.StringID;
import com.eressea.util.CollectionFactory;
import com.eressea.util.ROIterator;
import com.eressea.util.Umlaut;


/**
 * A class implementing Eressea specific rules. Primarily, this class
 * collects all the well-known object-types which in turn provide
 * information about their properties as they are defined in the
 * rules of Eressea.
 * In fact, there is nothing eressea specific in Rules anymore, so this is
 * the generic rules object.
 */
public class GenericRules implements Rules {

	private Map races = CollectionFactory.createHashtable();
	private Map raceNames = CollectionFactory.createHashtable();
	private Map shipTypes = CollectionFactory.createHashtable();
	private Map shipTypeNames = CollectionFactory.createHashtable();
	private Map buildingTypes = CollectionFactory.createHashtable();
	private Map buildingTypeNames = CollectionFactory.createHashtable();
	private Map regionTypes = CollectionFactory.createHashtable();
	private Map regionTypeNames = CollectionFactory.createHashtable();
	private Map itemTypes = CollectionFactory.createHashtable();
	private Map itemTypeNames = CollectionFactory.createHashtable();
	private Map skillTypes = CollectionFactory.createHashtable();
	private Map skillTypeNames = CollectionFactory.createHashtable();
	private Map itemCategories = CollectionFactory.createHashtable();
	private Map itemCategorieNames = CollectionFactory.createHashtable();
	private Map skillCategories = CollectionFactory.createHashtable();
	private Map skillCategorieNames = CollectionFactory.createHashtable();
	private Map optionCategories = CollectionFactory.createHashtable();
	private Map optionCategorieNames = CollectionFactory.createHashtable();


	public Race addRace(Race t) {
		addObject(t, races, raceNames);
		return t;
	}

	public Iterator getRaces() {
		return new ROIterator(races.values().iterator());
	}

	public Race getRace(ID id) {
		return getRace(id, false);
	}

	public Race getRace(ID id, boolean add) {
		Race r = (Race)races.get(id);
		if (r == null && add) {
			r = this.addRace(new Race(id));
			r.setName(id.toString());
		}
		return r;
	}

	public Race getRace(String name) {
		return (Race)getObjectType(races, raceNames, name);
	}

	public ShipType addShipType(ShipType t) {
		addObject(t, shipTypes, shipTypeNames);
		return t;
	}

	public Iterator getShipTypes() {
		return new ROIterator(shipTypes.values().iterator());
	}

	public ShipType getShipType(ID id) {
		return getShipType(id, false);
	}

	public ShipType getShipType(ID id, boolean add) {
		ShipType r = (ShipType)shipTypes.get(id);
		if (r == null && add) {
			r = this.addShipType(new ShipType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public ShipType getShipType(String name) {
		return (ShipType)getObjectType(shipTypes, shipTypeNames, name);
	}

	public BuildingType addBuildingType(BuildingType t) {
		addObject(t, buildingTypes, buildingTypeNames);
		return t;
	}

	public Iterator getBuildingTypes() {
		return new ROIterator(buildingTypes.values().iterator());
	}

	public BuildingType getBuildingType(ID id) {
		return getBuildingType(id, false);
	}

	public BuildingType getBuildingType(ID id, boolean add) {
		BuildingType r = (BuildingType)buildingTypes.get(id);
		if (r == null && add) {
			r = this.addBuildingType(new BuildingType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public BuildingType getBuildingType(String name) {
		return (BuildingType)getObjectType(buildingTypes, buildingTypeNames, name);
	}

	public RegionType addRegionType(RegionType t) {
		addObject(t, regionTypes, regionTypeNames);
		return t;
	}

	public Iterator getRegionTypes() {
		return new ROIterator(regionTypes.values().iterator());
	}

	public RegionType getRegionType(ID id) {
		return getRegionType(id, false);
	}

	public RegionType getRegionType(ID id, boolean add) {
		RegionType r = (RegionType)regionTypes.get(id);
		if (r == null && add) {
			r = this.addRegionType(new RegionType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public RegionType getRegionType(String name) {
		return (RegionType)getObjectType(regionTypes, regionTypeNames, name);
	}

	public ItemType addItemType(ItemType t) {
		addObject(t, itemTypes, itemTypeNames);
		return t;
	}

	public Iterator getItemTypes() {
		return new ROIterator(itemTypes.values().iterator());
	}

	public ItemType getItemType(ID id) {
		return getItemType(id, false);
	}

	public ItemType getItemType(ID id, boolean add) {
		ItemType r = (ItemType)itemTypes.get(id);
		if (r == null && add) {
			r = this.addItemType(new ItemType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public ItemType getItemType(String name) {
		return (ItemType)getObjectType(itemTypes, itemTypeNames, name);
	}

	public SkillType addSkillType(SkillType t) {
		addObject(t, skillTypes, skillTypeNames);
		return t;
	}

	public Iterator getSkillTypes() {
		return new ROIterator(skillTypes.values().iterator());
	}

	public SkillType getSkillType(ID id) {
		return getSkillType(id, false);
	}

	public SkillType getSkillType(ID id, boolean add) {
		SkillType r = (SkillType)skillTypes.get(id);
		if (r == null && add) {
			r = this.addSkillType(new SkillType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public SkillType getSkillType(String name) {
		return (SkillType)getObjectType(skillTypes, skillTypeNames, name);
	}

	public ItemCategory addItemCategory(ItemCategory t) {
		itemCategories.put(t.getID(), t);
		return t;
	}

	public Iterator getItemCategories() {
		return new ROIterator(itemCategories.values().iterator());
	}

	public ItemCategory getItemCategory(ID id) {
		return getItemCategory(id, false);
	}

	public ItemCategory getItemCategory(ID id, boolean add) {
		ItemCategory r = (ItemCategory)itemCategories.get(id);
		if (r == null && add) {
			r = this.addItemCategory(new ItemCategory(id));
			r.setName(id.toString());
		}
		return r;
	}

	public Iterator getSkillCategories() {
		return new ROIterator(skillCategories.values().iterator());
	}
	
	public SkillCategory getSkillCategory(ID id, boolean add) {
		SkillCategory r = (SkillCategory)skillCategories.get(id);
		if (r == null && add) {
			r = this.addSkillCategory(new SkillCategory(id));
			r.setName(id.toString());
		}
		return r;
	}
	
	public SkillCategory addSkillCategory(SkillCategory t) {
		skillCategories.put(t.getID(), t);
		return t;
	}
	
	public SkillCategory getSkillCategory(ID id) {
		return getSkillCategory(id, false);
	}

	public OptionCategory getOptionCategory(ID id, boolean add) {
		OptionCategory r = (OptionCategory)optionCategories.get(id);
		if (r == null && add) {
			r = this.addOptionCategory(new OptionCategory(id));
			r.setName(id.toString());
		}
		return r;
	}
	
	public OptionCategory addOptionCategory(OptionCategory t) {
		optionCategories.put(t.getID(), t);
		return t;
	}

	public Iterator getOptionCategories() {
		return new ROIterator(optionCategories.values().iterator());
	}
	
	public ObjectType changeName(String from, String to) {
		return changeName(StringID.create(from), to);
	}

	/**
	 * Changes the name of an object identified by the specified id.
	 * 
	 * This method serves as a convenience as it relieves the
	 * implementor of the arduous task of determining the kind of
	 * object type (ItemType, SkillType etc.) and accessing the
	 * corresponding data structures. It also ensures that the object
	 * is also accessible by calling the getXXX methods with the new
	 * name.
	 */
	private ObjectType changeName(ID id, String name) {
		ObjectType ot = null;
		if ((ot = getBuildingType(id)) != null) {
			if (!ot.getName().equals(name)) {
				buildingTypeNames.remove(Umlaut.normalize(ot.getName()));
				ot.setName(name);
				addObject(ot, buildingTypes, buildingTypeNames);
			}
		} else
		if ((ot = getItemType(id)) != null) {
			if (!ot.getName().equals(name)) {
				itemTypeNames.remove(Umlaut.normalize(ot.getName()));
				ot.setName(name);
				addObject(ot, itemTypes, itemTypeNames);
			}
		} else
		if ((ot = getRace(id)) != null) {
			if (!ot.getName().equals(name)) {
				raceNames.remove(Umlaut.normalize(ot.getName()));
				ot.setName(name);
				addObject(ot, races, raceNames);
			}
		} else
		if ((ot = getRegionType(id)) != null) {
			if (!ot.getName().equals(name)) {
				regionTypeNames.remove(Umlaut.normalize(ot.getName()));
				ot.setName(name);
				addObject(ot, regionTypes, regionTypeNames);
			}
		} else
		if ((ot = getShipType(id)) != null) {
			if (!ot.getName().equals(name)) {
				shipTypeNames.remove(Umlaut.normalize(ot.getName()));
				ot.setName(name);
				addObject(ot, shipTypes, shipTypeNames);
			}
		} else
		if ((ot = getSkillType(id)) != null) {
			if (!ot.getName().equals(name)) {
				skillTypeNames.remove(Umlaut.normalize(ot.getName()));
				ot.setName(name);
				addObject(ot, skillTypes, skillTypeNames);
			}
		}

		return ot;
	}

	/**
	 * Adds the specified object to the specified map by id and by
	 * name.
	 */
	private void addObject(NamedObject o, Map m, Map names) {
		m.put(o.getID(), o);
		if (o.getName() != null) {
			names.put(Umlaut.normalize(o.getName()), o);
		}
	}

	/**
	 * Tries to retrieve an object type form the specified map by
	 * its name.
	 * If the name is not used as a key in the map but an object
	 * with the specified name exists, the object is put into the map
	 * with the name as its key for speeding up future look-ups.
	 */
	private ObjectType getObjectType(Map objects, Map names, String name) {
		String normName = Umlaut.normalize(name);
		if (names.containsKey(normName)) {
			return (ObjectType)names.get(normName);
		} else {
			for (Iterator iter = objects.values().iterator(); iter.hasNext(); ) {
				ObjectType ot = (ObjectType)iter.next();
				if (Umlaut.normalize(ot.getName()).equals(normName)) {
					names.put(normName, ot);
					return ot;
				}
			}
		}
		return null;
	}

}
