// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.swing.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.eressea.Building;
import com.eressea.Region;
import com.eressea.Ship;
import com.eressea.Unit;
import com.eressea.UnitID;
import com.eressea.rules.BuildingType;
import com.eressea.rules.CastleType;
import com.eressea.rules.ShipType;
import com.eressea.swing.preferences.DetailedPreferencesAdapter;
import com.eressea.swing.preferences.PreferencesAdapter;
import com.eressea.util.CollectionFactory;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.EresseaRaceConstants;
import com.eressea.util.Translations;
/**
 * TODO(pavkovic): remove this stuff, is better suited in inspector environment
 * @author  SirBacon
 */
public class UnitWarning {
	
	private Pref pref;
	
	private String someCommands[];
	private int types[][];
	
	private final static String COMMENT1 = "//";
	private final static String COMMENT2 = ";";
	private final static String TEMP = "TEMP";
	
	public UnitWarning() {
		someCommands = new String[4];
		someCommands[0] = Translations.getOrderTranslation(EresseaOrderConstants.O_MOVE);
		someCommands[1] = Translations.getOrderTranslation(EresseaOrderConstants.O_TEACH);
		someCommands[2] = Translations.getOrderTranslation(EresseaOrderConstants.O_BUY);
		someCommands[3] = Translations.getOrderTranslation(EresseaOrderConstants.O_SELL);
		types = new int[][] {{0,1},{2},{3},{3}};
	}
	
	/** Should return true if the unit has errors.
	 */
	public boolean warn(Unit u) {
		if (u.getOrders().size()>0) {
			Iterator it = u.getOrders().iterator();
			while(it.hasNext()) {
				String cmd = it.next().toString();
				if (cmd.startsWith(COMMENT1) || cmd.startsWith(COMMENT2)) {
					continue;
				}
				String vgl = cmd.toUpperCase();
				for(int i=0;i<someCommands.length;i++) {
					if (vgl.startsWith(someCommands[i])) {
						for(int j=0;j<types[i].length;j++) {
							if (pref.properties[types[i][j]]) {
								if (check(u, types[i][j], cmd)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	protected boolean check(Unit u, int type, String order) {
		StringTokenizer tok;
		int count;
		Region r;
		Ship ship;
		switch(type) {
			case 0: // NACH and Overload
				int rad = countTokens(order);
				if (rad == 0) {
					return false;
				}
				ship = u.getShip();
				int modLoad = 0;
				if (ship != null) {
					// only if captain, else use other case 
					if (u == ship.getOwnerUnit()) {
						ShipType stype = ship.getShipType();
						if (stype != null) {
							Iterator it = ship.units().iterator();
							while(it.hasNext()) {
								modLoad += ((Unit)it.next()).getModifiedLoad();
							}
							return modLoad > stype.getCapacity();
						} else {
							return false; // can't say anything about the ship
						}
					}
				}
				// should care for streets
				modLoad = u.getModifiedLoad();
				Map passengers = u.getPassengers();
				if (passengers.size() > 0) {
					for (Iterator iter = passengers.values().iterator(); iter.hasNext(); ) {
						Unit passenger = (Unit)iter.next();
						modLoad += passenger.getModifiedWeight();
					}
				}
				if (rad == 1) {
					int maxOnFoot = u.getPayloadOnFoot();
					if (maxOnFoot == Unit.CAP_UNSKILLED) {
						return false;
					}					
					if (maxOnFoot - modLoad < 0) {
						return false;
					}
				}
				if (rad == 2) {
					int maxOnHorse = u.getPayloadOnHorse();
					if (maxOnHorse != Unit.CAP_NO_HORSES) {
						if (maxOnHorse == Unit.CAP_UNSKILLED) {
							return false;
						}
						if (maxOnHorse - modLoad < 0) {
							return false;
						}
					}
				}
				return true;
			case 1: // NACH and Radius
				count = countTokens(order);
				ship = u.getShip();
				if (ship != null) {
					// only if captain, else use other case 
					if (u == ship.getOwnerUnit()) {
						ShipType stype = ship.getShipType();
						if (stype != null) {
							int mod = 0;
							if (u.race != null && u.race.getID().equals(EresseaRaceConstants.R_MEERMENSCHEN)) {
								mod = 1;
							}
							return count > stype.getRange()+mod;
						} else {
							return false; // can't say anything about the ship
						}
					}
				}
				return countTokens(order) > u.getRadius();
			case 2: // Teaching and pupils
				tok = new StringTokenizer(order);
				tok.nextToken(); // skip order type
				count = 0;
				r = u.getRegion();
				if (r == null) {
					return false;
				}
				while(tok.hasMoreTokens()) {
					boolean temp = false;
					String cmd = tok.nextToken();
					// count everything that's not a comment
					if (cmd.startsWith(COMMENT1) || cmd.startsWith(COMMENT2)) {
						break;
					}
					if (cmd.equals(TEMP)) {
						temp = true;
						if (tok.hasMoreTokens()) {
							cmd = tok.nextToken();
						} else {
							break;
						}
					}
					try{
						UnitID id = UnitID.createUnitID(cmd);
						if (temp) {
							id = UnitID.createUnitID(-id.intValue());
						}
						Unit unit = r.getUnit(id);
						if (unit != null) {
							count += unit.persons;
						}
					}catch(Exception exc) {}
				}
				return count > u.persons*10;
			case 3: // Market
				r = u.getRegion();
				if (r!=null) {
					Collection c = r.buildings();
					Iterator it = c.iterator();
					while(it.hasNext()) {
						Building b = (Building)it.next();
						BuildingType bt = b.getBuildingType();
						if (bt != null && (bt instanceof CastleType)) {
							if (((CastleType)bt).getMinSize()>=2) {
								return false;
							}
						}
					}
				}
				return true;
			default:
				break;
		}
		return false;
	}
	
	protected int countTokens(String cmd) {
		StringTokenizer tok = new StringTokenizer(cmd);
		tok.nextToken(); // skip order type
		int count = 0;
		while(tok.hasMoreTokens()) {
			cmd = tok.nextToken();
			// count everything that's not a comment
			if (cmd.startsWith(COMMENT1) || cmd.startsWith(COMMENT2)) {
				break;
			}
			count++;
		}
		return count;
	}
	
	
	public String toString() {
		return "Default warner implementation";
	}
	
	/** Should return a preferences adapter if the warner needs one.
	 */
	public PreferencesAdapter getAdapter(Properties settings, String prefix) {
		if (pref == null) {
			pref = new Pref(settings, prefix);
		}
		return pref;
	}
	
	protected class Pref extends DetailedPreferencesAdapter {
		
		public Pref(Properties settings, String prefix) {
			super(4, null, settings, prefix, new String[][] {
				{"warner.goto", "true"},
				{"warner.radius", "true"},
				{"warner.teach", "true"},
				{"warner.market", "true"}
			}, new String[] {
				"prefs.goto",
				"prefs.radius",
				"prefs.teach",
				"prefs.market"
			}, 0, true);
			init();
		}
		
		public String getString(String key) {
			return com.eressea.util.Translations.getTranslation(UnitWarning.class,key);
		}
		
		public void applyChanges(int indices[]) {
		}
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
			defaultTranslations.put("prefs.title" , "Default Unit Warning Impl");
			defaultTranslations.put("prefs.goto" , "Check overload and goto comand");
			defaultTranslations.put("prefs.radius" , "Check radius and goto command");
			defaultTranslations.put("prefs.teach" , "Check teaching and student count");
			defaultTranslations.put("prefs.market" , "Check market command and needed building");
		}
		return defaultTranslations;
	}

}
