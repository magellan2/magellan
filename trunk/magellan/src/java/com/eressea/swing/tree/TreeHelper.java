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

package com.eressea.swing.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import com.eressea.Border;
import com.eressea.Building;
import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.TempUnit;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;
import com.eressea.util.comparator.BuildingTypeComparator;
import com.eressea.util.comparator.FactionTrustComparator;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.NameComparator;
import com.eressea.util.comparator.TaggableComparator;
import com.eressea.util.comparator.UnitCombatStatusComparator;
import com.eressea.util.comparator.UnitFactionComparator;
import com.eressea.util.comparator.UnitFactionDisguisedComparator;
import com.eressea.util.comparator.UnitGroupComparator;
import com.eressea.util.comparator.UnitHealthComparator;
import com.eressea.util.comparator.UnitTempUnitComparator;
import com.eressea.util.comparator.UnitTrustComparator;
import com.eressea.util.comparator.tree.GroupingComparator;

/**
 * To help constructing the tree structure.
 *
 * @author Andreas, Ulrich Küster
 */
public class TreeHelper {
	/**
	 * These are some constants used to encode the various criteria by which the units in the tree
	 * may be organized.
	 */
	public static final int FACTION = 0;

	/** TODO: DOCUMENT ME! */
	public static final int GROUP = 1;

	/** TODO: DOCUMENT ME! */
	public static final int COMBAT_STATUS = 2;

	/** TODO: DOCUMENT ME! */
	public static final int HEALTH = 3;

	/** TODO: DOCUMENT ME! */
	public static final int FACTION_DISGUISE_STATUS = 4;

	/** TODO: DOCUMENT ME! */
	public static final int TRUSTLEVEL = 5;

	/** TODO: DOCUMENT ME! */
	public static final int TAGGABLE  = 6;
    public static final int TAGGABLE2 = 7;
    public static final int TAGGABLE3 = 8;
    public static final int TAGGABLE4 = 9;
    public static final int TAGGABLE5 = 10;
    
    public static final String TAGGABLE_STRING = "ejcTaggableComparator";
    public static final String TAGGABLE_STRING2 = "ejcTaggableComparator2";
    public static final String TAGGABLE_STRING3 = "ejcTaggableComparator3";
    public static final String TAGGABLE_STRING4 = "ejcTaggableComparator4";
    public static final String TAGGABLE_STRING5 = "ejcTaggableComparator5";
    
	private static final Comparator nameComparator = new NameComparator(IDComparator.DEFAULT);
	private static final Comparator buildingCmp = new BuildingTypeComparator(new NameComparator(IDComparator.DEFAULT));
	private static final Comparator shipComparator = nameComparator;
    private static final Comparator healthCmp = new UnitHealthComparator(null);

    // pavkovic 2004.01.04: we dont want to sort groups by group id but name;
    // if they are sorted by id this would make tree hierarchy
    // (trustlevel, group) somehow uninteresting
    // Side effect: Groups are sorted by name
    private static final Comparator groupCmp = new UnitGroupComparator(new NameComparator(null), null, null);

    private static final Comparator factionDisguisedCmp = new UnitFactionDisguisedComparator(null);
    
    private static final Comparator combatCmp = new UnitCombatStatusComparator(null);
    private static final Comparator factionCmp = new UnitFactionComparator(new FactionTrustComparator(NameComparator.DEFAULT), null);
    private static final Comparator trustlevelCmp = UnitTrustComparator.DEFAULT_COMPARATOR;
    private static final Comparator taggableCmp  = new TaggableComparator(TAGGABLE_STRING,  null);
    private static final Comparator taggableCmp2 = new TaggableComparator(TAGGABLE_STRING2, null);
    private static final Comparator taggableCmp3 = new TaggableComparator(TAGGABLE_STRING3, null);
    private static final Comparator taggableCmp4 = new TaggableComparator(TAGGABLE_STRING4, null);
    private static final Comparator taggableCmp5 = new TaggableComparator(TAGGABLE_STRING5, null);

