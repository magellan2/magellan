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

import com.eressea.io.cr.CRGameNameIO;
import com.eressea.io.file.FileType;
import com.eressea.io.xml.XMLGameNameIO;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GameNameReader {
	/**
	 * Creates a new GameNameReader object.
	 */
	public GameNameReader() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param filetype TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getGameName(FileType filetype) {
		try {
			String gameName = CRGameNameIO.singleton().getGameName(filetype);

			return (gameName != null) ? gameName : XMLGameNameIO.singleton().getGameName(filetype);
		} catch(IOException e) {
			return null;
		}
	}
}
