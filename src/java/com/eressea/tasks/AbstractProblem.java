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
public abstract class AbstractProblem implements Problem {
	protected Object source;
	protected HasRegion object;
	protected Inspector inspector;
	protected String message;
	protected int line;

	/**
	 * Creates a new AbstractProblem object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param o TODO: DOCUMENT ME!
	 * @param i TODO: DOCUMENT ME!
	 * @param m TODO: DOCUMENT ME!
	 */
	public AbstractProblem(Object s, HasRegion o, Inspector i, String m) {
		this(s, o, i, m, -1);
	}

	/**
	 * Creates a new AbstractProblem object.
	 *
	 * @param s TODO: DOCUMENT ME!
	 * @param o TODO: DOCUMENT ME!
	 * @param i TODO: DOCUMENT ME!
	 * @param m TODO: DOCUMENT ME!
	 * @param l TODO: DOCUMENT ME!
	 *
	 * @throws NullPointerException TODO: DOCUMENT ME!
	 */
	public AbstractProblem(Object s, HasRegion o, Inspector i, String m, int l) {
		if((s == null) || (o == null) || (i == null) || (m == null)) {
			throw new NullPointerException();
		}

		source = s;
		object = o;
		inspector = i;
		message = m;
		line = l;
	}

	/**
	 * returns the type of the problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract int getType();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLine() {
		return line;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Inspector getInspector() {
		return inspector;
	}

	/**
	 * returns the object which originated this problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * returns the object which is attached to this problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public HasRegion getObject() {
		return object;
	}

	/**
	 * returns the message of the problem
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return message;
	}
}
