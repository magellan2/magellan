// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.resource;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import com.eressea.swing.InternationalizedPanel;
import com.eressea.swing.preferences.ExtendedPreferencesAdapter;
import com.eressea.swing.preferences.PathPreferencesAdapter;
import com.eressea.util.CollectionFactory;

public class ResourceSettings extends InternationalizedPanel implements ExtendedPreferencesAdapter {
	private JButton btnAdd = null;
	private JButton btnRemove = null;
	private JButton btnEdit = null;
	private JList lstPaths = null;
	private Properties settings = null;
	private List subAdapter;
	
	public ResourceSettings(Properties settings) {
		this.settings = settings;
		initComponents ();
	}

	private void initComponents () {
		this.setLayout(new java.awt.GridBagLayout());

		this.lstPaths = new JList(getWrappedURLs(ResourcePathClassLoader.getStaticPaths()));  // later we need to assume that this list's model is a DefaultListModel!
		this.lstPaths.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		GridBagConstraints c = new java.awt.GridBagConstraints();
		c.gridheight = 3;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new java.awt.Insets (5, 5, 5, 5);
		c.weightx = 0.1;
		c.weighty = 0.1;
		this.add(new JScrollPane(lstPaths, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), c);


		this.btnAdd = new JButton(getString("btn.new.caption"));
		this.btnAdd.addActionListener (
			new java.awt.event.ActionListener () {
				public void actionPerformed (java.awt.event.ActionEvent evt) {
					btnAddActionPerformed(evt);
				}
			}
		);

		c.gridheight = 1;
		c.gridx = 1;
		c.gridy = 0;
		c.fill = java.awt.GridBagConstraints.HORIZONTAL;
		c.insets = new java.awt.Insets (5, 0, 5, 5);
		c.weightx = 0.0;
		c.weighty = 0.0;
		this.add (btnAdd, c);


		this.btnRemove = new JButton(getString("btn.remove.caption"));
		btnRemove.addActionListener (
			new java.awt.event.ActionListener () {
				public void actionPerformed (java.awt.event.ActionEvent evt) {
					btnRemoveActionPerformed (evt);
				}
			}
		);

		c.gridx = 1;
		c.gridy = 1;
		c.insets = new java.awt.Insets (0, 0, 5, 5);
		add (btnRemove, c);


		this.btnEdit = new JButton(getString("btn.edit.caption"));
		btnEdit.addActionListener (
			new java.awt.event.ActionListener () {
				public void actionPerformed (java.awt.event.ActionEvent evt) {
					btnEditActionPerformed (evt);
				}
			}
		);

		c.gridx = 1;
		c.gridy = 2;
		c.insets = new java.awt.Insets (0, 0, 5, 5);
		c.anchor = java.awt.GridBagConstraints.NORTH;
		add (btnEdit, c);
		
		subAdapter = CollectionFactory.createArrayList(1);
		PathPreferencesAdapter ppa = new PathPreferencesAdapter(settings);
		ppa.addPath("ECheck:", "JECheckPanel.echeckEXE");
		ppa.addPath("Vorlage:", "JVorlage.vorlageFile");
		subAdapter.add(ppa);
		subAdapter.add(new com.eressea.extern.ExternalModuleSettings(settings));
	}
	
	public List getChildren() {
		return subAdapter;
	}

