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

package com.eressea.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.Collection;
import java.util.Iterator;

import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.Group;
import com.eressea.Item;
import com.eressea.Region;
import com.eressea.StringID;
import com.eressea.TempUnit;
import com.eressea.Unit;

import com.eressea.rules.BuildingType;

/**
 * A class for writing orders of all units of a certain faction to a stream.
 */
public class OrderWriter {
	/** TODO: DOCUMENT ME! */
	public static final String CONFIRMED = "bestaetigt";

	/** TODO: DOCUMENT ME! */
	public static final String CONFIRMEDTEMP = CONFIRMED + "_temp";
	private String echeckOptions = " -s -l -w4";
	private GameData world = null;
	private Faction faction = null;
	private Group group = null;
	private boolean addECheckComments = true;
	private boolean removeSCComments = false;
	private boolean removeSSComments = false;
	private boolean confirmedOnly = false;
	private boolean forceUnixLineBreaks = false;
	private Collection regions = null;

	/**
	 * Creates a new OrderWriter object extracting the orders of faction f's units and writing them
	 * to the stream w.
	 *
	 * @param g GameData object ot get orders from.
	 * @param f the faction the orders are written for.
	 */
	public OrderWriter(GameData g, Faction f) {
		this(g, f, null);
	}

	/**
	 * Creates a new OrderWriter object extracting the orders of faction f's units and writing them
	 * to the stream w with the specified options for E-Check.
	 *
	 * @param g GameData object ot get orders from.
	 * @param f the faction the orders are written for.
	 * @param echeckOpts options for E-Check, default is " -s -l -w4"
	 */
	public OrderWriter(GameData g, Faction f, String echeckOpts) {
		world = g;
		faction = f;

		if(echeckOpts != null) {
			echeckOptions = echeckOpts;
		}

		if(f.getType() != null) {
			echeckOptions += (" -r" + faction.getRace().getRecruitmentCosts());
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param stream TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public int write(Writer stream) throws IOException {
		return write(new BufferedWriter(stream));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param stream TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public int write(BufferedWriter stream) throws IOException {
		writeHeader(stream);

		int units = writeRegions(((this.regions != null) && (this.regions.size() > 0)) ? regions
																					   : world.regions()
																							  .values(),
								 stream);
		writeFooter(stream);

		// we flush on purpose to fill the underlying Writer
		// with the buffered content of the BufferedWriter
		stream.flush();

		return units;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setAddECheckComments(boolean bool) {
		addECheckComments = bool;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param semicolon TODO: DOCUMENT ME!
	 * @param slashslash TODO: DOCUMENT ME!
	 */
	public void setRemoveComments(boolean semicolon, boolean slashslash) {
		removeSCComments = semicolon;
		removeSSComments = slashslash;
	}

	/**
	 * Enforce that only Unix-style linebreaks are used. This is necessary when writing to the
	 * clipboard under Windows.
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setForceUnixLineBreaks(boolean bool) {
		forceUnixLineBreaks = bool;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param group TODO: DOCUMENT ME!
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	private void writeHeader(BufferedWriter stream) throws IOException {
		stream.write(Translations.getOrderTranslation(EresseaOrderConstants.O_ERESSEA));
		stream.write(" " + faction.getID());
		writeln(stream, " \"" + faction.password + "\"");
		writeln(stream, "; TIMESTAMP " + getTimeStamp());

		if(addECheckComments == true) {
			writeln(stream, "; ECHECK " + echeckOptions);
		}

		if(VersionInfo.getBuildDate() != null) {
			writeln(stream, "; VERSION " + VersionInfo.getBuildDate());
		}

		// pavkovic 2003.09.11: use system locale and NOT faction locale!
		// if (faction.getLocale() != null) {
		// writeln(stream, "LOCALE " + faction.getLocale().getLanguage());
		// }
		writeln(stream, "LOCALE " + Locales.getOrderLocale().getLanguage());
	}

	private int writeRegions(Collection regions, BufferedWriter stream) throws IOException {
		int writtenUnits = 0;

		for(Iterator iter = regions.iterator(); iter.hasNext();) {
			Region r = (Region) iter.next();
			Collection units = filterUnits(r.units());

			if(units.size() > 0) {
				writtenUnits += writeRegion(r, units, stream);
				units.clear(); // this should help the garbage collector
			}

			units = null;
		}

		return writtenUnits;
	}

	private int writeRegion(Region r, Collection units, BufferedWriter stream)
					 throws IOException
	{
		if(addECheckComments) {
			String name = r.getName();

			if(name == null) {
				name = "Ozean";
			}

			stream.write(Translations.getOrderTranslation(EresseaOrderConstants.O_REGION));
			writeln(stream, " " + r.getID().toString(",") + " ; " + r.getName());
			writeln(stream, "; ECheck Lohn " + r.wage);
		}

		int writtenUnits = 0;

		for(Iterator iter = units.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();

			if(writeUnit(u, stream)) {
				writtenUnits++;
			}
		}

		return writtenUnits;
	}

	private boolean writeUnit(Unit unit, BufferedWriter stream) throws IOException {
		if(unit instanceof TempUnit) {
			return false;
		}

		stream.write(Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + " " +
					 unit.getID().toString());

		if(addECheckComments == true) {
			int money = 0;
			Item silver = unit.getItem(world.rules.getItemType(StringID.create("Silber"), true));

			if(silver != null) {
				money = silver.getAmount();
			}

			stream.write(";\t\t" + unit.getName() + " [" + unit.persons + "," + money + "$");

			if(unit.getBuilding() != null) {
				if(unit.equals(unit.getBuilding().getOwnerUnit())) {
					BuildingType type = unit.getBuilding().getBuildingType();

					if(type != null) {
						Item i = type.getMaintenance(StringID.create("Silber"));

						if(i != null) {
							stream.write(",U" + i.getAmount());
						}
					}
				}
			}

			if(unit.getShip() != null) {
				if(unit.equals(unit.getShip().getOwnerUnit())) {
					stream.write(",S");
				} else {
					stream.write(",s");
				}

				stream.write(unit.getShip().getID().toString());
			}

			stream.write("]");
		}

		writeln(stream, null);

		// confirmed?
		if(unit.ordersConfirmed && !removeSCComments) {
			writeln(stream, ";" + CONFIRMED);
		}

		writeOrders(unit.getCompleteOrders(), stream);

		return true;
	}

	private void writeOrders(Collection cmds, BufferedWriter stream) throws IOException {
		for(Iterator it = cmds.iterator(); it.hasNext();) {
			String cmd = (String) it.next();
			String trimmedAndBurning = cmd.trim();

			if((removeSCComments && trimmedAndBurning.startsWith(";")) ||
				   (removeSSComments && trimmedAndBurning.startsWith("//"))) {
				// consume
			} else {
				writeln(stream, cmd);
			}
		}
	}

	private void writeFooter(BufferedWriter stream) throws IOException {
		writeln(stream, Translations.getOrderTranslation(EresseaOrderConstants.O_NEXT));
	}

	private Collection filterUnits(Collection units) {
		Collection filteredUnits = CollectionFactory.createLinkedList();

		for(Iterator iter = units.iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();

			if(filterUnit(u)) {
				filteredUnits.add(u);
			}
		}

		return filteredUnits;
	}

	private boolean filterUnit(Unit u) {
		if(u.getFaction().equals(faction) && !u.isSpy()) {
			if(!confirmedOnly || u.ordersConfirmed) {
				if((group == null) || group.equals(u.getGroup())) {
					return true;
				}
			} else {
				/* if this is a parent unit, it has to be added if
				   one of it's children has unconfirmed orders */
				if(confirmedOnly && !(u instanceof TempUnit) && !u.tempUnits().isEmpty()) {
					for(Iterator tempIter = u.tempUnits().iterator(); tempIter.hasNext();) {
						TempUnit tu = (TempUnit) tempIter.next();

						if(tu.ordersConfirmed) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private String getTimeStamp() {
		long time = System.currentTimeMillis();
		int x = System.getProperties().getProperty("user.name").hashCode();
		int y = System.getProperties().getProperty("os.name").hashCode();
		int z = System.getProperties().getProperty("java.version").hashCode();
		long sum = x + y + z;
		String strSum = Long.toString(sum);
		String strTime = Long.toString(time);
		String strSumPart = strSum.substring(strSum.length() - 3);
		String strTimePart = strTime.substring(strTime.length() - 6, strTime.length() - 3);
		String rot = rotate(strSumPart, Integer.parseInt(strTimePart));
		String merge = "";

		for(int i = 0; i < 3; i++) {
			merge += ((Integer.parseInt(rot.substring(i, i + 1)) +
			Integer.parseInt(strTimePart.substring(i, i + 1))) % 10);
		}

		int foo = Integer.parseInt(merge);
		String padded = ((foo < 100) ? "0" : "") + ((foo < 10) ? "0" : "") + foo;
		String res = strTime.substring(0, strTime.length() - 3) + padded;

		return res;
	}

	private String rotate(String str, int amount) {
		char res[] = new char[str.length()];

		for(int i = 0; i < res.length; i++) {
			res[i] = str.charAt((i + amount) % res.length);
		}

		return new String(res);
	}

	private void writeln(BufferedWriter stream, String text) throws IOException {
		if(text != null) {
			stream.write(text);
		}

		if(forceUnixLineBreaks) {
			stream.write('\n');
		} else {
			stream.newLine();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getConfirmedOnly() {
		return this.confirmedOnly;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param confirmedOnly TODO: DOCUMENT ME!
	 */
	public void setConfirmedOnly(boolean confirmedOnly) {
		this.confirmedOnly = confirmedOnly;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aRegions TODO: DOCUMENT ME!
	 */
	public void setRegions(Collection aRegions) {
		regions = aRegions;
	}
}
