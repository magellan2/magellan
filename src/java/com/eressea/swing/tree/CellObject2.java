// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;

import java.util.List;

public interface CellObject2 extends CellObject{

	/** 
	 * 
	 */
	List getGraphicsElements();
	
	/** */
	boolean reverseOrder();
}

