// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;

import java.util.Properties;

import com.eressea.GameData;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;

public class InternationalizedDataPanel extends InternationalizedPanel implements GameDataListener {
	
	protected GameData data = null;
	protected Properties settings = null;
	protected EventDispatcher dispatcher = null;
	
	public InternationalizedDataPanel(EventDispatcher ed) {
		this(ed, new Properties());
	}
	
	public InternationalizedDataPanel(EventDispatcher ed, Properties p) {
		this(ed, null, p);
	}

	public InternationalizedDataPanel(EventDispatcher ed, GameData initData, Properties p) {
		super();
		this.dispatcher = ed;
		if (this.dispatcher != null) {
			this.dispatcher.addGameDataListener(this);
		}
		this.data = initData;
		this.settings = p;
	}
	
	public void quit() {
		if (this.dispatcher != null) {
			dispatcher.removeGameDataListener(this);
			// remove stale listeners
			dispatcher.removeAllListeners(this);
		}
	}
	
	public void gameDataChanged(GameDataEvent e) {
		this.data = e.getGameData();
	}
}
