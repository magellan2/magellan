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

import com.eressea.Potion;

public class PotionNodeWrapper implements CellObject, SupportsClipboard {
	private Potion potion = null;
	private String displayedString = null;

	public PotionNodeWrapper(Potion p) {
		this(p, null);
	}

	public PotionNodeWrapper(Potion p, String postfix) {
		this.potion = p;
		if (postfix != null) {
			this.displayedString = p.toString() + postfix;
		} else {
			this.displayedString = p.toString();
		}
	}

	public Potion getPotion() {
		return potion;
	}

	public String toString() {
		return displayedString;
	}

	public List getIconNames() {
		return null;
	}

	public boolean emphasized() {
		return false;
	}

	public void propertiesChanged() {
	}

	public String getClipboardValue() {
		if (potion != null) {
		    return potion.getName();
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
