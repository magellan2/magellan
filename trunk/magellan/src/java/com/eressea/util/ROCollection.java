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

package com.eressea.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ROCollection implements Collection {
	protected Collection c = null;

	/** TODO: DOCUMENT ME! */
	public static final ROCollection EMPTY_COLLECTION = new ROCollection();

	/**
	 * Creates a new ROCollection object.
	 */
	public ROCollection() {
		this.c = null;
	}

	/**
	 * Creates a new ROCollection object.
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public ROCollection(Collection c) {
		this.c = c;
	}

	/**
	 * Creates a new ROCollection object.
	 *
	 * @param m TODO: DOCUMENT ME!
	 */
	public ROCollection(Map m) {
		if(m != null) {
			this.c = m.values();
		} else {
			this.c = null;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws UnsupportedOperationException TODO: DOCUMENT ME!
	 */
	public boolean add(Object o) {
		throw new UnsupportedOperationException("ROCollection.add(): this is a read only collection not supporting this operation");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws UnsupportedOperationException TODO: DOCUMENT ME!
	 */
	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException("ROCollection.addAll(): this is a read only collection not supporting this operation");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @throws UnsupportedOperationException TODO: DOCUMENT ME!
	 */
	public void clear() {
		throw new UnsupportedOperationException("ROCollection.clear(): this is a read only collection not supporting this operation");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean contains(Object o) {
		if(c != null) {
			return c.contains(o);
		} else {
			return false;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean containsAll(Collection c) {
		if(this.c != null) {
			return this.c.containsAll(c);
		} else {
			return false;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isEmpty() {
		if(this.c != null) {
			return this.c.isEmpty();
		} else {
			return true;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator iterator() {
		if(this.c != null) {
			return new ROIterator(c.iterator());
		} else {
			return ROIterator.EMPTY_ITERATOR;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws UnsupportedOperationException TODO: DOCUMENT ME!
	 */
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("ROCollection.remove(): this is a read only collection not supporting this operation");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws UnsupportedOperationException TODO: DOCUMENT ME!
	 */
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException("ROCollection.removeAll(): this is a read only collection not supporting this operation");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws UnsupportedOperationException TODO: DOCUMENT ME!
	 */
	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException("ROCollection.retainAll(): this is a read only collection not supporting this operation");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int size() {
		if(this.c != null) {
			return this.c.size();
		} else {
			return 0;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object[] toArray() {
		if(this.c != null) {
			return this.c.toArray();
		} else {
			return new Object[0];
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param a TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object[] toArray(Object a[]) {
		if(this.c != null) {
			return this.c.toArray(a);
		} else {
			return new Object[0];
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return c.toString();
	}
}
