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

import java.awt.Frame;

import java.util.Properties;

import com.eressea.GameData;

import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataListener;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class InternationalizedDataDialog extends InternationalizedDialog
	implements GameDataListener
{
	protected GameData		  data		 = null;
	protected Properties	  settings   = null;
	protected EventDispatcher dispatcher = null;

	/**
	 * Creates a new InternationalizedDataDialog object.
	 *
	 * @param owner TODO: DOCUMENT ME!
	 * @param modal TODO: DOCUMENT ME!
	 * @param ed TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public InternationalizedDataDialog(Frame owner, boolean modal,
									   EventDispatcher ed, GameData initData,
									   Properties p) {
		super(owner, modal);
		this.dispatcher = ed;

		if(this.dispatcher != null) {
			this.dispatcher.addGameDataListener(this);
		}

		this.data     = initData;
		this.settings = p;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(com.eressea.event.GameDataEvent e) {
		this.data = e.getGameData();
	}

	protected void quit() {
		super.quit();

		if(dispatcher != null) {
			// remove stale listeners
			dispatcher.removeAllListeners(this);

			//dispatcher.removeGameDataListener(this);
		}
	}
}
