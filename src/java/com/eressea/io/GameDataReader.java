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

package com.eressea.io;

import java.io.IOException;
import java.io.Reader;

import com.eressea.GameData;
import com.eressea.Rules;
import com.eressea.io.cr.CRParser;
import com.eressea.io.file.FileType;

/**
 * The <code>GameDataReader</code> reads a <code>GameData</code> from a given <code>FileType</code>
 *
 * @author $author$
 * @version $Revision$
 */
public class GameDataReader {
	/**
	 * Read a gamedata from a given File. At the beginning the game name is read by a
	 * <code>GameNameReader</code>. With this  name the corresponding rules and game
	 *
	 * @param aFileType the filetype representing a cr or xml file.
	 *
	 * @return a GameData object read from the cr or xml file.
	 *
	 * @throws IOException iff something went wrong while reading the file.
	 */
	public GameData readGameData(FileType aFileType) throws IOException {
		// a) read game name
		String gameName = new GameNameReader().getGameName(aFileType);

		if(gameName == null) {
			throw new IOException("Unable to determine game name of file " + aFileType);
		}

		if(isXMLFile(aFileType)) {
			GameData data = readGameDataXML(aFileType, gameName);

			if(data != null) {
				data.postProcess();
			}

			return data;
		}

		if(isCRFile(aFileType)) {
			GameData data = readGameDataCR(aFileType, gameName);

			if(data != null) {
				data.postProcess();
			}

			return data;
		}

		throw new IOException("Don't know how to read unknown file format in " + aFileType);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aFileType TODO: DOCUMENT ME!
	 * @param aGameName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public GameData readGameDataXML(FileType aFileType, String aGameName) throws IOException {
		throw new IOException("Reading of xml files unfinished");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param aFileType TODO: DOCUMENT ME!
	 * @param aGameName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @throws IOException TODO: DOCUMENT ME!
	 */
	public GameData readGameDataCR(FileType aFileType, String aGameName) throws IOException {
		GameData newData = createGameData(aGameName);
		newData.filetype = aFileType;

		Reader reader = aFileType.createReader();

		try {
			new CRParser().read(reader, newData);
		} finally {
			try {
				reader.close();
			} catch(IOException e) {
			}
		}

		// after reading the filetype may be written
		aFileType.setReadonly(false);

		return newData;
	}

	public GameData createGameData(String aGameName) throws IOException {
		Rules rules = new RulesReader().readRules(aGameName);

		if(rules == null) {
			// This should never happen but who knows
			throw new IOException("No Rules for game '" + aGameName + "' readable!");
		}

		return rules.getGameSpecificStuff().createGameData(rules, aGameName);
	}

	private boolean isXMLFile(FileType aFileType) throws IOException {
		return false;
	}

	private boolean isCRFile(FileType aFileType) throws IOException {
		return true;
	}
}
