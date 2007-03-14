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

package com.eressea.util;

import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JOptionPane;

import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.StringID;
import com.eressea.Unit;
import com.eressea.rules.BuildingType;
import com.eressea.swing.RoutingDialog;

/**
 * Works together with com.eressea.swing.RoutingDialog to calculate the route for a ship.
 *
 * @author Ulrich Küster
 * @author Andreas
 */
public class ShipRoutePlanner {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param ship TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean canPlan(Ship ship) {
		if(ship.size < ship.getShipType().getMaxSize()) {
			return false;
		}

		return (ship.getOwnerUnit() != null) && ship.getOwnerUnit().getFaction().isPrivileged();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param ship TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param ui TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Unit planShipRoute(Ship ship, GameData data, Component ui) {
		// fetch all coast regions
		// Map oceans = Regions.getOceanRegionTypes(data.rules);
		Collection coast = CollectionFactory.createLinkedList();

		try {
			Map regionMap = data.regions();
			Iterator cIt = regionMap.values().iterator();

			while(cIt.hasNext()) {
				try {
					Region region = (Region) cIt.next();
					Map m = Regions.getAllNeighbours(regionMap, region.getCoordinate(), 1, null);
					Iterator cIt2 = m.values().iterator();

					while(cIt2.hasNext()) {
						Region r2 = (Region) cIt2.next();

						//if(oceans.values().contains(r2.getRegionType())) {
						if(r2.getRegionType().isOcean()) {
							coast.add(region);

							break;
						}
					}
				} catch(Exception exc) {
				}
			}
		} catch(Exception coastException) {
		}

		// get the data:
		RoutingDialog.RetValue v = (new RoutingDialog(JOptionPane.getFrameForComponent(ui), data,
													  (coast.size() == 0) ? null : coast)).showRoutingDialog();

		if(v != null) {
			Unit shipOwner = ship.getOwnerUnit();

			if(shipOwner != null) {
				if((shipOwner.getFaction() != null) && shipOwner.getFaction().isPrivileged()) {
					int meerManBonus = 0;

					try {
						meerManBonus = shipOwner.getFaction().getRace().getAdditiveShipBonus();
					} catch(Exception exc) {
					}

					BuildingType harbour = data.rules.getBuildingType(StringID.create("Hafen"));

					List path = Regions.planShipRoute(ship, v.dest, data.regions(), harbour,
													  meerManBonus);

					if(path != null) {
						// Now try to calculate the orders:
						int shipRange = 0;

						try {
							shipRange = ship.getShipType().getRange() + meerManBonus;
						} catch(Exception exc) {
						}

						if(!v.useRange) {
							shipRange = Integer.MAX_VALUE;
						} else if(shipRange <= 0) {
							// couldn't determine shiprange
							JOptionPane.showMessageDialog(ui,
														  getString("msg.shiprangeiszero.text"),
														  getString("msg.title"),
														  JOptionPane.WARNING_MESSAGE);
							shipRange = Integer.MAX_VALUE;
						}

						List curPath = CollectionFactory.createLinkedList();
						List orders = CollectionFactory.createLinkedList();
						String order = "";

						if(v.makeRoute) {
							// TODO(pavkovic): move to EresseaOrderChanger
							order = Translations.getOrderTranslation(EresseaOrderConstants.O_ROUTE);
							order += (" " + Regions.getDirections(path));
							order += (" " +
							Translations.getOrderTranslation(EresseaOrderConstants.O_PAUSE));
							Collections.reverse(path);
							order += (" " + Regions.getDirections(path));
							order += (" " +
							Translations.getOrderTranslation(EresseaOrderConstants.O_PAUSE));
						} else {
							order = Translations.getOrderTranslation(EresseaOrderConstants.O_MOVE) +
									" ";

							int count = shipRange;
							int after = 0;
							String temp = ""; // saves whether a closing bracket must be added: "}"

							for(Iterator iter = path.iterator(); iter.hasNext();) {
								curPath.add(iter.next());

								if(curPath.size() > 1) {
									String dir = Regions.getDirections(curPath);

									if(dir != null) {
										if((count == 0) ||
											   ((count != shipRange) &&
											   Regions.containsHarbour((Region) curPath.get(0),
																		   harbour))) {
											after++;
											count = shipRange;

											if(v.useVorlage) {
												order += temp;
												orders.add(0, order);
												order = "// #after " + after + " { " +
														Translations.getOrderTranslation(EresseaOrderConstants.O_MOVE) +
														" ";
												temp = "}";
											} else {
												orders.add(0, order);
												order = "// " +
														Translations.getOrderTranslation(EresseaOrderConstants.O_MOVE) +
														" ";
											}
										}

										order += (dir + " ");
										count--;
									}

									curPath.remove(0);
								}
							}

							order += temp;
						}

						orders.add(0, order);

						if(v.replaceOrders) {
							shipOwner.setOrders(orders);
						} else {
							for(ListIterator iter = orders.listIterator(); iter.hasNext();) {
								shipOwner.addOrder((String) iter.next(), false, 0);
							}
						}

						return shipOwner;
					} else {
						// No path could be found from start to destination region.
						JOptionPane.showMessageDialog(ui, getString("msg.nopathfound.text"),
													  getString("msg.title"),
													  JOptionPane.WARNING_MESSAGE);
					}
				} else {
					// Captain of the ship does not belong to a privileged faction.
					// No orders can be given.
					JOptionPane.showMessageDialog(ui, getString("msg.captainnotprivileged.text"),
												  getString("msg.title"),
												  JOptionPane.WARNING_MESSAGE);
				}
			} else {
				// Ship has no captain. No orders will be given.
				JOptionPane.showMessageDialog(ui, getString("msg.captainnotfound.text"),
											  getString("msg.title"), JOptionPane.WARNING_MESSAGE);
			}
		}

		return null;
	}

	private static String getString(String key) {
		return com.eressea.util.Translations.getTranslation(ShipRoutePlanner.class, key);
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
			defaultTranslations.put("msg.title", "Ship route scheduler");
			defaultTranslations.put("msg.nopathfound.text", "Error: No valid route found!");
			defaultTranslations.put("msg.captainnotfound.text", "Error: Captain not found!");
			defaultTranslations.put("msg.captainnotprivileged.text",
									"Error: Captain does not belong to a privileged faction!");
			defaultTranslations.put("msg.shiprangeiszero.text",
									"Warning: Ship range specified to zero - will be ignored!");
		}

		return defaultTranslations;
	}
}
