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
public class NotReplacer extends AbstractParameterReplacer {
	
	/** Creates new NotReplacer */
	public NotReplacer() {
		super(1);
	}
	
	public Object getReplacement(Object o) {
		Object obj = getParameter(0, o);
		if (obj != null) {
			if (TRUE.equals(obj.toString())) {
				return FALSE;
			}
			if (FALSE.equals(obj.toString())) {
				return TRUE;
			}
		}
		return null;
	}
	
}
