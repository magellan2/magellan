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

package com.eressea.rules;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.eressea.ID;
import com.eressea.Rules;
import com.eressea.StringID;

import com.eressea.util.CollectionFactory;
import com.eressea.util.Umlaut;
import com.eressea.util.logging.Logger;

/**
 * A class implementing Eressea specific rules. Primarily, this class collects
 * all the well-known object-types which in turn provide information about
 * their properties as they are defined in the rules of Eressea. In fact,
 * there is nothing eressea specific in Rules anymore, so this is the generic
 * rules object.
 */
public class GenericRules implements Rules {
	private static final Logger log = Logger.getInstance(GenericRules.class);

	// Map consisting of Race, RegionType, ShipType, BuildingType, CastleType
	private Map mapUnitContainerType	  = CollectionFactory.createOrderedHashtable();
	private Map mapUnitContainerTypeNames = CollectionFactory.createOrderedHashtable();

	// Map consisting of ItemType, Herb
	private Map mapItemType		 = CollectionFactory.createOrderedHashtable();
	private Map mapItemTypeNames = CollectionFactory.createOrderedHashtable();

	// Map consisting of AllianceCategory
	private Map mapAllianceCategory		 = CollectionFactory.createOrderedHashtable();
	private Map mapAllianceCategoryNames = CollectionFactory.createOrderedHashtable();

	// Map consisting of OptionCategory
	private Map mapOptionCategory	   = CollectionFactory.createOrderedHashtable();
	private Map mapOptionCategoryNames = CollectionFactory.createOrderedHashtable();

	// Map consisting of ItemCategory
	private Map mapItemCategory		 = CollectionFactory.createOrderedHashtable();
	private Map mapItemCategoryNames = CollectionFactory.createOrderedHashtable();

	// Map consisting of SkillCategory
	private Map mapSkillCategory	  = CollectionFactory.createOrderedHashtable();
	private Map mapSkillCategoryNames = CollectionFactory.createOrderedHashtable();

