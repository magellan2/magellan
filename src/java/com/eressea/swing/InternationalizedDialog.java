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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import com.eressea.util.JVMUtilities;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class InternationalizedDialog extends JDialog {
	private static final Logger log = Logger.getInstance(InternationalizedDialog.class);

	/**
	 * Creates a new InternationalizedDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 */
	public InternationalizedDialog(Frame owner, boolean modal) {
		super(owner, modal);
		initDialog();
	}

	/**
	 * Creates a new InternationalizedDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 */
	public InternationalizedDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		initDialog();
	}

	protected void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);

		if((e.getID() == KeyEvent.KEY_PRESSED) && (e.getKeyCode() == KeyEvent.VK_ESCAPE)) {
			quit();
		}
	}

	protected void initDialog() {
		// call setFocusableWindowState (true) on java 1.4 while staying compatible with Java 1.3
		JVMUtilities.setFocusableWindowState(this, true);

		addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(log.isDebugEnabled()) {
						log.debug("InternationalizedDialog.KeyEvent :" + e);
					}

					if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						quit();
					}
				}
			});
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if(log.isDebugEnabled()) {
						log.debug("InternationalizedDialog.WindowEvent :" + e);
					}

					quit();
				}
			});
	}

	protected void quit() {
		if(log.isDebugEnabled()) {
			log.debug("InternationalizedDialog.quit called. (" + this + ")");
		}

		dispose();
	}

	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}
}
