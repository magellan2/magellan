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

package com.eressea.util;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.eressea.CoordinateID;

/**
 * A class providing convience functions for handling directions like in ships or borders. There
 * are three direction formats and the coversions between them supported: integer representation
 * (0 = north west and clockwise up), string representation (like 'NW' or 'Nordwesten') and
 * relative coordinate representation (coordinate with x = -1, y = 1).
 */
public class Direction {
	/** Invalid/unknown direction */
	public static final int DIR_INVALID = -1;

	/** north west direction */
	public static final int DIR_NW = 0;

	/** north east direction */
	public static final int DIR_NE = 1;

	/** east direction */
	public static final int DIR_E = 2;

	/** south east direction */
	public static final int DIR_SE = 3;

	/** south west direction */
	public static final int DIR_SW = 4;

	/** west direction */
	public static final int DIR_W = 5;
	private static List shortNames = null;
	private static List longNames = null;
	private static Locale usedLocale = null;
	private int dir = DIR_INVALID;

	/**
	 * Creates a new Direction object interpreting the specified integer as a direction according
	 * to the direction constants of this class.
	 *
	 * @param direction TODO: DOCUMENT ME!
	 */
	public Direction(int direction) {
		if((direction > -1) && (direction < 6)) {
			dir = direction;
		} else {
			dir = DIR_INVALID;
		}
	}

	/**
	 * Creates a new Direction object interpreting the specified coordinate as a direction.
	 *
	 * @param c a relative coordinate, e.g. (1, 0) for DIR_E. If c is null an
	 * 		  IllegalArgumentException is thrown.
	 *
	 * @throws IllegalArgumentException if the c param is null.
	 */
	public Direction(CoordinateID c) {
		if(c != null) {
			dir = toInt(c);
		} else {
			throw new IllegalArgumentException("Direction.Direction(Coordinate c): invalid coordinate specified!");
		}
	}

	/**
	 * Creates a new Direction object interpreting the specified String as a direction.
	 *
	 * @param str a german name for a direction, e.g. "Osten" for DIR_E. If str is null an
	 * 		  IllegalArgumentException is thrown.
	 *
	 * @throws IllegalArgumentException if the str param is null.
	 */
	public Direction(String str) {
		if(str != null) {
			dir = toInt(str);
		} else {
			throw new IllegalArgumentException("Direction.Direction(String str): invalid string specified!");
		}
	}

	/**
	 * Returns the actual direction of this object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getDir() {
		return dir;
	}

	/**
	 * Returns a String representation for this direction.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return toString(this.dir, false);
	}

	/**
	 * Returns a relative coordinate representing the direction dir. E.g. the direction DIR_W would
	 * create the coordinate (-1, 0).
	 *
	 * @param dir TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static CoordinateID toCoordinate(int dir) {
		int x = 0;
		int y = 0;

		switch(dir) {
		case DIR_NW:
			x = -1;

		case DIR_NE:
			y = 1;

			break;

		case DIR_SE:
			y = -1;

		case DIR_E:
			x = 1;

			break;

		case DIR_SW:
			y = -1;

			break;

		case DIR_W:
			x = -1;

			break;
		}

		return new CoordinateID(x, y);
	}

	/**
	 * Returns a String representation of the specified direction.
	 *
	 * @param dir TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String toString(int dir) {
		return toString(dir, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String toString(CoordinateID c) {
		return toString(toInt(c));
	}

	/**
	 * Returns a String representation of the specified direction.
	 *
	 * @param dir if true, a short form of the direction's string representation is returned.
	 * @param shortForm TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String toString(int dir, boolean shortForm) {
		if((dir < DIR_NW) || (dir > DIR_W)) {
			dir = DIR_INVALID;
		}

		if(shortForm) {
			return getShortDirectionString(dir);
		} else {
			return getLongDirectionString(dir);
		}
	}

	/**
	 * Converts a relative coordinate to an integer representation of the direction.
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static int toInt(CoordinateID c) {
		int dir = DIR_INVALID;

		if(c.x == -1) {
			if(c.y == 0) {
				dir = DIR_W;
			} else if(c.y == 1) {
				dir = DIR_NW;
			}
		} else if(c.x == 0) {
			if(c.y == -1) {
				dir = DIR_SW;
			} else if(c.y == 1) {
				dir = DIR_NE;
			}
		} else if(c.x == 1) {
			if(c.y == -1) {
				dir = DIR_SE;
			} else if(c.y == 0) {
				dir = DIR_E;
			}
		}

		return dir;
	}

	/**
	 * Converts a string to an integer representation of the direction.
	 *
	 * @param str TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static int toInt(String str) {
		int dir = DIR_INVALID;
		String s = str.toLowerCase();

		dir = find(s, getShortNames());

		if(dir == DIR_INVALID) {
			dir = find(s, getLongNames());
		}

		return dir;
	}

	private static String getLongDirectionString(int key) {
		switch(key) {
		case DIR_NW:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_NORTHWEST);

		case DIR_NE:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_NORTHEAST);

		case DIR_E:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_EAST);

		case DIR_SE:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_SOUTHEAST);

		case DIR_SW:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_SOUTHWEST);

		case DIR_W:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_WEST);
		}

		return Translations.getTranslation(Direction.class, "name.long.invalid");
	}

	private static String getShortDirectionString(int key) {
		switch(key) {
		case DIR_NW:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_NW);

		case DIR_NE:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_NE);

		case DIR_E:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_E);

		case DIR_SE:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_SE);

		case DIR_SW:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_SW);

		case DIR_W:
			return Translations.getOrderTranslation(EresseaOrderConstants.O_W);
		}

		return Translations.getTranslation(Direction.class, "name.short.invalid");
	}

	/**
	 * Returns the names of all valid directions in an all-lowercase short form.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List getShortNames() {
		if(!Locales.getOrderLocale().equals(usedLocale)) {
			shortNames = null;
			longNames = null;
		}

		if(shortNames == null) {
			usedLocale = Locales.getOrderLocale();
			shortNames = CollectionFactory.createArrayList(6);

			for(int i = 0; i < 6; i++) {
				shortNames.add(getShortDirectionString(i).toLowerCase());
			}
		}

		return shortNames;
	}

	/**
	 * Returns the names of all valid directions in an all-lowercase long form.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List getLongNames() {
		if(!Locales.getOrderLocale().equals(usedLocale)) {
			shortNames = null;
			longNames = null;
		}

		if(longNames == null) {
			usedLocale = Locales.getOrderLocale();
			longNames = CollectionFactory.createArrayList(6);

			for(int i = 0; i < 6; i++) {
				longNames.add(getLongDirectionString(i).toLowerCase());
			}
		}

		return longNames;
	}

	/**
	 * Finds pattern in the set of matches (case-sensitively) and returns the index of the hit.
	 * Pattern may be an abbreviation of any of the matches. If pattern is ambiguous or cannot be
	 * found among the matches, -1 is returned
	 *
	 * @param pattern TODO: DOCUMENT ME!
	 * @param matches TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static int find(String pattern, List matches) {
		int i = 0;
		int hitIndex = -1;
		int hits = 0;

		for(Iterator iter = matches.iterator(); iter.hasNext(); i++) {
			String match = (String) iter.next();

			if(match.startsWith(pattern)) {
				hits++;
				hitIndex = i;
			}
		}

		if(hits == 1) {
			return hitIndex;
		} else {
			return -1;
		}
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("name.long.invalid", "invalid direction");
			defaultTranslations.put("name.short.invalid", "n/a");
		}

		return defaultTranslations;
	}
}
