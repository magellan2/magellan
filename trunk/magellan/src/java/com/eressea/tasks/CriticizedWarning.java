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

package com.eressea.tasks;

import com.eressea.HasRegion;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class CriticizedWarning extends AbstractProblem implements Problem {
	/**
									 *
									 */
	public CriticizedWarning(Object s, HasRegion o, Inspector i, String m) {
		super(s, o, i, m);
	}

	/**
	 * Creates a new CriticizedWarning object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param o TODO: DOCUMENT ME!
	 * @param i TODO: DOCUMENT ME!
	 * @param m TODO: DOCUMENT ME!
	 * @param l TODO: DOCUMENT ME!
	 */
	public CriticizedWarning(Object s, HasRegion o, Inspector i, String m, int l) {
		super(s, o, i, m, l);
	}

	/**
	 * returns the type of the problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getType() {
		return WARNING;
	}
}
