/*
 * SimpleStringReplacer.java
 *
 * Created on 19. Mai 2002, 12:13
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version
 */
public class SimpleStringReplacer implements Replacer {
	
	protected String string;
	
	/** Creates new SimpleStringReplacer */
	public SimpleStringReplacer(String string) {
		this.string = string;
	}
	
	public Object getReplacement(Object o) {
		return string;
	}
	
	public String getDescription() {
		return "simple string";
	}
	
}
