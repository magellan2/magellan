// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.demo.actions;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.eressea.util.Translations;

/**
 * A common super class for all menu actions. It offers all necessary
 * information to build a menu with it.
 */
public abstract class MenuAction extends AbstractAction {
	/**
	 * Creates a new MenuAction object reading its name, mnemonic and
	 * accelerator from the dictionary.
	 */
	public MenuAction() {
		this.setName(getNameTranslated());

		if(getMnemonicTranslated() != null) {
			this.putValue("mnemonic"   , new Character(getMnemonicTranslated().charAt(0)));
		}
		if(getAcceleratorTranslated() != null) {
			this.putValue("accelerator", KeyStroke.getKeyStroke(getAcceleratorTranslated()));
		}	
		if(getTooltipTranslated() != null) {
			this.putValue("tooltip"    , getTooltipTranslated());
		}
	}

	/**
	 * These methods are now needed to keep translation in the corresponding class. they MAY deliver null!
	 */
	protected final String getNameTranslated() {
		return Translations.getTranslation(this,"name");
	}
	protected final String getMnemonicTranslated() {		
		return Translations.getTranslation(this,"mnemonic");
	}
	protected final String getAcceleratorTranslated() {
		return Translations.getTranslation(this,"accelerator");
	}
	protected final String getTooltipTranslated() {
		return Translations.getTranslation(this,"tooltip");
	}
 
	/**
	 * This method is called whenever this action is invoked.
	 */
	public abstract void actionPerformed(java.awt.event.ActionEvent e);
	
	/**
	 * Sets the name of this menu action.
	 */
	public void setName(String name) {
		this.putValue(javax.swing.Action.NAME, name);
	}
	
	/**
	 * Returns the name of this menu action.
	 */
	public String getName() {
		return (String)this.getValue(javax.swing.Action.NAME);
	}
	
	/**
	 * Returns the mnemonic of the menu this menu action is to be
	 * associated with.
	 *
	 * @returns the mnemonic, a value of 0 means that no mnemonic is
	 * set.
	 */
	public char getMnemonic() {
		Character c = (Character)this.getValue("mnemonic");
		if (c != null) {
			return c.charValue();
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns the shortcut key stroke this menu action is to be
	 * invokable with.
	 *
	 * @returns the accelerator or null, if the menu has no
	 * accelerator.
	 */
	public javax.swing.KeyStroke getAccelerator() {
		return (javax.swing.KeyStroke)this.getValue("accelerator");
	}
	
	/**
	 * Returns the tool tip for this menu action.
	 *
	 * @returns the tool tip String or null, if no tool tip is set.
	 */
	public String getToolTip() {
		return (String)this.getValue("tooltip");
	}

	/**
	 * Returns a String representation of this MenuAction object.
	 */
	public String toString() {
		return this.getName();
	}


	protected String getString(String key) {
		return Translations.getTranslation(this,key);
	}
}
