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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author Andreas Gampe
 * @version
 */
public class OrderedOutputProperties extends Properties {
	/**
	 * Creates new OrderedOutputProperties
	 */
	public OrderedOutputProperties() {
	}

	/**
	 * Creates a new OrderedOutputProperties object.
	 *
	 * @param def TODO: DOCUMENT ME!
	 */
	public OrderedOutputProperties(Properties def) {
		super(def);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Enumeration keys() {
		List l = CollectionFactory.createLinkedList();
		l.addAll(keySet());
		Collections.sort(l);

		return new IteratorEnumeration(l.iterator());
	}

	private class IteratorEnumeration implements Enumeration {
		protected Iterator iterator;

		/**
		 * Creates a new IteratorEnumeration object.
		 *
		 * @param it TODO: DOCUMENT ME!
		 */
		public IteratorEnumeration(Iterator it) {
			iterator = it;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean hasMoreElements() {
			return iterator.hasNext();
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public Object nextElement() {
			return iterator.next();
		}
	}
}
