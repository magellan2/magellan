// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Martin Hershoff, Sebastian Pappert,
//							Klaas Prause,  Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.crtonr;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eressea.Building;
import com.eressea.CombatSpell;
import com.eressea.Coordinate;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.IntegerID;
import com.eressea.Item;
import com.eressea.LuxuryPrice;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.RegionResource;
import com.eressea.Ship;
import com.eressea.Skill;
import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.rules.Race;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

public class RegionWriter
{
	private final static Logger log = Logger.getInstance(RegionWriter.class);
	Region region;
	Faction myFac;
	PrintStream out;
	GameData m_data;
	
	static String[] terrain = {"Ozean", "Ebene", "Wüste",
		                       "Hochland", "Berge", "Sumpf",
		                       "Wald", "Ozean", "Gletscher",
							   "Eisberg", "Bergland", "Vulkan"};
	
	static char[] gender = {'m', 'w', 'w', 
		                    's', 'w', 'm', 
		                    'm', ' ', 'm',
							'm', 's', 'm'};
	
	static String[] shore = {"Nordwest", "Nordost", "Ost", 
							 "Südost", "Südwest", "West"};
	
		static String[] nouns = {"Elf", "Elfen",
		"Katze",				"Katzen",
		"Insekt", 				"Insekten",
		"Halbling", 			"Halblinge",
		"Zwerg", 				"Zwerge",
		"Ork",					"Orks",
		"Troll",				"Trolle",
		"Goblin",				"Goblins",
		"Dämon",				"Dämonen",
		"Mensch",				"Menschen",
		"Meermensch",			"Meermenschen",
		"Dracoid",				"Dracoide",
		"Riesenschildkröte",	"Riesenschildkröten",
		"Luchs",				"Luchse",
		"Eule",					"Eulen",
		"Wyrm",					"Wyrme",
		
		"Bogen", 				"Bögen",
		"Laenkettenhemd",		"Laenkettenhemden",
		"Laenschild",			"Laenschilde",
		"Wagen", 				"Wagen",
		"Katapult", 			"Katapulte",
		"Armbrust",				"Armbrüste",
		"Kriegsaxt",			"Kriegsäxte",
		"Kettenhemd",			"Kettenhemden",
		"Bihänder",				"Bihänder",
		"Lanze",				"Lanzen",
		
		"Würziger Wagemut",		"Würzige Wagemut",
		"Eisblume",				"Eisblumen",
		"Weißer Wüterich",		"Weiße Wüteriche",
		"Eulenauge",			"Eulenaugen",
		"Windbeutel",			"Windbeutel",
		"Fjordwuchs",			"Fjordwuchse",
		"Alraune",				"Alraunen",
		"Steinbeißer",			"Steinbeißer",
		"Flachwurz",			"Flachwurz",
		"Schneekristall", 		"Schneekristalle",
		"Grüner Spinnerich", 	"Grüne Spinneriche",
		"Gurgelkraut",			"Gurgelkräuter",
		"Spaltwachs",			"Spaltwachse",
		"Knotiger Saugwurz",	"Knotige Saugwurze",
		"Höhlenglimm",			"Höhlenglimme",
		"Wasserfinder",			"Wasserfinder",
		"Sandfäule",			"Sandfäulen",
		"Blasenmorchel",		"Blasenmorcheln",
		"Blauer Baumringel",	"Blaue Baumringel",
		"Elfenlieb",			"Elfenlieb",
		
		"Hellebarde",			"Hellebarden",
		"Elfenbogen",			"Elfenbögen",
		"Laenschwert",			"Laenschwerter",
		"Flammenschwert",		"Flammenschwerter",
		"Pferd",				"Pferde",
		"Seide",				"Seide",
		"Weihrauch",			"Weihrauch",
		"Juwel",				"Juwelen",
		"Eisen",				"Eisen",
		
		"Holz",					"Holz",
		"Mallorn",				"Mallorn",
		"Laen",					"Laen",
		"Silber",				"Silber",
		"Plattenpanzer",		"Plattenpanzer",
		"Rostiges Kettenhemd",	"Rostige Kettenhemden",
		"Schartiges Schwert",	"Schartige Schwerter",
		"Schild",				"Schilde",
		"Öl",					"Öl",
		"Gehirnschmalz",		"Gehirnschmalz",
		"Speer", 				"Speere",
		"Stein",				"Steine",
		"Schwert",              "Schwerter",
		"Mallornspeer",			"Mallornspeere",
		
		"Myrrhe",				"Myrrhe",
		"Gewürz",				"Gewürze",
		"Balsam",				"Balsam",
		
		"Phiole",				"Phiolen",
		"Drachenblut",			"Drachenblut",
		"Heiltrank",			"Heiltränke",
		"Schaffenstrunk",		"Schaffenstrünke",
		"Bauernblut",			"Bauernblut",
		"Gehirnschmalz",		"Gehirnschmalz",
		"Antimagiekristall",	"Antimagiekristalle",
		"Amulett",				"Amulette",
		"Amulett der Heilung",	"Amuletet der Heilung",
		"Amulett des wahren Sehens", "Amulette des wahren Sehens",
		"Amulett der Keuschheit", "Amulette der Keuschheit"
	};
	
	
	static String[] luxuries = {"Balsam", "Gewürz", "Juwel", "Myrrhe", "Öl",
								  "Seide", "Weihrauch"};
	