	private void btnEditActionPerformed (java.awt.event.ActionEvent evt) {
		if (lstPaths.getSelectedValue() == null) {
			return;
		}
			
		Component parent = this.getTopLevelAncestor();
		URLWrapper w = (URLWrapper)lstPaths.getSelectedValue();
		if (w != null && w.url != null) {
			Object[] selectionValues = { w.toString() };
			String input = (String)JOptionPane.showInputDialog(parent, getString("msg.edit.text"), getString("msg.edit.title"), JOptionPane.PLAIN_MESSAGE, null, null, selectionValues[0]);
			if (input != null) {
				if (input.startsWith("http")) {
					try {
						w.url = new URL(input);
					}
					catch (MalformedURLException mue) {
						JOptionPane.showMessageDialog(parent, getString("msg.invalidformat.text"));
					}
				} else {
					File f = new File(input);
					if (!f.exists()) {
						if (JOptionPane.showConfirmDialog(parent, getString("msg.usenonexisting.text"), getString("msg.usenonexisting.title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							try {
								w.url = new URL(input);
							}
							catch (MalformedURLException mue) {
								JOptionPane.showMessageDialog(parent, getString("msg.invalidformat.text"));
							}
						}
					} else {
						try {
							w.url = f.toURL();
						}
						catch (MalformedURLException mue) {
							JOptionPane.showMessageDialog(parent, getString("msg.invalidformat.text"));
						}
					}
				}
			}
		}
	}

	private void btnRemoveActionPerformed (java.awt.event.ActionEvent evt) {
		if (this.lstPaths.getSelectedValue() != null) {
			((DefaultListModel)this.lstPaths.getModel()).removeElementAt(this.lstPaths.getSelectedIndex());
		}
	}

	private void btnAddActionPerformed (java.awt.event.ActionEvent evt) {
		URLWrapper urlWrapper = null;
		Component parent = this.getTopLevelAncestor();
		
		javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
		fc.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
		if (fc.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
			java.io.File file = fc.getSelectedFile();
			
			try{
				if (file.exists()) {
					if (file.isDirectory()) {
						urlWrapper = new URLWrapper(file.toURL());
					}else{
						urlWrapper = new URLWrapper(new URL("jar:" + file.toURL().toString() + "!/"));
					}
				} else {
					String name = file.getName();
					String parentName = "";
					if (file.getParentFile() != null) {
						parentName = file.getParentFile().getName();
					}
					if (!name.equals("") && name.equals(parentName)) {
						// in this case the user double clicked a directory instead of just selecting it
						urlWrapper = new URLWrapper(file.getParentFile().toURL());
					} else {
						JOptionPane.showMessageDialog(parent, getString("msg.nonexistingfile.text"));
					}
				}
			} catch(MalformedURLException ex) {
				JOptionPane.showMessageDialog(parent, getString("msg.urlexception.text") + " " + ex.toString());
			}
		}
		
		((DefaultListModel)this.lstPaths.getModel()).insertElementAt(urlWrapper, 0);
	}

	public void applyPreferences() {
		Collection resourcePaths = CollectionFactory.createLinkedList();
		ListModel listModel = this.lstPaths.getModel();
		
		for (int j = 0; j < listModel.getSize(); j++) {
			URLWrapper wrapper = (URLWrapper)listModel.getElementAt(j);
			if (wrapper != null && wrapper.url != null) {
				resourcePaths.add(wrapper.url);
			}
		}
		
		com.eressea.resource.ResourcePathClassLoader.setStaticPaths(resourcePaths);
		com.eressea.resource.ResourcePathClassLoader.storePaths(resourcePaths, this.settings);
	}
	
	public Component getComponent() {
		return this;
	}
	
	public String getTitle() {
		return getString("title");
	}

	private DefaultListModel getWrappedURLs(Collection urls) {
		DefaultListModel wrappers = new DefaultListModel();
		for (Iterator iter = urls.iterator(); iter.hasNext();) {
			wrappers.add(wrappers.getSize(), new URLWrapper((URL)iter.next()));
		}
		return wrappers;
	}

	private class URLWrapper {
		public URL url;
		
		public URLWrapper(URL url) {
			this.url = url;
		}
		
		public String toString() {
			if (url.getProtocol().equals("file")) {
				File f = new File(url.getPath());
				if (f.exists()) {
					return f.toString();
				} else {
					return url.toString();
				}
			} else {
				return url.toString();
			}
		}
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
			defaultTranslations.put("btn.new.caption" , "Add...");
			defaultTranslations.put("btn.remove.caption" , "Remove");
			defaultTranslations.put("btn.edit.caption" , "Edit");
			defaultTranslations.put("chk.usefallback.caption" , "Use the internet as fall-back option");
			
			defaultTranslations.put("msg.edit.text" , "Edit resource path:");
			defaultTranslations.put("msg.edit.title" , "Edit");
			defaultTranslations.put("msg.invalidformat.text" , "The specified URL is not in a valid format. No resource path could be created from it.");
			defaultTranslations.put("msg.usenonexisting.text" , "The specified file or directory could not be found. Do you want to use it as a resource path anyway?");
			defaultTranslations.put("msg.usenonexisting.title" , "Confirm resource path");
			defaultTranslations.put("msg.nonexistingfile.text" , "File or directory does not exist!");
			defaultTranslations.put("msg.urlexception.text" , "Unable to convert to a valid URL:");
			
			defaultTranslations.put("title" , "Resources");
			
			
			/*
			  defaultTranslations.put("msg.confirmnetconnect.text" , 
			  "Unable to find resource \"{0}\". Do you want Magellan to look for the resource on the internet?");
			  defaultTranslations.put("msg.confirmnetconnect.title" , "Internet connection");
			*/
		}
		return defaultTranslations;
	}


}
