// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.Translations;

/**
 * A class representing an alliance status between two factions.
 * The faction having this alliance is implicit, the target faction
 * is an explicite field of this class.
 */
public class Alliance {
	/** A state selector for the "Helfe Silber" state. */
	private static final int SILVER = 1 << 0;
	/** A state selector for the "Helfe Kämpfe" state. */
	private static final int COMBAT = 1 << 1;
	/** A state selector for the "Helfe Gib" state. */
	private static final int GIVE = 1 << 3;
	/** A state selector for the "Helfe Bewache" state. */
	private static final int GUARD = 1 << 4;
	/** A state selector for the "Helfe Parteitarnung" state. */
	private static final int GUISE = 1 << 5;
	/** A state selector for the "Helfe ?" state. */
	private static final int WHATEVER = 1 << 6;
	/** A state selector for all of the alliance states. */
	// private static final int ALL = 0x003B;
	private static final int ALL = SILVER | COMBAT | GIVE | GUARD | GUISE | WHATEVER; // (binary value should be: 111101 (#123) )
	
	private final Faction faction;
	private final Rules rules;
	private int state = 0;

	/**
	 * Create a new Alliance object for an alliance with the
	 * specified faction and without any alliance status set.
	 *
	 * @param faction the faction to establish an alliance with. An
	 * IllegalArgumentException is thrown if faction is null.
	 * @throws IllegalArgumentException if the faction parameter is
	 * null.
	 */
	public Alliance(Faction faction, Rules rules) {
		this(faction, rules, 0);
	}

	/**
	 * Create a new Alliance object for an alliance with the
	 * specified faction and the specified status.
	 *
	 * @param faction the faction to establish an alliance with
	 * 
	 * @param rules the underlying rules object to get connection to 
	 * Alliance Categories.
	 * @param state the alliance status, must be one of constants
	 * SILVER, FIGHT, GIVE, GUARD, GUISE or ALL.
	 * @throws IllegalArgumentException if the faction parameter is
	 * null.
	 */
	public Alliance(Faction faction, Rules rules, int state) {
		if(faction == null) throw new NullPointerException();
		if(rules == null) throw new NullPointerException();
		this.faction = faction;
		this.rules = rules;
		this.state = state;
	}
	
	
	/** copy constructor */
	public Alliance(Alliance orig) {
		this(orig.faction,orig.rules,orig.state);
	}

	/**
	 * Returns the faction this alliance refers to. The return value
	 * is never null.
	 */
	public Faction getFaction() {
		return faction;
	}
	
	/**
	 * Get the state bit-field of this alliance.
	 * 
	 * @return the state bitfield.
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Set the state bit-field of this alliance.
	 * 
	 * @param the state bitfield.
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * Determine whether a specific state of this alliance is set.
	 *
	 * @param selector specifying one of the constants in this class.
	 * @return true if specific state is set, false if not
	 * which state should be evaluated.
	 */
	public boolean getState(int selector) {
		return ((state & selector) == selector);
	}
	
	/**
	 * Get a string representation of the alliance state.
	 * 
	 * @return the alliance state as string.
	 */
	public String stateToString() {
		
		if (getState(ALL)) {
			return Translations.getOrderTranslation(EresseaOrderConstants.O_ALL);
		}
		
		int[] stateSet = { SILVER, COMBAT, GIVE, GUARD, GUISE };
		String[] names = {
			Translations.getOrderTranslation(EresseaOrderConstants.O_SILVER),
			Translations.getOrderTranslation(EresseaOrderConstants.O_COMBAT),
			Translations.getOrderTranslation(EresseaOrderConstants.O_GIVE),
			Translations.getOrderTranslation(EresseaOrderConstants.O_GUARD),
			Translations.getOrderTranslation(EresseaOrderConstants.O_FACTIONSTEALTH)
		};
		StringBuffer retVal = new StringBuffer();
		
		// connect all state strings separated by spaces
		for (int stateNo = 0; stateNo < stateSet.length; stateNo++) {
			if (getState(stateSet[stateNo])) {
				retVal.append(names[stateNo]).append(" ");
			}
		}
		// remove trailing space
		if (retVal.length()>0) {retVal.deleteCharAt(retVal.length() - 1);}
		
		return retVal.toString();
	}
	
	/**
	 * Return a string representation of this alliance object.
	 * 
	 * @return the alliance object as string.
	 */
	public String toString() {
		return faction.toString() + ": " + stateToString();
	}
}
