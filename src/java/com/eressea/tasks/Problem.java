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

package com.eressea.tasks;

import com.eressea.HasRegion;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface Problem {
	/** TODO: DOCUMENT ME! */
	public static final int INFORMATION = 0;

	/** TODO: DOCUMENT ME! */
	public static final int WARNING = 1;

	/** TODO: DOCUMENT ME! */
	public static final int ERROR = 2;

	/**
	 * returns the creating inspector
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Inspector getInspector();

	/**
	 * returns the type of the problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getType();

	/**
	 * returns the type of the problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLine();

	/**
	 * returns the object this problem criticizes
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public HasRegion getObject();

	/**
	 * returns the originating object
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getSource();

	/**
	 * returns the message of the problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString();
}
