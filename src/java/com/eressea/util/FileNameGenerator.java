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
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.eressea.swing.layout.GridBagHelper;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.swing.preferences.PreferencesFactory;

/**
 * DOCUMENT ME!
 *
 * @author Fiete
 * @version
 */
public class FileNameGenerator implements PreferencesFactory {
	
	String ordersSaveFileNamePattern = null;
	public static final String defaultPattern = System.getProperty("user.home")+System.getProperty("file.separator")+"{round}-{factionnr}.txt";
	
	static Properties settings;
	static FileNameGenerator gen;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param set TODO: DOCUMENT ME!
	 */
	public static void init(Properties set) {
		settings = set;
		new FileNameGenerator(settings);
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public static void quit() {
		if(gen != null) {
			gen.close();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static FileNameGenerator getInstance() {
		if(gen == null) {
			new FileNameGenerator(settings);
		}
		return gen;
	}

	private FileNameGenerator(Properties settings) {	
		this.ordersSaveFileNamePattern = settings.getProperty("FileNameGenerator.ordersSaveFileNamePattern");
		gen = this;
	}



	protected void close() {
		
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
			defaultTranslations.put("prefs.title", "FileName generator");
			defaultTranslations.put("field.ordersSaveFileNamePattern.label", "Pattern for generating auto filenames when saving orders");
			defaultTranslations.put("field.ordersSaveFileNameInfo.label", "possible replacers (case sensitive!): {faction} {factionnr} {round} {group}");	
		}

		return defaultTranslations;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter createPreferencesAdapter() {
		return new FileNameGenPrefAdapter();
	}

	protected class FileNameGenPrefAdapter extends JPanel implements PreferencesAdapter {
		
		protected JTextField patternField;

		/**
		 * Creates a new FileNameGenPrefAdapter object.
		 */
		public FileNameGenPrefAdapter() {
			initGUI();
		}

		private void initGUI() {
			/*
			*/

			// set up the panel for the maximum file history size
			// layout this container
			setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.insets.top = 10;
			c.insets.bottom = 10;
			GridBagHelper.setConstraints(c, 0, 0, GridBagConstraints.REMAINDER, 1, 1.0, 1.0,
										 GridBagConstraints.NORTHWEST,
										 GridBagConstraints.HORIZONTAL, c.insets, 0, 0);

			this.add(getFileNameGeneratorPanel(), c);

		}

		private Component getFileNameGeneratorPanel() {
			JPanel fileNameGeneratorPanel = new JPanel();
			fileNameGeneratorPanel.setLayout(new GridBagLayout());
			fileNameGeneratorPanel.setBorder(new TitledBorder(new CompoundBorder(BorderFactory.createEtchedBorder(),
															   new EmptyBorder(0, 3, 3, 3)),
											getString("prefs.title")));

			GridBagConstraints c = new GridBagConstraints(0, 0, 2, 1, 1, 0,
														  GridBagConstraints.WEST,
														  GridBagConstraints.HORIZONTAL,
														  new Insets(2, 10, 1, 10), 0, 0);

			JLabel ordersSaveFileNamePatternLabel = new JLabel(getString("field.ordersSaveFileNamePattern.label"));
			fileNameGeneratorPanel.add(ordersSaveFileNamePatternLabel,c);
			
			c.gridy++;
			patternField = new JTextField(settings.getProperty("FileNameGenerator.ordersSaveFileNamePattern"),20);
			patternField.setText(settings.getProperty("FileNameGenerator.ordersSaveFileNamePattern", defaultPattern));
			fileNameGeneratorPanel.add(patternField, c);
			
			c.gridy++;
			JLabel ordersSaveFileNamePatternInfo = new JLabel(getString("field.ordersSaveFileNameInfo.label"));
			fileNameGeneratorPanel.add(ordersSaveFileNamePatternInfo,c);

			return fileNameGeneratorPanel;
		}

        public void initPreferences() {
            // what to do?
        }

		/**
		 * Saves the editid pattern to the properties or removes the entry if pattern is 
		 * not fine
		 */
		public void applyPreferences() {
			String newPattern = patternField.getText();
			if (newPattern != null && newPattern.length()>2) {
				settings.setProperty("FileNameGenerator.ordersSaveFileNamePattern", newPattern);
			} else {
				settings.remove("FileNameGenerator.ordersSaveFileNamePattern");
			}
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
	}

	/**
	 * @return the ordersSaveFileNamePattern
	 */
	public String getOrdersSaveFileNamePattern() {
		return ordersSaveFileNamePattern;
	}

	/**
	 * @param ordersSaveFileNamePattern the ordersSaveFileNamePattern to set
	 */
	public void setOrdersSaveFileNamePattern(String ordersSaveFileNamePattern) {
		this.ordersSaveFileNamePattern = ordersSaveFileNamePattern;
	}
	
	/**
	 * 
	 * @param pattern String the user defined pattern for generating the FileName
	 * @param feed FileNameGeneratorFeed with needed information
	 * @return the new FileName or Null, if pattern is null
	 */
	public static String getFileName(String pattern, FileNameGeneratorFeed feed){
		if (pattern == null) {return null;}
		if (feed == null) {return null;}
		
		// Lets work in extra String
		String res = pattern.toString();
		
		res = replaceAll(res,"{faction}",feed.getFaction());
		res = replaceAll(res,"{factionnr}",feed.getFactionnr());
		int i = feed.getRound();
		if (i > -1){
			res = replaceAll(res,"{round}",Integer.toString(i));
			res = replaceAll(res,"{nextround}",Integer.toString(i+1));
		} else {
			res = replaceAll(res,"{round}",null);
			res = replaceAll(res,"{nextround}",null);
		}
		res = replaceAll(res,"{group}",feed.getGroup());
		
		return res;
	}
	
	/**
	 * @param originalString String to work with
	 * @param searchString String to be replaced with "replaceString"
	 * @param replaceString String to replace "searchString"
	 * @return originalString with searchString replaced by replaceString
	 * 
	 * we could use String.replaceAll for that...but, it's built in java 1.4 and
	 * just now we are compatible with 1.3. ... (Fiete 20061108)
	 * 
	 */
	public static String replaceAll(String originalString,String searchString, String replaceString){
		if (originalString == null){return null;}
		if (searchString == null){return originalString;}
		String myReplaceString = "";
		if (replaceString!=null){myReplaceString = replaceString;}
		
		String res = originalString.toString();
		int i = res.indexOf(searchString);
		
		while (i >-1){
			
			res = res.substring(0,i) + myReplaceString + res.substring(i + searchString.length());
			i = res.indexOf(searchString);
		}
		
		return res;
	}
	
}
