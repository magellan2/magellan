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

import com.eressea.rules.Race;
import com.eressea.rules.RegionType;
import com.eressea.rules.SkillType;

/**
 * A class representing a certain skill level of a unit for some skill type.
 * Since there is no strict mapping of skill points to skill levels, this
 * class allows to specify points and levels independently of each other but
 * also offers convenience functions for converting the values between each
 * other.
 */
public class Skill {
	private final SkillType type;
	private final boolean   noSkillPoints;

	/**
	 * The total of points_per_person  persons as it is found in the report.
	 * Note, that the report can contain skill point values that are not
	 * dividable by the unit's number of persons (e.g. 65 skill point with a
	 * unit of 2 persons)
	 */
	private int points = 0;
	private int level = 0;

	/** The number of persons in the unit this skill belongs to. */
	private int persons = 0;

	/** The level of change. Only important in merged reports. */
	private int changeLevel = 0;

	/** Holds value of property levelChanged. */
	private boolean levelChanged;

	/**
	 * Creates a new Skill object.
	 *
	 * @param type TODO: DOCUMENT ME!
	 * @param points TODO: DOCUMENT ME!
	 * @param level TODO: DOCUMENT ME!
	 * @param persons TODO: DOCUMENT ME!
	 * @param noSkillPoints TODO: DOCUMENT ME!
	 */
	public Skill(SkillType type, int points, int level, int persons,
				 boolean noSkillPoints) {
		this.type		   = type;
		this.points		   = points;
		this.level		   = level;
		this.persons	   = persons;
		this.noSkillPoints = noSkillPoints;
	}

