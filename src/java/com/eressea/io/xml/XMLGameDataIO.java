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
import java.io.Reader;

import com.eressea.GameData;
import com.eressea.io.GameDataIO;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class XMLGameDataIO implements GameDataIO {
	/** TODO: DOCUMENT ME! */
	public static final Logger log = Logger.getInstance(XMLGameDataIO.class);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param in TODO: DOCUMENT ME!
	 * @param world TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public GameData read(Reader in, GameData world) throws IOException {
		throw new IOException("Implementation incomplete");

		// c) use corresponding gamebinding object (or eressea gamebinding object if 
		//    no special implementation found) to read the cr/xml
		// 
		//return new XMLGameDataReader(file).readGameData();
	}
}
