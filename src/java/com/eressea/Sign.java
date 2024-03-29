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

package com.eressea;


/**
 * class for a region sign based on our own representation in the CR.
 * @author Fiete
 *
 * @see com.eressea.Region#getSigns()
 */
public class Sign  {

	/** The type of this border. */
	private String text = null;

	/**
	 * Create a new <tt>Sign</tt> object
	 *
	 * @param id the id of the border
	 */
	public Sign() {
		
	}

	/**
	 * Create a new <tt>Sign</tt> object initialized to the specified values.
	 *
	 * @param text the text of the sign object
	 * 
	 */
	public Sign(String text) {
		this.text = text;
	}

	/**
	 * Return a string representation of this <tt>Sign</tt> object.
	 *
	 * @return Sign object as string.
	 */
	public String toString() {
		if (this.text!=null){
			return this.text;
		}
		return "undef";
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
}