    /**
	 * Creates the subtree for one region with units (sorted by faction or other criteria), ships,
	 * buildings, borders etc.
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param factory TODO: DOCUMENT ME!
	 * @param activeAlliances TODO: DOCUMENT ME!
	 * @param unitNodes TODO: DOCUMENT ME!
	 * @param buildingNodes TODO: DOCUMENT ME!
	 * @param shipNodes TODO: DOCUMENT ME!
	 * @param unitSorting TODO: DOCUMENT ME!
	 * @param treeStructure TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public TreeNode createRegionNode(Region r, NodeWrapperFactory factory,
											Map activeAlliances, Map unitNodes, Map buildingNodes,
											Map shipNodes, Comparator unitSorting,
											int treeStructure[], GameData data, 
											boolean sortUnderUnitParent) {
		RegionNodeWrapper regionNodeWrapper = factory.createRegionNodeWrapper(r, 0);
		DefaultMutableTreeNode regionNode = new DefaultMutableTreeNode(regionNodeWrapper);
		DefaultMutableTreeNode node = null;

		List units = new ArrayList(r.units());

		if(units.size() > 0) {
			if(unitSorting != null) {
				Collections.sort(units, unitSorting);
			}
			
			addUnits(regionNode, treeStructure, 0, units, factory, activeAlliances,
						   unitNodes, data);
		}

                
		// add ships
		List sortedShips = new ArrayList(r.ships());
		Collections.sort(sortedShips, shipComparator);
		for(Iterator iter = sortedShips.iterator(); iter.hasNext(); ) {
			Ship s = (Ship) iter.next();
            if(shipNodes == null || !shipNodes.containsKey(s.getID())) {
                node = new DefaultMutableTreeNode(factory.createUnitContainerNodeWrapper(s));
                if(sortUnderUnitParent && s.getOwnerUnit() != null) {
                    DefaultMutableTreeNode unitNode = (DefaultMutableTreeNode) 
                        unitNodes.get(s.getOwnerUnit().getID());
                    ((DefaultMutableTreeNode) unitNode.getParent()).add(node);
                } else {
                    regionNode.add(node);                    
                }
			
                if(shipNodes != null) {
                    shipNodes.put(s.getID(), node);
                }
            }
		}

		// add buildings
		List sortedBuildings = new ArrayList(r.buildings());
		Collections.sort(sortedBuildings, buildingCmp);

		for(Iterator iter = sortedBuildings.iterator(); iter.hasNext(); ) {
			Building b = (Building) iter.next();
			node = new DefaultMutableTreeNode(factory.createUnitContainerNodeWrapper(b));
			regionNode.add(node);

			if(buildingNodes != null) {
				buildingNodes.put(b.getID(), node);
			}
		}

		// add borders
		for(Iterator iter = r.borders().iterator(); iter.hasNext(); ) {
			Border b = (Border) iter.next();
			node = new DefaultMutableTreeNode(factory.createBorderNodeWrapper(b));
			regionNode.add(node);
		}

		return regionNode;
	}

	/**
	 * This method assumes that the units are already sorted corresponding to treeStructure. (This
	 * is done in createRegionNode(...).)
	 *
	 * @param mother TODO: DOCUMENT ME!
	 * @param treeStructure TODO: DOCUMENT ME!
	 * @param sortCriteria TODO: DOCUMENT ME!
	 * @param units TODO: DOCUMENT ME!
	 * @param factory TODO: DOCUMENT ME!
	 * @param activeAlliances TODO: DOCUMENT ME!
	 * @param unitNodes TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @return the number of persons (not units) that were added
	 */
	private int addUnits(DefaultMutableTreeNode mother, int treeStructure[],
	        int sortCriteria, List units, NodeWrapperFactory factory,
	        Map activeAlliances, Map unitNodes, GameData data) {
		SupportsEmphasizing se = null;

		if(mother.getUserObject() instanceof SupportsEmphasizing) {
			se = (SupportsEmphasizing) mother.getUserObject();
		}

		int retVal = 0;
		Unit curUnit = null;
		Unit prevUnit = null;
		List helpList = new ArrayList();

		for(Iterator iter = units.iterator(); iter.hasNext();) {
			Unit unit = (Unit) iter.next();

			// ignore temp units
			// they are added under their mother unit
			if(unit instanceof TempUnit) {
				continue;
			}

			prevUnit = curUnit;
			curUnit = unit;

			if(sortCriteria >= treeStructure.length) {
				// all structuring has been done
				// simply add the units
				UnitNodeWrapper nodeWrapper = factory.createUnitNodeWrapper(curUnit, curUnit.persons);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeWrapper);

				if(unitNodes != null) {
					unitNodes.put(curUnit.getID(), node);
				}

				mother.add(node);

				if(se != null) {
					se.getSubordinatedElements().add(nodeWrapper);
				}

				retVal += curUnit.persons;

				// take care of temp units
				for(Iterator tempUnits = curUnit.tempUnits().iterator(); tempUnits.hasNext();) {
					Unit tempUnit = (Unit) tempUnits.next();
					UnitNodeWrapper tempNodeWrapper = factory.createUnitNodeWrapper(tempUnit,
																					tempUnit.persons);
					DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(tempNodeWrapper);
					node.add(tempNode);
					nodeWrapper.getSubordinatedElements().add(tempNodeWrapper);

					if(unitNodes != null) {
						unitNodes.put(tempUnit.getID(), tempNode);
					}
				}
                
                /*
                if(curUnit.getShip() != null){
                    Ship s = curUnit.getShip();
                    
                    // also add ships under parent of the current unit
                    node = new DefaultMutableTreeNode(factory.createUnitContainerNodeWrapper(s));
                    mother.add(node);
            
                    if(shipNodes != null) {
                        shipNodes.put(s.getID(), node);
                    }
                }
                */
                
			} else {
                if(change(treeStructure[sortCriteria], curUnit, prevUnit)) { 
                    // change in current sortCriteria?
                    switch(treeStructure[sortCriteria]) {
                    case FACTION:
                        FactionNodeWrapper factionNodeWrapper = factory.createFactionNodeWrapper(prevUnit.getFaction(),
																								 prevUnit.getRegion(),
																								 activeAlliances);
						DefaultMutableTreeNode factionNode = new DefaultMutableTreeNode(factionNodeWrapper);
						mother.add(factionNode);

						if(se != null) {
							se.getSubordinatedElements().add(factionNodeWrapper);
						}

						retVal += addUnits(factionNode, treeStructure, sortCriteria + 1,
												 helpList, factory, activeAlliances, unitNodes, data);
						helpList.clear();

						break;

				case GROUP:
						// Do the units belong to a group?
						if(prevUnit.getGroup() != null) {
							GroupNodeWrapper groupNodeWrapper = factory.createGroupNodeWrapper(prevUnit.getGroup());
							SimpleNodeWrapper simpleGroupNodeWrapper = factory.createSimpleNodeWrapper(groupNodeWrapper, "groups");
							// DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(groupNodeWrapper, "groups"));
							// DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupNodeWrapper);
							DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(simpleGroupNodeWrapper);
							mother.add(groupNode);

							if(se != null) {
								se.getSubordinatedElements().add(simpleGroupNodeWrapper);
							}

							retVal += addUnits(groupNode, treeStructure, sortCriteria + 1,
													 helpList, factory, activeAlliances, unitNodes,
													 data);
						} else {
							retVal += addUnits(mother, treeStructure, sortCriteria + 1,
													 helpList, factory, activeAlliances, unitNodes,
													 data);
						}

						helpList.clear();

						break;

				case HEALTH:
						String verw = data.getTranslation("verwundet");
						String sverw = data.getTranslation("schwer verwundet");
						String ersch = data.getTranslation("erschöpft");
						String hicon = "gesund";
						String text = prevUnit.health;

						if(text == null) {
							text = getString("healthy");
						} else if(text.equals(verw)) {
							hicon = "verwundet";
						} else if(text.equals(sverw)) {
							hicon = "schwerverwundet";
						} else if(text.equals(ersch)) {
							hicon = "erschoepft";
						}

						//parent.add(createSimpleNode(u.health,hicon));
						SimpleNodeWrapper healthNodeWrapper = factory.createSimpleNodeWrapper(text,
																							  hicon);
						DefaultMutableTreeNode healthNode = new DefaultMutableTreeNode(healthNodeWrapper);
						mother.add(healthNode);

						if(se != null) {
							se.getSubordinatedElements().add(healthNodeWrapper);
						}

						retVal += addUnits(healthNode, treeStructure, sortCriteria + 1,
												 helpList, factory, activeAlliances, unitNodes, data);
						helpList.clear();

						break;

				case COMBAT_STATUS:
						SimpleNodeWrapper combatNodeWrapper = factory.createSimpleNodeWrapper(Unit.combatStatusToString(prevUnit),
																							  "kampfstatus");
						DefaultMutableTreeNode combatNode = new DefaultMutableTreeNode(combatNodeWrapper);
						mother.add(combatNode);

						if(se != null) {
							se.getSubordinatedElements().add(combatNodeWrapper);
						}

						retVal += addUnits(combatNode, treeStructure, sortCriteria + 1,
												 helpList, factory, activeAlliances, unitNodes, data);
						helpList.clear();

                        break;

				case FACTION_DISGUISE_STATUS:
						if(prevUnit.hideFaction) {
							SimpleNodeWrapper fdsNodeWrapper = factory.createSimpleNodeWrapper(getString("factiondisguised"),
																								  "tarnung");
							DefaultMutableTreeNode fdsNode = new DefaultMutableTreeNode(fdsNodeWrapper);
							mother.add(fdsNode);

							if(se != null) {
								se.getSubordinatedElements().add(fdsNodeWrapper);
							}

							retVal += addUnits(fdsNode, treeStructure, sortCriteria + 1,
													 helpList, factory, activeAlliances, unitNodes,
													 data);
						} else {
							retVal += addUnits(mother, treeStructure, sortCriteria + 1,
													 helpList, factory, activeAlliances, unitNodes,
													 data);
						}

						helpList.clear();

						break;

				case TRUSTLEVEL:
						SimpleNodeWrapper trustlevelNodeWrapper = factory.createSimpleNodeWrapper(FactionTrustComparator.getTrustLevelLabel(prevUnit.getFaction().trustLevel),
																							  null);
						DefaultMutableTreeNode trustlevelNode = new DefaultMutableTreeNode(trustlevelNodeWrapper);
						mother.add(trustlevelNode);

						if(se != null) {
							se.getSubordinatedElements().add(trustlevelNodeWrapper);
						}

						retVal += addUnits(trustlevelNode, treeStructure, sortCriteria + 1,
												 helpList, factory, activeAlliances, unitNodes, data);
						helpList.clear();
                        
						break;
					
				case TAGGABLE:
                case TAGGABLE2:
                case TAGGABLE3:
                case TAGGABLE4:
                case TAGGABLE5:
						String label = getTaggableLabel(prevUnit,treeStructure[sortCriteria]);
						if(label != null) {
							SimpleNodeWrapper simpleNodeWrapper = factory.createSimpleNodeWrapper(label,null);
							DefaultMutableTreeNode taggableNode = new DefaultMutableTreeNode(simpleNodeWrapper);
							mother.add(taggableNode);

							if(se != null) {
								se.getSubordinatedElements().add(simpleNodeWrapper);
							}
						
							retVal += addUnits(taggableNode, treeStructure, sortCriteria + 1,
													 helpList, factory, activeAlliances, unitNodes, data);
						} else {
							retVal += addUnits(mother, treeStructure, sortCriteria + 1,
													 helpList, factory, activeAlliances, unitNodes,
													 data);
						}
						helpList.clear();

						break;
                    } // end of switch
                }
                
				helpList.add(curUnit);
			}
		}

