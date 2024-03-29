/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
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

package com.eressea.rules;

import java.util.Collection;
import java.util.Iterator;

import com.eressea.ID;
import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public abstract class Category extends ObjectType {
	private int sortIndex = 0;
	private Category parent = null;
	private Collection children = null;
	private Collection data = null;

	/**
	 * Creates a new Category object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public Category(ID id) {
		this(id, null);
	}

	/**
	 * Creates a new Category object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 * @param parent TODO: DOCUMENT ME!
	 */
	public Category(ID id, Category parent) {
		super(id);
		setParent(parent);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Category getParent() {
		return parent;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p TODO: DOCUMENT ME!
	 */
	public void setParent(Category p) {
		if(parent != p) {
			if (parent != null) {
				parent.removeChild(this);
			}
			
			parent = p;
			
			if(p != null) {
				parent.addChild(this);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean hasChildren() {
		return (children != null) && (children.size() > 0);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getChildren() {
		if(children == null) {
			children = CollectionFactory.createHashSet();
		}

		return children;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param ic TODO: DOCUMENT ME!
	 */
	public void addChild(Category ic) {
		getChildren().add(ic);
	}

	protected void removeChild(Category ic) {
		if(hasChildren()) {
			getChildren().remove(ic);
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean hasInstances() {
		return (data != null) && (data.size() > 0);
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getInstances() {
		if(data == null) {
			data = CollectionFactory.createHashSet();
		}

		return data;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public abstract boolean isInstance(Object o);

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean addInstance(Object o) {
		if(!isInstance(o)) {
			return false;
		}

		getInstances().add(o);

		if(parent != null) {
			parent.addInstance(o);
		}

		return true;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param o TODO: DOCUMENT ME!
	 */
	public void removeInstance(Object o) {
		if(!isInstance(o)) {
			return;
		}

		if(hasInstances()) {
			getInstances().remove(o);
		}

		if(hasChildren()) {
			Iterator it = getChildren().iterator();

			while(it.hasNext()) {
				((Category) it.next()).removeInstance(o);
			}
		}
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param i TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Category getTopLevelAncestor(Category i) {
		while(i.getParent() != null) {
			i = i.getParent();
		}

		return i;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param ic TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Category[] getPath(Category ic) {
		int i = 1;
		Category j = ic;

		while(j.getParent() != null) {
			j = j.getParent();
			i++;
		}

		Category path[] = new Category[i];

		for(int k = 0; k < i; k++) {
			path[i - k - 1] = ic;
			ic = ic.getParent();
		}

		return path;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param p TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean isDescendant(Category p) {
		Category path[] = getPath(this);

		for(int i = 0; i < path.length; i++) {
			if(path[i].equals(p)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * helper method for xml reader
	 *
	 * @param i TODO: DOCUMENT ME!
	 */
	public void setNaturalorder(String i) {
		setSortIndex(Integer.parseInt(i));
	}

	/**
	 * Sets the sort index of this item category indicating its natural ordering compared to other
	 * ItemCategory objects.
	 *
	 * @param sortIndex TODO: DOCUMENT ME!
	 */
	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	/**
	 * Returns the sort index of this item category indicating its natural ordering compared to
	 * other ItemCategory objects.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSortIndex() {
		return sortIndex;
	}

	/**
	 * Imposes a natural ordering on ItemCategory objects. Since we may have more structured item
	 * types, begin with the top-level ancestors and work down.
	 *
	 * @param o TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int compareTo(Object o) {
		Category path1[] = getPath(this);
		Category path2[] = getPath((Category) o);
		int j = path1.length;

		if(path2.length < j) {
			j = path2.length;
		}

		for(int i = 0; i < j; i++) {
			int k = compareImpl(path1[i], path2[i]);

			if(k != 0) {
				return k;
			}
		}

		if(path1.length > path2.length) {
			return 1;
		} else if(path2.length > path1.length) {
			return -1;
		}

		return 0;
	}

	protected static int compareImpl(Category i1, Category i2) {
		if(i1.getSortIndex() != i2.getSortIndex()) {
			return i1.getSortIndex() - i2.getSortIndex();
		} else {
			return i1.getID().compareTo(i2.getID());
		}
	}

	private String iconName = null;

	/**
	 * Returns the file name of the icon to use for this item.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getIconName() {
		if((iconName == null) && (parent != null)) {
			return parent.getIconName();
		}

		return iconName;
	}

	/**
	 * Sets the file name of the icon to use for this item.
	 *
	 * @param iName TODO: DOCUMENT ME!
	 */
	public void setIconName(String iName) {
		iconName = iName;
	}
}
