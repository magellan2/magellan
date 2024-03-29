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

package com.eressea.event;

import java.util.Collection;
import java.util.EventObject;

/**
 * An event issued when the user activates a different object or selects a number of objects. This
 * might occur for example when the user clicks on a region on the map.
 *
 * @see SelectionListener
 * @see EventDispatcher
 */
public class SelectionEvent extends EventObject {
	/**
	 * Some flags to specify what to do with SE or where they come from etc. Please document, where
	 * an whatfor they are used
	 */
	/** Default value. */
	public static final int ST_DEFAULT = 0;

	/**
	 * Indicates, that some regions on the map have been selected or shall be selected. Used by the
	 * SelectionActions classes and in the Mapper class. These selections have to be ignored by
	 * some components (like EMapOverviewPanel) as the selectionstate of the map is not mirrored
	 * in the tree in EMapOverviewPanel. On the other hand the Mapper class should ignore all
	 * SelectionEvents with a type different to ST_REGIONS (This makes it possible for the user to
	 * treat selections of regions on the map and other selections in different ways.)
	 */
	public static final int ST_REGIONS = 1;
	private Collection selectedObjects;
	private Object activeObject;
	private int selectionType;

	/**
	 * Constructs a new selection event with selectionType = ST_DEFAULT.
	 * 
	 * <p>
	 * Usually such an event indicates only a change of the active object which is indicated by
	 * activeObject != null and selectedObjects == null.
	 * </p>
	 *
	 * @param source the object issuing the event.
	 * @param selectedObjects the objects selected by the user. This collection does not
	 * 		  necessarily contain activeObject. Specifying null for this parameter indicates that
	 * 		  the selected objects did actually not change.
	 * @param activeObject the single object activated by the user.
	 */
	public SelectionEvent(Object source, Collection selectedObjects, Object activeObject) {
		this(source, selectedObjects, activeObject, SelectionEvent.ST_DEFAULT);
	}

	/**
	 * Creates a new SelectionEvent object.
	 *
	 * @param source TODO: DOCUMENT ME!
	 * @param selectedObjects TODO: DOCUMENT ME!
	 * @param activeObject TODO: DOCUMENT ME!
	 * @param selectionType TODO: DOCUMENT ME!
	 */
	public SelectionEvent(Object source, Collection selectedObjects, Object activeObject,
						  int selectionType) {
		super(source);
		this.selectedObjects = selectedObjects;
		this.activeObject = activeObject;
		this.selectionType = selectionType;
	}

	/**
	 * Returns the possibly multiple objects selected by the user. They do not necessarily include
	 * the active object. A value of null indicates that previously selected objects are not
	 * affected by this event.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getSelectedObjects() {
		return selectedObjects;
	}

	/**
	 * Returns the one single object activated by the user.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Object getActiveObject() {
		return activeObject;
	}

	/**
	 * Returns the type of the SelectionEvent. This has to be one of final int-values defined above
	 * (like ST_REGIONS, ST_DEFAULT).
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getSelectionType() {
		return selectionType;
	}
}
