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

package com.eressea.demo.actions;

import java.awt.event.ActionEvent;

import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.eressea.util.FileHistory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class FileHistoryAction extends AbstractAction {
	private File	    file;
	private FileHistory history;

	/**
	 * Creates a new FileHistoryAction object.
	 *
	 * @param history TODO: DOCUMENT ME!
	 * @param cr TODO: DOCUMENT ME!
	 */
	public FileHistoryAction(FileHistory history, File cr) {
		file		 = cr;
		this.history = history;

		// format the text
		StringBuffer path = new StringBuffer();
		path.append("...").append(File.separatorChar).append(cr.getName());
		cr = cr.getParentFile();

		while((cr != null) && ((path.length() + cr.getName().length()) < 30)) {
			path.insert(4, File.separatorChar).insert(4, cr.getName());
			cr = cr.getParentFile();
		}

		putValue(Action.NAME, path.toString());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		history.loadFile(file);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public File getFile() {
		return file;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		if((o != null) && o instanceof FileHistoryAction) {
			return file.equals(((FileHistoryAction) o).getFile());
		}

		return false;
	}
}
