package com.eressea.util;

import java.util.Map;


/**
 * object for one syntax token and properties of the token
 * 
 * @author Fiete
 *
 */
public class SpellSyntaxToken {
	
	/**
	 * token types
	 */
	public final static int SST_undef = 0;
	public final static int SST_String = 1;
	public final static int SST_KeyWord = 2;
	public final static int SST_Number = 3;
	public final static int SST_ShipID = 4;
	public final static int SST_BuildingID = 5;
	public final static int SST_Coordinate = 6;
	public final static int SST_UnitID = 7;
	
	
	public int getTokenType(){
		int retVal = SST_undef;	
		if (this.tokenChar==null){
			return retVal;
		} else if (getTokenString().equals("c")){
			retVal = SST_String;
		} else if (getTokenString().equals("k")){
			retVal = SST_KeyWord;
		} else if (getTokenString().equals("i")){
			retVal = SST_Number;
		} else if (getTokenString().equals("s")){
			retVal = SST_ShipID;
		} else if (getTokenString().equals("b")){
			retVal = SST_BuildingID;
		} else if (getTokenString().equals("r")){
			retVal = SST_Coordinate;
		} else if (getTokenString().equals("u")){
			retVal = SST_UnitID;
		}
		return retVal;
	}
	
	
	/**
	Enno 17.02.2007 in e-client
	'c' = Zeichenkette
	'k' = REGION|EINHEIT|STUFE|SCHIFF|GEBAEUDE
	'i' = Zahl
	's' = Schiffsnummer
	'b' = Gebaeudenummer
	'r' = Regionskoordinaten (x, y)
	'u' = Einheit
	'+' = Wiederholung des vorangehenden Parameters
	'?' = vorangegangener Parameter ist nicht zwingend
	*/
	private Character tokenChar = null;
	private boolean needed = true;
	private boolean multiple = false;
	
	/**
	 * constructor
	 *
	 */
	public SpellSyntaxToken(char c){
		this.tokenChar = new Character(c);
	}
	
	/**
	 * constructor
	 *
	 */
	public SpellSyntaxToken(Character c){
		this.tokenChar = c;
	}

	/**
	 * @return the multiple
	 */
	public boolean isMultiple() {
		return multiple;
	}

	/**
	 * @param multiple the multiple to set
	 */
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	/**
	 * @return the needed
	 */
	public boolean isNeeded() {
		return needed;
	}

	/**
	 * @param needed the needed to set
	 */
	public void setNeeded(boolean needed) {
		this.needed = needed;
	}

	/**
	 * @return the tokenCharacter
	 */
	public Character getTokenCharacter() {
		return tokenChar;
	}
	
	/**
	 * @return the tokenCharacter as String
	 */
	public String getTokenString(){
		if (this.tokenChar==null){
			return null;
		} else {
			return this.tokenChar.toString();
		}
	}
	
	/**
	 * returns a string representation of the object
	 */
	public String toString(){
		String retVal = getTokenString();
		if (retVal==null){
			return retVal;
		}
		String tokenString = "<" + getString("SpellSyntaxToken." + retVal) + ">";
		retVal = tokenString;
		// if optional...let it show
		if (!isNeeded()){
			retVal = "[" + retVal;
		}
		
		// if multiple entries are possibel...let it show
		if (isMultiple()){
			retVal = retVal + " [" + tokenString + " ...]";
		}
		// if optional...let it show
		if (!isNeeded()){
			retVal = retVal + "]";
		}
		
		return retVal;
	}
	
	
	
	/**
	 * Returns a translation from the translation table for the specified key.
	 *
	 * @param key TODO: DOCUMENT ME!
	 *
	 * @return TODO: DOCUMENT ME!
	 */
	protected String getString(String key) {
		return Translations.getTranslation(this, key);
	}
	
//	 pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
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
			defaultTranslations.put("SpellSyntaxToken.c", "String");
			defaultTranslations.put("SpellSyntaxToken.k", "REGION|UNIT|LEVEL|SHIP|BUILDING");
			defaultTranslations.put("SpellSyntaxToken.i", "Number");
			defaultTranslations.put("SpellSyntaxToken.s", "Shipnumber");
			defaultTranslations.put("SpellSyntaxToken.b", "Buildingnumber");
			defaultTranslations.put("SpellSyntaxToken.r", "Coordinates (X Y)");
			defaultTranslations.put("SpellSyntaxToken.u", "Unit");
		}
		return defaultTranslations;
	}
}