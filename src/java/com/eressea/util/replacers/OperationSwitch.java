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

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class OperationSwitch extends AbstractParameterReplacer
	implements EnvironmentDependent, SwitchOnly
{
	protected ReplacerEnvironment environment;

	/**
	 * Creates new FactionSwitch
	 */
	public OperationSwitch() {
		super(1);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this, "description");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param src TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getReplacement(Object src) {
		try {
			String fName = getParameter(0, src).toString();
			((OperationMode) environment.getPart(ReplacerEnvironment.OPERATION_PART)).setNullEqualsZero(fName.equals("true"));
		} catch(NullPointerException npe) {
		}

		return BLANK;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param env TODO: DOCUMENT ME!
	 */
	public void setEnvironment(ReplacerEnvironment env) {
		environment = env;
	}
}
