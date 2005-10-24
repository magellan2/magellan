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

package com.eressea.main;

import java.util.Properties;

import com.eressea.GameData;
import com.eressea.demo.Client;
import com.eressea.event.EventDispatcher;
import com.eressea.resource.ResourcePathClassLoader;
import com.eressea.util.IDBaseConverter;
import com.eressea.util.ImageFactory;
import com.eressea.util.Locales;
import com.eressea.util.NameGenerator;
import com.eressea.util.Translations;
import com.eressea.util.replacers.ReplacerHelp;

/**
 * This class keeps all anchors to global resources e.g. EventDispatcher, Properties...<br>
 */
public class MagellanContext implements MagellanEnvironment {
    private Properties settings;
    private Properties completionSettings;
    private EventDispatcher dispatcher;
    private GameData data;
    private Client client;
    
	public MagellanContext(Client client) {
        this.client = client;
	}
    
    public Client getClient() {
        return client;
    }
    
	/** 
	 * Returns the properties of Magellan.
	 */
	public Properties getProperties() {
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

	/** 
	 * Returns the EventDispatcher of Magellan.
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}

	public void setEventDispatcher(EventDispatcher d) {
		dispatcher = d;
        dispatcher.setMagellanContext(this);
	}

    /** 
     * Returns the current GameData.
     */
    public GameData getGameData() {
        return data;
    }

    public void setGameData(GameData d) {
        data = d;
    }

    ImageFactory imageFactory = null;
    public ImageFactory getImageFactory() {
        return imageFactory;
    }

    private ReplacerHelp replacerHelp;
    public ReplacerHelp getReplacerHelp() {
        return replacerHelp;
    }
    /** 
	 * Initializes global resources.
	 */
	public synchronized void init() {
		ResourcePathClassLoader.init(settings); // init resource class with new settings
		
		Locales.init(settings); // init the locales with new settings
		
		// init the translations with the loaded settings
		Translations.setClassLoader(new ResourcePathClassLoader(settings));
		
		// init the idbaseconverter
		IDBaseConverter.init();

		NameGenerator.init(settings);

		// inits ImageFactory
        imageFactory = new ImageFactory(getEventDispatcher());
        
        // inits ReplacerHelp
        replacerHelp = new ReplacerHelp(getEventDispatcher(),getGameData());
    }

    public Properties getCompletionProperties() {
        return completionSettings;
    }

    public void setCompletionProperties(Properties completionSettings2) {
        completionSettings = completionSettings2;
    }
}
