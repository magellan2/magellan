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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TagMap implements Map {
	protected class Tag {
		/** TODO: DOCUMENT ME! */
		public String key;

		/** TODO: DOCUMENT ME! */
		public String value;

		/**
		 * Creates a new Tag object.
		 */
		public Tag() {
		}

		/**
		 * Creates a new Tag object.
		 *
		 * @param k TODO: DOCUMENT ME!
		 * @param v TODO: DOCUMENT ME!
		 */
		public Tag(String k, String v) {
			key   = k;
			value = v;
		}
	}

	protected Tag tags[] = null;

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void clear() {
		tags = null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean containsKey(Object obj) {
		if(!(obj instanceof String) || (tags == null)) {
			return false;
		}

		String key = (String) obj;

		for(int i = 0; i < tags.length; i++) {
			if(tags[i].key.equals(key)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean containsValue(Object obj) {
		if(!(obj instanceof String) || (tags == null)) {
			return false;
		}

		String value = (String) obj;

		for(int i = 0; i < tags.length; i++) {
			if(tags[i].value.equals(value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Set entrySet() {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object obj) {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object get(Object obj) {
		if((obj == null) || !(obj instanceof String) || (tags == null)) {
			return null;
		}

		String key = (String) obj;

		for(int i = 0; i < tags.length; i++) {
			if(tags[i].key.equals(key)) {
				return tags[i].value;
			}
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int hashCode() {
		if(tags == null) {
			return super.hashCode();
		}

		int j = 0;

		for(int i = 0; i < tags.length; i++) {
			j += tags[i].key.hashCode();
		}

		return j;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isEmpty() {
		return tags == null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Set keySet() {
		Set s = CollectionFactory.createHashSet();

		if(tags != null) {
			for(int i = 0; i < tags.length; i++) {
				s.add(tags[i].key);
			}
		}

		return s;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param key TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object put(Object key, Object value) {
		if((key == null) ||
			   !((key instanceof String) && (value instanceof String))) {
			return null;
		}

		if(containsKey(key)) {
			for(int i = 0; i < tags.length; i++) {
				if(tags[i].key.equals(key)) {
					Object old = tags[i].value;
					tags[i].value = (String) value;

					return old;
				}
			}
		} else {
			int curSize = 0;

			if(tags != null) {
				curSize = tags.length;
			}

			Tag temp[] = new Tag[curSize + 1];

			for(int i = 0; i < curSize; i++) {
				temp[i + 1] = tags[i];
			}

			temp[0] = new Tag((String) key, (String) value);
			tags    = temp;
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param map TODO: DOCUMENT ME!
	 */
	public void putAll(Map map) {
		if(map.size() > 0) {
			Set		 s  = map.keySet();
			Iterator it = s.iterator();

			while(it.hasNext()) {
				Object key = it.next();
				put(key, map.get(key));
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object remove(Object obj) {
		if((obj == null) || !containsKey(obj)) {
			return null;
		}

		if(tags.length == 1) {
			Object old = tags[0].value;
			tags = null;

			return old;
		}

		Tag    temp[] = new Tag[tags.length - 1];
		int    j   = 0;
		Object old = null;

		for(int i = 0; i < tags.length; i++) {
			if(!tags[i].key.equals(obj)) {
				temp[j] = tags[i];
				j++;
			} else {
				old = tags[i].value;
			}
		}

		tags = temp;

		return old;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int size() {
		if(tags == null) {
			return 0;
		}

		return tags.length;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection values() {
		int s = 0;

		if(tags != null) {
			s = tags.length;
		}

		List l = CollectionFactory.createArrayList(s);

		if(tags != null) {
			for(int i = 0; i < tags.length; i++) {
				l.add(tags[i].value);
			}
		}

		return l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTag(String tag) {
		return (String) get(tag);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param tag TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String putTag(String tag, String value) {
		return (String) put(tag, value);
	}
}
