package com.eressea.gamebinding.eressea;

import com.eressea.Alliance;

public class EresseaTrustLevelEvaluator {

	private EresseaTrustLevelEvaluator() {
	}

	private final static EresseaTrustLevelEvaluator singleton= new EresseaTrustLevelEvaluator();
	public static EresseaTrustLevelEvaluator getSingleton() {
		return singleton;
	}

	/**
	 * A method to convert an alliance into a trustlevel.
	 * This method should be uses when Magellan calculates trust levels on its own.
	 * @deprecated 
	 */
	public int getTrustLevel(Alliance alliance) {
		return alliance.getTrustLevel();
	}
}
