package com.eressea.main;

import java.util.Properties;

import com.eressea.event.EventDispatcher;

import com.eressea.resource.*;
import com.eressea.util.*;


/** 
 * This class keeps all anchors to global resources e.g. EventDispatcher, Properties...<br>
 * This class implements the <tt>Singleton</tt> pattern.
 * 
 */
public class MagellanContext {

	// prevent creation 
	private MagellanContext() {
	}

	private final static MagellanContext CONTEXT = new MagellanContext();

	public static MagellanContext getInstance() {
		return CONTEXT;
	}

	private Properties settings;
	public Properties getSettings() {
		return settings;
	}
	public void setProperties(Properties p) {
		settings = p;
	}

	//private EventDispatcher dispatcher;
	/**
	 * Returns the shared instance of the event dispatcher. This will create a
	 * new one if there's no current one. This is the Singleton pattern.
	 */
	public EventDispatcher getEventDispatcher() {
		return EventDispatcher.getDispatcher();
		//if (dispatcher ==null) {
		//	dispatcher = new EventDispatcher();
		//}
		//return dispatcher;
	}


	public void init(Properties settings) {
		setProperties(settings);

		ResourcePathClassLoader.init(settings);	// init resource class with new settings
		
		Locales.init(settings); // init the locales with new settings

		// init the translations with the loaded settings
		Translations.setClassLoader(new ResourcePathClassLoader(settings));
		
		// init the idbaseconverter
		IDBaseConverter.init();
		
		NameGenerator.init(settings);
	}
}
