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

package com.eressea.gamebinding;

import java.util.List;

import com.eressea.Unit;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface TempUnitFactory {
	/**
	 * Returns the orders necessary to issue the creation of all the child temp
	 * units of this unit.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getTempOrders(Unit unit);

	/**
			 *
			 */
	public List extractTempUnits(Unit unit);
}
