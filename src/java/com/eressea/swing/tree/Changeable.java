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

package com.eressea.swing.tree;

import com.eressea.swing.context.ContextFactory;

/**
 * An interface signaling a context manager that this element can be edited. At
 * this time only context menus are supported.
 *
 * @author Andreas
 * @version
 */
public interface Changeable {
	/** TODO: DOCUMENT ME! */
	public static final int CONTEXT_MENU = 1;

	/** TODO: DOCUMENT ME! */
	public static final int CELL_EDITOR = 2;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getChangeModes();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ContextFactory getContextFactory();

	//public CellEditor getCellEditor();
	public Object getArgument();
}
