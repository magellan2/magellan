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
public class OperationMode extends EnvironmentPart {
	
	protected boolean nullEqualsZero = false;
	
	public void reset() {
		nullEqualsZero = false;
	}
	
	public boolean isNullEqualsZero() {
		return nullEqualsZero;
	}
	
	public void setNullEqualsZero(boolean bool) {
		nullEqualsZero = bool;
	}	
}
