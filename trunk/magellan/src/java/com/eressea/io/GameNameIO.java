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

package com.eressea.io;

import java.io.IOException;

import com.eressea.io.file.FileType;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface GameNameIO {
	/**
	 * Returns the game data found in the given FileType
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getGameName(FileType file) throws IOException;
}
