package com.eressea.gamebinding.eressea;

import com.eressea.gamebinding.GameSpecificStuff;
import com.eressea.gamebinding.MovementEvaluator;
import com.eressea.gamebinding.OrderChanger;
import com.eressea.gamebinding.RelationFactory;

public class EresseaSpecificStuff implements GameSpecificStuff {
	public OrderChanger getOrderChanger() {
		return EresseaOrderChanger.getSingleton();
	}

	public RelationFactory getRelationFactory() {
		return EresseaRelationFactory.getSingleton();
	}

	public MovementEvaluator getMovementEvaluator() {
		return EresseaMovementEvaluator.getSingleton();
	}
}
