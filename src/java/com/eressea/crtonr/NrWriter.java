// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Martin Hershoff, Sebastian Pappert,
//							Klaas Prause,  Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.crtonr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eressea.Battle;
import com.eressea.Coordinate;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Item;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.rules.EresseaOption;
import com.eressea.util.CollectionFactory;

public class NrWriter
{
	static char[] cspaces = new char[80];
	static String spaces;
	
	static String[] buildings = {"Leuchtturm", "Bergwerk", "Sägewerk"};
	
	static int[] costs = {100, 300, 250};
	
	static Map costMap = CollectionFactory.createHashMap();
	
	static int getCost(String buildingname)
	{
		Object retval = costMap.get(buildingname);
		if (retval == null) 
			return 0;
		return ((Integer)retval).intValue();
	}
	
	static {
		for (int i=0;i<80;i++) {
			cspaces[i] = ' ';
		}
		spaces = new String(cspaces);
		for (int i=0;i<buildings.length;i++) {
			costMap.put(buildings[i], new Integer(costs[i]));
		}
	}
		
	GameData m_data;
	
	public NrWriter(GameData data)
	{
		m_data = data;
	}
	
	static String toBase36(Integer nr) 
	{
		String ret = Integer.toString(nr.intValue(), 36);
		return ret.replace('l', 'L');
	}
	
	public static void center(PrintStream out, String line, int size)
	{
		int nspace = (size - line.length()) / 2;
		if (line.length() > size) {
			int idx = size;
			while (line.charAt(idx) != ' ') idx--;
			String thisLine = line.substring(0, idx);
			nspace = (size - thisLine.length()) / 2;
			thisLine = spaces.substring(0, nspace) + thisLine;
			out.println(thisLine);
			center(out, line.substring(idx+1), size);
		}
		else {
			String newLine = spaces.substring(0, nspace) + line;
			out.println(newLine);
		}
	}
	
	public static void writeWithWrap(PrintStream out, StringBuffer line, 
		int maxlen, int indent)
	{
		int offset = 0;
		int len = line.length();
		String tab = spaces.substring(0,indent);
		while (offset + maxlen < len) {
			int idx = offset + maxlen;
			while (line.charAt(idx) != ' ') idx--;
			if (offset > 0) 
				out.print(tab);
			else
				maxlen -= indent;
			out.println(line.substring(offset, idx));
			offset = idx + 1;
		}
		if (offset > 0) out.print(tab);
		out.println(line.substring(offset));
	}
	
	protected void writeHeader(PrintStream out, Faction myFac)
	{
		StringBuffer line = new StringBuffer("Report für ");
		line.append(m_data.name);
		out.println(line);
		com.eressea.rules.Date date = m_data.getDate();
		// DateFormat format = DateFormat.getDateTimeInstance();
		// line.append(format.format(date));
		line = new StringBuffer(date.toString(com.eressea.rules.Date.TYPE_PHRASE_AND_SEASON)+".");
		center(out,line.toString(), 79); 
		line = new StringBuffer(myFac.toString()).append(", ");
		line.append(myFac.getType().toString()).append("/");
		line.append(myFac.spellSchool).append(" (");
		line.append(myFac.email).append(")");
		center(out, line.toString(), 79);
		out.println();
		if (isOptionActive(myFac, "PUNKTE")) {
			line = new StringBuffer("Deine Partei hat ").append(myFac.score);
			line.append(" Punkte. Der Durchschnitt für Parteien ähnlichen Alters ist ");
			line.append(myFac.averageScore).append(" Punkte.");
			center(out, line.toString(), 79);
		}
		line = new StringBuffer("Deine Partei hat ").append(myFac.persons);
		line.append(" Personen in ").append(myFac.units().size());
		line.append(" Einheiten.");
		center(out, line.toString(), 79);
		out.println();
	}
	
