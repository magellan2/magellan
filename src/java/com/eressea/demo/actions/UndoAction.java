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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.Action;

import com.eressea.demo.Client;
import com.eressea.demo.MagellanUndoManager;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class UndoAction extends MenuAction implements PropertyChangeListener {
	private MagellanUndoManager undo;
	private String name = null;

	/**
	 * Creates a new UndoAction object.
	 *
	 * @param m TODO: DOCUMENT ME!
	 */
	public UndoAction(Client client, MagellanUndoManager m) {
        super(client);
		this.name = getName();
		undo = m;
		setEnabled(undo.canUndo());

		if(isEnabled()) {
			putValue(Action.NAME, name + ": " + undo.getUndoPresentationName());
		}

		undo.addPropertyChangeListener(MagellanUndoManager.UNDO, this);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getIconName() {
		return "undo_edit";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if (undo.canUndo())
			undo.undo();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p1 TODO: DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent p1) {
		boolean enabled = ((Boolean) p1.getNewValue()).booleanValue();

		if(enabled) {
			putValue(Action.NAME, name + ": " + undo.getUndoPresentationName());
		} else {
			putValue(Action.NAME, name);
		}
		setEnabled(enabled);

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
			defaultTranslations.put("name", "Undo");
			defaultTranslations.put("mnemonic", "u");
			defaultTranslations.put("accelerator", "ctrl Z");
			defaultTranslations.put("tooltip", "");
		}

		return defaultTranslations;
	}
}
