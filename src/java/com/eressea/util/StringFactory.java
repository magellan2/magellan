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
 * $Id$
 */

package com.eressea.util;

import java.util.Map;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class StringFactory {
	private static final StringFactory sf = new StringFactory();

	private StringFactory() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static StringFactory getFactory() {
		return sf;
	}

	private Map strings = CollectionFactory.createHashMap();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String intern(String s) {
		String is = (String) strings.get(s);

		if(is == null) {
			is = getOptimizedString(s);
			strings.put(is, is);
		}

		return is;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getOptimizedString(String s) {
		// copy all strings into new char and recreate string with it. 
		// Prevent inefficient use of char[]
		char allchars[] = new char[s.length()];
		s.getChars(0, s.length(), allchars, 0);

		return new String(allchars);
	}
}
