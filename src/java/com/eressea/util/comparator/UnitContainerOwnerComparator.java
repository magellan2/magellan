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

package com.eressea.util.comparator;

import java.util.Comparator;

import com.eressea.Unit;
import com.eressea.UnitContainer;

/**
 * A comparator imposing a total ordering on unit containers based on their owner
 */
public class UnitContainerOwnerComparator implements Comparator {
    Comparator unitComparator;
    /**
	 * Creates a new UnitContainerOwnerComparator object.
	 */
	public UnitContainerOwnerComparator(Comparator unitComparator) {
        this.unitComparator = unitComparator;
	}
    
	
	/**
	 * Compares its two arguments for order according to their ids.
	 *
	 * @return a container with an owner is less then a container without an owner.
	 */
	public int compare(Object o1, Object o2) {
        UnitContainer c1 = (UnitContainer) o1;
        UnitContainer c2 = (UnitContainer) o2;
        
        Unit owner1 = c1.getOwnerUnit();
        Unit owner2 = c2.getOwnerUnit();
        
        if(owner1 != null && owner2 != null) {
            return unitComparator != null ? unitComparator.compare(owner1, owner2) : 0;
        } else {
            return owner2 == null ? -1 : 1;
        }
	}
}
