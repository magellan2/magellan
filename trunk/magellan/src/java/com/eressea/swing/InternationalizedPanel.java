// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing;

import javax.swing.JPanel;

import com.eressea.util.Translations;

public class InternationalizedPanel extends JPanel {
	public InternationalizedPanel() {
		super();
	}

	/**
	 * Returns a translation from the translation table for the
	 * specified key.
	 */
	protected String getString(String key) {
		return Translations.getTranslation(this,key);
	}
}
