package com.eressea.test.merge;

import junit.framework.TestCase;

import com.eressea.GameData;
import com.eressea.Region;
import com.eressea.Skill;
import com.eressea.Unit;

public class MergeWithRoads extends TestCase {

	public MergeWithRoads(String aName) {
		super(aName);
	}

	// bugzilla bug #819
	public void testLooseOldRoadInformation() throws Exception {
		GameDataBuilder builder = new GameDataBuilder();

		GameData gd1 = builder.createSimpleGameData(350);
		GameData gd2 = builder.createSimpleGameData(351);

		Region r1 = (Region) gd1.regions().values().iterator().next();
		builder.addRoad(r1, 1, 1, 100);
		
		GameData gd4 = GameData.merge(gd1, gd2);
		// WriteGameData.writeCR(gdMerged, gdMerged.getDate().getDate()+"_gd.cr");

		Region r4 = gd4.getRegion(r1.getID());
		
		assertTrue(r4 != null);
		assertTrue(r4.borders().isEmpty());
	}

	public void testKeepNewRoadInformation() throws Exception {
		GameDataBuilder builder = new GameDataBuilder();

		GameData gd1 = builder.createSimpleGameData(350);
		GameData gd2 = builder.createSimpleGameData(351);

		Region r2 = (Region) gd2.regions().values().iterator().next();
		builder.addRoad(r2, 1, 1, 100);
		
		GameData gd4 = GameData.merge(gd1, gd2);
		// WriteGameData.writeCR(gdMerged, gdMerged.getDate().getDate()+"_gd.cr");

		Region r4 = gd4.getRegion(r2.getID());
		
		assertTrue(r4 != null);
		assertTrue(!r4.borders().isEmpty());
	}

}
