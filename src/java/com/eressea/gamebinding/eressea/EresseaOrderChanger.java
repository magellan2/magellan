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

public class EresseaOrderChanger implements OrderChanger {

	private EresseaOrderChanger() {
	}

	private final static EresseaOrderChanger singleton= new EresseaOrderChanger();
	public static EresseaOrderChanger getSingleton() {
		return singleton;
	}

	public void addNamingOrder(Unit unit, String name) {
		String order = createNamingOrder(name);
		unit.addOrder(order, true, 2);
	}
	
	private String createNamingOrder(String name) {
		return Translations.getOrderTranslation(EresseaOrderConstants.O_NAME) + " " + 
			Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + 
			" \"" + name + "\"";
	}
	
	public void addNamingOrder(Unit unit, UnitContainer uc, String name) {
		String order = createNamingOrder(uc, name);
		unit.addOrder(order, true, 2);
	}
	
	private String createNamingOrder(UnitContainer uc, String name) {
		String order = null;
		if (uc instanceof Building) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_CASTLE);
		} else if (uc instanceof Ship) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP);
		} else if (uc instanceof Region) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_REGION);
		} else if (uc instanceof Faction) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_FACTION);
		}
		return Translations.getOrderTranslation(EresseaOrderConstants.O_NAME) + " " + order +
			" \"" + name + "\"";
	}

	public void addDescribeUnitContainerOrder(Unit unit, UnitContainer uc, String descr) {
		String suborder = createDescribeUnitContainerOrder(uc);
		String order =  suborder + " \"" + descr + "\"";
		unit.addOrder(order, true, suborder.indexOf(" ") >= 0 ? 2 : 1);
	}

	private String createDescribeUnitContainerOrder(UnitContainer uc) {
		String order = null;
		if (uc instanceof Building) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_CASTLE);
		} else if (uc instanceof Ship) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP);
		} else if (uc instanceof Region) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_REGION);
		} else if (uc instanceof Faction) {
			order = Translations.getOrderTranslation(EresseaOrderConstants.O_BANNER);
		}
		return order;
	}

	public void addDescribeUnitPrivateOrder(Unit unit, String descr) {
		String order = createDescribeUnitPrivateOrder(descr);
		unit.addOrder(order, true, 2);
	}

	private String createDescribeUnitPrivateOrder(String descr) {
		return Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " +
			Translations.getOrderTranslation(EresseaOrderConstants.O_PRIVATE)+ 
			" \"" + descr + "\"";
	}

	public void addDescribeUnitOrder(Unit unit, String descr) {
		String order = createDescribeUnitOrder(descr);
		unit.addOrder(order, true, 2);
	}

	public String createDescribeUnitOrder(String descr) {
		return Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + 
			Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + 
			" \"" + descr + "\"";
	}

	public void addHideOrder(Unit unit, String level) {
		Collection orders = CollectionFactory.createArrayList();
		orders.addAll(unit.getOrders());
		// remove hide (but not hide faction) order
		for(Iterator iter = orders.iterator(); iter.hasNext(); ) {
			String order = (String)iter.next();
			if (order.startsWith(Translations.getOrderTranslation(EresseaOrderConstants.O_HIDE)) && order.indexOf(Translations.getOrderTranslation(EresseaOrderConstants.O_FACTION))==-1) {
				boolean raceFound = false;
				for(Iterator it2 = unit.getRegion().getData().rules.getRaces(); it2.hasNext(); ) {
					Race race = (Race)it2.next();
					if (order.indexOf(race.getName())>0) {
						raceFound = true;
						break;
					}
				}
				if (!raceFound) {
					iter.remove();
				}
			}
		}
		orders.add(createHideOrder(level));
		unit.setOrders(orders);
	}

	private String createHideOrder(String level) {
		return Translations.getOrderTranslation(EresseaOrderConstants.O_HIDE)+" "+level;
	}

	public void addCombatOrder(Unit unit, int newState) {
		String order = getCombatOrder(unit, newState);
		unit.addOrder(order,true,1);
	}

	private String getCombatOrder(Unit unit, int newState) {
		String str = Translations.getOrderTranslation(EresseaOrderConstants.O_COMBAT)+" ";
		switch(newState) {
		case 0: str += Translations.getOrderTranslation(EresseaOrderConstants.O_AGGRESSIVE);break;
		case 1: str += Translations.getOrderTranslation(EresseaOrderConstants.O_FRONT);break;
		case 2: str += Translations.getOrderTranslation(EresseaOrderConstants.O_REAR);break;
		case 3: str += Translations.getOrderTranslation(EresseaOrderConstants.O_DEFENSIVE);break;
		case 4: str += Translations.getOrderTranslation(EresseaOrderConstants.O_NOT);break;
		case 5: str += Translations.getOrderTranslation(EresseaOrderConstants.O_FLEE);break;
		default: break;
		}
		return str;
	}

	public void addRecruitOrder(Unit unit, int i) {
		String order = Translations.getOrderTranslation(EresseaOrderConstants.O_RECRUIT) + " " + String.valueOf(i);
		unit.addOrders(order);
	}

	public void addMultipleHideOrder(Unit unit) {
		List orders = CollectionFactory.createLinkedList();
		orders.add( Translations.getOrderTranslation(EresseaOrderConstants.O_NUMBER) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + " " );
		orders.add( Translations.getOrderTranslation(EresseaOrderConstants.O_NAME) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + " \"\"" );
		orders.add( Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + " \"\"" );
		orders.add( Translations.getOrderTranslation(EresseaOrderConstants.O_HIDE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_FACTION) );

		if (unit.getShip() != null) {
			orders.add( Translations.getOrderTranslation(EresseaOrderConstants.O_NUMBER) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP) );
			orders.add( Translations.getOrderTranslation(EresseaOrderConstants.O_NAME) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP) + " \"\"" );
			orders.add( Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP) + " \"\"" );
		}

		orders.add( "// " + Translations.getOrderTranslation(EresseaOrderConstants.O_NUMBER) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + " " + unit.getID());
		orders.add( "// " + Translations.getOrderTranslation(EresseaOrderConstants.O_NAME) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + " \"" + unit.getName() + "\"" );
		if (unit.getDescription() != null) {
			orders.add( "// " + Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_UNIT) + " \"" + unit.getDescription() + "\"" );
		}
		if (!unit.hideFaction) {
			orders.add( "// " + Translations.getOrderTranslation(EresseaOrderConstants.O_HIDE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_FACTION) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_NOT) );
		}

		if (unit.getShip() != null) {
			orders.add( "// " + Translations.getOrderTranslation(EresseaOrderConstants.O_NUMBER) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP) + " " +
				unit.getShip().getID().toString() );
			orders.add( "// " + Translations.getOrderTranslation(EresseaOrderConstants.O_NAME) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP) + " \"" +
				unit.getShip().getName() + "\"" );
			if (unit.getShip().getDescription() != null) {
				orders.add( "// " + Translations.getOrderTranslation(EresseaOrderConstants.O_DESCRIBE) + " " + Translations.getOrderTranslation(EresseaOrderConstants.O_SHIP) + " \"" +
					unit.getShip().getDescription() + "\"" );
			}
		}
		unit.addOrders(orders);
	}
}
