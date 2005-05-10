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

package com.eressea.swing.context;

import java.util.Collection;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import com.eressea.GameData;
import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.swing.tree.UnitNodeWrapper;

/**
 * Context Factory for unit contexts.
 *
 * @author Andreas
 * @version
 */
public class UnitContextFactory implements ContextFactory {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param data TODO: DOCUMENT ME!
	 * @param argument TODO: DOCUMENT ME!
	 * @param selectedObjects TODO: DOCUMENT ME!
	 * @param node TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JPopupMenu createContextMenu(EventDispatcher dispatcher,
            GameData data, Object argument,
            Collection selectedObjects,
            DefaultMutableTreeNode node) {
		if(argument instanceof Unit) {
			return new UnitContextMenu((Unit) argument, selectedObjects,
									   dispatcher, data);
		} else if(argument instanceof UnitNodeWrapper) {
			return new UnitContextMenu(((UnitNodeWrapper) argument).getUnit(), selectedObjects,
									   dispatcher, data);
		}

		return null;
	}
}
