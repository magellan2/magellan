// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.cr;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.eressea.Alliance;
import com.eressea.Battle;
import com.eressea.Border;
import com.eressea.Building;
import com.eressea.CombatSpell;
import com.eressea.Coordinate;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Group;
import com.eressea.HotSpot;
import com.eressea.ID;
import com.eressea.IntegerID;
import com.eressea.Island;
import com.eressea.Item;
import com.eressea.LongID;
import com.eressea.LuxuryPrice;
import com.eressea.Message;
import com.eressea.Potion;
import com.eressea.Region;
import com.eressea.RegionResource;
import com.eressea.Rules;
import com.eressea.Scheme;
import com.eressea.Ship;
import com.eressea.Skill;
import com.eressea.Spell;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.UnitID;
import com.eressea.io.RulesIO;
import com.eressea.rules.AllianceCategory;
import com.eressea.rules.BuildingType;
import com.eressea.rules.CastleType;
import com.eressea.rules.EresseaDate;
import com.eressea.rules.GenericRules;
import com.eressea.rules.Herb;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.MessageType;
import com.eressea.rules.OptionCategory;
import com.eressea.rules.Options;
import com.eressea.rules.Race;
import com.eressea.rules.RegionType;
import com.eressea.rules.Resource;
import com.eressea.rules.ShipType;
import com.eressea.rules.SkillCategory;
import com.eressea.rules.SkillType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.file.FileType;
import com.eressea.util.logging.Logger;

/**
 * Parser for cr-files.
 **/
public class CRParser implements RulesIO {
	private final static Logger log = Logger.getInstance(CRParser.class);

	Scanner  sc;
	GameData world;
	String   configuration;
	String   coordinates;
	String   game;
	boolean  umlauts;
	int	  version = 0;		// the version of the report
	/**
	 * Print an error message on the standard output channel.
	 *
	 * @param context The context (usually a block) within the error
	 * has been found.
	 *
	 * @param fetch If this is true, read the next line and skip the
	 * line with the error. Otherwise the line stays still at the
	 * front of the input.
	 **/
	private void unknown(String context, boolean fetch) throws IOException {
		int i;
		log.warn("unknown in line " + sc.lnr + ": (" +
				 context + ")");
		StringBuffer msg = new StringBuffer();
		for (i = 0; i < sc.argc; i++) {
			if (sc.isString[i]) {
				msg.append("\"");
			}
			msg.append(sc.argv[i]);
			if (sc.isString[i]) {
				msg.append("\"");
			}
			if (i + 1 < sc.argc) {
				msg.append(";");
			}
		}
		log.warn(msg);
		if (fetch)
			sc.getNextToken();
	}

	/*
	 * Helper function: Find a faction in world. If not found, create
	 * one and insert it.
	 */
	private Faction getAddFaction(GameData world, EntityID id) {
		Faction faction = world.getFaction(id);
		if (faction == null) {
			faction = new Faction(id, world);
			world.addFaction(faction);
		}

		return faction;
	}

	/*
	 * Helper function: Find a unit in world. If not found, create
	 * one and insert it.
	 */
	private Unit getAddUnit(GameData world, UnitID id) {
		Unit unit = world.getUnit(id);
		if (unit == null) {
			unit = new Unit(id);
			world.addUnit(unit);
		}
		return unit;
	}

	/*
	 * Helper function: Find a building in world. If not found, create
	 * one and insert it.
	 */
	private Building getAddBuilding(GameData world, EntityID id) {
		Building building = world.getBuilding(id);
		if (building == null) {
			building = new Building(id, world);
			world.addBuilding(building);
		}
		return building;
	}

	/*
	 * Helper function: Find a ship in world. If not found, create
	 * one and insert it.
	 */
	private Ship getAddShip(GameData world, EntityID id) {
		Ship ship = world.getShip(id);
		if (ship == null) {
			ship = new Ship(id, world);
			world.addShip(ship);
		}

		return ship;
	}

