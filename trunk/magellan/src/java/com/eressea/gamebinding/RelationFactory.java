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

package com.eressea.gamebinding;

import java.util.List;

import com.eressea.Unit;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface RelationFactory {
	/**
	 * Creates a list of com.eressea.util.Relation objects  for a unit starting at order position
	 * <tt>from</tt>
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List createRelations(Unit u, int from);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 * @param orders TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List createRelations(Unit u, List orders);
}
