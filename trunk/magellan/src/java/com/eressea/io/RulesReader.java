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

import java.io.File;
import java.io.IOException;

import com.eressea.Rules;

import com.eressea.io.cr.CRParser;
import com.eressea.io.file.FileType;
import com.eressea.io.file.FileTypeFactory;

import com.eressea.rules.GenericRules;

import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class RulesReader {
	private static final Logger log = Logger.getInstance(RulesReader.class);

	/**
	 * Creates a new RulesReader object.
	 */
	public RulesReader() {
	}

	/**
	 * Reads the rules of the given gamedata. Right now it first tries to read
	 * it from an xml. If this fails it  possibly reads the cr
	 *
	 * @param gameName TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Rules readRules(String gameName) {
		return loadRules(gameName);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Rules loadRules(String name) {
		String ending = new File("XML").exists() ? ".xml" : ".cr";

		if(log.isDebugEnabled()) {
			log.debug("loading rules for \"" + name + "\" (ending: " + ending +
					  ")");
		}

		try {
			FileType filetype = FileTypeFactory.singleton()
											   .createInputStreamSourceFileType("rules/" +
																				name +
																				ending);

			return new CRParser().readRules(filetype);
		} catch(IOException e) {
			/* The desired rule file doesn't exist. Fallback to default, if
			   we haven't tried that yet. */
			if(name.equalsIgnoreCase("eressea")) {
				/* This is bad. We don't even have the default rules. */
				log.warn("The default ruleset couldn't be found! Operating with an empty ruleset.",
						 e);

				return new GenericRules();
			} else {
				return loadRules("eressea");
			}
		}
	}
}
