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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.demo.Client;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;
import com.eressea.io.file.FileBackup;
import com.eressea.swing.EresseaFileFilter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Ilja Pavkovic
 */
public class SaveSelectionAction extends MenuAction implements SelectionListener, GameDataListener {
	private static final Logger log = Logger.getInstance(SaveSelectionAction.class);

	// FIXME: 
	// TODO: Move to EresseaFileFilter, 
	// add descriptions to res/lang/com-eressea-swing-eresseafilefilter.properties
	// add descriptions to res/lang/com-eressea-swing-eresseafilefilter_en.properties

	/** TODO: DOCUMENT ME! */
	public static final String DESCRIPTION = "Selections";

	/** TODO: DOCUMENT ME! */
	public static final String EXTENSION = "sel";

	/** TODO: DOCUMENT ME! */
	public static final String DELIMITER = " ";

	/** TODO: DOCUMENT ME! */
	public static final char COMMENT = ';';
	private Map selectedRegions = CollectionFactory.createTreeMap();

	/**
	 * Creates a new SaveSelectionAction object.
	 *
	 * @param client TODO: DOCUMENT ME!
	 */
	public SaveSelectionAction(Client client) {
        super(client);
		this.client.getDispatcher().addSelectionListener(this);
		this.client.getDispatcher().addGameDataListener(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void selectionChanged(SelectionEvent e) {
		if(e.getSource() == this) {
			return;
		}

		if((e.getSelectedObjects() != null) && (e.getSelectionType() == SelectionEvent.ST_REGIONS)) {
			selectedRegions.clear();

			for(Iterator iter = e.getSelectedObjects().iterator(); iter.hasNext();) {
				Object o = iter.next();

				if(o instanceof Region) {
					Region r = (Region) o;
					selectedRegions.put(r.getID(), r);
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		selectedRegions.clear();
	}

	protected String getPropertyName() {
		return "Client.lastSELSaved";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new EresseaFileFilter(EXTENSION, DESCRIPTION));
		fc.setSelectedFile(new File(client.getProperties().getProperty(getPropertyName(), "")));
		fc.setDialogTitle(getString("title"));

		if(fc.showSaveDialog(client) == JFileChooser.APPROVE_OPTION) {
			PrintWriter pw = null;
			try {
				client.getProperties().setProperty(getPropertyName(),
												 fc.getSelectedFile().getAbsolutePath());

				if(fc.getSelectedFile().exists()) {
					// create backup file
					try {
						File backup = FileBackup.create(fc.getSelectedFile());
						log.info("Created backupfile " + backup);
					} catch(IOException ie) {
						log.warn("Could not create backupfile for file " + fc.getSelectedFile());
					}
				} 
				
				pw = new PrintWriter(new BufferedWriter(new FileWriter(fc.getSelectedFile())));

				for(Iterator iter = selectedRegions.keySet().iterator(); iter.hasNext();) {
					pw.println(((Coordinate) iter.next()).toString(DELIMITER));
				}

				pw.close();
			} catch(IOException exc) {
				log.error(exc);
				JOptionPane.showMessageDialog(client, exc.toString(),
											  Translations.getTranslation(this,
																		  "msg.filesave.error.title"),
											  JOptionPane.ERROR_MESSAGE);
			} finally {
				if(pw != null) {
					pw.close();
				}
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
			defaultTranslations.put("name", "Save selection as...");
			defaultTranslations.put("mnemonic", "e");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
			defaultTranslations.put("msg.filesave.error.title", "Error on save");
			defaultTranslations.put("title", "save selection file as");
		}

		return defaultTranslations;
	}
}
