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
import java.util.StringTokenizer;

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
		name = doExpandValue(name,  "OrderWriter.outputFile", "|");
		name = doExpandValue(name,  "CRWriterDialog.outputFile", "|");
		name = doExpandValue(name,  "Client.fileHistory", "|");
		name = renameProperty(name, "DirectoryHistory", "HistoryAccessory.directoryHistory" );
		name = doExpandValue(name,  "HistoryAccessory.directoryHistory", "|");
		// name = doExpandFactionColors(name, "GeomRenderer.FactionColors");
		// name = doExpandFactionColors(name, "Minimap.FactionColors");
		// name = doExpandFactionColors(name, "Minimap.RegionColors");

	}

	/** 
	 * 
	 */
	private String doExpandFactionColors(String oldName, String key) {
		if(oldName.equals(key)) {
			int i=0;
			String delim = ";";
			for(StringTokenizer st=new StringTokenizer(getProperty(oldName), delim); st.hasMoreTokens(); i++) {
				String value = st.nextToken();
				setProperty(oldName+".name."+i, value);

				value = st.nextToken();
				value += delim;
				value += st.nextToken();
				value += delim;
				value += st.nextToken();
				setProperty(oldName+".color."+i, value);
			}
			remove(oldName);
			String newName = oldName+".name.count";
			setProperty(newName, String.valueOf(i));
			String newName2 = oldName+".color.count";
			setProperty(newName2, String.valueOf(i));
			log.error("SelfCleaningProperties.doClean: Expanded property "+oldName+" to "+newName);
			return newName;
		}
		return oldName;
	}

	/** 
	 * rename property e.g. Ausdauer.compareValue to ClientPreferences.compareValue.Ausdauer
	 */
	private String doCleanCompareValue(String name) {
		if(name.endsWith(".compareValue")) {
			String newName = "ClientPreferences.compareValue."+name.substring(0,name.lastIndexOf(".compareValue"));
			renameProperty(name,newName);
			return newName;
		}
		return name;
	}

	/** 
	 * Expands value in form a|b|... for property <tt>key</tt>
	 */
	private String doExpandValue(String name, String key, String delim) {
		if(name.equals(key)) {
			return expandList(name, delim);
		}
		return name;
	}

	/** 
	 * generic function to expand a string into a list
	 */
	private String expandList(String oldName, String delim) {
		int i=0;
		for(StringTokenizer st=new StringTokenizer(getProperty(oldName), delim); st.hasMoreTokens(); i++) {
			String value = st.nextToken();
			setProperty(oldName+"."+i, value);
		}
		remove(oldName);
		String newName = oldName+".count";
		setProperty(newName, String.valueOf(i));
		log.error("SelfCleaningProperties.doClean: Expanded property "+oldName+" to "+newName);
		return newName;
	}

	/**
	 * generic function to rename a property
	 */ 
	private String renameProperty(String key, String oldName, String newName) {
		if(key.equals(oldName)) {
			return renameProperty(oldName,newName);
		}
		return key;
	}

	/**
	 * generic function to rename a property
	 */ 
	private String renameProperty(String oldName, String newName) {
		String value = getProperty(oldName);
		remove(oldName);
		setProperty(newName,value);
		log.error("SelfCleaningProperties.doClean: Renamed property "+oldName+" to "+newName);
		return newName;
	}
		
}
