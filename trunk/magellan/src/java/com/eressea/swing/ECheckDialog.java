// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.eressea.GameData;
import com.eressea.event.EventDispatcher;
import com.eressea.util.CollectionFactory;

/**
 * A dialog wrapper for the ECheck panel.
 */
public class ECheckDialog extends InternationalizedDataDialog {
	private ECheckPanel pnlECheck = null;

	/**
	 * Create a new ECheckDialog object as a dialog with a parent
	 * window.
	 */
	public ECheckDialog(Frame owner, boolean modal, EventDispatcher ed, GameData initData, Properties p) {
		this(owner, modal, ed, initData, p, null);
	}

	/**
	 * Create a new ECheckDialog object as a dialog with a parent
	 * window.
	 */
	public ECheckDialog(Frame owner, boolean modal, EventDispatcher ed, GameData initData, Properties p, Collection regions) {
		super(owner, modal, ed, initData, p);
		init(regions);
	}

	private void init(Collection regions) {
		if(regions == null) {
			pnlECheck = new ECheckPanel(dispatcher, data, settings);
		} else {
			pnlECheck = new ECheckPanel(dispatcher, data, settings,regions);
		}
		
		setContentPane(getMainPane());
		setTitle(getString("window.title"));
		int width = Integer.parseInt(settings.getProperty("ECheckDialog.width", "500"));
		int height = Integer.parseInt(settings.getProperty("ECheckDialog.height", "300"));
		this.setSize(width, height);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = Integer.parseInt(settings.getProperty("ECheckDialog.x", ((screen.width - getWidth()) / 2) + ""));
		int y = Integer.parseInt(settings.getProperty("ECheckDialog.y", ((screen.height - getHeight()) / 2) + ""));
		this.setLocation(x, y);
		pnlECheck.setSelRegionsOnly(new Boolean(settings.getProperty("ECheckDialog.includeSelRegionsOnly", "false")).booleanValue());
		pnlECheck.setConfirmedOnly(new Boolean(settings.getProperty("ECheckDialog.confirmedOnly", "false")).booleanValue());
	}
	
	private Container getMainPane() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(getButtonPanel(), BorderLayout.NORTH);
		
		JPanel mainPanel = new JPanel(new BorderLayout(6, 0));
		mainPanel.setBorder(new EmptyBorder(4, 4, 4, 4));
		mainPanel.add(pnlECheck, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.EAST);
		return mainPanel;
	}
	
	private Container getButtonPanel() {
		
		JButton btnRun = new JButton(getString("btn.run.caption"));
		btnRun.setMnemonic(getString("btn.run.mnemonic").charAt(0));
		btnRun.setDefaultCapable(true);
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnlECheck.runECheck();
			}
		});
		this.getRootPane().setDefaultButton(btnRun);
		
		JButton btnClose = new JButton(getString("btn.close.caption"));
		btnClose.setMnemonic(getString("btn.close.mnemonic").charAt(0));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		
		JPanel buttonPanel = new JPanel(new GridLayout(2, 0, 0, 4));
		buttonPanel.add(btnRun);
		buttonPanel.add(btnClose);
		
		return buttonPanel;
	}
	
	private void storeSettings() {
		settings.setProperty("ECheckDialog.x", getX() + "");
		settings.setProperty("ECheckDialog.y", getY() + "");
		settings.setProperty("ECheckDialog.width", getWidth() + "");
		settings.setProperty("ECheckDialog.height", getHeight() + "");
		settings.setProperty("ECheckDialog.includeSelRegionsOnly",
							 new Boolean(pnlECheck.getSelRegionsOnly())+"");
		settings.setProperty("ECheckDialog.confirmedOnly",
							 new Boolean(pnlECheck.getConfirmedOnly())+"");

	}
	
	protected void quit() {
		storeSettings();
		pnlECheck.quit();
		super.quit();
	}
	public void exec() {
		pnlECheck.runECheck();
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
			defaultTranslations.put("window.title","ECheck frontend");
			
			defaultTranslations.put("btn.run.caption" , "Run");
			defaultTranslations.put("btn.run.mnemonic" , "r");
			defaultTranslations.put("btn.close.caption" , "Close");
			defaultTranslations.put("btn.close.mnemonic" , "c");
		}
		return defaultTranslations;
	}

}
