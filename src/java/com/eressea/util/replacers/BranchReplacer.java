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
 * BranchReplacer.java
 *
 * Created on 19. Mai 2002, 12:02
 */
package com.eressea.util.replacers;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public interface BranchReplacer extends Replacer {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getBranchCount();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getBranchSign(int index);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 * @param obj TODO: DOCUMENT ME!
	 */
	public void setBranch(int index, Object obj);
}
