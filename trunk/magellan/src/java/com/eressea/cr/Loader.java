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

package com.eressea.cr;

import java.io.File;
import java.io.IOException;

import com.eressea.CoordinateID;
import com.eressea.GameData;
import com.eressea.io.GameDataReader;
import com.eressea.io.cr.CRWriter;
import com.eressea.io.file.CopyFile;
import com.eressea.io.file.FileType;
import com.eressea.io.file.FileTypeFactory;
import com.eressea.util.logging.Logger;

/**
 * This used to be the Loader class. Now it only supports cloning via  cr writing/reading
 *
 * @author $author$
 * @version $Revision$
 */
public class Loader {
	private static final Logger log = Logger.getInstance(Loader.class);

	/**
	 * Creates a clone of the GameData using CRWriter/CRParser
	 *
	 * @param data the given GameData
	 *
	 * @return a clone of the given GameData
	 *
	 * @throws CloneNotSupportedException if cloning failed
	 */
	public GameData cloneGameData(GameData data) throws CloneNotSupportedException {
		return cloneGameData(data, new CoordinateID(0,0));
	}
		
	/**
	 * Creates a clone of the GameData using CRWriter/CRParser
	 *
	 * @param data the given GameData
	 *
	 * @return a clone of the given GameData
	 *
	 * @throws CloneNotSupportedException if cloning failed
	 */
	public GameData cloneGameData(GameData data, CoordinateID newOrigin) throws CloneNotSupportedException {
		try {
			File tempFile = CopyFile.createCrTempFile();
			tempFile.deleteOnExit();

			FileType filetype = FileTypeFactory.singleton().createFileType(tempFile, false);
			filetype.setCreateBackup(false);
			
			// write cr to file
			CRWriter crw = new CRWriter(filetype,data.encoding);

			try {
				crw.write(data);
			} finally {
				crw.close();
			}
			
			GameData newData = new GameDataReader().readGameData(filetype, newOrigin);
			newData.filetype = data.filetype;
			tempFile.delete();
			
			return newData;
		} catch(IOException ioe) {
			log.error("Loader.cloneGameData failed!", ioe);
			throw new CloneNotSupportedException();
		}
	}
}
