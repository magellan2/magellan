// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 *
 * @author  Andreas
 * @version
 */
public class ReplacerEnvironment {
	
	public final static String OPERATION_PART = "Op";
	public final static String UNITSELECTION_PART = "Unit";
	
	private Map parts;
	
	/** Creates new ReplacerEnvironment */
	public ReplacerEnvironment() {
		parts = new HashMap();
		
		// put some default parts
		parts.put(OPERATION_PART, new OperationMode());
		parts.put(UNITSELECTION_PART, new UnitSelection());
	}
	
	public EnvironmentPart getPart(String part) {
		return (EnvironmentPart)parts.get(part);
	}
	
	public void setPart(String name, EnvironmentPart part) {
		parts.put(name, part);
	}
	
	public void reset() {
		Iterator it = parts.values().iterator();
		while(it.hasNext()) {
			((EnvironmentPart)it.next()).reset();			
		}
	}
}
