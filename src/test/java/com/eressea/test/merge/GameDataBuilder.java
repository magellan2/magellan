package com.eressea.test.merge;

import java.util.*;

import com.eressea.*;
import com.eressea.io.*;
import com.eressea.rules.*;
import com.eressea.util.*;


public class GameDataBuilder {

	private final int BASE_ROUND = 360;

	public GameData createSimplestGameData() throws Exception {
		return createSimplestGameData(BASE_ROUND);
	}
	
	public GameData createSimplestGameData(int round) throws Exception {
		return createSimplestGameData(round, true);
	}

	public GameData createSimplestGameData(int round, boolean postProcess) throws Exception {
		GameData data = new GameDataReader().createGameData("Eressea");
		
		data.base=36;
		// this is sadly needed
		IDBaseConverter.setBase(data.base);
		
		data.noSkillPoints=true;
		
		data.setLocale(Locale.GERMAN);
		
		EresseaDate ed = new EresseaDate(round);
		ed.setEpoch(2);
		data.setDate(ed);
		
		//data.setCurTempID
		//data.mailTo
		//data.mailSubject

		
		// data.addFaction
		Faction faction = addFaction(data,"867718","Faction_867718","Meermenschen",1);

		Island island = addIsland(data,"1","Island_1");

		Region region = addRegion(data,"0 0", "Region_0_0","Gletscher",1);
		region.setIsland(island);

		Unit unit = addUnit(data, "1", "Unit_1", faction, region);

		if(postProcess) {
			data.postProcess();
		}
		return data;
	}

	public GameData createSimpleGameData() throws Exception {
		return createSimpleGameData(BASE_ROUND);
	}
	
	public GameData createSimpleGameData(int round) throws Exception {
		GameData data = createSimplestGameData(round, false);

		Unit unit = (Unit) data.units().values().iterator().next();

		Skill skill1 = addSkill(unit, "Hiebwaffen", 4, 3, true); // Hiebwaffen 4 (+3)
		Skill skill2 = addSkill(unit, "Segeln", 4, 3,false); // Segeln 4
		Skill skill3 = addSkill(unit, "Magie", -1, -3,true); // Magie - (-3)
		Skill skill4 = addSkill(unit, "Steinbau", -1, -3,false); // Steinbau - 

		data.postProcess();
		return data;
	}

	public Faction addFaction(GameData data, String number, String name, String race, int sortIndex) {
		EntityID id = EntityID.createEntityID(number, 10);

		Faction faction = new Faction(id, data);
		data.addFaction(faction);

		faction.setName(name);

		faction.password = name;

		faction.setType(data.rules.getRace(StringID.create(race),true));

		faction.setSortIndex(sortIndex);
		
		return faction;
	}

	public Island addIsland(GameData data, String number, String name) {
		IntegerID id = IntegerID.create(number);

		Island island = new Island(id, data);
		data.addIsland(island);
		
		island.setName(name);

		return island;
	}

	public Region addRegion(GameData data, String number, String name, String type, int sortIndex) {
		ID c = Coordinate.parse(number, " ");
		
		Region region = new Region(c, data);
		data.addRegion(region);
		
		region.setName(name);
		
		region.setType(data.rules.getRegionType(StringID.create(type), true));
		
		region.setSortIndex(sortIndex);
		return region;
	}
	
	public Unit addUnit(GameData data, String number, String name,  Faction faction, Region region) {
		UnitID id = UnitID.createUnitID(number);

		Unit unit = new Unit(id);
		data.addUnit(unit);
		
		unit.setName(name);

		unit.setFaction(faction);

		unit.race = faction.getRace();
		unit.realRace = faction.getRace();
		
		unit.setRegion(region);
		
		unit.setOrders(Collections.singleton(""));

		return unit;
	}
	
	public Skill addLostSkill(Unit unit, String name, int level) {
		return addSkill(unit, name, -1, level,true);
	}

	public Skill addSkill(Unit unit, String name, int level) {
		return addSkill(unit, name, level, level, false);
	}
						 
	public Skill addChangedSkill(Unit unit, String name, int level, int fromLevel) {
		return addSkill(unit, name, level, fromLevel, true);
	}

	protected Skill addSkill(Unit unit, String name, int level, int change, boolean changed) {
		
		SkillType skt = unit.getRegion().getData().rules.getSkillType(StringID.create(name), true);
		int raceBonus = unit.realRace.getSkillBonus(skt);
		int points = Skill.getPointsAtLevel(level-raceBonus);

		Skill skill = new Skill(skt, points, level, unit.persons, unit.getRegion().getData().noSkillPoints);

		skill.setChangeLevel(change);

		skill.setLevelChanged(changed);

		unit.addSkill(skill);

		return skill;
	}
	

}
