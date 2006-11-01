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

package com.eressea.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * This class logically accesses values in a given Properties object
 */
public class PropertiesHelper {
	/**
	 * Extracts properties by given prefix. If there exists a key called prefix.count this is used
	 * as order
	 *
	 * @param p TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List getList(Properties p, String prefix) {
		List ret = CollectionFactory.createLinkedList();
		String count = p.getProperty(prefix + ".count");

		if(count == null) {
            boolean hasMore = true;
            for(int i=0; hasMore; i++) {
                String prop = p.getProperty(prefix + "." + i);

                if(prop == null) {
                    prop = p.getProperty(prefix + i);
                }
                
                if(prop != null) {
                    ret.add(prop);
                } else {
                    hasMore = false;
                }
            }
		} else {
			for(int i = 0, max = new Integer(count).intValue(); i < max; i++) {
				String prop = p.getProperty(prefix + "." + i);

				if(prop != null) {
					ret.add(prop);
				}
			}
		}

		return ret;
	}

	// a) remove old properties
	// b) set prefix.count value
	// c) set prefix.0 .. prefix.n values
	public static void setList(Properties p, String prefix, Collection list) {
		// a) remove old properties
		for(Iterator iter = getPrefixedList(p, prefix).iterator(); iter.hasNext();) {
			p.remove(iter.next());
		}

		// b) set prefix.count value
		p.setProperty(prefix + ".count", Integer.toString(list.size()));

		// c) set prefix.0 .. prefix.n values
		int i = 0;

		for(Iterator iter = list.iterator(); iter.hasNext(); i++) {
			Object value = iter.next();
			p.setProperty(prefix + "." + i, value.toString());
		}
	}

	/**
	 * Delivers a list of all keys having the prefix <tt>prefix</tt>
	 *
	 * @param p TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List getPrefixedList(Properties p, String prefix) {
		List ret = CollectionFactory.createLinkedList();

		for(Enumeration e = p.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();

			if(key.startsWith(prefix)) {
				ret.add(p.getProperty(key));
			}
		}

		return ret;
	}

	/*
	public static Boolean getBoolean(Properties p, String key) {
	    String val = p.getProperty(key);
	    if(val==null|| val.equalsIgnoreCase()) {
	        return null;
	    }
	    return new Boolean()
	}
	*/
	public static boolean getboolean(Properties p, String key, boolean def) {
		String val = p.getProperty(key);

		if(val != null) {
			return Boolean.valueOf(val).booleanValue();
		}

		return def;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p TODO: DOCUMENT ME!
	 * @param key TODO: DOCUMENT ME!
	 * @param def TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static float getfloat(Properties p, String key, float def) {
		String val = p.getProperty(key);

		if(val != null) {
			try {
				return Float.valueOf(val).floatValue();
			} catch(NumberFormatException nfe) {
			}
		}

		return def;
	}
}
