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

/*
 * IfBranchReplacer.java
 *
 * Created on 20. Mai 2002, 17:05
 */
package com.eressea.util.replacers;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class IfBranchReplacer implements BranchReplacer, ParameterReplacer {
	protected Object criterion;
	protected Object branches[] = new Object[2];

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getBranchSign(int index) {
		if(index == 1) {
			return NEXT_BRANCH;
		}

		return END;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 * @param obj TODO: DOCUMENT ME!
	 */
	public void setBranch(int index, Object obj) {
		branches[index] = obj;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getBranchCount() {
		return 2;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getReplacement(Object o) {
		if(criterion != null) {
			String ret = null;

			if(criterion instanceof Replacer) {
				try {
					ret = ((Replacer) criterion).getReplacement(o).toString();
				} catch(Exception exc) {
					return null;
				}
			} else {
				ret = criterion.toString();
			}

			int index = 1;

			if(ret.equals(TRUE)) {
				index = 0;
			}

			if(branches[index] == null) {
				return BLANK;
			}

			if(branches[index] instanceof Replacer) {
				return ((Replacer) branches[index]).getReplacement(o);
			}

			return branches[index];
		}

		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getParameterCount() {
		return 1;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 * @param obj TODO: DOCUMENT ME!
	 */
	public void setParameter(int index, Object obj) {
		criterion = obj;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		try {
			return "if " + criterion + " then " + branches[0] + " else " +
				   branches[1];
		} catch(Exception exc) {
		}

		return "IfReplacer";
	}
}
