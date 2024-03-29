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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import javax.swing.JOptionPane;

import com.eressea.demo.Client;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HelpAction extends MenuAction {
	private static final Logger log = Logger.getInstance(HelpAction.class);
	private Object helpBroker = null;

	/**
	 * Creates a new HelpAction object.
	 *
	 * @param client TODO: DOCUMENT ME!
	 */
	public HelpAction(Client client) {
        super(client);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// SG: had a lot of fun when I implemented this :-)
		try {
			ClassLoader loader = new ResourcePathClassLoader(client.getProperties());

			URL hsURL = loader.getResource("magellan.hs");

			if(hsURL == null) {
				JOptionPane.showMessageDialog(client,
											  Translations.getTranslation(this,
																		  "msg.helpsetnotfound.text"));

				return;
			}

			Class helpSetClass = null;
			Class helpBrokerClass = null;

			if(this.helpBroker == null) {
				try {
					helpSetClass = loader.loadClass("javax.help.HelpSet");
					loader.loadClass("javax.help.CSH$DisplayHelpFromSource");
					helpBrokerClass = loader.loadClass("javax.help.HelpBroker");
				} catch(ClassNotFoundException ex) {
					JOptionPane.showMessageDialog(client,
												  Translations.getTranslation(this,
																			  "msg.javahelpnotfound.text"));

					return;
				}

				Class helpSetConstructorSignature[] = {
														  Class.forName("java.lang.ClassLoader"),
														  hsURL.getClass()
													  };
				Constructor helpSetConstructor = helpSetClass.getConstructor(helpSetConstructorSignature);
				Object helpSetConstructorArgs[] = { loader, hsURL };

				// this calls new javax.help.Helpset(ClassLoader, URL)
				Object helpSet = helpSetConstructor.newInstance(helpSetConstructorArgs);

				Method helpSetCreateHelpBrokerMethod = helpSetClass.getMethod("createHelpBroker",
																			  null);

				// this calls new javax.help.Helpset.createHelpBroker()
				this.helpBroker = helpSetCreateHelpBrokerMethod.invoke(helpSet, null);

				Method initPresentationMethod = helpBrokerClass.getMethod("initPresentation", null);

				// this calls new javax.help.HelpBroker.initPresentation()
				initPresentationMethod.invoke(this.helpBroker, null);
			}

			Class setDisplayedMethodSignature[] = { boolean.class };
			Method setDisplayedMethod = this.helpBroker.getClass().getMethod("setDisplayed",
																			 setDisplayedMethodSignature);
			Object setDisplayedMethodArgs[] = { Boolean.TRUE };

			// this calls new javax.help.HelpBroker.setDisplayed(true)
			setDisplayedMethod.invoke(this.helpBroker, setDisplayedMethodArgs);
		} catch(Exception ex) {
			log.error(ex);
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
			defaultTranslations.put("name", "Help...");
			defaultTranslations.put("mnemonic", "h");
			defaultTranslations.put("accelerator", "F1");
			defaultTranslations.put("tooltip", "");
			defaultTranslations.put("msg.helpsetnotfound.text",
									"The Magellan help could not be found. Along with JavaHelp\n" +
									"it is available at the Magellan homepage and needs to be\n" +
									"included in a resource path via the options dialog.");
			defaultTranslations.put("msg.javahelpnotfound.text",
									"JavaHelp could not be found. Along with the Magellan help\n" +
									"it is available at the Magellan homepage and needs to be\n" +
									"included in a resource path via the options dialog.");
		}

		return defaultTranslations;
	}
}
