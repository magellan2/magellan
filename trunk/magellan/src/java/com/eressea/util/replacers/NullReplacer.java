/*
 * NotReplacer.java
 *
 * Created on 21. Mai 2002, 17:24
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version
 */
public class NullReplacer extends AbstractParameterReplacer {
	
	public final static String TRUE = "true";
	public final static String FALSE = "false";
	
	/** Creates new NotReplacer */
	public NullReplacer() {
		super(1);
	}
	
	public Object getReplacement(Object o) {
		Object obj = getParameter(0, o);
		if (obj == null) {
			return TRUE;
		}
		return FALSE;
	}
	
}
