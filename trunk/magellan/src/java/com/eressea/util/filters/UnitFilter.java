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

package com.eressea.util.filters;

import java.util.Collection;
import java.util.Iterator;

import com.eressea.Unit;

import com.eressea.util.CollectionFactory;

/**
 * The base class for filtering units. Designed after FileFilter and similar
 * interfaces, but as an abstract class to have a short-cut for Collections
 * implemented. ,
 *
 * @author Andreas
 * @version
 */
public abstract class UnitFilter {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract boolean acceptUnit(Unit u);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param col TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection acceptUnits(Collection col) {
		return acceptUnits(col, false);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param col TODO: DOCUMENT ME!
	 * @param useThis TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection acceptUnits(Collection col, boolean useThis) {
		Collection col2 = null;

		if(useThis) {
			col2 = col;
		} else {
			col2 = CollectionFactory.createLinkedList(col);
		}

		Iterator it = col2.iterator();

		while(it.hasNext()) {
			if(!acceptUnit((Unit) it.next())) {
				it.remove();
			}
		}

		return col2;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		String ret = com.eressea.util.Translations.getTranslation(UnitFilter.class,
																  getClass()
																	  .getName());

		if(ret == null) {
			ret = "UnitFilter";
		}

		return ret;
	}
}
