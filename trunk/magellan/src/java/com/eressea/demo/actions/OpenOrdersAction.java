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
import java.io.FileReader;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.eressea.demo.Client;
import com.eressea.swing.OpenOrdersAccessory;
import com.eressea.util.CollectionFactory;
import com.eressea.util.OrderReader;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 *
 * @author  Andreas
 * @version
 */
public class OpenOrdersAction extends MenuAction {
	private final static Logger log = Logger.getInstance(OpenOrdersAction.class);

	private Client client;

	public OpenOrdersAction(Client parent) {
		client=parent;
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		Properties settings=client.getSettings();
		fc.addChoosableFileFilter(new com.eressea.swing.EresseaFileFilter(com.eressea.swing.EresseaFileFilter.TXT_FILTER));
		fc.setSelectedFile(new File(settings.getProperty("Client.lastOrdersOpened", "")));

		OpenOrdersAccessory acc = new OpenOrdersAccessory(settings, fc);
		fc.setAccessory(acc);
		if (fc.showOpenDialog(client) == JFileChooser.APPROVE_OPTION) {
			settings.setProperty("Client.lastOrdersOpened", fc.getSelectedFile().getAbsolutePath());
			OrderReader r = new OrderReader(client.getData());
			r.setAutoConfirm(acc.getAutoConfirm());
			r.ignoreSemicolonComments(acc.getIgnoreSemicolonComments());
			try {
				r.read(new FileReader(fc.getSelectedFile().getAbsolutePath()));
				OrderReader.Status status = r.getStatus();
				Object[] msgArgs = {
					new Integer(status.factions),
					new Integer(status.units)
				};
				JOptionPane.showMessageDialog(client, (new java.text.MessageFormat(Translations.getTranslation(this,"msg.fileordersopen.status.text"))).format(msgArgs), Translations.getTranslation(this,"msg.fileordersopen.status.title"), JOptionPane.PLAIN_MESSAGE);
			} catch (Exception exc) {
				log.error(exc);
				JOptionPane.showMessageDialog(client, Translations.getTranslation(this,"msg.fileordersopen.error.text") + e.toString(), Translations.getTranslation(this,"msg.fileordersopen.error.title"), JOptionPane.ERROR_MESSAGE);
			}
			client.getDispatcher().fire(new com.eressea.event.GameDataEvent(client, client.getData()));
		}

		// repaint since command confirmation status may have changed
		client.getDesktop().repaint("OVERVIEW");
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
			defaultTranslations.put("name"       , "Open orders...");
			defaultTranslations.put("mnemonic"   , "p");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
			defaultTranslations.put("msg.fileordersopen.status.text",
									"Read orders for {0} faction(s) and {1} unit(s).");
			defaultTranslations.put("msg.fileordersopen.status.title",
									"Orders read");
			defaultTranslations.put("msg.fileordersopen.error.text",
									"While loading the orders the following error occurred:\n");
		}
		return defaultTranslations;
	}
}
