// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===
 
package com.eressea.util;

import java.io.InputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.Properties;

import com.eressea.util.logging.Logger;

/**
 *
 * This is a self cleaning properties implementation. The cleaning is done after 
 * loading the properties.
 * 
 * @author  Ilja Pavkovic
 */
public class SelfCleaningProperties extends OrderedOutputProperties {
	private final static Logger log = Logger.getInstance(SelfCleaningProperties.class);
	
	/** Creates new SelfCleaningProperties */
    public SelfCleaningProperties() {
    }
	
	public SelfCleaningProperties(Properties def) {
		super(def);
	}

    public synchronized void load(InputStream inStream) throws IOException {
		super.load(inStream);
		doClean();
	}

	/** 
	 * This operation iterates other a copy of all keys to check if they can be cleaned
	 */
	private void doClean() {
		// operate on a copy of the keys as they can be deleted/changed while runtime
		for(Iterator iter=CollectionFactory.createHashSet(keySet()).iterator(); iter.hasNext(); ) {
			String name = (String) iter.next();
			doClean(name);
		}
	}
	
	/** 
	 * this operation check possible clean states on a given property name.
	 * Add more cleanings here (and take care to return a possibly new key name value!
	 */
	private void doClean(String name) {
		name = doCleanCompareValue(name);
	}

	/** 
	 * rename property e.g. Ausdauer.compareValue to ClientPreferences.compareValue.Ausdauer
	 */
	private String doCleanCompareValue(String name) {
		if(name.endsWith(".compareValue")) {
			String newName = "ClientPreferences.compareValue."+name.substring(0,name.lastIndexOf(".compareValue"));
			renameProperty(name,newName);
			name = newName;
		}
		return name;
	}

	/**
	 * generice function to rename a property
	 */ 
	private void renameProperty(String oldName, String newName) {
		String value = getProperty(oldName);
		remove(oldName);
		setProperty(newName,value);
		log.error("SelfCleaningProperties.doClean: Renamed property "+oldName+" to "+newName);

	}
		
}
