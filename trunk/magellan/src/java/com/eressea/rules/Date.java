// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===



package com.eressea.rules;

import com.eressea.ID;

/**
 *
 * @author  Sebastian
 * @version
 */
abstract public class Date extends Object implements ID {
	protected int iDate = 0;

	public static final int TYPE_SHORT = 0;
	public static final int TYPE_LONG = 1;
	public static final int TYPE_PHRASE = 2;
	public static final int TYPE_PHRASE_AND_SEASON = 3;

	/** Creates new Date */
	public Date(int iInitDate) {
		iDate = iInitDate;
	}

	public int getDate() {
		return iDate;
	}

	public void setDate(int newDate) {
		iDate = newDate;
	}

	public boolean equals(Object o) {
		return this == o ||
			(o instanceof Date && iDate == ((Date)o).iDate);
	}

	public int hashCode() {
		return iDate;
	}

	public String toString() {
		return toString(TYPE_SHORT);
	}

	public String toString(String delim) {
		return toString();
	}

	/**
	 * Imposes a natural ordering on date objects based on the
	 * numeric ordering of the integer date value.
	 */
	public int compareTo(Object o) {
		Date d = (Date) o;
		return iDate > d.iDate ? 1 : (iDate == d.iDate ? 0 : -1);
	}

	/**
	 * Creates a copy of this Date object.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	abstract public String toString(int iDateType);
}
