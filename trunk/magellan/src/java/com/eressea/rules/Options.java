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
 */

package com.eressea.rules;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.ID;
import com.eressea.Rules;

import com.eressea.util.CollectionFactory;
import com.eressea.util.ROCollection;
import com.eressea.util.logging.Logger;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Options {
	private static final Logger log     = Logger.getInstance(Options.class);
	private Map				    options = null;
	Rules					    rules;

	/**
	 * Creates a new Options object.
	 *
	 * @param rules TODO: DOCUMENT ME!
	 */
	public Options(Rules rules) {
		this.rules = rules;
		initOptions(rules);
	}

	/**
	 * copy constructor
	 *
	 * @param orig TODO: DOCUMENT ME!
	 */
	public Options(Options orig) {
		this(orig.rules);
		setValues(orig.getBitMap());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getBitMap() {
		int bitMap = 0;

		for(Iterator iter = options.values().iterator(); iter.hasNext();) {
			OptionCategory o = (OptionCategory) iter.next();

			if(o.isActive()) {
				bitMap = bitMap | o.getBitMask();
			}
		}

		return bitMap;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param bitMap TODO: DOCUMENT ME!
	 */
	public void setValues(int bitMap) {
		for(Iterator iter = options.values().iterator(); iter.hasNext();) {
			OptionCategory o = (OptionCategory) iter.next();
			o.setActive((bitMap & o.getBitMask()) != 0);
		}

		if(bitMap != getBitMap()) {
			log.info("Options.setValues(): invalid value computed!");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection options() {
		if(options == null) {
			initOptions(rules);
		}

		return new ROCollection(options);
	}

	private void initOptions(Rules rules) {
		options = CollectionFactory.createOrderedHashtable();

		for(Iterator iter = rules.getOptionCategoryIterator(); iter.hasNext();) {
			OptionCategory orig = (OptionCategory) iter.next();
			options.put(orig.getID(), new OptionCategory(orig));
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isActive(ID id) {
		OptionCategory o = (OptionCategory) options.get(id);

		return (o != null) && o.isActive();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param active TODO: DOCUMENT ME!
	 */
	public void setActive(ID id, boolean active) {
		OptionCategory o = (OptionCategory) options.get(id);

		if(o != null) {
			o.setActive(active);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for(Iterator iter = options.values().iterator(); iter.hasNext();) {
			OptionCategory o = (OptionCategory) iter.next();
			sb.append(o.getID() + ": " + o.isActive());

			if(iter.hasNext()) {
				sb.append(", ");
			}
		}

		return sb.toString();
	}
}
