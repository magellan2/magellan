// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import com.eressea.Region;
/**
 *
 * @author  unknown
 * @version 
 */
public abstract class AbstractRegionReplacer implements Replacer {

	public Object getReplacement(Object r) {
		if ((r instanceof Region))
			return getRegionReplacement((Region)r);
		return null;
	}
		
	public abstract Object getRegionReplacement(Region r);

	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description");
	}		
}
