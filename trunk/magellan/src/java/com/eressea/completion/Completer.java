// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.completion;

import java.util.List;

import com.eressea.Unit;

/**
 *
 * @author  Andreas
 * @version 
 */
public interface Completer {
	
	public List getCompletions(Unit u, String line, List old);

}

