// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.extern;

import java.util.Properties;

import com.eressea.demo.Client;

/**
 * This interface allows it to write modules for Magellan that
 * are automatically integrated if found in one of the resource
 * paths of Magellan. All that has to be done is to implement this
 * interface and integrate the class file into a resource path.
 * The class must have the default constructor.
 * For performance reasons the name of the class must end with
 * "Module2".
 * @author Ilja Pavkovic
 */
public interface ExternalModule2 {

	/**
	 * Retrieve the MenuItemName by which the module shall be invoked.
	 * It is automatically added to the extras menu of Magellan.
	 */
	public String getMenuItemName();

	/**
	 * This method is called to run the module. Client is sufficient for all interesting instances
	 */
	public void start(Client client);

}
