// ===
// Copyright (C) 2000, 2001 Roger Butenuth, Andreas Gampe, Stefan Götz, Sebastian Pappert, Ilja Pavkovic, Klaas Prause, Enno Rehling, Sebastian Tusk
// ---
// This file is part of the Eressea Java Code Base, see the file LICENSING for the licensing information applying to this file
// ===

package com.eressea.util.replacers;

import com.eressea.util.logging.Logger;
/**
 *
 * @author  Andreas
 * @version
 */
public class OperationSwitch extends AbstractParameterReplacer implements EnvironmentDependent, SwitchOnly {
	private final static Logger log = Logger.getInstance(FactionSwitch.class);
	
	protected ReplacerEnvironment environment;
	
	/** Creates new FactionSwitch */
	public OperationSwitch() {
		super(1);
	}
	
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description");
	}
	
	public Object getReplacement(Object src) {
		try{
			String fName = getParameter(0, src).toString();
			((OperationMode)environment.getPart(ReplacerEnvironment.OPERATION_PART)).setNullEqualsZero(fName.equals("true"));
		}catch(NullPointerException npe) {}
		return BLANK;
	}
	
	public void setEnvironment(ReplacerEnvironment env) {
		environment = env;
	}	
}
