// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.demo.desktop;

import java.util.Iterator;

/**
 *
 * @author  Andreas
 * @version 
 */
public interface ShortcutListener {
	
	/**
	 * Should return all short cuts this class want to be informed.
	 * The elements should be of type javax.swing.KeyStroke
	 */
	public Iterator getShortCuts();
	
	/**
	 * This method is called when a shortcut from getShortCuts() is
	 * recognized.
	 */
	public void shortCut(javax.swing.KeyStroke shortcut);

	public String getListenerDescription();
	public String getShortcutDescription(Object stroke);
}
