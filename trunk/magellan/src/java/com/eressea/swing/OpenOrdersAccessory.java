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

package com.eressea.swing;

import java.awt.GridBagConstraints;

import java.util.Map;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class OpenOrdersAccessory extends HistoryAccessory {
	private JCheckBox chkAutoConfirm			 = null;
	private JCheckBox chkIgnoreSemicolonComments = null;

	/**
	 * Creates a new OpenOrdersAccessory object.
	 *
	 * @param setting TODO: DOCUMENT ME!
	 * @param fileChooser TODO: DOCUMENT ME!
	 */
	public OpenOrdersAccessory(Properties setting, JFileChooser fileChooser) {
		super(setting, fileChooser);

		GridBagConstraints c = new GridBagConstraints();

		chkAutoConfirm = new JCheckBox(getString("chk.autoconfirmation.caption"));
		chkAutoConfirm.setToolTipText(getString("chk.autoconfirmation.tooltip"));

		chkIgnoreSemicolonComments = new JCheckBox(getString("chk.ignoresemicoloncomments.caption"));
		chkIgnoreSemicolonComments.setToolTipText(getString("chk.ignoresemicoloncomments.tooltip"));

		c.anchor     = GridBagConstraints.NORTHWEST;
		c.gridx		 = 0;
		c.gridy		 = 2;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.fill		 = GridBagConstraints.NONE;
		c.weightx    = 0.0;
		c.weighty    = 0.0;
		this.add(chkAutoConfirm, c);

		c.anchor     = GridBagConstraints.NORTHWEST;
		c.gridx		 = 0;
		c.gridy		 = 3;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.fill		 = GridBagConstraints.NONE;
		c.weightx    = 0.0;
		c.weighty    = 0.0;
		this.add(chkIgnoreSemicolonComments, c);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getAutoConfirm() {
		return chkAutoConfirm.isSelected();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setAutoConfirm(boolean bool) {
		chkAutoConfirm.setSelected(bool);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIgnoreSemicolonComments() {
		return chkIgnoreSemicolonComments.isSelected();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bool TODO: DOCUMENT ME!
	 */
	public void setIgnoreSemicolonComments(boolean bool) {
		chkIgnoreSemicolonComments.setSelected(bool);
	}

	private String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this, key);
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
			defaultTranslations.put("chk.autoconfirmation.caption",
									"Auto order confirmation");
			defaultTranslations.put("chk.autoconfirmation.tooltip",
									"Confirms the orders of all units that can be found in the file to open.");
			defaultTranslations.put("chk.ignoresemicoloncomments.caption",
									"Ignore ';' comments");
			defaultTranslations.put("chk.ignoresemicoloncomments.tooltip",
									"When reading the orders from the file to open all comments starting with a semicolon are ignored. Make sure to turn this off if you want to take on the confirmation status of the orders in the file to open.");
		}

		return defaultTranslations;
	}
}
