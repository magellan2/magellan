// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

import java.util.Map;

import com.eressea.util.CollectionFactory;

/**
 * Container class for a spell based on its representation in a
 * cr version >= 42.
 */
public class Spell extends DescribedObject {
	private int blockID = -1;		// this is the id of the ZAUBER block in the cr
	private int level = -1;			// a mage's level has to be at least this value to be able to cast this spell
	private int rank = -1;
	private String type = null;		// represents the 'class' tag, can't be named like that, though
	private boolean onShip = false;
	private boolean onOcean = false;
	private boolean isFamiliar = false;
	private boolean isFar = false;
	private Map components = null;	// map of String objects

	public Spell(ID id) {
		super(id);
	}

	/**
	 * Returns the integer serving as the block id in the cr.
	 */
	public int getBlockID() {
		return blockID;
	}

	/**
	 * Sets the integer serving as the block id in the cr.
	 */
	public void setBlockID(int id) {
		this.blockID = id;
	}

	/**
	 * Returns the level of this spell which indicates the lowest
	 * skill level a mage must have to be able to cast this spell.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level of this spell which indicates the lowest
	 * skill level a mage must have to be able to cast this spell.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Returns the class attribute of this spell.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the class attribute of this spell.
	 */
	public void setType(String type) {
		this.type = type;
	}

	public boolean getOnOcean() {
		return onOcean;
	}

	public void setOnOcean(boolean onOcean) {
		this.onOcean = onOcean;
	}

	public boolean getOnShip() {
		return onShip;
	}

	public void setOnShip(boolean onShip) {
		this.onShip = onShip;
	}

	public boolean getIsFamiliar() {
		return isFamiliar;
	}

	public void setIsFamiliar(boolean isFamiliar) {
		this.isFamiliar = isFamiliar;
	}

	public boolean getIsFar() {
		return isFar;
	}
	public void setIsFar(boolean _isFar) {
		isFar = _isFar;
	}

	/**
	 * Returns the components of this spell as Strings.
	 */
	public Map getComponents() {
		return components;
	}

	public void setComponents(Map components) {
		this.components = components;
	}

	public String toString() {
		return this.getName();
	}

	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * Returns a name for this spell's type.
	 */
	public String getTypeName() {
		if (this.type != null) {
			return getString(this.type);
		} else {
			return getString("unspecified");
		}
	}

	/**
	 * Indicates whether this Spell object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class Spell and o's id is equal to the id of this
	 * Spell object.
	 */
	public boolean equals(Object o) {
		if (o instanceof Spell) {
			return this.getID().equals(((Spell)o).getID());
		} else {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on Spell objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Spell)o).getID());
	}

	/**
	 * Merges spells.
	 */
	public static void merge(GameData curGD, Spell curSpell, GameData newGD, Spell newSpell) {
		if (curSpell.getBlockID() != -1) {
			newSpell.setBlockID(curSpell.getBlockID());
		}
		if (curSpell.getName() != null) {
			newSpell.setName(curSpell.getName());
		}
		if (curSpell.getDescription() != null) {
			newSpell.setDescription(curSpell.getDescription());
		}
		if (curSpell.level != -1) {
			newSpell.level = curSpell.level;
		}
		if (curSpell.rank != -1) {
			newSpell.rank = curSpell.rank;
		}
		if (curSpell.type != null) {
			newSpell.type = curSpell.type;
		}
		if (curSpell.onShip != false) {
			newSpell.onShip = curSpell.onShip;
		}
		if (curSpell.onOcean != false) {
			newSpell.onOcean = curSpell.onOcean;
		}
		if (curSpell.isFamiliar != false) {
			newSpell.isFamiliar = curSpell.isFamiliar;
		}
		if (curSpell.isFar != false) {
			newSpell.isFar = curSpell.isFar;
		}
		if (curSpell.components != null && curSpell.components.size() > 0) {
			newSpell.components = CollectionFactory.createHashtable();
			newSpell.components.putAll(curSpell.components);
		}
	}

	private String getString(String key) {
		// TODO: Uebersetzungen in orders.properties verschieben!!!!
		//
		return com.eressea.util.Translations.getTranslation(this,key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("unspecified" , "unknown type");
			defaultTranslations.put("normal" , "ordinary spell");
			defaultTranslations.put("precombat" , "pre-combat spell");
			defaultTranslations.put("combat" , "combat spell");
			defaultTranslations.put("postcombat" , "post-combat spell");
		}
		return defaultTranslations;
	}



}
