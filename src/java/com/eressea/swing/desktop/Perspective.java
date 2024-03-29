/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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

package com.eressea.swing.desktop;

import javax.swing.JPanel;

/**
 * A perspective is a composite view with a menubar 
 *
 * @author Ilja Pavkovic
 * @version $Revision$
 */
public interface Perspective {
	/*
	 * A Perspective holds informations about the desktop view
	 */

	public JPanel getJPanel();
}
