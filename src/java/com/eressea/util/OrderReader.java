// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import com.eressea.CompleteData;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.Region;
import com.eressea.TempUnit;
import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.UnitID;
import com.eressea.util.logging.Logger;

/**
 * A class for reading a orders file for unit orders.
 */
public class OrderReader {
	private final static Logger log = Logger.getInstance(OrderReader.class);
	private GameData data=null;
	private LineNumberReader stream=null;
	private boolean autoConfirm = false;
	private boolean ignoreSemicolonComments = false;
	private Status status = null;

	/**
	 * Creates a new OrderReader object adding the read orders
	 * to the units it can find in the specified game data object.
	 * This function clears the caches of all units.
	 */
	public OrderReader(GameData g) {
		data = g;
		if (data == null) {
			log.info("OrderReader.OrderReader(): game data is null! Creating empty game data to proceed.");
			data = new CompleteData(new com.eressea.rules.Eressea());
		}

		// clear the caches in game data
		if (data.units() != null) {
			for (Iterator iter = data.units().values().iterator(); iter.hasNext();) {
				Unit u = (Unit)iter.next();
				if (u.cache != null) {
					u.cache.clear();
					u.cache = null;
				}
			}
		}
		if (data.regions() != null) {
			for (Iterator iter = data.regions().values().iterator(); iter.hasNext();) {
				UnitContainer uc = (UnitContainer)iter.next();
				if (uc.cache != null) {
					uc.cache.clear();
					uc.cache = null;
				}
			}
		}
	}

