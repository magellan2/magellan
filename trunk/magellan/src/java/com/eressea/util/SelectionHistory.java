// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeListener;

import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;

public class SelectionHistory {
	private static SelectionListener eventHook = new EventHook();
	private static Bucket history = new Bucket(10);
	private static Collection ignoredSources = CollectionFactory.createHashSet();
	private static List listeners=CollectionFactory.createArrayList();
	
	public static SelectionListener getEventHook() {
		return eventHook;
	}
	
	public static void selectionChanged(SelectionEvent e) {
		if (e.getActiveObject() != null && !ignoredSources.contains(e.getSource())) {
			history.add(e.getActiveObject());
			informListeners();
		}
	}
	
	public static void addListener(ChangeListener l) {
		listeners.add(l);			
	}
	
	private static void informListeners() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Iterator it=listeners.iterator();
				while(it.hasNext())
					((ChangeListener)it.next()).stateChanged(null);
			}
		});
	}
	
	public static Collection getHistory() {
		return history;
	}
	
	public static void ignoreSource(Object o) {
		ignoredSources.add(o);
	}
	
	public static void unignoreSource(Object o) {
		ignoredSources.remove(o);
	}
	
	public static void setMaxSize(int i) {
		history.setMaxSize(i);
	}
	
	public static void clear() {
		history.clear();
	}
	
	private static class EventHook implements SelectionListener {
		public void selectionChanged(SelectionEvent e) {
			SelectionHistory.selectionChanged(e);
		}
	}
}
