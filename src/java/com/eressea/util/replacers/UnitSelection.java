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

/*
 * FactionSelectableHelp.java
 *
 * Created on 30. Dezember 2001, 16:01
 */
package com.eressea.util.replacers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.eressea.Region;
import com.eressea.Unit;

import com.eressea.util.CollectionFactory;
import com.eressea.util.filters.UnitFilter;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class UnitSelection extends EnvironmentPart {
	protected List filters = CollectionFactory.createLinkedList();

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void reset() {
		filters.clear();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param f TODO: DOCUMENT ME!
	 */
	public void addFilter(UnitFilter f) {
		filters.add(f);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param filterClass TODO: DOCUMENT ME!
	 */
	public void removeFilters(Class filterClass) {
		Iterator it = filters.iterator();

		while(it.hasNext()) {
			Class c = it.next().getClass();

			if(filterClass.equals(c)) {
				it.remove();
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param f TODO: DOCUMENT ME!
	 */
	public void removeFilter(UnitFilter f) {
		filters.remove(f);
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void removeAllFilters() {
		filters.clear();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean belongsTo(Object o) {
		if(!(o instanceof Unit)) {
			return false;
		}

		if(filters.size() == 0) {
			return true;
		}

		Unit u = (Unit) o;
		Iterator it = filters.iterator();

		while(it.hasNext()) {
			UnitFilter filter = (UnitFilter) it.next();

			if(!filter.acceptUnit(u)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getUnits(Region r) {
		Collection retList = CollectionFactory.createLinkedList(r.units());
		Iterator it = filters.iterator();
		int i = 0;

		while(it.hasNext()) {
			retList = ((UnitFilter) it.next()).acceptUnits(retList, true);

			if(retList.size() == 0) {
				return retList;
			}

			i++;
		}

		return retList;
	}
}
