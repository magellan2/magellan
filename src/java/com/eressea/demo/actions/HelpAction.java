// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import com.eressea.util.CollectionFactory;

import javax.swing.JOptionPane;

import com.eressea.demo.Client;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

public class HelpAction extends MenuAction {
	private final static Logger log = Logger.getInstance(HelpAction.class);

	private Object helpBroker = null;
	private Client client;

	public HelpAction(Client parent) {
		client=parent;
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		// SG: had a lot of fun when I implemented this :-)
		try {
			Class[] cParamArray = new Class[1];
			Object[] paramArray = new Object[2];
			ClassLoader loader = new com.eressea.resource.ResourcePathClassLoader(client.getSettings());
			
			URL hsURL = loader.getResource("magellan.hs");
			
			if (hsURL == null) {
				JOptionPane.showMessageDialog(client, Translations.getTranslation(this,"msg.helpsetnotfound.text"));
				return;
			}

			Class helpSetClass = null;
			Class displayHelpFromSourceClass = null;
			Class helpBrokerClass = null;
			
			if (this.helpBroker == null) {
				try {
					helpSetClass = loader.loadClass("javax.help.HelpSet");
					displayHelpFromSourceClass = loader.loadClass("javax.help.CSH$DisplayHelpFromSource");
					helpBrokerClass = loader.loadClass("javax.help.HelpBroker");
				} catch (ClassNotFoundException ex) {
					JOptionPane.showMessageDialog(client, Translations.getTranslation(this,"msg.javahelpnotfound.text"));
					return;
				}
				Class[] helpSetConstructorSignature = {
					Class.forName("java.lang.ClassLoader"),
					hsURL.getClass()
				};
				Constructor helpSetConstructor = helpSetClass.getConstructor(helpSetConstructorSignature);
				Object[] helpSetConstructorArgs = {
					loader,
					hsURL
				};
				// this calls new javax.help.Helpset(ClassLoader, URL)
				Object helpSet = helpSetConstructor.newInstance(helpSetConstructorArgs);

				Method helpSetCreateHelpBrokerMethod = helpSetClass.getMethod("createHelpBroker", null);
				// this calls new javax.help.Helpset.createHelpBroker()
				this.helpBroker = helpSetCreateHelpBrokerMethod.invoke(helpSet, null);
				
				
				Method initPresentationMethod = helpBrokerClass.getMethod("initPresentation", null);
				// this calls new javax.help.HelpBroker.initPresentation()
				initPresentationMethod.invoke(this.helpBroker, null);
			}

			Class[] setDisplayedMethodSignature = {
				boolean.class
			};
			Method setDisplayedMethod = this.helpBroker.getClass().getMethod("setDisplayed", setDisplayedMethodSignature);
			Object[] setDisplayedMethodArgs = {
				new Boolean(true)
			};
			// this calls new javax.help.HelpBroker.setDisplayed(true)
			setDisplayedMethod.invoke(this.helpBroker, setDisplayedMethodArgs);
		} catch (Exception ex) {
			log.error(ex);
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
			defaultTranslations.put("name"       , "Help...");
			defaultTranslations.put("mnemonic"   , "h");
			defaultTranslations.put("accelerator", "F1");
			defaultTranslations.put("tooltip"    , "");
			defaultTranslations.put("msg.helpsetnotfound.text",
									"The Magellan help could not be found. Along with JavaHelp\n"+
									"it is available at the Magellan homepage and needs to be\n"+
									"included in a resource path via the options dialog.");
			defaultTranslations.put("msg.javahelpnotfound.text",
									"JavaHelp could not be found. Along with the Magellan help\n"+
									"it is available at the Magellan homepage and needs to be\n"+
									"included in a resource path via the options dialog.");
			
		}
		return defaultTranslations;
	}
}
