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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ROIterator implements Iterator {
	protected Iterator i = null;

	/** TODO: DOCUMENT ME! */
	public static final ROIterator EMPTY_ITERATOR = new ROIterator();

	/**
	 * Creates a new ROIterator object.
	 */
	public ROIterator() {
		this.i = null;
	}

	/**
	 * Creates a new ROIterator object.
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public ROIterator(Iterator i) {
		this.i = i;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean hasNext() {
		if(i != null) {
			return i.hasNext();
		} else {
			return false;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws NoSuchElementException TODO: DOCUMENT ME!
	 */
	public Object next() {
		if(i != null) {
			return i.next();
		} else {
			throw new NoSuchElementException("ROIterator.next(): no more elements in iteration");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @throws UnsupportedOperationException TODO: DOCUMENT ME!
	 */
	public void remove() {
		throw new UnsupportedOperationException("ROIterator.remove(): this is a read only iterator not supporting the remove() operation");
	}
}
