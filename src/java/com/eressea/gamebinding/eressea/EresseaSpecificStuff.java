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

public class EresseaSpecificStuff implements GameSpecificStuff {

	/**
	 * This is a callback interface to let the 
	 * GameSpecificStuff create the GameData object.
	 */
	public GameData createGameData(Rules rules, String name) {
		return new CompleteData(rules, name, this);
	}

	public GameDataIO getGameDataIO() {
		return null;
	}
	
	public void postProcess(GameData data) {
		EresseaPostProcessor.getSingleton().postProcess(data);
	}

	public void postProcessAfterTrustlevelChange(GameData data) {
		EresseaPostProcessor.getSingleton().postProcessAfterTrustlevelChange(data);
	}

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
