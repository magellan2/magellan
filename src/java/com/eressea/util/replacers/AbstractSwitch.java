// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;


/**
 * A replacer that modifies the definition list by skipping all elements
 * till next occurence of the implemented class if a certain condition is true.
 *
 * @author  Andreas
 * @version 1.0
 */
public abstract class AbstractSwitch implements Replacer{	
	/**
	 * Tests on switch activity and returns the string representation of this.
	 */
	public Object getReplacement(Object o) {
		try{
			if (isSwitchingObject(o)) {
				return TRUE;
			}
			return FALSE;
		}catch(RuntimeException exc) {
			return null;
		}
	}
	
	/**
	 * Defines the state of this switch for the given object.
	 */
	public abstract boolean isSwitchingObject(Object o);

	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description");
	}
}
