// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.completion;


import java.util.Collection;

import javax.swing.text.JTextComponent;

import com.eressea.completion.AutoCompletion;
import com.eressea.util.Completion;

/**
 * Simple base interface for all elements that show the user possible
 * completions.
 *
 * @author  Andreas
 * @version 
 */
public interface CompletionGUI {
	
	/**
	 * Initialize this GUI for use with the given AutoCompletion. This method is
	 * called by AutoCompletion when the GUI is added to it.
	 */
	public void init(AutoCompletion ac);
	
	/**
	 * Should return true if this GUI is currently offering a completion to the
	 * user.
	 */
	public boolean isOfferingCompletion();
	/**
	 * Called the advice this GUI to offer the given completions in the given Editor
	 * to the user.
	 */
	public void offerCompletion(JTextComponent editor,Collection completions, String stub);

	/**
	 * Called to cycle through multiple completions. Use the given index to find the
	 * completion that should be shown.
	 *
	 * Following is guaranteed:
	 * <ul>
	 *   <li>A call to offerCompletion was made before any cycleCompletion calls
	 *   <li>The given parameters are the same used in the offerCompletion call
	 * </ul>
	 */
	public void cycleCompletion(JTextComponent editor,Collection completions, String stub, int index);
	/**
	 * Called when this GUI should stop offering completions.
	 */
	public void stopOffer();
	
	/**
	 * If this GUI needs some special keys the Key-Codes con be obtained by this
	 * method.
	 */
	public int[] getSpecialKeys();
	
	/**
	 * When AutoCompletion recognizes a special key of getSpecialKeys(), this method
	 * is called with the key found.
	 */
	public void specialKeyPressed(int key);
	
	/**
	 * If the editor my lose the focus because of a GUI action(usually after
	 * specialKeyPressed()), this method should return true to avoid AutoCompletion
	 * calling stopOffer().
	 */
	public boolean editorMayLoseFocus();
	/**
	 * If the editor my update the caret because of a GUI action(usually after
	 * specialKeyPressed()), this method should return true to avoid AutoCompletion
	 * calling stopOffer().
	 */	
	public boolean editorMayUpdateCaret();
	
	/**
	 * Returns the currently selected Completion object.
	 */
	public Completion getSelectedCompletion();
	
	/**
	 * Returns the name of this CompletionGUI.
	 */
	public String getTitle();
}

