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

package com.eressea.gamebinding.eressea;

import com.eressea.CompleteData;
import com.eressea.GameData;
import com.eressea.Rules;
import com.eressea.completion.Completer;
import com.eressea.completion.CompleterSettingsProvider;
import com.eressea.completion.OrderParser;
import com.eressea.gamebinding.GameSpecificStuff;
import com.eressea.gamebinding.MovementEvaluator;
import com.eressea.gamebinding.OrderChanger;
import com.eressea.gamebinding.RelationFactory;
import com.eressea.io.GameDataIO;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EresseaSpecificStuff implements GameSpecificStuff {
	/**
	 * This is a callback interface to let the  GameSpecificStuff create the GameData object.
	 *
	 * @param rules TODO: DOCUMENT ME!
	 * @param name TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public GameData createGameData(Rules rules, String name) {
		return new CompleteData(rules, name);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public GameDataIO getGameDataIO() {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param data TODO: DOCUMENT ME!
	 */
	public void postProcess(GameData data) {
		EresseaPostProcessor.getSingleton().postProcess(data);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param data TODO: DOCUMENT ME!
	 */
	public void postProcessAfterTrustlevelChange(GameData data) {
		EresseaPostProcessor.getSingleton().postProcessAfterTrustlevelChange(data);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OrderChanger getOrderChanger() {
		return EresseaOrderChanger.getSingleton();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RelationFactory getRelationFactory() {
		return EresseaRelationFactory.getSingleton();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public MovementEvaluator getMovementEvaluator() {
		return EresseaMovementEvaluator.getSingleton();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param data TODO: DOCUMENT ME!
	 * @param csp TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Completer getCompleter(GameData data, CompleterSettingsProvider csp) {
		return new EresseaOrderCompleter(data, csp);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rules TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OrderParser getOrderParser(Rules rules) {
		return new EresseaOrderParser(rules);
	}
}
