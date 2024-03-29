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

package com.eressea.swing;

import java.text.MessageFormat;

import javax.swing.JPanel;

import com.eressea.util.Translations;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public abstract class InternationalizedPanel extends JPanel {
	/**
	 * Creates a new InternationalizedPanel object.
	 */
	public InternationalizedPanel() {
		super();
	}

	/**
	 * Returns a translation from the translation table for the specified key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}
    
    protected String getString(String key, Object[] args) {
        return new MessageFormat(getString(key)).format(args);
    }
}