		// end of unit iterator
		// take care of all units that are left
		if(!helpList.isEmpty()) {
			DefaultMutableTreeNode node = null;

			if(sortCriteria <= treeStructure.length) {
				switch(treeStructure[sortCriteria]) {
				case FACTION:
					node = new DefaultMutableTreeNode(factory.createFactionNodeWrapper(curUnit.getFaction(),
																					   curUnit.getRegion(),
																					   activeAlliances));

					break;

				case GROUP:

					if(curUnit.getGroup() != null) {
						// node = new DefaultMutableTreeNode(factory.createGroupNodeWrapper(curUnit.getGroup()));
						GroupNodeWrapper groupNodeWrapper = factory.createGroupNodeWrapper(curUnit.getGroup());
						node = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(groupNodeWrapper, "groups"));
					} else {
						node = null;
					}

					break;

				case HEALTH:

					String verw = data.getTranslation("verwundet");
					String sverw = data.getTranslation("schwer verwundet");
					String ersch = data.getTranslation("erschöpft");
					String hicon = "gesund";
					String text = curUnit.health;

					if(text == null) {
						text = getString("healthy");
					} else if(text.equals(verw)) {
						hicon = "verwundet";
					} else if(text.equals(sverw)) {
						hicon = "schwerverwundet";
					} else if(text.equals(ersch)) {
						hicon = "erschoepft";
					}

					node = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(text, hicon));

					break;

				case COMBAT_STATUS:

					node = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(Unit.combatStatusToString(curUnit),"kampfstatus"));

