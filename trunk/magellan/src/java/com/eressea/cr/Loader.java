// ===
// Copyright (C) 2000, 2001,2002 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.cr;

import java.io.File;
import java.io.IOException;

import com.eressea.GameData;
import com.eressea.io.GameDataReader;
import com.eressea.io.cr.CRWriter;
import com.eressea.io.file.CopyFile;
import com.eressea.io.file.FileType;
import com.eressea.io.file.FileTypeFactory;
import com.eressea.util.logging.Logger;

public class Loader {
	private final static Logger log = Logger.getInstance(Loader.class);

	/** 
	 * Creates a clone of the GameData using CRWriter/CRParser 
	 */
	public GameData cloneGameData(GameData data) throws CloneNotSupportedException {
		try {
			File tempFile = CopyFile.createCrTempFile();
			tempFile.deleteOnExit();
			FileType filetype = FileTypeFactory.singleton().createFileType(tempFile);
			
			// write cr to file
			CRWriter crw = new CRWriter(filetype);
			try {
				crw.write(data);
			} finally {
				crw.close();
			}
			

			GameData newData = new GameDataReader().readGameData(filetype);
			newData.filetype=data.filetype;
			tempFile.delete();
			
			return newData;
		} catch(IOException ioe) {
			log.error("Loader.cloneGameData failed!",ioe);
			throw new CloneNotSupportedException();
		}
	}
		
}
