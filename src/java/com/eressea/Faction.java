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

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.eressea.rules.Options;
import com.eressea.rules.Race;

import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

/**
 * A class representing a faction in Eressea.
 */
public class Faction extends UnitContainer {
	private static final Logger log = Logger.getInstance(Faction.class);

	/* Implementation note on trust levels:
	 * Trust levels have been introduced to replace the inherently
	 * wrong concept of owner factions. This way there can for
	 * example be more than one faction that can edit its units'
	 * orders.
	 * TL_DEFAULT must keep a value of 0, so this equivalent to a
	 * faction without a specified trust level. Trust levels
	 * expressing an increased amount trust or privileges should get
	 * ascending positive numbers, and the other way round with
	 * negative trust levels.
	 * Therefore comparisons for trust levels should in most cases be
	 * 'greater than' or 'less than' relations rather than absolute
	 * comparisons.
	 */

	/** Any faction has this trust level if not otherwise specified. */
	public static final int TL_DEFAULT = 0;

	/**
	 * This trust level indicates that units of this faction may receive new
	 * orders and similar privileges.
	 */
	public static final int TL_PRIVILEGED = 100;

	/**
	 * The password of this faction required for authentication of orders sent
	 * to the Eressea server.
	 */
	public String password = null;

	/** TODO: DOCUMENT ME! */
	public String email = null;

	/** TODO: DOCUMENT ME! */
	public Options options = null;

	/** TODO: DOCUMENT ME! */
	public int score = -1;

	/** TODO: DOCUMENT ME! */
	public int averageScore = -1;

	/** TODO: DOCUMENT ME! */
	public int persons = -1;

	/** TODO: DOCUMENT ME! */
	public int migrants = -1;

	/** TODO: DOCUMENT ME! */
	public int maxMigrants = -1;

	/** TODO: DOCUMENT ME! */
	public String spellSchool = null; // Magiegebiet

	/**
	 * Indicates to what amount this faction can be trusted. It also influences
	 * the privileges of this faction (e.g. being able to edit its units'
	 * orders).
	 */
	public int trustLevel = TL_DEFAULT;

	/**
	 * true: indicates that this trustlevel was explicitly set by the user or
	 * read from a CR-file false: indicates that this is either a default
	 * level or was calculated by Magellan based on the alliances of the
	 * privileged factions.
	 */
	public boolean trustLevelSetByUser = false;

	/** TODO: DOCUMENT ME! */
	public List messages = null; // contains all messages for this faction as <tt>Message</tt> objects

	/** TODO: DOCUMENT ME! */
	public List errors = null; // contains error messages for this faction as <tt>String</tt> objects

	/** TODO: DOCUMENT ME! */
	public List battles = null; // contains the battles, this faction had in the current round, as <tt>Battle</tt> objects

	/**
	 * The allies of this faction are stored in this map with the faction ID of
	 * the ally as key and an <tt>Alliance</tt> object as value.
	 */
	public Map allies = null;

	/**
	 * The different groups in this faction. The map contains <tt>ID</tt>
	 * objects with the group id as keys and <tt>Group</tt> objects as values.
	 */
	public Map groups = null;

	/** The country code indicating the locale for this faction. */
	private Locale locale = null;

	/**
	 * Creates a new Faction object with the specified id on top of the
	 * specified game data object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 */
	public Faction(ID id, GameData data) {
		super(id, data);
	}

	/**
	 * Assigns this faction a locale indicating the language of its report and
	 * the orders.
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setLocale(Locale l) {
		this.locale = l;
	}

	/**
	 * Returns the locale of this faction indicating the language of its report
	 * and orders.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * A faction dependent prefix to be prepended to this faction's race name.
	 */
	private String raceNamePrefix = null;

	/**
	 * Sets the faction dependent prefix for the race name.
	 *
	 * @param prefix TODO: DOCUMENT ME!
	 */
	public void setRaceNamePrefix(String prefix) {
		this.raceNamePrefix = prefix;
	}

