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
import com.eressea.Item;
import com.eressea.Skill;
import com.eressea.util.CollectionFactory;
import com.eressea.util.ROIterator;
import com.eressea.util.Umlaut;

public class ItemType extends ObjectType {
	private float weight = 0;
	private String iconName = null;
	private Skill makeSkill = null;
	private Skill useSkill = null;
	private ItemCategory category = null;
	private Map resources = null;

	public ItemType(ID id) {
		super(id);
		iconName=id.toString();
	}

	public void setWeight(float w) {
		weight = w;
	}

	public float getWeight() {
		return weight;
	}

	public void setMakeSkill(Skill s) {
		makeSkill = s;
	}

	public Skill getMakeSkill() {
		return makeSkill;
	}

	public void setUseSkill(Skill s) {
		useSkill=s;
	}
	
	public Skill getUseSkill() {
		return useSkill;
	}

	public void setCategory(ItemCategory c) {
		this.category = c;
		if (c != null) {
			c.addInstance(this);
		}
	}

	public ItemCategory getCategory() {
		return this.category;
	}

	public void addResource(Item i) {
		if (resources == null) {
			resources = CollectionFactory.createHashtable();
		}
		resources.put(i.getItemType().getID(), i);
	}

	public Iterator getResources() {
		if (resources != null) {
			return new ROIterator(resources.values().iterator());
		} else {
			return new ROIterator();
		}
	}

	public Item getResource(ID id) {
		if (resources != null) {
			return (Item)resources.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Indicates whether this ItemType object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class ItemType and o's id is equal to the id of this
	 * ItemType object.
	 */
	public boolean equals(Object o) {
		return this == o || 
			(o instanceof ItemType && this.getID().equals(((ItemType)o).getID()));
	}

	/**
	 * Imposes a natural ordering on ItemType objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((ItemType)o).getID());
	}

	/**
	 * Returns the file name of the icon to use for this item.
	 */
	public String getIconName() {
		if (this.iconName == null) {
			this.iconName = Umlaut.convertUmlauts(this.getID().toString()).toLowerCase();
		}
		return this.iconName;
	}

	/**
	 * Sets the file name of the icon to use for this item.
	 */
	public void setIconName(String iName) {
		iconName = iName;
	}
}
