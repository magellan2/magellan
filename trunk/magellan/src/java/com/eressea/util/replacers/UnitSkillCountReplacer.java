/*
 * UnitSkillCountReplacer.java
 *
 * Created on 30. Dezember 2001, 17:23
 */

package com.eressea.util.replacers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.eressea.Region;
import com.eressea.Skill;
import com.eressea.Unit;
import com.eressea.rules.SkillType;
import com.eressea.util.CollectionFactory;
/**
 *
 * @author  Andreas
 * @version
 */
public class UnitSkillCountReplacer extends AbstractParameterReplacer implements EnvironmentDependent {
	
	public final static int MODE_SKILL = 0;
	public final static int MODE_SKILL_MIN = 1;
	public final static int MODE_SKILL_SUM = 2;
	public final static int MODE_SKILL_SUM_MIN = 3;
	
	protected int mode;
	
	private final static int[] MODE_LENGTHS = {1, 2, 1, 2};
	
	protected ReplacerEnvironment environment;
	
	/** Creates new UnitSkillCountReplacer */
	public UnitSkillCountReplacer(int mode) {
		super(MODE_LENGTHS[mode]);
		this.mode = mode;
	}
	
	public Object getReplacement(Object o) {
		if (!(o instanceof Region))
			return null;
		boolean minMode= (mode == MODE_SKILL_MIN || mode == MODE_SKILL_SUM_MIN);
		boolean sumMode= (mode == MODE_SKILL_SUM || mode == MODE_SKILL_SUM_MIN);
		int min=1;
		String skill = getParameter(0, o).toString();
		if (minMode) {
			Object obj = getParameter(1, o);
			if (obj instanceof Number) {
				min = ((Number)obj).intValue();
			} else try {
				min = (int)Double.parseDouble(obj.toString());
			}catch(NumberFormatException nfe) {}
		}
		
		Collection units = ((UnitSelection)environment.getPart(ReplacerEnvironment.UNITSELECTION_PART)).getUnits((Region)o);
		if (units==null || units.size()==0)
			return new Integer(0);
		int count=0;
		Iterator it=units.iterator();
		while(it.hasNext()) {
			Unit u=(Unit)it.next();
			Iterator it2=u.getSkills().iterator();
			while(it2.hasNext()) {
				Skill sk=(Skill)it2.next();
				SkillType sty=sk.getType();
				if (sty.getName().equals(skill) || sty.getID().toString().equals(skill)) {
					if (!minMode || (sk.getLevel()>=min)) {
						if (sumMode)
							count+=u.persons*sk.getLevel();
						else
							count+=u.persons;
					}
					break;
				}
			}
		}
		return new Integer(count);
	}
	
	public String getDescription() {
		return com.eressea.util.Translations.getTranslation(this,"description."+mode);
	}		
	
	public void setEnvironment(ReplacerEnvironment env) {
		environment = env;
	}

	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map defaultTranslations;
	public synchronized static Map getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = CollectionFactory.createHashtable();
			// FIXME(pavkovic) 
			defaultTranslations.put("description.2" , "Z\u00E4hlt alle Personen, die das nachfolgend angegebene Talent haben und berechnet die Summe der Talentpunkte (Summe \u00FCber Personenzahl mal Talentstufe). Einschr\u00E4nkungen der Grundmenge k\u00F6nnen durch Einheitenfilter vorgenommen werden.");
			defaultTranslations.put("description.3" , "Z\u00E4hlt alle Personen, die das nachfolgend angegebene Talent (Argument) haben und berechnet die Summe der Talentpunkte (Summe \u00FCber Personenzahl mal Talentstufe). Dabei werden nur Einheiten gez\u00E4hlt, die die angegebene Stufe (Argument 2) haben (oder \u00FCberschreiten). Einschr\u00E4nkungen der Grundmenge k\u00F6nnen durch Einheitenfilter vorgenommen werden.");
			defaultTranslations.put("description.1" , "Z\u00E4hlt alle Personen, die das nachfolgend angegebene Talent (Argument 1) mindestens auf der angegebenen Stufe (Argument 2) haben (oder \u00FCberschreiten). Einschr\u00E4nkungen der Grundmenge k\u00F6nnen durch Einheitenfilter vorgenommen werden.");
			defaultTranslations.put("description.0" , "Z\u00E4hlt alle Personen, die das nachfolgend angegebene Talent haben. Einschr\u00E4nkungen der Grundmenge k\u00F6nnen durch Einheitenfilter vorgenommen werden.");
		}
		return defaultTranslations;
	}
	
}
