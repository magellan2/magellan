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

package com.eressea.completion;

import java.util.List;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface CompleterSettingsProvider {
	/**
	 * Delivers a list of completions for self defined completions.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getSelfDefinedCompletions();

	/**
	 * Returns true iff LimitMakeCompletion is turned on
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getLimitMakeCompletion();
}
