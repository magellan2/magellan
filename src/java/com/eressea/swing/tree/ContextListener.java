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

/*
 * ContextListener.java
 *
 * Created on 6. Mai 2002, 15:21
 */
package com.eressea.swing.tree;

import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public interface ContextListener {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 * @param e TODO: DOCUMENT ME!
	 */
	public void contextFailed(Component c, MouseEvent e);
}
