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

import com.eressea.Alliance;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EresseaTrustLevelEvaluator {
	private EresseaTrustLevelEvaluator() {
	}

	private static final EresseaTrustLevelEvaluator singleton = new EresseaTrustLevelEvaluator();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static EresseaTrustLevelEvaluator getSingleton() {
		return singleton;
	}

	/**
	 * A method to convert an alliance into a trustlevel. This method should be uses when Magellan
	 * calculates trust levels on its own.
	 *
	 * @param alliance TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @deprecated
	 */
	public int getTrustLevel(Alliance alliance) {
		return alliance.getTrustLevel();
	}
}
