package com.eressea.gamebinding;

import com.eressea.Unit;

public interface MovementEvaluator {
	/** The unit does not possess horses */	
	public static final int CAP_NO_HORSES = Integer.MIN_VALUE;

	/* The unit is not sufficiently skilled in horse riding */
	public static final int CAP_UNSKILLED = CAP_NO_HORSES + 1;

	/**
	 * Returns the maximum payload in GE * 100 of this unit when it
	 * travels by horse.
	 * Horses, carts and persons are taken into account for this
	 * calculation.
	 * If the unit has a sufficient skill in horse riding but there
	 * are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 *
	 * @returns the payload in GE * 100, CAP_NO_HORSES if the unit
	 * does not possess horses or CAP_UNSKILLED if the unit is not
	 * sufficiently skilled in horse riding to travel on horseback.
	 */
	public int getPayloadOnHorse(Unit unit);

	/**
	 * Returns the maximum payload in GE * 100 of this unit when it
	 * travels on foot.
	 * Horses, carts and persons are taken into account for this
	 * calculation.
	 * If the unit has a sufficient skill in horse riding but there
	 * are too many carts for the horses, the weight of the additional
	 * carts are also already considered.
	 * The calculation also takes into account that trolls can tow
	 * carts.
	 *
	 * @returns the payload in GE * 100, CAP_UNSKILLED if the unit is
	 * not sufficiently skilled in horse riding to travel on horseback.
	 */	
	public int getPayloadOnFoot(Unit unit);

	public int getLoad(Unit unit);

	public int getModifiedLoad(Unit unit);

	
}
