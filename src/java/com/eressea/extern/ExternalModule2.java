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

import com.eressea.demo.Client;

/**
 * This interface allows it to write modules for Magellan that are automatically integrated if
 * found in one of the resource paths of Magellan. All that has to be done is to implement this
 * interface and integrate the class file into a resource path. The class must have the default
 * constructor. For performance reasons the name of the class must end with "Module2". If classes
 * implement SelectionListener, they are automatically registered at the event dispatcher.
 *
 * @author Ilja Pavkovic
 */
public interface ExternalModule2 {
	/**
	 * Retrieve the MenuItemName by which the module shall be invoked. It is automatically added to
	 * the extras menu of Magellan. If the value is null there will be no menu item.
	 *
	 * @return the name of the menu 
	 */
	public String getMenuItemName();

	/**
	 * This method is called after pressing the corresponding menu item
	 */
	public void startMenuItemAction(Client client);
    
    /**
     * @return the name of the external module
     */
    public String getName();
}
