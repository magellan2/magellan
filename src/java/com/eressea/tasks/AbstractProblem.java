
package com.eressea.tasks;

import com.eressea.*;

public abstract class AbstractProblem implements Problem {
	protected Object source;
	protected HasRegion object;
	protected Inspector inspector;
	protected String message;
	protected int line;

	public AbstractProblem(Object s, HasRegion o, Inspector i, String m) {
		this(s, o, i ,m, -1);
	}

	public AbstractProblem(Object s, HasRegion o, Inspector i, String m, int l) {
		if(s == null || o == null || i==null || m == null) throw new NullPointerException();
		source = s;
		object = o;
		inspector = i;
		message = m;
		line = l;
	}

	

	/** 
	 * returns the type of the problem 
	 */
	public abstract int getType();

	public int getLine() {
		return line;
	}

	public Inspector getInspector() {
		return inspector;
	}

	/** 
	 * returns the object which originated this problem
	 */
	public Object getSource() {
		return source;
	}

	/** 
	 * returns the object which is attached to this problem
	 */
	public HasRegion getObject() {
		return object;
	}

	/**
	 *  returns the message of the problem
	 */
	public String toString() {
		return message;
	}
}
