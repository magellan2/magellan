// ===
// Copyright (C) 2000, 2001,2002 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.cr;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

import com.eressea.CompleteData;
import com.eressea.GameData;
import com.eressea.Rules;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.io.xml.*;
import com.eressea.rules.GenericRules;
import com.eressea.util.CollectionFactory;
import com.eressea.util.file.CopyFile;
import com.eressea.util.file.FileType;
import com.eressea.util.logging.Logger;


public class Loader {
	private final static Logger log = Logger.getInstance(Loader.class);

	public static class MissingCRException extends IOException {
		public MissingCRException() {
		}
	}

	/** @author Rainer Klaffehn
	 *  @author Ilja Pavkovic
	 *  Moved method from com.eressea.demo.Client (including submethods)
	 * Rewrite of the original method. This version is cleaned up a bit, and
	 * attaches a ruleset based on the game type in the CR.
	 * @param fileName The file name to be loaded.
	 * @return a new <tt>GameData</tt> object filled with the data from the CR.
	 * @deprecated use new Loader().doLoadCR(fileName) instead
	 */
	public static GameData loadCR(String fileName) throws IOException {
		return new Loader().doLoadCR(fileName);
	}
	
	
	/** @author Rainer Klaffehn
	 *  @author Ilja Pavkovic
	 *  Moved method from com.eressea.demo.Client (including submethods)
	 * Rewrite of the original method. This version is cleaned up a bit, and
	 * attaches a ruleset based on the game type in the CR.
	 * @param fileName The file name to be loaded.
	 * @return a new <tt>GameData</tt> object filled with the data from the CR.
	 */
	public GameData doLoadCR(String fileName) throws IOException {
		FileType fileType = FileType.createFileType(fileName);
		if(log.isDebugEnabled()) {
			fileType.createReader().close();
			log.debug("Loader.loadCR("+fileName+"): "+fileType);
		}
		Reader file = new BufferedReader(fileType.createReader());
		Reader head = new BufferedReader(fileType.createReader());

		// find out the name of the game of this report
		String game = getGameName(head);
		head.close();

		/* Now that we know the game, we can create and read the data object. */
		GameData newData = new CompleteData(loadRules(game), game);
		newData.filetype = fileType;
		(new CRParser()).read(file, newData);
		file.close();
		/* the gamedata is read. now we do the necessary post processing. */
		newData.postProcess();
		return newData;
	}

	/**
	 * Returns the game's name of the specified computer report.
	 */
	private String getGameName(Reader report) {
		Map headerMap = CollectionFactory.createHashMap();
		try {
			headerMap = (new CRParser()).readHeader(report);
		} catch (IOException e) {
			log.error("Loader.getGameName(): unable to determine game's name of report " + report,e);
		}
		if (headerMap.containsKey("Spiel")) {
			return (String)headerMap.get("Spiel");
		} else {
			log.warn("Loader.getGameName(): report header does not contain 'Spiel' tag!");
			return "Eressea";
		}
	}

	private Rules loadRulesXML(String name) {
		log.info("LOAD RULES VIA XML PARSER !!!");

		log.debug("loading rules for \""+name+"\"");
		URL url = ResourcePathClassLoader.getResourceStatically("rules/" + name.toLowerCase() + ".xml");
		if (url != null) {
			Rules rules = null;
			try {
				rules = (new XMLRulesIO()).readRules(url.openStream());
			} catch (IOException e) {
				log.error("Exception while reading the rules for game " + name + ".",e);
			}
			if (rules == null) {
				/* This doesn't seem to be a rule file. Fallback to default,
				 if we haven't tried that yet. */
				log.warn("Encountered invalid rule file for game " + name + ".");
				/* This is bad, the default rules are invalid. */
				log.warn("The default ruleset is invalid. Operating with empty ruleset.");
				return new GenericRules();
			}
			return rules;
		} else {
			/* The desired rule file doesn't exist. Fallback to default, if
			 we haven't tried that yet. */
			/* This is bad. We don't even have the default rules. */
			log.warn("The default ruleset couldn't be found! Operating with an empty ruleset.");
			return new GenericRules();
		}
	}

	/** @author Rainer Klaffehn
	 * Read a rule file given by the specific name. This allows us to have
	 * separate rule files for different games. If a specific rule file
	 * couldn't be found, we use a default fallback.
	 * @param name The name of the game, for which we want a rule file.
	 * @return the ruleset object.
	 */
	private Rules loadRules(String name) {
		if(new File("XML").exists()) {
			return loadRulesXML(name);
		}

		log.debug("loading rules for \""+name+"\"");
		URL url = ResourcePathClassLoader.getResourceStatically("rules/" + name.toLowerCase() + ".cr");
		if (url != null) {
			Rules rules = null;
			try {
				rules = (new CRParser()).readRules(url.openStream());
			} catch (IOException e) {
				log.error("Exception while reading the rules for game " + name + ".",e);
				rules = null;
			}
			if (rules == null) {
				/* This doesn't seem to be a rule file. Fallback to default,
				 if we haven't tried that yet. */
				log.warn("Encountered invalid rule file for game " + name + ".");
				if (name.equalsIgnoreCase("eressea")) {
					/* This is bad, the default rules are invalid. */
					log.warn("The default ruleset is invalid. Operating with empty ruleset.");
					return new GenericRules();
				} else {
					return loadRules("eressea");
				}
			}
			return rules;
		} else {
			/* The desired rule file doesn't exist. Fallback to default, if
			 we haven't tried that yet. */
			if (name.equalsIgnoreCase("eressea")) {
				/* This is bad. We don't even have the default rules. */
				log.warn("The default ruleset couldn't be found! Operating with an empty ruleset.");
				return new GenericRules();
			} else {
				return loadRules("eressea");
			}
		}
	}


	/** 
	 * Creates a clone of the GameData using CRWriter/CRParser 
	 */
	public GameData doCloneGameData(GameData data) throws CloneNotSupportedException {
		try {
			File tempFile = CopyFile.createCrTempFile();
			tempFile.deleteOnExit();
			
			// write cr to file
			CRWriter crw = new CRWriter(FileType.createFileType(tempFile));
			crw.write(data);
			crw.close();
			
			GameData newData = Loader.loadCR(tempFile.getPath());
			newData.filetype=new FileType(data.filetype);
			tempFile.delete();
			
			return newData;
		} catch(IOException ioe) {
			log.error("Loader.cloneGameData failed!",ioe);
			throw new CloneNotSupportedException();
		}
	}
		
	/** 
	 * Creates a clone of the GameData using CRWriter/CRParser 
	 * @deprecated use new Loader().doCloneGameData(data) instead
	 */
	public static GameData cloneGameData(GameData data) throws CloneNotSupportedException {
		return new Loader().doCloneGameData(data);
	}
}
