/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 * $Id$
 */

package com.eressea.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.eressea.Building;
import com.eressea.Coordinate;
import com.eressea.GameData;
import com.eressea.ID;
import com.eressea.Message;
import com.eressea.Region;
import com.eressea.Rules;
import com.eressea.Ship;
import com.eressea.Unit;

import com.eressea.rules.BuildingType;
import com.eressea.rules.RegionType;

import com.eressea.util.logging.Logger;

/**
 * A class offering common operations on regions.
 */
public class Regions {
	private static final Logger log = Logger.getInstance(Regions.class);

	/**
	 * Retrieve the regions within radius around region center.
	 *
	 * @param regions a map containing the existing regions.
	 * @param center the region the neighbours of which are retrieved.
	 * @param radius the maximum distance between center and any region to be
	 * 		  regarded as a neighbour within radius.
	 * @param excludedRegionTypes region types that disqualify regions as valid
	 * 		  neighbours. This also may be null
	 *
	 * @return a map with all neighbours that were found, including     region
	 * 		   center. The keys are instances of class ID,     values are
	 * 		   objects of class Region.
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public static Map getAllNeighbours(Map regions, ID center, int radius,
									   Map excludedRegionTypes) {
		if(center instanceof Coordinate) {
			return getAllNeighbours(regions, (Coordinate) center, radius,
									excludedRegionTypes);
		} else {
			throw new IllegalArgumentException("center is not an eressea coordinate. Support for e2 incomplete!");
		}
	}

	/**
	 * Retrieve the regions within radius around region center.
	 *
	 * @param regions a map containing the existing regions.
	 * @param center the region the neighbours of which are retrieved.
	 * @param radius the maximum distance between center and any region to be
	 * 		  regarded as a neighbour within radius.
	 * @param excludedRegionTypes region types that disqualify regions as valid
	 * 		  neighbours.
	 *
	 * @return a map with all neighbours that were found, including     region
	 * 		   center. The keys are instances of class Coordinate,     values
	 * 		   are objects of class Region.
	 */
	private static Map getAllNeighbours(Map regions, Coordinate center,
										int radius, Map excludedRegionTypes) {
		Map		   neighbours = CollectionFactory.createHashtable();
		Coordinate c = new Coordinate(0, 0, center.z);

		for(int dx = -radius; dx <= radius; dx++) {
			for(int dy = (-radius + Math.abs(dx)) - ((dx > 0) ? dx : 0);
					dy <= ((radius - Math.abs(dx)) - ((dx < 0) ? dx : 0));
					dy++) {
				c.x = center.x + dx;
				c.y = center.y + dy;

				Region neighbour = (Region) regions.get(c);

				if(neighbour != null) {
					if((excludedRegionTypes == null) ||
						   !excludedRegionTypes.containsKey(neighbour.getType()
																		 .getID())) {
						neighbours.put(neighbour.getID(), neighbour);
					}
				}
			}
		}

		return neighbours;
	}

	/**
	 * Retrieve the regions directly connected with the center region
	 * (including it).
	 *
	 * @param regions a map containing the existing regions.
	 * @param center the region the neighbours of which are retrieved.
	 * @param excludedRegionTypes region types that disqualify regions as valid
	 * 		  neighbours.
	 *
	 * @return a map with all neighbours that were found, including     region
	 * 		   center. The keys are instances of class Coordinate,     values
	 * 		   are objects of class Region.
	 */
	public static Map getAllNeighbours(Map regions, ID center,
									   Map excludedRegionTypes) {
		return getAllNeighbours(regions, center, 1, excludedRegionTypes);
	}

	/**
	 * Find a way from one region to another region and  get the directions in
	 * which to move to follow a sequence of regions. This is virtually the
	 * same as
	 * <pre>getDirections(getPath(regions, start, dest, excludedRegionTypes));</pre>
	 *
	 * @param regions TODO: DOCUMENT ME!
	 * @param start TODO: DOCUMENT ME!
	 * @param dest TODO: DOCUMENT ME!
	 * @param excludedRegionTypes TODO: DOCUMENT ME!
	 *
	 * @return a String telling the direction statements necessary     to
	 * 		   follow the sequence of regions contained in regions.
	 */
	public static String getDirections(Map regions, ID start, ID dest,
									   Map excludedRegionTypes) {
		return getDirections(getPath(regions, start, dest, excludedRegionTypes));
	}

