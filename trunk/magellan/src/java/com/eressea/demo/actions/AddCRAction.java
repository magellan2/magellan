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
import com.eressea.swing.HistoryAccessory;
import com.eressea.util.CollectionFactory;
import com.eressea.util.ReportMerger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas, Ulrich Küster
 */
public class AddCRAction extends MenuAction {

	/**
	 * Creates new AddCRAction
	 *
	 * @param parent TODO: DOCUMENT ME!
	 */
	public AddCRAction(Client client) {
        super(client);
	}

	/**
	 * Called when the file->add menu is selected in order to add a certain cr file to current game
	 * data. Displays a file chooser and adds the selected cr file to the current game data.
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		final Client theclient = client;
		Properties settings = client.getProperties();
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);

		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.CR_FILTER));
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.GZ_FILTER));
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.BZ2_FILTER));
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.ZIP_FILTER));

		int lastFileFilter = Integer.parseInt(settings.getProperty("Client.lastSelectedAddCRFileFilter",
																   "3"));
		fc.setFileFilter(fc.getChoosableFileFilters()[lastFileFilter]);

		File file = new File(settings.getProperty("Client.lastCRAdded", ""));
		fc.setSelectedFile(file);

		if(file.exists()) {
			fc.setCurrentDirectory(file.getParentFile());
		}

		HistoryAccessory acc = new HistoryAccessory(settings, fc);
		fc.setAccessory(acc);
		fc.setDialogTitle(getString("title"));

		if(fc.showOpenDialog(client) == JFileChooser.APPROVE_OPTION) {
			// find selected FileFilter
			int i = 0;

			while(!fc.getChoosableFileFilters()[i].equals(fc.getFileFilter())) {
				i++;
			}

			settings.setProperty("Client.lastSelectedAddCRFileFilter", String.valueOf(i));

			// force user to choose a file on save
			//client.setDataFile(null);
			ReportMerger merger = null;
			File files[] = fc.getSelectedFiles();

			if(files.length == 0) {
				merger = new ReportMerger(client.getData(), fc.getSelectedFile(),
										  new ReportMerger.Loader() {
						// pavkovic 2002.11.05: prevent name clash with variable "file"
						public GameData load(File aFile) {
							return theclient.loadCR(aFile.getAbsolutePath());
						}
					},
										  new ReportMerger.AssignData() {
						public void assign(GameData _data) {
							theclient.setData(_data);
						}
					});
				settings.setProperty("Client.lastCRAdded", fc.getSelectedFile().getAbsolutePath());
			} else {
				merger = new ReportMerger(client.getData(), files,
										  new ReportMerger.Loader() {
						// pavkovic 2002.11.05: prevent name clash with variable "file"
						public GameData load(File aFile) {
							return theclient.loadCR(aFile.getAbsolutePath());
						}
					},
										  new ReportMerger.AssignData() {
						public void assign(GameData _data) {
							theclient.setData(_data);
						}
					});
				settings.setProperty("Client.lastCRAdded", files[files.length - 1].getAbsolutePath());
			}

			merger.merge(client);
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
			defaultTranslations.put("name", "Add...");
			defaultTranslations.put("mnemonic", "d");
			defaultTranslations.put("accelerator", "ctrl D");
			defaultTranslations.put("tooltip", "");

			defaultTranslations.put("title", "Add cr file(s)");
		}

		return defaultTranslations;
	}
}
