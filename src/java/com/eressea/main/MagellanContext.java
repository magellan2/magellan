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

package com.eressea.main;

import java.util.Properties;

import com.eressea.event.EventDispatcher;

import com.eressea.resource.ResourcePathClassLoader;

import com.eressea.util.IDBaseConverter;
import com.eressea.util.ImageFactory;
import com.eressea.util.Locales;
import com.eressea.util.NameGenerator;
import com.eressea.util.Translations;

/**
 * This class keeps all anchors to global resources e.g. EventDispatcher, Properties...<br>
 * This class implements the <tt>Singleton</tt> pattern.
 */
public class MagellanContext {
	// prevent creation 
	private MagellanContext() {
	}

	private static final MagellanContext CONTEXT = new MagellanContext();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static MagellanContext getInstance() {
		return CONTEXT;
	}

	private Properties settings;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Properties getSettings() {
		return settings;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p TODO: DOCUMENT ME!
	 */
	public void setProperties(Properties p) {
		settings = p;
	}

	//private EventDispatcher dispatcher;

	/**
	 * Returns the shared instance of the event dispatcher. This will create a
	 * new one if there's no current one. This is the Singleton pattern.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public EventDispatcher getEventDispatcher() {
		return EventDispatcher.getDispatcher();

		//if (dispatcher ==null) {
		//	dispatcher = new EventDispatcher();
		//}
		//return dispatcher;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 */
	public void init(Properties settings) {
		setProperties(settings);

		ResourcePathClassLoader.init(settings); // init resource class with new settings

		Locales.init(settings); // init the locales with new settings

		// init the translations with the loaded settings
		Translations.setClassLoader(new ResourcePathClassLoader(settings));

		// init the idbaseconverter
		IDBaseConverter.init();

		NameGenerator.init(settings);

		// inits ImageFactory
		ImageFactory.getFactory();
	}
}
