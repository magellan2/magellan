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

package com.eressea.util.replacers;

/**
 * A replacer that defines a operation with the following definition elements treated as numbers.
 * To support complex formulae it is necessary to use Polish Notation that is Postfix-Notation.
 *
 * @author Andreas
 * @version 1.0
 */
public abstract class AbstractOperator extends AbstractParameterReplacer
	implements EnvironmentDependent
{
	protected Object numbers[];
	protected Float evolved = null;
	protected static final Float ZERO = new Float(0);
	protected ReplacerEnvironment environment;

	protected AbstractOperator(int params) {
		super(params);
		numbers = new Object[params];
	}

	// try to compute the operation to save time
	public void setParameter(int param, Object obj) {
		evolved = null;
		super.setParameter(param, obj);

		if(param == (numbers.length - 1)) { // all parameters set

			try {
				evolved = (Float) getReplacement(null);
			} catch(RuntimeException exc) {
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param numbers TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract Object compute(Object numbers[]);

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
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getReplacement(Object o) {
		if(evolved != null) {
			return evolved;
		}

		boolean nullAsZero = ((OperationMode) environment.getPart(ReplacerEnvironment.OPERATION_PART)).isNullEqualsZero();

		for(int i = 0; i < numbers.length; i++) {
			Object param = getParameter(i, o);

			if(param == null) {
				if(!nullAsZero) {
					return null;
				}

				param = ZERO;
			}

			if(!(param instanceof Number)) {
				try {
					Float fl = Float.valueOf(param.toString());
					numbers[i] = fl;
				} catch(NumberFormatException exc) {
					return null;
				}
			} else {
				numbers[i] = param;
			}
		}

		// now all parameters are Number objects
		return compute(numbers);
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
