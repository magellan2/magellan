package com.eressea.gamebinding.eressea;

import com.eressea.GameData;
import com.eressea.Rules;
import com.eressea.completion.Completer;
import com.eressea.completion.CompleterSettingsProvider;
import com.eressea.gamebinding.GameSpecificStuff;
import com.eressea.gamebinding.MovementEvaluator;
import com.eressea.gamebinding.OrderChanger;
import com.eressea.completion.OrderParser;
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

	public Completer getCompleter(GameData data, CompleterSettingsProvider csp) {
		return new EresseaOrderCompleter(data, csp);
	}

	public OrderParser getOrderParser(Rules rules) {
		return new EresseaOrderParser(rules);
	}

}
