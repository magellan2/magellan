/*
 * ContextListener.java
 *
 * Created on 6. Mai 2002, 15:21
 */

package com.eressea.swing.tree;

import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 *
 * @author  Andreas
 * @version 
 */
public interface ContextListener {

	public void contextFailed(Component c, MouseEvent e);
}

