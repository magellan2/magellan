package com.eressea.gamebinding;

import com.eressea.GameData;
import com.eressea.Rules;
import com.eressea.completion.Completer;
import com.eressea.completion.CompleterSettingsProvider;
import com.eressea.completion.OrderParser;
import com.eressea.io.GameDataIO;

public interface GameSpecificStuff {
	
	// for io binding
	public GameData   createGameData(Rules rules, String name);
	public GameDataIO getGameDataIO();

	// for GameData
	/**
	 * Processes a GameData object augmenting objects with additional
	 * information from messages, simplifying the GameData structure,
	 * creating temp units as distinct objects etc.
	 * Note that this method requires the classes Locales and
	 * Translations to be set up properly so the order translations
	 * can be found.
	 *
	 * @param data the GameData object to process.
	 */
	public void postProcess(GameData data);

	// for TrustLevels
	public void postProcessAfterTrustlevelChange(GameData data);

	// for Unit
	/** 
	 * Delivers a game specific RelationFactory
	 */
	public RelationFactory getRelationFactory();

	/** 
	 * Delivers a game specific MovementEvaluator
	 */
	public MovementEvaluator getMovementEvaluator();

	/** 
	 * Delivers a game specific OrderChanger
	 */
	public OrderChanger    getOrderChanger();


	/** 
	 * Delivers a game specific OrderParser
	 */
	public OrderParser getOrderParser(Rules rules);

	/** 
	 * Delivers a game specific Completer
	 */
	public Completer getCompleter(GameData data, CompleterSettingsProvider csp);

}
