// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.eressea.demo.Client;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesDialog;
import com.eressea.swing.preferences.PreferencesFactory;
import com.eressea.util.CollectionFactory;

/**
 *
 * @author  Andreas Gampe
 * @author  Ilja Pavkovic
 * @version $Revision$
 */
public class OptionAction extends MenuAction {

	private List adapters;
	private Client client;

	/** 
	 * This timer object is used to rebuild the PreferencesDialog in background.
	 * If the actionPerformed method is called the dialog will be discarded and recreated
	 */
	private Timer t;
	
	public OptionAction(Client parent, List adapters) {
		this.adapters = adapters;
		client = parent;
		initTimer();
	}

	PreferencesDialog dialog=null;
	public void actionPerformed(ActionEvent e) {
		if(dialog==null) {
			buildDialog();
		}
		dialog.show();
		dialog = null;
	}

	private void initTimer() {
		t = new Timer(true);
		t.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					buildDialog();
				}
			} , 1000, 1000);
	}

	private void buildDialog() {
		if(dialog != null) return;
		PreferencesDialog pd = new PreferencesDialog(client, true, client.getSettings(),adapters);
		if(dialog==null) {
			dialog = pd;
		}
	}

	public void updateLaF() {
		if(dialog != null) {
			dialog.updateLaF();
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
			defaultTranslations.put("name"       , "Options...");
			defaultTranslations.put("mnemonic"   , "p");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip"    , "");
		}
		return defaultTranslations;
	}
}
