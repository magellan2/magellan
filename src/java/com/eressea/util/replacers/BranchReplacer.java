/*
 * BranchReplacer.java
 *
 * Created on 19. Mai 2002, 12:02
 */

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version 
 */
public interface BranchReplacer extends Replacer {

	public int getBranchCount();
	
	public String getBranchSign(int index);
	
	public void setBranch(int index, Object obj);
}

