/*
 * AbstractParameterReplacer.java
 *
 * Created on 29. Dezember 2001, 16:17
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version
 */
public abstract class AbstractParameterReplacer implements ParameterReplacer {
	
	protected Object parameters[];
	
	protected AbstractParameterReplacer(int parameters) {
		this.parameters = new Object[parameters];
	}
	
	public int getParameterCount() {
		return parameters.length;
	}
	
	public void setParameter(int param, java.lang.Object obj) {
		parameters[param] = obj;
	}
	
	protected Object getParameter(int index, Object o) {
		if (parameters[index] != null) {
			if (parameters[index] instanceof Replacer) {
				return ((Replacer)parameters[index]).getReplacement(o);
			}			
		}
		return parameters[index];
	}	
	
	public String getDescription() {
		return null;
	}	
}
