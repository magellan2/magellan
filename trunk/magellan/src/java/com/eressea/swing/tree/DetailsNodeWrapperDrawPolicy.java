// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// ===


package com.eressea.swing.tree;

import java.util.Map;
import java.util.Properties;

import java.awt.*;
import javax.swing.*;

import com.eressea.swing.preferences.DetailedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesAdapter;

/**
 *
 * @author  Andreas
 * @version
 */
public class DetailsNodeWrapperDrawPolicy extends AbstractNodeWrapperDrawPolicy {
	
	// data for pref adapter
	protected int count, rows, subcount[];
	protected Properties settings;
	protected String prefix, sK[][], lK[];
	protected Class languageClass;
	protected Map defaultTranslations;
	
	boolean properties[];
	
	/** Creates new NodeWrapperPreferencesDialog */
	public DetailsNodeWrapperDrawPolicy(int count, int[] subcount, Properties p, String prefix, String[][] sK, String[] lK, int rows, Class languageClass, Map defaultTrans) {
		this.count = count;
		this.subcount = subcount;
		this.settings = p;
		this.prefix = prefix;
		this.sK = sK;
		this.lK = lK;
		this.rows = rows;
		this.languageClass = languageClass;
		this.defaultTranslations = defaultTrans;
	
		loadSettings();
	}
	
	protected void loadSettings() {
		int sum = 0;
		if (subcount != null) {
			for(int i=0; i<count; i++) {			
				sum += Math.max(0, subcount[i]);
				sum++;
			}
		} else {
			sum = count;
		}
		properties = new boolean[sum];
		for(int i=0; i<properties.length; i++) {
			properties[i] = settings.getProperty(prefix+"."+sK[i][0], sK[i][1]).equals("true");
		}
	}
	
	// pavkovic 2003.06.23: back to version 1.3
	// public void applyPreferences() {
	public void applyPreferences(DetailedPreferencesAdapter adapter) {
		System.arraycopy(adapter.properties,0,properties,0,properties.length);
		applyPreferences();
	}

	public PreferencesAdapter createPreferencesAdapter() {
		return new DetailPolicyPreferencesAdapter(count, subcount,settings,prefix,sK,lK,rows,this);
	}
	
	public String getString(String key) {
		return com.eressea.util.Translations.getTranslation(languageClass,key);		
	}
	
	protected JPanel getExternalDetailContainer(int index) {
		return null;
	}
	
	public Map getDefaultTranslations() {
		return defaultTranslations;
	}

	class DetailPolicyPreferencesAdapter extends DetailedPreferencesAdapter {		
		
		DetailsNodeWrapperDrawPolicy parent;
		
		public DetailPolicyPreferencesAdapter(int count, int subcount[], Properties p, String prefix,String sK[][], String lK[], int rows,DetailsNodeWrapperDrawPolicy parent) {
			super(count,subcount,p,prefix,sK,lK,rows,true);
			this.parent = parent;
			init();
		}
		
		protected void applyChanges(int[] indices) {
			parent.applyPreferences(this);
		}
		
		protected String getString(String key) {
			return parent.getString(key);
		}
		
	}
	
	public String getTitle() {
		return getString("pref.title");
	}
}
