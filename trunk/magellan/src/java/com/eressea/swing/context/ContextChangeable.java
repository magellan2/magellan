/*
 * ContextChangeable.java
 *
 * Created on 1. März 2002, 15:32
 */

package com.eressea.swing.context;

import javax.swing.JMenuItem;

/**
 *
 * @author  Andreas
 * @version 
 */
public interface ContextChangeable {
	
	public JMenuItem getContextAdapter();
	
	public void setContextObserver(ContextObserver co);

}

