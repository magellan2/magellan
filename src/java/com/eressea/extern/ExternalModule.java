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

package com.eressea.extern;

import java.util.Properties;

import com.eressea.GameData;
import com.eressea.event.EventDispatcher;

/**
 * This interface allows it to write modules for Magellan that are automatically integrated if
 * found in one of the resource paths of Magellan. All that has to be done is to implement this
 * interface and integrate the class file into a resource path. The class must have the default
 * constructor. For performance reasons the name of the class must end with "Module". If classes
 * implement SelectionListener, they are automatically registered at the event dispatcher.
 *
 * @author Ulrich Küster
 */
public interface ExternalModule {
	/**
	 * Retrieve the MenuItemName by which the module shall be invoked. It is automatically added to
	 * the extras menu of Magellan.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getMenuItemName();

	/**
	 * This method is called to run the module
	 */
	public void start(GameData data, EventDispatcher dispatcher, Properties settings);
}
