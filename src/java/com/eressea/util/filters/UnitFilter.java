// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// ===

package com.eressea.util.filters;

import java.util.Collection;
import java.util.Iterator;


import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

/**
 * The base class for filtering units.
 *
 * Designed after FileFilter and similar interfaces, but as an abstract class
 * to have a short-cut for Collections implemented.
 *,
 * @author Andreas
 * @version
 */
public abstract class UnitFilter {

	public abstract boolean acceptUnit(Unit u);
	
	public Collection acceptUnits(Collection col) {
		return acceptUnits(col, false);
	}
	
	public Collection acceptUnits(Collection col, boolean useThis) {
		Collection col2 = null;
		if (useThis) {
			col2 = col;
		} else {
			col2 = CollectionFactory.createLinkedList(col);
		}
		Iterator it = col2.iterator();
		while(it.hasNext()) {
			if (!acceptUnit((Unit)it.next())) {
				it.remove();
			}
		}
		return col2;
	}
	
	public String getName() {
		String ret = com.eressea.util.Translations.getTranslation(UnitFilter.class,getClass().getName());
		if(ret == null) {
			ret = "UnitFilter";
		}
		return ret;
	}
}
