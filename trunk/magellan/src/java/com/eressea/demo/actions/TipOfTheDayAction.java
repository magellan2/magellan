// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;

import java.awt.Frame;
import java.util.Map;
import java.util.Properties;

import com.eressea.swing.TipOfTheDay;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas
 * @version
 */
public class TipOfTheDayAction extends MenuAction {

	private Frame parent;
	private Properties settings;

	public TipOfTheDayAction(Frame parent, Properties settings) {
		this.parent = parent;
		this.settings = settings;
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		if (!TipOfTheDay.active) {
			TipOfTheDay totd = new TipOfTheDay(parent, settings);
			totd.show();
			totd.showNextTip();
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
			defaultTranslations.put("name"       , "Tip Of The Day...");
			defaultTranslations.put("mnemonic"   , "t");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
