// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.util;

import java.awt.Component;

import java.io.File;

import java.util.Locale;
import java.util.Properties;


/**
 * Helper class for centrally managing different locales.
 *
 * This class provides the locales statically. Optionally, you can
 * specify a Properties object from which this class determines which
 * Locale to use. If the Locales are changed this is also recorded in
 * the Properties object.
 */
public class Locales {

	private static Properties settings = null;
	private static Locale guiLocale = null;
	private static Locale orderLocale = null;
	/**
	 * Provides this class with settings to load the default locales
	 * from and store them to, if they are changed.
	 */
	public static void init(Properties p) {
		settings = p;
		// initialize Locale
		getGUILocale();
	}
	
	/**
	 * Sets the locale for the user interface. If Locales was
	 * initialized with a Properties object earlier, the new Locale is
	 * stored in it.
	 */
	public static void setGUILocale(Locale l) {
		Locale.setDefault(l);
		guiLocale = l;
		if (settings != null) {
			settings.setProperty("locales.gui", l.getLanguage());
		}
	}
	
	/**
	 * Returns the locale applicable for the user interface.
	 *
	 * @throws IllegalStateException when the method is invoked
	 * and neither the init() nor the setGUILocale() methodes were
	 * invoked earlier with valid arguments.
	 */
	public static Locale getGUILocale() throws IllegalStateException {
		if (guiLocale == null) {
			if (settings != null) {
				setGUILocale(new Locale(settings.getProperty("locales.gui", Locale.getDefault().getLanguage()), ""));
			} else {
				throw new IllegalStateException("Locales is not initialized");
			}
		}
		return guiLocale;
	}

	/**
	 * Sets the locale for the unit orders. If Locales was
	 * initialized with a Properties object earlier, the new Locale is
	 * stored in it.
	 */
	public static void setOrderLocale(Locale l) {
		orderLocale = l;
		if (settings != null) {
			settings.setProperty("locales.orders", l.getLanguage());
		}
	}

	/**
	 * Returns the locale applicable for the unit orders.
	 *
	 * @throws IllegalStateException when the method is invoked
	 * and neither the init() nor the setGUILocale() methodes were
	 * invoked earlier with valid arguments.
	 */
	public static Locale getOrderLocale() throws IllegalStateException {
		if (orderLocale == null) {
			if (settings != null) {
				orderLocale = new Locale(settings.getProperty("locales.orders", Locale.getDefault().getLanguage()), "");
			} else {
				throw new IllegalStateException("Locales is not initialized");
			}
		}
		return orderLocale;
	}
}