	static Map singularMap = CollectionFactory.createHashMap();
	
	static Map pluralMap   = CollectionFactory.createHashMap();
	
	static Map genderMap   = CollectionFactory.createHashMap();
	
	static {
		for (int i=0;i<nouns.length;i+=2)
		{
			singularMap.put(nouns[i+1], nouns[i]);
			pluralMap.put(nouns[i], nouns[i+1]);
		}
		for (int i=0;i<terrain.length;i++)
		{
			genderMap.put(terrain[i], new Character(gender[i]));
		}
	}
	
	static String getSingular(String plural)
	{
		Object retval = singularMap.get(plural);
		if (retval == null)
			retval = plural;
		return retval.toString();
	}
	
	static String getPlural(String singular)
	{
		Object retval = pluralMap.get(singular);
		if (retval == null)
			retval = singular;
		return retval.toString();
	}

	
	public RegionWriter(GameData data, PrintStream ps, Region r, Faction fac) 
	{
		region = r;
		myFac = fac;
		out = ps;
		m_data = data;
		writeRegion();
	}
	
	protected void writeRegion()
	{
		out.println(" ------------------------------------------------------------------------------");
		out.println();
		StringBuffer line;
		if (region.getType().toString().equals("Ozean"))
			line = new StringBuffer("Ozean (");
		else
			line = new StringBuffer(region.getName()).append(" (");
		Coordinate coord = (Coordinate)region.getID();
		line.append(coord.toString(",",false)).append("), ");
		line.append(region.getType());
		if (region.trees > 0) {
			line.append(", ").append(region.trees).append("/");
			if(region.mallorn) {
				line.append(region.sprouts>0?region.sprouts:0).append(" Mallornbäume");
			} else {
				line.append(region.sprouts>0?region.sprouts:0).append(" Bäume");
			}
		}
		if (region.stones > 0) {
			RegionResource res = getResourceByName(region, "Steine");
			appendResource(line, res);
		}
		if (region.iron > 0) {
			RegionResource res = getResourceByName(region, "Eisen");
			appendResource(line, res);
		}
		if (region.peasants > 0)
			line.append(", ").append(region.peasants).append(" Bauern");
		if (region.silver > 0)
			line.append(", ").append(region.silver).append(" Silber");
		if (region.horses > 0)
			line.append(", ").append(region.horses).append(" Pferde");
		line.append(". ");
		if (region.getDescription() != null) {
			line.append(region.getDescription());
			if (line.charAt(line.length()-1) == '.')
			line.append(" ");
		}
		appendNeighbors(line);
		NrWriter.writeWithWrap(out, line, 78, 0);
		out.println();
		line = new StringBuffer();
		appendLuxeries(line);
		NrWriter.writeWithWrap(out, line, 78, 0);
		// out.println();
		appendGuards();
		appendDurchreise();
		if (NrWriter.isOptionActive(myFac, "STATISTIK")) 
		{
			appendStatistik();
		}
		writeRegionMessages();
		List buildings = CollectionFactory.createArrayList();
		buildings.addAll(region.buildings());
		Iterator it = buildings.iterator();
		while (it.hasNext())
			appendUnits((UnitContainer)it.next());
		appendUnits(region);
		it = region.ships().iterator();
		while (it.hasNext())
			appendUnits((UnitContainer)it.next());
	}
	