	// Map consisting of SkillType
	private Map mapSkillType	  = CollectionFactory.createOrderedHashtable();
	private Map mapSkillTypeNames = CollectionFactory.createOrderedHashtable();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getRegionTypeIterator() {
		return getIterator(RegionType.class, mapUnitContainerType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(ID id) {
		return getRegionType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(ID id, boolean add) {
		Object uct = mapUnitContainerType.get(id);
		if(uct != null && ! (uct instanceof RegionType)) {
			return null;
		}
		RegionType r = (RegionType) mapUnitContainerType.get(id);

		if((r == null) && add) {
			r = (RegionType) addObject(new RegionType(id),
									   mapUnitContainerType,
									   mapUnitContainerTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(String id) {
		return getRegionType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getRegionType(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getShipTypeIterator() {
		return getIterator(ShipType.class, mapUnitContainerType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(ID id) {
		return getShipType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(ID id, boolean add) {
		Object uct = mapUnitContainerType.get(id);
		if(uct != null && ! (uct instanceof ShipType)) {
			return null;
		}
		ShipType r = (ShipType) mapUnitContainerType.get(id);

		if((r == null) && add) {
			r = (ShipType) addObject(new ShipType(id), mapUnitContainerType,
									 mapUnitContainerTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(String id) {
		return getShipType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getShipType(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getBuildingTypeIterator() {
		return getIterator(BuildingType.class, mapUnitContainerType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(ID id) {
		return getBuildingType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(ID id, boolean add) {
		Object uct = mapUnitContainerType.get(id);
		if(uct != null && ! (uct instanceof BuildingType)) {
			return null;
		}
		BuildingType r = (BuildingType) mapUnitContainerType.get(id);

		if((r == null) && add) {
			r = (BuildingType) addObject(new BuildingType(id),
										 mapUnitContainerType,
										 mapUnitContainerTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(String id) {
		return getBuildingType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getBuildingType(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getCastleTypeIterator() {
		return getIterator(CastleType.class, mapUnitContainerType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(ID id) {
		return getCastleType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(ID id, boolean add) {
		Object uct = mapUnitContainerType.get(id);
		if(uct != null && ! (uct instanceof CastleType)) {
			return null;
		}
		CastleType r = (CastleType) mapUnitContainerType.get(id);

		if((r == null) && add) {
			r = (CastleType) addObject(new CastleType(id),
									   mapUnitContainerType,
									   mapUnitContainerTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(String id) {
		return getCastleType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getCastleType(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getRaceIterator() {
		return getIterator(Race.class, mapUnitContainerType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(ID id) {
		return getRace(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(ID id, boolean add) {
		Object uct = mapUnitContainerType.get(id);
		if(uct != null && ! (uct instanceof Race)) {
			return null;
		}
		Race r = (Race) mapUnitContainerType.get(id);

		if((r == null) && add) {
			r = (Race) addObject(new Race(id), mapUnitContainerType,
								 mapUnitContainerTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(String id) {
		return getRace(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getRace(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getItemTypeIterator() {
		return getIterator(ItemType.class, mapItemType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(ID id) {
		return getItemType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(ID id, boolean add) {
		ItemType r = (ItemType) mapItemType.get(id);

		if((r == null) && add) {
			r = (ItemType) addObject(new ItemType(id), mapItemType,
									 mapItemTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(String id) {
		return getItemType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getItemType(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getHerbIterator() {
		return getIterator(Herb.class, mapItemType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(ID id) {
		return getHerb(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(ID id, boolean add) {
		Object uct = mapItemType.get(id);
		if(uct != null && ! (uct instanceof Herb)) {
			return null;
		}
		Herb r = (Herb) mapItemType.get(id);

		if((r == null) && add) {
			r = (Herb) addObject(new Herb(id), mapItemType, mapItemTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(String id) {
		return getHerb(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getHerb(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getAllianceCategoryIterator() {
		return getIterator(AllianceCategory.class, mapAllianceCategory);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(ID id) {
		return getAllianceCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(ID id, boolean add) {
		AllianceCategory r = (AllianceCategory) mapAllianceCategory.get(id);

		if((r == null) && add) {
			r = (AllianceCategory) addObject(new AllianceCategory(id),
											 mapAllianceCategory,
											 mapAllianceCategoryNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(String id) {
		return getAllianceCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getAllianceCategory(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getOptionCategoryIterator() {
		return getIterator(OptionCategory.class, mapOptionCategory);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(ID id) {
		return getOptionCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(ID id, boolean add) {
		OptionCategory r = (OptionCategory) mapOptionCategory.get(id);

		if((r == null) && add) {
			r = (OptionCategory) addObject(new OptionCategory(id),
										   mapOptionCategory,
										   mapOptionCategoryNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(String id) {
		return getOptionCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getOptionCategory(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getSkillCategoryIterator() {
		return getIterator(SkillCategory.class, mapSkillCategory);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(ID id) {
		return getSkillCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(ID id, boolean add) {
		SkillCategory r = (SkillCategory) mapSkillCategory.get(id);

		if((r == null) && add) {
			r = (SkillCategory) addObject(new SkillCategory(id),
										  mapSkillCategory,
										  mapSkillCategoryNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(String id) {
		return getSkillCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getSkillCategory(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getItemCategoryIterator() {
		return getIterator(ItemCategory.class, mapItemCategory);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(ID id) {
		return getItemCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(ID id, boolean add) {
		ItemCategory r = (ItemCategory) mapItemCategory.get(id);

		if((r == null) && add) {
			r = (ItemCategory) addObject(new ItemCategory(id), mapItemCategory,
										 mapItemCategoryNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(String id) {
		return getItemCategory(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getItemCategory(StringID.create(id), add);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getSkillTypeIterator() {
		return getIterator(SkillType.class, mapSkillType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(ID id) {
		return getSkillType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(ID id, boolean add) {
		SkillType r = (SkillType) mapSkillType.get(id);

		if((r == null) && add) {
			r = (SkillType) addObject(new SkillType(id), mapSkillType,
									  mapSkillTypeNames);
			r.setName(id.toString());
		}

		return r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(String id) {
		return getSkillType(id, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(String id, boolean add) {
		if((id == null) || id.equals("")) {
			return null;
		}

		return getSkillType(StringID.create(id), add);
	}

	private Iterator getIterator(Class c, Map m) {
		return new ClassIterator(c, CollectionFactory.unmodifiableIterator(m));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param from TODO: DOCUMENT ME!
	 * @param to TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ObjectType changeName(String from, String to) {
		return changeName(StringID.create(from), to);
	}

	/**
	 * Changes the name of an object identified by the specified id.  This
	 * method serves as a convenience as it relieves the implementor of the
	 * arduous task of determining the kind of object type (ItemType,
	 * SkillType etc.) and accessing the corresponding data structures. It
	 * also ensures that the object is also accessible by calling the getXXX
	 * methods with the new name.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private ObjectType changeName(ID id, String name) {
		ObjectType ot = null;

		/*
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
		*/
		return ot;
	}

	/**
	 * Adds the specified object to the specified map by id and by name.
	 *
	 * @param o TODO: DOCUMENT ME!
	 * @param m TODO: DOCUMENT ME!
	 * @param n TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private ObjectType addObject(ObjectType o, Map m, Map n) {
		if(log.isDebugEnabled()) {
			log.debug("GenericRules.addObject(" + o.getClass().toString() +
					  "," + o.getID() + ")");
		}

		m.put(o.getID(), o);

		if(o.getName() != null) {
			n.put(Umlaut.normalize(o.getName()), o);
		}

		return o;
	}

	/**
	 * Tries to retrieve an object type form the specified map by its name. If
	 * the name is not used as a key in the map but an object with the
	 * specified name exists, the object is put into the map with the name as
	 * its key for speeding up future look-ups.
	 *
	 * @param objects TODO: DOCUMENT ME!
	 * @param names TODO: DOCUMENT ME!
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private ObjectType getObjectType(Map objects, Map names, String name) {
		String normName = Umlaut.normalize(name);

		if(names.containsKey(normName)) {
			return (ObjectType) names.get(normName);
		} else {
			for(Iterator iter = objects.values().iterator(); iter.hasNext();) {
				ObjectType ot = (ObjectType) iter.next();

				if(Umlaut.normalize(ot.getName()).equals(normName)) {
					names.put(normName, ot);

					return ot;
				}
			}
		}

		return null;
	}

	private static class ClassIterator implements Iterator {
		private Class    givenClass;
		private Iterator givenIterator;
		private Object   currentObject;

		/**
		 * Creates a new ClassIterator object.
		 *
		 * @param c TODO: DOCUMENT ME!
		 * @param i TODO: DOCUMENT ME!
		 *
		 * @throws NullPointerException TODO: DOCUMENT ME!
		 */
		public ClassIterator(Class c, Iterator i) {
			if(c == null) {
				throw new NullPointerException();
			}

			if(i == null) {
				throw new NullPointerException();
			}

			givenClass    = c;
			givenIterator = i;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean hasNext() {
			possiblyMoveToNext();

			return currentObject != null;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 *
		 * @throws NoSuchElementException TODO: DOCUMENT ME!
		 */
		public Object next() {
			possiblyMoveToNext();

			if(currentObject == null) {
				throw new NoSuchElementException();
			}

			Object ret = currentObject;
			currentObject = null;

			return ret;
		}

		private void possiblyMoveToNext() {
			if(currentObject != null) {
				return;
			}

			try {
				Object newObject = null;

				while(givenIterator.hasNext() && (newObject == null)) {
					newObject = givenIterator.next();

					if(!givenClass.isInstance(newObject)) {
						newObject = null;
					}
				}

				currentObject = newObject;
			} catch(NoSuchElementException e) {
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 */
		public void remove() {
			givenIterator.remove();
		}
	}
}
