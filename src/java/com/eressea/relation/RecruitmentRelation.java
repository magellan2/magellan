/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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

package com.eressea.relation;

import com.eressea.Unit;

/**
 * A relation indicating that a unit recruits a certain amount of peasants.
 */
public class RecruitmentRelation extends PersonTransferRelation {
	/* pavkovic 2003.02.17: made RecruitmentRelation an inverse PersonTransferRelation! */
	public RecruitmentRelation(Unit t, int a, int line) {
		super(t.getRegion().getZeroUnit(), t, a, (t.realRace != null) ? t.realRace : t.race, line);

		// super(t, t.getRegion().getZeroUnit(), -amount, t.realRace != null ? t.realRace : t.race);
		// ...but we need to remember that the target unit is the originator of this
		// relation...
		this.origin = t;
	}
}
