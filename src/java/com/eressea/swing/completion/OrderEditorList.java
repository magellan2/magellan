// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.completion;


import java.awt.event.FocusListener;
import java.awt.event.KeyListener;

import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import com.eressea.Unit;
import com.eressea.swing.preferences.PreferencesAdapter;
/**
 *
 * @author  Andreas
 * @version 
 */
public interface OrderEditorList extends com.eressea.event.SelectionListener {
	
	public JTextComponent getCurrentEditor();
	public Unit getCurrentUnit();
	
	public void addExternalKeyListener(KeyListener k);
	public void removeExternalKeyListener(KeyListener k);
	
	public void addExternalCaretListener(CaretListener k);
	public void removeExternalCaretListener(CaretListener k);
	
	public void addExternalFocusListener(FocusListener k);
	public void removeExternalFocusListener(FocusListener k);	
	
	public PreferencesAdapter getPreferencesAdapter();
}

