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

package com.eressea.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeListener;

import com.eressea.event.SelectionEvent;
import com.eressea.event.SelectionListener;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SelectionHistory {
	private static SelectionListener eventHook = new EventHook();
	private static Bucket history = new Bucket(10);
	private static Collection ignoredSources = CollectionFactory.createHashSet();
	private static List listeners = CollectionFactory.createArrayList();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static SelectionListener getEventHook() {
		return eventHook;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param e TODO: DOCUMENT ME!
	 */
	public static void selectionChanged(SelectionEvent e) {
		if((e.getActiveObject() != null) && !ignoredSources.contains(e.getSource())) {
			history.add(e.getActiveObject());
			informListeners();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public static void addListener(ChangeListener l) {
		listeners.add(l);
	}

	private static void informListeners() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Iterator it = listeners.iterator();

					while(it.hasNext()) {
						((ChangeListener) it.next()).stateChanged(null);
					}
				}
			});
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Collection getHistory() {
		return history;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 */
	public static void ignoreSource(Object o) {
		ignoredSources.add(o);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 */
	public static void unignoreSource(Object o) {
		ignoredSources.remove(o);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public static void setMaxSize(int i) {
		history.setMaxSize(i);
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public static void clear() {
		history.clear();
	}

	private static class EventHook implements SelectionListener {
		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param e TODO: DOCUMENT ME!
		 */
		public void selectionChanged(SelectionEvent e) {
			SelectionHistory.selectionChanged(e);
		}
	}
}
