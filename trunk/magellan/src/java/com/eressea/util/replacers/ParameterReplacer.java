/*
 * ParameterReplacer.java
 *
 * Created on 20. Mai 2002, 14:11
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version 
 */
public interface ParameterReplacer extends Replacer {

	public int getParameterCount();
	
	public void setParameter(int index, Object obj);
}

