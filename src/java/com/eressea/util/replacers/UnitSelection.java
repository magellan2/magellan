/*
 * FactionSelectableHelp.java
 *
 * Created on 30. Dezember 2001, 16:01
 */

package com.eressea.util.replacers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.eressea.util.CollectionFactory;


import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.util.filters.UnitFilter;

/**
 *
 * @author  Andreas
 * @version 
 */
public class UnitSelection extends EnvironmentPart {
	
	protected List filters = CollectionFactory.createLinkedList();
	
	public void reset() {
		filters.clear();
	}
	
	public void addFilter(UnitFilter f) {
		filters.add(f);
	}
	
	public void removeFilters(Class filterClass) {
		Iterator it = filters.iterator();
		while(it.hasNext()) {
			Class c = it.next().getClass();
			if (filterClass.equals(c)) {
				it.remove();
			}
		}
	}
	
	public void removeFilter(UnitFilter f) {
		filters.remove(f);
	}
	
	public void removeAllFilters() {
		filters.clear();
	}

	public boolean belongsTo(Object o) {
		if (!(o instanceof Unit))
			return false;
		if (filters.size() == 0) {
			return true;
		}
		Unit u=(Unit)o;
		Iterator it = filters.iterator();
		while(it.hasNext()) {
			UnitFilter filter = (UnitFilter)it.next();
			if (!filter.acceptUnit(u)) {
				return false;
			}
		}
		return true;
	}
	
	public Collection getUnits(Region r) {
		Collection retList = CollectionFactory.createLinkedList(r.units());
		Iterator it = filters.iterator();
		int i = 0;
		while(it.hasNext()) {
			retList = ((UnitFilter)it.next()).acceptUnits(retList, true);
			if (retList.size() == 0) {
				return retList;
			}
			i++;
		}
		return retList;
	}
}
