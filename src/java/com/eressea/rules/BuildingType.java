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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.ID;
import com.eressea.Item;

import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class BuildingType extends UnitContainerType {
	private int minSkillLevel = -1;
	private int maxSize = -1;
	private Map rawMaterials = null;
	private Map maintenance = null;
	private Map skillBonuses = null;
	private Map regionTypes = null;

	/**
	 * Creates a new BuildingType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public BuildingType(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void addRawMaterial(Item i) {
		if(rawMaterials == null) {
			rawMaterials = CollectionFactory.createHashtable();
		}

		rawMaterials.put(i.getItemType().getID(), i);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getRawMaterials() {
		return CollectionFactory.unmodifiableIterator(rawMaterials);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item getRawMaterial(ID id) {
		if(rawMaterials != null) {
			return (Item) rawMaterials.get(id);
		} else {
			return null;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void addMaintenance(Item i) {
		if(maintenance == null) {
			maintenance = CollectionFactory.createHashtable();
		}

		maintenance.put(i.getItemType().getID(), i);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getMaintenanceItems() {
		return CollectionFactory.unmodifiableIterator(maintenance);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item getMaintenance(ID id) {
		if(maintenance != null) {
			return (Item) maintenance.get(id);
		} else {
			return null;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setMinSkillLevel(int l) {
		minSkillLevel = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMinSkillLevel() {
		return minSkillLevel;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param m TODO: DOCUMENT ME!
	 */
	public void setMaxSize(int m) {
		maxSize = m;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSkillBonus(SkillType skillType) {
		int bonus = 0;

		if(skillBonuses != null) {
			Integer i = (Integer) skillBonuses.get(skillType.getID());

			if(i != null) {
				bonus = i.intValue();
			}
		}

		return bonus;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 * @param bonus TODO: DOCUMENT ME!
	 */
	public void setSkillBonus(SkillType skillType, int bonus) {
		if(skillBonuses == null) {
			skillBonuses = CollectionFactory.createHashtable();
		}

		skillBonuses.put(skillType.getID(), new Integer(bonus));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param type TODO: DOCUMENT ME!
	 */
	public void addRegionType(RegionType type) {
		if(regionTypes == null) {
			regionTypes = CollectionFactory.createHashtable();
		}

		regionTypes.put(type.getID(), type);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param t TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean containsRegionType(RegionType t) {
		return (regionTypes != null) && regionTypes.containsKey(t.getID());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection regionTypes() {
		return CollectionFactory.unmodifiableCollection(regionTypes);
	}

	/**
	 * Indicates whether this BuildingType object is equal to another object. Returns true only if
	 * o is not null and an instance of class BuildingType and o's id is equal to the id of this
	 * BuildingType object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (this == o) ||
			   (o instanceof BuildingType && this.getID().equals(((BuildingType) o).getID()));
	}

	/**
	 * Imposes a natural ordering on BuildingType objects equivalent to the natural ordering of
	 * their ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((BuildingType) o).getID());
	}
}