	protected void writeRegionMessages()
	{
		if (region.messages == null)
			return;
		NrWriter.center(out, "Meldungen und Ereignisse", 79);
		out.println();
		Iterator it = region.messages.iterator();
		while (it.hasNext()) {
			Message msg = (Message)it.next();
			NrWriter.writeWithWrap(out, new StringBuffer(msg.getText()), 78,2);
			out.println();
		}
	}
	
	protected void appendGuards()
	{
		List guards = region.getGuards();
		if (guards == null)
			return;
		out.println();
		List factions = CollectionFactory.createArrayList();
		Iterator uit = guards.iterator();
		while (uit.hasNext()) {
			Unit u = (Unit)uit.next();
			Faction f = u.getFaction();
			if (!factions.contains(f))
				factions.add(f);
		}
		StringBuffer buf = new StringBuffer("Die Region wird von ");
		int i=0;
		int size = factions.size();
		Iterator it = factions.iterator();
		while (it.hasNext()) {
			Faction f = (Faction)it.next();
			if (i > 0 && i < size-1)
				buf.append(", ");
			if (i > 0 && i == size-1)
				buf.append(" und ");
			String name = f.toString();
			if (name.equals("Parteigetarnte (-1)")) 
				name = "unbekannten Einheiten";
			buf.append(name);
			i++;
		}
		buf.append(" bewacht.");
		NrWriter.writeWithWrap(out, buf, 79, 0);
	}

	
	protected void appendLuxeries(StringBuffer line)
	{
		if (region.prices == null) return;
		List keySet = CollectionFactory.createArrayList();
		keySet.addAll(region.prices.keySet());
		Collections.sort(keySet);
		// Collections.reverse(keySet);
		line.append("Auf dem Markt wird für ");
		Iterator it = keySet.iterator();
		Object buy = null;
		while (it.hasNext() && buy == null) {
			Object luxury = it.next();
			int price = ((LuxuryPrice)region.prices.get(luxury)).getPrice();
			if (price < 0)
				buy = luxury;
		}
		String display = getPlural(buy.toString());
		line.append(display).append(" ");
		line.append(((LuxuryPrice)region.prices.get(buy)).getPrice() * -1);
		line.append(" Silber verlangt. Geboten wird für ");
		int missing = 6;
		it = keySet.iterator();
		while (it.hasNext()) {
			Object luxury = it.next();
			int price = ((LuxuryPrice)region.prices.get(luxury)).getPrice();
			if (price > 0) {
				missing--;
				String lux = luxury.toString(); 
				lux = (String)getPlural(lux); 
				line.append(lux).append(" ");
				line.append(price).append(" Silber");
				if (missing > 1)
					line.append(", für ");
				else if (missing != 0)
					line.append(" und für ");			
			}
		}
		line.append(".");
	}

	
	protected void appendUnits(UnitContainer uc)
	{
		if (uc instanceof Building)
		{
			Building b = (Building) uc;
			StringBuffer line = new StringBuffer("  ");
			line.append(b.getName()).append(" (");
			line.append(b.getID().toString()).append(")");
			line.append(", Größe ").append(b.getSize());
			line.append(", ").append(b.getType());
			if (b.getDescription() != null) {
				line.append("; ").append(b.getDescription());			
			}
			appendDot(line);
			NrWriter.writeWithWrap(out, line, 78, 2);
			out.println();
		}
		
		if (uc instanceof Ship)
		{
			Ship s = (Ship) uc;
			StringBuffer line = new StringBuffer("  ");
			line.append(s.getName()).append(" (");
			line.append(s.getID().toString()).append(")");
			// line.append(", Größe ").append(b.getSize());
			line.append(", ").append(s.getType());
			if (s.capacity != -1) {
				line.append(", (").append(s.load).append("/");
				line.append(s.capacity).append(")");
			}
			/* TODO: Ships im Bau 
			int size = s.size;
			int nominalShipSize = ((com.eressea.rules.ShipType)s.getType()).getWood();
			if (size < nominalShipSize) {
				line.append(", im Bau (");
				line.append(size).append("/").append(nominalShipSize).append(")");
			} */
			if (s.damageRatio > 0)
				line.append(", ").append(s.damageRatio).append("% beschädigt");
			if (s.shoreId != -1) {
				line.append(", ").append(shore[s.shoreId]).append("küste");
			}
			if (s.getDescription() != null) {
				line.append("; ").append(s.getDescription());			
			}
			char c = line.charAt(line.length()-1);
			if (c != '.' && c != '!' && c != '?')
				line.append(".");
			NrWriter.writeWithWrap(out, line, 78, 2);
			out.println();
		}
		
		Iterator uit  = uc.units().iterator();
		while (uit.hasNext())
		{
			StringBuffer line = new StringBuffer("  ");
			if (!(uc instanceof Region))
				line.append("  ");
			Unit u = (Unit)uit.next();
			if (uc instanceof Region && (u.getBuilding() != null || u.getShip() != null))
				continue;
			if (isOfMyFaction(u))
				line.append("* ");
			else if (myFac.allies != null && myFac.allies.containsKey(u.getFaction().getID()))
				line.append("+ ");
			else if (u.isSpy())
				line.append("! ");
			else
				line.append("- ");
			line.append(u.getName()).append(" (");
			line.append(u.getID().toString()).append(")");
			Faction f = u.getFaction();
			// TODO: implementation for these variables missing (id, tttt, iiii)
			// int id = ((IntegerID)f.getID()).intValue();
			// String tttt = u.getName();
			// String iiii = u.getID().toString();
			if (u.hideFaction)
				line.append(", anonym");
			else 
				if (!isOfMyFaction(u))
					line.append(", ").append(f.toString());
			line.append(", ").append(u.persons).append(" ");
			String prefix = f.getRaceNamePrefix();
			if (prefix == null)
				prefix = u.getRaceNamePrefix();
			if (prefix != null)
				line.append(prefix);
			String race = (u.persons == 1)?getSingular(u.race.toString()):u.race.toString();
			if (prefix != null)
				race = race.toLowerCase();
			line.append(race);
			Race realRace = u.realRace;
			if (realRace != null) {
				race = realRace.getID().toString();
				race = (u.persons == 1)?getSingular(realRace.toString()):realRace.toString();
				line.append(" (").append(race).append(")");
			}
			if (isOfMyFaction(u)) {
				line.append(", ");
				line.append(Unit.combatStatusToString(u));
				if (u.health != null) {
					line.append(" (").append(u.health);
					if (u.isStarving) 
						line.append(", hungert");
					line.append(")");
				}
				else if (u.isStarving) 
					line.append("( hungert)");
				if (u.guard == 1)
					line.append(", bewacht die Region");
				appendSkills(u, line);
				
			}
			else
				if (u.guard == 1)
					line.append(", bewacht die Region");
			appendItems(u, line);
			if (isMagician(u)) {
				appendMagic(u, line);
			}
			if (isOfMyFaction(u)) {
				if (u.ordersAreNull()) {
					log.warn("Unit " + u.getID().toString() + " has no commands");
				} else {
					String order = u.getOrders().iterator().next().toString();
					String low = order.toLowerCase();
					if (low.startsWith("kaufe") ||
						low.startsWith("verkaufe") ||
						low.startsWith("zauber") ||
						low.startsWith("//")) {
						;
					} else {
						line.append(", \"").append(order);
						line.append("\"");
					}
				}
			}
			if (u.getDescription() != null)
				line.append("; ").append(u.getDescription());
			char c = line.charAt(line.length()-1);
			if (c != '.' && c != '!' && c != '?')
				line.append(".");
			int indent = uc instanceof Region?4:6;
			int len = uc instanceof Region?78:78;
			NrWriter.writeWithWrap(out, line, len, indent);
			out.println();
		}
	}
	
