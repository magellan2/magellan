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
	
	public Race addRace(Race t);
	public Race getRace(ID id);
	public Race getRace(ID id, boolean add);

	/**
	 * get race by (possibly localized) name 
	 */
	public Race getRace(String name);
	public Iterator getRaces();

	public ShipType addShipType(ShipType t);
	public ShipType getShipType(ID id, boolean add);
	public ShipType getShipType(ID id);
	/**
	 * get ShipType by (possibly localized) name 
	 */
	public ShipType getShipType(String name);
	public Iterator getShipTypes();

	public BuildingType addBuildingType(BuildingType t);
	public BuildingType getBuildingType(ID id, boolean add);
	public BuildingType getBuildingType(ID id);
	/**
	 * get BuildingType by (possibly localized) name 
	 */
	public BuildingType getBuildingType(String name);
	public Iterator getBuildingTypes();

	public RegionType addRegionType(RegionType t);
	public RegionType getRegionType(ID id, boolean add);
	public RegionType getRegionType(ID id);
	/**
	 * get RegionType by (possibly localized) name 
	 */
	public RegionType getRegionType(String name);
	public Iterator getRegionTypes();

	public ItemType addItemType(ItemType t);
	public ItemType getItemType(ID id, boolean add);
	public ItemType getItemType(ID id);
	/**
	 * get ItemType by (possibly localized) name 
	 */
	public ItemType getItemType(String name);
	public Iterator getItemTypes();

	public SkillType addSkillType(SkillType t);
	public SkillType getSkillType(ID id, boolean add);
	public SkillType getSkillType(ID id);
	/**
	 * get SkillType by (possibly localized) name 
	 */
	public SkillType getSkillType(String name);
	public Iterator getSkillTypes();

	public ItemCategory addItemCategory(ItemCategory t);
	public ItemCategory getItemCategory(ID id, boolean add);
	public ItemCategory getItemCategory(ID id);
	public Iterator getItemCategories();
	
	public SkillCategory addSkillCategory(SkillCategory t);
	public SkillCategory getSkillCategory(ID id, boolean add);
	public SkillCategory getSkillCategory(ID id);
	public Iterator getSkillCategories();

	public OptionCategory getOptionCategory(ID id, boolean add);
	public Iterator getOptionCategories();


	/**
	 * Changes the name of an object identified by the given old name.
	 *
	 * @return the modified object type or null, if no object type is
	 * registered with the specified id.
	 */
	public ObjectType changeName(String from, String to);
}
