// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.completion;



/**
 *
 * @author  Andreas
 * @version
 */
public abstract class AbstractCompletionGUI implements CompletionGUI {
	
	/**
	 * Returns the title of this GUI.
	 */
	public String toString() {
		return getTitle();
	}
	
	/**
	 * Returns the name of this CompletionGUI. This implementation returns
	 * the content of "gui.title" key in the current Resource Bundle.
	 */
	public String getTitle() {
		return getString("gui.title");
	}
	
	/**
	 * Returns a translation for the specified key.
	 */
	protected String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this,key);
	}	
}
