package com.eressea.gamebinding;

import com.eressea.GameData;
import com.eressea.Rules;
import com.eressea.completion.Completer;
import com.eressea.completion.OrderParser;
import com.eressea.completion.CompleterSettingsProvider;

public interface GameSpecificStuff {

	// for Unit
	public RelationFactory getRelationFactory();
	public MovementEvaluator getMovementEvaluator();

	// for EMapDetailsPanel
	public OrderChanger    getOrderChanger();

	// public 
	public OrderParser getOrderParser(Rules rules);
	// public OrderWriter getOrderReader();
	// public OrderWriter getOrderWriter();
	
	public Completer getCompleter(GameData data, CompleterSettingsProvider csp);

}