	/**
	 * Returns the skill points required to reach the specified level.
	 *
	 * @param level TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static final int getPointsAtLevel(int level) {
		return 30 * (((level + 1) * level) / 2);
	}

	/**
	 * Returns the skill level gained at the specified number of skill points.
	 *
	 * @param points TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static final int getLevelAtPoints(int points) {
		int i = 1;

		while(getPointsAtLevel(i) <= points) {
			++i;
		}

		return i - 1;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param pointsPerPerson TODO: DOCUMENT ME!
	 * @param raceBonus TODO: DOCUMENT ME!
	 * @param terrainBonus TODO: DOCUMENT ME!
	 * @param buildingBonus TODO: DOCUMENT ME!
	 * @param isStarving TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static final int getLevel(int pointsPerPerson, int raceBonus,
									 int terrainBonus, int buildingBonus,
									 boolean isStarving) {
		int baseLevel = getLevelAtPoints(pointsPerPerson);
		int level = 0;

		if(baseLevel > 0) {
			level = Math.max(baseLevel + raceBonus + terrainBonus, 0);
		}

		if(level > 0) {
			level = Math.max(level + buildingBonus, 0);
		}

		if(isStarving) {
			level /= 2;
		}

		return level;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param includeBuilding TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLevel(Unit unit, boolean includeBuilding) {
		if((unit != null) && (unit.persons != 0)) {
			int raceBonus     = 0;
			int terrainBonus  = 0;
			int buildingBonus = 0;

			if(unit.realRace != null) {
				raceBonus = unit.realRace.getSkillBonus(getType());
			} else {
				if(unit.race != null) {
					raceBonus = unit.race.getSkillBonus(getType());
				}
			}

			if(unit.getRegion() != null) {
				terrainBonus = unit.race.getSkillBonus(getType(),
													   unit.getRegion()
														   .getRegionType());
			}

			if(includeBuilding && (unit.getBuilding() != null)) {
				buildingBonus = (unit.getBuilding().getBuildingType()).getSkillBonus(getType());
			}

			return getLevel(getPoints() / unit.persons, raceBonus,
							terrainBonus, buildingBonus, unit.isStarving);
		}

		return 0;
	}

	/**
	 * Calculates the skill level for the given modified number of persons in
	 * the unit and the skill points of this skill.
	 *
	 * @param unit TODO: DOCUMENT ME!
	 * @param includeBuilding TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getModifiedLevel(Unit unit, boolean includeBuilding) {
		if((unit != null) && (unit.getModifiedPersons() != 0)) {
			int raceBonus     = 0;
			int terrainBonus  = 0;
			int buildingBonus = 0;

			if(unit.race != null) {
				raceBonus = unit.race.getSkillBonus(getType());
			}

			if(unit.getRegion() != null) {
				terrainBonus = unit.race.getSkillBonus(getType(),
													   unit.getRegion()
														   .getRegionType());
			}

			if(includeBuilding && (unit.getBuilding() != null)) {
				buildingBonus = unit.getBuilding().getBuildingType()
									.getSkillBonus(getType());
			}

			return getLevel(getPoints() / unit.getModifiedPersons(), raceBonus,
							terrainBonus, buildingBonus, unit.isStarving);
		}

		return 0;
	}

	/**
	 * Returns the modifier of the specified race has on the specified skill in
	 * the specified terrain.
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 * @param race TODO: DOCUMENT ME!
	 * @param terrain TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static int getModifier(SkillType skillType, Race race,
								  RegionType terrain) {
		int modifier = 0;

		if(race != null) {
			modifier += race.getSkillBonus(skillType);

			if(terrain != null) {
				modifier += race.getSkillBonus(skillType, terrain);
			}
		}

		return modifier;
	}

	/**
	 * Returns the modifier of the specified unit's race has on the specified
	 * skill in the terrain the specified unit resides in.
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 * @param unit TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static int getModifier(SkillType skillType, Unit unit) {
		Race	   race    = (unit.realRace != null) ? unit.realRace : unit.race;
		RegionType terrain = (unit.getRegion() != null)
							 ? unit.getRegion().getRegionType() : null;

		return getModifier(skillType, race, terrain);
	}

	/**
	 * Returns the modifier of the specified unit's race has on this skill in
	 * the terrain the specified unit resides in.
	 *
	 * @param unit TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getModifier(Unit unit) {
		return getModifier(this.type, unit);
	}

	/**
	 * Indicated whether the skill points value of this Skill object has
	 * relevance, i.e. was either read from a report or calculated as there
	 * can be reports with only skill levels and no points.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean noSkillPoints() {
		return this.noSkillPoints;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillType getSkillType() {
		return type;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 *
	 * @deprecated
	 */
	public SkillType getType() {
		return getSkillType();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getName() {
		return type.getName();
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param l TODO: DOCUMENT ME!
	 */
	public void setLevel(int l) {
		level = l;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLevel() {
		return Math.max(0, level);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param d TODO: DOCUMENT ME!
	 */
	public void setPoints(int d) {
		points = d;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p TODO: DOCUMENT ME!
	 */
	public void setPersons(int p) {
		this.persons = p;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPointsPerPerson() {
		if(persons != 0) {
			return this.getPoints() / persons;
		} else {
			return 0;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getChangeLevel() {
		return changeLevel;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param change TODO: DOCUMENT ME!
	 */
	public void setChangeLevel(int change) {
		changeLevel = change;

		if(changeLevel != 0) {
			setLevelChanged(true);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		if(this.noSkillPoints()) {
			return getName() + " " + getLevel();
		} else {
			return getName() + " " + getLevel() + " [" + getPointsPerPerson() +
				   "]";
		}
	}

	/**
	 * Getter for property levelChanged.
	 *
	 * @return Value of property levelChanged.
	 */
	public boolean isLevelChanged() {
		return levelChanged;
	}

	/**
	 * Setter for property levelChanged.
	 *
	 * @param levelChanged New value of property levelChanged.
	 */
	public void setLevelChanged(boolean levelChanged) {
		this.levelChanged = levelChanged;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isLostSkill() {
		return level < 0;
	}
}
