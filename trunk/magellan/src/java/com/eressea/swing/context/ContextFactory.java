// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// ===


package com.eressea.swing.context;

/**
 * Interface for automated context menu creation.
 *
 * @author  Andreas
 * @version 
 */
public interface ContextFactory {

	public javax.swing.JPopupMenu createContextMenu(com.eressea.GameData data, Object argument, java.util.Collection selectedObjects, javax.swing.tree.DefaultMutableTreeNode node);
	
}

