// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;


import java.util.Iterator;
import java.util.NoSuchElementException;

public class ROIterator implements Iterator {
	protected Iterator i = null;
	
	public final static ROIterator EMPTY_ITERATOR = new ROIterator();
	
	public ROIterator() {
		this.i = null;
	}
	
	public ROIterator(Iterator i) {
		this.i = i;
	}
	
	public boolean hasNext() {
		if (i != null) {
			return i.hasNext();
		} else {
			return false;
		}
	}
	
	public Object next() {
		if (i != null) {
			return i.next();
		} else {
			throw new NoSuchElementException("ROIterator.next(): no more elements in iteration");
		}
	}
	
	public void remove() {
		throw new UnsupportedOperationException("ROIterator.remove(): this is a read only iterator not supporting the remove() operation");
	}
}