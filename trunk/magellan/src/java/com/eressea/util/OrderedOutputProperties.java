// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

// Simply returns ordered enumerations of keys.
// This means that output to streams is also ordered.

package com.eressea.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author  Andreas Gampe
 * @version 
 */
public class OrderedOutputProperties extends Properties {

	/** Creates new OrderedOutputProperties */
    public OrderedOutputProperties() {
    }
	
	public OrderedOutputProperties(Properties def) {
		super(def);
	}
	
	public Enumeration keys() {
		List l=CollectionFactory.createLinkedList();
		l.addAll(keySet());
		Collections.sort(l);
		return new IteratorEnumeration(l.iterator());
	}
	
	private class IteratorEnumeration implements Enumeration {
		protected Iterator iterator;
		public IteratorEnumeration(Iterator it) {
			iterator=it;
		}
		public boolean hasMoreElements() {
			return iterator.hasNext();
		}
		public Object nextElement() {
			return iterator.next();
		}
	}
}
