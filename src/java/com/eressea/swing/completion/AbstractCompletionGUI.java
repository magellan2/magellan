/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 * $Id$
 */

package com.eressea.swing.completion;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public abstract class AbstractCompletionGUI implements CompletionGUI {
	/**
	 * Returns the title of this GUI.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return getTitle();
	}

	/**
	 * Returns the name of this CompletionGUI. This implementation returns the
	 * content of "gui.title" key in the current Resource Bundle.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTitle() {
		return getString("gui.title");
	}

	/**
	 * Returns a translation for the specified key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this, key);
	}
}
