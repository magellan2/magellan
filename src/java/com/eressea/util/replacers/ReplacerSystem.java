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

package com.eressea.util.replacers;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class ReplacerSystem {
	protected ReplacerEnvironment environment;
	protected Replacer			  base;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ReplacerEnvironment getEnvironment() {
		if(environment == null) {
			environment = new ReplacerEnvironment();
		}

		return environment;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Replacer getBase() {
		return base;
	}

	protected void setBase(Replacer replacer) {
		base = replacer;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public synchronized Object getReplacement(Object obj) {
		if(environment != null) {
			environment.reset();
		}

		Object ret = null;

		try {
			ret = base.getReplacement(obj);
		} catch(Exception exc) {
		}

		if(environment != null) {
			environment.reset();
		}

		return ret;
	}
}
