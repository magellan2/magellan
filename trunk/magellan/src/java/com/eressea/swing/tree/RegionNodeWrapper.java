// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;


import java.awt.Image;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;

import com.eressea.Region;
import com.eressea.util.CollectionFactory;
import com.eressea.util.StringFactory;

public class RegionNodeWrapper implements CellObject2, SupportsClipboard, SupportsEmphasizing {
	private Region region = null;
	private List GEs=null;
	private int amount = Integer.MIN_VALUE;

	private List subordinatedElements = new LinkedList();

	public RegionNodeWrapper(Region r) {
		this(r, Integer.MIN_VALUE);
	}

	public RegionNodeWrapper(Region r, int amount) {
		this.region = r;
		this.amount = amount;
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
		String text;
		if (this.region.getName() != null) {
			text = region.getName();
		} else {
			text = region.getType().toString();
		}
		text +=  " (" + region.getCoordinate().toString(", ") + ")";
		if (amount > Integer.MIN_VALUE) {
			text += ": " + amount;
		}

		return text;
	}

	// pavkovic 2003.10.01: prevent multiple Lists to be generated for nearly static code
	private static Map iconNamesLists = CollectionFactory.createHashtable();
	public List getIconNames() {
		Object key = region.getType().getID();
		List iconNames = (List) iconNamesLists.get(key);
		if (iconNames == null) {
			iconNames=Collections.singletonList(StringFactory.getFactory().intern(key.toString()));
			iconNamesLists.put(key,iconNames);
		}
		return iconNames;
	}

	public List getSubordinatedElements() {
		return subordinatedElements;
	}

	public boolean emphasized() {
		for (Iterator iter = subordinatedElements.iterator(); iter.hasNext(); ) {
			SupportsEmphasizing se = (SupportsEmphasizing)iter.next();
			if (se.emphasized()) {
				return true;
			}
		}
		return false;
		/* OLD IMPLEMENTATION:
		boolean ret = false;
		Iterator it = region.units().iterator();
		if (it != null) {
			Unit u = null;
			while (it.hasNext()) {
				u = (Unit)it.next();
				if (u.getFaction().trustLevel >= Faction.TL_PRIVILEGED) {
					if (!u.ordersConfirmed) {
						ret = true;
						break;
					}
				}
			}
		}
		return ret;
		*/
	}

	public void propertiesChanged() {
	}

	public List getGraphicsElements() {
		if (GEs==null) {
			GraphicsElement ge=new RegionGraphicsElement(toString(),null,null,region.getType().getID().toString());
			ge.setTooltip(region.getType().getName());
			ge.setType(GraphicsElement.MAIN);

			GEs=CollectionFactory.singletonList(ge);
		}
		return GEs;
	}

	public boolean reverseOrder() {
		return false;
	}
	protected class RegionGraphicsElement extends GraphicsElement {
		public RegionGraphicsElement(Object o,Icon i,Image im,String s) {
			super(o,i,im,s);
			setType(MAIN);
		}
		public boolean isEmphasized() {
			return emphasized();
		}
	}

	public String getClipboardValue() {
		if (region != null) {
			return region.toString();
		} else {
			return toString();
		}
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, String prefix, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
}
