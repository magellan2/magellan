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

package com.eressea.util.replacers;

import com.eressea.Region;
import com.eressea.RegionResource;
import com.eressea.rules.ItemType;
import com.eressea.rules.RegionType;

/**
 * DOCUMENT ME!
 *
 * @author Fiete
 * @version
 */
public class MallornReplacer extends AbstractRegionReplacer {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param region TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getRegionReplacement(Region region) {
		if (!region.mallorn){
			return new Integer(0);
		}
		ItemType mallornType = region.getData().rules.getItemType("Mallorn");
		if (mallornType==null){
			return new Integer(0);
		}
		RegionResource mallornResource = region.getResource(mallornType);
		if (mallornResource==null){
			return new Integer(0);
		}
		return new Integer(mallornResource.getAmount());
	}
}
