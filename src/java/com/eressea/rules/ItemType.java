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

import com.eressea.ID;
import com.eressea.Item;
import com.eressea.Skill;

import com.eressea.util.CollectionFactory;
import com.eressea.util.Umlaut;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ItemType extends ObjectType {
	private float weight = 0;
	private String iconName = null;
	private Skill makeSkill = null;
	private Skill useSkill = null;
	private ItemCategory category = null;
	private Map resources = null;

	/**
	 * Creates a new ItemType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public ItemType(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param w TODO: DOCUMENT ME!
	 */
	public void setWeight(float w) {
		weight = w;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	public void setMakeSkill(Skill s) {
		makeSkill = s;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Skill getMakeSkill() {
		return makeSkill;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	public void setUseSkill(Skill s) {
		useSkill = s;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Skill getUseSkill() {
		return useSkill;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setCategory(ItemCategory c) {
		this.category = c;

		if(c != null) {
			c.addInstance(this);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getCategory() {
		return this.category;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void addResource(Item i) {
		if(resources == null) {
			resources = CollectionFactory.createHashtable();
		}

		resources.put(i.getItemType().getID(), i);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getResources() {
		return CollectionFactory.unmodifiableIterator(resources);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item getResource(ID id) {
		if(resources != null) {
			return (Item) resources.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Indicates whether this ItemType object is equal to another object. Returns true only if o is
	 * not null and an instance of class ItemType and o's id is equal to the id of this ItemType
	 * object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (this == o) ||
			   (o instanceof ItemType && this.getID().equals(((ItemType) o).getID()));
	}

	/**
	 * Imposes a natural ordering on ItemType objects equivalent to the natural ordering of their
	 * ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((ItemType) o).getID());
	}

	/**
	 * Returns the file name of the icon to use for this item.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getIconName() {
		if(!iconNameEvaluated && (iconName == null)) {
			if(category != null) {
				iconName = category.getIconName();
			}

			if(iconName == null) {
				iconName = getID().toString();
			}

			iconName = Umlaut.convertUmlauts(iconName.toLowerCase());
			iconNameEvaluated = true;
		}

		return iconName;
	}

	private boolean iconNameEvaluated = false;

	/**
	 * Sets the file name of the icon to use for this item.
	 *
	 * @param iName TODO: DOCUMENT ME!
	 */
	public void setIconName(String iName) {
		iconName = iName;
		iconNameEvaluated = false;
	}
}
