// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.eressea.*;
import com.eressea.util.*;
import com.eressea.util.comparator.*;

/**
 * To help constructing the tree structure.
 * @author  Andreas, Ulrich Küster
 */
public class TreeHelper {

	/**
	 * These are some constants used to encode the various
	 * criteria by which the units in the tree may be organized.
	 */
	public static final int FACTION = 0;
	public static final int GROUP = 1;
	public static final int COMBAT_STATUS = 2;
	public static final int HEALTH = 3;
	public static final int FACTION_DISGUISE_STATUS = 4;
	public static final int TRUSTLEVEL = 5;

	public final static Comparator buildingComparator = new BuildingTypeComparator(new NameComparator(new IDComparator()));
	public final static Comparator healthCmp = new UnitHealthComparator(null);

	/**
	 * Creates the subtree for one region with units (sorted by faction or other
	 * criteria), ships, buildings, borders etc.
	 */
	public static TreeNode createRegionNode(Region r, NodeWrapperFactory factory, Map activeAlliances, Map unitNodes, Map buildingNodes, Map shipNodes, Comparator unitSorting, int[] treeStructure, GameData data) {

		RegionNodeWrapper regionNodeWrapper = factory.createRegionNodeWrapper(r, 0);
		DefaultMutableTreeNode regionNode = new DefaultMutableTreeNode(regionNodeWrapper);
		DefaultMutableTreeNode node = null;

		java.util.List units = new LinkedList(r.units());

		Iterator it = null;

		if (units.size() > 0) {
			if (unitSorting != null) {
				Collections.sort(units, unitSorting);
			}
			int regionUnits = addSortedUnits(regionNode, treeStructure, 0, units, factory, activeAlliances, unitNodes, data);
		}

		// add ships
		it = r.ships().iterator();
		while (it.hasNext()) {
			Ship s = (Ship)it.next();
			node = new DefaultMutableTreeNode(factory.createUnitContainerNodeWrapper( s ));
			regionNode.add(node);
			if (shipNodes != null) {
				shipNodes.put(s.getID(), node);
			}
		}

		// add buildings
		java.util.List sortedBuildings = new LinkedList(r.buildings());
		Collections.sort(sortedBuildings, buildingComparator);
		it = sortedBuildings.iterator();
		while (it.hasNext()) {
			Building b = (Building)it.next();
			node = new DefaultMutableTreeNode(factory.createUnitContainerNodeWrapper( b ));
			regionNode.add(node);
			if (buildingNodes != null) {
				buildingNodes.put(b.getID(), node);
			}
		}

		// add borders
		it = r.borders().iterator();
		while(it.hasNext()) {
			com.eressea.Border b = (com.eressea.Border)it.next();
			node = new DefaultMutableTreeNode(factory.createBorderNodeWrapper(b));
			regionNode.add(node);
		}

		return regionNode;
	}

