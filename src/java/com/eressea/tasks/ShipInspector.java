
package com.eressea.tasks;

import com.eressea.*;
import com.eressea.util.*;

import com.eressea.tasks.*;

import java.util.*;

/** 
 * A Inspector inspects the given resource (TODO: Unit, region or whole gamedata?) 
 * and returns a list of problems;
 */
public class ShipInspector extends AbstractInspector implements Inspector {
	public final static ShipInspector INSPECTOR = new ShipInspector();

	public static ShipInspector getInstance() {
		return INSPECTOR;
	}
	protected ShipInspector() {
	}

	public List reviewRegion(Region r, int type) {
		// we notify errors only
		if(type != Problem.ERROR) {
			return Collections.EMPTY_LIST;
		}
		
		// fail fast if prerequisites are not fulfilled
		if(r==null || r.units() == null || r.units().isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		
		// this inspector is only interested in ships
		if(r.ships() == null || r.ships().isEmpty()) {
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
		for(Iterator iter = r.ships().iterator(); iter.hasNext(); ) {
			Ship s = (Ship) iter.next();
			problems.addAll(reviewShip(r,s));
		}
		return problems;
	}
	
	private List reviewShip(Region r, Ship s) {
		if(s.modifiedUnits().isEmpty()) {
			return Collections.singletonList(new CriticizedError(r, s,this,"Ship has no crew!"));
		}
		if(s.getModifiedLoad() > s.getMaxCapacity()*100) {
			return Collections.singletonList(new CriticizedError(r, s,this,"Ship is overloaded!"));
		}
		return Collections.EMPTY_LIST;
	}
	
}