	/**
	 * Get the directions in which to move to follow a sequence of regions.
	 *
	 * @param regions an ordered consecutive sequence of regions.
	 *
	 * @return a String telling the direction statements necessary     to
	 * 		   follow the sequence of regions contained in regions.
	 */
	public static String getDirections(Collection regions) {
		if(regions == null) {
			return null;
		}

		List directions = getDirectionObjectsOfRegions(regions);

		if(directions == null) {
			return null;
		}

		StringBuffer dir = new StringBuffer();

		for(Iterator iter = directions.iterator(); iter.hasNext();) {
			Direction d = (Direction) iter.next();

			if(dir.length() > 0) {
				dir.append(" ");
			}

			dir.append(Direction.toString(d.getDir(), true));
		}

		return dir.toString();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param regions TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List getDirectionObjectsOfRegions(Collection regions) {
		if(regions == null) {
			return null;
		}

		List coordinates = CollectionFactory.createArrayList(regions.size());

		for(Iterator iter = regions.iterator(); iter.hasNext();) {
			Region r = (Region) iter.next();
			coordinates.add(r.getCoordinate());
		}

		return getDirectionObjectsOfCoordinates(coordinates);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param coordinates TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List getDirectionObjectsOfCoordinates(Collection coordinates) {
		if(coordinates == null) {
			return null;
		}

		List	   directions = CollectionFactory.createArrayList(coordinates.size());

		Coordinate prev = null;
		Coordinate cur  = null;

		Iterator   iter = coordinates.iterator();

		if(iter.hasNext()) {
			prev = (Coordinate) iter.next();
		}

		while(iter.hasNext()) {
			cur = (Coordinate) iter.next();

			Coordinate diffCoord = new Coordinate(cur.x - prev.x,
												  cur.y - prev.y, 0);
			int		   intDir = Direction.toInt(diffCoord);

			if(intDir != -1) {
				directions.add(new Direction(intDir));
			} else {
				log.warn("Regions.getDirectionsOfCoordinates(): invalid direction encountered");

				return null;
			}

			prev = cur;
		}

		return directions;
	}

	/**
	 * Find a way from one region to another region.
	 *
	 * @param regions TODO: DOCUMENT ME!
	 * @param start TODO: DOCUMENT ME!
	 * @param dest TODO: DOCUMENT ME!
	 * @param excludedRegionTypes TODO: DOCUMENT ME!
	 *
	 * @return a Collection of regions that have to be trespassed in  order to
	 * 		   get from the one to the other specified region, including both
	 * 		   of them.
	 *
	 * @throws IllegalArgumentException TODO: DOCUMENT ME!
	 */
	public static List getPath(Map regions, ID start, ID dest,
							   Map excludedRegionTypes) {
		if(start instanceof Coordinate && dest instanceof Coordinate) {
			return getPath(regions, (Coordinate) start, (Coordinate) dest,
						   excludedRegionTypes);
		} else {
			throw new IllegalArgumentException("start of dest is not an eressea coordinate. Support for e2 incomplete!");
		}
	}

	/**
	 * Find a way from one region to another region.
	 *
	 * @param regions TODO: DOCUMENT ME!
	 * @param start TODO: DOCUMENT ME!
	 * @param dest TODO: DOCUMENT ME!
	 * @param excludedRegionTypes TODO: DOCUMENT ME!
	 *
	 * @return a Collection of regions that have to be trespassed in  order to
	 * 		   get from the one to the other specified region, including both
	 * 		   of them.
	 */
	private static List getPath(Map regions, Coordinate start, Coordinate dest,
								Map excludedRegionTypes) {
		if((regions == null) || (start == null) || (dest == null)) {
			log.warn("Regions.getPath(): invalid argument");

			return new LinkedList();
		}

		Map distances = CollectionFactory.createHashtable();
		distances.put(start, new Float(0.0f)); // contains the distances from the start region to all other regions as Float objects

		LinkedList path					   = new LinkedList();
		LinkedList backlogList			   = new LinkedList(); // contains regions with unknown distance to the start region
		Map		   backlogMap			   = CollectionFactory.createHashMap(); // contains the same entries as the backlog list. It's contents are unordered but allow a fast look-up by coordinate
		Region     curRegion			   = null;
		Coordinate curCoord				   = null;
		int		   consecutiveReenlistings = 0; // safe-guard against endless loops

		if(excludedRegionTypes == null) {
			excludedRegionTypes = CollectionFactory.createHashtable();
		}

		/* initialize the backlog list and map with the neighbours of
		   the start region */
		Map initNeighbours = getAllNeighbours(regions, start,
											  excludedRegionTypes);
		initNeighbours.remove(start);
		backlogList.addAll(initNeighbours.values());
		backlogMap.putAll(initNeighbours);

		/* first, determine the distance from the start region to all
		   other regions */
		while(true) {
			/* in this loop the backlog list contains all regions with
			   unkown distance to start */
			if((backlogList == null) || (backlogList.size() == 0)) {
				break;
			}

			/* take the first region from the backlog list */
			curRegion = (Region) backlogList.getFirst();
			curCoord  = curRegion.getCoordinate();

			/* safety checks */
			if(excludedRegionTypes.containsKey(curRegion.getType().getID())) {
				log.warn("Regions.getPath(): Found an region of type " +
						 curRegion.getType().getName() +
						 " in region list! Removing and ignoring it.");
				backlogList.removeFirst();
				backlogMap.remove(curCoord);

				continue;
			}

			if(distances.containsKey(curCoord)) {
				log.warn("Regions.getPath(): Found a region with known distance in region list! Removing and ignoring it.");
				backlogList.removeFirst();
				backlogMap.remove(curCoord);

				continue;
			}

			/* determine all neighbours of the current region taken
			   from the backlog list */
			float minDistance = Float.MAX_VALUE;
			Map   neighbours = getAllNeighbours(regions, curCoord,
												excludedRegionTypes);
			neighbours.remove(curCoord);

			/* now determine the distance from the start region to the
			   current region taken from the backlog list by checking
			   its neighbour's distances to the start region */
			for(Iterator iter = neighbours.values().iterator(); iter.hasNext();) {
				Region     curNb	  = (Region) iter.next();
				Coordinate curNbCoord = curNb.getCoordinate();
				Float	   dist		  = (Float) distances.get(curNbCoord);

				if(dist != null) {
					/* we know the distance from the start region to
					   this neighbour, so we can determine the
					   distance from the start region to the current
					   region taken from the backlog list */
					float curDistance = getDistance(curNb, curRegion) +
										dist.floatValue();

					if(curDistance < minDistance) {
						minDistance = curDistance;
					}
				} else {
					/* we do not know the distance from the start
					   region to this neighbour, so we store this
					   neighbour in the backlog list */
					if(!backlogMap.containsKey(curNbCoord)) {
						backlogList.add(curNb);
						backlogMap.put(curNbCoord, null);
					}
				}
			}

			/* If we could determine the distance from the start
			   region to the current region taken from the backlog
			   list, we can remove it from that list and record the
			   distance */
			if(minDistance < Float.MAX_VALUE) {
				consecutiveReenlistings = 0;
				backlogList.removeFirst();
				backlogMap.remove(curCoord);
				distances.put(curCoord, new Float(minDistance));
			} else {
				backlogList.removeFirst();

				if(!distances.containsKey(curCoord)) {
					backlogList.addLast(curRegion);
					consecutiveReenlistings++;

					if(consecutiveReenlistings > backlogList.size()) {
						log.warn("Regions.getPath(): looks like an endless loop. Exiting.");

						break;
					}
				} else {
					log.warn("Regions.getPath(): Found a region with known distance in backlog list: " +
							 curRegion);
				}
			}
		}

		// backtracking
		/* now we know the distance of each region to the start region
		   but we do not know a shortest path. We can find one simply
		   by starting at the destination region, looking at its
		   neighbours and choosing the one with the smallest distance
		   to the start region until we reach the start region. This
		   sequence of regions is the reverse shortest path. */
		curRegion = (Region) regions.get(dest);
		curCoord  = dest;
		path.add(curRegion);

		while((curRegion != null) && (curCoord != null) &&
				  !curCoord.equals(start)) {
			Float dist = (Float) distances.get(curCoord);

			if(dist != null) {
				float	   minDistance    = dist.floatValue();
				Coordinate closestNbCoord = null;
				Map		   neighbours     = getAllNeighbours(regions, curCoord,
															 excludedRegionTypes);
				neighbours.remove(curCoord);

				for(Iterator iter = neighbours.values().iterator();
						iter.hasNext();) {
					Region     curNb	  = (Region) iter.next();
					Coordinate curNbCoord = curNb.getCoordinate();
					Float	   nbDist     = (Float) distances.get(curNbCoord);

					if(nbDist != null) {
						float curDistance = nbDist.floatValue();

						if(curDistance < minDistance) {
							minDistance    = curDistance;
							closestNbCoord = curNbCoord;
						}
					} else {
						log.warn("Regions.getPath(): Found neighbouring region without distance: " +
								 curNb + " neighbouring " + curRegion);
					}
				}

				if(closestNbCoord != null) {
					curCoord  = closestNbCoord;
					curRegion = (Region) regions.get(curCoord);
					path.addFirst(curRegion);
				} else {
					log.warn("Regions.getPath(): Discovered region without any distanced neighbours while backtracking");
					path.clear();

					break;
				}
			} else {
				log.warn("Regions.getPath(): Discovered region without distance while backtracking");
				path.clear();

				break;
			}
		}

		return path;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param harbour TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean containsHarbour(Region r, BuildingType harbour) {
		boolean harbourFound = false;

		if(harbour != null) {
			for(Iterator iter = r.buildings().iterator(); iter.hasNext();) {
				Building b = (Building) iter.next();

				if(b.getType().equals(harbour) &&
					   (b.getSize() == harbour.getMaxSize())) {
					harbourFound = true;

					break;
				}
			}
		}

		return harbourFound;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param ship TODO: DOCUMENT ME!
	 * @param destination TODO: DOCUMENT ME!
	 * @param allregions TODO: DOCUMENT ME!
	 * @param oceanType TODO: DOCUMENT ME!
	 * @param harbour TODO: DOCUMENT ME!
	 * @param speedBonus TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static List planShipRoute(Ship ship, Coordinate destination,
									 Map allregions, RegionType oceanType,
									 BuildingType harbour, int speedBonus) {
		if(destination != null) {
			Map regions		   = CollectionFactory.createHashtable();
			Map harbourRegions = CollectionFactory.createHashtable();

			// Fetch all ocean-regions and all regions, that contain a harbour.
			// These are the valid one in witch a path shall be searched.
			if(oceanType != null) {
				for(Iterator iter = allregions.values().iterator();
						iter.hasNext();) {
					Region r = (Region) iter.next();

					if((r.getType() != null) && r.getType().equals(oceanType)) {
						regions.put(r.getCoordinate(), r);
					} else if(containsHarbour(r, harbour)) {
						harbourRegions.put(r.getCoordinate(), r);
					}
				}
			}

			// Add destination region:
			Region destRegion = (Region) allregions.get(destination);

			if(destRegion != null) {
				regions.put(destination, destRegion);
			}

			// determine the possible ways off (ships casually can not leave
			// land to all directions)
			List startregions = CollectionFactory.createLinkedList();
			int  shipRange = ship.getShipType().getRange();
			shipRange += speedBonus;

			Region curRegion = ship.getRegion();

			if((ship.shoreId == Direction.DIR_INVALID) ||
				   containsHarbour(curRegion, harbour)) {
				// Ship can leave in all directions
				startregions.add(curRegion);
				regions.put(curRegion.getCoordinate(), curRegion);
			} else {
				/*
				 * Ship can not leave in all directions.
				 * Idea: All possibilities to leave land are evaluated and
				 * for all of them a single path is calculated. The shortest of
				 * these paths will be taken and the way from the land to that
				 * oceanregion will be added.
				 */
				Coordinate c = null;
				Region     r = null;

				// central direction
				int shoreID = ship.shoreId;
				c = Direction.toCoordinate(shoreID);
				c.x += curRegion.getCoordinate().x;
				c.y += curRegion.getCoordinate().y;
				r = (Region) allregions.get(c);

				if((r != null) &&
					   (r.getType().equals(oceanType) ||
					   containsHarbour(r, harbour))) {
					startregions.add(r);
					regions.put(r.getCoordinate(), r);
				}

				// left neighbour
				shoreID--;

				if(shoreID == -1) {
					shoreID = 5;
				}

				c = Direction.toCoordinate(shoreID);
				c.x += curRegion.getCoordinate().x;
				c.y += curRegion.getCoordinate().y;
				r = (Region) allregions.get(c);

				if((r != null) &&
					   (r.getType().equals(oceanType) ||
					   containsHarbour(r, harbour))) {
					startregions.add(r);
					regions.put(r.getCoordinate(), r);
				}

				// right neighbour
				shoreID = (shoreID + 2) % 6;
				c	    = Direction.toCoordinate(shoreID);
				c.x += curRegion.getCoordinate().x;
				c.y += curRegion.getCoordinate().y;
				r = (Region) allregions.get(c);

				if((r != null) &&
					   (r.getType().equals(oceanType) ||
					   containsHarbour(r, harbour))) {
					startregions.add(r);
					regions.put(r.getCoordinate(), r);
				}
			}

			/*
			 * Now determine the several paths for the (possible) multiple
			 * ways of leaving land. There's another hazard. Harbours can be used
			 * as canals but they can not be crossed in one game round. (Your reach them
			 * in one and leave them in the next instead. So first a way without using
			 * harbours is searched and then another considering them. Then they are compared...
			 */
			List paths = CollectionFactory.createLinkedList();
			harbourRegions.putAll(regions);

			for(Iterator iter = startregions.iterator(); iter.hasNext();) {
				Region startregion = (Region) iter.next();
				List   path = Regions.getPath(regions,
											  startregion.getCoordinate(),
											  destination,
											  CollectionFactory.createHashtable());

				if((path != null) && (path.size() > 0)) {
					paths.add(path);
				}

				List pathWithHarbours = Regions.getPath(harbourRegions,
														startregion.getCoordinate(),
														destination,
														CollectionFactory.createHashtable());

				if((pathWithHarbours != null) && (pathWithHarbours.size() > 0) &&
					   !pathWithHarbours.equals(path)) {
					paths.add(pathWithHarbours);
				}
			}

			// search for shortest path (only if more than one path found)
			int minpos = 0;

			if(paths.size() > 1) {
				int curpos   = 0;
				int minweeks = Integer.MAX_VALUE;

				for(Iterator iter = paths.iterator(); iter.hasNext();
						curpos++) {
					List path = (List) iter.next();

					// determine path size considering ship-range
					// don't count regions due to harbours!
					int weeks = -1;

					if(shipRange > 0) {
						int counter = shipRange;
						weeks = 1;

						for(Iterator i = path.iterator(); i.hasNext();
								counter--) {
							if(counter == 0) {
								counter = shipRange;
								weeks++;
							} else if(containsHarbour((Region) i.next(), harbour)) {
								counter = shipRange;
								weeks++;
							}
						}
					}

					if(weeks < minweeks) {
						minweeks = weeks;
						minpos   = curpos;
					} else if((weeks == minweeks) &&
								  (((List) paths.get(minpos)).size() > path.size())) {
						minpos = curpos;
					}
				}
			}

			if(paths.size() > 0) {
				List path = (List) paths.get(minpos);

				if(!path.get(0).equals(curRegion)) {
					path.add(0, curRegion);
				}

				return path;
			}
		}

		return null;
	}

	private static float getDistance(Region r1, Region r2) {
		return 1.0f;
	}

	/**
	 * Retrieve the coordinates the unit passes from the messages of the
	 * regions.
	 *
	 * @param data the unit
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return a List of Coordinate objects of the path the unit used (although
	 * 		   evaluated via  backtracking) from start to end.
	 */
	public static List getMovement(GameData data, Unit u) {
		List coordinates = CollectionFactory.createArrayList(2);

		// first of all add current coordinate
		coordinates.add(u.getRegion().getID());

		// we need a string which is useable for travelThru AND travelThruShip
		String ID = (u.getShip() == null) ? u.toString()
										  : u.getShip().toString(false);

		// run over neighbours recursively
		Coordinate c = getMovement(data, ID, u.getRegion().getCoordinate(),
								   coordinates);

		while((c != null) && !coordinates.contains(c)) {
			coordinates.add(c);
			c = getMovement(data, ID, c, coordinates);
		}

		Collections.reverse(coordinates);

		return coordinates;

		// return getDirectionObjectsOfCoordinates(coordinates);
	}

	private static Coordinate getMovement(GameData data, String ID,
										  Coordinate c, List travelledRegions) {
		Map neighbours = getAllNeighbours(data.regions(), c,
										  Collections.EMPTY_MAP);

		for(Iterator iter = neighbours.values().iterator(); iter.hasNext();) {
			Region     r		 = (Region) iter.next();
			Coordinate neighbour = r.getCoordinate();

			if(neighbour.equals(c) || travelledRegions.contains(neighbour)) {
				// dont add our own or an already visited coordinate
				continue;
			}

			if(messagesContainsString(r.travelThru, ID) ||
				   messagesContainsString(r.travelThruShips, ID)) {
				return neighbour;
			}
		}

		return null;
	}

	private static boolean messagesContainsString(List messages, String ID) {
		if(messages == null) {
			return false;
		}

		for(Iterator iter = messages.iterator(); iter.hasNext();) {
			Message m = (Message) iter.next();

			if(m.getText().equals(ID)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rules TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getOceanRegionTypes(Rules rules) {
		Map ret = CollectionFactory.createHashtable();

		for(Iterator iter = rules.getRegionTypeIterator(); iter.hasNext();) {
			RegionType rt = (RegionType) iter.next();

			if(rt.isOcean()) {
				ret.put(rt.getID(), rt);
			}
		}

		return ret;
	}
}
