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

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.eressea.cr.Loader;
import com.eressea.gamebinding.GameSpecificStuff;
import com.eressea.io.file.FileType;
import com.eressea.rules.Date;
import com.eressea.rules.EresseaDate;
import com.eressea.rules.MessageType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.IDBaseConverter;
import com.eressea.util.Locales;
import com.eressea.util.Regions;
import com.eressea.util.logging.Logger;

/**
 * This is the central class for collecting all the data representing one computer report.
 *
 * <p>
 * The maps units, regions and so on are declared as abstract methods and the getX and addX provide
 * access to them. This allows for subclasses that implicitely represent only a certain part of
 * the game data by declaring certain maps as <tt>null</tt> and returning <tt>null</tt> on the
 * corresponding getX() methods. This concept has so far not been applied and you usually operate
 * on the <tt>CompleteData</tt> subclass.
 * </p>
 */
public abstract class GameData implements Cloneable {
	private static final Logger log = Logger.getInstance(GameData.class);

	/** Game specific and usually fixed data (like races etc.). */
	public final Rules rules;

	/** The name of the game. */
	public final String name;

	/**
	 * The current TempUnit-ID. This means, if a new TempUnit is created, it's suggested ID is
	 * usually curTempID and if this suggestion is accepted by the user (which means, that a
	 * TempUnit with this id was created) curTempID is increased. A value of -1 indicates, that
	 * the variable is uninitialized and a value of 0 that the old system shall be used (which
	 * means, that the suggested temp id shall be calculated out of the id of the parent unit of
	 * the tempunit).
	 */
	protected int curTempID = -1;

