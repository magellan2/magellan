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

package com.eressea.rules;

import com.eressea.ID;

/**
 * DOCUMENT ME!
 *
 * @author Sebastian
 * @version
 */
public abstract class Date extends Object implements ID {
	protected int iDate = 0;

	/** TODO: DOCUMENT ME! */
	public static final int TYPE_SHORT = 0;

	/** TODO: DOCUMENT ME! */
	public static final int TYPE_LONG = 1;

	/** TODO: DOCUMENT ME! */
	public static final int TYPE_PHRASE = 2;

	/** TODO: DOCUMENT ME! */
	public static final int TYPE_PHRASE_AND_SEASON = 3;

	/**
	 * Creates new Date
	 *
	 * @param iInitDate TODO: DOCUMENT ME!
	 */
	public Date(int iInitDate) {
		iDate = iInitDate;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getDate() {
		return iDate;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param newDate TODO: DOCUMENT ME!
	 */
	public void setDate(int newDate) {
		iDate = newDate;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (this == o) || (o instanceof Date && (iDate == ((Date) o).iDate));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int hashCode() {
		return iDate;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return toString(TYPE_SHORT);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param delim TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString(String delim) {
		return toString();
	}

	/**
	 * Imposes a natural ordering on date objects based on the numeric ordering of the integer date
	 * value.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		Date d = (Date) o;

		return (iDate > d.iDate) ? 1 : ((iDate == d.iDate) ? 0 : (-1));
	}

	/**
	 * Creates a copy of this Date object.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws CloneNotSupportedException TODO: DOCUMENT ME!
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param iDateType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract String toString(int iDateType);
}