					break;

				case FACTION_DISGUISE_STATUS:

					if(curUnit.hideFaction) {
						node = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(getString("factiondisguised"), "tarnung"));
					} else {
						node = null;
					}

					break;

				case TRUSTLEVEL:
					node = new DefaultMutableTreeNode(
                            factory.createSimpleNodeWrapper(FactionTrustComparator.getTrustLevelLabel(curUnit.getFaction().trustLevel), null));

					break;
                    
                case TAGGABLE:
                case TAGGABLE2:
                case TAGGABLE3:
                case TAGGABLE4:
                case TAGGABLE5:

                    String label = getTaggableLabel(curUnit, treeStructure[sortCriteria]);
                    if(label != null) {
                        node = new DefaultMutableTreeNode(factory.createSimpleNodeWrapper(label,null));
                    } else {
                        node = null;
                    }

                    break;

				} // end of switch
			}

			// end of if (sortCriteria <= treeStructure.length)
			// now add units
			if(node == null) {
				retVal += addUnits(mother, treeStructure, sortCriteria + 1, helpList,
										 factory, activeAlliances, unitNodes, data);
			} else {
				mother.add(node);

				if((se != null) && node.getUserObject() instanceof SupportsEmphasizing) {
					se.getSubordinatedElements().add(node.getUserObject());
				}

				retVal += addUnits(node, treeStructure, sortCriteria + 1, helpList, factory,
										 activeAlliances, unitNodes, data);
			}
		}

		// now add number of persons to node
		Object user = mother.getUserObject();

		if(user instanceof RegionNodeWrapper) {
			((RegionNodeWrapper) user).setAmount(retVal);
		} else if(user instanceof FactionNodeWrapper) {
			((FactionNodeWrapper) user).setAmount(retVal);
		} else if(user instanceof GroupNodeWrapper) {
			((GroupNodeWrapper) user).setAmount(retVal);
		} else if(user instanceof SimpleNodeWrapper) {
			((SimpleNodeWrapper) user).setAmount(retVal);
		} else {
			System.out.println("TreeHelper.addSortedUnits(): unknown user object.");
		}

		return retVal;
	}

    private String getTaggableLabel(Unit unit, int taggable) {
        String tagName = null;
        switch(taggable) {
        case TAGGABLE: 
            tagName = TAGGABLE_STRING;
            break;
        case TAGGABLE2: 
            tagName = TAGGABLE_STRING2;
            break;
        case TAGGABLE3: 
            tagName = TAGGABLE_STRING3;
            break;
        case TAGGABLE4: 
            tagName = TAGGABLE_STRING4;
            break;
        case TAGGABLE5: 
            tagName = TAGGABLE_STRING5;
            break;
        }
        return tagName == null || unit == null? null : unit.getTag(tagName);
    }
    
	/**
	 * Little helper function that determines, whether the two given units differ in regard to the
	 * given flag. The flag should be given according to the constants defined in this class
	 * (FACTION, GROUP, ...) If one of the unit arguments is null, false is returned.
	 *
	 * @param flag TODO: DOCUMENT ME!
	 * @param curUnit TODO: DOCUMENT ME!
	 * @param prevUnit TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private boolean change(int flag, Unit curUnit, Unit prevUnit) {
		if((curUnit == null) || (prevUnit == null)) {
			return false;
		}

		switch(flag) {
		case FACTION:
		    return factionCmp.compare(prevUnit, curUnit) != 0;

		case GROUP:
            return groupCmp.compare(prevUnit, curUnit) != 0;
            
		case HEALTH:
			return healthCmp.compare(prevUnit, curUnit) != 0;

		case COMBAT_STATUS:
			return combatCmp.compare(prevUnit,curUnit) != 0;

		case FACTION_DISGUISE_STATUS:
			return factionDisguisedCmp.compare(prevUnit,curUnit) != 0;

		case TRUSTLEVEL:
			return trustlevelCmp.compare(prevUnit,curUnit) != 0;

		case TAGGABLE:
			return taggableCmp.compare(prevUnit, curUnit) != 0;
        case TAGGABLE2:
            return taggableCmp2.compare(prevUnit, curUnit) != 0;
        case TAGGABLE3:
            return taggableCmp3.compare(prevUnit, curUnit) != 0;
        case TAGGABLE4:
            return taggableCmp4.compare(prevUnit, curUnit) != 0;
        case TAGGABLE5:
            return taggableCmp5.compare(prevUnit, curUnit) != 0;
		}

		return false; // default
	}
    
    public static Comparator buildComparator(Comparator cmp, int[] treeStructure) {
        // now build the Comparator used for unit sorting
        GroupingComparator comp = new GroupingComparator(cmp,null);

        for(int i = treeStructure.length - 1; i >= 0; i--) {
            switch(treeStructure[i]) {
            case TreeHelper.FACTION:
                comp = new GroupingComparator(factionCmp,comp);
                break;

            case TreeHelper.GROUP:

                // pavkovic 2004.01.04: we dont want to sort groups by group id but name;
                // if they are sorted by id this would make tree hierarchy
                // (trustlevel, group) somehow uninteresting
                // Side effect: Groups are sorted by name
                comp = new GroupingComparator(groupCmp, comp);
                break;

            case TreeHelper.COMBAT_STATUS:
                comp = new GroupingComparator(combatCmp, comp);
                break;

            case TreeHelper.HEALTH:
                comp = new GroupingComparator(healthCmp, comp);
                break;

            case TreeHelper.FACTION_DISGUISE_STATUS:
                comp = new GroupingComparator(factionDisguisedCmp, comp);
                break;

            case TreeHelper.TRUSTLEVEL:
                comp = new GroupingComparator(trustlevelCmp, comp);
                break;

            case TreeHelper.TAGGABLE:
                comp = new GroupingComparator(taggableCmp,  comp);
                break;
            case TreeHelper.TAGGABLE2:
                comp = new GroupingComparator(taggableCmp2, comp);
                break;
            case TreeHelper.TAGGABLE3:
                comp = new GroupingComparator(taggableCmp3, comp);
                break;
            case TreeHelper.TAGGABLE4:
                comp = new GroupingComparator(taggableCmp4, comp);
                break;
            case TreeHelper.TAGGABLE5:
                comp = new GroupingComparator(taggableCmp5, comp);
                break;
            }
        }

        // care for temp units
        return new UnitTempUnitComparator(IDComparator.DEFAULT, comp);
    }
    
    

	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("healthy", "healthy");
			defaultTranslations.put("factiondisguised", "Faction disguised");
		}

		return defaultTranslations;
	}
}
