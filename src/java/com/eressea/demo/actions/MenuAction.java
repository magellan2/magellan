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
 * $Id$
 */

package com.eressea.demo.actions;

import java.awt.Image;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.eressea.util.ImageFactory;
import com.eressea.util.Translations;
import com.eressea.util.logging.Logger;

/**
 * A common super class for all menu actions. It offers all necessary
 * information to build a menu with it.
 */
public abstract class MenuAction extends AbstractAction {
	private static final Logger log = Logger.getInstance(MenuAction.class);

	/**
	 * Creates a new MenuAction object reading its name, mnemonic and
	 * accelerator from the dictionary.
	 */
	public MenuAction() {
		this.setName(getNameTranslated());

		this.setIcon(getIconName());

		if(getMnemonicTranslated() != null) {
			this.putValue("mnemonic",
						  new Character(getMnemonicTranslated().charAt(0)));
		}

		if(getAcceleratorTranslated() != null) {
			this.putValue("accelerator",
						  KeyStroke.getKeyStroke(getAcceleratorTranslated()));
		}

		if(getTooltipTranslated() != null) {
			this.putValue("tooltip", getTooltipTranslated());
		}
	}

	/**
	 * These methods are now needed to keep translation in the corresponding
	 * class. they MAY deliver null!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected final String getNameTranslated() {
		return Translations.getTranslation(this, "name");
	}

	protected final String getMnemonicTranslated() {
		return Translations.getTranslation(this, "mnemonic");
	}

	protected final String getAcceleratorTranslated() {
		return Translations.getTranslation(this, "accelerator");
	}

	protected final String getTooltipTranslated() {
		return Translations.getTranslation(this, "tooltip");
	}

	/**
	 * This method is called whenever this action is invoked.
	 */
	public abstract void actionPerformed(java.awt.event.ActionEvent e);

	/**
	 * Sets the name of this menu action.
	 *
	 * @param name TODO: DOCUMENT ME!
	 */
	public void setName(String name) {
		this.putValue(Action.NAME, name);
	}

	/**
	 * Returns the name of this menu action.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		return (String) this.getValue(Action.NAME);
	}

	/**
	 * Sets the icon of this menu action by iconname.
	 *
	 * @param aName TODO: DOCUMENT ME!
	 */
	public void setIcon(String aName) {
		Icon icon = null;

		if(log.isDebugEnabled()) {
			log.debug("MenuAction.setIcon(" + aName + ") called");
		}

		if(aName != null) {
			String name		 = "images/gui/actions/" + aName;
			Image  imageIcon = ImageFactory.getFactory().loadImage(name);

			if(imageIcon != null) {
				icon = new ImageIcon(imageIcon);
			}
		}

		this.putValue(Action.SMALL_ICON, icon);
	}

	/**
				 *
				 */
	public String getIconName() {
		//String className = this.getClass().getName().toLowerCase();
		//int pos = className.lastIndexOf(".");
		//return pos == -1 ? className : className.substring(pos+1);
		return null;
	}

	/**
	 * Returns the mnemonic of the menu this menu action is to be associated
	 * with.
	 *
	 * @return the mnemonic, a value of 0 means that no mnemonic is set.
	 */
	public char getMnemonic() {
		Character c = (Character) this.getValue("mnemonic");

		if(c != null) {
			return c.charValue();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the shortcut key stroke this menu action is to be invokable
	 * with.
	 *
	 * @return the accelerator or null, if the menu has no accelerator.
	 */
	public KeyStroke getAccelerator() {
		return (KeyStroke) this.getValue("accelerator");
	}

	/**
	 * Returns the tool tip for this menu action.
	 *
	 * @return the tool tip String or null, if no tool tip is set.
	 */
	public String getToolTip() {
		return (String) this.getValue("tooltip");
	}

	/**
	 * Returns a String representation of this MenuAction object.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return this.getName();
	}

	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}
}
