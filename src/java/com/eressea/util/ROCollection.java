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
import java.util.Map;

public class ROCollection implements Collection {
	protected Collection c = null;
	
	public final static ROCollection EMPTY_COLLECTION = new ROCollection();
	
	public ROCollection() {
		this.c = null;
	}
	
	public ROCollection(Collection c) {
		this.c = c;
	}
	
	public ROCollection(Map m) {
		if (m != null) {
			this.c = m.values();
		} else {
			this.c = null;
		}
	}
	
	public boolean add(Object o) {
		throw new UnsupportedOperationException("ROCollection.add(): this is a read only collection not supporting this operation");
	}

	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException("ROCollection.addAll(): this is a read only collection not supporting this operation");
	}
	
	public void clear() {
		throw new UnsupportedOperationException("ROCollection.clear(): this is a read only collection not supporting this operation");
	}
	
	public boolean contains(Object o) {
		if (c != null) {
			return c.contains(o);
		} else {
			return false;
		}
	}
	
	public boolean containsAll(Collection c) {
		if (this.c != null) {
			return this.c.containsAll(c);
		} else {
			return false;
		}
	}
	
	public boolean isEmpty() {
		if (this.c != null) {
			return this.c.isEmpty();
		} else {
			return true;
		}
	}
	
	public Iterator iterator() {
		if (this.c != null) {
			return new ROIterator(c.iterator());
		} else {
			return ROIterator.EMPTY_ITERATOR;
		}
	}
	
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("ROCollection.remove(): this is a read only collection not supporting this operation");
	}

	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException("ROCollection.removeAll(): this is a read only collection not supporting this operation");
	}

	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException("ROCollection.retainAll(): this is a read only collection not supporting this operation");
	}
	
	public int size() {
		if (this.c != null) {
			return this.c.size();
		} else {
			return 0;
		}
	}
	
	public Object[] toArray() {
		if (this.c != null) {
			return this.c.toArray();
		} else {
			return new Object[0];
		}
	}
	
	public Object[] toArray(Object[] a) {
		if (this.c != null) {
			return this.c.toArray(a);
		} else {
			return new Object[0];
		}
	}

	public String toString() {
		return c.toString();
	}
}