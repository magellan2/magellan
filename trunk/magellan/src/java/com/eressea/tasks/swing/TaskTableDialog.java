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

package com.eressea.tasks.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import java.util.Map;
import java.util.Properties;

import com.eressea.GameData;

import com.eressea.event.EventDispatcher;

import com.eressea.swing.InternationalizedDataDialog;

import com.eressea.util.CollectionFactory;

/**
 * A dialog wrapper for the TaskTable panel.
 */
public class TaskTableDialog extends InternationalizedDataDialog {
	private TaskTablePanel panel = null;

	/**
	 * Create a new TaskTableDialog object as a dialog with a parent window.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 * @param ed TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public TaskTableDialog(Frame owner, boolean modal, EventDispatcher ed,
						   GameData initData, Properties p) {
		super(owner, modal, ed, initData, p);
		init();

		//pack();
	}

	private void init() {
		setContentPane(getMainPane());
		setTitle(getString("window.title"));

		int width = Integer.parseInt(settings.getProperty("TaskTableDialog.width",
														  "500"));
		int height = Integer.parseInt(settings.getProperty("TaskTableDialog.height",
														   "300"));
		this.setSize(width, height);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int		  x = Integer.parseInt(settings.getProperty("TaskTableDialog.x",
															((screen.width -
															getWidth()) / 2) +
															""));
		int y = Integer.parseInt(settings.getProperty("TaskTableDialog.y",
													  ((screen.height -
													  getHeight()) / 2) + ""));
		this.setLocation(x, y);
	}

	private Container getMainPane() {
		if(panel == null) {
			panel = new TaskTablePanel(dispatcher, data, settings);
		}

		return panel;

		/*
		JPanel mainPanel = new JPanel();
		mainPanel.add(panel);
		return mainPanel;
		*/
	}

	private void storeSettings() {
		settings.setProperty("TaskTableDialog.x", getX() + "");
		settings.setProperty("TaskTableDialog.y", getY() + "");
		settings.setProperty("TaskTableDialog.width", getWidth() + "");
		settings.setProperty("TaskTableDialog.height", getHeight() + "");
	}

	protected void quit() {
		storeSettings();
		panel.quit();
		super.quit();
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
			defaultTranslations.put("window.title", "Open Tasks");
		}

		return defaultTranslations;
	}
}
