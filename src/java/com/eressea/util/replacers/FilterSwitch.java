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

import com.eressea.Unit;
import com.eressea.util.filters.UnitFilter;

/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class FilterSwitch implements ParameterReplacer, BranchReplacer, EnvironmentDependent {
	protected Object branch;
	protected ReplacerEnvironment env;
	protected UnitFilter myFilter;

	/**
	 * Creates new Template
	 */
	public FilterSwitch() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 * @param obj TODO: DOCUMENT ME!
	 */
	public void setParameter(int index, Object obj) {
		createFilter(obj);
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
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getBranchSign(int index) {
		return END;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param index TODO: DOCUMENT ME!
	 * @param obj TODO: DOCUMENT ME!
	 */
	public void setBranch(int index, Object obj) {
		branch = obj;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getBranchCount() {
		return 1;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getReplacement(Object o) {
		if(branch != null) {
			if(branch instanceof Replacer) {
				Replacer r = (Replacer) branch;

				if((myFilter != null) && (env != null) &&
					   (env.getPart(ReplacerEnvironment.UNITSELECTION_PART) != null)) {
					UnitSelection us = (UnitSelection) env.getPart(ReplacerEnvironment.UNITSELECTION_PART);
					us.addFilter(myFilter);

					Object obj = r.getReplacement(o);
					us.removeFilter(myFilter);

					return obj;
				}

				return r.getReplacement(o);
			}

			return BLANK;
		}

		return BLANK;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param env TODO: DOCUMENT ME!
	 */
	public void setEnvironment(ReplacerEnvironment env) {
		this.env = env;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getDescription() {
		return null;
	}

	protected void createFilter(Object o) {
		myFilter = new MyFilterClass(o);
	}

	protected class MyFilterClass extends UnitFilter {
		int always = 0;
		protected Replacer rep = null;

		/**
		 * Creates a new MyFilterClass object.
		 *
		 * @param o TODO: DOCUMENT ME!
		 */
		public MyFilterClass(Object o) {
			if(o instanceof Replacer) {
				rep = (Replacer) o;
			} else {
				if(o.toString().equals(TRUE)) {
					always = 1;
				} else {
					always = 2;
				}
			}
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @param u TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean acceptUnit(Unit u) {
			if(always != 0) {
				return always == 1;
			}

			try {
				String s = rep.getReplacement(u).toString();

				return s.equals(TRUE);
			} catch(Exception exc) {
			}

			return false;
		}
	}
}
