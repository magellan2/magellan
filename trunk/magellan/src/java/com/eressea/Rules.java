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

package com.eressea;

import java.util.Iterator;

import com.eressea.rules.AllianceCategory;
import com.eressea.rules.BuildingType;
import com.eressea.rules.CastleType;
import com.eressea.rules.Herb;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.ObjectType;
import com.eressea.rules.OptionCategory;
import com.eressea.rules.Race;
import com.eressea.rules.RegionType;
import com.eressea.rules.ShipType;
import com.eressea.rules.SkillCategory;
import com.eressea.rules.SkillType;

/**
 * <p>
 * A class summarizing the static information about a game system (a set of
 * rules).
 * </p>
 * 
 * <p>
 * If internationalization is a concern, implementing sub-classes should ensure
 * that the access-methods to the various collections (<tt>getXXX()</tt>)
 * return their objects not only by their (usually language-independent) id
 * but also by their (laguage-dependent) name as it may be supplied by the
 * user.
 * </p>
 * 
 * <p>
 * If necessary, subclasses could also provide additional access methods to
 * distinguish between an access by id or name.
 * </p>
 * 
 * <p>
 * The methods called getXXX(ID id, boolean add) adds and returns  a new
 * Object.
 * </p>
 */
public interface Rules {
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getRegionTypeIterator();

	/**
	 * get RegionType by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public RegionType getRegionType(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getRaceIterator();

	/**
	 * get Race by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Race getRace(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getShipTypeIterator();

	/**
	 * get ShipType by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ShipType getShipType(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getBuildingTypeIterator();

	/**
	 * get BuildingType by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public BuildingType getBuildingType(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getCastleTypeIterator();

	/**
	 * get CastleType by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public CastleType getCastleType(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getItemTypeIterator();

	/**
	 * get ItemType by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemType getItemType(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getHerbIterator();

	/**
	 * get Herb by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Herb getHerb(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getSkillTypeIterator();

	/**
	 * get SkillType by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getItemCategoryIterator();

	/**
	 * get ItemCategory by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public ItemCategory getItemCategory(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getSkillCategoryIterator();

	/**
	 * get SkillCategory by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getSkillCategory(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getOptionCategoryIterator();

	/**
	 * get OptionCategory by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public OptionCategory getOptionCategory(String id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(ID id);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param add TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(ID id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Iterator getAllianceCategoryIterator();

	/**
	 * get AllianceCategory by (possibly localized) name
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(String id, boolean add);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param id TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public AllianceCategory getAllianceCategory(String id);

	/**
	 * Changes the name of an object identified by the given old name.
	 *
	 * @return the modified object type or null, if no object type is
	 * 		   registered with the specified id.
	 */
	public ObjectType changeName(String from, String to);
}
