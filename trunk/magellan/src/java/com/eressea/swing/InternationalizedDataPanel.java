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

import java.util.Properties;

import com.eressea.GameData;

import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class InternationalizedDataPanel extends InternationalizedPanel
	implements GameDataListener
{
	protected GameData		  data		 = null;
	protected Properties	  settings   = null;
	protected EventDispatcher dispatcher = null;

	/**
	 * Creates a new InternationalizedDataPanel object.
	 *
	 * @param ed TODO: DOCUMENT ME!
	 */
	public InternationalizedDataPanel(EventDispatcher ed) {
		this(ed, new Properties());
	}

	/**
	 * Creates a new InternationalizedDataPanel object.
	 *
	 * @param ed TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public InternationalizedDataPanel(EventDispatcher ed, Properties p) {
		this(ed, null, p);
	}

	/**
	 * Creates a new InternationalizedDataPanel object.
	 *
	 * @param ed TODO: DOCUMENT ME!
	 * @param initData TODO: DOCUMENT ME!
	 * @param p TODO: DOCUMENT ME!
	 */
	public InternationalizedDataPanel(EventDispatcher ed, GameData initData,
									  Properties p) {
		super();
		this.dispatcher = ed;

		if(this.dispatcher != null) {
			this.dispatcher.addGameDataListener(this);
		}

		this.data     = initData;
		this.settings = p;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void quit() {
		if(this.dispatcher != null) {
			dispatcher.removeGameDataListener(this);

			// remove stale listeners
			dispatcher.removeAllListeners(this);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public void gameDataChanged(GameDataEvent e) {
		this.data = e.getGameData();
	}
}
