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

package com.eressea.tasks;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.eressea.Coordinate;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.StringID;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Direction;
import com.eressea.util.Regions;

/**
 */
public class ShipInspector extends AbstractInspector implements Inspector {
	/** TODO: DOCUMENT ME! */
	public static final ShipInspector INSPECTOR = new ShipInspector();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static ShipInspector getInstance() {
		return INSPECTOR;
	}

	protected ShipInspector() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewRegion(Region r, int type) {
		// we notify errors only
		if(type != Problem.ERROR) {
			return Collections.EMPTY_LIST;
		}

		// fail fast if prerequisites are not fulfilled
		if((r == null) || (r.units() == null) || r.units().isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		// this inspector is only interested in ships
		if((r.ships() == null) || r.ships().isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		List problems = reviewShips(r);

		if(problems.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			return problems;
		}
	}

	private List reviewShips(Region r) {
		List problems = CollectionFactory.createArrayList(2);

		for(Iterator iter = r.ships().iterator(); iter.hasNext();) {
			Ship s = (Ship) iter.next();
			problems.addAll(reviewShip(s));
		}

		return problems;
	}

	private List reviewShip(Ship s) {
		int nominalShipSize = s.getShipType().getMaxSize();

		if(s.size != nominalShipSize) {
			// ship will be build, so dont review ship
			return Collections.EMPTY_LIST;
		}

		if(s.modifiedUnits().isEmpty()) {
			return Collections.singletonList(new CriticizedError(s.getRegion(), s, this,
																 "Ship has no crew!"));
		}

		if(s.getModifiedLoad() > (s.getMaxCapacity())) {
			return Collections.singletonList(new CriticizedError(s.getRegion(), s, this,
																 "Ship is overloaded!"));
		}

		return reviewMovingShip(s);
	}

	private List reviewMovingShip(Ship s) {
		if(s.getOwnerUnit() == null) {
			return Collections.EMPTY_LIST;
		}

		;

		List modifiedMovement = s.getOwnerUnit().getModifiedMovement();

		if(modifiedMovement.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		Coordinate nextRegionCoord = (Coordinate) modifiedMovement.get(1);
		Region nextRegion = s.getRegion().getData().getRegion(nextRegionCoord);

		if(nextRegion != null) {
			if(nextRegion.getData().rules.getRegionType(StringID.create("Ozean")).equals(nextRegion.getRegionType()) == false) {
				return Collections.singletonList(new CriticizedError(s.getRegion(), s, this,
																	 "Ship cannot move to this direction."));
			}
		}

		if(s.shoreId != -1) {
			Direction d = (Direction) Regions.getDirectionObjectsOfCoordinates(modifiedMovement)
											 .get(0);

			if((((6 + s.shoreId) - d.getDir()) % 6) > 1) {
				//if(log.isDebugEnabled()) {
				//log.debug("ShipInspector.reviewShip("+s+"):"+s.shoreId+" to "+d.getDir()+" ("+((6+s.shoreId-d.getDir()) % 6)+")");
				//}
				return Collections.singletonList(new CriticizedError(s.getRegion(), s, this,
																	 "Ship cannot move to this direction!"));
			}
		}

		return Collections.EMPTY_LIST;
	}
}
