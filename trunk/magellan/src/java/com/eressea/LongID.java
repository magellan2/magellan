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
 * $Id$
 */

package com.eressea;

import java.util.Map;

import com.eressea.util.CollectionFactory;

/**
 * A class establishing the uniqueness property through a long. This class
 * assumes the representation of integers to be decimal in all cases.
 */
public class LongID implements ID {
	/** The Long object this id is based on. */

	// pavkovic 2003.09.18: changed to primitive type to avoid memory overhead
	protected final long id;

	/**
	 * Constructs a new LongID object from the specified integer.
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	protected LongID(Long l) {
		this(l.longValue());
	}

	/**
	 * Constructs a new LongID object based on an Long object created from the
	 * specified long.
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	protected LongID(long l) {
		this.id = l;
	}

	/**
	 * Creates a new LongID object by parsing the specified string for a
	 * decimal integer.
	 *
	 * @param s TODO: DOCUMENT ME!
	 */
	protected LongID(String s) {
		this(Long.valueOf(s));
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
	public static LongID create(Long o) {
		if(o == null) {
			throw new NullPointerException();
		}

		LongID id = (LongID) idMap.get(o);

		if(id == null) {
			id = new LongID(o);
			idMap.put(o, id);
		}

		return id;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param s TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static LongID create(String s) {
		return create(Long.valueOf(s));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static LongID create(int i) {
		return create(new Long(i));
	}

	/**
	 * Returns a string representation of the underlying integer.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return Long.toString(id);
	}

	/**
	 * Returns a string representation of the underlying integer.
	 *
	 * @param delim TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString(String delim) {
		return toString();
	}

	/**
	 * Returns the value of this LongID as an int.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public long longValue() {
		return longValue();
	}

	/**
	 * Indicates whether this LongID object is equal to some other object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return true, if o is an instance of class LongID and the numerical
	 * 		   values of this and the specified object are equal.
	 */
	public boolean equals(Object o) {
		return (this == o) || (o instanceof LongID && (id == ((LongID) o).id));
	}

	/**
	 * Imposes a natural ordering on LongID objects which is based on the
	 * natural ordering of the underlying integers.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		long anotherId = ((LongID) o).id;

		return (id < anotherId) ? (-1) : ((id == anotherId) ? 0 : 1);
	}

	/**
	 * Returns a hash code for this object.
	 *
	 * @return a hash code value based on the hash code returned by the
	 * 		   underlying Long object.
	 */
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

	/**
	 * Returns a copy of this LongID object.
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws CloneNotSupportedException TODO: DOCUMENT ME!
	 */
	public Object clone() throws CloneNotSupportedException {
		// pavkovic 2003.07.08: we dont really clone this object as LongID is unchangeable after creation
		return this;
	}
}
