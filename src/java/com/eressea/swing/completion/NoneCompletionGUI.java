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

/*
 * NoneCompletionGUI.java
 *
 * Created on 16. Oktober 2001, 12:53
 */
package com.eressea.swing.completion;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.text.JTextComponent;

import com.eressea.completion.AutoCompletion;
import com.eressea.completion.Completion;
import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class NoneCompletionGUI extends AbstractCompletionGUI {
	protected Completion last = null;
	protected boolean offering = false;

	/**
	 * Creates new NoneCompletionGUI
	 */
	public NoneCompletionGUI() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean editorMayLoseFocus() {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean editorMayUpdateCaret() {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Completion getSelectedCompletion() {
		return last;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int[] getSpecialKeys() {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param autoCompletion TODO: DOCUMENT ME!
	 */
	public void init(AutoCompletion autoCompletion) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isOfferingCompletion() {
		return offering;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param jTextComponent TODO: DOCUMENT ME!
	 * @param collection TODO: DOCUMENT ME!
	 * @param str TODO: DOCUMENT ME!
	 */
	public void offerCompletion(javax.swing.text.JTextComponent jTextComponent,
								java.util.Collection collection, java.lang.String str) {
		last = (Completion) collection.iterator().next();
		offering = true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param editor TODO: DOCUMENT ME!
	 * @param completions TODO: DOCUMENT ME!
	 * @param stub TODO: DOCUMENT ME!
	 * @param index TODO: DOCUMENT ME!
	 */
	public void cycleCompletion(JTextComponent editor, Collection completions, String stub,
								int index) {
		Iterator it = completions.iterator();

		for(int i = 0; i <= index; i++) {
			last = (Completion) it.next();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param param TODO: DOCUMENT ME!
	 */
	public void specialKeyPressed(int param) {
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void stopOffer() {
		offering = false;
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
			defaultTranslations.put("gui.title", "No display");
		}

		return defaultTranslations;
	}
}
