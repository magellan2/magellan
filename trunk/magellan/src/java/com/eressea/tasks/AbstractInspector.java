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

package com.eressea.tasks;

import java.util.Collections;
import java.util.List;

import com.eressea.Region;
import com.eressea.Unit;
import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public abstract class AbstractInspector implements Inspector {
	protected AbstractInspector() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewUnit(Unit u) {
		List problems = CollectionFactory.createArrayList(10);

		problems.addAll(reviewUnit(u, Problem.INFORMATION));
		problems.addAll(reviewUnit(u, Problem.WARNING));
		problems.addAll(reviewUnit(u, Problem.ERROR));

		return problems.isEmpty() ? Collections.EMPTY_LIST : problems;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param u TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewUnit(Unit u, int type) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewRegion(Region r) {
		List problems = CollectionFactory.createArrayList(2);
		problems.addAll(reviewRegion(r, Problem.INFORMATION));
		problems.addAll(reviewRegion(r, Problem.WARNING));
		problems.addAll(reviewRegion(r, Problem.ERROR));

		return problems.isEmpty() ? Collections.EMPTY_LIST : problems;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param r TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List reviewRegion(Region r, int type) {
		return Collections.EMPTY_LIST;
	}
}