	protected void appendStatus(PrintStream out, Faction myFac) 
	{
		StringBuffer line = new StringBuffer("Aktueller Status");
		center(out, new String(line), 79);
	}
	
	public void writeNr(String filename) throws IOException
	{
		Map facMap = m_data.factions();
		Collection coll = facMap.values();
		FileOutputStream fout = new FileOutputStream(filename);
		PrintStream out = new PrintStream(fout);
		Iterator it = coll.iterator();
		Faction myFac = (Faction)it.next();
		writeHeader(out, myFac);
		StringBuffer line = new StringBuffer("");
		appendOptions(myFac, line);
		center(out, line.toString(), 79);
		writeMessages(out, myFac);
		Collection regions = m_data.regions().values();
		Iterator rit = regions.iterator();
		while (rit.hasNext()) {
			Region r = (Region)rit.next();
			if (r.units().size() > 0 || r.messages != null) {
				//writeRegion(out, r, myFac);
				new RegionWriter(m_data,out,r, myFac);
			}
		}
		writeAdresses(out, m_data);
		if (isOptionActive(myFac, "ZUGVORLAGE")) {
			writeZugvorlage(out, myFac);
		}
		out.close();
	}
	
	protected void appendOptions(Faction myFac, StringBuffer line)
	{
		line.append("Optionen:");
		Iterator it = myFac.options.options().iterator();
		while (it.hasNext())
		{
			EresseaOption cur = (EresseaOption)it.next();
			if (cur.isActive())
				line.append(" ").append(cur.getID().toString());
		}
	}
	
	protected void writeMessages(PrintStream out, Faction myFac)
	{
		String[] sections = {"errors", "Warnungen und Fehler",
							 "production", "Rohstoffe und Produktion",
							 "events", "Meldungen und Ereignisse",
							 "movement", "Reisen und Bewegung",
							 "economy", "Wirtschaft und Handel",
							 "study", "Lehren und Lernen",
							 "magic", "Magie und Artefakte"};
		for (int i=0;i<sections.length;i+=2) {
			boolean found = false;
			Iterator mit = myFac.messages.iterator();
			while (mit.hasNext()) {
				Message msg = (Message)mit.next();
				if (msg.getType().getSection().equals(sections[i])) {
					if (!found)
					{
						out.println();
						center(out, sections[i+1], 79);
						out.println();
						found = true;
					}
					writeWithWrap(out, new StringBuffer(msg.getText()), 78, 2);
				}
				else {
					//out.println("tpye = " + msg.getType().getSection());
					//out.println(msg.getText());
				}
			}
		}
		out.println();
		
		if (myFac.battles != null) {
			center(out,"Kämpfe", 79);
			out.println();
			Iterator bit = myFac.battles.iterator();
			while (bit.hasNext()) {
				Battle btl = (Battle)bit.next();
				Coordinate coord = (Coordinate)btl.getID();
				Region reg = m_data.getRegion(coord);
				out.println();
				StringBuffer strHead = new StringBuffer("In ");
				strHead.append(reg.getName());
				strHead.append(" (").append(reg.getCoordinate().x);
				strHead.append(",").append(reg.getCoordinate().y).append(")");
				strHead.append(" findet ein Kampf statt:");
				center(out,new String(strHead), 78);
				out.println();
				boolean ballteround = false;
				Iterator mit = btl.messages().iterator();
				while (mit.hasNext()) {
					Message msg = (Message)mit.next();
					if (msg.getText().startsWith("Einheiten vor der")) {
						ballteround = true;
					}
					int indent = ballteround?2:4;
					writeWithWrap(out, new StringBuffer(msg.getText()), 78, indent);
				}
			}
		}
	}
	
	public static boolean isOptionActive(Faction myFac, String option)
	{
		Iterator it = myFac.options.options().iterator();
		while (it.hasNext())
		{
			EresseaOption cur = (EresseaOption)it.next();
			String id = cur.getID().toString();
			if (id.equals(option) &&
				cur.isActive()) {
					return true;
			}
		}
		return false;
	}
		
