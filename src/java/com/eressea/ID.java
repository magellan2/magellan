// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

/**
 * A general interface to ID objects conveying "uniqueness".
 */
public interface ID extends Comparable, Cloneable {

	/**
	 * Returns a String representation of the ID formatted in a 
	 * user friendly manner.
	 */
	public String toString();

	/**
	 * Returns a String representation of the ID formatted in a 
	 * user friendly manner with a given seperator. Right now only
     * Coordinate should implement this, all others should invoke
	 * toString()!
	 */
	public String toString(String delim);
	
	/**
	 * Compares this object to the specified object. The result is
	 * true if and only if the argument is not null and is an object
	 * of the same class implementing this interface and contains the
	 * same unique value.
	 */
	public boolean equals(Object o);
	
	/**
	 * Returns a hashcode for this ID.
	 */
	public int hashCode();
	
	/**
	 * Imposes a natural ordering on ID objects.
	 */
	public int compareTo(Object o);
	
	/**
	 * Returns a copy of this object.
	 */
	public Object clone() throws CloneNotSupportedException;

}
