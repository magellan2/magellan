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

package com.eressea.gamebinding.eressea;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.eressea.Building;
import com.eressea.Faction;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.Unit;
import com.eressea.UnitContainer;

import com.eressea.gamebinding.OrderChanger;

import com.eressea.rules.Race;

import com.eressea.util.CollectionFactory;
import com.eressea.util.Translations;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EresseaOrderChanger implements OrderChanger {
	private EresseaOrderChanger() {
	}

	private static final EresseaOrderChanger singleton = new EresseaOrderChanger();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static EresseaOrderChanger getSingleton() {
		return singleton;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param name TODO: DOCUMENT ME!
	 */
	public void addNamingOrder(Unit unit, String name) {
		String order = createNamingOrder(name);
		unit.addOrder(order, true, 2);
	}

	private String createNamingOrder(String name) {
		return Translations.getOrderTranslation(EresseaConstants.O_NAME) + " " +
			   Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
			   " \"" + name + "\"";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param uc TODO: DOCUMENT ME!
	 * @param name TODO: DOCUMENT ME!
	 */
	public void addNamingOrder(Unit unit, UnitContainer uc, String name) {
		String order = createNamingOrder(uc, name);
		unit.addOrder(order, true, 2);
	}

	private String createNamingOrder(UnitContainer uc, String name) {
		String order = null;

		if(uc instanceof Building) {
			order = Translations.getOrderTranslation(EresseaConstants.O_CASTLE);
		} else if(uc instanceof Ship) {
			order = Translations.getOrderTranslation(EresseaConstants.O_SHIP);
		} else if(uc instanceof Region) {
			order = Translations.getOrderTranslation(EresseaConstants.O_REGION);
		} else if(uc instanceof Faction) {
			order = Translations.getOrderTranslation(EresseaConstants.O_FACTION);
		}

		return Translations.getOrderTranslation(EresseaConstants.O_NAME) + " " +
			   order + " \"" + name + "\"";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param uc TODO: DOCUMENT ME!
	 * @param descr TODO: DOCUMENT ME!
	 */
	public void addDescribeUnitContainerOrder(Unit unit, UnitContainer uc,
											  String descr) {
		String suborder = createDescribeUnitContainerOrder(uc);
		String order = suborder + " \"" + descr + "\"";
		unit.addOrder(order, true, (suborder.indexOf(" ") >= 0) ? 2 : 1);
	}

	private String createDescribeUnitContainerOrder(UnitContainer uc) {
		String order = null;

		if(uc instanceof Building) {
			order = Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
					" " +
					Translations.getOrderTranslation(EresseaConstants.O_CASTLE);
		} else if(uc instanceof Ship) {
			order = Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
					" " +
					Translations.getOrderTranslation(EresseaConstants.O_SHIP);
		} else if(uc instanceof Region) {
			order = Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
					" " +
					Translations.getOrderTranslation(EresseaConstants.O_REGION);
		} else if(uc instanceof Faction) {
			order = Translations.getOrderTranslation(EresseaConstants.O_BANNER);
		}

		return order;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param descr TODO: DOCUMENT ME!
	 */
	public void addDescribeUnitPrivateOrder(Unit unit, String descr) {
		String order = createDescribeUnitPrivateOrder(descr);
		unit.addOrder(order, true, 2);
	}

	private String createDescribeUnitPrivateOrder(String descr) {
		return Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
			   " " +
			   Translations.getOrderTranslation(EresseaConstants.O_PRIVATE) +
			   " \"" + descr + "\"";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param descr TODO: DOCUMENT ME!
	 */
	public void addDescribeUnitOrder(Unit unit, String descr) {
		String order = createDescribeUnitOrder(descr);
		unit.addOrder(order, true, 2);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param descr TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String createDescribeUnitOrder(String descr) {
		return Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
			   " " + Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
			   " \"" + descr + "\"";
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param level TODO: DOCUMENT ME!
	 */
	public void addHideOrder(Unit unit, String level) {
		Collection orders = CollectionFactory.createArrayList();
		orders.addAll(unit.getOrders());

		// remove hide (but not hide faction) order
		for(Iterator iter = orders.iterator(); iter.hasNext();) {
			String order = (String) iter.next();

			if(order.startsWith(Translations.getOrderTranslation(EresseaConstants.O_HIDE)) &&
				   (order.indexOf(Translations.getOrderTranslation(EresseaConstants.O_FACTION)) == -1)) {
				boolean raceFound = false;

				for(Iterator it2 = unit.getRegion().getData().rules.getRaceIterator();
						it2.hasNext();) {
					Race race = (Race) it2.next();

					if(order.indexOf(race.getName()) > 0) {
						raceFound = true;

						break;
					}
				}

				if(!raceFound) {
					iter.remove();
				}
			}
		}

		orders.add(createHideOrder(level));
		unit.setOrders(orders);
	}

	private String createHideOrder(String level) {
		return Translations.getOrderTranslation(EresseaConstants.O_HIDE) + " " +
			   level;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param newState TODO: DOCUMENT ME!
	 */
	public void addCombatOrder(Unit unit, int newState) {
		String order = getCombatOrder(unit, newState);
		unit.addOrder(order, true, 1);
	}

	private String getCombatOrder(Unit unit, int newState) {
		String str = Translations.getOrderTranslation(EresseaConstants.O_COMBAT) +
					 " ";

		switch(newState) {
		case 0:
			str += Translations.getOrderTranslation(EresseaConstants.O_AGGRESSIVE);

			break;

		case 1:
			str += Translations.getOrderTranslation(EresseaConstants.O_FRONT);

			break;

		case 2:
			str += Translations.getOrderTranslation(EresseaConstants.O_REAR);

			break;

		case 3:
			str += Translations.getOrderTranslation(EresseaConstants.O_DEFENSIVE);

			break;

		case 4:
			str += Translations.getOrderTranslation(EresseaConstants.O_NOT);

			break;

		case 5:
			str += Translations.getOrderTranslation(EresseaConstants.O_FLEE);

			break;

		default:
			break;
		}

		return str;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param i TODO: DOCUMENT ME!
	 */
	public void addRecruitOrder(Unit unit, int i) {
		String order = Translations.getOrderTranslation(EresseaConstants.O_RECRUIT) +
					   " " + String.valueOf(i);
		unit.addOrders(order);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 */
	public void addMultipleHideOrder(Unit unit) {
		List orders = CollectionFactory.createLinkedList();
		orders.add(Translations.getOrderTranslation(EresseaConstants.O_NUMBER) +
				   " " +
				   Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
				   " ");
		orders.add(Translations.getOrderTranslation(EresseaConstants.O_NAME) +
				   " " +
				   Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
				   " \"\"");
		orders.add(Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
				   " " +
				   Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
				   " \"\"");
		orders.add(Translations.getOrderTranslation(EresseaConstants.O_HIDE) +
				   " " +
				   Translations.getOrderTranslation(EresseaConstants.O_FACTION));

		if(unit.getShip() != null) {
			orders.add(Translations.getOrderTranslation(EresseaConstants.O_NUMBER) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_SHIP));
			orders.add(Translations.getOrderTranslation(EresseaConstants.O_NAME) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_SHIP) +
					   " \"\"");
			orders.add(Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_SHIP) +
					   " \"\"");
		}

		orders.add("// " +
				   Translations.getOrderTranslation(EresseaConstants.O_NUMBER) +
				   " " +
				   Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
				   " " + unit.getID());
		orders.add("// " +
				   Translations.getOrderTranslation(EresseaConstants.O_NAME) +
				   " " +
				   Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
				   " \"" + unit.getName() + "\"");

		if(unit.getDescription() != null) {
			orders.add("// " +
					   Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_UNIT) +
					   " \"" + unit.getDescription() + "\"");
		}

		if(!unit.hideFaction) {
			orders.add("// " +
					   Translations.getOrderTranslation(EresseaConstants.O_HIDE) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_FACTION) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_NOT));
		}

		if(unit.getShip() != null) {
			orders.add("// " +
					   Translations.getOrderTranslation(EresseaConstants.O_NUMBER) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_SHIP) +
					   " " + unit.getShip().getID().toString());
			orders.add("// " +
					   Translations.getOrderTranslation(EresseaConstants.O_NAME) +
					   " " +
					   Translations.getOrderTranslation(EresseaConstants.O_SHIP) +
					   " \"" + unit.getShip().getName() + "\"");

			if(unit.getShip().getDescription() != null) {
				orders.add("// " +
						   Translations.getOrderTranslation(EresseaConstants.O_DESCRIBE) +
						   " " +
						   Translations.getOrderTranslation(EresseaConstants.O_SHIP) +
						   " \"" + unit.getShip().getDescription() + "\"");
			}
		}

		unit.addOrders(orders);
	}
}
