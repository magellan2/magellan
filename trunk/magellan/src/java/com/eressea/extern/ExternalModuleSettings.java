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

package com.eressea.extern;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.eressea.swing.InternationalizedPanel;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ExternalModuleSettings extends InternationalizedPanel implements PreferencesAdapter {
	protected Properties settings;
	protected JCheckBox chkSearchResources;
	protected JCheckBox chkSearchClassPath;

	/**
	 * Creates a new ExternalModuleSettings object.
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public ExternalModuleSettings(Properties settings) {
		this.settings = settings;
		initComponents();
	}

	private void initComponents() {
		setLayout(new java.awt.GridBagLayout());

		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(new javax.swing.border.TitledBorder(BorderFactory.createEtchedBorder(),
														  getString("border.externalmodules")));

		GridBagConstraints c = new GridBagConstraints();

		chkSearchResources = new JCheckBox(getString("chk.searchResources"),
										   Boolean.valueOf(settings.getProperty("ExternalModuleLoader.searchResourcePathClassLoader",
																			"true")).booleanValue());

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 1.0;
		pnl.add(chkSearchResources, c);

		chkSearchClassPath = new JCheckBox(getString("chk.searchClassPath"),
										   Boolean.valueOf(settings.getProperty("ExternalModuleLoader.searchClassPath",
																			"true")).booleanValue());

		c.gridx = 0;
		c.gridy = 1;
		pnl.add(chkSearchClassPath, c);

		c = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
								   GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0);
		this.add(pnl, c);
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void applyPreferences() {
		settings.setProperty("ExternalModuleLoader.searchResourcePathClassLoader",
							 String.valueOf(chkSearchResources.isSelected()));
		settings.setProperty("ExternalModuleLoader.searchClassPath",
							 String.valueOf(chkSearchClassPath.isSelected()));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Component getComponent() {
		return this;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTitle() {
		return getString("title");
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
			defaultTranslations.put("title", "External modules");
			defaultTranslations.put("chk.searchResources", "resource paths");
			defaultTranslations.put("chk.searchClassPath", "class path");
			defaultTranslations.put("border.externalmodules", "Search external modules in");
		}

		return defaultTranslations;
	}
}
