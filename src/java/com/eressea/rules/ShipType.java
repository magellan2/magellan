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
public class ShipType extends UnitContainerType {
	private int maxSize = -1;
	private int buildLevel = -1;
	private int range = -1;
	private int capacity = -1;
	private int captainLevel = -1;
	private int sailorLevel = -1;

	/**
	 * Creates a new ShipType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public ShipType(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	public void setMaxSize(int s) {
		maxSize = s;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setBuildLevel(int l) {
		buildLevel = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getBuildLevel() {
		return buildLevel;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 */
	public void setRange(int r) {
		range = r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getRange() {
		return range;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setCapacity(int c) {
		capacity = c;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setCaptainSkillLevel(int l) {
		captainLevel = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getCaptainSkillLevel() {
		return captainLevel;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setSailorSkillLevel(int l) {
		sailorLevel = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSailorSkillLevel() {
		return sailorLevel;
	}
}
