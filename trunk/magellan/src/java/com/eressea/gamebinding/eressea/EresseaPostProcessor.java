package com.eressea.gamebinding.eressea;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eressea.Building;
import com.eressea.Coordinate;
import com.eressea.EntityID;
import com.eressea.Faction;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.IntegerID;
import com.eressea.LongID;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.RegionResource;
import com.eressea.Ship;
import com.eressea.Skill;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.rules.BuildingType;
import com.eressea.rules.ItemType;
import com.eressea.rules.RegionType;
import com.eressea.util.CollectionFactory;
import com.eressea.util.Regions;
import com.eressea.util.comparator.IDComparator;
import com.eressea.util.comparator.SortIndexComparator;

public class EresseaPostProcessor {

	private EresseaPostProcessor() {
	}

	private final static EresseaPostProcessor singleton= new EresseaPostProcessor();
	public static EresseaPostProcessor getSingleton() {
		return singleton;
	}

	public void postProcess(GameData data) {
		/* scan the messages for additional information */
		if (data != null && data.factions() != null) {
			for (Iterator factions = data.factions().values().iterator(); factions.hasNext(); ) {
				Faction f = (Faction)factions.next();
				if (f.messages != null) {
					for (Iterator iter = f.messages.iterator(); iter.hasNext(); ) {
						Message m = (Message)iter.next();
						if (m.getMessageType() != null) {
							switch ((((IntegerID)m.getMessageType().getID()).intValue())) {
							case 1511758069:
							case 18362:
								// a herb was found in a region
							case 1349776898:
								// a certain amount of herbs has been detected in a region
								if (m.attributes != null && m.attributes.containsKey("region")) {
									String str = (String) m.attributes.get("region");
									ID coord = Coordinate.parse(str, ",");
									if (coord == null) {
										coord = Coordinate.parse(str, " ");
									}
									Region r = data.getRegion(coord);
									if (r != null) {
										String value =  (String)m.attributes.get("herb");
										if (value != null) {
											ItemType type = data.rules.getItemType(StringID.create(value), true);
											r.herb = type;
										}
										if((((IntegerID)m.getMessageType().getID()).intValue()) == 1349776898) {
											// a certain amount of herbs has been detected in a region
											String amount = (String) m.attributes.get("amount");
											if (amount != null) {
												r.herbAmount = amount;
											}
										}
									}
								}
								break;
							}
						}
					}
				}
			}
		}

		// there can be dummy units (UnitContainer owners and such), find and remove these
		if (data.units() != null) {
			Collection dummyUnitIDs = CollectionFactory.createLinkedList();
			for (Iterator iter = data.units().values().iterator(); iter.hasNext(); ) {
				Unit unit = (Unit)iter.next();
				if (unit.getName() == null) {
					dummyUnitIDs.add(unit.getID());
				}
			}
			for (Iterator iter = dummyUnitIDs.iterator(); iter.hasNext(); ) {
				data.units().remove(iter.next());
			}
		}

		/* retrieve the temp units mentioned in the orders and
		 create them as TempUnit objects */
		int sortIndex = 0;
		List sortedUnits = CollectionFactory.createLinkedList(data.units().values());
		Collections.sort(sortedUnits, new SortIndexComparator(new IDComparator()));
		for (Iterator unitIter = sortedUnits.iterator(); unitIter.hasNext(); ) {
			Unit unit = (Unit)unitIter.next();
			unit.setSortIndex(sortIndex++);
			sortIndex = unit.extractTempUnits(sortIndex);
		}

		/* 'known' information does not necessarily show up in the
		report. e.g. depleted region resources are not mentioned
		although we actually know that the resource is available with
		an amount of 0. Resolve this ambiguity here: */
		if (data != null && data.regions() != null) {
			ID sproutResourceID = StringID.create("Schößlinge");
			ID treeResourceID = StringID.create("Bäume");
			ID mallornSproutResourceID = StringID.create("Mallornschößlinge");
			ID mallornTreeResourceID = StringID.create("Mallorn");
			for (Iterator regionIter = data.regions().values().iterator(); regionIter.hasNext(); ) {
				Region region = (Region)regionIter.next();
				/* first determine whether we know everything about
				this region */
				if (!region.units().isEmpty()) {
					/* now patch as much missing information as
					possible */
					if (region.horses < 0) {
						region.horses = 0;
					}
					if (region.peasants < 0) {
						region.peasants = 0;
					}
					if (region.silver < 0) {
						region.silver = 0;
					}
					if (region.sprouts < 0) {
						region.sprouts = 0;
					}
					if (data.rules != null) {
						// 2002.05.21 pavkovic:
						// first of all: Remove resource information for sprouts, trees,
						// mallornsprouts and mallorntrees!
						// this is cumbersome, and will only stay for some time (two months)
						// to get rid of double or triple entries of these resources
						//
						boolean cleanup = true;
						if(cleanup) {
							Set cleanupSet = CollectionFactory.createHashSet();
							for(Iterator riter = region.resources().iterator();riter.hasNext(); ) {
								RegionResource rr =(RegionResource) riter.next();
								if(rr.getID().equals(sproutResourceID) ||
								   rr.getID().equals(treeResourceID) ||
								   rr.getID().equals(mallornSproutResourceID) ||
								   rr.getID().equals(mallornTreeResourceID) ||
								   rr.getType().getID().equals(sproutResourceID) ||
								   rr.getType().getID().equals(treeResourceID) ||
								   rr.getType().getID().equals(mallornSproutResourceID) ||
								   rr.getType().getID().equals(mallornTreeResourceID)) {
									cleanupSet.add(rr.getID());
									cleanupSet.add(rr.getType().getID());
								}
							}
							for(Iterator riter = cleanupSet.iterator(); riter.hasNext(); ) {
								ID id = (ID) riter.next();
								region.removeResource(id);
							}
						}
						if(region.mallorn) {
							// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
							region.removeResource(sproutResourceID);
							// add new resource
							if(region.getResource(mallornSproutResourceID) == null) {
								RegionResource res = new RegionResource(LongID.create(mallornSproutResourceID.hashCode()), data.rules.getItemType(mallornSproutResourceID, true));
								res.setAmount(region.sprouts);
								region.addResource(res);
							}
						} else {
							// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
							region.removeResource(mallornSproutResourceID);
							// add new resource
							if (region.getResource(sproutResourceID) == null) {
								RegionResource res = new RegionResource(LongID.create(sproutResourceID.hashCode()), data.rules.getItemType(sproutResourceID, true));
								res.setAmount(region.sprouts);
								region.addResource(res);
							}
						}
					}
					if (region.trees < 0) {
						region.trees = 0;
					}
					if(region.mallorn) {
						// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
						region.removeResource(treeResourceID);
						// add new resource
						if (region.getResource(mallornTreeResourceID) == null) {
							RegionResource res = new RegionResource(LongID.create(mallornTreeResourceID.hashCode()), data.rules.getItemType(mallornTreeResourceID, true));
							res.setAmount(region.trees);
							region.addResource(res);
						}
					} else {
						// pavkovic 2002.05.06: remove disjunct resource (trees and sprouts)
						region.removeResource(mallornTreeResourceID);
						// add new resource
						if(data.rules != null) {
							if (region.getResource(treeResourceID) == null) {
								RegionResource res = new RegionResource(LongID.create(treeResourceID.hashCode()), data.rules.getItemType(treeResourceID, true));
								res.setAmount(region.trees);
								region.addResource(res);
							}
						}
					}

					if (region.wage < 0) {
						region.wage = 0;
					}
				}
			}
		}
	}

