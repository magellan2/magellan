package com.eressea.gamebinding;

import com.eressea.Unit;
import com.eressea.UnitContainer;

public interface OrderChanger {
	// for EMapDetailsPanel
	public void addCombatOrder(Unit unit, int newstate);

	// for EMapDetailsPanel
	public void addDescribeUnitContainerOrder(Unit unit, UnitContainer uc, String descr);

	// for EMapDetailsPanel
	public void addDescribeUnitOrder(Unit unit, String descr);

	// for EMapDetailsPanel
	public void addDescribeUnitPrivateOrder(Unit unit, String descr);

	// for EMapDetailsPanel
	public void addHideOrder(Unit unit, String level); 

	// for EMapDetailsPanel, MultiEditorOrderEditorList
	public void addNamingOrder(Unit unit, String name);

	// for EMapDetailsPanel
	public void addNamingOrder(Unit unit, UnitContainer uc, String name);

	//
	public void addRecruitOrder(Unit u,int amount);

	// for UnitContextMenu
	public void addMultipleHideOrder(Unit u);

	// for MultiEditorOrderEditorList
	// TODO!
	// public void addGiveSilverOrder(Unit u, 
}
