// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.rules;


import com.eressea.ID;

public class SkillType extends ObjectType {
	
	protected SkillCategory category;

	public SkillType(ID id) {
		super(id);
	}
	
	/**
	 * Indicates whether this SkillType object is equal to another
	 * object. Returns true only if o is not null and an instance of
	 * class SkillType and o's id is equal to the id of this 
	 * SkillType object.
	 */
	public boolean equals(Object o) {
		return this == o ||
			(o instanceof SkillType &&  this.getID().equals(((SkillType)o).getID()));
	}
	
	/**
	 * Imposes a natural ordering on SkillType objects equivalent to
	 * the natural ordering of their ids.
	 */
	public int compareTo(Object o) {
		return this.getID().compareTo(((SkillType)o).getID());
	}
	
	public SkillCategory getCategory() {
		return category;
	}
	
	public void setCategory(SkillCategory sc) {
		category = sc;
		if (sc != null) {
			sc.addInstance(this);
		}
	}
}