	/**
	 * This method assumes that the units are already sorted corresponding to treeStructure.
	 * (This is done in createRegionNode(...).)
	 * @return the number of persons (not units) that were added
	 */
	private static int addSortedUnits(DefaultMutableTreeNode mother, int[] treeStructure, int sortCriteria, List units, NodeWrapperFactory factory, Map activeAlliances, Map unitNodes, GameData data) {
		SupportsEmphasizing se = null;
		if (mother.getUserObject() instanceof SupportsEmphasizing) {
			se = (SupportsEmphasizing)mother.getUserObject();
		}

		int retVal = 0;
		Unit curUnit = null;
		Unit prevUnit = null;
		List helpList = new LinkedList();
		for (Iterator iter = units.iterator(); iter.hasNext(); ) {
			// ignore temp units
			// they are added under their mother unit
			Unit unit = (Unit)iter.next();
			if (unit instanceof TempUnit) {
				continue;
			}
			prevUnit = curUnit;
			curUnit = unit;

			if (sortCriteria >= treeStructure.length) {
				// all structuring has been done
				// simply add the units
				UnitNodeWrapper nodeWrapper = factory.createUnitNodeWrapper(curUnit, curUnit.persons);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeWrapper);
				if (unitNodes != null) {
					unitNodes.put(curUnit.getID(), node);
				}
				mother.add(node);
				if (se != null) {
					se.getSubordinatedElements().add(nodeWrapper);
				}
				retVal += curUnit.persons;
				// take care of temp units
				for (Iterator tempUnits = curUnit.tempUnits().iterator(); tempUnits.hasNext(); ) {
					Unit tempUnit = (Unit)tempUnits.next();
					UnitNodeWrapper tempNodeWrapper = factory.createUnitNodeWrapper(tempUnit, tempUnit.persons);
					DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(tempNodeWrapper);
					node.add(tempNode);
					nodeWrapper.getSubordinatedElements().add(tempNodeWrapper);
					if (unitNodes != null) {
						unitNodes.put(tempUnit.getID(), tempNode);
					}
				}
			} else {
				// change in current sortCriteria?
				switch (treeStructure[sortCriteria]) {
					case FACTION:
						if (change(FACTION, curUnit, prevUnit)) {
							FactionNodeWrapper factionNodeWrapper =factory.createFactionNodeWrapper(prevUnit.getFaction(), prevUnit.getRegion(), activeAlliances);
							DefaultMutableTreeNode factionNode = new DefaultMutableTreeNode(factionNodeWrapper);
							mother.add(factionNode);
							if (se != null) {
								se.getSubordinatedElements().add(factionNodeWrapper);
							}
							retVal += addSortedUnits(factionNode, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							helpList.clear();
						}
						break;
					case GROUP:
						if (change(GROUP, curUnit, prevUnit)) {
							// Do the units belong to a group?
							if (prevUnit.getGroup() != null) {
								GroupNodeWrapper groupNodeWrapper = factory.createGroupNodeWrapper(prevUnit.getGroup());
								DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupNodeWrapper);
								mother.add(groupNode);
								if (se != null) {
									se.getSubordinatedElements().add(groupNodeWrapper);
								}
								retVal += addSortedUnits(groupNode, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							} else {
								retVal += addSortedUnits(mother, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							}
							helpList.clear();
						}
						break;
					case HEALTH:
						if (change(HEALTH, curUnit, prevUnit)) {
							String verw  = data.getTranslation("verwundet");
							String sverw = data.getTranslation("schwer verwundet");
							String ersch = data.getTranslation("erschöpft");
							String hicon = "gesund";
							String text = prevUnit.health;
							if (text == null) {
								text = getString("healthy");
							} else if (text.equals(verw)) {
								hicon = "verwundet";
							} else if (text.equals(sverw)) {
								hicon = "schwerverwundet";
							} else if (text.equals(ersch)) {
								hicon = "erschoepft";
							}
							//parent.add(createSimpleNode(u.health,hicon));
							SimpleNodeWrapper simpleNodeWrapper = factory.createSimpleNodeWrapper(text, hicon);
							DefaultMutableTreeNode healthNode = new DefaultMutableTreeNode(simpleNodeWrapper);
							mother.add(healthNode);
							if (se != null) {
								se.getSubordinatedElements().add(simpleNodeWrapper);
							}
							retVal += addSortedUnits(healthNode, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							helpList.clear();
						}
						break;
					case COMBAT_STATUS:
						if (change(COMBAT_STATUS, curUnit, prevUnit)) {
							SimpleNodeWrapper simpleNodeWrapper = factory.createSimpleNodeWrapper(Unit.combatStatusToString(prevUnit), "kampfstatus");
							DefaultMutableTreeNode combatNode = new DefaultMutableTreeNode(simpleNodeWrapper);
							mother.add(combatNode);
							if (se != null) {
								se.getSubordinatedElements().add(simpleNodeWrapper);
							}
							retVal += addSortedUnits(combatNode, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							helpList.clear();
						}
						break;
					case FACTION_DISGUISE_STATUS:
						if (change(FACTION_DISGUISE_STATUS, curUnit, prevUnit)) {
							if (prevUnit.hideFaction) {
								SimpleNodeWrapper simpleNodeWrapper = factory.createSimpleNodeWrapper(getString("factiondisguised"),"tarnung");
								DefaultMutableTreeNode fdsNode = new DefaultMutableTreeNode(simpleNodeWrapper);
								mother.add(fdsNode);
								if (se != null) {
									se.getSubordinatedElements().add(simpleNodeWrapper);
								}
								retVal += addSortedUnits(fdsNode, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							} else {
								retVal += addSortedUnits(mother, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							}
							helpList.clear();
						}
						break;
					case TRUSTLEVEL:
						if (change(TRUSTLEVEL, curUnit, prevUnit)) {
							SimpleNodeWrapper simpleNodeWrapper = factory.createSimpleNodeWrapper(FactionTrustComparator.getTrustLevelLabel(prevUnit.getFaction().trustLevel),null);
							DefaultMutableTreeNode trustlevelNode = new DefaultMutableTreeNode(simpleNodeWrapper);
							mother.add(trustlevelNode);
							if (se != null) {
								se.getSubordinatedElements().add(simpleNodeWrapper);
							}
							retVal += addSortedUnits(trustlevelNode, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
							helpList.clear();
						}
						break;
				} // end of switch
				helpList.add(curUnit);
			}
		} // end of unit iterator
		// take care of all units that are left
		if (!helpList.isEmpty()) {
			DefaultMutableTreeNode node = null;
			if (sortCriteria <= treeStructure.length) {
				switch (treeStructure[sortCriteria]) {
					case FACTION:
						node = new DefaultMutableTreeNode(factory.createFactionNodeWrapper(curUnit.getFaction(), curUnit.getRegion(), activeAlliances));
						break;
					case GROUP:
						if (curUnit.getGroup() != null) {
							node = new DefaultMutableTreeNode(factory.createGroupNodeWrapper(curUnit.getGroup()));
						} else {
							node = null;
						}
						break;
					case HEALTH:
						String verw  = data.getTranslation("verwundet");
						String sverw = data.getTranslation("schwer verwundet");
						String ersch = data.getTranslation("erschöpft");
						String hicon = "gesund";
						String text = curUnit.health;
						if (text == null) {
							text = getString("healthy");
						} else if (text.equals(verw)) {
							hicon = "verwundet";
						} else if (text.equals(sverw)) {
							hicon = "schwerverwundet";
						} else if (text.equals(ersch)) {
							hicon = "erschoepft";
						}
						node = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(text, hicon));
						break;
					case COMBAT_STATUS:
						Object o = factory.createSimpleNodeWrapper(Unit.combatStatusToString(curUnit), "kampfstatus");
						node = new DefaultMutableTreeNode(o);
						break;
					case FACTION_DISGUISE_STATUS:
						if (curUnit.hideFaction) {
							o = factory.createSimpleNodeWrapper(getString("factiondisguised"),"tarnung");
							node = new DefaultMutableTreeNode(o);
						} else {
							node = null;
						}
						break;
					case TRUSTLEVEL:
						o = factory.createSimpleNodeWrapper(FactionTrustComparator.getTrustLevelLabel(curUnit.getFaction().trustLevel),null);
						node = new DefaultMutableTreeNode(o);
						break;
				} // end of switch
			} // end of if (sortCriteria <= treeStructure.length)
			// now add units
			if (node == null) {
				retVal += addSortedUnits(mother, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
			} else {
				mother.add(node);
				if (se != null && node.getUserObject() instanceof SupportsEmphasizing) {
					se.getSubordinatedElements().add(node.getUserObject());
				}
				retVal += addSortedUnits(node, treeStructure, sortCriteria + 1, helpList, factory, activeAlliances, unitNodes, data);
			}
		}
		// now add number of persons to node
		Object user = mother.getUserObject();
		if (user instanceof RegionNodeWrapper) {
			((RegionNodeWrapper)user).setAmount(retVal);
		} else if (user instanceof FactionNodeWrapper) {
			((FactionNodeWrapper)user).setAmount(retVal);
		} else if (user instanceof GroupNodeWrapper) {
			((GroupNodeWrapper)user).setAmount(retVal);
		} else if (user instanceof SimpleNodeWrapper) {
			((SimpleNodeWrapper)user).setAmount(retVal);
		} else {
			System.out.println("TreeHelper.addSortedUnits(): unknown user object.");
		}
		return retVal;
	}

	/**
	 * Little helper function that determines, whether the two
	 * given units differ in regard to the given flag.
	 * The flag should be given according to the constants
	 * defined in this class (FACTION, GROUP, ...)
	 * If one of the unit arguments is null, false is returned.
	 */
	private static boolean change(int flag, Unit curUnit, Unit prevUnit) {
		if (curUnit == null || prevUnit == null) {
			return false;
		}
		switch(flag) {
			case FACTION:
				ID prevUnitFactionID = null;
				if (prevUnit.getFaction() != null) {
					prevUnitFactionID = prevUnit.getFaction().getID();
				}
				ID curUnitFactionID = null;
				if (curUnit.getFaction() != null) {
					curUnitFactionID = curUnit.getFaction().getID();
				}
				if (curUnitFactionID == null && prevUnitFactionID == null) {
					return false;
				} else if (curUnitFactionID == null || prevUnitFactionID == null) {
					return true;
				} else {
					return !curUnitFactionID.equals(prevUnitFactionID);
				}
			case GROUP:
				ID prevUnitGroupID = null;
				if (prevUnit.getGroup() != null) {
					prevUnitGroupID = prevUnit.getGroup().getID();
				}
				ID curUnitGroupID = null;
				if (curUnit.getGroup() != null) {
					curUnitGroupID = curUnit.getGroup().getID();
				}
				if (curUnitGroupID == null && prevUnitGroupID == null) {
					return false;
				} else if (curUnitGroupID == null || prevUnitGroupID == null) {
					return true;
				} else {
					return !curUnitGroupID.equals(prevUnitGroupID);
				}
			case HEALTH:
				return healthCmp.compare(prevUnit, curUnit) != 0;
			case COMBAT_STATUS:
				return prevUnit.combatStatus != curUnit.combatStatus;
			case FACTION_DISGUISE_STATUS:
				return prevUnit.hideFaction != curUnit.hideFaction;
			case TRUSTLEVEL: 
				return UnitTrustComparator.DEFAULT_COMPARATOR.compare(prevUnit,curUnit) != 0;
		}
		return false; // default
	}


	protected static String getString(String key) {
		return com.eressea.util.Translations.getTranslation(TreeHelper.class,key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("healthy" , "healthy");
			defaultTranslations.put("factiondisguised", "Faction disguised");
		}
		return defaultTranslations;
	}

}
