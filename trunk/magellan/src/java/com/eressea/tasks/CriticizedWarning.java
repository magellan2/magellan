
package com.eressea.tasks;

import com.eressea.HasRegion;

public class CriticizedWarning extends AbstractProblem implements Problem {
	/** 
	 *
	 */
	public CriticizedWarning(Object s,HasRegion o, Inspector i, String m) {
		super(s, o,i,m);
	}

	public CriticizedWarning(Object s, HasRegion o, Inspector i, String m, int l) {
		super(s,o,i,m,l);
	}

	/** 
	 * returns the type of the problem 
	 */
	public int getType() {
		return WARNING;
	}
}