	public void postProcessAfterTrustlevelChange(GameData data) {
		// initialize fog-of-war cache (FIXME(pavkovic): Do it always?)

		// clear all fog-of-war caches
		if (data.regions() != null) {
			for (Iterator iter = data.regions().values().iterator(); iter.hasNext();) {
				Region r = (Region)iter.next();
				r.setFogOfWar(-1);
			}
		}
		
		// intialize the fog-of-war cache for all regions that are covered by lighthouses
		if (data.buildings() != null) {
			BuildingType type = data.rules.getBuildingType(StringID.create("Leuchtturm"));
			RegionType oceanType = data.rules.getRegionType(StringID.create("Ozean"));
			Comparator sortIndexComparator = new SortIndexComparator(new IDComparator());
			if (type != null) {
				for (Iterator iter = data.buildings().values().iterator(); iter.hasNext();) {
					Building b = (Building)iter.next();
					if (type.equals(b.getType()) && b.getSize() >= 10) {
						int personCounter = 0;
						int perceptionSkillLevel = 0;
						List sortedInmates = CollectionFactory.createLinkedList(b.units());
						Collections.sort(sortedInmates, sortIndexComparator);
						for (Iterator inmates = sortedInmates.iterator(); inmates.hasNext() && personCounter < 4; personCounter++) {
							Unit inmate = (Unit)inmates.next();
							Skill perceptionSkill = inmate.getSkill(data.rules.getSkillType(EresseaConstants.S_WAHRNEHMUNG, true));
							if (perceptionSkill != null) {
								perceptionSkillLevel = Math.max(perceptionSkill.getLevel(), perceptionSkillLevel);
							}
						}
						int maxRadius = (int)Math.min((Math.log(b.getSize())/Math.log(10)) + 1, perceptionSkillLevel / 3);
						if (maxRadius > 0) {
							Map regions = Regions.getAllNeighbours(data.regions(), b.getRegion().getCoordinate(), maxRadius, null);
							for (Iterator regionIter = regions.values().iterator(); regionIter.hasNext();) {
								Region r = (Region)regionIter.next();
								if (oceanType == null || oceanType.equals(r.getType())) {
									r.setFogOfWar(0);
								}
							}
						}
					}
				}
			}
		}
		// intialize the fog-of-war cache for all regions where units or ships traveled through
		for (Iterator iterator = data.regions().values().iterator(); iterator.hasNext(); ) {
			Region r = (Region)iterator.next();
			if (r.travelThru!=null) {
				initTravelThru(data, r, r.travelThru);
			}
			if (r.travelThruShips!=null) {
				initTravelThru(data, r, r.travelThruShips);
			}
		}
	}

	private void initTravelThru(GameData data, Region region, Collection travelThru) {
		for (Iterator iter = travelThru.iterator(); iter.hasNext();) {
			Message mes = (Message) iter.next();
			// fetch ID of Unit or Ship from Message of type "<name> (<id>)"
			String s = mes.getText();
			int startpos = s.lastIndexOf("(") + 1;
			int endpos = s.length() - 1;
			if (startpos>-1 && endpos>startpos) {
				try {
					ID id = EntityID.createEntityID(s.substring(startpos, endpos));
					if (data.getUnit(id)!=null && data.getUnit(id).getFaction().trustLevel>=Faction.TL_PRIVILEGED) {
						// fast return
						region.setFogOfWar(0);
						return;
					} else {
						Ship ship = data.getShip(id);
						if (ship!=null) {
							for (Iterator i = ship.units().iterator(); i.hasNext(); ) {
								if (((Unit)i.next()).getFaction().trustLevel>=Faction.TL_PRIVILEGED) {
									// fast return
									region.setFogOfWar(0);
									return;
								}
							}
						}
					}
				} catch (NumberFormatException e) {
				}
			}
		}
	}
}
