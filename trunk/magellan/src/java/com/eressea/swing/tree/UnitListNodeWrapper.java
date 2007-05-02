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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.util.CollectionFactory;

/**
 * DOCUMENT ME!
 *
 * @author Ulrich Küster A simple nodewrapper wrapping a list of units allowing acces to them via
 * 		   getUnits().
 */
public class UnitListNodeWrapper implements CellObject, SupportsClipboard {
	// identifies that this UnitListNodeWrapper contains a list of units that are
	// some other unit's students

	protected static final List defaultIcon = CollectionFactory.singletonList("simpledefault");
	
	/** TODO: DOCUMENT ME! */
	public static final int STUDENT_LIST = 1;
	private int type = 0;
	protected Collection units = null;
	protected String text = null;
	protected String clipboardValue = null;
	
	protected List icons;
	protected List returnIcons;
	protected DetailsNodeWrapperDrawPolicy adapter;

	/**
	 * Creates new UnitListNodeWrapper
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param clipboardValue TODO: DOCUMENT ME!
	 * @param units TODO: DOCUMENT ME!
	 * @param type TODO: DOCUMENT ME!
	 */
	public UnitListNodeWrapper(String text, String clipboardValue, Collection units, int type) {
		this(text, clipboardValue, units);
		this.type = type;
	}

	/**
	 * Creates a new UnitListNodeWrapper object.
	 *
	 * @param text TODO: DOCUMENT ME!
	 * @param clipboardValue TODO: DOCUMENT ME!
	 * @param units TODO: DOCUMENT ME!
	 */
	public UnitListNodeWrapper(String text, String clipboardValue, Collection units) {
		this.text = text;
		this.units = units;
		this.clipboardValue = clipboardValue;
	}

	public UnitListNodeWrapper(String text, String clipboardValue, Collection units, Object icons) {
		this(text, clipboardValue, units);
		this.icons = null;

        if(icons != null) {
            if(icons instanceof Collection) {
                this.icons = CollectionFactory.createArrayList((Collection) icons);
            } else if(icons instanceof Map) {
                Map m = (Map) icons;

                this.icons = CollectionFactory.createArrayList(m.size());

                for(Iterator iter = m.values().iterator(); iter.hasNext();) {
                    this.icons.add(iter.next().toString());
                }
            } else {
                this.icons = CollectionFactory.singletonList(icons.toString());
            }
        }
	}
	
	
	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getType() {
		return type;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Collection getUnits() {
		return units;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return text;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(clipboardValue == null) {
			return toString();
		} else {
			return clipboardValue;
		}
	}
	
	public List getIconNames() {
		if(returnIcons == null) {
			if((icons == null)) {
				returnIcons = defaultIcon;
			} else {
				returnIcons = icons;
			}
		}

		return returnIcons;
	}
	/**
	 * Controls whether the tree cell renderer should display this item more noticeably than other
	 * nodes.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean emphasized() {
		return false;
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
		return init(settings, "UnitListNodeWrapper", adapter);
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
		if(adapter == null) {
			adapter = createSimpleDrawPolicy(settings, prefix);
		}

		adapter.addCellObject(this);
		this.adapter = (DetailsNodeWrapperDrawPolicy) adapter;

		return adapter;
	}
	
	protected NodeWrapperDrawPolicy createSimpleDrawPolicy(Properties settings, String prefix) {
		return new DetailsNodeWrapperDrawPolicy(1, null, settings, prefix,
												new String[][] {
													{ "simple.showIcon", "true" }
												}, new String[] { "icons.text" }, 0, getClass(),
												getDefaultTranslations());
	}
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}
	private static final Map defaultTranslations = CollectionFactory.createHashtable();
	
	/**
	 * TODO: DOCUMENT ME!
	 */
	public void propertiesChanged() {
		returnIcons = null;
	}
	
}
