/*
 * AbstractParameterSwitch.java
 *
 * Created on 20. Mai 2002, 14:47
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version
 */
public abstract class AbstractParameterSwitch extends AbstractSwitch implements ParameterReplacer {
	
	protected Object parameters[];
	
	/** Creates new AbstractParameterSwitch */
	public AbstractParameterSwitch(int parameters) {
		this.parameters = new Object[parameters];
	}
	
	public int getParameterCount() {
		return parameters.length;
	}
	
	public void setParameter(int index, Object obj) {
		parameters[index] = obj;
	}
	
	protected Object getParameter(int index, Object o) {
		if (parameters[index] != null) {
			if (parameters[index] instanceof Replacer) {
				return ((Replacer)parameters[index]).getReplacement(o);
			}			
		}
		return parameters[index];
	}	
}
