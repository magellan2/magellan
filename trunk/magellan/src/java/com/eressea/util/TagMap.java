// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagMap implements Map {

	protected class Tag {
		public String key;
		public String value;
		public Tag() {}
		public Tag(String k,String v) {
			key=k;
			value=v;
		}
	}

	protected Tag tags[]=null;

	public void clear() {
		tags=null;
	}

	public boolean containsKey(Object obj) {
		if (!(obj instanceof String) || tags==null)
			return false;
		String key=(String)obj;
		for(int i=0;i<tags.length;i++)
			if (tags[i].key.equals(key))
				return true;
		return false;
	}

	public boolean containsValue(Object obj) {
		if (!( obj instanceof String) || tags==null)
			return false;
		String value=(String)obj;
		for(int i=0;i<tags.length;i++)
			if (tags[i].value.equals(value))
				return true;
		return false;
	}

	public Set entrySet() {
		return null;
	}

	public boolean equals(Object obj) {
		return false;
	}

	public Object get(Object obj) {
		if (obj==null || !( obj instanceof String) || tags==null)
			return null;
		String key=(String)obj;
		for(int i=0;i<tags.length;i++)
			if (tags[i].key.equals(key))
				return tags[i].value;
		return null;
	}

	public int hashCode() {
		if (tags==null)
			return super.hashCode();
		int j=0;
		for(int i=0;i<tags.length;i++)
			j+=tags[i].key.hashCode();
		return j;
	}

	public boolean isEmpty() {
		return tags==null;
	}

	public Set keySet() {
		Set s = CollectionFactory.createHashSet();
		if (tags!=null)
			for(int i=0;i<tags.length;i++)
				s.add(tags[i].key);
		return s;
	}

	public Object put(Object key,Object value) {
		if (key==null || !((key instanceof String) && (value instanceof String))) {
			return null;
		}
		if (containsKey(key)) {
			for(int i=0;i<tags.length;i++)
				if (tags[i].key.equals(key)) {
					Object old=tags[i].value;
					tags[i].value=(String)value;
					return old;
				}
		} else {
			int curSize=0;
			if (tags!=null) {
				curSize=tags.length;
			}
			Tag temp[]=new Tag[curSize+1];
			for(int i=0;i<curSize;i++) {
				temp[i+1]=tags[i];
			}
			temp[0]=new Tag((String)key,(String)value);
			tags=temp;
		}
		return null;
	}

	public void putAll(Map map) {
		if (map.size()>0) {
			Set s= map.keySet();
			Iterator it=s.iterator();
			while(it.hasNext()) {
				Object key=it.next();
				put(key,map.get(key));
			}
		}
	}

	public Object remove(Object obj) {
		if (obj==null || !containsKey(obj))
			return null;
		if (tags.length==1) {
			Object old=tags[0].value;
			tags=null;
			return old;
		}
		Tag temp[]=new Tag[tags.length-1];
		int j=0;
		Object old=null;
		for(int i=0;i<tags.length;i++)
			if (!tags[i].key.equals(obj)) {
				temp[j]=tags[i];
				j++;
			}
			else
				old=tags[i].value;
		tags=temp;
		return old;
	}

	public int size() {
		if (tags==null)
			return 0;
		return tags.length;
	}

	public Collection values() {
		int s=0;
		if (tags!=null)
			s=tags.length;
		List l = CollectionFactory.createArrayList(s);
		if (tags!=null) {
			for(int i=0;i<tags.length;i++) {
				l.add(tags[i].value);
			}
		}
		return l;
	}

	public String getTag(String tag) {
		return (String)get(tag);
	}

	public String putTag(String tag,String value) {
		return (String)put(tag,value);
	}

}
