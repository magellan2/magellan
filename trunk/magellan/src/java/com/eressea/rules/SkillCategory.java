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

/*
 * ItemCategory.java
 *
 * Created on 9. März 2002, 20:39
 */
package com.eressea.rules;

import com.eressea.ID;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class SkillCategory extends Category {
	/**
	 * Creates new ItemCategory
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public SkillCategory(ID id) {
		super(id);
	}

	/**
	 * Creates a new SkillCategory object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	public SkillCategory(ID id, Category parent) {
		super(id, parent);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isInstance(Object o) {
		if(o instanceof SkillType) {
			SkillType st = (SkillType) o;

			if(st.getCategory() != null) {
				return st.getCategory().isDescendant(this);
			}
		}

		return false;
	}
}
