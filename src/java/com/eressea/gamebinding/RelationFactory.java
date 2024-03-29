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
 * A RelationFactory creates relatations of a unit to other game objects according to the unit's
 * orders. This interface is implemented by game specific classes.
 * 
 * @author $author$
 * @version $Revision$
 */
public interface RelationFactory {
	/**
	 * Creates a list of com.eressea.util.Relation objects  for a unit starting at order position
	 * <tt>from</tt>.
	 *
	 * @param u The unit
	 * @param from The line of the <code>unit</code>'s orders where to start. Must be > 0
	 * @return A List of Relations for this unit
	 */
	public List createRelations(Unit u, int from);

	/**
	 * Creates a list of com.eressea.util.Relation objects  for a unit using <code>orders</code>.
	 *
	 * @param u The unit
	 * @param orders Use these orders instead of the unit's orders
	 * @return A List of Relations for this unit
	 */
	public List createRelations(Unit u, List orders);
}
