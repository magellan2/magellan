// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;

import java.util.Iterator;

import com.eressea.rules.BuildingType;
import com.eressea.rules.CastleType;

import com.eressea.rules.AllianceCategory;
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
 * <p>A class summarizing the static information about a game system
 * (a set of rules).</p>
 *
 * <p>If internationalization is a concern, implementing sub-classes
 * should ensure that the access-methods to the various collections
 * (<tt>getXXX()</tt>) return their objects not only by their (usually
 * language-independent) id but also by their (laguage-dependent)
 * name as it may be supplied by the user.</p>
 * <p>If necessary, subclasses could also provide additional access
 * methods to distinguish between an access by id or name.</p>
 * <p>The methods called getXXX(ID id, boolean add) adds and returns 
 * a new Object.</p>
 */
public interface Rules {
	public RegionType getRegionType(ID id, boolean add);
	public RegionType getRegionType(ID id);
 	public Iterator getRegionTypeIterator();
	/**
	 * get RegionType by (possibly localized) name 
	 */
	public RegionType getRegionType(String id, boolean add);
	public RegionType getRegionType(String id);

	public Race getRace(ID id);
	public Race getRace(ID id, boolean add);
	public Iterator getRaceIterator();
	/**
	 * get Race by (possibly localized) name 
	 */
	public Race getRace(String id, boolean add);
	public Race getRace(String id);

	public ShipType getShipType(ID id);
	public ShipType getShipType(ID id, boolean add);
	public Iterator getShipTypeIterator();
	/**
	 * get ShipType by (possibly localized) name 
	 */
	public ShipType getShipType(String id, boolean add);
	public ShipType getShipType(String id);

	public BuildingType getBuildingType(ID id);
	public BuildingType getBuildingType(ID id, boolean add);
	public Iterator getBuildingTypeIterator();
	/**
	 * get BuildingType by (possibly localized) name 
	 */
	public BuildingType getBuildingType(String id, boolean add);
	public BuildingType getBuildingType(String id);

	public CastleType getCastleType(ID id);
	public CastleType getCastleType(ID id, boolean add);
	public Iterator getCastleTypeIterator();
	/**
	 * get CastleType by (possibly localized) name 
	 */
	public CastleType getCastleType(String id, boolean add);
	public CastleType getCastleType(String id);


	public ItemType getItemType(ID id);
	public ItemType getItemType(ID id, boolean add);
	public Iterator getItemTypeIterator();
	/**
	 * get ItemType by (possibly localized) name 
	 */
	public ItemType getItemType(String id, boolean add);
	public ItemType getItemType(String id);

	public Herb getHerb(ID id);
	public Herb getHerb(ID id, boolean add);
	public Iterator getHerbIterator();
	/**
	 * get Herb by (possibly localized) name 
	 */
	public Herb getHerb(String id, boolean add);
	public Herb getHerb(String id);


	public SkillType getSkillType(ID id);
	public SkillType getSkillType(ID id, boolean add);
	public Iterator getSkillTypeIterator();
	/**
	 * get SkillType by (possibly localized) name 
	 */
	public SkillType getSkillType(String id, boolean add);
	public SkillType getSkillType(String id);


	public ItemCategory getItemCategory(ID id);
	public ItemCategory getItemCategory(ID id, boolean add);
	public Iterator getItemCategoryIterator();
	/**
	 * get ItemCategory by (possibly localized) name 
	 */
	public ItemCategory getItemCategory(String id, boolean add);
	public ItemCategory getItemCategory(String id);


	public SkillCategory getSkillCategory(ID id);
	public SkillCategory getSkillCategory(ID id, boolean add);
	public Iterator getSkillCategoryIterator();
	/**
	 * get SkillCategory by (possibly localized) name 
	 */
	public SkillCategory getSkillCategory(String id, boolean add);
	public SkillCategory getSkillCategory(String id);



	public OptionCategory getOptionCategory(ID id);
	public OptionCategory getOptionCategory(ID id, boolean add);
	public Iterator getOptionCategoryIterator();
	/**
	 * get OptionCategory by (possibly localized) name 
	 */
	public OptionCategory getOptionCategory(String id, boolean add);
	public OptionCategory getOptionCategory(String id);

	public AllianceCategory getAllianceCategory(ID id);
	public AllianceCategory getAllianceCategory(ID id, boolean add);
	public Iterator getAllianceCategoryIterator();
	/**
	 * get AllianceCategory by (possibly localized) name 
	 */
	public AllianceCategory getAllianceCategory(String id, boolean add);
	public AllianceCategory getAllianceCategory(String id);

	/**
	 * Changes the name of an object identified by the given old name.
	 *
	 * @return the modified object type or null, if no object type is
	 * registered with the specified id.
	 */
	public ObjectType changeName(String from, String to);
}
