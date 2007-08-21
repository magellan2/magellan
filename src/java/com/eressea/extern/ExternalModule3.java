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
import javax.swing.JMenu;

/**
 * This interface allows it to write modules for Magellan that are automatically integrated if
 * found in one of the resource paths of Magellan. All that has to be done is to implement this
 * interface and integrate the class file into a resource path. The class must have the default
 * constructor. For performance reasons the name of the class must end with "Module3".
 * If classes implement SelectionListener, they are NOT automatically registered at the event dispatcher.
 *
 * Module3 classes can add a master menu to the menubar - thats the difference!
 *
 * @author Fiete
 */
public interface ExternalModule3 {
	
	
	/**
	 * 
	 * The called JMenu will be added to the magellan top menubar
	 * @param The client, if some client infos are needed for menubuildung
	 * @return The JMenu to add
	 * 
	 * @author Fiete
	 * 
	 */
	
	public JMenu getJMenu(Client client);
	
   
    /**
     * @return the name of the external module
     */
    public String getName();
}
