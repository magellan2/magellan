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

/*
 * AbstractParameterReplacer.java
 *
 * Created on 29. Dezember 2001, 16:17
 */
package com.eressea.util.replacers;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public abstract class AbstractParameterReplacer implements ParameterReplacer {
	protected Object parameters[];

	protected AbstractParameterReplacer(int parameters) {
		this.parameters = new Object[parameters];
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getParameterCount() {
		return parameters.length;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param param TODO: DOCUMENT ME!
	 * @param obj TODO: DOCUMENT ME!
	 */
	public void setParameter(int param, java.lang.Object obj) {
		parameters[param] = obj;
	}

	protected Object getParameter(int index, Object o) {
		if(parameters[index] != null) {
			if(parameters[index] instanceof Replacer) {
				return ((Replacer) parameters[index]).getReplacement(o);
			}
		}

		return parameters[index];
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return null;
	}
}
