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

package com.eressea.util.replacers;

import com.eressea.Region;

import com.eressea.rules.RegionType;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class MaxWorkersReplacer extends AbstractRegionReplacer {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param region TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getRegionReplacement(Region region) {
		if((region.trees != -1) && (region.sprouts != -1) && (region.getType() != null)) {
			return new Integer(((RegionType) region.getType()).getInhabitants() -
							   (8 * region.trees) - (4 * region.sprouts));
		}

		return null;
	}
}
