/*
 * AbstractRegionSwitch.java
 *
 * Created on 1. Dezember 2001, 15:16
 */

package com.eressea.util.replacers;

import com.eressea.Region;
/**
 *
 * @author  Andreas
 * @version 
 */
public abstract class AbstractRegionSwitch extends AbstractSwitch {

	/**
	 * Defines the state of this switch for the given object. The object
	 * is casted to a Region object and forwarded to the abstract <i>
	 * isSwitchingRegion()</i> method. If the object is no region, <i>false</i>
	 * is returned.
	 */
	public boolean isSwitchingObject(Object o) {
		if (o instanceof Region)
			return isSwitchingRegion((Region)o);
		return false;
	}
	
	/**
	 * Returns the Switch state for the given region. A value of <i>true</i> tells
	 * the switch to skip some definition elements.
	 */
	public abstract boolean isSwitchingRegion(Region region);
}
