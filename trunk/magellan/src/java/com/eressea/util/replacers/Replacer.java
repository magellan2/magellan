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

package com.eressea.util.replacers;

/**
 * Base interface for Replacer architecture.
 *
 * @author Andreas
 * @version
 */
public interface Replacer {
	/** TODO: DOCUMENT ME! */
	public static final String BLANK = "";

	/** TODO: DOCUMENT ME! */
	public static final String TRUE = "true";

	/** TODO: DOCUMENT ME! */
	public static final String FALSE = "false";

	/** TODO: DOCUMENT ME! */
	public static final String CLEAR = "clear";

	/** TODO: DOCUMENT ME! */
	public static final String NEXT_BRANCH = "else";

	/** TODO: DOCUMENT ME! */
	public static final String END = "end";

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getReplacement(Object o);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription();
}