	protected boolean isOfMyFaction(Unit u)
	{
		return u.getFaction().equals(myFac);
	}

	
	protected char getGender(String type)
	{
		try {
			return ((Character)genderMap.get(type)).charValue();
		}
		catch (NullPointerException ex) {
			log.warn("Unknown Regiontype " + type);
			return ' ';
		}
	}
	
	protected void appendNeighbors(StringBuffer line)
	{
		Region r = findRegion(region, -1, 1);
		line.append("Im Nordwesten der Region liegt ");
		appendRegion(r, line);
		
		r = findRegion(region, 0, 1);
		line.append(", im Nordosten ");
		appendRegion(r, line);
		
		r = findRegion(region, 1, 0);
		line.append(", im Osten ");
		appendRegion(r, line);
		
		r = findRegion(region, 1, -1);
		line.append(", im Südosten ");
		appendRegion(r, line);
		
		r = findRegion(region, 0, -1);
		line.append(", im Südwesten ");
		appendRegion(r, line);
		
		r = findRegion(region, -1, 0);
		line.append(" und im Westen ");
		appendRegion(r, line);
		line.append(".");
	}
	
	protected void appendRegion(Region r, StringBuffer line)
	{
		if (r == null) return;
		String type = r.getType().toString();
		if (type.equals("Berge"))
			type = "Bergland";
		char g = getGender(type);
		if (g == 'm')
			line.append("der ");
		if (g == 'w')
			line.append("die ");
		if (g == 's')
			line.append("das ");
		line.append(type);
		if (!type.equals("Ozean")) {
			line.append(" von ");
			line.append(r.getName());
		}
		line.append(" (");
		line.append(((Coordinate)r.getID()).toString(","));
		line.append(")");
	}
	
