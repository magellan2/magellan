// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import com.eressea.Region;
import com.eressea.rules.RegionType;

/**
 *
 * @author  Andreas
 * @version 
 */
public class MaxWorkersReplacer extends AbstractRegionReplacer {

		public Object getRegionReplacement(Region region) {
			if (region.trees!=-1 && region.sprouts!=-1 && region.getType()!=null)
				return new Integer(((RegionType)region.getType()).getMaxWorkers()-8*region.trees-4*region.sprouts);
			return null;				
		}
		
}
