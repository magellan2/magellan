// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;

import java.awt.Frame;
import java.util.Properties;

import com.eressea.GameData;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataListener;

public class InternationalizedDataDialog extends InternationalizedDialog implements GameDataListener {
	protected GameData data = null;
	protected Properties settings = null;
	protected EventDispatcher dispatcher = null;
	
	public InternationalizedDataDialog(Frame owner, boolean modal, EventDispatcher ed, GameData initData, Properties p) {
		super(owner, modal);
		this.dispatcher = ed;
		if (this.dispatcher != null) {
			this.dispatcher.addGameDataListener(this);
		}
		this.data = initData;
		this.settings = p;
	}

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
