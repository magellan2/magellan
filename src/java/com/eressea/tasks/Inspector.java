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

import java.util.List;

import com.eressea.Region;
import com.eressea.Unit;

/**
 * An Inspector review the given resource and returns a list of problems
 */
public interface Inspector {
	/**
	 * This Function is called to review a unit and returns a list of <tt>Problem</tt> objects. It
	 * should generally call reviewUnit(u,Problem.INFO), reviewUnit(u,Problem.WARNING)...
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewUnit(Unit u);

	/**
	 * This Function is called to review a unit and returns a list of <tt>Problem</tt> objects.
	 *
	 * @param u unit to review
	 * @param type the type of the review e.g. Problem.INFO
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewUnit(Unit u, int type);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewRegion(Region r);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewRegion(Region r, int type);

	// public List reviewGameData(GameData gd); 
}
