// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.eressea.Coordinate;
import com.eressea.demo.Client;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.event.SelectionEvent;
import com.eressea.swing.EresseaFileFilter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * @author  Ilja Pavkovic
 */
public class OpenSelectionAction extends MenuAction implements GameDataListener {
	private final static Logger log = Logger.getInstance(OpenSelectionAction.class);

	protected Client client;
	protected Map    selectedRegions = CollectionFactory.createHashtable();
	
	public OpenSelectionAction(Client client) {
		this.client = client;
		client.getDispatcher().addGameDataListener(this);
	}
	
	public void gameDataChanged(GameDataEvent e) {
		selectedRegions.clear();
	}
	
	/* will be void in AddSelectionAction */
	protected void preSetCleanSelection() {
		selectedRegions.clear();
	}

	/* will be overwritten by AddSelectionAction*/ 
	protected String getPropertyName() {
		return "Client.lastSELOpened";
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new EresseaFileFilter(SaveSelectionAction.EXTENSION,
														SaveSelectionAction.DESCRIPTION));
		fc.setSelectedFile(new File(client.getSettings().getProperty(getPropertyName(), "")));
		fc.setDialogTitle(getString("title"));
		if (fc.showOpenDialog(client) == JFileChooser.APPROVE_OPTION) {
			client.getSettings().setProperty(getPropertyName(), fc.getSelectedFile().getAbsolutePath());

			List coordinates = CollectionFactory.createLinkedList();
			try {
				BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()));
				while(true) {
					String line = br.readLine();
					if(line == null) break;
					coordinates.add(Coordinate.parse(line,SaveSelectionAction.DELIMITER));
				}
				br.close();
			} catch (Exception exc) {				
				log.error(exc);
				JOptionPane.showMessageDialog(client, Translations.getTranslation(this,"msg.fileordersopen.error.text") + e.toString(), Translations.getTranslation(this,"msg.fileordersopen.error.title"), JOptionPane.ERROR_MESSAGE);
			}
			// load successful, now fill up selection
			preSetCleanSelection();
			for(Iterator iter = coordinates.iterator();iter.hasNext();) {
				Coordinate c = (Coordinate) iter.next();
				if(client.getData().regions().get(c) != null) {
					selectedRegions.put(c,client.getData().regions().get(c));
				}
			}
			// fire change event
			client.getDispatcher().fire(new SelectionEvent(this, selectedRegions.values(), null, SelectionEvent.ST_REGIONS));
		}
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
			defaultTranslations.put("name"       , "Open selection...");
			defaultTranslations.put("mnemonic"   , "p");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
			defaultTranslations.put("msg.fileordersopen.error.text",
									"While loading the selection the following error occurred:\n");
			defaultTranslations.put("msg.fileordersopen.error.title",
									"Error on load");
			defaultTranslations.put("title", "open selection file");

		}
		return defaultTranslations;
	}
}
