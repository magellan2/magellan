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
	 */
	public int getTrustLevel(Alliance alliance) {
		int retVal = 0;
		if (alliance.getState(EresseaConstants.A_ALL)) {
			retVal = 60;
		} else if (alliance.getState(EresseaConstants.A_GUISE)) {
			retVal = 50;
		} else if (alliance.getState(EresseaConstants.A_COMBAT)) {
			retVal = 40;
		} else if (alliance.getState(EresseaConstants.A_GUARD)) {
			retVal = 30;
		} else if (alliance.getState(EresseaConstants.A_GIVE)) {
			retVal = 20;
		} else if (alliance.getState(EresseaConstants.A_SILVER)) {
			retVal = 10;
		}
		return retVal;
	}
}
