package com.eressea.gamebinding;

import java.util.List;

import com.eressea.Unit;

public interface TempUnitFactory {

	/**
	 * Returns the orders necessary to issue the creation of all the
	 * child temp units of this unit.
	 */	
	public List getTempOrders(Unit unit);

	/** 
	 * 
	 */
	public List extractTempUnits(Unit unit);
}
