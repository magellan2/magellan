// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Ulrich Küster, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;

import java.awt.Image;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.eressea.Group;
import com.eressea.util.CollectionFactory;


/**
 * @author  Andreas, Ulrich Küster
 */
public class GroupNodeWrapper implements CellObject2, SupportsClipboard, SupportsEmphasizing {

	protected Group group;
	protected List GE;
	protected static Icon icon;

	private int amount = -1;

	private List subordinatedElements = CollectionFactory.createArrayList();

	/** Creates new GroupNodeWrapper */
	public GroupNodeWrapper(Group g) {
		group = g;
		if (icon == null) {
			icon = UIManager.getIcon("Tree.closedIcon");
		}
	}

	public Group getGroup() {
		return group;
	}

	public List getGraphicsElements() {
		if (GE==null) {
			GraphicsElement ge=new GroupGraphicsElement(group,icon,null,null);
			Tag2Element.start(group);
			Tag2Element.apply(ge);
			ge.setType(GraphicsElement.MAIN);

			GE = CollectionFactory.singletonList(ge);
		}
		return GE;
	}

	private class GroupGraphicsElement extends GraphicsElement {
		public GroupGraphicsElement(Object object, Icon icon, Image image, String imageName) {
			super(object, icon, image, imageName);
		}

		public boolean isEmphasized() {
			return emphasized();
		}
	}

	public List getSubordinatedElements() {
		return subordinatedElements;
	}

	public boolean reverseOrder() {
		return false;
	}

	public void setAmount(int i) {
		this.amount = i;
	}

	public int getAmount() {
		return this.amount;
	}

	public java.lang.String toString() {
		if (this.amount == -1) {
			return group.toString();
		} else {
			return group.toString() + ": " + amount;
		}
	}

	public boolean emphasized() {
		for (Iterator iter = subordinatedElements.iterator(); iter.hasNext(); ) {
			SupportsEmphasizing se = (SupportsEmphasizing)iter.next();
			if (se.emphasized()) {
				return true;
			}
		}
		return false;
	}

	public List getIconNames() {
		return null;
	}

	public void propertiesChanged() {
	}

	public String getClipboardValue() {
		if (group != null) {
			return group.getName();
		} else {
			return toString();
		}
	}
	
	public NodeWrapperDrawPolicy init(java.util.Properties settings, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
	public NodeWrapperDrawPolicy init(java.util.Properties settings, String prefix, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
}
