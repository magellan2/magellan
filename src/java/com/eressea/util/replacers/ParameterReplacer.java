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

/*
 * ParameterReplacer.java
 *
 * Created on 20. Mai 2002, 14:11
 */
package com.eressea.util.replacers;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public interface ParameterReplacer extends Replacer {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getParameterCount();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 * @param obj TODO: DOCUMENT ME!
	 */
	public void setParameter(int index, Object obj);
}
