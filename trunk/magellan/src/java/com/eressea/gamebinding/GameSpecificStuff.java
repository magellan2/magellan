package com.eressea.gamebinding;

import com.eressea.GameData;
import com.eressea.Rules;
import com.eressea.completion.Completer;
import com.eressea.completion.OrderParser;
import com.eressea.completion.CompleterSettingsProvider;

public interface GameSpecificStuff {

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
