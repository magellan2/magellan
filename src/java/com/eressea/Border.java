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

package com.eressea;

import java.util.List;

import com.eressea.util.Direction;

/**
 * Container class for a region border based on its representation in a cr version > 45.
 *
 * @see com.eressea.Region#getBorders()
 */
public class Border extends Identifiable {
	/**
	 * The direction in which the border lies. The value must  be one of the DIR_XXX constants in
	 * class Direction.
	 */
	public int direction;

	/** The type of this border. */
	public String type;

	/**
	 * Indicates, to what extend this border type is completed. Values may range from 0 to 100, or
	 * -1 standing for an uninitialized/invalid value.
	 */
	public int buildRatio;

	/** A list containing <tt>String</tt> objects, specifying  effects on this border. */
	public List effects;

	/**
	 * Create a new <tt>Border</tt> object with the specified id.
	 *
	 * @param id the id of the border
	 */
	public Border(ID id) {
		this(id, Direction.DIR_INVALID, null, -1);
	}

	/**
	 * Create a new <tt>Border</tt> object initialized to the specified values.
	 *
	 * @param id the id of the border
	 * @param direction the direction of the border
	 * @param type the type of the border
	 * @param buildRatio indicates, to what extend this border type is completed (e.g. street)
	 */
	public Border(ID id, int direction, String type, int buildRatio) {
		super(id);
		this.direction = direction;
		this.type = type;
		this.buildRatio = buildRatio;
	}

	/**
	 * Return a string representation of this <tt>Border</tt> object.
	 *
	 * @return Border object as string.
	 */
	public String toString() {
		if(buildRatio == 100 || buildRatio < 0) {
			return type + ": " + Direction.toString(direction);
		} else {
			return type + ": " + Direction.toString(direction) + " (" + buildRatio + "%)";
		}
	}
}
