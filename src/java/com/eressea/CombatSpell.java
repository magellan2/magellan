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

/**
 * A class representing a combat spell set for a certain unit. It links a unit with a certain spell
 * and contains information at which level the unit wants to cast the spell.
 */
public class CombatSpell extends Identifiable {
	private Spell spell;
	private Unit unit;
	private int castingLevel;

	/**
	 * Creates a new CombatSpell object with the specified id.
	 *
	 * @param id the if of the spell.
	 */
	public CombatSpell(ID id) {
		super(id);
	}

	/**
	 * Get the actuell spell to be cast in combat.
	 *
	 * @return the spell to be cast.
	 */
	public Spell getSpell() {
		return spell;
	}

	/**
	 * Specify the actual spell of this CombatSpell.
	 *
	 * @param spell the spell that shall be cast in combat.
	 */
	public void setSpell(Spell spell) {
		this.spell = spell;
	}

	/**
	 * Retrieve the unit that has this combat spell set as a combat spell.
	 *
	 * @return the casting unit.
	 */
	public Unit getUnit() {
		return this.unit;
	}

	/**
	 * Sets the unit which has this combat spell set as a combat spell.
	 *
	 * @param unit the casting unit.
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	/**
	 * Gets the level at which the unit wants to cast this spell.
	 *
	 * @return the level of the spell to be casted.
	 */
	public int getCastingLevel() {
		return this.castingLevel;
	}

	/**
	 * Sets a level at which the unit wants to cast this spell.
	 *
	 * @param castingLevel this value must be greater than 0 and  not greater than the unit's magic
	 * 		  skill level.
	 */
	public void setCastingLevel(int castingLevel) {
		this.castingLevel = castingLevel;
	}

	/**
	 * Returns a String representation of this combat spell.
	 *
	 * @return combat spell object as string.
	 */
	public String toString() {
		return (getSpell() == null) ? ""
									: (getSpell().getTypeName() + ", " + getCastingLevel() + ": " +
									getSpell().toString());
	}

	/**
	 * Indicates whether this CombatSpell object is equal to another object.
	 *
	 * @param o the CombatSpell object to compare with.
	 *
	 * @return true only if o is not null and an instance of class CombatSpell and o's id is equal
	 * 		   to the id of this  CombatSpell object.
	 */
	public boolean equals(Object o) {
		return (this == o) ||
			   (o instanceof CombatSpell && this.getID().equals(((CombatSpell) o).getID()));
	}

	/**
	 * Imposes a natural ordering on CombatSpell objects equivalent to the natural ordering of
	 * their ids.
	 *
	 * @param o the CombatSpell object to compare with.
	 *
	 * @return zero if equal.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((CombatSpell) o).getID());
	}

	/**
	 * Merges two combat spells.
	 *
	 * @param curGD the current GameData.
	 * @param curCS the current CombatSpell.
	 * @param newGD the new GameData.
	 * @param newCS the new CombatSpell.
	 */
	public static void merge(GameData curGD, CombatSpell curCS, GameData newGD, CombatSpell newCS) {
		// transfer the level of the casted spell
		if(curCS.getCastingLevel() != -1) {
			newCS.setCastingLevel(curCS.getCastingLevel());
		}

		// transfer the spell
		if(curCS.getSpell() != null) {
			newCS.setSpell(newGD.getSpell(curCS.getSpell().getID()));
		}

		// transfer the casting unit
		if(curCS.getUnit() != null) {
			newCS.setUnit(newGD.getUnit(curCS.getUnit().getID()));
		}
	}
}
