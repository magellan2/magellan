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

package com.eressea.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class CollectionFactory {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List createArrayList() {
		return new ArrayList();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aSize TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List createArrayList(int aSize) {
		return new ArrayList(aSize);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List createArrayList(Collection c) {
		return new ArrayList(c);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List createLinkedList() {
		return new LinkedList();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param size TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List createLinkedList(int size) {
		return createLinkedList();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List createLinkedList(Collection c) {
		return new LinkedList(c);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map createHashtable() {
		return new Hashtable();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aMap TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map createHashtable(Map aMap) {
		return new Hashtable(aMap);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map createOrderedHashtable() {
		return new OrderedHashtable();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aMap TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map createOrderedHashtable(Map aMap) {
		return new OrderedHashtable(aMap);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map createHashMap() {
		return new HashMap();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map createTreeMap() {
		return new TreeMap();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Set createHashSet() {
		return new HashSet();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Set createHashSet(Collection c) {
		return new HashSet(c);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Set singleton(Object o) {
		return Collections.singleton(o);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List singletonList(Object o) {
		return Collections.singletonList(o);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param key TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map singletonMap(Object key, Object value) {
		return Collections.singletonMap(key, value);
	}

	/** TODO: DOCUMENT ME! */
	public static final Iterator EMPTY_ITERATOR = Collections.EMPTY_SET.iterator();

	/** TODO: DOCUMENT ME! */
	public static final Collection EMPTY_COLLECTION = Collections.EMPTY_SET;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param m TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Iterator unmodifiableIterator(Map m) {
		return CollectionFactory.unmodifiableIterator((m == null) ? null : m.values());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Iterator unmodifiableIterator(Collection c) {
		return (c != null) ? Collections.unmodifiableCollection(c).iterator() : EMPTY_ITERATOR;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param m TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Collection unmodifiableCollection(Map m) {
		return (m != null) ? CollectionFactory.unmodifiableCollection(m.values()) : EMPTY_COLLECTION;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Collection unmodifiableCollection(Collection c) {
		return (c != null) ? Collections.unmodifiableCollection(c) : EMPTY_COLLECTION;
	}
}
