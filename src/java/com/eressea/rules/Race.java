// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;

import java.util.Map;

import com.eressea.ID;
import com.eressea.util.CollectionFactory;

public class Race extends UnitContainerType {
	private int recruit = 0;
	private float weight = 0;
	private float capacity = 0;
	private Map skillBonuses = null;

	public Race(ID id) {
		super(id);
	}

	public void setRecruitmentCosts(int r) {
		recruit = r;
	}

	public int getRecruitmentCosts() {
		return recruit;
	}

	public void setWeight(float w) {
		weight = w;
	}

	public float getWeight() {
		return weight;
	}

	public void setCapacity(float c) {
		capacity = c;
	}

	public float getCapacity() {
		return capacity;
	}
	
	/**
	 * Returns the bonus this race has on the specified skill.
	 *
	 * @return the bonus for the specified skill or 0, if no
	 * bonus-information is available for this skill.
	 */
	public int getSkillBonus(SkillType skillType) {
		int bonus = 0;
		if (skillBonuses != null) {
			Integer i = (Integer)skillBonuses.get(skillType.getID());
			if (i != null) {
				bonus = i.intValue();
			}
		}
		return bonus;
	}
	
	/**
	 * Sets the bonus this race has on the specified skill.
	 */
	public void setSkillBonus(SkillType skillType, int bonus) {
		if (skillBonuses == null) {
			skillBonuses = CollectionFactory.createHashtable();
		}
		skillBonuses.put(skillType.getID(), new Integer(bonus));
	}
	
	/**
	 * Returns the bonus this race has in certain region terrains.
	 */
	public int getSkillBonus(SkillType skillType, RegionType regionType) {
		// let's do a dirty trick and abuse skillBonuses for these 
		// bonuses too *g*, the index is the regionType id, the value
		// is a map containing the skills like in skillBonuses
		int bonus = 0;
		if (skillBonuses != null) {
			Map m = (Map)skillBonuses.get(regionType.getID());
			if (m != null) {
				Integer i = (Integer)m.get(skillType.getID());
				if (i != null) {
					bonus = i.intValue();
				}
			}
		}
		return bonus;
	}
	
	/**
	 * Sets the bonus this race has in certain region terrains.
	 */
	public void setSkillBonus(SkillType skillType, RegionType regionType, int bonus) {
		// let's do a dirty trick and abuse skillBonuses for these 
		// bonuses too *g*, the index is the regionType id, the value
		// is a map containing the skills like in skillBonuses
		if (skillBonuses == null) {
			skillBonuses = CollectionFactory.createHashtable();
		}
		Map m = (Map)skillBonuses.get(regionType.getID());
		if (m == null) {
			m = CollectionFactory.createHashtable();
			skillBonuses.put(regionType.getID(), m);
		}
		m.put(skillType.getID(), new Integer(bonus));
	}
	
	/**
	 * Indicates whether this Race object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class Race and o's id is equal to the id of this 
	 * Race object.
	 */
	public boolean equals(Object o) {
		return this == o ||
			(o instanceof Race && this.getID().equals(((Race)o).getID()));
	}
	
	/**
	 * Imposes a natural ordering on Race objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Race)o).getID());
	}
}