	/**
	 * Read the MESSAGETYPES block.
	 * Note that message type stubs have already been created by
	 * parsing the messages themselves.
	 *
	 * @returns the resulting list of <tt>MessageType</tt> objects.
	 **/
	private List parseMessageTypes(GameData data) throws IOException {
		List list = CollectionFactory.createLinkedList();
		sc.getNextToken();	  // skip the block
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2) {
				try {
					MessageType mt = data.getMsgType(IntegerID.create(sc.argv[1]));
					if (mt == null) {
						mt = new MessageType(IntegerID.create(sc.argv[1]), sc.argv[0]);
						data.addMsgType(mt);
					} else {
						mt.setPattern(sc.argv[0]);
					}
				}catch (NumberFormatException e) {
					log.error(e);
				}
			}
			sc.getNextToken();
		}

		return list;
	}

	/**
	 * Read a MESSAGETYPE block.
	 * Note that message type stubs have already been created by
	 * parsing the messages themselves.
	 **/
	private void parseMessageType(GameData data) throws IOException {
		ID id = IntegerID.create(sc.argv[0].substring(12).trim());
		MessageType mt = data.getMsgType(id);
		if (mt == null) {
			mt = new MessageType(id);
			data.addMsgType(mt);
		}
		sc.getNextToken();	  // skip the block
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("text")) {
				mt.setPattern(sc.argv[0]);
				sc.getNextToken();
			} else
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("section")) {
				mt.setSection(sc.argv[0]);
				sc.getNextToken();
			} else {
				unknown("MESSAGETYPE", true);
			}
		}
	}

	/**
	 * Handle a sequence of quoted strings, interpreting them as messages.
	 *
	 * @param msgs a list to add the read messages to
	 * @returns the resulting list of <tt>Message</tt> objects.
	 **/
	private List parseMessageSequence(List msgs) throws IOException {
		sc.getNextToken();	  // skip the block
		while (!sc.eof && sc.argc == 1 && sc.isString[0]) {
			if (msgs == null) {
				msgs = CollectionFactory.createLinkedList();
			}
			// 2002.04.24 pavkovic: remove duplicate entries
			Message msg = new Message(new String(sc.argv[0]));
			if(msgs.contains(msg)) {
				log.warn("Duplicate message \""+msg.getText()+"\" found, removing it.");
				if(log.isDebugEnabled()) {
					log.debug("List: "+msgs);
					log.debug("new entry:"+msg);
				}
			} else {
				msgs.add(msg);
			}
			sc.getNextToken();
		}

		return msgs;
	}

	/**
	 * Handle a sequence of quoted strings, storing them as
	 * <tt>String</tt> objects. String interpretation starts
	 * with the next line.
	 *
	 * @param strings a list to add the read strings to.
	 * @returns the resulting list of <tt>String</tt> objects.
	 **/
	private List parseStringSequence(List strings) throws IOException {
		sc.getNextToken();	  // skip the block
		while (!sc.eof && sc.argc == 1 && sc.isString[0]) {
			if (strings == null) {
				strings = CollectionFactory.createLinkedList();
			}
			strings.add(new String(sc.argv[0]));
			sc.getNextToken();
		}

		// avoid unnecessary list allocations
		if ((strings != null) && (strings.size() == 0)) {
			strings = null;
		}

		return strings;
	}

	/**
	 * Parse the SPRUECHE sub block of UNIT and add them as <tt>Spell</tt>
	 * objects.
	 *
	 * @param world the game data to get the spells from
	 * @param map a map to add the read spells to
	 * @returns the resulting map of <tt>Spell</tt> objects.
	 **/
	private Map parseUnitSpells(GameData world, Map map) throws IOException {
		sc.getNextToken();	  // skip the block
		while (!sc.eof && !sc.isBlock) {
			ID id = StringID.create(sc.argv[0]);
			Spell s = world.getSpell(id);
			if (s == null) {
				s = new Spell(id);
				s.setName(sc.argv[0]);
				world.addSpell(s);
			}
			if (map == null) {
				map = CollectionFactory.createOrderedHashtable();
			}
			map.put(s.getID(), s);
			sc.getNextToken();
		}

		return map;
	}

	/**
	 * Parse a KAMPFZAUBER sub block of UNIT and add it as <tt>CombatSpell</tt>
	 * object.
	 *
	 * @param world the game data to get the spells from
	 * @param unit the unit that should get the combat spells set
	 */
	private void parseUnitCombatSpells(GameData world, Unit unit) throws IOException {
		ID id = IntegerID.create(sc.argv[0].substring(12).trim());
		CombatSpell s = new CombatSpell(id);
		s.setUnit(unit);
		if (unit.combatSpells == null) {
			unit.combatSpells = CollectionFactory.createHashtable();
		}
		unit.combatSpells.put(s.getID(), s);
		sc.getNextToken();
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("name")) {
				ID spellID = StringID.create(sc.argv[0]);
				Spell spell = world.getSpell(spellID);
				if (spell == null) {
					log.warn("CRParser.parseUnitCombatSpells(): a combat spell refers to an unknown spell (line " + sc.lnr + ")");
					spell = new Spell(spellID);
					spell.setName(sc.argv[0]);
					world.addSpell(spell);
				}
				s.setSpell(spell);
			} else
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("level")) {
				s.setCastingLevel(Integer.parseInt(sc.argv[0]));
			} else {
				unknown("KAMPFZAUBER", false);
			}
			sc.getNextToken();
		}
	}

	/**
	 * Parse message blocks as can be found in cr versions >= 41.
	 * This function evaluates only two special message attributes.
	 * These are the ";type" and ";rendered" attributes, which are
	 * directly accessible in the <tt>Message</tt> object as type
	 * and text.
	 * If there is no MessageType object for this type of message,
	 * a stub MessageType object is created and added to world.
	 *
	 * @returns a list containing <tt>Message</tt> objects for all
	 * messages read.
	 */
	private List parseMessages(GameData world, List list) throws IOException {
		return parseMessages(world,list,true);
	}

	private List parseMessages(GameData world, List list,boolean removeDouble) throws IOException {
		while (sc.isBlock && sc.argv[0].startsWith("MESSAGE ")) {
			ID id = IntegerID.create(sc.argv[0].substring(8));
			Message msg = new Message(id);
			// read message attributes
			sc.getNextToken();	// skip MESSAGE xx
			while (!sc.eof && !sc.isBlock) {
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("type")) {
					ID typeID = IntegerID.create(sc.argv[0]);
					MessageType mt = world.getMsgType(typeID);
					if (mt == null) {
						mt = new MessageType(typeID);
						world.addMsgType(mt);
					}
					msg.setType(mt);
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("rendered")) {
					msg.setText(sc.argv[0]);
				} else
				if (sc.argc == 2) {
					if (msg.attributes == null) {
						msg.attributes = CollectionFactory.createOrderedHashtable();
					}
					msg.attributes.put(sc.argv[1], sc.argv[0]);
				}
				sc.getNextToken();
			}

			if (list == null) {
				list = CollectionFactory.createLinkedList();
			}
			// 2002.04.24 pavkovic: remove duplicate entries
			if(removeDouble && list.contains(msg)) {
				log.warn("Duplicate message \""+msg.getText()+"\" found, removing it.");
				if(log.isDebugEnabled()) {
					log.debug("List: "+list);
					log.debug("new entry:"+msg);
				}
			} else {
				list.add(msg);
			}
		}

		return list;
	}

	/**
	 * Parse a battle block sequence. Currently this is a block of message blocks.
	 *
	 * @returns A List of instances of class Battle.
	 **/
	private List parseBattles(GameData world, List list) throws IOException {
		while (!sc.eof && sc.argv[0].startsWith("BATTLE ")) {
			ID c = Coordinate.parse(sc.argv[0].substring(sc.argv[0].indexOf(" ", 0)), " ");
			if (c == null) {
				unknown("BATTLE", true);
				continue;
			}

			Battle battle = new Battle(c);
			if (list == null) {
				list = CollectionFactory.createLinkedList();
			}
			list.add(battle);
			sc.getNextToken();  // skip BATTLE x y
			parseMessages(world, battle.messages(),false);
		}

		return list;
	}

	/**
	 * Parse a battlespec block sequence. Currently this is a block of message blocks.
	 *
	 * @returns A List of instances of class Battle.
	 **/
	private List parseBattleSpecs(GameData world, List list) throws IOException {
		while (!sc.eof && sc.argv[0].startsWith("BATTLESPEC ")) {
			ID c = Coordinate.parse(sc.argv[0].substring(sc.argv[0].indexOf(" ", 0)), " ");
			if (c == null) {
				unknown("BATTLESPEC", true);
				continue;
			}

			Battle battle = new Battle(c, true);
			if (list == null) {
				list = CollectionFactory.createLinkedList();
			}
			list.add(battle);
			sc.getNextToken();  // skip BATTLE x y
			parseMessages(world, battle.messages(),false);
		}

		return list;
	}

	/**
	 * Parse a sequence of spell blocks.
	 * Do not confuse this with the spells block of a unit!
	 */
	private void parseSpells(GameData world) throws IOException {
		while (!sc.eof && sc.isBlock && sc.argv[0].startsWith("ZAUBER ")) {
			ID id = IntegerID.create(sc.argv[0].substring(7).trim());
			// not adding spell immediately is required here, please do not change this, unless you really know, what you're doing!
			Spell spell = new Spell(id);
			spell.setBlockID(((IntegerID)id).intValue());
			sc.getNextToken();  // skip ZAUBER nr
			while (!sc.eof) {
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("name")) {
					spell.setName(sc.argv[0]);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("info")) {
					spell.setDescription(sc.argv[0]);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("level")) {
					spell.setLevel(Integer.parseInt(sc.argv[0]));
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("rank")) {
					spell.setRank(Integer.parseInt(sc.argv[0]));
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("class")) {
					spell.setType(sc.argv[0]);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("ship")) {
					spell.setOnShip(Integer.parseInt(sc.argv[0]) != 0);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("ocean")) {
					spell.setOnOcean(Integer.parseInt(sc.argv[0]) != 0);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("familiar")) {
					spell.setIsFamiliar(Integer.parseInt(sc.argv[0]) != 0);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("far")) {
					spell.setIsFar(Integer.parseInt(sc.argv[0]) != 0);
					sc.getNextToken();
				} else
				if (sc.isBlock && sc.argv[0].equals("KOMPONENTEN")) {
					Map map = CollectionFactory.createHashtable();
					sc.getNextToken(); // skip KOMPONENTEN
					while (!sc.eof && !sc.isBlock && sc.argc == 2) {
						map.put(sc.argv[1], sc.argv[0]);
						sc.getNextToken();
					}
					spell.setComponents(map);
				} else if (sc.isBlock) {
					break;
				} else {
					unknown("ZAUBER", true);
				}
			}

			if (spell.getName() != null) {
				// spell.setID(StringID.create(spell.getName()));
				world.addSpell(spell);
			}
		}
	}

	/**
	 * Parse a sequence of potion (TRANK) blocks.
	 */
	private void parsePotions(GameData world) throws IOException {
		while (!sc.eof && sc.isBlock && sc.argv[0].startsWith("TRANK ")) {
			ID id = IntegerID.create(sc.argv[0].substring(6));
			Potion potion = world.getPotion(id);
			if (potion == null) {
				potion = new Potion(id);
				world.addPotion(potion);
			}

			sc.getNextToken();  // skip TRANK nr
			while (!sc.eof) {
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("Name")) {
					potion.setName(sc.argv[0]);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("Stufe")) {
					potion.setLevel(Integer.parseInt(sc.argv[0]));
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("Beschr")) {
					potion.setDescription(sc.argv[0]);
					sc.getNextToken();
				} else
				if (sc.isBlock && sc.argv[0].equals("ZUTATEN")) {
					sc.getNextToken(); // skip ZUTATEN block
					while (!sc.eof && !sc.isBlock && sc.argc == 1) {
						ItemType it = world.rules.getItemType(StringID.create(sc.argv[0]), true);
						Item i = new Item(it, 1);
						potion.addIngredient(i);
						sc.getNextToken();
					}
				} else if (sc.isBlock) {
					break;
				} else {
					unknown("TRANK", true);
				}
			}
		}
	}

	/**
	 * Parse a sequence of island blocks.
	 */
	private void parseIslands(GameData world) throws IOException {
		while (!sc.eof && sc.isBlock && sc.argv[0].startsWith("ISLAND ")) {
			ID id = IntegerID.create(sc.argv[0].substring(7));
			Island island = world.getIsland(id);
			if (island == null) {
				island = new Island(id, world);
				world.addIsland(island);
			}

			sc.getNextToken();  // skip ISLAND nr
			while (!sc.eof && !sc.isBlock) {
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("name")) {
					island.setName(sc.argv[0]);
					sc.getNextToken();
				} else
				if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("Beschr")) {
					island.setDescription(sc.argv[0]);
					sc.getNextToken();
				} else {
					unknown("ISLAND", true);
				}
			}
		}
	}

	/** @author Rainer Klaffehn
	 * Read just the header of a CR. Since the header is expected to start
	 * with VERSION xx, we will also add a fake-tag named _version_ so this
	 * value can also be retrieved.
	 * <br>This function is synchronized.
	 * @param in The reader, that will read the file for us.
	 * @return a map, that maps all found header tags to their values.
	 */
	public synchronized Map readHeader(java.io.Reader in) throws java.io.IOException {
		Map map = CollectionFactory.createHashMap();
		sc = new com.eressea.cr.Scanner(in);
		sc.getNextToken();
		if (!sc.argv[0].startsWith("VERSION ")) {
			log.warn("CRParser.readHeader(): CR doesn't start with VERSION block.");
			return map;
		} else {
			try {
				map.put("_version_", new Integer(sc.argv[0].substring(sc.argv[0].indexOf(' ')).trim()));
			}
			catch (java.lang.Exception exc) {
				log.warn("CRParser.readHeader(): Failed to parse  VERSION number. (setting 0)");
				log.warn(exc.toString());
				map.put("_version_", new Integer(0));
			}
		}
		/* Now read as long as the file lasts, or until a new block is found,
		 * which will terminate the header. */
		sc.getNextToken();
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2) {
				map.put(sc.argv[1], sc.argv[0]);
			} else {
				log.warn("CRParser.readHeader(): Malformed tag on line " + sc.lnr);
			}
			sc.getNextToken();
		}
		return map;
	}

	/**
	 * Handle the header, i.e.:
	 * VERSION 37
	 * "Eressea";Spiel
	 * "Standard";Konfiguration
	 * "Hex";Koordinaten
	 * 36;Basis
	 * 1;Umlaute
	 **/
	private void parseHeader(GameData world) throws IOException {
		Region specialRegion = null;
		int factionSortIndex = 0;
		int regionSortIndex = 0;
		int blankPos = sc.argv[0].indexOf(' ');
		if (blankPos > 0) {
			version = Integer.parseInt(sc.argv[0].substring(blankPos).trim());
		} else {
			version = 0;
		}

		sc.getNextToken();	  // skip "VERSION xx"
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Spiel")) {
				game = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Konfiguration")) {
				configuration = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Koordinaten")) {
				coordinates = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Basis")) {
				try {
					world.base = java.lang.Integer.parseInt(sc.argv[0]);
				}
				catch (NumberFormatException e) {
					world.base = 0;
				}
				if (world.base <= 0 || world.base > 36) {
					world.base = 10;
				}
				com.eressea.util.IDBaseConverter.setBase(world.base);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Umlaute")) {
				umlauts = Integer.parseInt(sc.argv[0]) != 0;
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("curTempID")) {
				try {
					world.setCurTempID(Integer.parseInt(sc.argv[0]));
				} catch (java.lang.NumberFormatException nfe) {
					log.warn("Error: Illegal Number format in line " + sc.lnr + ": " + sc.argv[0]);
					log.warn("Setting the corresponding value GameData.curTempID to default value!");
					world.setCurTempID(-1);
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Runde") == true) {
				com.eressea.rules.Date d = world.getDate();
				if (d == null) {
					world.setDate(new EresseaDate(java.lang.Integer.parseInt(sc.argv[0])));
				} else {
					d.setDate(java.lang.Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Zeitalter") == true) {
				EresseaDate d = (EresseaDate)world.getDate();
				if (d == null) {
					d = new EresseaDate(0);
					d.setEpoch(java.lang.Integer.parseInt(sc.argv[0]));
					world.setDate(d);
				} else {
					d.setEpoch(java.lang.Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("locale") == true) {
				world.setLocale(new Locale(sc.argv[0], ""));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("date") == true) {
				// ignore date tag
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("noskillpoints")) {
				world.noSkillPoints = (Integer.parseInt(sc.argv[0]) != 0);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("mailcmd")) {
				world.mailSubject = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("mailto")) {
				world.mailTo = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 1 && sc.argv[0].startsWith("RULES")) {
				parseRules(world.rules);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("HOTSPOT ")) {
				parseHotSpot(world);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("PARTEI ")) {
				parseFaction(world, factionSortIndex++);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("ZAUBER ")) {
				parseSpells(world);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("TRANK ")) {
				parsePotions(world);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("ISLAND ")) {
				parseIslands(world);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("REGION ")) {
				parseRegion(world, regionSortIndex++);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("SPEZIALREGION ")) {
				specialRegion = parseSpecialRegion(world, specialRegion);
			} else if (sc.argc == 1 && sc.argv[0].equals("MESSAGETYPES")) {
				parseMessageTypes(world);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("MESSAGETYPE ")) {
				parseMessageType(world);
			} else if (sc.argc == 1 && sc.argv[0].equals("TRANSLATION")) {
				parseTranslation(world);
			} else {
				unknown("VERSION", true);
			}
		}
	}

	private void parseRace(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		String id = sc.argv[0].substring(f + 1, t);
		Race race = rules.getRace(StringID.create(id),true);
		race.setName(id);
		sc.getNextToken();	  // skip RACE xx
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("recruitmentcosts") == true) {
				race.setRecruitmentCosts(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name") == true) {
				race.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("weight") == true) {
				race.setWeight(java.lang.Float.parseFloat(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("capacity") == true) {
				race.setCapacity(java.lang.Float.parseFloat(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.isBlock &&
				sc.argv[0].equals("TALENTBONI")) {
				parseRaceSkillBonuses(race, rules);
			} else if (sc.isBlock &&
				sc.argv[0].startsWith("TALENTBONI ")) {
				parseRaceTerrainSkillBonuses(race, rules);
			} else if (sc.isBlock) {
				break;
			} else {
				unknown("RACE", true);
			}
		}
	}

	private void parseRaceSkillBonuses(Race race, Rules rules) throws IOException {
		sc.getNextToken();	  // skip TALENTBONI
		while (!sc.eof && !sc.isBlock) {
			try {
				SkillType skillType = rules.getSkillType(StringID.create(sc.argv[1]), true);
				race.setSkillBonus(skillType, Integer.parseInt(sc.argv[0]));
			} catch (NumberFormatException e) {
				log.warn("CRParser.parseRaceSkillBonuses(): in line " + sc.lnr + ": unable to convert skill bonus " + sc.argv[0] + " to an integer. Ignoring bonus for skill " + sc.argv[1]);
			}
			sc.getNextToken();
		}
	}

	private void parseRaceTerrainSkillBonuses(Race race, Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		String id = sc.argv[0].substring(f + 1, t);
		RegionType rType = rules.getRegionType(StringID.create(id), true);
		sc.getNextToken();	  // skip TALENTBONI
		while (!sc.eof && !sc.isBlock) {
			try {
				SkillType skillType = rules.getSkillType(StringID.create(sc.argv[1]), true);
				race.setSkillBonus(skillType, rType, Integer.parseInt(sc.argv[0]));
			} catch (NumberFormatException e) {
				log.warn("CRParser.parseRaceTerrainSkillBonuses(): in line " + sc.lnr + ": unable to convert skill bonus " + sc.argv[0] + " to an integer. Ignoring bonus for skill " + sc.argv[1]);
			}
			sc.getNextToken();
		}
	}

	private void parseItemType(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		String id = sc.argv[0].substring(f + 1, t);
		ItemType itemType = null;
		if (sc.argv[0].startsWith("ITEM ")) {
			itemType = rules.getItemType(StringID.create(id),true);
		} else if (sc.argv[0].startsWith("HERB ")) {
			itemType = rules.getHerb(StringID.create(id),true);
		}
		itemType.setName(id);
		Skill makeSkill = null;
		sc.getNextToken();	  // skip ITEM xx
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("weight") == true) {
				itemType.setWeight(java.lang.Float.parseFloat(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("makeskill") == true) {
				makeSkill = new Skill(rules.getSkillType(StringID.create(sc.argv[0]), true), 0, 0, 0, false);
				itemType.setMakeSkill(makeSkill);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("makeskilllevel") == true) {
				makeSkill.setLevel(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name") == true) {
				itemType.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("category") == true) {
				ID catID = StringID.create(sc.argv[0]);
				ItemCategory cat = rules.getItemCategory(catID, true);
				itemType.setCategory(cat);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("region") == true) {
				ID regionID = StringID.create(sc.argv[0]);
				RegionType rType = rules.getRegionType(regionID, true);
				((Herb)itemType).setRegionType(rType);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("iconname")) {
				itemType.setIconName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("useskill")) {
				Skill useSkill = new Skill(rules.getSkillType(StringID.create(sc.argv[0]), true), 0, 1, 0, false);
				itemType.setUseSkill(useSkill);
				sc.getNextToken();
			} else if (sc.isBlock && sc.argv[0].equals("RESOURCES")) {
				parseItemTypeResources(itemType, rules);
			} else if (sc.isBlock) {
				break;
			} else {
				unknown("ITEM", true);
			}
		}
	}

	private void parseItemTypeResources(ItemType itemType, Rules rules) throws IOException {
		sc.getNextToken();	// skips the block header
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2) {
				ItemType component = rules.getItemType(StringID.create(sc.argv[1]), true);
				Item i = new Item(component, Integer.parseInt(sc.argv[0]));
				itemType.addResource(i);
				sc.getNextToken();
			} else {
				unknown("RESOURCES", true);
			}
		}
	}

	private void parseSkillType(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		String id = sc.argv[0].substring(f + 1, t);
		SkillType skillType = rules.getSkillType(StringID.create(id),true);
		sc.getNextToken();	  // skip SKILL xx
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Name") == true) {
				skillType.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("category") == true) {
				ID catID = StringID.create(sc.argv[0]);
				SkillCategory cat = rules.getSkillCategory(catID, true);
				skillType.setCategory(cat);
				sc.getNextToken();
			} else {
				unknown("SKILL", true);
			}
		}

		if (skillType.getName() == null) {
			skillType.setName(id);
		}
	}

	private void parseShipType(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		String id = sc.argv[0].substring(f + 1, t);
		ShipType shipType = rules.getShipType(StringID.create(id),true);
		sc.getNextToken();	  // skip SHIPTYPE xx
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("size") == true) {
				shipType.setMaxSize(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name") == true) {
				shipType.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("level") == true) {
				shipType.setBuildLevel(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("range") == true) {
				shipType.setRange(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("capacity") == true) {
				shipType.setCapacity(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("captainlevel") == true) {
				shipType.setCaptainSkillLevel(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("sailorlevel") == true) {
				shipType.setSailorSkillLevel(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else {
				unknown("SHIPTYPE", true);
			}
		}
	}

	private void parseBuildingType(Rules rules) throws IOException {
		BuildingType bType = null;
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		String id = sc.argv[0].substring(f + 1, t);
		String blockName = sc.argv[0].substring(0, sc.argv[0].indexOf(" "));
		if (blockName.equals("BUILDINGTYPE")) {
			bType = rules.getBuildingType(StringID.create(id),true);
		} else if (blockName.equals("CASTLETYPE")) {
			bType = rules.getCastleType(StringID.create(id),true);
		}
		sc.getNextToken();	  // skip GEBÄUDETYP xx
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name") == true) {
				bType.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("level") == true) {
				bType.setMinSkillLevel(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("maxsize") == true) {
				bType.setMaxSize(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("minsize") == true) {
				if (bType instanceof CastleType) {
					((CastleType)bType).setMinSize(java.lang.Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("wage") == true) {
				if (bType instanceof CastleType) {
					((CastleType)bType).setPeasantWage(java.lang.Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("tradetax") == true) {
				if (bType instanceof CastleType) {
					((CastleType)bType).setTradeTax(java.lang.Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else if (sc.isBlock &&
				sc.argv[0].equals("TALENTBONI")) {
				parseBuildingSkillBonuses(bType, rules);
			} else if (sc.isBlock &&
				sc.argv[0].equals("RAWMATERIALS")) {
				parseBuildingRawMaterials(bType, rules);
			} else if (sc.isBlock &&
				sc.argv[0].equals("MAINTENANCE")) {
				parseBuildingMaintenance(bType, rules);
			} else if (sc.isBlock &&
				sc.argv[0].equals("REGIONTYPES")) {
				parseBuildingTerrain(bType, rules);
			} else if (sc.isBlock) {
				break;
			} else {
				unknown(blockName, true);
			}
		}
	}

	private void parseBuildingSkillBonuses(BuildingType bType, Rules rules) throws IOException {
		sc.getNextToken();	  // skip TALENTBONI
		while (!sc.eof && !sc.isBlock) {
			try {
				SkillType skillType = rules.getSkillType(StringID.create(sc.argv[1]), true);
				bType.setSkillBonus(skillType, Integer.parseInt(sc.argv[0]));
			} catch (NumberFormatException e) {
				log.warn("CRParser.parseBuildingSkillBonuses(): in line " + sc.lnr + ": unable to convert skill bonus " + sc.argv[0] + " to an integer. Ignoring bonus for skill " + sc.argv[1]);
			}
			sc.getNextToken();
		}
	}

	private void parseBuildingRawMaterials(BuildingType bType, Rules rules) throws IOException {
		sc.getNextToken();	  // skip RAWMATERIALS
		while (!sc.eof && !sc.isBlock) {
			try {
				ItemType itemType = rules.getItemType(StringID.create(sc.argv[1]), true);
				Item i = new Item(itemType, Integer.parseInt(sc.argv[0]));
				bType.addRawMaterial(i);
			} catch (NumberFormatException e) {
				log.warn("CRParser.parseBuildingRawMaterials(): in line " + sc.lnr + ": unable to convert item amount " + sc.argv[0] + " to an integer. Ignoring amount for item " + sc.argv[1]);
			}
			sc.getNextToken();
		}
	}

	private void parseBuildingMaintenance(BuildingType bType, Rules rules) throws IOException {
		sc.getNextToken();	  // skip MAINTENANCE
		while (!sc.eof && !sc.isBlock) {
			try {
				ItemType itemType = rules.getItemType(StringID.create(sc.argv[1]), true);
				Item i = new Item(itemType, Integer.parseInt(sc.argv[0]));
				bType.addMaintenance(i);
			} catch (NumberFormatException e) {
				log.warn("CRParser.parseBuildingMaintenance(): in line " + sc.lnr + ": unable to convert item amount " + sc.argv[0] + " to an integer. Ignoring amount for item " + sc.argv[1]);
			}
			sc.getNextToken();
		}
	}

	private void parseBuildingTerrain(BuildingType bType, Rules rules) throws IOException {
		sc.getNextToken();	  // skip MAINTENANCE
		while (!sc.eof && !sc.isBlock) {
			RegionType t = rules.getRegionType(StringID.create(sc.argv[0]), true);
			bType.addRegionType(t);
			sc.getNextToken();
		}
	}

	private void parseRegionType(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		ID id = StringID.create(sc.argv[0].substring(f + 1, t));
		RegionType regionType = rules.getRegionType(id, true);
		sc.getNextToken();	  // skip REGIONSTYP xx
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("maxworkers")) {
				regionType.setInhabitants(Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name")) {
				regionType.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("roadstones")) {
				Resource resource = new Resource(Integer.parseInt(sc.argv[0]));
				resource.setObjectType(rules.getItemType(StringID.create("Stein"),true));
				regionType.addRoadResource(resource);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("roadsupportbuilding")) {
				Resource resource = new Resource();
				resource.setObjectType(rules.getBuildingType(StringID.create(sc.argv[0]), true));
				regionType.addRoadResource(resource);
				sc.getNextToken();
			} else if (sc.argc == 2) {
				unknown("REGIONTYPE", true);
			} else {
				unknown("GEBÄUDETYP", true);
			}
		}
	}

	private void parseItemCategory(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		ID id = StringID.create(sc.argv[0].substring(f + 1, t));
		ItemCategory cat = rules.getItemCategory(id, true);
		sc.getNextToken();	  // skip ITEMCATEGORY xx
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name") == true) {
				cat.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("naturalorder") == true) {
				cat.setSortIndex(Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("parent")) {
				ItemCategory parent = rules.getItemCategory(StringID.create(sc.argv[0]), false);
				cat.setParent(parent);
				sc.getNextToken();
			} else if (sc.argc == 2) {
				unknown("ITEMCATEGORY", true);
			} else {
				break;
			}
		}
	}

	private void parseSkillCategory(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		ID id = StringID.create(sc.argv[0].substring(f + 1, t));
		SkillCategory cat = rules.getSkillCategory(id, true);
		sc.getNextToken();	  // skip SKILLCATEGORY xx
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name") == true) {
				cat.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("naturalorder") == true) {
				cat.setSortIndex(Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("parent")) {
				SkillCategory parent = rules.getSkillCategory(StringID.create(sc.argv[0]), false);
				cat.setParent(parent);
				sc.getNextToken();
			} else if (sc.argc == 2) {
				unknown("SKILLCATEGORY", true);
			} else {
				break;
			}
		}
	}

	public Rules readRules(InputStream is) throws IOException {
		return readRules(FileType.createEncodingReader(is));
	}
	/** @author Rainer Klaffehn
	 * Read a rule file, which consists of a header and a RULES block. This
	 * method will fail, if it is no rule file.
	 * The expected rule file format is:
	 * VERSION &lt;num&gt;<br>
	 * REGELN "id"<br>
	 * &lt;Game object rule block&gt;<br>
	 * ...<br>
	 * This function is synchronized.
	 * @param in The reader that will read the file for us.
	 * @return a ruleset object, or null, if the file hasn't been a ruleset.
	 */
	private synchronized Rules readRules(Reader in) throws java.io.IOException {
		Rules rules = new GenericRules();
		sc = new Scanner(in);
		sc.getNextToken();
		if (!sc.argv[0].startsWith("VERSION ") || sc.argc != 1) {
			log.warn("CRParser.readRules(): corrupt rule file missing VERSION on first line.");
			return null;
		}
		if (!sc.eof) {
			sc.getNextToken();
			if (!sc.argv[0].startsWith("RULES ") || sc.argc != 1) {
				log.warn("CRParser.readRules(): corrupt rule file missing RULE block.");
				return null;
			}
		}
		/* The desired header has been parsed. Continue parsing the sequent
		 * rule blocks until the file ends. */
		parseRules(rules);
		return rules;
	}

	private void parseRules(Rules rules) throws IOException {
		sc.getNextToken();	  // skip "RULES"
		while (!sc.eof) {
			if (sc.argv[0].startsWith("RACE ")) {
				parseRace(rules);
			} else if (sc.argv[0].startsWith("ITEM ") || sc.argv[0].startsWith("HERB ")) {
				parseItemType(rules);
			} else if (sc.argv[0].startsWith("SHIPTYPE ")) {
				parseShipType(rules);
			} else if (sc.argv[0].startsWith("BUILDINGTYPE ") || sc.argv[0].startsWith("CASTLETYPE ")) {
				parseBuildingType(rules);
			} else if (sc.argv[0].startsWith("REGIONTYPE ")) {
				parseRegionType(rules);
			} else if (sc.argv[0].startsWith("SKILL ")) {
				parseSkillType(rules);
			} else if (sc.argv[0].startsWith("ITEMCATEGORY ")) {
				parseItemCategory(rules);
			} else if (sc.argv[0].startsWith("SKILLCATEGORY ")) {
				parseSkillCategory(rules);
			} else if (sc.argv[0].startsWith("OPTIONCATEGORY ")) {
				parseOptionCategory(rules);
			} else if (sc.argv[0].startsWith("ALLIANCECATEGORY ")) {
				parseAllianceCategory(rules);
			} else {
				break;
			}
		}
	}

	private void parseOptionCategory(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		ID id = StringID.create(sc.argv[0].substring(f + 1, t));
		OptionCategory opt = rules.getOptionCategory(id, true);
		sc.getNextToken();	  // skip OPTIONCATEGORY xx
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name")) {
				opt.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("order")) {
				opt.setOrder(sc.argv[0].equals("true"));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("bitmask")) {
				opt.setBitMask( 1 << Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2) {
				unknown("OPTIONCATEGORY", true);
			} else {
				break;
			}
		}
	}

	private void parseAllianceCategory(Rules rules) throws IOException {
		int f = sc.argv[0].indexOf("\"", 0);
		int t = sc.argv[0].indexOf("\"", f + 1);
		ID id = StringID.create(sc.argv[0].substring(f + 1, t));
		AllianceCategory opt = rules.getAllianceCategory(id, true);
		sc.getNextToken();	  // skip ALLIANCECATEGORY xx
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("name")) {
				opt.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("bitmask")) {
				opt.setBitMask(Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2) {
				unknown("ALLIANCECATEGORY", true);
			} else {
				break;
			}
		}
	}




	/*
	 * This is the new version, the old is called "ALLIERTE"
	 * Heuristic for end of block detection: There are no
	 * subblocks in one ALLIANZ block.
	 */
	private Map parseAlliance(Map allies) throws IOException {
		if (allies == null) {
			allies = CollectionFactory.createOrderedHashtable();
		}

		EntityID id = EntityID.createEntityID(sc.argv[0].substring(8), 10);
		sc.getNextToken();
		int state = -1;
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Parteiname") == true) {
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Status") == true) {
				state = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else {
				unknown("ALLIANZ", true); // loop within one ALLIANZ
				break;
			}
		}
		if (state != -1) {
			Faction faction = getAddFaction(world, id);
			Alliance alliance = new Alliance(faction, world.rules, state);
			allies.put(faction.getID(), alliance);
		}

		return allies;
	}

	/*
	 * This is the old "ALLIERTE" version.
	 * Heuristic for block termination:
	 *  - Terminate on any other block
	 *** This method isn't implemented yet. It skips the entire
	 *** "ALLIERTE" block.
	 */
	private Map parseAlliierte() throws IOException {
		Map allies = CollectionFactory.createHashtable();
		sc.getNextToken(); //skip "ALLIERTE" tag
		while (!sc.eof && !sc.isBlock) {
			sc.getNextToken();
		}

		return allies;
	}

	/**
	 * This is the old "ADRESSEN" version.
	 * Heuristic for block termination:
	 *  - Terminate on any other block
	 *** This method isn't implemented yet. It skips the entire
	 *** "ADRESSEN" block.
	 **/
	private void parseAdressen() throws IOException {
		sc.getNextToken(); //skip "ADRESSEN" tag
		while (!sc.eof && !sc.isBlock) {
			sc.getNextToken();
		}
	}

	/*
	 * Parse the FACTION block with all its subblocks.
	 * Heuristic for block termination:
	 *  - Terminate on another PARTEI block (without warning)
	 *  - Terminate on another id block (without warning)
	 *  - Terminate on any other unknown block (with warning)
	 */
	private Faction parseFaction(GameData world, int sortIndex) throws IOException {
		Race type = null;
		int raceRecruit = -1;
		int groupSortIndex = 0;
		EntityID id = EntityID.createEntityID(sc.argv[0].substring(7), 10);
		sc.getNextToken();	  // skip PARTEI nr
		Faction faction = getAddFaction(world, id);
		faction.setSortIndex(sortIndex);
		while (!sc.eof && !sc.argv[0].startsWith("PARTEI ")) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Runde") == true) {
				com.eressea.rules.Date d = world.getDate();
				if (d == null) {
					world.setDate(new EresseaDate(Integer.parseInt(sc.argv[0])));
				} else {
					d.setDate(Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Passwort") == true) {
				//faction.password = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Optionen") == true) {
				if (faction.options == null) {
					faction.options = new Options(world.rules);
				}
				faction.options.setValues(java.lang.Integer.parseInt(sc.argv[0]));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Punkte") == true) {
				faction.score = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Punktedurchschnitt") == true) {
				faction.averageScore = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("email") == true) {
				faction.email = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("banner") == true) {
				faction.setDescription(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				(sc.argv[1].equalsIgnoreCase("Typ") == true ||
					sc.argv[1].equalsIgnoreCase("Typus") == true ||
					sc.argv[1].equalsIgnoreCase("race") == true)) {
				type = world.rules.getRace(StringID.create(sc.argv[0]), true);
				faction.setType(type);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Rekrutierungskosten") == true) {
				raceRecruit = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Anzahl Personen") == true) {
				faction.persons = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Anzahl Immigranten") == true) {
				faction.migrants = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Max. Immigranten") == true) {
				faction.maxMigrants = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Parteiname") == true) {
				faction.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Magiegebiet") == true) {
				faction.spellSchool = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("trustlevel") == true) {
				faction.trustLevel = Integer.parseInt(sc.argv[0]);
				faction.trustLevelSetByUser = true;
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("locale") == true) {
				faction.setLocale(new Locale(sc.argv[0], ""));
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("typprefix") == true) {
				faction.setRaceNamePrefix(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("ZAT") == true) {
				/* Verdanon tag */
				sc.getNextToken();
			} else if (sc.argc == 1 &&
				(sc.argv[0].equals("EREIGNISSE") ||
					sc.argv[0].equals("EINKOMMEN") ||
					sc.argv[0].equals("HANDEL") ||
					sc.argv[0].equals("PRODUKTION") ||
					sc.argv[0].equals("BEWEGUNGEN") ||
					sc.argv[0].equals("MELDUNGEN"))) {
				faction.messages = parseMessageSequence(faction.messages);
			} else if (sc.argc == 1 &&
				sc.argv[0].startsWith("MESSAGE ")) {
				faction.messages = parseMessages(world, faction.messages);
			} else if (sc.argc == 1 &&
				sc.argv[0].startsWith("BATTLE ")) {
				faction.battles = parseBattles(world, faction.battles);
			} else if (sc.argc == 1 &&
				sc.argv[0].startsWith("BATTLESPEC ")) {
				faction.battles = parseBattleSpecs(world, faction.battles);
			} else if (sc.argc == 1 &&
				sc.argv[0].startsWith("KAEMPFE")) {
				sc.getNextToken(); // skip KAEMPFE
				// Skip the whole block (old syntax, no longer supported)
				while (!sc.eof && !sc.isBlock) {
					sc.getNextToken();
				}
			} else if (sc.argc == 1 &&
				sc.argv[0].equals("ZAUBER")) { // old syntax, ignore
				parseMessageSequence(null);
			} else if (sc.argc == 1 &&
				sc.argv[0].startsWith("TRAENKE")) { // old syntax, ignore
				parseMessageSequence(null);
			} else if (sc.argc == 1 &&
				sc.argv[0].startsWith("ALLIIERTE")) {
				faction.allies = parseAlliierte(); // old syntax
			} else if (sc.isBlock && sc.argv[0].startsWith("ALLIANZ ")) {
				faction.allies = parseAlliance(faction.allies); // newer syntax
			} else if (sc.isBlock && sc.argv[0].equals("ADRESSEN")) {
				parseAdressen();
			} else if (sc.isBlock && sc.argv[0].equals("OPTIONEN")) {
				// ignore this block, if there are options, they are
				// encoded as a bit field whereas these string
				// representation is not fixed and eventually leads
				// to trouble
				parseOptions(null);
			} else if (sc.isBlock && sc.argv[0].startsWith("GRUPPE ")) {
				faction.groups = parseGroup(faction.groups, faction, groupSortIndex++);
			} else if (sc.argc == 1 && sc.argv[0].equals("COMMENTS")) {
				faction.comments = parseStringSequence(faction.comments);
			} else if (sc.argc == 1 && sc.argv[0].equals("FEHLER")) {
				faction.errors = parseStringSequence(faction.errors);
			} else if (sc.argc == 1 && sc.argv[0].equals("WARNUNGEN")) {
				/* Verdanon messages */
				faction.messages = parseMessageSequence(faction.messages);
			} else if (sc.isBlock) {
				if (!sc.isIdBlock)
					unknown("PARTEI", false);
				break;
			} else {
				unknown("PARTEI", true);
			}
		}

		if (type != null && raceRecruit != -1) {
			type.setRecruitmentCosts(raceRecruit);
		}

		return faction;
	}

	private Options parseOptions(Options options) throws IOException {
		sc.getNextToken();	  // skip OPTIONEN
		//if (options == null) {
		//	options = new Options();
		//}

		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2) {
				//options.setActive(StringID.create(sc.argv[1]), java.lang.Integer.parseInt(sc.argv[0]) != 0);
				sc.getNextToken();
			} else {
				unknown("OPTIONEN", true);
			}
		}

		return options;
	}

	private Map parseGroup(Map groups, Faction faction, int sortIndex) throws IOException {
		ID id = IntegerID.create(sc.argv[0].substring(7));
		Group g = null;
		if (groups == null) {
			groups = CollectionFactory.createOrderedHashtable();
		}

		g = (Group)groups.get(id);
		if (g == null) {
			g = new Group(id, world);
		}
		g.setFaction(faction);
		g.setSortIndex(sortIndex);
		groups.put(id, g);
		sc.getNextToken();	  // skip GRUPPE nr
		while (!sc.eof) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("name")) {
				g.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("typprefix")) {
				g.setRaceNamePrefix(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.isBlock && sc.argv[0].startsWith("ALLIANZ ")) {
				parseAlliance(g.allies());
			} else if (sc.isBlock) {
				break;
			} else {
				unknown("GRUPPE", true);
				break;
			}
		}

		return groups;
	}

	/**
	 * Accesses unit.persons which must be > 0 and just adds new
	 * skills, existing skills are not deleted.
	 */
	private void parseSkills(Unit unit, GameData world) throws IOException {
		sc.getNextToken();	  // skip TALENTE
		if (unit == null) {
			invalidParam("parseSkills", "unit is null");
			return;
		}
		if (unit.persons <= 0) {
			invalidParam("parseSkills", "unit.persons <= 0");
			return;
		}
		if (world == null) {
			invalidParam("parseSkills", "world is null");
			return;
		}
		if (world.rules == null) {
			invalidParam("parseSkills", "rules is null");
			return;
		}

		while (!sc.eof && sc.argc == 2) {
			int points = 0;
			int level = 0;
			int change = 0;
			boolean changed = false;

			int s = sc.argv[0].indexOf(' ', 0);
			int s2 = sc.argv[0].indexOf(' ',s+1);

			if (s > -1) {
				points = java.lang.Integer.parseInt(sc.argv[0].substring(0, s));
				if (s2 > -1) {
					level = java.lang.Integer.parseInt(sc.argv[0].substring(s + 1,s2));
					change = java.lang.Integer.parseInt(sc.argv[0].substring(s2+1));
					changed = true;
				} else {
					level = java.lang.Integer.parseInt(sc.argv[0].substring(s + 1));
				}
			} else {
				level = java.lang.Integer.parseInt(sc.argv[0]);
			}

			Skill skill = new Skill(world.rules.getSkillType(StringID.create(sc.argv[1]), true), points, level, unit.persons, world.noSkillPoints);
			skill.setChangeLevel(change);
			skill.setLevelChanged(changed);
			unit.addSkill(skill);
			sc.getNextToken();
		}
	}

	/*
	 * Syntax: "count;item"
	 * Example: "1;Steine"
	 * Does not delete existing items.
	 */
	private void parseItems(Unit unit) throws IOException {
		if (unit == null) {
			invalidParam("parseItems", "unit is null");
			return;
		}
		sc.getNextToken();	  // skip GEGENSTAENDE
		while (!sc.eof && sc.argc == 2) {
			Item item = new Item(world.rules.getItemType(StringID.create(sc.argv[1]), true), java.lang.Integer.parseInt(sc.argv[0]));
			unit.addItem(item);
			sc.getNextToken();
		}
	}

	private int parseUnit(GameData world, Region region, int sortIndex) throws IOException {
		Unit unit = getAddUnit(world, UnitID.createUnitID(sc.argv[0].substring(8), 10));
		EntityID factionID = EntityID.createEntityID(-1);
		ID groupID = null;
		if (region != unit.getRegion()) {
			unit.setRegion(region);
		}

		// if there is a unit in the region, this means we have
		// infos about it:

		if (region != null) {
			region.trees = Math.max(region.trees, 0);
			region.peasants = Math.max(region.peasants, 0);
			region.horses = Math.max(region.horses, 0);
			region.trees = Math.max(region.trees, 0);
		}

		sc.getNextToken();	  // skip "EINHEIT nr"
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Name") == true) {
				unit.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Beschr") == true) {
				unit.setDescription(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Typ") == true) {
				unit.race = world.rules.getRace(StringID.create(sc.argv[0]), true);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("wahrerTyp") == true) {
				unit.realRace = world.rules.getRace(StringID.create(sc.argv[0]), true);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("temp") == true) {
				unit.setTempID(UnitID.createUnitID(sc.argv[0], 10));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("alias") == true) {
				unit.setAlias(UnitID.createUnitID(sc.argv[0], 10));
				sc.getNextToken();
			}else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("privat") == true) {
				unit.privDesc = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Anzahl") == true) {
				unit.persons = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Partei") == true) {
				factionID = EntityID.createEntityID(sc.argv[0], 10);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Parteiname") == true) {
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Parteitarnung") == true) {
				if (java.lang.Integer.parseInt(sc.argv[0]) != 0)
					unit.hideFaction = true;
				else
					unit.hideFaction = false;
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("bewacht") == true) {
				unit.guard = java.lang.Integer.parseInt(sc.argv[0]);
				Region r = unit.getRegion();
				if (r != null)
					r.addGuard(unit);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("belagert") == true) {
				unit.siege =
						getAddBuilding(world,
							EntityID.createEntityID(sc.argv[0], 10));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("folgt") == true) {
				unit.follows =
						getAddUnit(world, UnitID.createUnitID(sc.argv[0], 10));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Silber") == true) {
				int money = java.lang.Integer.parseInt(sc.argv[0]);
				Item item = new Item(world.rules.getItemType(StringID.create("Silber"), true), money);
				unit.addItem(item);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Burg") == true) {
				Integer.parseInt(sc.argv[0]);
				Building b =
					getAddBuilding(world,
						EntityID.createEntityID(sc.argv[0], 10));
				if (unit.getBuilding() != b) {
					unit.setBuilding(b);
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Schiff") == true) {
				Ship s = getAddShip(world,
						EntityID.createEntityID(sc.argv[0], 10));
				if (unit.getShip() != s) {
					unit.setShip(s);
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Kampfstatus") == true) {
				// pre 57:
				// 0: VORNE
				// 1: HINTEN
				// 2: NICHT
				// 3: FLIEHE
				//
				// 57 and later:
				// 0 AGGRESSIV: 1. Reihe, flieht nie.
				// 1 VORNE: 1. Reihe, kämpfen bis 20% HP
				// 2 HINTEN: 2. Reihe, kämpfen bis 20% HP
				// 3 DEFENSIV: 2. Reihe, kämpfen bis 90% HP
				// 4 NICHT: 3. Reihe, kämpfen bis 90% HP
				// 5 FLIEHE: 4. Reihe, flieht immer.
				unit.combatStatus = java.lang.Integer.parseInt(sc.argv[0]);
				// convert status from old to new
				if (version < 57) {
					unit.combatStatus++;
					if (unit.combatStatus > 2) {
						unit.combatStatus++;
					}
				}

				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("unaided") == true) {
				unit.unaided = (java.lang.Integer.parseInt(sc.argv[0]) != 0);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Tarnung") == true) {
				unit.stealth = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Aura") == true) {
				unit.aura = java.lang.Integer.parseInt(sc.argv[0]);
				;
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Auramax") == true) {
				unit.auraMax = java.lang.Integer.parseInt(sc.argv[0]);
				;
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("hp") == true) {
				unit.health = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("hunger") == true) {
				unit.isStarving = (Integer.parseInt(sc.argv[0]) != 0);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("ejcOrdersConfirmed") == true) {
				unit.ordersConfirmed = (Integer.parseInt(sc.argv[0]) != 0);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("gruppe") == true) {
				groupID = IntegerID.create(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("verraeter") == true) {
				unit.setSpy(true);
				sc.getNextToken();
				/* currently, verkleidung was announced but it seems that
				 anderepartei is used. Please remove one as soon as it
				 is clear which one can be discarded */
			} else if (sc.argc == 2 &&
				(sc.argv[1].equalsIgnoreCase("verkleidung") == true || sc.argv[1].equalsIgnoreCase("anderepartei") == true)) {
				ID fid = EntityID.createEntityID(sc.argv[0], 10);
				unit.setGuiseFaction(world.getFaction(fid));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("typprefix") == true) {
				unit.setRaceNamePrefix(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("ladung") == true) {
				// Verdanon tag
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("kapazitaet") == true) {
				// Verdanon tag
				sc.getNextToken();
			} else if (sc.argc == 1 &&
				sc.argv[0].equals("COMMANDS") == true) {
				// there can be only one order block for a unit, replace existing ones
				unit.setOrders(parseStringSequence(null),false);
			} else if (sc.argc == 1 &&
				sc.argv[0].equals("TALENTE") == true) {
				// there can be only one skills block for a unit, replace existing ones
				unit.clearSkills();
				parseSkills(unit, world);
			} else if (sc.argc == 1 &&
				sc.argv[0].equals("SPRUECHE") == true) {
				// there can be only one spells block for a unit, replace existing ones
				unit.spells = parseUnitSpells(world, null);
			} else if (sc.argc == 1 &&
				sc.argv[0].equals("GEGENSTAENDE") == true) {
				/* in verdanon reports the silver can already be
				 included in the items */
				parseItems(unit);
			} else if (sc.isBlock && sc.argv[0].equals("EINHEITSBOTSCHAFTEN")) {
				unit.unitMessages = parseMessageSequence(unit.unitMessages);
			} else if (sc.argc == 1 && sc.argv[0].equals("EFFECTS")) {
				unit.effects = parseStringSequence(unit.effects);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("KAMPFZAUBER ")) {
				parseUnitCombatSpells(world, unit);
			} else if (sc.isBlock) {
				break;
			} else {
				if (sc.argc == 2)
					unit.putTag(sc.argv[1], sc.argv[0]);
				unknown("EINHEIT", true);
			}
		}

		// set the sortIndex so the original ordering of the units
		// can be restored
		unit.setSortIndex(sortIndex++);
		Faction faction = getAddFaction(world, factionID);
		if (faction.getName() == null) {
			if (factionID.intValue() == -1)
				faction.setName("Parteigetarnte");
			else if (factionID.intValue() == 0)
				faction.setName("Monster");
			else
				faction.setName("Partei " + factionID);
		}

		if (unit.getFaction() != faction) {
			unit.setFaction(faction);
		}

		if (groupID != null) {
			Group g = null;
			if (faction.groups != null && (g = (Group)faction.groups.get(groupID)) != null) {
				unit.setGroup(g);
			} else {
				log.warn("CRParser.parseUnit(): Unable to assign group " + groupID + " to unit " + unit.getID());
			}
		}

		/* a missing combat status can have two meanings:
		 1. this is a unit we know everything about and the combat
		 status is AGGRESSIVE
		 2. this is a unit we just see but does not belong to us so we
		 do not know its combat status.
		 */
		if (!unit.ordersAreNull() && unit.combatStatus < 0) {
			unit.combatStatus = 0;
		}

		return sortIndex;
	}

	/*
	 * Syntax: value;item
	 * Example: 24;Balsam
	 * < 0: offered in this region
	 * > 0: demanded in this region
	 */
	private Map parsePrices(Map prices) throws IOException {
		sc.getNextToken();	  // skip PREISE
		while (!sc.eof && sc.argc == 2) {
			ItemType itemType = world.rules.getItemType(StringID.create(sc.argv[1]), true);
			LuxuryPrice pr = new LuxuryPrice(itemType, Integer.parseInt(sc.argv[0]));
			if (prices == null) {
				prices = CollectionFactory.createOrderedHashtable();
			}
			prices.put(itemType.getID(), pr);
			sc.getNextToken();
		}

		return prices;
	}

	private void parseShip(GameData world, Region region, int sortIndex) throws IOException {
		EntityID id = EntityID.createEntityID(sc.argv[0].substring(7), 10);
		sc.getNextToken();	  // skip "SCHIFF nr"
		Ship ship = getAddShip(world, id);
		if (ship.getRegion() != region) {
			ship.setRegion(region);
		}
		ship.setSortIndex(sortIndex);
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Name") == true) {
				ship.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Typ") == true) {
				ShipType type = world.rules.getShipType(StringID.create(sc.argv[0]), true);
				ship.setType(type);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Beschr") == true) {
				ship.setDescription(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Partei") == true) {
				if (ship.getOwnerUnit() != null && ship.getOwnerUnit().getFaction() == null) {
					Faction f = world.getFaction(EntityID.createEntityID(sc.argv[0], 10));
					ship.getOwnerUnit().setFaction(f);
				}
				sc.getNextToken();
			}else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Kapitaen") == true) {
				ship.setOwnerUnit(
					getAddUnit(world,
						UnitID.createUnitID(sc.argv[0], 10)));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Kueste") == true) {
				ship.shoreId = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Groesse") == true) {
				ship.size = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Prozent") == true &&
				ship.getType() != null) {
				ship.size = (ship.getShipType().getMaxSize() * java.lang.Integer.parseInt(sc.argv[0])) / 100;
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Schaden") == true) {
				ship.damageRatio = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Ladung") == true) {
				ship.load = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("MaxLadung") == true) {
				ship.capacity = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 1 && sc.argv[0].equals("EFFECTS")) {
				ship.effects = parseStringSequence(ship.effects);
			} else if (sc.argc == 1 && sc.argv[0].equals("COMMENTS")) {
				ship.comments = parseStringSequence(ship.comments);
			} else if (sc.isBlock) {
				break;
			} else {
				unknown("SCHIFF", true);
			}
		}
	}

	/*
	 *
	 */
	private void parseBuilding(GameData world, Region region, int sortIndex) throws IOException {
		EntityID id = EntityID.createEntityID(sc.argv[0].substring(5), 10);
		sc.getNextToken();	  // skip "BURG nr"
		Building bld = getAddBuilding(world, id);
		if (bld.getRegion() != region) {
			bld.setRegion(region);
		}
		bld.setSortIndex(sortIndex);
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Name") == true) {
				bld.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Typ") == true) {
				BuildingType type = world.rules.getBuildingType(StringID.create(sc.argv[0]), true);
				bld.setType(type);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Beschr") == true) {
				bld.setDescription(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Besitzer") == true) {
				UnitID unitID = UnitID.createUnitID(sc.argv[0], 10);
				bld.setOwnerUnit(
					getAddUnit(world, unitID));
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Partei") == true) {
				if (bld.getOwnerUnit() != null && bld.getOwnerUnit().getFaction() == null) {
					Faction f = world.getFaction(EntityID.createEntityID(sc.argv[0], 10));
					bld.getOwnerUnit().setFaction(f);
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Groesse") == true) {
				bld.setSize( java.lang.Integer.parseInt(sc.argv[0]) );
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Unterhalt") == true) {
				bld.setCost( java.lang.Integer.parseInt(sc.argv[0]) );
				sc.getNextToken();
			} else if (sc.argc == 1 && sc.argv[0].equals("EFFECTS")) {
				bld.effects = parseStringSequence(bld.effects);
			} else if (sc.argc == 1 && sc.argv[0].equals("COMMENTS")) {
				bld.comments = parseStringSequence(bld.comments);
			} else {
				break;
			}
		}
	}

	/**
	 * Parse consecutive GRENZE sub blocks of the REGION block.
	 *
	 * @param list a list to add the read borders to
	 * @returns the resulting list of <tt>Border</tt> objects.
	 */
	private void parseBorders(Region r) throws IOException {
		while (!sc.eof && sc.isBlock && sc.argv[0].startsWith("GRENZE ")) {
			Border b = parseBorder();
			r.addBorder(b);
		}
	}

	/**
	 * Parse one GRENZE sub block of the REGION block.
	 *
	 * @returns the resulting <tt>Border</tt> object.
	 */
	private Border parseBorder() throws IOException {
		ID id = IntegerID.create(sc.argv[0].substring(7));
		Border b = new Border(id);
		sc.getNextToken();	  // skip the block
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("richtung")) {
				b.direction = -1;
				try {
					b.direction = Integer.parseInt(sc.argv[0]);
				} catch (NumberFormatException e) {
					final String dirNames[] = {"Nordwesten", "Nordosten", "Osten", "Südosten", "Südwesten", "Westen"};
					for (int i = 0; i < dirNames.length; i++) {
						if (sc.argv[0].equalsIgnoreCase(dirNames[i]) == true) {
							b.direction = i;
							break;
						}
					}
				}
				sc.getNextToken();
			} else
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("typ")) {
				b.type = sc.argv[0];
				sc.getNextToken();
			} else
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("prozent")) {
				b.buildRatio = Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else
			if (sc.isBlock == true) {
				break;
			} else {
				unknown("GRENZE", false);
			}
		}

		return b;
	}

	private void parseHotSpot(GameData data) throws IOException {
		ID id = IntegerID.create(sc.argv[0].substring(8));
		HotSpot h = new HotSpot(id);
		sc.getNextToken();	  // skip the block
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("coord") == true) {
				h.setCenter(Coordinate.parse(sc.argv[0], " "));
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("name") == true) {
				h.setName(sc.argv[0]);
				sc.getNextToken();
			} else {
				unknown("HOTSPOT", true);
			}
		}

		data.setHotSpot(h);
	}

	/*
	 * Parse everything within one region.
	 * Heuristic for block termination:
	 *  - Terminate on another REGION or SPEZIALREGION block (without warning)
	 *  - Terminate on any other unknown block (with warning)
	 */
	private void parseRegion(GameData world, int sortIndex) throws IOException {
		int iValidateFlags = 0; // 1 - terrain type
		int unitSortIndex = 0;
		int shipSortIndex = 0;
		int buildingSortIndex = 0;
		ID c = Coordinate.parse(sc.argv[0].substring(sc.argv[0].indexOf(" ", 0)), " ");
		if (c == null) {
			unknown("REGION", true);
			return;
		}
		sc.getNextToken();	  // skip "REGION x y"
		Region region = world.getRegion(c);
		if (region == null) {
			region = new Region(c, world);
		}
		region.setSortIndex(sortIndex);
		while (!sc.eof) {
			if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Name") == true) {
				// regions doesn't have name if name == type; e.g. "Ozean"=="Ozean"
				if (region.getType() == null) {
					region.setName(sc.argv[0]);
				} else {
					// FIX this will bite us sooner or later
				//	if (!region.getType().getName().equals(sc.argv[0])) {
						region.setName(sc.argv[0]);
				//	}

				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Beschr") == true) {
				region.setDescription(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Strasse") == true) {
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Insel") == true) {
				try {
					ID islandID = IntegerID.create(sc.argv[0]);
					Island i = world.getIsland(islandID);
					region.setIsland(i);
					if (i == null) {
						log.warn("CRParser.parseRegion(): unknown island " + sc.argv[0] + " with region " + region + " in line " + sc.lnr);
					}
				} catch (NumberFormatException nfe) {
					log.warn("CRParser.parseRegion(): unknown island " + sc.argv[0] + " with region " + region + " in line " + sc.lnr);
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Lohn") == true) {
				region.wage = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letzterlohn") == true) {
				region.oldWage = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Terrain") == true) {
				try {
					RegionType type = world.rules.getRegionType(StringID.create(sc.argv[0]), true);
					region.setType(type);
				} catch (IllegalArgumentException e) {
					// can happen in StringID constructor if sc.argv[0] == ""
					log.warn("CRParser.parseRegion(): found region without a valid region type in line " + sc.lnr);
				}
				// regions doesn't have name if name == type; e.g. "Ozean"=="Ozean"
				if (region.getType() != null) {
					if (region.getType().getName() != null) {
						// FIX this will bite us sooner or later
			//			if (region.getType().getName().equals(region.getName())) {
			//				region.setName( null );
			//			}
					} else {
						log.warn("CRParser.parseRegion(): found region type without a valid name in line " + sc.lnr);
					}
				} else {
					log.warn("CRParser.parseRegion(): found region without a valid region type in line " + sc.lnr);
				}
				iValidateFlags |= 1;
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Baeume") == true) {
				region.trees = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letztebaeume") == true) {
				region.oldTrees = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Bauern") == true) {
				region.peasants = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letztebauern") == true) {
				region.oldPeasants = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Silber") == true) {
				region.silver = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letztessilber") == true) {
				region.oldSilver = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Eisen") == true) {
				region.iron = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letzteseisen") == true) {
				region.oldIron = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Laen") == true) {
				region.laen = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letzteslaen") == true) {
				region.oldLaen = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Pferde") == true) {
				region.horses = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letztepferde") == true) {
				region.oldHorses = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Unterh") == true) {
				// Has not to be stored.
				sc.getNextToken();
				// pavkovic 2002.05.10: recruits (and old recruits are used from cr)
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Rekruten") == true) {
				if(version >= 64) {
					region.recruits = java.lang.Integer.parseInt(sc.argv[0]);
				} else {
					// Has not to be stored.
				}
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letzterekruten") == true) {
				region.oldRecruits = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("maxLuxus") == true) {
				// Has not to be stored.
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Mallorn") == true) {
				if (java.lang.Integer.parseInt(sc.argv[0]) > 0)
					region.mallorn = true;
				else
					region.mallorn = false;
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("herb")) {
				ItemType type = world.rules.getHerb(StringID.create(sc.argv[0]), true);
				region.herb = type;
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("herbamount")) {
				region.herbAmount = sc.argv[0];
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].compareTo("Runde") == 0) {
				// ignore this tag
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Verorkt")) {
				region.orcInfested = true;
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Schoesslinge")) {
				region.sprouts = Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letzteSchoesslinge")) {
				region.oldSprouts = Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("Steine")) {
				region.stones = Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("letztesteine") == true) {
				region.oldStones = java.lang.Integer.parseInt(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.argc == 2 &&
				sc.argv[1].equalsIgnoreCase("visibility") == true) {
				region.setVisibility(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.isBlock &&
				sc.argv[0].equals("PREISE")) {
				region.prices = parsePrices(region.prices);
			} else if (sc.isBlock &&
				sc.argv[0].equals("LETZTEPREISE")) {
				region.oldPrices = parsePrices(region.oldPrices);
			} else if (sc.isBlock &&
				sc.argv[0].startsWith("GRENZE ")) {
				parseBorders(region);
			} else if (sc.isBlock &&
				sc.argv[0].startsWith("EINHEIT ")) {
				unitSortIndex = parseUnit(world, region, ++unitSortIndex);
			} else if (sc.isBlock &&
				sc.argv[0].startsWith("SCHIFF ")) {
				parseShip(world, region, ++shipSortIndex);
			} else if (sc.isBlock &&
				sc.argv[0].startsWith("BURG ")) {
				parseBuilding(world, region, ++buildingSortIndex);
			} else if (sc.isBlock &&
				sc.argv[0].startsWith("MESSAGE ")) {
				region.messages = parseMessages(world, region.messages);
			} else if (sc.isBlock &&
				sc.argv[0].equals("REGIONSEREIGNISSE")) {
				region.events = parseMessageSequence(region.events);
				/*} else if(sc.argc == 1 &&
				 sc.argv[0].equals("REGIONSKOMMENTAR")) {
				 region.comments = parseMessageSequence(region.comments);*/
			} else if (sc.isBlock &&
				sc.argv[0].equals("REGIONSBOTSCHAFTEN")) {
				region.playerMessages = parseMessageSequence(region.playerMessages);
			} else if (sc.isBlock &&
				sc.argv[0].equals("UMGEBUNG")) {
				region.surroundings = parseMessageSequence(region.surroundings);
			} else if (sc.isBlock &&
				sc.argv[0].equals("DURCHREISE")) {
				region.travelThru = parseMessageSequence(region.travelThru);
			} else if (sc.isBlock && sc.argv[0].equals("DURCHSCHIFFUNG")) {
				region.travelThruShips = parseMessageSequence(region.travelThruShips);
			} else if (sc.isBlock && sc.argv[0].equals("EFFECTS")) {
				region.effects = parseStringSequence(region.effects);
			} else if (sc.isBlock && sc.argv[0].equals("COMMENTS")) {
				region.comments = parseStringSequence(region.comments);
			} else if (sc.isBlock && sc.argv[0].startsWith("RESOURCE ")) {
				RegionResource res = parseRegionResource(world.rules, region);
				if (res != null) {
					region.addResource(res);
				}
			} else if (sc.isBlock && sc.argv[0].startsWith("SCHEMEN ")) {
				parseScheme(world, region);
			} else if (sc.isBlock && sc.argv[0].equals("MESSAGETYPES")) {
				break;
			} else if (sc.isBlock && sc.argv[0].startsWith("REGION ")) {
				break;
			} else if (sc.isBlock) {
				break;
			} else {
				if (sc.argc == 2)
					region.putTag(sc.argv[1], sc.argv[0]);
				unknown("REGION", true);
			}
		}

		//validate region before add to world data
		if ((iValidateFlags & 1) == 0) {
			log.warn("Warning: No region type is given for region '" + region.toString() + "' - it is ignored.");
		} else {
			world.addRegion(region);
		}
	}

	private Region parseSpecialRegion(GameData world, Region specialRegion) throws IOException {
		int unitSortIndex = 0;
		sc.getNextToken();	  // skip "SPEZIALREGION x y"
		if (specialRegion == null) {
			Coordinate c = new Coordinate(0, 0, 1);
			while (world.getRegion(c) != null) {
				c = new Coordinate(0, 0, (int)(Math.random() * (Integer.MAX_VALUE - 1)) + 1);
			}
			specialRegion = new Region(c, world);
			specialRegion.setName("Astralregion");
			world.addRegion(specialRegion);
		}

		while (!sc.eof) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("terrain")) {
				specialRegion.setType(world.rules.getRegionType(StringID.create(sc.argv[0]), true));
				sc.getNextToken();
			} else if (sc.isBlock && sc.argv[0].startsWith("SCHEMEN ")) {
				parseScheme(world, specialRegion);
			} else if (sc.isBlock && sc.argv[0].startsWith("MESSAGE ")) {
				specialRegion.messages = parseMessages(world, specialRegion.messages);
			} else if (sc.isBlock && sc.argv[0].equals("EFFECTS")) {
				specialRegion.effects = parseStringSequence(specialRegion.effects);
			} else if (sc.isBlock && sc.argv[0].equals("COMMENTS")) {
				specialRegion.comments = parseStringSequence(specialRegion.comments);
			} else if (sc.isBlock && sc.argv[0].startsWith("GRENZE ")) {
				parseBorders(specialRegion);
			} else if (sc.isBlock && sc.argv[0].equals("REGIONSEREIGNISSE")) {
				specialRegion.events = parseMessageSequence(specialRegion.events);
			} else if (sc.isBlock && sc.argv[0].equals("REGIONSBOTSCHAFTEN")) {
				specialRegion.playerMessages = parseMessageSequence(specialRegion.playerMessages);
			} else if (sc.isBlock && sc.argv[0].equals("UMGEBUNG")) {
				specialRegion.surroundings = parseMessageSequence(specialRegion.surroundings);
			} else if (sc.isBlock && sc.argv[0].equals("DURCHREISE")) {
				specialRegion.travelThru = parseMessageSequence(specialRegion.travelThru);
			} else if (sc.isBlock && sc.argv[0].equals("DURCHSCHIFFUNG")) {
				specialRegion.travelThruShips = parseMessageSequence(specialRegion.travelThruShips);
			} else if (sc.isBlock && sc.argv[0].startsWith("EINHEIT ")) {
				unitSortIndex = parseUnit(world, specialRegion, ++unitSortIndex);
			} else if (sc.isBlock) {
				break;
			} else {
				unknown("SPEZIALREGION", true);
				break;
			}
		}

		return specialRegion;
	}

	private RegionResource parseRegionResource(Rules rules, Region region) throws IOException {
		RegionResource r = null;
		ID id = null;
		ItemType type = null;
		id = LongID.create(sc.argv[0].substring(sc.argv[0].indexOf(" ", 0) + 1));
		sc.getNextToken();	// skip "RESOURCE id"
		while (!sc.eof && !sc.isBlock) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("type")) {
				if (r == null) {
					type = rules.getItemType(StringID.create(sc.argv[0]), true);
					if (type != null) {
						r = new RegionResource(id, type);
					}
				}
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("skill")) {
				if (r != null) {
					r.setSkillLevel(Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("number")) {
				if (r != null) {
					r.setAmount(Integer.parseInt(sc.argv[0]));
				}
				sc.getNextToken();
			} else {
				unknown("RESOURCE", true);
			}
		}

		return r;
	}

	private void parseScheme(GameData world, Region region) throws IOException {
		Coordinate c = Coordinate.parse(sc.argv[0].substring(sc.argv[0].indexOf(" ", 0)), " ");
		if (c == null) {
			unknown("SCHEMEN", true);
			return;
		}
		sc.getNextToken();	  // skip "SCHEMEN x y"
		Scheme scheme = new Scheme(c);
		region.addScheme(scheme);
		while (!sc.eof) {
			if (sc.argc == 2 && sc.argv[1].equalsIgnoreCase("name")) {
				scheme.setName(sc.argv[0]);
				sc.getNextToken();
			} else if (sc.isBlock) {
				break;
			} else {
				unknown("SCHEMEN", true);
				break;
			}
		}
	}

	/**
	 * This function parses the informations found in Reader in and creates a corresponding 
	 * GameData object tree.
	 * @param in Reader to cr file
	 * @param world GameData to be filled with informations of given cr file
	 * This function is synchronized.
	 */

	public synchronized GameData read(Reader in, GameData world) throws IOException {
		boolean bCorruptReportMsg = false;
		int regionSortIndex = 0;
		this.world = world;
		sc = new Scanner(in);
		sc.getNextToken();
		while (!sc.eof) {
			if (sc.argv[0].startsWith("VERSION")) {
				parseHeader(world);
			} else if (sc.argc == 1 && sc.argv[0].startsWith("REGION ")) {
				if (!bCorruptReportMsg) {
					log.warn("Warning: This computer report is " +
						"missing the header and is therfore invalid or " +
						"corrupted. Please contact the originator of this " +
						"report if you experience data loss.");
					bCorruptReportMsg = true;
				}
				parseRegion(world, ++regionSortIndex);
			} else {
				unknown("top level", true);
			}
		}

		return this.world;
	}

	private void invalidParam(String method, String msg) {
		log.warn("CRParser." + method + "(): invalid parameter specified, " + msg + "! Unable to parse block in line " + sc.lnr);
	}

	private void parseTranslation(GameData data) throws IOException {
		sc.getNextToken();
		while (!sc.eof) {
			if (sc.argc == 2) {
				data.addTranslation(sc.argv[1], sc.argv[0]);
				sc.getNextToken();
			} else if (sc.isBlock) {
				break;
			} else {
				unknown("TRANSLATION", true);
				break;
			}
		}
	}
}