	protected RegionResource getResourceByName(Region r, String name)
	{
		Iterator it = region.resources().iterator();
		while (it.hasNext()) {
			RegionResource cur = (RegionResource)it.next();
			if (name.equals(cur.getType().toString())) {
				return cur;
			}
		}
		return null;
	}
	
	protected void appendResource(StringBuffer line, RegionResource res) 
	{
		if (res == null) return;
		line.append(", ").append(res.getAmount()).append(" ").append(res.getType());
		line.append("/").append(res.getSkillLevel());
	}
	
	protected void appendDurchreise()
	{
		if (region.travelThru == null)
			return;
		out.println();
		Iterator it = region.travelThru.iterator();
		StringBuffer strAll = new StringBuffer();
		while (it.hasNext()) {
			Message msg = (Message)it.next();
			if (strAll.length() > 0) 
				strAll.append(", ");
			strAll.append(msg.getText());
		}
		strAll.append(region.travelThru.size() > 1?" haben":" hat");
		strAll.append(" die Region durchquert.");
		NrWriter.writeWithWrap(out, strAll, 79, 2);
	}
	
	protected void appendStatistik()
	{
		out.println();
		StringBuffer line = new StringBuffer("Statistik für ");
		if (region.getType().toString().equals("Ozean"))
			line.append("Ozean");
		else
			line.append(region.getName());
		line.append(" (");
		line.append(((Coordinate)region.getID()).toString(",")).append("):");
		out.println();
		out.println(line);
		out.println();
		int persons = 0;
		Map items = CollectionFactory.createHashMap();
		Iterator uit  = region.units().iterator();
		while (uit.hasNext())
		{
			line = new StringBuffer("  ");
			Unit u = (Unit)uit.next();
			if (isOfMyFaction(u)) {
				persons += u.persons;
				if (u.items != null) {
					Iterator iit = u.items.values().iterator();
					while (iit.hasNext()) {
						Item item = (Item)iit.next();
						String name = item.getName();
						Integer knownItem = (Integer)items.get(name);
						int knownCount = 0;
						if (knownItem != null)
							knownCount = knownItem.intValue();
						knownCount += item.getAmount();
						items.put(name, new Integer(knownCount));
					}
				}
			}
		}
		if (!(region.getType().toString().equals("Ozean"))) {
			if (region.silver > 0) {
				line = new StringBuffer("Unterhaltung: max. ");
				line.append(region.silver / 20);
				line.append(" Silber");
				out.println(line);
			}
			line = new StringBuffer("Lohn für Arbeit: ");
			line.append(region.wage);
			line.append(" Silber");
			out.println(line);
			if (region.peasants > 0) {
				line = new StringBuffer("Rekrutieren: max. ");
				line.append(region.maxRecruit());
				line.append(" Bauern");
				out.println(line);
				line = new StringBuffer("Luxusgüter zum angegebenen Preis: ");
				line.append(region.peasants / 100);
				out.println(line);
			}
		}
		line = new StringBuffer("Personen: ");
		line.append(persons);
		out.println(line);
		// output in order of nouns
		for (int i= 0; i< nouns.length; i+=2) {
			String name = nouns[i];
			Integer oInteger = (Integer)items.get(name);
			if (oInteger != null) {
				int count = ((Integer)items.get(name)).intValue();
				line = new StringBuffer();
				if (count == 1)
					line.append(name);
				else
					line.append(getPlural(name));
				line.append(": ").append(count);
				out.println(line);
			}
		}
	}
	
