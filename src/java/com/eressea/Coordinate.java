// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


import java.util.StringTokenizer;

/**
 * A coordinate uniquely identifies a location in a three dimensional
 * space by x-, y- and z-axis components.
 */
public class Coordinate implements ID {
	/**
	 * The x-axis part of this coordinate. Modifying the x, y and z
	 * values changes the hash value of this Coordinate!
	 */
	public int x;
	/**
	 * The y-axis part of this coordinate. Modifying the x, y and z
	 * values changes the hash value of this Coordinate!
	 */
	public int y;
	/**
	 * The z-axis part of this coordinate. Modifying the x, y and z
	 * values changes the hash value of this Coordinate!
	 */
	public int z;

	/**
	 * Create a new Coordinate with a z-value of 0.
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}

	public Coordinate(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coordinate(Coordinate c) {
		this.x = c.x;
		this.y = c.y;
		this.z = c.z;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Coordinate)) return false;
		Coordinate c = (Coordinate)o;
		return (c == this || (x == c.x && y == c.y && z == c.z));
	}

	/**
	 * Returns a String representation of this corrdinate. The x, y
	 * and z components are seperated by semicolon with a blank and the z component is
	 * ommitted if it equals 0.
	 */
	public String toString() {
		return toString(", ", false);
	}

	/**
	 * Returns a String representation of this coordinate consisting
	 * of the x, y and, if not 0, z coordinates delimited by delim.
	 */
	public String toString(String delim) {
		return toString(delim, false);
	}
	
	/**
	 * Returns a String representation of this coordinate. The x, y
	 * and z components are seperated by the specified string and the
	 * z component is ommitted if it equals 0 and forceZ is false.
	 *
	 * @param delim the string to delimit the x, y and z components.
	 * @param forceZ if true, the z component is only included if it
	 * is not 0, else the z component is always included.
	 */
	public String toString(String delim, boolean forceZ) {
		if (!forceZ && z == 0) {
			return x + delim + y;
		} else {
			return x + delim + y + delim + z;
		}
	}

	/**
	 * Returns a hash code value for this Coordinate. The value
	 * depends on the x, y and z values, so be careful when modifying
	 * these values.
	 */
	public int hashCode() {
		return (x << 12) ^ (y << 6) ^ z;
	}

	/**
	 * Creates a new <tt>Coordinate</tt> object from a string
	 * containing the coordinates separated by delimiters.
	 */
	public static Coordinate parse(String coords, String delim) {
		Coordinate c = null;
		
		if (coords != null) {
			StringTokenizer st = new StringTokenizer(coords, delim);
			
			if (st.countTokens() == 2) {
				try {
					c = new Coordinate(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
				} catch (Exception e) {
					c = null;
				}
			} else
			if (st.countTokens() == 3) {
				try {
					c = new Coordinate(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
				} catch (Exception e) {
					c = null;
				}
			}
		}
		
		return c;
	}
	
	/**
	 * Translates this coordinate by c.x on the x-axis and c.y on the
	 * y-axis and c.z on the z-axis.
	 * Be careful when using this method on a coordinate used as a
	 * key in a hash map: modifying the x, y and z values changes the
	 * hash value.
	 *
	 * @param c the relative coordinate to translate the current one
	 * by.
	 * @return this.
	 */
	public Coordinate translate(Coordinate c) {
		x += c.x;
		y += c.y;
		z += c.z;
		return this;
	}
	
	/**
	 * Defines the natural ordering of coordinates which is:
	 * Iff the z coordinates differ their difference is returend.
	 * Iff the y coordinates differ their difference is returend.
	 * Else the difference of the x coordinates is returned.
	 */
	public int compareTo(Object o) {
		Coordinate c = (Coordinate)o;
		if (!this.equals(c)) {
			if (this.z != c.z) {
				return this.z - c.z;
			} else if (this.y != c.y) {
				return (c.y - this.y);
			} else {
				return (this.x - c.x);
			}
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns a copy of this Coordinate object.
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
