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

package com.eressea.swing;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EresseaFileFilter extends javax.swing.filechooser.FileFilter {
	/** Selects .cr-files */
	public static final int CR_FILTER = 0;

	/** Selects .txt-files */
	public static final int TXT_FILTER = 1;

	/** Selects .zip-files */
	public static final int ZIP_FILTER = 2;

	/** Selects .gz-files */
	public static final int GZ_FILTER = 3;

	/** Selects .bz2-files */
	public static final int BZ2_FILTER = 4;

	/** Selects .cr, .zip, .gz, and .bz2-files */
	public static final int ALLCR_FILTER = 5;

	
	private List extensions;
	protected String description = "";
	
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
			defaultTranslations.put("defaults.description.0", "Eressea computer report files");
			defaultTranslations.put("defaults.description.1", "Eressea order files");
			defaultTranslations.put("defaults.description.2", "ZIP archive files");
			defaultTranslations.put("defaults.description.3", "GZIP archive files");
			defaultTranslations.put("defaults.description.4", "BZIP2 archive files");
			defaultTranslations.put("defaults.description.5", "All Eressea report files");

			defaultTranslations.put("defaults.extension.0", "cr");
			defaultTranslations.put("defaults.extension.1", "txt");
			defaultTranslations.put("defaults.extension.2", "zip");
			defaultTranslations.put("defaults.extension.3", "gz");
			defaultTranslations.put("defaults.extension.4", "bz2");
		}

		return defaultTranslations;
	}

	/**
	 * Creates a new EresseaFileFilter object.
	 *
	 * @param flag One of the <code>FILTER</code>-flags
	 */
	public EresseaFileFilter(int flag) {
		extensions = new LinkedList();
		if (flag>=CR_FILTER && flag<=BZ2_FILTER){
			extensions.add(getExtension(flag));
		}else if (flag==ALLCR_FILTER){
			extensions.add(getExtension(CR_FILTER));
			extensions.add(getExtension(ZIP_FILTER));
			extensions.add(getExtension(GZ_FILTER));
			extensions.add(getExtension(BZ2_FILTER));
		}
		description=getDescription(flag);
	}

	/**
	 * Creates a new EresseaFileFilter object.
	 *
	 * @param ext Only files with this extension will be accepted by this filter
	 * @param desc A description to identify for this filter
	 */
	public EresseaFileFilter(String ext, String desc) {
		extensions=new LinkedList();
		extensions.add(ext);
		this.description = desc;
	}

	/**
	 * Creates a new EresseaFileFilter object.
	 *
	 * @param ext A List of Strings. Only files with these extensions will be accepted by this filter
	 * @param desc A description to identify for this filter
	 */
	public EresseaFileFilter(List ext, String desc) {
		extensions=new LinkedList(ext);
		this.description = desc;
	}


	/**
	 * Append an appropriate extension to a file.
	 *
	 * @param aFile 
	 *
	 * @return A File with the filename extended by the current extension
	 */
	public File addExtension(File aFile) {
		return accept(aFile) ? aFile : new File(aFile.getPath() + getExtension());
	}

	/**
	 * Returns <code>true</code> iff this file is accepted by this filter.
	 *
	 * @param f Any File
	 *
	 * @return <code>true</code> iff this file is accepted
	 */
	public boolean accept(File f) {
		if (f.isDirectory()) return true;
		for (Iterator it = extensions.iterator(); it.hasNext(); )
			if (f.getName().toLowerCase().endsWith((String) it.next()))
				return true;
		return false;
	}

	private String getExtension(int flag) {
		return "." + getString("defaults.extension." + flag).toLowerCase();
	}

	private String getExtension() {
		if (extensions.isEmpty())
			return null;
		return (String) extensions.get(0);
	}

	/**
	 * Returns the current description.
	 *
	 * @return The description of this filter
	 */
	public String getDescription() {
		return description;
	}
	
	
	/**
	 * Returns the description for the flag.
	 *
	 * @param flag
	 * @return The appropriate description
	 */
	protected String getDescription(int flag) {
		String retVal = "";
		retVal = getString("defaults.description." + flag);
		if (retVal==null)
			retVal = "unknown";
		if (!extensions.isEmpty())
			retVal += " (";
		for (Iterator it = extensions.iterator(); it.hasNext(); ){
		    retVal += "*" + (String) it.next();
		    if (it.hasNext())
		    	retVal +=", ";
		    else
		    	retVal +=")";
		}
		return retVal;
	}

}
