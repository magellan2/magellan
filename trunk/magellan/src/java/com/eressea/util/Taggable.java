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

/*
 * Taggable.java
 *
 * Created on 6. Juni 2002, 18:44
 */
package com.eressea.util;

import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public interface Taggable {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean hasTags();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean containsTag(String tag);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String putTag(String tag, String value);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTag(String tag);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String removeTag(String tag);

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void deleteAllTags();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getTagMap();
}
