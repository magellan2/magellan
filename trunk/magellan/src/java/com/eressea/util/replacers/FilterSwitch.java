// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import com.eressea.Unit;
import com.eressea.util.filters.UnitFilter;
/**
 *
 * @author  Andreas
 * @version 
 */
public class FilterSwitch implements ParameterReplacer, BranchReplacer, EnvironmentDependent {
	
	protected Object branch;
	protected ReplacerEnvironment env;
	protected UnitFilter myFilter;
	
	/** Creates new Template */
	public FilterSwitch() {
	}

	public void setParameter(int index, Object obj) {
		createFilter(obj);
	}
	
	public int getParameterCount() {
		return 1;
	}
		
	public String getBranchSign(int index) {
		return END;
	}
	
	public void setBranch(int index, Object obj) {
		branch = obj;
	}
	
	public int getBranchCount() {
		return 1;
	}
	
	public Object getReplacement(Object o) {
		if (branch != null) {
			if (branch instanceof Replacer) {
				Replacer r = (Replacer)branch;
				if (myFilter != null && env != null && env.getPart(ReplacerEnvironment.UNITSELECTION_PART) != null) {
					UnitSelection us = (UnitSelection)env.getPart(ReplacerEnvironment.UNITSELECTION_PART);
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
	
	public void setEnvironment(ReplacerEnvironment env) {
		this.env = env;
	}
	
	public String getDescription() {
		return null;
	}
	
	protected void createFilter(Object o) {
		myFilter = new MyFilterClass(o);
	}
	
	protected class MyFilterClass extends UnitFilter {
		
		int always = 0;
		protected Replacer rep = null;
		
		public MyFilterClass(Object o) {
			if (o instanceof Replacer) {
				rep = (Replacer)o;				
			} else {
				if (o.toString().equals(TRUE)) {
					always = 1;
				} else {
					always = 2;
				}
			}
		}
		
		public boolean acceptUnit(Unit u) {
			if (always != 0) {
				return always == 1;
			}
			try{
				String s = rep.getReplacement(u).toString();
				return s.equals(TRUE);
			}catch(Exception exc) {}
			return false;
		}		
	}
}
