/*
 * IfBranchReplacer.java
 *
 * Created on 20. Mai 2002, 17:05
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version
 */
public class IfBranchReplacer implements BranchReplacer, ParameterReplacer {
	
	protected Object criterion, branches[] = new Object[2];
	
	public String getBranchSign(int index) {
		if (index == 1) {
			return NEXT_BRANCH;
		}
		return END;
	}
	
	public void setBranch(int index, Object obj) {
		branches[index] = obj;
	}
	
	public int getBranchCount() {
		return 2;
	}
	
	public Object getReplacement(Object o) {
		if (criterion != null) {
			String ret = null;
			if (criterion instanceof Replacer) {
				try{
					ret = ((Replacer)criterion).getReplacement(o).toString();
				} catch(Exception exc) {
					return null;
				}
			} else {
				ret = criterion.toString();
			}
			int index = 1;
			if (ret.equals(TRUE)) {
				index = 0;
			}
			if (branches[index] == null) {
				return BLANK;
			}
			if (branches[index] instanceof Replacer) {
				return ((Replacer)branches[index]).getReplacement(o);
			}
			return branches[index];
		}
		return null;
	}
	
	public String getDescription() {
		return null;
	}
	
	public int getParameterCount() {
		return 1;
	}
	
	public void setParameter(int index, Object obj) {
		criterion = obj;
	}
	
	public String toString() {
		try{
			return "if "+criterion+" then "+branches[0]+" else "+branches[1];
		}catch(Exception exc) {}
		return "IfReplacer";
	}
}
