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

import java.util.Map;

import com.eressea.ID;

import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Race extends UnitContainerType {
	private int   recruit	   = 0;
	private float weight	   = 0;
	private float capacity     = 0;
	private Map   skillBonuses = null;

	/**
	 * Creates a new Race object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public Race(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 */
	public void setRecruitmentCosts(int r) {
		recruit = r;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getRecruitmentCosts() {
		return recruit;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param w TODO: DOCUMENT ME!
	 */
	public void setWeight(float w) {
		weight = w;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param c TODO: DOCUMENT ME!
	 */
	public void setCapacity(float c) {
		capacity = c;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public float getCapacity() {
		return capacity;
	}

	/**
	 * Returns the bonus this race has on the specified skill.
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 *
	 * @return the bonus for the specified skill or 0, if no bonus-information
	 * 		   is available for this skill.
	 */
	public int getSkillBonus(SkillType skillType) {
		int bonus = 0;

		if(skillBonuses != null) {
			Integer i = (Integer) skillBonuses.get(skillType.getID());

			if(i != null) {
				bonus = i.intValue();
			}
		}

		return bonus;
	}

	/**
	 * Sets the bonus this race has on the specified skill.
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 * @param bonus TODO: DOCUMENT ME!
	 */
	public void setSkillBonus(SkillType skillType, int bonus) {
		if(skillBonuses == null) {
			skillBonuses = CollectionFactory.createHashtable();
		}

		skillBonuses.put(skillType.getID(), new Integer(bonus));
	}

	/**
	 * Returns the bonus this race has in certain region terrains.
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 * @param regionType TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSkillBonus(SkillType skillType, RegionType regionType) {
		// let's do a dirty trick and abuse skillBonuses for these 
		// bonuses too *g*, the index is the regionType id, the value
		// is a map containing the skills like in skillBonuses
		int bonus = 0;

		if(skillBonuses != null) {
			Map m = (Map) skillBonuses.get(regionType.getID());

			if(m != null) {
				Integer i = (Integer) m.get(skillType.getID());

				if(i != null) {
					bonus = i.intValue();
				}
			}
		}

		return bonus;
	}

	/**
	 * Sets the bonus this race has in certain region terrains.
	 *
	 * @param skillType TODO: DOCUMENT ME!
	 * @param regionType TODO: DOCUMENT ME!
	 * @param bonus TODO: DOCUMENT ME!
	 */
	public void setSkillBonus(SkillType skillType, RegionType regionType,
							  int bonus) {
		// let's do a dirty trick and abuse skillBonuses for these 
		// bonuses too *g*, the index is the regionType id, the value
		// is a map containing the skills like in skillBonuses
		if(skillBonuses == null) {
			skillBonuses = CollectionFactory.createHashtable();
		}

		Map m = (Map) skillBonuses.get(regionType.getID());

		if(m == null) {
			m = CollectionFactory.createHashtable();
			skillBonuses.put(regionType.getID(), m);
		}

		m.put(skillType.getID(), new Integer(bonus));
	}

	/**
	 * Indicates whether this Race object is equal to another object. Returns
	 * true only if o is not null and an instance of class Race and o's id is
	 * equal to the id of this  Race object.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean equals(Object o) {
		return (this == o) ||
			   (o instanceof Race && this.getID().equals(((Race) o).getID()));
	}

	/**
	 * Imposes a natural ordering on Race objects equivalent to the natural
	 * ordering of their ids.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((Race) o).getID());
	}
}
