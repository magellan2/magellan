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

package com.eressea;

import java.util.Map;

import com.eressea.util.CollectionFactory;
import com.eressea.util.StringFactory;
import com.eressea.util.Umlaut;

/**
 * An implementation of the ID interface providing uniqueness and identifiability through strings.
 * The strings used to establish the uniqueness of this id may differ from the strings specified
 * with the constructor of a StringID object. I.e. although with String s1 and s2 s1.equals(s2) is
 * false (new StringID(s1)).equals(new StringID(s2)) may be true. Two StringID objects are
 * regarded as equal when s1 differs from s2 only in case or the umlaut expansion of s1 equals s2
 * (or vice versa).
 */
public class StringID implements ID {
	/** The string used to establish the uniqueness of this ID. */
	protected final String id;

	/** The string specified at construction time of this object. */
	protected final String originalString;

	/**
	 * Creates a new StringID object. See the class description on how the specified string is used
	 * to establish the uniqueness of this id.
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	protected StringID(String i) {
		if((i == null) || i.equals("")) {
			throw new IllegalArgumentException("StringID: empty String specified as an ID");
		}

		this.originalString = StringFactory.getFactory().intern(i);
		this.id = StringFactory.getFactory().intern(Umlaut.normalize(i));
	}

	/** a static cache to use this class as flyweight factory */
	private static Map idMap = CollectionFactory.createHashMap();

	/**
	 * Returns a (possibly) new StringID object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws NullPointerException TODO: DOCUMENT ME!
	 */
	public static StringID create(String o) {
		if(o == null) {
			throw new NullPointerException();
		}

		StringID id = (StringID) idMap.get(o);

		if(id == null) {
			id = new StringID(o);
			idMap.put(o, id);
		}

		return id;
	}

	/**
	 * Returns the string used to construct this StringID object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return originalString;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param delim TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString(String delim) {
		return toString();
	}

	/**
	 * Indicates whether this id is "equal to" some other object. For equality rules see the class
	 * description, of course o must be an instance of StringID.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (this == o) || (o instanceof StringID && id.equals(((StringID) o).id));
	}

	/**
	 * Returns a hash code based on the identifying string.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * Performs a lexicographical comparision between this object and another instance of class
	 * StringID.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return id.compareTo(((StringID) o).id);
	}

	/**
	 * Returns a copy of this string id object.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws CloneNotSupportedException TODO: DOCUMENT ME!
	 */
	public Object clone() throws CloneNotSupportedException {
		// pavkovic 2003.07.08: we dont really clone this object as StringID are immutable after creation
		return this;
	}
}
