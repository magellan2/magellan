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
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AllianceCategory extends ObjectType {
	private int bitMask = -1;

	/**
	 * Creates a new AllianceCategory object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public AllianceCategory(ID id) {
		super(id);
	}

	/**
	 * copy constructor
	 *
	 * @param orig TODO: DOCUMENT ME!
	 */
	public AllianceCategory(AllianceCategory orig) {
		super(orig.getID());
		bitMask = orig.bitMask;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param mask TODO: DOCUMENT ME!
	 */
	public void setBitMask(int mask) {
		this.bitMask = mask;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getBitMask() {
		return this.bitMask;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (o == this) ||
			   ((o instanceof AllianceCategory) &&
			   ((AllianceCategory) o).getID().equals(this.getID()));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		int anotherBitMask = ((AllianceCategory) o).bitMask;

		return (bitMask < anotherBitMask) ? (-1)
										  : ((bitMask == anotherBitMask) ? 0 : 1);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return "AllianceCategory[name=" + name + ", bitMask=" + bitMask + "]";
	}
}
