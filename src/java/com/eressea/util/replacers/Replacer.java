// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===
package com.eressea.util.replacers;

/**
 * Base interface for Replacer architecture.
 *
 * @author  Andreas
 * @version 
 */
public interface Replacer {
	public final static String BLANK       = "";
	public final static String TRUE        = "true";
	public final static String FALSE       = "false";
	public final static String CLEAR       = "clear";
	public final static String NEXT_BRANCH = "else";
	public final static String END         = "end";

	public Object getReplacement(Object o);

	public String getDescription();
}

