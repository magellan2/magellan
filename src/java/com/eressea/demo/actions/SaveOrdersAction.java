// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import com.eressea.util.CollectionFactory;

import javax.swing.KeyStroke;

import com.eressea.demo.Client;
import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.demo.desktop.ShortcutListener;
import com.eressea.swing.OrderWriterDialog;
import com.eressea.util.Translations;

/**
 *
 * @author  Andreas
 * @version
 */
public class SaveOrdersAction extends MenuAction implements ShortcutListener {

	private Client client;
	private List shortCuts;

	/** Creates new OpenCRAction */
	public SaveOrdersAction(Client parent) {
		client=parent;
		
		shortCuts = CollectionFactory.createArrayList(2);
		shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		DesktopEnvironment.registerShortcutListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		OrderWriterDialog d = new OrderWriterDialog(client, true, client.getData(), client.getSettings(), client.getSelectedRegions().values());
		d.setVisible(true);
	}
	
	public void shortCut(javax.swing.KeyStroke shortcut) {
		int index = shortCuts.indexOf(shortcut);
		if (index>=0 && index<3) {
			switch(index) {
				case 0: new OrderWriterDialog(client, true, client.getData(), client.getSettings(), client.getSelectedRegions().values()).runClipboard(); break;
				case 1: new OrderWriterDialog(client, true, client.getData(), client.getSettings(), client.getSelectedRegions().values()).runMail(); break;
			}
		}
	}
	
	/**
	 * Should return all short cuts this class want to be informed.
	 * The elements should be of type javax.swing.KeyStroke
	 */
	public java.util.Iterator getShortCuts() {
		return shortCuts.iterator();
	}
	
	public java.lang.String getShortcutDescription(java.lang.Object obj) {
		int index = shortCuts.indexOf(obj);
		return Translations.getTranslation(this,"shortcuts.description."+String.valueOf(index));
	}
	
	public java.lang.String getListenerDescription() {
		return com.eressea.util.Translations.getTranslation(this,"shortcuts.title");
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
			defaultTranslations.put("name"       , "Save orders...");
			defaultTranslations.put("mnemonic"   , "r");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
			
			defaultTranslations.put("shortcuts.title",
									"Fast save");
			defaultTranslations.put("shortcuts.description.-1",
									"Save dialog");
			defaultTranslations.put("shortcuts.description.0",
									"Save to clipboard");
			defaultTranslations.put("shortcuts.description.1",
									"Send e-mail");
		}
		return defaultTranslations;
	}
}
