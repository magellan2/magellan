// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.swing.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
/**
 *
 * @author  Andreas
 * @version 
 */
public class MixedTreeCellRenderer implements TreeCellRenderer {
	
	protected Map renderers;
	protected TreeCellRenderer def;

	/** Creates new Template */
	public MixedTreeCellRenderer(TreeCellRenderer def) {
		this.def = def;
		renderers = new HashMap();
	}
	
	public void putRenderer(Object o, TreeCellRenderer r) {
		if (o instanceof Class) {
			renderers.put(o, r);
		} else {
			renderers.put(o.getClass(), r);
		}
	}
	
	protected TreeCellRenderer findRenderer(Class c) {
		if (renderers.containsKey(c)) {
			return (TreeCellRenderer)renderers.get(c);
		}
		Iterator it = renderers.keySet().iterator();
		while(it.hasNext()) {
			Class o = (Class)it.next();
			if (o.isAssignableFrom(c)) {
				return (TreeCellRenderer)renderers.get(o);
			}
		}
		return def;
	}

	public java.awt.Component getTreeCellRendererComponent(javax.swing.JTree jTree, java.lang.Object obj, boolean param, boolean param3, boolean param4, int param5, boolean param6) {
		Object o = obj;
		if (obj instanceof DefaultMutableTreeNode) {
			o = ((DefaultMutableTreeNode)obj).getUserObject();
		}
		TreeCellRenderer tcr = findRenderer(o.getClass());
		if (tcr != null) {
			return tcr.getTreeCellRendererComponent(jTree, obj, param, param3, param4, param5, param6);
		}
		return null;			
	}
	
}
