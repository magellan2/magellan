// ===// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan G�tz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// --
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Map;

import com.eressea.demo.EMapOverviewPanel;
import com.eressea.util.CollectionFactory;
/**
 *
 * @author  Andreas
 * @version
 */
public class FindPreviousUnconfirmedAction extends MenuAction{

	private EMapOverviewPanel target;

	public FindPreviousUnconfirmedAction(Component client,EMapOverviewPanel e) {
		target=e;
	}

	public void actionPerformed(ActionEvent e) {
		target.shortCut_Reverse_N();
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
			defaultTranslations.put("name"       , "Previous unconfirmed");
			defaultTranslations.put("mnemonic"   , "p");
			defaultTranslations.put("accelerator", "ctrl shift N");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
