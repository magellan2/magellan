// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.awt.Color;

import com.eressea.util.logging.Logger;

/**
 * @author Ilja Pavkovic
 * A class to unify encoding and decoding of colors stored in string representation
 * (e.g. in magellan.ini).
 */
public class Colors {
	private final static Logger log = Logger.getInstance(Colors.class);

	private final static String SEPARATOR = ",";

	/**
	 * Decode a string with three integers separated by ";"
	 * to a Color.
	 */
	public static Color decode(String txt) {
		int firstSeparatorPos = txt.indexOf(SEPARATOR);
		int secondSeparatorPos = txt.lastIndexOf(SEPARATOR);
		if (firstSeparatorPos > -1 && secondSeparatorPos > -1) {
			try {
				int r = Integer.parseInt(txt.substring(0, firstSeparatorPos));
				int g = Integer.parseInt(txt.substring(firstSeparatorPos + 1, secondSeparatorPos));
				int b = Integer.parseInt(txt.substring(secondSeparatorPos + 1, txt.length()));
				return new Color(r, g, b);
			} catch (NumberFormatException e) {
				log.warn("Colors.decode(\"" + txt + "\") failed",e);
			}
		}
		return Color.black;
	}

	/**
	 * Encode a color into a String with three integers separated by 
	 * separator ";".
	 */
	public static String encode(Color c) {
		return c.getRed() + SEPARATOR + c.getGreen() + SEPARATOR + c.getBlue();
	}
}