	/**
	 * Reads the orders from the specified Reader.
	 * Orders for multiple factions can be read.
	 * Region lines are ignored.
	 * Unit are not created. If there are orders for a unit that
	 * cannot be found in the game data these orders are ignored.
	 * Lines containing ECHECK comments are always ignored.
	 * Comments starting with a semicolon and containing the literal
	 * 'bestaetigt' (case and umlaut insensitive) after an arbitrary
	 * number of whitespace characters are never added to a unit's
	 * orders, instead they set the order confirmation status of
	 * the unit to true.
	 */
	public void read(Reader in) throws IOException {
		stream = new LineNumberReader(new MergeLineReader(in));

		String line = stream.readLine();

		while (line != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);
			if (tokenizer.hasMoreTokens()) {
				String token = Umlaut.normalize(tokenizer.nextToken());
				if (Translations.getOrderTranslation(EresseaOrderConstants.O_FACTION).startsWith(token) ||
					Translations.getOrderTranslation(EresseaOrderConstants.O_ERESSEA).startsWith(token)) {
					token = tokenizer.nextToken();
					try {
						ID fID = EntityID.createEntityID(token);
						Faction f = data.getFaction(fID);
						if (f != null) {
							readFaction(fID);
						} else {
							log.info("OrderReader.read(): The faction with id " + fID + " (" + token +
									 ") is not present in the game data, skipping this faction.");
						}
					} catch (NumberFormatException e) {
						log.error("OrderReader.read(): Unable to parse faction id: " + e.toString() + " at line " + stream.getLineNumber(),e);
					}
				}
			}
			line = stream.readLine();
		}
	}

	private void readFaction(ID id) throws IOException {
		Faction faction = data.getFaction(id);
		if (faction == null) {
			data.addFaction(new Faction(id, data));
		}
		String line = null;	// the line read from the file
		Region currentRegion = null;	// keeps track of the region we are in
		Unit currentUnit = null;	// keeps track of the unit which is currently processed
		Locale currentLocale = Locales.getOrderLocale();	// start out with the currently set default order locale
		/* normalized orders that have to be checked often in the loop
		these have to be updated whenever the locale changes */
		String naechsterOrder = Umlaut.normalize(Translations.getOrderTranslation(EresseaOrderConstants.O_NEXT, currentLocale));
		String localeOrder = Umlaut.normalize(Translations.getOrderTranslation(EresseaOrderConstants.O_LOCALE, currentLocale));

		if (status == null) {
			status = new Status();
		}
		status.factions++;

		while ((line = stream.readLine()) != null) {

			StringTokenizer tokenizer = new StringTokenizer(line, " ;");
			/*
			There was a problem using this StringTokenizer:
			If a unit had an order like " ; Einheit hat Kommando" the tokenizer
			skipped the leading semicolon and tried to parse a new order instead
			of parsing this line as a comment.
			So treat lines, that start with a semicolon special!
			*/

			if (line.trim().startsWith(";")) {
				if (currentUnit != null) {
					// mark orders as confirmed on a ";bestaetigt" comment
					String rest = Umlaut.normalize(line.substring(line.indexOf(';') + 1).trim());
					if (rest.equalsIgnoreCase(OrderWriter.CONFIRMED)) {
						currentUnit.ordersConfirmed = true;
					} else if (ignoreSemicolonComments == false &&
							   rest.startsWith("ECHECK") == false) {
						// add all other comments except "; ECHECK ..." to the orders
						currentUnit.addOrders(line);
					}
				}
				continue;
			}
			
			if (!tokenizer.hasMoreTokens() || line.trim().equals("")) {
				// empty line
				if (currentUnit != null) {
					currentUnit.addOrders(line);
				}
				continue;
			}
			String token = Umlaut.normalize(tokenizer.nextToken().trim());

			if (naechsterOrder.startsWith(token)) {
				/* turn orders into 'real' temp units */
				if (currentUnit != null) {
					currentUnit.extractTempUnits(0, currentLocale);
				}
				break;
			} else if (localeOrder.startsWith(token)) {
				if (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken().replace('"', ' ').trim();
					currentLocale = new Locale(token, "");

					/* update the locale dependent cached orders */
					naechsterOrder = Umlaut.normalize(Translations.getOrderTranslation(EresseaOrderConstants.O_NEXT, currentLocale));
					localeOrder = Umlaut.normalize(Translations.getOrderTranslation(EresseaOrderConstants.O_LOCALE, currentLocale));
				}
			} else if (Translations.getOrderTranslation(EresseaOrderConstants.O_REGION, currentLocale).startsWith(token)) {
				//ignore
				currentUnit = null;
			} else if (Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT, currentLocale).startsWith(token)) {
				token = tokenizer.nextToken();

				ID unitID = null;
				try {
					unitID = UnitID.createUnitID(token);
				} catch (NumberFormatException e) {
					log.error("OrderReader.readFaction(): " + e.toString() + " at line " + stream.getLineNumber(),e);
				}

				if (unitID != null) {
					status.units++;

					/* turn orders into 'real' temp units */
					if (currentUnit != null) {
						currentUnit.extractTempUnits(0, currentLocale);
					}

					currentUnit = data.getUnit(unitID);
					if (currentUnit == null) {
						currentUnit = new Unit(unitID);
						currentUnit.setFaction(faction);
						if (currentRegion != null) {
							currentUnit.setRegion(currentRegion);
						}
						data.addUnit(currentUnit);
					} else {
						/* the unit already exists so delete all its
						   temp units */
						Collection victimIDs = CollectionFactory.createLinkedList();
						for (Iterator tempIter = currentUnit.tempUnits().iterator(); tempIter.hasNext();) {
							victimIDs.add(((TempUnit)tempIter.next()).getID());
						}
						for (Iterator idIter = victimIDs.iterator(); idIter.hasNext();) {
							currentUnit.deleteTemp((ID)idIter.next(), data);
						}
					}
					currentUnit.clearOrders();
					currentUnit.ordersConfirmed = autoConfirm;
				} else {
					currentUnit = null;
				}

			} else if (currentUnit != null) {
				currentUnit.addOrders(line);
			}
		}
	}

	/**
	 * Returns whether all read orders get automatically confirmed.
	 */
	public boolean getAutoConfirm() {
		return this.autoConfirm;
	}

	/**
	 * Sets whether all read orders get automatically confirmed.
	 */
	public void setAutoConfirm(boolean autoConfirm)	{
		this.autoConfirm = autoConfirm;
	}

	/**
	 * Returns whether all comments in the orders starting with a
	 * semicolon (except confirmation comments) are ignored.
	 */
	public boolean isIgnoringSemicolonComments() {
		return ignoreSemicolonComments;
	}

	/**
	 * Sets whether all comments in the orders starting with a
	 * semicolon (except confirmation comments) are ignored.
	 */
	public void ignoreSemicolonComments(boolean ignoreSemicolonComments) {
		this.ignoreSemicolonComments = ignoreSemicolonComments;
	}

	/**
	 * Returns the number of factions and units that were read.
	 * This method should only be called after reading the orders has
	 * finished.
	 */
	public Status getStatus() {
		return status;
	}

	public class Status {
		public int units = 0;
		public int factions = 0;
	}
}
