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
 * AbstractRegionSwitch.java
 *
 * Created on 1. Dezember 2001, 15:16
 */
package com.eressea.util.replacers;

import com.eressea.Region;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public abstract class AbstractRegionSwitch extends AbstractSwitch {
	/**
	 * Defines the state of this switch for the given object. The object is casted to a Region
	 * object and forwarded to the abstract <i> isSwitchingRegion()</i> method. If the object is
	 * no region, <i>false</i> is returned.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isSwitchingObject(Object o) {
		if(o instanceof Region) {
			return isSwitchingRegion((Region) o);
		}

		return false;
	}

	/**
	 * Returns the Switch state for the given region. A value of <i>true</i> tells the switch to
	 * skip some definition elements.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract boolean isSwitchingRegion(Region region);
}
