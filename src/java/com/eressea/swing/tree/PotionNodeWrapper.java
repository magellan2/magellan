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

package com.eressea.swing.tree;

import java.util.List;
import java.util.Properties;

import com.eressea.Potion;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PotionNodeWrapper implements CellObject, SupportsClipboard {
	private Potion potion = null;
    private String name = null;
    private String postfix = null;

	/**
	 * Creates a new PotionNodeWrapper object.
	 *
	 * @param p TODO: DOCUMENT ME!
	 */
	public PotionNodeWrapper(Potion p) {
		this(p, null);
	}

	/**
	 * Creates a new PotionNodeWrapper object.
	 *
	 * @param p TODO: DOCUMENT ME!
	 * @param postfix TODO: DOCUMENT ME!
	 */
	public PotionNodeWrapper(Potion p, String postfix) {
        this(p, p.getName(),postfix);
    }
    public PotionNodeWrapper(Potion p, String name, String postfix) {
        this.potion = p;
        this.name = name;
        this.postfix = postfix;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Potion getPotion() {
		return potion;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
	    return postfix == null ? name : (name+postfix);	}

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
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		return false;
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
		if(potion != null) {
			return potion.getName();
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
