/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
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

/*
 * LanguageDialog.java
 *
 * Created on 28. März 2002, 10:38
 */
package com.eressea.util;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class LanguageDialog {
	private static final Logger log = Logger.getInstance(LanguageDialog.class);

	// the settings needed for the resource loader
	protected Properties settings;

	// a list containing all installed languages as Lang objects
	protected List languageList;

	// a Lang object defining the system default language
	protected Lang sysDefault;

	// the base name for installed language identification files

	/** TODO: DOCUMENT ME! */
	public static String LANG_FILE = "installed-language";

	/**
	 * Creates new LanguageDialog
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param magDir TODO: DOCUMENT ME!
	 */
	public LanguageDialog(Properties settings, File magDir) {
		this.settings = settings;

		Translations.setClassLoader(new ResourcePathClassLoader(settings));

		findLanguages(magDir);
	}

	protected void findLanguages(File baseDir) {
		Locale sysDefault = Locale.getDefault();

		// collect all jars in the base directory
		Collection col = CollectionFactory.createLinkedList();
		String jarSuffix = ".jar";
		String magSuffix = "magellan.jar";
		log.info("Looking for language jar files.");

		try {
			File files[] = baseDir.listFiles();

			if((files != null) && (files.length > 0)) {
				for(int i = 0; i < files.length; i++) {
					if(files[i].toString().toLowerCase().endsWith(jarSuffix) &&
						   !files[i].toString().toLowerCase().endsWith(magSuffix)) {
						try {
							URL url = new URL("jar:" + files[i].toURL().toString() + "!/");

							if(ResourcePathClassLoader.getStaticPaths().contains(url)) {
								continue;
							}

							Collection col2 = CollectionFactory.createArrayList(1);
							col2.add(url);
							col.add(col2);
							log.info("Also determining " + files[i]);
						} catch(Exception inner) {
						}
					}
				}
			}
		} catch(Exception exc) {
		}

		if(col.size() == 0) {
			col = null;
		}

		ResourcePathClassLoader loader = new ResourcePathClassLoader(settings);

		String langs[] = Locale.getISOLanguages();
		int j = 0;
		languageList = CollectionFactory.createLinkedList();

		if((langs != null) && (langs.length > 0)) {
			for(int i = 0; i < langs.length; i++) {
				// first check default resources
				String name = "res/lang/" + LANG_FILE + "_" + langs[i];

				Locale locale = new Locale(langs[i], "");

				URL url = ResourcePathClassLoader.getResourceStatically(name);

				if(url != null) {
					Lang lang = new Lang(locale);
					languageList.add(lang);

					if(sysDefault.equals(locale)) {
						this.sysDefault = lang;
					}

					j++;
				} else if(col != null) { // search in collected path

					Iterator it = col.iterator();

					while(it.hasNext()) {
						Collection col2 = (Collection) it.next();
						URL url2 = ResourcePathClassLoader.getResourceStatically(name, col2);

						if(url2 != null) { // found new language jar, mount it

							Lang lang = new Lang(locale);
							languageList.add(lang);

							Collection col3 = CollectionFactory.createLinkedList(loader.getPaths());
							col3.add(col2.iterator().next());
							loader.setPaths(col3);
							ResourcePathClassLoader.storePaths(col3, settings);
							ResourcePathClassLoader.init(settings); // re-initialize static path
							col.remove(col2); // remove from external list

							if(sysDefault.equals(locale)) {
								this.sysDefault = lang;
							}

							j++;

							break;
						}
					}
				}
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param parent TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Locale showDialog(Component parent) {
		if(languagesFound()) {
			Object ret = JOptionPane.showInputDialog(parent,
													 Translations.getTranslation(this, "choose"),
													 Translations.getTranslation(this, "title"),
													 JOptionPane.QUESTION_MESSAGE, null,
													 languageList.toArray(), sysDefault);

			if(ret != null) {
				return ((Lang) ret).locale;
			}
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean languagesFound() {
		return languageList.size() > 0;
	}

	protected class Lang {
		protected Locale locale;

		/**
		 * Creates a new Lang object.
		 *
		 * @param lang TODO: DOCUMENT ME!
		 */
		public Lang(String lang) {
			locale = new Locale(lang, "");
		}

		/**
		 * Creates a new Lang object.
		 *
		 * @param l TODO: DOCUMENT ME!
		 */
		public Lang(Locale l) {
			locale = l;
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public String toString() {
			return locale.getDisplayLanguage();
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
			defaultTranslations.put("choose",
									"The following languages were found. Please choose one.");
			defaultTranslations.put("title", "Choose a language");
		}

		return defaultTranslations;
	}
}
