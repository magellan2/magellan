// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.Action;

import com.eressea.demo.MagellanUndoManager;
import com.eressea.util.CollectionFactory;
/**
 *
 * @author  Andreas
 * @version
 */
public class RedoAction extends MenuAction implements PropertyChangeListener{

	private MagellanUndoManager undo;
	private String name = null;

	public RedoAction(MagellanUndoManager m) {
		name = this.getName();
		undo=m;
		setEnabled(undo.canUndo());
		if (isEnabled()) {
			putValue(Action.NAME, name + ": " + undo.getUndoPresentationName());
		}
		undo.addPropertyChangeListener(MagellanUndoManager.REDO,this);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		undo.redo();
	}

	public void propertyChange(java.beans.PropertyChangeEvent p1) {
		boolean b=((Boolean)p1.getNewValue()).booleanValue();
		if (b) {
			putValue(Action.NAME, name + ": " + undo.getRedoPresentationName());
			setEnabled(true);
		}
		else {
			putValue(Action.NAME, name);
			setEnabled(false);
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
			defaultTranslations.put("name"       , "Redo");
			defaultTranslations.put("mnemonic"   , "r");
			defaultTranslations.put("accelerator", "ctrl Y");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
