/*
 * ReplacerFactory.java
 *
 * Created on 20. Mai 2002, 14:09
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version
 */
public interface ReplacerFactory {
	
	public java.util.Set getReplacers();
	
	public boolean isReplacer(String name);
	
	public Replacer createReplacer(String name);
}

