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

package com.eressea.swing.completion;

import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import com.eressea.Unit;
import com.eressea.swing.preferences.PreferencesAdapter;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public interface OrderEditorList extends com.eressea.event.SelectionListener {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public JTextComponent getCurrentEditor();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Unit getCurrentUnit();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void addExternalKeyListener(KeyListener k);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void removeExternalKeyListener(KeyListener k);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void addExternalCaretListener(CaretListener k);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void removeExternalCaretListener(CaretListener k);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void addExternalFocusListener(FocusListener k);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param k TODO: DOCUMENT ME!
	 */
	public void removeExternalFocusListener(FocusListener k);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public PreferencesAdapter getPreferencesAdapter();
}
