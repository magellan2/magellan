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

package com.eressea.rules;

import com.eressea.ID;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class OptionCategory extends ObjectType {
	private int     bitMask  = 0;
	private boolean isActive = false;
	private boolean isOrder  = false;

	/**
	 * Creates a new OptionCategory object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public OptionCategory(ID id) {
		super(id);
	}

	/**
	 * copy constructor
	 *
	 * @param orig TODO: DOCUMENT ME!
	 */
	public OptionCategory(OptionCategory orig) {
		super(orig.getID());
		bitMask  = orig.bitMask;
		isActive = orig.isActive;
		isOrder  = orig.isOrder;
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
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setActive(boolean bool) {
		this.isActive = bool;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isActive() {
		return this.isActive;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setOrder(boolean bool) {
		this.isOrder = bool;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isOrder() {
		return this.isOrder;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (o instanceof OptionCategory) &&
			   ((OptionCategory) o).getID().equals(this.getID());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((OptionCategory) o).getID());
	}
}
