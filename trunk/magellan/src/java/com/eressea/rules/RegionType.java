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

package com.eressea.rules;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.eressea.ID;
import com.eressea.StringID;
import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class RegionType extends UnitContainerType {
	/** A static instance of the unknown region type */
	public static RegionType unknown = new RegionType(StringID.create("unbekannt"));
	private int inhabitants = -1;

	/**
	 * Creates a new RegionType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public RegionType(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void setInhabitants(int i) {
		inhabitants = i;
	}

	/**
	 * helper method for xml reader
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void setInhabitants(String i) {
		setInhabitants(Integer.parseInt(i));
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getInhabitants() {
		return inhabitants;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @deprecated
	 */
	public int getRoadStones() {
		for(Iterator iter = resources.iterator(); iter.hasNext();) {
			Resource r = (Resource) iter.next();

			if(r.getObjectType() instanceof ItemType) {
				return r.getAmount();
			}
		}

		return -1;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @deprecated
	 */
	public BuildingType getRoadSupportBuilding() {
		for(Iterator iter = resources.iterator(); iter.hasNext();) {
			Resource r = (Resource) iter.next();

			if(r.getObjectType() instanceof BuildingType) {
				return (BuildingType) r.getObjectType();
			}
		}

		return null;
	}

	private List resources = CollectionFactory.createLinkedList();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 */
	public void addRoadResource(Resource r) {
		resources.add(r);
	}

	/**
	 * Gets a List of needed Resources for road building
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getRoadResources() {
		return Collections.unmodifiableList(resources);
	}

	private boolean isOcean = false;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isOcean() {
		return isOcean;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param isOcean TODO: DOCUMENT ME!
	 */
	public void setIsOcean(boolean isOcean) {
		this.isOcean = isOcean;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		String s = getName();

		if(s == null) {
			s = id.toString();
		}

		return s;
	}
}
