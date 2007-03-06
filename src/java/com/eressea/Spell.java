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

package com.eressea;

import java.util.Map;

import com.eressea.gamebinding.eressea.EresseaConstants;
import com.eressea.util.CollectionFactory;
import com.eressea.util.SpellSyntax;
import com.eressea.util.Translations;

/**
 * Container class for a spell based on its representation in a cr version >= 42.
 */
public class Spell extends DescribedObject {
	private int blockID = -1; // this is the id of the ZAUBER block in the cr
	private int level = -1; // a mage's level has to be at least this value to be able to cast this spell
	private int rank = -1;
	private String type = null; // represents the 'class' tag, can't be named like that, though
	private boolean onShip = false;
	private boolean onOcean = false;
	private boolean isFamiliar = false;
	private boolean isFar = false;
	private Map components = null; // map of String objects
	
	private String syntax = null; // FF 20070221 new CR tag syntax
	/**
	 * the spellsytnax object
	 */
	private SpellSyntax spellSyntax = null;

	/**
	 * Creates a new Spell object.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public Spell(ID id) {
		super(id);
	}

	// TODO: this is bad, but right now i dont have a better idea
	/** 
	 * @see com.eressea.Unique#getID()
	 */
	public ID getID() {
	    return StringID.create(getName());
	}

	/**
	 * Returns the integer serving as the block id in the cr.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getBlockID() {
		return blockID;
	}

	/**
	 * Sets the integer serving as the block id in the cr.
	 *
	 * @param id TODO: DOCUMENT ME!
	 */
	public void setBlockID(int id) {
		this.blockID = id;
	}