	/**
	 * Returns the faction dependent prefix for the race name.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getRaceNamePrefix() {
		return this.raceNamePrefix;
	}

	/**
	 * Returns the race of this faction. This method is an alias for the
	 * getType() method.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace() {
		return (Race) this.getType();
	}

	/**
	 * Returns a string representation of this faction.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return getName() + " (" + this.getID() + ")";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curFaction TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newFaction TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Faction curFaction,
							 GameData newGD, Faction newFaction) {
		UnitContainer.merge(curGD, curFaction, newGD, newFaction);

		if((curFaction.allies != null) && (curFaction.allies.size() > 0)) {
			if(newFaction.allies == null) {
				newFaction.allies = CollectionFactory.createOrderedHashtable();
			} else {
				newFaction.allies.clear();
			}

			for(Iterator iter = curFaction.allies.values().iterator();
					iter.hasNext();) {
				Alliance alliance = (Alliance) iter.next();
				Faction  ally = newGD.getFaction(alliance.getFaction().getID());
				newFaction.allies.put(ally.getID(),
									  new Alliance(ally, alliance.getState()));
			}
		}

		if(curFaction.email != null) {
			newFaction.email = curFaction.email;
		}

		if((curFaction.groups != null) && (curFaction.groups.size() > 0)) {
			if(newFaction.groups == null) {
				newFaction.groups = CollectionFactory.createHashtable();
			} else {
				newFaction.groups.clear();
			}

			for(Iterator iter = curFaction.groups.values().iterator();
					iter.hasNext();) {
				Group curGroup = (Group) iter.next();
				Group newGroup = null;

				try {
					newGroup = new Group((ID) curGroup.getID().clone(), newGD);
				} catch(CloneNotSupportedException e) {
				}

				Group.merge(curGD, curGroup, newGD, newGroup);
				newFaction.groups.put(newGroup.getID(), newGroup);
			}
		}

		if(curFaction.getLocale() != null) {
			newFaction.setLocale(curFaction.getLocale());
		}

		if(curFaction.maxMigrants != -1) {
			newFaction.maxMigrants = curFaction.maxMigrants;
		}

		if(curFaction.options != null) {
			newFaction.options = new Options(curFaction.options);
		}

		if(curFaction.password != null) {
			newFaction.password = curFaction.password;
		}

		if(curFaction.spellSchool != null) {
			newFaction.spellSchool = curFaction.spellSchool;
		}

		// one trustLevel is guaranteed to be TL_DEFAULT
		// ReportMerger.mergeReport() :

		/**
		 * prepare faction trustlevel for merging: - to be added CR is older or
		 * of same age -> hold existing trust levels - to be added CR is newer
		 * and contains trust level that were set by the user explicitly (or
		 * read from CR what means the same) -> take the trust levels out of
		 * the new CR otherwise -> hold existing trust levels This means: set
		 * those trust levels, that will not be retained to default values
		 */
		if((curFaction.trustLevel != Faction.TL_DEFAULT) ||
			   curFaction.trustLevelSetByUser) {
			newFaction.trustLevel		   = curFaction.trustLevel;
			newFaction.trustLevelSetByUser = curFaction.trustLevelSetByUser;
		}

		// see Region.merge() for the meaning of the following if
		if(curGD.getDate().equals(newGD.getDate())) {
			if(curFaction.averageScore != -1) {
				newFaction.averageScore = curFaction.averageScore;
			}

			if((curFaction.battles != null) && (curFaction.battles.size() > 0)) {
				newFaction.battles = CollectionFactory.createLinkedList();

				for(Iterator iter = curFaction.battles.iterator();
						iter.hasNext();) {
					Battle curBattle = (Battle) iter.next();

					try {
						Battle newBattle = new Battle((ID) curBattle.getID()
																	.clone(),
													  curBattle.isBattleSpec());

						for(Iterator msgs = curBattle.messages().iterator();
								msgs.hasNext();) {
							Message curMsg = (Message) msgs.next();
							Message newMsg = new Message((ID) curMsg.getID()
																	.clone());
							Message.merge(curGD, curMsg, newGD, newMsg);
							newBattle.messages().add(newMsg);
						}

						newFaction.battles.add(curBattle);
					} catch(CloneNotSupportedException e) {
						log.error("Faction.merge()", e);
					}
				}
			}

			if((curFaction.errors != null) && (curFaction.errors.size() > 0)) {
				newFaction.errors = CollectionFactory.createLinkedList(curFaction.errors);
			}

			if((curFaction.messages != null) &&
				   (curFaction.messages.size() > 0)) {
				if(newFaction.messages == null) {
					newFaction.messages = CollectionFactory.createLinkedList();
				} else {
					newFaction.messages.clear();
				}

				for(Iterator iter = curFaction.messages.iterator();
						iter.hasNext();) {
					Message curMsg = (Message) iter.next();
					Message newMsg = null;

					try {
						newMsg = new Message((ID) curMsg.getID().clone());
					} catch(CloneNotSupportedException e) {
					}

					Message.merge(curGD, curMsg, newGD, newMsg);
					newFaction.messages.add(newMsg);
				}
			}

			if(curFaction.migrants != -1) {
				newFaction.migrants = curFaction.migrants;
			}

			if(curFaction.persons != -1) {
				newFaction.persons = curFaction.persons;
			}

			if(curFaction.raceNamePrefix != null) {
				newFaction.raceNamePrefix = curFaction.raceNamePrefix;
			}

			if(curFaction.score != -1) {
				newFaction.score = curFaction.score;
			}
		}
	}

	/**
	 * Compares this faction to another object. Returns true only if o is not
	 * null and an instance of class Faction and their id's are equal.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		try {
			return this.getID().equals(((Faction) o).getID());
		} catch(Exception e) {
			return false;
		}
	}

	/**
	 * Imposes a natural ordering on faction objects based on the natural
	 * ordering of their ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Faction) o).getID());
	}
}
