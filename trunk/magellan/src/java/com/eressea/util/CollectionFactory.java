package com.eressea.util;

import java.util.*;

public class CollectionFactory {

	public static List createArrayList() {
		return new ArrayList();
	}

	public static List createArrayList(int aSize) {
		return new ArrayList(aSize);
	}

	public static List createArrayList(Collection c) {
		return new ArrayList(c);
	}
	
	public static List createLinkedList() {
		return new LinkedList();
	}

	public static List createLinkedList(int size) {
		return createLinkedList();
	}

	public static List createLinkedList(Collection c) {
		return new LinkedList(c);
	}

	public static Map createHashtable() {
		return new Hashtable();
	}

	public static Map createHashtable(Map aMap) {
		return new Hashtable(aMap);
	}

	public static Map createOrderedHashtable() {
		return new OrderedHashtable();
	}

	public static Map createOrderedHashtable(Map aMap) {
		return new OrderedHashtable(aMap);
	}

	public static Map createHashMap() {
		return new HashMap();
	}

	public static Map createTreeMap() {
		return new TreeMap();
	}

	public static Set createHashSet() {
		return new HashSet();
	}

	public static Set createHashSet(Collection c) {
		return new HashSet(c);
	}

	public static Set singleton(Object o) {
		return Collections.singleton(o);
	}

	public static List singletonList(Object o) {
		return Collections.singletonList(o);
	}

    public static Map singletonMap(Object key, Object value) {
		return Collections.singletonMap(key, value);
	}

}
