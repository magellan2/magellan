package com.eressea.test.merge;

import java.util.*;

import com.eressea.*;
import com.eressea.io.*;
import com.eressea.rules.*;
import com.eressea.util.*;


public class GameDataBuilder {

	public GameData createSimpleGameData() throws Exception {
		return createSimpleGameData(350);
	}

	public GameData createSimpleGameData(int round) throws Exception {
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

		Skill skill1 = addSkill(data, unit, "Hiebwaffen", 4, 3, true); // Hiebwaffen 4 (+3)
		Skill skill2 = addSkill(data, unit, "Segeln", 4, 3,false); // Segeln 4
		Skill skill3 = addSkill(data, unit, "Magie", -1, -3,true); // Magie - (-3)
		Skill skill4 = addSkill(data, unit, "Steinbau", -1, -3,false); // Steinbau - 


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
		
		return unit;
	}

	public Skill addSkill(GameData data, Unit unit, String name, int level, int change, boolean changed) {
		
		SkillType skt = data.rules.getSkillType(StringID.create(name), true);
		int raceBonus = unit.realRace.getSkillBonus(skt);
		int points = Skill.getPointsAtLevel(level-raceBonus);

		Skill skill = new Skill(skt, points, level, unit.persons, data.noSkillPoints);

		skill.setChangeLevel(change);

		skill.setLevelChanged(changed);

		unit.addSkill(skill);

		return skill;
	}
	

}
