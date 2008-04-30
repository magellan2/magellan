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

package com.eressea.io.cr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.eressea.Alliance;
import com.eressea.Battle;
import com.eressea.Border;
import com.eressea.Building;
import com.eressea.CombatSpell;
import com.eressea.CoordinateID;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Group;
import com.eressea.HotSpot;
import com.eressea.IntegerID;
import com.eressea.Island;
import com.eressea.Item;
import com.eressea.LuxuryPrice;
import com.eressea.Message;
import com.eressea.Potion;
import com.eressea.Region;
import com.eressea.RegionResource;
import com.eressea.Scheme;
import com.eressea.Ship;
import com.eressea.Sign;
import com.eressea.Skill;
import com.eressea.Spell;
import com.eressea.TempUnit;
import com.eressea.Unit;
import com.eressea.UnitID;
import com.eressea.io.file.FileType;
import com.eressea.rules.EresseaDate;
import com.eressea.rules.MessageType;
import com.eressea.rules.OptionCategory;
import com.eressea.rules.Options;
import com.eressea.rules.Race;
import com.eressea.rules.UnitContainerType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Umlaut;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.SortIndexComparator;
import com.eressea.util.logging.Logger;

/**
 * A class for writing game data, or parts of it, to a stream in the computer report format.
 * 
 * <p>
 * The generated output has platform dependent line breaks.
 * </p>
 * 
 * <p>
 * Implementation notes:
 * </p>
 * 
 * <p>
 * The basic mechanism of this class is to overload the <tt>write()</tt> method for most of the
 * Eressea base classes. Since many blocks and tags come in bunches there are helper functions for
 * each such sequence handling the collections in which the data objects are stored in.
 * </p>
 */
public class CRWriter extends BufferedWriter {
	private static final Logger log = Logger.getInstance(CRWriter.class);
	private boolean useTildesForQuotes = false;
	private Comparator sortIndexComparator = new SortIndexComparator(IDComparator.DEFAULT);
	private String encoding = FileType.DEFAULT_ENCODING;

	// incremented whenever a unit is written, can then be compared
	// to the total number of units in the game data
	private int unitsWritten = 0;
	
	// fiete: see no other choice to find the familiarmage - unit
	private GameData data = null;

	/**
	 * Escape quotation marks in <tt>text</tt> with a backslash.
	 *
	 * @param text the string to be modified.
	 *
	 * @return the resulting string with escaped quotation marks.
	 */
	private String escapeQuotes(String text) {
		if(text == null) {
			log.warn("CRWriter.escapeQuotes(): argument 'text' is null");

			return null;
		}

		StringBuffer sb = new StringBuffer(text.length() + 2);

		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if(c == '"') {
				sb.append('\\');
			}

			sb.append(c);
		}

