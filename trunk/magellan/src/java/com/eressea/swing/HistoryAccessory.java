// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.eressea.util.Bucket;

public class HistoryAccessory extends JPanel {
	protected Properties settings = null;
	protected JFileChooser chooser = null;
	private Bucket history = new Bucket(6);
	
	public HistoryAccessory(Properties setting, JFileChooser fileChooser) {
		this.settings = setting;
		this.chooser = fileChooser;
		
		// load history fifo buffer
		String hist = settings.getProperty("DirectoryHistory", "");
		StringTokenizer st = new StringTokenizer(hist, "|");
		while (st.hasMoreTokens()) {
			File dir = new File(st.nextToken());
			if (dir.exists() && dir.isDirectory()) {
				history.add(new DirWrapper(dir));
			}
		}
		
		// intercept file chooser selection approvals
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
					approveSelection();
				}
			}
		});
		
		// set-up GUI
		if (history.size() > 1) {
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			c.weighty = 0.0;
			this.add(new JLabel("Directory History:"), c);
			
			JComboBox cmbHistory = new JComboBox(history.toArray());
			cmbHistory.setEditable(false);
			cmbHistory.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					chooser.setCurrentDirectory(((DirWrapper)((JComboBox)e.getSource()).getSelectedItem()).getDirectory());
					//chooser.setSelectedFile(new File(""));
				}
			});

			c.anchor = GridBagConstraints.NORTH;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;
			c.weighty = 0.0;
			this.add(cmbHistory, c);
		}
	}
	
	protected void approveSelection() {
		history.add(new DirWrapper(chooser.getSelectedFile().getParentFile()));
		
		StringBuffer sb = new StringBuffer();
		for (Iterator iter = history.iterator(); iter.hasNext();) {
			sb.insert(0, '|').insert(0, ((DirWrapper)iter.next()).getDirectory().getAbsolutePath());
		}
		settings.setProperty("DirectoryHistory", sb.toString());
	}
}

class DirWrapper {
	private File dir = null;
	
	public DirWrapper(File f) {
		this.dir = f;
	}
	
	public File getDirectory() {
		return dir;
	}
	
	public String toString() {
		String dirName = dir.getAbsolutePath();
		String str = new String(dirName);
		if (dirName.length() > 30) {
			str = dirName.substring(0, 10) + "..." + dirName.substring(dirName.length() - 18);
		}
		return str;
	}
	
	// Bucket needs this
	public boolean equals(Object o) {
		if (o != null && o instanceof DirWrapper) {
			return this.getDirectory().equals(((DirWrapper)o).getDirectory());
		} else {
			return false;
		}
	}
}
