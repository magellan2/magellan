
package com.eressea.tasks;

import com.eressea.*;

public class CriticizedInformation extends AbstractProblem implements Problem {
	/** 
	 *
	 */
	public CriticizedInformation(Object s, HasRegion o, Inspector i, String m) {
		super(s,o,i,m);
	}

	public CriticizedInformation(Object s, HasRegion o, Inspector i, String m, int l) {
		super(s,o,i,m,l);
	}

	/** 
	 * returns the type of the problem 
	 */
	public int getType() {
		return INFORMATION;
	}
}
