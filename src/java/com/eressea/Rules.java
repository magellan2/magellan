// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea;


import com.eressea.rules.BuildingType;
import com.eressea.rules.ItemCategory;
import com.eressea.rules.ItemType;
import com.eressea.rules.Race;
import com.eressea.rules.RegionType;
import com.eressea.rules.ShipType;
import com.eressea.rules.SkillCategory;
import com.eressea.rules.SkillType;
import com.eressea.util.ROIterator;

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
 */
abstract public class Rules {
	public int version = 0;

	abstract public Race addRace(Race t);
	abstract public Race getRace(ID id);
	abstract public Race getRace(ID id, boolean add);
	abstract public ROIterator getRaces();

	abstract public ShipType addShipType(ShipType t);
	abstract public ShipType getShipType(ID id);
	abstract public ShipType getShipType(ID id, boolean add);
	abstract public ROIterator getShipTypes();

	abstract public BuildingType addBuildingType(BuildingType t);
	abstract public BuildingType getBuildingType(ID id);
	abstract public BuildingType getBuildingType(ID id, boolean add);
	abstract public ROIterator getBuildingTypes();

	abstract public RegionType addRegionType(RegionType t);
	abstract public RegionType getRegionType(ID id);
	abstract public RegionType getRegionType(ID id, boolean add);
	abstract public ROIterator getRegionTypes();

	abstract public ItemType addItemType(ItemType t);
	abstract public ItemType getItemType(ID id);
	abstract public ItemType getItemType(ID id, boolean add);
	abstract public ROIterator getItemTypes();

	abstract public SkillType addSkillType(SkillType t);
	abstract public SkillType getSkillType(ID id);
	abstract public SkillType getSkillType(ID id, boolean add);
	abstract public ROIterator getSkillTypes();
	abstract public int getSkillCost(SkillType type, Unit unit);
	abstract public int getSkillCost(SkillType type);

	abstract public ItemCategory addItemCategory(ItemCategory t);
	abstract public ItemCategory getItemCategory(ID id);
	abstract public ItemCategory getItemCategory(ID id, boolean add);
	abstract public ROIterator getItemCategories();
	
	abstract public SkillCategory addSkillCategory(SkillCategory t);
	abstract public SkillCategory getSkillCategory(ID id);
	abstract public SkillCategory getSkillCategory(ID id, boolean add);
	abstract public ROIterator getSkillCategories();
}
