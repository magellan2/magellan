// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// ===

package com.eressea.swing.context;

import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.swing.tree.UnitNodeWrapper;
/**
 * Context Factory for unit contexts.
 *
 * @author  Andreas
 * @version 
 */
public class UnitContextFactory implements ContextFactory {
	
	public javax.swing.JPopupMenu createContextMenu(com.eressea.GameData data, Object argument, java.util.Collection selectedObjects, javax.swing.tree.DefaultMutableTreeNode node) {
		if (argument instanceof Unit) {
			return new UnitContextMenu((Unit)argument, selectedObjects, EventDispatcher.getDispatcher(), data);
		} else if (argument instanceof UnitNodeWrapper) {
			return new UnitContextMenu(((UnitNodeWrapper)argument).getUnit(), selectedObjects, EventDispatcher.getDispatcher(), data);
		}
		return null;
	}
		
}
