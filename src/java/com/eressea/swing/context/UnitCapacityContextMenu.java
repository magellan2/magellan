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
    	
    	// new: all Items in CR/rules.cr
    	JMenuItem toogleAllItems = null;
    	toogleAllItems = new JMenuItem(getString("menu.toggleShowAllItems.caption"));	
        toogleAllItems.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	settings.setProperty("unitCapacityContextMenuShowAll", "true");
                	settings.setProperty("unitCapacityContextMenuShowSome", "false");
                	settings.setProperty("unitCapacityContextMenuShowFriendly", "false");
                	// FIXME how to notify to rebuild the tree
                	// just for now only one idea: gamedatachangevent
                	GameDataEvent newE = new GameDataEvent(this,data);
                	dispatcher.fire(newE);
                }
            });
        add(toogleAllItems);
        
        
        // new: all Items in region, regardles which faction (= some)
        JMenuItem toogleSomeItems = null;
    	toogleSomeItems = new JMenuItem(getString("menu.toggleShowSomeItems.caption"));	
        toogleSomeItems.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	settings.setProperty("unitCapacityContextMenuShowAll", "false");
                	settings.setProperty("unitCapacityContextMenuShowSome", "true");
                	settings.setProperty("unitCapacityContextMenuShowFriendly", "false");
                	// FIXME how to notify to rebuild the tree
                	// just for now only one idea: gamedatachangevent
                	GameDataEvent newE = new GameDataEvent(this,data);
                	dispatcher.fire(newE);
                }
            });
        add(toogleSomeItems);
        
        // old = normal behaviour
        JMenuItem toogleFriendlyItems = null;
    	toogleFriendlyItems = new JMenuItem(getString("menu.toggleShowFriendlyItems.caption"));	
        toogleFriendlyItems.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	settings.setProperty("unitCapacityContextMenuShowAll", "false");
                	settings.setProperty("unitCapacityContextMenuShowSome", "false");
                	settings.setProperty("unitCapacityContextMenuShowFriendly", "true");
                	// FIXME how to notify to rebuild the tree
                	// just for now only one idea: gamedatachangevent
                	GameDataEvent newE = new GameDataEvent(this,data);
                	dispatcher.fire(newE);
                }
            });
        add(toogleFriendlyItems);
        
        
        
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
			defaultTranslations.put("menu.toggleShowAllItems.caption", "show all items");
			defaultTranslations.put("menu.toggleShowSomeItems.caption", "show all items here");
			defaultTranslations.put("menu.toggleShowFriendlyItems.caption", "show friendly items here");
		}

		return defaultTranslations;
	}

}
