/*
 * ColorAwareCellObject.java
 *
 * Created on 30. August 2001, 16:54
 */

package com.eressea.swing.tree;

import com.eressea.Group;
import com.eressea.Unit;
import com.eressea.UnitContainer;

/**
 *
 * @author  Andreas
 * @version 
 */
public class Tag2Element {
	
	protected static String lastStyle=null;
	
	protected final static String STYLE_TAG="magStyle";
	
	public static void start(Unit u) {
		lastStyle=null;
		if (u.containsTag(STYLE_TAG)) {
			lastStyle=u.getTag(STYLE_TAG);
		}
	}
	
	public static void start(UnitContainer u) {
		lastStyle=null;
		if (u.containsTag(STYLE_TAG)) {
			lastStyle=u.getTag(STYLE_TAG);
		}
	}
	
	public static void start(Group g) {
		lastStyle=null;
		if (g.containsTag(STYLE_TAG)) {
			lastStyle=g.getTag(STYLE_TAG);
		}		
	}
	
	public static void apply(GraphicsElement ge) {
		if (ge.getStyleset()==null || lastStyle!=null)
			ge.setStyleset(lastStyle);		
	}
}
