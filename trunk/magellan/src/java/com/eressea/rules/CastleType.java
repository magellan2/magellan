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

package com.eressea.rules;

import com.eressea.ID;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class CastleType extends BuildingType implements Comparable {
	private int minSize;
	private int wage = -1;
	private int tax = -1;

	/**
	 * Creates a new CastleType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public CastleType(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param w TODO: DOCUMENT ME!
	 */
	public void setPeasantWage(int w) {
		wage = w;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPeasantWage() {
		return wage;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param t TODO: DOCUMENT ME!
	 */
	public void setTradeTax(int t) {
		tax = t;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getTradeTax() {
		return tax;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	public void setMinSize(int s) {
		this.minSize = s;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMinSize() {
		return minSize;
	}

	/**
	 * Imposes a natural ordering on CastleType objects according to their minimum size attribute.
	 * If obj is an instance of class BuildingType the return value reflects the natural ordering
	 * of the ids of this object and obj.
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object obj) {
		if(obj instanceof CastleType) {
			CastleType c = (CastleType) obj;

			if(this.minSize < c.minSize) {
				return -1;
			}

			if(this.minSize > c.minSize) {
				return 1;
			}

			return 0;
		} else {
			return super.compareTo(obj);
		}
	}
}
