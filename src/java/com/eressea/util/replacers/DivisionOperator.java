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

package com.eressea.util.replacers;

import java.util.Map;

import com.eressea.util.CollectionFactory;

/**
 * An division operator.
 * 
 * <p>
 * A Divide By Zero returns <i>null</i>.
 * </p>
 *
 * @author Andreas
 * @version
 */
public class DivisionOperator extends AbstractOperator {
	/**
	 * Creates a new DivisionOperator object.
	 */
	public DivisionOperator() {
		super(2);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param numbers TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object compute(Object numbers[]) {
		if(((Number) numbers[1]).floatValue() == 0) {
			return null;
		}

		return new Float(((Number) numbers[0]).floatValue() / ((Number) numbers[1]).floatValue());
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("description",
									"Divides the following two defintion elements (developing other operators thereby).");
		}

		return defaultTranslations;
	}
}
