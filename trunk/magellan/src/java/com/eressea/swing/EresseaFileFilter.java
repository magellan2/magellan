// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;


import java.io.File;

import java.util.Map;

import javax.swing.filechooser.FileFilter;
import com.eressea.util.CollectionFactory;

public class EresseaFileFilter extends javax.swing.filechooser.FileFilter {
	public static final int CR_FILTER    = 0;
	public static final int TXT_FILTER   = 1;
	public static final int ZIP_FILTER   = 2;
	public static final int GZ_FILTER    = 3;
	public static final int BZ2_FILTER   = 4;
	public static final int MAX_DEFAULTS = 5;

	protected int flag = 0;
	protected String optExtension = null;
	protected String optDescription = "";

	private String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this,key);
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
			defaultTranslations.put("defaults.description.0" , "Eressea computer report files");
			defaultTranslations.put("defaults.description.1" , "Eressea order files");
			defaultTranslations.put("defaults.description.2" , "ZIP archive files");
			defaultTranslations.put("defaults.description.3" , "GZIP archive files");
			defaultTranslations.put("defaults.description.4" , "BZIP2 archive files");
			
			defaultTranslations.put("defaults.extension.0" , "cr");
			defaultTranslations.put("defaults.extension.1" , "txt");
			defaultTranslations.put("defaults.extension.2" , "zip");
			defaultTranslations.put("defaults.extension.3" , "gz");
			defaultTranslations.put("defaults.extension.4" , "bz2");
		}
		return defaultTranslations;
	}
	
	public EresseaFileFilter(int flag) {
		if (flag >= 0 && flag < MAX_DEFAULTS) {
			this.flag = flag;
		}
	}
	
	public EresseaFileFilter(String ext, String desc) {
		optExtension = ext;
		optDescription = desc;
	}

	public File addExtension(File aFile) {
		return accept(aFile) ? aFile : new File(aFile.getPath()+getExtension());
	}
	
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(getExtension());
	}
	
	private String getExtension() {
		return "."  + (optExtension != null ? optExtension : getString("defaults.extension." + flag)).toLowerCase();
	}

	public String getDescription() {
		String retVal = "";
		if (optExtension != null) {
			retVal = optDescription + " (*." + optExtension + ")";
		} else {
			retVal = getString("defaults.description." + flag) + " (*." + getString("defaults.extension." + flag) + ")";
		}
		return retVal;
	}
}
