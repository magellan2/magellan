// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.ID;
import com.eressea.StringID;
import com.eressea.util.CollectionFactory;
import com.eressea.util.EresseaOrderConstants;
import com.eressea.util.ROCollection;
import com.eressea.util.logging.Logger;

public class Options {
	private final static Logger log = Logger.getInstance(Options.class);
	private Map options = null;
	
	public void initOptions() {
		options = CollectionFactory.createOrderedHashtable();
		
		EresseaOption o = null;
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_REPORT));
		o.setOrder(true);
		o.setBitMask(1 << 0);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_COMPUTER));
		o.setOrder(true);
		o.setBitMask(1 << 1);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_TEMPLATE));
		o.setOrder(true);
		o.setBitMask(1 << 2);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_SILVERPOOL));
		o.setOrder(true);
		o.setBitMask(1 << 3);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_STATISTICS));
		o.setOrder(true);
		o.setBitMask(1 << 4);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create("DEBUG"));
		o.setOrder(false);
		o.setBitMask(1 << 5);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_ZIPPED));
		o.setOrder(true);
		o.setBitMask(1 << 6);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create("ZEITUNG"));
		o.setOrder(false);
		o.setBitMask(1 << 7);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_ITEMPOOL));
		o.setOrder(true);
		o.setBitMask(1 << 8);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_ADDRESSES));
		o.setOrder(true);
		o.setBitMask(1 << 9);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create(EresseaOrderConstants.O_BZIP2));
		o.setOrder(true);
		o.setBitMask(1 << 10);
		options.put(o.getID(), o);
		
		o = new EresseaOption(StringID.create("PUNKTE"));
		o.setOrder(false);
		o.setBitMask(1 << 11);
		options.put(o.getID(), o);
	}

	public Options() {
		initOptions();
	}
	
	public Options(int bitMap) {
		initOptions();
		setValues(bitMap);
	}
	
	public int getBitMap() {
		int bitMap = 0;
		int i = 0;
		
		for (Iterator iter = options.values().iterator(); iter.hasNext();) {
			EresseaOption o = (EresseaOption)iter.next();
			if (o.isActive()) {
				bitMap = bitMap | o.getBitMask();
			}
		}
		
		return bitMap;
	}
	
	public void setValues(int bitMap) {
		for (Iterator iter = options.values().iterator(); iter.hasNext();) {
			EresseaOption o = (EresseaOption)iter.next();
			o.setActive((bitMap & o.getBitMask()) != 0);
		}
		if (bitMap != getBitMap()) {
			log.info("Options.setValues(): invalid value computed!");
		}
	}
	
	public Collection options() {
		return new ROCollection(options);
	}
	
	public boolean isActive(ID id) {
		EresseaOption o = (EresseaOption)options.get(id);
		return o != null && o.isActive();
	}

	public void setActive(ID id, boolean active) {
		EresseaOption o = (EresseaOption)options.get(id);
		if (o != null) {
			o.setActive(active);
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(com.eressea.util.Translations.getOrderTranslation(EresseaOrderConstants.O_OPTION));
		for (Iterator iter = options.values().iterator(); iter.hasNext();) {
			EresseaOption o = (EresseaOption)iter.next();
			sb.append(o.getID() + ": " + o.isActive() + ", ");
		}
		return sb.toString();
	}
}
