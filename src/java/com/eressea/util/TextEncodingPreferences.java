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

package com.eressea.util;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.eressea.swing.layout.GridBagHelper;
import com.eressea.swing.preferences.PreferencesAdapter;

/**
 * DOCUMENT ME!
 *
 * @author Fiete
 * @version
 */
public class TextEncodingPreferences extends JPanel implements PreferencesAdapter {

	static Properties settings;
	protected JCheckBox saveOrders;
	protected JCheckBox openOrders;
	protected JCheckBox runEcheck;
	protected JCheckBox runJVorlage;

	public TextEncodingPreferences(Properties _settings){
		settings = _settings;
		this.initGUI();
	}
	
	
	/* (non-Javadoc)
	 * @see com.eressea.swing.preferences.PreferencesAdapter#initPreferences()
	 */
	public void initPreferences() {
		// nothing to do...
		
	}
	
	private void initGUI() {
		/*
		*/

		// layout this container
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.insets.top = 10;
		c.insets.bottom = 10;
		GridBagHelper.setConstraints(c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
									 GridBagConstraints.NORTHWEST,
									 GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

		this.add(TextEncodingPrefrencesPanel(), c);

	}

	private Component TextEncodingPrefrencesPanel() {
		JPanel textEncodingPrefrencesPanel = new JPanel();
		textEncodingPrefrencesPanel.setLayout(new GridBagLayout());
		textEncodingPrefrencesPanel.setBorder(new TitledBorder(new CompoundBorder(BorderFactory.createEtchedBorder(),
														   new EmptyBorder(0, 3, 3, 3)),
										getString("prefs.title")));

		GridBagConstraints c = new GridBagConstraints(0, 0, 2, 1, 1, 0,
													  GridBagConstraints.WEST,
													  GridBagConstraints.HORIZONTAL,
													  new Insets(2, 10, 1, 10), 0, 0);

		JLabel textEncodingInfoLabel = new JLabel(getString("prefs.info1"));
		textEncodingPrefrencesPanel.add(textEncodingInfoLabel,c);
		
		c.gridy++;
		textEncodingInfoLabel = new JLabel(getString("prefs.info2"));
		textEncodingPrefrencesPanel.add(textEncodingInfoLabel,c);
		
		c.gridy++;
		saveOrders = new JCheckBox(getString("checkbox.textEncodingISOsaveOrders.label"),PropertiesHelper.getboolean(settings, "TextEncoding.ISOsaveOrders", false));
		textEncodingPrefrencesPanel.add(saveOrders, c);
		
		c.gridy++;
		openOrders = new JCheckBox(getString("checkbox.textEncodingISOopenOrders.label"),PropertiesHelper.getboolean(settings, "TextEncoding.ISOopenOrders", false));
		textEncodingPrefrencesPanel.add(openOrders, c);
		
		c.gridy++;
		runEcheck = new JCheckBox(getString("checkbox.textEncodingISOECheck.label"),PropertiesHelper.getboolean(settings, "TextEncoding.ISOrunEcheck", false));
		textEncodingPrefrencesPanel.add(runEcheck, c);
		
		c.gridy++;
		runJVorlage = new JCheckBox(getString("checkbox.textEncodingISOJVorlage.label"),PropertiesHelper.getboolean(settings, "TextEncoding.ISOrunJVorlage", false));
		runJVorlage.setEnabled(false);
		textEncodingPrefrencesPanel.add(runJVorlage, c);
		
		return textEncodingPrefrencesPanel;
	}

   	/**
	 * save settings 
	 * 
	 */
	public void applyPreferences() {
		settings.setProperty("TextEncoding.ISOsaveOrders", (saveOrders.isSelected() ? "true" : "false"));
		settings.setProperty("TextEncoding.ISOopenOrders", (openOrders.isSelected() ? "true" : "false"));
		settings.setProperty("TextEncoding.ISOrunEcheck", (runEcheck.isSelected() ? "true" : "false"));
		settings.setProperty("TextEncoding.ISOrunJVorlage", (runJVorlage.isSelected() ? "true" : "false"));
	}

	/**
	 * Returns the component for showing in preferences dialog
	 *
	 * @return The Component
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
		return getString("prefs.title");
	}

	protected String getString(String key) {
		return Translations.getTranslation(this, key);
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
			defaultTranslations.put("prefs.title", "Text Encoding");
			defaultTranslations.put("prefs.info1", "Select the function(s) for enforcing ISO-encoding.");
			defaultTranslations.put("prefs.info2", "(If not selected, the behaviour of encoding is unchanged and depends on system settings)");
			defaultTranslations.put("checkbox.textEncodingISOsaveOrders.label", "save orders");
			defaultTranslations.put("checkbox.textEncodingISOopenOrders.label", "open orders");
			defaultTranslations.put("checkbox.textEncodingISOECheck.label", "run Echeck");
			defaultTranslations.put("checkbox.textEncodingISOJVorlage.label", "run JVorlage");
		}

		return defaultTranslations;
	}

	
}
