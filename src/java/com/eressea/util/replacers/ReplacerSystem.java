// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

/**
 *
 * @author  Andreas
 * @version
 */
public class ReplacerSystem {
	
	protected ReplacerEnvironment environment;
	protected Replacer base;
	
	public ReplacerEnvironment getEnvironment() {
		if (environment == null) {
			environment = new ReplacerEnvironment();
		}
		return environment;
	}
	
	public Replacer getBase() {
		return base;
	}
	
	protected void setBase(Replacer replacer) {
		base = replacer;
	}
	
	public synchronized Object getReplacement(Object obj) {
		if (environment != null) {
			environment.reset();
		}
		Object ret = null;
		try{
			ret = base.getReplacement(obj);
		}catch(Exception exc) {}
		if (environment != null) {
			environment.reset();
		}
		return ret;
	}
	
}
