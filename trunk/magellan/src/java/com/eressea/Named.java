// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

/**
 * An interface granting access to the name of an object.
 */
public interface Named {

	/**
	 * Sets the name of this object.
	 */
	public void setName(String name);

	/**
	 * Returns the name of this object.
	 */
	public String getName();
}