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
 */

package com.eressea.demo.desktop;

import java.util.Iterator;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public interface ShortcutListener {
	/**
	 * Should return all short cuts this class want to be informed. The
	 * elements should be of type javax.swing.KeyStroke
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getShortCuts();

	/**
	 * This method is called when a shortcut from getShortCuts() is recognized.
	 */
	public void shortCut(javax.swing.KeyStroke shortcut);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getListenerDescription();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param stroke TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getShortcutDescription(Object stroke);
}
