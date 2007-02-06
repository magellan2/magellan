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
import java.util.Properties;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import com.eressea.GameData;
import com.eressea.UnitContainer;
import com.eressea.event.EventDispatcher;
import com.eressea.swing.tree.FactionNodeWrapper;
import com.eressea.swing.tree.RegionNodeWrapper;
import com.eressea.swing.tree.UnitContainerNodeWrapper;

/**
 * Context Factory for unit-container contexts.
 *
 * @author Andreas
 * @version
 */
public class UnitContainerContextFactory implements ContextFactory {
	protected Properties settings;

	/**
	 * Creates a new UnitContainerContextFactory object.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public UnitContainerContextFactory(Properties settings) {
		this.settings = settings;
	}

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
            
                    GameData data, 
                                                    Object argument,
													Collection selectedObjects,
													DefaultMutableTreeNode node) {
		if(argument instanceof UnitContainer) {
			return new UnitContainerContextMenu((UnitContainer) argument,
												dispatcher, data, settings,selectedObjects);
		} else if(argument instanceof RegionNodeWrapper) {
			return new UnitContainerContextMenu(((RegionNodeWrapper) argument).getRegion(),
												dispatcher, data, settings,selectedObjects);
		} else if(argument instanceof FactionNodeWrapper) {
			return new UnitContainerContextMenu(((FactionNodeWrapper) argument).getFaction(),
												dispatcher, data, settings,selectedObjects);
		} else if(argument instanceof UnitContainerNodeWrapper) {
			return new UnitContainerContextMenu(((UnitContainerNodeWrapper) argument).getUnitContainer(),
												dispatcher, data, settings,selectedObjects);
		}

		return null;
	}
}
