// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;

import java.util.*;
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

	private Map namedObjects      = CollectionFactory.createOrderedHashtable();
	private Map namedObjectsNames = CollectionFactory.createOrderedHashtable();


	public Iterator getRegionTypeIterator() {
		return getIterator(RegionType.class);
	}
	public RegionType getRegionType(ID id) {
		return getRegionType(id, false);
	}
	public RegionType getRegionType(ID id, boolean add) {
		RegionType r = (RegionType)namedObjects.get(id);
		if (r == null && add) {
			r = (RegionType) addObject(new RegionType(id));
			r.setName(id.toString());
		}
		return r;
	}
	public RegionType getRegionType(String id) {
		return getRegionType(id, false);
	}
	public RegionType getRegionType(String id, boolean add) {
		return getRegionType(StringID.create(id), add);
	}

	public Iterator getRaceIterator() {
		return getIterator(Race.class);
	}
	public Race getRace(ID id) {
		return getRace(id, false);
	}
	public Race getRace(ID id, boolean add) {
		Race r = (Race)namedObjects.get(id);
		if (r == null && add) {
			r = (Race) addObject(new Race(id));
			r.setName(id.toString());
		}
		return r;
	}
	public Race getRace(String id) {
		return getRace(id, false);
	}
	public Race getRace(String id, boolean add) {
		return getRace(StringID.create(id), add);
	}

	public Iterator getShipTypeIterator() {
		return getIterator(ShipType.class);
	}

	public ShipType getShipType(ID id) {
		return getShipType(id, false);
	}

	public ShipType getShipType(ID id, boolean add) {
		ShipType r = (ShipType)namedObjects.get(id);
		if (r == null && add) {
			r = (ShipType) addObject(new ShipType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public ShipType getShipType(String id) {
		return getShipType(id, false);
	}

	public ShipType getShipType(String id, boolean add) {
		return getShipType(StringID.create(id), add);
	}





	public Iterator getBuildingTypeIterator() {
		return getIterator(BuildingType.class);
	}

	public BuildingType getBuildingType(ID id) {
		return getBuildingType(id, false);
	}

	public BuildingType getBuildingType(ID id, boolean add) {
		BuildingType r = (BuildingType)namedObjects.get(id);
		if (r == null && add) {
			r = (BuildingType) addObject(new BuildingType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public BuildingType getBuildingType(String id) {
		return getBuildingType(id, false);
	}

	public BuildingType getBuildingType(String id, boolean add) {
		return getBuildingType(StringID.create(id), add);
	}





	public Iterator getCastleTypeIterator() {
		return getIterator(CastleType.class);
	}

	public CastleType getCastleType(ID id) {
		return getCastleType(id, false);
	}

	public CastleType getCastleType(ID id, boolean add) {
		CastleType r = (CastleType)namedObjects.get(id);
		if (r == null && add) {
			r = (CastleType) addObject(new CastleType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public CastleType getCastleType(String id) {
		return getCastleType(id, false);
	}

	public CastleType getCastleType(String id, boolean add) {
		return getCastleType(StringID.create(id), add);
	}




 
	public Iterator getItemTypeIterator() {
		return getIterator(ItemType.class);
	}

	public ItemType getItemType(ID id) {
		return getItemType(id, false);
	}

	public ItemType getItemType(ID id, boolean add) {
		ItemType r = (ItemType)namedObjects.get(id);
		if (r == null && add) {
			r = (ItemType) addObject(new ItemType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public ItemType getItemType(String id) {
		return getItemType(id, false);
	}

	public ItemType getItemType(String id, boolean add) {
		return getItemType(StringID.create(id), add);
	}



 

	public Iterator getHerbIterator() {
		return getIterator(Herb.class);
	}

	public Herb getHerb(ID id) {
		return getHerb(id, false);
	}

	public Herb getHerb(ID id, boolean add) {
		Herb r = (Herb)namedObjects.get(id);
		if (r == null && add) {
			r = (Herb) addObject(new Herb(id));
			r.setName(id.toString());
		}
		return r;
	}

	public Herb getHerb(String id) {
		return getHerb(id, false);
	}

	public Herb getHerb(String id, boolean add) {
		return getHerb(StringID.create(id), add);
	}





	public Iterator getItemCategoryIterator() {
		return getIterator(ItemCategory.class);
	}

	public ItemCategory getItemCategory(ID id) {
		return getItemCategory(id, false);
	}

	public ItemCategory getItemCategory(ID id, boolean add) {
		ItemCategory r = (ItemCategory)namedObjects.get(id);
		if (r == null && add) {
			r = (ItemCategory) addObject(new ItemCategory(id));
			r.setName(id.toString());
		}
		return r;
	}

	public ItemCategory getItemCategory(String id) {
		return getItemCategory(id, false);
	}

	public ItemCategory getItemCategory(String id, boolean add) {
		return getItemCategory(StringID.create(id), add);
	}





	public Iterator getSkillTypeIterator() {
		return getIterator(SkillType.class);
	}

	public SkillType getSkillType(ID id) {
		return getSkillType(id, false);
	}

	public SkillType getSkillType(ID id, boolean add) {
		SkillType r = (SkillType)namedObjects.get(id);
		if (r == null && add) {
			r = (SkillType) addObject(new SkillType(id));
			r.setName(id.toString());
		}
		return r;
	}

	public SkillType getSkillType(String id) {
		return getSkillType(id, false);
	}

	public SkillType getSkillType(String id, boolean add) {
		return getSkillType(StringID.create(id), add);
	}





	public Iterator getSkillCategoryIterator() {
		return getIterator(SkillCategory.class);
	}

	public SkillCategory getSkillCategory(ID id) {
		return getSkillCategory(id, false);
	}

	public SkillCategory getSkillCategory(ID id, boolean add) {
		SkillCategory r = (SkillCategory)namedObjects.get(id);
		if (r == null && add) {
			r = (SkillCategory) addObject(new SkillCategory(id));
			r.setName(id.toString());
		}
		return r;
	}

	public SkillCategory getSkillCategory(String id) {
		return getSkillCategory(id, false);
	}

	public SkillCategory getSkillCategory(String id, boolean add) {
		return getSkillCategory(StringID.create(id), add);
	}





	public Iterator getOptionCategoryIterator() {
		return getIterator(OptionCategory.class);
	}

	public OptionCategory getOptionCategory(ID id) {
		return getOptionCategory(id, false);
	}

	public OptionCategory getOptionCategory(ID id, boolean add) {
		OptionCategory r = (OptionCategory)namedObjects.get(id);
		if (r == null && add) {
			r = (OptionCategory) addObject(new OptionCategory(id));
			r.setName(id.toString());
		}
		return r;
	}

	public OptionCategory getOptionCategory(String id) {
		return getOptionCategory(id, false);
	}

	public OptionCategory getOptionCategory(String id, boolean add) {
		return getOptionCategory(StringID.create(id), add);
	}





	public Iterator getAllianceCategoryIterator() {
		return getIterator(AllianceCategory.class);
	}

	public AllianceCategory getAllianceCategory(ID id) {
		return getAllianceCategory(id, false);
	}

	public AllianceCategory getAllianceCategory(ID id, boolean add) {
		AllianceCategory r = (AllianceCategory)namedObjects.get(id);
		if (r == null && add) {
			r = (AllianceCategory) addObject(new AllianceCategory(id));
			r.setName(id.toString());
		}
		return r;
	}

	public AllianceCategory getAllianceCategory(String id) {
		return getAllianceCategory(id, false);
	}

	public AllianceCategory getAllianceCategory(String id, boolean add) {
		return getAllianceCategory(StringID.create(id), add);
	}









	private Iterator getIterator(Class c) {
		return new ROIterator(new ClassIterator(c, namedObjects.values().iterator()));
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
	 * Adds the specified object to the specified map by id and by
	 * name.
	 */
	private ObjectType addObject(ObjectType o) {
		namedObjects.put(o.getID(), o);
		if (o.getName() != null) {
			namedObjectsNames.put(Umlaut.normalize(o.getName()), o);
		}
		return o;
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

	private static class ClassIterator implements Iterator {
		private Class    givenClass;
		private Iterator givenIterator;
		private Object   currentObject;
		
		public ClassIterator(Class c, Iterator i) {
			if(c == null) throw new NullPointerException();
			if(i == null) throw new NullPointerException();
			givenClass = c;
			givenIterator = i;
		}
		
		public boolean hasNext() {
			possiblyMoveToNext();
			return currentObject != null;
		}
		
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
			if(currentObject != null) return;
			try {
				Object newObject = null;
				while(givenIterator.hasNext() && newObject == null) {
					newObject = givenIterator.next();
					if(!givenClass.isInstance(newObject)) {
						newObject = null;
					}
				}
				currentObject = newObject;
			} catch(NoSuchElementException e) {
			}
		}

		public void remove() {
			givenIterator.remove();
		}
	}
}
