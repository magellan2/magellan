package com.eressea.gamebinding;

public interface GameSpecificStuff {

	// for Unit
	public RelationFactory getRelationFactory();
	public MovementEvaluator getMovementEvaluator();

	// for EMapDetailsPanel
	public OrderChanger    getOrderChanger();

	// public OrderParser getOrderParser();
	// public OrderWriter getOrderReader();
	// public OrderWriter getOrderWriter();
	// public OrderCompleter getOrderCompleter?
}
