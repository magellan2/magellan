// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.KeyStroke;

import com.eressea.demo.Client;
import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.demo.desktop.ShortcutListener;
import com.eressea.swing.ECheckDialog;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;

/**
 *
 * @author  Andreas
 * @version
 */
public class ECheckAction extends MenuAction implements ShortcutListener{
	
	private Client client;
	private KeyStroke imStroke;
	
	public ECheckAction(Client parent) {
		client=parent;
		imStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E,KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
		DesktopEnvironment.registerShortcutListener(imStroke, this);
	}
	
	public void actionPerformed(java.awt.event.ActionEvent e) {
		ECheckDialog d = new ECheckDialog(client, false, client.getDispatcher(), client.getData(), client.getSettings(), client.getSelectedRegions().values());
		d.setVisible(true);
	}
	
	public void shortCut(javax.swing.KeyStroke shortcut) {
		ECheckDialog d = new ECheckDialog(client, false, client.getDispatcher(), client.getData(), client.getSettings(), client.getSelectedRegions().values());
		d.setVisible(true);
		d.exec();
	}
	
	public Iterator getShortCuts() {
		return null;
	}
	
	public java.lang.String getShortcutDescription(java.lang.Object obj) {
		if (imStroke.equals(obj)) {
			return Translations.getTranslation(this,"shortcuts.description.1");
		}
		return Translations.getTranslation(this,"shortcuts.description.0");
	}	
	
	public java.lang.String getListenerDescription() {
		return Translations.getTranslation(this,"shortcuts.title");
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
			defaultTranslations.put("name"       , "ECheck...");
			defaultTranslations.put("mnemonic"   , "e");
			defaultTranslations.put("accelerator", "ctrl E");
			defaultTranslations.put("tooltip"    , "");
			defaultTranslations.put("shortcuts.description.1",
									"Run ECheck immediately");
			defaultTranslations.put("shortcuts.description.0",
									"Show dialog");
			defaultTranslations.put("shortcuts.title",
									"ECheck");
		}
		return defaultTranslations;
	}
}
