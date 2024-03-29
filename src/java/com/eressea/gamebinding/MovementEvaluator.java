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

package com.eressea.gamebinding;

import com.eressea.Unit;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface MovementEvaluator {
	/** The unit does not possess horses */
	public static final int CAP_NO_HORSES = Integer.MIN_VALUE;

	/* The unit is not sufficiently skilled in horse riding */

	/** TODO: DOCUMENT ME! */
	public static final int CAP_UNSKILLED = CAP_NO_HORSES + 1;

	/**
	 * Returns the maximum payload in GE  100 of this unit when it travels by horse. Horses, carts
	 * and persons are taken into account for this calculation. If the unit has a sufficient skill
	 * in horse riding but there are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 *
	 * @return the payload in GE  100, CAP_NO_HORSES if the unit does not possess horses or
	 * 		   CAP_UNSKILLED if the unit is not sufficiently skilled in horse riding to travel on
	 * 		   horseback.
	 */
	public int getPayloadOnHorse(Unit unit);

	/**
	 * Returns the maximum payload in GE  100 of this unit when it travels on foot. Horses, carts
	 * and persons are taken into account for this calculation. If the unit has a sufficient skill
	 * in horse riding but there are too many carts for the horses, the weight of the additional
	 * carts are also already considered. The calculation also takes into account that trolls can
	 * tow carts.
	 *
	 * @return the payload in GE  100, CAP_UNSKILLED if the unit is not sufficiently skilled in
	 * 		   horse riding to travel on horseback.
	 */
	public int getPayloadOnFoot(Unit unit);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLoad(Unit unit);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getModifiedLoad(Unit unit);
}
