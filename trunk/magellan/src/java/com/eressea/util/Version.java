// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;


import java.util.StringTokenizer;

public class Version implements Comparable {
	private int major = 0;
	private int minor = 0;
	private int build = 0;
	
	public Version(String str, String delim) throws NumberFormatException {
		StringTokenizer st = new StringTokenizer(str, delim);
		if (st.countTokens() == 3) {
			major = Integer.parseInt(st.nextToken());
			minor = Integer.parseInt(st.nextToken());
			build = Integer.parseInt(st.nextToken());
		} else {
			throw new NumberFormatException("Unable to parse the specified version string \"" + str + "\" with the delimiter \"" + delim + "\"");
		}
	}
	
	public int getMajor() {
		return major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public int getBuild() {
		return build;
	}
	
	public String toString() {
		return toString(".");
	}
	
	public String toString(String delim) {
		return major + delim + minor + delim + build;
	}
	
	/**
	 * Compares this object with the specified object for order. 
	 * Returns a negative integer, zero, or a positive integer as this
	 * object is less than, equal to, or greater than the specified
	 * object.
	 */
	public int compareTo(Object o) {
		Version v = (Version)o;
		
		if (this.getMajor() == v.getMajor()) {
			if (this.getMinor() == v.getMinor()) {
				return this.getBuild() - v.getBuild();
			} else {
				return this.getMinor() - v.getMinor();
			}
		} else {
			return this.getMajor() - v.getMajor();
		}
	}
}