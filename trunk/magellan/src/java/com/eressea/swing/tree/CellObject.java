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
 * $Id$
 */

package com.eressea.swing.tree;

import java.util.List;
import java.util.Properties;

/**
 * An interface serving as an abstraction layer between a tree cell renderer
 * and the user object to render. Implementations of this interface mainly can
 * decide what information of the user object they want to encode as icons.
 *
 * @author Sebastian
 * @version
 */
public interface CellObject {
	NodeWrapperDrawPolicy init(Properties settings,
							   NodeWrapperDrawPolicy adapter);

	NodeWrapperDrawPolicy init(Properties settings, String prefix,
							   NodeWrapperDrawPolicy adapter);

	void propertiesChanged();

	/**
	 * Returns a collection of String objects that denote the file name
	 * (without the extension) of the icons to be displayed by the tree cell
	 * renderer. The order of the elements is obeyed. A return value of null
	 * is valid to indicate that no icons should be displayed.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	List getIconNames();

	/**
	 * Controls whether the tree cell renderer should display this item more
	 * noticeably than other nodes.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	boolean emphasized();

	/**
	 * This enforces the toString method
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	String toString();
}