	protected void appendItems(Unit u, StringBuffer line)
	{
		if (u.items == null) return;
		line.append(", hat: ");
		Iterator it = u.items.values().iterator();
		while (it.hasNext()) {
			Item item = (Item)it.next();
			if (item.getAmount() > 1) {
				line.append(item.getAmount()).append(" ");
				line.append(getPlural(item.getName())).append(", ");
			}
			else 
				line.append(item.getName()).append(", ");
		}
		line.setLength(line.length()-2);
	}
	
	protected void appendSkills(Unit u, StringBuffer line)
	{
		if (u.skills == null) return;
		line.append(", Talente: ");
		Iterator it = u.skills.values().iterator();
		while (it.hasNext()) {
			Skill sk = (Skill)it.next();
			line.append(sk.getName()).append(" ");
			if (sk.getName().equals("Magie")) {
				line.append(u.getFaction().spellSchool).append(" ");
			}
			line.append(sk.getLevel()).append(" [");
			line.append(sk.getPoints()/u.persons).append("], ");
		}
		line.setLength(line.length()-2);
	}
	
	protected void appendMagic(Unit u, StringBuffer line)
	{
		line.append(". Aura ").append(u.aura).append("/").append(u.auraMax).append(", Zauber:");
		Iterator it = u.spells.values().iterator();
		while (it.hasNext()) {
			line.append(" ").append(it.next().toString()).append(",");
		}
		line.setLength(line.length()-1);
		if (u.combatSpells != null) {
			String k1 = "keiner", k2 = "keiner", k3 = "keiner";
			int l1 = 0, l2 = 0, l3 = 0;
			line.append(", Kampfzauber:");
			it = u.combatSpells.values().iterator();
				while (it.hasNext()) {
					CombatSpell spell = (CombatSpell)it.next();
				int prepost = ((IntegerID)spell.getID()).intValue();
				if (prepost == 0) {
					k1 = spell.getSpell().getName();
					l1 = spell.getCastingLevel();
				}
				if (prepost == 1) {
					k2 = spell.getSpell().getName();
					l2 = spell.getCastingLevel();
				}
				if (prepost == 2) {
					k3 = spell.getSpell().getName();
					l3 = spell.getCastingLevel();
				}
			}
			line.append(" ").append(k1);
			if (l1 > 0)
				line.append(" (").append(l1).append(")");
			line.append(",");
			line.append(" ").append(k2);
			if (l2 > 0)
				line.append(" (").append(l2).append(")");
			line.append(",");
			line.append(" ").append(k3);
			if (l3 > 0)
				line.append(" (").append(l3).append(")");
		}
	}
		
	private boolean isMagician(Unit u)
	{
		return u.auraMax > 0;
	}
	
	protected void appendDot(StringBuffer line)
	{
		char c = line.charAt(line.length()-1);
		if (c != '.' && c != '!'  && c != '?')
			line.append(".");
	}
	
	protected Region findRegion(Region r, int offsetX, int offsetY)
	{
		Coordinate coord = (Coordinate)r.getID();
		int x = coord.x + offsetX;
		int y = coord.y + offsetY;
		coord = new Coordinate(x,y);
		return m_data.getRegion(coord);
	}
}
