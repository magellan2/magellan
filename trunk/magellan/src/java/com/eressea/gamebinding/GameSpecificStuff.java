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

package com.eressea.gamebinding;

import com.eressea.GameData;
import com.eressea.Rules;

import com.eressea.completion.Completer;
import com.eressea.completion.CompleterSettingsProvider;
import com.eressea.completion.OrderParser;

import com.eressea.io.GameDataIO;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface GameSpecificStuff {
	// for io binding
	public GameData createGameData(Rules rules, String name);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public GameDataIO getGameDataIO();

	// for GameData

	/**
	 * Processes a GameData object augmenting objects with additional
	 * information from messages, simplifying the GameData structure, creating
	 * temp units as distinct objects etc. Note that this method requires the
	 * classes Locales and Translations to be set up properly so the order
	 * translations can be found.
	 *
	 * @param data the GameData object to process.
	 */
	public void postProcess(GameData data);

	// for TrustLevels
	public void postProcessAfterTrustlevelChange(GameData data);

	// for Unit

	/**
	 * Delivers a game specific RelationFactory
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RelationFactory getRelationFactory();

	/**
	 * Delivers a game specific MovementEvaluator
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public MovementEvaluator getMovementEvaluator();

	/**
	 * Delivers a game specific OrderChanger
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OrderChanger getOrderChanger();

	/**
	 * Delivers a game specific OrderParser
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OrderParser getOrderParser(Rules rules);

	/**
	 * Delivers a game specific Completer
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Completer getCompleter(GameData data, CompleterSettingsProvider csp);
}
