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

package com.eressea.demo.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFileChooser;

import com.eressea.GameData;
import com.eressea.demo.Client;
import com.eressea.swing.EresseaFileFilter;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class OpenCRAction extends MenuAction {
	private Client client;

	/**
	 * Creates new OpenCRAction
	 *
	 * @param parent TODO: DOCUMENT ME!
	 */
	public OpenCRAction(Client parent) {
		client = parent;
	}

	/**
	 * Called when the file->open menu is selected in order to open a certain cr file. Displays a
	 * file chooser and loads the selected cr file.
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if(!client.askToSave()) {
			return;
		}

		JFileChooser fc = new JFileChooser();
		Properties settings = client.getSettings();
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.CR_FILTER));
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.GZ_FILTER));
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.BZ2_FILTER));
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.ZIP_FILTER));

		int lastFileFilter = Integer.parseInt(settings.getProperty("Client.lastSelectedOpenCRFileFilter",
																   "3"));
		lastFileFilter = Math.min(fc.getChoosableFileFilters().length - 1, lastFileFilter);
		fc.setFileFilter(fc.getChoosableFileFilters()[lastFileFilter]);

		File file = new File(settings.getProperty("Client.lastCROpened", ""));
		fc.setSelectedFile(file);

		if(file.exists()) {
			fc.setCurrentDirectory(file.getParentFile());
		}

		fc.setSelectedFile(file);
		fc.setAccessory(new com.eressea.swing.HistoryAccessory(settings, fc));
		fc.setDialogTitle(getString("title"));

		if(fc.showOpenDialog(client) == JFileChooser.APPROVE_OPTION) {
			// find selected FileFilter
			int i = 0;

			while(!fc.getChoosableFileFilters()[i].equals(fc.getFileFilter())) {
				i++;
			}

			settings.setProperty("Client.lastSelectedOpenCRFileFilter", String.valueOf(i));

			settings.setProperty("Client.lastCROpened", fc.getSelectedFile().getAbsolutePath());
			client.addFileToHistory(fc.getSelectedFile());

			boolean bOpenEqualsSave = Boolean.valueOf(settings.getProperty("Client.openEqualsSave",
																		   "false")).booleanValue();

			if(bOpenEqualsSave) {
				settings.setProperty("Client.lastCRSaved", fc.getSelectedFile().getAbsolutePath());
			}

			GameData data = client.loadCR(fc.getSelectedFile().getPath());

			if(data != null) {
				client.setData(data);
				client.setReportChanged(false);
			}
		}
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("name", "Open...");
			defaultTranslations.put("mnemonic", "o");
			defaultTranslations.put("accelerator", "ctrl O");
			defaultTranslations.put("tooltip", "");

			defaultTranslations.put("title", "Open cr file");
		}

		return defaultTranslations;
	}
}
