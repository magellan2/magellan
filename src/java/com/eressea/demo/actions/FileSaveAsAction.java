// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.eressea.cr.CRWriter;
import com.eressea.demo.Client;
import com.eressea.swing.EresseaFileFilter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;
import com.eressea.util.file.FileBackup;
import com.eressea.util.file.FileType;
import com.eressea.util.logging.Logger;

/**
 *
 * @author  Andreas
 * @version
 */
public class FileSaveAsAction extends MenuAction {
	private final static Logger log = Logger.getInstance(FileSaveAsAction.class);

	protected Client  client;

	public FileSaveAsAction(Client parent) {
		client=parent;
	}

	public void actionPerformed(ActionEvent e) {
		FileType file = getFile();
		if(file != null) {
			doSaveAction(file);
		} else {
			doSaveAsAction();
		}
	}

	protected void doSaveAsAction() {
		Properties settings=client.getSettings();
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
        EresseaFileFilter crFilter = new EresseaFileFilter(EresseaFileFilter.CR_FILTER);
		fc.addChoosableFileFilter(crFilter);
        EresseaFileFilter gzFilter = new EresseaFileFilter(EresseaFileFilter.GZ_FILTER);
		fc.addChoosableFileFilter(gzFilter);
        EresseaFileFilter bz2Filter = new EresseaFileFilter(EresseaFileFilter.BZ2_FILTER);
		fc.addChoosableFileFilter(bz2Filter);
        File selectedFile = new File(settings.getProperty("Client.lastCRSaved", ""));
		fc.setSelectedFile(selectedFile);
        // select an active file filter
        if (selectedFile != null) {
            if (crFilter.accept(selectedFile)) {
                fc.setFileFilter(crFilter);
            } else if (gzFilter.accept(selectedFile)) {
                fc.setFileFilter(gzFilter);
            } else if (bz2Filter.accept(selectedFile)) {
                fc.setFileFilter(bz2Filter);
            }
        }

		fc.setAccessory(new com.eressea.swing.HistoryAccessory(settings, fc));
		fc.setDialogTitle(getString("title"));

		if (fc.showSaveDialog(client) == JFileChooser.APPROVE_OPTION) {

			boolean bOpenEqualsSave = Boolean.valueOf( settings.getProperty("Client.openEqualsSave", "false" )).booleanValue();

			if (bOpenEqualsSave)
				settings.setProperty("Client.lastCROpened", fc.getSelectedFile().getAbsolutePath());

			File dataFile = fc.getSelectedFile();
			EresseaFileFilter actFilter = (EresseaFileFilter) fc.getFileFilter();
			dataFile = actFilter.addExtension(dataFile);

			if(dataFile.exists()) {
				// FIXME(pavkovic) ask, if file should be overwritten
				// stop execution of saveaction if necessary
				try {
					File backup = FileBackup.create(dataFile);
					log.info("Created backupfile "+backup);
				} catch(IOException ie) {
					log.warn("Could not create backupfile for file "+dataFile);
				}
			}
			doSaveAction(FileType.createFileType(dataFile));
		}
	}

	protected void doSaveAction(FileType filetype) {
		try {
			// create backup file
			File backup = FileBackup.create(filetype.getFile());
			log.info("Created backupfile "+backup);

			// write cr to file
			CRWriter crw = new CRWriter(filetype);
			crw.write(client.getData());
			crw.close();

			// everything worked fine, so reset reportchanged state and also store new FileType setting
			client.setReportChanged(false);
			client.getData().filetype = filetype;
			client.getData().resetToUnchanged();
			client.getSettings().setProperty("Client.lastCRSaved", filetype.getName());

		} catch (IOException exc) {
			log.error(exc);
			JOptionPane.showMessageDialog(client,
										  exc.toString(),
										  Translations.getTranslation(FileSaveAction.class,"msg.filesave.error.title"),
										  JOptionPane.ERROR_MESSAGE);
		}
	}
	/** this function delivers overwriteable FileType. In FileSaveAsAction it shall deliver null,
	 * in FileSaveAction the file type of the gamedata if exists.
	 */
 	protected FileType getFile() {
		return null;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("name"       , "Save as...");
			defaultTranslations.put("mnemonic"   , "a");
			defaultTranslations.put("accelerator", "ctrl shift S");
			defaultTranslations.put("tooltip"    , "");

			defaultTranslations.put("msg.filesave.error.title",
									"Error on save");
			defaultTranslations.put("title", "save cr file as");
		}
		return defaultTranslations;
	}
}
