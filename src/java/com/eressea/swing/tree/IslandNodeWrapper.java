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

package com.eressea.swing.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.eressea.Island;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class IslandNodeWrapper implements CellObject, SupportsClipboard {
	private Island island = null;

	// a static list (will never change) its value over all instances of IslandNodeWrapper
	private static List iconNames = CollectionFactory.singletonList("insel");

	/**
	 * Creates a new IslandNodeWrapper object.
	 *
	 * @param island TODO: DOCUMENT ME!
	 */
	public IslandNodeWrapper(Island island) {
		this.island = island;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Island getIsland() {
		return island;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return island.getName();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getIconNames() {
		return iconNames;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		for(Iterator regionIter = island.regions().iterator(); regionIter.hasNext();) {
			Iterator it = ((Region) regionIter.next()).units().iterator();

			if(it != null) {
				while(it.hasNext()) {
					Unit u = (Unit) it.next();

					if(u.getFaction().isPrivileged()) {
						if(!u.ordersConfirmed) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void propertiesChanged() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		return (island != null) ? island.getName() : toString();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings, String prefix,
									  NodeWrapperDrawPolicy adapter) {
		return null;
	}
}
