package com.eressea.gamebinding;

import com.eressea.Unit;
import com.eressea.UnitContainer;

public interface OrderChanger {
	public void addCombatOrder(Unit unit, int newstate);

	/**
	 * Adds a command line "DESCRIBE uc \"descr\"" ("BESCHREIBE uc \"descr\"") , 
	 * e.g. "DESCRIBE SHIP \"A wonderful small boat.\"" ("BESCHREIBE SCHIFF \"Ein wundervolles kleines Boot.\"")
	 * to the given unit. See EMapDetailsPanel.
	 */
	public void addDescribeUnitContainerOrder(Unit unit, UnitContainer uc, String descr);

	/**
	 * Adds a command line "DESCRIBE UNIT \"descr\"" ("BESCHREIBE EINHEIT \"descr\"") , 
	 * e.g. "DESCRIBE UNIT \"A wonderful sailor.\"" ("BESCHREIBE EINHEIT \"Ein wundervoller Segler.\"")
	 * to the given unit. See EMapDetailsPanel.
	 */
	public void addDescribeUnitOrder(Unit unit, String descr);

	/**
	 * Adds a command line "DESCRIBE PRIVATE \"descr\"" ("BESCHREIBE PRIVAT \"descr\"") , 
	 * e.g. "DESCRIBE PRIVATE \"My spy!.\"" ("BESCHREIBE PRIVAT \"Mein Spion!\"")
	 * to the given unit. See EMapDetailsPanel.
	 */
	public void addDescribeUnitPrivateOrder(Unit unit, String descr);

	/**
	 * Adds a command line "HIDE newstate" ("TARNE newstate") , e.g. "HIDE 3" ("TARNE 3")
	 * to the given unit. See EMapDetailsPanel.
	 */
	public void addHideOrder(Unit unit, String level); 

	/**
	 * Adds a command line "NAME UNIT \"name\"" ("BENENNE EINHEIT \"name\"") , 
	 * e.g. "NAME UNIT \"Magellan.\"" ("BENENNE EINHEIT \"Magellan.\"")
	 * to the given unit. See EMapDetailsPanel.
	 */
	public void addNamingOrder(Unit unit, String name);

	/**
	 * Adds a command line "NAME uc \"name\"" ("BENENNE uc \"name\"") , 
	 * e.g. "NAME SHIP \"Santa Barbara.\"" ("BENENNE SCHIFF \"Santa Barbara.\"")
	 * to the given unit. See EMapDetailsPanel.
	 */
	public void addNamingOrder(Unit unit, UnitContainer uc, String name);

	public void addRecruitOrder(Unit u,int amount);

	// for UnitContextMenu
	public void addMultipleHideOrder(Unit u);
}
