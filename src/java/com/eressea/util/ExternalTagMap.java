/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
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

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

import com.eressea.ID;

import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ExternalTagMap extends HashMap {
	private static final Logger log = Logger.getInstance(ExternalTagMap.class);
	private static final String METHOD_NAME = "getID";

	protected ID getID(Object o) {
		if(o instanceof ID) {
			return (ID) o;
		}

		Class c = o.getClass();

		try {
			Method m = c.getMethod(METHOD_NAME, null);

			if(m != null) {
				try {
					Object o2 = m.invoke(o, null);

					if(o2 instanceof ID) {
						return (ID) o2;
					}
				} catch(Exception inner) {
				}
			}
		} catch(NoSuchMethodException nsme) {
			log.error("Error trying to get ID: " + o);
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 * @param tag TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String putTag(Object o, String tag, String value) {
		ID id = getID(o);

		if(id == null) {
			return null;
		}

		if(!containsKey(id)) {
			put(id, new TagMap());
		}

		Map m = (Map) get(id);

		return (String) m.put(tag, value);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTag(Object o, String tag) {
		ID id = getID(o);

		if(id == null) {
			return null;
		}

		Map m = (Map) get(id);

		if(m == null) {
			return null;
		}

		return (String) m.get(tag);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean containsTag(Object o, String tag) {
		ID id = getID(o);

		if(id == null) {
			return false;
		}

		Map m = (Map) get(id);

		if(m == null) {
			return false;
		}

		return m.containsKey(tag);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String removeTag(Object o, String tag) {
		ID id = getID(o);

		if(id == null) {
			return null;
		}

		Map m = (Map) get(id);

		if(m == null) {
			return null;
		}

		return (String) m.remove(tag);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 * @param create TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getTagMap(Object o, boolean create) {
		ID id = getID(o);

		if(id == null) {
			return null;
		}

		Map m = (Map) get(id);

		if((m == null) && create) {
			m = new TagMap();
			put(id, m);
		}

		return m;
	}
}
