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
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.KeyStroke;

import com.eressea.demo.Client;
import com.eressea.demo.desktop.DesktopEnvironment;
import com.eressea.demo.desktop.ShortcutListener;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.swing.OrderWriterDialog;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class SaveOrdersAction extends MenuAction implements ShortcutListener,GameDataListener {
	private List shortCuts;

	/**
	 * Creates new OpenCRAction
	 *
	 * @param parent TODO: DOCUMENT ME!
	 */
	public SaveOrdersAction(Client client) {
        super(client);

		shortCuts = CollectionFactory.createArrayList(2);
		shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK |
											 KeyEvent.SHIFT_MASK));
		shortCuts.add(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_MASK |
											 KeyEvent.SHIFT_MASK));
		DesktopEnvironment.registerShortcutListener(this);
        setEnabled(false);
        client.getDispatcher().addGameDataListener(this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		OrderWriterDialog d = new OrderWriterDialog(client, true, client.getData(),
													client.getProperties(),
													client.getSelectedRegions().values());
		d.setVisible(true);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param shortcut TODO: DOCUMENT ME!
	 */
	public void shortCut(KeyStroke shortcut) {
		int index = shortCuts.indexOf(shortcut);

		if((index >= 0) && (index < 3)) {
			switch(index) {
			case 0:
				new OrderWriterDialog(client, true, client.getData(), client.getProperties(),
									  client.getSelectedRegions().values()).runClipboard();

				break;

			case 1:
				new OrderWriterDialog(client, true, client.getData(), client.getProperties(),
									  client.getSelectedRegions().values()).runMail();

				break;
			}
		}
	}

	/**
	 * Should return all short cuts this class want to be informed. The elements should be of type
	 * javax.swing.KeyStroke
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getShortCuts() {
		return shortCuts.iterator();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param obj TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getShortcutDescription(java.lang.Object obj) {
		int index = shortCuts.indexOf(obj);

		return Translations.getTranslation(this, "shortcuts.description." + String.valueOf(index));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public java.lang.String getListenerDescription() {
		return com.eressea.util.Translations.getTranslation(this, "shortcuts.title");
	}
	
	public void gameDataChanged(GameDataEvent e) {
		// TODO Auto-generated method stub
		int i = super.client.getData().regions().size();
		if (i>0) {
			setEnabled(true);
		} else {
			setEnabled(false);
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
			defaultTranslations.put("name", "Save orders...");
			defaultTranslations.put("mnemonic", "r");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");

			defaultTranslations.put("shortcuts.title", "Fast save");
			defaultTranslations.put("shortcuts.description.-1", "Save dialog");
			defaultTranslations.put("shortcuts.description.0", "Save to clipboard");
			defaultTranslations.put("shortcuts.description.1", "Send e-mail");
		}

		return defaultTranslations;
	}
}
