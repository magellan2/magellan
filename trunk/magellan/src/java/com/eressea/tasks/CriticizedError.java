
package com.eressea.tasks;

import com.eressea.*;

public class CriticizedError extends AbstractProblem implements Problem {

	/** 
	 *
	 */
	public CriticizedError(Object s, HasRegion o, Inspector i, String m) {
		super(s,o,i,m);
	}

	/** 
	 *
	 */
	public CriticizedError(Object s, HasRegion o, Inspector i, String m, int l) {
		super(s,o,i,m,l);
	}

	/** 
	 * returns the type of the problem 
	 */
	public int getType() {
		return ERROR;
	}
}
