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

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class RegionNodeWrapper implements CellObject2, SupportsClipboard,
										  SupportsEmphasizing
{
	private Region region			    = null;
	private List   GEs				    = null;
	private int    amount			    = Integer.MIN_VALUE;
	private List   subordinatedElements = new LinkedList();

	/**
	 * Creates a new RegionNodeWrapper object.
	 *
	 * @param r TODO: DOCUMENT ME!
	 */
	public RegionNodeWrapper(Region r) {
		this(r, Integer.MIN_VALUE);
	}

	/**
	 * Creates a new RegionNodeWrapper object.
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param amount TODO: DOCUMENT ME!
	 */
	public RegionNodeWrapper(Region r, int amount) {
		this.region = r;
		this.amount = amount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Region getRegion() {
		return region;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param amount TODO: DOCUMENT ME!
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getAmount() {
		return this.amount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return (amount > Integer.MIN_VALUE) ? (region.toString() + ": " +
											amount) : region.toString();
	}

	// pavkovic 2003.10.01: prevent multiple Lists to be generated for nearly static code
	private static Map iconNamesLists = CollectionFactory.createHashtable();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getIconNames() {
		Object key		 = region.getType().getID();
		List   iconNames = (List) iconNamesLists.get(key);

		if(iconNames == null) {
			iconNames = Collections.singletonList(StringFactory.getFactory()
															   .intern(key.toString()));
			iconNamesLists.put(key, iconNames);
		}

		return iconNames;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getSubordinatedElements() {
		return subordinatedElements;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		for(Iterator iter = subordinatedElements.iterator(); iter.hasNext();) {
			SupportsEmphasizing se = (SupportsEmphasizing) iter.next();
			
			if(se.emphasized()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void propertiesChanged() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getGraphicsElements() {
		if(GEs == null) {
			GraphicsElement ge = new RegionGraphicsElement(toString(), null,
														   null,
														   region.getType()
																 .getID()
																 .toString());
			ge.setTooltip(region.getType().getName());
			ge.setType(GraphicsElement.MAIN);

			GEs = CollectionFactory.singletonList(ge);
		}

		return GEs;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean reverseOrder() {
		return false;
	}

	protected class RegionGraphicsElement extends GraphicsElement {
		/**
		 * Creates a new RegionGraphicsElement object.
		 *
		 * @param o TODO: DOCUMENT ME!
		 * @param i TODO: DOCUMENT ME!
		 * @param im TODO: DOCUMENT ME!
		 * @param s TODO: DOCUMENT ME!
		 */
		public RegionGraphicsElement(Object o, Icon i, Image im, String s) {
			super(o, i, im, s);
			setType(MAIN);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean isEmphasized() {
			return emphasized();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(region != null) {
			return region.toString();
		} else {
			return toString();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings,
									  NodeWrapperDrawPolicy adapter) {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings, String prefix,
									  NodeWrapperDrawPolicy adapter) {
		return null;
	}
}
