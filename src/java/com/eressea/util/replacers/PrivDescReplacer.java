// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import com.eressea.Unit;
/**
 *
 * @author  Andreas
 * @version 
 */
public class PrivDescReplacer extends AbstractUnitReplacer {

	public Object getUnitReplacement(Unit r) {
		return r.privDesc;
	}
	
}
