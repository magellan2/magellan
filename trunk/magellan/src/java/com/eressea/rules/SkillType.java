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

package com.eressea.rules;

import com.eressea.ID;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SkillType extends ObjectType {
	protected SkillCategory category;

	/**
	 * Creates a new SkillType object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public SkillType(ID id) {
		super(id);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public SkillCategory getCategory() {
		return category;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param sc TODO: DOCUMENT ME!
	 */
	public void setCategory(SkillCategory sc) {
		category = sc;

		if(sc != null) {
			sc.addInstance(this);
		}
	}
}
