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

package com.eressea.io.xml;

import java.io.IOException;

import com.eressea.io.GameNameIO;
import com.eressea.io.file.FileType;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class XMLGameNameIO implements GameNameIO {
	private XMLGameNameIO() {
	}

	private static XMLGameNameIO singleton = new XMLGameNameIO();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static XMLGameNameIO singleton() {
		return singleton;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param filetype TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public String getGameName(FileType filetype) throws IOException {
		return null;
	}
}
