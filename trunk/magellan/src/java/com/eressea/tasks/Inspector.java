package com.eressea.tasks;

import java.util.List;

import com.eressea.Region;
import com.eressea.Unit;

/** 
 * An Inspector review the given resource (TODO: Unit, region or whole gamedata?) 
 * and returns a list of problems
 */
public interface Inspector {

	/**
	 * This Function is called to review a unit and returns a list of 
	 * <tt>Problem</tt> objects. It should generally call 
	 * reviewUnit(u,Problem.INFO), reviewUnit(u,Problem.WARNING)...
	 */
	public List reviewUnit(Unit u);

	/**
	 * This Function is called to review a unit and returns a list of 
	 * <tt>Problem</tt> objects.
     * @param u unit to review
	 * @param type the type of the review e.g. Problem.INFO
	 */
	public List reviewUnit(Unit u, int type);

	public List reviewRegion(Region r);
	public List reviewRegion(Region r, int type);

	// public List reviewGameData(GameData gd); 

}
