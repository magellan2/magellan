// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;



/**
 * An interface to be implemented by all classes that want to be
 * notified about cache clean-up.
 */
public interface CacheHandler {
	public void clearCache(Cache c);
}