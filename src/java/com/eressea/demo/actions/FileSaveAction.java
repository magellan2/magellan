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

import java.util.Map;

import com.eressea.demo.Client;
import com.eressea.io.file.FileType;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class FileSaveAction extends FileSaveAsAction {
	// pavkovic 2003.05.20: this object is essentially doing the same as FileSaveAsAction
	public FileSaveAction(Client parent) {
		super(parent);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getIconName() {
		return "save_edit";
	}

	/**
	 * This function delivers the target file. In FileSaveAction use  the possibly well known
	 * FileType of the gamedata object
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected FileType getFile() {
		return (client.getData() == null) ? null : (client.getData().filetype);
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
			defaultTranslations.put("name", "Save");
			defaultTranslations.put("mnemonic", "s");
			defaultTranslations.put("accelerator", "ctrl S");
			defaultTranslations.put("tooltip", "");
		}

		return defaultTranslations;
	}
}