	/**
	 * This method sets the current temp id with respect to the possible  max value of the current
	 * base. The value also has to be >= -1
	 *
	 * @param newTempID temp id
	 */
	public void setCurTempID(int newTempID) {
		curTempID = Math.max(-1, Math.min(newTempID, IDBaseConverter.getMaxId(this.base)));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	public void setCurTempID(String s) {
		setCurTempID("".equals(s) ? 0 : IDBaseConverter.parse(s));
	}

	/**
	 * This method sets the current temp id.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getCurTempID() {
		return curTempID;
	}

	/**
	 * The current file attached to the game data. If it is null, the  save as dialog shall be
	 * opened.
	 */
	public FileType filetype = null;

	/**
	 * The 'round' this game data belongs to. Note that this imposes a restriction on how
	 * fine-grained date information can be applied to game data or certain parts of it. This will
	 * probably have to be changed the one or the other way.
	 */
	protected Date date = null;

	/** The 'mail' connection this game data belongs to. This may  be null */
	public String mailTo = null;

	/** The 'mail' subject for this game data. This may  be null */
	public String mailSubject = null;

	/**
	 * A collection of all units. The keys are <tt>Integer</tt> objects containg the unit's ids.
	 * The values consist of objects of class <tt>Unit</tt>. TEMP units are not included, they are
	 * only stored in the unit collection of their parents and their regions and in the tempUnits map.
	 *
	 * @return returns the units map
	 */
	public abstract Map units();

	/**
	 * A collection of tempUnits. The keys are <tt>Integer</tt> objects containg the unit's ids.
	 * The values consist of objects of class <tt>TempUnit</tt>.
	 * @return returns the tempunits map
	 */
	public abstract Map tempUnits();

	/**
	 * All regions in this game data. The keys are <tt>Coordinate</tt> objects containg the id of
	 * each region. The values consist of objects of class <tt>Region</tt>.
	 *
	 * @return returns the regions map
	 */
	public abstract Map regions();

	/**
	 * All factions in this game data. The keys are <tt>Integer</tt> objects containg the id of
	 * each faction. The values consist of objects of class <tt>Faction</tt>. One of these
	 * factions can be referenced by the ownerFaction attribute.
	 *
	 * @return returns the factions map
	 */
	public abstract Map factions();

	/**
	 * All buildings in this game data. The keys are <tt>Integer</tt> objects containg the id of
	 * each building. The values consist of objects of class <tt>Building</tt>.
	 *
	 * @return returns the buildings map
	 */
	public abstract Map buildings();

	/**
	 * All ships in this game data. The keys are <tt>Integer</tt> objects containg the id of each
	 * ship. The values consist of objects of class <tt>Ship</tt>.
	 *
	 * @return returns the ships map
	 */
	public abstract Map ships();

	/**
	 * All message types in this game data. The keys are <tt>Integer</tt> objects containg the id
	 * of each message type. The values consist of <tt>MessageType</tt> objects.
	 *
	 * @return returns the messageType map
	 */
	public abstract Map msgTypes();

	/**
	 * All magic spells in this game data. The keys are <tt>Integer</tt> objects containg the id of
	 * each spell. The values consist of objects of class <tt>Spell</tt>.
	 *
	 * @return returns the spells map
	 */
	public abstract Map spells();

	/**
	 * All potions in this game data. The keys are <tt>Integer</tt> objects containg the id of each
	 * potion. The values consist of objects of class <tt>Potion</tt>.
	 *
	 * @return returns the potions map
	 */
	public abstract Map potions();

	/**
	 * All islands in this game data. The keys are <tt>Integer</tt> objects containing the id of
	 * each island. The values consist of objects of class <tt>Island</tt>.
	 *
	 * @return returns the islands map
	 */
	public abstract Map islands();

	/**
	 * All HotSpots existing for this game data. Hot spots are used to quickly access regions of
	 * interest on the map. The keys are Integer representations of the hot spot id, the values
	 * are Coordinate objects.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract Map hotSpots();

	/**
	 * Represents the table of translations from the report.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract Map translations();

	/**
	 * Creates a new GameData object.
	 *
	 * @param _rules TODO: DOCUMENT ME!
	 */
	public GameData(Rules _rules) {
		this(_rules, "default");
	}

	/**
	 * Creates a new GameData object.
	 *
	 * @param _rules TODO: DOCUMENT ME!
	 * @param _name TODO: DOCUMENT ME!
	 */
	public GameData(Rules _rules, String _name) {
		if(_rules == null) throw new NullPointerException();
		rules = _rules;
		name = _name;
	}

	/**
	 * Retrieve a building from buildings() by id.
	 *
	 * @param id the id of the building to be retrieved.
	 *
	 * @return an instance of class <tt>Building</tt> or <tt>null</tt> if there is no building with
	 * 		   the specified id or if buildings() is <tt>null</tt>.
	 */
	public Building getBuilding(ID id) {
		return (buildings() == null) ? null : (Building) buildings().get(id);
	}

	/**
	 * Retrieve a ship from ships() by id.
	 *
	 * @param id the id of the ship to be retrieved.
	 *
	 * @return an instance of class <tt>Ship</tt> or <tt>null</tt> if there is no ship with the
	 * 		   specified id or if ships() is <tt>null</tt>.
	 */
	public Ship getShip(ID id) {
		return (ships() == null) ? null : (Ship) ships().get(id);
	}

	/**
	 * Retrieve a faction from factions() by id.
	 *
	 * @param id the id of the faction to be retrieved.
	 *
	 * @return an instance of class <tt>Faction</tt> or <tt>null</tt> if there is no faction with
	 * 		   the specified id or if factions() is <tt>null</tt>.
	 */
	public Faction getFaction(ID id) {
		return (factions() == null) ? null : (Faction) factions().get(id);
	}

	/**
	 * Retrieve a unit from units() by id.
	 *
	 * @param id the id of the unit to be retrieved.
	 *
	 * @return an instance of class <tt>Unit</tt> or <tt>null</tt> if there is no unit with the
	 * 		   specified id or if units() is <tt>null</tt>.
	 */
	public Unit getUnit(ID id) {
		return (units() == null) ? null : (Unit) units().get(id);
	}

	/**
	 * Retrieve a region from regions() by id.
	 *
	 * @param id region coordinate
	 *
	 * @return an instance of class <tt>Region</tt> or <tt>null</tt> if there is no region with the
	 * 		   specified coordinates or if regions() is <tt>null</tt>.
	 */
	public Region getRegion(ID id) {
		return (regions() == null) ? null : (Region) regions().get(id);
	}

	/**
	 * Retrieve a message type from msgTypes() by id.
	 *
	 * @param id the id of the message type to be retrieved.
	 *
	 * @return an instance of class <tt>MessageType</tt> or <tt>null</tt> if there is no message
	 * 		   type with the specified id or if msgTypes() is <tt>null</tt>.
	 */
	public MessageType getMsgType(ID id) {
		return (msgTypes() == null) ? null : (MessageType) msgTypes().get(id);
	}

	/**
	 * Retrieve a spell from spells() by id.
	 *
	 * @param id the id of the spell to be retrieved.
	 *
	 * @return an instance of class <tt>Spell</tt> or <tt>null</tt> if there is no spell with the
	 * 		   specified id or if spells() is <tt>null</tt>.
	 */
	public Spell getSpell(ID id) {
		return (spells() == null) ? null : (Spell) spells().get(id);
	}

	/**
	 * Retrieve a potion from potions() by id.
	 *
	 * @param id the id of the potion to be retrieved.
	 *
	 * @return an instance of class <tt>Potion</tt> or <tt>null</tt> if there is no potion with the
	 * 		   specified id or if potions() is <tt>null</tt>.
	 */
	public Potion getPotion(ID id) {
		return (potions() == null) ? null : (Potion) potions().get(id);
	}

	/**
	 * Retrieve a island from islands() by id.
	 *
	 * @param id the id of the island to be retrieved.
	 *
	 * @return an instance of class <tt>Island</tt> or <tt>null</tt> if there is no island with the
	 * 		   specified id or if islands() is <tt>null</tt>.
	 */
	public Island getIsland(ID id) {
		return (islands() == null) ? null : (Island) islands().get(id);
	}

	/**
	 * Add a faction to the specified game data. If factions() is <tt>null</tt>, this method has no
	 * effect.
	 *
	 * @param f the faction to be added.
	 */
	public void addFaction(Faction f) {
		if(factions() != null) {
			factions().put(f.getID(), f);
		}
	}

	/**
	 * Add a unit to the specified game data. If units() is <tt>null</tt>, this method has no
	 * effect.
	 *
	 * @param u the unit to be added.
	 */
	public void addUnit(Unit u) {
		if(units() != null) {
			units().put(u.getID(), u);
		}
	}

	/**
	 * Add a region to the specified game data. If regions() is <tt>null</tt>, this method has no
	 * effect.
	 *
	 * @param r the region to be added.
	 */
	public void addRegion(Region r) {
		if(regions() != null) {
			regions().put(r.getID(), r);
		}
	}

	/**
	 * Add a ship to the specified game data. If ships() is <tt>null</tt>, this method has no
	 * effect.
	 *
	 * @param s the ship to be added.
	 */
	public void addShip(Ship s) {
		if(ships() != null) {
			ships().put(s.getID(), s);
		}
	}

	/**
	 * Add a building to the specified game data. If buildings() is <tt>null</tt>, this method has
	 * no effect.
	 *
	 * @param b the building to be added.
	 */
	public void addBuilding(Building b) {
		if(buildings() != null) {
			buildings().put(b.getID(), b);
		}
	}

	/**
	 * Add a message type to the specified game data. If msgTypes() is <tt>null</tt>, this method
	 * has no effect.
	 *
	 * @param type the message type to be added.
	 */
	public void addMsgType(MessageType type) {
		if(msgTypes() != null) {
			msgTypes().put(type.getID(), type);
		}
	}

	/**
	 * Add a spell to the specified game data. If spells() is <tt>null</tt>, this method has no
	 * effect.
	 *
	 * @param s the spells to be added.
	 */
	public void addSpell(Spell s) {
		if(spells() != null) {
			spells().put(s.getID(), s);
		}
	}

	/**
	 * Add a pption to the specified game data. If potions() is <tt>null</tt>, this method has no
	 * effect.
	 *
	 * @param p the potion to be added.
	 */
	public void addPotion(Potion p) {
		if(potions() != null) {
			potions().put(p.getID(), p);
		}
	}

	/**
	 * Add an island to the specified game data. If islands() is <tt>null</tt>, this method has no
	 * effect.
	 *
	 * @param i the island to be added.
	 */
	public void addIsland(Island i) {
		if(islands() != null) {
			islands().put(i.getID(), i);
		}
	}

	/**
	 * Add or set a hot spot to the specified game data. If hotSpots() is <tt>null</tt>, this
	 * method has no effect.
	 *
	 * @param h the hot spot to be added.
	 */
	public void setHotSpot(HotSpot h) {
		if(hotSpots() != null) {
			hotSpots().put(h.getID(), h);
		}
	}

	/**
	 * Retrieve a hot spot from hotSpots() by its id.
	 *
	 * @param id the id of the hot spot to be retrieved.
	 *
	 * @return an instance of class <tt>HotSpot</tt> or <tt>null</tt> if there is no hot spot with
	 * 		   the specified id or if hotSpots() is <tt>null</tt>.
	 */
	public HotSpot getHotSpot(ID id) {
		return (hotSpots() == null) ? null : (HotSpot) hotSpots().get(id);
	}

	/**
	 * Remove a hot spot from hotSpots() by its id.
	 *
	 * @param id the id of the hot spot to be removed.
	 */
	public void removeHotSpot(ID id) {
		if(hotSpots() != null) {
			hotSpots().remove(id);
		}
	}

	/**
	 * Puts a translation into the translation table.
	 *
	 * @param from a language independent key.
	 * @param to the language dependent translation of key.
	 */
	public void addTranslation(String from, String to) {
		if(translations() != null) {
			translations().put(from, to);

			if(rules != null) {
				// dynamically add translation key to rules to access object by name
				rules.changeName(from, to);
			}
		}
	}

	/**
	 * Retrieve a translation from translations().
	 *
	 * @param key the key of the translation to be retrieved.
	 *
	 * @return an instance of class <tt>String</tt> or <tt>null</tt> if there is no value mapping
	 * 		   to the specified key of if translations() is <tt>null</tt>.
	 */
	public String getTranslation(String key) {
		return (translations() == null) ? null : (String) translations().get(key);
	}

	/**
	 * Retrieve a translation from translations().
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return an instance of class <tt>String</tt>. If no translation could be found, the key is
	 * 		   returned.
	 */
	public String getTranslationOrKeyIfNull(String key) {
		String retVal = getTranslation(key);

		if(retVal != null) {
			return retVal;
		} else {
			return key;
		}
	}

	/**
	 * Set a date, or a 'round', for this game data.
	 *
	 * @param d the new date.
	 */
	public void setDate(Date d) {
		date = d;
	}

	/**
	 * Get the date associated with this game data.
	 *
	 * @return rules.Date object
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * The base (radix) in which ids are interpreted. The default value is 10. Note that all
	 * internal and cr representation is always decimal.
	 */
	public int base = 10;

	/**
	 * Indicates whether in this report skill points are to be expected or whether they are
	 * meaningful, respecitively.
	 */
	public boolean noSkillPoints = false;

	/**
	 * Sets the region at origin as the map origin. (e.g. an origin of (1,0,0) moves all regions in
	 * level 0 one step to the west using eressea coordinates)
	 *
	 * @param origin translation vector as coordinate object
	 */
	public void placeOrigin(Coordinate origin) {
		// fast break
		if((regions() == null) || (origin == null) ||
			   ((origin.x == 0) && (origin.y == 0) && (origin.z == 0))) {
			return;
		}

		// It can be assumed safely that a region's coordinate and the
		// key in the regions map are the same object.
		for(Iterator iter = regions().keySet().iterator(); iter.hasNext();) {
			Coordinate coord = (Coordinate) iter.next();

			if(coord.z == origin.z) {
				coord.x -= origin.x;
				coord.y -= origin.y;
			}
		}

		// since the coordinate is the hash key, the modified
		// coordinates produce invalid hash codes in all maps
		// so everything has to be rehashed. Unfortunately, the
		// regions map has to be copied two times.
		Map r = CollectionFactory.createOrderedHashtable(regions());
		regions().clear();
		regions().putAll(r);

		for(Iterator iter = islands().values().iterator(); iter.hasNext();) {
			Island i = (Island) iter.next();
			i.invalidateRegions();
		}

		// now we must change the messages because they use string representations
		// of coordinates
		// all factions
		for(Iterator iter = factions().values().iterator(); iter.hasNext();) {
			Faction f = (Faction) iter.next();

			// all messages
			if(f.messages != null) {
				for(Iterator msgIter = f.messages.iterator(); msgIter.hasNext();) {
					Message msg = (Message) msgIter.next();

					if(msg.attributes != null) {
						for(Iterator attrIter = msg.attributes.keySet().iterator();
								attrIter.hasNext();) {
							Object key = attrIter.next();
							String strCoord = (String) msg.attributes.get(key);
							Coordinate coord = Coordinate.parse(strCoord, ",");

							if((coord != null) && (coord.z == origin.z)) {
								coord.x -= origin.x;
								coord.y -= origin.y;
								msg.attributes.put(key, coord.toString(","));
							} else {
								coord = Coordinate.parse(strCoord, " ");

								if((coord != null) && (coord.z == origin.z)) {
									coord.x -= origin.x;
									coord.y -= origin.y;
									msg.attributes.put(key, coord.toString(" ", true));
								}
							}
						}
					}
				}
			}

			// change battle IDs
			if(f.battles != null) {
				for(Iterator battles = f.battles.iterator(); battles.hasNext();) {
					Battle b = (Battle) battles.next();

					// currently the coordinate can overwritten, it
					// does not serve as key in any map
					Coordinate newCoord = (Coordinate) b.getID();

					// we dont need to copy the coordinate as they are mutable
					newCoord.x -= origin.x;
					newCoord.y -= origin.y;
				}
			}
		}
	}

	/**
	 * Sets the valid locale for this report. Currently, this is only used to remember this setting
	 * and write it back into the cr.
	 */
	public abstract void setLocale(Locale l);

	/**
	 * Returns the locale of this report. Currently, this is only used to remember this setting and
	 * write it back into the cr.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract Locale getLocale();

	/**
	 * Merges the specified dataset with this dataset.
	 *
	 * @param gd1 the first game data object for merging
	 * @param gd2 the second game data object for merging
	 *
	 * @return the new merged game data object
	 *
	 * @throws IllegalArgumentException if first and second game data
	 * object are from different game types.
	 */
	public static GameData merge(GameData gd1, GameData gd2) {
		// make sure, the game types are the same.
		if(!gd1.name.equalsIgnoreCase(gd2.name)) {
			throw new IllegalArgumentException("GameData.merge(): Can't merge different game types. (" +
											   gd1.name + " via " + gd2.name + ")");
		}

		// make sure that a date object is available
		if(gd1.getDate() == null) {
			gd1.setDate(new EresseaDate(0));
		}

		if(gd2.getDate() == null) {
			gd2.setDate(new EresseaDate(0));
		}

		if(gd1.getDate().compareTo(gd2.getDate()) > 0) {
			return mergeIt(gd2, gd1);
		} else {
			return mergeIt(gd1, gd2);
		}
	}

	/**
	 * Merges the two game data containers yielding a third one. By convention, gd1 must not be
	 * newer than gd2. The resulting game data container inherits the rules and name from
	 * <b>gd2</b>.
	 */
	private static GameData mergeIt(GameData gd1, GameData gd2) {
		// 2002.02.20 pavkovic: the newer rules are in GameData gd2. So we take
		// them for the new GameData
		// FIXME(pavkovic) rules should be loaded instead of just used in this situation
		GameData newGD = new CompleteData(gd2.rules, gd2.name);

		// DATE
		EresseaDate date = new EresseaDate(gd2.getDate().getDate());
		date.setEpoch(((EresseaDate) gd2.getDate()).getEpoch());
		newGD.setDate(date);

		boolean sameRound = gd1.getDate().equals(gd2.getDate());

		// MAIL TO, MAIL SUBJECT
		if(gd2.mailTo != null) {
			newGD.mailTo = gd2.mailTo;
		} else {
			newGD.mailTo = gd1.mailTo;
		}

		if(gd2.mailSubject != null) {
			newGD.mailSubject = gd2.mailSubject;
		} else {
			newGD.mailSubject = gd1.mailSubject;
		}

		// BASE
		if(gd2.base != 0) {
			newGD.base = gd2.base;
		} else {
			newGD.base = gd1.base;
		}

		// NOSKILLPOINTS: the newer report determines the skill point handling
		newGD.noSkillPoints = gd2.noSkillPoints;

		// curTempID
		// (it is assured, that at least on of the GameData-objects
		//  contains a default value for curTempID)
		if(gd2.curTempID != -1) {
			newGD.curTempID = gd2.curTempID;
		} else {
			newGD.curTempID = gd1.curTempID;
		}

		// LOCALE
		if(gd2.getLocale() != null) {
			newGD.setLocale(gd2.getLocale());
		} else {
			newGD.setLocale(gd1.getLocale());
		}

		// MESSAGETYPES
		// simple objects, created and merged in one step
		if(gd1.msgTypes() != null) {
		for(Iterator iter = gd1.msgTypes().values().iterator(); iter.hasNext();) {
			MessageType mt = (MessageType) iter.next();
			MessageType newMT = null;

			try {
				newMT = new MessageType((ID) mt.getID().clone());
			} catch(CloneNotSupportedException e) {
				log.error(e);
			}

			MessageType.merge(gd1, mt, newGD, newMT);
			newGD.addMsgType(newMT);
		}
		}

		if(gd2.msgTypes() != null) {
		for(Iterator iter = gd2.msgTypes().values().iterator(); iter.hasNext();) {
			MessageType mt = (MessageType) iter.next();
			MessageType newMT = newGD.getMsgType(mt.getID());

			if(newMT == null) {
				try {
					newMT = new MessageType((ID) mt.getID().clone());
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}

			MessageType.merge(gd2, mt, newGD, newMT);
			newGD.addMsgType(newMT);
		}
		}

		// SPELLS
		// simple objects, created and merged in one step
		if(gd1.spells() != null) {
		for(Iterator iter = gd1.spells().values().iterator(); iter.hasNext();) {
			Spell spell = (Spell) iter.next();
			Spell newSpell = null;

			try {
				newSpell = new Spell((ID) spell.getID().clone());
			} catch(CloneNotSupportedException e) {
				log.error(e);
			}

			Spell.merge(gd1, spell, newGD, newSpell);
			newGD.addSpell(newSpell);
		}
		}

		if(gd2.spells() != null) {
		for(Iterator iter = gd2.spells().values().iterator(); iter.hasNext();) {
			Spell spell = (Spell) iter.next();
			Spell newSpell = newGD.getSpell(spell.getID());

			if(newSpell == null) {
				try {
					newSpell = new Spell((ID) spell.getID().clone());
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}

			Spell.merge(gd2, spell, newGD, newSpell);
			newGD.addSpell(newSpell);
		}
		}

		// POTIONS
		// simple objects, created and merged in one step
		if(gd1.potions() != null) {
		for(Iterator iter = gd1.potions().values().iterator(); iter.hasNext();) {
			Potion potion = (Potion) iter.next();
			Potion newPotion = null;

			try {
				newPotion = new Potion((ID) potion.getID().clone());
			} catch(CloneNotSupportedException e) {
				log.error(e);
			}

			Potion.merge(gd1, potion, newGD, newPotion);
			newGD.addPotion(newPotion);
		}
		}

		if(gd2.potions() != null) {
		for(Iterator iter = gd2.potions().values().iterator(); iter.hasNext();) {
			Potion potion = (Potion) iter.next();
			Potion newPotion = newGD.getPotion(potion.getID());

			if(newPotion == null) {
				try {
					newPotion = new Potion((ID) potion.getID().clone());
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}

			Potion.merge(gd2, potion, newGD, newPotion);
			newGD.addPotion(newPotion);
		}
		}

		// TRANSLATIONS
		// simple objects, created and merged in one step
		if(newGD.translations() != null) {
			if(gd1.translations() != null) {
				newGD.translations().putAll(gd1.translations());
			}

			if((gd2.translations() != null) && gd1.getLocale().equals(gd2.getLocale())) {
				newGD.translations().putAll(gd2.translations());
			}
		}

		// FACTIONS
		if(gd1.factions() != null) {
			for(Iterator iter = gd1.factions().values().iterator(); iter.hasNext();) {
				Faction f = (Faction) iter.next();

				try {
					newGD.addFaction(new Faction((ID) f.getID().clone(), newGD));
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}

		if(gd2.factions() != null) {
			for(Iterator iter = gd2.factions().values().iterator(); iter.hasNext();) {
				Faction f = (Faction) iter.next();

				if(newGD.getFaction(f.getID()) == null) {
					try {
						newGD.addFaction(new Faction((ID) f.getID().clone(), newGD));
					} catch(CloneNotSupportedException e) {
						log.error(e);
					}
				}
			}
		}

		// REGIONS
		if(gd1.regions() != null) {
			for(Iterator iter = gd1.regions().values().iterator(); iter.hasNext();) {
				Region r = (Region) iter.next();

				try {
					newGD.addRegion(new Region((ID) r.getID().clone(), newGD));
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}

		if(gd2.regions() != null) {
			for(Iterator iter = gd2.regions().values().iterator(); iter.hasNext();) {
				Region r = (Region) iter.next();

				if(newGD.getRegion(r.getID()) == null) {
					try {
						newGD.addRegion(new Region((ID) r.getID().clone(), newGD));
					} catch(CloneNotSupportedException e) {
						log.error(e);
					}
				}
			}
		}

		// ISLANDS
		if(gd1.islands() != null) {
			for(Iterator iter = gd1.islands().values().iterator(); iter.hasNext();) {
				Island i = (Island) iter.next();

				try {
					newGD.addIsland(new Island((ID) i.getID().clone(), newGD));
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}

		if(gd2.islands() != null) {
			for(Iterator iter = gd2.islands().values().iterator(); iter.hasNext();) {
				Island i = (Island) iter.next();

				if(gd1.getIsland(i.getID()) == null) {
					try {
						newGD.addIsland(new Island((ID) i.getID().clone(), newGD));
					} catch(CloneNotSupportedException e) {
						log.error(e);
					}
				}
			}
		}

		// HOTSPOTS
		if(gd1.hotSpots() != null) {
			for(Iterator iter = gd1.hotSpots().values().iterator(); iter.hasNext();) {
				HotSpot h = (HotSpot) iter.next();

				try {
					newGD.setHotSpot(new HotSpot((ID) h.getID().clone()));
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}

		if(gd2.hotSpots() != null) {
			for(Iterator iter = gd2.hotSpots().values().iterator(); iter.hasNext();) {
				HotSpot h = (HotSpot) iter.next();

				if(newGD.getHotSpot(h.getID()) == null) {
					try {
						newGD.setHotSpot(new HotSpot((ID) h.getID().clone()));
					} catch(CloneNotSupportedException e) {
						log.error(e);
					}
				}
			}
		}

		// BUILDINGS
		if(gd2.buildings() != null) {
			for(Iterator iter = gd2.buildings().values().iterator(); iter.hasNext();) {
				Building b = (Building) iter.next();

				try {
					newGD.addBuilding(new Building((ID) b.getID().clone(), newGD));
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}

		if(gd1.buildings() != null) {
			// buildings are persistent.
			// Accept old buildings not occuring in the new report
			// only if there are no units in that region
			for(Iterator iter = gd1.buildings().values().iterator(); iter.hasNext();) {
				Building b = (Building) iter.next();
				Building curBuilding = gd2.getBuilding(b.getID());

				if(curBuilding == null) {
					// check if the building disappeared because we do
					// not know the region anymore or if it was
					// destroyed
					// FIXME(pavkovic): shouldn't it be Region curRegion = b.getRegion(); ?
					Region curRegion = gd2.getRegion(b.getRegion().getID());

					if((curRegion == null) || curRegion.units().isEmpty()) {
						try {
							newGD.addBuilding(new Building((ID) b.getID().clone(), newGD));
						} catch(CloneNotSupportedException e) {
							log.error(e);
						}
					} else {
						// we just don't see this region anymore so
						// keep the building
					}
				} else {
					// the building occurs in gd2 so we already
					// included its current version in newGD
				}
			}
		}

		// SHIPS
		if(sameRound && (gd1.ships() != null)) {
			for(Iterator iter = gd1.ships().values().iterator(); iter.hasNext();) {
				Ship s = (Ship) iter.next();

				try {
					newGD.addShip(new Ship((ID) s.getID().clone(), newGD));
				} catch(CloneNotSupportedException e) {
					log.error(e);
				}
			}
		}

		if(gd2.ships() != null) {
			for(Iterator iter = gd2.ships().values().iterator(); iter.hasNext();) {
				Ship s = (Ship) iter.next();

				if(newGD.getShip(s.getID()) == null) {
					try {
						newGD.addShip(new Ship((ID) s.getID().clone(), newGD));
					} catch(CloneNotSupportedException e) {
						log.error(e);
					}
				}
			}
		}

		// UNITS

		/* Note: To gather the information needed for level changes, report one
		 *       is always treated. But in the case of unequal dates only units
		 *       that are also in the second report are added to the new one and
		 *       temp units are ignored. IDs are used for comparism.
		*/
		Map parentUnits1 = CollectionFactory.createHashtable();

		if(gd1.units() != null) {
			for(Iterator iter = gd1.units().values().iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();

				if(sameRound || (gd2.getUnit(u.getID()) != null)) {
					try {
						newGD.addUnit(new Unit((ID) u.getID().clone()));
					} catch(CloneNotSupportedException e) {
						log.error(e);
					}
				}

				if(sameRound && !u.tempUnits().isEmpty()) {
					parentUnits1.put(u.getID(), u);
					u.setOrders(u.getCompleteOrders(), false);

					// temp units are not deleted, assume that the
					// old game data is thrown away anyway
					// FIXME(pavkovic): this is NOT the case if we
					// use export cr for exporting
				}
			}
		}

		Map parentUnits2 = CollectionFactory.createHashtable();

		if(gd2.units() != null) {
			for(Iterator iter = gd2.units().values().iterator(); iter.hasNext();) {
				Unit u = (Unit) iter.next();

				if(newGD.getUnit(u.getID()) == null) {
					try {
						newGD.addUnit(new Unit((ID) u.getID().clone()));
					} catch(CloneNotSupportedException e) {
						log.error(e);
					}
				}

				/*
				 * Ilja Pavkovic 2001.10.18: even if we don't add this Unit to the newGD,
				 * because it is already added from the GameData 01, the temp units have to
				 * be folded into the original unit (and extracted after the merge).
				 */
				if(!u.tempUnits().isEmpty()) {
					parentUnits2.put(u.getID(), u);
					u.setOrders(u.getCompleteOrders(), false);

					// temp units are not deleted, assume that the
					// old game data is thrown away anyway
				}
			}
		}

		// MERGE FACTIONS
		if(gd1.factions() != null) {
			for(Iterator iter = gd1.factions().values().iterator(); iter.hasNext();) {
				Faction curFaction = (Faction) iter.next();
				Faction newFaction = newGD.getFaction(curFaction.getID());
				// first pass
				Faction.merge(gd1, curFaction, newGD, newFaction);
			}
		}

		if(gd2.factions() != null) {
			for(Iterator iter = gd2.factions().values().iterator(); iter.hasNext();) {
				Faction curFaction = (Faction) iter.next();
				Faction newFaction = newGD.getFaction(curFaction.getID());
				// second pass
				Faction.merge(gd2, curFaction, newGD, newFaction);
			}
		}

		// MERGE REGIONS
		if(gd1.regions() != null) {
			for(Iterator iter = gd1.regions().values().iterator(); iter.hasNext();) {
				Region curRegion = (Region) iter.next();
				Region newRegion = newGD.getRegion(curRegion.getID());
				// first pass
				Region.merge(gd1, curRegion, newGD, newRegion, sameRound);
			}
		}

		if(gd2.regions() != null) {
			for(Iterator iter = gd2.regions().values().iterator(); iter.hasNext();) {
				Region curRegion = (Region) iter.next();
				Region newRegion = newGD.getRegion(curRegion.getID());
				// second pass
				Region.merge(gd2, curRegion, newGD, newRegion, true);
			}
		}

		// MERGE ISLANDS
		if(gd1.islands() != null) {
			for(Iterator iter = gd1.islands().values().iterator(); iter.hasNext();) {
				Island curIsland = (Island) iter.next();
				Island newIsland = newGD.getIsland(curIsland.getID());
				// first pass
				Island.merge(gd1, curIsland, newGD, newIsland);
			}
		}

		if(gd2.islands() != null) {
			for(Iterator iter = gd2.islands().values().iterator(); iter.hasNext();) {
				Island curIsland = (Island) iter.next();
				Island newIsland = newGD.getIsland(curIsland.getID());
				// second pass
				Island.merge(gd2, curIsland, newGD, newIsland);
			}
		}

		// MERGE HOTSPOTS
		if(gd1.hotSpots() != null) {
			for(Iterator iter = gd1.hotSpots().values().iterator(); iter.hasNext();) {
				HotSpot curHotSpot = (HotSpot) iter.next();
				HotSpot newHotSpot = newGD.getHotSpot(curHotSpot.getID());
				HotSpot.merge(gd1, curHotSpot, newGD, newHotSpot);
			}
		}

		if(gd2.hotSpots() != null) {
			for(Iterator iter = gd2.hotSpots().values().iterator(); iter.hasNext();) {
				HotSpot curHotSpot = (HotSpot) iter.next();
				HotSpot newHotSpot = newGD.getHotSpot(curHotSpot.getID());
				HotSpot.merge(gd2, curHotSpot, newGD, newHotSpot);
			}
		}

		// MERGE BUILDINGS
		if(gd1.buildings() != null) {
			for(Iterator iter = gd1.buildings().values().iterator(); iter.hasNext();) {
				Building curBuilding = (Building) iter.next();
				Building newBuilding = newGD.getBuilding(curBuilding.getID());

				if(newBuilding != null) {
					Building.merge(gd1, curBuilding, newGD, newBuilding);
				}
			}
		}

		if(gd2.buildings() != null) {
			for(Iterator iter = gd2.buildings().values().iterator(); iter.hasNext();) {
				Building curBuilding = (Building) iter.next();
				Building newBuilding = newGD.getBuilding(curBuilding.getID());

				if(newBuilding != null) {
					Building.merge(gd2, curBuilding, newGD, newBuilding);
				}
			}
		}

		// MERGE SHIPS
		// only merge ships from the "older" game data if they are from the same round
		if(sameRound && (gd1.ships() != null)) {
			for(Iterator iter = gd1.ships().values().iterator(); iter.hasNext();) {
				Ship curShip = (Ship) iter.next();
				Ship newShip = newGD.getShip(curShip.getID());

				// first pass
				Ship.merge(gd1, curShip, newGD, newShip);
			}
		}

		if(gd2.ships() != null) {
			for(Iterator iter = gd2.ships().values().iterator(); iter.hasNext();) {
				Ship curShip = (Ship) iter.next();
				Ship newShip = newGD.getShip(curShip.getID());

				// second pass
				Ship.merge(gd2, curShip, newGD, newShip);
			}
		}

		// MERGE UNITS

		/* Note: To gather level change informations all units are used.
		 *       If the dates are equal, a fully merge is done, if not, only the
		 *       skills are retrieved.
		 */
		Iterator it = newGD.units().values().iterator();

		while(it.hasNext()) {
			Unit newUnit = (Unit) it.next();

			// find the second first since we may need the temp id
			Unit curUnit2 = gd2.findUnit(newUnit.getID(), null, null);

			// find a temp ID to gather information out of the temp unit
			ID tempID = null;
			Region newRegion = null;

			if((curUnit2 != null) && !sameRound) { // only use temp ID if reports have different date
				tempID = curUnit2.getTempID();

				if(tempID != null) {
					tempID = new UnitID(-((UnitID) tempID).intValue());
				}

				newRegion = curUnit2.getRegion();
			}

			Unit curUnit1 = gd1.findUnit(newUnit.getID(), tempID, newRegion); // now get the unit of the first report

			// first merge step
			if(curUnit1 != null) {
				if(sameRound) { // full merge
					Unit.merge(gd1, curUnit1, newGD, newUnit);
				} else { // only copy the skills to get change-level base

					if((curUnit2.skills != null) || (curUnit1.getFaction().isPrivileged())) {
						Unit.copySkills(curUnit1, newUnit);
					}
				}
			}

			// second merge step
			if(curUnit2 != null) {
				Unit.merge(gd2, curUnit2, newGD, newUnit);
			}
		}

		// TEMP UNITS
		for(Iterator iter = parentUnits1.values().iterator(); iter.hasNext();) {
			Unit curParent = (Unit) iter.next();
			Unit newParent = newGD.getUnit(curParent.getID());

			if(newParent != null) {
				newParent.extractTempUnits(0);
			} else {
				log.warn("Parent unit " + curParent + " not found");
			}
		}

		for(Iterator iter = parentUnits2.values().iterator(); iter.hasNext();) {
			Unit curParent = (Unit) iter.next();
			Unit newParent = newGD.getUnit(curParent.getID());

			if(newParent != null) {
				newParent.extractTempUnits(0);
			} else {
				log.warn("Parent unit " + curParent + " not found");
			}
		}

		newGD.postProcess();
		newGD.resetToUnchanged();

		return newGD;
	}

	protected Unit findUnit(ID id, ID tempID, Region newRegion) {
		// search for a temp unit
		if(tempID != null) {
			if(newRegion == null) {
				Iterator it = units().values().iterator();

				while(it.hasNext()) {
					Unit u = (Unit) it.next();
					Unit u2 = u.getTempUnit(tempID);

					if(u2 != null) {
						return u2;
					}
				}
			} else {
				Map m = Regions.getAllNeighbours(regions(), newRegion.getID(), 3, null);

				if(m != null) {
					Iterator it = m.values().iterator();

					while(it.hasNext()) {
						Region r = (Region) it.next();
						Unit u2 = r.getUnit(tempID);

						if(u2 != null) {
							return u2;
						}
					}
				}
			}
		}

		// standard search
		return getUnit(id);
	}

	/**
	 * This function checks if the game data have been manipulated somehow (merge will lead to a
	 * filetype null).
	 *
	 * @param g TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean gameDataChanged(GameData g) {
		if(g.filetype == null) {
			return true;
		}

		for(Iterator iter = g.units().values().iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();

			if(u.ordersHaveChanged()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * reset change state of all units to false
	 */
	public void resetToUnchanged() {
		for(Iterator iter = units().values().iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			u.setOrdersChanged(false);
		}
	}

	/**
	 * returns a clone of the game data (using CRWriter/CRParser  trick encapsulated in Loader)
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws CloneNotSupportedException TODO: DOCUMENT ME!
	 */
	public Object clone() throws CloneNotSupportedException {
		return new Loader().cloneGameData(this);
	}

	/**
	 * Provides the encapsulating of game specific stuff
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public GameSpecificStuff getGameSpecificStuff() {
		return rules.getGameSpecificStuff();
	}

	/** Post processes the game data (if necessary)  once */
	private boolean postProcessed = false;

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void postProcess() {
		if(postProcessed) {
			return;
		}

		// enforce locale to be non-null
		postProcessLocale();

		// attach Regions to Islands
		Island.postProcess(this);

		// remove double messages
		postProcessMessages();

		getGameSpecificStuff().postProcess(this);
		postProcessed = true;
	}

	/**
	 * Adds the order locale of Magellan if locale is null.
	 * This should prevent some NPE with the sideeffect to
	 * store a locale in a locale-less game data object.
	 */
	private void postProcessLocale() {
		if(getLocale() == null) {
			setLocale(Locales.getOrderLocale());
		}
	}

	/**
	 * This function post processes the message blocks to remove duplicate messages. In former
	 * times this has been done while loading the game data but  this had a negative time tradeoff
	 * (O(n^2)). This functions needs about O(n log n).
	 */
	private void postProcessMessages() {
		// faction.messages
		for(Iterator iter = factions().values().iterator(); iter.hasNext();) {
			Faction o = (Faction) iter.next();
			postProcessMessages(o.messages);
		}

		// region.messages
		for(Iterator iter = regions().values().iterator(); iter.hasNext();) {
			Region o = (Region) iter.next();
			postProcessMessages(o.messages);
		}
	}

	/**
	 * Postprocess a given list of messages. To remove duplicate messages  we put all messages in
	 * an ordered hashtable and put them back into the messages collection.
	 *
	 * @param messages TODO: DOCUMENT ME!
	 */
	private void postProcessMessages(Collection messages) {
		if(messages == null) {
			return;
		}

		Map ht = CollectionFactory.createOrderedHashtable();

		for(Iterator iter = messages.iterator(); iter.hasNext();) {
			Message msg = (Message) iter.next();

			if(ht.put(msg, msg) != null) {
				// log.warn("Duplicate message \"" + msg.getText() + "\" found, removing it.");
			}
		}

		messages.clear();
		messages.addAll(ht.values());
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void postProcessAfterTrustlevelChange() {
		getGameSpecificStuff().postProcessAfterTrustlevelChange(this);
	}
}
