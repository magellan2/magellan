// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.EventDispatcher;

/**
 * A class for handling the input and output of ids at certain bases.
 */
public class IDBaseConverter {
	private static int base = 10;
	private static int lDigit = Character.digit('l', base);

	/**
	 * Sets the base to use when interpreting ids.
	 * The default value is 10.
	 *
	 * @param base must not be less than Character.MIN_RADIX and not
	 * greater than Character.MAX_RADIX, else an
	 * IllegalArgumentException is thrown.
	 * @throws IllegalArgumentException
	 */
	public static void setBase(int b) {
		if (b >= Character.MIN_RADIX && b <= Character.MAX_RADIX) {
			base = b;
			lDigit = Character.digit('l', base);
		} else {
			throw new IllegalArgumentException("IDBaseConverter.setBase(): invalid base specified!");
		}
	}

	/**
	 * Returns the base that is currently set for id
	 * interpretation.
	 */
	public static int getBase() {
		return base;
	}

	/**
	 * Parses a String and interprets it as a number in the base that
	 * is currently set.
	 *
	 * @param str the string to parse. If str is null or of zero
	 * length or cannot be interpreted in the current base, an
	 * NumberFormatException is thrown.
	 * @return a decimal integer representation of the string.
	 * @throws NumberFormatException
	 */
	public static int parse(String str) throws NumberFormatException {
		return Integer.parseInt(str, base);
	}

	/**
	 * Returns a string representation of id in the currently set
	 * base. For clarity lowercase 'l's are converted to uppercase.
	 *
	 * @param id the id to convert.
	 * @return the String representation of id.
	 */
	public static String toString(int id) {
		String str = Integer.toString(id, base);
		if (lDigit != -1) {
			str = str.replace('l', 'L');
		}
		return str;
	}

	/**
	 * Returns the largest integer representing a valid id.
	 */
	public static int getMaxId() {
		return getMaxId(base);
	}
	
	/**
	 * Returns the largest integer representing a valid id by the given base.
	 * @param base
	 */
	public static int getMaxId(int base) {
		// base36 is limited to 4 digits
		return base == 10 ? Integer.MAX_VALUE : base*base*base*base - 1;
	}
	
	public static String toString(Integer id) {
		return toString(id.intValue());
	}

	private static GameDataListener listener = null;
	public static void init() {
		if(listener == null) {
			listener = new IDBaseConverterListener();
			EventDispatcher.getDispatcher().addGameDataListener(listener);
		}
		
	}

	private static class IDBaseConverterListener implements GameDataListener {
		public IDBaseConverterListener() {
			
		}
		
		public void gameDataChanged(GameDataEvent e) {
			int oldbase = IDBaseConverter.getBase();
			try {
				IDBaseConverter.setBase(e.getGameData().base);
			} catch(IllegalArgumentException iae) {
			}
		}
	}
}
