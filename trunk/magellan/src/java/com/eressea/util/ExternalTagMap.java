// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.eressea.ID;
import com.eressea.util.logging.Logger;

public class ExternalTagMap extends HashMap {
	private final static Logger log = Logger.getInstance(ExternalTagMap.class);

	private final static String METHOD_NAME="getID";

	protected ID getID(Object o) {
		if (o instanceof ID)
			return (ID)o;
		Class c=o.getClass();
		try{
			Method m=c.getMethod(METHOD_NAME,null);
			if (m!=null) try {
				Object o2=m.invoke(o,null);
				if (o2 instanceof ID)
					return (ID)o2;
			}catch(Exception inner) {}
		}
		catch(NoSuchMethodException nsme) {
			log.error("Error trying to get ID: "+o);
		}
		return null;
	}

	public String putTag(Object o,String tag,String value) {
		ID id=getID(o);
		if (id==null)
			return null;
		if (!containsKey(id))
			put(id,new TagMap());
		Map m=(Map)get(id);
		return (String)m.put(tag,value);
	}

	public String getTag(Object o,String tag) {
		ID id=getID(o);
		if (id==null)
			return null;
		Map m=(Map)get(id);
		if (m==null)
			return null;
		return (String)m.get(tag);
	}

	public boolean containsTag(Object o,String tag) {
		ID id=getID(o);
		if (id==null)
			return false;
		Map m=(Map)get(id);
		if (m==null)
			return false;
		return m.containsKey(tag);
	}

	public String removeTag(Object o,String tag) {
		ID id=getID(o);
		if (id==null)
			return null;
		Map m=(Map)get(id);
		if (m==null)
			return null;
		return (String)m.remove(tag);
	}

	public Map getTagMap(Object o,boolean create) {
		ID id=getID(o);
		if (id==null)
			return null;
		Map m=(Map)get(id);
		if (m==null && create) {
			m=new TagMap();
			put(id,m);
		}
		return m;
	}
}
