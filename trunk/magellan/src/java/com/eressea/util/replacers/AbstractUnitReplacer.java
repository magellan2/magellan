// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import com.eressea.Unit;
/**
 *
 * @author  unknown
 * @version 
 */
public abstract class AbstractUnitReplacer implements Replacer {

	public Object getReplacement(Object r) {
		if (r instanceof Unit)
			return getUnitReplacement((Unit)r);
		return null;
	}
		
	public abstract Object getUnitReplacement(Unit r);

	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description");
	}		
}