	/**
	 * Returns the level of this spell which indicates the lowest skill level a mage must have to
	 * be able to cast this spell.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level of this spell which indicates the lowest skill level a mage must have to be
	 * able to cast this spell.
	 *
	 * @param level TODO: DOCUMENT ME!
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param rank TODO: DOCUMENT ME!
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Returns the class attribute of this spell.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the class attribute of this spell.
	 *
	 * @param type TODO: DOCUMENT ME!
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getOnOcean() {
		return onOcean;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param onOcean TODO: DOCUMENT ME!
	 */
	public void setOnOcean(boolean onOcean) {
		this.onOcean = onOcean;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getOnShip() {
		return onShip;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param onShip TODO: DOCUMENT ME!
	 */
	public void setOnShip(boolean onShip) {
		this.onShip = onShip;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIsFamiliar() {
		return isFamiliar;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param isFamiliar TODO: DOCUMENT ME!
	 */
	public void setIsFamiliar(boolean isFamiliar) {
		this.isFamiliar = isFamiliar;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public boolean getIsFar() {
		return isFar;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param _isFar TODO: DOCUMENT ME!
	 */
	public void setIsFar(boolean _isFar) {
		isFar = _isFar;
	}

	/**
	 * Returns the components of this spell as Strings.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public Map getComponents() {
		if (components==null)
			components = CollectionFactory.createOrderedHashtable();
		return components;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @param components TODO: DOCUMENT ME!
	 */
	public void setComponents(Map components) {
		this.components = components;
	}

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String toString() {
		return this.getName();
	}

	/**
	 * Returns a name for this spell's type.
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public String getTypeName() {
		if(this.type != null) {
			return getString(this.type);
		} else {
			return getString("unspecified");
		}
	}

	/**
	 * A string with information about the syntax of the spell (FF)
	 * @return
	 */
	public String getSyntaxString(){
		String retVal = "";
		
		// Region, if is far
		if (isFar){
			retVal = "[" + getString("region") + " X Y]";
		}
		
		// Level...allways possible, but not allways usefull
		// we have no info, how to decide here - we add it.
		if (retVal.length()>0){
			retVal += " ";
		}
		retVal += "[" + getString("level") + " n]";
		
		// name of spell in "
		if (retVal.length()>0){
			retVal += " ";
		}
		retVal += "\"" + this.getName() + "\"";
		
		// Syntax
		if (this.getSpellSyntax()!=null && this.getSpellSyntax().toString()!=null){
			if (retVal.length()>0){
				retVal += " ";
			}
			retVal += this.getSpellSyntax().toString();
		}
		
		// if nothing was added, return null
		if (retVal.length()==0) {
			retVal = null;
		} else {
			// präfix: 
			retVal = "Syntax: " + Translations.getOrderTranslation(EresseaConstants.O_CAST) + " " + retVal;
		}
		return retVal;
	}
	
	
	
	/**
	 * Merges spells.
	 *
	 * @param curGD TODO: DOCUMENT ME!
	 * @param curSpell TODO: DOCUMENT ME!
	 * @param newGD TODO: DOCUMENT ME!
	 * @param newSpell TODO: DOCUMENT ME!
	 */
	public static void merge(GameData curGD, Spell curSpell, GameData newGD, Spell newSpell) {
		if(curSpell.getBlockID() != -1) {
			newSpell.setBlockID(curSpell.getBlockID());
		}

		if(curSpell.getName() != null) {
			newSpell.setName(curSpell.getName());
		}

		if(curSpell.getDescription() != null) {
			newSpell.setDescription(curSpell.getDescription());
		}

		if(curSpell.level != -1) {
			newSpell.level = curSpell.level;
		}

		if(curSpell.rank != -1) {
			newSpell.rank = curSpell.rank;
		}

		if(curSpell.type != null) {
			newSpell.type = curSpell.type;
		}

		// FF 20070221: syntax
		if (curSpell.getSyntax()!=null && curSpell.getSyntax().length()>0){
			newSpell.setSyntax(curSpell.getSyntax());
		}
		
		if(curSpell.onShip != false) {
			newSpell.onShip = curSpell.onShip;
		}

		if(curSpell.onOcean != false) {
			newSpell.onOcean = curSpell.onOcean;
		}

		if(curSpell.isFamiliar != false) {
			newSpell.isFamiliar = curSpell.isFamiliar;
		}

		if(curSpell.isFar != false) {
			newSpell.isFar = curSpell.isFar;
		}

		if((curSpell.components != null) && (curSpell.components.size() > 0)) {
			newSpell.components = CollectionFactory.createOrderedHashtable();
			newSpell.components.putAll(curSpell.components);
		}
	}

	private String getString(String key) {
		// TODO: Uebersetzungen in orders.properties verschieben!!!!
		//
		return com.eressea.util.Translations.getTranslation(this, key);
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;

	/**
	 * TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	public static synchronized Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			defaultTranslations.put("unspecified", "unknown type");
			defaultTranslations.put("normal", "ordinary spell");
			defaultTranslations.put("precombat", "pre-combat spell");
			defaultTranslations.put("combat", "combat spell");
			defaultTranslations.put("postcombat", "post-combat spell");
			defaultTranslations.put("level", "LEVEL");
			defaultTranslations.put("region", "REGION");
		}

		return defaultTranslations;
	}


	/**
	 * Enno in e-client about the syntax:
	 * 'c' = Zeichenkette
	 * 'k' = REGION|EINHEIT|STUFE|SCHIFF|GEBAEUDE
	 * 'i' = Zahl
	 * 's' = Schiffsnummer
	 * 'b' = Gebaeudenummer
	 * 'r' = Regionskoordinaten (x, y)
	 * 'u' = Einheit
	 * '+' = Wiederholung des vorangehenden Parameters
	 * '?' = vorangegangener Parameter ist nicht zwingend

	 *	Syntaxcheks, die der Server auf dieser Basis macht, sind nicht perfekt;
	 *	es ist notwendig, aber nicht hinreichend, dass die Syntax erfuellt wird.
	 *	Aber in den vielen Faellen kann man damit schonmal sagen, was denn
	 *	falsch war.
	 *
	 * 
	 * @return the syntax
	 */
	public String getSyntax() {
		return syntax;
	}
	

	/**
	 * @param syntax the syntax to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax; 
	}
	
	/**
	 * returns the spellsyntax object of this spell
	 * @return a SpellSyntax object
	 */
	public SpellSyntax getSpellSyntax(){
		if (this.syntax==null || this.syntax.length()==0){
			return null;
		}
		
		// creating a new one if it does not exists
		if (this.spellSyntax==null){
			this.spellSyntax = new SpellSyntax(this.syntax);
		}
		
		return this.spellSyntax;
	}
	
	
}
