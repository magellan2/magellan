
package com.eressea.tasks;

import com.eressea.*;

public interface Problem {
	public final static int INFORMATION = 0;
	public final static int WARNING     = 1;
	public final static int ERROR       = 2;

	/** 
	 * returns the creating inspector
	 */
	public Inspector getInspector();

	/** 
	 * returns the type of the problem 
	 */
	public int getType();

	/** 
	 * returns the type of the problem 
	 */
	public int getLine();

	/** 
	 * returns the object this problem criticizes
	 */
	public HasRegion getObject();

	/** 
	 * returns the originating object 
	 */
	public Object getSource();

	/** 
	 * returns the message of the problem
	 */
	public String toString();

}
