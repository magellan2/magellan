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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Properties;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.eressea.GameData;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.util.CollectionFactory;
import com.eressea.util.PropertiesHelper;
import com.eressea.util.Translations;


/**
 * TODO: DOCUMENT ME!
 *
 * @author Fiete
 * @version $Revision: 313 $
 */
public class UnitCapacityContextMenu extends JPopupMenu {

	private GameData data;
	private EventDispatcher dispatcher;
	private Properties settings;

	
	/**
	 * Creates new UnitCapacityContextMenu
	 *
	 * @param dispatcher EventDispatcher
	 * @param data the actual GameData or World
	 */
	public UnitCapacityContextMenu(EventDispatcher dispatcher, GameData data,Properties settings) {
		super(":-)");

		this.data = data;
		this.dispatcher = dispatcher;
		this.settings = settings;

        init();
    }
    
    private void init() {
    	
    	boolean actStatusShowAll = PropertiesHelper.getboolean(this.settings, "unitCapacityContextMenuShowAll", false);
    	JMenuItem toogleAllItems = null;
    	if (!actStatusShowAll){
    		toogleAllItems = new JMenuItem(getString("menu.toggleShowAllItems.caption"));
    	} else {
    		toogleAllItems = new JMenuItem(getString("menu.toggleShowSomeItems.caption"));
    	}
    		
        toogleAllItems.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	boolean actStatusShowAll = PropertiesHelper.getboolean(settings, "unitCapacityContextMenuShowAll", false);
                	settings.setProperty("unitCapacityContextMenuShowAll", actStatusShowAll ? "false" : "true");
                	// how to notify to rebuild the tree
                	// just for now only one idea: gamedatachangevent
                	GameDataEvent newE = new GameDataEvent(this,data);
                	dispatcher.fire(newE);
                }
            });
        add(toogleAllItems);
	}
  	
	

	private String getString(String key) {
		return Translations.getTranslation(this, key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("menu.toggleShowAllItems.caption", "show all Items");
			defaultTranslations.put("menu.toggleShowSomeItems.caption", "show Items useful here");
		}

		return defaultTranslations;
	}

}
