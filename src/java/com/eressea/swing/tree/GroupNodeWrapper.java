/*
 *  Copyright (C) 2000-2003 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package com.eressea.swing.tree;

import java.awt.Image;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.eressea.Group;

import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Andreas, Ulrich Küster
 */
public class GroupNodeWrapper implements CellObject2, SupportsClipboard, SupportsEmphasizing {
	protected Group group;
	protected List GE;
	protected static Icon icon;
	private int amount = -1;
	private List subordinatedElements = CollectionFactory.createArrayList();

	/**
	 * Creates new GroupNodeWrapper
	 *
	 * @param g TODO: DOCUMENT ME!
	 */
	public GroupNodeWrapper(Group g) {
		group = g;

		if(icon == null) {
			icon = UIManager.getIcon("Tree.closedIcon");
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getGraphicsElements() {
		if(GE == null) {
			GraphicsElement ge = new GroupGraphicsElement(group, icon, null, null);
			Tag2Element.start(group);
			Tag2Element.apply(ge);
			ge.setType(GraphicsElement.MAIN);

			GE = CollectionFactory.singletonList(ge);
		}

		return GE;
	}

	private class GroupGraphicsElement extends GraphicsElement {
		/**
		 * Creates a new GroupGraphicsElement object.
		 *
		 * @param object TODO: DOCUMENT ME!
		 * @param icon TODO: DOCUMENT ME!
		 * @param image TODO: DOCUMENT ME!
		 * @param imageName TODO: DOCUMENT ME!
		 */
		public GroupGraphicsElement(Object object, Icon icon, Image image, String imageName) {
			super(object, icon, image, imageName);
		}

		/**
		 * TODO: DOCUMENT ME!
		 *
		 * @return TODO: DOCUMENT ME!
		 */
		public boolean isEmphasized() {
			return emphasized();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getSubordinatedElements() {
		return subordinatedElements;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean reverseOrder() {
		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void setAmount(int i) {
		this.amount = i;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getAmount() {
		return this.amount;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		if(this.amount == -1) {
			return group.toString();
		} else {
			return group.toString() + ": " + amount;
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		for(Iterator iter = subordinatedElements.iterator(); iter.hasNext();) {
			SupportsEmphasizing se = (SupportsEmphasizing) iter.next();

			if(se.emphasized()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getIconNames() {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 */
	public void propertiesChanged() {
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(group != null) {
			return group.getName();
		} else {
			return toString();
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return null;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param settings TODO: DOCUMENT ME!
	 * @param prefix TODO: DOCUMENT ME!
	 * @param adapter TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public NodeWrapperDrawPolicy init(Properties settings, String prefix,
									  NodeWrapperDrawPolicy adapter) {
		return null;
	}
}
