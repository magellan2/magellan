// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.ID;
import com.eressea.Item;
import com.eressea.util.CollectionFactory;
import com.eressea.util.ROCollection;
import com.eressea.util.ROIterator;

public class BuildingType extends UnitContainerType {
	private int minSkillLevel = -1;
	private int maxSize = -1;
	private Map rawMaterials = null;
	private Map maintenance = null;
	private Map skillBonuses = null;
	private Map regionTypes = null;

	public BuildingType(ID id) {
		super(id);
	}

	public void addRawMaterial(Item i) {
		if (rawMaterials == null) {
			rawMaterials = CollectionFactory.createHashtable();
		}
		rawMaterials.put(i.getItemType().getID(), i);
	}

	public ROIterator getRawMaterials() {
		if (rawMaterials != null) {
			return new ROIterator(rawMaterials.values().iterator());
		} else {
			return new ROIterator();
		}
	}

	public Item getRawMaterial(ID id) {
		if (rawMaterials != null) {
			return (Item)rawMaterials.get(id);
		} else {
			return null;
		}
	}

	public void addMaintenance(Item i) {
		if (maintenance == null) {
			maintenance = CollectionFactory.createHashtable();
		}
		maintenance.put(i.getItemType().getID(), i);
	}

	public Iterator getMaintenanceItems() {
		if (maintenance != null) {
			return new ROIterator(maintenance.values().iterator());
		} else {
			return (CollectionFactory.createLinkedList()).iterator();
		}
	}

	public Item getMaintenance(ID id) {
		if (maintenance != null) {
			return (Item)maintenance.get(id);
		} else {
			return null;
		}
	}

	public void setMinSkillLevel(int l) {
		minSkillLevel = l;
	}

	public int getMinSkillLevel() {
		return minSkillLevel;
	}

	public void setMaxSize(int m) {
		maxSize = m;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public int getSkillBonus(SkillType skillType) {
		int bonus = 0;
		if (skillBonuses != null) {
			Integer i = (Integer)skillBonuses.get(skillType.getID());
			if (i != null) {
				bonus = i.intValue();
			}
		}
		return bonus;
	}

	public void setSkillBonus(SkillType skillType, int bonus) {
		if (skillBonuses == null) {
			skillBonuses = CollectionFactory.createHashtable();
		}
		skillBonuses.put(skillType.getID(), new Integer(bonus));
	}

	public void addRegionType(RegionType type) {
		if (regionTypes == null) {
			regionTypes = CollectionFactory.createHashtable();
		}
		regionTypes.put(type.getID(), type);
	}

	public boolean containsRegionType(RegionType t) {
		return regionTypes != null &&  regionTypes.containsKey(t.getID());
	}

	public Collection regionTypes() {
		return new ROCollection(regionTypes);
	}

	/**
	 * Indicates whether this BuildingType object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class BuildingType and o's id is equal to the id of this
	 * BuildingType object.
	 */
	public boolean equals(Object o) {
		return this == o || 
		   (o instanceof BuildingType && this.getID().equals(((BuildingType)o).getID()));
	}

	/**
	 * Imposes a natural ordering on BuildingType objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((BuildingType)o).getID());
	}
}
