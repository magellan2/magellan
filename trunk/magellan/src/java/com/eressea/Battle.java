// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

import java.util.List;

import com.eressea.util.CollectionFactory;

/**
 * Container class for a battle. 
 */
public class Battle extends Identifiable {
	
	/**
	 * defines if this is a default BATTLE or a BATTLESPEC block.
	 */
	private boolean isBattleSpec = false;
	/**
	 * List of messages for this Battle object.
	 */
	private List messages;

	/**
	 * Create an empty Battle object with the specified id.
	 *
	 * @param id an unique identifier for this battle.
	 **/
	public Battle(ID id) {
		super(id);
		messages = CollectionFactory.createLinkedList();
	}

	/**
	 * Create an empty Battle object with the specified id.
	 *
	 * @param id an unique identifier for this battle.
	 * @param spec indicates that the CR representation of this battle
	 * is a BATTLESPEC block in the computer report.
	 **/
	public Battle(ID id, boolean spec) {
		this(id);
		this.isBattleSpec = true;
	}

	/**
	 * Get the messages of this battle.
	 *
	 * @return a reference to the list of messages stored for this
	 * battle. This value is never null.
	 **/
	public List messages() {
		return messages;
	}

	/**
	 * Sets whether the CR representation of this battle is a standard
	 * BATTLE block or a BATTLESPEC block.
	 * 
	 * @param bool set true to mark it as BATTLESPEC block,
	 * false to mark as standard BATTLE block.
	 */
	public void setBattleSpec(boolean bool) {
		this.isBattleSpec = bool;
	}

	/**
	 * Check if the Battle object is a BATTLESPEC or BATTLE block.
	 * 
	 * @return true if the CR representation of this battle is a
	 * BATTLESPEC block, false if it's a standard BATTLE block.
	 */
	public boolean isBattleSpec() {
		return this.isBattleSpec;
	}

	/**
	 * Indicates whether this Battle object is equal to another
	 * object.
	 * 
	 * @param o the Battle object to compare with.
	 * @return true only if o is not null and an instance of
	 * class Battle and o's id is equal to the id of this 
	 * Battle object.
	 */
	public boolean equals(Object o) {
		if (o instanceof Battle) {
			return this.getID().equals(((Battle)o).getID());
		} else {
			return false;
		}
	}
	
	/**
	 * Imposes a natural ordering on Battle objects equivalent to
	 * the natural ordering of their ids.
	 * 
	 * @param o the Battle object to compare with.
	 * @return zero if equal.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Battle)o).getID());
	}
}
