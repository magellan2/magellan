/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.util.CollectionFactory;

/**
 * Container class for a potion based on its representation in a cr version >= 42.
 */
public class Potion extends DescribedObject {
	private int level = -1;

	/** The ingredients needed for this potion. The list contains <tt>String</tt> objects. */
	private Map ingredients = null;

	/**
	 * Constructs a new Potion object identified by id.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public Potion(ID id) {
		super(id);
	}

	/**
	 * Sets the level of this Potion.
	 *
	 * @param level TODO: DOCUMENT ME!
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns the level of this Potion.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Returns the ingredients required for this potion. The elements are instances of class Item.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection ingredients() {
		return CollectionFactory.unmodifiableCollection(ingredients);
	}

	/**
	 * Returns a specific ingredient of this potion.
	 *
	 * @param key the item type id of the ingredient to be returned.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item getIngredient(ID key) {
		if(this.ingredients != null) {
			return (Item) this.ingredients.get(key);
		}

		return null;
	}

	/**
	 * Puts a new element into the list of ingredients required to brew this potion.
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item addIngredient(Item i) {
		if(this.ingredients == null) {
			this.ingredients = CollectionFactory.createOrderedHashtable();
		}

		this.ingredients.put(i.getItemType().getID(), i);

		return i;
	}

	/**
	 * Removes an item from the list of ingredients required to brew this potion.
	 *
	 * @param key the id of the item's item type to be removed.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Item removeIngredient(ID key) {
		if(this.ingredients != null) {
			return (Item) this.ingredients.remove(key);
		}

		return null;
	}

	/**
	 * Removes all ingredients of this potion.
	 */
	public void clearIngredients() {
		if(this.ingredients != null) {
			this.ingredients.clear();
			this.ingredients = null;
		}
	}

	/**
	 * Merges potion.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curPotion TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newPotion TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Potion curPotion, GameData newGD, Potion newPotion) {
		if(curPotion.getName() != null) {
			newPotion.setName(curPotion.getName());
		}

		if(curPotion.getDescription() != null) {
			newPotion.setDescription(curPotion.getDescription());
		}

		if(curPotion.getLevel() != -1) {
			newPotion.setLevel(curPotion.getLevel());
		}

		if(!curPotion.ingredients().isEmpty()) {
			newPotion.clearIngredients();

			for(Iterator iter = curPotion.ingredients().iterator(); iter.hasNext();) {
				Item i = (Item) iter.next();
				com.eressea.rules.ItemType it = newGD.rules.getItemType(i.getItemType().getID(),
																		true);
				newPotion.addIngredient(new Item(it, i.getAmount()));
			}
		}
	}
}
