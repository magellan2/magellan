// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.Alliance;
import com.eressea.Faction;
import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;
import com.eressea.util.logging.Logger;

public class FactionNodeWrapper implements CellObject2, SupportsClipboard, SupportsEmphasizing {
	private final static Logger log = Logger.getInstance(FactionNodeWrapper.class);
	private Faction faction = null;
	private Region region = null;
	private List GEs=null;
	private int amount = -1;
	private List subordinatedElements = CollectionFactory.createLinkedList();

	/**
	 * This Map is used to respond dynamically to changes
	 * of the currently active alliance-state that changes, when the user
	 * changes between units/factions/groups etc. in EMapOverviewPanel.
	 * Don't change it's contents here!
	 */
	private Map activeAlliances;

	public FactionNodeWrapper(Faction f, Region r, Map activeAlliances) {
		this.activeAlliances = activeAlliances;
		this.faction = f;
		this.region = r;
	}

	public Faction getFaction() {
		return faction;
	}

	public Region getRegion() {
		return region;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return this.amount;
	}

	public String toString() {
		if (amount == -1) {
			return faction.toString();
		} else {
			return faction.toString() + ": " + amount;
		}
	}

	public List getSubordinatedElements() {
		return subordinatedElements;
	}

	/**
	 * to stay compatible to CellObject
	 */
	// pavkovic 2003.10.01: prevent multiple Lists to be generated for nearly static code
	private static Map iconNamesLists = CollectionFactory.createHashtable();
	public List getIconNames() {
		if(activeAlliances == null) {
			// this should never happen !!
			log.warn("Found activeAlliances-map to be null in FactionNodeWrapper.getGraphicsElements()! Please report to an magellan developer.");
			return null;
		}
		
		Alliance alliance = (Alliance)activeAlliances.get(faction.getID());
		String key;
		if (alliance == null) {
			key = "alliancestate_0";
		} else {
			// This is a workaround and indicates, that this faction
			// is that one upon whose alliances the activeAlliances depends
			if (alliance.getState() == Integer.MAX_VALUE) {
				key = "alliancestate_basisfaction";
			} else {
				key = "alliancestate_" + alliance.getState();
			}
		}

		List iconNames = (List) iconNamesLists.get(key);
		if(iconNames == null) {
			iconNames = CollectionFactory.singletonList(key);
			iconNamesLists.put(key, iconNames);
		}
		return iconNames;
	}

	public boolean emphasized() {
		for (Iterator iter = subordinatedElements.iterator(); iter.hasNext(); ) {
			SupportsEmphasizing se = (SupportsEmphasizing)iter.next();
			if (se.emphasized()) {
				return true;
			}
		}
		return false;
	}

	public void propertiesChanged() {
	}


	public List getGraphicsElements() {
		if (GEs == null) {
			GEs = CollectionFactory.createLinkedList();
		} else {
			GEs.clear();
		}
		String icon = null;
		String tooltip = null;
		if (activeAlliances == null) {
			// this should never happen !!
			log.warn("Warning: Found activeAlliances-map to be null in FactionNodeWrapper.getGraphicsElements()! Please report to an magellan developer.");
		} else {
			Alliance alliance = (Alliance)activeAlliances.get(faction.getID());
			if (alliance != null) {
				// This is a workaround and indicates, that this faction
				// is that one upon whose alliances the activeAlliances depends
				if (alliance.getState() == Integer.MAX_VALUE) {
					icon = "alliancestate_basisfaction";
					tooltip = getString("basis");
				} else {
					icon = "alliancestate_" + alliance.getState();
					tooltip = getString("allied") + alliance.stateToString();
				}
			} else {
				icon = "alliancestate_0";
				tooltip = getString("neutral");
			}
		}
		GraphicsElement ge = new FactionGraphicsElement(toString(), icon);
		ge.setTooltip(tooltip);
		ge.setType(GraphicsElement.MAIN);
		GEs.add(ge);
		return GEs;
	}

	public boolean reverseOrder() {
		return false;
	}

	protected class FactionGraphicsElement extends GraphicsElement {
		public FactionGraphicsElement(String text, String icon) {
			super(text, null, null, icon);
		}
		public boolean isEmphasized() {
			return emphasized();
		}
	}

	public String getClipboardValue() {
		if (faction != null) {
			return faction.getName();
		} else {
			return toString();
		}
	}

	private String getString(String key) {
		return com.eressea.util.Translations.getTranslation(this,key);
	}
	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private final static Map defaultTranslations = CollectionFactory.createHashtable();
	static {
		defaultTranslations.put("basis" , "Display bases on the alliances of this faction");
		defaultTranslations.put("allied" , "Allied: ");
		defaultTranslations.put("neutral" , "Not allied");

	}
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, String prefix, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
}
