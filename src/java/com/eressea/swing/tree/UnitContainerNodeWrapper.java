// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.eressea.UnitContainer;
import com.eressea.util.CollectionFactory;
import com.eressea.util.StringFactory;

public class UnitContainerNodeWrapper implements CellObject, SupportsClipboard {
	private UnitContainer uc = null;

	public UnitContainerNodeWrapper(UnitContainer uc) {
		this.uc = uc;
	}

	public UnitContainer getUnitContainer() {
		return uc;
	}

	public String toString() {
		return uc.toString();
	}

	private static Map iconNamesLists = CollectionFactory.createHashtable();
	public List getIconNames() {
		Object key = uc.getType().getID();
		List iconNames = (List) iconNamesLists.get(key);
		if (iconNames == null) {
			iconNames=CollectionFactory.singletonList(StringFactory.getFactory().intern(key.toString()));
			iconNamesLists.put(key,iconNames);
		}
		return iconNames;
	}

	public boolean emphasized() {
		return false;
	}

	public void propertiesChanged() {
	}

	public String getClipboardValue() {
		if (this.uc != null) {
		    return uc.toString();
		} else {
		    return toString();
		}
	}

	public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
	public NodeWrapperDrawPolicy init(Properties settings, String prefix, NodeWrapperDrawPolicy adapter) {
		return null;
	}
	
}
