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

import javax.swing.KeyStroke;

/**
 * An interface for extended short-cut tasks. An extended short-cut is one that
 * redirects the following short-cut to a given listener.
 *
 * @author Andreas
 * @version
 */
public interface ExtendedShortcutListener extends ShortcutListener {
	/**
	 * Returns wether the given stroke is for an extended short-cut.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isExtendedShortcut(KeyStroke stroke);

	/**
	 * Returns the listener responsible for the sub-short-cuts
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShortcutListener getExtendedShortcutListener(KeyStroke stroke);
}
