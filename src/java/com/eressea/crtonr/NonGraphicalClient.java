// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Martin Hershoff, Sebastian Pappert,
//							Klaas Prause,  Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.crtonr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.eressea.GameData;
import com.eressea.cr.Loader;
import com.eressea.util.Locales;
import com.eressea.util.OrderedOutputProperties;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

public class NonGraphicalClient
{
	private final static Logger log = Logger.getInstance(NonGraphicalClient.class);
	private GameData data = null;
	private static File filesDirectory = null;
	private boolean everLoadedReport = false;
	private Properties settings = null;
	
	public NonGraphicalClient(GameData gd, File fileDir) {
		super();
		data = gd;
		filesDirectory = fileDir;
		
		settings = new OrderedOutputProperties();
		File settingsFile = new File(filesDirectory, "magellan.ini");
		// load settings from file
		if (settingsFile.exists()) {
			try {
				settings.load(new FileInputStream(settingsFile));
			}
			catch (IOException e) {
				log.error(e);
			}
		} else {
			log.info("Client.Client(): settings file does not exist, using default values.");
		}

		// init the locales
		Locales.init(settings);
		// init the translations
		Translations.setClassLoader(new com.eressea.resource.ResourcePathClassLoader(settings));
	}

  /**
   * Load a CR file into a GameData object.
   * 
   * @param fileName CR file to be loaded.
   * @return the GameData object build from CR file.
   */	
	public GameData loadCR(java.lang.String fileName) {
		GameData d = null;
		try {
			d = Loader.loadCR(fileName);
			everLoadedReport = true;
		} catch (Loader.MissingCRException e) {
			// JOptionPane.showMessageDialog(this, getDict().getString("msg.loadcr.missingcr.text.1") + fileName + getDict().getString("msg.loadcr.missingcr.text.2"), getDict().getString("msg.loadcr.error.title"), JOptionPane.ERROR_MESSAGE);
		} catch (IOException exc) {
			// JOptionPane.showMessageDialog(this, getDict().getString("msg.loadcr.error.text") + exc.toString(), getDict().getString("msg.loadcr.error.title"), JOptionPane.ERROR_MESSAGE);
			log.error(exc);
		}

		return d;
	}
}
