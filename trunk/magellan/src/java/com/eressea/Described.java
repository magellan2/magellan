// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


/**
 * An interface granting access to the description of an object.
 */
public interface Described {
	
	/**
	 * Sets the description of this object.
	 */
	public void setDescription(String description);
	
	/**
	 * Returns the description of this object.
	 */
	public String getDescription();
}