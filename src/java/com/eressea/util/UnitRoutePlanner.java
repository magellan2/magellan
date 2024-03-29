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
import java.util.Map;

import javax.swing.JOptionPane;

import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.swing.RoutingDialog;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class UnitRoutePlanner {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean canPlan(Unit unit) {
		if(unit.getRegion() == null) {
			return false;
		}

		if(getModifiedRadius(unit) < 1) {
			return false;
		}

		if(unit.getFaction() != null) {
			return unit.getFaction().isPrivileged();
		}

		return false;
	}

	protected static int getModifiedRadius(Unit unit) {
		int load = unit.getModifiedLoad();

		int payload = unit.getPayloadOnHorse();

		if((payload >= 0) && (payload >= load)) {
			return 2;
		}

		payload = unit.getPayloadOnFoot();

		if((payload >= 0) && (payload >= load)) {
			return 1;
		}

		return 0;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param data TODO: DOCUMENT ME!
	 * @param ui TODO: DOCUMENT ME!
	 * @param otherUnits TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static boolean planUnitRoute(Unit unit, GameData data, Component ui,
										Collection otherUnits) {
		// check for island regions
		Region start = unit.getRegion();
		Collection island = CollectionFactory.createLinkedList();

		if(start.getIsland() != null) {
			island.addAll(start.getIsland().regions());
			island.remove(start);
		} else {
			Map m = Islands.getIsland(data.rules, data.regions(), start);

			if(m != null) {
				island.addAll(m.values());
				island.remove(start);
			}
		}

		if(island.size() == 0) {
			return false;
		}

		// get the data:
		RoutingDialog.RetValue v = (new RoutingDialog(JOptionPane.getFrameForComponent(ui), data,
													  island)).showRoutingDialog();

		if(v != null) {
			Map excludeMap = Regions.getOceanRegionTypes(data.rules);

			List path = Regions.getPath(data.regions(), start.getCoordinate(), v.dest, excludeMap);

			if((path != null) && (path.size() > 1)) {
				int range = getModifiedRadius(unit);

				if(!v.useRange) {
					range = Integer.MAX_VALUE;
				} else {
					if(range <= 0) {
						// couldn't determine shiprange
						JOptionPane.showMessageDialog(ui, getString("msg.unitrangeiszero.text"),
													  getString("msg.title"),
													  JOptionPane.WARNING_MESSAGE);
						range = Integer.MAX_VALUE;
					}
				}

				List orders = CollectionFactory.createLinkedList();
				String order = "";

				if(v.makeRoute) {
					order = getOrder(EresseaOrderConstants.O_ROUTE);
					order += (" " + Regions.getDirections(path));
					order += (" " + getOrder(EresseaOrderConstants.O_PAUSE));
					Collections.reverse(path);
					order += (" " + Regions.getDirections(path));
					order += (" " + getOrder(EresseaOrderConstants.O_PAUSE));
					orders.add(order);
				} else {
					String nach = getOrder(EresseaOrderConstants.O_MOVE) + " ";
					int count = 0;
					int after = 0;
					List curPath = CollectionFactory.createLinkedList();
					int index = 1;

					do {
						curPath.clear();
						curPath.add(path.get(index - 1));

						count = 0;

						while((index < path.size()) && (count < range)) {
							curPath.add(path.get(index));

							count++;
							index++;
						}

						if(v.useVorlage && (after > 0)) {
							orders.add("// #after " + after + " { " + nach +
									   Regions.getDirections(curPath) + " }");
						} else {
							orders.add(nach + Regions.getDirections(curPath));
						}

						after++;
					} while(index < path.size());

					Collections.reverse(orders);
				}

				if(v.replaceOrders) {
					unit.setOrders(orders);
				} else {
					for(Iterator iter = orders.iterator(); iter.hasNext();) {
						unit.addOrder((String) iter.next(), false, 0);
					}
				}

				if((otherUnits != null) && (otherUnits.size() > 0)) {
					Iterator it = otherUnits.iterator();

					while(it.hasNext()) {
						Unit u = (Unit) it.next();

						if(!u.equals(unit)) {
							if(v.replaceOrders) {
								u.setOrders(orders);
							} else {
								for(Iterator iter = orders.iterator(); iter.hasNext();) {
									u.addOrder((String) iter.next(), false, 0);
								}
							}
						}
					}
				}

				return true;
			} else {
				// No path could be found from start to destination region.
				JOptionPane.showMessageDialog(ui, getString("msg.nopathfound.text"),
											  getString("msg.title"), JOptionPane.WARNING_MESSAGE);
			}
		}

		return false;
	}

	private static String getString(String key) {
		return Translations.getTranslation(UnitRoutePlanner.class, key);
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
			defaultTranslations.put("msg.title", "Unit route scheduler");
			defaultTranslations.put("msg.nopathfound.text", "Error: No valid route found!");
			defaultTranslations.put("msg.unitrangeiszero.text",
									"Warning: Unit range specified to zero - will be ignored!");
		}

		return defaultTranslations;
	}

	/**
	 * Returns a translation for the specified order key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	private static String getOrder(String key) {
		return Translations.getOrderTranslation(key);
	}
}
