// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===


package com.eressea.completion;

import java.util.List;

import com.eressea.GameData;
import com.eressea.Unit;
import com.eressea.event.EventDispatcher;
import com.eressea.event.GameDataEvent;
import com.eressea.event.GameDataListener;
import com.eressea.util.OrderCompleter;
/**
 *
 * @author  Andreas
 * @version
 */
public class EresseaCompleter implements Completer, GameDataListener {

		protected OrderCompleter completer;

		/** Creates new EresseaCompleter */
		public EresseaCompleter(GameData data, EventDispatcher d, AutoCompletion ac) {
			completer=new OrderCompleter(data, ac);
			d.addGameDataListener(this);
		}

		public List getCompletions(Unit u, String line, List old) {
			if (old==null || old.size()==0) {
				return completer.getCompletions(u,line);
			} else {
				return completer.crop(old,line);
			}
		}

		public void gameDataChanged(GameDataEvent e) {
			completer.setGameData(e.getGameData());
		}

}
