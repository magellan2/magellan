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
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class ECheckAction extends MenuAction implements ShortcutListener {
	private KeyStroke imStroke;

	/**
	 * Creates a new ECheckAction object.
	 *
	 * @param parent TODO: DOCUMENT ME!
	 */
	public ECheckAction(Client client) {
        super(client);
		imStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
		DesktopEnvironment.registerShortcutListener(imStroke, this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		ECheckDialog d = new ECheckDialog(client, false, client.getDispatcher(), client.getData(),
										  client.getProperties(), client.getSelectedRegions().values());
		d.setVisible(true);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param shortcut TODO: DOCUMENT ME!
	 */
	public void shortCut(javax.swing.KeyStroke shortcut) {
		ECheckDialog d = new ECheckDialog(client, false, client.getDispatcher(), client.getData(),
										  client.getProperties(), client.getSelectedRegions().values());
		d.setVisible(true);
		d.exec();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getShortCuts() {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public java.lang.String getShortcutDescription(java.lang.Object obj) {
		if(imStroke.equals(obj)) {
			return Translations.getTranslation(this, "shortcuts.description.1");
		}

		return Translations.getTranslation(this, "shortcuts.description.0");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public java.lang.String getListenerDescription() {
		return Translations.getTranslation(this, "shortcuts.title");
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
			defaultTranslations.put("name", "ECheck...");
			defaultTranslations.put("mnemonic", "e");
			defaultTranslations.put("accelerator", "ctrl E");
			defaultTranslations.put("tooltip", "");
			defaultTranslations.put("shortcuts.description.1", "Run ECheck immediately");
			defaultTranslations.put("shortcuts.description.0", "Show dialog");
			defaultTranslations.put("shortcuts.title", "ECheck");
		}

		return defaultTranslations;
	}
}