	protected void writeAdresses(PrintStream out, GameData world) 
	{
		out.println(" ------------------------------------------------------------------------------");
		center(out, "Liste aller Adressen", 78);
		out.println();
		List keySet = CollectionFactory.createArrayList();
		keySet.addAll(world.factions().keySet());
		Collections.sort(keySet);
		Iterator it = keySet.iterator();
		while (it.hasNext()) {
			Faction fac = (Faction)world.factions().get(it.next());
			if (!fac.getID().toString().equals("-1")  && fac.email != null) {
				StringBuffer line = new StringBuffer("  * ");
				line.append(fac.getName());
				line.append(" (");
				line.append(fac.getID().toString());
				line.append("): ");
				line.append(fac.email).append("; ");
				if (fac.getDescription() != null)
					line.append(fac.getDescription());
				writeWithWrap(out, line, 78, 4);
			}
		}
		out.println();
	}
	
	protected boolean isIllusion(Unit u)
	{
		String[] illusionRaces = {"Schablonen"};
		
		if (u.realRace == null) {
			return false;
		}
		String realRace = u.realRace.toString();
		for (int i=0;i<illusionRaces.length;i++) {
			if (realRace.equals(illusionRaces[i])) {
				return true;
			}
		}
		return false;
	}
	
	protected void writeZugvorlage(PrintStream out, Faction myFac)
	{
		out.println(" ------------------------------------------------------------------------------");
		out.println();
		out.println("Vorlage für den nächsten Zug:");
		out.println();
		StringBuffer line = new StringBuffer("ERESSEA ");
		line.append(myFac.getID().toString());
		line.append(" \"hier_passwort_eintragen\"");
		out.println(line);
		Iterator rit = m_data.regions().values().iterator();
		while (rit.hasNext()) {
			Region r = (Region)rit.next();
			if (r.getName() != null && r.units().size() > 0) {
				line = new StringBuffer("REGION ");
				line.append(r.getCoordinate().toString(","));
				line.append(" ; ");
				line.append(r.getName());
				out.println(line);
				line = new StringBuffer("; ECheck Lohn ");
				line.append(r.wage);
				out.println(line);
				out.println();
				Iterator uit = r.units().iterator();
				while (uit.hasNext()) {
					Unit u = (Unit)uit.next();
					if (u.getFaction() == myFac) {
						line = new StringBuffer("EINHEIT ");
						line.append(u.getID().toString());
						line.append(";\t\t");
						line.append(u.getName()).append(" [");
						line.append(u.persons).append(",");
						if (u.items != null) {
							Item item = (Item)u.items.get(StringID.create("SILBER"));
							if (item == null)
								line.append("0$");
							else
								line.append(item.getAmount()).append("$");
						}
						else 
							line.append("0$");
						if (u.getShip() != null) {
							line.append(",");
							Unit owner = u.getShip().getOwnerUnit();
							if (u == owner)
								line.append("S");
							else
								line.append("s");
							line.append(u.getShip().getID());
						}
						if (u.getBuilding() != null) {
							Unit owner = u.getBuilding().getOwnerUnit();
							if (u == owner) {
								String type = u.getBuilding().getType().toString();
								int cost = getCost(type);
								if (cost > 0)
									line.append(",U").append(cost);
							}
						}
						if (isIllusion(u)) {
							line.append(",I");
						}
						line.append("]");
						out.println(line);
						if (!u.ordersAreNull()) {
							List orders = CollectionFactory.createLinkedList();
							orders.addAll(u.getOrders());
							Collections.reverse(orders);
							Iterator cit = orders.iterator();
							while (cit.hasNext()) {
								line = new StringBuffer("   ");
								line.append(cit.next());
								out.println(line);
							}	
						}
					}
				}
				out.println();
			}
		}
		out.println("NÄCHSTER");
	}
}

