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
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.eressea.util.Bucket;
import com.eressea.util.CollectionFactory;
import com.eressea.util.PropertiesHelper;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HistoryAccessory extends JPanel {
	protected Properties   settings = null;
	protected JFileChooser chooser = null;
	private Bucket		   history = new Bucket(6);

	/**
	 * Creates a new HistoryAccessory object.
	 *
	 * @param setting TODO: DOCUMENT ME!
	 * @param fileChooser TODO: DOCUMENT ME!
	 */
	public HistoryAccessory(Properties setting, JFileChooser fileChooser) {
		this.settings = setting;
		this.chooser  = fileChooser;

		// load history fifo buffer
		for(Iterator iter = PropertiesHelper.getList(settings,
													 "HistoryAccessory.directoryHistory")
											.iterator(); iter.hasNext();) {
			String dirName = (String) iter.next();
			File   dir = new File(dirName);

			if(dir.exists() && dir.isDirectory()) {
				history.add(new DirWrapper(dir));
			}
		}

		// intercept file chooser selection approvals
		chooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand() == JFileChooser.APPROVE_SELECTION) {
						approveSelection();
					}
				}
			});

		// set-up GUI
		if(history.size() > 1) {
			this.setLayout(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();

			c.anchor     = GridBagConstraints.NORTHWEST;
			c.gridx		 = 0;
			c.gridy		 = 0;
			c.gridwidth  = 1;
			c.gridheight = 1;
			c.fill		 = GridBagConstraints.NONE;
			c.weightx    = 0.0;
			c.weighty    = 0.0;
			this.add(new JLabel("Directory History:"), c);

			JComboBox cmbHistory = new JComboBox(history.toArray());
			cmbHistory.setEditable(false);
			cmbHistory.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						chooser.setCurrentDirectory(((DirWrapper) ((JComboBox) e.getSource()).getSelectedItem()).getDirectory());

						//chooser.setSelectedFile(new File(""));
					}
				});

			c.anchor     = GridBagConstraints.NORTH;
			c.gridx		 = 0;
			c.gridy		 = 1;
			c.gridwidth  = 1;
			c.gridheight = 1;
			c.fill		 = GridBagConstraints.NONE;
			c.weightx    = 0.0;
			c.weighty    = 0.0;
			this.add(cmbHistory, c);
		}
	}

	protected void approveSelection() {
		history.add(new DirWrapper(chooser.getSelectedFile().getParentFile()));

		List dirs = CollectionFactory.createArrayList(7);

		for(Iterator iter = history.iterator(); iter.hasNext();) {
			dirs.add(((DirWrapper) iter.next()).getDirectory().getAbsolutePath());
		}

		Collections.reverse(dirs);
		PropertiesHelper.setList(settings, "HistoryAccessory.directoryHistory",
								 dirs);
	}
}


class DirWrapper {
	private File dir = null;

	/**
	 * Creates a new DirWrapper object.
	 *
	 * @param f TODO: DOCUMENT ME!
	 */
	public DirWrapper(File f) {
		this.dir = f;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public File getDirectory() {
		return dir;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		String dirName = dir.getAbsolutePath();
		String str = new String(dirName);

		if(dirName.length() > 30) {
			str = dirName.substring(0, 10) + "..." +
				  dirName.substring(dirName.length() - 18);
		}

		return str;
	}

	// Bucket needs this
	public boolean equals(Object o) {
		if((o != null) && o instanceof DirWrapper) {
			return this.getDirectory().equals(((DirWrapper) o).getDirectory());
		} else {
			return false;
		}
	}
}