		return sb.toString();
	}

	/**
	 * Remove quotes from text and replace blanks whithin the quoted section with ~ characters. ("a
	 * \"b c\"" -> "a b~c")
	 *
	 * @param text the string to be modified.
	 *
	 * @return the resulting string.
	 */
	private String tildeQuotes(String text) {
		if(text == null) {
			log.warn("CRWriter.tildeQuotes(): argument 'text' is null");

			return null;
		}

		StringBuffer sb = new StringBuffer(text.length() + 2);
		boolean replace = false;

		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if(c == '"') {
				replace = !replace;
			} else if((c == ' ') && replace) {
				sb.append('~');
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * Write the String <tt>str</tt> quoted to the underlying stream. If a part of <tt>str</tt> is
	 * quoted, its quotes are escaped according to the current quote escape setting.
	 *
	 * @param str TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	private void writeQuotedString(String str) throws IOException {
		if(str == null) {
			log.warn("CRWriter.writeQuotedString(): argument str is null");

			return;
		}

		boolean repairString = false;

		if(str.indexOf('\n') != -1) {
			repairString = true;
			log.warn("CRWriter.writeQuotedString(): argument str contains \'\\n\'. Splitting line.");
		}

		if(str.indexOf('\r') != -1) {
			repairString = true;
			log.warn("CRWriter.writeQuotedString(): argument str contains \'\\r\'. Splitting line.");
		}

		if(repairString) {
			// 2002.04.05 pavkovic: It seems that where exist a string with "\r\n" inside
			// These will be written linewise
			StringTokenizer st = new StringTokenizer(str, "\n\r");

			while(st.hasMoreTokens()) {
				writeQuotedString(st.nextToken());
			}

			return;
		}

		if(useTildesForQuotes) {
			write("\"" + tildeQuotes(str) + "\"");
		} else {
			write("\"" + escapeQuotes(str) + "\"");
		}

		newLine();
	}

	/**
	 * Write the String <tt>str</tt> quoted along with the specified tag to the underlying stream.
	 * If a part of <tt>str</tt> is quoted, its quotes are escaped according to the current quote
	 * escape setting. writeQuotedTag("a b", "tag") results in writing "\"a b\";tag\n" to the
	 *
	 * @param str the string that is to be put in quotes and written to the
	 * @param tag the tag to be written to the stream, separated from <tt>str</tt> by a semicolon.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	private void writeQuotedTag(String str, String tag) throws IOException {
		if(str == null) {
			log.warn("CRWriter.writeQuotedTag(): argument str is null");

			return;
		}

		if(tag == null) {
			log.warn("CRWriter.writeQuotedTag(): argument tag is null");

			return;
		}

		if(useTildesForQuotes) {
			write("\"" + tildeQuotes(str) + "\";" + tag);
		} else {
			write("\"" + escapeQuotes(str) + "\";" + tag);
		}

		newLine();
	}

	/**
	 * Write a sequence of message blocks to the underlying stream.
	 *
	 * @param list a list containing the <tt>Message</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeMessages(List list) throws IOException {
		if(list == null) {
			return;
		}

		for(Iterator iter = list.iterator(); iter.hasNext();) {
			writeMessage((Message) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Message</tt> object to the underlying stream.
	 *
	 * @param msg TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeMessage(Message msg) throws IOException {
		if(msg == null) {
			return;
		}

		write("MESSAGE " + msg.getID());
		newLine();

		if(msg.getMessageType() != null) {
			write(msg.getMessageType().getID() + ";type");
			newLine();
		}

		if(msg.getText() != null) {
			writeQuotedTag(msg.getText(), "rendered");
		}

		if(msg.attributes != null) {
			for(Iterator iter = msg.attributes.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) msg.attributes.get(key);

				try {
					Integer.parseInt((String) value);
					write(value + ";" + key);
					newLine();
				} catch(NumberFormatException e) {
					CoordinateID c = CoordinateID.parse((String) value, " ");

					if(c != null) {
						write(value + ";" + key);
						newLine();
					} else {
						writeQuotedTag((String) value, (String) key);
					}
				}
			}
		}
	}

	/**
	 * Write the data as one block named <tt>blockName</tt> to the underlying stream. The data is
	 * written as simple cr strings. The block name is only written if there is data to follow.
	 *
	 * @param blockName the name of the block to be written (can not be a block with an id).
	 * @param data a collection containing <tt>Message</tt> objects.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeMessageBlock(String blockName, Collection data) throws IOException {
		if((data == null) || data.isEmpty()) {
			return;
		}

		write(blockName);
		newLine();
		writeMessageSequence(data);
	}

	/**
	 * Write the data as one sequence of simple cr strings.
	 *
	 * @param data a collection containing <tt>Message</tt> objects.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeMessageSequence(Collection data) throws IOException {
		if((data == null) || data.isEmpty()) {
			return;
		}

		for(Iterator iter = data.iterator(); iter.hasNext();) {
			Message msg = (Message) iter.next();
			writeQuotedString(msg.getText());
		}
	}

	/**
	 * Write a the data as one sequence of simple cr strings.
	 *
	 * @param data a collection containing <tt>String</tt> objects.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeStringSequence(Collection data) throws IOException {
		if((data == null) || data.isEmpty()) {
			return;
		}

		for(Iterator iter = data.iterator(); iter.hasNext();) {
			String str = (String) iter.next();
			writeQuotedString(str);
		}
	}

	/**
	 * Write the data as one block named <tt>blockName</tt> to the underlying stream. The data is
	 * written as simple cr strings. The block name is only written if there is data to follow.
	 *
	 * @param blockName the name of the block to be written (can not be a block with an id).
	 * @param data a colleciton containing <tt>String</tt> objects.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeStringBlock(String blockName, Collection data) throws IOException {
		if((data == null) || data.isEmpty()) {
			return;
		}

		write(blockName);
		newLine();
		writeStringSequence(data);
	}

	/**
	 * Write the VERSION block for the specified game data to the underyling
	 *
	 * @param world TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeVersion(GameData world) throws IOException {
		write("VERSION 64");
		newLine();
		
		// The Echecker of German Atlantis has problems with the locale line
		// so we check the game name
		if (!world.name.startsWith("GAV")){
		    writeQuotedTag(world.encoding,"charset");
			if(world.getLocale() != null) {
				writeQuotedTag(world.getLocale().toString(), "locale");
			}
		}
		if(world.noSkillPoints) {
			write("1;noskillpoints");
			newLine();
		}

		write((System.currentTimeMillis() / 1000) + ";date");
		newLine();

		// keep the game type, when writing a CR.
		writeQuotedTag(world.name, "Spiel");

		if(serverConformance) {
			writeQuotedTag("Standard", "Konfiguration");
		} else {
			writeQuotedTag("Java-Tools", "Konfiguration");
		}

		writeQuotedTag("Hex", "Koordinaten");
		
		// Tracking a bug
		String actGameName = world.name.toLowerCase();
		if ((actGameName.indexOf("eressea")>-1 || actGameName.indexOf("vinyambar")>-1) && (world.base!=36)){
			// this should not happen
			log.warn("BASE ERROR !! report to write could have not base36 !! Changed to base36. (Was " + world.base + ")");
			world.base = 36;
		}
		write(world.base + ";Basis");
		newLine();
		write("1;Umlaute");
		newLine();

		if(!serverConformance && (world.getCurTempID() != -1)) {
			write(world.getCurTempID() + ";curTempID");

			/**
			 * @see com.eressea.GameData#curTempID
			 */
			newLine();
		}

		if(world.getDate() != null) {
			write(world.getDate().getDate() + ";Runde");
			newLine();
			write(((EresseaDate) world.getDate()).getEpoch() + ";Zeitalter");
			newLine();
		}

		if(world.mailTo != null) {
			writeQuotedTag(world.mailTo, "mailto");
		}

		if(world.mailSubject != null) {
			writeQuotedTag(world.mailSubject, "mailcmd");
		}
	}

	/**
	 * Write a spells (ZAUBER) block to the underlying stream.
	 *
	 * @param map a map containing the spells to write. The keys are expected to be
	 * 		  <tt>Integer</tt> objects containing the ids of the spells. The values are expected
	 * 		  to be instances of class <tt>Spell</tt>.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeSpells(Map map) throws IOException {
		if(map == null) {
			return;
		}

		for(Iterator iter = map.values().iterator(); iter.hasNext();) {
			write((Spell) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Spell</tt> object to the underlying stream.
	 *
	 * @param spell TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(Spell spell) throws IOException {
		if(spell.getBlockID() == -1) {
			return;
		}

		write("ZAUBER " + spell.getBlockID());
		newLine();

		if(spell.getName() != null) {
			writeQuotedTag(spell.getName(), "name");
		}

		write(spell.getLevel() + ";level");
		newLine();
		write(spell.getRank() + ";rank");
		newLine();

		if(spell.getDescription() != null) {
			writeQuotedTag(spell.getDescription(), "info");
		}

		if(spell.getType() != null) {
			writeQuotedTag(spell.getType(), "class");
		}

		if(spell.getOnOcean()) {
			write("1;ocean");
			newLine();
		}

		if(spell.getOnShip()) {
			write("1;ship");
			newLine();
		}

		if(spell.getIsFar()) {
			write("1;far");
			newLine();
		}

		if(spell.getIsFamiliar()) {
			write("1;familiar");
			newLine();
		}

		if (spell.getSyntax()!=null){
			writeQuotedTag(spell.getSyntax(),"syntax");
		}
		
		
		writeSpellComponents(spell.getComponents());
	}

	/**
	 * Write a sequence of potion (TRANK) blocks to the underlying stream.
	 *
	 * @param map a map containing the potions to write. The keys are expected to be
	 * 		  <tt>Integer</tt> objects containing the ids of the potions. The values are expected
	 * 		  to be instances of class <tt>Potion</tt>.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writePotions(Map map) throws IOException {
		if(map == null) {
			return;
		}

		for(Iterator iter = map.values().iterator(); iter.hasNext();) {
			write((Potion) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Potion</tt> object to the underlying stream.
	 *
	 * @param potion TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(Potion potion) throws IOException {
		write("TRANK " + potion.getID().toString());
		newLine();

		if(potion.getName() != null) {
			writeQuotedTag(potion.getName(), "Name");
		}

		write(potion.getLevel() + ";Stufe");
		newLine();
		writeQuotedTag(potion.getDescription(), "Beschr");
		writePotionIngredients(potion.ingredients());
	}

	/**
	 * Writes the ingredients of a potion as a ZUTATEN block to the underlying stream.
	 *
	 * @param ingredients a collection containing Item objects.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writePotionIngredients(Collection ingredients) throws IOException {
		if(!ingredients.isEmpty()) {
			write("ZUTATEN");
			newLine();

			for(Iterator iter = ingredients.iterator(); iter.hasNext();) {
				Item i = (Item) iter.next();
				writeQuotedString(i.getItemType().getID().toString());
			}
		}
	}

	/**
	 * Write a spell components (KOMPONENTEN) block to the underyling. The block name is only
	 * written, if there are components in <tt>comps</tt>.
	 *
	 * @param comps a map containing the components to be written. The map is expected to contain
	 * 		  the names of the components as keys and the component data as values (both
	 * 		  as<tt>String</tt> objects). Such a map can be found in the <tt>Spell</tt> class.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 *
	 * @see com.eressea.Spell
	 */
	public void writeSpellComponents(Map comps) throws IOException {
		if(comps == null) {
			return;
		}

		Iterator iter = comps.keySet().iterator();

		if(iter.hasNext()) {
			write("KOMPONENTEN");
			newLine();
		}

		while(iter.hasNext()) {
			String key = (String) iter.next();
			String value = (String) comps.get(key);

			try {
				Integer.parseInt(value);
				write(value + ";" + key);
				newLine();
			} catch(NumberFormatException e) {
				writeQuotedTag(value, key);
			}
		}
	}

	/**
	 * Write the cr representation of a <tt>Option</tt> object to the underlying stream.
	 *
	 * @param options TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(Options options) throws IOException {
		write("OPTIONEN");
		newLine();

		for(Iterator iter = options.options().iterator(); iter.hasNext();) {
			OptionCategory o = (OptionCategory) iter.next();
			write((o.isActive() ? "1" : "0") + ";" + o.getID().toString());
			newLine();
		}
	}

	/**
	 * Write a sequence of group (GRUPPE) blocks to the underlying stream.
	 *
	 * @param map a map containing the groups to write. The keys are expected to be
	 * 		  <tt>Integer</tt> objects containing the ids of the alliances. The values are
	 * 		  expected to be instances of class <tt>Group</tt>.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeGroups(Map map) throws IOException {
		if(map == null) {
			return;
		}

		for(Iterator i = map.values().iterator(); i.hasNext();) {
			writeGroup((Group) i.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Group</tt> object to the underlying stream.
	 *
	 * @param group TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeGroup(Group group) throws IOException {
		if(group == null) {
			return;
		}

		write("GRUPPE " + group.getID());
		newLine();

		if(group.getName() != null) {
			writeQuotedTag(group.getName(), "name");
		}

		if(group.getRaceNamePrefix() != null) {
			writeQuotedTag(group.getRaceNamePrefix(), "typprefix");
		}

		writeAlliances(group.allies());
	}

	/**
	 * Write a sequence of alliance (ALLIANZ) blocks to the underlying stream.
	 *
	 * @param map a map containing the alliances to write. The keys are expected to be
	 * 		  <tt>Integer</tt> objects containing the ids of the alliances. The values are
	 * 		  expected to be instances of class <tt>Alliance</tt>.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeAlliances(Map map) throws IOException {
		if(map == null) {
			return;
		}

		for(Iterator iter = map.values().iterator(); iter.hasNext();) {
			write((Alliance) iter.next());
		}
	}

	/**
	 * Write the cr representation of an <tt>Alliance</tt> object to the underlying stream.
	 *
	 * @param alliance TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(Alliance alliance) throws IOException {
		if(alliance == null) {
			return;
		}

		Faction f = alliance.getFaction();
		write("ALLIANZ " + ((EntityID) f.getID()).intValue());
		newLine();

		if(f.getName() != null) {
			writeQuotedTag(f.getName(), "Parteiname");
		}

		write(alliance.getState() + ";Status");
		newLine();
	}

	/**
	 * Write a sequence of battle (BATTLE) blocks to the underlying stream.
	 *
	 * @param list a list containing the <tt>Battle</tt> objects to be written
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeBattles(List list) throws IOException {
		if(list == null) {
			return;
		}

		for(Iterator iter = list.iterator(); iter.hasNext();) {
			write((Battle) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Battle</tt> object to the underlying stream.
	 *
	 * @param battle TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(Battle battle) throws IOException {
		if(battle == null) {
			return;
		}

		if(!battle.isBattleSpec()) {
			write("BATTLE " + battle.getID().toString(" "));
		} else {
			write("BATTLESPEC " + battle.getID().toString(" "));
		}

		newLine();
		writeMessages(battle.messages());
	}

	/**
	 * Write a sequence of faction (PARTEI) blocks to the underlying stream.
	 *
	 * @param map a map containing the factions to write. The keys are expected to be
	 * 		  <tt>Integer</tt> objects containing the ids of the factions. The values are expected
	 * 		  to be instances of class <tt>Faction</tt>.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeFactions(Map map) throws IOException {
		if(map == null) {
			return;
		}

		// this assumes that if somebody doesn't write units
		// also factions aren't necessary; maybe this needs further
		// specification
		if(includeUnits) {
		    // write owner first
		    Faction ownerFaction = null;
		    if (map.values().size() > 0)
		      ownerFaction = (Faction) map.values().iterator().next();
		    if (ownerFaction != null)
		      writeFaction(ownerFaction);
		    List sorted = CollectionFactory.createArrayList(map.values());
		    Collections.sort(sorted, sortIndexComparator);

		    // write other factions
		    for (Iterator it = sorted.iterator(); it.hasNext();) {
		    	Faction f = (Faction) it.next();
		    	if (ownerFaction == null || !f.equals(ownerFaction))
		    		writeFaction(f);
		    }
		}
	}

	/**
	 * Write the cr representation of a <tt>Faction</tt> object to the underlying stream.
	 *
	 * @param faction TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeFaction(Faction faction) throws IOException {
		if(((EntityID) faction.getID()).intValue() == -1) {
			return;
		}

		write("PARTEI " + ((EntityID) faction.getID()).intValue());
		newLine();

		//if (faction.password != null) {
		//	writeQuotedTag(faction.password, "Passwort");
		//}
		if(faction.getLocale() != null) {
			writeQuotedTag(faction.getLocale().toString(), "locale");
		}

		if(faction.options != null) {
			write(faction.options.getBitMap() + ";Optionen");
			newLine();
		}

		if(faction.score != -1) {
			write(faction.score + ";Punkte");
			newLine();
		}

		if(faction.averageScore != -1) {
			write(faction.averageScore + ";Punktedurchschnitt");
			newLine();
		}

		Race race = faction.getRace();

		if(race != null) {
			if(race.toString() != null) {
				writeQuotedTag(race.getID().toString(), "Typ");
			}

			if(race.getRecruitmentCosts() != -1) {
				write(race.getRecruitmentCosts() + ";Rekrutierungskosten");
				newLine();
			}
		}

		if(faction.persons != -1) {
			write(faction.persons + ";Anzahl Personen");
			newLine();
		}

		if(faction.migrants != -1) {
			write(faction.migrants + ";Anzahl Immigranten");
			newLine();
		}
		
		if(faction.heroes != -1) {
			write(faction.heroes + ";heroes");
			newLine();
		}
		
		if(faction.maxHeroes != -1) {
			write(faction.maxHeroes + ";max_Heroes");
			newLine();
		}
		
		if(faction.age != -1) {
			write(faction.age + ";age");
			newLine();
		}
		
		if(faction.maxMigrants != -1) {
			write(faction.maxMigrants + ";Max. Immigranten");
			newLine();
		}

		if(faction.spellSchool != null) {
			writeQuotedTag(faction.spellSchool, "Magiegebiet");
		}

		if(faction.getName() != null) {
			writeQuotedTag(faction.getName(), "Parteiname");
		}

		if(faction.email != null) {
			writeQuotedTag(faction.email, "email");
		}

		if(faction.getDescription() != null) {
			writeQuotedTag(faction.getDescription(), "banner");
		}

		if(faction.getRaceNamePrefix() != null) {
			writeQuotedTag(faction.getRaceNamePrefix(), "typprefix");
		}

		if(!serverConformance && faction.trustLevelSetByUser) {
			write(faction.trustLevel + ";trustlevel");
			newLine();
		}
		
		writeItems(faction.getItems().iterator());

		if(faction.options != null) {
			write(faction.options);
		}

		writeAlliances(faction.allies);
		writeGroups(faction.groups);

		if(includeMessages) {
			writeStringBlock("FEHLER", faction.errors);
			writeMessages(faction.messages);
			writeBattles(faction.battles);

			if(!serverConformance) {
				writeStringBlock("COMMENTS", faction.comments);
			}
		}
	}

	/**
	 * Write a sequence of ship (SCHIFF) blocks to the underlying stream.
	 *
	 * @param ships an iterator containing the<tt>Ship</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeShips(Collection ships) throws IOException {
		if(ships == null) {
			return;
		}

		List sorted = CollectionFactory.createArrayList(ships);
		Collections.sort(sorted, sortIndexComparator);

		for(Iterator iter = sorted.iterator(); iter.hasNext();) {
			writeShip((Ship) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Ship</tt> object to the underlying stream.
	 *
	 * @param ship TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeShip(Ship ship) throws IOException {
		write("SCHIFF " + ((EntityID) ship.getID()).intValue());
		newLine();

		if(ship.getName() != null) {
			writeQuotedTag(ship.getName(), "Name");
		}

		if(ship.getDescription() != null) {
			writeQuotedTag(ship.getDescription(), "Beschr");
		}

		UnitContainerType type = ship.getType();

		if(type != null) {
			writeQuotedTag(type.getID().toString(), "Typ");
		}

		if(ship.damageRatio > 0) {
			write(ship.damageRatio + ";Schaden");
			newLine();
		}

		if(ship.size != -1) {
			write(ship.size + ";Groesse");
			newLine();
		}

		if(ship.shoreId != -1) {
			write(ship.shoreId + ";Kueste");
			newLine();
		}

		if(shallExportUnit(ship.getOwnerUnit())) {
			write(((UnitID) ship.getOwnerUnit().getID()).intValue() + ";Kapitaen");
			newLine();

			if(ship.getOwnerUnit().getFaction() != null) {
				write(((EntityID) ship.getOwnerUnit().getFaction().getID()).intValue() + ";Partei");
				newLine();
			}
		}

		if(ship.cargo != -1) {
			write(ship.cargo + ";cargo");
			newLine();
		}

		if(ship.capacity != -1) {
			write(ship.capacity + ";capacity");
			newLine();
		}
		
		if(ship.deprecatedLoad != -1) {
			write(ship.deprecatedLoad + ";Ladung");
			newLine();
		}

		if(ship.deprecatedCapacity != -1) {
			write(ship.deprecatedCapacity + ";MaxLadung");
			newLine();
		}

		if(includeMessages) {
			writeStringBlock("EFFECTS", ship.effects);

			if(!serverConformance) {
				writeStringBlock("COMMENTS", ship.comments);
			}
		}
	}

	/**
	 * Write a sequence of building (BURG) blocks to the underlying stream.
	 *
	 * @param buildings an iterator containing the<tt>Building</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeBuildings(Collection buildings) throws IOException {
		if(buildings == null) {
			return;
		}

		List sorted = CollectionFactory.createArrayList(buildings);
		Collections.sort(sorted, sortIndexComparator);

		for(Iterator iter = sorted.iterator(); iter.hasNext();) {
			writeBuilding((Building) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Building</tt> object to the underlying stream.
	 *
	 * @param building TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeBuilding(Building building) throws IOException {
		if(building == null) {
			return;
		}

		UnitContainerType type = building.getType();
		write("BURG " + ((EntityID) building.getID()).intValue());
		newLine();

		if(type != null) {
			writeQuotedTag(type.getID().toString(), "Typ");
		}

		if(building.getName() != null) {
			writeQuotedTag(building.getName(), "Name");
		}

		if(building.getDescription() != null) {
			writeQuotedTag(building.getDescription(), "Beschr");
		}

		if(building.getSize() > 0) {
			write(building.getSize() + ";Groesse");
			newLine();
		}

		if(shallExportUnit(building.getOwnerUnit())) {
			write(((UnitID) building.getOwnerUnit().getID()).intValue() + ";Besitzer");
			newLine();

			if(building.getOwnerUnit().getFaction() != null) {
				write(((EntityID) building.getOwnerUnit().getFaction().getID()).intValue() +
					  ";Partei");
				newLine();
			}
		}

		if(building.getCost() > 0) {
			write(building.getCost() + ";Unterhalt");
			newLine();
		}

		if(includeMessages) {
			writeStringBlock("EFFECTS", building.effects);

			if(!serverConformance) {
				writeStringBlock("COMMENTS", building.comments);
			}
		}
	}

	/**
	 * Write a skills (TALENTE) block to the underlying stream. The block is only written, if
	 * <tt>skills</tt> contains at least one <tt>Skill</tt> object.
	 *
	 * @param skills an iterator over the <tt>Skill</tt> objects to write.
	 * @param persons the number of persons in the unit this skill belongs to.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeSkills(Iterator skills, int persons) throws IOException {
		if(skills.hasNext()) {
			write("TALENTE");
			newLine();
		}

		while(skills.hasNext()) {
			writeSkill((Skill) skills.next(), persons);
		}
	}

	/**
	 * Write the cr representation of a <tt>Skill</tt> object to the underlying stream.
	 *
	 * @param skill TODO: DOCUMENT ME!
	 * @param persons TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeSkill(Skill skill, int persons) throws IOException {
		write(skill.getPoints() + " " + skill.getRealLevel());

		if(!getServerConformance() && skill.isLevelChanged()) {
			write(" " + skill.getChangeLevel());
		}

		write(";" + skill.getSkillType().getID());

		newLine();
	}

	/**
	 * Write a COMMANDS block to the underlying stream. The block is only written, if <tt>list</tt>
	 * contains at least one <tt>String</tt> object representing an order.
	 *
	 * @param list a list with the <tt>String</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeOrders(List list) throws IOException {
		if((list == null) || list.isEmpty()) {
			return;
		}

		write("COMMANDS");
		newLine();

		for(Iterator iter = list.iterator(); iter.hasNext();) {
			writeQuotedString((String) iter.next());
		}
	}

	/**
	 * Write a unit's spell (SPRUECHE) block to the underlying stream. The block is only written,
	 * if <tt>list</tt> contains at least one <tt>Spell</tt> object.
	 *
	 * @param spells a list with the<tt>Spell</tt> object names to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeUnitSpells(Map spells) throws IOException {
		if(spells == null) {
			return;
		}

		Iterator i = spells.values().iterator();

		if(i.hasNext()) {
			write("SPRUECHE");
			newLine();
		}

		while(i.hasNext()) {
			Spell s = (Spell) i.next();
			writeQuotedString(s.getName());
		}
	}

	/**
	 * Write a unit's combat spell (KAMPFZAUBER) blocks to the underlying stream.
	 *
	 * @param map a Map with the <tt>CombatSpell</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeUnitCombatSpells(Map map) throws IOException {
		if(map == null) {
			return;
		}

		Iterator i = map.values().iterator();

		while(i.hasNext()) {
			CombatSpell cs = (CombatSpell) i.next();
			write(cs);
		}
	}

	/**
	 * Write the cr representation of a <tt>CombatSpell</tt> object to the underlying stream.
	 *
	 * @param cs TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(CombatSpell cs) throws IOException {
		if(cs != null) {
			if(cs.getID() != null) {
				write("KAMPFZAUBER " + cs.getID().toString());
				newLine();

				if(cs.getSpell() != null) {
					if(cs.getSpell().getName() != null) {
						writeQuotedTag(cs.getSpell().getName(), "name");
					} else {
						log.warn("CRWriter.write(CombatSpell): warning: spell name is null!");
					}
				} else {
					log.warn("CRWriter.write(CombatSpell): warning: spell is null!");
				}

				write(cs.getCastingLevel() + ";level");
				newLine();
			} else {
				log.warn("CRWriter.write(CombatSpell): warning: combat spell ID is null!");
			}
		} else {
			log.warn("CRWriter.write(CombatSpell): warning: combat spell is null!");
		}
	}

	/**
	 * Write a unit's items (GEGENSTAENDE) block to the underlying stream. The block is only
	 * written, if <tt>items</tt> contains at least one <tt>Item</tt> object.
	 *
	 * @param items an iterator over the <tt>Item</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeItems(Iterator items) throws IOException {
		if(items.hasNext()) {
			write("GEGENSTAENDE");
			newLine();
		}

		while(items.hasNext()) {
			Item item = (Item) items.next();
			write(item);
		}
	}

	/**
	 * Write the cr representation of a <tt>Item</tt> object to the underlying stream.
	 *
	 * @param item TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(Item item) throws IOException {
		write(item.getAmount() + ";" + item.getItemType().getID());
		newLine();
	}

	/**
	 * Write a sequence of unit (EINHEIT) blocks to the underlying stream.
	 *
	 * @param units an iterator for the<tt>Unit</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeUnits(Collection units) throws IOException {
		if(units == null) {
			return;
		}

		List sorted = CollectionFactory.createArrayList(units);
		Collections.sort(sorted, sortIndexComparator);

		for(Iterator iter = sorted.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			writeUnit(u);
		}
	}

	/**
	 * 
	 * @param u the unit to export
	 * @return true iff units == null or empty or units contains u
	 */
	private boolean shallExportUnit(Unit u) {
		return u != null && 
			(units == null || units.isEmpty() || units.contains(u));
	}

	/**
	 * Write the cr representation of a <tt>Unit</tt> object to the underyling
	 *
	 * @param unit TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeUnit(Unit unit) throws IOException {
		if(unit instanceof TempUnit || !shallExportUnit(unit)) {
			return;
		}

		unitsWritten++;
		write("EINHEIT " + ((UnitID) unit.getID()).intValue());
		newLine();

		if(unit.getName() != null) {
			writeQuotedTag(unit.getName(), "Name");
		}

		if(unit.getDescription() != null) {
			writeQuotedTag(unit.getDescription(), "Beschr");
		}

		if(unit.privDesc != null) {
			writeQuotedTag(unit.privDesc, "privat");
		}

		if(unit.getFaction() != null) {
			int id = ((EntityID) unit.getFaction().getID()).intValue();

			if(id != -1) {
				write(id + ";Partei");
				newLine();
			}
		}

		write(unit.persons + ";Anzahl");
		newLine();

		if(unit.race != null) {
			write("\"" + unit.race.getID().toString() + "\";Typ");
			newLine();
		}

		if(unit.getTempID() != null) {
			write(((UnitID) unit.getTempID()).intValue() + ";temp");
			newLine();
		}

		if(unit.getAlias() != null) {
			write(((UnitID) unit.getAlias()).intValue() + ";alias");
			newLine();
		}

		if(unit.combatStatus != -1) {
			write(unit.combatStatus + ";Kampfstatus");
			newLine();
		}

		if(unit.unaided) {
			write("1;unaided");
			newLine();
		}

		if(unit.stealth != -1) {
			write(unit.stealth + ";Tarnung");
			newLine();
		}

		if(unit.realRace != null) {
			write("\"" + unit.realRace + "\";wahrerTyp");
			newLine();
		}

		if(unit.getShip() != null) {
			write(((EntityID) unit.getShip().getID()).intValue() + ";Schiff");
			newLine();
		}

		if(unit.getBuilding() != null) {
			write(((EntityID) unit.getBuilding().getID()).intValue() + ";Burg");
			newLine();
		}

		// since CR Version 51 Silber is an normal item

		/*int silver = unit.getSilver();
		 if (silver > 0) {
		 write(silver + ";Silber");
		 newLine();
		 }*/
		if(unit.hideFaction) {
			write("1;Parteitarnung");
			newLine();
		}

		if(shallExportUnit(unit.follows)) {
			write(((UnitID) unit.follows.getID()).intValue() + ";folgt");
			newLine();
		}

		
		
		
		if(unit.guard != 0) {
			write(unit.guard + ";bewacht");
			newLine();
		}

		if(unit.aura != -1) {
			write(unit.aura + ";Aura");
			newLine();
		}

		if(unit.auraMax != -1) {
			write(unit.auraMax + ";Auramax");
			newLine();
		}

		if(unit.health != null) {
			writeQuotedTag(unit.health, "hp");
		}

		if(unit.isHero) {
			write("1;hero");
			newLine();
		}

		if(unit.isStarving) {
			write("1;hunger");
			newLine();
		}

		if(!serverConformance && unit.ordersConfirmed) {
			write("1;ejcOrdersConfirmed");
			newLine();
		}

		if(unit.getGroup() != null) {
			write(unit.getGroup().getID() + ";gruppe");
			newLine();
		}

		if(unit.isSpy()) {
			write("1;Verraeter");
			newLine();
		}

		if(unit.getGuiseFaction() != null) {
			// write(((IntegerID) unit.getGuiseFaction().getID()).intValue() + ";Verkleidung");
			// Anderepartei
			write(((IntegerID) unit.getGuiseFaction().getID()).intValue() + ";Anderepartei");
			newLine();
		}

		if(unit.weight != -1) {
			write(unit.weight+";weight");
			newLine();
		}
		
		//  fiete: familiarmage
		if (unit.familiarmageID!=null) {
			IntegerID iID = (IntegerID) unit.familiarmageID;
			write(iID.intValue() + ";familiarmage");
			newLine();
		}
		
		
		if(unit.getRaceNamePrefix() != null) {
			writeQuotedTag(unit.getRaceNamePrefix(), "typprefix");
		}

		if(unit.hasTags()) {
			java.util.Map map = unit.getTagMap();
			java.util.Iterator it = map.keySet().iterator();

			while(it.hasNext()) {
				Object key = it.next();
				Object value = map.get(key);

				try {
					Integer.parseInt(value.toString());
					write(value + ";" + key);
					newLine();
				} catch(NumberFormatException e) {
					writeQuotedTag(value.toString(), key.toString());
				}
			}
		}

		if(includeMessages) {
			writeStringBlock("EFFECTS", unit.effects);
			writeMessageBlock("EINHEITSBOTSCHAFTEN", unit.unitMessages);
			if(!serverConformance) {
				writeStringBlock("COMMENTS", unit.comments);
			}
		}
		

		//
		//writeOrders(unit.orders);
		//writeStringSequence(unit.getTempOrders());
		writeOrders(unit.getCompleteOrders());
		writeSkills(unit.getSkills().iterator(), unit.persons);
		writeUnitSpells(unit.spells);
		writeUnitCombatSpells(unit.combatSpells);
		writeItems(unit.getItems().iterator());
	}

	/**
	 * Write a region prices (PREISE) block to the underlying stream.
	 *
	 * @param map list containing the<tt>LuxuryPrice</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writePrices(Map map) throws IOException {
		if(map == null) {
			return;
		}

		Iterator i = map.values().iterator();

		if(i.hasNext()) {
			write("PREISE");
			newLine();
		}

		while(i.hasNext()) {
			write((LuxuryPrice) i.next());
		}
	}

	/**
	 * Write region block containing the luxury prices of the last turn (LETZTEPREISE) to the
	 * underlying stream.
	 *
	 * @param map a map containing the <tt>LuxuryPrice</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeOldPrices(Map map) throws IOException {
		if(map == null) {
			return;
		}

		Iterator i = map.values().iterator();

		if(i.hasNext()) {
			write("LETZTEPREISE");
			newLine();
		}

		while(i.hasNext()) {
			write((LuxuryPrice) i.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>LuxuryPrice</tt> object to the underlying stream.
	 *
	 * @param price TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(LuxuryPrice price) throws IOException {
		write(price.getPrice() + ";" + price.getItemType().getID().toString());
		newLine();
	}

	/**
	 * Write a sequence of region border (GRENZE) blocks to the underlying stream.
	 *
	 * @param c collection containing the <tt>Border</tt> objects to be written.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeBorders(Collection c) throws IOException {
		if(c == null) {
			return;
		}

		Iterator i = c.iterator();

		while(i.hasNext()) {
			writeBorder((Border) i.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Border</tt> object to the underlying stream.
	 *
	 * @param border TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeBorder(Border border) throws IOException {
		write("GRENZE " + border.getID());
		newLine();
		writeQuotedTag(border.type, "typ");
		write(border.direction + ";richtung");
		newLine();
		write(border.buildRatio + ";prozent");
		newLine();
	}

	/**
	 * Write a sequence of region blocks to the underlying stream.
	 *
	 * @param map a map containing the region to write. The keys are expected to be
	 * 		  <tt>Integer</tt> objects containing the ids of the regions. The values are expected
	 * 		  to be instances of class <tt>Region</tt>.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeRegions(Map map) throws IOException {
		if(map == null) {
			return;
		}

		writeRegions(map.values());
	}

	/**
	 * Write a sequence of region (REGION) blocks to the underlying stream.
	 *
	 * @param regions a collection containing the regions to write.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeRegions(Collection regions) throws IOException {
		if(regions == null) {
			return;
		}

		for(Iterator iter = regions.iterator(); iter.hasNext();) {
			writeRegion((Region) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>Region</tt> object to the underlying stream.
	 *
	 * @param region TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeRegion(Region region) throws IOException {
		
		// Fiete 20070117
		// Exception: Magellan-added Regions to show TheVoid
		// these regions should not be written
		if (region.getRegionType().equals(data.rules.getRegionType("Leere"))){
			return;
		}
		
		
		write("REGION " + region.getID().toString(" "));
		newLine();

		// Fiete: starting in round 570 we can have region.UID within
		// eressea, coming from the server.
		// if UID is known, write it now
		// UID=0 reserved for no UID.
		if (region.getUID()!=0){
		   // first example was quoted
		   // writeQuotedTag(region.getUID() + "", "id");
		   // finally we use not quoted IDs
		  write(region.getUID()+ ";id");
		  newLine();
		}
		
		
		UnitContainerType type = region.getType();

		if((region.getName() != null) && !region.getName().equals("")) {
			// write name only if it differs from type
			if(type != null) {
				String strRegion = Umlaut.normalize(region.getName());
				String strType = Umlaut.normalize(type.toString());

				if(!strRegion.equalsIgnoreCase(strType)) {
					writeQuotedTag(region.getName(), "Name");
				}
			}
		}

		if(type != null) {
			writeQuotedTag(type.getID().toString(), "Terrain");
		}

		if(region.getDescription() != null) {
			writeQuotedTag(region.getDescription(), "Beschr");
		}

		if(includeIslands && !serverConformance && (region.getIsland() != null)) {
			writeQuotedTag(region.getIsland().getID().toString(), "Insel");
		}

		if(!serverConformance && region.getData().getSelectedRegionCoordinates().containsKey(region.getID())) {
			write("1;ejcIsSelected");
			newLine();
		}

		if(region.hasTags()) {
			java.util.Map map = region.getTagMap();
			java.util.Iterator it = map.keySet().iterator();

			while(it.hasNext()) {
				Object key = it.next();
				Object value = map.get(key);

				try {
					Integer.parseInt(value.toString());
					write(value + ";" + key);
					newLine();
				} catch(NumberFormatException e) {
					writeQuotedTag(value.toString(), key.toString());
				}
			}
		}

		if(includeRegionDetails) {
			if(region.trees > 0) {
				write(region.trees + ";Baeume");
				newLine();
			}

			if(region.mallorn) {
				write("1;Mallorn");
				newLine();
			}

			if(!serverConformance && (region.oldTrees > -1)) {
				write(region.oldTrees + ";letztebaeume");
				newLine();
			}

			if(region.sprouts > 0) {
				write(region.sprouts + ";Schoesslinge");
				newLine();
			}

			if(!serverConformance && (region.oldSprouts > -1)) {
				write(region.oldSprouts + ";letzteSchoesslinge");
				newLine();
			}

			if(region.peasants > 0) {
				write(region.peasants + ";Bauern");
				newLine();
			}

			if(!serverConformance && (region.oldPeasants > -1)) {
				write(region.oldPeasants + ";letztebauern");
				newLine();
			}

			if(region.horses > 0) {
				write(region.horses + ";Pferde");
				newLine();
			}

			if(!serverConformance && (region.oldHorses > -1)) {
				write(region.oldHorses + ";letztepferde");
				newLine();
			}

			if(region.silver > 0) {
				write(region.silver + ";Silber");
				newLine();
			}

			if(!serverConformance && (region.oldSilver > -1)) {
				write(region.oldSilver + ";letztessilber");
				newLine();
			}

			if(region.maxEntertain() > 0) {
				write(region.maxEntertain() + ";Unterh");
				newLine();
			}

			if(region.maxRecruit() > 0) {
				write(region.maxRecruit() + ";Rekruten");
				newLine();
			}

			// pavkovic 2002.05.10: recruits (and old recruits are used from cr)
			if(!serverConformance && (region.maxOldRecruit() > -1)) {
				write(region.maxOldRecruit() + ";letzterekruten");
				newLine();
			}

			if(region.wage > 0) {
				if(includeBuildings) {
					write(region.wage + ";Lohn");
				} else {
					write("10;Lohn");
				}

				newLine();
			}

			if(includeBuildings && !serverConformance && (region.oldWage > -1)) {
				write(region.oldWage + ";letzterlohn");
				newLine();
			}

			if(region.iron > 0) {
				write(region.iron + ";Eisen");
				newLine();
			}

			if(!serverConformance && (region.oldIron > -1)) {
				write(region.oldIron + ";letzteseisen");
				newLine();
			}

			if(region.laen > 0) {
				write(region.laen + ";Laen");
				newLine();
			}

			if(!serverConformance && (region.oldLaen > -1)) {
				write(region.oldLaen + ";letzteslaen");
				newLine();
			}

			if(region.stones > 0) {
				write(region.stones + ";Steine");
				newLine();
			}

			if(!serverConformance && (region.oldStones > -1)) {
				write(region.oldStones + ";letztesteine");
				newLine();
			}

			if((region.herb != null) && !serverConformance) {
				writeQuotedTag(region.herb.getID().toString(), "herb");
			}

			if((region.herbAmount != null) && !serverConformance) {
				writeQuotedTag(region.herbAmount, "herbamount");
			}

			if(region.orcInfested) {
				write("1;Verorkt");
				newLine();
			}

			if(region.getVisibility() != null) {
				writeQuotedTag(region.getVisibility(), "visibility");
			}

			writeRegionResources(region.resources());
			writePrices(region.prices);

			if(!serverConformance && (region.oldPrices != null)) {
				writeOldPrices(region.oldPrices);
			}
			
			if(!serverConformance && (region.getSigns()!=null)) {
				writeSigns(region.getSigns());
			}

			writeBorders(region.borders());

			if(includeMessages) {
				writeStringBlock("EFFECTS", region.effects);

				if(!serverConformance) {
					writeStringBlock("COMMENTS", region.comments);
				}

				writeMessageBlock("REGIONSEREIGNISSE", region.events);

				//writeMessageBlock("REGIONSKOMMENTAR", region.comments);
				writeMessageBlock("REGIONSBOTSCHAFTEN", region.playerMessages);
				writeMessageBlock("UMGEBUNG", region.surroundings);
				writeMessageBlock("DURCHREISE", region.travelThru);
				writeMessageBlock("DURCHSCHIFFUNG", region.travelThruShips);
				writeMessages(region.messages);
			}
		}

		writeSchemes(region.schemes());

		if(includeBuildings) {
			writeBuildings(region.buildings());
		}

		if(includeShips) {
			writeShips(region.ships());
		}

		if(includeUnits) {
			writeUnits(region.units());
		}
	}

	/**
	 * Write a collection of signs to the underlying stream
	 *
	 * @param signs Collection of signs
	 *
	 * @throws IOException passes a IOException from streamwriter
	 */
	private void writeSigns(Collection signs) throws IOException {
		if (signs == null || signs.isEmpty()){
			return;
		}
		int counter = 1;
		for (Iterator iter = signs.iterator();iter.hasNext();){
			writeSign((Sign)iter.next(),counter);
			counter++;
		}
	}
	
	/**
	 * Write a presentation of a sign to the underlying stream
	 *
	 * @param sign the sign
	 * @param int counter  just a counter for IDing the sign
	 *
	 * @throws IOException passes a IOException from streamwriter
	 */
	private void writeSign(Sign s, int counter) throws IOException {
		write("SIGN " + counter);
		newLine();
		writeQuotedTag(s.getText(), "text");
	}
	
	/**
	 * Write a collection of schemes to the underlying stream
	 *
	 * @param schemes TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeSchemes(Collection schemes) throws IOException {
		if((schemes == null) || schemes.isEmpty()) {
			return;
		}

		for(Iterator iter = schemes.iterator(); iter.hasNext();) {
			writeScheme((Scheme) iter.next());
		}
	}

	/**
	 * Writes the cr representation of a Scheme object to the underlying stream.
	 *
	 * @param scheme TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeScheme(Scheme scheme) throws IOException {
		write("SCHEMEN " + scheme.getID().toString(" "));
		newLine();

		if(scheme.getName() != null) {
			writeQuotedTag(scheme.getName(), "Name");
		}
	}

	/**
	 * Write a collection of region resources to the underlying stream
	 *
	 * @param resources TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeRegionResources(Collection resources) throws IOException {
		if((resources == null) || resources.isEmpty()) {
			return;
		}

		for(Iterator iter = resources.iterator(); iter.hasNext();) {
			writeRegionResource((RegionResource) iter.next());
		}
	}

	/**
	 * Writes the cr representation of a region resource object to the underlying stream.
	 *
	 * @param res TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeRegionResource(RegionResource res) throws IOException {
		write("RESOURCE " + res.getID().toString());
		newLine();
		writeQuotedTag(res.getType().getID().toString(), "type");

		if(res.getAmount() > -1) {
			write(res.getAmount() + ";number");
			newLine();
		}

		if(res.getSkillLevel() > -1) {
			write(res.getSkillLevel() + ";skill");
			newLine();
		}
	}

	/**
	 * Write message type blocks to the underlying stream.
	 *
	 * @param map a map containing the <tt>MessageType</tt> objects to be written as values.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeMsgTypes(Map map) throws IOException {
		if(map == null) {
			return;
		}

		for(Iterator iter = map.values().iterator(); iter.hasNext();) {
			writeMessageType((MessageType) iter.next());
		}
	}

	/**
	 * Write the cr representation of a <tt>MessageType</tt> object to the underlying stream.
	 *
	 * @param msgType TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeMessageType(MessageType msgType) throws IOException {
		if(msgType == null) {
			log.warn("CRWriter.writeMessageType(): argument msgType is null");

			return;
		}

		if((msgType.getID() == null) || (((IntegerID) msgType.getID()).intValue() < 0)) {
			log.warn("CRWriter.writeMessageType(): invalid ID");

			return;
		}

		if(msgType.getPattern() == null) {
			log.warn("CRWriter.writeMessageType(): pattern of message type " + msgType.getID() +
					 " is null");

			return;
		}

		write("MESSAGETYPE " + msgType.getID().toString());
		newLine();
		writeQuotedTag(msgType.getPattern(), "text");

		if(msgType.getSection() != null) {
			writeQuotedTag(msgType.getSection(), "section");
		}
	}

	/**
	 * Write the complete game data from <tt>world</tt> in the cr format.
	 *
	 * @param world the game data to write.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 * @throws NullPointerException TODO: DOCUMENT ME!
	 */
	public void write(GameData world) throws IOException, NullPointerException {
		if(world == null) {
			throw new NullPointerException("CRWriter.write(GameData): argument world is null");
		}

		this.data = world;
		
		writeVersion(world);

		if(!serverConformance) {
			writeHotSpots(world.hotSpots());
		}

		writeFactions(world.factions());

		if(includeSpellsAndPotions) {
			writeSpells(world.spells());
			writePotions(world.potions());
		}

		if(!serverConformance && includeIslands) {
			writeIslands(world.islands());
		}

		if(includeRegions) {
			if((regions != null) && (regions.size() > 0)) {
				writeRegions(regions);
			} else {
				writeRegions(world.regions());
			}
		}

		if(includeMessages) {
			writeMsgTypes(world.msgTypes());
		}

		writeTranslations(world.translations());

		if(includeRegions && includeUnits && ((regions == null) || (regions.size() == 0))) {
			if(world.units() != null) {
				if(world.units().size() != unitsWritten) {
					int homelessUnitsCounter = 0;

					for(Iterator iter = world.units().values().iterator(); iter.hasNext();) {
						Unit u = (Unit) iter.next();

						if(u.getRegion() == null) {
							homelessUnitsCounter++;
						}
					}

					if((world.units().size() - homelessUnitsCounter) != unitsWritten) {
						throw new IOException("Although there are " +
											  (world.units().size() - homelessUnitsCounter) +
											  " units, only " + unitsWritten + " were written!");
					}
				}
			}
		}
	}

	/**
	 * Change the quote escape behaviour of this CRWriter. Tilde escapes look like: 'a "b c"' -> 
     * 'a b~c', whereas a backslash escape works like this: 'a "b c"' -> 'a \"b c\"'
	 *
	 * @param bool if <tt>true</tt>, escape quoted parts of any string written to the underlying
	 * 		  stream with tildes. If <tt>false</tt>, use backslash character to escape the
	 * 		  quotation marks themselves.
	 */
	public void setTildeEscapes(boolean bool) {
		useTildesForQuotes = true;
	}

	/**
	 * Creates a CR writer with a default-sized ouput buffer.
	 *
	 * @param out the stream to write output to.
	 */
	public CRWriter(Writer out) {
		super(out);
	}

	/**
	 * Creates a CR writer with a ouput buffer of the specified size.
	 *
	 * @param out the stream to write output to.
	 * @param sz the size of the output buffer.
	 */
	public CRWriter(Writer out, int sz) {
		super(out, sz);
	}

	/**
	 * Creates a CR writer with a ouput buffer of the specified size.
	 *
	 * @param fileType the filetype to write to
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public CRWriter(FileType fileType, String encoding) throws IOException {
		super(fileType.createWriter(encoding));
		this.encoding = encoding;
	}

	private boolean includeRegions = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes information about the regions in data
	 * to the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeRegions() {
		return this.includeRegions;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes information about the regions in data
	 * to the underlying stream.
	 *
	 * @param includeRegions TODO: DOCUMENT ME!
	 */
	public void setIncludeRegions(boolean includeRegions) {
		this.includeRegions = includeRegions;
	}

	private boolean includeBuildings = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes information about the buildings in data
	 * to the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeBuildings() {
		return this.includeBuildings;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes information about the buildings in data
	 * to the underlying stream.
	 *
	 * @param includeBuildings TODO: DOCUMENT ME!
	 */
	public void setIncludeBuildings(boolean includeBuildings) {
		this.includeBuildings = includeBuildings;
	}

	private boolean includeShips = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes information about the ships in data to
	 * the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeShips() {
		return this.includeShips;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes information about the ships in data to
	 * the underlying stream.
	 *
	 * @param includeShips TODO: DOCUMENT ME!
	 */
	public void setIncludeShips(boolean includeShips) {
		this.includeShips = includeShips;
	}

	private boolean includeUnits = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes information about the units in data to
	 * the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeUnits() {
		return this.includeUnits;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes information about the units in data to
	 * the underlying stream.
	 *
	 * @param includeUnits TODO: DOCUMENT ME!
	 */
	public void setIncludeUnits(boolean includeUnits) {
		this.includeUnits = includeUnits;
	}

	private boolean includeRegionDetails = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes detailed information about the regions
	 * in data to the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeRegionDetails() {
		return this.includeRegionDetails;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes detailed information about the regions
	 * in data to the underlying stream.
	 *
	 * @param includeRegionDetails TODO: DOCUMENT ME!
	 */
	public void setIncludeRegionDetails(boolean includeRegionDetails) {
		this.includeRegionDetails = includeRegionDetails;
	}

	private boolean includeIslands = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes information about islands to the
	 * underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeIslands() {
		return this.includeIslands;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes information about islands to the
	 * underlying stream.
	 *
	 * @param includeIslands TODO: DOCUMENT ME!
	 */
	public void setIncludeIslands(boolean includeIslands) {
		this.includeIslands = includeIslands;
	}

	private boolean includeMessages = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes messages contained in the game data to
	 * the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeMessages() {
		return this.includeMessages;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes messages contained in the game data to
	 * the underlying stream.
	 *
	 * @param includeMessages TODO: DOCUMENT ME!
	 */
	public void setIncludeMessages(boolean includeMessages) {
		this.includeMessages = includeMessages;
	}

	private boolean includeSpellsAndPotions = true;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes messages contained in the game data to
	 * the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIncludeSpellsAndPotions() {
		return this.includeSpellsAndPotions;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes messages contained in the game data to
	 * the underlying stream.
	 *
	 * @param includeSpellsAndPotions TODO: DOCUMENT ME!
	 */
	public void setIncludeSpellsAndPotions(boolean includeSpellsAndPotions) {
		this.includeSpellsAndPotions = includeSpellsAndPotions;
	}

	private boolean serverConformance = false;

	/**
	 * Returns whether <tt>write(GameData data)</tt> writes a cr that is compatible with cr's
	 * generated by the Eressea server, i.e. not including JavaClient specific data.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getServerConformance() {
		return this.serverConformance;
	}

	/**
	 * Toggles whether <tt>write(GameData data)</tt> writes a cr that is compatible with cr's
	 * generated by the Eressea server, i.e. not including JavaClient specific data.
	 *
	 * @param serverConformance TODO: DOCUMENT ME!
	 */
	public void setServerConformance(boolean serverConformance) {
		this.serverConformance = serverConformance;
	}

	/**
	 * Write a sequence of island blocks to the underlying stream.
	 *
	 * @param map a map containing the islands to write. The keys are expected to be
	 * 		  <tt>Integer</tt> objects containing the ids of the islands. The values are expected
	 * 		  to be instances of class <tt>Island</tt>.
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeIslands(Map map) throws IOException {
		if(map == null) {
			return;
		}

		for(Iterator iter = map.values().iterator(); iter.hasNext();) {
			write((Island) iter.next());
		}
	}

	/**
	 * Write the cr representation of an <tt>Island</tt> object to the underlying stream.
	 *
	 * @param island TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(Island island) throws IOException {
		if(island == null) {
			return;
		}

		write("ISLAND " + island.getID());
		newLine();

		if(island.getName() != null) {
			writeQuotedTag(island.getName(), "name");
		}

		if(island.getDescription() != null) {
			writeQuotedTag(island.getDescription(), "Beschr");
		}
	}

	/**
	 * Write a sequence of hot spot blocks to the underlying stream.
	 *
	 * @param hotSpots TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeHotSpots(Map hotSpots) throws IOException {
		if(hotSpots == null) {
			return;
		}

		for(Iterator iter = hotSpots.values().iterator(); iter.hasNext();) {
			write((HotSpot) iter.next());
		}
	}

	/**
	 * Write the cr representation of a hot spot to the underlying stream.
	 *
	 * @param h TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void write(HotSpot h) throws IOException {
		if(h == null) {
			return;
		}

		write("HOTSPOT " + h.getID().toString(" "));
		newLine();
		writeQuotedTag(h.getName(), "name");
		writeQuotedTag(h.getCenter().toString(" "), "coord");
	}

	private Collection regions = null;

	/**
	 * Returns the regions this object writes to the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getRegions() {
		return this.regions;
	}

	/**
	 * Supply the writer with a collection of regions it should write to the underlying stream
	 * instead of all regions contained in the game data. If regions is null or if there is no
	 * element in the supplied collection, the writer returns to writing all regions defined in
	 * the game data.
	 *
	 * @param regions TODO: DOCUMENT ME!
	 */
	public void setRegions(Collection regions) {
		this.regions = regions;
	}


	private Collection units = null;

	/**
	 * Returns the units this object writes to the underlying stream.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getUnits() {
		return this.units;
	}

	/**
	 * Supply the writer with a collection of units it should write to the underlying stream
	 * instead of all units contained in the game data. If units is null or if there is no
	 * element in the supplied collection, the writer returns to writing all units defined in
	 * the game data.
	 *
	 * @param units TODO: DOCUMENT ME!
	 */
	public void setUnits(Collection units) {
		this.units = units;
	}

	/**
	 * Write the translation table to underlying stream.
	 *
	 * @param m TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public void writeTranslations(Map m) throws IOException {
		if((m == null) || (m.size() == 0)) {
			return;
		}

		write("TRANSLATION");
		newLine();

		List sorted = CollectionFactory.createArrayList(m.keySet());
		Collections.sort(sorted);

		for(Iterator iter = sorted.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			String value = (String) m.get(key);
			writeQuotedTag(value, key);
		}
	}
}
