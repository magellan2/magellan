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

package com.eressea.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eressea.Skill;
import com.eressea.Unit;
import com.eressea.rules.SkillType;
import com.eressea.util.comparator.SkillComparator;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich K�ster a class for holding statistic information about units and their skills
 * 		   like number of persons with a specified skill or total skillpoints or things like that.
 * 		   Units can be added by a call to the addUnit-Method but not removed.
 */
public class SkillStats {
	/**
	 * Default constructor
	 */
	public SkillStats() {
	}

	/**
	 * Constructor that initialize the internal data with the given units
	 *
	 * @param units TODO: DOCUMENT ME!
	 */
	public SkillStats(List units) {
		for(Iterator iter = units.iterator(); iter.hasNext();) {
			addUnit((Unit) iter.next());
		}
	}

	// maps skillTypes to SkillStorage-Objects
	private Map skillData = CollectionFactory.createHashtable();

	/**
	 * returns a List containing the units with the specified skill at the specified level
	 *
	 * @param skill TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getUnits(Skill skill) {
		SkillStorage skillStorage = (SkillStorage) skillData.get(skill.getSkillType());

		if(skillStorage == null) {
			return CollectionFactory.createLinkedList();
		} else {
			Map levelTable = skillStorage.levelTable;
			UnitVector uv = (UnitVector) levelTable.get(new Integer(skill.getLevel()));

			if(uv == null) {
				return CollectionFactory.createLinkedList();
			} else {
				return uv.units;
			}
		}
	}

	/**
	 * returns the number of persons that master the specified skill at exact that level, specified
	 * in the skill Object. That means, a call with a skill-Object containing let's say skilllevel
	 * 5, will not consider persons, that master this skill at a higher level
	 *
	 * @param skill TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPersonNumber(Skill skill) {
		SkillStorage skillStorage = (SkillStorage) skillData.get(skill.getSkillType());

		if(skillStorage == null) {
			return 0;
		} else {
			Map levelTable = skillStorage.levelTable;
			UnitVector uv = (UnitVector) levelTable.get(new Integer(skill.getLevel()));

			if(uv == null) {
				return 0;
			} else {
				return uv.personCounter;
			}
		}
	}

	/**
	 * returns the total number of days learned yet the specified SkillType
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSkillPointsNumber(SkillType skillType) {
		SkillStorage skillStorage = (SkillStorage) skillData.get(skillType);

		if(skillStorage == null) {
			return 0;
		} else {
			return skillStorage.skillPointCounter;
		}
	}

	/**
	 * just like getSkillPointsNumber(SkillType) but limited to a single level
	 *
	 * @param skill TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSkillPointsNumber(Skill skill) {
		int retVal = 0;

		for(Iterator iter = getUnits(skill).iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			retVal += u.getSkill(skill.getSkillType()).getPoints();
		}

		return retVal;
	}

	/**
	 * returns the total number of skilllevel of the specified SkillType
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSkillLevelNumber(SkillType skillType) {
		SkillStorage skillStorage = (SkillStorage) skillData.get(skillType);

		if(skillStorage == null) {
			return 0;
		} else {
			return skillStorage.skillLevelCounter;
		}
	}

	/**
	 * just like getSkillLevelNumber(SkillType) but limited to a level
	 *
	 * @param skill TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSkillLevelNumber(Skill skill) {
		int retVal = 0;

		for(Iterator iter = getUnits(skill).iterator(); iter.hasNext();) {
			Unit u = (Unit) iter.next();
			retVal += (u.persons * skill.getLevel());
		}

		return retVal;
	}

	/**
	 * returns the total number of persons that master the specified SkillType at any level (also
	 * level 0, as long as they have more than zero skillpoints of this skillType)
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getPersonNumber(SkillType skillType) {
		SkillStorage skillStorage = (SkillStorage) skillData.get(skillType);

		if(skillStorage == null) {
			return 0;
		} else {
			return skillStorage.personCounter;
		}
	}

	/**
	 * returns a sorted Collection containing the existing entries (type Skill) for the specified
	 * SkillType in the internal data. If type == null returns a Collection containing all
	 * existing entries (for all skilltypes)
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getKnownSkills(SkillType type) {
		if(type == null) {
			List v = CollectionFactory.createLinkedList();

			for(Iterator iter = skillData.keySet().iterator(); iter.hasNext();) {
				type = (SkillType) iter.next();

				SkillStorage skillStorage = (SkillStorage) skillData.get(type);

				for(Iterator i = skillStorage.levelTable.keySet().iterator(); i.hasNext();) {
					Integer level = (Integer) i.next();
					v.add(new Skill(type, 1, level.intValue(), 1, false));
				}
			}

			Collections.sort(v, new SkillComparator());

			return v;
		} else {
			SkillStorage skillStorage = (SkillStorage) skillData.get(type);

			if(skillStorage == null) {
				return CollectionFactory.createLinkedList();
			} else {
				Map levelTable = skillStorage.levelTable;
				List v = CollectionFactory.createLinkedList();

				for(Iterator iter = levelTable.keySet().iterator(); iter.hasNext();) {
					int level = ((Integer) iter.next()).intValue();
					v.add(new Skill(type, Skill.getPointsAtLevel(level), level, 1, false));
				}

				Collections.sort(v, new SkillComparator());

				return v;
			}
		}
	}

	/**
	 * returns an Collection containing the known SkillTypes in the internal data.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getKnownSkillTypes() {
		List v = CollectionFactory.createLinkedList();

		for(Iterator iter = skillData.keySet().iterator(); iter.hasNext();) {
			SkillType type = (SkillType) iter.next();

			if(!v.contains(type)) {
				v.add(type);
			}
		}

		Collections.sort(v);

		return v;
	}

	/**
	 * returns the lowest level of the specified skillType known in the internal data
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLowestKnownSkillLevel(SkillType type) {
		SkillStorage skillStorage = (SkillStorage) skillData.get(type);

		if(skillStorage == null) {
			return 0;
		} else {
			Map levelTable = skillStorage.levelTable;
			int retVal = Integer.MAX_VALUE;

			for(Iterator iter = levelTable.keySet().iterator(); iter.hasNext();) {
				int i = ((Integer) iter.next()).intValue();

				if(i < retVal) {
					retVal = i;
				}
			}

			return retVal;
		}
	}

	/**
	 * returns the highest level of the specified skillType known in the internal data
	 *
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getHighestKnownSkillLevel(SkillType type) {
		SkillStorage skillStorage = (SkillStorage) skillData.get(type);

		if(skillStorage == null) {
			return 0;
		} else {
			Map levelTable = skillStorage.levelTable;
			int retVal = Integer.MIN_VALUE;

			for(Iterator iter = levelTable.keySet().iterator(); iter.hasNext();) {
				int i = ((Integer) iter.next()).intValue();

				if(i > retVal) {
					retVal = i;
				}
			}

			return retVal;
		}
	}

	/**
	 * adds a unit to the internal statistics
	 *
	 * @param u TODO: DOCUMENT ME!
	 */
	public void addUnit(Unit u) {
		for(Iterator iter = u.getSkills().iterator(); iter.hasNext();) {
			Skill skill = (Skill) iter.next();
			SkillStorage skillStorage = (SkillStorage) skillData.get(skill.getSkillType());

			if(skillStorage == null) {
				skillStorage = new SkillStorage();
				skillData.put(skill.getSkillType(), skillStorage);
			}

			Map levelTable = skillStorage.levelTable;
			UnitVector uv = (UnitVector) levelTable.get(new Integer(skill.getLevel()));

			if(uv == null) {
				uv = new UnitVector();
				levelTable.put(new Integer(skill.getLevel()), uv);
			}

			uv.units.add(u);
			uv.personCounter += u.persons;
			skillStorage.personCounter += u.persons;
			skillStorage.skillPointCounter += u.getSkill(skill.getSkillType()).getPoints();
			skillStorage.skillLevelCounter += (u.getSkill(skill.getSkillType()).getLevel() * u.persons);
		}
	}

	// inner helper classes
	private class UnitVector {
		int personCounter = 0;
		List units = CollectionFactory.createLinkedList();
	}

	private class SkillStorage {
		// maps level (Integerobjects) to UnitVector-Objects
		Map levelTable = CollectionFactory.createHashtable();
		int skillPointCounter = 0;
		int personCounter = 0;
		int skillLevelCounter = 0;
	}
}
