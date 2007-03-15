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
import java.util.Map;
import java.util.Properties;

import com.eressea.Ship;
import com.eressea.UnitContainer;
import com.eressea.util.CollectionFactory;
import com.eressea.util.StringFactory;
import com.eressea.util.Translations;

/**
 * TODO: DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class UnitContainerNodeWrapper implements CellObject, SupportsClipboard {
	private UnitContainer uc = null;
	private boolean showFreeLoad = false;
	private boolean hasOwner=false;

	public UnitContainerNodeWrapper(UnitContainer uc) {
// 		this(uc, false);
 		this(uc, true, false);
	}

	/**
	 * Creates a new UnitContainerNodeWrapper object.
	 *
	 * @param uc TODO: DOCUMENT ME!
	 */
	public UnitContainerNodeWrapper(UnitContainer uc, boolean showFreeLoad) {
		this(uc, showFreeLoad, false);
	}

	public UnitContainerNodeWrapper(UnitContainer uc, boolean showFreeLoad, boolean hasOwner) {
		this.uc = uc;
		this.showFreeLoad = showFreeLoad;
		this.hasOwner = hasOwner;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public UnitContainer getUnitContainer() {
		return uc;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		StringBuffer text= new StringBuffer(uc.toString());
		if(showFreeLoad && uc instanceof Ship) {
			double free = .01 * (((Ship) uc).getMaxCapacity() - ((Ship) uc).getModifiedLoad());
			text.append(": ");
			text.append(free);
			// overloading
			if (free<0){
				text.append(" (!!!)");
			}
		}
		if (hasOwner)
			text.append(" ("+Translations.getTranslation(this, "owner")+")");
		return text.toString();
	}

	private static Map iconNamesLists = CollectionFactory.createHashtable();

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public List getIconNames() {
		Object key = uc.getType().getID();
		List iconNames = (List) iconNamesLists.get(key);

		if(iconNames == null) {
			iconNames = CollectionFactory.singletonList(StringFactory.getFactory().intern(key.toString()));
			iconNamesLists.put(key, iconNames);
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
	 * @return TODO: DOCUMENT ME!
	 */
	public String getClipboardValue() {
		if(this.uc != null) {
			return uc.toString();
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
	
//	 pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static final Map defaultTranslations = CollectionFactory.createHashtable();

	static {
		defaultTranslations.put("owner", "owner");
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static Map getDefaultTranslations() {
		return defaultTranslations;
	}
}
