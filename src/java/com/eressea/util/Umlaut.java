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

import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author Sebastian
 * @version
 */
public class Umlaut {
	private static final char     UMLAUTS[]    = {
													 '�', '�', '�', '�', '�',
													 '�', '�'
												 };
	private static final String   EXPANSIONS[] = {
													 "Ae", "Oe", "Ue", "ae",
													 "oe", "ue", "ss"
												 };
	private static final Map recodedStrings = CollectionFactory.createHashtable();

	/**
	 * Expand all umlauts in a string. Note that uppercase umlauts are
	 * converted to mixed case expansions (� -> Ae).
	 *
	 * @param string TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static String convertUmlauts(String string) {
		return recode(string, UMLAUTS, EXPANSIONS);
	}

	/**
	 * Search <tt>string</tt> for a character contained in <tt>keys[]</tt> and
	 * replace it with the corresponding string in <tt>values[]</tt>.
	 *
	 * @param string the string to recode.
	 * @param keys an array of chars to be replaced in     <tt>string</tt>.
	 * @param values an array of the strings that are used as  replacements for
	 * 		  the corresponding char in  <tt>keys</tt>.
	 *
	 * @return a string with all occurrences of an element of the <tt>keys</tt>
	 * 		   array replaced by the corresponding     element in the
	 * 		   <tt>values</tt> array.
	 */
	public static String recode(String string, char keys[], String values[]) {
		// recoding is kind of expensive, so store recoded strings 
		String s = (String) recodedStrings.get(string);

		if(s == null) {
			s = recodeIt(string, UMLAUTS, EXPANSIONS);
			recodedStrings.put(StringFactory.getFactory().intern(string), s);
		}

		return s;
	}

	private static String recodeIt(String string, char keys[], String values[]) {
		char		   chars[]     = string.toCharArray();
		StringBuffer   sb		   = null;
		boolean		   foundUmlaut = false;

		for(int i = 0; i < chars.length; i++) {
			foundUmlaut = false;

			char c = chars[i];

			for(int j = 0; j < keys.length; j++) {
				if(c == keys[j]) {
					if(sb == null) {
						sb = new StringBuffer(string.length() +
											  values[j].length());
						sb.insert(0, chars, 0, i);
					}

					sb.append(values[j]);
					foundUmlaut = true;
				}
			}

			if((foundUmlaut == false) && (sb != null)) {
				sb.append(c);
			}
		}

		if(sb == null) {
			return StringFactory.getFactory().intern(string);
		} else {
			return StringFactory.getFactory().intern(sb.toString());
		}
	}

	/**
	 * Expand all umlauts in a string and convert it to uppercase.
	 *
	 * @param str the string to be normalized.
	 *
	 * @return the uppercase version of <tt>str</tt> with all umlauts expanded.
	 */
	public static String normalize(String str) {
		return StringFactory.getFactory().intern(Umlaut.convertUmlauts(str)
													   .toUpperCase());
	}
}
