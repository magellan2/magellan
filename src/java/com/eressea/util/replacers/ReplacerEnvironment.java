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

package com.eressea.util.replacers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class ReplacerEnvironment {
	/** TODO: DOCUMENT ME! */
	public static final String OPERATION_PART = "Op";

	/** TODO: DOCUMENT ME! */
	public static final String UNITSELECTION_PART = "Unit";
	private Map				   parts;

	/**
	 * Creates new ReplacerEnvironment
	 */
	public ReplacerEnvironment() {
		parts = new HashMap();

		// put some default parts
		parts.put(OPERATION_PART, new OperationMode());
		parts.put(UNITSELECTION_PART, new UnitSelection());
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param part TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public EnvironmentPart getPart(String part) {
		return (EnvironmentPart) parts.get(part);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param name TODO: DOCUMENT ME!
	 * @param part TODO: DOCUMENT ME!
	 */
	public void setPart(String name, EnvironmentPart part) {
		parts.put(name, part);
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void reset() {
		Iterator it = parts.values().iterator();

		while(it.hasNext()) {
			((EnvironmentPart) it.next()).reset();
		}
	}
}
