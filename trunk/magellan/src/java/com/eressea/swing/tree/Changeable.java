// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// ===


package com.eressea.swing.tree;

import com.eressea.swing.context.ContextFactory;

/**
 * An interface signaling a context manager that this element can be edited.
 *
 * At this time only context menus are supported.
 *
 * @author  Andreas
 * @version 
 */
public interface Changeable {
	
	public final static int CONTEXT_MENU = 1;
	public final static int CELL_EDITOR = 2;
	
	public int getChangeModes();
	
	public ContextFactory getContextFactory();
	//public CellEditor getCellEditor();

	public Object getArgument();
}

