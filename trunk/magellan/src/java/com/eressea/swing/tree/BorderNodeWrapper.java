// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ---
// $Id$
// ===

package com.eressea.swing.tree;




import java.util.List;
import java.util.Properties;

import com.eressea.Border;
import com.eressea.util.CollectionFactory;

public class BorderNodeWrapper implements CellObject, SupportsClipboard {
	private Border border = null;
	private List iconNames = null;

	public BorderNodeWrapper(Border border) {
		this.border = border;
	}

	public Border getBorder() {
		return border;
	}

	public String toString() {
		return border.toString();
	}

	public List getIconNames() {
		if (iconNames == null) {
			iconNames = CollectionFactory.singletonList(border.type);
		}
		return iconNames;
	}

	public boolean emphasized() {
		return false;
	}

	public void propertiesChanged() {
	}

	public NodeWrapperDrawPolicy init(Properties settings,NodeWrapperDrawPolicy adapter) {
		return null;
	}

	public NodeWrapperDrawPolicy init(Properties settings,String prefix,NodeWrapperDrawPolicy adapter) {
		return null;
	}

	public String getClipboardValue() {
		if (border != null) {
		    return border.toString();
		} else {
		    return toString();
		}
	}
}
