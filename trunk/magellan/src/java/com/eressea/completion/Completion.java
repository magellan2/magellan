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
 */

package com.eressea.completion;

/**
 * A class representing a possible completion of an incomplete order.
 */
public class Completion {
	private String name		    = null;
	private String value	    = null;
	private String postfix	    = null;
	private int    priority     = 9;
	private int    cursorOffset = 0;

	/**
	 * Creates a new Completion object.
	 *
	 * @param text TODO: DOCUMENT ME!
	 */
	public Completion(String text) {
		this(text, text, "", 9, 0);
	}

	/**
	 * Creates a new Completion object.
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param prio TODO: DOCUMENT ME!
	 */
	public Completion(String text, int prio) {
		this(text, text, "", prio, 0);
	}

	/**
	 * Creates a new Completion object.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 * @param postfix TODO: DOCUMENT ME!
	 */
	public Completion(String name, String value, String postfix) {
		this(name, value, postfix, 9, 0);
	}

	/**
	 * Creates a new Completion object.
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param postfix TODO: DOCUMENT ME!
	 */
	public Completion(String text, String postfix) {
		this(text, text, postfix, 9, 0);
	}

	/**
	 * Creates a new Completion object.
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param postfix TODO: DOCUMENT ME!
	 * @param prio TODO: DOCUMENT ME!
	 */
	public Completion(String text, String postfix, int prio) {
		this(text, text, postfix, prio, 0);
	}

	/**
	 * Creates a new Completion object.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 * @param postfix TODO: DOCUMENT ME!
	 * @param prio TODO: DOCUMENT ME!
	 */
	public Completion(String name, String value, String postfix, int prio) {
		this(name, value, postfix, prio, 0);
	}

	/**
	 * Creates a new Completion object.
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param value TODO: DOCUMENT ME!
	 * @param postfix TODO: DOCUMENT ME!
	 * @param prio TODO: DOCUMENT ME!
	 * @param cursorOffset TODO: DOCUMENT ME!
	 */
	public Completion(String name, String value, String postfix, int prio,
					  int cursorOffset) {
		this.name		  = name;
		this.value		  = value;
		this.postfix	  = postfix;
		this.priority     = prio;
		this.cursorOffset = cursorOffset;
	}

	/**
	 * Creates a new Completion object.
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public Completion(Completion c) {
		this.name     = c.getName();
		this.value    = c.getValue();
		this.postfix  = c.getPostfix();
		this.priority = c.getPriority();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		return name;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getValue() {
		return value + postfix;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPriority() {
		return priority;
	}

	/*
	public void setPriority(int prio) {
	    this.priority = prio;
	}
	*/
	public String getPostfix() {
		return postfix;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param postfix TODO: DOCUMENT ME!
	 */
	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getCursorOffset() {
		return this.cursorOffset;
	}

	/*
	public void setCursorOffset(int offset) {
	    this.cursorOffset = offset;
	}

	*/
	/*
	public void set(String name, String value) {
	    this.name = name;
	    this.value = value;
	}
	*/
	public String toString() {
		return name;
	}
}
