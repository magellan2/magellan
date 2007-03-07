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

import com.eressea.Unit;
import com.eressea.UnitContainer;
import com.eressea.util.CollectionFactory;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 171 $
 */
public class UnitContainerCommentNodeWrapper implements CellObject, SupportsClipboard {
	private String comment = null;
	private UnitContainer uc = null;
	private List iconNames = null;

	/**
	 * Creates a new BorderNodeWrapper object.
	 *
	 * @param border TODO: DOCUMENT ME!
	 */
	public UnitContainerCommentNodeWrapper(UnitContainer uc,String comment) {
		this.comment = comment;
		this.uc=uc;
	}

	
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return this.comment;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getIconNames() {
		if(iconNames == null) {
			iconNames = CollectionFactory.singletonList("comment_marker");
		}

		return iconNames;
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

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(comment != null) {
			return comment;
		} else {
			return "";
		}
	}


	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}


	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}




	/**
	 * @return the uc
	 */
	public UnitContainer getUnitContainer() {
		return uc;
	}
}
