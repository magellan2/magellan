// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.eressea.util.logging.Logger;

/**
 *
 * This objects keep track of variable access and stores, if a key is accessed in the session
 * As we lost control over the variables (some are even dynamically) we need to count the overall 
 * access in multiple sessions.
 * 
 * @author  Ilja Pavkovic
 * @version 
 */
// public class AgingProperties extends OrderedOutputProperties {
public class AgingProperties extends SelfCleaningProperties {
	private final static Logger log = Logger.getInstance(AgingProperties.class);

	// store in this set, if the actual session have tried to access a key
	private Set accessedKeys   = CollectionFactory.createHashSet();

	// store in this Integer map, how many sessions have tried to access a key
	private Map sessionsOfKeys = CollectionFactory.createHashMap(); // String -> Integer
	private int numberOfSessions = 1;

	private final static String DELIMPROPERTY = "&&";
	private final static String DELIMINTEGER  = "||";

	private final static String SESSIONSOFKEYS   = "AgingProperties.sessionsofkeys";
	private final static String NUMBEROFSESSIONS = "AgingProperties.numberofsessions";

	/** Creates new OrderedOutputProperties */
    public AgingProperties() {
    }
	
	public AgingProperties(Properties defaults) {
		super(defaults);
	}
	
	public synchronized Object setProperty(String key, String value) {
		// notify access of property 
		addKey(key);
		return super.setProperty(key,value);
	}
	
	public String getProperty(String key) {
		// notify access of property
		addKey(key);
		return super.getProperty(key);
	}

	private void addKey(String key) {
		if(log.isDebugEnabled()) {
			if(!accessedKeys.contains(key)) {
				log.debug("Adding key "+key);
			}
		}
		accessedKeys.add(key);

	}
    public String getProperty(String key, String defaultValue) {
		// notify reading of property
		if(!key.startsWith("AgingProperties.")) {
			accessedKeys.add(key);
		}
		return super.getProperty(key,defaultValue);
	}
	
	
    public synchronized void load(InputStream inStream) throws IOException {
		super.load(inStream);
		loadSessionsOfKeys();
		checkLoadedProperties();
	}
	
    public synchronized void store(OutputStream out, String header) throws IOException {
		storeSessionsOfKeys();
		super.store(out,header);
	}
	
	// public void synchronized save() ; -> calls store
	
	private void loadSessionsOfKeys() {
		sessionsOfKeys.clear();
		for(StringTokenizer s = new StringTokenizer(getProperty(SESSIONSOFKEYS,""),DELIMPROPERTY); 
			s.hasMoreTokens(); ) {
			String property = s.nextToken();
			StringTokenizer s2 = new StringTokenizer(property,DELIMINTEGER);
			String  key = s2.nextToken();
			Integer val = new Integer(s2.nextToken());
			sessionsOfKeys.put(key,val);
		}
		//
		numberOfSessions = Integer.parseInt(getProperty(NUMBEROFSESSIONS,"0"));
	}
	
	private void storeSessionsOfKeys() {
		accessedKeys.remove(SESSIONSOFKEYS);
		accessedKeys.remove(NUMBEROFSESSIONS);

		String prop = null;
		for(Iterator iter = sessionsOfKeys.keySet().iterator(); iter.hasNext();) {
			String  key = (String) iter.next();
			Integer val = (Integer) sessionsOfKeys.get(key);
			if(accessedKeys.contains(key)) {
				val = new Integer(val.intValue()+1);
				accessedKeys.remove(key);
			}
			if(prop == null) {
				prop  = "";
			} else {
				prop += DELIMPROPERTY;
			}
			prop += key + DELIMINTEGER + val.toString();
		}
		for(Iterator iter = accessedKeys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			if(prop == null) {
				prop = "";
			} else {
				prop += DELIMPROPERTY;
			}
			prop += key + DELIMINTEGER + "1";
		}
		// now store evaluated keys
		setProperty(SESSIONSOFKEYS  , prop);
		setProperty(NUMBEROFSESSIONS, Integer.toString(numberOfSessions+1));
	}

	// helperfunction: if NUMBEROFSESSIONS > 50, 
	// we throw out all entries that are not used or only used less than 10 percent.
	private void checkLoadedProperties() {
		if(log.isDebugEnabled()) {
			// if(numberOfSessions % 50 != 0) {
			//	return;
			//}
			log.debug("AgingProperties: checkLoadedProperties started with "+numberOfSessions+" sessions");
			for(Enumeration enum = propertyNames(); enum.hasMoreElements(); ) {
				String name   = (String) enum.nextElement();
				int    amount = 0;
				if(sessionsOfKeys.get(name) != null) {
					amount = ((Integer) sessionsOfKeys.get(name)).intValue();
				}
				if(amount == 0) {
					log.debug("AgingProperties: Property \""+name+"\" not used at all!");
				} else if(amount*100/numberOfSessions < 10) {
					log.debug("AgingProperties: Property \""+name+"+\" only used "+amount*100/numberOfSessions+" percent.");
				}
			}
		}
	}
}
